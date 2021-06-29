package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.Adapter.OrderDetailsAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.OrderCancel.OrderCancel;
import com.futureskyltd.app.utils.OrderDetails.Item;
import com.futureskyltd.app.utils.OrderDetails.OrderDetails;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.Status.ChangeStatus;
import com.futureskyltd.app.utils.TrackingMethod.TrackingMethod;

import java.util.ArrayList;

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
    private SharedPreferences preferences;
    private OrderDetails orderDetails;
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
                String[] values = new String[]{"কনফার্ম", "শিপড", "বাতিল করুন","ইনভয়েস ডাউনলোড করুন"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderDetailsActivity.this,
                        R.layout.option_row_item, android.R.id.text1, values);
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = layoutInflater.inflate(R.layout.option_layout, null);
                final PopupWindow popup = new PopupWindow(OrderDetailsActivity.this);
                popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.setContentView(layout);
                popup.setWidth(display.getWidth() * 50 / 100);
                popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popup.setFocusable(true);

                final ListView lv = (ListView) layout.findViewById(R.id.listView);
                lv.setAdapter(adapter);

                popup.showAsDropDown(v, -((display.getWidth() * 40 / 100)), -50);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int pos, final long id) {
                        switch (pos) {
                            case 0:
                                status = "Processing";
                                preferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE);
                                String retrievedMode  = preferences.getString("MODE",null);
                                if(orderDetails.getResult().getStatus().equals("Shipped")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি শিপপড হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }else if(orderDetails.getResult().getStatus().equals("Delivered")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি ডেলিভারি হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }else if(orderDetails.getResult().getStatus().equals("Canceled")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি বাতিল করা হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }else {
                                    callStatusChange(status);
                                }
                                break;
                            case 1:
                                status = "Shipped";
                                String shipped = "true";
                                preferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE);
                                preferences.edit().putString("MODE",shipped).apply();
                                if(orderDetails.getResult().getStatus().equals("Shipped")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি শিপপড হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }
                                else if(orderDetails.getResult().getStatus().equals("Delivered")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি ডেলিভারি হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }else if(orderDetails.getResult().getStatus().equals("Canceled")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি বাতিল করা হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }else {
                                    Retrofit retrofit = RetrofitClient.getRetrofitClient();
                                    ApiInterface api = retrofit.create(ApiInterface.class);

                                    Call<TrackingMethod> trackingMethodCall = api.postByTrackingMethod("Bearer "+ GetSet.getToken(), GetSet.getUserId(), orderId);

                                    trackingMethodCall.enqueue(new Callback<TrackingMethod>() {
                                        @Override
                                        public void onResponse(Call<TrackingMethod> call, Response<TrackingMethod> response) {
                                            if(response.code() == 200){
                                                TrackingMethod trackingMethod = response.body();
                                                if(trackingMethod.getStatus().equals("true")){
                                                    Intent intent = new Intent(OrderDetailsActivity.this, ShippingDetailsActivity.class);
                                                    intent.putExtra("trackingMethod", trackingMethod);
                                                    intent.putExtra("orderDetails", orderDetails);
                                                    intent.putExtra("status", status);
                                                    startActivity(intent);
                                                }else if(trackingMethod.getStatus().equals("false")){
                                                    Toast.makeText(OrderDetailsActivity.this, trackingMethod.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<TrackingMethod> call, Throwable t) {

                                        }
                                    });

                                }
                                //callStatusChange(status);
                                break;

                            case 2:
                                status = "cancel";
                                if(orderDetails.getResult().getStatus().equals("Canceled")){
                                    Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি পূর্বেই বাতিল হয়ে গেছে", Toast.LENGTH_SHORT).show();
                                }else{
                                    popUpDialog();
                                }
                                //Toast.makeText(OrderDetailsActivity.this, "Order canceled", Toast.LENGTH_SHORT).show();
                                break;

                            case 3:
                                status = "Invoice";
                               /* Intent url5 = new Intent(Intent.ACTION_VIEW);
                                url5.setData(Uri.parse("https://laalsobuj.com/api/downloadinvoice/"+orderId));
                                startActivity(url5);*/
                                downloadInvoice(orderId);
                                Toast.makeText(OrderDetailsActivity.this, "ইনভয়েস ডাউনলোড হচ্ছে", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    private void popUpDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailsActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.order_cancel_dialog_lay, null);
        final EditText write_notes = (EditText) dialogView.findViewById(R.id.write_notes);
        final TextView cancelBtn = (TextView) dialogView.findViewById(R.id.cancel);
        final TextView dismisBtn = (TextView) dialogView.findViewById(R.id.dismis);


        alertDialog.setView(dialogView);
        final AlertDialog alertDialog1 = alertDialog.create();

        dismisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = write_notes.getText().toString().trim();
                if(message.isEmpty()){
                    write_notes.setError("কেনো বাতিল করছেন তা জানাতে হবে ");
                    write_notes.requestFocus();
                    return;
                }
                setCancelBtn(message);
            }
        });

        alertDialog1.show();

    }

    private void setCancelBtn(String message) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<OrderCancel> orderCancelCall = api.postByCancelOrder("Bearer "+ GetSet.getToken(), orderId, message);

        orderCancelCall.enqueue(new Callback<OrderCancel>() {
            @Override
            public void onResponse(Call<OrderCancel> call, Response<OrderCancel> response) {
                if(response.code() == 200){
                    OrderCancel orderCancel = response.body();
                    if(orderCancel.getStatus().equals("true")){
                        Toast.makeText(OrderDetailsActivity.this, "অর্ডারটি সফলভাবে বাতিল হয়ছে", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(OrderDetailsActivity.this, NewOrder.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderCancel> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadInvoice(String orderId) {
        ///Do code for download invoice///
        Log.d(TAG, "downloadInvoice: "+ orderId);
        String url = "https://laalsobuj.com/pdf/download.php?order_id="+orderId;
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Download Pdf");
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Invoice"+".pdf");
        DownloadManager downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
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
                    Toast.makeText(OrderDetailsActivity.this, "অর্ডার অবস্থা পরিবর্তন হয়েছে !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangeStatus> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    ///Convert English number to Bnagla number////
    public String getENtoBN(String string)
    {
        Character bangla_number[]={'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        Character eng_number[]={'0','1','2','3','4','5','6','7','8','9'};
        String values = "";
        char[] character = string.toCharArray();
        for (int i=0; i<character.length ; i++) {
            Character c = ' ';
            for (int j = 0; j < eng_number.length; j++) {
                if(character[i]==eng_number[j])
                {
                    c=bangla_number[j];
                    break;
                }else {
                    c=character[i];
                }
            }
            values=values+c;
        }
        return values;
    }
    ///Convert English number to Bnagla number////

    private void getOrderDetails(String orderId) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<OrderDetails> orderDetailsCall = api.getByOrderDetails("Bearer "+ GetSet.getToken(), GetSet.getUserId(), orderId);

        orderDetailsCall.enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {
                if(response.code() == 200){
                    orderDetails = response.body();
                    if(orderDetails.getResult().getStatus().equals("Pending")){
                        orderStatus.setText(getString(R.string.pending_status));
                    }else if(orderDetails.getResult().getStatus().equals("Processing")){
                        orderStatus.setText("কনফার্ম হয়েছে");
                    }else if(orderDetails.getResult().getStatus().equals("Shipped")){
                        orderStatus.setText(getString(R.string.shipped_status));
                    }else if(orderDetails.getResult().getStatus().equals("Delivered")){
                        orderStatus.setText(getString(R.string.delivered_status));
                    }else if(orderDetails.getResult().getStatus().equals("Canceled")){
                        orderStatus.setText("ওর্ডারটি বাতিল হয়েছে");
                        Toast.makeText(OrderDetailsActivity.this, "ওর্ডারটি বাতিল করা হয়ে গেছে", Toast.LENGTH_SHORT).show();
                    }

                    String oId = String.valueOf(orderDetails.getResult().getOrderId());
                    String gTotal = String.valueOf(orderDetails.getResult().getGrandTotal());
                    orderDate.setText("অর্ডারের তারিখ: "+getENtoBN(orderDetails.getResult().getSaleDate()));
                    if(orderDetails.getResult().getPaymentMode().equals("COD")){

                        paymentMethod.setText("পেমেন্ট পদ্ধতি: "+ "ক্যাশ অন ডেলিভারি");
                        paymentStatus.setText("পেমেন্ট অবস্থা: "+"ক্যাশ অন ডেলিভারি");
                    }
                    buyerName.setText("ক্রেতার নাম: "+orderDetails.getResult().getShipping().getFullName());
                    buyerPhone.setText("ক্রেতার ফোন নং: "+getENtoBN(orderDetails.getResult().getShipping().getPhone()));
                    buyerAddress.setText("ক্রেতার ঠিকানা: "+getENtoBN(orderDetails.getResult().getShipping().getAddress1()));
                    userOrderId.setText("অর্ডার আইডি: #০০০০"+getENtoBN(oId));
                    grandTotal.setText("মোট মূল্য: ৳ "+getENtoBN(gTotal));

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