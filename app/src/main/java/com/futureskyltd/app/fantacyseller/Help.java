package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class Help extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<HashMap<String, String>> helpAry = new ArrayList<HashMap<String, String>>();
    RelativeLayout progress, nullLay;
    ImageView back, appName;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress = (RelativeLayout) findViewById(R.id.progress);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        back = (ImageView) findViewById(R.id.backBtn);
        appName = (ImageView) findViewById(R.id.appName);
        title = (TextView) findViewById(R.id.title);

        appName.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);

        title.setText(getResources().getString(R.string.help));

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        recyclerViewAdapter = new RecyclerViewAdapter(this, helpAry);
        recyclerView.setAdapter(recyclerViewAdapter);

        progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        nullLay.setVisibility(View.GONE);

        back.setOnClickListener(this);

        getHelpData();

    }

    private void getHelpData() {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_HELP, new Response.Listener<String>() {

            @Override
            public void onResponse(String res) {
                try {
                    JSONObject json = new JSONObject(res);
                    Log.d(TAG, "getHelpDataRes: " + json);
                    String status = DefensiveClass.optString(json, Constants.TAG_STATUS);
                    if (status.equalsIgnoreCase("true")) {

                        JSONArray result = json.getJSONArray(Constants.TAG_RESULT);
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject temp = result.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(Constants.TAG_PAGE_NAME, DefensiveClass.optString(temp, Constants.TAG_PAGE_NAME));
                            map.put(Constants.TAG_MAIN_CONTENT, DefensiveClass.optString(temp, Constants.TAG_MAIN_CONTENT));
                            map.put(Constants.TAG_SUB_CONTENT, DefensiveClass.optString(temp, Constants.TAG_SUB_CONTENT));
                            helpAry.add(map);
                        }
                        recyclerViewAdapter.notifyDataSetChanged();
                        progress.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        nullLay.setVisibility(View.GONE);
                    } else if (status.equalsIgnoreCase("error")) {
                        setErrorLayout();
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
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
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        nullLay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> helpList;
        Context context;

        public RecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> helpList) {
            this.helpList = helpList;
            this.context = context;
        }

        @Override
        public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.help_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.MyViewHolder holder, int position) {
            holder.name.setText(helpList.get(position).get(Constants.TAG_PAGE_NAME));
            if (helpList.get(position).get(Constants.TAG_PAGE_NAME).equalsIgnoreCase("About")) {
                holder.bgView.setVisibility(View.VISIBLE);
            } else {
                holder.bgView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return helpList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView name;
            View bgView;
            RelativeLayout mainLay;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                bgView = (View) view.findViewById(R.id.bgView);
                mainLay = (RelativeLayout) view.findViewById(R.id.mainLay);
                mainLay.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mainLay:
                        HelpContent.mainContent = helpList.get(getAdapterPosition()).get(Constants.TAG_MAIN_CONTENT);
                        HelpContent.subContent = helpList.get(getAdapterPosition()).get(Constants.TAG_SUB_CONTENT);
                        Intent i = new Intent(Help.this, HelpContent.class);
                        i.putExtra("pageName", helpList.get(getAdapterPosition()).get(Constants.TAG_PAGE_NAME));
                        startActivity(i);
                        break;
                }
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
