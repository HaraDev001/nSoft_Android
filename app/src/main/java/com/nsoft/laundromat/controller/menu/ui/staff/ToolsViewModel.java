package com.nsoft.laundromat.controller.menu.ui.staff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToolsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ToolsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is staff fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}