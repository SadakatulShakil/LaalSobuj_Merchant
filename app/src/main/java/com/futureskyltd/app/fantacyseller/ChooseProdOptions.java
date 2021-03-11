package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class ChooseProdOptions extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();
    ListView listView;
    String from = "";
    RelativeLayout progressLay, nullLay;
    ImageView nullImage, backBtn, appName;
    TextView nullText, addBtn, resetBtn, screenTitle;
    ArrayList<HashMap<String, String>> shippingTimeAry = new ArrayList<HashMap<String, String>>();
    LinearLayout addLay;
    ArrayList<String> colorsList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> selectedColorLists = new ArrayList<>();
    ChooseOptionAdapter chooseOptionAdapter;
    String colorMethod = "auto";
    String selectedTimeDuration = "";
    private String color_mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_prod_options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        nullText = (TextView) findViewById(R.id.nullText);
        addLay = (LinearLayout) findViewById(R.id.addLay);
        progressLay = (RelativeLayout) findViewById(R.id.progress);
        appName = (ImageView) findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        addBtn = (TextView) findViewById(R.id.addProduct);
        resetBtn = (TextView) findViewById(R.id.resetProduct);

        screenTitle.setText(getString(R.string.add_product));
        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        nullLay.setVisibility(View.GONE);
        progressLay.setVisibility(View.VISIBLE);
        addLay.setVisibility(GONE);
        shippingTimeAry.clear();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        from = getIntent().getStringExtra(Constants.FROM);

        if (from.equals("shippingTime")) {
            selectedTimeDuration = getIntent().getStringExtra(Constants.SELECTED_TIME_DURATION);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            screenTitle.setText(getString(R.string.shipping_time_lbl));
            getShippingTime();
            addLay.setVisibility(GONE);

        } else if (from.equals("colors")) {
            if ((ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.COLOR_LIST) != null)
                selectedColorLists = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(Constants.COLOR_LIST);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            screenTitle.setText(getString(R.string.select_color));
            getColors();
            addLay.setVisibility(View.VISIBLE);
            addBtn.setText(getString(R.string.save_btn_lbl));
            resetBtn.setVisibility(GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedColorLists.isEmpty()) {
                    colorMethod = "manual";
                    onBackPressed();
                } else {
                    FantacySellerApplication.showToast(ChooseProdOptions.this, getString(R.string.reqd_color), Toast.LENGTH_LONG);
                }
            }
        });
        color_mode = getIntent().getStringExtra(Constants.TAG_COLOR_MODE);
        setAdapter();
    }

    private void getShippingTime() {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_GET_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "getShippingTimeRes=" + res);
                    JSONObject json = new JSONObject(res);
                    progressLay.setVisibility(View.GONE);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONArray shippingTime = json.getJSONArray(Constants.TAG_SHIPPING_DELIVERY_TIME);
                        for (int i = 0; i < shippingTime.length(); i++) {
                            JSONObject temp = shippingTime.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Constants.TAG_ID, DefensiveClass.optString(temp, Constants.TAG_ID));
                            map.put(Constants.TAG_TIME, DefensiveClass.optString(temp, Constants.TAG_TIME));
                            shippingTimeAry.add(map);
                            chooseOptionAdapter.notifyDataSetChanged();
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(ChooseProdOptions.this);
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
                Log.e(TAG, "getShippingTimeError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                Log.i(TAG, "getShippingTimeParams: "+map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().getRequestQueue().cancelAll("tag");
        FantacySellerApplication.getInstance().addToRequestQueue(req, "cat");
    }

    private void getColors() {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_GET_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "getColorsRes=" + res);
                    JSONObject json = new JSONObject(res);
                    progressLay.setVisibility(View.GONE);
                    String status = DefensiveClass.optString(json, Constants.TAG_STATUS);
                    if (status.equalsIgnoreCase("true")) {
                        JSONArray color = json.getJSONArray(Constants.TAG_COLOR);
                        colorsList.add(getString(R.string.auto_detect));
                        for (int i = 0; i < color.length(); i++) {
                            JSONObject temp = color.getJSONObject(i);
                            String colorName = DefensiveClass.optString(temp, Constants.TAG_NAME);
                            colorsList.add(colorName);
                            chooseOptionAdapter.notifyDataSetChanged();
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(ChooseProdOptions.this);
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
                Log.e(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                Log.i(TAG, "getColorsParams: " + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().getRequestQueue().cancelAll("tag");
        FantacySellerApplication.getInstance().addToRequestQueue(req, "cat");
    }

    private void setAdapter() {
        if (from.equals("shippingTime")) {
            chooseOptionAdapter = new ChooseOptionAdapter(this, shippingTimeAry);
            listView.setAdapter(chooseOptionAdapter);
        } else {
            chooseOptionAdapter = new ChooseOptionAdapter(colorsList, this);
            listView.setAdapter(chooseOptionAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (from.equals("shippingTime")) {
                    selectedTimeDuration = shippingTimeAry.get(position).get(Constants.TAG_TIME);
                    view.findViewById(R.id.icon).setVisibility(View.VISIBLE);
                    chooseOptionAdapter.notifyDataSetChanged();
                    onBackPressed();
                } else {
                    TextView textView = (TextView) view.findViewById(R.id.name);
                    if (textView.getText().equals(getString(R.string.auto_detect))) {
                        colorMethod = "auto";
                        if (selectedColorLists != null)
                            selectedColorLists.clear();
                        onBackPressed();
                    } else {
                        colorMethod = "manual";
                        color_mode = "nocolor";
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constants.TAG_COLOR_NAME, colorsList.get(position));
                        if (selectedColorLists.contains(hashMap)) {
                            selectedColorLists.remove(hashMap);
                            chooseOptionAdapter.notifyDataSetChanged();
                        } else {
                            selectedColorLists.add(hashMap);
                            chooseOptionAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
        listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.divider)));
        listView.setDividerHeight(FantacySellerApplication.dpToPx(this, 1));
    }

    public class ChooseOptionAdapter extends BaseAdapter {
        private Context context;
        ArrayList<HashMap<String, String>> shipAry;
        ArrayList<String> colorAry = new ArrayList<>();
        ViewHolder viewHolder;

        public ChooseOptionAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
            this.context = context;
            this.shipAry = arrayList;
        }

        public ChooseOptionAdapter(ArrayList<String> colorAry, Context context) {
            this.context = context;
            this.colorAry = colorAry;
        }

        private class ViewHolder {
            ImageView icon;
            TextView name;
            RelativeLayout mainLay;

            ViewHolder(View convertView) {
                name = (TextView) convertView.findViewById(R.id.name);
                icon = (ImageView) convertView.findViewById(R.id.icon);
                mainLay = (RelativeLayout) convertView.findViewById(R.id.mainLay);
            }
        }

        @Override
        public int getCount() {
            if (from.equals("shippingTime")) {
                return shippingTimeAry.size();
            } else {
                return colorAry.size();
            }
        }

        @Override
        public String getItem(int position) {
            return shippingTimeAry.get(position).get(Constants.TAG_COLOR_NAME);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.prod_option_first_row, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (from.equals("shippingTime")) {
                HashMap<String, String> tempMap = shipAry.get(position);
                viewHolder.name.setText(tempMap.get(Constants.TAG_TIME));

                if (tempMap.get(Constants.TAG_TIME).equals(selectedTimeDuration)) {
                    viewHolder.icon.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.icon.setVisibility(View.INVISIBLE);
                }
            } else {
                viewHolder.name.setText(colorAry.get(position));
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.TAG_COLOR_NAME, colorAry.get(position));
                if ((color_mode.trim().equalsIgnoreCase(context.getString(R.string.auto))) || (selectedColorLists.contains(hashMap))) {
                    color_mode = "nocolor";
                    viewHolder.icon.setVisibility(View.VISIBLE);
                } else
                    viewHolder.icon.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    private void showErrorLayout() {
        progressLay.setVisibility(View.GONE);
        if (shippingTimeAry.size() == 0) {
            nullLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra(Constants.FROM).equals("colors")) {
            getIntent().putExtra(Constants.FROM, "colors");
            getIntent().putExtra(Constants.COLOR_LIST, selectedColorLists);
            getIntent().putExtra(Constants.TAG_COLOR_METHOD, colorMethod);
            this.setResult(RESULT_OK, getIntent());
            super.onBackPressed();
        } else if (getIntent().getStringExtra(Constants.FROM).equals("shippingTime")) {
            getIntent().putExtra(Constants.FROM, "shippingTime");
            getIntent().putExtra(Constants.SELECTED_TIME_DURATION, selectedTimeDuration);
            this.setResult(RESULT_OK, getIntent());
            super.onBackPressed();
        }
    }
}