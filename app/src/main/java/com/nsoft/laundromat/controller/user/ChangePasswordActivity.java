package com.nsoft.laundromat.controller.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.userId;
import static com.nsoft.laundromat.common.Global.userPassword;

public class ChangePasswordActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private EditText edtCurrentPassword;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private Button btnChange;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_change_password);

        initBasicUI();
        showBottomInformation();
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
        txtTopTitle.setText(R.string.title_change_password);
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(thisContext, MenuActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                thisActivity.startActivity(intent);
//                finish();
            }
        });
        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopBranch = findViewById(R.id.txt_shop_branch);

        edtCurrentPassword = findViewById(R.id.edt_current_password);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnChange = findViewById(R.id.btn_change);
        btnChange.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                changePassword();
            }
        });
        mSlidingUpPanelLayout = findViewById(R.id.activity_change_password);
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
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    private void changePassword(){
        String strCurrentPassword = edtCurrentPassword.getText().toString();
        String strPassword = edtPassword.getText().toString();
        String strConfirmPassword = edtConfirmPassword.getText().toString();

        if (checkInputValues(strCurrentPassword, strPassword, strConfirmPassword)){
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_change_password);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("userId", userId + "");
            paramsPost.put("password", getMD5(strPassword));
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == Global.RESULT_SUCCESS) {
                            CustomProgress.dismissDialog();
                            JSONObject responseData =  response.getJSONObject("data");
                            userPassword = responseData.getString("password");
                            showToast("password successfully changed");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("change failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomProgress.dismissDialog();
                        showToast("Network error");
                    }

                }
            }.execute(httpCallPost);
        }
    }

    private boolean checkInputValues(String currentPass, String pass, String confirmPass){
        boolean isValue = true;
        if (currentPass.equals("")){
            showToast("Please input current password");
            isValue = false;
        }
        else if (pass.equals("")){
            showToast("Please input new password");
            isValue = false;
        }
        else if (confirmPass.equals("")){
            showToast("Please input confirm password");
            isValue = false;
        }
        else if (!confirmPass.equals(pass)){
            showToast("Please input correct confirm password");
            isValue = false;
        }
        else if (!getMD5(currentPass).equals(userPassword)){
            showToast("Please input correct current password");
            isValue = false;
        }
        return isValue;
    }
}
