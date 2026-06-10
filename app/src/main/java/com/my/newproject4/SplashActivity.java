package com.my.newproject4;

import android.animation.*;
import android.app.*;
import android.content.*;
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
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.my.newproject4.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class SplashActivity extends AppCompatActivity {
	
	private SplashBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = SplashBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
	}
	
	private void initializeLogic() {
		// كود ينتظر ثانيتين (2000 مللي ثانية) ثم ينتقل للرئيسية
		// نستخدم الـ Handler لضمان التوافق مع جافا القديمة
		
		final android.os.Handler handler = new android.os.Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// الانتقال إلى النشاط الرئيسي
				android.content.Intent intent = new android.content.Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				
				// إغلاق شاشة الانطلاق لكي لا يعود إليها المستخدم عند الضغط على زر الرجوع
				finish();
				
				// إضافة حركة انتقال سلسة (Animation)
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}, 2000); // مدة الانتظار: ثانيتان
		
		// ==========================================
		// إغلاق الدالة
	}
	private void dummy() {
	}
	
}