package com.nsoft.laundromat.controller.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.nsoft.laundromat.common.Global._isTest;
import static com.nsoft.laundromat.common.Global.userId;

public class EmailBindingActivity extends BaseActivity {
    private ImageView imgBack;
    private Button btnSend;
    private EditText edtEmail;
    private EditText edtVerificationCode;
    private LinearLayout layEmail;
    private LinearLayout layVerification;
    private TextView txtNotice;

    private int pageStatus = 1; // 1: email status, 2: verify status, 3: set password status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_binding);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_email_binding);

        initBasicUI();
        showEmailStatus();
        pageStatus = 1;
    }

    private void initBasicUI(){
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoLoginActivity();
            }
        });
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                switch (pageStatus){
                    case 1:
                        sendEmail();
                        break;
                    case 2:
                        sendVerificationCode();
                        break;
                }
                showPageStatus();
            }
        });

        layEmail = findViewById(R.id.lay_email);
        layVerification = findViewById(R.id.lay_verification);
        edtEmail = findViewById(R.id.edt_email);
        if (_isTest){
            edtEmail.setText("joseowen1101@gmail.com");
        }
        edtVerificationCode = findViewById(R.id.edt_verification);
        txtNotice = findViewById(R.id.txt_notice);
    }

    private void showPageStatus(){
        switch (pageStatus){
            case 1:
                showEmailStatus();
                break;
            case 2:
                showVerificationStatus();
                break;
        }
    }

    private void showEmailStatus(){
        layEmail.setVisibility(View.VISIBLE);
        layVerification.setVisibility(View.GONE);
        txtNotice.setText(getResources().getString(R.string.message_content_input_email));
        btnSend.setText(getResources().getString(R.string.send_email));
    }

    private void showVerificationStatus(){
        layEmail.setVisibility(View.GONE);
        layVerification.setVisibility(View.VISIBLE);
        txtNotice.setText(getResources().getString(R.string.message_content_input_code));
        btnSend.setText(getResources().getString(R.string.send_code));
    }

    private void sendEmail(){
        String email = edtEmail.getText().toString();

        if (email.equals("")) {
            showToast("Pleas input email");
        }
        else if (!isEmailValid(email)){
            showToast("Please input correct email address");
        }
        else {
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_sdk_binding_email);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("email", email);
            paramsPost.put("userId", Global.userId + "");
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
                            pageStatus = 2;
                            JSONObject responseData = response.getJSONObject("data");
                            Global.userId = responseData.getInt("userId");
                            showPageStatus();
                        }
                        else if (responseCode == Global.RESULT_EMAIL_DUPLICATE) {
                            CustomProgress.dismissDialog();
                            showToast("This email already exist");
                        }
                        else if (responseCode == Global.RESULT_SEND_EMAIL_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Email sending failed");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Failed");
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

    private void sendVerificationCode(){
        String code = edtVerificationCode.getText().toString();
        if (code.equals("")) {
            showToast("Pleas input code");
        }
        else {
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_verification_code_binding);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("userId", userId + "");
            paramsPost.put("code", code);
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
                            showToast("Email binding success");
                            gotoLoginActivity();
                        }
                        else if (responseCode == Global.RESULT_VERIFICATION_CODE_USED) {
                            CustomProgress.dismissDialog();
                            showToast("Code already used");
                        }
                        else if (responseCode == Global.RESULT_VERIFICATION_CODE_INCORRECT) {
                            CustomProgress.dismissDialog();
                            showToast("Code is incorrect");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Failed");
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

    private void gotoLoginActivity(){
        Intent intent = new Intent(EmailBindingActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
        finish();
    }
}
