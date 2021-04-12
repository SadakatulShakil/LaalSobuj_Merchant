package com.futureskyltd.app.fantacyseller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.Adapter.DistrictAdapter;
import com.futureskyltd.app.utils.Adapter.UpazilaAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.District.District;
import com.futureskyltd.app.utils.District.DistrictList;
import com.futureskyltd.app.utils.EditMerchant.EditMerchant;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.Profile.Profile;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.Upazila.Upazila;
import com.futureskyltd.app.utils.Upazila.UpazilatList;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, updateProfileBtn;
    private Profile profileInfo;
    private EditText shopName, fullName, contact1, contact2, email, address, password, conPassword, userNId, userZip;
    private ProgressBar progressBar;
    private ImageView profileImage;
    private Intent pickIntent, chooseIntent;
    private File proFile;
    private Uri proImageUri;
    private RequestBody requestBody;
    private String result = null, latitude = "23.8360019", longitude = "90.3711972";
    private Spinner districtSpinner, upazilaSpinner;
    private String districtName, upazilaName;
    private ArrayList<District> mDistrictList = new ArrayList<>();
    private ArrayList<Upazila> mUpazilaList = new ArrayList<>();
    private DistrictAdapter mDistrictAdapter;
    private UpazilaAdapter mUpazilaAdapter;
    private int district_id, upazila_id;
    private int desiredUp=0;
    private RadioButton rbGeneralBank, rbMobileBank;
    private String bankText = "bank";
    private EditText gAccountUserName, gBankNameOrMAccountNumber, gAccountNumberOrUserPhone;
    private LinearLayout generalBankingField;
    private TextView demo1, demo2, demo3;
    public static final String TAG ="Edit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        inItView();
        Intent intent = getIntent();
        profileInfo = (Profile) intent.getSerializableExtra("profileInfo");
        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("তথ্য এডিট");

        back.setOnClickListener(this);
        /////set merchant Data///
        if(profileInfo.getPaymentMethod().equals("bank")){
            rbGeneralBank.setChecked(true);
            rbMobileBank.setChecked(false);
        }else if(profileInfo.getPaymentMethod().equals("mobilebank")){
            rbMobileBank.setChecked(true);
            rbGeneralBank.setChecked(false);
        }
        shopName.setText(profileInfo.getShopName());
        fullName.setText(profileInfo.getFirstName());
        contact1.setText(profileInfo.getPhoneNo());
        contact2.setText(profileInfo.getPhoneOne());
        email.setText(profileInfo.getEmail());
        address.setText(profileInfo.getUserAddress());
        userNId.setText(profileInfo.getNid());
        userZip.setText(profileInfo.getZip());
        gAccountUserName.setText(profileInfo.getAccountName());
        gBankNameOrMAccountNumber.setText(profileInfo.getBankName());
        gAccountNumberOrUserPhone.setText(profileInfo.getAccountNumber());
        Picasso.with(getApplicationContext()).load(profileInfo.getProfileImage()).into(profileImage);


        //////Get District List/////////
        Retrofit retrofit = RetrofitClient.getRetrofitClient1();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<DistrictList> districtCall = api.getByDistrict();

        districtCall.enqueue(new Callback<DistrictList>() {
            @Override
            public void onResponse(Call<DistrictList> call, Response<DistrictList> response) {
                Log.d(TAG, "onResponse: " + response.code());
                DistrictList districtList = response.body();
                mDistrictList = (ArrayList<District>) districtList.getDistricts();
                Log.d(TAG, "onResponse: " + districtList.toString());
                Log.d(TAG, "onResponse: "+mDistrictList.size());
                mDistrictAdapter = new DistrictAdapter(EditProfileActivity.this, mDistrictList);
                districtSpinner.setAdapter(mDistrictAdapter);
                int districtId = Integer.parseInt(profileInfo.getDistrict());
                districtSpinner.setSelection(districtId-1);

                districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        District clickedDistrict = (District) parent.getItemAtPosition(position);

                        districtName = clickedDistrict.getDistrict();
                        district_id = clickedDistrict.getId();

                        getUpazilaList(district_id);

                        //Toast.makeText(EditProfileActivity.this, districtName +" is selected !"+" id: "+ district_id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<DistrictList> call, Throwable t) {
                Log.d(TAG, "onGetDistrictFailure: " + t.getMessage());
            }
        });
        //Log.d(TAG, "onCreate: " +profileInfo.getNid());
        password.setText("");
        conPassword.setText("");
        ///Set banking Data

        rbGeneralBank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bankText = "bank";
                    demo1.setText("এ্যকাউন্ট ধারীর নাম (বাধ্যতামূলক)");
                    gAccountUserName.setHint("বএ্যকাউন্ট ধারীর নাম (বাধ্যতামূলক)");
                    demo2.setText("ব্যাংকের নাম (বাধ্যতামূলক)");
                    gBankNameOrMAccountNumber.setHint("ব্যাংকের নাম (বাধ্যতামূলক)");
                    demo3.setText("এ্যকাউন্ট নম্বর (বাধ্যতামূলক)");
                    gAccountNumberOrUserPhone.setHint("এ্যকাউন্ট নম্বর (বাধ্যতামূলক)");
                    rbMobileBank.setChecked(false);
                }
            }
        });

        rbMobileBank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bankText = "mobilebank";
                    demo1.setText("মোবাইল ব্যাংকিং এর নাম");
                    gAccountUserName.setHint("মোবাইল ব্যাংকিং এর নাম");
                    demo2.setText("বিকাশ / রকেট / নগদ নম্বর লিখুন (বাধ্যতামূলক)");
                    gBankNameOrMAccountNumber.setHint("বিকাশ / রকেট / নগদ নম্বর লিখুন (বাধ্যতামূলক)");
                    demo3.setText("ফোন নম্বর লিখুন (বাধ্যতামূলক)");
                    gAccountNumberOrUserPhone.setHint("ফোন নম্বর লিখুন (বাধ্যতামূলক)");
                    rbGeneralBank.setChecked(false);

                }
            }
        });
        ///Image picker////
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uEmail = email.getText().toString().trim();
                String uPhone1 = contact1.getText().toString().trim();
                String uFullName = fullName.getText().toString().trim();
                String uUserNid = userNId.getText().toString().trim();
                String uPhone2 = contact2.getText().toString().trim();
                String uPassword = password.getText().toString().trim();
                String uConPassword = conPassword.getText().toString().trim();
                String uShopName = shopName.getText().toString().trim();
                String uAddress = address.getText().toString().trim();
                String uZip = userZip.getText().toString().trim();
                String uAccountUserName = gAccountUserName.getText().toString().trim();
                String uBankNameOrMAccountNumber = gBankNameOrMAccountNumber.getText().toString().trim();
                String uAccountNumberOrUserPhone = gAccountNumberOrUserPhone.getText().toString().trim();


                if (uPhone1.isEmpty()) {
                    contact1.setError("আপনার ফোন নম্বরটি দিতে হবে ");
                    contact1.requestFocus();
                    return;
                }

                if (uFullName.isEmpty()) {
                    fullName.setError("এই তথ্যটি দিতে হবে");
                    fullName.requestFocus();
                    return;
                }

                if (uShopName.isEmpty()) {
                    shopName.setError("এই তথ্যটি দিতে হবে");
                    shopName.requestFocus();
                    return;
                }

                if (uAddress.isEmpty()) {
                    address.setError("এই তথ্যটি দিতে হবে");
                    address.requestFocus();
                    return;
                }

                if (uUserNid.isEmpty()) {
                    userNId.setError("এই তথ্যটি দিতে হবে");
                    userNId.requestFocus();
                    return;
                }

                if (uAccountUserName.isEmpty()) {
                    gAccountUserName.setError("এই তথ্যটি দিতে হবে");
                    gAccountUserName.requestFocus();
                    return;
                }

                if (uBankNameOrMAccountNumber.isEmpty()) {
                    gBankNameOrMAccountNumber.setError("এই তথ্যটি দিতে হবে");
                    gBankNameOrMAccountNumber.requestFocus();
                    return;
                }

                if (uAccountNumberOrUserPhone.isEmpty()) {
                    gAccountNumberOrUserPhone.setError("এই তথ্যটি দিতে হবে");
                    gAccountNumberOrUserPhone.requestFocus();
                    return;
                }


                if(uPassword != null){
                    if (uPassword.equals(uConPassword)) {
                        UpdateUserData(uShopName, uFullName, uPhone1, uPhone2, uEmail, uAddress, uPassword,
                                uUserNid, latitude, longitude, district_id, upazila_id, uZip, bankText, uAccountUserName,
                                uBankNameOrMAccountNumber, uAccountNumberOrUserPhone);
                    }else {
                        Toast.makeText(EditProfileActivity.this, "পাসওয়ার্ড মিলে নাই !", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        password.setError("পাসওয়ার্ড মিলে নাই !");
                        password.requestFocus();

                        conPassword.setError("পাসওয়ার্ড মিলে নাই !");
                        conPassword.requestFocus();
                    }
                }else{
                    UpdateUserData(uShopName, uFullName, uPhone1, uPhone2, uEmail, uAddress, uPassword, uUserNid,
                            latitude, longitude, district_id, upazila_id, uZip, bankText, uAccountUserName, uBankNameOrMAccountNumber, uAccountNumberOrUserPhone);
                }
            }
        });
    }

    private void getUpazilaList(int district_id) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient1();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<UpazilatList> upazilaCall = api.postByUpazila(district_id);

        upazilaCall.enqueue(new Callback<UpazilatList>() {
            @Override
            public void onResponse(Call<UpazilatList> call, Response<UpazilatList> response) {
                Log.d(TAG, "onResponse: " + response.code());
                UpazilatList upazilatList = response.body();
                mUpazilaList = (ArrayList<Upazila>) upazilatList.getUpazila();
                Log.d(TAG, "onResponse: " + upazilatList.toString());
                mUpazilaAdapter = new UpazilaAdapter(EditProfileActivity.this, mUpazilaList);
                upazilaSpinner.setAdapter(mUpazilaAdapter);
                Log.d(TAG, "onResponse: "+ upazilatList.toString());
                for(int i =0; i<mUpazilaList.size(); i++){
                    //String getUp = String.valueOf(upazilatList.getUpazila().get(i).getId());
                    //Log.d(TAG, "onRequestUp: "+ getUp);
                    int profileUp = Integer.parseInt(profileInfo.getUpazila());
                    Log.d(TAG, "onProfileUp: "+profileUp);
                    if(upazilatList.getUpazila().get(i).getId() == profileUp){
                        Log.d(TAG, "onResList: "+upazilatList.getUpazila().get(i).getId());
                        Log.d(TAG, "onResProList: " + profileUp);
                        desiredUp = i;
                    }

                    /*if(getUp.equals(profileInfo.getUpazila())){
                        upazilaSpinner.setSelection(upazilatList.getUpazila().get(i).getId());
                    }*/
                }

                upazilaSpinner.setSelection(desiredUp);
                upazilaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Upazila clickedUpazila = (Upazila) parent.getItemAtPosition(position);

                        upazilaName = clickedUpazila.getUpazila();
                        upazila_id = clickedUpazila.getId();

                        //Toast.makeText(EditProfileActivity.this, upazilaName +" is selected !"+" id: "+ upazila_id, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<UpazilatList> call, Throwable t) {
                Log.d(TAG, "onGetUpazilaFailure: " + t.getMessage());
            }
        });
    }
    private void UpdateUserData(final String uShopName, final String uFullName, final String uPhone1, final String uPhone2, final String uEmail,
                                final String uAddress, final String uPassword, final String uUserNid, final String latitude, final String longitude,
                                final int district_id, final int upazila_id,
                                final String uZip, final String bankText, final String uAccountUserName, final String uBankNameOrMAccountNumber, final String uAccountNumberOrUserPhone) {

        String dId = String.valueOf(district_id);
        String uId = String.valueOf(upazila_id);
        RequestBody shopRequest = RequestBody.create(MediaType.parse("text/plain"), uShopName);
        RequestBody userIdRequest = RequestBody.create(MediaType.parse("text/plain"), GetSet.getUserId());
        RequestBody fullNameRequest = RequestBody.create(MediaType.parse("text/plain"), uFullName);
        RequestBody phone1Request = RequestBody.create(MediaType.parse("text/plain"), uPhone1);
        RequestBody phone2Request = RequestBody.create(MediaType.parse("text/plain"), uPhone2);
        RequestBody emailRequest = RequestBody.create(MediaType.parse("text/plain"), uEmail);
        RequestBody addressRequest = RequestBody.create(MediaType.parse("text/plain"), uAddress);
        RequestBody passwordRequest = RequestBody.create(MediaType.parse("text/plain"), uPassword);
        RequestBody latitudeRequest = RequestBody.create(MediaType.parse("text/plain"), latitude);
        RequestBody longitudeRequest = RequestBody.create(MediaType.parse("text/plain"), longitude);
        RequestBody nIdRequest = RequestBody.create(MediaType.parse("text/plain"), uUserNid);
        RequestBody districtRequest = RequestBody.create(MediaType.parse("text/plain"), dId);
        RequestBody upazilaRequest = RequestBody.create(MediaType.parse("text/plain"), uId);
        RequestBody zipRequest = RequestBody.create(MediaType.parse("text/plain"), uZip);
        RequestBody bankTextRequest = RequestBody.create(MediaType.parse("text/plain"), bankText);
        RequestBody uAccountUserNameRequest = RequestBody.create(MediaType.parse("text/plain"), uAccountUserName);
        RequestBody uBankNameOrMAccountNumberRequest = RequestBody.create(MediaType.parse("text/plain"), uBankNameOrMAccountNumber);
        RequestBody uAccountNumberOrUserPhoneRequest = RequestBody.create(MediaType.parse("text/plain"), uAccountNumberOrUserPhone);

        if(proImageUri != null){
            Log.d(TAG, "UpdateUserData: " +"file error");
            proFile = new File(getRealPathFromUri(proImageUri));
            requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), proFile);
            Log.d(TAG, "onImage: " + requestBody.toString());
        }

        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<EditMerchant> editMerchantCall = api.postByEditMerchant("Bearer "+ GetSet.getToken(), userIdRequest, fullNameRequest, phone1Request, phone2Request, nIdRequest, emailRequest,
                passwordRequest, shopRequest, addressRequest, requestBody, latitudeRequest,
                longitudeRequest, districtRequest, upazilaRequest, zipRequest, bankTextRequest, uAccountUserNameRequest, uBankNameOrMAccountNumberRequest, uAccountNumberOrUserPhoneRequest);
        Log.d(TAG, "UpdateUserData: " + GetSet.getUserId()+ bankText + uAccountUserName + uBankNameOrMAccountNumber + uAccountNumberOrUserPhone);
        editMerchantCall.enqueue(new Callback<EditMerchant>() {
            @Override
            public void onResponse(Call<EditMerchant> call, Response<EditMerchant> response) {
                Log.d(TAG, "onResponse: "+response.code());
                if(response.code() == 200){
                    progressBar.setVisibility(View.GONE);
                    EditMerchant editMerchant = response.body();

                    Log.d(TAG, "onResponses: " +response.body().toString());

                    if(editMerchant.getStatus().equals("true")){
                        shopName.setText("");
                        fullName.setText("");
                        contact1.setText("");
                        contact2.setText("");
                        email.setText("");
                        address.setText("");
                        password.setText("");
                        conPassword.setText("");

                        finish();
                        Intent intent = new Intent(EditProfileActivity.this, FragmentMainActivity.class);
                        startActivity(intent);
                        Toast.makeText(EditProfileActivity.this, "আপনার তথ্য অপডেট হয়েছে !", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EditProfileActivity.this, editMerchant.getMessage(), Toast.LENGTH_LONG).show();

                        if(editMerchant.getErrors().getEmail()!= null){
                            email.setError(editMerchant.getErrors().getEmail().getUnique());
                            email.requestFocus();

                        }if(editMerchant.getErrors().getPhoneNo()!= null){
                            contact1.setError(editMerchant.getErrors().getPhoneNo().getUnique());
                            contact1.requestFocus();
                        }
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this, "সার্ভারের সমস্যা !", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<EditMerchant> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: "+t.getMessage());
                //Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ChooseImage() {
        chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: " + "Activity result success");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == -1 && data != null && data.getData() != null) {
                proImageUri = data.getData();
                Log.d(TAG, "onActivityResult: " + proImageUri);
                Picasso.with(getApplicationContext()).load(proImageUri).into(profileImage);

            }
        }
    }

    private String getRealPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(EditProfileActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        if(cursor != null){

            result = cursor.getString(column_index);
        }
        cursor.close();
        return result;
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        shopName = findViewById(R.id.shopName);
        fullName = findViewById(R.id.fullName);
        contact1 = findViewById(R.id.userContact1);
        contact2 = findViewById(R.id.userContact2);
        email = findViewById(R.id.userEmail);
        address = findViewById(R.id.userAddress);
        password = findViewById(R.id.password);
        conPassword = findViewById(R.id.conPassword);
        progressBar = findViewById(R.id.progressBar);
        profileImage = findViewById(R.id.profileImage);
        userNId = findViewById(R.id.userNID);
        updateProfileBtn = findViewById(R.id.updateMerchantBt);
        districtSpinner = findViewById(R.id.userDistrictSpinner);
        upazilaSpinner = findViewById(R.id.userUpazilaSpinner);
        userZip = findViewById(R.id.userZip);
        generalBankingField = findViewById(R.id.generalBankingField);
        rbGeneralBank = findViewById(R.id.generalBankingRb);
        rbMobileBank = findViewById(R.id.mobileBankingRb);
        gAccountUserName = findViewById(R.id.gAccountUserName);
        gBankNameOrMAccountNumber = findViewById(R.id.gBankNameOrMAccountNumber);
        gAccountNumberOrUserPhone = findViewById(R.id.gAccountNumberOrUserPhone);
        demo1 = findViewById(R.id.demo1);
        demo2 = findViewById(R.id.demo2);
        demo3 = findViewById(R.id.demo3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }
}