package com.big.shamba.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.pesapal.PesapalRepository;
import com.big.shamba.data.pesapal.models.AuthResponsePesapal;
import com.big.shamba.data.pesapal.models.RegisterIpnRequest;
import com.big.shamba.data.pesapal.models.RegisterIpnResponse;

public class PesapalViewModel extends ViewModel {
    private final PesapalRepository repository;

    public PesapalViewModel() {
        repository = new PesapalRepository();
    }

    public LiveData<AuthResponsePesapal> authenticate(String consumerKey, String consumerSecret) {
        return repository.authenticate(consumerKey, consumerSecret);
    }

    public LiveData<RegisterIpnResponse> registerIPN(String token, String url, String ipnNotificationType) {
        RegisterIpnRequest request = new RegisterIpnRequest(url, ipnNotificationType);
        return repository.registerIPN(token, request);
    }
}