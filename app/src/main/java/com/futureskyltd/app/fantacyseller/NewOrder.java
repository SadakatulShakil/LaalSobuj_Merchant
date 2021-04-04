package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.external.RecyclerOnScrollListener;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewOrder extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();
    RecyclerView recyclerView;
    RelativeLayout progress, nullLay;
    SwipeRefreshLayout swipeRefreshLayout;
    NewProductAdapter newProductAdapter;
    ArrayList<HashMap<String, String>> newProductList = new ArrayList<>();
    ImageView back, appName, nullImage;
    TextView title, nullText;
    LinearLayoutManager linearLayoutManager;
    RecyclerOnScrollListener mScrollListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress = (RelativeLayout) findViewById(R.id.progress);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        nullText = (TextView) findViewById(R.id.nullText);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);

        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);

        title.setText(getResources().getText(R.string.new_orders));
        nullText.setText(getString(R.string.no_orders_found));
        nullImage.setImageDrawable(getResources().getDrawable(R.drawable.neworders_icon));
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        newProductAdapter = new NewProductAdapter(newProductList, NewOrder.this);
        recyclerView.setAdapter(newProductAdapter);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.progresscolor));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.resetpagecount();
                getData(0);

            }
        });
        mScrollListener = new RecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    getData(current_page * Constants.OVERALL_LIMIT);

                }
            }
        };
        back.setOnClickListener(this);
        recyclerView.addOnScrollListener(mScrollListener);

        getData(0);
    }

    private void getData(final int offset) {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.API_NEW_ORDERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    swipeRefreshLayout.setEnabled(true);
                    if (newProductList.size() >= Constants.OVERALL_LIMIT && newProductList.get(newProductList.size() - 1) == null) {
                        newProductList.remove(newProductList.size() - 1);
                        newProductAdapter.notifyItemRemoved(newProductList.size());
                    }
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        newProductList.clear();
                        swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
                    }
                    JSONObject json = new JSONObject(response);
                    Log.i(TAG, "getDataRes= " + json);
                    swipeRefreshLayout.setEnabled(true);

                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONArray result = json.getJSONArray(Constants.TAG_ORDERS);
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject orders = result.getJSONObject(i);
                            HashMap<String, String> orderHash = new HashMap<>();
                            orderHash.put(Constants.TAG_ORDER_ID, orders.getString(Constants.TAG_ORDER_ID));
                            orderHash.put(Constants.TAG_ITEM_NAME, orders.getString(Constants.TAG_ITEM_NAME));
                            orderHash.put(Constants.TAG_ITEM_IMAGE, orders.getString(Constants.TAG_ITEM_IMAGE));
                            orderHash.put(Constants.TAG_ORDERS_DATE, orders.getString(Constants.TAG_ORDERS_DATE));
                            orderHash.put(Constants.TAG_STATUS, orders.getString(Constants.TAG_STATUS));
                            newProductList.add(orderHash);
                        }
                        if (mScrollListener != null && newProductList.size() % Constants.OVERALL_LIMIT == 0) {
                            mScrollListener.setLoading(false);
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(NewOrder.this);
                        }
                    }
                    if (newProductList.size() == 0) {
                        setErrorLayout();
                    } else {
                        nullLay.setVisibility(View.GONE);
                    }
                    newProductAdapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setErrorLayout();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newProductList.size() >= Constants.OVERALL_LIMIT) {
                            newProductList.add(null);
                            newProductAdapter.notifyItemInserted(newProductList.size() - 1);
                        } else if (newProductList.size() == 0 && !swipeRefreshLayout.isRefreshing()) {
                            progress.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setEnabled(false);
                        }
                        if (mScrollListener != null) {
                            mScrollListener.setLoading(true);
                        }
                    }
                });
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                map.put(Constants.TAG_SEARCH_KEY, "");
                map.put(Constants.TAG_LIMIT, Integer.toString(Constants.OVERALL_LIMIT));
                map.put(Constants.TAG_OFFSET, Integer.toString(offset));
                Log.i(TAG, "getDataParams: " + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(request, "New Order");

    }

    private void setErrorLayout() {
        if (newProductList.size() == 0) {
            progress.setVisibility(View.GONE);
            nullLay.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
    }

    private class NewProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
        ArrayList<HashMap<String, String>> orderList;
        Context context;

        public NewProductAdapter(ArrayList<HashMap<String, String>> items, Context context) {
            orderList = items;
            this.context = context;
        }

        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_order_item, parent, false));
            } else if (viewType == VIEW_TYPE_LOADING) {
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return orderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.newOrderName.setText(orderList.get(position).get(Constants.TAG_ITEM_NAME));
                switch (orderList.get(position).get(Constants.TAG_STATUS)) {
                    case "Pending":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.pending_status));
                        break;
                    case "Publish":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.pending_status));
                        break;
                    case "New":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.new_status));
                        break;
                    case "Delivered":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.delivered_status));
                        break;
                    case "Cancelled":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.cancelled_status));
                        break;
                    case "Shipped":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.shipped_status));
                        break;
                    case "Refunded":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.refunded_status));
                        break;
                    case "Paid":
                        ((ViewHolder) holder).orderStatus.setText(getString(R.string.paid_status));
                        break;
                    case "Processing":
                        ((ViewHolder) holder).orderStatus.setText("কনফার্ম হয়েছে");
                        break;
                    default:
                        ((ViewHolder) holder).orderStatus.setText(orderList.get(position).get(Constants.TAG_STATUS));
                        break;
                }
                if (!orderList.get(position).get(Constants.TAG_ORDERS_DATE).equals(null) && !orderList.get(position).get(Constants.TAG_ORDERS_DATE).equals("")) {
                    viewHolder.newOrderDate.setText(FantacySellerApplication.getDate(orderList.get(position).get(Constants.TAG_ORDERS_DATE)));
                }
                Picasso.with(context).load(orderList.get(position).get(Constants.TAG_ITEM_IMAGE)).placeholder(R.drawable.app_name).into(viewHolder.newOrderImage);
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, orderList.get(position).get(Constants.TAG_ORDER_ID)+"  Order clicked !", Toast.LENGTH_SHORT).show();
                    Intent detailsIntent = new Intent(NewOrder.this, OrderDetailsActivity.class);
                    detailsIntent.putExtra("orderId", orderList.get(position).get(Constants.TAG_ORDER_ID));
                    startActivity(detailsIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView orderStatus, newOrderName, newOrderDate;
            ImageView newOrderImage;

            public ViewHolder(View itemView) {
                super(itemView);
                orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);
                newOrderName = (TextView) itemView.findViewById(R.id.newOrderName);
                newOrderDate = (TextView) itemView.findViewById(R.id.newOrderDate);
                newOrderImage = (ImageView) itemView.findViewById(R.id.newOrderImage);
            }
        }
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
