package com.futureskyltd.app.fantacyseller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.futureskyltd.app.external.CirclePageIndicator;
import com.futureskyltd.app.helper.NetworkReceiver;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hitasoft on 12/6/17.
 */

public class LoginActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener, View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();
    ViewPager desPager;
    SharedPreferences.Editor editor;
    CirclePageIndicator pagerIndicator;
    SharedPreferences pref;
    EditText email, password;
    TextView signin, forgetpassword;
    private ImageView image1, image2, image3, image5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        desPager = (ViewPager) findViewById(R.id.desPager);
        pagerIndicator = (CirclePageIndicator) findViewById(R.id.pagerIndicator);
        email = (EditText) findViewById((R.id.email));
        password = (EditText) findViewById((R.id.password));
        signin = (TextView) findViewById((R.id.signin));
        forgetpassword = (TextView) findViewById((R.id.forgetpassword));
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image5 = findViewById(R.id.image5);

        pref = getApplicationContext().getSharedPreferences("FantacyPref", MODE_PRIVATE);
        editor = pref.edit();

        String[] names = {getString(R.string.signin_des1), getString(R.string.signin_des2), getString(R.string.signin_des3)};
        DesPagerAdapter desPagerAdapter = new DesPagerAdapter(LoginActivity.this, names);
        desPager.setAdapter(desPagerAdapter);
        pagerIndicator.setViewPager(desPager);

        signin.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);

        // register connection status listener
        FantacySellerApplication.getInstance().setConnectivityListener(this);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        openUrl();
    }

    private void openUrl() {
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent url1 = new Intent(Intent.ACTION_VIEW);
                url1.setData(Uri.parse("https://totthoapa.gov.bd/"));
                startActivity(url1);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent url2 = new Intent(Intent.ACTION_VIEW);
                url2.setData(Uri.parse("http://www.jms.gov.bd/"));
                startActivity(url2);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent url3 = new Intent(Intent.ACTION_VIEW);
                url3.setData(Uri.parse("https://bfti.org.bd/"));
                startActivity(url3);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent url5 = new Intent(Intent.ACTION_VIEW);
                url5.setData(Uri.parse("https://www.futureskyltd.com/"));
                startActivity(url5);
            }
        });
    }

    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(desPager.getCurrentItem() == 0){
                        desPager.setCurrentItem(1);
                    } else if(desPager.getCurrentItem() == 1){
                        desPager.setCurrentItem(2);
                    }else{
                        desPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void loginUser(final String usremail, final String usrpassword) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.d(TAG, "loginUserRes=" + res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        GetSet.setLogged(true);
                        GetSet.setUserId(DefensiveClass.optString(json, Constants.TAG_USER_ID));
                        GetSet.setImageUrl(DefensiveClass.optString(json, Constants.TAG_USER_IMAGE));
                        GetSet.setUserName(DefensiveClass.optString(json, Constants.TAG_USER_NAME));
                        GetSet.setFullName(DefensiveClass.optString(json, Constants.TAG_FULL_NAME));
                        GetSet.setRating(DefensiveClass.optString(json, Constants.TAG_RATING));
                        GetSet.setToken(DefensiveClass.optString(json, Constants.TAG_TOKEN));

                        editor.putBoolean(Constants.IS_LOGGED, true);
                        editor.putString(Constants.TAG_USER_ID, DefensiveClass.optString(json, Constants.TAG_USER_ID));
                        editor.putString(Constants.TAG_TOKEN, DefensiveClass.optString(json, Constants.TAG_TOKEN));
                        editor.putString(Constants.TAG_USER_NAME, DefensiveClass.optString(json, Constants.TAG_USER_NAME));
                        editor.putString(Constants.TAG_USER_IMAGE, DefensiveClass.optString(json, Constants.TAG_USER_IMAGE));
                        editor.putString(Constants.TAG_FULL_NAME, DefensiveClass.optString(json, Constants.TAG_FULL_NAME));
                        editor.putString(Constants.TAG_LANGUAGE, Constants.DEFAULT_LANGUAGE);
                        editor.putString(Constants.TAG_RATING, DefensiveClass.optString(json, Constants.TAG_RATING));
                        editor.commit();
                        /*Open Home Page*/
                        Intent success = new Intent(LoginActivity.this, FragmentMainActivity.class);
                        startActivity(success);
                        finish();

                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("false")) {
                        if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("Please enter correct email and password"))
                            FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.enter_correct_username_password), Toast.LENGTH_SHORT);
                        else if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("Your account is disabled by Admin"))
                            FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.account_disabled), Toast.LENGTH_SHORT);
                        else if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("Please activate your account by the email sent to you"))
                            FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.activate_email), Toast.LENGTH_SHORT);
                        else if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("Please wait for admin approval"))
                            FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.admin_approval_wait), Toast.LENGTH_LONG);
                        password.setText("");
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
                Log.e(TAG, "loginUserError: " + error.getMessage());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        })/////////finish request queue//////////////
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_EMAIL, usremail);
                map.put(Constants.TAG_PASSWORD, usrpassword);
                Log.i(TAG, "loginUserParams: " + map);
                return map;
            }
        };
        dialog.show();
        FantacySellerApplication.getInstance().addToRequestQueue(req);
    }

    private void forgotDialog() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_NoActionBar);
        Display display;
        display = getWindowManager().getDefaultDisplay();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setContentView(R.layout.forgot_pass_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);

        final EditText email = dialog.findViewById(R.id.editText);
        final TextView reset = dialog.findViewById(R.id.reset);
        final ImageView cancel = dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FantacySellerApplication.hideSoftKeyboard(LoginActivity.this, cancel);
                dialog.dismiss();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FantacySellerApplication.hideSoftKeyboard(LoginActivity.this, reset);
                if (email.getText().toString().trim().length() == 0) {
//                    FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.please_type_mail), Toast.LENGTH_SHORT);
                    email.setError(getString(R.string.please_type_mail));
                    email.requestFocus();
                } /*else if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
//                    FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.email_error), Toast.LENGTH_SHORT);
                    email.setError(getString(R.string.email_error));
                    email.requestFocus();
                }*/ else {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    dialog.dismiss();
                    userForgetPassword(email.getText().toString());
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void userForgetPassword(final String usremail) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.pleasewait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_FORGET_PSWD, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.v(TAG, "userForgetPasswordRes=" + res);
                    JSONObject json = new JSONObject(res);
                    if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("true")) {
                        FantacySellerApplication.showStatusDialog(LoginActivity.this, true, getString(R.string.reset_success_message), LoginActivity.class);
                    } else if (DefensiveClass.optString(json, Constants.TAG_STATUS).equalsIgnoreCase("false")) {
                        if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("User not found")) {
                            FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.user_not_found), Toast.LENGTH_LONG);
                        } else if (DefensiveClass.optString(json, Constants.TAG_MESSAGE).equals("Enter the correct user Email id"))
                            FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.merchant_not_valid), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                    FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "userForgetPasswordError: " + error.getMessage());
                FantacySellerApplication.showToast(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constants.TAG_FORGET_EMAIL, usremail);
                Log.i(TAG, "userForgetPasswordParams: " + map);
                return map;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + GetSet.getToken());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };
        dialog.show();
        FantacySellerApplication.getInstance().addToRequestQueue(req);
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
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.v("isConnected", "isConnected=" + isConnected);
        FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), isConnected);
    }

    class DesPagerAdapter extends PagerAdapter {
        Context context;
        LayoutInflater inflater;
        String[] names;

        public DesPagerAdapter(Context act, String[] names) {
            this.names = names;
            this.context = act;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.signin_des_text,
                    collection, false);

            TextView name = itemView.findViewById(R.id.name);
            name.setText(names[position]);

            collection.addView(itemView, 0);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((ViewGroup) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                if (NetworkReceiver.isConnected()) {
                    if (email.getText().toString().trim().length() == 0) {
                        email.setError(getString(R.string.please_type_mail));
                        email.requestFocus();
                    } /*else if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                        email.setError(getString(R.string.email_error));
                        email.requestFocus();
                    }*/ else if (password.getText().toString().length() == 0) {
                        password.setError(getString(R.string.please_type_password));
                        password.requestFocus();
                    } else {
                        loginUser(email.getText().toString(), password.getText().toString());
                    }
                } else {
                    FantacySellerApplication.showSnack(this, findViewById(R.id.parentLay), false);
                }

                break;
            case R.id.forgetpassword:
                //forgotDialog();
                popUpDialog();
                break;
        }
    }

    private void popUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("পাসওয়ার্ড ভুলে গেছেন?");
        alertDialog.setMessage("পাসওয়ার্ড ভুলে গিয়ে থাকলে আপনার তথ্য কেন্দ্রে যোগাযোগ করুন অথবা কল করুন   ০৯৬৭৮৮৪৪৪৮৫");
        alertDialog.setIcon(R.drawable.ic_lock);
        alertDialog.setPositiveButton("ওকে", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /*alertDialog.setNegativeButton("আরও যোগ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { {
                dialog.dismiss();

            }
            }
        });*/

        alertDialog.create();
        alertDialog.show();
    }
}