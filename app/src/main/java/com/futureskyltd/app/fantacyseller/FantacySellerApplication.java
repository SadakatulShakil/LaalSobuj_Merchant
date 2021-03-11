package com.futureskyltd.app.fantacyseller;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.futureskyltd.app.external.FontsOverride;
import com.futureskyltd.app.external.RateThisApp;
import com.futureskyltd.app.helper.NetworkReceiver;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hitasoft on 11/5/17.
 */

@ReportsCrashes(mailTo = "developer.lalsobuj@gmail.com")
public class FantacySellerApplication extends Application {

    private static FantacySellerApplication mInstance;
    public static final String TAG = FantacySellerApplication.class.getSimpleName();
    private static int successProduct;
    private RequestQueue mRequestQueue;
    private static Snackbar snackbar = null;
    private static Toast toast = null;
    public static SharedPreferences pref, langPref;
    public static SharedPreferences.Editor editor;
    public static DecimalFormat decimal = new DecimalFormat(".##");

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        mInstance = this;
        pref = getApplicationContext().getSharedPreferences("FantacyPref", MODE_PRIVATE);
        langPref = getApplicationContext().getSharedPreferences("LangPref", MODE_PRIVATE);
        editor = pref.edit();
        if (pref.getBoolean(Constants.IS_LOGGED, false)) {
            GetSet.setLogged(true);
            GetSet.setUserId(pref.getString(Constants.TAG_USER_ID, null));
            GetSet.setUserName(pref.getString(Constants.TAG_USER_NAME, null));
            GetSet.setRating(pref.getString(Constants.TAG_RATING, null));
            GetSet.setFullName(pref.getString(Constants.TAG_FULL_NAME, null));
            GetSet.setImageUrl(pref.getString(Constants.TAG_USER_IMAGE, null));
            GetSet.setToken(pref.getString(Constants.TAG_TOKEN, null));
        }
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font_regular.ttf");

        // Monitor launch times and interval from installation
        RateThisApp.Config configure = new RateThisApp.Config(3, 10);
        configure.setUrl("https://play.google.com/store/apps/details?id=" + getPackageName());
        RateThisApp.init(configure);
        RateThisApp.onStart(this, "app");

        setLocale(this);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    public static synchronized FantacySellerApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(NetworkReceiver.ConnectivityReceiverListener listener) {
        NetworkReceiver.connectivityReceiverListener = listener;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addNewsRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * function for avoiding emoji typing in keyboard
     **/
    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };


