package com.big.shamba.utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class TokenStorage {
    private static final String PREF_NAME = "auth_prefs";
    private static final String TOKEN_KEY = "access_token";

    public static void saveToken(Context context, String token) throws Exception {
        String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences prefs = EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        prefs.edit().putString(TOKEN_KEY, token).apply();
    }

    public static String getToken(Context context) throws Exception {
        String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences prefs = EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        return prefs.getString(TOKEN_KEY, null);
    }
}
