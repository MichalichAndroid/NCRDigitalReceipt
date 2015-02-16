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
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

public class Receiver extends ParsePushBroadcastReceiver {

	public static final String FromPushFlag = "FromPushFlag";
	public static final int NotificationId = 325436546;

	@Override
	public void onPushOpen(Context context, Intent intent) {
		Log.e("Push", "Clicked");

		/*
		 * Intent i = new Intent(context, HomeActivity.class);
		 * i.putExtras(intent.getExtras());
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); context.startActivity(i);
		 */
		// super.onPushReceive(arg0, arg1);
	}

	@Override
	public void onPushReceive(Context context, Intent intent) {
		Log.e("Push", "onPushReceive");
		Bundle extras = intent.getExtras();

		String jsonData = extras.getString("com.parse.Data");
		String heading = "";
		String dataString = "";
		String pincode = "";
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonData);
			heading = jsonObject.getString("title");
			dataString = jsonObject.getString("alert");
			pincode = jsonObject.getInt("pinCode") + "";
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Boolean isAppRunning = false;
		Application.RegenerateOfBarcodeRequired = true;
		isAppRunning = FindIsAppRunning(context.getApplicationContext());
		if (!isAppRunning) {
			OpenFromBackground(context, heading, dataString);

		} else {
			if (FindActivity(".RecieptsActivity", context)) {
				updateMyActivity(context, "df", Application.UpdateList_ACTION);
			}

			if (FindActivity(".MainActivity", context)) {
				updateMyActivity(context, "df",
						Application.UpdateMainMenu_ACTION);
			}
		}

		// boolean isActivityFound = FindActivity(messagetype, context);

	}

	@SuppressLint("NewApi")
	private void OpenFromBackground(Context context, String heading,
			String dataString) {
		Intent notIntent = new Intent(context.getApplicationContext(),
				MainActivity.class);
		notIntent.putExtra(FromPushFlag, true);

		PendingIntent pIntent = PendingIntent.getActivity(
				context.getApplicationContext(), 0, notIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification notif = null;
		Uri soundUri = null;

		soundUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			notif = new Notification.Builder(Application.GetContext())
					.setContentTitle(heading)
					.setSmallIcon(R.drawable.ic_launcher).setTicker(dataString)
					.setSound(soundUri).setContentText(dataString)
					.setContentIntent(pIntent).getNotification();
		} else {
			notif = new Notification(R.drawable.ic_launcher, dataString,
					System.currentTimeMillis());
			notif.sound = soundUri;
			notif.tickerText = dataString;

		}
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.flags |= Notification.FLAG_SHOW_LIGHTS;
		notif.ledARGB = 0xffbe621c;
		notif.ledOnMS = 300;
		notif.ledOffMS = 1000;

		NotificationManager mNotificationManager = (NotificationManager) (context
				.getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE));
		notif.setLatestEventInfo(Application.GetContext(), heading, "", pIntent);
		mNotificationManager.notify(NotificationId, notif);
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

	private Boolean FindActivity(String activityName, Context context) {
		Boolean isactivityFound = false;
		Boolean isAppRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		if (services.get(0).topActivity.getPackageName().toString()
				.equalsIgnoreCase(context.getPackageName().toString())) {
			isAppRunning = true;
			if (services.get(0).topActivity.getShortClassName().equals(
					activityName))
				isactivityFound = true;
		}
		return isactivityFound;
	}

	static void updateMyActivity(Context context, String message, String action) {

		Intent broadcast = new Intent();
		broadcast.setAction(action);
		context.sendBroadcast(broadcast);
	}
}