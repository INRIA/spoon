package importCleaner;

import com.basho.riak.client.api.commands.buckets.StoreBucketProperties;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.UpdateValue;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.LongIntIndex;
import com.basho.riak.client.core.util.BinaryValue;
import site.ycsb.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.indexes.IntIndexQuery;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

import static site.ycsb.db.riak.RiakUtils.createResultHashMap;
import static site.ycsb.db.riak.RiakUtils.getKeyAsLong;
import static site.ycsb.db.riak.RiakUtils.serializeTable;

public class TypeImportButUseSubType {
    private static final String HOST_PROPERTY = "riak.hosts";
    private static final String PORT_PROPERTY = "riak.port";
    private static final String BUCKET_TYPE_PROPERTY = "riak.bucket_type";
    private static final String R_VALUE_PROPERTY = "riak.r_val";
    private static final String W_VALUE_PROPERTY = "riak.w_val";
    private static final String READ_RETRY_COUNT_PROPERTY = "riak.read_retry_count";
    private static final String WAIT_TIME_BEFORE_RETRY_PROPERTY = "riak.wait_time_before_retry";
    private static final String TRANSACTION_TIME_LIMIT_PROPERTY = "riak.transaction_time_limit";
    private static final String STRONG_CONSISTENCY_PROPERTY = "riak.strong_consistency";
    private static final String STRONG_CONSISTENT_SCANS_BUCKET_TYPE_PROPERTY = "riak.strong_consistent_scans_bucket_type";
    private static final String DEBUG_PROPERTY = "riak.debug";

    private static final int TIME_OUT = 1;

    private String[] hosts;
    private int port;
    private String bucketType;
    private String bucketType2i;
    private Quorum rvalue;
    private Quorum wvalue;
    private int readRetryCount;
    private int waitTimeBeforeRetry;
    private int transactionTimeLimit;
    private boolean strongConsistency;
    private String strongConsistentScansBucketType;
    private boolean performStrongConsistentScans;
    private boolean debug;

    private RiakClient riakClient;
    private RiakCluster riakCluster;

