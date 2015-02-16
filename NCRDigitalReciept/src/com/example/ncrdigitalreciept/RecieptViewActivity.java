package com.example.ncrdigitalreciept;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

public class RecieptViewActivity extends Activity {

	private static final String FILENAME = "Receipt.html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_reciept_view);
			WebView view = (WebView) findViewById(R.id.activity_reciept_viewer_webview);

			String data = getIntent().getStringExtra("reciept");
			view.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= 19) {
				findViewById(R.id.header_right).setVisibility(View.VISIBLE);
			}
		} catch (Exception exception) {

		}
	}

	public void onPrint(View view) {
		WebView webView = (WebView) findViewById(R.id.activity_reciept_viewer_webview);
		createWebPrintJob(webView);
	}

	@SuppressLint("InlinedApi")
	public void onShare(View view) {
		//SendAsBody();
		SendAsFile();
	}

	private void SendAsBody() {
		String data = getIntent().getStringExtra("reciept");
		Intent share = new Intent(Intent.ACTION_SEND);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;

		Spanned spanned = Html.fromHtml(data);
		share.putExtra(Intent.EXTRA_TEXT, spanned);
		share.putExtra(Intent.EXTRA_SUBJECT, "Receipt");
		share.setType("text/plain");

		startActivity(Intent.createChooser(share, "Share Receipt"));
	}

	private void SendAsFile() {
		String data = getIntent().getStringExtra("reciept");
		/*
		 * String fileNameWithPath = getString(R.string.app_name) +
		 * File.pathSeparator + "PDF-HtmlWorkerParsed.pdf"; try {
		 * 
		 * Document document = new Document(PageSize.A4);
		 * 
		 * FileOutputStream fos = new FileOutputStream(fileNameWithPath);
		 * PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
		 * 
		 * document.open();
		 * 
		 * // ********************************************************** // if
		 * required, you can add document meta data
		 * document.addAuthor("Ravinder"); // document.addCreator( creator );
		 * document.addSubject("HtmlWoker Parsed Pdf from iText");
		 * document.addCreationDate();
		 * document.addTitle("HtmlWoker Parsed Pdf from iText"); //
		 * *********************************************************
		 */

		/*
		 * HTMLWorker htmlWorker = new HTMLWorker(document);
		 * htmlWorker.parse(new StringReader(data));
		 * 
		 * document.close(); fos.close();
		 * 
		 * } catch (Exception exception) { exception.printStackTrace(); }
		 */
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + File.separator
				+ getString(R.string.app_name));
		myDir.mkdirs();
		File f = new File(root + File.separator + getString(R.string.app_name)
				+ File.separator + FILENAME);
		if (f.exists())
			f.delete();

		FileOutputStream out;
		try {
			f.createNewFile();
			out = new FileOutputStream(f);
			out.write(data.getBytes("UTF-8"));
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Uri uri = Uri.fromFile(f);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, "");
		i.putExtra(Intent.EXTRA_SUBJECT, "Receipt");
		i.putExtra(Intent.EXTRA_TEXT, "");
		i.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(i, "Share Receipt"));
	}

	@SuppressLint("NewApi")
	private void createWebPrintJob(WebView webView) {

		try {
			PrintManager printManager = (PrintManager) this
					.getSystemService(Context.PRINT_SERVICE);

			PrintDocumentAdapter printAdapter = webView
					.createPrintDocumentAdapter();

			String jobName = getString(R.string.app_name);

			printManager.print(jobName, printAdapter,
					new PrintAttributes.Builder().build());
		} catch (Exception exception) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Print Error!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// do things
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

}
