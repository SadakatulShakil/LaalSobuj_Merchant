package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.Adapter.OrderDetailsAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.OrderDetails.Item;
import com.futureskyltd.app.utils.OrderDetails.OrderDetails;
import com.futureskyltd.app.utils.Profile.Profile;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.Status.ChangeStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back, appName;
    TextView title, editInfo;
    private TextView orderStatus, orderDate, paymentMethod, paymentStatus, buyerName, buyerPhone, buyerAddress, userOrderId, grandTotal, actionBt;
    private ProgressBar progressBar;
    public static final String TAG = "profile";
    private ArrayList<Item> mItemArrayList = new ArrayList<>();
    private OrderDetailsAdapter orderDetailsAdapter;
    private RecyclerView itemListRevView;
    private String orderId;
    private Display display;
    private String status ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        inItView();
        progressBar.setVisibility(View.VISIBLE);
        Intent orderIntent = getIntent();
        orderId = orderIntent.getStringExtra("orderId");

        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("অর্ডার বিস্তারিত");
        display = this.getWindowManager().getDefaultDisplay();
        back.setOnClickListener(this);
        getOrderDetails(orderId);
        actionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] values = new String[]{"Confirm", "Shipped"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderDetailsActivity.this,
                        R.layout.option_row_item, android.R.id.text1, values);
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = layoutInflater.inflate(R.layout.option_layout, null);
                final PopupWindow popup = new PopupWindow(OrderDetailsActivity.this);
                popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.setContentView(layout);
                popup.setWidth(display.getWidth() * 48 / 100);
                popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popup.setFocusable(true);

                final ListView lv = (ListView) layout.findViewById(R.id.listView);
                lv.setAdapter(adapter);

                popup.showAsDropDown(v, -((display.getWidth() * 40 / 100)), -50);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int pos, long id) {
                        switch (pos) {
                            case 0:
                                status = "Processing";
                                callStatusChange(status);
                                break;
                            case 1:
                                status = "Shipped";
                                callStatusChange(status);
                                break;
                        }
                    }
                });
            }
        });
    }

    private void callStatusChange(String status) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<ChangeStatus> changeStatusCall = api.getChangeStatus("Bearer "+ GetSet.getToken(), GetSet.getUserId(), orderId, status);
        Log.d(TAG, "callStatusChange: " + GetSet.getToken()+"//"+GetSet.getUserId()+"//"+orderId+"//"+status);
        changeStatusCall.enqueue(new Callback<ChangeStatus>() {
            @Override
            public void onResponse(Call<ChangeStatus> call, Response<ChangeStatus> response) {
                if(response.code() == 200){
                    ChangeStatus changeStatus = response.body();
                    Log.d(TAG, "onResponseCng: "+ changeStatus.toString());
                    finish();
                    Intent intent = new Intent(OrderDetailsActivity.this, NewOrder.class);
                    startActivity(intent);
                    Toast.makeText(OrderDetailsActivity.this, "Status change successfully !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangeStatus> call, Throwable t) {

            }
        });
    }

    private void getOrderDetails(String orderId) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<OrderDetails> orderDetailsCall = api.getByOrderDetails("Bearer "+ GetSet.getToken(), GetSet.getUserId(), orderId);

        orderDetailsCall.enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {
                if(response.code() == 200){
                    OrderDetails orderDetails = response.body();
                    if(orderDetails.getResult().getStatus().equals("Pending")){
                        orderStatus.setText(getString(R.string.pending_status));
                    }else if(orderDetails.getResult().getStatus().equals("Processing")){
                        orderStatus.setText("কনফার্ম হয়েছে");
                    }else if(orderDetails.getResult().getStatus().equals("Shipped")){
                        orderStatus.setText(getString(R.string.shipped_status));
                    }else if(orderDetails.getResult().getStatus().equals("Delivered")){
                        orderStatus.setText(getString(R.string.delivered_status));
                    }
                    orderDate.setText("Order Date: "+orderDetails.getResult().getSaleDate());
                    paymentMethod.setText("Payment method: "+orderDetails.getResult().getPaymentMode());
                    paymentStatus.setText("Payment status: "+orderDetails.getResult().getPaymentMode());
                    buyerName.setText("Buyer name: "+orderDetails.getResult().getShipping().getFullName());
                    buyerPhone.setText("Buyer phone: "+orderDetails.getResult().getShipping().getPhone());
                    buyerAddress.setText("Buyer address: "+orderDetails.getResult().getShipping().getAddress1());
                    userOrderId.setText("Order Id: "+orderDetails.getResult().getOrderId());
                    grandTotal.setText("Grand total: "+orderDetails.getResult().getGrandTotal());

                    mItemArrayList.addAll(orderDetails.getResult().getItems());
                    Log.d(TAG, "onResponseList: "+mItemArrayList.size());
                    orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, mItemArrayList);
                    itemListRevView.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
                    itemListRevView.setAdapter(orderDetailsAdapter);
                    orderDetailsAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        orderStatus = findViewById(R.id.orderStatus);
        orderDate = findViewById(R.id.orderDate);
        paymentMethod = findViewById(R.id.paymentMethod);
        paymentStatus = findViewById(R.id.paymentStatus);
        buyerName = findViewById(R.id.buyerName);
        buyerPhone = findViewById(R.id.buyerPhone);
        buyerAddress = findViewById(R.id.buyerAddress);
        userOrderId = findViewById(R.id.orderId);
        progressBar = findViewById(R.id.progressBar);
        grandTotal = findViewById(R.id.grandTotal);
        itemListRevView = findViewById(R.id.orderListRevView);
        actionBt = findViewById(R.id.actionBt);
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