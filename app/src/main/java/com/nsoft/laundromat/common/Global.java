package com.nsoft.laundromat.common;

import android.view.View;

import com.nsoft.laundromat.controller.customer.CustomerTransactionView;
import com.nsoft.laundromat.controller.item.ItemView;
import com.nsoft.laundromat.controller.menu.ui.home.ShopView;
import com.nsoft.laundromat.controller.menu.ui.offer.OfferView;
import com.nsoft.laundromat.controller.model.CustomerObject;
import com.nsoft.laundromat.controller.model.DashInventoryObject;
import com.nsoft.laundromat.controller.model.DashMachineObject;
import com.nsoft.laundromat.controller.model.DashUserObject;
import com.nsoft.laundromat.controller.model.NoticeObject;
import com.nsoft.laundromat.controller.model.OfferDetailObject;
import com.nsoft.laundromat.controller.model.ReplenishItemDetailObject;
import com.nsoft.laundromat.controller.model.ReplenishItemObject;
import com.nsoft.laundromat.controller.model.ReportObject;
import com.nsoft.laundromat.controller.model.StaffObject;
import com.nsoft.laundromat.controller.model.TransactionObject;
import com.nsoft.laundromat.controller.staff.StaffTransactionView;

import java.util.ArrayList;
import java.util.Timer;

public class Global {

    public static int sdkFacebook = 1;
    public static int sdkGoogle = 2;

    // region -- query --
    public static int DASHBOARD_GET = 100;
    public static int DASHBOARD_GET_CATEGORY = 101;
    public static int MY_SHOPS_GET_AMOUNT = 110;
    public static int MY_SHOPS_ADD = 111;
    public static int TRANSACTIONS_GET = 120;
    public static int TRANSACTIONS_GET_PREV = 121;
    public static int TRANSACTIONS_GET_NEXT = 122;
    public static int CUSTOMERS_GET_TOP20 = 130;
    public static int CUSTOMERS_GET_SEARCH_ALL = 134;
    public static int CUSTOMERS_GET_SEARCH = 135;
    public static int CUSTOMERS_SELECTED_DETAIL = 131;
    public static int CUSTOMERS_PREV_DETAIL = 132;
    public static int CUSTOMERS_NEXT_DETAIL = 133;
    public static int STAFFS_GET = 140;
    public static int STAFFS_SELECTED_DETAIL = 141;
    public static int STAFFS_PREV_DETAIL = 142;
    public static int STAFFS_NEXT_DETAIL = 143;
    public static int OFFERS_GET = 150;
    public static int OFFERS_GET_DETAIL = 151;
    public static int OFFERS_SAVE_DETAIL = 152;
    public static int OFFERS_REPLENISH_GET_CATEGORY = 153;
    public static int OFFERS_REPLENISH_GET_CATEGORY_DETAIL = 154;
    public static int OFFERS_REPLENISH_SAVE = 155;
    public static int REPORTS_SALES = 160;
    public static int REPORTS_ITEM_SOLD = 161;
    public static int REPORTS_CONSOLIDATE = 162;
    public static int REPORTS_MORE = 163;
    public static int NOTICE_GET = 170;
    public static int NOTICE_VIEWED = 171;
    public static int NOTICE_HIDDEN = 172;
    public static int NOTICE_ACTED= 173;
    public static int EMAIL_STAFF_PROFILE = 241;
    public static int EMAIL_REPORTS_SALES = 260;
    public static int EMAIL_REPORTS_ITEM_SOLD = 261;
    public static int EMAIL_REPORTS_CONSOLIDATE = 262;
    public static int EMAIL_REPORTS_CUSTOMER_LIST = 263;
    public static int EMAIL_REPORTS_PRODUCT_ITEM_LIST = 264;
    public static int EMAIL_REPORTS_INVENTORY = 265;
    public static int EMAIL_REPORTS_TOP_ITEMS = 266;
    public static int EMAIL_REPORTS_LEAST_ITEMS = 267;
    public static int EMAIL_REPORTS_MONTHLY_REPORT = 268;
    public static int EMAIL_REPORTS_ITEM_SOLD_BREAKDOWN = 269;
    public static int EMAIL_REPORTS_PAYINS_PAYOUT = 2610;
    public static int EMAIL_REPORTS_FINANCIAL_STATEMENT = 2611;
    public static int EMAIL_REPORTS_PETTY_CASH= 2612;

    // endregion

    public static int SERVER_CONNECTION_COUNT = 9;

    // region -- valuer in DB --
    public static int ACTIVATED = 1;
    public static int DEACTIVATED = 2;
    public static int EXPIRED = 3;
    public static int DATA_REQUESTED = 4;
    public static int DATA_RESPONSED = 5;
    // endregion

     // region -- test --
    public static boolean _isTest = false;
    public static boolean _isCloud = true;
    // endregion

    public  static String str_cloud_http_server = "http://35.247.134.80";
    public  static String str_local_http_server = "http://192.168.1.100:10000";

    // region -- http communication [201, 300] --
    public static int RESULT_SUCCESS = 201;
    public static int RESULT_FAILED = 202;
    public static int RESULT_EMAIL_PASSWORD_INCORRECT = 203;
    public static int RESULT_ACCOUNT_DUPLICATE = 204;
    public static int RESULT_EMAIL_DUPLICATE = 205;
    public static int RESULT_SEARCH_EMPTY = 206;
    public static int RESULT_EMPTY_SHOP = 207;
    public static int RESULT_OVER_EXPIRED = 208;
    public static int RESULT_MACHINE_ID_EXIST = 209;
    public static int RESULT_EMPTY_MACHINE_ID = 210;

