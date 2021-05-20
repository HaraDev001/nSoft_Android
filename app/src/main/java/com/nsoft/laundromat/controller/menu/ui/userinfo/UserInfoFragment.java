package com.nsoft.laundromat.controller.menu.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.menu.ui.setting.SettingViewModel;
import com.nsoft.laundromat.controller.user.ChangePasswordActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;
import static com.nsoft.laundromat.common.Global.userEmail;
import static com.nsoft.laundromat.common.Global.userExpiredDate;
import static com.nsoft.laundromat.common.Global.userName;
import static com.nsoft.laundromat.common.Global.userOwnerLevel;

public class UserInfoFragment extends BaseFragment {
    private TextView txtPageTitle;
    private LinearLayout layNotice;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;

    private SettingViewModel settingViewModel;
    private ImageView imgHome;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtExpiryDate;
    private TextView txtRole;
    private LinearLayout layChangePassword;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private TextView txtShopName;
    private TextView txtShopBranch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                ViewModelProviders.of(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user_info, container, false);
        currentRoot = root;
        final TextView textView = root.findViewById(R.id.text_send);
        settingViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        invisibleTopIcons();
        imgHome = root.findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent i = new Intent(getActivity(), MenuActivity.class);
//                startActivity(i);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        txtShopName = root.findViewById(R.id.txt_shop_name);
        txtShopBranch = root.findViewById(R.id.txt_shop_branch);

        txtName = root.findViewById(R.id.txt_name);
        txtEmail = root.findViewById(R.id.txt_mail);
        txtExpiryDate = root.findViewById(R.id.txt_expiry_date);
        txtRole = root.findViewById(R.id.txt_role);
        layChangePassword = root.findViewById(R.id.lay_change_password);
        layChangePassword.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoChangePasswordActivity();
            }
        });

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_user_info);
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED && previousState != SlidingUpPanelLayout.PanelState.EXPANDED) {
                    sendNoticeRequest(NOTICE_GET, EMPTY_STRING);
                }
            }
        });
        showBottomInformation();
        showUserInformation();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        strNoticeDetailActivity = "";
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_user_info));
        layNotice = getActivity().findViewById(R.id.lay_notice);
        layNextPrevious = getActivity().findViewById(R.id.lay_next_previous);
        layPremiumRegular = getActivity().findViewById(R.id.lay_premium_regular);
        layAvailableDisable = getActivity().findViewById(R.id.lay_available_disable);
        layNextPrevious.setVisibility(View.GONE);
        layPremiumRegular.setVisibility(View.GONE);
        layAvailableDisable.setVisibility(View.GONE);
        layNotice.setVisibility(View.GONE);
    }
    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    private void showUserInformation(){
        txtName.setText(userName);
        txtEmail.setText(userEmail);
        String dtStart = userExpiredDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dtStart);
            SimpleDateFormat spf= new SimpleDateFormat("EEE, d MMM yyyy");
            String dateOne = spf.format(date);
            txtExpiryDate.setText(dateOne);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        0: Owner, 1: Manager, 2: Supervisor, 3: Staff
        switch (userOwnerLevel){
            case 0:
                txtRole.setText(getContext().getResources().getString(R.string.str_owner));
                break;
            case 1:
                txtRole.setText(getContext().getResources().getString(R.string.str_manager));
                break;
            case 2:
                txtRole.setText(getContext().getResources().getString(R.string.str_supervisor));
                break;
            case 3:
                txtRole.setText(getContext().getResources().getString(R.string.str_staff));
                break;
            default:
                txtRole.setText(EMPTY_STRING);
                break;
        }
    }

    private void gotoChangePasswordActivity(){
        Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }
}