package com.nsoft.laundromat.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.userEmail;
import static com.nsoft.laundromat.common.Global.userExpiredDate;
import static com.nsoft.laundromat.common.Global.userName;
import static com.nsoft.laundromat.common.Global.userOwnerLevel;

public class UserInfoActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtExpiryDate;
    private TextView txtRole;
    private LinearLayout layChangePassword;


    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_user_info);

        initBasicUI();
        showBottomInformation();
        showUserInformation();
    }
    private void initBasicUI(){
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_user_information);
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(AddShopActivity.this, MenuActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                thisActivity.startActivity(intent);
//                finish();
            }
        });
        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopBranch = findViewById(R.id.txt_shop_branch);

        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_mail);
        txtExpiryDate = findViewById(R.id.txt_expiry_date);
        txtRole = findViewById(R.id.txt_role);
        layChangePassword = findViewById(R.id.lay_change_password);
        layChangePassword.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoChangePasswordActivity();
            }
        });

        mSlidingUpPanelLayout = findViewById(R.id.activity_user_info);
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
        Global.isMainActivity = false;
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
            SimpleDateFormat spf= new SimpleDateFormat("MMMM d, yyyy");
            String dateOne = spf.format(date);
            txtExpiryDate.setText(dateOne);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        0: Owner, 1: Manager, 2: Supervisor, 3: Staff
        switch (userOwnerLevel){
            case 0:
                txtRole.setText(getResources().getString(R.string.str_owner));
                break;
            case 1:
                txtRole.setText(getResources().getString(R.string.str_manager));
                break;
            case 2:
                txtRole.setText(getResources().getString(R.string.str_supervisor));
                break;
            case 3:
                txtRole.setText(getResources().getString(R.string.str_staff));
                break;
            default:
                txtRole.setText(EMPTY_STRING);
                break;
        }
    }

    private void gotoChangePasswordActivity(){
        Intent intent = new Intent(thisContext, ChangePasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }
}
