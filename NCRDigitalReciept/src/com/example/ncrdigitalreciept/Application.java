package com.example.ncrdigitalreciept;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;

public class Application extends android.app.Application {
	// Debugging switch

	// Debugging tag for the application
	public static final String APPTAG = "DigitalReciept";

	public static final String PushTokenId = "PushTokenId";
	public static final String SharedPreferencesName = "com.example.ncrdigitalreciept";
	public static String UpdateList_ACTION = "com.example.ncrdigitalreciept.UpdateList";
	public static String UpdateMainMenu_ACTION = "com.example.ncrdigitalreciept.UpdateMainMenu";
	public static final String UserNameKey = "UserNameKey";
	public static final String LastFetchDateKey = "LastFetchDateKey";

	private static Context context;

	public Application() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		Parse.enableLocalDatastore(this);

		
		//Production
		 Parse.initialize(this, getString(R.string.application_id),
				getString(R.string.client_id));
				
				
		//Test
	/*
		Parse.initialize(this, "SNGu8bRK5nxTwGTh3qrvDzFE8tRbIarDGaRsKxwr",
				"W3CAAQRO4Xq2TqtDtvRgZKtYaQLrGIZsPnbOKCwp");
		UpdateInstalation();
*/
		/*
		 * <key>FacebookAppID</key> <string></string>
		 * <key>FacebookDisplayName</key> <string>NCR Receipts</string>
		 */
		ParseFacebookUtils.initialize("1591217224448027");
		ParseTwitterUtils.initialize("mtRTM5d2lVViR2JJ9ZpC75hXg",
				"6HnMg8YC0b8IZbJdl7VvBDDclw86XbP1EZlPIQfDyovvk0tTmn");

		// ParseUser.enableAutomaticUser();

		// "YOUR_PARSE_APPLICATION_ID",
		// "YOUR_PARSE_CLIENT_KEY");

	}

	public static boolean RegenerateOfBarcodeRequired = false;

	public static void SavePushToken(String token) {
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		prefs.edit().putString(PushTokenId, token).commit();
	}

	public static String GetPushToken() {
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		return prefs.getString(PushTokenId, "");
	}

	public static Context GetContext() {

		return context;
	}

	public static void UpdateInstalation() {
		ParseUser parseUser = ParseUser.getCurrentUser();
		ParseInstallation parseInstallation = ParseInstallation
				.getCurrentInstallation();
		if (parseUser != null) {
			ParseACL postACL = new ParseACL(parseUser);
			postACL.setPublicReadAccess(true);
			postACL.setPublicWriteAccess(true);
			parseInstallation.put("user", parseUser);
		} else {
			parseInstallation.remove("user");
		}

		parseInstallation.saveInBackground();
	}

	public static void SetUserName(String string) {
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		prefs.edit().putString(UserNameKey, string).commit();

	}

	public static String GetUserName() {

		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		return prefs.getString(UserNameKey, "");
	}

	/*public static void SaveLastFetchDate() {
		// TODO Auto-generated method stub
		SaveLastFetchDate(System.currentTimeMillis());
	}*/

	public static void SaveLastFetchDate(long millis) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		prefs.edit().putLong(LastFetchDateKey, millis).commit();
	}

	public static Date GetLastFetchDate() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		Date date = new Date(prefs.getLong(LastFetchDateKey, 0));
		return date;
	}

	public static void OnLogout() {
		ParseObject.unpinAllInBackground();
		SetUserName("");
		SaveLastFetchDate(0);
	}

}
