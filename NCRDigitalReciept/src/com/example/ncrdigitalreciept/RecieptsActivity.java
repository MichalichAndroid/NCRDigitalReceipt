package com.example.ncrdigitalreciept;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecieptsActivity extends MyBaseActivity {

	List<ParseObject> reciepts = new ArrayList<ParseObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reciepts);
		DownloadAllReciepts();
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Application.UpdateList_ACTION);
		registerReceiver(mMessageReceiver, filter);

	}

	protected void onStop() {
		unregisterReceiver(mMessageReceiver);
		super.onStop();
	}

	protected void onResume() {
		super.onResume();
		SetLoginState();

	}

	public void onReceive(Context context, Intent intent) {
		DownloadAllReciepts();
	}

	public void onRightSwipe() {
		OpenMainMenu();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/*
		 * Rect editTextRect = new Rect();
		 * findViewById(R.id.main_view_qr_code).getHitRect(editTextRect);
		 * 
		 * if (!editTextRect.contains((int) event.getX(), (int) event.getY())) {
		 * return super.onTouchEvent(event); }
		 */

		return gestureDetector.onTouchEvent(event);
	}

	public void onRefresh(View view) {
		DownloadAllReciepts();
	}

	private void DownloadAllReciepts() {
		// TODO Auto-generated method stub
		findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		findViewById(R.id.activity_reciepts_listview).setVisibility(View.GONE);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("installationId", ParseInstallation.getCurrentInstallation()
				.getInstallationId());
		params.put("lastFetchTime", Application.GetLastFetchDate());

		ParseUser currentUser = ParseUser.getCurrentUser();
		String functionName = "fetchReceiptsByInstallationId";
		if (currentUser != null) {
			functionName = "fetchReceiptsByUser";
		}
		ParseCloud.callFunctionInBackground(functionName, params,
				new FunctionCallback<Object>() {

					@Override
					public void done(Object response, ParseException exception) {
						// TODO Auto-generated method stub
						if (response instanceof java.util.ArrayList) {

							
							OnSuccesDownload( (ArrayList<ParseObject>) response);

						} else {
							ShowReceiptsFromStorage();

						}

					}

				});

	}

	private void SetFinishDownloadVisability() {
		findViewById(R.id.progressBar1).setVisibility(View.GONE);
		findViewById(R.id.activity_reciepts_listview).setVisibility(
				View.VISIBLE);
	}

	private void OnSuccesDownload(final List<ParseObject> reciepts1)

	{

		
		ArrayList<String> uniqueRetailers = new ArrayList<String>();
		
		long batchStart = System.currentTimeMillis();
		Log.w("Pin of All Object", "Start " + (System.currentTimeMillis() - batchStart));
		for (int i = 0; i < reciepts1.size(); i++) {
			try {
				long start = System.currentTimeMillis();
				//reciepts1.get(i).getParseObject("retailer").fetchIfNeeded();
				ParseObject retailer = reciepts1.get(i).getParseObject("retailer");
				String objectId = retailer.getObjectId();
				if (uniqueRetailers.contains(objectId)){
					Log.w("'Pin' of Not unique Object", "" + (System.currentTimeMillis() - start));
					continue;
				}
				uniqueRetailers.add(objectId);
				reciepts1.get(i).getParseObject("retailer").pin();
				Log.w("Pin of Object", "" + (System.currentTimeMillis() - start));
                
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		Log.w("Pin of All Object", "End " + (System.currentTimeMillis() - batchStart));

		ParseObject.pinAllInBackground(reciepts1, new SaveCallback() {

			@Override
			public void done(ParseException parseException) {
				if ( parseException==null && reciepts1.size() > 0) {
					Application.SaveLastFetchDate(reciepts1.get(0).getCreatedAt().getTime());
				}

				ShowReceiptsFromStorage();
			}
		});

	}

	private void ShowReceiptsFromStorage() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Receipts");
	//	query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.fromLocalDatastore();
		query.orderByDescending("createdAt");
		query.include("retailer");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> recieptsRes, ParseException arg1) {
				
				
				if (recieptsRes == null)
					recieptsRes = new ArrayList<ParseObject>();
				RecieptsActivity.this.reciepts = recieptsRes;
				DrowListOfReciepts(reciepts);
				SetFinishDownloadVisability();
			}
		});
	}

	private void DrowListOfReciepts(List<ParseObject> reciepts) {

		this.reciepts = reciepts;
		ListView listView = (ListView) findViewById(R.id.activity_reciepts_listview);
		RecieptPreviewAdapter adapter = new RecieptPreviewAdapter(
				RecieptsActivity.this, reciepts);

		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				ShowRecieptAtPosition(position);
				return false;
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ShowRecieptAtPosition(position);

			}
		});

		SetFinishDownloadVisability();

	}

	private void ShowRecieptAtPosition(int position) {
		Intent intent = new Intent(RecieptsActivity.this,
				RecieptViewActivity.class);
		intent.putExtra("reciept", RecieptsActivity.this.reciepts.get(position)
				.getString("receipt"));
		startActivity(intent);
	}

}
