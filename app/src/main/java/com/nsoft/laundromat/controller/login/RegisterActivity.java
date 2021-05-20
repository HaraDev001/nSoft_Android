package com.nsoft.laundromat.controller.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import static com.nsoft.laundromat.common.Global._isTest;
import static com.nsoft.laundromat.common.Global.sdkFacebook;
import static com.nsoft.laundromat.common.Global.sdkGoogle;
import static com.nsoft.laundromat.common.Global.strRegisterActivity;
import static com.nsoft.laundromat.common.Global.userEmail;
import static com.nsoft.laundromat.common.Global.userId;

public class RegisterActivity extends BaseActivity {

    private ImageView imgBack;
    private Button btnRegister;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private ImageView imgGoogle;
    private ImageView imgFacebook;

    private LinearLayout layRegister;
    private LinearLayout layBindingEmail;
    private LinearLayout laySendVerificationCode;

    private TextView txtNotice;
    private EditText edtBindingEmail;
    private EditText edtVerificationCode;

    int RG_SIGN_IN = 9001;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    private LoginButton facbookLoginButton;
    private CallbackManager callbackManager;

    private int pageStatus = 1; // 1: register, 2: send email, 3: verification

    private String first_name = "";
    private String last_name = "";
    private String email = "";
    private String imageUrl = "";
    private String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_register);

        strRegisterActivity = "RegisterActivity";

        initBasicUI();

        callbackManager = CallbackManager.Factory.create();
        facbookLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        facbookLoginButton.setLoginBehavior(LoginBehavior.WEB_ONLY);

        facbookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult.getAccessToken() != null){
                    if (strRegisterActivity.equals("RegisterActivity")){
                        loadUserProfile(loginResult.getAccessToken());
                    }
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Global.isMainActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        strRegisterActivity = "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RG_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//            if (currentAccessToken == null){
////                Toast.makeText(RegisterActivity.this, "User Logged out", Toast.LENGTH_SHORT).show();
////                showToast("don't call load user profile");
//            }
//
//            else{
//                if (strRegisterActivity.equals("RegisterActivity")){
////                    showToast("call load profile");
//                    loadUserProfile(currentAccessToken);
//                }
//            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken){

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                first_name = object.optString("first_name");
                last_name= object.optString("last_name");
                email = object.optString("email");
                id = object.optString("id");
                String image_url = "http://graph.facebook.com/" + id + "/picture?type=normal";
//                showToast("fn: " + first_name +  "ln: " + last_name + "email: " + email + "id: " + id + "url: " + image_url);
                trySocialRegister(id, first_name, last_name, email, image_url, Global.sdkFacebook);

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void initBasicUI(){
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                tryRegister();
            }
        });
        edtFirstName = findViewById(R.id.txt_first_name);
        edtLastName = findViewById(R.id.txt_last);
        edtEmail = findViewById(R.id.txt_email);
        edtPassword = findViewById(R.id.txt_password);
        edtConfirmPassword = findViewById(R.id.txt_confirm_password);
        imgFacebook = findViewById(R.id.img_facebook);
        imgFacebook.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                facbookLoginButton.performClick();
            }
        });
        imgGoogle = findViewById(R.id.img_google);
        imgGoogle.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                googleSignIn();
            }
        });

        signInButton = findViewById(R.id.btn_google_register);
        signInButton.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                googleSignIn();
            }
        });
        facbookLoginButton = findViewById(R.id.btn_face_register);

        layRegister = findViewById(R.id.lay_register);
        layBindingEmail = findViewById(R.id.lay_binding_email);
        laySendVerificationCode = findViewById(R.id.lay_verification);
        txtNotice = findViewById(R.id.txt_notice);
        edtBindingEmail = findViewById(R.id.edt_binding_email);
        edtVerificationCode = findViewById(R.id.edt_verification);
    }

    private void tryRegister(){
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String password= edtPassword.getText().toString();
        String confirmPassword= edtConfirmPassword.getText().toString();
        if (checkInputValue(firstName, lastName, email, password, confirmPassword)){
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_register);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("first_name", firstName);
            paramsPost.put("last_name", lastName);
            paramsPost.put("email", email);
            paramsPost.put("password", getMD5(password));
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
                            showToast("Please wait for account activation, or call nSofts for activation");
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                        else if (responseCode == Global.RESULT_EMAIL_DUPLICATE) {
                            CustomProgress.dismissDialog();
                            showToast("This email already exist");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Register failed");
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

    private void trySocialRegister(String id, String firstName, String lastName, String email, String imageUrl, final int sdkType){
        if (sdkType == sdkFacebook){
            facebookLogout();
        }
//        showToast("tryRegister is called");
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_social_register);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("first_name", firstName);
        paramsPost.put("last_name", lastName);
        paramsPost.put("email", email);
        paramsPost.put("sdk_id", id);
        paramsPost.put("photo_url", imageUrl);
        paramsPost.put("type", sdkType + "");
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
                        Global.userId = responseData.getInt("id");
                        Global.userEmail = responseData.getString("email");
                        if (userEmail.equals("") || userEmail == null){
                            showToast("Please wait for account activation, or call nSofts for activation.");
                            Intent intent = new Intent(RegisterActivity.this, EmailBindingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                        else {
                            showToast("Please wait for account activation, or call nSofts for activation");
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                    }
                    else if (responseCode == Global.RESULT_EMAIL_DUPLICATE) {
                        CustomProgress.dismissDialog();
                        showToast("This account already registered");
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

    private boolean checkInputValue(String firstName, String lastName, String email, String password, String confirmPassword){
        boolean isValue = true;
        if (firstName.equals("")){
            showToast("Please input First description");
            isValue = false;
        }
        else if(lastName.equals("")){
            showToast("Please input Last description");
            isValue = false;
        }
        else if(email.equals("")){
            showToast("Please input email");
            isValue = false;
        }
        else if(password.equals("")){
            showToast("Please input password");
            isValue = false;
        }
        else if(confirmPassword.equals("")){
            showToast("Please input Confirm password");
            isValue = false;
        }
        else if (!isEmailValid(email)){
            showToast("Please input correct email address");
            isValue = false;
        }
        else if (!confirmPassword.equals(password)){
            showToast("Please input correct password");
            isValue = false;
        }
        return isValue;
    }

    private void googleSignIn(){
        if (_isTest)
            showToast("google sign in is called");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RG_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
//            showToast("success");
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            showInformation(account);
        }
        catch (ApiException e){
            Log.e("Google sign in Error", "signInResult: Failed categoryName");
//            Log.e("Google sign in Error", "signInResult: Failed categoryName" + e.getStatusCode());
            Toast.makeText(RegisterActivity.this, "Google sign in failed", Toast.LENGTH_LONG).show();
        }
    }
    private void showInformation(GoogleSignInAccount account){
        if (_isTest)
            showToast("showInformation is called");
        first_name = account.getGivenName();
        last_name = account.getFamilyName();
        email = account.getEmail();
        id = account.getId();
        if (account.getPhotoUrl() != null){
            imageUrl = account.getPhotoUrl().toString();
        }
        if (_isTest)
            showToast( first_name + " "  + " " + last_name + " " + email + " " + id + " " + imageUrl);
        trySocialRegister(id, first_name, last_name, email, imageUrl, sdkGoogle);
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
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_forgot_password_email);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("email", email);
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
                            JSONObject responseData = response.getJSONObject("data");
                            userId = responseData.getInt("userId");
                            showPageStatus();
                        }
                        else if (responseCode == Global.RESULT_EMAIL_INCORRECT) {
                            CustomProgress.dismissDialog();
                            showToast("Your email incorrect");
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
            String url = "\n" + getServerUrl() + getString(R.string.str_url_send_verification_code);
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
                            pageStatus = 3;
                            showPageStatus();
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

    private void showPageStatus(){
        switch (pageStatus){
            case 1:
                showRegisterStatus();
                break;
            case 2:
                showEmailStatus();
                break;
            case 3:
                showVerificationStatus();
                break;
        }
    }

    private void showRegisterStatus(){
        layRegister.setVisibility(View.VISIBLE);
        layBindingEmail.setVisibility(View.GONE);
        laySendVerificationCode.setVisibility(View.GONE);
    }

    private void showEmailStatus(){
        layRegister.setVisibility(View.GONE);
        layBindingEmail.setVisibility(View.VISIBLE);
        laySendVerificationCode.setVisibility(View.GONE);
    }

    private void showVerificationStatus(){
        layRegister.setVisibility(View.GONE);
        layBindingEmail.setVisibility(View.GONE);
        laySendVerificationCode.setVisibility(View.VISIBLE);
    }
}
