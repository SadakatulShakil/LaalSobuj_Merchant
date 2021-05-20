package com.futureskyltd.app.fantacyseller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.futureskyltd.app.utils.Adapter.UnitAdapter;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.Unit;

import java.util.ArrayList;
import java.util.HashMap;


public class AddSizeProperty extends AppCompatActivity implements View.OnClickListener {
    EditText addProperty, addUnits, addPrice;
    RecyclerView recyclerView;
    TextView varientPrice, addVarientPrice, title, addBtn, resetBtn, sellPercentagePrice;
    RecyclerAdapter recyclerAdapter;
    ArrayList<HashMap<String, Object>> propertyList = new ArrayList<>();
    ImageView backBtn, appname;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Unit> mUnitList;
    private UnitAdapter mUnitAdapter;
    private Spinner unitSpinner;
    private String selectedMz = "unSelected";
    private String unitName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        inItUnitList();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        addProperty = (EditText) findViewById(R.id.addProperty);
        addUnits = (EditText) findViewById(R.id.addUnits);
        addPrice = (EditText) findViewById(R.id.addPrice);
        varientPrice = (TextView) findViewById(R.id.varientPrice);
        addBtn = (TextView) findViewById(R.id.addProduct);
        resetBtn = (TextView) findViewById(R.id.resetProduct);
        addVarientPrice = (TextView) findViewById(R.id.addVarientPrice);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        appname = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        sellPercentagePrice = findViewById(R.id.sellPercentagePrice);

