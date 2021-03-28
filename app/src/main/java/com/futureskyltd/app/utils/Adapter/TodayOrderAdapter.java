package com.futureskyltd.app.utils.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futureskyltd.app.fantacyseller.OrderDetailsActivity;
import com.futureskyltd.app.fantacyseller.R;
import com.futureskyltd.app.utils.TodayNewOrder.Datum;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TodayOrderAdapter extends RecyclerView.Adapter<TodayOrderAdapter.viewHolder> {
    private Context context;
    private ArrayList<Datum> todayOrderArrayList;

    public TodayOrderAdapter(Context context, ArrayList<Datum> todayOrderArrayList) {
        this.context = context;
        this.todayOrderArrayList = todayOrderArrayList;
    }

    @NonNull
    @Override
    public TodayOrderAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_order_view, parent, false);
        return new TodayOrderAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayOrderAdapter.viewHolder holder, int position) {
        final Datum resultInfo = todayOrderArrayList.get(position);
        if(resultInfo.getStatus().equals("Pending")){
            holder.orderStatus.setText(context.getString(R.string.pending_status));
        }else if(resultInfo.getStatus().equals("Processing")){
            holder.orderStatus.setText("কনফার্ম হয়েছে");
        }else if(resultInfo.getStatus().equals("Shipped")){
            holder.orderStatus.setText(context.getString(R.string.shipped_status));
        }else if(resultInfo.getStatus().equals("Delivered")){
            holder.orderStatus.setText(context.getString(R.string.delivered_status));
        }
       // holder.orderStatus.setText("Order status: "+ resultInfo.getStatus());
        int oId = resultInfo.getOrderid();
        int tCost = resultInfo.getTotalcost();
        String orId = String.valueOf(oId);
        String totCost = String.valueOf(tCost);
        holder.orderId.setText("Order id: "+orId);
        holder.totalCost.setText("Total cost: "+totCost);
        holder.orderDate.setText("Order date: "+resultInfo.getOrderdate());
        holder.deliveryType.setText("Delivery type: "+resultInfo.getDeliverytype());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId", String.valueOf(resultInfo.getOrderid()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todayOrderArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView orderStatus, orderId, orderDate, totalCost, deliveryType;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            totalCost = itemView.findViewById(R.id.totalCost);
            deliveryType = itemView.findViewById(R.id.deliveryType);
        }
    }
}
