package com.inspirenetz.api.core.service.impl;

import com.inspirenetz.api.core.dictionary.*;
import com.inspirenetz.api.core.dictionary.PaymentStatus;
import com.inspirenetz.api.core.domain.*;
import com.inspirenetz.api.core.loyaltyengine.CatalogueRedemption;
import com.inspirenetz.api.core.repository.BaseRepository;
import com.inspirenetz.api.core.repository.RedemptionRepository;
import com.inspirenetz.api.core.service.*;
import com.inspirenetz.api.rest.exception.InspireNetzException;
import com.inspirenetz.api.util.*;
import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by sandheepgr on 16/2/14.
 */
@Service
public class RedemptionServiceImpl extends BaseServiceImpl<Redemption> implements RedemptionService {

    private static Logger log = LoggerFactory.getLogger(RedemptionServiceImpl.class);

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private MerchantSettingService merchantSettingService;

    @Autowired
    private CustomerRewardExpiryService customerRewardExpiryService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerRewardBalanceService customerRewardBalanceService;

    @Autowired
    private CatalogueService catalogueService;

    @Autowired
    CardMasterService cardMasterService;

    @Autowired
    private LinkedLoyaltyService linkedLoyaltyService;

    @Autowired
    private AccountBundlingSettingService accountBundlingSettingService;

    @Autowired
    private CustomerSubscriptionService customerSubscriptionService;

    @Autowired
    private LoyaltyEngineService loyaltyEngineService;

    @Autowired
    private  MerchantService merchantService;

    @Autowired
    UserMessagingService userMessagingService;

    @Autowired
    Environment environment;

    @Autowired
    RewardCurrencyService rewardCurrencyService;

    @Autowired
    GeneralUtils generalUtils;

    @Autowired
    private DataSource dataSource;

    @Autowired
    AuthSessionUtils authSessionUtils;

    @Autowired
    CustomerActivityService customerActivityService;

    @Autowired
    PartyApprovalService partyApprovalService;

    @Autowired
    AccountBundlingUtils accountBundlingUtils;

    @Autowired
    OneTimePasswordService oneTimePasswordService;

    @Autowired
    LoyaltyEngineUtils loyaltyEngineUtils;

    @Autowired
    UserService userService;

    @Autowired
    MerchantLocationService merchantLocationService;

    @Autowired
    RedemptionMerchantService redemptionMerchantService;

    @Autowired
    MerchantSettlementService merchantSettlementService;


    public RedemptionServiceImpl(){
        super(Redemption.class);
    }

    @Override
    protected BaseRepository<Redemption, Long> getDao() {
        return redemptionRepository;
    }



    @Override
    public List<Redemption> findByRdmMerchantNoAndRdmUniqueBatchTrackingId(Long rdmMerchantNo,String rdmUniqueBatchTrackingId) {

        // Get the redemptions matching the tracking id
        List<Redemption> redemptions = redemptionRepository.findByRdmMerchantNoAndRdmUniqueBatchTrackingId(rdmMerchantNo,rdmUniqueBatchTrackingId);

        // Return the redemptions
        return  redemptions;

    }

    /**
     * Function to deduct the points based on the CustomerRewardExpiry
     * Here we are processing the CustomerRewardExpiry entries in FIFO manner and update the transaction
     * for each entry.
     * This function will take care of all the linked reward entries as well
     *
     * @param redemption                : The Redemption object
     * @param customerRewardExpiryList  : The list of CustomerRewardExpiry entries
     * @param totalRewardQty            : The total reward quantity that is required for the redemption
     * @param location                  : The location of redemption
     */
    @Transactional
    public boolean deductPoints(Redemption redemption,List<CustomerRewardExpiry> customerRewardExpiryList, double totalRewardQty,Long location) {


        // Create the variable holding the cre reward balance
        double creRewardBalance =  0;

        // Variable holding the reward qty used for current iteration
        double creCurrRewardQty = 0;

        // Variable holding the quantity to be redeemed
        double redeemQty = 0;


        // Store the value of total reward quantity to be redeemed in redeemQty
        redeemQty = totalRewardQty;


        // Go through each of the item in the List for CustomerRewardExpiry
        for(CustomerRewardExpiry customerRewardExpiry : customerRewardExpiryList ) {

            // Get the creRewardBalance
            creRewardBalance = customerRewardExpiry.getCreRewardBalance();

            // Check if the current balance is sufficient for the redemption
            if ( creRewardBalance >= redeemQty  ) {

                creRewardBalance -= redeemQty;

                creCurrRewardQty = redeemQty;

                totalRewardQty = 0;

            } else {

                totalRewardQty = totalRewardQty - creRewardBalance;

                creCurrRewardQty = creRewardBalance;

                creRewardBalance = 0;
            }


            // Set the updated reward balance in the CustomerRewardExpiry object
            customerRewardExpiry.setCreRewardBalance(creRewardBalance);

            // Set the updated_by auditDetails as the createdby field from the redemption request
            customerRewardExpiry.setUpdatedBy(redemption.getCreatedBy());

            // Update the value
            customerRewardExpiryService.update(customerRewardExpiry);




            // Get the current balance for the customer
            CustomerRewardBalance customerRewardBalance = customerRewardBalanceService.findByCrbLoyaltyIdAndCrbMerchantNoAndCrbRewardCurrency(
                    customerRewardExpiry.getCreLoyaltyId(),
                    customerRewardExpiry.getCreMerchantNo(),
                    customerRewardExpiry.getCreRewardCurrencyId()
            );



            // Add the redemption transaction
            addRedemptionTransactionEntry(redemption,location,customerRewardBalance.getCrbRewardBalance(),creCurrRewardQty);


            // Update the reward balance after deduction
            customerRewardBalance.setCrbRewardBalance(customerRewardBalance.getCrbRewardBalance() - creCurrRewardQty);

            // Set the updated_by auditDetails as the createdby field from the redemption request
            customerRewardBalance.setUpdatedBy(redemption.getCreatedBy());

            // Save the updated the customer reward balance
            customerRewardBalanceService.update(customerRewardBalance);



            // If the totalRewardQty has become 0, then we don't need to update anything more
            if( totalRewardQty == 0  ){

                return true;

            }

        }


        // If the totalRewardQty is 0, then return true
        return false;

    }

    /**
     * Function to add the redemption transaction entry to the table
     * Here we pass the Redemption entry , location and rewardQtyPreBal and the reward quantity
     *
     * @param redemption        : The Redemption object
     * @param location          : The location of redemption
     * @param rewardQtyPreBal   : The balance before transaction
     * @param rewardQty         : The quantity deducted
     * @return                  : true if successful
     *                            false if failed.
     */
    public boolean addRedemptionTransactionEntry(Redemption redemption,Long location,double rewardQtyPreBal,double rewardQty) {

        // Create the Transaction object
        Transaction transaction = new Transaction();

        // Set the fields
        transaction.setTxnAmount(redemption.getRdmCashAmount());

        transaction.setTxnCrDbInd(CreditDebitInd.DEBIT);

        transaction.setTxnDate(redemption.getRdmDate());

        transaction.setTxnInternalRef(redemption.getRdmId().toString());

        transaction.setTxnExternalRef(redemption.getRdmUniqueBatchTrackingId());

        transaction.setTxnLocation(location);

        transaction.setTxnLoyaltyId(redemption.getRdmLoyaltyId());

        transaction.setTxnMerchantNo(redemption.getRdmMerchantNo());

        transaction.setTxnProgramId(0L);

        transaction.setTxnRewardCurrencyId(redemption.getRdmRewardCurrencyId());

        transaction.setTxnRewardQty(rewardQty);

        transaction.setTxnRewardPreBal(rewardQtyPreBal);

        transaction.setTxnRewardPostBal(rewardQtyPreBal - rewardQty);

        transaction.setTxnStatus(TransactionStatus.PROCESSED);

        transaction.setTxnRewardExpDt(DBUtils.covertToSqlDate("9999-12-31"));


        // Check the redemption type and then set the transaction type
        if ( redemption.getRdmType() == RedemptionType.CATALOGUE ) {

            transaction.setTxnType(TransactionType.REDEEM);

        } else if ( redemption.getRdmType() == RedemptionType.CASHBACK ) {

            transaction.setTxnType(TransactionType.CASHBACK);

        }



        transaction.setCreatedBy(redemption.getCreatedBy());

        // Insert the transaction object
        transactionService.saveTransaction(transaction)  ;


        // Check the if the transaction id is null, if not then we have got the trasnasction saved
        if ( transaction.getTxnId() != null ) {

            return true;

        }


        return false;

    }

    @Override
    public CashBackRedemptionResponse doCashbackRedemption(CashBackRedemptionRequest cashbackRedemptionRequest) throws InspireNetzException {

        // Log the request
        log.info("doCashbackRedemption -> CashBackRedemptionRequest object " + cashbackRedemptionRequest.toString());

        // Create the CashBackRedemptionResponse object
        CashBackRedemptionResponse retData = new CashBackRedemptionResponse();

        // Get the customer information
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(cashbackRedemptionRequest.getLoyaltyId(), cashbackRedemptionRequest.getMerchantNo());

        // Check if the customer exists
        // If the catalogue is null, then we need to show the message
        if ( customer == null || customer.getCusLoyaltyId() == "" || customer.getCusStatus() != CustomerStatus.ACTIVE) {

            // Log the response
            log.info("doCashbackRedemption - Response : No customer found");

            // Set the status as failed
            retData.setStatus(APIResponseStatus.failed.name());

            //check if the customer loyaltyid is null and set the error code
            if (customer.getCusLoyaltyId() == null || customer.getCusLoyaltyId() == ""){

                //throw an error
                throw new InspireNetzException(APIErrorCode.ERR_NO_LOYALTY_ID);

            } else {

                //throw an error
                throw new InspireNetzException(APIErrorCode.ERR_CUSTOMER_NOT_ACTIVE);
            }



        }


        // Get the RewardCurrency for the specified reward currency
        RewardCurrency rewardCurrency = rewardCurrencyService.findByRwdCurrencyId(cashbackRedemptionRequest.getRewardCurrencyId());

        // Check if the rewardCurrency exists and is enabled for cashback
        if ( rewardCurrency == null || rewardCurrency.getRwdCashbackIndicator() == IndicatorStatus.NO ) {

            // Set the status as failed
            retData.setStatus(APIResponseStatus.failed.name());

            // Set the error as not data found
            retData.setErrorcode(APIErrorCode.ERR_NO_DATA.name());

            // Log the response
            log.info("doCashbackRedemption - Response : reward currency cash back disabled");

            // Return the object
            return retData;

        }


        // Set the rewardCurrency object in the request object
        cashbackRedemptionRequest.setRewardCurrency(rewardCurrency);


        // Variable holding the redemptionSTatus
        int redemptionStatus = RedemptionStatus.RDM_STATUS_NEW;




        // Get the rewardQty required for the amount
        double rewardQty = rewardCurrencyService.getCashbackQtyForAmount(cashbackRedemptionRequest.getRewardCurrency(),cashbackRedemptionRequest.getAmount());


        // Check the qty is greater than 0
        if ( rewardQty < 0 ) {

            // Set the status
            retData.setStatus(APIResponseStatus.failed.name());

            // Set the error message
            retData.setBalance(0);

            // Set the error code
            retData.setErrorcode(APIErrorCode.ERR_OPERATION_FAILED.name());

            // Return the object
            return retData;

        }

        //check cash back indicator enabled or not
        if(rewardCurrency.getRwdCashbackIndicator() == IndicatorStatus.YES){

            //check reward quantity is greater than minimum or equal minimum amount
            Integer minimumPoint =rewardCurrency.getRwdRedemptionMinPoints() ==null?0:rewardCurrency.getRwdRedemptionMinPoints();

            //check the redeemed point
            if(rewardQty < minimumPoint.intValue()){

                retData.setStatus(APIResponseStatus.failed.name());

                // Set the error message
                retData.setBalance(0);

                // Set the error code
                retData.setErrorcode(APIErrorCode.ERR_MINIMUM_POINT_VIOLATION.name());

                // Return the object
                return retData;
            }
        }


        // Get the rewardBalance for the customer
        // RewardBalance for the reward currency id required by the Catalogue
        CustomerRewardBalance customerRewardBalance = customerRewardBalanceService.findByCrbLoyaltyIdAndCrbMerchantNoAndCrbRewardCurrency(
                cashbackRedemptionRequest.getLoyaltyId(),
                cashbackRedemptionRequest.getMerchantNo(),
                cashbackRedemptionRequest.getRewardCurrencyId()
        );


        // Check if the rewardBalance Field is null, then throw error
        if ( customerRewardBalance == null || rewardQty > customerRewardBalance.getCrbRewardBalance() ) {

            // Set the status
            retData.setStatus(APIResponseStatus.failed.name());

            // Set the error message
            retData.setBalance(0);

            // Set the error code
            retData.setErrorcode(APIErrorCode.ERR_INSUFFICIENT_POINT_BALANCE.name());

            //for log activity for insufficient point balance
            customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Cash Back Redemption Failed Due To In Sufficient Point Balance ",cashbackRedemptionRequest.getMerchantNo(),"");

            // Return the retData;
            return retData;
        }


        // Create the Redemption object
        Redemption redemption = new Redemption();

        // Set the fields in the Redemption
        //
        // Set the rdmMerchatNo
        redemption.setRdmMerchantNo(cashbackRedemptionRequest.getMerchantNo());

        redemption.setRdmCashAmount(cashbackRedemptionRequest.getAmount());

        // Set the status
        redemption.setRdmStatus(redemptionStatus);

        // Set the type
        redemption.setRdmType(RedemptionType.CASHBACK);

        // Set the reward currency id
        redemption.setRdmRewardCurrencyId(cashbackRedemptionRequest.getRewardCurrencyId());

        // set the reward quantity
        redemption.setRdmRewardCurrencyQty(rewardQty);

        // Set the payment status
        redemption.setRdmCashPaymentStatus(PaymentStatus.PAYMENT_STATUS_NOT_PAID);

        // Set the Product code
        redemption.setRdmProductCode("0");

        // Set the tracking id
        // Get the trackingId
        String trackingId = generalUtils.getUniqueId(cashbackRedemptionRequest.getLoyaltyId());

        trackingId = trackingId==null?"0":trackingId;

        redemption.setRdmUniqueBatchTrackingId(trackingId);

        // Set the loyalty id
        redemption.setRdmLoyaltyId(cashbackRedemptionRequest.getLoyaltyId());

        // Set the delivery ind to be 0
        redemption.setRdmDeliveryInd(IndicatorStatus.NO);

        // Set the totalCashAmount
        redemption.setRdmCashbackAmount(cashbackRedemptionRequest.getAmount());

        // Set the date
        redemption.setRdmDate(new java.sql.Date(new Date().getTime()));

        // Set the time
        redemption.setRdmTime(new Time(new java.util.Date().getTime()));



        // set the user not to 0
        redemption.setRdmUserNo(cashbackRedemptionRequest.getUserNo());

        // Set the contact number
        redemption.setRdmContactNumber("0");

        // Set the audit details
        redemption.setCreatedBy(cashbackRedemptionRequest.getAuditDetails());

        redemption.setRdmRef(cashbackRedemptionRequest.getTxnRef());

        // Insert the redemption
        redemption = redemptionRepository.save(redemption);


        // Check if the redemption has been been saved
        if ( redemption == null || redemption.getRdmId() == null ) {


            // Set the status
            retData.setStatus("failed");

            // Set the error code
            retData.setErrorcode("ERR_OPERATION_FAILED");

            // Return the object
            return retData;

        }



        // Get the list of CustomerRewardExpiry entries
        List<CustomerRewardExpiry> customerRewardExpiryList = customerRewardExpiryService.getFIFOCustomerExpiryList(
                redemption.getRdmMerchantNo(),
                redemption.getRdmLoyaltyId(),
                redemption.getRdmRewardCurrencyId()
        );




        try {

            // Call the deductPoints function
            boolean isPointsDeduct = deductPoints(redemption,customerRewardExpiryList, rewardQty,cashbackRedemptionRequest.getUserLocation());


            // If the point deduction is successful, then we need to set the status
            // as successful
            if ( isPointsDeduct ) {

                // Set the RedemptionStatus variable to processed successfully
                redemptionStatus = RedemptionStatus.RDM_STATUS_PROCESSED_SUCCESSFULLY;

                // If the points has been deducted, the set the redemption as completed successfully
                // Set the status
                retData.setStatus("success");

                // Set the balance to the customerRewardBalance - rewardQty
                retData.setBalance(customerRewardBalance.getCrbRewardBalance() -  rewardQty);

                // Set the transaction reference as the redemption id
                retData.setTxnRef(redemption.getRdmId().toString());

                //set the redeemed quantity
                retData.setPointRedeemed(rewardQty);

            } else {

                // Set the redemption status to failed
                redemptionStatus = RedemptionStatus.RDM_STATUS_FAILED;

                // Set the exception details
                // Set the status
                retData.setStatus("failed");

                // Set the error code
                retData.setErrorcode("ERR_OPERATION_FAILED");


            }



        } catch (Exception e) {

            // Set the RedemptionStatus to failed
            redemptionStatus = RedemptionStatus.RDM_STATUS_FAILED;

            // Set the exception details
            // Set the status
            retData.setStatus("failed");

            // Set the error code
            retData.setErrorcode("ERR_OPERATION_FAILED");

        }

        // Set the redemption status
        redemption.setRdmStatus(redemptionStatus);

        // Update the Redemption Status
        redemptionRepository.save(redemption);

        //update the transaction table


        // Return the retData object
        return retData;


    }

