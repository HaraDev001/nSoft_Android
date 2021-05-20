package com.nsoft.laundromat.controller.menu.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.login.LoginActivity;
import com.nsoft.laundromat.controller.menu.ui.offer.OfferViewModel;

import static com.nsoft.laundromat.common.Global.userEmailKey;
import static com.nsoft.laundromat.common.Global.userId;
import static com.nsoft.laundromat.common.Global.userPasswordKey;
import static com.nsoft.laundromat.common.Global.userSdkKey;

public class LogoutFragment extends BaseFragment {

    private OfferViewModel offerViewModel;
    private LinearLayout layItemReplenish;
    private ImageView imgHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);

        imgHome = root.findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
            }
        });

        tryUserLogOut();

        return root;
    }

    private void tryUserLogOut(){
        userId = -100;
        Global.userName = "";
        Global.userEmail = "";
        Global.userFacebookId = "";
        Global.userGoogleId = "";
        Global.userPaypalEmail = "";
        Global.userPassword = "";
        Global.userUniqueId = "";
        setInformationToSystem(userEmailKey, "");
        setInformationToSystem(userPasswordKey, "");
        setInformationToSystem(userSdkKey, "");
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

}