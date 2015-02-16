package com.example.ncrdigitalreciept;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainDrawerActivity extends Activity {

	private String[] mLeftTitles = new String[] { "left1", "left2", "left3" };
	private String[] mRightTitles = new String[] { "right1", "right2", "right3" };
	private DrawerLayout mDrawerLayout;
	private ListView mLeftDrawerList;
	private ListView mRightDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_main_drawer);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title_bar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_drawer_layout);
		mLeftDrawerList = (ListView) findViewById(R.id.activity_main_drawer_left_drawer);
		mRightDrawerList = (ListView) findViewById(R.id.activity_main_drawer_right_drawer);

		// Set the adapter for the list view
		mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mLeftTitles));
		// Set the list's click listener
		mLeftDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onLeftItemClicked(position);

					}

				});

		mRightDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mRightTitles));

		mRightDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onRightItemClicked(position);

					}

				});

	}

	private void onLeftItemClicked(int position) {

		mLeftDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mLeftDrawerList);
		SetTitleText(mLeftTitles[position]);
	}

	private void SetTitleText(String text) {
		((TextView)findViewById(R.id.custom_title_text)).setText(text);
		
	}

	private void onRightItemClicked(int position) {
		mRightDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mRightDrawerList);
		SetTitleText(mRightTitles[position]);
	}

	public void onLeftClick(View view) {
		mDrawerLayout.openDrawer(Gravity.START);
	}

	public void onRightClick(View view) {
		mDrawerLayout.openDrawer(Gravity.END);
	}

}
