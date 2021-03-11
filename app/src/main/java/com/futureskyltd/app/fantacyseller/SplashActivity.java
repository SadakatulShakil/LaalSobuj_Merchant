package com.futureskyltd.app.fantacyseller;
/****************
 *
 * @author 'Hitasoft Technologies'
 *
 * Description:
 * This class is used for displaying splash screen
 *
 * Revision History:
 * Version 1.0 - Initial Version
 *
 *****************/

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.GetSet;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();
    int SPLASH_TIME_OUT = 2000;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        pref = getApplicationContext().getSharedPreferences("FantacyPref", MODE_PRIVATE);
        if (pref.getBoolean(Constants.IS_LOGGED, false)) {
            GetSet.setLogged(true);
            GetSet.setUserId(pref.getString(Constants.TAG_USER_ID, null));
            GetSet.setUserName(pref.getString(Constants.TAG_USER_NAME, null));
            GetSet.setRating(pref.getString(Constants.TAG_RATING, null));
            GetSet.setFullName(pref.getString(Constants.TAG_FULL_NAME, null));
            GetSet.setImageUrl(pref.getString(Constants.TAG_USER_IMAGE, null));
        }

        FantacySellerApplication.setLocale(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            openActivity();
        }
    }

    private void openActivity() {
        if (GetSet.isLogged()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,
                            FragmentMainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }, SPLASH_TIME_OUT);
        } else {
            startActivity(new Intent(SplashActivity.this,
                    LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "requestCode=" + requestCode);
        switch (requestCode) {
            case 100:
                openActivity();
                break;
        }
    }
}