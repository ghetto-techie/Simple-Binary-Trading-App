package com.big.shamba.ui.viewmodels.factories;

import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.pesapal.PesapalRepository;

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
