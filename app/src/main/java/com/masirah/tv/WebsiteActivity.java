package com.masirah.tv;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import android.widget.LinearLayout;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import android.os.Handler;
import android.app.Activity;

public class WebsiteActivity extends AppCompatActivity {
	
	public final int REQ_CD_A = 101;
	
	private LinearLayout linear1;
	private LinearLayout linear3;
	private LinearLayout linear4;
	private SwipeRefreshLayout swipeRefreshLayout1;
	private WebView webView1;
	private BottomNavigationView bottomnavigation1;
	
	private Intent a = new Intent(Intent.ACTION_GET_CONTENT);
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.website);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear3 = findViewById(R.id.linear3);
		linear4 = findViewById(R.id.linear4);
		swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout1);
		webView1 = findViewById(R.id.webView1);
		webView1.getSettings().setJavaScriptEnabled(true);
		webView1.getSettings().setSupportZoom(true);
		bottomnavigation1 = findViewById(R.id.bottomnavigation1);
		a.setType("*/*");
		a.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		webView1.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView _param1, String _param2, Bitmap _param3) {
				final String _url = _param2;
				
				super.onPageStarted(_param1, _param2, _param3);
			}
			
			@Override
			public void onPageFinished(WebView _param1, String _param2) {
				final String _url = _param2;
				
				super.onPageFinished(_param1, _param2);
			}
		});
		
		bottomnavigation1.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				final int _itemId = item.getItemId();
				bottomnavigation1.setOnNavigationItemSelectedListener(
				new com.google.android.material.bottomnavigation.
				BottomNavigationView.OnNavigationItemSelectedListener(){
					
					@Override
					public boolean onNavigationItemSelected(
					android.view.MenuItem item){
						
						switch(item.getItemId()){
							
							case 0:
							webView1.loadUrl("https://masirahtv.net/");
							return true;
							
							case 1:
							webView1.loadUrl("https://masirahtv.net/news");
							return true;
							
							case 2:
							webView1.loadUrl("https://masirahtv.net/leader");
							return true;
						}
						
						return false;
					}
				});
				return true;
			}
		});
	}
	
	private void initializeLogic() {
		// ================== إعدادات WebView ==================
		
		webView1.getSettings().setJavaScriptEnabled(true);
		webView1.getSettings().setDomStorageEnabled(true);
		webView1.getSettings().setDatabaseEnabled(true);
		webView1.getSettings().setLoadsImagesAutomatically(true);
		webView1.getSettings().setCacheMode(
		android.webkit.WebSettings.LOAD_DEFAULT);
		
		webView1.getSettings().setAllowFileAccess(true);
		webView1.getSettings().setAllowContentAccess(true);
		webView1.getSettings().setUseWideViewPort(true);
		webView1.getSettings().setLoadWithOverviewMode(true);
		
		if(android.os.Build.VERSION.SDK_INT >= 21){
			webView1.getSettings().setMixedContentMode(
			android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		
		webView1.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		
		
		// ================== WebViewClient ==================
		
		webView1.setWebViewClient(new android.webkit.WebViewClient(){
			
			@Override
			public void onPageFinished(android.webkit.WebView view, String url) {
				
				// حفظ نسخة Offline
				webView1.saveWebArchive(
				getFilesDir() + "/offline_page.mht");
				
				swipeRefreshLayout1.setRefreshing(false);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(
			android.webkit.WebView view,
			String url){
				
				// السماح فقط بروابط الويب
				if(url.startsWith("http://") || url.startsWith("https://")){
					view.loadUrl(url);
					return true;
				}
				
				// اتصال هاتفي
				if(url.startsWith("tel:")){
					startActivity(new android.content.Intent(
					android.content.Intent.ACTION_DIAL,
					android.net.Uri.parse(url)));
					return true;
				}
				
				// بريد إلكتروني
				if(url.startsWith("mailto:")){
					startActivity(new android.content.Intent(
					android.content.Intent.ACTION_SENDTO,
					android.net.Uri.parse(url)));
					return true;
				}
				
				// intent مثل واتساب
				if(url.startsWith("intent:")){
					try{
						android.content.Intent intent =
						android.content.Intent.parseUri(
						url,
						android.content.Intent.URI_INTENT_SCHEME);
						startActivity(intent);
					}catch(Exception e){}
					return true;
				}
				
				// منع cid: وأي scheme غير معروف
				return true;
			}
		});
		
		
		
		// ================== تحميل الصفحة حسب الإنترنت ==================
		
		if(_isConnected()){
			webView1.loadUrl("https://masirahtv.net/");
		}else{
			
			java.io.File file =
			new java.io.File(getFilesDir() + "/offline_page.mht");
			
			if(file.exists()){
				webView1.loadUrl("file://" + file.getAbsolutePath());
			}else{
				webView1.loadData(
				"<h2 style='text-align:center;'>📡 لا يوجد اتصال بالإنترنت</h2>",
				"text/html",
				"UTF-8");
			}
		}
		
		
		// ================== Swipe To Refresh ==================
		
		swipeRefreshLayout1.setOnRefreshListener(
		new androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener(){
			
			@Override
			public void onRefresh(){
				
				if(_isConnected()){
					webView1.reload();
				}else{
					
					android.widget.Toast.makeText(
					getApplicationContext(),
					"📡 لا يوجد اتصال بالإنترنت",
					android.widget.Toast.LENGTH_SHORT).show();
					
					swipeRefreshLayout1.setRefreshing(false);
				}
			}
		});
		
		
		// منع التعارض مع التمرير
		webView1.setOnScrollChangeListener(new View.OnScrollChangeListener(){
			@Override
			public void onScrollChange(View v,
			int scrollX,
			int scrollY,
			int oldScrollX,
			int oldScrollY){
				
				swipeRefreshLayout1.setEnabled(scrollY == 0);
			}
		});
		
		swipeRefreshLayout1.setColorSchemeColors(
		android.graphics.Color.RED);
		
		
		// ================== BottomNavigation ==================
		
		bottomnavigation1.getMenu().add(0, 0, 0, "البث")
		.setIcon(R.drawable.default_image);
		
		bottomnavigation1.getMenu().add(0, 1, 0, "الاخبار")
		.setIcon(R.drawable.default_image);
		
		bottomnavigation1.getMenu().add(0, 2, 0, "السيد القائد")
		.setIcon(R.drawable.default_image);
		webView1.setWebChromeClient(new android.webkit.WebChromeClient(){
			
			private View mCustomView;
			private android.webkit.WebChromeClient.CustomViewCallback mCustomViewCallback;
			
			@Override
			public void onShowCustomView(View view,
			CustomViewCallback callback){
				
				if(mCustomView != null){
					callback.onCustomViewHidden();
					return;
				}
				
				mCustomView = view;
				mCustomViewCallback = callback;
				
				((FrameLayout)getWindow().getDecorView())
				.addView(mCustomView,
				new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
				
				webView1.setVisibility(View.GONE);
			}
			
			@Override
			public void onHideCustomView(){
				
				((FrameLayout)getWindow().getDecorView())
				.removeView(mCustomView);
				
				mCustomView = null;
				webView1.setVisibility(View.VISIBLE);
				mCustomViewCallback.onCustomViewHidden();
			}
		});
		webView1.setDownloadListener(new android.webkit.DownloadListener(){
			@Override
			public void onDownloadStart(String url,
			String userAgent,
			String contentDisposition,
			String mimetype,
			long contentLength){
				
				android.app.DownloadManager.Request request =
				new android.app.DownloadManager.Request(
				android.net.Uri.parse(url));
				
				request.setNotificationVisibility(
				android.app.DownloadManager.Request.
				VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				
				request.setDestinationInExternalPublicDir(
				android.os.Environment.DIRECTORY_DOWNLOADS,
				android.webkit.URLUtil.guessFileName(
				url, contentDisposition, mimetype));
				
				android.app.DownloadManager dm =
				(android.app.DownloadManager)
				getSystemService(android.content.Context.DOWNLOAD_SERVICE);
				
				dm.enqueue(request);
				
				android.widget.Toast.makeText(
				getApplicationContext(),
				"⬇️ بدأ التحميل...",
				android.widget.Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_A:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		
		if(webView1.canGoBack()){
			
			String currentUrl = webView1.getUrl();
			
			// إذا ليست الصفحة الرئيسية → يرجع للرئيسية
			if(!currentUrl.equals("https://masirahtv.net/")){
				webView1.loadUrl("https://masirahtv.net/");
			}else{
				super.onBackPressed();
			}
			
		}else{
			super.onBackPressed();
		}
		
	}
	public boolean _isConnected() {
		android.net.ConnectivityManager cm =
		(android.net.ConnectivityManager)
		getSystemService(CONNECTIVITY_SERVICE);
		
		if (cm != null) {
			android.net.NetworkInfo ni = cm.getActiveNetworkInfo();
			return ni != null && ni.isConnected();
		}
		
		return false;
	}
	
}