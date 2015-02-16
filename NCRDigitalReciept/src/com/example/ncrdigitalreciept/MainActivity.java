package com.example.ncrdigitalreciept;

import java.util.HashMap;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.google.zxing.BarcodeFormat;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class MainActivity extends MyBaseActivity {

	public static final int LoginActivityId = 2;
	private static final String tag = "MainActivity";

	// PowerManager.WakeLock wl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		findViewById(R.id.main_view_qr_code)
				.setOnTouchListener(gestureListener);

		boolean fromPush = getIntent().getBooleanExtra(Receiver.FromPushFlag,
				false);
		getIntent().putExtra(Receiver.FromPushFlag, false);
		if (fromPush) {

			ShowRecieptsActivity(false);
		} else {

			Generate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Rect editTextRect = new Rect();
		findViewById(R.id.main_view_qr_code).getHitRect(editTextRect);

		if (!editTextRect.contains((int) event.getX(), (int) event.getY())) {
			return super.onTouchEvent(event);
		}

		return gestureDetector.onTouchEvent(event);
	}

	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Application.UpdateMainMenu_ACTION);
		registerReceiver(mMessageReceiver, filter);
		NotificationManager mNotificationManager = (NotificationManager) (MainActivity.this
				.getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE));
		mNotificationManager.cancel(Receiver.NotificationId);
	}

	protected void onStop() {
		super.onStop();
		unregisterReceiver(mMessageReceiver);
	}

	protected void onResume() {
		super.onResume();
		SetLoginState();
		/*
		 * if (Build.VERSION.SDK_INT < 17) { PowerManager pm = (PowerManager)
		 * getSystemService(Context.POWER_SERVICE); wl =
		 * pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag"); wl.acquire();
		 * } else {
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (Application.RegenerateOfBarcodeRequired) {
			Generate();
		}
		// }
	}

	protected void onPause() {
		super.onPause();
		try {
			// wl.release();
		} catch (Exception exception) {
		}
	}

	public void onListOfReciept(View view) {
		ShowRecieptsActivity();
	}

	private void ShowRecieptsActivity() {
		Intent intent = new Intent(MainActivity.this, RecieptsActivity.class);
		startActivity(intent);

	}

	private void ShowRecieptsActivity(boolean withStack) {
		if (withStack) {
			ShowRecieptsActivity();
		} else {
			Intent intent = new Intent(MainActivity.this,
					RecieptsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}

	}

	public void onReceive(Context context, Intent intent) {
		ShowRecieptsActivity();
	}

	public void onLeftSwipe() {
		Log.i(tag, "onLeftSwipe");
		
		if(findViewById(R.id.main_view_loading).getVisibility() == View.GONE)
		ShowRecieptsActivity();
	}

	public void onRightSwipe() {
		// Do something

	}

	public void onLogin(View view) {

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			ParseLoginBuilder builder = new ParseLoginBuilder(MainActivity.this);
			startActivityForResult(builder.build(), LoginActivityId);
		}

		else {
			ParseUser.logOut();
			currentUser = ParseUser.getCurrentUser(); // this will now be null

			ParseInstallation installation = ParseInstallation
					.getCurrentInstallation();
			installation.remove("user");
			installation.saveInBackground();

			Application.OnLogout();
			SetLoginState();
			Generate();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == LoginActivityId) {
			SetLoginState();
			Generate();
			ParseUser parseUser = ParseUser.getCurrentUser();
			ParseInstallation parseInstallation = ParseInstallation
					.getCurrentInstallation();
			Application.UpdateInstalation();

			AssociateUser(parseInstallation);

			// Make sure the request was successful
			if (resultCode == RESULT_OK) {

			}
		}
	}

	private void AssociateUser(ParseInstallation parseInstallation) {
		// TODO Auto-generated method stub

		findViewById(R.id.main_view_loading).setVisibility(View.VISIBLE);
		findViewById(R.id.main_view_qr_code).setVisibility(View.GONE);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("installationId", parseInstallation.getInstallationId());
		ParseCloud.callFunctionInBackground("associateInstallationWithUser",
				params, new FunctionCallback<Object>() {

					@Override
					public void done(Object arg0, ParseException arg1) {
						try {

							
							//Log.i("MainActivity", arg0.toString());
							if (arg1 != null) {
								arg1.printStackTrace();

							}

						} catch (Exception e) {

							e.printStackTrace();
						}

						findViewById(R.id.main_view_loading).setVisibility(
								View.GONE);
						findViewById(R.id.main_view_qr_code).setVisibility(
								View.VISIBLE);

					}

				});
	}
	

	@SuppressLint("NewApi")
	private int GetBitmapSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		int width;
		int height;
		if (Build.VERSION.SDK_INT >= 13) {
			display.getSize(size);
			width = size.x;
			height = size.y;

		} else {
			width = display.getWidth(); // deprecated
			height = display.getHeight();
		}

		return (int) (Math.min(width, height) * 0.8);

	}

	public void onRefresh(View view) {
		Generate();
	}

	public void Generate() {
		Application.RegenerateOfBarcodeRequired = false;

		findViewById(R.id.main_view_loading).setVisibility(View.VISIBLE);
		findViewById(R.id.main_view_qr_code).setVisibility(View.GONE);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				RegisterToPush();

			}
		}, 50);

	}

	private void RegisterToPush() {

		try {
			/*
			 * GCMRegistrar.checkDevice(MainActivity.this);
			 * GCMRegistrar.checkManifest(MainActivity.this); String regId =
			 * GCMRegistrar .getRegistrationId(MainActivity.this); if
			 * (regId.equals("")) {
			 * 
			 * regId = Application.GetPushToken(); if (!regId.equals("")) {
			 * pushToken = regId; return; }
			 * GCMRegistrar.register(MainActivity.this,
			 * GCMIntentService.SENDER_ID); for (int i = 0; i < 200 &&
			 * regId.equals(""); i++) { regId = GCMRegistrar
			 * .getRegistrationId(MainActivity.this); if (regId.equals("")) {
			 * try { // Do some work here Thread.sleep(100); continue; } catch
			 * (Exception e) { } } pushToken = regId; } } else { pushToken =
			 * regId; Log.v("GCM", "Already registered"); }
			 */

			GetQRCode();

		} catch (Exception exception) {
			exception.printStackTrace();
			findViewById(R.id.main_view_loading).setVisibility(View.GONE);
			findViewById(R.id.main_view_qr_code).setVisibility(View.VISIBLE);
		}

	}

	private void GetQRCode() {
		runOnUiThread(new Runnable() {
			public void run() {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("installationId", ParseInstallation
						.getCurrentInstallation().getInstallationId());

				ParseCloud.callFunctionInBackground("registerReceipt", params,
						new FunctionCallback<ParseObject>() {

							@Override
							public void done(ParseObject arg0,
									ParseException arg1) {
								try {

									if (arg0 == null) {
										arg0 = new ParseObject("aaaaaaaaa");
									}
									Log.i("MainActivity", arg0.toString());
									if (arg1 != null) {
										arg1.printStackTrace();
										ShowAlertBadInternet();
									}

									int pincode = arg0.getInt("pinCode");
									int size = GetBitmapSize();
									QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(
											pincode + "", null,
											Contents.Type.TEXT,
											BarcodeFormat.QR_CODE.toString(),
											size);
									Bitmap bitmap;

									bitmap = qrCodeEncoder.encodeAsBitmap();

									ImageView myImage = (ImageView) findViewById(R.id.main_view_qr_code_image);
									android.view.ViewGroup.LayoutParams layoutParams = myImage
											.getLayoutParams();
									layoutParams.height = size;
									layoutParams.width = size;
									myImage.setLayoutParams(layoutParams);
									myImage.setImageBitmap(bitmap);
									TextView textView = (TextView) findViewById(R.id.main_view_qr_code_managment_code_value);

									textView.setText(String.format("%05d",
											pincode));

								} catch (Exception e) {

									e.printStackTrace();
								}

								findViewById(R.id.main_view_loading)
										.setVisibility(View.GONE);
								findViewById(R.id.main_view_qr_code)
										.setVisibility(View.VISIBLE);

							}

						});
			}
		});
	}

	private void ShowAlertBadInternet() {
		// TODO Auto-generated method stub
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.Error)
				.setMessage(R.string.No_internet)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setNegativeButton(R.string.Retry,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Generate();
							}
						})

				.create();
		alertDialog.show();
	}

	@Override
	public void OnRequestExecuted(String res, int requestTypeId,
			Object customObjectForResponse, int httpErrorCode) {
		// TODO Auto-generated method stub
		super.OnRequestExecuted(res, requestTypeId, customObjectForResponse,
				httpErrorCode);

	}

}
