package com.futureskyltd.app.fantacyseller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.ContactModel.ContactModel;
import com.futureskyltd.app.utils.Profile.Profile;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.SaveContact.SaveContact;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.helper.FragmentChangeListener;
import com.futureskyltd.app.helper.NetworkReceiver;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentMainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, FragmentChangeListener,
        NetworkReceiver.ConnectivityReceiverListener {
    public final String TAG = this.getClass().getSimpleName();
    Toolbar toolbar;
    ListView listView;
    NavigationView navigationView;
    DrawerLayout drawer;
    Fragment mContent;
    Snackbar snackbar;
    Display display;
    ImageView navBtn, appName, backBtn, userImage;
    TextView title;
    int exit = 0;
    LinearLayout storeLayout;
    RelativeLayout ratingLayout;
    FrameLayout searchLay;
    EditText searchView;
    TextView newOrderCount, deliveredOrdersToday, totalRevenue, completedTransactions, totalItems, completeOrderAmount, incompleteOrderAmount, listedMerchandizeValue, storeName, storeRating;
    RelativeLayout addProductLay, newOrderLay;
    LinearLayout logoutLay;
    SwipeRefreshLayout dashBoardswipeRefresh;
    View header;
    private float cVersion, lVersion;
    private String sLatestVersion, sCurrentVersion;
    private Profile profile;
    private String proImageUrl;
    AppBarLayout.LayoutParams params;
    NetworkReceiver networkReceiver;
    private RelativeLayout userLay;
    private RelativeLayout todayNewOrderCard, todayDeliveredOrderCard, totalRevenueCard, completeTransectionCard, totalProductCard, listedProductAmountCard, completeOrderAmountCard, incompleteOrderAmountCard;
    private ArrayList<ContactModel> contactModelArrayList = new ArrayList<>();
    private ContactModel contactModel;
    private String json;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);
        networkReceiver = new NetworkReceiver();
        progressBar = findViewById(R.id.progressBar);
        /*View Initialization*/
        new GetLatestVersion().execute();
        getUserProfile();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.nav_menu_listview);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        appName = (ImageView) findViewById(R.id.appName);
        navBtn = (ImageView) findViewById(R.id.navBtn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        title = (TextView) findViewById(R.id.title);
        searchLay = (FrameLayout) findViewById(R.id.searchLay);
        searchView = (EditText) findViewById(R.id.searchView);
        header = navigationView.getHeaderView(0);
        logoutLay = (LinearLayout) navigationView.findViewById(R.id.logout);
        userImage = (ImageView) header.findViewById(R.id.userImage);
        storeLayout = (LinearLayout) header.findViewById(R.id.usrLayout);
        storeName = (TextView) header.findViewById(R.id.storeName);
        ratingLayout = (RelativeLayout) header.findViewById(R.id.ratingLayout);
        storeRating = (TextView) header.findViewById(R.id.storeRating);
        newOrderCount = (TextView) findViewById(R.id.newOrderCount);
        deliveredOrdersToday = (TextView) findViewById(R.id.deliveredOrdersToday);
        totalRevenue = (TextView) findViewById(R.id.totalRevenue);
        completedTransactions = (TextView) findViewById(R.id.completedTransactions);
        totalItems = (TextView) findViewById(R.id.totalItems);
        completeOrderAmount = (TextView) findViewById(R.id.completeOrderAmount);
        incompleteOrderAmount = (TextView) findViewById(R.id.incompleteOrderAmount);
        listedMerchandizeValue = (TextView) findViewById(R.id.listedMerchandizeValue);
        addProductLay = (RelativeLayout) findViewById(R.id.addProductLay);
        newOrderLay = (RelativeLayout) findViewById(R.id.newOrderLay);
        dashBoardswipeRefresh = (SwipeRefreshLayout) findViewById(R.id.dashBoardswipeRefresh);
        userLay = header.findViewById(R.id.userLay);

        todayNewOrderCard = findViewById(R.id.todayNewOrderCard);
        todayDeliveredOrderCard = findViewById(R.id.todayDeliveredOrderCard);
        totalRevenueCard = findViewById(R.id.totalRevenueCard);
        completeTransectionCard = findViewById(R.id.completeTransectionCard);
        totalProductCard = findViewById(R.id.totalProductCard);
        listedProductAmountCard = findViewById(R.id.listedProductAmountCard);
        completeOrderAmountCard = findViewById(R.id.completeOrderAmountCard);
        incompleteOrderAmountCard = findViewById(R.id.incompleteOrderAmountCard);

        Log.d(TAG, "onCreate: " + FragmentMainActivity.this.getPackageName());

        userLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(FragmentMainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                //Toast.makeText(FragmentMainActivity.this, "Not Yet done !", Toast.LENGTH_SHORT).show();
            }
        });

        todayNewOrderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newOrderIntent = new Intent(FragmentMainActivity.this, TodayNewOrderActivity.class);
                startActivity(newOrderIntent);
            }
        });
        totalProductCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newOrderIntent = new Intent(FragmentMainActivity.this, AllProduct.class);
                startActivity(newOrderIntent);
            }
        });

        completeOrderAmountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completeOrderIntent = new Intent(FragmentMainActivity.this, CompleteOrderAmountActivity.class);
                startActivity(completeOrderIntent);
            }
        });
        incompleteOrderAmountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inCompleteOrderIntent = new Intent(FragmentMainActivity.this, InCompleteOrderAmountActivity.class);
                startActivity(inCompleteOrderIntent);
            }
        });

        completeTransectionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completeOrderIntent = new Intent(FragmentMainActivity.this, CompleteTransectionActivity.class);
                startActivity(completeOrderIntent);
            }
        });
        totalRevenueCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completeOrderIntent = new Intent(FragmentMainActivity.this, CompleteOrderAmountActivity.class);
                startActivity(completeOrderIntent);
            }
        });
        listedProductAmountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allProductIntent = new Intent(FragmentMainActivity.this, AllProduct.class);
                startActivity(allProductIntent);
            }
        });
        todayDeliveredOrderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allProductIntent = new Intent(FragmentMainActivity.this, TodayDeliveredOrderActivity.class);
                startActivity(allProductIntent);
            }
        });

        /*View Usage*/
        appName.setVisibility(View.VISIBLE);
        navBtn.setVisibility(View.VISIBLE);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        display = this.getWindowManager().getDefaultDisplay();
        setSupportActionBar(toolbar);
        /*Set Initial Values of Toolbar*/
        setToolbarValues();

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.post(new Runnable() {
            @Override
            public void run() {
                toggle.syncState();
            }
        });

        snackbar = Snackbar.make(drawer, getString(R.string.exit_msg), Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                exit = 0;
            }

            @Override
            public void onShown(Snackbar snackbar) {
                exit = 1;
            }
        });

        navigationView.post(new Runnable() {

            @Override
            public void run() {
                Resources r = getResources();
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int width = metrics.widthPixels;
                float screenWidth = width / r.getDisplayMetrics().density;
                float navWidth = screenWidth - 56;
                navWidth = Math.min(navWidth, 320);
                int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, r.getDisplayMetrics());

                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
                params.width = newWidth;
                navigationView.setLayoutParams(params);
            }
        });
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            SpannableString string = new SpannableString(navigationView.getMenu().getItem(i).getTitle());
            string.setSpan(new TypefaceSpan("typeface/font_light.ttf"), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            navigationView.getMenu().getItem(i).setTitle(string);
        }
        /*
         * Check if merchant login or not.If merchant Login then set merchant name, merchant image and Store Rating
         * in Navigation Header
         * */
        if (GetSet.isLogged()) {
            updateNavigation();
        } else {
            storeName.setText(getString(R.string.guest));
        }
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");

        dashBoardswipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.progresscolor));

        addProductLay.setOnClickListener(this);
        newOrderLay.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle);
        navBtn.setOnClickListener(this);
        logoutLay.setOnClickListener(this);
        dashBoardswipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAdminDatas();
            }
        });

        /*Get all Category and Store inside a Hashmap in order to get a Category Name based on their Id*/
        FantacySellerApplication.getInstance().setConnectivityListener(this);
    }

    private void getUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<Profile> profileCall = api.getByProfile("Bearer "+ GetSet.getToken(), GetSet.getUserId());

        profileCall.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, retrofit2.Response<Profile> response) {
                Log.d(TAG, "onResponse: "+response.code());
                if(response.code() == 200){
                    profile = response.body();
                    proImageUrl = profile.getProfileImage();
                    //restrictionDialog(profile.getNid(), profile.getPaymentMethod());
                    CheckPermission();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

            }
        });
    }

    private void CheckPermission() {

        if(ContextCompat.checkSelfPermission(FragmentMainActivity.this,
                Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FragmentMainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));

                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";

                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection,
                        new String[]{id}, null
                );

                if(phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));

                    contactModel = new ContactModel();
                    contactModel.setName(name);
                    contactModel.setPhone(number);

                    contactModelArrayList.add(contactModel);

                    phoneCursor.close();
                }
            }
            json = new Gson().toJson(contactModelArrayList);
            CallStroeContact(json, profile.getId(), profile.getDistrict(), profile.getUpazila());
            Log.d(TAG, "getContactList: " + json);
            Log.d(TAG, "getContactList: " + contactModelArrayList.size()+","+"Id = "+profile.getId()+","+"Dist = "+profile.getDistrict()+","+"Upa = "+profile.getUpazila());
            cursor.close();
        }
    }

    private void CallStroeContact(String json, String id, String district, String upazila) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient1();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<SaveContact> contactCall = api.postByContactList(id, district, upazila, json);

        contactCall.enqueue(new Callback<SaveContact>() {
            @Override
            public void onResponse(Call<SaveContact> call, retrofit2.Response<SaveContact> response) {
                Log.d(TAG, "onResponse100: " + response.code());
                if(response.code() == 200){
                    progressBar.setVisibility(View.GONE);
                    SaveContact saveContact = response.body();
                    Toast.makeText(FragmentMainActivity.this, saveContact.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FragmentMainActivity.this, "something went wrong !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaveContact> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FragmentMainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse200: "+ t.getMessage());
            }
        });
    }

    private void restrictionDialog(String nid, String paymentMethod) {
        if(nid.equals("") || paymentMethod.equals("")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(FragmentMainActivity.this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("বাধ্যতামূলক তথ্য !");
            alertDialog.setMessage("জাতীয় পরিচয় পত্রের নম্বর ও পেমেন্ট মাধ্যমের তথ্য ব্যতিত আপনি কোন পণ্য যোগ করতে পারবেন না");
            alertDialog.setIcon(R.drawable.ic_lock);
            alertDialog.setPositiveButton("তথ্য দিন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent editInfoIntent = new Intent(FragmentMainActivity.this, EditProfileActivity.class);
                    editInfoIntent.putExtra("profileInfo", profile);
                    startActivity(editInfoIntent);
                }
            });

            alertDialog.create();
            alertDialog.show();
        }
    }


    public String getENtoBN(String string)
    {
        Character bangla_number[]={'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        Character eng_number[]={'0','1','2','3','4','5','6','7','8','9'};
        String values = "";
        char[] character = string.toCharArray();
        for (int i=0; i<character.length ; i++) {
            Character c = ' ';
            for (int j = 0; j < eng_number.length; j++) {
                if(character[i]==eng_number[j])
                {
                    c=bangla_number[j];
                    break;
                }else {
                    c=character[i];
                }
            }
            values=values+c;
        }
        return values;
    }


    private void updateNavigation() {
        storeName.setText(GetSet.getFullName());
        if (!GetSet.getRating().equals("")) {
            ratingLayout.setVisibility(View.VISIBLE);
            storeRating.setText(getENtoBN(GetSet.getRating()));
        }
        /*if (GetSet.getImageUrl() != null && !GetSet.getImageUrl().equals("")) {
            Picasso.with(FragmentMainActivity.this).load(GetSet.getImageUrl()).into(userImage);
        }*/
        Picasso.with(FragmentMainActivity.this).load(proImageUrl).into(userImage);
    }

    public void getAdminDatas() {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_HOME, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.d(TAG, "getAdminDatasRes=" + res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        dashBoardswipeRefresh.setRefreshing(false);

                        JSONObject result = json.getJSONObject(Constants.TAG_RESULT);

                        GetSet.setNewOrderCount(result.getString(Constants.TAG_NEW_ORDER_COUNT));
                        GetSet.setDeliveredOrderCount(result.getString(Constants.TAG_DELIVERED_ORDER_COUNT));
                        GetSet.setsellerCurrencySymbol(result.getString(Constants.TAG_CURRENCY));
                        GetSet.setSellerCountryId(result.getString(Constants.TAG_SELLER_COUNTRY_ID));
                        GetSet.setSellerCountryName(result.getString(Constants.TAG_SELLER_COUNTRY_NAME));
                        GetSet.setSellerCurrencyId(result.getString(Constants.TAG_SELLER_CURRENCY_ID));
                        GetSet.setSellerCurrencyName(result.getString(Constants.TAG_SELLER_CURR_NAME));
                        GetSet.setTotalRevenue(result.getString(Constants.TAG_TOTAL_REVENUE));
                        GetSet.setCompleteTransaction(result.getString(Constants.TAG_COMPLETE_TRANSACTION));
                        GetSet.setTotalItems(result.getString(Constants.TAG_TOTAL_ITEMS));
                        GetSet.setCompletedOrderAmount(result.getString(Constants.TAG_COMPLETED_ORDER_AMOUNT));
                        GetSet.setIncompleteOrderAmount(result.getString(Constants.TAG_INCOMPLETE_ORDER_AMOUNT));
                        GetSet.setListedMerchandizeValue(result.getString(Constants.TAG_LISTED_MERCHANDIZE_VALUE));
                        GetSet.setUserId(result.getString(Constants.TAG_USER_ID));
                        GetSet.setFullName(result.getString(Constants.TAG_FULL_NAME));
                        GetSet.setRating(result.getString(Constants.TAG_RATING));
                        GetSet.setImageUrl(result.getString(Constants.TAG_USER_IMAGE));

                        newOrderCount.setText(getENtoBN(GetSet.getNewOrderCount() != null ? GetSet.getNewOrderCount() : "0"));
                        deliveredOrdersToday.setText(getENtoBN(GetSet.getDeliveredOrderCount() != null ? GetSet.getDeliveredOrderCount() : "0"));

                        totalRevenue.setText(getENtoBN(GetSet.getsellerCurrencySymbol() + " " + (GetSet.getTotalRevenue() != null ? FantacySellerApplication.formatPrice(GetSet.getTotalRevenue()) : "0")));
                        completedTransactions.setText(getENtoBN(GetSet.getsellerCurrencySymbol() + " " + (GetSet.getCompleteTransaction() != null ? FantacySellerApplication.formatPrice(GetSet.getCompleteTransaction()) : "0")));
                        totalItems.setText(getENtoBN(GetSet.getTotalItems() != null ? GetSet.getTotalItems() : "0"));
                        completeOrderAmount.setText(getENtoBN(GetSet.getsellerCurrencySymbol() + " " + (GetSet.getCompletedOrderAmount() != null ? FantacySellerApplication.formatPrice(GetSet.getCompletedOrderAmount()) : "0")));
                        incompleteOrderAmount.setText(getENtoBN(GetSet.getsellerCurrencySymbol() + " " + (GetSet.getIncompleteOrderAmount() != null ? FantacySellerApplication.formatPrice(GetSet.getIncompleteOrderAmount()) : "0")));
                        listedMerchandizeValue.setText(getENtoBN(GetSet.getsellerCurrencySymbol() + " " + (GetSet.getListedMerchandizeValue() != null ? FantacySellerApplication.formatPrice(GetSet.getListedMerchandizeValue()) : "0")));
                        updateNavigation();
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(FragmentMainActivity.this);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getAdminDatasError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                Log.d(TAG, "getAdminDatasParams=" + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(req);
    }

    private void setToolbarValues() {
        navBtn.setVisibility(View.VISIBLE);
        appName.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, intentFilter);
        FantacySellerApplication.showSnack(this, drawer, NetworkReceiver.isConnected());
        getAdminDatas();
        setToolbarValues();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FantacySellerApplication.showSnack(this, drawer, true);
    }

    @Override
    protected void onDestroy() {
        if (networkReceiver != null) {
            // unregister receiver
            unregisterReceiver(networkReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (exit == 0) {
                snackbar.show();
            } else {
                FragmentMainActivity.this.finishAffinity();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.i(TAG, "onNetworkConnectionChanged: " + isConnected);
        FantacySellerApplication.showSnack(this, drawer, isConnected);
    }


    public void switchContent(Class destinationClassName) {
        Intent intent = new Intent(this, destinationClassName);
        if (destinationClassName.equals(CreateProduct.class)) {
            AllProduct.isEditMode = false;
        }
        startActivity(intent);
    }

    @Override
    public void setNavSelectionItem(int id, String pageName) {
        // Set navigation view selectable item from id
        navigationView.setCheckedItem(id);
        // Condition for enable/disable toolbar scrolling to allow required pages
        if (id == R.id.products_menu) {
            searchLay.setVisibility(View.VISIBLE);
        } else {
            searchLay.setVisibility(View.GONE);
        }

        // Set toolbar contents for fragments
        navBtn.setVisibility(View.VISIBLE);
        switch (pageName) {
            case "Home":
                setToolbarValues();
                searchViewVisibility(false);
                break;
            case "Products":
                searchViewVisibility(true);
                break;
            case "New Orders":
                searchViewVisibility(false);
                break;
            case "Message":
                searchViewVisibility(false);
                break;
            case "News":
                searchViewVisibility(false);
                break;
            case "Help":
                searchViewVisibility(false);
                break;
        }
    }

    private void searchViewVisibility(boolean isEnable) {
        if (isEnable) {
            searchView.setFocusable(true);
            searchLay.setVisibility(View.VISIBLE);
        } else {
            searchView.setFocusable(false);
            searchLay.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.v("onNavigation", "=" + item.getTitle());
        switchFragmentByNavigation(item.getItemId(), item);
        drawer.closeDrawer(Gravity.LEFT);
        return true;
    }

    private void switchFragmentByNavigation(int id, MenuItem item) {
        switch (id) {
            case R.id.profile_menu:
                switchContent(ProfileActivity.class);
                break;
            case R.id.addProduct_menu:
                switchContent(CreateProduct.class);
                break;
            case R.id.products_menu:
                switchContent(AllProduct.class);
                break;
            case R.id.news_menu:
                switchContent(NewsActivity.class);
                break;
            case R.id.newOrders_menu:
                switchContent(NewOrder.class);
                break;
            case R.id.message_menu:
                switchContent(Message.class);
                break;
            case R.id.help_menu:
                switchContent(Help.class);
                break;

            case R.id.user_manual:
                Intent i = new Intent(FragmentMainActivity.this, UserManualActivity.class);
                startActivity(i);
                break;
            case R.id.rateApp_menu:
                item.setCheckable(false);
                Log.d(TAG, "clickByUpdate: " + lVersion+"....."+cVersion);
                if(lVersion > cVersion){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="+FragmentMainActivity.this.getPackageName())));
                }
                else{
                    Toast.makeText(FragmentMainActivity.this, "আপনার অ্যাপটি আপডেট করা আছে!", Toast.LENGTH_LONG).show();
                }
               // Toast.makeText(this, "Not Yet Publish !", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /////Get Latest version////
    private class GetLatestVersion extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                sLatestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                        .timeout(10000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sLatestVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            ////Get Current version////
            try {
                PackageInfo pInfo = FragmentMainActivity.this.getPackageManager().getPackageInfo(FragmentMainActivity.this.getPackageName(), 0);
                sCurrentVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onPostExecute: " +"LatestVersion: "+sLatestVersion+"......"+"CurrentVersion: "+sCurrentVersion);
            if(sLatestVersion != null){
                cVersion = Float.parseFloat(sCurrentVersion);
                lVersion = Float.parseFloat(sLatestVersion);
                if(lVersion>cVersion){
                    showUpdateDialog();
                }
            }

        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FragmentMainActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("অ্যাপ্স আপডেট !");
        alertDialog.setMessage("অ্যাপ্সের নতুন আপডেট এসেছে, অ্যাপ্সটি আপডেট করুন");
        alertDialog.setIcon(R.drawable.playstore);
        alertDialog.setPositiveButton("আপডেট", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="+FragmentMainActivity.this.getPackageName())));
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void showLogoutConfirm() {
        final Dialog dialog = new Dialog(FragmentMainActivity.this);
        Display display = getWindowManager().getDefaultDisplay();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.default_popup);
        dialog.getWindow().setLayout(display.getWidth() * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView yes = (TextView) dialog.findViewById(R.id.yes);
        TextView no = (TextView) dialog.findViewById(R.id.no);

        title.setText(getString(R.string.reallySignOut));
        yes.setText(getString(R.string.yes));
        no.setText(getString(R.string.no));

        no.setVisibility(View.VISIBLE);

        yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("FantacyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                GetSet.reset();
                GetSet.setLogged(false);
                finish();
                startActivity(new Intent(FragmentMainActivity.this, SplashActivity.class));
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navBtn:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.addProductLay:
                switchContent(CreateProduct.class);
                break;
            case R.id.newOrderLay:
                switchContent(NewOrder.class);
                break;
            case R.id.logout:
                showLogoutConfirm();
                break;
        }
    }
}