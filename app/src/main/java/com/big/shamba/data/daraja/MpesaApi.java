package com.big.shamba.data.daraja;

import com.big.shamba.models.dto.B2CRequest;
import com.big.shamba.models.dto.STKPushRequest;
import com.big.shamba.models.dto.B2CResponse;
import com.big.shamba.models.dto.OAuthResponse;
import com.big.shamba.models.dto.STKPushResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MpesaApi {

    @POST("oauth/v1/generate?grant_type=client_credentials")
    Call<OAuthResponse> getOAuthToken(@Header("Authorization") String authHeader);

    @POST("mpesa/b2c/v1/paymentrequest")
    Call<B2CResponse> performB2CPayment(@Header("Authorization") String authHeader, @Body B2CRequest b2cRequest);

    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPushResponse> performSTKPush(@Header("Authorization") String authHeader, @Body STKPushRequest stkPushRequest);
}
