package com.nsoft.laundromat.controller.menu.ui.offer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OfferViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OfferViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is product fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}