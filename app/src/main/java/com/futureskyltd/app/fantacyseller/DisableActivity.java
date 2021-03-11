package com.futureskyltd.app.fantacyseller;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.futureskyltd.app.external.CircleProgress;
import com.futureskyltd.app.helper.NetworkReceiver;

/**
 * Created by hitasoft on 6/7/17.
 */

public class DisableActivity extends AppCompatActivity implements View.OnClickListener, NetworkReceiver.ConnectivityReceiverListener {

    public String TAG = this.getClass().getSimpleName();
    CircleProgress circleProgress;
    ImageView backBtn, nullImage;
    TextView title, subTitle, continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable);

        circleProgress = (CircleProgress) findViewById(R.id.circleProgress);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        title = (TextView) findViewById(R.id.title);
        subTitle = (TextView) findViewById(R.id.subTitle);
        continueBtn = (TextView) findViewById(R.id.continueBtn);

        backBtn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);

        nullImage.setVisibility(View.VISIBLE);
        circleProgress.setVisibility(View.GONE);
        title.setText(getString(R.string.admin_block));
        subTitle.setText(getString(R.string.admin_block_text));
        continueBtn.setText(getString(R.string.to_login));
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), NetworkReceiver.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), true);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), isConnected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.continueBtn:
                finish();
                Intent intent = new Intent(DisableActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }
}
