package com.big.shamba.data.pesapal;

import com.big.shamba.data.pesapal.models.AuthRequestPesapal;
import com.big.shamba.data.pesapal.models.AuthResponsePesapal;
import com.big.shamba.data.pesapal.models.RegisterIpnRequest;
import com.big.shamba.data.pesapal.models.RegisterIpnResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PesapalApiService {
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("api/Auth/RequestToken")
    Call<AuthResponsePesapal> getAuthToken(
            @Body AuthRequestPesapal request
    );

    @POST("api/URLSetup/RegisterIPN")
    Call<RegisterIpnResponse> registerIPN(
            @Header("Authorization")
            String bearerToken,
            @Body
            RegisterIpnRequest registerIpnRequest
    );

}
