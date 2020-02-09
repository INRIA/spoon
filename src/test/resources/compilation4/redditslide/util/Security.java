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
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.Signature;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the
 * application on the device. For the sake of simplicity and clarity of this
 * example, this code is included here and is executed on the device. If you
 * must verify the purchases on the phone, you should obfuscate this code to
 * make it harder for an attacker to replace the code with stubs that treat all
 * purchases as verified.
 */
class Security {
    private static final java.lang.String TAG = "IABUtil/Security";

    private static final java.lang.String KEY_FACTORY_ALGORITHM = "RSA";

    private static final java.lang.String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * Verifies that the data was signed with the given signature, and returns
     * the verified purchase. The data is in JSON format and signed
     * with a private key. The data also contains the {@link PurchaseState}
     * and product ID of the purchase.
     *
     * @param base64PublicKey
     * 		the base64-encoded public key to use for verifying.
     * @param signedData
     * 		the signed JSON string (signed, not encrypted)
     * @param signature
     * 		the signature for the data, signed with the private key
     */
    public static boolean verifyPurchase(java.lang.String base64PublicKey, java.lang.String signedData, java.lang.String signature) {
        if ((android.text.TextUtils.isEmpty(signedData) || android.text.TextUtils.isEmpty(base64PublicKey)) || android.text.TextUtils.isEmpty(signature)) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Purchase verification failed: missing data.");
            return false;
        }
        java.security.PublicKey key = me.ccrama.redditslide.util.Security.generatePublicKey(base64PublicKey);
        return me.ccrama.redditslide.util.Security.verify(key, signedData, signature);
    }

    /**
     * Generates a PublicKey instance from a string containing the
     * Base64-encoded public key.
     *
     * @param encodedPublicKey
     * 		Base64-encoded public key
     * @throws IllegalArgumentException
     * 		if encodedPublicKey is invalid
     */
    private static java.security.PublicKey generatePublicKey(java.lang.String encodedPublicKey) {
        try {
            byte[] decodedKey = me.ccrama.redditslide.util.Base64.decode(encodedPublicKey);
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance(me.ccrama.redditslide.util.Security.KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(decodedKey));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new java.lang.RuntimeException(e);
        } catch (java.security.spec.InvalidKeySpecException e) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Invalid key specification.");
            throw new java.lang.IllegalArgumentException(e);
        } catch (me.ccrama.redditslide.util.Base64DecoderException e) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Base64 decoding failed.");
            throw new java.lang.IllegalArgumentException(e);
        }
    }

    /**
     * Verifies that the signature from the server matches the computed
     * signature on the data.  Returns true if the data is correctly signed.
     *
     * @param publicKey
     * 		public key associated with the developer account
     * @param signedData
     * 		signed data from server
     * @param signature
     * 		server signature
     * @return true if the data and signature match
     */
    private static boolean verify(java.security.PublicKey publicKey, java.lang.String signedData, java.lang.String signature) {
        java.security.Signature sig;
        try {
            sig = java.security.Signature.getInstance(me.ccrama.redditslide.util.Security.SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(me.ccrama.redditslide.util.Base64.decode(signature))) {
                android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Signature verification failed.");
                return false;
            }
            return true;
        } catch (java.security.NoSuchAlgorithmException e) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "NoSuchAlgorithmException.");
        } catch (java.security.InvalidKeyException e) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Invalid key specification.");
        } catch (java.security.SignatureException e) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Signature exception.");
        } catch (me.ccrama.redditslide.util.Base64DecoderException e) {
            android.util.Log.e(me.ccrama.redditslide.util.Security.TAG, "Base64 decoding failed.");
        }
        return false;
    }
}