    @Override
    public CatalogueRedemptionResponse doCatalogueRedemption(CatalogueRedemptionRequest catalogueRedemptionRequest) throws InspireNetzException {


        // Create the CatalogueRedemptionResponse object to return
        CatalogueRedemptionResponse retData = new CatalogueRedemptionResponse();

        // Get the merchantNo;
        Long merchantNo = catalogueRedemptionRequest.getMerchantNo();

        // Get the loyaltyId
        String loyaltyId = catalogueRedemptionRequest.getLoyaltyId();


        // Get the customer information for the mobile
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(loyaltyId, merchantNo);

        // Check if the customer exists
        // If the catalogue is null, then we need to show the message
        if ( customer == null || customer.getCusLoyaltyId() == "" || customer.getCusStatus() != CustomerStatus.ACTIVE) {

            //log the error
            log.info("do catalogue redemption  - No customer found");

            //check for loyalty id
            if (customer.getCusLoyaltyId() == null || customer.getCusLoyaltyId() == "") {

                //throw an exception
                throw new InspireNetzException(APIErrorCode.ERR_NO_LOYALTY_ID);

            } else {

                //throw an exception
                throw new InspireNetzException(APIErrorCode.ERR_CUSTOMER_NOT_ACTIVE);
            }

        }



        // Create the list holding the CatalogueRedemptionItemResponse
        List<CatalogueRedemptionItemResponse> catalogueRedemptionItemResponseList = new ArrayList<CatalogueRedemptionItemResponse>();

        // Go through each of RedemptionCatalogues in the CatalogueRedemptionRequest and then call the redeem catalogue
        for( RedemptionCatalogue redemptionCatalogue : catalogueRedemptionRequest.getRedemptionCatalogues() ) {


            // Get the CatalogueRedemptionItemResponse
            CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = redeemCatalogue(
                    catalogueRedemptionRequest,
                    redemptionCatalogue.getCatProductCode(),
                    redemptionCatalogue.getCatQty()
            );

            // Set the productNo on the itemresponse to be returned
            catalogueRedemptionItemResponse.setPrd_no(redemptionCatalogue.getCatProductNo().toString());

            // Add the catalogueRedemptionResposne to the list
            catalogueRedemptionItemResponseList.add(catalogueRedemptionItemResponse);

        }



        // Set the data to be the list
        retData.setCatalogueRedemptionItemResponseList(catalogueRedemptionItemResponseList);

        // Set the status as succes
        retData.setStatus("success");



        // Return the retData
        return retData;
    }

    @Override
    public CatalogueRedemptionItemResponse redeemCatalogue(CatalogueRedemptionRequest catalogueRedemptionRequest, String prdCode, Integer qty) {


        // Get the merchantNo
        Long merchantNo = catalogueRedemptionRequest.getMerchantNo();

        // Get the loyaltyId
        String loyaltyId = catalogueRedemptionRequest.getLoyaltyId();



        // Variable holding the total Reward quantity
        double totalRewardQty = 0;

        // Variable holding the totalCashAmount
        double totalCashAmount = 0;




        // Create the CatalogueRedemptionItemResponse object
        CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();




        // Get the  catalogue for the given product code
        Catalogue catalogue = catalogueService.findByCatProductCodeAndCatMerchantNo(prdCode,merchantNo);

        // If the catalogue is null, then we need to show the message
        if ( catalogue == null || catalogue.getCatProductCode() == "" ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_PRODUCT_NOT_FOUND");

            // Return the object
            return catalogueRedemptionItemResponse;

        }


        // Set the product number
        catalogueRedemptionItemResponse.setPrd_no(catalogue.getCatProductNo().toString());



        // Calculate the totalRewardQty
        totalRewardQty = catalogue.getCatNumPoints() * qty;

        // Calculate the totalCashAmount
        totalCashAmount = catalogue.getCatPartialCash() * qty;




        // RewardBalance for the reward currency id required by the Catalogue
        CustomerRewardBalance customerRewardBalance = customerRewardBalanceService.findByCrbLoyaltyIdAndCrbMerchantNoAndCrbRewardCurrency(
                loyaltyId,
                merchantNo,
                catalogue.getCatRewardCurrencyId()
        );


        // Check if the rewardBalance Field is null, then throw error
        if ( customerRewardBalance == null || totalRewardQty > customerRewardBalance.getCrbRewardBalance() ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_NO_BALANCE");

            // Return the object
            return catalogueRedemptionItemResponse;

        }


        // Create the UniqueIdGenerator
        DBUniqueIdGenerator generator = new DBUniqueIdGenerator(dataSource);

        // Get the trackingId
        Long trackingId = generator.getNextUniqueId(UniqueIdType.REDEMPTION_BATCH_TRACKING_ID);

        // If the trackingId is -1, then the unique id generation failed
        if ( trackingId.longValue() == -1 ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_OPERATION_FAILED");

            // Return the object
            return catalogueRedemptionItemResponse;

        }

        // Create the Redemption object
        Redemption redemption = new Redemption();

        // Set the fields in the Redemption
        //
        // Set the rdmMerchatNo
        redemption.setRdmMerchantNo(merchantNo);

        // Set the status
        redemption.setRdmStatus(RedemptionStatus.RDM_STATUS_NEW);

        // Set the type
        redemption.setRdmType(RedemptionType.CATALOGUE);

        // Set the reward currency id
        redemption.setRdmRewardCurrencyId(catalogue.getCatRewardCurrencyId());

        // set the reward quantity
        redemption.setRdmRewardCurrencyQty(totalRewardQty);

        // set the product code
        redemption.setRdmProductCode(prdCode);

        // set quantity
        redemption.setRdmQty(qty);

        // Set the loyalty id
        redemption.setRdmLoyaltyId(loyaltyId);

        // Set the delivery ind to be 0
        redemption.setRdmDeliveryInd(catalogueRedemptionRequest.getDeliveryInd());

        // Set the totalCashAmount
        redemption.setRdmCashAmount(totalCashAmount);;

        // Set the date
        redemption.setRdmDate(new java.sql.Date(new Date().getTime()));

        // Set the time
        redemption.setRdmTime(new Time(new java.util.Date().getTime()));

        // Set the tracking id
        redemption.setRdmUniqueBatchTrackingId(trackingId.toString());

        // Set the cashPaymentStatus
        redemption.setRdmCashPaymentStatus(PaymentStatus.PAYMENT_STATUS_PAID);

        // set the user not to 0
        redemption.setRdmUserNo(catalogueRedemptionRequest.getUserNo());

        // Set the contact number
        redemption.setRdmContactNumber(catalogueRedemptionRequest.getContactNumber());

        //Check is the redemption of partner product
        if(catalogue.getCatRedemptionMerchant() != null && catalogue.getCatRedemptionMerchant().longValue() != 0L){

            //set the rdm partner no
            redemption.setRdmPartnerNo(catalogue.getCatRedemptionMerchant());

        }else {

            //Set rdm partner no as 0
            redemption.setRdmPartnerNo(0L);

        }

        // Set the auditDetails
        redemption.setCreatedBy(catalogueRedemptionRequest.getAuditDetails());

        // Insert the redemption
        redemption = redemptionRepository.save(redemption);


        // Check if the redemption has been been saved
        if ( redemption == null || redemption.getRdmId() == null ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_OPERATION_FAILED");

            // Set the tracking id
            catalogueRedemptionItemResponse.setTracking_id(trackingId.toString());

            // Return the object
            return catalogueRedemptionItemResponse;

        }



        // Get the list of CustomerRewardExpiry entries
        List<CustomerRewardExpiry> customerRewardExpiryList = customerRewardExpiryService.getFIFOCustomerExpiryList(
                redemption.getRdmMerchantNo(),
                redemption.getRdmLoyaltyId(),
                redemption.getRdmRewardCurrencyId()
        );


        try {

            // Call the deductPoints function
            boolean isPointsDeduct = deductPoints(redemption,customerRewardExpiryList, totalRewardQty,catalogueRedemptionRequest.getUserLocation());

            // If the points has been deducted, the set the redemption as completed successfully
            // Set the status
            catalogueRedemptionItemResponse.setStatus("success");

            // Set the trackingId
            catalogueRedemptionItemResponse.setTracking_id(trackingId.toString());


        } catch (Exception e) {

            // Set the exception details
            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the tracking id
            catalogueRedemptionItemResponse.setTracking_id(trackingId.toString());

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_OPERATION_FAILED");

        }



        // Return the catalogueRedemptionItemResponse
        return catalogueRedemptionItemResponse;


    }

    @Override
    public List<RedemptionCatalogue> getRedemptionCatalogues(Map<String,String> params) {

        // Create the list to hold the RedemptionCatalogues
        List<RedemptionCatalogue> redemptionCatalogueList = new ArrayList<RedemptionCatalogue>();

        // Set the index to 0
        int index = 0;

        // Go through the indexs and set the data
        for  ( ; index < 10 ; index++) {

            // Create the RedemptionCatalogue object
            RedemptionCatalogue redemptionCatalogue = new RedemptionCatalogue();

            // Check if the key  prd_no existing
            if ( params.containsKey("cat_data["+Integer.toString(index)+"][prd_no]") ) {

                redemptionCatalogue.setCatProductNo(Long.parseLong(params.get("cat_data["+Integer.toString(index)+"][prd_no]")));

            } else {

                continue;
            }

            // Check if the key prd_code is existing
            if ( params.containsKey("cat_data["+Integer.toString(index)+"][prd_code]") ) {

                redemptionCatalogue.setCatProductCode(params.get("cat_data[" + Integer.toString(index) + "][prd_code]"));

            } else {

                continue;

            }


            // Check if the key merchant_no is existing
            if ( params.containsKey("cat_data["+Integer.toString(index)+"][merchant_no]") ) {

                redemptionCatalogue.setCatMerchantNo(Long.parseLong(params.get("cat_data[" + Integer.toString(index) + "][merchant_no]")));

            } else {

                continue;

            }


            // Check if the key qty is existing
            if ( params.containsKey("cat_data["+Integer.toString(index)+"][qty]") ) {

                Double qty = Double.parseDouble(params.get("cat_data["+Integer.toString(index)+"][qty]"));

                redemptionCatalogue.setCatQty(qty.intValue());

            } else {

                continue;

            }


            // Add the redemptionCatalogue to the list
            redemptionCatalogueList.add(redemptionCatalogue);

        }


        // Return the object
        return redemptionCatalogueList;

    }

