package com.big.shamba.data.pesapal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.big.shamba.data.pesapal.models.AuthRequestPesapal;
import com.big.shamba.data.pesapal.models.AuthResponsePesapal;
import com.big.shamba.data.pesapal.models.RegisterIpnRequest;
import com.big.shamba.data.pesapal.models.RegisterIpnResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesapalRepository {
    private final PesapalApiService apiService;

    public PesapalRepository() {
        apiService = RetrofitInstance.getRetrofitInstance().create(PesapalApiService.class);
    }

    public LiveData<AuthResponsePesapal> authenticate(
            String consumerKey,
            String consumerSecret
    ) {
        MutableLiveData<AuthResponsePesapal> livedata = new MutableLiveData<>();
        AuthRequestPesapal authRequestPesapal = new AuthRequestPesapal(consumerKey, consumerSecret);
        apiService
                .getAuthToken(authRequestPesapal)
                .enqueue(new Callback<AuthResponsePesapal>() {
                    @Override
                    public void onResponse(Call<AuthResponsePesapal> call, Response<AuthResponsePesapal> response) {
                        if (response.isSuccessful()) {
                            AuthResponsePesapal authResponse = response.body();
                            livedata.postValue(authResponse);
                        } else {
                            AuthResponsePesapal errorResponse = new AuthResponsePesapal();
                            errorResponse.setMessage("Error: " + response.message());
                            livedata.postValue(errorResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponsePesapal> call, Throwable t) {
                        AuthResponsePesapal failureResponse = new AuthResponsePesapal();
                        failureResponse.setMessage("Failure: " + t.getMessage());
                        livedata.postValue(failureResponse);
                    }
                })
        ;
        return livedata;
    }

    public LiveData<RegisterIpnResponse> registerIPN(
            String token,
            RegisterIpnRequest request
    ) {
        MutableLiveData<RegisterIpnResponse> liveData = new MutableLiveData<>();

        String bearerToken = "Bearer " + token;
        apiService.registerIPN(bearerToken, request).enqueue(new Callback<RegisterIpnResponse>() {
            @Override
            public void onResponse(Call<RegisterIpnResponse> call, Response<RegisterIpnResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    RegisterIpnResponse errorResponse = new RegisterIpnResponse();
                    errorResponse.setStatus("Error");
                    errorResponse.setError(response.message());
                    liveData.postValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<RegisterIpnResponse> call, Throwable t) {
                RegisterIpnResponse errorResponse = new RegisterIpnResponse();
                errorResponse.setStatus("Error");
                errorResponse.setError(t.getMessage());
                liveData.postValue(errorResponse);
            }
        });

        return liveData;
    }
}
