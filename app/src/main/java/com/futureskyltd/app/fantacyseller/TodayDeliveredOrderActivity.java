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

import com.futureskyltd.app.utils.Adapter.MyOrderAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.MyOrder.MyOrder;
import com.futureskyltd.app.utils.MyOrder.Order;
import com.futureskyltd.app.utils.RetrofitClient;

import java.util.ArrayList;

public class TodayDeliveredOrderActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, editInfo;
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private ArrayList<Order> deliveredOrderArrayList = new ArrayList<>();
    private MyOrderAdapter myOrderAdapter;
    private RecyclerView deliveredOrderRevView;
    private ProgressBar progressBar;
    private LinearLayout nullLay;
    public static final String TAG = "deliver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_delivered_order);
        inItView();
        progressBar.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("সকল ডেলিভারি তালিকা");

        back.setOnClickListener(this);

        getAllDeliveredOrder();
    }

    private void getAllDeliveredOrder() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<MyOrder> myOrderCall = api.getMyOrder("Bearer "+ GetSet.getToken(), GetSet.getUserId());

        myOrderCall.enqueue(new Callback<MyOrder>() {
            @Override
            public void onResponse(Call<MyOrder> call, Response<MyOrder> response) {
                if(response.code() == 200){
                    MyOrder myOrder = response.body();
                    if(myOrder.getOrders()!=null){

                        orderArrayList.addAll(myOrder.getOrders());
                        for(int i = 0; i<orderArrayList.size();i++){
                            if(myOrder.getOrders().get(i).getStatus().equals("Delivered")){
                                deliveredOrderArrayList.add(myOrder.getOrders().get(i));
                            }
                        }
                        Log.d(TAG, "onRList: "+deliveredOrderArrayList.size());
                        myOrderAdapter = new MyOrderAdapter(TodayDeliveredOrderActivity.this, deliveredOrderArrayList);
                        deliveredOrderRevView.setLayoutManager(new LinearLayoutManager(TodayDeliveredOrderActivity.this));
                        deliveredOrderRevView.setAdapter(myOrderAdapter);
                        myOrderAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                    }

                    if(deliveredOrderArrayList.size() == 0 ){
                        deliveredOrderRevView.setVisibility(View.GONE);
                        nullLay.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyOrder> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TodayDeliveredOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        deliveredOrderRevView = findViewById(R.id.inCompleteOrderAmountRevView);
        progressBar = findViewById(R.id.progressBar);
        nullLay = findViewById(R.id.nullLay);
        deliveredOrderRevView=  findViewById(R.id.deliveredOrderRevView);
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