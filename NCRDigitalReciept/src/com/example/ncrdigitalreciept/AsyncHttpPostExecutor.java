package com.example.ncrdigitalreciept;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncHttpPostExecutor extends AsyncTask<String, String, String> {

	private BasicNameValuePair data = null;
	public String FResult = "";
	private Context context;
	private ProgressDialog dialog;
	private OnRequestExecuted onRequestExecuted;
	String title;
	String msg;
	Boolean isSync;
	private int requestTypeId;
	private Object customObjectForResponse;
	private int httpErrorCode = 0;
	private File  file;

	public AsyncHttpPostExecutor(BasicNameValuePair basicNameValuePair,
			Context context, String title, String msg, Boolean isSync,
			OnRequestExecuted onRequestExecuted, int requestTypeId,File  file,
			Object customObjectForResponse) {
		data = basicNameValuePair;
		this.context = context;
		this.title = title;
		this.msg = msg;
		this.onRequestExecuted = onRequestExecuted;
		this.isSync = isSync;
		this.requestTypeId = requestTypeId;
		this.customObjectForResponse = customObjectForResponse;
		this.file = file;

	}

	@Override
	protected void onPreExecute() {

		if (isSync) {

			dialog = new ProgressDialog(context);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			if (msg != null)
				dialog.setMessage(msg);
			if (title != null)
				dialog.setTitle(title);
			dialog.show();
		}
	}

	@Override
	protected String doInBackground(String... arg0) {
		String str = "";

		int timeoutConnection = 30000;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
	
		
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		 
		DefaultHttpClient client = new DefaultHttpClient();
		 
		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory(); 
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		client = new DefaultHttpClient(mgr, client.getParams());
		 
		// Set verifier      
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		int timeoutSocket = 30000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		

		try {
			if ((data.getValue() == null || data.getValue().equals("") ) && (file == null )) {
				HttpGet get = new HttpGet(arg0[0]);
				
				HttpResponse response = client.execute(get);
				StatusLine statusLine = response.getStatusLine();
				byte[] result;
				httpErrorCode = statusLine.getStatusCode();
				if (httpErrorCode == HttpURLConnection.HTTP_OK
						|| httpErrorCode == HttpURLConnection.HTTP_CREATED) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} else {
				HttpPost post = new HttpPost(arg0[0]);
				if (!(data.getValue() == null || data.getValue().equals("")))
				{
				StringEntity se = new StringEntity(data.getValue(), "UTF-8");
				se.setContentType("application/json");
				post.setEntity(se);
				}
				
				if (!(file == null ))
				{
					
					MultipartEntity mpEntity = new MultipartEntity();
					
					String filetype = "audio/3gp";
					if (file.getName().endsWith("jpg") || file.getName().endsWith("jpeg") )
						filetype="image/jpeg";
				    ContentBody cbFile = new FileBody(file, filetype);
				    mpEntity.addPart("file", cbFile);
				    post.setEntity(mpEntity);
					/*FileEntity  fe = new FileEntity (file, "application/json");
					fe.setContentType("image/jpeg");*/
				//	fe.
				
				//post.setEntity(fe);
				}

				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				byte[] result;
				httpErrorCode = statusLine.getStatusCode();
				if (httpErrorCode == HttpURLConnection.HTTP_OK
						|| httpErrorCode == HttpURLConnection.HTTP_CREATED) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return str;
	}

	@Override
	protected void onPostExecute(String result) {
		FResult = result;
		if (this.dialog != null && this.dialog.isShowing()) {
			this.dialog.dismiss();

		}
		if (onRequestExecuted != null)
			onRequestExecuted.OnRequestExecuted(result, requestTypeId,
					customObjectForResponse,httpErrorCode);

	}

}
