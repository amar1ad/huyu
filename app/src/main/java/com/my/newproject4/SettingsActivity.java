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

public class SettingsActivity extends AppCompatActivity {
	
	private SettingsBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = SettingsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
	}
	
	private void initializeLogic() {
		// 1. استدعاء قاعدة البيانات المحلية
		final android.content.SharedPreferences sp = getSharedPreferences("QuranApp", android.content.Context.MODE_PRIVATE);
		
		// 2. ربط العناصر من ملف الـ XML
		android.widget.ImageView btnBack = findViewById(R.id.btnBackSettings);
		android.widget.LinearLayout rowClearFav = findViewById(R.id.rowClearFav);
		android.widget.LinearLayout rowClearLastRead = findViewById(R.id.rowClearLastRead);
		
		// برمجة زر الرجوع للشاشة السابقة
		if (btnBack != null) {
			btnBack.setOnClickListener(new android.view.View.OnClickListener() {
				@Override public void onClick(android.view.View v) { finish(); }
			});
		}
		
		// 🌟 برمجة زر "مسح المفضلة" 🌟
		if (rowClearFav != null) {
			rowClearFav.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					// تفريغ قائمة المفضلة
					sp.edit().remove("fav_surahs").apply();
					android.widget.Toast.makeText(SettingsActivity.this, "تم حذف جميع السور من المفضلة", android.widget.Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		// 🌟 برمجة زر "مسح آخر قراءة" 🌟
		if (rowClearLastRead != null) {
			rowClearLastRead.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					// حذف بيانات آخر سورة مقروءة
					sp.edit().remove("last_surah_id").remove("last_surah_name").apply();
					android.widget.Toast.makeText(SettingsActivity.this, "تم مسح علامة آخر قراءة (ستختفي عند العودة للرئيسية)", android.widget.Toast.LENGTH_LONG).show();
				}
			});
		}
		
		// ==========================================
		// إغلاق دالة onCreate الأساسية في سكتشوير
	}
	private void dummyForSketchware() {
		
		
	}
	
}