package com.example.ncrdigitalreciept;

import org.apache.http.message.BasicNameValuePair;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.twitter.Twitter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public abstract class MyBaseActivity extends RecieverActivity implements
		OnRequestExecuted {

	public final int GetTwitterName = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gestureDetector = new GestureDetector(new SwipeGestureDetector());

		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	}

	protected void OpenMainMenu() {
		Intent intent = new Intent(MyBaseActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	protected void SetLoginState() {
		final TextView textView = (TextView) findViewById(R.id.headerLoginText);
		Button button = (Button) findViewById(R.id.header_left);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			textView.setText(R.string.anonymus);
			button.setBackgroundResource(R.drawable.enter);
		} else {

			if (ParseFacebookUtils.isLinked(currentUser)) {
				String username = Application.GetUserName();
				if (username == null || username.equals("")) {
					textView.setText(R.string.default_facebook_name);
				} else {
					textView.setText(username);
				}
				Request.newMeRequest(ParseFacebookUtils.getSession(),
						new GraphUserCallback() {

							@Override
							public void onCompleted(
									com.facebook.model.GraphUser user,
									com.facebook.Response response) {
								// TODO Auto-generated method stub

								if (user != null) {
									String userName = user.getName();
									textView.setText(userName);
									Application.SetUserName(userName);
								}
							}
						}).executeAsync();
			} else if (ParseTwitterUtils.isLinked(currentUser)) {

				textView.setText(ParseTwitterUtils.getTwitter().getScreenName());

			} else {
				textView.setText(currentUser.getUsername());

			}
			button.setBackgroundResource(R.drawable.exit);
		}
	}

	// This is the handler that will manager to process the broadcast intent
	protected GestureDetector gestureDetector;
	protected View.OnTouchListener gestureListener;

	public void onLeftSwipe() {
		// TODO Auto-generated method stub

	}

	public void onRightSwipe() {
		// TODO Auto-generated method stub

	}

	class SwipeGestureDetector extends SimpleOnGestureListener {
		// Swipe properties, you can change it to make the swipe
		// longer or shorter and speed
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 400;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		private static final String tag = "tag";

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				
				
				float diffAbs = Math.abs(e1.getY() - e2.getY());
				float diff = e1.getX() - e2.getX();
				
				Log.i(tag, "onFling  diffAbs="+diffAbs +" diff="+diff + " SWIPE_MAX_OFF_PATH="+SWIPE_MAX_OFF_PATH );
				Log.i(tag, "onFling  SWIPE_MIN_DISTANCE="+SWIPE_MIN_DISTANCE +" velocityX="+velocityX + " SWIPE_THRESHOLD_VELOCITY="+SWIPE_THRESHOLD_VELOCITY );

				if (diffAbs > SWIPE_MAX_OFF_PATH)
				{
					Log.i(tag, "onFling  diffAbs > SWIPE_MAX_OFF_PATH  " + " velocityX=" + velocityX+ " SWIPE_THRESHOLD_VELOCITY=" + SWIPE_THRESHOLD_VELOCITY);
					return false;
				}
				// Left swipe
				if (diff > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					onLeftSwipe();

					// Right swipe
				} else if (-diff > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					onRightSwipe();
				}
			} catch (Exception e) {
				Log.e("YourActivity", "Error on gestures");
			}
			return false;
		}
	}

	protected void SendRequest(String url, String request, String title,
			String msg, Boolean sync, Boolean notify, int requestType,
			Object customObjectForResponse) {
		OnRequestExecuted onRequestExecuted = null;
		if (notify)
			onRequestExecuted = MyBaseActivity.this;

		AsyncHttpPostExecutor asyncHttpPost = new AsyncHttpPostExecutor(
				new BasicNameValuePair("appRequest", request),
				MyBaseActivity.this, title, msg, sync, onRequestExecuted,
				requestType, null, customObjectForResponse);
		asyncHttpPost.execute(url);

	}

	@Override
	public void OnRequestExecuted(String res, int requestTypeId,
			Object customObjectForResponse, int httpErrorCode) {
		switch (requestTypeId) {
		case GetTwitterName:

			break;

		default:
			break;
		}

	}

}
