package com.futureskyltd.app.fantacyseller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.futureskyltd.app.helper.NetworkReceiver;

/**
 * Created by hitasoft on 18/7/17.
 */

public class HelpContent extends AppCompatActivity implements View.OnClickListener, NetworkReceiver.ConnectivityReceiverListener {

    public final String TAG = this.getClass().getSimpleName();
    public static String mainContent = "", subContent = "";
    ImageView backBtn, appName;
    TextView title;
    String pageName = "";
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_content_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = (TextView) findViewById(R.id.title);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        webView = (WebView) findViewById(R.id.webView);
        appName = (ImageView) findViewById(R.id.appName);

        appName.setVisibility(View.INVISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);

        pageName = getIntent().getExtras().getString("pageName");
        title.setText(pageName);
        webView.loadData(mainContent + "\n" + subContent, "text/html", "UTF-8");
        backBtn.setOnClickListener(this);

        // register connection status listener
        FantacySellerApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkReceiver.isConnected()) {
            FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), true);
        } else {
            FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), false);
        }
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
        }
    }
}
