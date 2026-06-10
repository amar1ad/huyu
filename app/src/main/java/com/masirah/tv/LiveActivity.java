package com.masirah.tv;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.media.MediaPlayer;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import android.os.Handler;
import android.app.Activity;

public class LiveActivity extends AppCompatActivity {
	
	private LinearLayout linear2;
	private LinearLayout linear1;
	private ImageView imageview1;
	private Button button1;
	private TextView textview1;
	private Button button2;
	
	private MediaPlayer s;
	private Intent intent = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.live);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear2 = findViewById(R.id.linear2);
		linear1 = findViewById(R.id.linear1);
		imageview1 = findViewById(R.id.imageview1);
		button1 = findViewById(R.id.button1);
		textview1 = findViewById(R.id.textview1);
		button2 = findViewById(R.id.button2);
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
				android.net.Uri.parse("https://live.cdnbridge.tv/Almasirah/Almasirah_all/Almasirah/Almasirah_240p/chunks.m3u8"),
				"application/vnd.apple.mpegurl");
				startActivity(intent);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
	}
	
	private void initializeLogic() {
	}
	
}