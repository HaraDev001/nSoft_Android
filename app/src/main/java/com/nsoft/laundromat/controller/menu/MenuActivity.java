package com.nsoft.laundromat.controller.menu;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.controller.login.LoginActivity;
import com.nsoft.laundromat.controller.user.UserInfoActivity;
import com.nsoft.laundromat.utils.CustomTypefaceSpan;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsoft.laundromat.common.Global.userPhotoUrl;

public class MenuActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private LinearLayout layPrevious;
    private LinearLayout layNext;
    private CircleImageView imgUserLogo;
    private TextView txtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_shop, R.id.nav_transaction, R.id.nav_customer,
                R.id.nav_stuff, R.id.nav_product, R.id.nav_setting, R.id.nav_report, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        txtUserName =  headerView.findViewById(R.id.txt_user_name);
        txtUserName.setText(Global.userName);
        imgUserLogo = headerView.findViewById(R.id.img_user_logo);
        imgUserLogo.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoUserInfoActivity();
            }
        });
        if (!userPhotoUrl.equals("") && userPhotoUrl != null){
//            showToast("not empty");
            Glide.with(thisContext)
                    .load(userPhotoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.staff_user)
                    .into(imgUserLogo);
        }
        else{
//            showToast("empty");
            imgUserLogo.setImageDrawable(getResources().getDrawable(R.drawable.staff_user));
        }
        initBasicUI();
        Global.isMainActivity = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //<<<<mars_del_20191114 top bar menu icon
//        getMenuInflater().inflate(R.menu.main, menu);
        //>>>>
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_logout:
                Log.e("MenuActivity", "log out is clicked!!!");
                Toast.makeText(getApplicationContext(), "Log out clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dashboard:
                Log.e("MenuActivity", "Dashboard is clicked!!!");
                Toast.makeText(getApplicationContext(), "Dashboard is clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void initBasicUI(){
        layPrevious = findViewById(R.id.lay_previous);
        layPrevious.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                showToast("previous is clicked");
            }
        });
        layNext = findViewById(R.id.lay_next);
        layNext.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                showToast("next is clicked");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(thisActivity, LoginActivity.class);
        startActivity(i);
        thisActivity.overridePendingTransition(0, 0);
        finish();
    }

    private void gotoUserInfoActivity(){
        Intent intent = new Intent(thisContext, UserInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "lato_light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
