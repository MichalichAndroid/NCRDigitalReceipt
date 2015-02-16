package com.example.ncrdigitalreciept;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecieptPreviewAdapter extends ArrayAdapter<ParseObject> {
	private final MyBaseActivity context;
	private final List<ParseObject> values;
	private final Spanned[] textValues;
	private static final int DEFAULT_THRESHOLD = 128;

	SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dt2 = new SimpleDateFormat("HH:mm");
	Typeface custom_font;

	public RecieptPreviewAdapter(MyBaseActivity context,
			List<ParseObject> values) {
		super(context, R.layout.reciepts_list_item, values);
		this.context = context;
		this.values = values;
		textValues = new Spanned[values.size()];
		try {
			custom_font = Typeface.createFromAsset(context.getAssets(),
					"fonts/SpecialElite.ttf");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.reciepts_list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.logoImage = (ImageView) rowView
					.findViewById(R.id.reciept_list_item_retailer_logo);
			viewHolder.retailerName = (TextView) rowView
					.findViewById(R.id.reciept_list_item_text_container_left_text_retailer_name);
			viewHolder.textAdress = (TextView) rowView
					.findViewById(R.id.reciept_list_item_text_container_left_text_retailer_adress);
			viewHolder.totalAmount = (TextView) rowView
					.findViewById(R.id.reciept_list_item_text_container_left_text_retailer_purchase_amount);

			TextView textView = (TextView) rowView
					.findViewById(R.id.reciept_list_item_text_container_left_text_retailer_purchase_head);
			if (custom_font != null) {
				viewHolder.totalAmount.setTypeface(custom_font);
				textView.setTypeface(custom_font);
			}
			viewHolder.textDate = (TextView) rowView
					.findViewById(R.id.reciept_list_item_text_container_right_text_date);

			// viewHolder.webView = (WebView)
			// rowView.findViewById(R.id.reciepts_list_item_webview);

			// viewHolder.webView = (ImageView)
			// rowView.findViewById(R.id.reciepts_list_item_webview);
			// viewHolder.webView = (TextView)
			// rowView.findViewById(R.id.reciepts_list_item_webview);

			viewHolder.index = position;

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		String data = values.get(position).getString("receipt");
		holder.textAdress.setText(values.get(position)
				.getString("storeAddress"));
		holder.totalAmount.setText(values.get(position).getString("total"));

		Date date = values.get(position).getUpdatedAt();
		if (!isToday(date)) {
			holder.textDate.setText(dt1.format(date));
		} else {
			holder.textDate.setText(dt2.format(date));
		}
		ParseObject object = values.get(position).getParseObject("retailer");
		try {
			//object.fetchIfNeeded();
			holder.retailerName.setText(object.getString("name"));
			final ImageView logoImage = holder.logoImage;
			
			object.getParseFile("logo").getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] arg0, ParseException arg1) {
					// TODO Auto-generated method stub
					try {
						Bitmap bMap = BitmapFactory.decodeByteArray(arg0, 0,
								arg0.length);
						logoImage.setImageBitmap(bMap);
					} catch (Exception exception) {
						exception.printStackTrace();
					}

				}
			});
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		return rowView;
	}

	private boolean isToday(Date date) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal2.setTime(date);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
						.get(Calendar.DAY_OF_YEAR);
		return sameDay;
	}

	public boolean loadImageFromURL(String fileUrl, ImageView iv) {
		try {

			URL myFileUrl = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();

			InputStream is = conn.getInputStream();
			iv.setImageBitmap(BitmapFactory.decodeStream(is));

			return true;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	static class ViewHolder {

		public ImageView logoImage;
		public TextView textDate;
		public TextView retailerName;
		public TextView totalAmount;
		public TextView textAdress;
		// public WebView webView;
		// public ImageView webView;
		// public TextView webView;
		public int index;

	}
}