    // Showing network status in Snackbar
    public static void showSnack(final Context context, View view, boolean isConnected) {
        if (snackbar == null) {
            snackbar = Snackbar
                    .make(view, context.getString(R.string.network_failure), Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
        }

        if (isConnected) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
                snackbar = null;
            }
        } else {
            if (!snackbar.isShown()) {
                snackbar.show();
            }
        }
    }

    // Showing network status in Snackbar
    public static void defaultSnack(View view, String message, String type) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            if (type.equals("alert"))
                textView.setTextColor(Color.WHITE);
            if (type.equals("error"))
                textView.setTextColor(Color.RED);
        }
        if (!snackbar.isShown()) {
            snackbar.show();
            snackbar = null;
        } else {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    public static void showToast(final Context context, String text, int duration) {

        if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
            toast = Toast.makeText(context, text, duration);
            toast.show();
        } else toast.cancel();
    }

    public static void defaultDialog(final Activity context, final String message, final String className) {
        Display display;
        display = context.getWindowManager().getDefaultDisplay();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.default_popup);
        dialog.getWindow().setLayout(display.getWidth() * 90 / 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView yes = (TextView) dialog.findViewById(R.id.yes);
        TextView no = (TextView) dialog.findViewById(R.id.no);
        if (message.equals(context.getString(R.string.product_success_post_msg))) {
            successProduct = 1;
        }
        title.setText(message);
        if (successProduct == 1) {
            yes.setText(context.getString(R.string.ok));
            no.setVisibility(View.GONE);
        } else {
            no.setVisibility(View.VISIBLE);
        }
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                context.finish();
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

    /**
     * function for change the selected language
     **/
    public static void setLocale(Context context) {
        String[] languages = context.getResources().getStringArray(R.array.languages);
        String[] langCode = context.getResources().getStringArray(R.array.languageCode);
        String selectedLang = langPref.getString("language", Constants.DEFAULT_LANGUAGE);
        int index = Arrays.asList(languages).indexOf(selectedLang);
        String languageCode = Arrays.asList(langCode).get(index);
        Log.v(TAG, "languageCode=" + languageCode);

        Locale myLocale = new Locale(languageCode);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static boolean isRTL(Context context) {
        if (context != null && context.getResources().getConfiguration().locale.toString().equals("ar")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * To convert the given dp value to pixel
     **/
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void hideSoftKeyboard(Activity context, View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException npe) {
        } catch (Exception e) {
        }
    }

    public static ArrayList<HashMap<String, String>> getSize(String json) {
        ArrayList<HashMap<String, String>> sizelist = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray size = new JSONArray(json);
            for (int x = 0; x < size.length(); x++) {
                JSONObject pobj = size.getJSONObject(x);
                HashMap<String, String> tmpMap = new HashMap<String, String>();

                String qty = DefensiveClass.optString(pobj, Constants.TAG_UNIT);
                if (!qty.equals("0")) {
                    String name = DefensiveClass.optString(pobj, Constants.TAG_SIZE);
                    String price = DefensiveClass.optString(pobj, Constants.TAG_PRICE);

                    tmpMap.put(Constants.TAG_SIZE, name);
                    tmpMap.put(Constants.TAG_UNIT, qty);
                    tmpMap.put(Constants.TAG_PRICE, price);
                    tmpMap.put(Constants.TAG_MAIN_PRICE, price);
                    sizelist.add(tmpMap);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sizelist;
    }

    public static boolean isDealEnabled(String validTill) {
        if (!validTill.equals("")) {
            long timeStamp = Long.parseLong(validTill);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeStamp * 1000);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);

            String calDate = android.text.format.DateFormat.format("dd-MM-yyyy", cal).toString();
            String currentDate = android.text.format.DateFormat.format("dd-MM-yyyy", (System.currentTimeMillis())).toString();
            if (currentDate.equals(calDate)) {
                return true;
            }
        }
        return false;
    }

    public static String extractYoutubeId(String url) {
        String id = "";
        try {
            String query = new URL(url).getQuery();
            String[] param = query.split("&");
            for (String row : param) {
                String[] param1 = row.split("=");
                if (param1.length >= 2 && param1[0].equals("v")) {
                    id = param1[1];
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void showStatusDialog(final Activity context, boolean status, String postedMessage, final Class destinationClass) {
        final Dialog dialog = new Dialog(context, R.style.AppTheme_NoActionBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.news_posted_dialog);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(width, height);
        //dialog.getWindow().addFlags(WindowManager.LayoutParams.);
        ImageView close = (ImageView) dialog.findViewById(R.id.close);
        TextView tick_posted = (TextView) dialog.findViewById(R.id.tick_posted);
        tick_posted.setText(postedMessage);
        if (status) {
            dialog.show();
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.finish();
                    dialog.dismiss();
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, destinationClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            }, Constants.DIALOG_DELAY);
        } else
            Toast.makeText(context, "Post failed!", Toast.LENGTH_SHORT).show();
    }

    /**
     * To convert timestamp to Date
     **/
    public static String getDate(String tstamp) {

        try {
            Long timeStamp = Long.parseLong(tstamp);
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date netDate = (new Date(timeStamp * 1000L));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    public static void logout(Context context) {
        SharedPreferences pref = context.getSharedPreferences("FantacyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        GetSet.reset();
        GetSet.setLogged(false);
        Intent i = new Intent(context, DisableActivity.class);
        i.putExtra("from", "block");
        context.startActivity(i);
    }

    public static class DecimalDigitsInputFilter implements InputFilter {

        private int mDigitsBeforeZero;
        private int mDigitsAfterZero;
        private Pattern mPattern;
        private static final int DIGITS_BEFORE_ZERO_DEFAULT = 100;
        private static final int DIGITS_AFTER_ZERO_DEFAULT = 100;

        public DecimalDigitsInputFilter(Integer digitsBeforeZero, Integer digitsAfterZero) {
            this.mDigitsBeforeZero = (digitsBeforeZero != null ? digitsBeforeZero : DIGITS_BEFORE_ZERO_DEFAULT);
            this.mDigitsAfterZero = (digitsAfterZero != null ? digitsAfterZero : DIGITS_AFTER_ZERO_DEFAULT);
            mPattern = Pattern.compile("-?[0-9]{0," + (mDigitsBeforeZero) + "}+((\\.[0-9]{0," + (mDigitsAfterZero)
                    + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String replacement = source.subSequence(start, end).toString();
            String newVal = dest.subSequence(0, dstart).toString() + replacement
                    + dest.subSequence(dend, dest.length()).toString();
            Matcher matcher = mPattern.matcher(newVal);
            if (matcher.matches())
                return null;
            if (TextUtils.isEmpty(source))
                return dest.subSequence(dstart, dend);
            else
                return "";
        }

    }

    public static String formatPrice(String price) {
        DecimalFormat precision = new DecimalFormat("#.##");
        return precision.format(Double.parseDouble(price));
    }
}
