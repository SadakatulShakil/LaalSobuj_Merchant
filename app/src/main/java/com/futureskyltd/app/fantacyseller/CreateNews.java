package com.futureskyltd.app.fantacyseller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateNews extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = this.getClass().getSimpleName();
    ImageView back, appName;
    TextView title, send;
    EditText message;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.activity_news_create);
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        send = (TextView) findViewById(R.id.processBtn);
        message = (EditText) findViewById(R.id.add_news);

        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        send.setVisibility(View.VISIBLE);

        title.setText(getResources().getString(R.string.news));
        send.setText(getResources().getString(R.string.send));

        back.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    private void postMessage(final String totalMessage) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_SEND_NEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.i(TAG, "postMessageRes: " + json);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        dialog.dismiss();
                        FantacySellerApplication.showStatusDialog(CreateNews.this, true, getResources().getString(R.string.posted_news), NewsActivity.class);
                    } else {
                        dialog.dismiss();
                        FantacySellerApplication.showToast(CreateNews.this, getString(R.string.no_followers), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG, "postMessageError: " + error.getMessage());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", GetSet.getUserId());
                params.put("message", totalMessage);
                Log.i(TAG, "postMessageParams: " + params);
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        dialog.show();
        FantacySellerApplication.getInstance().addNewsRequestQueue(req, "send_news");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                FantacySellerApplication.hideSoftKeyboard(CreateNews.this, v);
                finish();
                break;
            case R.id.processBtn:
                FantacySellerApplication.hideSoftKeyboard(CreateNews.this, v);
                if (message.getText().toString().trim().length() != 0)
                    postMessage(message.getText().toString());
                else
                    FantacySellerApplication.showToast(CreateNews.this, getString(R.string.reqd_news), Toast.LENGTH_LONG);
                break;
        }
    }
}
