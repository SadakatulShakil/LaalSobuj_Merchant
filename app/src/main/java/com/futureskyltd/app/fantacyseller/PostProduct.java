package com.futureskyltd.app.fantacyseller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.utils.Adapter.DistrictAdapter;
import com.futureskyltd.app.utils.Adapter.UpazilaAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.District.District;
import com.futureskyltd.app.utils.District.DistrictList;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.Upazila.Upazila;
import com.futureskyltd.app.utils.Upazila.UpazilatList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class PostProduct extends AppCompatActivity implements View.OnClickListener {

    final String TAG = this.getClass().getSimpleName();
    final int ACTION_COLOR = 100, ACTION_SHIPSTO = 200, ACTION_POST_PRODUCT = 600, ACTION_SIZE = 300;
    ImageView backBtn, appName;
    TextView addBtn, resetBtn, processBtn, shippingTime, colorSelect, screenTitle;
    SwitchCompat sizeSwitch, codSwitch, dailyDealsSwitch, fbdiscountSwitch;
    LinearLayout sizeOptionLay, dailyDealsOptionLay, fbOptionLay;
    RelativeLayout shippingTimeLayout, shipsToLay, colorSelectLay;
    private ImageView addShippingMark;
    EditText fbdiscountPercentage, dailyDealLayDiscPercent, sizeOptQuantity, sizeOptPrice, dailyDealDate, minOrderQuantity;
    Toolbar toolbar;
    String sdate, item_id = "";
    long shippingTimeStamp = 1616241081;
    int previousPosition;
    String item_image = "", color = "", shipsTo = "", sizes = "", colorNames, colorMethod = "auto", min_quantity;
    boolean iseElseAmount, isShipsToAmount;
    RelativeLayout sizeCompLay;
    public static Activity activity;
    HashMap<String, String> productDatas = new HashMap<>();
    ArrayList<HashMap<String, Object>> imageNameList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> selectedColorLists = new ArrayList<>();
    ArrayList<HashMap<String, Object>> sizeList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> shipsToList = new ArrayList<>();
    private Spinner districtSpinner, upazilaSpinner;
    private String districtName, upazilaName;
    private int district_id, upazila_id;
    private ArrayList<District> mDistrictList = new ArrayList<>();
    private ArrayList<Upazila> mUpazilaList = new ArrayList<>();
    private DistrictAdapter mDistrictAdapter;
    private UpazilaAdapter mUpazilaAdapter;
    private LinearLayout discountOptionLay;
    private RadioButton dailyDealRb, regularDealRb;
    private String pro_discount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_final);

        activity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        addBtn = (TextView) findViewById(R.id.addProduct);
        resetBtn = (TextView) findViewById(R.id.resetProduct);
        appName = (ImageView) findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);
        processBtn = (TextView) findViewById(R.id.processBtn);
        sizeSwitch = (SwitchCompat) findViewById(R.id.sizeAvailableSwitch);
        codSwitch = (SwitchCompat) findViewById(R.id.codSwitch);
        dailyDealsSwitch = (SwitchCompat) findViewById(R.id.dailyDealsSwitch);
        fbdiscountSwitch = (SwitchCompat) findViewById(R.id.fbanddiscountSwitch);
        sizeOptionLay = (LinearLayout) findViewById(R.id.sizeOptionLay);
        dailyDealsOptionLay = (LinearLayout) findViewById(R.id.dailyDealsOptionLay);
        fbOptionLay = (LinearLayout) findViewById(R.id.fbOptionLay);
        shippingTimeLayout = (RelativeLayout) findViewById(R.id.shippingTimeLayout);
        shipsToLay = (RelativeLayout) findViewById(R.id.shipsToLay);
        sizeCompLay = (RelativeLayout) findViewById(R.id.sizeCompLay);
        fbdiscountPercentage = (EditText) findViewById(R.id.fbdiscountPercentage);
        colorSelectLay = (RelativeLayout) findViewById(R.id.colorSelectLay);
        dailyDealLayDiscPercent = (EditText) findViewById(R.id.dailyDealLayDiscPercent);
        dailyDealDate = (EditText) findViewById(R.id.dailyDealDate);
        sizeOptPrice = (EditText) findViewById(R.id.sizeOptPrice);
        sizeOptQuantity = (EditText) findViewById(R.id.sizeOptQuantity);
        minOrderQuantity = (EditText) findViewById(R.id.minOrderQuantity);
        shippingTime = (TextView) findViewById(R.id.shippingTime);
        colorSelect = (TextView) findViewById(R.id.colorSelect);
        districtSpinner = findViewById(R.id.userDistrictSpinner);
        upazilaSpinner = findViewById(R.id.userUpazilaSpinner);
        discountOptionLay = findViewById(R.id.discountOption);
        dailyDealRb = findViewById(R.id.dailyDeal);
        regularDealRb = findViewById(R.id.regularDeal);
        addShippingMark = findViewById(R.id.addShippingMark);

        getDistrictList();
       // min_quantity = minOrderQuantity.getText().toString().trim();
        if (!AllProduct.isEditMode) {
            screenTitle.setText(getString(R.string.add_product));
            addBtn.setText(getString(R.string.add_btn_lbl));
        } else {
            screenTitle.setText(getString(R.string.edit_product));
            addBtn.setText(getString(R.string.save_btn_lbl));
        }
        resetBtn.setText(getString(R.string.back_lbl));
        processBtn.setText(getString(R.string.step2_lbl));
        sizeOptPrice.setFilters(new InputFilter[]{new FantacySellerApplication.DecimalDigitsInputFilter(6, 2)});

        productDatas = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INIT_PRODUCT_LIST);
        imageNameList = getImgNameFromList((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.PROD_IMG_LIST));
        if ((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.COLOR_LIST) != null)
            selectedColorLists = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.COLOR_LIST);
        if ((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SIZE_LIST) != null)
            sizeList = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SIZE_LIST);
        if ((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SHIPSTO_LIST) != null)
            shipsToList = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SHIPSTO_LIST);
        Log.d(TAG, "productDatas=" + productDatas);
        Log.d(TAG, "selectedColorLists" + selectedColorLists);
        /*Get Data From Intent*/
        getDataFromIntent(getIntent());
        if (imageNameList != null) {
            item_image = listToJSONObject(imageNameList, Constants.TAG_ALL_ITEM_IMAGES).toString();
        }

        processBtn.setVisibility(View.VISIBLE);
        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        sizeOptionLay.setVisibility(View.VISIBLE);
        dailyDealsOptionLay.setVisibility(View.GONE);
        fbOptionLay.setVisibility(View.GONE);

        addBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        colorSelectLay.setOnClickListener(this);
        shippingTimeLayout.setOnClickListener(this);
        dailyDealDate.setOnClickListener(this);
        shipsToLay.setOnClickListener(this);
    }

    private void getDistrictList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<DistrictList> districtCall = api.getByDistrict();

        districtCall.enqueue(new Callback<DistrictList>() {
            @Override
            public void onResponse(Call<DistrictList> call, retrofit2.Response<DistrictList> response) {
                Log.d(TAG, "onResponse: " + response.code());
                DistrictList districtList = response.body();
                mDistrictList = (ArrayList<District>) districtList.getDistricts();
                Log.d(TAG, "onResponse: " + districtList.toString());
                Log.d(TAG, "onResponse: "+mDistrictList.size());
                mDistrictAdapter = new DistrictAdapter(PostProduct.this, mDistrictList);
                districtSpinner.setAdapter(mDistrictAdapter);

                districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        District clickedDistrict = (District) parent.getItemAtPosition(position);

                        districtName = clickedDistrict.getDistrict();
                        district_id = clickedDistrict.getId();

                        getUpazilaList(district_id);

                        //Toast.makeText(PostProduct.this, districtName +" is selected !"+" id: "+ district_id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<DistrictList> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

    private void getUpazilaList(int district_id) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<UpazilatList> upazilaCall = api.postByUpazila(district_id);

        upazilaCall.enqueue(new Callback<UpazilatList>() {
            @Override
            public void onResponse(Call<UpazilatList> call, retrofit2.Response<UpazilatList> response) {
                Log.d(TAG, "onResponse: " + response.code());
                UpazilatList upazilatList = response.body();
                mUpazilaList = (ArrayList<Upazila>) upazilatList.getUpazila();
                Log.d(TAG, "onResponse: " + upazilatList.toString());
                mUpazilaAdapter = new UpazilaAdapter(PostProduct.this, mUpazilaList);
                upazilaSpinner.setAdapter(mUpazilaAdapter);

                upazilaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Upazila clickedUpazila = (Upazila) parent.getItemAtPosition(position);

                        upazilaName = clickedUpazila.getUpazila();
                        upazila_id = clickedUpazila.getId();
                        //Toast.makeText(PostProduct.this, upazilaName +" is selected !"+" id: "+ upazila_id, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<UpazilatList> call, Throwable t) {
                Log.d(TAG, "onFailure: " +t.getMessage());
            }
        });
    }

    private ArrayList<HashMap<String, Object>> getImgNameFromList(ArrayList<HashMap<String, Object>> prodImgsList) {
        for (int i = 0; i < prodImgsList.size(); i++) {
            HashMap<String, Object> hMap = new HashMap<>();
            hMap.put(Constants.TAG_IMAGE, prodImgsList.get(i).get(Constants.TAG_IMAGE_NAME));
            imageNameList.add(hMap);
        }
        return imageNameList;
    }

    private void getDataFromIntent(Intent intent) {
        item_id = getIntent().getStringExtra("item_id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Get Data From Intent*/
        getDataFromIntent(getIntent());
        setUpUI();
        previousPosition = getIntent().getIntExtra("selectedPosition", 0);
        getDataFromIntent(getIntent());
    }

    private void setUpUI() {
        if (productDatas.get(Constants.TAG_DEAL_DATE) != null && !productDatas.get(Constants.TAG_DEAL_DATE).equals("") && (!productDatas.get(Constants.TAG_DEAL_DATE).equals("0"))) {
            shippingTimeStamp = Long.parseLong(productDatas.get(Constants.TAG_DEAL_DATE));
            dailyDealDate.setText(FantacySellerApplication.getDate(productDatas.get(Constants.TAG_DEAL_DATE)));
        }
        fbdiscountPercentage.setText(productDatas.get(Constants.TAG_FB_DISC_PERCENTAGE));
        dailyDealLayDiscPercent.setText(productDatas.get(Constants.TAG_DISCOUNT_PERCENTAGE));
        sizeOptPrice.setText(productDatas.get(Constants.TAG_MAIN_PRICE));
        sizeOptQuantity.setText(productDatas.get(Constants.TAG_QUANTITY));
        controlSwitch(codSwitch, Boolean.valueOf(productDatas.get(Constants.TAG_COD)), "cod");
        controlSwitch(fbdiscountSwitch, Boolean.valueOf(productDatas.get(Constants.TAG_FB_DISC_ENABLE)), "fb");
        controlSwitch(sizeSwitch, Boolean.valueOf(productDatas.get(Constants.TAG_SIZE_AVAILABILTY)), "size");
        controlSwitch(dailyDealsSwitch, Boolean.valueOf(productDatas.get(Constants.TAG_DEAL_ENABLED)), "deals");
        minOrderQuantity.setText(productDatas.get(Constants.TAG_MIN_QUANTITY));
        sizeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    productDatas.put(Constants.TAG_SIZE_AVAILABILTY, "true");
                    sizeOptionLay.setVisibility(View.GONE);
                    productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
                    productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
                    productDatas.put(Constants.TAG_MIN_QUANTITY, minOrderQuantity.getText().toString());
                    productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
                    if (shippingTimeStamp != 0)
                        productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                    productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
                    Intent intent = new Intent(PostProduct.this, AddSizeProperty.class);
                    startActivityForResult(intent, ACTION_SIZE);
                } else {
                    productDatas.put(Constants.TAG_SIZE_AVAILABILTY, "false");
                    sizeOptionLay.setVisibility(View.VISIBLE);
                    productDatas.put(Constants.TAG_MIN_QUANTITY, minOrderQuantity.getText().toString());
                }
            }
        });
        if (sizeSwitch.isChecked()) {
            sizeOptionLay.setVisibility(View.GONE);
            if (!AllProduct.isEditMode) {
                sizeOptPrice.setText("");
                sizeOptQuantity.setText("");
            }
        } else {
            sizeOptionLay.setVisibility(View.VISIBLE);
        }
        if (dailyDealsSwitch.isChecked()) {
            discountOptionLay.setVisibility(View.VISIBLE);
            dailyDealsOptionLay.setVisibility(View.VISIBLE);
            if(dailyDealRb.isChecked()){
                if (productDatas.get(Constants.TAG_DEAL_DATE) != "" || productDatas.get(Constants.TAG_DEAL_DATE) != "0")
                    dailyDealDate.setText(FantacySellerApplication.getDate(productDatas.get(Constants.TAG_DEAL_DATE)));
            }else{
                dailyDealDate.setVisibility(View.GONE);
                dailyDealDate.setText("");
            }
        } else {
            dailyDealLayDiscPercent.setText("");
            dailyDealDate.setText("");
            dailyDealsOptionLay.setVisibility(View.GONE);
            discountOptionLay.setVisibility(View.GONE);
        }
        if (fbdiscountSwitch.isChecked()) {
            fbOptionLay.setVisibility(View.VISIBLE);
        } else {
            fbdiscountPercentage.setText("");
            fbOptionLay.setVisibility(View.GONE);
        }
        productDatas.put(Constants.TAG_MIN_QUANTITY, minOrderQuantity.getText().toString());
        dailyDealsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    productDatas.put(Constants.TAG_DEAL_ENABLED, "true");
                    dailyDealsOptionLay.setVisibility(View.VISIBLE);
                    discountOptionLay.setVisibility(View.VISIBLE);
                    dailyDealRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                regularDealRb.setChecked(false);
                                pro_discount = "dailydeal";
                                dailyDealDate.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    regularDealRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                dailyDealRb.setChecked(false);
                                pro_discount = "regulardeal";
                                dailyDealDate.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    productDatas.put(Constants.TAG_DEAL_ENABLED, "false");
                    dailyDealsOptionLay.setVisibility(View.GONE);
                    discountOptionLay.setVisibility(View.GONE);
                }
            }
        });
        fbdiscountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    productDatas.put(Constants.TAG_FB_DISC_ENABLE, "true");
                    fbOptionLay.setVisibility(View.VISIBLE);
                } else {
                    productDatas.put(Constants.TAG_FB_DISC_ENABLE, "false");
                    fbOptionLay.setVisibility(View.GONE);
                }
            }
        });
        codSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    productDatas.put(Constants.TAG_COD, "true");
                } else {
                    productDatas.put(Constants.TAG_COD, "false");
                }
            }
        });

        if (productDatas.get(Constants.TAG_SHIPPING_TIME) != ""){
            shippingTime.setText(productDatas.get(Constants.TAG_SHIPPING_TIME));
            //addShippingMark.setVisibility(View.GONE);
        }else{
            //addShippingMark.setVisibility(View.VISIBLE);
        }


        if (!selectedColorLists.isEmpty()) {
            color = listToJSONObject(selectedColorLists, Constants.TAG_COLORS).toString();
        }
        if (selectedColorLists.size() != 0)
            colorNames = selectedColorLists.size() + " " + getString(R.string.color_selected);
        else
            colorNames = getString(R.string.auto);
        Log.v(TAG, "selectedColorLists=" + selectedColorLists.size());
        Log.v(TAG, "colorNames=" + colorNames);
        colorSelect.setText(colorNames + " ");

        if (shipsToList != null) {
            shipsTo = listToJSONObject(shipsToList, Constants.TAG_SHIPPING_TO).toString();
            shipsTo = checkListisEmpty(shipsTo);
        }

        if ((sizeList != null) && (!sizeList.isEmpty())) {
            sizes = listToJSONObject(sizeList, Constants.TAG_SIZES).toString();
            sizes = checkListisEmpty(sizes);
        }
        if (Boolean.valueOf(productDatas.get(Constants.TAG_SIZE_AVAILABILTY))) {
            sizeCompLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productDatas.put(Constants.TAG_MAIN_PRICE, sizeOptPrice.getText().toString());
                    productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
                    productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
                    productDatas.put(Constants.TAG_MIN_QUANTITY, minOrderQuantity.getText().toString());
                    productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
                    if (shippingTimeStamp != 0)
                        productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                    productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
                    Intent intent = new Intent(PostProduct.this, AddSizeProperty.class);
                    intent.putExtra(Constants.SIZE_LIST, sizeList);
                    startActivityForResult(intent, ACTION_SIZE);
                }
            });
        }
    }

    private String checkListisEmpty(String jsonString) {
        String resulted = "";
        if (jsonString.equals("{\"shipping_to\":[]}")) {
            return resulted;
        }
        if (jsonString.equals("{\"sizes\":[]}")) {
            return resulted;
        } else
            return jsonString;

    }

    private void controlSwitch(SwitchCompat Switch, boolean isEnable, String switchName) {
        if (isEnable) {
            if (switchName.equals("cod"))
                productDatas.put(Constants.TAG_COD, "true");
            else if (switchName.equals("fb"))
                productDatas.put(Constants.TAG_FB_DISC_ENABLE, "true");
            else if (switchName.equals("size"))
                productDatas.put(Constants.TAG_SIZE_AVAILABILTY, "true");
            else
                productDatas.put(Constants.TAG_DEAL_ENABLED, "true");

            Switch.setChecked(true);
        } else {
            if (switchName.equals("cod"))
                productDatas.put(Constants.TAG_COD, "false");
            else if (switchName.equals("fb"))
                productDatas.put(Constants.TAG_FB_DISC_ENABLE, "false");
            else if (switchName.equals("size"))
                productDatas.put(Constants.TAG_SIZE_AVAILABILTY, "false");
            else
                productDatas.put(Constants.TAG_DEAL_ENABLED, "false");

            Switch.setChecked(false);
        }
    }

    private boolean checkSwitchEnable(String switchName, SwitchCompat allSwitch) {
        if (allSwitch.isChecked()) {
            if (switchName.equalsIgnoreCase("deal")) {
                productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
            } else if (switchName.equalsIgnoreCase("fb"))
                productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
            return true;
        } else {
            return false;
        }
    }

    private void showDateSelector() {
        final int mYear, mMonth, mDay;
        final Calendar mcurrentDate = Calendar.getInstance();
        if (shippingTimeStamp != 0) {
            mcurrentDate.setTimeInMillis(shippingTimeStamp * 1000L);
        }
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(PostProduct.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                Calendar calendar = new GregorianCalendar(selectedyear, selectedmonth, selectedday, mcurrentDate.getTime().getHours(), mcurrentDate.getTime().getMinutes());
                shippingTimeStamp = calendar.getTimeInMillis() / 1000L;
                Log.v(TAG, "timeStamp=" + shippingTimeStamp);
                sdate = FantacySellerApplication.getDate(shippingTimeStamp + "");
                dailyDealDate.setText(sdate);
                productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle(getString(R.string.select_product_date));
        mDatePicker.setCancelable(false);
        mDatePicker.show();
    }

    private JSONObject listToJSONObject(ArrayList<HashMap<String, Object>> list, String keyName) {
        JSONArray jsonArray = new JSONArray();
        JSONObject resultantJsonObj = new JSONObject();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> tmpMap = list.get(i);
            JSONObject jsonObject = new JSONObject(tmpMap);
            jsonArray.put(jsonObject);
        }
        try {
            resultantJsonObj.put(keyName, jsonArray);
            return resultantJsonObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addProduct() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_ADD_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.d(TAG, "addProductRes=" + res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        FantacySellerApplication.showStatusDialog(PostProduct.this, true, getString(R.string.product_success_post_msg), FragmentMainActivity.class);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("false")) {
                        if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("Sorry, Your product is not posted try after sometime"))
                            FantacySellerApplication.showToast(PostProduct.this, getString(R.string.prod_not_posted), Toast.LENGTH_LONG);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(PostProduct.this);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "addProductError: " + error.getMessage());
                FantacySellerApplication.showToast(PostProduct.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> hashMap = new HashMap<String, String>();
                hashMap.put(Constants.TAG_USER_ID, GetSet.getUserId());
                if (productDatas.get(Constants.TAG_ITEM_ID) != null && !productDatas.get(Constants.TAG_ITEM_ID).equals(""))
                    hashMap.put(Constants.TAG_ITEM_ID, productDatas.get(Constants.TAG_ITEM_ID));
                else
                    hashMap.put(Constants.TAG_ITEM_ID, "");

                hashMap.put(Constants.TAG_CATEGORY_ID, productDatas.get(Constants.TAG_CATEGORY_ID));
                if (productDatas.get(Constants.TAG_SUBCATEGORY_ID) != null)
                    hashMap.put(Constants.TAG_SUBCATEGORY_ID, productDatas.get(Constants.TAG_SUBCATEGORY_ID));
                else
                    hashMap.put(Constants.TAG_SUBCATEGORY_ID, "");
                if (productDatas.get(Constants.TAG_SUPER_CATEGORY_ID) != null)
                    hashMap.put(Constants.TAG_SUPER_CATEGORY_ID, productDatas.get(Constants.TAG_SUPER_CATEGORY_ID));
                else
                    hashMap.put(Constants.TAG_SUBCATEGORY_ID, "");

                hashMap.put(Constants.TAG_ITEM_IMAGE, item_image);
                hashMap.put(Constants.TAG_ITEM_TITLE, productDatas.get(Constants.TAG_ITEM_TITLE));
                hashMap.put(Constants.TAG_ITEM_DESCRIPTION, productDatas.get(Constants.TAG_ITEM_DESCRIPTION));
                hashMap.put(Constants.TAG_VIDEO_URL, productDatas.get(Constants.TAG_VIDEO_URL));
                hashMap.put(Constants.TAG_BARCODE, productDatas.get(Constants.TAG_BARCODE));

                hashMap.put(Constants.TAG_COLOR, color);
                if (productDatas.get(Constants.TAG_COLOR_METHOD) == null)
                    productDatas.put(Constants.TAG_COLOR_METHOD, colorMethod);
                hashMap.put(Constants.TAG_COLOR_METHOD, productDatas.get(Constants.TAG_COLOR_METHOD).toLowerCase());

                hashMap.put(Constants.TAG_COD, productDatas.get(Constants.TAG_COD) + "");
                hashMap.put(Constants.TAG_FB_DISC_ENABLE, productDatas.get(Constants.TAG_FB_DISC_ENABLE));
                hashMap.put(Constants.TAG_SIZE_AVAILABILTY, productDatas.get(Constants.TAG_SIZE_AVAILABILTY));
                hashMap.put(Constants.TAG_CURRENCY_ID, GetSet.getSellerCurrencyId());
                hashMap.put(Constants.TAG_FB_DISC_PERCENTAGE, productDatas.get(Constants.TAG_FB_DISC_PERCENTAGE));
                hashMap.put(Constants.TAG_SHIPPING_TO, shipsTo);
                if (productDatas.get(Constants.TAG_EVERYWHERE_ELSE) != null)
                    hashMap.put(Constants.TAG_EVERYWHERE_ELSE, productDatas.get(Constants.TAG_EVERYWHERE_ELSE));
                else
                    hashMap.put(Constants.TAG_EVERYWHERE_ELSE, "");
                hashMap.put(Constants.TAG_DEAL_ENABLED, productDatas.get(Constants.TAG_DEAL_ENABLED));
                hashMap.put(Constants.TAG_DISCOUNT_PERCENTAGE, productDatas.get(Constants.TAG_DISCOUNT_PERCENTAGE));
                hashMap.put(Constants.TAG_DEAL_DATE, productDatas.get(Constants.TAG_DEAL_DATE));
                hashMap.put(Constants.TAG_SHIPPING_TIME, productDatas.get(Constants.TAG_SHIPPING_TIME));
                hashMap.put(Constants.TAG_SIZES, sizes);
                hashMap.put(Constants.TAG_COUNTRY_ID, GetSet.getSellerCountryId());
                hashMap.put(Constants.TAG_PRICE, productDatas.get(Constants.TAG_PRICE));
                hashMap.put(Constants.TAG_QUANTITY, productDatas.get(Constants.TAG_QUANTITY));
                hashMap.put(Constants.TAG_MIN_QUANTITY, productDatas.get(Constants.TAG_MIN_QUANTITY));
                hashMap.put(Constants.TAG_DEAL_TYPE, pro_discount);
                hashMap.put(Constants.TAG_DISTRICT, String.valueOf(district_id));
                hashMap.put(Constants.TAG_UPAZILA, String.valueOf(upazila_id));
                Log.d(TAG, "addProductParams=" + hashMap);
                return hashMap;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        dialog.show();
        FantacySellerApplication.getInstance().addToRequestQueue(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*Remove Edittext focus*/
        super.onActivityResult(requestCode, resultCode, data);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (requestCode == ACTION_COLOR) {
            if (data.getStringExtra(Constants.FROM).equals("colors")) {
                selectedColorLists = (ArrayList<HashMap<String, Object>>) data.getSerializableExtra(Constants.COLOR_LIST);
                Log.d(TAG, "selectedColorLists-an" + selectedColorLists);
                colorMethod = data.getStringExtra(Constants.TAG_COLOR_METHOD);
                productDatas.put(Constants.TAG_COLOR_METHOD, colorMethod);
            } else {
                productDatas.put(Constants.TAG_SHIPPING_TIME, data.getStringExtra(Constants.SELECTED_TIME_DURATION));
            }
        } else if (requestCode == ACTION_SHIPSTO) {
            productDatas.put(Constants.TAG_EVERYWHERE_ELSE, data.getStringExtra(Constants.TAG_EVERYWHERE_ELSE));
            shipsToList = (ArrayList<HashMap<String, Object>>) data.getSerializableExtra(Constants.SHIPSTO_LIST);
        } else if (requestCode == ACTION_SIZE) {
            productDatas.put(Constants.TAG_SIZE_AVAILABILTY, data.getStringExtra(Constants.IS_SIZE_ENABLE));
            sizeList = ((ArrayList<HashMap<String, Object>>) data.getSerializableExtra(Constants.SIZE_LIST));
            Log.d(TAG, "sizelistvalues" + sizeList);
            if (sizeList.isEmpty()) {
                controlSwitch(sizeSwitch, false, "size");
            }
        } else if (requestCode == ACTION_POST_PRODUCT) {
            //    setUpUI();
        }
        sizeOptPrice.setText(productDatas.get(Constants.TAG_MAIN_PRICE));
        sizeOptQuantity.setText(productDatas.get(Constants.TAG_QUANTITY));
    }

    @Override
    public void onBackPressed() {
        productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
        productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
        productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
        productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
        productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
        Log.d(TAG, "productDatas=" + productDatas);
        getIntent().putExtra(Constants.INIT_PRODUCT_LIST, productDatas);
        getIntent().putExtra(Constants.COLOR_LIST, selectedColorLists);
        getIntent().putExtra(Constants.SHIPSTO_LIST, shipsToList);
        getIntent().putExtra(Constants.SIZE_LIST, sizeList);
        this.setResult(RESULT_OK, getIntent());
        super.onBackPressed();
    }

    private boolean isStringNull(String status, String checkString) {
        if (Boolean.valueOf(status)) {
            if ((TextUtils.isEmpty(checkString.trim()) || (checkString).trim().equalsIgnoreCase("0")))
                return true;
            else
                return false;
        } else
            return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addProduct:
                if (addBtn.getText().toString().equalsIgnoreCase(getString(R.string.add))) {
                    productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
                    productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
                    productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                }
                productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
                productDatas.put(Constants.TAG_MIN_QUANTITY, minOrderQuantity.getText().toString().trim());
                productDatas.put(Constants.TAG_COD, String.valueOf(checkSwitchEnable("cod", codSwitch)));
                productDatas.put(Constants.TAG_DEAL_ENABLED, String.valueOf(checkSwitchEnable("deals", dailyDealsSwitch)));
                productDatas.put(Constants.TAG_FB_DISC_ENABLE, String.valueOf(checkSwitchEnable("fb", fbdiscountSwitch)));
                productDatas.put(Constants.TAG_SIZE_AVAILABILTY, String.valueOf(checkSwitchEnable("size", sizeSwitch)));
                iseElseAmount = TextUtils.isEmpty(productDatas.get(Constants.TAG_EVERYWHERE_ELSE));
                isShipsToAmount = TextUtils.isEmpty(shipsTo);
                if (!Boolean.valueOf(productDatas.get(Constants.TAG_SIZE_AVAILABILTY)) && sizeOptPrice.getText().toString().equals(""))
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_item_price), Toast.LENGTH_LONG);
                else if (!Boolean.valueOf(productDatas.get(Constants.TAG_SIZE_AVAILABILTY)) && sizeOptQuantity.getText().toString().equals(""))
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_item_quantity), Toast.LENGTH_LONG);
                else if (isStringNull(productDatas.get(Constants.TAG_DEAL_ENABLED), dailyDealLayDiscPercent.getText().toString()))
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_deals_discount), Toast.LENGTH_LONG);
                else if (isStringNull(productDatas.get(Constants.TAG_DEAL_ENABLED), shippingTimeStamp + "") && pro_discount.equals("dailydeal"))
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_deals_date), Toast.LENGTH_LONG);
                else if (isStringNull(productDatas.get(Constants.TAG_FB_DISC_ENABLE), fbdiscountPercentage.getText().toString()))
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_fbdiscount_percentage), Toast.LENGTH_LONG);
                else if (TextUtils.isEmpty(productDatas.get(Constants.TAG_SHIPPING_TIME)))
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_shipping_time), Toast.LENGTH_LONG);
                else if (isShipsToAmount)
                    FantacySellerApplication.showToast(PostProduct.this, getString(R.string.reqd_ships_to), Toast.LENGTH_LONG);
                else if (minOrderQuantity.getText().toString().equals("")){
                    FantacySellerApplication.showToast(PostProduct.this, "Minimum quantity required", Toast.LENGTH_LONG);
                }
                else
                    addProduct();
                break;
            case R.id.resetProduct:
                onBackPressed();
                break;
            case R.id.shippingTimeLayout:
                productDatas.put(Constants.TAG_MAIN_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
                productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
                if (shippingTimeStamp != 0)
                    productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
                Intent intent = new Intent(PostProduct.this, ChooseProdOptions.class);
                intent.putExtra(Constants.FROM, "shippingTime");
                intent.putExtra(Constants.TAG_COLOR_MODE, "nocolor");
                intent.putExtra(Constants.SELECTED_TIME_DURATION, productDatas.get(Constants.TAG_SHIPPING_TIME));
                startActivityForResult(intent, ACTION_COLOR);
                break;

                ////////////////////Need to vanish That button and add extra 2 default field//////////////
            case R.id.shipsToLay:
                productDatas.put(Constants.TAG_MAIN_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
                productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
                if (shippingTimeStamp != 0)
                    productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
                Intent intent1 = new Intent(PostProduct.this, AddShipsTo.class);
                intent1.putExtra(Constants.FROM, "shipsTo");
                intent1.putExtra(Constants.SHIPSTO_LIST, shipsToList);
                intent1.putExtra(Constants.TAG_EVERYWHERE_ELSE, productDatas.get(Constants.TAG_EVERYWHERE_ELSE));
                startActivityForResult(intent1, ACTION_SHIPSTO);
                break;
            case R.id.colorSelectLay:
                productDatas.put(Constants.TAG_MAIN_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_PRICE, sizeOptPrice.getText().toString());
                productDatas.put(Constants.TAG_QUANTITY, sizeOptQuantity.getText().toString());
                productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, dailyDealLayDiscPercent.getText().toString());
                if (shippingTimeStamp != 0)
                    productDatas.put(Constants.TAG_DEAL_DATE, String.valueOf(shippingTimeStamp));
                productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, fbdiscountPercentage.getText().toString());
                Intent intent2 = new Intent(PostProduct.this, ChooseProdOptions.class);
                intent2.putExtra(Constants.FROM, "colors");
                intent2.putExtra(Constants.TAG_COLOR_MODE, colorSelect.getText().toString());
                intent2.putExtra(Constants.COLOR_LIST, selectedColorLists);
                startActivityForResult(intent2, ACTION_COLOR);
                break;
            case R.id.dailyDealDate:
                FantacySellerApplication.hideSoftKeyboard(this, this.getCurrentFocus());
                showDateSelector();
                break;
            case R.id.backBtn:
                onBackPressed();
                break;
        }
    }
}
