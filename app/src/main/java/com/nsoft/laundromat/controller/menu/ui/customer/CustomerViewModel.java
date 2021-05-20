package com.nsoft.laundromat.controller.menu.ui.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CustomerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is customer fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}