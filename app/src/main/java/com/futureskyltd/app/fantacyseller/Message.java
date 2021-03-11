package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class Message extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public final String TAG = this.getClass().getSimpleName();
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<HashMap<String, String>> messageList = new ArrayList<>();
    RelativeLayout progress, nullLay;
    ImageView nullImage, clearBtn, backBtn, appName;
    TextView nullText, screenTitle;
    Display display;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerOnScrollListener mScrollListener = null;
    EditText searchView;
    String searchKey = "";
    boolean isCleared;
    AppBarLayout.LayoutParams params;
    DividerItemDecoration itemDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress = (RelativeLayout) findViewById(R.id.progress);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        nullText = (TextView) findViewById(R.id.nullText);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        searchView = (EditText) findViewById(R.id.searchView);
        clearBtn = (ImageView) findViewById(R.id.clearBtn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);

        screenTitle.setText(getString(R.string.message));
        nullText.setText(getString(R.string.no_message));
        searchView.setText("");

        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        itemDivider = new DividerItemDecoration(this,
                linearLayoutManager.getOrientation());
        itemDivider.setDrawable(getResources().getDrawable(R.drawable.emptyspace));
        recyclerView.addItemDecoration(itemDivider);
        messageAdapter = new MessageAdapter(Message.this, messageList);
        recyclerView.setAdapter(messageAdapter);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        display = this.getWindowManager().getDefaultDisplay();
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.progresscolor));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.resetpagecount();
                getMessage(0, searchView.getText().toString());
            }
        });
        searchView.setOnEditorActionListener(this);
        searchView.addTextChangedListener(new TextWatcher() {
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
        });
        backBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        mScrollListener = new RecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Log.i("mScrollListener", String.valueOf(mScrollListener));
                    getMessage(current_page * Constants.OVERALL_LIMIT, searchView.getText().toString());
                }
            }
        };
        recyclerView.addOnScrollListener(mScrollListener);

        getMessage(0, searchView.getText().toString());
    }

    private void getMessage(final int offset, final String search_key) {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_CONTACT_SELLER_MESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    JSONObject json = new JSONObject(res);
                    Log.d(TAG, "getMessageRes=" + res);
                    progress.setVisibility(View.GONE);
                    swipeRefreshLayout.setEnabled(true);
                    if (messageList.size() >= Constants.OVERALL_LIMIT && messageList.get(messageList.size() - 1) == null) {
                        messageList.remove(messageList.size() - 1);
                        messageAdapter.notifyItemRemoved(messageList.size());
                    }
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        messageList.clear();
                        swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
                    }
                    if (isCleared) {
                        isCleared = false;
                        messageList.clear();
                    }

                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONArray result = json.getJSONArray(Constants.TAG_RESULT);
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject temp = result.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Constants.TAG_CHAT_ID, DefensiveClass.optString(temp, Constants.TAG_CHAT_ID));
                            map.put(Constants.TAG_ITEM_TITLE, DefensiveClass.optString(temp, Constants.TAG_ITEM_TITLE));
                            map.put(Constants.TAG_IMAGE, DefensiveClass.optString(temp, Constants.TAG_IMAGE));
                            map.put(Constants.TAG_BUYER_NAME, DefensiveClass.optString(temp, Constants.TAG_BUYER_NAME));
                            map.put(Constants.TAG_BUYER_IMAGE, DefensiveClass.optString(temp, Constants.TAG_BUYER_IMAGE));
                            map.put(Constants.TAG_MESSAGE, DefensiveClass.optString(temp, Constants.TAG_MESSAGE));
                            map.put(Constants.TAG_CHAT_DATE, DefensiveClass.optString(temp, Constants.TAG_CHAT_DATE));
                            map.put(Constants.TAG_LAST_TO_READ, DefensiveClass.optString(temp, Constants.TAG_LAST_TO_READ));
                            map.put(Constants.TAG_LAST_REPLIED, DefensiveClass.optString(temp, Constants.TAG_LAST_REPLIED));
                            messageList.add(map);
                        }
                        if (mScrollListener != null && messageList.size() % Constants.OVERALL_LIMIT == 0) {
                            mScrollListener.setLoading(false);
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        finish();
                        FantacySellerApplication.logout(Message.this);
                    }
                    if (messageList.size() == 0) {
                        nullImage.setImageResource(R.drawable.no_message);

                        nullLay.setVisibility(View.VISIBLE);
                    } else {
                        nullLay.setVisibility(View.GONE);
                    }
                    messageAdapter.notifyDataSetChanged();
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
                setErrorLayout();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (messageList.size() >= Constants.OVERALL_LIMIT) {
                            messageList.add(null);
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                        } else if (messageList.size() == 0 && !swipeRefreshLayout.isRefreshing()) {
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
                map.put(Constants.TAG_LIMIT, Integer.toString(Constants.OVERALL_LIMIT));
                map.put(Constants.TAG_OFFSET, Integer.toString(offset));
                map.put(Constants.TAG_SEARCH_KEY, search_key);
                Log.i(TAG, "getMessageParams: " + map);
                return map;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + GetSet.getToken());
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(req);
    }

    private void setErrorLayout() {
        if (messageList.size() == 0) {
            progress.setVisibility(View.GONE);
            nullLay.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchKey = searchView.getText().toString().trim();
            FantacySellerApplication.hideSoftKeyboard(Message.this, textView);
            if (!searchKey.equals("")) {
                nullLay.setVisibility(View.GONE);
                mScrollListener.resetpagecount();
                searchView.clearFocus();
                FantacySellerApplication.hideSoftKeyboard(Message.this, textView);
                messageList.clear();
                getMessage(0, searchKey);
            } else {
                FantacySellerApplication.showToast(Message.this, getString(R.string.reqd_message), Toast.LENGTH_SHORT);
            }
            return true;
        }
        return false;
    }


    private class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
        ArrayList<HashMap<String, String>> messageList;
        Context context;

        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        public MessageAdapter(Context context, ArrayList<HashMap<String, String>> messageList) {
            this.context = context;
            this.messageList = messageList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView itemImage;
            TextView itemName, buyerName, message, date;
            RelativeLayout mainLay;

            public MyViewHolder(View view) {
                super(view);

                itemImage = (ImageView) view.findViewById(R.id.itemImage);
                itemName = (TextView) view.findViewById(R.id.itemName);
                buyerName = (TextView) view.findViewById(R.id.buyerName);
                message = (TextView) view.findViewById(R.id.message);
                date = (TextView) view.findViewById(R.id.date);
                mainLay = (RelativeLayout) view.findViewById(R.id.mainLay);

                itemImage.setOnClickListener(this);
                mainLay.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mainLay:
                        Intent i = new Intent(getApplicationContext(), Chat.class);
                        i.putExtra(Constants.TAG_CHAT_ID, messageList.get(getAdapterPosition()).get(Constants.TAG_CHAT_ID));
                        startActivity(i);
                        break;
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false));
            } else if (viewType == VIEW_TYPE_LOADING) {
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return messageList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyViewHolder) {
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.itemName.setText(messageList.get(position).get(Constants.TAG_ITEM_TITLE));
                viewHolder.buyerName.setText(getString(R.string.by) + " " + messageList.get(position).get(Constants.TAG_BUYER_NAME));
                viewHolder.message.setText(messageList.get(position).get(Constants.TAG_MESSAGE));
                if (!messageList.get(position).get(Constants.TAG_CHAT_DATE).equals(null) && !messageList.get(position).get(Constants.TAG_CHAT_DATE).equals("")) {
                    viewHolder.date.setText(FantacySellerApplication.getDate(messageList.get(position).get(Constants.TAG_CHAT_DATE)));
                }
                if (messageList.get(position).get(Constants.TAG_IMAGE) != null && !messageList.get(position).get(Constants.TAG_IMAGE).equals("")) {
                    Picasso.with(context).load(messageList.get(position).get(Constants.TAG_IMAGE)).into(viewHolder.itemImage);
                }
                //0-unread,1-read
                if (messageList.get(position).get(Constants.TAG_LAST_TO_READ).equals("0")) {// && !messageList.get(position).get(Constants.TAG_LAST_REPLIED).equals(GetSet.getUserId())) {
                    viewHolder.message.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                } else {
                    viewHolder.message.setTextColor(ContextCompat.getColor(context, R.color.textSecondary));
                }

                if (FantacySellerApplication.isRTL(getApplicationContext())) {
                    viewHolder.itemName.setGravity(Gravity.RIGHT);
                } else {
                    viewHolder.itemName.setGravity(Gravity.LEFT);
                }
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearBtn:
                nullLay.setVisibility(View.VISIBLE);
                mScrollListener.resetpagecount();
                searchView.setText("");
                searchKey = "";//Remove Previous Search Text
                nullLay.setVisibility(View.GONE);
                clearBtn.setVisibility(View.GONE);
                isCleared = true;
                getMessage(0, searchKey);
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }
}
