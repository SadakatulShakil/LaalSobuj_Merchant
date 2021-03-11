package com.futureskyltd.app.fantacyseller;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.external.RecyclerOnScrollListener;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = this.getClass().getSimpleName();
    RecyclerView recyclerView;
    RelativeLayout progress, nullLay;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<HashMap<String, String>> newsList = new ArrayList<>();
    NewsAdapter newsAdapter;
    ImageView back, appName, nullImage;
    TextView title, create, nullText;
    LinearLayoutManager linearLayoutManager;
    RecyclerOnScrollListener mScrollListener = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress = (RelativeLayout) findViewById(R.id.progress);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);
        create = (TextView) findViewById(R.id.processBtn);
        nullText = (TextView) findViewById(R.id.nullText);
        nullImage = (ImageView) findViewById(R.id.nullImage);

        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        create.setVisibility(View.VISIBLE);

        title.setText(getResources().getString(R.string.news));
        create.setText(getResources().getString(R.string.create));
        nullText.setText(getString(R.string.no_news));

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        newsAdapter = new NewsAdapter(NewsActivity.this, newsList);
        recyclerView.setAdapter(newsAdapter);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.progresscolor));

        create.setOnClickListener(this);
        back.setOnClickListener(this);

        mScrollListener = new RecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    getNews(current_page * Constants.OVERALL_LIMIT);
                    Log.v("offset:", "On offset" + (Constants.OVERALL_LIMIT * current_page));
                }
            }
        };

        recyclerView.addOnScrollListener(mScrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.resetpagecount();
                newsList.clear();
                getNews(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsList.clear();
        progress.setVisibility(View.VISIBLE);
        getNews(0);
    }

    private void getNews(final int offset) {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.API_ALL_NEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.i(TAG, "getNewsRes: " + json);
                    swipeRefreshLayout.setEnabled(true);
                    if (newsList.size() >= Constants.OVERALL_LIMIT && newsList.get(newsList.size() - 1) == null) {
                        newsList.remove(newsList.size() - 1);
                        newsAdapter.notifyItemRemoved(newsList.size());
                    }
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        newsList.clear();
                        swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
                    }
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        for (int i = 0; i < json.getJSONArray(Constants.TAG_RESULT).length(); i++) {
                            JSONObject news_message = json.getJSONArray(Constants.TAG_RESULT).getJSONObject(i);
                            HashMap<String, String> news = new HashMap<>();
                            news.put(Constants.TAG_MESSAGE, news_message.getString(Constants.TAG_MESSAGE));
                            news.put(Constants.TAG_MESSAGE_DATE, news_message.getString(Constants.TAG_MESSAGE_DATE));
                            newsList.add(news);
                        }

                        if (mScrollListener != null && newsList.size() % Constants.OVERALL_LIMIT == 0) {
                            mScrollListener.setLoading(false);
                        }
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(NewsActivity.this);
                        }
                    }
                    if (newsList.size() == 0) {
                        setErrorLayout();
                    } else {
                        nullLay.setVisibility(View.GONE);
                    }
                    newsAdapter.notifyDataSetChanged();
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
                        if (newsList.size() >= Constants.OVERALL_LIMIT) {
                            newsList.add(null);
                            newsAdapter.notifyItemInserted(newsList.size() - 1);
                        } else if (newsList.size() == 0 && !swipeRefreshLayout.isRefreshing()) {
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
                Log.i(TAG, "getNewsParams: " + map);
                return map;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(request, "news");
    }

    private void setErrorLayout() {
        if (newsList.size() == 0) {
            progress.setVisibility(View.GONE);
            nullLay.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
    }

    private class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
        ArrayList<HashMap<String, String>> newsList;
        Context context;

        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        public NewsAdapter(NewsActivity context, ArrayList<HashMap<String, String>> items) {
            this.context = context;
            this.newsList = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false));
            } else if (viewType == VIEW_TYPE_LOADING) {
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return newsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.newsDetail.setText(newsList.get(position).get(Constants.TAG_MESSAGE));
                if (!newsList.get(position).get(Constants.TAG_MESSAGE_DATE).equals(null) && !newsList.get(position).get(Constants.TAG_MESSAGE_DATE).equals("")) {
                    viewHolder.newsTime.setText(FantacySellerApplication.getDate(newsList.get(position).get(Constants.TAG_MESSAGE_DATE)));
                }
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView newsDetail, newsTime;

            public ViewHolder(View itemView) {
                super(itemView);
                newsDetail = (TextView) itemView.findViewById(R.id.newsItemDetail);
                newsTime = (TextView) itemView.findViewById(R.id.newsItemTime);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                newsList.clear();
                finish();
                break;
            case R.id.processBtn:
                startActivity(new Intent(NewsActivity.this, CreateNews.class));
                break;
        }
    }
}