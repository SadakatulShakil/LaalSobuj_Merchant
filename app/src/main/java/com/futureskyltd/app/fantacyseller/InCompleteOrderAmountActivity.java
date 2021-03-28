package com.futureskyltd.app.fantacyseller;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InCompleteOrderAmountActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, editInfo;
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private ArrayList<Order> completeOrderArrayList = new ArrayList<>();
    private MyOrderAdapter myOrderAdapter;
    private RecyclerView completeOrderRevView;
    private ProgressBar progressBar;
    private LinearLayout nullLay;
    public static final String TAG ="complete";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomplete_order_amount);
        inItView();
        progressBar.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("অসফল অর্ডার");

        back.setOnClickListener(this);

        getCompleteOrderAmountList();
    }

    private void getCompleteOrderAmountList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<MyOrder> myOrderCall = api.getMyOrder("Bearer "+ GetSet.getToken(), GetSet.getUserId());

        myOrderCall.enqueue(new Callback<MyOrder>() {
            @Override
            public void onResponse(Call<MyOrder> call, Response<MyOrder> response) {
                if(response.code() == 200){
                    MyOrder myOrder = response.body();
                    orderArrayList.addAll(myOrder.getOrders());
                    for(int i = 0; i<orderArrayList.size();i++){
                        if(myOrder.getOrders().get(i).getStatus().equals("Pending")){
                            completeOrderArrayList.add(myOrder.getOrders().get(i));
                        }
                    }
                    Log.d(TAG, "onRList: "+completeOrderArrayList.size());
                    myOrderAdapter = new MyOrderAdapter(InCompleteOrderAmountActivity.this, completeOrderArrayList);
                    completeOrderRevView.setLayoutManager(new LinearLayoutManager(InCompleteOrderAmountActivity.this));
                    completeOrderRevView.setAdapter(myOrderAdapter);
                    myOrderAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    if(completeOrderArrayList.size() == 0){
                        completeOrderRevView.setVisibility(View.GONE);
                        nullLay.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyOrder> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
                Toast.makeText(InCompleteOrderAmountActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        completeOrderRevView = findViewById(R.id.inCompleteOrderAmountRevView);
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