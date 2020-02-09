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
 * Represents an in-app product's listing details.
 */
public class SkuDetails {
    private final java.lang.String mJson;

    private java.lang.String mSku;

    private java.lang.String mType;

    private java.lang.String mPrice;

    private java.lang.String mTitle;

    private java.lang.String mDescription;

    public SkuDetails(java.lang.String jsonSkuDetails) throws org.json.JSONException {
        this(me.ccrama.redditslide.util.IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public SkuDetails(java.lang.String itemType, java.lang.String jsonSkuDetails) throws org.json.JSONException {
        java.lang.String mItemType = itemType;
        mJson = jsonSkuDetails;
        org.json.JSONObject o = new org.json.JSONObject(mJson);
        mSku = o.optString("productId");
        mType = o.optString("type");
        mPrice = o.optString("price");
        mTitle = o.optString("title");
        mDescription = o.optString("description");
    }

    public java.lang.String getSku() {
        return mSku;
    }

    public java.lang.String getType() {
        return mType;
    }

    public java.lang.String getPrice() {
        return mPrice;
    }

    public java.lang.String getTitle() {
        return mTitle;
    }

    public java.lang.String getDescription() {
        return mDescription;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "SkuDetails:" + mJson;
    }
}