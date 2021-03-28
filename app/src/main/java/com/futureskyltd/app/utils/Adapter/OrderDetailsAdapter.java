package com.futureskyltd.app.utils.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futureskyltd.app.fantacyseller.R;
import com.futureskyltd.app.utils.OrderDetails.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.viewHolder> {
    private Context context;
    private ArrayList<Item> itemArrayList;

    public OrderDetailsAdapter(Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_view, parent, false);
        return new OrderDetailsAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.viewHolder holder, int position) {
        Item itemInfo = itemArrayList.get(position);
        int iId = itemInfo.getItemId();
        String Id = String.valueOf(iId);
        int qnt = itemInfo.getQuantity();
        String qnty = String.valueOf(qnt);
        holder.itemId.setText("Item id: "+getENtoBN(Id));
        holder.itemName.setText("Item name: "+itemInfo.getItemName());
        holder.quantity.setText("Quantity: "+getENtoBN(qnty));
        holder.size.setText("Size: "+getENtoBN(itemInfo.getSize()));
        holder.price.setText("Item Price: ৳ "+getENtoBN(itemInfo.getPrice()));

        Picasso.with(context).load(itemInfo.getItemImage()).into(holder.itemImage);
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

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView itemId, itemName, quantity, size, price;
        private ImageView itemImage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            itemId = itemView.findViewById(R.id.itemId);
            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
            itemImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
