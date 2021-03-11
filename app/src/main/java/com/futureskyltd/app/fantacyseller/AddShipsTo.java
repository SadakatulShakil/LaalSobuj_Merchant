package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class AddShipsTo extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    public final String TAG = this.getClass().getSimpleName();
    RelativeLayout eelseLay, defcountryLay, everyWhereelsecomp, defcountrycomp, progressLay, nullLay;
    RecyclerView countryList;
    LinearLayoutManager itemManager;
    CountryViewAdapter countryAdapter;
    ArrayList<HashMap<String, Object>> countryAry = new ArrayList<HashMap<String, Object>>();
    ArrayList<HashMap<String, Object>> shipsToList = new ArrayList<HashMap<String, Object>>();
    TextView processBtn, eelseSave, everyWherePrice, defcountryPrice, defcountrylbl, addBtn, resetBtn, screenTitle, defcountrySave;
    ImageView backBtn, clearBtn, appName;
    EditText eelsePrice, searchCountries, defPrice;
    Toolbar toolbar;
    LinearLayout everyWhereelseLay;
    String eelseAmount = "", openPosition = "";
    private String default_country_price = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ships_to);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        everyWhereelsecomp = (RelativeLayout) findViewById(R.id.everyWhereelsecomp);
        defcountrycomp = (RelativeLayout) findViewById(R.id.defcountrycomp);
        everyWhereelseLay = (LinearLayout) findViewById(R.id.everyWhereelseLay);
        defcountryLay = (RelativeLayout) findViewById(R.id.defcountryLay);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        eelseLay = (RelativeLayout) findViewById(R.id.eelseLay);
        processBtn = (TextView) findViewById(R.id.processBtn);
        progressLay = (RelativeLayout) findViewById(R.id.progress);
        countryList = (RecyclerView) findViewById(R.id.countryList);
        eelsePrice = (EditText) findViewById(R.id.eelsePrice);
        defPrice = (EditText) findViewById(R.id.defPrice);
        defcountrylbl = (TextView) findViewById(R.id.defcountrylbl);
        defcountrySave = (TextView) findViewById(R.id.defcountrySave);
        defcountryPrice = (TextView) findViewById(R.id.defcountryPrice);
        addBtn = (TextView) findViewById(R.id.addProduct);
        resetBtn = (TextView) findViewById(R.id.resetProduct);
        searchCountries = (EditText) findViewById(R.id.searchCountries);
        appName = (ImageView) findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        clearBtn = (ImageView) findViewById(R.id.clearBtn);
        eelseSave = (TextView) findViewById(R.id.eelseSave);
        everyWherePrice = (TextView) findViewById(R.id.everyWherePrice);

        resetBtn.setVisibility(GONE);
        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        processBtn.setVisibility(View.GONE);
        resetBtn.setVisibility(GONE);
        everyWhereelseLay.setVisibility(View.GONE);
        progressLay.setVisibility(View.VISIBLE);

        if (getIntent().getStringExtra(Constants.TAG_EVERYWHERE_ELSE) != null)
            eelseAmount = getIntent().getStringExtra(Constants.TAG_EVERYWHERE_ELSE);
        if ((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SHIPSTO_LIST) != null)
            shipsToList = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.SHIPSTO_LIST);

        screenTitle.setText(getString(R.string.ships_to_lbl));
        addBtn.setText(getString(R.string.next_lbl));
        eelsePrice.setText(eelseAmount);

        if (!eelseAmount.equals(""))
            everyWherePrice.setText(GetSet.getsellerCurrencySymbol() + " " + eelseAmount);

        defcountrylbl.setText(GetSet.getSellerCountryName() + " " + getString(R.string.default_lbl));

        eelsePrice.setFilters(new InputFilter[]{new FantacySellerApplication.DecimalDigitsInputFilter(6, 2)});

        countryList.setHasFixedSize(true);
        itemManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        countryList.setLayoutManager(itemManager);

        countryAdapter = new CountryViewAdapter(this, countryAry);
        countryList.setAdapter(countryAdapter);

        getCountry();

        eelseSave.setOnClickListener(this);
        everyWhereelsecomp.setOnClickListener(this);
        defcountrycomp.setOnClickListener(this);
        defcountrySave.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        // listening to search query text change
        searchCountries.addTextChangedListener(this);

    }


    private void getCountry() {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_GET_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "getCountryRes=" + res);
                    JSONObject json = new JSONObject(res);
                    progressLay.setVisibility(View.GONE);
                    everyWhereelseLay.setVisibility(View.VISIBLE);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONArray country = json.getJSONArray(Constants.TAG_COUNTRY);
                        for (int i = 0; i < country.length(); i++) {
                            JSONObject temp = country.getJSONObject(i);
                            if (!isDefaultCountry(DefensiveClass.optString(temp, Constants.TAG_COUNTRY_ID))) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(Constants.TAG_COUNTRY_ID, DefensiveClass.optString(temp, Constants.TAG_COUNTRY_ID));
                                map.put(Constants.TAG_COUNTRY_NAME, DefensiveClass.optString(temp, Constants.TAG_COUNTRY_NAME));
                                map.put(Constants.TAG_SHIPPING_PRICE, "");
                                for (int j = 0; j < shipsToList.size(); j++) {
                                    if (DefensiveClass.optString(temp, Constants.TAG_COUNTRY_ID).equals(shipsToList.get(j).get(Constants.TAG_COUNTRY_ID))) {
                                        map.put(Constants.TAG_COUNTRY_ID, shipsToList.get(j).get(Constants.TAG_COUNTRY_ID));
                                        map.put(Constants.TAG_SHIPPING_PRICE, shipsToList.get(j).get(Constants.TAG_SHIPPING_PRICE));
                                    }
                                }
                                countryAry.add(map);
                            } else {
                                for (int j = 0; j < shipsToList.size(); j++) {
                                    if (DefensiveClass.optString(temp, Constants.TAG_COUNTRY_ID).equals(shipsToList.get(j).get(Constants.TAG_COUNTRY_ID))) {
                                        String defCountryPrice = shipsToList.get(j).get(Constants.TAG_SHIPPING_PRICE).toString();
                                        defcountryPrice.setText(GetSet.getsellerCurrencySymbol() + " " + defCountryPrice);
                                        defPrice.setText(defCountryPrice);
                                    }
                                }
                            }
                        }
                        countryAdapter.notifyDataSetChanged();
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(AddShipsTo.this);
                        } else {
                            showErrorLayout();
                        }
                    }
                } catch (JSONException e) {
                    showErrorLayout();
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    showErrorLayout();
                    e.printStackTrace();
                } catch (Exception e) {
                    showErrorLayout();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorLayout();
                Log.e(TAG, "getCountryError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().getRequestQueue().cancelAll("cat");
        FantacySellerApplication.getInstance().addToRequestQueue(req, "cat");
    }

    private boolean isDefaultCountry(String country_id) {
        return GetSet.getSellerCountryId().equalsIgnoreCase(country_id);
    }

    public class CountryViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
        ArrayList<HashMap<String, Object>> countryList;
        ArrayList<HashMap<String, Object>> countryListFiltered;
        Context context;

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    countryListFiltered = new ArrayList<HashMap<String, Object>>();
                    if (charString.isEmpty()) {
                        countryListFiltered.addAll(countryList);
                    } else {
                        ArrayList<HashMap<String, Object>> filteredList = new ArrayList<>();
                        for (int i = 0; i < countryList.size(); i++) {
                            if (countryList.get(i).get(Constants.TAG_COUNTRY_NAME).toString().toLowerCase().startsWith(charString.toLowerCase())) {
                                HashMap<String, Object> hashMap = new HashMap();
                                hashMap.put(Constants.TAG_COUNTRY_ID, countryList.get(i).get(Constants.TAG_COUNTRY_ID).toString());
                                hashMap.put(Constants.TAG_COUNTRY_NAME, countryList.get(i).get(Constants.TAG_COUNTRY_NAME).toString());
                                hashMap.put(Constants.TAG_SHIPPING_PRICE, countryList.get(i).get(Constants.TAG_SHIPPING_PRICE).toString());
                                filteredList.add(hashMap);
                            }
                        }
                        countryListFiltered.addAll(filteredList);
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = countryListFiltered;
                    filterResults.count = countryListFiltered.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    countryListFiltered = (ArrayList<HashMap<String, Object>>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView countryName, countryPrice, saveBtn;
            EditText shipPrice;
            RelativeLayout shipPriceMainLay, shipPriceLay;
            String countryShippingPrice;

            public MyViewHolder(View view) {
                super(view);
                countryName = (TextView) view.findViewById(R.id.countryName);
                countryPrice = (TextView) view.findViewById(R.id.countryFinalPrice);
                saveBtn = (TextView) view.findViewById(R.id.countryPriceSaveBtn);
                shipPrice = (EditText) view.findViewById(R.id.countryPrice);
                shipPriceMainLay = (RelativeLayout) view.findViewById(R.id.shipPriceMainLay);
                shipPriceLay = (RelativeLayout) view.findViewById(R.id.shipPriceLay);
                shipPriceMainLay.setOnClickListener(this);
                saveBtn.setOnClickListener(this);

                shipPrice.setFilters(new InputFilter[]{new FantacySellerApplication.DecimalDigitsInputFilter(6, 2)});
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.countryPriceSaveBtn:
                        countryShippingPrice = shipPrice.getText().toString();
                        countryPrice.setText(GetSet.getsellerCurrencySymbol() + " " + countryShippingPrice);
                        shipPrice.setText(countryShippingPrice);
                        shipPriceLay.setVisibility(View.GONE);
                        shipsToList.add(updatedMap(countryListFiltered.get(getAdapterPosition()), countryShippingPrice));
                        countryListFiltered.get(getAdapterPosition()).put(Constants.TAG_SHIPPING_PRICE, countryShippingPrice);
                        for (int j = 0; j < countryList.size(); j++) {
                            if (countryListFiltered.get(getAdapterPosition()).get(Constants.TAG_COUNTRY_ID).equals(countryList.get(j).get(Constants.TAG_COUNTRY_ID))) {
                                countryList.get(j).put(Constants.TAG_SHIPPING_PRICE, countryShippingPrice);
                                break;
                            }
                        }
                        FantacySellerApplication.hideSoftKeyboard(AddShipsTo.this, shipPriceLay);
                        openPosition = "";
                        notifyDataSetChanged();
                        break;
                    case R.id.shipPriceMainLay:
                        if (String.valueOf(getAdapterPosition()).equals(openPosition)) {
                            openPosition = "";
                        }
                        openPosition = String.valueOf(getAdapterPosition());
                        notifyDataSetChanged();
                        break;
                }
            }
        }

        public CountryViewAdapter(Context context, ArrayList<HashMap<String, Object>> countryList) {
            this.countryList = countryList;
            this.countryListFiltered = countryList;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_country_price_lay, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            HashMap<String, Object> tmpMap = countryListFiltered.get(position);
            holder.countryName.setText(tmpMap.get(Constants.TAG_COUNTRY_NAME).toString());

            if (String.valueOf(position).equals(openPosition)) {
                holder.shipPriceMainLay.setBackground(getResources().getDrawable(R.drawable.bottom_border));
                holder.shipPriceLay.setVisibility(View.VISIBLE);
            } else {
                holder.shipPriceMainLay.setBackground(null);
                holder.shipPriceLay.setVisibility(View.GONE);
            }

            if (!tmpMap.get(Constants.TAG_SHIPPING_PRICE).toString().trim().equals("")) {
                holder.shipPrice.setText(tmpMap.get(Constants.TAG_SHIPPING_PRICE).toString());
                holder.countryPrice.setText(GetSet.getsellerCurrencySymbol() + " " + tmpMap.get(Constants.TAG_SHIPPING_PRICE));
            } else {
                holder.shipPrice.setText("");
                holder.countryPrice.setText("");
            }
            if (defcountryLay.getVisibility() == View.VISIBLE)
                defcountryLay.setVisibility(GONE);
            if (eelseLay.getVisibility() == View.VISIBLE)
                eelseLay.setVisibility(GONE);
        }

        @Override
        public int getItemCount() {
            return countryListFiltered.size();
        }
    }

    private HashMap<String, Object> updatedMap(HashMap<String, Object> hashMap, String countryShippingPrice) {
        HashMap<String, Object> resultedMap = new HashMap<>();
        resultedMap.put(Constants.TAG_COUNTRY_ID, hashMap.get(Constants.TAG_COUNTRY_ID));
        resultedMap.put(Constants.TAG_SHIPPING_PRICE, countryShippingPrice);
        return resultedMap;
    }

    private void showErrorLayout() {
        progressLay.setVisibility(View.GONE);
        everyWhereelseLay.setVisibility(View.GONE);
        if (countryAry.size() == 0) {
            nullLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        getIntent().putExtra(Constants.TAG_EVERYWHERE_ELSE, eelseAmount);
        getIntent().putExtra(Constants.SHIPSTO_LIST, shipsToList);
        this.setResult(RESULT_OK, getIntent());
        super.onBackPressed();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        clearBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onTextChanged(CharSequence query, int start, int before, int count) {
        if (count != 0) {
            clearBtn.setVisibility(View.VISIBLE);
        } else {
            clearBtn.setVisibility(View.INVISIBLE);
        }
        countryAdapter.getFilter().filter(query);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.everyWhereelsecomp:
                if (eelseLay.getVisibility() == GONE) {
                    if (defcountryLay.getVisibility() == View.VISIBLE)
                        defcountryLay.setVisibility(GONE);
                    eelseLay.setVisibility(View.VISIBLE);
                } else {
                    eelseLay.setVisibility(View.GONE);
                }
                break;

            case R.id.defcountrycomp:
                if (defcountryLay.getVisibility() == GONE) {
                    if (eelseLay.getVisibility() == View.VISIBLE)
                        eelseLay.setVisibility(GONE);
                    defcountryLay.setVisibility(View.VISIBLE);
                } else {
                    defcountryLay.setVisibility(View.GONE);
                }
                break;

            case R.id.eelseSave:
                String tmp = eelsePrice.getText().toString();
                if (!tmp.equals(""))
                    everyWherePrice.setText(GetSet.getsellerCurrencySymbol() + " " + tmp);
                else
                    everyWherePrice.setText("");
                eelseLay.setVisibility(View.GONE);
                eelseAmount = tmp;
                break;
            case R.id.defcountrySave:
                default_country_price = defPrice.getText().toString();
                if (!default_country_price.equals("")) {
                    boolean addNow = false;
                    defcountryPrice.setText(GetSet.getsellerCurrencySymbol() + " " + default_country_price);
                    if (shipsToList.size() == 0) {
                        addNow = true;
                    } else {
                        for (int i = 0; i < shipsToList.size(); i++) {
                            if (isDefaultCountry(String.valueOf(shipsToList.get(i).get(Constants.TAG_COUNTRY_ID)))) {
                                shipsToList.get(i).put(Constants.TAG_SHIPPING_PRICE, default_country_price);
                                break;
                            } else {
                                addNow = true;
                            }
                        }
                    }

                    if (addNow) {
                        HashMap<String, Object> resultedMap = new HashMap<>();
                        resultedMap.put(Constants.TAG_COUNTRY_ID, GetSet.getSellerCountryId());
                        resultedMap.put(Constants.TAG_COUNTRY_NAME, GetSet.getSellerCountryName());
                        resultedMap.put(Constants.TAG_SHIPPING_PRICE, default_country_price);
                        shipsToList.add(resultedMap);
                    }
                } else {
                    defcountryPrice.setText("");
                    for (int i = 0; i < shipsToList.size(); i++) {
                        if (isDefaultCountry(String.valueOf(shipsToList.get(i).get(Constants.TAG_COUNTRY_ID)))) {
                            shipsToList.remove(i);
                            break;
                        }
                    }
                }
                defcountryLay.setVisibility(View.GONE);
                break;
            case R.id.backBtn:
                onBackPressed();
                break;
            case R.id.clearBtn:
                searchCountries.setText("");
                clearBtn.setVisibility(View.GONE);
                break;
            case R.id.addProduct:
                default_country_price = defPrice.getText().toString();
                FantacySellerApplication.hideSoftKeyboard(AddShipsTo.this, addBtn);
                if (default_country_price.trim().equalsIgnoreCase("") || default_country_price.trim().equalsIgnoreCase("0")) {
                    FantacySellerApplication.showToast(AddShipsTo.this, getString(R.string.default_country_price_reqd), Toast.LENGTH_LONG);
                } else {
                    onBackPressed();
                }
                break;
        }
    }
}
