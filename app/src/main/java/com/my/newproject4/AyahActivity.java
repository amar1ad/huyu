package com.my.newproject4;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
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
import androidx.recyclerview.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.my.newproject4.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class AyahActivity extends AppCompatActivity {
	
	private AyahBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = AyahBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
	}
	
	private void initializeLogic() {
		// 1. استقبال البيانات وحفظ آخر قراءة
		final int surahId = getIntent().getIntExtra("surah_id", -1);
		final String surahName = getIntent().getStringExtra("surah_name");
		int juzId = getIntent().getIntExtra("juz_id", -1);
		String juzName = getIntent().getStringExtra("juz_name");
		
		final android.content.SharedPreferences sp = getSharedPreferences("QuranApp", android.content.Context.MODE_PRIVATE);
		
		if (surahId != -1 && surahName != null) {
			sp.edit().putInt("last_surah_id", surahId).putString("last_surah_name", surahName).apply();
		}
		
		// 2. ربط العناصر
		final android.widget.TextView tvTitle = findViewById(R.id.tvAyahSurahName);
		final android.widget.ImageView btnBack = findViewById(R.id.btnBack);
		final android.widget.ImageView btnFont = findViewById(R.id.btnFontSize);
		final androidx.recyclerview.widget.RecyclerView rvAyahs = findViewById(R.id.rvAyahs);
		final com.google.android.material.floatingactionbutton.FloatingActionButton fabUp = findViewById(R.id.fabUp);
		
		if (tvTitle != null) {
			if (surahId != -1) tvTitle.setText("سورة " + surahName);
			else if (juzId != -1) tvTitle.setText(juzName);
		}
		
		// التحكم في حجم الخط
		if (btnFont != null) {
			btnFont.setOnClickListener(new android.view.View.OnClickListener() {
				@Override public void onClick(android.view.View v) {
					final int size = sp.getInt("font_size", 24);
					android.widget.PopupMenu p = new android.widget.PopupMenu(AyahActivity.this, v);
					p.getMenu().add("تكبير الخط (+)").setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {
						@Override public boolean onMenuItemClick(android.view.MenuItem i) { sp.edit().putInt("font_size", size + 4).apply(); rvAyahs.getAdapter().notifyDataSetChanged(); return true; }
					});
					p.getMenu().add("تصغير الخط (-)").setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {
						@Override public boolean onMenuItemClick(android.view.MenuItem i) { if (size > 16) { sp.edit().putInt("font_size", size - 4).apply(); rvAyahs.getAdapter().notifyDataSetChanged(); } return true; }
					});
					p.show();
				}
			});
		}
		
		if (btnBack != null) btnBack.setOnClickListener(new android.view.View.OnClickListener() { @Override public void onClick(android.view.View v) { finish(); } });
		
		// 3. إعداد القائمة
		if (rvAyahs != null) {
			rvAyahs.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
			rvAyahs.setAdapter(new AyahAdapter(loadAyahs(surahId, juzId), sp));
			
			if (fabUp != null) {
				fabUp.setOnClickListener(new android.view.View.OnClickListener() { @Override public void onClick(android.view.View v) { rvAyahs.smoothScrollToPosition(0); } });
				rvAyahs.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
					@Override public void onScrolled(androidx.recyclerview.widget.RecyclerView r, int dx, int dy) {
						if (((androidx.recyclerview.widget.LinearLayoutManager)r.getLayoutManager()).findFirstVisibleItemPosition() > 5) fabUp.setVisibility(android.view.View.VISIBLE);
						else fabUp.setVisibility(android.view.View.GONE);
					}
				});
			}
		}
		// ---------------------------------------------------------
	}
	
	class Ayah { String t; int n; public Ayah(String t, int n) { this.t = t; this.n = n; } }
	
	private java.util.List<Ayah> loadAyahs(int sid, int jid) {
		java.util.List<Ayah> list = new java.util.ArrayList<>();
		try {
			java.io.InputStream is = getAssets().open("Ayah.json");
			byte[] b = new byte[is.available()]; is.read(b); is.close();
			org.json.JSONArray arr = new org.json.JSONArray(new String(b, "UTF-8"));
			for (int i = 0; i < arr.length(); i++) {
				org.json.JSONObject obj = arr.getJSONObject(i);
				int curS = Integer.parseInt(obj.getString("surahId"));
				int curJ = Integer.parseInt(obj.getString("juzId"));
				if ((sid != -1 && curS == sid) || (jid != -1 && curJ == jid)) {
					list.add(new Ayah(obj.getString("text"), Integer.parseInt(obj.getString("ayahIndex"))));
				}
			}
		} catch (Exception e) {} return list;
	}
	
	class AyahAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<AyahAdapter.ViewHolder> {
		private java.util.List<Ayah> d; private android.content.SharedPreferences sp;
		public AyahAdapter(java.util.List<Ayah> d, android.content.SharedPreferences sp) { this.d = d; this.sp = sp; }
		@androidx.annotation.NonNull @Override public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup p, int vt) {
			return new ViewHolder(android.view.LayoutInflater.from(p.getContext()).inflate(R.layout.item_ayah, p, false));
		}
		@Override public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder h, int p) {
			final Ayah a = d.get(p); h.t.setText(a.t); h.n.setText(String.valueOf(a.n));
			h.t.setTextSize(sp.getInt("font_size", 24));
			h.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
				@Override public boolean onLongClick(android.view.View v) {
					String c = a.t + " [" + a.n + "]";
					android.content.ClipboardManager cl = (android.content.ClipboardManager) v.getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
					cl.setPrimaryClip(android.content.ClipData.newPlainText("Ayah", c));
					android.content.Intent sh = new android.content.Intent(android.content.Intent.ACTION_SEND);
					sh.setType("text/plain"); sh.putExtra(android.content.Intent.EXTRA_TEXT, c);
					v.getContext().startActivity(android.content.Intent.createChooser(sh, "مشاركة")); return true;
				}
			});
		}
		@Override public int getItemCount() { return d.size(); }
		class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder { android.widget.TextView t, n; public ViewHolder(android.view.View v) { super(v); t = v.findViewById(R.id.tvAyahText); n = v.findViewById(R.id.tvAyahNumber); } }
	}
	
	private void dummyMethodForSketchware() {
		
		
		binding.tvAyahSurahName.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/cf_bsml.ttf"), 0);
	}
	
}