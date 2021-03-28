package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.Adapter.TodayOrderAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.TodayNewOrder.Datum;
import com.futureskyltd.app.utils.TodayNewOrder.TodayNewOrder;

import java.util.ArrayList;

public class TodayNewOrderActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, editInfo;
    public static final String TAG = "order";
    private TodayOrderAdapter todayOrderAdapter;
    private ArrayList<Datum> resultArrayList = new ArrayList<>();
    private RecyclerView todayOrderListRevView;
    private ProgressBar progressBar;
    private LinearLayout nullLay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_new_order);
        inItView();
        progressBar.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("আজকের নতুন অর্ডার");

        back.setOnClickListener(this);
        getTodayOrderList();
    }

    private void getTodayOrderList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<TodayNewOrder> todayOrderCall = api.getTodayOrder("Bearer "+ GetSet.getToken(), GetSet.getUserId());

        todayOrderCall.enqueue(new Callback<TodayNewOrder>() {
            @Override
            public void onResponse(Call<TodayNewOrder> call, Response<TodayNewOrder> response) {
                if(response.code() == 200){
                    TodayNewOrder todayNewOrder = response.body();
                    Log.d(TAG, "result: "+todayNewOrder.toString());
                    resultArrayList.addAll(todayNewOrder.getData());
                    Log.d(TAG, "onResponseList: "+resultArrayList.size());

                        todayOrderListRevView.setVisibility(View.VISIBLE);
                        nullLay.setVisibility(View.GONE);
                        todayOrderAdapter = new TodayOrderAdapter(TodayNewOrderActivity.this, resultArrayList);
                        todayOrderListRevView.setLayoutManager(new LinearLayoutManager(TodayNewOrderActivity.this));
                        todayOrderListRevView.setAdapter(todayOrderAdapter);
                        todayOrderAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);

                    if(resultArrayList.size() == 0){
                        todayOrderListRevView.setVisibility(View.GONE);
                        nullLay.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Call<TodayNewOrder> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TodayNewOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        todayOrderListRevView = findViewById(R.id.orderListRevView);
        progressBar = findViewById(R.id.progressBar);
        nullLay = findViewById(R.id.nullLay);
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