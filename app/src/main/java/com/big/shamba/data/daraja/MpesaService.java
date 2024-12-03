package com.big.shamba.data.daraja;

import android.util.Base64;
import android.util.Log;

import com.big.shamba.models.dto.B2CRequest;
import com.big.shamba.models.dto.STKPushRequest;
import com.big.shamba.models.dto.B2CResponse;
import com.big.shamba.models.dto.OAuthResponse;
import com.big.shamba.models.dto.STKPushResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class MpesaService {
    private static final String BASE_URL = "https://sandbox.safaricom.co.ke/";
    private static final String TAG = "MpesaService";
    private MpesaApi mpesaApi;

    public MpesaService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mpesaApi = retrofit.create(MpesaApi.class);
    }

    // Get OAuth Token from API
//    public void getOAuthToken(String consumerKey, String consumerSecret, Callback<OAuthResponse> callback) {
//        String credentials = consumerKey + ":" + consumerSecret;
//        String authHeader = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        Call<OAuthResponse> call = mpesaApi.getOAuthToken(authHeader);
//        call.enqueue(callback);
//    }

    public void getOAuthToken(String consumerKey, String consumerSecret, Callback<OAuthResponse> callback) {
        String credentials = consumerKey + ":" + consumerSecret;
        String authHeader = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Call<OAuthResponse> call = mpesaApi.getOAuthToken(authHeader);
        call.enqueue(new Callback<OAuthResponse>() {
            @Override
            public void onResponse(Call<OAuthResponse> call, Response<OAuthResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token obtained: " + response.body().getAccess_token());
                    callback.onResponse(call, response);
                } else {
                    Log.d(TAG, "Failed to get token: " + response.message());
                    callback.onFailure(call, new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<OAuthResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed to get token: " + t.getLocalizedMessage());
                t.printStackTrace();
                callback.onFailure(call, t);
            }
        });
    }

    // STK Push for deposits
    public void performSTKPush(String token, STKPushRequest stkPushRequest, Callback<STKPushResponse> callback) {
        String authHeader = "Bearer " + token;
        Call<STKPushResponse> call = mpesaApi.performSTKPush(authHeader, stkPushRequest);
        call.enqueue(callback);
    }

    // For Bulk payments
    public void performB2CPayment(String token, B2CRequest b2cRequest, Callback<B2CResponse> callback) {
        String authHeader = "Bearer " + token;
        Call<B2CResponse> call = mpesaApi.performB2CPayment(authHeader, b2cRequest);
        call.enqueue(callback);
    }
}

