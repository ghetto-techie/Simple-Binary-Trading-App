package com.big.shamba.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ReferralCodeGenerator {

    public static String generateReferralCode(String userID) {
        try {
            // Create SHA-256 hash as per the user ID
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(userID.getBytes());

            // Encode with Base64 to make it URL-Friendly
            String encoded = android.util.Base64.encodeToString(hash, android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING | android.util.Base64.NO_WRAP);

            return encoded.substring(0, 8); // Trim the length of characters to 8
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
