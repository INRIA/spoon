/* Copyright (c) 2012 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package me.ccrama.redditslide.util;
/**
 * Represents an in-app billing purchase.
 */
public class Purchase {
    final java.lang.String mItemType;// ITEM_TYPE_INAPP or ITEM_TYPE_SUBS


    private final java.lang.String mOriginalJson;

    private java.lang.String mOrderId;

    private java.lang.String mPackageName;

    private java.lang.String mSku;

    private long mPurchaseTime;

    private int mPurchaseState;

    private java.lang.String mDeveloperPayload;

    private java.lang.String mToken;

    private java.lang.String mSignature;

    public Purchase(java.lang.String itemType, java.lang.String jsonPurchaseInfo, java.lang.String signature) throws org.json.JSONException {
        mItemType = itemType;
        mOriginalJson = jsonPurchaseInfo;
        org.json.JSONObject o = new org.json.JSONObject(mOriginalJson);
        mOrderId = o.optString("orderId");
        mPackageName = o.optString("packageName");
        mSku = o.optString("productId");
        mPurchaseTime = o.optLong("purchaseTime");
        mPurchaseState = o.optInt("purchaseState");
        mDeveloperPayload = o.optString("developerPayload");
        mToken = o.optString("token", o.optString("purchaseToken"));
        mSignature = signature;
    }

    public java.lang.String getItemType() {
        return mItemType;
    }

    public java.lang.String getOrderId() {
        return mOrderId;
    }

    public java.lang.String getPackageName() {
        return mPackageName;
    }

    public java.lang.String getSku() {
        return mSku;
    }

    public long getPurchaseTime() {
        return mPurchaseTime;
    }

    public int getPurchaseState() {
        return mPurchaseState;
    }

    public java.lang.String getDeveloperPayload() {
        return mDeveloperPayload;
    }

    public java.lang.String getToken() {
        return mToken;
    }

    public java.lang.String getOriginalJson() {
        return mOriginalJson;
    }

    public java.lang.String getSignature() {
        return mSignature;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return (("PurchaseInfo(type:" + mItemType) + "):") + mOriginalJson;
    }
}