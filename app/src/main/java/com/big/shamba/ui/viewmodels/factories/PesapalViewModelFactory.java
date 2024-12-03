package com.big.shamba.ui.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.pesapal.PesapalRepository;
import com.big.shamba.ui.viewmodels.PesapalViewModel;

public class PesapalViewModelFactory implements ViewModelProvider.Factory {
    private final PesapalRepository repository;
    public PesapalViewModelFactory(PesapalRepository pesapalRepository) {
        this.repository = pesapalRepository;
    }

//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (modelClass.isAssignableFrom(PesapalViewModel.class)) {
//            return (T) new PesapalViewModel(repository);
//        }
//        throw new IllegalArgumentException("Unknown ViewModel class");
//    }
}
