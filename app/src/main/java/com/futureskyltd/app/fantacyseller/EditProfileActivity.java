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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.EditMerchant.EditMerchant;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.Profile.Profile;
import com.futureskyltd.app.utils.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, updateProfileBtn;
    private Profile profileInfo;
    private EditText shopName, fullName, contact1, contact2, email, address, password, conPassword, userNId;
    private ProgressBar progressBar;
    private ImageView profileImage;
    private Intent pickIntent, chooseIntent;
    private File proFile;
    private Uri proImageUri;
    private RequestBody requestBody;
    private String result = null, latitude = "23.8360019", longitude = "90.3711972";
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
        shopName.setText(profileInfo.getShopName());
        fullName.setText(profileInfo.getFirstName());
        contact1.setText(profileInfo.getPhoneNo());
        contact2.setText(profileInfo.getPhoneOne());
        email.setText(profileInfo.getEmail());
        address.setText(profileInfo.getUserAddress());
        userNId.setText((profileInfo.getNid()));
        Picasso.with(getApplicationContext()).load(profileInfo.getProfileImage()).into(profileImage);
        //Log.d(TAG, "onCreate: " +profileInfo.getNid());
        password.setText("");
        conPassword.setText("");
        ///Set merchant Data

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

                if(uPassword != null){
                    if (uPassword.equals(uConPassword)) {
                        UpdateUserData(uShopName, uFullName, uPhone1, uPhone2, uEmail, uAddress, uPassword, uUserNid, latitude, longitude);
                    }else {
                        Toast.makeText(EditProfileActivity.this, "পাসওয়ার্ড মিলে নাই !", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        password.setError("পাসওয়ার্ড মিলে নাই !");
                        password.requestFocus();

                        conPassword.setError("পাসওয়ার্ড মিলে নাই !");
                        conPassword.requestFocus();
                    }
                }else{
                    UpdateUserData(uShopName, uFullName, uPhone1, uPhone2, uEmail, uAddress, uPassword, uUserNid, latitude, longitude);
                }
            }
        });
    }

    private void UpdateUserData(final String uShopName, final String uFullName, final String uPhone1, final String uPhone2, final String uEmail,
                                final String uAddress, final String uPassword, final String uUserNid, final String latitude, final String longitude) {

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
                passwordRequest, shopRequest, addressRequest, requestBody, latitudeRequest, longitudeRequest);
        Log.d(TAG, "UpdateUserData: " + GetSet.getUserId());
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
                Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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