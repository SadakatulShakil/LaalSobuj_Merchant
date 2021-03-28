package com.futureskyltd.app.utils.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futureskyltd.app.fantacyseller.OrderDetailsActivity;
import com.futureskyltd.app.fantacyseller.R;
import com.futureskyltd.app.utils.MyOrder.Order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.viewHolder> {
    private Context context;
    private ArrayList<Order> myOrderArrayList;

    public MyOrderAdapter(Context context, ArrayList<Order> myOrderArrayList) {
        this.context = context;
        this.myOrderArrayList = myOrderArrayList;
    }

    @NonNull
    @Override
    public MyOrderAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_view, parent, false);
        return new MyOrderAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.viewHolder holder, int position) {
        final Order orderInfo = myOrderArrayList.get(position);
        if(orderInfo.getStatus().equals("Pending")){
            holder.orderStatus.setText(context.getString(R.string.pending_status));
        }else if(orderInfo.getStatus().equals("Processing")){
            holder.orderStatus.setText("কনফার্ম হয়েছে");
        }else if(orderInfo.getStatus().equals("Shipped")){
            holder.orderStatus.setText(context.getString(R.string.shipped_status));
        }else if(orderInfo.getStatus().equals("Delivered")){
            holder.orderStatus.setText(context.getString(R.string.delivered_status));
        }
        holder.orderDate.setText("Order date: "+getENtoBN(getDate(orderInfo.getOrderDate())));
        int oId = orderInfo.getOrderId();
        String orderId = String.valueOf(oId);
        holder.orderId.setText("Order id: #০০০০০০"+getENtoBN(orderId));
        holder.totalCost.setText("Total cost: ৳ "+getENtoBN(orderInfo.getTotalcost()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId", String.valueOf(orderInfo.getOrderId()));
                context.startActivity(intent);
            }
        });
    }


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


    private String getDate(Integer orderDate) {
            try {
                Long timeStamp = Long.valueOf(orderDate);
                DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date netDate = (new Date(timeStamp * 1000L));
                return sdf.format(netDate);
            } catch (Exception ex) {
                return "";
            }
    }

    @Override
    public int getItemCount() {
        return myOrderArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView orderStatus, orderId, orderDate, totalCost;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            totalCost = itemView.findViewById(R.id.totalCost);
        }
    }
}
