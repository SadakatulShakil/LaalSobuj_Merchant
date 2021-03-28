package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TodayDeliveredOrderActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, editInfo;
    public static final String TAG = "deliver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_delivered_order);
        inItView();

        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("আজকে ডেলিভারি হয়েছে");

        back.setOnClickListener(this);
    }
    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
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