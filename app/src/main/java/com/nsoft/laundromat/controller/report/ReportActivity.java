package com.nsoft.laundromat.controller.report;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

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
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.controller.menu.MenuActivity;
import com.nsoft.laundromat.utils.CustomTypefaceSpan;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsoft.laundromat.common.Global.userPhotoUrl;

public class ReportActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private CircleImageView imgUserLogo;
    private TextView txtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        thisContext = this;
        setContentView(R.layout.activity_report);
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

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_sales_report, R.id.nav_item_sold, R.id.nav_consolidated, R.id.nav_more,
                R.id.nav_week, R.id.nav_month, R.id.nav_year, R.id.nav_consolidated, R.id.nav_report_customer, R.id.nav_report_offer)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        txtUserName =  headerView.findViewById(R.id.txt_user_name);
        txtUserName.setText(Global.userName);
        imgUserLogo = headerView.findViewById(R.id.img_user_logo);

        if (!userPhotoUrl.equals("") && userPhotoUrl != null){
            Glide.with(thisContext)
                    .load(userPhotoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.staff_user)
                    .into(imgUserLogo);
        }
        else{
            imgUserLogo.setImageDrawable(getResources().getDrawable(R.drawable.staff_user));
        }

        Global.isMainActivity = false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(thisActivity, MenuActivity.class);
        startActivity(i);
        thisActivity.overridePendingTransition(0, 0);
        finish();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "lato_light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
