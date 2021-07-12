package com.futureskyltd.app.fantacyseller;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.Adapter.CourierAdapter;
import com.futureskyltd.app.utils.Adapter.UpazilaAdapter;
import com.futureskyltd.app.utils.ApiInterface;
import com.futureskyltd.app.utils.GetSet;
import com.futureskyltd.app.utils.OrderDetails.OrderDetails;
import com.futureskyltd.app.utils.OrderPlace.OrderPlace;
import com.futureskyltd.app.utils.RetrofitClient;
import com.futureskyltd.app.utils.Status.ChangeStatus;
import com.futureskyltd.app.utils.TrackingMethod.Datum;
import com.futureskyltd.app.utils.TrackingMethod.TrackingMethod;
import com.futureskyltd.app.utils.Upazila.Upazila;

import java.util.ArrayList;
import java.util.Calendar;

public class ShippingDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back, appName;
    TextView title, editInfo, orderId, tips, orderDate, paymentMethod, buyerName, buyerPhone, grandTotal, buyerAddress, dateField;
    private EditText write_notes;
    private TrackingMethod trackingMethod;
    private OrderDetails orderDetails;
    private DatePickerDialog.OnDateSetListener setListener;
    private String status, oId;
    private Spinner courierMethodSpinner;
    private CourierAdapter courierAdapter;
    private String courierMethodName;
    private ArrayList<Datum> courierList = new ArrayList<>();
    private TextView sendBtn, cancelBtn;
    public static final String TAG ="track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_details);
        inItView();
        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("শিপিং এর বিস্তারিত");
        back.setOnClickListener(this);

        Intent intent = getIntent();
        trackingMethod = (TrackingMethod) intent.getSerializableExtra("trackingMethod");
        orderDetails = (OrderDetails) intent.getSerializableExtra("orderDetails");
        Log.d(TAG, "onCreate: "+ trackingMethod.getData());
        courierList = (ArrayList<Datum>) trackingMethod.getData();
        status = intent.getStringExtra("status");
        courierAdapter = new CourierAdapter(ShippingDetailsActivity.this, courierList);
        courierMethodSpinner.setAdapter(courierAdapter);


        oId = String.valueOf(orderDetails.getResult().getOrderId());
        String gTotal = String.valueOf(orderDetails.getResult().getGrandTotal());
        orderDate.setText("অর্ডারের তারিখ: "+getENtoBN(orderDetails.getResult().getSaleDate()));
        if(orderDetails.getResult().getPaymentMode().equals("COD")){

            paymentMethod.setText("পেমেন্ট পদ্ধতি: "+ "ক্যাশ অন ডেলিভারি");
        }

        buyerName.setText("ক্রেতার নাম: "+orderDetails.getResult().getShipping().getFullName());
        buyerPhone.setText("ক্রেতার ফোন নং: "+getENtoBN(orderDetails.getResult().getShipping().getPhone()));
        buyerAddress.setText("ক্রেতার ঠিকানা: "+getENtoBN(orderDetails.getResult().getShipping().getAddress1()));
        orderId.setText("অর্ডার আইডি: #০০০০"+getENtoBN(oId));
        grandTotal.setText("মোট মূল্য: ৳ "+getENtoBN(gTotal));

        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ShippingDetailsActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String nDate = day+"/"+month+"/"+year;
                dateField.setText(nDate);
            }
        };

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ShippingDetailsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                String uNotes = write_notes.getText().toString().trim();
                if(uNotes.isEmpty()){
                    write_notes.setError("মতামত জানাতে হবে ");
                    write_notes.requestFocus();
                    return;
                }
                if(dateField.getText().toString().trim().isEmpty()){
                    dateField.setError("তারিখ বাছাই করতে হবে ");
                    write_notes.requestFocus();
                    return;
                }

                setUpCourierService(uNotes, dateField.getText().toString().trim(), courierMethodName, GetSet.getUserId(), oId);
                Log.d(TAG, "onClick: " + uNotes+"...."+dateField.getText().toString().trim()+"...."+courierMethodName+"...."+ GetSet.getUserId()+"...."+oId);
            }
        });

    }

    private void setUpCourierService(String uNotes, String date, String courierMethodName, String userId, final String orderId) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<OrderPlace> orderPlaceCall = api.postByOrderPlace("Bearer "+ GetSet.getToken(), userId, orderId, courierMethodName, date, uNotes);

        orderPlaceCall.enqueue(new Callback<OrderPlace>() {
            @Override
            public void onResponse(Call<OrderPlace> call, Response<OrderPlace> response) {
                if(response.code() == 200){
                    OrderPlace orderPlace = response.body();
                    if(orderPlace.getStatus().equals("true")){
                        Toast.makeText(ShippingDetailsActivity.this,"অর্ডারটি সফল ভাবে পাঠানো হয়েছে", Toast.LENGTH_SHORT).show();
                        callStatusChange(status);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderPlace> call, Throwable t) {
                Toast.makeText(ShippingDetailsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callStatusChange(String status) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<ChangeStatus> changeStatusCall = api.getChangeStatus("Bearer "+ GetSet.getToken(), GetSet.getUserId(), oId, status);
        Log.d(TAG, "callStatusChange: " + GetSet.getToken()+"//"+GetSet.getUserId()+"//"+orderId+"//"+status);
        changeStatusCall.enqueue(new Callback<ChangeStatus>() {
            @Override
            public void onResponse(Call<ChangeStatus> call, Response<ChangeStatus> response) {
                if(response.code() == 200){
                    ChangeStatus changeStatus = response.body();
                    Log.d(TAG, "onResponseCng: "+ changeStatus.toString());
                    finish();
                    Intent intent = new Intent(ShippingDetailsActivity.this, NewOrder.class);
                    startActivity(intent);
                    //Toast.makeText(ShippingDetailsActivity.this, , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangeStatus> call, Throwable t) {
                Toast.makeText(ShippingDetailsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void popUpDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShippingDetailsActivity.this);
        alertDialog.setTitle("কুরিয়ার অর্ডার প্লেইস সংক্রান্ত টিপস");
        alertDialog.setMessage("আপনার পণ্যের অর্ডারটি গ্রাহকের নিকট পৌঁছে দেয়ার জন্য ফরম এ উল্লেখিত যেকোনো একটি কুরিয়ার সার্ভিস ব্যবহার করুন।" +
                "আপনার এলাকাতে যদি উল্লেখিত কুরিয়ার সার্ভিস এর কোনটিই অর্ডার গ্রহণে সম্ভব না হয় তাহলে আপনার অর্ডারটি বাতিল করুন।");
        alertDialog.setIcon(R.drawable.tips);
        alertDialog.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void inItView() {
        back = (ImageView) findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        orderId = (TextView) findViewById(R.id.orderId);
        tips = (TextView) findViewById(R.id.tips);
        orderDate = (TextView) findViewById(R.id.orderDate);
        buyerName = (TextView) findViewById(R.id.buyerName);
        buyerPhone = (TextView) findViewById(R.id.buyerPhone);
        grandTotal = (TextView) findViewById(R.id.grandTotal);
        buyerAddress = (TextView) findViewById(R.id.buyerAddress);
        dateField = (TextView) findViewById(R.id.dateField);
        write_notes = (EditText) findViewById(R.id.write_notes);
        paymentMethod = (TextView) findViewById(R.id.paymentMethod);
        courierMethodSpinner = findViewById(R.id.methodSpinner);

        courierMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Datum clickedMethod = (Datum) parent.getItemAtPosition(position);

                courierMethodName = clickedMethod.getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }
}