    @Override
    public Page<Redemption> listRedemptionRequests(Long rdmMerchantNo, String filterType, String query, Integer status, Pageable pageable) {

        // Page holding the result
        Page<Redemption> redemptions = null ;

        // Get the result set for the loyatly id
        int page=pageable.getPageNumber();

        int pageSize = pageable.getPageSize();

        //create request with sorting parameter
        Pageable newPageableRequest = new PageRequest(page,pageSize, new Sort(Sort.Direction.DESC,"rdmId"));

        // Check the filter type and then call the appropriate repository method for getting the result
        if (filterType.equalsIgnoreCase("loyaltyid") ) {

            if(status == 0){

                redemptions =  redemptionRepository.findByRdmMerchantNoAndRdmLoyaltyIdAndRdmRecordStatus(rdmMerchantNo, query, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            } else {

                redemptions =  redemptionRepository.findByRdmMerchantNoAndRdmLoyaltyIdAndRdmStatusAndRdmRecordStatus(rdmMerchantNo,query,status,RecordStatus.RECORD_STATUS_ACTIVE,newPageableRequest);

            }


        } else if ( filterType.equalsIgnoreCase("trackingid") ) {

            if(status == 0){

                // Get the result set for the tracking id
                redemptions = redemptionRepository.findByRdmMerchantNoAndRdmUniqueBatchTrackingIdAndRdmRecordStatus(rdmMerchantNo,query,RecordStatus.RECORD_STATUS_ACTIVE,newPageableRequest);

            } else {

                // Get the result set for the tracking id
                redemptions = redemptionRepository.findByRdmMerchantNoAndRdmUniqueBatchTrackingIdAndRdmStatusAndRdmRecordStatus(rdmMerchantNo,query,status,RecordStatus.RECORD_STATUS_ACTIVE,newPageableRequest);

            }


        } else if (filterType.equalsIgnoreCase("productcode") ) {

            if(status == 0){

                // Get the result set for the product code
                redemptions = redemptionRepository.findByRdmMerchantNoAndRdmProductCodeAndRdmRecordStatus(rdmMerchantNo,query,RecordStatus.RECORD_STATUS_ACTIVE,newPageableRequest);

            } else {

                // Get the result set for the product code
                redemptions = redemptionRepository.findByRdmMerchantNoAndRdmProductCodeAndRdmStatusAndRdmRecordStatus(rdmMerchantNo,query,status,RecordStatus.RECORD_STATUS_ACTIVE,newPageableRequest);

            }


        } else if ( filterType.equals("0") && query.equals("0") ) {

            if(status == 0){

                // Get the redmeption page for no filtering
                redemptions = redemptionRepository.findByRdmMerchantNoAndRdmRecordStatus(rdmMerchantNo, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            } else {

                // Get the redmeption page for no filtering
                redemptions = redemptionRepository.findByRdmMerchantNoAndRdmStatusAndRdmRecordStatus(rdmMerchantNo, status, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            }

        }


        // Return the redemptionPage object
        return redemptions;

    }


    /**
     * Function to get the List of CustomerRewardExpiry entries based on the linked loyalty
     * Here we pass the primary loyaltyid ( the loyalty id of the customer who is redeeming)
     * and the reward currency id of the currency that is needed for redemption
     *
     * @param primary       : The primary customer object
     * @param rwdId         : The reward currency id for the reward currency
     * @return              : CustomerRewardExpiry List
     */
    public List<CustomerRewardExpiry> getCustomerRewardExpiryList(Customer primary,Long rwdId) {

        // List holding the CustomerRewardExpiries in a List of HashMaps
        List<CustomerRewardExpiry> listCustomerRewardExpiry = new ArrayList<CustomerRewardExpiry>();

        // Now the get the list of CustomerRewardExpiry entry for the current customer
        // Get the CustomerRewardExpiry object for the cusotmer for the given reward currency id
        List<CustomerRewardExpiry> customerRewardExpiryList = customerRewardExpiryService.findByCreLoyaltyIdAndCreRewardCurrencyId(
                primary.getCusLoyaltyId(),
                rwdId
        );

        // If the returned value is not null, then continue to the next item
        if ( customerRewardExpiryList != null ) {

            // Add the list to the listCustomerRewardExpiry
            listCustomerRewardExpiry.addAll(customerRewardExpiryList);

        }



        // Get the list of linked loyalty members
        List<LinkedLoyalty> linkedLoyaltyList = linkedLoyaltyService.findByLilParentCustomerNo(primary.getCusCustomerNo());

        // If there are no linked customers, then we just initialize it to an empty array
        if ( linkedLoyaltyList == null ) {

            linkedLoyaltyList = new ArrayList<LinkedLoyalty>();

        }




        // Go through each of the llCustomers and then get the reward expiry for them
        for(LinkedLoyalty linkedLoyalty: linkedLoyaltyList) {

            // Get the customer object
            Customer customer = customerService.findByCusCustomerNo(linkedLoyalty.getLilChildCustomerNo());

            // Get the CustomerRewardExpiry object for the cusotmer for the given reward currency id
            customerRewardExpiryList = customerRewardExpiryService.findByCreLoyaltyIdAndCreRewardCurrencyId(
                    customer.getCusLoyaltyId(),
                    rwdId
            );


            // If the returned value is not null, then continue to the next item
            if ( customerRewardExpiryList == null ) continue;


            // Add the list to the listCustomerRewardExpiry
            listCustomerRewardExpiry.addAll(customerRewardExpiryList);


        }


        // Before we return the List, we need to sort the CustomerRewardExpiry objects using the
        // expiry date field of the CRE table
        //
        // Create the BeanComparator with the field as creExpiryDt
        BeanComparator fieldComparator = new BeanComparator("creExpiryDt");

        // Sort the List
        Collections.sort(listCustomerRewardExpiry, fieldComparator);


        // Return the list
        return listCustomerRewardExpiry;

    }



    @Override
    public Redemption saveRedemption(Redemption redemption) {

        // Save the redemption
        redemption = redemptionRepository.save(redemption);

        // return the redemption object
        return redemption;

    }

    @Override
    public boolean deleteRedemption(Long rdmId) throws InspireNetzException {

        // Delete the redemption object with the given rdmId
        redemptionRepository.delete(rdmId);

        // Return true
        return true;

    }

    @Override
    public CatalogueRedemptionResponse validateAndDoCatalogueRedemption(CatalogueRedemptionRequest catalogueRedemptionRequest) throws InspireNetzException {

        //check the access rights of the user
        authSessionUtils.validateFunctionAccess(FunctionCode.FNC_ADD_REDEMPTION_DATA);

        return doCatalogueRedemption(catalogueRedemptionRequest);
    }

    @Override
    public boolean validateAndDeleteRedemption(Long rdmId) throws InspireNetzException {


        //check the access rights of the user
        authSessionUtils.validateFunctionAccess(FunctionCode.FNC_ADD_REDEMPTION_DATA);

        return deleteRedemption(rdmId);
    }


    @Override
    public Redemption findByRdmId(Long rdmId) {

        // Get the Redemption object
        Redemption redemption = redemptionRepository.findByRdmId(rdmId);

        // Return the object
        return redemption;

    }


    @Override
    public Page<Redemption> findByRdmMerchantNoAndRdmLoyaltyIdAndRdmDateBetween(Long rdmMerchantNo, String rdmLoyaltyId, java.sql.Date rdmStartDate, java.sql.Date rdmEndDate, Pageable pageable) {

        // Get the list of items
        Page<Redemption> redemptionPage = redemptionRepository.findByRdmMerchantNoAndRdmLoyaltyIdAndRdmRecordStatusAndRdmDateBetween(rdmMerchantNo, rdmLoyaltyId, RecordStatus.RECORD_STATUS_ACTIVE, rdmStartDate, rdmEndDate, pageable);

        // Return the age
        return redemptionPage;

    }

    @Override
    public Page<Redemption> findByRdmMerchantNoAndRdmTypeAndRdmDateBetween(Long rdmMerchantNo, int rdmType, java.sql.Date rdmStartDate, java.sql.Date rdmEndDate, Pageable pageable) {


        // Get the list of items
        Page<Redemption> redemptionPage = redemptionRepository.findByRdmMerchantNoAndRdmTypeAndRdmRecordStatusAndRdmDateBetween(rdmMerchantNo, rdmType, RecordStatus.RECORD_STATUS_ACTIVE, rdmStartDate, rdmEndDate, pageable);

        // Return the age
        return redemptionPage;

    }

    @Override
    public Page<Redemption> findByRdmMerchantNoAndRdmLoyaltyIdAndRdmTypeAndRdmDateBetween(Long rdmMerchantNo, String rdmLoyaltyId, Integer rdmType, java.sql.Date rdmStartDate, java.sql.Date rdmEndDate, Pageable pageable) {

        // Get the redemption page
        Page<Redemption> redemptionPage = redemptionRepository.findByRdmMerchantNoAndRdmLoyaltyIdAndRdmTypeAndRdmRecordStatusAndRdmDateBetween(rdmMerchantNo, rdmLoyaltyId, rdmType, RecordStatus.RECORD_STATUS_ACTIVE, rdmStartDate, rdmEndDate, pageable);

        // Return the page
        return  redemptionPage;

    }

    @Override
    public CatalogueRedemptionItemResponse redeemCatalogueItems(CatalogueRedemptionItemRequest catalogueRedemptionItemRequest) throws InspireNetzException {

        String loyaltyId = catalogueRedemptionItemRequest.getLoyaltyId();
        String prdCode = catalogueRedemptionItemRequest.getPrdCode();
        String destLoyaltyId = catalogueRedemptionItemRequest.getDestLoyaltyId();
        Long merchantNo = catalogueRedemptionItemRequest.getMerchantNo();
        Integer rdmChannel = catalogueRedemptionItemRequest.getChannel();
        Integer quantity = catalogueRedemptionItemRequest.getQty();
        String address1= catalogueRedemptionItemRequest.getAddress1();
        String address2= catalogueRedemptionItemRequest.getAddress2();
        String address3= catalogueRedemptionItemRequest.getAddress3();
        String city = catalogueRedemptionItemRequest.getCity();
        String state = catalogueRedemptionItemRequest.getState();
        String country = catalogueRedemptionItemRequest.getCountry();
        String contactNo = catalogueRedemptionItemRequest.getCatContactNo();
        String pinCode = catalogueRedemptionItemRequest.getPincode();


        MessageWrapper messageWrapper = generalUtils.getMessageWrapperObject("",loyaltyId,"","","",generalUtils.getDefaultMerchantNo(),new HashMap<String, String>(0),MessageSpielChannel.ALL,IndicatorStatus.YES);

        //variale for storing the errorcodes
        APIErrorCode apiErrorCode = null;

        //object for storing destination customer data, if pasa rewards
        Customer destCustomer = null;

        //check the user type if the user type is 5 the pick the loyalty id based on user
        //get loyalty id based on user
        Long userNo =authSessionUtils.getUserNo();

        //get user type of the customer
        int userType =authSessionUtils.getUserType();


        if(userType ==5){

            //find customer details of current user based on user number
            Customer customer =customerService.findByCusUserNoAndCusMerchantNoAndCusStatus(userNo,merchantNo, CustomerStatus.ACTIVE);

            //check customer null or not
            if(customer ==null){

                log.info("Transaction Service Impl->searchCustomerTransaction: customer not a loyalty member  ");

                throw new InspireNetzException(APIErrorCode.ERR_NO_LOYALTY_ID);
            }

            //get loyalty id of the customer
            loyaltyId = customer.getCusLoyaltyId()==null?"":customer.getCusLoyaltyId();

            //set to redemptionm rewuest object
            catalogueRedemptionItemRequest.setLoyaltyId(loyaltyId);

        }
/*
        //catalogue redemption request object
        CatalogueRedemptionItemRequest catalogueRedemptionItemRequest = new CatalogueRedemptionItemRequest();*/

        //catalogue redemption response object
        CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();

        //String trackingId
        String trackingId = "";

        //Get the customer details
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(loyaltyId,merchantNo);

        //check customer exists
        if(customer == null){

            //log error
            log.error("Catalogue Redemption : Customer not Found ");

            messageWrapper.setSpielName(MessageSpielValue.GENERAL_ERROR_MESSAGE);
            messageWrapper.setMobile(loyaltyId);
            messageWrapper.setMerchantNo(merchantNo);
            messageWrapper.setChannel(MessageSpielChannel.SMS);
            messageWrapper.setIsCustomer(IndicatorStatus.NO);

            userMessagingService.transmitNotification(messageWrapper);

            //log the activity
            customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Redemption failed , Invalid Request",merchantNo,prdCode);

            // throw InspireNetzException
            throw new InspireNetzException(APIErrorCode.ERR_NO_DATA_FOUND);

        }

        messageWrapper.setMerchantNo(customer.getCusMerchantNo());

        //if redemption type is pasa rewards , check the destination customer details
        if(!destLoyaltyId.equals("0")){

            //check if the destination customer is active
            destCustomer = customerService.findByCusMobileAndCusMerchantNo(destLoyaltyId,merchantNo);

            //check customer exists
            if(destCustomer == null || destCustomer.getCusStatus() != CustomerStatus.ACTIVE || destCustomer.getCusRegisterStatus().intValue()==IndicatorStatus.NO){

                //log error
                log.error("Catalogue Redemption : Destination Customer not Found ");

                messageWrapper.setSpielName(MessageSpielValue.GENERAL_ERROR_MESSAGE);
                messageWrapper.setLoyaltyId(customer.getCusLoyaltyId());
                messageWrapper.setMerchantNo(customer.getCusMerchantNo());
                messageWrapper.setChannel(MessageSpielChannel.ALL);
                messageWrapper.setIsCustomer(IndicatorStatus.YES);


                userMessagingService.transmitNotification(messageWrapper);

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Redemption failed , pasa rewards recipient is inactive",merchantNo,prdCode);

                // throw InspireNetzException
                throw new InspireNetzException(APIErrorCode.ERR_PASA_REWARDS_INVALID_DESTINATION);

            } else if(loyaltyId.equals(destLoyaltyId)){

                log.error("Catalogue Redemption : Pasa rewards , source and destination are same ");

                messageWrapper.setSpielName(MessageSpielValue.GENERAL_ERROR_MESSAGE);
                messageWrapper.setLoyaltyId(customer.getCusLoyaltyId());
                messageWrapper.setMerchantNo(customer.getCusMerchantNo());
                messageWrapper.setChannel(MessageSpielChannel.ALL);
                messageWrapper.setIsCustomer(IndicatorStatus.YES);

                userMessagingService.transmitNotification(messageWrapper);

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Redemption failed , pasa rewards recipient and requestor are same",merchantNo,prdCode);

                // throw InspireNetzException
                throw new InspireNetzException(APIErrorCode.ERR_PASA_REWARDS_SOURCE_DESTINATION_SAME);
            }

        }

        //log the activity
        customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION_ENQUIRY,"Redemption inquiry ("+prdCode+")",merchantNo,prdCode);

        //Get catalogue details using product code
        Catalogue catalogue = catalogueService.findByCatProductCodeAndCatMerchantNo(prdCode,merchantNo);

        // If the catalogue is null, then we need to throw the error and send message
        if(catalogue == null){

            //log error
            log.error("Catalogue Redemption : No catalogue item found...");

            messageWrapper.setSpielName(MessageSpielValue.REDEMPTION_FAILED_INVALID_ITEM);
            messageWrapper.setLoyaltyId(customer.getCusLoyaltyId());
            messageWrapper.setMerchantNo(customer.getCusMerchantNo());
            messageWrapper.setChannel(MessageSpielChannel.ALL);
            messageWrapper.setIsCustomer(IndicatorStatus.YES);

            userMessagingService.transmitNotification(messageWrapper);

            // Throw the exception
            throw new InspireNetzException(APIErrorCode.ERR_PRODUCT_NOT_FOUND);

        }

        //set the redemption channel to the catalogue
        catalogue.setRdmChannel(rdmChannel);

        //get reward currency of catalogue item
        Long rwdCurrencyId  = catalogue.getCatRewardCurrencyId();

        //Get the catalogue redemption object
        CatalogueRedemption catalogueRedemption = catalogueService.getCatalogueRedemption(catalogue);

        try{

            //get the catalogue redemption request object
            catalogueRedemptionItemRequest = getRedemptionRequestObject(customer,catalogue);

            //set customer's loyalty id as credit loyalty id
            catalogueRedemptionItemRequest.setCreditLoyaltyId(customer.getCusLoyaltyId());

            //set customer
            catalogueRedemptionItemRequest.setCreditCustomerNo(customer.getCusCustomerNo());

            //if redemption is pasa rewards , set destination loyalty id
            if(!destLoyaltyId.equals("0")){

                //set destination loyalty id
                destCustomer = customerService.findByCusMobileAndCusMerchantNo(destLoyaltyId,merchantNo);

                //set destination customer loyalty id
                catalogueRedemptionItemRequest.setDestLoyaltyId(destCustomer.getCusLoyaltyId());

                //set pasa rewards to true
                catalogueRedemptionItemRequest.setPasaRewards(true);

                //set customer's loyalty id as credit loyalty id
                catalogueRedemptionItemRequest.setCreditLoyaltyId(destCustomer.getCusLoyaltyId());

                //set customer
                catalogueRedemptionItemRequest.setCreditCustomerNo(destCustomer.getCusCustomerNo());

                //set the catType
                catalogueRedemptionItemRequest.setCatType(catalogue.getCatType());


            }
            // check if the cataloguedelivery type is home delivery
            if(catalogue.getCatDeliveryType() == DeliveryType.HOME_DELIVERY){

                catalogueRedemptionItemRequest.setAddress1(address1);
                catalogueRedemptionItemRequest.setAddress2(address2);
                catalogueRedemptionItemRequest.setAddress3(address3);
                catalogueRedemptionItemRequest.setCity(city);
                catalogueRedemptionItemRequest.setState(state);
                catalogueRedemptionItemRequest.setCountry(country);
                catalogueRedemptionItemRequest.setPincode(pinCode);
                catalogueRedemptionItemRequest.setCatContactNo(contactNo);
                catalogueRedemptionItemRequest.setCatDeliveryType(DeliveryType.HOME_DELIVERY);
            }
            //check the general rules for the redemption request
            checkGeneralRulesValidity(catalogueRedemptionItemRequest);

            //check if the request is valid
            catalogueRedemption.isRequestValid(catalogueRedemptionItemRequest);




        }catch(InspireNetzException e){

            //log error
            log.error("redeemCatalogueItems : Error code -"+e.getErrorCode());

            //print stack trace
            e.printStackTrace();

            if(e.getErrorCode() != APIErrorCode.ERR_INVALID_POSTPAID_PLAN){

                messageWrapper.setSpielName(MessageSpielValue.GENERAL_ERROR_MESSAGE);
                messageWrapper.setLoyaltyId(customer.getCusLoyaltyId());
                messageWrapper.setMerchantNo(customer.getCusMerchantNo());
                messageWrapper.setChannel(MessageSpielChannel.ALL);
                messageWrapper.setIsCustomer(IndicatorStatus.YES);

                userMessagingService.transmitNotification(messageWrapper);

            }

            //log the activity
            customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,getRedemptionErrorMessage(e.getErrorCode()),merchantNo,prdCode);

            //for check error code is catalogue item is not supported passa reward
            if(e.getErrorCode() ==APIErrorCode.ERR_CATALOGUE_ITEM_NOT_ALLOW_PASSA_REWARD){

                throw new InspireNetzException(APIErrorCode.ERR_CATALOGUE_ITEM_NOT_ALLOW_PASSA_REWARD);
            }

            throw new InspireNetzException(APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED);

        }

        List<String > voucherCodes = new ArrayList<String>();

        //if quantity is greater tahn zero call redeem points multiplte times
        for(int i=0;i<quantity;i++){

            //get the catalogue redemption request object
            catalogueRedemptionItemRequest = getRedemptionRequestObject(customer,catalogue);

            //if redemption is pasa rewards , set destination loyalty id
            if(!destLoyaltyId.equals("0")){


                //set destination loyalty id
                destCustomer = customerService.findByCusMobileAndCusMerchantNo(destLoyaltyId,merchantNo);

                //set destination loyalty id
                catalogueRedemptionItemRequest.setDestLoyaltyId(destCustomer.getCusLoyaltyId());

                //set credit loyalty id as destination customer's loyaltyId
                catalogueRedemptionItemRequest.setCreditLoyaltyId(destCustomer.getCusLoyaltyId());

                //set credit customer no
                catalogueRedemptionItemRequest.setCreditCustomerNo(destCustomer.getCusCustomerNo());

                //set credit loyalty id as destination customer's loyaltyId
                catalogueRedemptionItemRequest.setDebitLoyaltyId(customer.getCusLoyaltyId());

                //set credit customer no
                catalogueRedemptionItemRequest.setDebitCustomerNo(customer.getCusCustomerNo());

                //set pasa rewards to true
                catalogueRedemptionItemRequest.setPasaRewards(true);


            } else {

                //set customer loyalty id
                catalogueRedemptionItemRequest.setCreditLoyaltyId(customer.getCusLoyaltyId());

                //set customer
                catalogueRedemptionItemRequest.setCreditCustomerNo(customer.getCusCustomerNo());

                //set pasa rewards to False
                catalogueRedemptionItemRequest.setPasaRewards(false);

                //set credit customer no
                catalogueRedemptionItemRequest.setDebitCustomerNo(customer.getCusCustomerNo());


            }

            // check if the cataloguedelivery type is home delivery
            if(catalogue.getCatDeliveryType() == DeliveryType.HOME_DELIVERY){

                catalogueRedemptionItemRequest.setCatDeliveryType(DeliveryType.HOME_DELIVERY);
                catalogueRedemptionItemRequest.setAddress1(address1);
                catalogueRedemptionItemRequest.setAddress2(address2);
                catalogueRedemptionItemRequest.setAddress3(address3);
                catalogueRedemptionItemRequest.setCity(city);
                catalogueRedemptionItemRequest.setState(state);
                catalogueRedemptionItemRequest.setCountry(country);
                catalogueRedemptionItemRequest.setPincode(pinCode);
                catalogueRedemptionItemRequest.setCatContactNo(contactNo);


            }

            try{

                //process the redemption type
                catalogueRedemptionItemResponse = processCatalogueRedemption(catalogueRedemption,catalogueRedemptionItemRequest);

                //get the tracking id
                trackingId = catalogueRedemptionItemResponse.getTracking_id();

                //if voucher code is present , then add it to the voucher code list
                if(catalogueRedemptionItemResponse.getVoucherCode() != null){

                    //add to voucher code list
                    voucherCodes.add(catalogueRedemptionItemResponse.getVoucherCode());

                }



            }catch(InspireNetzException ex){

                apiErrorCode = ex.getErrorCode();

            }

        }

        if(apiErrorCode != null){

            throw new InspireNetzException(apiErrorCode);
        }

        //if redemption is success , get the customer reward balance
        List<CustomerRewardBalance> customerRewardBalances = customerRewardBalanceService.searchBalances(merchantNo, loyaltyId, rwdCurrencyId);

        //set customer reward balance
        catalogueRedemptionItemResponse.setCrbRewardBalance(customerRewardBalances.get(0).getCrbRewardBalance());

        //set tracking id for the redemption
        catalogueRedemptionItemResponse.setTracking_id(trackingId);

        //set the voucher codes
        catalogueRedemptionItemResponse.setVoucherCodeList(voucherCodes);

        String redemptionCode = "";

        //check the voucher code list
        if(voucherCodes != null && voucherCodes.size()>0){

            //get the first voucher code
            redemptionCode  = voucherCodes.get(0);

        } else {

            //set product code as voucher code
            redemptionCode = prdCode;

        }

        //log the activity
        customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Redeemed "+redemptionCode+ " ("+generalUtils.getFormattedValue(catalogue.getCatNumPoints())+" points) ",merchantNo,prdCode);

        //send notification message contact person
        sendNotificationMessageToMerchant(loyaltyId, trackingId, merchantNo, prdCode);

        if(catalogueRedemptionItemRequest.isPasaRewards()){

            //log the activity
            customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Gift rewards( "+prdCode+ " ) to "+ catalogueRedemptionItemRequest.getDestLoyaltyId(),merchantNo,prdCode);

            //log the activity
            customerActivityService.logActivity(catalogueRedemptionItemRequest.getDestLoyaltyId(),CustomerActivityType.REDEMPTION,"Gift rewards( "+redemptionCode+ " ) from "+ catalogueRedemptionItemRequest.getLoyaltyId(),merchantNo,prdCode);

        }
        //return the reward balance object
        return catalogueRedemptionItemResponse;

    }

    private void sendNotificationMessageToMerchant(String loyaltyId, String trackingId, Long merchantNo, String prdCode) throws InspireNetzException {

        //get the merchant settings enable redemption notification
        boolean isEnabledMerchantSettings = merchantSettingService.isSettingEnabledForMerchant(AdminSettingsConfiguration.MER_ENABLE_REDEMPTION_NOTIFICATION,merchantNo);

        //get the catalogue name
        Catalogue catalogue =catalogueService.findByCatProductCodeAndCatMerchantNo(prdCode,merchantNo);

        if(isEnabledMerchantSettings){

            log.info("Merchant Enabled Redemption Notification+-------------merchant No="+merchantNo);

            //get the contact mobile and email id of the merchant
            Merchant merchant =merchantService.findByMerMerchantNo(merchantNo);

            //get the customer information
            Customer customer =customerService.findByCusLoyaltyIdAndCusMerchantNo(loyaltyId,merchantNo);

            //create message wrapper object
            MessageWrapper messageWrapper =new MessageWrapper();

            messageWrapper.setMerchantNo(merchantNo);
            messageWrapper.setEmailId(merchant.getMerContactEmail()==null?"":merchant.getMerContactEmail());
            messageWrapper.setEmailSubject(merchant.getMerMerchantName() +" Redemption Notification");
            messageWrapper.setMobile(merchant.getMerPhoneNo());
            messageWrapper.setIsCustomer(IndicatorStatus.NO);

            //message param
            HashMap<String ,String > smsParam = new HashMap<>(0);
            smsParam.put("#customerLoyaltyId",loyaltyId);
            smsParam.put("#customerMobile",customer.getCusMobile());
            smsParam.put("#customerEmail",customer.getCusEmail());
            smsParam.put("#productName",catalogue.getCatDescription());

            smsParam.put("#trackingId",trackingId);
            smsParam.put("#customerName",(customer.getCusFName())+"  "+(customer.getCusLName()==null?"":customer.getCusLName()));
            smsParam.put("#date",new Date().toString());
            smsParam.put("#merchantName",merchant.getMerMerchantName());

            messageWrapper.setChannel(MessageSpielChannel.ALL);
            messageWrapper.setParams(smsParam);
            messageWrapper.setSpielName(MessageSpielValue.MERCHANT_REDEMPTION_NOTIFICATION_SPIEL);

            userMessagingService.transmitNotification(messageWrapper);

            log.info("Merchant Notification Information +--------------"+messageWrapper.toString());
        }





    }

    @Override
    public CatalogueRedemptionItemResponse redeemCatalogueItemsForUser(String userLoginId, String prdCode, Integer quantity, Long merchantNo, Integer rdmChannel, String destLoyaltyId) throws InspireNetzException {

        //get user object
        User user=userService.findByUsrLoginId(userLoginId);

        if(user==null||user.getUsrUserNo()==null){

            //log the info
            log.info("No User Information Found");

            //throw exception
            throw new InspireNetzException(APIErrorCode.ERR_NO_DATA);
        }

        //get member customers,if catMerchantNo is zero or default merchant no return all members
        List<Customer> customers=customerService.getUserMemberships(merchantNo,user.getUsrUserNo(),CustomerStatus.ACTIVE);

        if(customers==null ||customers.isEmpty()){

            //log the info
            log.info("No Customer Information Found");

            return null;

        }

        //catalogue redemption response object
        CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();

        Customer customer=customers.get(0);

        // Create a catalogue redemption item request object
        CatalogueRedemptionItemRequest catalogueRedemptionItemRequest = new CatalogueRedemptionItemRequest();

        catalogueRedemptionItemRequest.setLoyaltyId(customer.getCusLoyaltyId());
        catalogueRedemptionItemRequest.setMerchantNo(customer.getCusMerchantNo());
        catalogueRedemptionItemRequest.setChannel(rdmChannel);
        catalogueRedemptionItemRequest.setQty(quantity);
        catalogueRedemptionItemRequest.setDestLoyaltyId(destLoyaltyId);
        catalogueRedemptionItemRequest.setPrdCode(prdCode);

        catalogueRedemptionItemResponse=redeemCatalogueItems(catalogueRedemptionItemRequest);

        return catalogueRedemptionItemResponse;


    }


    @Override
    public CatalogueRedemptionItemResponse processCatalogueRedemption(CatalogueRedemption catalogueRedemption, CatalogueRedemptionItemRequest catalogueRedemptionItemRequest) throws InspireNetzException {

        //get the redemption requested customer's loyalty id
        String loyaltyId = catalogueRedemptionItemRequest.getLoyaltyId();

        //get merchant no
        Long merchantNo = catalogueRedemptionItemRequest.getMerchantNo();

        //get product code
        String prdCode = catalogueRedemptionItemRequest.getCatProductCode();

        //get the catalogue items points
        Double catNumPoints  = catalogueRedemptionItemRequest.getCatNumPoints();

        //get reward currency id
        Long rewardCurrencyId = catalogueRedemptionItemRequest.getCatRewardCurrencyId();

        //get the account details of the points debited account
        String debitLoyaltyId = catalogueRedemptionItemRequest.getDebitLoyaltyId();

        //initialize apiError code for catching errors
        APIErrorCode apiErrorCode = null;

        //create redemption response object
        CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();

        try{

            //redeem points
            catalogueRedemptionItemResponse = catalogueRedemption.redeemPoints(catalogueRedemptionItemRequest);

        }catch(InspireNetzException ex){

            //log error
            log.error("redeemCatalogueItems : Error code -"+ex.getErrorCode());

            //print stack trace
            ex.printStackTrace();

            apiErrorCode = ex.getErrorCode();

        }catch(Exception e){

            //log error
            log.error("processCatalogueRedemption :"+e);

            //print stack trace
            e.printStackTrace();

            //log the activity
            customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Redemption failed for "+catalogueRedemptionItemRequest.getPrdCode(),merchantNo,prdCode);

            //get the customer to which reward adjustment to be made
            Customer rwdAdjustCustomer = accountBundlingUtils.getPrimaryCustomerForCustomer( merchantNo ,loyaltyId);

            String adLoyaltyId  = rwdAdjustCustomer != null ?rwdAdjustCustomer.getCusLoyaltyId():loyaltyId;

            //create reward adjustment object
            RewardAdjustment rewardAdjustment = createRewardAdjustmentObject(merchantNo,adLoyaltyId,rewardCurrencyId,catNumPoints,false,0l,"",catalogueRedemptionItemRequest.getPrdCode());

            //reverse the points redeemed
            customerRewardBalanceService.awardPointsForRewardAdjustment(rewardAdjustment);

            //throw error
            throw new InspireNetzException(getRedemptionErrorCode(catalogueRedemptionItemResponse.getRdmStatus()));


        }

        if(catalogueRedemptionItemResponse.getRdmStatus() != CatalogueRedemptionStatus.CAT_RDM_STATUS_SUCCESS){


            if(apiErrorCode != null && apiErrorCode.equals(APIErrorCode.ERR_INSUFFICIENT_POINT_BALANCE)){

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,"Redemption failed , Insufficient point balance",merchantNo,prdCode);

                //log error
                log.error("processCatalogueRedemption :  Redemption failed , insufficient point balance");

                throw new InspireNetzException(APIErrorCode.ERR_INSUFFICIENT_POINT_BALANCE);

            } else if (apiErrorCode != null && apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_WAITING_FOR_APPROVAL) ) {

                //log error
                log.error("processCatalogueRedemption : Response Error Code -"+apiErrorCode);

                //log the activity
                customerActivityService.logActivity(loyaltyId, CustomerActivityType.REDEMPTION, getRedemptionErrorMessage(apiErrorCode), merchantNo, prdCode);

                //send sms to the user
                //userMessagingService.sendSMS(MessageSpielValue.REDEMPTION_REQUEST_WAITING_FOR_APPROVAL,loyaltyId,new HashMap<String, String>(0));
                MessageWrapper messageWrapper =new MessageWrapper();

                messageWrapper.setLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());
                messageWrapper.setMerchantNo(catalogueRedemptionItemRequest.getMerchantNo());
                messageWrapper.setSpielName(MessageSpielValue.REDEMPTION_REQUEST_WAITING_FOR_APPROVAL);
                messageWrapper.setIsCustomer(IndicatorStatus.YES);
                messageWrapper.setChannel(MessageSpielChannel.ALL);
                boolean msgStatus = userMessagingService.transmitNotification(messageWrapper);


                //throw redemption pending status
                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_WAITING_FOR_APPROVAL);


            } else if (apiErrorCode != null &&  apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_REQUEST_REJECTED)) {

                //log error
                log.info("processCatalogueRedemption : Response Error Code -" + apiErrorCode);

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,getRedemptionErrorMessage(apiErrorCode),merchantNo,prdCode);

                //send sms to the user
                // userMessagingService.sendSMS(MessageSpielValue.REDEMPTION_REQUEST_REJECTED,loyaltyId,new HashMap<String, String>(0));

                MessageWrapper messageWrapper =new MessageWrapper();

                messageWrapper.setLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());
                messageWrapper.setMerchantNo(catalogueRedemptionItemRequest.getMerchantNo());
                messageWrapper.setSpielName(MessageSpielValue.REDEMPTION_REQUEST_REJECTED);
                messageWrapper.setIsCustomer(IndicatorStatus.YES);
                messageWrapper.setChannel(MessageSpielChannel.ALL);
                boolean msgStatus = userMessagingService.transmitNotification(messageWrapper);
                //throw redemption pending status
                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_REQUEST_REJECTED);

            } else if (apiErrorCode != null &&  apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_NOT_ALLOWED_FOR_ACCOUNT)) {

                //log error
                log.info("processCatalogueRedemption : Response Error Code -"+apiErrorCode);

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,getRedemptionErrorMessage(apiErrorCode),merchantNo,prdCode);

                //throw redemption pending status
                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_NOT_ALLOWED_FOR_ACCOUNT);

            } else if (apiErrorCode != null &&  apiErrorCode.equals(APIErrorCode.ERR_NO_VOUCHER_CODES)) {

                //log error
                log.info("processCatalogueRedemption : Response Error Code -" + apiErrorCode);

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,getRedemptionErrorMessage(apiErrorCode),merchantNo,prdCode);

                //send sms to the user
                MessageWrapper messageWrapper =new MessageWrapper();

                messageWrapper.setLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());
                messageWrapper.setMerchantNo(catalogueRedemptionItemRequest.getMerchantNo());
                messageWrapper.setSpielName(MessageSpielValue.NO_VOUCHER_CODE_AVAILABLE);
                messageWrapper.setIsCustomer(IndicatorStatus.YES);
                messageWrapper.setChannel(MessageSpielChannel.ALL);
                boolean msgStatus = userMessagingService.transmitNotification(messageWrapper);

                //throw redemption pending status
                throw new InspireNetzException(APIErrorCode.ERR_NO_VOUCHER_CODES);

            }
            else {

                //log error
                log.error("processCatalogueRedemption : Response -" + catalogueRedemptionItemResponse);

                String errorMessage =apiErrorCode!= null?getRedemptionErrorMessage(apiErrorCode): "Redemption failed for "+catalogueRedemptionItemRequest.getPrdCode();

                //log the activity
                customerActivityService.logActivity(loyaltyId,CustomerActivityType.REDEMPTION,errorMessage,merchantNo,prdCode);


                //get the customer to which reward adjustment to be made
                Customer rwdAdjustCustomer = accountBundlingUtils.getPrimaryCustomerForCustomer( merchantNo ,loyaltyId);

                String adLoyaltyId  = rwdAdjustCustomer != null ?rwdAdjustCustomer.getCusLoyaltyId():loyaltyId;

                //create reward adjustment object
                RewardAdjustment rewardAdjustment = createRewardAdjustmentObject(merchantNo,adLoyaltyId,rewardCurrencyId,catNumPoints,false,0L,catalogueRedemptionItemResponse.getRdmId()+"",catalogueRedemptionItemRequest.getPrdCode());

                //reverse the points redeemed
                customerRewardBalanceService.awardPointsForRewardAdjustment(rewardAdjustment);

            }

            //throw error
            throw new InspireNetzException(getRedemptionErrorCode(catalogueRedemptionItemResponse.getRdmStatus()));
        }

        return catalogueRedemptionItemResponse;
    }

    @Override
    public void checkPasaRewardRequestValidity(String destLoyaltyId, String prdCode,Long merchantNo) throws InspireNetzException {

        //get the customer details
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(destLoyaltyId,merchantNo);

        //get catalogue details
        Catalogue catalogue = catalogueService.findByCatProductCodeAndCatMerchantNo(prdCode,merchantNo);

        //get the preferred mega brands of the catalogue
        String catLocations = catalogue.getCatLocationValues();

        //check whether the catalogue has valid locations else throw error
        if(catLocations != null && catLocations.length() != 0){
/*

            //log error
            log.error("No Location values found for the catalogue item");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_ITEM_WITHOUT_LOCATION);
*/

            boolean hasValidLocation = generalUtils.isTokenizedValueExists(catLocations,",", customer.getCusLocation()+"");

            //if customer not in a valid mega brand throw error
            if(!hasValidLocation){

                log.error("Customer Mega Brand is not Valid");
                throw new InspireNetzException(APIErrorCode.ERR_PASA_REWARDS_INVALID_MEGA_BRAND);

            }


        }


        /*Check whether the customer is subscribed to any of the brands specified by the catalogue*/
        //Get customer subscriptions
        List<CustomerSubscription> customerSubscriptions = customerSubscriptionService.findByCsuCustomerNo(customer.getCusCustomerNo());

        //get the preferred brands from catalogue
        String catProductValues = catalogue.getCatProductValues();

        //check whether the catalogue has valid product values else throw error
        if(catProductValues != null && catProductValues.length() != 0){

            /*//log error
            log.error("No Brand values found for the catalogue item");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_ITEM_WITHOUT_BRAND);*/

            boolean hasValidBrand = false;

            //check whether any subscription of the customer matches the required brand
            if(customerSubscriptions != null && customerSubscriptions.size() > 0){

                for(CustomerSubscription customerSubscription : customerSubscriptions){

                    //calling the method to find matching brand in customer subscription
                    hasValidBrand = generalUtils.isTokenizedValueExists(catProductValues,",",customerSubscription.getCsuProductCode());

                    if(hasValidBrand){

                        break;
                    }
                }
            }

            //check customer has subscription to valid brand else throw error
            if(!hasValidBrand){

                log.error("Customer Doesn't have subscription to eligible products");

                throw new InspireNetzException(APIErrorCode.ERR_PASA_REWARDS_INVALID_SUBSCRIPTION);

            }



        }

    }

    @Override
    public Page<Redemption> searchRedemptionsForPay(Long rdmMerchantNo, Long userNo) {

        Page<Redemption> redemptionPage = null;

        List<Redemption> redemptionList = new ArrayList<Redemption>();

        //Check if the redemption merchant no is 0
        if(rdmMerchantNo == 0){

            //Get the customer details based on the logged in user
            List<Customer> customersList = customerService.findByCusUserNoAndCusStatus(userNo,CustomerStatus.ACTIVE);

            // Check if the customer list is null
            if(customersList != null && customersList.size() >0 ) {

                for ( Customer customer : customersList) {

                    //Get the redemption details
                    List<Redemption> redemptions = redemptionRepository.findByRdmMerchantNoAndRdmLoyaltyIdAndRdmTypeAndRdmRecordStatus(customer.getCusMerchantNo(),customer.getCusLoyaltyId(),RedemptionType.PAY,RecordStatus.RECORD_STATUS_ACTIVE);

                    redemptionList.addAll(redemptions);

                }

                redemptionPage = new PageImpl<>(redemptionList);

            }

        } else {

            //Get customer
            Customer customer = customerService.findByCusUserNoAndCusMerchantNoAndCusStatus(userNo,rdmMerchantNo,CustomerStatus.ACTIVE);

            //Get the redmptions
            List<Redemption> redemptions = redemptionRepository.findByRdmMerchantNoAndRdmLoyaltyIdAndRdmTypeAndRdmRecordStatus(customer.getCusMerchantNo(),customer.getCusLoyaltyId(),RedemptionType.PAY,RecordStatus.RECORD_STATUS_ACTIVE);

            redemptionPage = new PageImpl<>(redemptions);

        }
        return redemptionPage;
    }

    private APIErrorCode getRedemptionErrorCode(int status) {

        APIErrorCode apiErrorCode = APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED;

        switch( status) {

            case CatalogueRedemptionStatus.CAT_RDM_EXTERNAL_SERVICE_FAILED:

                apiErrorCode =  APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED;

            case CatalogueRedemptionStatus.CAT_RDM_BCODE_GENERATION_FAILED:

                apiErrorCode =  APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED;

            case CatalogueRedemptionStatus.CAT_RDM_BILL_REBATE_REQUEST_FAILED:

                apiErrorCode =  APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED;

            case CatalogueRedemptionStatus.CAT_RDM_VOUCHER_GENERATION_FAILED:

                apiErrorCode =  APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED;

            case CatalogueRedemptionStatus.CAT_RDM_REDEEM_FAILED:

                apiErrorCode = APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED;



        }

        return apiErrorCode;

    }

    public RewardAdjustment createRewardAdjustmentObject(Long merchantNo, String loyaltyId, Long adjCurrencyId, Double adjPoints, boolean isTierAffected, Long adjProgramNo, String adjIntReference,String adExtReference) {

        RewardAdjustment rewardAdjustment = new RewardAdjustment();

        //set values to the object
        rewardAdjustment.setLoyaltyId(loyaltyId);
        rewardAdjustment.setRwdCurrencyId(adjCurrencyId);
        rewardAdjustment.setRwdQty(adjPoints);
        rewardAdjustment.setMerchantNo(merchantNo);
        rewardAdjustment.setExternalReference(adExtReference);
        rewardAdjustment.setIntenalReference(adjIntReference);
        rewardAdjustment.setProgramNo(adjProgramNo);
        rewardAdjustment.setTierAffected(isTierAffected);
        rewardAdjustment.setPointReversed(true);

        //return the reward adjustment object
        return rewardAdjustment;
    }

    @Override
    public CatalogueRedemptionItemResponse redeemCatalogueItems(String prdCode, Integer quantity, Long merchantNo, Integer rdmChannel,String destLoyaltyId) throws InspireNetzException {

        //get customer information
        Long cusUserNo =authSessionUtils.getUserNo();

        Customer customer =customerService.findByCusUserNoAndCusMerchantNo(cusUserNo,merchantNo);

        if(customer ==null || customer.getCusRegisterStatus().intValue() ==IndicatorStatus.NO){

            throw new InspireNetzException(APIErrorCode.ERR_NO_LOYALTY_ID);
        }

        //get
        CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();

        // Create a catalogue redemption item request object
        CatalogueRedemptionItemRequest catalogueRedemptionItemRequest = new CatalogueRedemptionItemRequest();

        catalogueRedemptionItemRequest.setLoyaltyId(customer.getCusLoyaltyId());
        catalogueRedemptionItemRequest.setMerchantNo(customer.getCusMerchantNo());
        catalogueRedemptionItemRequest.setChannel(rdmChannel);
        catalogueRedemptionItemRequest.setQty(quantity);
        catalogueRedemptionItemRequest.setDestLoyaltyId(destLoyaltyId);
        catalogueRedemptionItemRequest.setPrdCode(prdCode);

        // Set the APIResposneObject to be response from the redueemCatalogueItems
        catalogueRedemptionItemResponse = redeemCatalogueItems(catalogueRedemptionItemRequest);

        return catalogueRedemptionItemResponse;

    }

    private void addTransactionForFailedRedemptions(Customer customer, Catalogue catalogue) {

        //create a transction object
        Transaction transaction = new Transaction();

        //set values to the transactions object
        transaction.setTxnLoyaltyId(customer.getCusLoyaltyId());
        transaction.setTxnAmount(0L);
        transaction.setTxnRewardQty(catalogue.getCatNumPoints());
        transaction.setTxnStatus(TransactionStatus.FAILED);
        transaction.setTxnDate(new java.sql.Date(System.currentTimeMillis()));
        transaction.setTxnExternalRef(catalogue.getCatProductCode());
        transaction.setTxnType(TransactionType.REDEEM);
        transaction.setTxnMerchantNo(customer.getCusMerchantNo());
        transaction.setTxnRewardCurrencyId(catalogue.getCatRewardCurrencyId());
        transaction.setTxnLocation(customer.getCusLocation());
        transaction.setTxnRewardExpDt(DBUtils.covertToSqlDate("9999-12-31"));
        transaction.setTxnInternalRef(catalogue.getCatProductCode());

        //save the transaction
        transactionService.saveTransaction(transaction);

    }

    @Override
    public boolean checkGeneralRulesValidity(CatalogueRedemptionItemRequest request) throws InspireNetzException {


        //get the customer details
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(request.getLoyaltyId(),request.getMerchantNo());

        //check the customer status , if not active throw exception
        if(customer == null || customer.getCusStatus() != CustomerStatus.ACTIVE){

            //log the error
            log.error("Customer is not active ");

            //throw exception
            throw new InspireNetzException(APIErrorCode.ERR_NO_LOYALTY_ID);

        }
        //get the customer details
        Catalogue catalogue = catalogueService.findByCatProductCodeAndCatMerchantNo(request.getCatProductCode(),request.getMerchantNo());

        if(catalogue==null){

            //log the error
            log.error("no catalogue data found");

            //throw exception
            throw new InspireNetzException(APIErrorCode.ERR_NO_DATA_FOUND);
        }
        //if destination customer is not null (If redemption is pasa rewards)
        if(!request.getDestLoyaltyId().equals("0")){

            //check catalogue item is support passa reward
            Integer pasaRewardEnabledFlag = catalogue.getCatPasaRewardsEnabled()==null?0:catalogue.getCatPasaRewardsEnabled();

            //if passareward flag is 0 catalogue item is not support passaReward
            if(pasaRewardEnabledFlag ==0){

                //log the error
                log.error("Catalogue Item is not support pasa reward");

                //throw exception
                throw new InspireNetzException(APIErrorCode.ERR_CATALOGUE_ITEM_NOT_ALLOW_PASSA_REWARD);
            }

            Customer destinationCustomer  = customerService.findByCusLoyaltyIdAndCusMerchantNo(request.getDestLoyaltyId(),request.getMerchantNo());

            if(destinationCustomer == null || destinationCustomer.getCusStatus() != CustomerStatus.ACTIVE){

                //log the error
                log.error("Destination customer is not active");

                //throw exception
                throw new InspireNetzException(APIErrorCode.ERR_NO_LOYALTY_ID);
            }

        }

        //get the channel through request came
        String channel  = request.getChannel()+"";

        //get the allowed channels for the catalogue item
        String catChannels= catalogue.getCatChannelValues();

        if(catChannels !=null){

            //check whether the channel is valid or not
            boolean isValidChannel = generalUtils.isTokenizedValueExists(catChannels,",",channel);

            if(!isValidChannel){

                //log error
                log.error("Request came through unauthorized channel");

                //throw exception
                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_CHANNEL_NOT_ALLOWED);
            }
        }

        //check whether the customer is secondary in any linked loyalty
        LinkedLoyalty linkedLoyalty = linkedLoyaltyService.findByLilChildCustomerNo(customer.getCusCustomerNo());

        //if linked loyalty found with customer as secondary ,check whether redemption is allowed for customer
        if(linkedLoyalty != null){

            //get the account bundling settings for the merchant
            AccountBundlingSetting accountBundlingSetting = accountBundlingSettingService.getDefaultAccountBundlingSetting(customer.getCusMerchantNo());

            //check if only primary can redeem,
            if(accountBundlingSetting.getAbsBundlingRedemption() == 2){

                //log the error
                log.error("Customer is secondary , caanot redeem points ");

                //throw exception
                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_SECONDARY);

            }

        }

        /*Check for the eligibility criteria specified by the catalogue
        Check whether the customer is included in any of the mega brands specified by the catalogue*/

        //get the preferred mega brands of the catalogue
        String catLocations = catalogue.getCatLocationValues();

        //check whether the catalogue has valid locations else throw error
        if(catLocations != null && catLocations.length() != 0){
/*

            //log error
            log.error("No Location values found for the catalogue item");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_ITEM_WITHOUT_LOCATION);
*/

            boolean hasValidLocation = generalUtils.isTokenizedValueExists(catLocations,",",customer.getCusLocation()+"");

            //if customer not in a valid mega brand throw error
            if(!hasValidLocation){

                log.error("Customer Mega Brand is not Valid");
                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_INVALID_LOCATION);

            }


        }


        /*Check whether the customer is subscribed to any of the brands specified by the catalogue*/
        //Get customer subscriptions
        List<CustomerSubscription> customerSubscriptions = customerSubscriptionService.findByCsuCustomerNo(customer.getCusCustomerNo());

        //get the preferred brands from catalogue
        String catProductValues = catalogue.getCatProductValues();

        //check whether the catalogue has valid product values else throw error
        if(catProductValues != null && catProductValues.length() != 0){

            /*//log error
            log.error("No Brand values found for the catalogue item");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_ITEM_WITHOUT_BRAND);*/

            boolean hasValidBrand = false;

            //check whether any subscription of the customer matches the required brand
            if(customerSubscriptions != null && customerSubscriptions.size() > 0){

                for(CustomerSubscription customerSubscription : customerSubscriptions){

                    //calling the method to find matching brand in customer subscription
                    hasValidBrand = generalUtils.isTokenizedValueExists(catProductValues,",",customerSubscription.getCsuProductCode());

                    if(hasValidBrand){

                        break;
                    }
                }
            }

            //check customer has subscription to valid brand else throw error
            if(!hasValidBrand){

                log.error("Customer Doesn't have subscription to eligible products");

                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_INVALID_BRAND);

            }



        }


        if(catalogue.getCatCustomerType().intValue() != 0){

            if(customer.getCusType() == null || catalogue.getCatCustomerType() == null){

                log.error("Customer Type not found for Customer or catalogue");

                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_NO_CUS_TYPE);

            }
            //if customer type is not similiar to eligible type throw error and set log
            if(customer.getCusType().intValue() != catalogue.getCatCustomerType().intValue()){

                log.error("Customer type not eligible for redemption");

                throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_INVALID_CUS_TYPE);
            }

        }

        //get the preferred brands from catalogue
        String catTierValues = catalogue.getCatCustomerTier();

        //check whether the catalogue has valid product values else throw error
        if(catTierValues != null && catTierValues.length() != 0){

            /*//log error
            log.error("No Brand values found for the catalogue item");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_ITEM_WITHOUT_BRAND);*/

            boolean hasValidTier = false;

            //check whether any subscription of the customer matches the required brand
            if(customer.getCusTier() != null && catTierValues.length()>0){

                //calling the method to find matching brand in customer subscription
                hasValidTier = generalUtils.isTokenizedValueExists(catTierValues,",",customer.getCusTier().toString());

                if(!hasValidTier){

                    log.error("Customer tier not eligible for redemption");

                    throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_INVALID_CUS_TIER);

                }
            }


        }

        /*Check current date is in between the prescribed time limit of catalogue redemption
        else throw error*/

        //Get the current date
        Date currentDate = new Date();

        if(catalogue.getCatStartDate() == null || catalogue.getCatEndDate() == null ){

            //log error
            log.error("Catalogue Redemption StartDate/ EndDate missing");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_NO_DATE);

        }
        // Check if the date is valid
        if ( currentDate.compareTo(catalogue.getCatStartDate()) < 0 ||
                currentDate.compareTo(catalogue.getCatEndDate()) > 0 ) {

            log.error("Current Date not inside the redemption period");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_INVALID_DATE);

        }

        if(catalogue.getCatAvailableStock() <= 0){

            log.error("Catalogue Item is out of stock");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_FAILED_ITEM_OUT_OF_STOCK);

        }

        return true;
    }

    @Override
    public CatalogueRedemptionItemResponse  redeemSingleCatalogueItem(CatalogueRedemptionItemRequest catalogueRedemptionItemRequest) throws InspireNetzException {


        // Get the merchantNo
        Long merchantNo = catalogueRedemptionItemRequest.getMerchantNo();

        // Get the loyaltyId
        String loyaltyId = catalogueRedemptionItemRequest.getLoyaltyId();

        //Get the Product Code
        String prdCode = catalogueRedemptionItemRequest.getPrdCode();

        //Get the qty
        Integer qty = catalogueRedemptionItemRequest.getQty();

        // Variable holding the total Reward quantity
        double totalRewardQty = 0;

        // Variable holding the totalCashAmount
        double totalCashAmount = 0;

        // Create the CatalogueRedemptionItemResponse object
        CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();

        // Get the  catalogue for the given product code
        Catalogue catalogue = catalogueService.findByCatProductCodeAndCatMerchantNo(catalogueRedemptionItemRequest.getPrdCode(),merchantNo);

        // If the catalogue is null, then we need to show the message
        if ( catalogue == null || catalogue.getCatProductCode() == "" ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_PRODUCT_NOT_FOUND");

            // Return the object
            return catalogueRedemptionItemResponse;

        }


        // Set the product number
        catalogueRedemptionItemResponse.setPrd_no(catalogue.getCatProductNo().toString());



        // Calculate the totalRewardQty
        totalRewardQty = catalogue.getCatNumPoints() * qty;

        // Calculate the totalCashAmount
        totalCashAmount = catalogue.getCatPartialCash() * qty;


        // Create the UniqueIdGenerator
        DBUniqueIdGenerator generator = new DBUniqueIdGenerator(dataSource);

        // Get the trackingId
        String trackingId = generalUtils.getUniqueId(loyaltyId);

        // If the trackingId is null, then the unique id generation failed
        if ( trackingId == null ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_OPERATION_FAILED");

            // Return the object
            return catalogueRedemptionItemResponse;

        }

        //set values for creating the redemption object
        catalogueRedemptionItemRequest.setQty(qty);
        catalogueRedemptionItemRequest.setTotalCashAmount(totalCashAmount);
        catalogueRedemptionItemRequest.setTotalRwdQty(totalRewardQty);
        catalogueRedemptionItemRequest.setTrackingId(trackingId);
        catalogueRedemptionItemRequest.setCatRewardCurrencyId(catalogue.getCatRewardCurrencyId());

        //check whether the redemption is allowed for the user
        catalogueRedemptionItemRequest = getRedemptionValidity(catalogueRedemptionItemRequest);

        if(catalogueRedemptionItemRequest.getEligibilityStatus() == RequestEligibilityStatus.INELIGIBLE){

            log.info("redeemSingleCatalgoueItem : Customer not eligle for redemption");

            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_NOT_ALLOWED_FOR_ACCOUNT);

        } else if(catalogueRedemptionItemRequest.getEligibilityStatus() == RequestEligibilityStatus.APPROVAL_NEEDED){

            log.info("redeemSingleCatalgoueItem : Customer eligible for redemption , approval needed");

            //check whether the redemption is authorized
            catalogueRedemptionItemRequest = processRedemptionApproval(catalogueRedemptionItemRequest);

            //if redemption is not allowed , add redemption entry and return
            if(!catalogueRedemptionItemRequest.isRedemptionAllowed()){

                if(!catalogueRedemptionItemRequest.isRedemptionApprovalStatus() ){

                    //log error
                    log.error("redeemSingleCatalogueItem: Redemption request rejected by approver");

                    //set redemption status to approval rejected equest
                    updateRedemptionStatus(catalogueRedemptionItemRequest.getRdmId(),RedemptionStatus.RDM_STATUS_REJECTED);

                    //throw error
                    throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_REQUEST_REJECTED);

                } else {

                    //log error
                    log.error("redeemSingleCatalogueItem: Redemption not allowed by approver , waiting for approval");

                    //set redemption status to approval rejected equest
                    updateRedemptionStatus(catalogueRedemptionItemRequest.getRdmId(),RedemptionStatus.RDM_STATUS_APPROVAL_WAITING);

                    //throw error
                    throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_WAITING_FOR_APPROVAL);


                }

            }

        } else {

            //call the redemption creation method
            catalogueRedemptionItemRequest = addRedemptionEntry(catalogueRedemptionItemRequest);

        }

        // Check if the redemption has been been saved
        if ( catalogueRedemptionItemRequest.getRdmId() == null ) {

            // Set the status
            catalogueRedemptionItemResponse.setStatus("failed");

            // Set the error code
            catalogueRedemptionItemResponse.setErrorcode("ERR_OPERATION_FAILED");

            // Set the tracking id
            catalogueRedemptionItemResponse.setTracking_id(trackingId);

            // Return the object
            return catalogueRedemptionItemResponse;

        }

        //get the merchant details
        Merchant merchant = merchantService.findByMerMerchantNo(merchantNo);

        //get Reward Currency Details
        RewardCurrency rewardCurrency = rewardCurrencyService.findByRwdCurrencyId(catalogue.getCatRewardCurrencyId());

        PointDeductData pointDeductData = new PointDeductData();

        Date currentDate = new Date();

        String txnDate = generalUtils.getDateAsString(currentDate);




        //create the pointDeductData object for deductPoints function of LoyaltyEngine
        pointDeductData.setLoyaltyId(catalogueRedemptionItemRequest.getDebitLoyaltyId());
        pointDeductData.setMerchantNo(merchantNo);
        pointDeductData.setRedeemQty(totalRewardQty);
        pointDeductData.setTxnAmount(0.0);
        pointDeductData.setUsrFName(catalogueRedemptionItemRequest.getUsrFName());
        pointDeductData.setUsrLName(catalogueRedemptionItemRequest.getUsrLName());
        pointDeductData.setUserNo(catalogueRedemptionItemRequest.getUserNo());
        pointDeductData.setRwdCurrencyId(catalogue.getCatRewardCurrencyId());
        pointDeductData.setTxnLocation(catalogueRedemptionItemRequest.getUserLocation());
        pointDeductData.setMerchantName(merchant.getMerMerchantName());
        pointDeductData.setRwdCurrencyName(rewardCurrency.getRwdCurrencyName());
        pointDeductData.setAuditDetails(authSessionUtils.getUserNo().toString());
        pointDeductData.setTxnDate(DBUtils.covertToSqlDate(txnDate));

        if(totalRewardQty > 0){

            // Call the deductPoints function
            loyaltyEngineService.deductPoints(pointDeductData);

        }


        Long currentStock = catalogue.getCatAvailableStock() - 1;

        //decrement the stock
        catalogue.setCatAvailableStock(currentStock);

        //save the catalogue object
        catalogue = catalogueService.saveCatalogue(catalogue);

        // If the points has been deducted, the set the redemption as completed successfully
        // Set the status
        catalogueRedemptionItemResponse.setStatus("success");

        catalogueRedemptionItemResponse.setRdmId(catalogueRedemptionItemRequest.getRdmId());

        // Set the trackingId
        catalogueRedemptionItemResponse.setTracking_id(trackingId);

        // Return the catalogueRedemptionItemResponse
        return catalogueRedemptionItemResponse;
    }

    @Override
    public void updateRedemptionStatus(Long rdmId, int rdmStatus) {

        //get the redemption
        Redemption redemption = findByRdmId(rdmId);

        //set redemption status
        if(null != redemption){

            redemption.setRdmStatus(rdmStatus);;
        }

        //save redemption
        saveRedemption(redemption);
    }

    private CatalogueRedemptionItemRequest processRedemptionApproval(CatalogueRedemptionItemRequest catalogueRedemptionItemRequest) throws InspireNetzException {

        //get the approver customer
        Customer approver = customerService.findByCusCustomerNo(catalogueRedemptionItemRequest.getApproverCustomerNo());

        //get the requestor details
        Customer requestor = customerService.findByCusCustomerNo(catalogueRedemptionItemRequest.getDebitCustomerNo());

        //if redemption id is 0 , then add a new redemption request and add a party approval request
        if(catalogueRedemptionItemRequest.getRdmId() == null || catalogueRedemptionItemRequest.getRdmId() == 0){

            log.info("redeemSingleCatalgoueItem : New redemption request received , adding entry to redemption table");

            //call the redemption creation method
            catalogueRedemptionItemRequest = addRedemptionEntry(catalogueRedemptionItemRequest);

            //send party approval
            partyApprovalService.sendApproval(requestor,approver,catalogueRedemptionItemRequest.getRdmId(),PartyApprovalType.PARTY_APPROVAL_REDEMPTION_REQUEST, catalogueRedemptionItemRequest.getCatProductCode(),"");

            log.info("redeemSingleCatalgoueItem : Approval request sent to approver account");

            //set redemption allowed to false
            catalogueRedemptionItemRequest.setRedemptionAllowed(false);

        } else{

            //check whether the
            boolean isRedemptionAllowed = isPartyApprovedRedemption(catalogueRedemptionItemRequest,approver,requestor);

            if(isRedemptionAllowed){

                log.info("redeemSingleCatalgoueItem : Redemption is approved by approver");

                //set redemption allowed to true
                catalogueRedemptionItemRequest.setRedemptionAllowed(true);

            } else {

                log.info("redeemSingleCatalgoueItem : Redemption request rejected by primary");

                //set redemption allowed to false
                catalogueRedemptionItemRequest.setRedemptionAllowed(false);

            }

        }

        return catalogueRedemptionItemRequest;
    }

    private boolean isPartyApprovedRedemption(CatalogueRedemptionItemRequest catalogueRedemptionItemRequest, Customer approver, Customer requestor) {

        // Check if there is a entry in the LinkingApproval table
        PartyApproval partyApproval = partyApprovalService.getExistingPartyApproval(approver,requestor, PartyApprovalType.PARTY_APPROVAL_REDEMPTION_REQUEST, catalogueRedemptionItemRequest.getRdmId());

        // If the partyApproval is not found, then return false
        if ( partyApproval == null) {

            // Log the information
            log.info("isPartyApproved -> Party has not approved linking");

            // return false
            return false;

        } else {

            return catalogueRedemptionItemRequest.isRedemptionApprovalStatus();
        }


    }

    private CatalogueRedemptionItemRequest getRedemptionValidity(CatalogueRedemptionItemRequest catalogueRedemptionItemRequest) throws InspireNetzException {

        //check whether the customer is linked
        List<LinkedLoyalty> linkedLoyalties = linkedLoyaltyService.getAllLinkedAccounts(catalogueRedemptionItemRequest.getDebitCustomerNo());

        if(linkedLoyalties == null || linkedLoyalties.size() == 0){

            //if not linked to any account , then customer is eligible to redeem
            catalogueRedemptionItemRequest.setEligibilityStatus(RequestEligibilityStatus.ELIGIBLE);

            //set debit loyalty id
            catalogueRedemptionItemRequest.setDebitLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());

            log.info("getRedemptionValidity : Account is not linked , eligible for redemption");

            return catalogueRedemptionItemRequest;

        } else {

            //check the account bundling settings
            AccountBundlingSetting accountBundlingSetting = accountBundlingSettingService.getAccountBundlingSetting();

            Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(catalogueRedemptionItemRequest.getDebitLoyaltyId(),catalogueRedemptionItemRequest.getMerchantNo());

            //check whether the customer is primary
            catalogueRedemptionItemRequest = isCustomerPrimary(linkedLoyalties,catalogueRedemptionItemRequest);

            //check redemption settings
            switch(accountBundlingSetting.getAbsBundlingRedemption()){

                case AccountBundlingSettingRedemption.PRIMARY_ONLY:

                    log.info("getRedemptionValidity : Redemption allowed only for primary");

                    if(catalogueRedemptionItemRequest.isCustomerPrimary()){

                        log.info("getRedemptionValidity : Requested customer is primary");

                        //set eligibility status as eligible
                        catalogueRedemptionItemRequest.setEligibilityStatus(RequestEligibilityStatus.ELIGIBLE);

                        log.info("getRedemptionValidity : Requested customer is primary , points will be debited from "+catalogueRedemptionItemRequest.getLoyaltyId());

                        //set debit loyalty id
                        catalogueRedemptionItemRequest.setDebitLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());

                    } else {


                        log.info("getRedemptionValidity : Redemption failed , requested customer is not primary");

                        //set ellgibility status to ineligible
                        catalogueRedemptionItemRequest.setEligibilityStatus(RequestEligibilityStatus.INELIGIBLE);
                    }

                    return catalogueRedemptionItemRequest;

                case AccountBundlingSettingRedemption.ANY_ACCOUNT_WITH_AUTHORIZATION:

                    log.info("getRedemptionValidity : Redemption allowed for any account with authorization from other account");

                    //set status as eligible
                    catalogueRedemptionItemRequest.setEligibilityStatus(RequestEligibilityStatus.ELIGIBLE);

                    if(catalogueRedemptionItemRequest.isCustomerPrimary()){

                        //set debit loyalty id
                        catalogueRedemptionItemRequest.setDebitLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());

                        log.info("getRedemptionValidity : Requested customer is primary , points will be debited from "+catalogueRedemptionItemRequest.getLoyaltyId());


                    } else {

                        Customer primary = accountBundlingUtils.getPrimaryCustomerForCustomer(catalogueRedemptionItemRequest.getMerchantNo(),catalogueRedemptionItemRequest.getLoyaltyId());

                        //set debit loyalty id
                        catalogueRedemptionItemRequest.setDebitLoyaltyId(primary.getCusLoyaltyId());

                        log.info("getRedemptionValidity : Requested customer is secondary , points will be debited from "+ primary.getCusLoyaltyId());

                    }

                    return catalogueRedemptionItemRequest;

                case AccountBundlingSettingRedemption.SECONDARY_WITH_AUTHORIZATION:

                    log.info("getRedemptionValidity : Redemption settings : Secondary can redeeom with authorization from primary");
                    if(catalogueRedemptionItemRequest.isCustomerPrimary()){

                        //set eligibility status as eligible
                        catalogueRedemptionItemRequest.setEligibilityStatus(RequestEligibilityStatus.ELIGIBLE);

                        log.info("getRedemptionValidity : Requested customer is primary , points will be debited from "+catalogueRedemptionItemRequest.getLoyaltyId());

                        //set debit loyalty id
                        catalogueRedemptionItemRequest.setDebitLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());


                    } else {

                        //set eligibility status as eligible
                        catalogueRedemptionItemRequest.setEligibilityStatus(RequestEligibilityStatus.APPROVAL_NEEDED);

                        Customer primary = accountBundlingUtils.getPrimaryCustomerForCustomer(catalogueRedemptionItemRequest.getMerchantNo(),catalogueRedemptionItemRequest.getLoyaltyId());

                        //set debit loyalty id
                        catalogueRedemptionItemRequest.setDebitLoyaltyId(primary.getCusLoyaltyId());

                        log.info("getRedemptionValidity : Requested customer is secondary , points will be debited from "+primary.getCusLoyaltyId());

                    }

                    //set debit loyalty id
                    catalogueRedemptionItemRequest.setDebitLoyaltyId(catalogueRedemptionItemRequest.getDebitLoyaltyId());


                    return  catalogueRedemptionItemRequest;

            }

            return catalogueRedemptionItemRequest;
        }
    }

    public CatalogueRedemptionItemRequest isCustomerPrimary(List<LinkedLoyalty> linkedLoyalties , CatalogueRedemptionItemRequest catalogueRedemptionItemRequest){


        //iterate through the linked loyalties
        for(LinkedLoyalty linkedLoyalty : linkedLoyalties){

            //if the customer is primary , approval is needed from secondary
            if(linkedLoyalty.getLilParentCustomerNo().longValue() == catalogueRedemptionItemRequest.getDebitCustomerNo()){

                //set customer primary as true
                catalogueRedemptionItemRequest.setCustomerPrimary(true);

                //set approver customer no
                catalogueRedemptionItemRequest.setApproverCustomerNo(linkedLoyalty.getLilParentCustomerNo());

            } else {

                //set customer primary as true
                catalogueRedemptionItemRequest.setCustomerPrimary(false);

                //set approver customer no
                catalogueRedemptionItemRequest.setApproverCustomerNo(linkedLoyalty.getLilParentCustomerNo());

            }

        }

        //return redemption request object
        return catalogueRedemptionItemRequest;

    }

    public CatalogueRedemptionItemRequest addRedemptionEntry(CatalogueRedemptionItemRequest catalogueRedemptionItemRequest){

        // Create the Redemption object
        Redemption redemption = new Redemption();

        // Set the fields in the Redemption
        //
        // Set the rdmMerchatNo
        redemption.setRdmMerchantNo(catalogueRedemptionItemRequest.getMerchantNo());

        // Set the status
        redemption.setRdmStatus(RedemptionStatus.RDM_STATUS_FAILED);

        // Set the type
        redemption.setRdmType(RedemptionType.CATALOGUE);

        // Set the reward currency id
        redemption.setRdmRewardCurrencyId(catalogueRedemptionItemRequest.getCatRewardCurrencyId());

        // set the reward quantity
        redemption.setRdmRewardCurrencyQty(catalogueRedemptionItemRequest.getTotalRwdQty());

        // set the product code
        redemption.setRdmProductCode(catalogueRedemptionItemRequest.getCatProductCode());

        // set quantity
        redemption.setRdmQty(catalogueRedemptionItemRequest.getQty());

        // Set the loyalty id
        redemption.setRdmLoyaltyId(catalogueRedemptionItemRequest.getLoyaltyId());

        // Set the delivery ind to be 0
        redemption.setRdmDeliveryInd(catalogueRedemptionItemRequest.getDeliveryInd());

        // Set the totalCashAmount
        redemption.setRdmCashAmount(catalogueRedemptionItemRequest.getTotalCashAmount());

        // Set the date
        redemption.setRdmDate(new java.sql.Date(new Date().getTime()));

        // Set the time
        redemption.setRdmTime(new Time(new java.util.Date().getTime()));

        // Set the tracking id
        redemption.setRdmUniqueBatchTrackingId(catalogueRedemptionItemRequest.getTrackingId());

        // Set the cashPaymentStatus
        redemption.setRdmCashPaymentStatus(PaymentStatus.PAYMENT_STATUS_PAID);

        // set the user not to 0
        redemption.setRdmUserNo(catalogueRedemptionItemRequest.getUserNo());

        // Set the contact number
        redemption.setRdmContactNumber(catalogueRedemptionItemRequest.getContactNumber());

        // Set the auditDetails
        redemption.setCreatedBy(catalogueRedemptionItemRequest.getAuditDetails());

        //set the redemption channel
        redemption.setRdmChannel(catalogueRedemptionItemRequest.getChannel());

        //set destLoyalty id if redemption is pasa rewards
        if(catalogueRedemptionItemRequest.isPasaRewards()){

            //set dest loyalty id
            redemption.setRdmDestLoyaltyId(catalogueRedemptionItemRequest.getDestLoyaltyId());
        }
        //check the catalogue type
        if(catalogueRedemptionItemRequest.getCatType() == CatalogueType.PARTNER_CATALOGUE){

            //set the redemption partner
            redemption.setRdmPartnerNo(catalogueRedemptionItemRequest.getCatRedemptionMerchant());

        }else if(catalogueRedemptionItemRequest.getCatType() == CatalogueType.MERCHANT_CATALOGUE){

            //set redemption partner as 0
            redemption.setRdmPartnerNo(0l);
        }

        if(catalogueRedemptionItemRequest.getCatDeliveryType() == DeliveryType.HOME_DELIVERY){

            // Set address fields
            redemption.setRdmDeliveryAddr1(catalogueRedemptionItemRequest.getAddress1());
            redemption.setRdmDeliveryAddr2(catalogueRedemptionItemRequest.getAddress2());
            redemption.setRdmDeliveryAddr3(catalogueRedemptionItemRequest.getAddress3());
            redemption.setRdmDeliveryCity(catalogueRedemptionItemRequest.getCity());
            redemption.setRdmDeliveryState(catalogueRedemptionItemRequest.getState());
            redemption.setRdmDeliveryCountry(catalogueRedemptionItemRequest.getCountry());
            redemption.setRdmDeliveryPostcode(catalogueRedemptionItemRequest.getPincode());
            redemption.setRdmContactNumber(catalogueRedemptionItemRequest.getCatContactNo());

        }
        // Insert the redemption
        redemption = redemptionRepository.save(redemption);

        catalogueRedemptionItemRequest.setRdmId(redemption.getRdmId());

        return catalogueRedemptionItemRequest;

    }

    @Override
    public CatalogueRedemptionItemRequest getRedemptionRequestObject(Customer customer, Catalogue catalogue) throws InspireNetzException {

        //create the CatalogueRedemptionRequest object for redeeming points
        CatalogueRedemptionItemRequest catalogueRedemptionRequest = new CatalogueRedemptionItemRequest();
        catalogueRedemptionRequest.setLoyaltyId(customer.getCusLoyaltyId());
        catalogueRedemptionRequest.setMerchantNo(customer.getCusMerchantNo());
        catalogueRedemptionRequest.setPrdCode(catalogue.getCatProductCode());
        catalogueRedemptionRequest.setQty(1);
        catalogueRedemptionRequest.setUsrFName(customer.getCusFName());
        catalogueRedemptionRequest.setUsrLName(customer.getCusLName());
        catalogueRedemptionRequest.setUserNo(customer.getCusUserNo());
        catalogueRedemptionRequest.setUserLocation(customer.getCusLocation());
        catalogueRedemptionRequest.setExternalRef(catalogue.getCatExtReference());
        catalogueRedemptionRequest.setChannel(catalogue.getRdmChannel());
        catalogueRedemptionRequest.setCatExtReference(catalogue.getCatExtReference());
        catalogueRedemptionRequest.setCatDescription(catalogue.getCatDescription());
        catalogueRedemptionRequest.setCatMessageSpiel(catalogue.getCatMessageSpiel());
        catalogueRedemptionRequest.setCatDtiNumber(catalogue.getCatDtiNumber() == null? "" : catalogue.getCatDtiNumber());
        catalogueRedemptionRequest.setCatNumPoints(catalogue.getCatNumPoints());
        catalogueRedemptionRequest.setCatRedemptionMerchant(catalogue.getCatRedemptionMerchant());
        catalogueRedemptionRequest.setCatProductCode(catalogue.getCatProductCode());
        catalogueRedemptionRequest.setCatRewardCurrencyId(catalogue.getCatRewardCurrencyId());
        catalogueRedemptionRequest.setCatType(catalogue.getCatType());

/*
        catalogueRedemptionRequest.setCatAccountNo(catalogue.getCatAccountNo() == null? 0L: catalogue.getCatAccountNo());
*/
        catalogueRedemptionRequest.setCatProductCost(catalogue.getCatProductCost() == null ? 0L : catalogue.getCatProductCost());

        //for setting value for redemption voucher expiry field
        catalogueRedemptionRequest.setCatProductNo(catalogue.getCatProductNo());



        //return the object
        return catalogueRedemptionRequest;

    }

    @Override
    public List<Map<String, String>> redeemCatalogueItemsCompatible(String loyaltyId, Long merchantNo,String destLoyaltyId, Map<String, String> params){

        //get the redemption catalogue from the request
        List<RedemptionCatalogue> redemptionCatalogues = getRedemptionCatalogues(params);

        List<Map<String,String>> retData = new ArrayList<>(0);

        String prdCode = "";

        Map responseData ;

        // Create a catalogue redemption item request object
        CatalogueRedemptionItemRequest catalogueRedemptionItemRequest = new CatalogueRedemptionItemRequest();

        catalogueRedemptionItemRequest.setLoyaltyId(loyaltyId);
        catalogueRedemptionItemRequest.setMerchantNo(merchantNo);
        catalogueRedemptionItemRequest.setDestLoyaltyId(destLoyaltyId);
        catalogueRedemptionItemRequest.setChannel(RequestChannel.RDM_MOBILE_APP);
        for(int key=0;key<redemptionCatalogues.size();key++){

            RedemptionCatalogue redemptionCatalogue = redemptionCatalogues.get(key);
            responseData = new HashMap<>();

            catalogueRedemptionItemRequest.setPrdCode(redemptionCatalogue.getCatProductCode());
            catalogueRedemptionItemRequest.setQty(redemptionCatalogues.get(key).getCatQty());

            //redeem points
            CatalogueRedemptionItemResponse catalogueRedemptionItemResponse = new CatalogueRedemptionItemResponse();
            try{

                catalogueRedemptionItemResponse = redeemCatalogueItems(catalogueRedemptionItemRequest);
                responseData.put("prd_no",redemptionCatalogue.getCatProductNo()+"");
                responseData.put("status","success");
                responseData.put("tracking_id",catalogueRedemptionItemResponse.getTracking_id());


            }catch(InspireNetzException ex){


                responseData.put("prd_no",redemptionCatalogue.getCatProductNo()+"");
                responseData.put("status","failed");
                responseData.put("errorcode",ex.getErrorCode().name());

            } catch(Exception ex ) {

                responseData.put("prd_no",redemptionCatalogue.getCatProductNo()+"");
                responseData.put("status","failed");
                responseData.put("errorcode",APIErrorCode.ERR_OPERATION_FAILED.name());


            }

            retData.add(responseData);

        }

        return retData;

    }

    @Override
    public String getRedemptionErrorMessage(APIErrorCode apiErrorCode){

        if(apiErrorCode.equals(APIErrorCode.ERR_PASA_REWARDS_INVALID_DESTINATION)){

            return "Redemption failed , pasa rewards recipient is inactive";

        }else if(apiErrorCode.equals(APIErrorCode.ERR_PASA_REWARDS_INVALID_MEGA_BRAND)){

            return "Redemption failed , pasa rewards recipient mega brand is not eligible ";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_PASA_REWARDS_INVALID_SUBSCRIPTION)){

            return "Redemption failed , pasa rewards recipient does not have subscription to eligible brand";

        }  else if(apiErrorCode.equals(APIErrorCode.ERR_PASA_REWARDS_SOURCE_DESTINATION_SAME)){

            return "Redemption failed , pasa rewards recipient and requestor are same";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_SECONDARY)){

            return "Redemption failed , customer is secondary , only primary can redeem";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_NO_LOYALTY_ID)){

            return ("Redemption failed , customer is not active");

        }  else if(apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_INVALID_LOCATION)){

            return "Redemption failed , customer megabrand is not eligible for redemption";

        }  else if(apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_FAILED_CUSTOMER_INVALID_BRAND)){

            return "Redemption failed , customer doesn't have subscription to eligible products";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_REQUEST_REJECTED)){

            return "Redemption failed , primary rejected redemption request";

        }  else if(apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_WAITING_FOR_APPROVAL)){

            return "Redemption request added , waiting for approval";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_CATALOGUE_REDEMPTION_FAILED)){

            return "Redemption failed";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_REDEMPTION_NOT_ALLOWED_FOR_ACCOUNT)){

            return "Redemption not allowed for account";

        } else if(apiErrorCode.equals(APIErrorCode.ERR_NO_VOUCHER_CODES)){

            return "Redemption failed, no voucher code available";

        }


        return "Redemption failed";
    }

    @Override
    public List<Redemption> findRdmLoyaltyIdAndRdmMerchantNo(String rdmLoyaltyId, Long rdmMerchantNo){

        //get the redemptions for the loyalty id
        List<Redemption> redemptions = redemptionRepository.findByRdmLoyaltyIdAndRdmMerchantNoAndRdmRecordStatus(rdmLoyaltyId,rdmMerchantNo,RecordStatus.RECORD_STATUS_ACTIVE);

        //return the list
        return redemptions;
    }

    @Override
    public CashBackRedemptionResponse doCashBackRedemption(CashBackRedemptionRequest cashbackRedemptionRequest, String otpCode) throws InspireNetzException {


        CashBackRedemptionResponse retData =new CashBackRedemptionResponse();

        //check merchant is enabled or disabled otp
        Long settingsId =getSettingsId(AdminSettingsConfiguration.MER_ENABLE_REDEMPTION_OTP);

        //get merchant settings value
        String settingsValue =getMerchantSettingsValue(cashbackRedemptionRequest.getMerchantNo(),settingsId);

        //convert settings value to integer
        Integer settings=Integer.parseInt(settingsValue);

        //if otp is disabled then we don't want any otp validation
        if(settings !=MerchantOtpConfiguration.OTPENABLED){

            //do cashBack redemption without otp checking
            CashBackRedemptionResponse cashbackRedemptionResponse =doCashbackRedemption(cashbackRedemptionRequest);

            //check the cash back redemption status and log the status
            if(cashbackRedemptionResponse.getStatus().equals("success")){

                //cash back redemption
                customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Redeemed : "+cashbackRedemptionResponse.getPointRedeemed()+" points via cashback",cashbackRedemptionRequest.getMerchantNo(),"");

                //send cash back redemption message
                sendCashBackRedemptionMessage(cashbackRedemptionRequest.getLoyaltyId(),cashbackRedemptionRequest.getMerchantNo(),cashbackRedemptionResponse.getPointRedeemed(),cashbackRedemptionRequest.getAmount(),cashbackRedemptionResponse.getBalance());

            }else if(!cashbackRedemptionResponse.getErrorcode().toString().equals("ERR_INSUFFICIENT_POINT_BALANCE")) {

                //for failed activity
                customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Cash Back Redemption Failed",cashbackRedemptionRequest.getMerchantNo(),"");
            }

            return cashbackRedemptionResponse;
        }


        //get customer loyalty Id
        String cusLoyaltyId =cashbackRedemptionRequest.getLoyaltyId()==null?"":cashbackRedemptionRequest.getLoyaltyId();

        //get customer number based on customer loyalty id
        Long cusCustomerNo =getCustomerNo(cusLoyaltyId,cashbackRedemptionRequest.getMerchantNo());

        //check otp is valid or not
        //Integer otpStatus =oneTimePasswordService.validateOTP(cashbackRedemptionRequest.getMerchantNo(),cusCustomerNo,OTPType.CASH_BACK_REQUEST,otpCode);

        Integer otpStatus =oneTimePasswordService.validateOTPGeneric(cashbackRedemptionRequest.getMerchantNo(),OTPRefType.CUSTOMER,cusCustomerNo.toString(),OTPType.CASH_BACK_REQUEST,otpCode);


        //check otpStatus
        if(otpStatus != OTPStatus.VALIDATED){

            log.info("Otp is not valid with status ="+otpStatus);

            //throw new inspireNetz error invalid otp
            // Set the status as failed
            retData.setStatus(APIResponseStatus.failed.name());

            // Set the error as not data found
            retData.setErrorcode(APIErrorCode.ERR_INVALID_OTP.name());

            //return error invalid otp
            return retData;

        }

        CashBackRedemptionResponse cashbackRedemptionResponse = doCashbackRedemption(cashbackRedemptionRequest);

        //check the cash back redemption status and log the status
        if(cashbackRedemptionResponse.getStatus().equals("success")){

            //cash back redemption
            customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Redeemed : "+cashbackRedemptionResponse.getPointRedeemed()+" points via cashback",cashbackRedemptionRequest.getMerchantNo(),"");

            //send cash back redemption message
            sendCashBackRedemptionMessage(cashbackRedemptionRequest.getLoyaltyId(),cashbackRedemptionRequest.getMerchantNo(),cashbackRedemptionResponse.getPointRedeemed(),cashbackRedemptionRequest.getAmount(),cashbackRedemptionResponse.getBalance());

        }else if(!cashbackRedemptionResponse.getErrorcode().toString().equals("ERR_INSUFFICIENT_POINT_BALANCE")) {

            //for failed activity
            customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Cash Back Redemption Failed",cashbackRedemptionRequest.getMerchantNo(),"");
        }


        //return cashBack redemption
        return  cashbackRedemptionResponse;
    }

    private void sendCashBackRedemptionMessage(String loyaltyId, Long merchantNo, double pointRedeemed,double amount,double pointBalance) throws InspireNetzException {

        HashMap<String ,String > smsParam = new HashMap<>(0);
        smsParam.put("#points",pointRedeemed+"");
        smsParam.put("#amount",amount+"");
        smsParam.put("#pointBalance",pointBalance+"");
        smsParam.put("#remarks","(cashback Redemption)");

        Customer customer=customerService.findByCusLoyaltyIdAndCusMerchantNo(loyaltyId,merchantNo);

        if(customer!=null){

            MessageWrapper messageWrapper = generalUtils.getMessageWrapperObject(MessageSpielValue.CASH_BACK_REDEMPTION,customer.getCusLoyaltyId(),customer.getCusMobile(),customer.getCusEmail(),"",customer.getCusMerchantNo(),smsParam,MessageSpielChannel.ALL,IndicatorStatus.YES );

            userMessagingService.transmitNotification(messageWrapper);

        }

    }

    @Override
    public CashBackRedemptionResponse doCashBackRedemptionForMerchant(String loyaltyId, double purchaseAmount, Long rwdCurrencyId,String location,String txnRef) throws InspireNetzException {

        //set all request into cash back object and call do cash back redemption method
        CashBackRedemptionRequest cashbackRedemptionRequest =new CashBackRedemptionRequest();

        //set merchant No
        Long merchantNo = authSessionUtils.getMerchantNo();

        Long userNo =authSessionUtils.getUserNo();

        Long userLocation =authSessionUtils.getUserLocation();

        if(location!=null&& !location.equals("")){

            MerchantLocation merchantLocation=merchantLocationService.findByMelMerchantNoAndMelLocation(merchantNo,location);

            if(merchantLocation!=null){

                userLocation=merchantLocation.getMelId();
            }

        }

        String auditDetails =authSessionUtils.getUserNo().toString();

        //set cash back request details
        cashbackRedemptionRequest.setAmount(purchaseAmount);

        cashbackRedemptionRequest.setAuditDetails(auditDetails);

        cashbackRedemptionRequest.setMerchantNo(merchantNo);

        cashbackRedemptionRequest.setUserNo(userNo);

        cashbackRedemptionRequest.setUserLocation(userLocation);

        //cashbackRedemptionRequest.
        cashbackRedemptionRequest.setRewardCurrencyId(rwdCurrencyId);

        cashbackRedemptionRequest.setLoyaltyId(loyaltyId);

        cashbackRedemptionRequest.setTxnRef(txnRef);

        //do cash back redemption
        CashBackRedemptionResponse cashBackRedemptionResponse =doCashbackRedemption(cashbackRedemptionRequest);

        //check the cash back redemption status and log the status
        if(cashBackRedemptionResponse.getStatus().equals("success")){

            //cash back redemption
            customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Redeemed : "+cashBackRedemptionResponse.getPointRedeemed()+" points via cashback",cashbackRedemptionRequest.getMerchantNo(),"");

            sendCashBackRedemptionMessage(cashbackRedemptionRequest.getLoyaltyId(),cashbackRedemptionRequest.getMerchantNo(),cashBackRedemptionResponse.getPointRedeemed(),cashbackRedemptionRequest.getAmount(),cashBackRedemptionResponse.getBalance());

        }else if(!cashBackRedemptionResponse.getErrorcode().toString().equals("ERR_INSUFFICIENT_POINT_BALANCE")) {

            //for failed activity
            customerActivityService.logActivity(cashbackRedemptionRequest.getLoyaltyId(),CustomerActivityType.CASH_BACK_REDEMPTION,"Cash Back Redemption Failed",cashbackRedemptionRequest.getMerchantNo(),"");
        }

        //return cash back response
        return cashBackRedemptionResponse;
    }


    private Long getCustomerNo(String cusLoyaltyId,Long merchantNo) {

        //find customer information
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(cusLoyaltyId,merchantNo);

        //check customer is null or not
        if(customer ==null){

            log.info("CardMasterService->getCustomerNo:Invalid Customer");

            return 0L;
        }

        //return customer number
        return customer.getCusCustomerNo()==null?0L:customer.getCusCustomerNo();
    }

    /**
     * @purpose:to get settings information based on settings name
     * @param merchantOtpSettingsName
     * @return
     */
    private Long getSettingsId(String merchantOtpSettingsName) {

        Setting setting = settingService.findBySetName(merchantOtpSettingsName);

        if(setting ==null){

            log.info("Otp enabled settings  not avialable in this merchant");

            //return 0
            return 0L;

        }

        return setting.getSetId()==null?0L:setting.getSetId();
    }


    private String getMerchantSettingsValue(Long merchantNo, Long settingsId) {

        MerchantSetting merchantSetting =merchantSettingService.findByMesMerchantNoAndMesSettingId(merchantNo,settingsId);

        if(merchantSetting ==null){

            log.info("merchant settings is not available for this operation");


            return "0";
        }

        return merchantSetting.getMesValue();
    }

    /**
     *
     * @param rdmPartnerNo
     * @param filterType
     * @param query
     * @param status
     * @param pageable
     * @return
     */
    @Override
    public Page<Redemption> listRedemptionRequestsInPartnerPortal(Long rdmPartnerNo, String filterType, String query, Integer status, Pageable pageable) {

        // Page holding the result
        Page<Redemption> redemptions = null ;

        // Get the result set for the loyatly id
        int page=pageable.getPageNumber();

        int pageSize = pageable.getPageSize();

        //create request with sorting parameter
        Pageable newPageableRequest = new PageRequest(page,pageSize, new Sort(Sort.Direction.DESC,"rdmId"));

        // Check for the filter type
        if ( filterType.equals("0") && query.equals("0") ) {

            if(status == 0){

                // Get the redmeption page for no filtering
                redemptions = redemptionRepository.findByRdmPartnerNoAndRdmRecordStatus(rdmPartnerNo, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            } else {

                // Get the redmeption page for no filtering
                redemptions = redemptionRepository.findByRdmPartnerNoAndRdmStatusAndRdmRecordStatus(rdmPartnerNo, status, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            }

            // Check the filter type and then call the appropriate repository method for getting the result
        }else if (filterType.equalsIgnoreCase("merchantno") && !query.equals("0") ) {

            if(status == 0){

                redemptions =  redemptionRepository.findByRdmPartnerNoAndRdmMerchantNoAndRdmRecordStatus(rdmPartnerNo, Long.parseLong(query), RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            } else {

                redemptions =  redemptionRepository.findByRdmPartnerNoAndRdmMerchantNoAndRdmStatusAndRdmRecordStatus(rdmPartnerNo, Long.parseLong(query), status, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            }


        }else if (filterType.equalsIgnoreCase("merchantno") && query.equals("0") ) {

            if(status == 0){

                redemptions =  redemptionRepository.findByRdmPartnerNoAndRdmRecordStatus(rdmPartnerNo, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            } else {

                redemptions =  redemptionRepository.findByRdmPartnerNoAndRdmStatusAndRdmRecordStatus(rdmPartnerNo, status, RecordStatus.RECORD_STATUS_ACTIVE, newPageableRequest);

            }


        }

        // Return the redemptionPage object
        return redemptions;

    }

    @Override
    public List<Redemption> findByRdmPartnerNoAndRdmUniqueBatchTrackingId(Long rdmPartnerNo, String trackingId) {

        // Get the redemptions matching the tracking id
        List<Redemption> redemptions = redemptionRepository.findByRdmPartnerNoAndRdmUniqueBatchTrackingId(rdmPartnerNo, trackingId);

        //Return redemptions
        return redemptions;

    }

    @Override
    public Page<Redemption> listPayRedemptionsInPartnerPortal(Long rdmMerchantNo, java.sql.Date startDate, java.sql.Date endDate, Pageable pageable) throws InspireNetzException {

        // If the start date is not set, then we need to set the date to the minimum value
        if ( startDate == null  ){

            // Create the calendar object
            Calendar cal = Calendar.getInstance();

            // set Date portion to January 1, 1970
            cal.set( cal.YEAR, 1970 );
            cal.set( cal.MONTH, cal.JANUARY );
            cal.set( cal.DATE, 1 );

            startDate = new java.sql.Date(cal.getTime().getTime());

        }

        // Check if the endDate is set, if not then we need to
        // set the date to the largest possible date
        if ( endDate == null ) {

            // Create the calendar object
            Calendar cal = Calendar.getInstance();

            // set Date portion to December 31, 9999
            cal.set( cal.YEAR, 9999 );
            cal.set( cal.MONTH, cal.DECEMBER );
            cal.set( cal.DATE, 31 );

            endDate = new java.sql.Date(cal.getTime().getTime());

        }

        //Get the redemption merchant
        RedemptionMerchant redemptionMerchant = redemptionMerchantService.findByRemNo(rdmMerchantNo);

        //check if the redemption merchant is null
        if(redemptionMerchant == null){

            //log error
            log.error("listPayRedemptionsInPartnerPortal  --> No information found");

            throw new InspireNetzException(APIErrorCode.ERR_NO_DATA_FOUND);

        }

        //Get the list of redemptions of pay
        Page<Redemption> redemptionPage = redemptionRepository.findByRdmProductCodeAndRdmTypeAndRdmDateBetween(redemptionMerchant.getRemCode(),RedemptionType.PAY,startDate,endDate,pageable);

        //Check if the redemptions are not null
        if(!redemptionPage.hasContent()){

            //log error
            log.error("listPayRedemptionsInPartnerPortal  --> No information found");

            throw new InspireNetzException(APIErrorCode.ERR_NO_DATA_FOUND);

        }

        return redemptionPage;
    }

    @Override
    public Boolean voidTransactions(Long rdmMerchantNo, Long rdmId) throws InspireNetzException {

        //Get the redemption
        Redemption redemption = findByRdmId(rdmId);

        //get the redemption merchant
        RedemptionMerchant redemptionMerchant = redemptionMerchantService.findByRemNo(rdmMerchantNo);

        //Check if the logged in user and the redemptionMerchant is same
        if(!redemptionMerchant.getRemCode().equals(redemption.getRdmProductCode())){

            log.error("Invalid redemption merchant");

            throw new InspireNetzException(APIErrorCode.ERR_INVALID_REDEMPTION_MERCHANT);

        }

        redemption.setRdmStatus(RedemptionStatus.RDM_STATUS_CANCELLED);

        redemption = saveRedemption(redemption);

        // Create pointRewardData object
        PointRewardData pointRewardData= getPointRewardObject(redemption);

        // Get the transaction object
        Transaction transaction = getTransactionObject(redemption);

        //Reverse the points to the rdm LoyaltyId
        loyaltyEngineService.awardPointsProxy(pointRewardData,transaction);

        // Update the merchant settlement entry
        boolean isDeleted = merchantSettlementService.deleteMerchantSettlementEntry(redemption,redemptionMerchant);

        if(!isDeleted){

            return false;

        }

        // Return true
        return true;

    }

    @Override
    public Redemption updateRedemptionDetails(String trackingId, Integer status, String rdmDeliveryCourierInfo, String rdmDeliveryCourierTracking) throws InspireNetzException {

        //Get the logged in user details
        Long rdmMerchantNo = authSessionUtils.getMerchantNo();

        //Get the redemption details
        List<Redemption> redemptionList = findByRdmMerchantNoAndRdmUniqueBatchTrackingId(rdmMerchantNo,trackingId);

        //Get the redemption object
        Redemption redemption = redemptionList.get(0);


        //Get the customer details
        Customer customer = customerService.findByCusLoyaltyIdAndCusMerchantNo(redemption.getRdmLoyaltyId(),redemption.getRdmMerchantNo());

        //Check the redemption staus
        if(status == RedemptionStatus.RDM_STATUS_IN_TRANSIT){

            //Update the redemption entry
            redemption.setRdmDeliveryCourierInfo(rdmDeliveryCourierInfo);
            redemption.setRdmDeliveryCourierTracking(rdmDeliveryCourierTracking);

        } else if(status == RedemptionStatus.RDM_STATUS_ORDER_CONFIRMED){

            //create message wrapper object
            MessageWrapper messageWrapper =new MessageWrapper();

            messageWrapper.setMerchantNo(redemption.getRdmMerchantNo());
            messageWrapper.setIsCustomer(IndicatorStatus.YES);
            messageWrapper.setChannel(MessageSpielChannel.EMAIL);
            messageWrapper.setLoyaltyId(redemption.getRdmLoyaltyId());
            messageWrapper.setSpielName(MessageSpielValue.REDEMPTION_CONFIRMED_NOTIFICATION_EMAIL);
            messageWrapper.setEmailId(customer.getCusEmail());

            userMessagingService.transmitNotification(messageWrapper);

            log.info("Merchant Notification Information +--------------"+messageWrapper.toString());


        }

        //Set redemption status
        redemption.setRdmStatus(status);

        //Save redemption
        redemption = saveRedemption(redemption);

        if(redemption == null){

            // log the error
            log.error("updateRedemptionDetails - Response : Unable to update redemption details");

            // throw an exception
            throw new InspireNetzException(APIErrorCode.ERR_REDEMPTION_UPDATION_FAILED);

        }

        return  redemption;

    }

    @Override
    public CatalogueRedemptionItemRequest getCatalogueRedemptionRequestObject(String loyaltyId, String prdCode, Integer quantity, Long merchantNo, Integer rdmChannel, String destLoyaltyId, String rdmDeliveryAddress1, String rdmDeliveryAddress2, String rdmDeliveryAddress3, String rdmDeliveryCity, String rdmDeliveryState, String rdmDeliveryCountry, String rdmDeliveryPostCode, String rdmContactNumber) throws InspireNetzException {

        //Cretae a catalogueRedemptionItemRequest object
        CatalogueRedemptionItemRequest catalogueRedemptionItemRequest = new CatalogueRedemptionItemRequest();

        //Set the paarameters
        catalogueRedemptionItemRequest.setLoyaltyId(loyaltyId);
        catalogueRedemptionItemRequest.setPrdCode(prdCode);
        catalogueRedemptionItemRequest.setQty(quantity);
        catalogueRedemptionItemRequest.setMerchantNo(merchantNo);
        catalogueRedemptionItemRequest.setChannel(rdmChannel);
        catalogueRedemptionItemRequest.setDestLoyaltyId(destLoyaltyId);
        catalogueRedemptionItemRequest.setAddress1(rdmDeliveryAddress1);
        catalogueRedemptionItemRequest.setAddress2(rdmDeliveryAddress2);
        catalogueRedemptionItemRequest.setAddress3(rdmDeliveryAddress3);
        catalogueRedemptionItemRequest.setCity(rdmDeliveryCity);
        catalogueRedemptionItemRequest.setState(rdmDeliveryState);
        catalogueRedemptionItemRequest.setCountry(rdmDeliveryCountry);
        catalogueRedemptionItemRequest.setPincode(rdmDeliveryPostCode);
        catalogueRedemptionItemRequest.setCatContactNo(rdmContactNumber);


        return catalogueRedemptionItemRequest;



    }


    private Transaction getTransactionObject(Redemption redemption) {


        //Create a transaction object
        Transaction transaction = new Transaction();


        transaction.setTxnInternalRef(redemption.getRdmId().toString());
        transaction.setTxnExternalRef("");
        transaction.setTxnType(TransactionType.REWARD_ADUJUSTMENT_AWARDING);
        transaction.setTxnDate(new java.sql.Date(new java.util.Date().getTime()));
        transaction.setTxnRewardExpDt(DBUtils.covertToSqlDate("9999-12-31"));
        transaction.setTxnMerchantNo(redemption.getRdmMerchantNo());
        transaction.setTxnLoyaltyId(redemption.getRdmLoyaltyId());
        transaction.setTxnRewardCurrencyId(redemption.getRdmRewardCurrencyId());
        transaction.setTxnProgramId(0L);
        transaction.setTxnRewardQty(redemption.getRdmRewardCurrencyQty());
        transaction.setTxnStatus(TransactionStatus.POINTS_REVERSED);
        transaction.setTxnCrDbInd(CreditDebitInd.CREDIT);
        transaction.setCreatedBy(authSessionUtils.getUserLoginId());

        return transaction;

    }

    private PointRewardData getPointRewardObject(Redemption redemption){

        //Set the values
        PointRewardData pointRewardData = new PointRewardData();

        pointRewardData.setMerchantNo(redemption.getRdmMerchantNo());
        pointRewardData.setAuditDetails(authSessionUtils.getUserLoginId());

        pointRewardData.setExpiryDt(DBUtils.covertToSqlDate("9999-12-31"));
        pointRewardData.setLoyaltyId(redemption.getRdmLoyaltyId());
        pointRewardData.setTxnDate(new java.sql.Date(new java.util.Date().getTime()));
        pointRewardData.setProgramId(0L);
        pointRewardData.setRewardQty(redemption.getRdmRewardCurrencyQty());
        pointRewardData.setTxnType(TransactionType.REWARD_ADUJUSTMENT_AWARDING);
        pointRewardData.setRwdCurrencyId(redemption.getRdmRewardCurrencyId());
        return pointRewardData;
    }
}