    private void loadDefaultProperties() {
        InputStream propFile = RiakKVClient.class.getClassLoader().getResourceAsStream("riak.properties");
        Properties propsPF = new Properties(System.getProperties());

        try {
            propsPF.load(propFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        hosts = propsPF.getProperty(HOST_PROPERTY).split(",");
        port = Integer.parseInt(propsPF.getProperty(PORT_PROPERTY));
        bucketType = propsPF.getProperty(BUCKET_TYPE_PROPERTY);
        rvalue = new Quorum(Integer.parseInt(propsPF.getProperty(R_VALUE_PROPERTY)));
        wvalue = new Quorum(Integer.parseInt(propsPF.getProperty(W_VALUE_PROPERTY)));
        readRetryCount = Integer.parseInt(propsPF.getProperty(READ_RETRY_COUNT_PROPERTY));
        waitTimeBeforeRetry = Integer.parseInt(propsPF.getProperty(WAIT_TIME_BEFORE_RETRY_PROPERTY));
        transactionTimeLimit = Integer.parseInt(propsPF.getProperty(TRANSACTION_TIME_LIMIT_PROPERTY));
        strongConsistency = Boolean.parseBoolean(propsPF.getProperty(STRONG_CONSISTENCY_PROPERTY));
        strongConsistentScansBucketType = propsPF.getProperty(STRONG_CONSISTENT_SCANS_BUCKET_TYPE_PROPERTY);
        debug = Boolean.parseBoolean(propsPF.getProperty(DEBUG_PROPERTY));
    }

    private void loadProperties() {
        // First, load the default properties...
        loadDefaultProperties();

        // ...then, check for some props set at command line!
        Properties props = getProperties();

        String portString = props.getProperty(PORT_PROPERTY);
        if (portString != null) {
            port = Integer.parseInt(portString);
        }

        String hostsString = props.getProperty(HOST_PROPERTY);
        if (hostsString != null) {
            hosts = hostsString.split(",");
        }

        String bucketTypeString = props.getProperty(BUCKET_TYPE_PROPERTY);
        if (bucketTypeString != null) {
            bucketType = bucketTypeString;
        }

        String rValueString = props.getProperty(R_VALUE_PROPERTY);
        if (rValueString != null) {
            rvalue = new Quorum(Integer.parseInt(rValueString));
        }

        String wValueString = props.getProperty(W_VALUE_PROPERTY);
        if (wValueString != null) {
            wvalue = new Quorum(Integer.parseInt(wValueString));
        }

        String readRetryCountString = props.getProperty(READ_RETRY_COUNT_PROPERTY);
        if (readRetryCountString != null) {
            readRetryCount = Integer.parseInt(readRetryCountString);
        }

        String waitTimeBeforeRetryString = props.getProperty(WAIT_TIME_BEFORE_RETRY_PROPERTY);
        if (waitTimeBeforeRetryString != null) {
            waitTimeBeforeRetry = Integer.parseInt(waitTimeBeforeRetryString);
        }

        String transactionTimeLimitString = props.getProperty(TRANSACTION_TIME_LIMIT_PROPERTY);
        if (transactionTimeLimitString != null) {
            transactionTimeLimit = Integer.parseInt(transactionTimeLimitString);
        }

        String strongConsistencyString = props.getProperty(STRONG_CONSISTENCY_PROPERTY);
        if (strongConsistencyString != null) {
            strongConsistency = Boolean.parseBoolean(strongConsistencyString);
        }

        String strongConsistentScansBucketTypeString = props.getProperty(STRONG_CONSISTENT_SCANS_BUCKET_TYPE_PROPERTY);
        if (strongConsistentScansBucketTypeString != null) {
            strongConsistentScansBucketType = strongConsistentScansBucketTypeString;
        }

        String debugString = props.getProperty(DEBUG_PROPERTY);
        if (debugString != null) {
            debug = Boolean.parseBoolean(debugString);
        }
    }

    public void init() throws Exception {
        loadProperties();

        RiakNode.Builder builder = new RiakNode.Builder().withRemotePort(port);
        List<RiakNode> nodes = RiakNode.Builder.buildNodes(builder, Arrays.asList(hosts));
        riakCluster = new RiakCluster.Builder(nodes).build();

        try {
            riakCluster.start();
            riakClient = new RiakClient(riakCluster);
        } catch (Exception e) {
            System.err.println("Unable to properly start up the cluster. Reason: " + e.toString());
            throw new Exception(e);
        }

        // If strong consistency is in use, we need to change the bucket-type where the 2i indexes will be stored.
        if (strongConsistency && !strongConsistentScansBucketType.isEmpty()) {
            // The 2i indexes have to be stored in the appositely created strongConsistentScansBucketType: this however has
            // to be done only if the user actually created it! So, if the latter doesn't exist, then the scan transactions
            // will not be performed at all.
            bucketType2i = strongConsistentScansBucketType;
            performStrongConsistentScans = true;
        } else {
            // If instead eventual consistency is in use, then the 2i indexes have to be stored in the bucket-type
            // indicated with the bucketType variable.
            bucketType2i = bucketType;
            performStrongConsistentScans = false;
        }

        if (debug) {
            System.err.println("DEBUG ENABLED. Configuration parameters:");
            System.err.println("-----------------------------------------");
            System.err.println("Hosts: " + Arrays.toString(hosts));
            System.err.println("Port: " + port);
            System.err.println("Bucket Type: " + bucketType);
            System.err.println("R Val: " + rvalue.toString());
            System.err.println("W Val: " + wvalue.toString());
            System.err.println("Read Retry Count: " + readRetryCount);
            System.err.println("Wait Time Before Retry: " + waitTimeBeforeRetry + " ms");
            System.err.println("Transaction Time Limit: " + transactionTimeLimit + " s");
            System.err.println("Consistency model: " + (strongConsistency ? "Strong" : "Eventual"));

            if (strongConsistency) {
                System.err.println("Strong Consistent Scan Transactions " +  (performStrongConsistentScans ? "" : "NOT ") +
                        "allowed.");
            }
        }
    }

    @Override
    public int read(String table, String key, Set<String> fields, Map<String, Iterator> result) {
        Location location = new Location(new Namespace(bucketType, table), key);
        FetchValue fv = new FetchValue.Builder(location).withOption(FetchValue.Option.R, rvalue).build();
        FetchValue.Response response;

        try {
            response = fetch(fv);

            if (response.isNotFound()) {
                if (debug) {
                    System.err.println("Unable to read key " + key + ". Reason: NOT FOUND");
                }

                return 2;
            }
        } catch (TimeoutException e) {
            if (debug) {
                System.err.println("Unable to read key " + key + ". Reason: TIME OUT");
            }

            return TIME_OUT;
        } catch (Exception e) {
            if (debug) {
                System.err.println("Unable to read key " + key + ". Reason: " + e.toString());
            }

            return 3;
        }

        // Create the result HashMap.
        HashMap<String, Iterator> partialResult = new HashMap<>();
        createResultHashMap(fields, response, partialResult);
        result.putAll(partialResult);
        return 1;
    }

    @Override
    public int scan(String table, String startkey, int recordcount, Set<String> fields,
                       Vector<HashMap<String, Iterator>> result) {
        if (strongConsistency && !performStrongConsistentScans) {
            return 403;
        }

        // The strong consistent bucket-type is not capable of storing 2i indexes. So, we need to read them from the fake
        // one (which we use only to store indexes). This is why, when using such a consistency model, the bucketType2i
        // variable is set to FAKE_BUCKET_TYPE.
        IntIndexQuery iiq = new IntIndexQuery
                .Builder(new Namespace(bucketType2i, table), "key", getKeyAsLong(startkey), Long.MAX_VALUE)
                .withMaxResults(recordcount)
                .withPaginationSort(true)
                .build();

        Location location;
        RiakFuture<IntIndexQuery.Response, IntIndexQuery> future = riakClient.executeAsync(iiq);

        try {
            IntIndexQuery.Response response = future.get(transactionTimeLimit, TimeUnit.SECONDS);
            List<IntIndexQuery.Response.Entry> entries = response.getEntries();

            // If no entries were retrieved, then something bad happened...
            if (entries.size() == 0) {
                if (debug) {
                    System.err.println("Unable to scan any record starting from key " + startkey + ", aborting transaction. " +
                            "Reason: NOT FOUND");
                }

                return 404;
            }

            for (IntIndexQuery.Response.Entry entry : entries) {
                // If strong consistency is in use, then the actual location of the object we want to read is obtained by
                // fetching the key from the one retrieved with the 2i indexes search operation.
                if (strongConsistency) {
                    location = new Location(new Namespace(bucketType, table), entry.getRiakObjectLocation().getKeyAsString());
                } else {
                    location = entry.getRiakObjectLocation();
                }

                FetchValue fv = new FetchValue.Builder(location)
                        .withOption(FetchValue.Option.R, rvalue)
                        .build();

                FetchValue.Response keyResponse = fetch(fv);

                if (keyResponse.isNotFound()) {
                    if (debug) {
                        System.err.println("Unable to scan all requested records starting from key " + startkey + ", aborting " +
                                "transaction. Reason: NOT FOUND");
                    }

                    return 404;
                }

                // Create the partial result to add to the result vector.
                HashMap<String, Iterator> partialResult = new HashMap<>();
                createResultHashMap(fields, keyResponse, partialResult);
                result.add(partialResult);
            }
        } catch (TimeoutException e) {
            if (debug) {
                System.err.println("Unable to scan all requested records starting from key " + startkey + ", aborting " +
                        "transaction. Reason: TIME OUT");
            }

            return TIME_OUT;
        } catch (Exception e) {
            if (debug) {
                System.err.println("Unable to scan all records starting from key " + startkey + ", aborting transaction. " +
                        "Reason: " + e.toString());
            }

            return 2;
        }

        return 1;
    }
}
