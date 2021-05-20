package com.futureskyltd.app.fantacyseller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.external.CirclePageIndicator;
import com.futureskyltd.app.external.MyTagHandler;
import com.futureskyltd.app.external.URLSpanNoUnderline;
import com.futureskyltd.app.helper.NetworkReceiver;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProductDetail extends AppCompatActivity implements View.OnClickListener, NetworkReceiver.ConnectivityReceiverListener {

    public final String TAG = this.getClass().getSimpleName();
    ImageView backBtn, likedBtn, codImage, cancelBtn, shippingImg;
    RelativeLayout toolBarLay, progress, progressLay;
    Display display;
    String itemId;
    int screenheight, position, imageHeight, selfiWidth, selectedSizePosition, itemWidth;
    TextView itemPrice, description, playText, itemName, discountPrice, discountPercent, likeCount, codText, shipping, availableQuantity;
    ViewPager imagePager;
    CirclePageIndicator pagerIndicator;
    LinearLayout bottomLay, sizeLay, playLay, codLay, shippingLay;
    RecyclerView sizeList;
    WebView mWebView;
    FloatingActionButton editProdfab;
    private Toolbar toolbar;
    HashMap<String, String> productMap = new HashMap<>();
    CoordinatorLayout parentLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        FantacySellerApplication.setLocale(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        cancelBtn = (ImageView) findViewById(R.id.cancelBtn);
        toolBarLay = (RelativeLayout) findViewById(R.id.toolBarLay);
        editProdfab = (FloatingActionButton) findViewById(R.id.fab);
        progress = (RelativeLayout) findViewById(R.id.progress);
        itemPrice = (TextView) findViewById(R.id.itemPrice);
        itemName = (TextView) findViewById(R.id.itemName);
        discountPrice = (TextView) findViewById(R.id.discountPrice);
        discountPercent = (TextView) findViewById(R.id.discountPercent);
        imagePager = (ViewPager) findViewById(R.id.imagePager);
        likedBtn = (ImageView) findViewById(R.id.likedBtn);
        shippingImg = (ImageView) findViewById(R.id.shippingImg);
        likeCount = (TextView) findViewById(R.id.productLikeCount);
        progressLay = (RelativeLayout) findViewById(R.id.progress);
        codText = (TextView) findViewById(R.id.codText);
        parentLay = (CoordinatorLayout) findViewById(R.id.parentLay);
        codImage = (ImageView) findViewById(R.id.codImage);
        shipping = (TextView) findViewById(R.id.shipping);
        availableQuantity = (TextView) findViewById(R.id.availableQuantity);
        pagerIndicator = (CirclePageIndicator) findViewById(R.id.pagerIndicator);
        description = (TextView) findViewById(R.id.description);
        bottomLay = (LinearLayout) findViewById(R.id.bottomLay);
        sizeList = (RecyclerView) findViewById(R.id.sizeList);
        sizeLay = (LinearLayout) findViewById(R.id.sizeLay);
        mWebView = (WebView) findViewById(R.id.webView);
        playLay = (LinearLayout) findViewById(R.id.playLay);
        playText = (TextView) findViewById(R.id.playText);
        codLay = (LinearLayout) findViewById(R.id.codLay);
        shippingLay = (LinearLayout) findViewById(R.id.shippingLay);

        display = this.getWindowManager().getDefaultDisplay();
        imageHeight = display.getWidth();
        screenheight = display.getHeight();
        itemWidth = display.getWidth() * 37 / 100;
        selfiWidth = FantacySellerApplication.dpToPx(ProductDetail.this, 100);

        itemId = getIntent().getExtras().getString(Constants.TAG_ITEM_ID);
        position = (int) getIntent().getExtras().get(Constants.TAG_POSITION);

        progress.setVisibility(View.VISIBLE);
        progressLay.setVisibility(View.VISIBLE);

        getItemDetails(itemId);

        backBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        editProdfab.setOnClickListener(this);

        FantacySellerApplication.getInstance().setConnectivityListener(this);
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = (Spannable) (textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkReceiver.isConnected()) {
            FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), true);
        } else {
            FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), true);
        if (mWebView.getVisibility() == View.VISIBLE) {
            float x = mWebView.getWidth() / 2;
            float y = mWebView.getHeight() / 2;
            MotionEvent motionEventDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
            MotionEvent motionEventUp = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 200, MotionEvent.ACTION_UP, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
            mWebView.dispatchTouchEvent(motionEventDown);
            mWebView.dispatchTouchEvent(motionEventUp);
        }
    }

    private ArrayList<HashMap<String, String>> setProductSelfies(String json) {
        ArrayList<HashMap<String, String>> photoAry = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray photos = new JSONArray(json);
            for (int p = 0; p < photos.length(); p++) {
                JSONObject pobj = photos.getJSONObject(p);
                HashMap<String, String> pmap = new HashMap<>();
                pmap.put(Constants.TAG_ITEM_URL_MAIN_350, DefensiveClass.optString(pobj, Constants.TAG_ITEM_URL_MAIN_350));
                pmap.put(Constants.TAG_IMAGE_ORG, DefensiveClass.optString(pobj, Constants.TAG_IMAGE_ORG));
                pmap.put(Constants.TAG_USER_ID, DefensiveClass.optString(pobj, Constants.TAG_USER_ID));
                pmap.put(Constants.TAG_USER_NAME, DefensiveClass.optString(pobj, Constants.TAG_USER_NAME));
                pmap.put(Constants.TAG_USER_IMAGE, DefensiveClass.optString(pobj, Constants.TAG_USER_IMAGE));
                photoAry.add(pmap);
            }
            imagePager.getLayoutParams().height = imageHeight;
            imagePager.setAdapter(new ImagePagerAdapter(ProductDetail.this, photoAry));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoAry;
    }

    private void getItemDetails(final String itemId) {
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_ITEM_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    Log.v(TAG, "onResponse= "+res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        JSONObject temp = json.optJSONObject(Constants.TAG_RESULT);
                        productMap.put(Constants.TAG_ID, DefensiveClass.optString(temp, Constants.TAG_ID));
                        productMap.put(Constants.TAG_ITEM_TITLE, DefensiveClass.optString(temp, Constants.TAG_ITEM_TITLE));
                        productMap.put(Constants.TAG_ITEM_DESCRIPTION, DefensiveClass.optString(temp, Constants.TAG_ITEM_DESCRIPTION));
                        productMap.put(Constants.TAG_CURRENCY, DefensiveClass.optString(temp, Constants.TAG_CURRENCY));
                        productMap.put(Constants.TAG_MAIN_PRICE, DefensiveClass.optString(temp, Constants.TAG_MAIN_PRICE));
                        productMap.put(Constants.TAG_LIKE_COUNT, DefensiveClass.optString(temp, Constants.TAG_LIKE_COUNT));
                        productMap.put(Constants.TAG_DEAL_ENABLED, DefensiveClass.optString(temp, Constants.TAG_DEAL_ENABLED));
                        productMap.put(Constants.TAG_DISCOUNT_PERCENTAGE, DefensiveClass.optString(temp, Constants.TAG_DISCOUNT_PERCENTAGE));
                        productMap.put(Constants.TAG_DEAL_DATE, DefensiveClass.optString(temp, Constants.TAG_DEAL_DATE));
                        productMap.put(Constants.TAG_QUANTITY, DefensiveClass.optString(temp, Constants.TAG_QUANTITY));
                        productMap.put(Constants.TAG_MIN_QUANTITY, DefensiveClass.optString(temp, Constants.TAG_MIN_QUANTITY));
                        productMap.put(Constants.TAG_COD, DefensiveClass.optString(temp, Constants.TAG_COD));
                        productMap.put(Constants.TAG_SHIPPING_TIME, DefensiveClass.optString(temp, Constants.TAG_SHIPPING_TIME));
                        productMap.put(Constants.TAG_UNIT_NAME, DefensiveClass.optString(temp, Constants.TAG_UNIT_NAME));
                        productMap.put(Constants.TAG_PRODUCT_URL, DefensiveClass.optString(temp, Constants.TAG_PRODUCT_URL));
                        productMap.put(Constants.TAG_VIDEO_URL, DefensiveClass.optString(temp, Constants.TAG_VIDEO_URL));

                        JSONArray size = temp.optJSONArray(Constants.TAG_SIZE);
                        if (size == null) {
                            productMap.put(Constants.TAG_SIZE, "");
                        } else if (size.length() == 0) {
                            productMap.put(Constants.TAG_SIZE, "");
                        } else {
                            productMap.put(Constants.TAG_SIZE, size.toString());
                        }

                        JSONArray photos = temp.optJSONArray(Constants.TAG_PHOTOS);
                        if (photos == null) {
                            productMap.put(Constants.TAG_PHOTOS, "");
                        } else if (photos.length() == 0) {
                            productMap.put(Constants.TAG_PHOTOS, "");
                        } else {
                            productMap.put(Constants.TAG_PHOTOS, photos.toString());
                        }

                        JSONArray selfies = temp.optJSONArray(Constants.TAG_PRODUCT_SELFIES);
                        if (selfies == null) {
                            productMap.put(Constants.TAG_PRODUCT_SELFIES, "");
                        } else if (selfies.length() == 0) {
                            productMap.put(Constants.TAG_PRODUCT_SELFIES, "");
                        } else {
                            productMap.put(Constants.TAG_PRODUCT_SELFIES, selfies.toString());
                        }
                        setItemDetails(productMap);
                        progressLay.setVisibility(View.GONE);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        progressLay.setVisibility(View.GONE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(ProductDetail.this);
                        }
                    } else {
                        progressLay.setVisibility(View.GONE);
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
                Log.e(TAG, "getItemDetailsError: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                if (GetSet.isLogged()) {
                    map.put(Constants.TAG_USER_ID, GetSet.getUserId());
                }
                map.put(Constants.TAG_ITEM_ID, String.valueOf(itemId));
                Log.v(TAG, "getItemDetailsParams=" + map);
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

    public void setLikeCount(String likedCount, TextView likeCount) {
        if (likedCount.equals("1"))
            likeCount.setText(likedCount + " " + getString(R.string.like));
        else
            likeCount.setText(likedCount + " " + getString(R.string.likes));
    }

    @SuppressLint("RestrictedApi")
    private void setItemDetails(final HashMap<String, String> productMap) {
        ViewCompat.setNestedScrollingEnabled(imagePager, false);
        itemName.setText(productMap.get(Constants.TAG_ITEM_TITLE));
        bottomLay.setVisibility(View.VISIBLE);
        String shippingTime = "";
        toolBarLay.setVisibility(View.VISIBLE);
        parentLay.setVisibility(View.VISIBLE);
        shippingLay.setVisibility(View.VISIBLE);
        likedBtn.setVisibility(View.VISIBLE);
        editProdfab.setVisibility(View.VISIBLE);
        switch (productMap.get(Constants.TAG_SHIPPING_TIME)) {
            case "১ দিন":
                shippingTime = getString(R.string.one_business_day);
                break;
            case "২ দিন":
                shippingTime = getString(R.string.one_two_business);
                break;
            case "৩ দিন":
                shippingTime = getString(R.string.one_three_business);
                break;
            case "১-৪ দিন":
                shippingTime = getString(R.string.three_five_business);
                break;
            case "১-২ সপ্তাহ":
                shippingTime = getString(R.string.one_two_week_business);
                break;
            case "২-৩ সপ্তাহ":
                shippingTime = getString(R.string.two_three_week_business);
                break;
            case "৩-৪ সপ্তাহ":
                shippingTime = getString(R.string.three_four_week_business);
                break;
            case "৪-৬ সপ্তাহ":
                shippingTime = getString(R.string.four_six_week_business);
                break;
            case "৬-৮ সপ্তাহ":
                shippingTime = getString(R.string.six_eight_week_business);
                break;
            default:
                shippingTime = productMap.get(Constants.TAG_SHIPPING_TIME);
                break;
        }
        shipping.setText(shippingTime);
        String replaceVar = productMap.get(Constants.TAG_ITEM_DESCRIPTION).trim().replace("\n","<br>");
        Spannable spannedText = (Spannable) Html.fromHtml(replaceVar, null, new MyTagHandler());
        description.setText(spannedText);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        stripUnderlines(description);
        if (!productMap.get(Constants.TAG_VIDEO_URL).equals("")) {
            playLay.setVisibility(View.VISIBLE);
        } else {
            playLay.setVisibility(View.GONE);
        }
        if (productMap.get(Constants.TAG_DEAL_ENABLED).equalsIgnoreCase("true") &&
                !productMap.get(Constants.TAG_DISCOUNT_PERCENTAGE).equals("0") && !productMap.get(Constants.TAG_DISCOUNT_PERCENTAGE).equals("") &&
                FantacySellerApplication.isDealEnabled(productMap.get(Constants.TAG_DEAL_DATE))) {
            discountPrice.setVisibility(View.VISIBLE);
            discountPercent.setVisibility(View.VISIBLE);
            discountPrice.setPaintFlags(discountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            float priceValue = Float.parseFloat(productMap.get(Constants.TAG_MAIN_PRICE)) - (Float.parseFloat(productMap.get(Constants.TAG_MAIN_PRICE))
                    * ((Float.parseFloat(productMap.get(Constants.TAG_DISCOUNT_PERCENTAGE))) / 100.0f));
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            itemPrice.setText(getENtoBN(productMap.get(Constants.TAG_CURRENCY) + " " + df.format(priceValue)));
            discountPrice.setText(getENtoBN(productMap.get(Constants.TAG_CURRENCY) + " " + productMap.get(Constants.TAG_MAIN_PRICE)));
            discountPercent.setText(getENtoBN(productMap.get(Constants.TAG_DISCOUNT_PERCENTAGE) + "% off"));
        } else {
            discountPrice.setVisibility(View.GONE);
            discountPercent.setVisibility(View.GONE);
            itemPrice.setText(getENtoBN(productMap.get(Constants.TAG_CURRENCY) + " " + productMap.get(Constants.TAG_MAIN_PRICE)));
        }
        likedBtn.setImageResource(R.drawable.liked);
        setLikeCount(productMap.get(Constants.TAG_LIKE_COUNT), likeCount);
        if (productMap.get(Constants.TAG_COD).equalsIgnoreCase("true")) {
            codLay.setVisibility(View.VISIBLE);
            codText.setText(getString(R.string.cod_available));
            codText.setTextColor(ContextCompat.getColor(ProductDetail.this, R.color.colorPrimary));
            codImage.setImageResource(R.drawable.tick_color);
            codImage.setColorFilter(ContextCompat.getColor(ProductDetail.this, R.color.colorPrimary));
        } else {
            codLay.setVisibility(View.GONE);
        }
        playLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playText.getVisibility() == View.VISIBLE) {
                    toolBarLay.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.VISIBLE);

                    WebChromeClient mWebChromeClient = new WebChromeClient() {
                        public void onProgressChanged(WebView view, int newProgress) {
                        }
                    };

                    int width = display.getWidth();
                    int height = imageHeight;

                    mWebView.getLayoutParams().height = height;
                    mWebView.getLayoutParams().width = width;

                    mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                    mWebView.setWebChromeClient(mWebChromeClient);
                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            Log.v(TAG, "onPageStarted=" + url);
                        }

                        public void onPageFinished(WebView view, String url) {
                            Log.v(TAG, "onPageFinished=" + url);
                            float x = view.getWidth() / 2;
                            float y = view.getHeight() / 2;
                            MotionEvent motionEventDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
                            MotionEvent motionEventUp = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 200, MotionEvent.ACTION_UP, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
                            view.dispatchTouchEvent(motionEventDown);
                            view.dispatchTouchEvent(motionEventUp);
                        }
                    });
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.getSettings().setAppCacheEnabled(true);
                    mWebView.setInitialScale(1);
                    mWebView.getSettings().setLoadWithOverviewMode(true);
                    mWebView.getSettings().setUseWideViewPort(true);
                    if (Build.VERSION.SDK_INT > 16) {
                        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                    }

                    mWebView.loadData("<html><body style=\"margin:0 0 0 0; padding:0 0 0 0;\"><iframe class=\"youtube-player\" " + "width=\"" + "100%" + "\" height=\"" + "100%" + "\" src=\"https://www.youtube.com/embed/" + FantacySellerApplication.extractYoutubeId(productMap.get(Constants.TAG_VIDEO_URL)) + "?autoplay=1&loop=0&autohide=2&controls=0&showinfo=0&theme=dark&modestbranding=1&fs=0&rel=0\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
                            , "text/html", "UTF-8");
                } else {
                    playText.setVisibility(View.VISIBLE);
                }
            }
        });
        if (productMap.get(Constants.TAG_SIZE).equals("")) {
            sizeLay.setVisibility(View.GONE);
        } else {
            ArrayList<HashMap<String, String>> sizeAry = FantacySellerApplication.getSize(productMap.get(Constants.TAG_SIZE));
            if (sizeAry.size() == 0) {
                sizeLay.setVisibility(View.GONE);
            } else {
                sizeLay.setVisibility(View.VISIBLE);
                sizeList.setHasFixedSize(true);
                LinearLayoutManager sizeManager = new LinearLayoutManager(ProductDetail.this, LinearLayoutManager.HORIZONTAL, false);
                sizeList.setLayoutManager(sizeManager);

                DividerItemDecoration itemDivider = new DividerItemDecoration(sizeList.getContext(),
                        sizeManager.getOrientation());
                itemDivider.setDrawable(getResources().getDrawable(R.drawable.whitespace));
                sizeList.addItemDecoration(itemDivider);
                if (FantacySellerApplication.isRTL(this)) {
                    sizeList.setPadding(FantacySellerApplication.dpToPx(this, 15), 0, 0, 0);
                } else {
                    sizeList.setPadding(FantacySellerApplication.dpToPx(this, 15), 0, FantacySellerApplication.dpToPx(this, 15), 0);
                }
                sizeList.setAdapter(new SizeViewAdapter(ProductDetail.this, sizeAry, position, productMap));
            }
        }
        try {
            availableQuantity.setVisibility(View.VISIBLE);
            availableQuantity.setText("আর মাত্র "+Integer.parseInt(productMap.get(Constants.TAG_QUANTITY))+ " "+productMap.get(Constants.TAG_UNIT_NAME)+" বাকি আছে");
            /*availableQuantity.setText(getString(R.string.only_qty_available, Integer.parseInt(productMap.get(Constants.TAG_QUANTITY))));*/
        } catch (NumberFormatException e) {
            e.printStackTrace();
            availableQuantity.setVisibility(View.GONE);
        }
        if (!productMap.get(Constants.TAG_PHOTOS).equals("")) {
            ArrayList<HashMap<String, String>> photosAry = getPhotos(productMap.get(Constants.TAG_PHOTOS));
            imagePager.setAdapter(new ImagePagerAdapter(ProductDetail.this, photosAry));
            if (photosAry.size() > 1) {
                pagerIndicator.setVisibility(View.VISIBLE);
                pagerIndicator.setViewPager(imagePager);
            } else {
                pagerIndicator.setVisibility(View.GONE);
            }
        }
        Log.d(TAG, "imageurl-init: " + productMap);
        setProductSelfies(productMap.get(Constants.TAG_PHOTOS));
    }

    private ArrayList<HashMap<String, String>> getPhotos(String json) {
        ArrayList<HashMap<String, String>> photoAry = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray photos = new JSONArray(json);
            for (int p = 0; p < photos.length(); p++) {
                JSONObject pobj = photos.getJSONObject(p);
                HashMap<String, String> pmap = new HashMap<>();
                pmap.put(Constants.TAG_ITEM_URL_MAIN_350, DefensiveClass.optString(pobj, Constants.TAG_ITEM_URL_MAIN_350));
                pmap.put(Constants.TAG_ITEM_URL_MAIN_ORG, DefensiveClass.optString(pobj, Constants.TAG_ITEM_URL_MAIN_ORG));
                pmap.put(Constants.TAG_HEIGHT, DefensiveClass.optString(pobj, Constants.TAG_HEIGHT));
                pmap.put(Constants.TAG_WIDTH, DefensiveClass.optString(pobj, Constants.TAG_WIDTH));
                pmap.put(Constants.TYPE, "image");
                photoAry.add(pmap);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), isConnected);
    }


    class ImagePagerAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> imageAry;

        public ImagePagerAdapter(Context act, ArrayList<HashMap<String, String>> newary) {
            this.imageAry = newary;
            this.context = act;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return imageAry.size();
        }

        public Object instantiateItem(ViewGroup collection, final int position) {
            final View itemView = inflater.inflate(R.layout.product_detail_image, collection, false);
            itemView.setTag("pos" + position);
            final ImageView image = (ImageView) itemView.findViewById(R.id.image);
            Log.d("imageurl", imageAry.get(position).get(Constants.TAG_ITEM_URL_MAIN_350) + " ");

            if (imageAry.get(position).get(Constants.TAG_ITEM_URL_MAIN_350) != null && !imageAry.get(position).get(Constants.TAG_ITEM_URL_MAIN_350).equals("")) {
                Picasso.with(context).load(imageAry.get(position).get(Constants.TAG_ITEM_URL_MAIN_350)).into(image);
            }
            ((ViewPager) collection).addView(itemView, 0);
            return itemView;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    public class SizeViewAdapter extends RecyclerView.Adapter<SizeViewAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> sizes;
        Context context;
        int pos;
        HashMap<String, String> itemMap;

        public SizeViewAdapter(Context context, ArrayList<HashMap<String, String>> sizes, int pos, HashMap<String, String> itemDetails) {
            this.sizes = sizes;
            this.context = context;
            this.pos = pos;
            this.itemMap = itemDetails;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.size_list_items, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.size.setText(sizes.get(position).get(Constants.TAG_SIZE));
            if (selectedSizePosition == position) {
                try {
                    availableQuantity.setVisibility(View.VISIBLE);
                    availableQuantity.setText("আর মাত্র "+Integer.parseInt(sizes.get(position).get(Constants.TAG_UNIT))+ " "+productMap.get(Constants.TAG_UNIT_NAME)+" বাকি আছে");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    availableQuantity.setVisibility(View.GONE);
                }
                holder.size.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.sizeLay.setBackground(ContextCompat.getDrawable(context, R.drawable.primary_color_sharp_corner));

                if (itemMap.get(Constants.TAG_DEAL_ENABLED).equalsIgnoreCase("true") &&
                        !itemMap.get(Constants.TAG_DISCOUNT_PERCENTAGE).equals("0") && !itemMap.get(Constants.TAG_DISCOUNT_PERCENTAGE).equals("")) {
                    discountPrice.setVisibility(View.VISIBLE);
                    discountPercent.setVisibility(View.VISIBLE);
                    discountPrice.setPaintFlags(discountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    float priceValue = Float.parseFloat(sizes.get(position).get(Constants.TAG_MAIN_PRICE)) - (Float.parseFloat(sizes.get(position).get(Constants.TAG_MAIN_PRICE))
                            * ((Float.parseFloat(itemMap.get(Constants.TAG_DISCOUNT_PERCENTAGE))) / 100.0f));

                    itemPrice.setText(getENtoBN(itemMap.get(Constants.TAG_CURRENCY) + " " + FantacySellerApplication.decimal.format(priceValue)));
                    discountPrice.setText(getENtoBN(itemMap.get(Constants.TAG_CURRENCY) + " " + sizes.get(position).get(Constants.TAG_MAIN_PRICE)));
                    discountPercent.setText(getENtoBN(itemMap.get(Constants.TAG_DISCOUNT_PERCENTAGE) + "% off"));
                } else {
                    discountPrice.setVisibility(View.GONE);
                    discountPercent.setVisibility(View.GONE);

                    itemPrice.setText(getENtoBN(itemMap.get(Constants.TAG_CURRENCY) + " " + sizes.get(position).get(Constants.TAG_MAIN_PRICE)));
                }

            } else {
                holder.sizeLay.setBackground(ContextCompat.getDrawable(context, R.drawable.divider_text_sharp_corner));
                holder.size.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
            }
        }

        @Override
        public int getItemCount() {
            return sizes.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView size;
            LinearLayout sizeLay;

            public MyViewHolder(View view) {
                super(view);
                size = (TextView) view.findViewById(R.id.size);
                sizeLay = (LinearLayout) view.findViewById(R.id.sizeLay);
                sizeLay.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sizeLay:
                        selectedSizePosition = getAdapterPosition();
                        notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    public String getENtoBN(String string)
    {
        Character bangla_number[]={'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        Character eng_number[]={'0','1','2','3','4','5','6','7','8','9'};
        String values = "";
        char[] character = string.toCharArray();
        for (int i=0; i<character.length ; i++) {
            Character c = ' ';
            for (int j = 0; j < eng_number.length; j++) {
                if(character[i]==eng_number[j])
                {
                    c=bangla_number[j];
                    break;
                }else {
                    c=character[i];
                }
            }
            values=values+c;
        }
        return values;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            Intent g = new Intent(Intent.ACTION_SEND);
            g.setType("text/plain");
            g.putExtra(Intent.EXTRA_TEXT, productMap.get(Constants.TAG_PRODUCT_URL));
            startActivity(Intent.createChooser(g, "Share"));
            return true;
        } else if (id == R.id.deleteProduct) {
            deleteDialog(itemId);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.share).setVisible(getIntent().getStringExtra(Constants.TAG_PRODUCT_STATUS).equalsIgnoreCase("publish"));
        return true;
    }

    private void switchContent(String itemId) {
        Log.d("itemId", itemId + " ");
        AllProduct.isEditMode = true;
        Intent intent = new Intent(ProductDetail.this, CreateProduct.class);
        intent.putExtra(Constants.TAG_ITEM_ID, itemId);
        startActivity(intent);
    }

    private void deleteDialog(final String item_id) {
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
                    Log.i(TAG, "deleteProductRes: " + res);
                    JSONObject json = new JSONObject(res);
                    progress.setVisibility(View.GONE);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        FantacySellerApplication.showStatusDialog(ProductDetail.this, true, getString(R.string.prod_delete_success_msg), AllProduct.class);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("error")) {
                        String message = DefensiveClass.optString(json, Constants.TAG_MESSAGE);
                        if (message.equals(getString(R.string.admin_error)) || message.equals(getString(R.string.admin_delete_error))) {
                            finish();
                            FantacySellerApplication.logout(ProductDetail.this);
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
                Log.e(TAG, "deleteProductError: " + error.getMessage());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                switchContent(itemId);
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.cancelBtn:
                TextView playText = (TextView) findViewById(R.id.playText);
                float x = mWebView.getWidth() / 2;
                float y = mWebView.getHeight() / 2;
                MotionEvent motionEventDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
                MotionEvent motionEventUp = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 200, MotionEvent.ACTION_UP, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
                mWebView.dispatchTouchEvent(motionEventDown);
                mWebView.dispatchTouchEvent(motionEventUp);
                mWebView.loadUrl("");
                mWebView.stopLoading();
                mWebView.clearCache(true);
                mWebView.clearHistory();
                mWebView.setVisibility(View.GONE);
                playText.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                toolBarLay.setVisibility(View.VISIBLE);
                break;
        }
    }
}