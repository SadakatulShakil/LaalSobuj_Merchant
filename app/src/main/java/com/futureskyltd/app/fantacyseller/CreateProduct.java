package com.futureskyltd.app.fantacyseller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.external.ImagePicker;
import com.futureskyltd.app.helper.ImageCompression;
import com.futureskyltd.app.helper.ImageStorage;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;


public class CreateProduct extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();
    static final int ACTION_CATEGORY = 500, ACTION_POST_PRODUCT = 600;
    TextView addBtn, resetBtn, processBtn, categorySelect, screenTitle;
    ImageView backBtn, appName, categoryMark;
    RecyclerView prodImgList;
    String categoryId = "", subcategoryId = "", supercategoryId = "", selectedCategory = "", itemId = "", imageName = "", categoryName = "";
    ProductImgListAdapter imagesAdapter;
    ArrayList<HashMap<String, Object>> images = new ArrayList<HashMap<String, Object>>();
    ProgressDialog pd;
    EditText productTitle, productDes, productVideoUrl, barcodeNo;
    LinearLayoutManager horizontalLayoutManager;
    HashMap<String, String> productDatas = new HashMap<>();
    ArrayList<HashMap<String, Object>> colorList, sizeList, shipsToList = new ArrayList<>();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        addBtn = (TextView) findViewById(R.id.addProduct);
        resetBtn = (TextView) findViewById(R.id.resetProduct);
        processBtn = (TextView) findViewById(R.id.processBtn);
        appName = (ImageView) findViewById(R.id.appName);
        screenTitle = (TextView) findViewById(R.id.title);
        categorySelect = (TextView) findViewById(R.id.categorySelect);
        prodImgList = (RecyclerView) findViewById(R.id.prodImgList);
        productTitle = (EditText) findViewById(R.id.productTitle);
        productDes = (EditText) findViewById(R.id.productDes);
        categoryMark = (ImageView) findViewById(R.id.categoryMark);
        productVideoUrl = (EditText) findViewById(R.id.productVideoUrl);
        barcodeNo = (EditText) findViewById(R.id.barcodeNo);

        productTitle.setFilters(new InputFilter[]{FantacySellerApplication.EMOJI_FILTER});
        if (!AllProduct.isEditMode) {
            screenTitle.setText(getString(R.string.add_product));
        } else {
            screenTitle.setText(getString(R.string.edit_product));
        }

        addBtn.setText(getString(R.string.next_lbl));
        processBtn.setText(getString(R.string.step1_lbl));
        processBtn.setVisibility(View.VISIBLE);
        appName.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        resetBtn.setVisibility(GONE);

        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        prodImgList.setLayoutManager(horizontalLayoutManager);

        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        categorySelect.setOnClickListener(this);

        pd = new ProgressDialog(CreateProduct.this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        setImageAdapter();
        getDataFromIntent();

        categoryName = getString(R.string.select_category);
        categorySelect.setText(categoryName);
    }

    private void getDataFromIntent() {
        itemId = getIntent().getStringExtra(Constants.TAG_ITEM_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*get Data from Intent*/
        getDataFromIntent();
    }

    /**
     * For set images to listview
     **/
    private void setImageAdapter() {
        try {
            //Add Mode
            if ((!AllProduct.isEditMode)) {
                if (images != null)
                    images.clear();
                addPlusIcon();
                imagesAdapter = new ProductImgListAdapter(CreateProduct.this, images);
                prodImgList.setAdapter(imagesAdapter);
            } else {//Edit Mode
                getProductDetail(getIntent().getStringExtra(Constants.TAG_ITEM_ID));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getProductDetail(final String itemId) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_ITEM_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "getProductDetailRes=" + res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONObject temp = json.optJSONObject(Constants.TAG_RESULT);

                        productDatas.put(Constants.TAG_ID, DefensiveClass.optString(temp, Constants.TAG_ID));
                        productDatas.put(Constants.TAG_ITEM_ID, DefensiveClass.optString(temp, Constants.TAG_ID));
                        productDatas.put(Constants.TAG_ITEM_TITLE, DefensiveClass.optString(temp, Constants.TAG_ITEM_TITLE));
                        productDatas.put(Constants.TAG_ITEM_DESCRIPTION, DefensiveClass.optString(temp, Constants.TAG_ITEM_DESCRIPTION));
                        productDatas.put(Constants.TAG_CURRENCY, DefensiveClass.optString(temp, Constants.TAG_CURRENCY));
                        productDatas.put(Constants.TAG_MAIN_PRICE, DefensiveClass.optString(temp, Constants.TAG_MAIN_PRICE));
                        productDatas.put(Constants.TAG_PRICE, DefensiveClass.optString(temp, Constants.TAG_PRICE));
                        productDatas.put(Constants.TAG_LIKE_COUNT, DefensiveClass.optString(temp, Constants.TAG_LIKE_COUNT));
                        productDatas.put(Constants.TAG_DEAL_ENABLED, DefensiveClass.optString(temp, Constants.TAG_DEAL_ENABLED));
                        productDatas.put(Constants.TAG_DISCOUNT_PERCENTAGE, DefensiveClass.optString(temp, Constants.TAG_DISCOUNT_PERCENTAGE));
                        productDatas.put(Constants.TAG_DEAL_DATE, DefensiveClass.optString(temp, Constants.TAG_DEAL_DATE));
                        productDatas.put(Constants.TAG_QUANTITY, DefensiveClass.optString(temp, Constants.TAG_QUANTITY));
                        productDatas.put(Constants.TAG_COD, DefensiveClass.optString(temp, Constants.TAG_COD));
                        productDatas.put(Constants.TAG_SHIPPING_TIME, DefensiveClass.optString(temp, Constants.TAG_SHIPPING_TIME));
                        productDatas.put(Constants.TAG_PRODUCT_URL, DefensiveClass.optString(temp, Constants.TAG_PRODUCT_URL));
                        productDatas.put(Constants.TAG_VIDEO_URL, DefensiveClass.optString(temp, Constants.TAG_VIDEO_URL));
                        productDatas.put(Constants.TAG_SELECTED_CATEGORY, DefensiveClass.optString(temp, Constants.TAG_SELECTED_CATEGORY));
                        productDatas.put(Constants.TAG_CATEGORY_ID, DefensiveClass.optString(temp, Constants.TAG_CATEGORY_ID));
                        productDatas.put(Constants.TAG_SUBCATEGORY_ID, DefensiveClass.optString(temp, Constants.TAG_SUBCATEGORY_ID));
                        productDatas.put(Constants.TAG_SUPER_CATEGORY_ID, DefensiveClass.optString(temp, Constants.TAG_SUPER_CATEGORY_ID));
                        productDatas.put(Constants.TAG_SIZE_AVAILABILTY, DefensiveClass.optString(temp, Constants.TAG_SIZE_AVAILABILTY));
                        productDatas.put(Constants.TAG_FB_DISC_ENABLE, DefensiveClass.optString(temp, Constants.TAG_FB_DISC_ENABLE));
                        productDatas.put(Constants.TAG_FB_DISC_PERCENTAGE, DefensiveClass.optString(temp, Constants.TAG_FB_DISC_PERCENTAGE));
                        productDatas.put(Constants.TAG_BARCODE, DefensiveClass.optString(temp, Constants.TAG_BARCODE));
                        productDatas.put(Constants.TAG_EVERYWHERE_ELSE, DefensiveClass.optString(temp, "Everywhere_else"));
                        productDatas.put(Constants.TAG_COLOR_METHOD, DefensiveClass.optString(temp, Constants.TAG_COLOR_METHOD));
                        shipsToList = parseJSON(temp, "shippingTo");
                        categoryId = DefensiveClass.optString(temp, Constants.TAG_CATEGORY_ID);
                        JSONArray size = temp.optJSONArray(Constants.TAG_SIZE);
                        if (size == null) {
                            productDatas.put(Constants.TAG_SIZE, "");
                        } else if (size.length() == 0) {
                            productDatas.put(Constants.TAG_SIZE, "");
                        } else {
                            productDatas.put(Constants.TAG_SIZE, size.toString());
                        }

                        colorList = parseColorArray(temp);
                        sizeList = parseJSON(temp, "sizes");
                        JSONArray photos = temp.optJSONArray(Constants.TAG_PHOTOS);
                        if (photos == null) {
                            productDatas.put(Constants.TAG_PHOTOS, "");
                        } else if (photos.length() == 0) {
                            productDatas.put(Constants.TAG_PHOTOS, "");
                        } else {
                            productDatas.put(Constants.TAG_PHOTOS, photos.toString());
                        }

                        JSONArray selfies = temp.optJSONArray(Constants.TAG_PRODUCT_SELFIES);
                        if (selfies == null) {
                            productDatas.put(Constants.TAG_PRODUCT_SELFIES, "");
                        } else if (selfies.length() == 0) {
                            productDatas.put(Constants.TAG_PRODUCT_SELFIES, "");
                        } else {
                            productDatas.put(Constants.TAG_PRODUCT_SELFIES, selfies.toString());
                        }

                        setProductDetails(productDatas, colorList, sizeList, shipsToList);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(CreateProduct.this);
                        }
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
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
                Log.e(TAG, "getProductDetailError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                if (GetSet.isLogged()) {
                    map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                }
                map.put(Constants.TAG_ITEM_ID, String.valueOf(itemId));
                Log.v(TAG, "getProductDetailParams=" + map);
                return map;

            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(req, "prodDetails");

    }

    private ArrayList<HashMap<String, Object>> parseColorArray(JSONObject temp) {
        try {
            JSONArray colors = temp.getJSONArray(Constants.TAG_COLORS);
            colorList = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < colors.length(); i++) {
                HashMap<String, Object> hashMap3 = new HashMap<>();
                String tmp = colors.getString(i).substring(0, 1).toUpperCase() + colors.getString(i).substring(1).toLowerCase();
                hashMap3.put(Constants.TAG_COLOR_NAME, tmp);
                colorList.add(hashMap3);
            }
        } catch (Exception e) {

        }
        return colorList;
    }

    private void setProductDetails(HashMap<String, String> productsList, ArrayList<HashMap<String, Object>> colorlist, ArrayList<HashMap<String, Object>> sizeList, ArrayList<HashMap<String, Object>> shipsToList) {
        if (!productsList.get(Constants.TAG_SELECTED_CATEGORY).equals(""))
            categorySelect.setText(productsList.get(Constants.TAG_SELECTED_CATEGORY));
        if (!productsList.get(Constants.TAG_ITEM_TITLE).equals(""))
            productTitle.setText(productsList.get(Constants.TAG_ITEM_TITLE));
        if (!productsList.get(Constants.TAG_ITEM_DESCRIPTION).equals(""))
            productDes.setText(FantacySellerApplication.stripHtml(productsList.get(Constants.TAG_ITEM_DESCRIPTION)));
        if (!productsList.get(Constants.TAG_BARCODE).equals(""))
            barcodeNo.setText(productsList.get(Constants.TAG_BARCODE));
        if (productsList.get(Constants.TAG_VIDEO_URL) != "")
            productVideoUrl.setText(productsList.get(Constants.TAG_VIDEO_URL));
        images = getPhotos(productsList.get(Constants.TAG_PHOTOS));
        imagesAdapter = new ProductImgListAdapter(CreateProduct.this, images);
        prodImgList.setAdapter(imagesAdapter);
    }

    private ArrayList<HashMap<String, Object>> getPhotos(String json) {
        ArrayList<HashMap<String, Object>> photoAry = new ArrayList<HashMap<String, Object>>();
        try {
            JSONArray photos = new JSONArray(json);

            HashMap<String, Object> pmap = new HashMap<>();
            pmap.put(Constants.TYPE, "add");
            photoAry.add(pmap);
            for (int p = 0; p < photos.length(); p++) {
                JSONObject pobj = photos.getJSONObject(p);
                HashMap<String, Object> pmap1 = new HashMap<>();
                String imageOrg = DefensiveClass.optString(pobj, Constants.TAG_ITEM_URL_MAIN_ORG);
                String imgName = getFileNameFromUrl(imageOrg);
                pmap1.put(Constants.TYPE, "url");
                pmap1.put(Constants.TAG_IMAGE, imageOrg);
                pmap1.put(Constants.TAG_IMAGE_NAME, imgName);
                photoAry.add(pmap1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoAry;
    }

    private String getFileNameFromUrl(String imageOrg) {
        return imageOrg.substring(imageOrg.lastIndexOf('/') + 1);
    }

    private void callImagePicker() {
        if (checkPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, CreateProduct.this)) {
            ImagePicker.pickImage(CreateProduct.this, getString(R.string.select_image));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    private boolean checkPermissions(String[] permissionList, CreateProduct activity) {
        boolean isPermissionsGranted = false;
        for (String permission : permissionList) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                isPermissionsGranted = true;
            } else {
                isPermissionsGranted = false;
                break;
            }
        }
        return isPermissionsGranted;
    }

    private boolean shouldShowRationale(String[] permissions, CreateProduct activity) {
        boolean showRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showRationale = true;
            } else {
                showRationale = false;
                break;
            }
        }
        return showRationale;
    }

    private void addPlusIcon() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.TYPE, "add");
        images.add(0, map);
    }

    class uploadImage extends AsyncTask<String, String, String> {

        JSONObject jsonobject = null;
        String Json = "", status, exsistingFileName = "", imageurl;

        @Override
        protected String doInBackground(String... imgpath) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            DataInputStream inStream = null;
            StringBuilder builder = new StringBuilder();
            String lineEnd = "\r\n", twoHyphens = "--", boundary = "*****", urlString = Constants.IMAGE_UPLOAD_URL;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            try {
                exsistingFileName = imgpath[0];
                Log.v(" exsistingFileName", exsistingFileName);
                FileInputStream fileInputStream = new FileInputStream(new File(exsistingFileName));
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data;name=\"type\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("item");
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"images\";filename=\""
                        + exsistingFileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e("MediaPlayer", "Headers are written");
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                Log.v("buffer", "buffer" + buffer);

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    Log.v("bytesRead", "bytesRead" + bytesRead);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                Log.v("in", "" + in);
                while ((inputLine = in.readLine()) != null)
                    builder.append(inputLine);

                Log.e("MediaPlayer", "File is written");
                fileInputStream.close();
                Json = builder.toString();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.e("MediaPlayer", "error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e("MediaPlayer", "error: " + ioe.getMessage(), ioe);
            }
            try {
                inStream = new DataInputStream(conn.getInputStream());
                String str;
                while ((str = inStream.readLine()) != null) {
                    Log.e("MediaPlayer", "Server Response" + str);
                }
                inStream.close();
            } catch (IOException ioex) {
                Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
            }
            try {
                jsonobject = new JSONObject(Json);
                Log.v(TAG, "json" + Json);
                status = jsonobject.getString("status");
                if (status.equals("true")) {
                    JSONObject image = jsonobject.getJSONObject("result");
                    imageName = DefensiveClass.optString(image, Constants.TAG_NAME);
                    imageurl = DefensiveClass.optString(image, Constants.TAG_IMAGE);
                }

            } catch (JSONException e) {
                status = "false";
                e.printStackTrace();
            } catch (NullPointerException e) {
                status = "false";
                e.printStackTrace();
            } catch (Exception e) {
                status = "false";
                e.printStackTrace();
            }

            return imageurl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addImage("", "");
            imagesAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(final String imageViewurl) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addImage(imageViewurl, imageName);
                    imagesAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void addImage(String imageViewurl, String imageName) {
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put(Constants.TYPE, "url");
        map1.put(Constants.TAG_IMAGE, imageViewurl);
        map1.put(Constants.TAG_IMAGE_NAME, imageName);

        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put(Constants.TYPE, "url");
        map2.put(Constants.TAG_IMAGE, "");
        map2.put(Constants.TAG_IMAGE_NAME, "");
        if (images.contains(map2)) {
            images.remove(map2);
            images.add(map1);
        } else {
            images.add(map2);
        }
    }

    public class ProductImgListAdapter extends RecyclerView.Adapter<ProductImgListAdapter.ImgViewHolder> {

        ArrayList<HashMap<String, Object>> imgAry;
        private Context mContext;

        public ProductImgListAdapter(Context ctx2, ArrayList<HashMap<String, Object>> data) {
            mContext = ctx2;
            imgAry = data;
        }

        @Override
        public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addproduct_image, parent, false);
            return new ImgViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ImgViewHolder holder, int position) {
            final ImgViewHolder imgViewholder = (ImgViewHolder) holder;
            final HashMap<String, Object> tempMap = imgAry.get(position);
            if (tempMap.get(Constants.TYPE).equals("add")) {
                imgViewholder.delete.setVisibility(View.GONE);
                imgViewholder.singleImage.setVisibility(GONE);
                imgViewholder.imageLoader.setVisibility(View.GONE);
                imgViewholder.addIconImage.setVisibility(View.VISIBLE);
            } else {
                /*Control Image Load and its Visibility*/
                imgViewholder.addIconImage.setVisibility(GONE);
                imgViewholder.singleImage.setVisibility(GONE);
                imgViewholder.imageLoader.setVisibility(View.VISIBLE);
                imgViewholder.delete.setVisibility(View.GONE);
                if (tempMap.get(Constants.TAG_IMAGE) != "") {
                    Picasso.with(CreateProduct.this)
                            .load(tempMap.get(Constants.TAG_IMAGE).toString())
                            .into(imgViewholder.singleImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    imgViewholder.delete.setVisibility(View.VISIBLE);
                                    imgViewholder.addIconImage.setVisibility(GONE);
                                    imgViewholder.imageLoader.setVisibility(View.GONE);
                                    imgViewholder.singleImage.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {
                                    imgViewholder.imageLoader.setVisibility(View.VISIBLE);
                                    imgViewholder.singleImage.setVisibility(View.GONE);
                                }
                            });
                }
            }
        }

        @Override
        public int getItemCount() {
            return imgAry.size();
        }

        public class ImgViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            RelativeLayout imageLay;
            ImageView singleImage, addIconImage, delete;
            AVLoadingIndicatorView imageLoader;

            public ImgViewHolder(View itemView) {
                super(itemView);
                imageLay = (RelativeLayout) itemView.findViewById(R.id.imageLay);
                singleImage = (ImageView) itemView.findViewById(R.id.imageView);
                addIconImage = (ImageView) itemView.findViewById(R.id.addIcon);
                delete = (ImageView) itemView.findViewById(R.id.delete);
                imageLoader = (AVLoadingIndicatorView) itemView.findViewById(R.id.imageLoader);
                imageLay.setOnClickListener(this);
                delete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageLay:
                        if (getAdapterPosition() == 0) {
                            callImagePicker();
                        }
                        break;
                    case R.id.delete:
                        if (images.size() > 0) {
                            if (images.get(getAdapterPosition()).get(Constants.TYPE).equals("url")) {
                                images.remove(images.get(getAdapterPosition()));
                                notifyItemRemoved(getAdapterPosition());
                            }
                        }
                }
            }
        }
    }

    /*Methods to Parse JSON Data and return ArrayList*/
    private ArrayList<HashMap<String, Object>> parseJSON(JSONObject json, String type) {
        ArrayList<HashMap<String, Object>> arrList = new ArrayList<>();
        try {
            if (type.equals("sizes")) {
                arrList.clear();
                JSONArray jsonArray = json.getJSONArray(Constants.TAG_SIZE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put(Constants.TAG_SIZE, jsonObject1.getString(Constants.TAG_SIZE));
                    hashMap1.put(Constants.TAG_UNIT, jsonObject1.get(Constants.TAG_UNIT).toString());
                    hashMap1.put(Constants.TAG_PRICE, jsonObject1.get(Constants.TAG_PRICE).toString());
                    arrList.add(hashMap1);
                }
            } else {
                arrList.clear();
                JSONArray jsonArray = json.getJSONArray(Constants.TAG_SHIPPING_TO);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put(Constants.TAG_COUNTRY_ID, jsonObject1.getString(Constants.TAG_COUNTRY_ID));
                    hashMap1.put(Constants.TAG_COUNTRY_NAME, "");
                    hashMap1.put(Constants.TAG_SHIPPING_PRICE, jsonObject1.get(Constants.TAG_SHIPPING_PRICE).toString());
                    arrList.add(hashMap1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "requestCode=" + requestCode);
        if (resultCode == -1 && requestCode == ImagePicker.DEFAULT_REQUEST_CODE) {
            imagesAdapter.notifyDataSetChanged();
            /*Store and Upload Image*/
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            ImageStorage imageStorage = new ImageStorage(this);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            if (imageStorage.saveToSdCard(bitmap, timestamp + ".jpg").equals("success")) {
                File file = imageStorage.getImage(timestamp + ".jpg");
                String filepath = file.getAbsolutePath();
                Log.i(TAG, "selectedImageFile: " + filepath);
                ImageCompression imageCompression = new ImageCompression(CreateProduct.this) {
                    @Override
                    protected void onPostExecute(String imagePath) {
                        new uploadImage().execute(imagePath);
                    }
                };
                imageCompression.execute(filepath);
            } else {
                FantacySellerApplication.defaultSnack(CreateProduct.this.findViewById(R.id.main), getString(R.string.something_went_wrong), "alert");
            }
        }
        if (resultCode == -1 && requestCode == ACTION_CATEGORY) {
            categoryName = data.getStringExtra(Constants.CAT_NAME);
            categorySelect.setText(categoryName);

            if (categoryName.equals(getResources().getString(R.string.select_category))) {
                categoryMark.setVisibility(GONE);
            } else {
                categoryMark.setVisibility(View.VISIBLE);
            }

            categoryId = data.getStringExtra(Constants.TAG_CATEGORY_ID);
            subcategoryId = data.getStringExtra(Constants.TAG_SUBCATEGORY_ID);
            supercategoryId = data.getStringExtra(Constants.TAG_SUPER_CATEGORY_ID);

            productDatas.put(Constants.TAG_CATEGORY_ID, categoryId);
            productDatas.put(Constants.TAG_SUBCATEGORY_ID, subcategoryId);
            productDatas.put(Constants.TAG_SUPER_CATEGORY_ID, supercategoryId);
        } else if (resultCode == -1 && requestCode == ACTION_POST_PRODUCT) {
            if (data != null) {
                productDatas = (HashMap<String, String>) data.getSerializableExtra(Constants.INIT_PRODUCT_LIST);
                colorList = (ArrayList<HashMap<String, Object>>) data.getSerializableExtra(Constants.COLOR_LIST);
                shipsToList = (ArrayList<HashMap<String, Object>>) data.getSerializableExtra(Constants.SHIPSTO_LIST);
                sizeList = (ArrayList<HashMap<String, Object>>) data.getSerializableExtra(Constants.SIZE_LIST);
                Log.v(TAG, "productDatas=" + productDatas);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(CreateProduct.this, getString(R.string.select_image));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRationale(permissions, CreateProduct.this)) {
                            ActivityCompat.requestPermissions(CreateProduct.this, permissions, 100);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 100);
                            Toast.makeText(getApplicationContext(), getString(R.string.enable_camera_and_storage_permissions), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    private void showExitConfirmDialog() {
        Display display;
        display = getWindowManager().getDefaultDisplay();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.default_popup);
        dialog.getWindow().setLayout(display.getWidth() * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView yes = (TextView) dialog.findViewById(R.id.yes);
        TextView no = (TextView) dialog.findViewById(R.id.no);
        title.setText(getString(R.string.usr_msg_upload_cancel));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images.clear();
                dialog.dismiss();
                finish();
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

    private void checkValidBarCode(final String barcode, final HashMap<String, String> productDatas) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_CHECK_BARCODE, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "checkValidBarCodeRes=" + res);

                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Intent intent = new Intent(CreateProduct.this, PostProduct.class);
                        intent.putExtra(Constants.TAG_SELECTED_CATEGORY, selectedCategory);
                        intent.putExtra(Constants.TAG_ITEM_ID, itemId);
                        intent.putExtra(Constants.INIT_PRODUCT_LIST, productDatas);
                        intent.putExtra(Constants.PROD_IMG_LIST, images);
                        intent.putExtra(Constants.COLOR_LIST, colorList);
                        intent.putExtra(Constants.SIZE_LIST, sizeList);
                        intent.putExtra(Constants.SHIPSTO_LIST, shipsToList);
                        startActivityForResult(intent, ACTION_POST_PRODUCT);
                        Log.d("selectedColorLists-send", colorList + "");
                        Log.v("productDatas", "productDatas=" + productDatas);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(CreateProduct.this);
                        }
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        FantacySellerApplication.showToast(CreateProduct.this, DefensiveClass.optString(json, Constants.TAG_MESSAGE), Toast.LENGTH_SHORT);
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
                Log.e(TAG, "checkValidBarCodeError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                if (GetSet.isLogged()) {
                    map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                }
                if (String.valueOf(itemId) != null)
                    map.put(Constants.TAG_ITEM_ID, String.valueOf(itemId));
                else
                    map.put(Constants.TAG_ITEM_ID, "");
                map.put(Constants.TAG_BARCODE, barcode);
                Log.v(TAG, "checkValidBarCodeParams=" + map);
                return map;

            }
        };
        FantacySellerApplication.getInstance().addToRequestQueue(req, "prodDetails");

    }

    @Override
    public void onBackPressed() {
        if ((categoryId.equals("")) && ((images.size() == 1) && (!images.contains(new HashMap<String, Object>().put(Constants.TYPE, "add")))))// || productTitle.getText().toString().equals("") || productDes.getText().toString().equals("") || barcodeNo.getText().toString().equals("")) {
            finish();
        else {
            images.clear();
            showExitConfirmDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addProduct:
                Log.d(TAG, "onClick: " + "Success");
                selectedCategory = categorySelect.getText().toString();
                FantacySellerApplication.hideSoftKeyboard(CreateProduct.this, addBtn);
                if (categoryId.equals("")) {
                    FantacySellerApplication.showToast(CreateProduct.this, getString(R.string.reqd_category), Toast.LENGTH_LONG);
                } else if ((images.size() == 1) && (!images.contains(new HashMap<String, Object>().put(Constants.TYPE, "add")))) {
                    FantacySellerApplication.showToast(CreateProduct.this, getString(R.string.reqd_image), Toast.LENGTH_LONG);
                } else if (productTitle.getText().toString().trim().equals("")) {
                    FantacySellerApplication.showToast(CreateProduct.this, getString(R.string.reqd_prod_title), Toast.LENGTH_LONG);
                } else if (productDes.getText().toString().trim().equals("")) {
                    FantacySellerApplication.showToast(CreateProduct.this, getString(R.string.reqd_prod_desc), Toast.LENGTH_LONG);
                } else if (productVideoUrl.getText().toString().trim().length() > 0 && !productVideoUrl.getText().toString().contains("watch?v=")) {
                    FantacySellerApplication.showToast(CreateProduct.this, getString(R.string.enter_valid_video_url), Toast.LENGTH_LONG);
                } else if (barcodeNo.getText().toString().equals("")) {
                    FantacySellerApplication.showToast(CreateProduct.this, getString(R.string.reqd_barcode), Toast.LENGTH_LONG);
                } else {
                    productDatas.put(Constants.TAG_ITEM_TITLE, productTitle.getText().toString());
                    productDatas.put(Constants.TAG_ITEM_DESCRIPTION, productDes.getText().toString());
                    productDatas.put(Constants.TAG_VIDEO_URL, productVideoUrl.getText().toString());
                    productDatas.put(Constants.TAG_BARCODE, barcodeNo.getText().toString());
                    //checkValidBarCode(barcodeNo.getText().toString(), productDatas);
                    Intent intent = new Intent(CreateProduct.this, PostProduct.class);
                    intent.putExtra(Constants.TAG_SELECTED_CATEGORY, selectedCategory);
                    intent.putExtra(Constants.TAG_ITEM_ID, itemId);
                    intent.putExtra(Constants.INIT_PRODUCT_LIST, productDatas);
                    intent.putExtra(Constants.PROD_IMG_LIST, images);
                    intent.putExtra(Constants.COLOR_LIST, colorList);
                    intent.putExtra(Constants.SIZE_LIST, sizeList);
                    intent.putExtra(Constants.SHIPSTO_LIST, shipsToList);
                    startActivityForResult(intent, ACTION_POST_PRODUCT);
                    Log.d("selectedColorLists-send", colorList + "");
                    Log.d("productDatas", "productDatas=" + productDatas);
                }
                break;
            case R.id.categorySelect:
                Intent intent = new Intent(CreateProduct.this, ChooseCategory.class);
                if (categorySelect.getText().toString().equals(getResources().getString(R.string.select_category))) {
                    intent.putExtra(Constants.TAG_CATEGORY_ID, "");
                } else {
                    intent.putExtra(Constants.TAG_CATEGORY_ID, categoryId);
                    intent.putExtra(Constants.TAG_SUBCATEGORY_ID, subcategoryId);
                    intent.putExtra(Constants.TAG_SUPER_CATEGORY_ID, supercategoryId);
                    intent.putExtra(Constants.CAT_NAME, categorySelect.getText().toString());
                }
                startActivityForResult(intent, ACTION_CATEGORY);
                break;
            case R.id.backBtn:
                if ((categoryId.equals("")) && ((images.size() == 1) && (!images.contains(new HashMap<String, Object>().put(Constants.TYPE, "add")))))// || productTitle.getText().toString().equals("") || productDes.getText().toString().equals("") || barcodeNo.getText().toString().equals("")) {
                    finish();
                else {
                    showExitConfirmDialog();
                }
                break;

        }
    }
}
