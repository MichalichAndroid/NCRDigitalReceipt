package com.example.ncrdigitalreciept;

import android.R.bool;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListView extends ListView {

	GestureDetector detector;

	public CustomListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomListView(Context context, GestureDetector detector) {
		super(context);
		this.detector = detector;
	}

	public boolean onTouchEvent(MotionEvent ev)

	{
		detector.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

}
