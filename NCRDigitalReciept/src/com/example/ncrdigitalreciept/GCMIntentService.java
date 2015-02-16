package com.example.ncrdigitalreciept;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;



import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMConstants;



public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "Push Notification Demo GCMIntentService";
    public static final String SENDER_ID = "55143498175";

    @Override
    protected void onError(Context context, String errorId) {

        if (GCMConstants.ERROR_ACCOUNT_MISSING.equalsIgnoreCase(errorId)) {
            Log.v(TAG, "Error Account Missing");
        } else if (GCMConstants.ERROR_AUTHENTICATION_FAILED
                .equalsIgnoreCase(errorId)) {
            Log.v(TAG, "Error Authentication Failed");
        } else if (GCMConstants.ERROR_INVALID_PARAMETERS
                .equalsIgnoreCase(errorId)) {
            Log.v(TAG, "Error Invalid Parameters");
        } else if (GCMConstants.ERROR_INVALID_SENDER.equalsIgnoreCase(errorId)) {
            Log.v(TAG, "Error Invalid Sender");
        } else if (GCMConstants.ERROR_PHONE_REGISTRATION_ERROR
                .equalsIgnoreCase(errorId)) {
            Log.v(TAG, "Error Phone Registration Error");
        } else if (GCMConstants.ERROR_SERVICE_NOT_AVAILABLE
                .equalsIgnoreCase(errorId)) {
            Log.v(TAG, "Error Service Not Available");
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onMessage(Context context, Intent intent) {

        try {
            String notifinMessage = intent.getStringExtra("payload");
            // {"messageType":"307","aps":{"alert":"dfdf","badge":38,"sound":"normal"},"messageBody":{"dateTime":"20-05-2013 19:18:47+0300","messageId":235}}
            String valAlert = "";
            String valSound = "";
            String valBadge = "";
            String messagetype = "";
            JSONObject json = null;
            Boolean isAppRunning = false;
            try {
                json = new JSONObject(notifinMessage);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONObject jsonApps = null;
            try {
                jsonApps = json.getJSONObject("aps");
                messagetype = json.getString("messageType");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (jsonApps != null) {
                try {
                    valAlert = jsonApps.getString("alert");
                    valSound = jsonApps.getString("sound");
                    valBadge = jsonApps.getString("badge");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

         //   boolean isActivityFound = FindActivity(messagetype, context);

            isAppRunning = FindIsAppRunning(context);

           

            try {
                Object[] keys = intent.getExtras().keySet().toArray();
                for (int i = 0; i < keys.length; i++) {
                    Log.v(TAG, "key: " + keys[i].toString() + "  value="
                            + intent.getStringExtra(keys[i].toString()));
                }
                String badge = "0";
                int savedBudge = 0;
                try {
                    savedBudge = Integer.parseInt(badge);
                } catch (Exception exception) {
                }
                int recievedBadge = 0;
                try {
                    recievedBadge = Integer.parseInt(valBadge);
                } catch (Exception exception) {
                }

                Intent notIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
               
               
                PendingIntent pIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0, notIntent,
                        Intent.FLAG_ACTIVITY_NEW_TASK);

                Notification notif = null;
                Uri soundUri = null;
                if (valSound.equals("silent"))
                    soundUri = null;
                else if (valSound.toLowerCase().contains("alert")) {
                    soundUri = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_ALARM);

                } else
                    soundUri = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    notif = new Notification.Builder(getApplicationContext())
                            .setContentTitle(valAlert)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setTicker(valAlert).setSound(soundUri)
                            .setContentIntent(pIntent).getNotification();
                } else {
                    notif = new Notification(R.drawable.ic_launcher, valAlert,
                            System.currentTimeMillis());
                    notif.sound = soundUri;
                    notif.tickerText = valAlert;

                }
                notif.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.flags |= Notification.FLAG_SHOW_LIGHTS;
                notif.ledARGB = 0xffbe621c;
                notif.ledOnMS = 300;
                notif.ledOffMS = 1000;

                if (valSound.equals("alert"))
                    notif.defaults |= Notification.DEFAULT_VIBRATE;
            
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notif.setLatestEventInfo(getApplicationContext(), valAlert, "",
                        pIntent);
                mNotificationManager.notify(10, notif);
             

            } catch (Exception exception) {
                Log.e(TAG, "onMessage", exception);
            }

        } catch (Exception exception) {
            Log.e(TAG, "onMessage", exception);
        }
    }

    private Boolean FindIsAppRunning(Context context) {
        Boolean isAppRunning = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString())) {
            isAppRunning = true;
        }
        return isAppRunning;
    }

  

    @Override
    protected void onRegistered(Context context, String regId) {

        Log.v(TAG, "Successfull Registration : " + regId);

        try {
          Application.SavePushToken(regId);
        } catch (Exception exception) {

        }

    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        try {
            Log.v(TAG, "Successfully Unregistred : " + regId);

            // CommunicationManager.GetInstance().UnRegisterFromPudhService();
        } catch (Exception exception) {
        }
    }

    @Override
    protected String[] getSenderIds(Context context) {
        return new String[]{SENDER_ID};
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        super.onDeletedMessages(context, total);
        Log.i(TAG, "onDeletedMessages: " + total);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }
}