    public static int RESULT_INCORRECT_UUID = 211;
    public static int RESULT_EMPTY_DATA = 212;
    public static int RESULT_EMAIL_INCORRECT = 213;
    public static int RESULT_SEND_EMAIL_FAILED = 214;

    public static int RESULT_VERIFICATION_CODE_USED = 215;
    public static int RESULT_VERIFICATION_CODE_INCORRECT = 216;
    // endregion

    // region --user information --
    public static int userId;
    public static String userName = "";
    public static String userEmail = "";
    public static String userFacebookId = "";
    public static String userGoogleId = "";
    public static String userPaypalEmail = "";
    public static String userPassword = "";
    public static String userUniqueId = "";
    public static String userPhotoUrl = "";
    public static int userLastShopId = -1;
    public static String userExpiredDate = "";
    public static int userOwnerLevel;
    public static int userEmailBindingStatus;
    // endregion

    //region --- arrayList ---
    public static ArrayList<CustomerObject> customerObjectArrayList;
    public static ArrayList<StaffObject> staffObjectArrayList;
    public static ArrayList<TransactionObject> transactionObjectArrayList;
    public static ArrayList<CustomerTransactionView> customerTransactionViewArrayList;
    public static ArrayList<StaffTransactionView> staffTransactionViewArrayList;
    public static ArrayList<ShopView> shopViewArrayList;
    public static ArrayList<ItemView> itemViewArrayList;
    public static ArrayList<ItemView> itemViewCategoryArrayList;
    public static ArrayList<OfferView> offerViewArrayList;
    public static ArrayList<DashMachineObject> dashMachineObjectArrayList;
    public static ArrayList<DashMachineObject> dashDryerObjectArrayList;
    public static ArrayList<DashMachineObject> dashWasherObjectArrayList;
    public static ArrayList<DashInventoryObject> dashInventoryObjectArrayList;
    public static ArrayList<DashUserObject> dashUserObjectArrayList;
    public static ArrayList<ReplenishItemObject> replenishItemObjectArrayList;
    public static ArrayList<ReplenishItemDetailObject> replenishItemDetailObjectArrayList;
    public static ArrayList<ReportObject> reportObjectArrayList;
    public static ArrayList<OfferDetailObject> offerDetailObjectArrayList;
    public static ArrayList<NoticeObject> noticeObjectArrayList;
    public static View row;
    // endregion

    public static Timer connectionTimer;
    public static int connectCount = 0;

    // region --- for coding const---
    public static int CUSTOMER_PREMIUM = 1;
    public static int CUSTOMER_REGULAR = 2;
    public static int OFFER_AVAILABLE = 1;
    public static int OFFER_DISABLE = 2;
    // endregion

    public static String DEFAULT_STRING = "";
    public static String EMPTY_STRING = "---";

    // region --shop information --
    public static int SHOP_PAGE_DELAY_TIME = 6000; // 6s
    public static String SHOP_NAME = "Squeaky Clean";
    public static String SHOP_BRANCH = "San Jose";
    public static String MACHINE_ID = "038D0240-045C-056B-E906-650700080009";
    // endregion

    // region --offer information --
    public static String currentOfferDescription = "";
    public static String currentOfferCode = "";
    public static String currentOfferCategory = "";
    public static String currentOfferPrice= "";
    // endregion

    // region --customer information --
    public static String currentCustomerId = "";
    public static String currentCustomerName = "";
    public static String currentCustomerAddress = "";
    public static String currentCustomerMobile = "";
    public static String currentCustomerEmail = "";
    public static String currentCustomerBalance = "";
    public static int currentCustomerType = 1;
    // endregion

    // region --staff information --
    public static String currentStaffId = "";
    public static int currentStaffPosition = 0;
    public static String currentStaffName = "";
    public static String currentStaffAddress = "";
    public static String currentStaffMobile = "";
    public static String currentStaffEmail = "";
    public static String currentStaffRole = "";
    // endregion

    // region --notice information --
    public static String currentNoticeType = "";
    public static String currentNoticeTitle = "";
    public static String currentNoticeContent = "";
    public static int currentNoticeNo = 0;
    public static String currentNoticeActionStatus = "";
    public static String currentNoticeViewStatus = "";
    // endregion

    // region --notice information --
    public static String userEmailKey = "user_email";
    public static String userPasswordKey = "user_pass";
    public static String userSdkKey = "user_sdk";
    // endregion

    public static int CATEGORY_FIRST_ID = 1000;

    public static String NOTICE_MESSAGE = "MESSAGE";
    public static String NOTICE_NOTICE = "NOTICE";
    public static String NOTICE_REQUEST = "REQUEST";
    public static String NOTICE_WARNING = "WARNING";

    public static String pastActivityName = "";
    public static String currentActivityName = "";
    public static boolean isMainActivity = false;
    public static String strAddShopActivity = "";
    public static String strNoticeDetailActivity = "";
    public static String strRegisterActivity = "";

    public static String strStaffLogIn = "login";
    public static String strStaffLogOut = "logout";
    public static String strStaffNone = "none";

    public static int REFRESH_TIME = 2000;

 }