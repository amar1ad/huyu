package com.masirah.tv;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;
import android.os.Handler;
import android.app.Activity;

public class MainActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private LinearLayout linear2;
	private LinearLayout linear3;
	private ImageView imageview1;
	private TextView textview1;
	private ProgressBar progressbar1;
	
	private TimerTask ad;
	private Intent aq = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		progressbar1 = findViewById(R.id.progressbar1);
	}
	
	private void initializeLogic() {
		textview1.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/fff.ttf"), 0);
		progressbar1.setIndeterminate(true);
		new android.os.Handler().postDelayed(
		new Runnable(){
			@Override
			public void run(){
				startActivity(new Intent(
				getApplicationContext(),
				WebsiteActivity.class));
				finish();
			}
		}, 2500);
		getWindow().setFlags(
		android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
		android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
		imageview1.setAlpha(0f);
		
		imageview1.animate()
		.alpha(1f)
		.setDuration(1500);
		textview1.setAlpha(0f);
		
		textview1.animate()
		.alpha(1f)
		.setDuration(1500);
	}
	
}