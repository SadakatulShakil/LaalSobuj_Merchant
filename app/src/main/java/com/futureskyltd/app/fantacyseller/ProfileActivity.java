package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private TextView userName, userId, userContact1, userContact2, userEmail, userAddress, userZip, userShopName;
    private CircleImageView profileImage;
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
                    userZip.setText(profile.getZip());
                    userAddress.setText(profile.getUserAddress());
                    userShopName.setText(profile.getShopName());
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