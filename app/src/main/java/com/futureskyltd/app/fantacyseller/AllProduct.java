package com.futureskyltd.app.fantacyseller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class AllProduct extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener, TextWatcher, View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();
    ProductAdapter productAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<HashMap<String, String>> productList = new ArrayList<>();
    RelativeLayout progress, nullLay;
    ImageView nullImage, clearBtn, backBtn, appName;
    TextView nullText, screenTitle;
    Display display;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerOnScrollListener mScrollListener = null;
    EditText searchView;
    String searchKey = "";
    static boolean isCleared;
    public static boolean isEditMode = false;
    AppBarLayout.LayoutParams params;
    DividerItemDecoration itemDivider;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress = (RelativeLayout) findViewById(R.id.progress);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        nullText = (TextView) findViewById(R.id.nullText);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        searchView = (EditText) findViewById(R.id.searchView);
        clearBtn = (ImageView) findViewById(R.id.clearBtn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        appName = findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);

        screenTitle.setText(getString(R.string.products));
        searchView.setText("");

        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        itemDivider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        itemDivider.setDrawable(getResources().getDrawable(R.drawable.emptyspace));
        recyclerView.addItemDecoration(itemDivider);
        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(productAdapter);

        display = this.getWindowManager().getDefaultDisplay();
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.progresscolor));

        swipeRefreshLayout.setOnRefreshListener(this);
        searchView.setOnEditorActionListener(this);
        searchView.addTextChangedListener(this);
        clearBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        mScrollListener = new RecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Log.i("mScrollListener", String.valueOf(mScrollListener));
                    getProducts(current_page * Constants.OVERALL_LIMIT, searchView.getText().toString());
                    Log.v("offset:", "On offset" + (Constants.OVERALL_LIMIT * current_page));
                }
            }
        };
        recyclerView.addOnScrollListener(mScrollListener);

        getProducts(0, searchView.getText().toString());
    }

    private void getProducts(final int offset, final String search_key) {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.d(TAG, "getProductsRes=" + res + "," + offset);
                    JSONObject json = new JSONObject(res);
                    progress.setVisibility(View.GONE);
                    swipeRefreshLayout.setEnabled(true);
                    if (productList.size() >= Constants.OVERALL_LIMIT && productList.get(productList.size() - 1) == null) {
                        productList.remove(productList.size() - 1);
                        productAdapter.notifyItemRemoved(productList.size());
                    }
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
                    }
                    if (isCleared) {
                        isCleared = false;
                        productList.clear();
                    }
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONArray products = json.getJSONArray(Constants.TAG_PRODUCTS);

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject prodJson = products.getJSONObject(i);
                            HashMap<String, String> prodMap = new HashMap<>();
                            prodMap.put(Constants.TAG_ITEM_ID, prodJson.getString(Constants.TAG_ITEM_ID));
                            prodMap.put(Constants.TAG_ITEM_NAME, prodJson.getString(Constants.TAG_ITEM_NAME));
                            prodMap.put(Constants.TAG_ITEM_IMAGE, prodJson.getString(Constants.TAG_ITEM_IMAGE));
                            prodMap.put(Constants.TAG_CURRENCY, prodJson.getString(Constants.TAG_CURRENCY));
                            prodMap.put(Constants.TAG_PRICE, prodJson.getString(Constants.TAG_PRICE));
                            prodMap.put(Constants.TAG_PRODUCT_STATUS, prodJson.getString(Constants.TAG_PRODUCT_STATUS));
                            productList.add(prodMap);
                        }

                        if (mScrollListener != null && productList.size() % Constants.OVERALL_LIMIT == 0) {
                            mScrollListener.setLoading(false);
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(AllProduct.this);
                        }
                    }
                    if (productList.size() == 0) {
                        nullText.setText(getResources().getText(R.string.no_item_found));
                        setErrorLayout();
                    } else {
                        nullLay.setVisibility(View.GONE);
                    }
                    productAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    setErrorLayout();
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    setErrorLayout();
                    e.printStackTrace();
                } catch (Exception e) {
                    setErrorLayout();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getProductsError: " + error.getMessage());
                setErrorLayout();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (productList.size() >= Constants.OVERALL_LIMIT) {
                            productList.add(null);
                            productAdapter.notifyItemInserted(productList.size() - 1);
                        } else if (productList.size() == 0 && !swipeRefreshLayout.isRefreshing()) {
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
                map.put(Constants.TAG_SEARCH_KEY, search_key);
                map.put(Constants.TAG_LIMIT, Integer.toString(Constants.OVERALL_LIMIT));
                map.put(Constants.TAG_OFFSET, Integer.toString(offset));
                Log.v(TAG, "getProductsParams=" + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };

        FantacySellerApplication.getInstance().addToRequestQueue(req, "products");
    }

    private void setErrorLayout() {
        if (productList.size() == 0) {
            progress.setVisibility(View.GONE);
            nullLay.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
    }

    public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
        ArrayList<HashMap<String, String>> prodLists;
        Context context;

        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView prodImage, options;
            TextView productName, productPrice, prodStatus;
            RelativeLayout main;

            public MyViewHolder(View view) {
                super(view);
                prodImage = (ImageView) view.findViewById(R.id.prodImage);
                options = (ImageView) view.findViewById(R.id.options);
                productName = (TextView) view.findViewById(R.id.prodName);
                productPrice = (TextView) view.findViewById(R.id.prodPrice);
                main = (RelativeLayout) view.findViewById(R.id.main);
                prodStatus = (TextView) view.findViewById(R.id.prodStatus);
                main.setOnClickListener(this);
                options.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.main:
                        Intent i = new Intent(AllProduct.this, ProductDetail.class);
                        i.putExtra(Constants.TAG_ITEM_ID, productList.get(getAdapterPosition()).get(Constants.TAG_ITEM_ID));
                        i.putExtra(Constants.TAG_PRODUCT_STATUS, productList.get(getAdapterPosition()).get(Constants.TAG_PRODUCT_STATUS));
                        i.putExtra(Constants.TAG_POSITION, getAdapterPosition());
                        Log.d(TAG, "getAdapterPosition: " + getAdapterPosition());
                        startActivity(i);
                        break;
                    case R.id.options:
                        String[] values = new String[]{getString(R.string.edit), getString(R.string.delete)};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllProduct.this,
                                R.layout.option_row_item, android.R.id.text1, values);
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = layoutInflater.inflate(R.layout.option_layout, null);
                        final PopupWindow popup = new PopupWindow(AllProduct.this);
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
                                        isEditMode = true;
                                        switchContent(prodLists.get(getAdapterPosition()));
                                        popup.dismiss();
                                        break;
                                    case 1:
                                        deleteDialog(prodLists.get(getAdapterPosition()).get(Constants.TAG_ITEM_ID));
                                        popup.dismiss();
                                        break;
                                }
                            }
                        });
                        break;
                }
            }
        }

        public ProductAdapter(Context context, ArrayList<HashMap<String, String>> Items) {
            this.prodLists = Items;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.prod_item_layout, parent, false));
            } else if (viewType == VIEW_TYPE_LOADING) {
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return prodLists.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MyViewHolder) {
                MyViewHolder holder = (MyViewHolder) viewHolder;
                holder.productName.setText(prodLists.get(position).get(Constants.TAG_ITEM_NAME));
                holder.productPrice.setText(prodLists.get(position).get(Constants.TAG_CURRENCY) + " " + prodLists.get(position).get(Constants.TAG_PRICE));
                Picasso.with(context).load(prodLists.get(position).get(Constants.TAG_ITEM_IMAGE)).placeholder(R.drawable.app_name).into(holder.prodImage);
                if (prodLists.get(position).get(Constants.TAG_PRODUCT_STATUS).equalsIgnoreCase("publish")) {
                    holder.prodStatus.setText(getString(R.string.publish_status));
                    holder.prodStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
                } else {
                    holder.prodStatus.setText(getString(R.string.pending_status));
                    holder.prodStatus.setTextColor(ContextCompat.getColor(context, R.color.red));

                }
            } else if (viewHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return prodLists.size();
        }

    }

    private void deleteDialog(final String item_id) {
        Display display;
        display = getWindowManager().getDefaultDisplay();
        final Dialog dialog = new Dialog(AllProduct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.default_popup);
        dialog.getWindow().setLayout(display.getWidth() * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView yes = (TextView) dialog.findViewById(R.id.yes);
        TextView no = (TextView) dialog.findViewById(R.id.no);
        no.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.prod_delete_confirmation));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(item_id);
                dialog.dismiss();

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

    private void deleteProduct(final String item_id) {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_DELETE_PRODUCT, new Response.Listener<String>() {

            @Override
            public void onResponse(String res) {
                try {
                    JSONObject json = new JSONObject(res);
                    progress.setVisibility(View.GONE);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        FantacySellerApplication.showStatusDialog(AllProduct.this, true, getString(R.string.prod_delete_success_msg), AllProduct.class);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(AllProduct.this);
                        }
                    }
                } catch (JSONException e) {
                    setErrorLayout();
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    setErrorLayout();
                    e.printStackTrace();
                } catch (Exception e) {
                    setErrorLayout();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "deleteProductError: " + error.getMessage());
                setErrorLayout();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                map.put(Constants.TAG_ITEM_ID, item_id);
                Log.v(TAG, "deleteProductParams=" + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };

        FantacySellerApplication.getInstance().addToRequestQueue(req, "deleteproduct");
    }

    private void switchContent(HashMap<String, String> productMap) {
        Log.d("prodmap", productMap + " ");
        Intent intent = new Intent(AllProduct.this, CreateProduct.class);
        intent.putExtra(Constants.FROM, "edit");
        intent.putExtra(Constants.TAG_ITEM_ID, productMap.get(Constants.TAG_ITEM_ID));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mScrollListener.resetpagecount();
        productList.clear();
        getProducts(0, searchView.getText().toString());
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mScrollListener.resetpagecount();
            searchView.clearFocus();
            searchKey = searchView.getText().toString().trim();
            FantacySellerApplication.hideSoftKeyboard(AllProduct.this, textView);
            productList.clear();
            getProducts(0, searchKey);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count != 0) {
            clearBtn.setVisibility(View.VISIBLE);
        } else {
            clearBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearBtn:
                mScrollListener.resetpagecount();
                searchView.setText("");
                searchKey = "";//Remove Previous Search Text
                nullLay.setVisibility(View.GONE);
                clearBtn.setVisibility(View.GONE);
                isCleared = true;
                productList.clear();
                getProducts(0, searchKey);
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }
}

