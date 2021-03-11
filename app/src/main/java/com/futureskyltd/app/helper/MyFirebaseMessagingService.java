package com.futureskyltd.app.helper;


import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.futureskyltd.app.fantacyseller.FantacySellerApplication;
import com.futureskyltd.app.utils.Constants;
import com.futureskyltd.app.utils.DefensiveClass;
import com.futureskyltd.app.utils.GetSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hitasoft on 03/11/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static String chatUserID = "";

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        storeToken(newToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //  sendPushNotification(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //this method will display the notification.We are passing the JSONObject that is received from
    //firebase cloud messaging
  /*  private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = DefensiveClass.optString(data, "title");
            JSONObject msg = data.optJSONObject("message");


            //get the logined user details from preference
            FantacyApplication.pref = getApplicationContext().getSharedPreferences("FantacyPref",
                    MODE_PRIVATE);
            FantacyApplication.editor = FantacyApplication.pref.edit();

            if (FantacyApplication.pref.getBoolean("isLogged", false)) {
                GetSet.setLogged(true);
                GetSet.setUserId(FantacyApplication.pref.getString(Constants.TAG_USER_ID, null));
                HashMap<String, String> tempMap = parseNotification(msg);

                switch (tempMap.get(Constants.TAG_TYPE)) {
                    case "follow":
                        showSmallNotification(tempMap, tempMap.get(Constants.TAG_USER_NAME) + " " + tempMap.get(Constants.TAG_MESSAGE));
                        break;

                    case "admin_message":
                        showSmallNotification(tempMap, "Admin " + tempMap.get(Constants.TAG_MESSAGE));
                        break;

                    case "dispute_accept":
                    case "dispute_admin":
                    case "orderstatus":
                    case "admin_refund":
                    case "mentioned":
                    case "cartnotification":
                    case "credit":
                        showSmallNotification(tempMap, tempMap.get(Constants.TAG_MESSAGE));
                        break;

                    case "post_status":
                    case "like_status":
                    case "comment_status":
                    case "share_status":
                    case "mention_item_comment":
                    case "mention_status":
                    case "mention_status_comment":
                        showSmallNotification(tempMap, tempMap.get(Constants.TAG_USER_NAME) + " " + Html.fromHtml(tempMap.get(Constants.TAG_MESSAGE)).toString());
                        break;

                    case "gift_card":
                        showSmallNotification(tempMap, Html.fromHtml(tempMap.get(Constants.TAG_MESSAGE)).toString());
                        break;
                }
                //  showSmallNotification(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* public void showSmallNotification(HashMap<String, String> tempMap, String message) {
        Intent intent = null;
        String appName = getString(R.string.app_name);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        long when = System.currentTimeMillis();
        switch (tempMap.get(Constants.TAG_TYPE)) {
            case "follow":
                intent = new Intent(getApplicationContext(), com.hitasoft.app.fantacy.Profile.class);
                intent.putExtra("userId", tempMap.get(Constants.TAG_USER_ID));
                break;

            case "admin_message":
            case "admin_refund":
            case "dispute_accept":
            case "dispute_admin":
            case "credit":
                intent = new Intent(getApplicationContext(), com.hitasoft.app.fantacy.FragmentMainActivity.class);
                intent.putExtra("fromNotification","admin");
                break;

            case "orderstatus":
                intent = new Intent(getApplicationContext(), com.hitasoft.app.fantacy.OrderDetail.class);
                intent.putExtra("orderId", tempMap.get(Constants.TAG_ORDER_ID));
                break;

            case "cartnotification":
                intent = new Intent(getApplicationContext(), com.hitasoft.app.fantacy.Profile.class);
                break;

            case "post_status":
            case "like_status":
            case "comment_status":
            case "share_status":
            case "mention_item_comment":
            case "mention_status":
            case "mention_status_comment":
                intent = new Intent(getApplicationContext(), com.hitasoft.app.fantacy.FragmentMainActivity.class);
                intent.putExtra("fromNotification","admin");
                break;

            case "gift_card":
                intent = new Intent(getApplicationContext(), com.hitasoft.app.fantacy.GiftHistory.class);
                break;
        }
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_ONE_SHOT);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.appicon);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.notification_small).setTicker(appName).setWhen(when)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(appName)
                .setSmallIcon(R.drawable.notification_small)
                .setLargeIcon(bitmap)
                .setContentText(message)
                .build();


        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(getResources(), R.mipmap.appicon);
        }
    }

    private HashMap<String, String> parseNotification(JSONObject temp) {
        HashMap<String, String> map = new HashMap<>();
        try {
            // JSONObject temp = new JSONObject(jsonString);

            String type = DefensiveClass.optString(temp, Constants.TAG_TYPE);
            map.put(Constants.TAG_TYPE, type);

            if (type.equalsIgnoreCase("follow")) {
                String user_id = DefensiveClass.optString(temp, Constants.TAG_USER_ID);
                String user_name = DefensiveClass.optString(temp, Constants.TAG_USER_NAME);
                String user_image = DefensiveClass.optString(temp, Constants.TAG_USER_IMAGE);
                String message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);
                map.put(Constants.TAG_USER_ID, user_id);
                map.put(Constants.TAG_USER_NAME, user_name);
                map.put(Constants.TAG_USER_IMAGE, user_image);
                map.put(Constants.TAG_MESSAGE, message);
            } else if (type.equalsIgnoreCase("admin_message") ||type.equalsIgnoreCase("admin_refund")) {
                String order_message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);
                map.put(Constants.TAG_MESSAGE, order_message);
            } else if (type.equalsIgnoreCase("orderstatus")) {
                String order_id = DefensiveClass.optString(temp, Constants.TAG_ORDER_ID);
                String order_message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);
                map.put(Constants.TAG_ORDER_ID, order_id);
                map.put(Constants.TAG_MESSAGE, order_message);
            } else if (type.equalsIgnoreCase("dispute_accept") || type.equalsIgnoreCase("dispute_accept")) {
                String dispute_id = DefensiveClass.optString(temp, Constants.TAG_DISPUTE_ID);
                String dispute_message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);
                map.put(Constants.TAG_DISPUTE_ID, dispute_id);
                map.put(Constants.TAG_MESSAGE, dispute_message);
            } else if (type.equalsIgnoreCase("cart_notification")) {
                String admin_image = DefensiveClass.optString(temp, Constants.TAG_ADMIN_IMAGE);
                String date = DefensiveClass.optString(temp, Constants.TAG_DATE);

                map.put(Constants.TAG_ADMIN_IMAGE, admin_image);
                map.put(Constants.TAG_DATE, date);
            } else if (type.equalsIgnoreCase("group_gift")) {
                String user_id = DefensiveClass.optString(temp, Constants.TAG_USER_ID);
                String user_name = DefensiveClass.optString(temp, Constants.TAG_USER_NAME);
                String user_image = DefensiveClass.optString(temp, Constants.TAG_USER_IMAGE);
                String gift_id = DefensiveClass.optString(temp, Constants.TAG_GIFT_ID);
                String message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);

                map.put(Constants.TAG_USER_ID, user_id);
                map.put(Constants.TAG_USER_NAME, user_name);
                map.put(Constants.TAG_USER_IMAGE, user_image);
                map.put(Constants.TAG_GIFT_ID, gift_id);
                map.put(Constants.TAG_MESSAGE, message);
            } else if (type.equalsIgnoreCase("credit") || type.equalsIgnoreCase("gift_card")) {
                String user_image = DefensiveClass.optString(temp, Constants.TAG_USER_IMAGE);
                String message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);
                String user_name = DefensiveClass.optString(temp, Constants.TAG_USER_NAME);

                map.put(Constants.TAG_USER_NAME, user_name);
                map.put(Constants.TAG_USER_IMAGE, user_image);
                map.put(Constants.TAG_MESSAGE, message);
            } else if (type.equalsIgnoreCase("post_status") || type.equalsIgnoreCase("like_status") || type.equalsIgnoreCase("comment_status")
                    || type.equalsIgnoreCase("share_status") || type.equalsIgnoreCase("mention_status")
                    || type.equalsIgnoreCase("mention_status_comment")) {
                String user_id = DefensiveClass.optString(temp, Constants.TAG_USER_ID);
                String user_name = DefensiveClass.optString(temp, Constants.TAG_USER_NAME);
                String user_image = DefensiveClass.optString(temp, Constants.TAG_USER_IMAGE);
                String feed_id = DefensiveClass.optString(temp, Constants.TAG_FEED_ID);
                String message = DefensiveClass.optString(temp, Constants.TAG_MESSAGE);
                map.put(Constants.TAG_USER_ID, user_id);
                map.put(Constants.TAG_USER_NAME, user_name);
                map.put(Constants.TAG_USER_IMAGE, user_image);
                map.put(Constants.TAG_FEED_ID, feed_id);
                map.put(Constants.TAG_MESSAGE, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }*/

    private void storeToken(String token) {
        //saving the token on shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);

        //get the logined user details from preference
        FantacySellerApplication.pref = getApplicationContext().getSharedPreferences("FantacyPref",
                MODE_PRIVATE);
        FantacySellerApplication.editor = FantacySellerApplication.pref.edit();

        if (FantacySellerApplication.pref.getBoolean(Constants.IS_LOGGED, false)) {
            GetSet.setLogged(true);
            GetSet.setUserId(FantacySellerApplication.pref.getString(Constants.TAG_USER_ID, null));

            addDeviceId();
        }
    }

    private void addDeviceId() {

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String deviceId = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        StringRequest req = new StringRequest(Request.Method.POST, Constants.API_PUSH_NOTIFICATION_REGISTER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String res) {
                        Log.v(TAG, "Res=" + res);
                        try {
                            JSONObject json = new JSONObject(res);
                            String response = DefensiveClass.optString(json, Constants.TAG_STATUS);
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
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", GetSet.getUserId());
                map.put("device_token", token);
                map.put("device_type", "1");
                map.put("device_id", deviceId);
                // map.put("device_mode", "1");
                Log.v("params", "params=" + map);
                return map;
            }
        };

        FantacySellerApplication.getInstance().addToRequestQueue(req, TAG);
    }
}