package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.futureskyltd.app.external.ReverseRecyclerOnScrollListener;
import com.futureskyltd.app.helper.NetworkReceiver;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity implements View.OnClickListener, NetworkReceiver.ConnectivityReceiverListener {
    public final String TAG = this.getClass().getSimpleName();
    ImageView backBtn, abItemImage, sendBtn, appName, nullImage;
    TextView abItemName, abShopName, nullText;
    RelativeLayout abItemLay, progressLay, nullLay;
    EditText chatEdit;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar loadmoreprogress;
    String chatid;
    ArrayList<HashMap<String, String>> backup = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> chatAry = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> detailMap = new HashMap<String, String>();
    boolean isFirstLoad = true;
    ReverseRecyclerOnScrollListener mScrollListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        abItemLay = (RelativeLayout) findViewById(R.id.abItemLay);
        abItemImage = (ImageView) findViewById(R.id.abItemImage);
        abItemName = (TextView) findViewById(R.id.abItemName);
        abShopName = (TextView) findViewById(R.id.abShopName);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        sendBtn = (ImageView) findViewById(R.id.sendBtn);
        chatEdit = (EditText) findViewById(R.id.chatEdit);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        nullText = (TextView) findViewById(R.id.nullText);
        progressLay = (RelativeLayout) findViewById(R.id.progress);
        loadmoreprogress = (ProgressBar) findViewById(R.id.loadmoreprogress);
        appName = (ImageView) findViewById(R.id.appName);

        backBtn.setVisibility(View.VISIBLE);
        abItemLay.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);

        backBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        chatid = (String) getIntent().getExtras().get(Constants.TAG_CHAT_ID);

        linearLayoutManager = new LinearLayoutManager(Chat.this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(this, chatAry);
        recyclerView.setAdapter(recyclerViewAdapter);

        getChats(0);

        mScrollListener = new ReverseRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadmoreprogress.setVisibility(View.VISIBLE);
                getChats(current_page * Constants.OVERALL_LIMIT);
                Log.v("offset:", "On offset" + (Constants.OVERALL_LIMIT * current_page));

            }
        };
        recyclerView.addOnScrollListener(mScrollListener);
        // register connection status listener
        FantacySellerApplication.getInstance().setConnectivityListener(this);
    }

    private void getChats(final int offset) {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_VIEW_SELLER_MESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "getChatsRes=" + res);
                    progressLay.setVisibility(View.GONE);
                    loadmoreprogress.setVisibility(View.GONE);

                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        detailMap.put(Constants.TAG_CHAT_ID, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_CHAT_ID));
                        detailMap.put(Constants.TAG_ITEM_TITLE, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_ITEM_TITLE));
                        detailMap.put(Constants.TAG_IMAGE, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_IMAGE));
                        detailMap.put(Constants.TAG_SUBJECT, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_SUBJECT));
                        detailMap.put(Constants.TAG_BUYER_NAME, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_BUYER_NAME));
                        //detailMap.put(Constants.TAG_ITEM_ID, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_ITEM_ID));
                        //detailMap.put(Constants.TAG_SHOP_ID, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_SHOP_ID));
                        //detailMap.put(Constants.TAG_SHOP_IMAGE, DefensiveClass.optString(json.getJSONObject(Constants.TAG_RESULT), Constants.TAG_SHOP_IMAGE));

                        JSONArray messages = json.getJSONObject(Constants.TAG_RESULT).optJSONArray(Constants.TAG_MESSAGES);
                        if (messages != null) {
                            ArrayList<HashMap<String, String>> tempAry = new ArrayList<HashMap<String, String>>();
                            for (int p = 0; p < messages.length(); p++) {
                                JSONObject pobj = messages.getJSONObject(p);
                                HashMap<String, String> pmap = new HashMap<>();
                                pmap.put(Constants.TAG_MESSAGE, DefensiveClass.optString(pobj, Constants.TAG_MESSAGE));
                                pmap.put(Constants.TAG_USER_NAME, DefensiveClass.optString(pobj, Constants.TAG_USER_NAME));
                                pmap.put(Constants.TAG_FULL_NAME, DefensiveClass.optString(pobj, Constants.TAG_FULL_NAME));
                                pmap.put(Constants.TAG_USER_ID, DefensiveClass.optString(pobj, Constants.TAG_USER_ID));
                                pmap.put(Constants.TAG_USER_IMAGE, DefensiveClass.optString(pobj, Constants.TAG_USER_IMAGE));
                                pmap.put(Constants.TAG_CHAT_DATE, DefensiveClass.optString(pobj, Constants.TAG_CHAT_DATE));
                                tempAry.add(pmap);
                            }

                            chatAry.addAll(tempAry);

                            if (mScrollListener != null) {
                                mScrollListener.setLoading(false);
                            }
                        }

                        abItemName.setText(detailMap.get(Constants.TAG_ITEM_TITLE));
                        //abShopName.setText(getString(R.string.by) + " " + detailMap.get(Constants.TAG_SHOP_NAME));
                        abShopName.setText(detailMap.get(Constants.TAG_BUYER_NAME));
                        if (!detailMap.get(Constants.TAG_IMAGE).equals("")) {
                            Picasso.with(Chat.this).load(detailMap.get(Constants.TAG_IMAGE)).into(abItemImage);
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(Chat.this);
                        }
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                    if (isFirstLoad) {
                        isFirstLoad = false;
                        recyclerView.scrollToPosition(0);
                    }
                    if (chatAry.size() == 0) {
                        nullLay.setVisibility(View.VISIBLE);
                        nullImage.setImageDrawable(getResources().getDrawable(R.drawable.no_message));
                        nullText.setText(getResources().getString(R.string.no_message));
                    } else {
                        nullLay.setVisibility(View.GONE);
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
                setErrorLayout();
                loadmoreprogress.setVisibility(View.GONE);
                Log.e(TAG, "getChatsError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Chat.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (chatAry.size() == 0) {
                            progressLay.setVisibility(View.VISIBLE);
                        }
                    }
                });
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                map.put(Constants.TAG_LIMIT, Integer.toString(Constants.OVERALL_LIMIT));
                map.put(Constants.TAG_OFFSET, Integer.toString(offset));
                map.put(Constants.TAG_CHAT_ID, chatid);
                Log.v(TAG, "getChatsParams=" + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(req);
    }

    private void setErrorLayout() {
        if (chatAry.size() == 0) {
            progressLay.setVisibility(View.GONE);
            nullLay.setVisibility(View.VISIBLE);
        }
    }

    private void sendChat(final String message) {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_SEND_SELLER_MESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "sendChatRes=" + res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        Log.v(TAG, "sendChat: " + DefensiveClass.optString(json, Constants.TAG_STATUS));
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(Chat.this);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "sendChatError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                map.put(Constants.TAG_CHAT_ID, detailMap.get(Constants.TAG_CHAT_ID));
                map.put(Constants.TAG_SUBJECT, detailMap.get(Constants.TAG_SUBJECT));
                map.put(Constants.TAG_MESSAGE, message);
                Log.v(TAG, "sendChatParams=" + map);

                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(req);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), NetworkReceiver.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), true);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), isConnected);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
        ArrayList<HashMap<String, String>> messageList;
        Context context;

        public RecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> items) {
            this.messageList = items;
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            return messageList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false));
            } else if (viewType == VIEW_TYPE_LOADING) {
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MyViewHolder) {
                MyViewHolder holder = (MyViewHolder) viewHolder;
                holder.leftLay.setVisibility(View.GONE);
                holder.rightLay.setVisibility(View.GONE);
                holder.dateLay.setVisibility(View.GONE);
                if (messageList.get(position).get(Constants.TAG_USER_ID).equals(GetSet.getUserId())) {
                    holder.rightLay.setVisibility(View.VISIBLE);
                    holder.rightMsg.setText(messageList.get(position).get(Constants.TAG_MESSAGE));
                    holder.rightTime.setText(getTime(Long.parseLong(messageList.get(position).get(Constants.TAG_CHAT_DATE)) * 1000));
                    String image = messageList.get(position).get(Constants.TAG_USER_IMAGE);
                    if (image != null && !image.equals("")) {
                        Picasso.with(context).load(image).into(holder.rightImage);
                    }
                    if (position < getItemCount() - 1) {
                        if (messageList.get(position + 1).get(Constants.TAG_USER_ID).equals(messageList.get(position).get(Constants.TAG_USER_ID))) {
                            holder.rightImage.setVisibility(View.INVISIBLE);
                        } else {
                            holder.rightImage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.rightImage.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.leftLay.setVisibility(View.VISIBLE);
                    holder.leftMsg.setText(messageList.get(position).get(Constants.TAG_MESSAGE));
                    holder.leftTime.setText(getTime(Long.parseLong(messageList.get(position).get(Constants.TAG_CHAT_DATE)) * 1000));
                    if (messageList.get(position).get(Constants.TAG_USER_IMAGE) != null && !messageList.get(position).get(Constants.TAG_USER_IMAGE).equals("")) {
                        Picasso.with(context).load(messageList.get(position).get(Constants.TAG_USER_IMAGE)).into(holder.leftImage);
                    }
                    if (position < getItemCount() - 1) {
                        if (messageList.get(position + 1).get(Constants.TAG_USER_ID).equals(messageList.get(position).get(Constants.TAG_USER_ID))) {
                            holder.leftImage.setVisibility(View.INVISIBLE);
                        } else {
                            holder.leftImage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.leftImage.setVisibility(View.VISIBLE);
                    }
                }

                try {
                    String chatDate = FantacySellerApplication.getDate(messageList.get(position).get(Constants.TAG_CHAT_DATE));
                    if (position == getItemCount() - 1) {
                        holder.dateLay.setVisibility(View.VISIBLE);
                        holder.date.setText(chatDate);
                    } else {
                        String ldate = FantacySellerApplication.getDate(messageList.get(position + 1).get(Constants.TAG_CHAT_DATE));
                        if (ldate.equals(chatDate)) {
                            holder.dateLay.setVisibility(View.GONE);
                        } else {
                            holder.dateLay.setVisibility(View.VISIBLE);
                            holder.date.setText(chatDate);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (viewHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView date, leftMsg, rightMsg, leftTime, rightTime;
            ImageView leftImage, rightImage;
            RelativeLayout dateLay, leftLay, rightLay;

            public MyViewHolder(View view) {
                super(view);
                date = (TextView) view.findViewById(R.id.date);
                leftMsg = (TextView) view.findViewById(R.id.leftMsg);
                rightMsg = (TextView) view.findViewById(R.id.rightMsg);
                leftTime = (TextView) view.findViewById(R.id.leftTime);
                rightTime = (TextView) view.findViewById(R.id.rightTime);
                leftImage = (ImageView) view.findViewById(R.id.leftImage);
                rightImage = (ImageView) view.findViewById(R.id.rightImage);
                dateLay = (RelativeLayout) view.findViewById(R.id.dateLay);
                leftLay = (RelativeLayout) view.findViewById(R.id.leftLay);
                rightLay = (RelativeLayout) view.findViewById(R.id.rightLay);
            }
        }

        String getTime(long timeStamp) {
            try {
                return new SimpleDateFormat("hh:mm a").format((new Date(timeStamp)));
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }

    @Override
    public void onBackPressed() {
        FantacySellerApplication.hideSoftKeyboard(this, backBtn);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                onBackPressed();
                break;
            case R.id.sendBtn:
                if (chatEdit.getText().toString().trim().length() == 0) {
                    FantacySellerApplication.showToast(Chat.this, getString(R.string.please_enter_message), Toast.LENGTH_SHORT);
                } else {
                    nullLay.setVisibility(View.GONE);
                    sendChat(chatEdit.getText().toString().trim());
                    HashMap<String, String> pmap = new HashMap<>();
                    pmap.put(Constants.TAG_MESSAGE, chatEdit.getText().toString().trim());
                    pmap.put(Constants.TAG_USER_NAME, GetSet.getUserName());
                    pmap.put(Constants.TAG_FULL_NAME, GetSet.getFullName());
                    pmap.put(Constants.TAG_USER_ID, GetSet.getUserId());
                    pmap.put(Constants.TAG_USER_IMAGE, GetSet.getImageUrl());
                    pmap.put(Constants.TAG_CHAT_DATE, String.valueOf(System.currentTimeMillis() / 1000L));
                    backup.clear();
                    backup.addAll(chatAry);
                    chatAry.clear();
                    chatAry.add(pmap);
                    chatAry.addAll(backup);
                    chatEdit.setText("");
                    recyclerView.scrollToPosition(0);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
