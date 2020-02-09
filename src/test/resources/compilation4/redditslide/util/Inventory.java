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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Represents a block of information about in-app items.
 * An Inventory is returned by such methods as {@link IabHelper#queryInventory}.
 */
public class Inventory {
    private final java.util.Map<java.lang.String, me.ccrama.redditslide.util.SkuDetails> mSkuMap = new java.util.HashMap<>();

    private final java.util.Map<java.lang.String, me.ccrama.redditslide.util.Purchase> mPurchaseMap = new java.util.HashMap<>();

    Inventory() {
    }

    /**
     * Returns the listing details for an in-app product.
     */
    public me.ccrama.redditslide.util.SkuDetails getSkuDetails(java.lang.String sku) {
        return mSkuMap.get(sku);
    }

    /**
     * Returns purchase information for a given product, or null if there is no purchase.
     */
    public me.ccrama.redditslide.util.Purchase getPurchase(java.lang.String sku) {
        return mPurchaseMap.get(sku);
    }

    /**
     * Returns whether or not there exists a purchase of the given product.
     */
    public boolean hasPurchase(java.lang.String sku) {
        return mPurchaseMap.containsKey(sku);
    }

    /**
     * Return whether or not details about the given product are available.
     */
    public boolean hasDetails(java.lang.String sku) {
        return mSkuMap.containsKey(sku);
    }

    /**
     * Erase a purchase (locally) from the inventory, given its product ID. This just
     * modifies the Inventory object locally and has no effect on the server! This is
     * useful when you have an existing Inventory object which you know to be up to date,
     * and you have just consumed an item successfully, which means that erasing its
     * purchase data from the Inventory you already have is quicker than querying for
     * a new Inventory.
     */
    public void erasePurchase(java.lang.String sku) {
        if (mPurchaseMap.containsKey(sku))
            mPurchaseMap.remove(sku);

    }

    /**
     * Returns a list of all owned product IDs.
     */
    java.util.List<java.lang.String> getAllOwnedSkus() {
        return new java.util.ArrayList<>(mPurchaseMap.keySet());
    }

    /**
     * Returns a list of all owned product IDs of a given type
     */
    java.util.List<java.lang.String> getAllOwnedSkus(java.lang.String itemType) {
        java.util.List<java.lang.String> result = new java.util.ArrayList<>();
        for (me.ccrama.redditslide.util.Purchase p : mPurchaseMap.values()) {
            if (p.getItemType().equals(itemType))
                result.add(p.getSku());

        }
        return result;
    }

    /**
     * Returns a list of all purchases.
     */
    public java.util.List<me.ccrama.redditslide.util.Purchase> getAllPurchases() {
        return new java.util.ArrayList<>(mPurchaseMap.values());
    }

    void addSkuDetails(me.ccrama.redditslide.util.SkuDetails d) {
        mSkuMap.put(d.getSku(), d);
    }

    void addPurchase(me.ccrama.redditslide.util.Purchase p) {
        mPurchaseMap.put(p.getSku(), p);
    }
}