        unitSpinner = findViewById(R.id.unitSpinner);
        mUnitAdapter = new UnitAdapter(AddSizeProperty.this, mUnitList);
        unitSpinner.setAdapter(mUnitAdapter);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position<1){
                    selectedMz = "unSelected";
                }else{
                    selectedMz = "selected";
                    Unit clickedUnit = (Unit) parent.getItemAtPosition(position);

                    unitName = clickedUnit.getUnit();
                    //Toast.makeText(PostProduct.this, unitName + " is selected !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addProperty.setFilters(new InputFilter[]{FantacySellerApplication.EMOJI_FILTER});
        appname.setVisibility(View.INVISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        resetBtn.setVisibility(View.GONE);

        title.setText(getResources().getText(R.string.add_property));
        addBtn.setText(getString(R.string.next_lbl));

        addPrice.setFilters(new InputFilter[]{new FantacySellerApplication.DecimalDigitsInputFilter(6, 2)});

        if ((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SIZE_LIST) != null)
            propertyList = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SIZE_LIST);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(propertyList, this);
        recyclerView.setAdapter(recyclerAdapter);

        addVarientPrice.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pPrice = addPrice.getText().toString().trim();
                if (pPrice.isEmpty()) {
                    sellPercentagePrice.setText("৳ "+0.0);
                }else {
                    double bPrice = Double.parseDouble(pPrice);
                    double result = Math.floor(bPrice+((7.5*bPrice)/100));
                    if(result % 5 != 0){
                        result= ((Math.floor(result/5))*5)+5;
                    }
                    String sellPrice = String.valueOf(result);
                    sellPercentagePrice.setText("৳ "+sellPrice);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        addPrice.addTextChangedListener(textWatcher);
    }

    private void inItUnitList() {
        mUnitList = new ArrayList<>();
        mUnitList.add(new Unit("একক বাছাই করুন"));
        mUnitList.add(new Unit("টি / পিছ"));
        mUnitList.add(new Unit("কেজি"));
        mUnitList.add(new Unit("গ্রাম"));
        mUnitList.add(new Unit("লিটার"));
        mUnitList.add(new Unit("মি.লি"));
        mUnitList.add(new Unit("ইঞ্চি"));
        mUnitList.add(new Unit("প্যাকেট / বাক্স"));
    }

    private void setAdapter(String size, String unit, String price) {
        HashMap<String, Object> property = new HashMap<>();
        property.put(Constants.TAG_SIZE, size);
        property.put(Constants.TAG_UNIT, unit);
        property.put(Constants.TAG_PRICE, price);
        propertyList.add(property);
        recyclerAdapter.notifyDataSetChanged();
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
        ArrayList<HashMap<String, Object>> sizeList;
        Context context;

        public RecyclerAdapter(ArrayList<HashMap<String, Object>> items, Context context) {
            sizeList = items;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_property_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public int getItemViewType(int position) {
            return sizeList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.addPropertyItem.setText("সাইজ: "+sizeList.get(position).get(Constants.TAG_SIZE).toString());
            viewHolder.addUnitsItem.setText("পণ্যের মজুদ পরিমান: "+sizeList.get(position).get(Constants.TAG_UNIT).toString());
            viewHolder.addPriceItem.setText("বিক্রয় মূল্য: ৳ "+sizeList.get(position).get(Constants.TAG_PRICE).toString());
            String buy_price = sizeList.get(position).get(Constants.TAG_PRICE).toString();
            double bPrice = Double.parseDouble(buy_price);
            double result = Math.floor(bPrice+((7.5*bPrice)/100));
            if(result % 5 != 0){
                result= ((Math.floor(result/5))*5)+5;
            }
            String sellPrice = String.valueOf(result);
            viewHolder.sellPrice.setText("কমিশন সহ বিক্রয় মূল্য: ৳ "+sellPrice);
            viewHolder.addClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sizeList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, sizeList.size());
                }
            });
        }

        @Override
        public int getItemCount() {
            return sizeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView addPropertyItem, addUnitsItem, addPriceItem, sellPrice;
            ImageView addClose;

            public ViewHolder(View itemView) {
                super(itemView);
                addPropertyItem = (TextView) itemView.findViewById(R.id.addPropertyItem);
                addUnitsItem = (TextView) itemView.findViewById(R.id.addUnitsItem);
                addPriceItem = (TextView) itemView.findViewById(R.id.addPriceItem);
                sellPrice = itemView.findViewById(R.id.sellingPriceItem);
                addClose = (ImageView) itemView.findViewById(R.id.addClose);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!propertyList.isEmpty()) {
            getIntent().putExtra(Constants.IS_SIZE_ENABLE, "true");
            getIntent().putExtra(Constants.SIZE_LIST, propertyList);
            getIntent().putExtra("unitName", unitName);
            getIntent().putExtra("unitIsSelect", selectedMz);
        } else {
            getIntent().putExtra(Constants.IS_SIZE_ENABLE, "false");
            getIntent().putExtra(Constants.SIZE_LIST, propertyList);
        }

        this.setResult(RESULT_OK, getIntent());
        super.onBackPressed();
    }

    public void defaultDialog(final Activity context, final String message) {
        Display display;
        display = context.getWindowManager().getDefaultDisplay();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.default_popup);
        dialog.getWindow().setLayout(display.getWidth() * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView yes = (TextView) dialog.findViewById(R.id.yes);
        TextView no = (TextView) dialog.findViewById(R.id.no);
        title.setText(message);
        no.setVisibility(View.VISIBLE);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addVarientPrice:
                if (addProperty.getText().toString().isEmpty() || addUnits.getText().toString().isEmpty() || addPrice.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.all_fields_reqd), Toast.LENGTH_SHORT).show();
                } else if (addProperty.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.size_fields_reqd), Toast.LENGTH_SHORT).show();
                } else if (addUnits.getText().toString().isEmpty() || addUnits.getText().toString().trim().equalsIgnoreCase("0")) {
                    FantacySellerApplication.showToast(getApplicationContext(), getString(R.string.unit_fields_reqd), Toast.LENGTH_SHORT);
                } else if (addPrice.getText().toString().isEmpty() || addPrice.getText().toString().trim().equalsIgnoreCase("0")) {
                    FantacySellerApplication.showToast(getApplicationContext(), getString(R.string.price_fields_reqd), Toast.LENGTH_SHORT);
                }else if(selectedMz.equals("unSelected")){
                    FantacySellerApplication.showToast(AddSizeProperty.this, "একক বাছাই করুন", Toast.LENGTH_LONG);
                } else {
                    FantacySellerApplication.hideSoftKeyboard(AddSizeProperty.this, v);
                    setAdapter(addProperty.getText().toString(), addUnits.getText().toString(), addPrice.getText().toString());
                    addProperty.setText("");
                    addUnits.setText("");
                    addPrice.setText("");
                }
                break;
            case R.id.addProduct:
                if (propertyList.size() == 0) {
                    if (addProperty.getText().toString().isEmpty() || addUnits.getText().toString().isEmpty() || addPrice.getText().toString().isEmpty()) {
                        FantacySellerApplication.hideSoftKeyboard(AddSizeProperty.this, addProperty);
                        Toast.makeText(getApplicationContext(), getString(R.string.no_available_variants), Toast.LENGTH_SHORT).show();
                    }else if(selectedMz.equals("unSelected")){
                        FantacySellerApplication.showToast(AddSizeProperty.this, "একক বাছাই করুন", Toast.LENGTH_LONG);
                    } else {
                        onBackPressed();
                    }
                } else {
                    onBackPressed();
                }
                break;
            case R.id.backBtn:
                if (addProperty.getText().toString().trim().length() != 0 || addUnits.getText().toString().trim().length() != 0 ||
                        addPrice.getText().toString().trim().length() != 0) {
                    defaultDialog(AddSizeProperty.this, getString(R.string.confirm_clear_message));
                } else {
                    onBackPressed();
                }
                break;
        }
    }
}
