package com.nsoft.laundromat.controller.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.nsoft.laundromat.controller.menu.MenuActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import static com.nsoft.laundromat.common.Global._isTest;
import static com.nsoft.laundromat.common.Global.sdkFacebook;
import static com.nsoft.laundromat.common.Global.sdkGoogle;
import static com.nsoft.laundromat.common.Global.strRegisterActivity;
import static com.nsoft.laundromat.common.Global.userEmail;
import static com.nsoft.laundromat.common.Global.userEmailBindingStatus;
import static com.nsoft.laundromat.common.Global.userEmailKey;
import static com.nsoft.laundromat.common.Global.userExpiredDate;
import static com.nsoft.laundromat.common.Global.userName;
import static com.nsoft.laundromat.common.Global.userOwnerLevel;
import static com.nsoft.laundromat.common.Global.userPasswordKey;
import static com.nsoft.laundromat.common.Global.userSdkKey;

public class LoginActivity extends BaseActivity {

    private Button btnLogin;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView txtRegister;
    private TextView txtForgot;
    private ImageView imgFaceBook;
    private ImageView imgGoogle;
    private LoginButton facbookLoginButton;

    private CallbackManager callbackManager;

    int RG_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_login);

        initBasicUI();

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getApplicationContext().getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.d("KeyHash:", "KeyHash name not exception");
//
//        } catch (NoSuchAlgorithmException e) {
//            Log.d("KeyHash:", "KeyHash algorithm not exception");
//
//        }

        checkAutoLogin();

        callbackManager = CallbackManager.Factory.create();
        facbookLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        facbookLoginButton.setLoginBehavior(LoginBehavior.WEB_ONLY );

        facbookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginActivity", "fb login success");
                if (loginResult.getAccessToken() == null){
                    Log.e("LoginActivity", "access token null");
                }

                else{
                    if (!strRegisterActivity.equals("RegisterActivity")){
                        Log.e("LoginActivity", "access token not null");
                        loadUserProfile(loginResult.getAccessToken());
                    }
                }
            }

            @Override
            public void onCancel() {
                Log.d("LoginActivity", "fb login cancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("LoginActivity", "fb login error");

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
////                Toast.makeText(LoginActivity.this, "User Logged out", Toast.LENGTH_SHORT).show();
//                Log.e("LoginActivity", "access token null");
//            }
//
//            else{
//                if (!strRegisterActivity.equals("RegisterActivity")){
//                    Log.e("LoginActivity", "access token not null");
//                    loadUserProfile(currentAccessToken);
//                }
//            }
        }
    };

    private void checkAutoLogin(){
        String strEmail = "";
        String strPass = "";
        String strSdk = "";
        strEmail = getInformationFromSystem(userEmailKey);
        strPass = getInformationFromSystem(userPasswordKey);
        strSdk = getInformationFromSystem(userSdkKey);
        if (!strSdk.equals("")){
            if (!strEmail.equals("")){
                int sdkType = Integer.parseInt(strSdk);
                String image_url = "";
                trySocialLogin(strEmail, image_url, sdkType);
            }
        }
        else if(!strEmail.equals("") && !strPass.equals("")){
            tryUserLogin(strEmail, strPass);
        }
    }

    private void initBasicUI(){
        edtEmail = findViewById(R.id.edt_user_name);
        edtPassword= findViewById(R.id.edt_user_password);
        btnLogin = findViewById(R.id.btn_login);
        if (_isTest){
//            edtEmail.setText("derek.nsofts@gmail.com");
//            edtEmail.setText("joseowen1101@gmail.com");
            edtEmail.setText("tomas@gmail.com");
            edtPassword.setText("aaa");
        }
        btnLogin.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                tryUserLogin(email, password);
            }
        });

        txtRegister = findViewById(R.id.txt_register);
        txtRegister.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });

        txtForgot = findViewById(R.id.txt_forgot);
        txtForgot.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                thisActivity.startActivity(intent);
            }
        });
        imgFaceBook = findViewById(R.id.img_facebook);

        imgFaceBook.setOnClickListener(new OnMultiClickListener() {
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

        facbookLoginButton = findViewById(R.id.login_button);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                googleSignIn();
            }
        });
    }

    private void loadUserProfile(AccessToken newAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
            try {
                String first_name = object.getString("first_name");
                String last_name = object.getString("last_name");
//                    String email = object.getString("email");
                String id = object.getString("id");
                String image_url = "http://graph.facebook.com/" + id + "/picture?type=normal";
                trySocialLogin(id, image_url, Global.sdkFacebook);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onStart() {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(thisContext);
//        if (account != null){
//            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
//        }
        super.onStart();
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RG_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            showInformation(account);
        }
        catch (ApiException e){
            Log.e("Google sign in Error", "signInResult: Failed categoryName");
//            Log.e("Google sign in Error", "signInResult: Failed categoryName" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Google sign in failed", Toast.LENGTH_LONG).show();
        }
    }

    private void showInformation(GoogleSignInAccount account){
        String email = account.getEmail();
        String id = account.getId();
        String image_url = "";
        if (account.getPhotoUrl() != null){
            image_url = account.getPhotoUrl().toString();
        }
        trySocialLogin(id, image_url, sdkGoogle);
    }

    private void tryUserLogin(String email, final String password){
        if (checkInputValues(email,password)) {
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.GET);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_login);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
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
                            JSONObject responseData =  response.getJSONObject("data");
                            Global.userId = responseData.getInt("id");
                            userEmail = responseData.getString("email");
                            userName = responseData.getString("first_name") + " " + responseData.getString("last_name");
                            userExpiredDate = responseData.getString("expired_date");
                            userOwnerLevel = responseData.getInt("owner_level");
                            Global.userPassword = responseData.getString("password");
                            Global.userUniqueId = responseData.optString("unique_id");
                            Global.userLastShopId = responseData.getInt("last_shop_id");
                            Global.userPhotoUrl = responseData.optString("photo_url");
                            Global.MACHINE_ID = responseData.getString("machine_id");
                            Global.SHOP_NAME = responseData.getString("shop_name");
                            Global.SHOP_BRANCH = responseData.getString("branch_name");

                            setInformationToSystem(userEmailKey, userEmail);
                            setInformationToSystem(userPasswordKey, password);
                            setInformationToSystem(userSdkKey, "");

                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                        else if (responseCode == Global.RESULT_OVER_EXPIRED) {
                            CustomProgress.dismissDialog();
                            showToast("Please call nSofts for subscription");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Login Failed");
                        }
                        else if (responseCode == Global.RESULT_EMAIL_PASSWORD_INCORRECT) {
                            CustomProgress.dismissDialog();
                            showToast("Email or password incorrect");
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

    public boolean checkInputValues(String email, String password) {
        boolean isValue = true;
        if(email.equals("")) {
            showToast("Please input Email");
            isValue = false;
        }
        else if(password.equals("")) {
            showToast("Please input Password");
            isValue = false;
        }
        else if (!isEmailValid(email)){
            showToast("Please input correct Email");
            isValue = false;
        }
        return isValue;
    }

    private void trySocialLogin(final String email, final String image_url, final int sdkType){
        if (sdkType == sdkFacebook){
            facebookLogout();
        }
//        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_login);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("email", email);
        paramsPost.put("type", sdkType + "");
        paramsPost.put("photo_url", image_url);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
//                        CustomProgress.dismissDialog();
                        JSONObject responseData =  response.getJSONObject("data");
                        Global.userId = responseData.getInt("id");
                        if (sdkType == Global.sdkFacebook){
                            Global.userFacebookId = responseData.optString("facebook_id");
                        }
                        else if (sdkType == Global.sdkGoogle){
                            Global.userGoogleId = responseData.optString("google_id");
                        }
                        userEmailBindingStatus = responseData.getInt("email_binding_status");
                        if (userEmailBindingStatus == 1){
                            userEmail = responseData.getString("email");
                            userName = responseData.getString("first_name") + " " + responseData.getString("last_name");
                            userExpiredDate = responseData.getString("expired_date");
                            userOwnerLevel = responseData.getInt("owner_level");
                            Global.userPassword = responseData.getString("password");
                            Global.userUniqueId = responseData.optString("unique_id");
                            Global.userPhotoUrl = responseData.getString("photo_url");
                            Global.userLastShopId = responseData.getInt("last_shop_id");
                            Global.MACHINE_ID = responseData.getString("machine_id");
                            Global.SHOP_NAME = responseData.getString("shop_name");
                            Global.SHOP_BRANCH = responseData.getString("branch_name");

                            setInformationToSystem(userEmailKey, email);
                            setInformationToSystem(userPasswordKey, "");
                            setInformationToSystem(userSdkKey, sdkType + "");

                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, EmailBindingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            finish();
                        }
                    }
                    else if (responseCode == Global.RESULT_OVER_EXPIRED) {
//                        CustomProgress.dismissDialog();
                        showToast("Please call nSofts for subscription");
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
//                        CustomProgress.dismissDialog();
                        showToast("Login failed");
                    }
                    else if (responseCode == Global.RESULT_EMAIL_PASSWORD_INCORRECT) {
//                        CustomProgress.dismissDialog();
                        showToast("This account doesn't exist");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }

            }
        }.execute(httpCallPost);
    }

}

