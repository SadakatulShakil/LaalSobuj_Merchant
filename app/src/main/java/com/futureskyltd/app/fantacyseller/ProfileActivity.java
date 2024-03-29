package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.Profile.Profile;
import com.futureskyltd.app.utils.RetrofitClient;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, editInfo;
    public static final String TAG = "profile";
    private ProgressBar progressBar;
    private Profile profile;
    private TextView userName, userId, userContact1, userContact2, userEmail, userAddress, userZip, userShopName, districtName, upazilaName, userNid;
    private CircleImageView profileImage;
    private TextView paymentMethod, accountUserName, bankName, accountNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        inItView();
        progressBar.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("প্রোফাইল");

        back.setOnClickListener(this);
        editInfo.setOnClickListener(this);

        getUserProfile();

    }

    private void getUserProfile() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<Profile> profileCall = api.getByProfile("Bearer "+ GetSet.getToken(), GetSet.getUserId());

        profileCall.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d(TAG, "onResponse: "+response.code());
                if(response.code() == 200){
                    progressBar.setVisibility(View.GONE);
                    profile = response.body();
                    userName.setText(profile.getFirstName());
                    userId.setText(profile.getId());
                    userContact1.setText(profile.getPhoneNo());
                    userContact2.setText(profile.getPhoneOne());
                    userEmail.setText(profile.getEmail());
                    userNid.setText(profile.getNid());
                    userZip.setText(profile.getZip());
                    userAddress.setText(profile.getUserAddress());
                    userShopName.setText(profile.getShopName());
                    districtName.setText(profile.getDistrictName());
                    upazilaName.setText(profile.getUpazilaName());
                    paymentMethod.setText(profile.getPaymentMethod());
                    accountUserName.setText(profile.getAccountName());
                    bankName.setText(profile.getBankName());
                    accountNumber.setText(profile.getAccountNumber());
                    restrictionDialog(userNid.getText().toString(), paymentMethod.getText().toString());
                    Picasso.with(getApplicationContext()).load(profile.getProfileImage()).into(profileImage);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

            }
        });
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        editInfo = findViewById(R.id.editInfo);
        progressBar = findViewById(R.id.progressBar);
        userName = findViewById(R.id.userName);
        userId = findViewById(R.id.userId);
        userContact1 = findViewById(R.id.userContact);
        userContact2 = findViewById(R.id.userContact1);
        userEmail = findViewById(R.id.userEmail);
       userAddress = findViewById(R.id.userAddress);
        userZip = findViewById(R.id.userZip);
        userShopName = findViewById(R.id.userShopName);
        profileImage = findViewById(R.id.profileImage);
        districtName = findViewById(R.id.district);
        upazilaName = findViewById(R.id.upazila);
        userNid = findViewById(R.id.userNid);
        paymentMethod = findViewById(R.id.paymentMethod);
        accountUserName = findViewById(R.id.accountUserName);
        bankName = findViewById(R.id.bankName);
        accountNumber = findViewById(R.id.accountNumber);
    }

    private void restrictionDialog(String nid, String paymentMethod) {
        if(nid.equals("") || paymentMethod.equals("")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("বাধ্যতামূলক তথ্য !");
            alertDialog.setMessage("জাতীয় পরিচয় পত্রের নম্বর ও পেমেন্ট মাধ্যমের তথ্য ব্যতিত আপনি কোন পণ্য যোগ করতে পারবেন না");
            alertDialog.setIcon(R.drawable.ic_lock);
            alertDialog.setPositiveButton("তথ্য দিন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent editInfoIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    editInfoIntent.putExtra("profileInfo", profile);
                    startActivity(editInfoIntent);
                }
            });

            alertDialog.setNegativeButton("পরে দিতে চান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent homeIntent = new Intent(ProfileActivity.this, FragmentMainActivity.class);
                    startActivity(homeIntent);
                }
            });

            alertDialog.create();
            alertDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.editInfo:
                Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                editIntent.putExtra("profileInfo", profile);
                startActivity(editIntent);
                break;
        }
    }
}