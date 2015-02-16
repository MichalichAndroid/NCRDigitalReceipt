package com.example.ncrdigitalreciept;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class RecieverActivity extends Activity{
	protected BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// Extract data included in the Intent
			RecieverActivity.this.onReceive(context, intent);

			// do other stuff here
		}

	};

	public  void onReceive(Context context, Intent intent)
	{
		
	}
}
