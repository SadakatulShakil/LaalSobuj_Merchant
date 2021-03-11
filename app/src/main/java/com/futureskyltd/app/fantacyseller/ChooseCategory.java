package com.futureskyltd.app.fantacyseller;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
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

/**
 * Created by hitasoft on 16/5/17.
 */

public class ChooseCategory extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    ExpandableListView listView;
    RelativeLayout progressLay, nullLay;
    ImageView nullImage, backBtn, appName;
    TextView nullText, screenTitle;
    ArrayList<HashMap<String, String>> categoryAry = new ArrayList<HashMap<String, String>>();
    String subcatId = "", supercatId = "", cateId="", selectedCatName="", selectedFrom="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main_layout);

        listView = (ExpandableListView) findViewById(R.id.listView);
        nullLay = (RelativeLayout) findViewById(R.id.nullLay);
        nullImage = (ImageView) findViewById(R.id.nullImage);
        nullText = (TextView) findViewById(R.id.nullText);
        progressLay = (RelativeLayout) findViewById(R.id.progress);
        appName = (ImageView) findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);
        backBtn = (ImageView) findViewById(R.id.backBtn);

        screenTitle.setText(getString(R.string.select_category));

        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        nullLay.setVisibility(View.GONE);
        progressLay.setVisibility(View.VISIBLE);

        categoryAry.clear();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getCategory();

        cateId = getIntent().getStringExtra(Constants.TAG_CATEGORY_ID);
        if (getIntent().getStringExtra(Constants.TAG_SUBCATEGORY_ID) != "")
            subcatId = getIntent().getStringExtra(Constants.TAG_SUBCATEGORY_ID);
        if (getIntent().getStringExtra(Constants.TAG_SUPER_CATEGORY_ID) != "")
            supercatId = getIntent().getStringExtra(Constants.TAG_SUPER_CATEGORY_ID);
    }

    private void setAdapter() {
        ViewCompat.setNestedScrollingEnabled(listView, true);

        listView.setAdapter(new ParentLevel(this, categoryAry));
        Log.v(TAG, "catID=" + cateId);
        if (cateId != null) {
            for (int i = 0; i < categoryAry.size(); i++) {
                if (cateId.equals(categoryAry.get(i).get(Constants.TAG_ID)) && !cateId.equals("")) {
                    if (!categoryAry.get(i).get(Constants.TAG_SIZE).equals("0")) {
                        listView.expandGroup(i);

                        break;
                    }
                }
            }
        }
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);

        //Latest UI Change Code
        //listView.setDividerHeight(FantacySellerApplication.dpToPx(this, 5 ));
        //Old UI Code
        listView.setChildDivider(new ColorDrawable(getResources().getColor(R.color.white)));
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.divider)));
        listView.setDividerHeight(FantacySellerApplication.dpToPx(ChooseCategory.this, 1));

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if (categoryAry.get(groupPosition).get(Constants.TAG_SIZE).equals("0")) {
                    Log.v("Parent cat", "cat name=" + categoryAry.get(groupPosition).get(Constants.TAG_NAME));
                    cateId = categoryAry.get(groupPosition).get(Constants.TAG_ID);
                    selectedFrom = "parent";
                    selectedCatName = categoryAry.get(groupPosition).get(Constants.TAG_NAME);
                    onBackPressed();
                }
                return false;
            }
        });
    }

    private void getCategory() {

        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_GET_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.d(TAG, "getCategoryRes=" + res);
                    JSONObject json = new JSONObject(res);
                    progressLay.setVisibility(View.GONE);
                    String status = DefensiveClass.optString(json, Constants.TAG_STATUS);
                    if (status.equalsIgnoreCase("true")) {
                        JSONArray category = json.getJSONArray(Constants.TAG_CATEGORY);
                        for (int i = 0; i < category.length(); i++) {
                            JSONObject temp = category.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<>();

                            JSONArray sub_category = temp.optJSONArray(Constants.TAG_SUB_CATEGORY);

                            map.put(Constants.TAG_ID, DefensiveClass.optString(temp, Constants.TAG_ID));
                            map.put(Constants.TAG_NAME, DefensiveClass.optString(temp, Constants.TAG_NAME));
                            map.put(Constants.TAG_ICON, DefensiveClass.optString(temp, Constants.TAG_ICON));
                            map.put(Constants.TAG_SUB_CATEGORY, String.valueOf(sub_category));
                            map.put(Constants.TAG_SIZE, String.valueOf(sub_category.length()));
                            categoryAry.add(map);
                        }
                    }  else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(ChooseCategory.this);
                        } else {
                            showErrorLayout();
                        }
                    }
                    setAdapter();
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
                Log.e(TAG, "getCategoryError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                Log.i(TAG, "getCategoryParams: " + map);
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

    private void showErrorLayout() {
        progressLay.setVisibility(View.GONE);
        if (categoryAry.size() == 0) {
            nullLay.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<HashMap<String, String>> getSubCategory(String from, String json) {
        ArrayList<HashMap<String, String>> subcat = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray sub = new JSONArray(json);
            for (int x = 0; x < sub.length(); x++) {
                JSONObject temp = sub.getJSONObject(x);
                HashMap<String, String> map = new HashMap<String, String>();

                String id = DefensiveClass.optString(temp, Constants.TAG_ID);
                String name = DefensiveClass.optString(temp, Constants.TAG_NAME);

                map.put(Constants.TAG_ID, id);
                map.put(Constants.TAG_NAME, name);

                if (from.equals("subcat")) {
                    JSONArray super_category = temp.optJSONArray(Constants.TAG_SUPER_CATEGORY);
                    map.put(Constants.TAG_SUPER_CATEGORY, String.valueOf(super_category));
                    map.put(Constants.TAG_SIZE, String.valueOf(super_category.length()));
                }

                subcat.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subcat;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FantacySellerApplication.getInstance().getRequestQueue().cancelAll("tag");
    }

    public class ParentLevel extends BaseExpandableListAdapter {

        ArrayList<HashMap<String, String>> catAry;
        GroupViewHolder groupViewHolder;
        Context context;

        public ParentLevel(Context context, ArrayList<HashMap<String, String>> arrayList) {
            this.context = context;
            this.catAry = arrayList;
        }

        @Override
        public Object getChild(int arg0, int arg1) {
            return arg1;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ArrayList<HashMap<String, String>> categoryAry = getSubCategory("subcat", catAry.get(groupPosition).get(Constants.TAG_SUB_CATEGORY));
            final String categoryId = catAry.get(groupPosition).get(Constants.TAG_ID);
            SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(context);
            secondLevelELV.setAdapter(new SecondLevelAdapter(context, categoryAry));
            if (subcatId != null) {
                for (int i = 0; i < categoryAry.size(); i++) {
                    if (subcatId.equals(categoryAry.get(i).get(Constants.TAG_ID)) && !subcatId.equals("")) {
                        secondLevelELV.expandGroup(i);
                        break;
                    }
                }
            }
            secondLevelELV.setGroupIndicator(null);
            secondLevelELV.setChildIndicator(null);

            //Old UI Code
            secondLevelELV.setChildIndicator(null);
            secondLevelELV.setChildDivider(new ColorDrawable(context.getResources().getColor(R.color.white)));
            secondLevelELV.setDivider(new ColorDrawable(context.getResources().getColor(R.color.divider)));
            secondLevelELV.setDividerHeight(FantacySellerApplication.dpToPx(context, 1));
            secondLevelELV.setPadding(FantacySellerApplication.dpToPx(context, 10), 0, FantacySellerApplication.dpToPx(context, 10), 0);

            secondLevelELV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    if (categoryAry.get(groupPosition).get(Constants.TAG_SIZE).equals("0")) {
                        cateId = categoryId;
                        selectedFrom = "sub";
                        selectedCatName = categoryAry.get(groupPosition).get(Constants.TAG_NAME);
                        subcatId = categoryAry.get(groupPosition).get(Constants.TAG_ID);
                        onBackPressed();
                    }
                    return false;
                }
            });

            secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    ArrayList<HashMap<String, String>> supercatAry = getSubCategory("supercat", categoryAry.get(groupPosition).get(Constants.TAG_SUPER_CATEGORY));
                    cateId = categoryId;
                    selectedFrom = "super";
                    selectedCatName = supercatAry.get(childPosition).get(Constants.TAG_NAME);
                    subcatId = categoryAry.get(groupPosition).get(Constants.TAG_ID);
                    supercatId = supercatAry.get(childPosition).get(Constants.TAG_ID);
                    onBackPressed();
                    return false;
                }
            });
            return secondLevelELV;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (catAry.get(groupPosition).get(Constants.TAG_SIZE).equals("0")) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return categoryAry.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_first_row, parent, false);

                groupViewHolder = new GroupViewHolder();

                groupViewHolder.name = (TextView) convertView.findViewById(R.id.name);
                groupViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }

            HashMap<String, String> tempMap = catAry.get(groupPosition);
            groupViewHolder.name.setText(tempMap.get(Constants.TAG_NAME));

            if (getChildrenCount(groupPosition) > 0) {
                groupViewHolder.icon.setVisibility(View.VISIBLE);
               /**/
            } else {
                groupViewHolder.icon.setVisibility(View.GONE);
            }


            if (isExpanded && getChildrenCount(groupPosition) > 0) {
                groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));
                groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.textPrimary));
                groupViewHolder.icon.setRotation(180);
                if (isExpanded && tempMap.get(Constants.TAG_ID).equals(cateId) && !cateId.equals("")) {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.colorPrimary));
                } else {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));
                    groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.textPrimary));
                }
            } else {
                groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));
                groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.textPrimary));
                groupViewHolder.icon.setRotation(0);
                if (tempMap.get(Constants.TAG_ID).equals(cateId) && !cateId.equals("")) {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));

                }
            }


            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class GroupViewHolder {
            ImageView icon;
            TextView name;
        }
    }

    public class SecondLevelExpandableListView extends ExpandableListView {

        public SecondLevelExpandableListView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public class SecondLevelAdapter extends BaseExpandableListAdapter {

        ArrayList<HashMap<String, String>> catAry;
        GroupViewHolder groupViewHolder;
        ChildViewHolder childViewHolder;
        private Context context;

        public SecondLevelAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
            this.context = context;
            this.catAry = arrayList;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return catAry.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_second_row, parent, false);
                groupViewHolder = new GroupViewHolder();

                groupViewHolder.name = (TextView) convertView.findViewById(R.id.name);
                groupViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }


            HashMap<String, String> tempMap = catAry.get(groupPosition);

            groupViewHolder.name.setText(tempMap.get(Constants.TAG_NAME));

            if (getChildrenCount(groupPosition) > 0) {
                groupViewHolder.icon.setVisibility(View.VISIBLE);
            } else {
                groupViewHolder.icon.setVisibility(View.GONE);


            }

            if (isExpanded && getChildrenCount(groupPosition) > 0) {
                groupViewHolder.icon.setImageResource(R.drawable.minus);
                if (isExpanded && tempMap.get(Constants.TAG_ID).equals(subcatId) && !subcatId.equals("")) {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.colorPrimary));
                } else {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));
                    groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.textPrimary));
                }

            } else {
                groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));
                groupViewHolder.icon.setColorFilter(getResources().getColor(R.color.textPrimary));
                groupViewHolder.icon.setImageResource(R.drawable.plus);
                if (tempMap.get(Constants.TAG_ID).equals(subcatId) && !subcatId.equals("")) {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    groupViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));
                }
            }


            return convertView;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_third_row, parent, false);
                childViewHolder = new ChildViewHolder();

                childViewHolder.name = (TextView) convertView.findViewById(R.id.name);


                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }

            ArrayList<HashMap<String, String>> supercatAry = getSubCategory("supercat", catAry.get(groupPosition).get(Constants.TAG_SUPER_CATEGORY));
            HashMap<String, String> tempMap = supercatAry.get(childPosition);

            childViewHolder.name.setText(tempMap.get(Constants.TAG_NAME));
            if (tempMap.get(Constants.TAG_ID).equals(supercatId) && !supercatId.equals("")) {
                childViewHolder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                childViewHolder.name.setTextColor(getResources().getColor(R.color.textPrimary));

            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return Integer.parseInt(catAry.get(groupPosition).get(Constants.TAG_SIZE));
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class GroupViewHolder {
            ImageView icon;
            TextView name;
        }

        private class ChildViewHolder {
            TextView name;
        }
    }

    @Override
    public void onBackPressed() {
        switch (selectedFrom) {
            case "parent":
                getIntent().putExtra(Constants.FROM, "category");
                getIntent().putExtra(Constants.TAG_CATEGORY_ID, cateId);
                getIntent().putExtra(Constants.TAG_SUBCATEGORY_ID, "");
                getIntent().putExtra(Constants.TAG_SUPER_CATEGORY_ID, "");
                getIntent().putExtra(Constants.CAT_NAME, selectedCatName);
                getIntent().putExtra(Constants.CAT_STATE, selectedFrom);
                if(selectedCatName != null && !selectedCatName.equals("")) {
                    this.setResult(RESULT_OK, getIntent());
                } else {
                    this.setResult(RESULT_CANCELED);
                }
                super.onBackPressed();
                break;
            case "sub":
                getIntent().putExtra(Constants.FROM, "category");
                getIntent().putExtra(Constants.TAG_CATEGORY_ID, cateId);
                getIntent().putExtra(Constants.TAG_SUBCATEGORY_ID, subcatId);
                getIntent().putExtra(Constants.TAG_SUPER_CATEGORY_ID, "");
                getIntent().putExtra(Constants.CAT_NAME, selectedCatName);
                getIntent().putExtra(Constants.CAT_STATE, selectedFrom);
                if(selectedCatName != null && !selectedCatName.equals("")) {
                    this.setResult(RESULT_OK, getIntent());
                } else {
                    this.setResult(RESULT_CANCELED);
                }
                super.onBackPressed();
                break;
            case "super":
                getIntent().putExtra(Constants.FROM, "category");
                getIntent().putExtra(Constants.TAG_CATEGORY_ID, cateId);
                getIntent().putExtra(Constants.TAG_SUBCATEGORY_ID, subcatId);
                getIntent().putExtra(Constants.TAG_SUPER_CATEGORY_ID, supercatId);
                getIntent().putExtra(Constants.CAT_NAME, selectedCatName);
                getIntent().putExtra(Constants.CAT_STATE, selectedFrom);
                if(selectedCatName != null && !selectedCatName.equals("")) {
                    this.setResult(RESULT_OK, getIntent());
                } else {
                    this.setResult(RESULT_CANCELED);
                }
                super.onBackPressed();
                break;
            default:
                getIntent().putExtra(Constants.FROM, "category");
                getIntent().putExtra(Constants.TAG_CATEGORY_ID, cateId);
                getIntent().putExtra(Constants.TAG_SUBCATEGORY_ID, subcatId);
                getIntent().putExtra(Constants.TAG_SUPER_CATEGORY_ID, supercatId);
                getIntent().putExtra(Constants.CAT_NAME, getIntent().getStringExtra(Constants.CAT_NAME));
                getIntent().putExtra(Constants.CAT_STATE, "");
                if(selectedCatName != null && !selectedCatName.equals("")) {
                    this.setResult(RESULT_OK, getIntent());
                } else {
                    this.setResult(RESULT_CANCELED);
                }
                super.onBackPressed();
                break;
        }
    }
}