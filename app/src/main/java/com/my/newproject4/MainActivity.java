package com.my.newproject4;

import com.my.newproject4.SplashActivity;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.my.newproject4.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends AppCompatActivity {
	
	private MainBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
	}
	
	private void initializeLogic() {
		// 1. إعداد الذاكرة المحلية (SharedPreferences)
		final android.content.SharedPreferences sp = getSharedPreferences("QuranApp", android.content.Context.MODE_PRIVATE);
		
		// 2. ربط العناصر (مع التأكد من وجودها في الـ XML)
		final com.google.android.material.tabs.TabLayout subTabs = findViewById(R.id.subTabs);
		final androidx.viewpager2.widget.ViewPager2 viewPager = findViewById(R.id.viewPager);
		final android.widget.EditText etSearch = findViewById(R.id.etSearch);
		final android.view.View lastReadCard = findViewById(R.id.lastReadCard);
		final android.widget.TextView tvLastSurah = findViewById(R.id.tvLastReadSurahName);
		
		// إعداد قائمة نتائج البحث برمجياً لتظهر فوق كل شيء
		final androidx.recyclerview.widget.RecyclerView rvSearch = new androidx.recyclerview.widget.RecyclerView(this);
		rvSearch.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
		rvSearch.setBackgroundColor(android.graphics.Color.parseColor("#0F1F1F"));
		rvSearch.setVisibility(android.view.View.GONE);
		((android.view.ViewGroup)viewPager.getParent()).addView(rvSearch, new android.view.ViewGroup.LayoutParams(-1, -1));
		
		// 3. تحديث واجهة "آخر قراءة"
		if (lastReadCard != null) {
			final int lastId = sp.getInt("last_surah_id", -1);
			final String lastName = sp.getString("last_surah_name", "");
			if (lastId != -1) {
				lastReadCard.setVisibility(android.view.View.VISIBLE);
				if (tvLastSurah != null) tvLastSurah.setText(lastName);
				lastReadCard.setOnClickListener(new android.view.View.OnClickListener() {
					@Override public void onClick(android.view.View v) {
						android.content.Intent i = new android.content.Intent(MainActivity.this, AyahActivity.class);
						i.putExtra("surah_id", lastId); i.putExtra("surah_name", lastName);
						startActivity(i);
					}
				});
			} else {
				lastReadCard.setVisibility(android.view.View.GONE);
			}
		}
		
		// 4. إعداد التبويبات والصفحات
		if (viewPager != null && subTabs != null) {
			final androidx.recyclerview.widget.RecyclerView.Adapter pagerAdapter = new androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
				@Override public int getItemViewType(int position) { return position; }
				@androidx.annotation.NonNull @Override public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
					int layoutId = R.layout.fragment_surah_list; 
					if (viewType == 1) layoutId = R.layout.fragment_juz_list; 
					else if (viewType == 2) layoutId = R.layout.fragment_favorites; 
					android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
					if (viewType == 0) {
						androidx.recyclerview.widget.RecyclerView rv = v.findViewById(R.id.rvSurahList);
						rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(MainActivity.this));
						rv.setAdapter(new SurahAdapter(loadSurahsFromJson(), sp));
					} else if (viewType == 1) {
						androidx.recyclerview.widget.RecyclerView rv = v.findViewById(R.id.rvJuzList);
						rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(MainActivity.this));
						rv.setAdapter(new JuzAdapter(loadJuzsFromJson()));
					} else if (viewType == 2) { updateFavUI(v, sp); }
					return new androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {};
				}
				@Override public void onBindViewHolder(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder h, int p) {}
				@Override public int getItemCount() { return 3; }
			};
			viewPager.setAdapter(pagerAdapter);
			new com.google.android.material.tabs.TabLayoutMediator(subTabs, viewPager, new com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy() {
				@Override public void onConfigureTab(@androidx.annotation.NonNull com.google.android.material.tabs.TabLayout.Tab tab, int position) {
					if (position == 0) tab.setText("سورة"); else if (position == 1) tab.setText("جزء"); else tab.setText("مفضلة");
				}
			}).attach();
		}
		
		// 5. محرك البحث العالمي (في ملف AyahSearch.json)
		if (etSearch != null) {
			etSearch.addTextChangedListener(new android.text.TextWatcher() {
				@Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
				@Override public void onTextChanged(CharSequence s, int st, int b, int c) {
					String q = s.toString().trim();
					if (q.length() > 2) {
						viewPager.setVisibility(android.view.View.GONE);
						subTabs.setVisibility(android.view.View.GONE);
						if(lastReadCard != null) lastReadCard.setVisibility(android.view.View.GONE);
						rvSearch.setVisibility(android.view.View.VISIBLE);
						rvSearch.setAdapter(new SearchAdapter(performSearch(q)));
					} else {
						viewPager.setVisibility(android.view.View.VISIBLE);
						subTabs.setVisibility(android.view.View.VISIBLE);
						if(lastReadCard != null) lastReadCard.setVisibility(android.view.View.VISIBLE);
						rvSearch.setVisibility(android.view.View.GONE);
					}
				}
				@Override public void afterTextChanged(android.text.Editable s) {}
			});
		}
		// ربط وبرمجة زر الإعدادات
		android.widget.ImageView btnSettings = findViewById(R.id.btnSettings);
		if (btnSettings != null) {
			btnSettings.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					android.content.Intent intent = new android.content.Intent(MainActivity.this, SettingsActivity.class);
					startActivity(intent);
				}
			});
		}
		
		// ---------------------------------------------------------
	}
	
	// --- الكلاسات والدوال المساعدة ---
	class SearchResult { String t, sn; int sid, ai; public SearchResult(String t, int sid, String sn, int ai) { this.t = t; this.sid = sid; this.sn = sn; this.ai = ai; } }
	
	private java.util.List<SearchResult> performSearch(String query) {
		java.util.List<SearchResult> list = new java.util.ArrayList<>();
		java.util.List<Surah> surahs = loadSurahsFromJson();
		try {
			java.io.InputStream is = getAssets().open("AyahSearch.json");
			byte[] buffer = new byte[is.available()]; is.read(buffer); is.close();
			org.json.JSONArray arr = new org.json.JSONArray(new String(buffer, "UTF-8"));
			for (int i = 0; i < arr.length(); i++) {
				org.json.JSONObject obj = arr.getJSONObject(i);
				String text = obj.optString("text");
				if (text.contains(query)) {
					int id = obj.optInt("surahId"); String name = "سورة " + id;
					for(Surah s : surahs) { if(s.id == id) { name = s.name; break; } }
					list.add(new SearchResult(text, id, name, obj.optInt("ayahIndex")));
				}
				if (list.size() > 50) break;
			}
		} catch (Exception e) {} return list;
	}
	
	class SearchAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SearchAdapter.ViewHolder> {
		private java.util.List<SearchResult> data;
		public SearchAdapter(java.util.List<SearchResult> d) { this.data = d; }
		@androidx.annotation.NonNull @Override public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup p, int vt) {
			return new ViewHolder(android.view.LayoutInflater.from(p.getContext()).inflate(R.layout.item_ayah, p, false));
		}
		@Override public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder h, int p) {
			final SearchResult r = data.get(p); h.t.setText(r.t); h.n.setText(r.sn + " (" + r.ai + ")");
			h.itemView.setOnClickListener(new android.view.View.OnClickListener() {
				@Override public void onClick(android.view.View v) {
					android.content.Intent i = new android.content.Intent(v.getContext(), AyahActivity.class);
					i.putExtra("surah_id", r.sid); i.putExtra("surah_name", r.sn); v.getContext().startActivity(i);
				}
			});
		}
		@Override public int getItemCount() { return data.size(); }
		class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder { android.widget.TextView t, n; public ViewHolder(android.view.View v) { super(v); t = v.findViewById(R.id.tvAyahText); n = v.findViewById(R.id.tvAyahNumber); } }
	}
	
	class Surah { int id; String name, type; int count; public Surah(int id, String name, String type, int count) { this.id = id; this.name = name; this.type = type; this.count = count; } }
	
	private java.util.List<Surah> loadSurahsFromJson() {
		java.util.List<Surah> list = new java.util.ArrayList<>();
		try {
			java.io.InputStream is = MainActivity.this.getAssets().open("Surah.json");
			byte[] b = new byte[is.available()]; is.read(b); is.close();
			org.json.JSONArray arr = new org.json.JSONArray(new String(b, "UTF-8"));
			for (int i = 0; i < arr.length(); i++) {
				org.json.JSONObject obj = arr.getJSONObject(i);
				if(obj.optString("type").equals("Surah")) list.add(new Surah(Integer.parseInt(obj.optString("surahId")), obj.optString("name"), obj.optString("mecca").equals("1") ? "مكية" : "مدنية", Integer.parseInt(obj.optString("ayahCount"))));
			}
		} catch (Exception e) {} return list;
	}
	
	class SurahAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SurahAdapter.ViewHolder> {
		private java.util.List<Surah> data; private android.content.SharedPreferences prefs;
		public SurahAdapter(java.util.List<Surah> d, android.content.SharedPreferences p) { this.data = d; this.prefs = p; }
		@androidx.annotation.NonNull @Override public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup p, int vt) { return new ViewHolder(android.view.LayoutInflater.from(p.getContext()).inflate(R.layout.item_surah, p, false)); }
		@Override public void onBindViewHolder(@androidx.annotation.NonNull final ViewHolder h, final int p) {
			final Surah s = data.get(p); h.name.setText(s.name); h.num.setText(String.valueOf(s.id)); h.type.setText(s.type); h.count.setText(s.count + " آية");
			final String fKey = "[" + s.id + "]"; boolean isF = prefs.getString("fav_surahs", "").contains(fKey);
			h.fav.setImageResource(isF ? R.drawable.ic_star_filled : R.drawable.ic_star_outline); h.fav.setColorFilter(android.graphics.Color.parseColor(isF ? "#D4A849" : "#6B8A8A"));
			h.fav.setOnClickListener(new android.view.View.OnClickListener() { @Override public void onClick(android.view.View v) { String f = prefs.getString("fav_surahs", ""); if(f.contains(fKey)) f = f.replace(fKey, ""); else f += fKey; prefs.edit().putString("fav_surahs", f).apply(); notifyItemChanged(p); } });
			h.itemView.setOnClickListener(new android.view.View.OnClickListener() { @Override public void onClick(android.view.View v) { android.content.Intent i = new android.content.Intent(v.getContext(), AyahActivity.class); i.putExtra("surah_id", s.id); i.putExtra("surah_name", s.name); v.getContext().startActivity(i); } });
		}
		@Override public int getItemCount() { return data.size(); }
		class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder { android.widget.TextView name, num, type, count; android.widget.ImageView fav; public ViewHolder(android.view.View v) { super(v); name = v.findViewById(R.id.tvSurahName); num = v.findViewById(R.id.tvSurahNumber); type = v.findViewById(R.id.tvSurahType); count = v.findViewById(R.id.tvVerseCount); fav = v.findViewById(R.id.ivFavorite); } }
	}
	
	class Juz { int id; String start; public Juz(int i, String s) { this.id = i; this.start = s; } }
	private java.util.List<Juz> loadJuzsFromJson() {
		java.util.List<Juz> list = new java.util.ArrayList<>();
		try {
			java.io.InputStream is = MainActivity.this.getAssets().open("Juz.json");
			byte[] b = new byte[is.available()]; is.read(b); is.close();
			org.json.JSONArray arr = new org.json.JSONArray(new String(b, "UTF-8"));
			java.util.List<Surah> surahs = loadSurahsFromJson();
			for (int i = 0; i < arr.length(); i++) {
				org.json.JSONObject obj = arr.getJSONObject(i); if(obj.optString("type").equals("Juz")) {
					int id = Integer.parseInt(obj.optString("surahId")); String n = "";
					for(Surah s : surahs) { if(s.id == id) { n = s.name; break; } }
					list.add(new Juz(Integer.parseInt(obj.optString("id")), "يبدأ من: سورة " + n + " (" + obj.optString("ayahIndex") + ")"));
				}
			}
		} catch (Exception e) {} return list;
	}
	
	class JuzAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<JuzAdapter.ViewHolder> {
		private java.util.List<Juz> data; private String[] ns = {"الأول", "الثاني", "الثالث", "الرابع", "الخامس", "السادس", "السابع", "الثامن", "التاسع", "العاشر", "الحادي عشر", "الثاني عشر", "الثالث عشر", "الرابع عشر", "الخامس عشر", "السادس عشر", "السابع عشر", "الثامن عشر", "التاسع عشر", "العشرون", "الحادي والعشرون", "الثاني والعشرون", "الثالث والعشرون", "الرابع والعشرون", "الخامس والعشرون", "السادس والعشرون", "السابع والعشرون", "الثامن والعشرون", "التاسع والعشرون", "الثلاثون"};
		public JuzAdapter(java.util.List<Juz> d) { this.data = d; }
		@androidx.annotation.NonNull @Override public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup p, int vt) { return new ViewHolder(android.view.LayoutInflater.from(p.getContext()).inflate(R.layout.item_juz, p, false)); }
		@Override public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder h, int p) {
			final Juz j = data.get(p); h.num.setText(String.valueOf(j.id)); final String n = (j.id <= 30) ? "الجزء " + ns[j.id - 1] : "الجزء " + j.id; h.name.setText(n); h.start.setText(j.start);
			h.itemView.setOnClickListener(new android.view.View.OnClickListener() { @Override public void onClick(android.view.View v) { android.content.Intent i = new android.content.Intent(v.getContext(), AyahActivity.class); i.putExtra("surah_id", -1); i.putExtra("juz_id", j.id); i.putExtra("juz_name", n); v.getContext().startActivity(i); } });
		}
		@Override public int getItemCount() { return data.size(); }
		class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder { android.widget.TextView num, name, start; public ViewHolder(android.view.View v) { super(v); num = v.findViewById(R.id.tvJuzNumber); name = v.findViewById(R.id.tvJuzName); start = v.findViewById(R.id.tvJuzStart); } }
	}
	
	private void updateFavUI(android.view.View v, android.content.SharedPreferences p) {
		androidx.recyclerview.widget.RecyclerView rv = v.findViewById(R.id.rvFavorites);
		android.view.View e = v.findViewById(R.id.layoutEmptyFavorites);
		if (rv == null || e == null) return;
		java.util.List<Surah> all = loadSurahsFromJson(); java.util.List<Surah> f = new java.util.ArrayList<>();
		String s = p.getString("fav_surahs", "");
		for(Surah surah : all) { if(s.contains("[" + surah.id + "]")) f.add(surah); }
		if(f.isEmpty()) { rv.setVisibility(android.view.View.GONE); e.setVisibility(android.view.View.VISIBLE); }
		else { rv.setVisibility(android.view.View.VISIBLE); e.setVisibility(android.view.View.GONE); rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(MainActivity.this)); rv.setAdapter(new SurahAdapter(f, p)); }
	}
	
	private void dummyMethodForSketchware() {
		
		
	}
	
}