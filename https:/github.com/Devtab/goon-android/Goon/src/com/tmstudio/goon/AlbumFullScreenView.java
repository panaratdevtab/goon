package com.tmstudio.goon;


import com.tmstudio.goon.R;
import com.tmstudio.goon.database.GoonDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

public class AlbumFullScreenView extends Activity{

	private HelperUtils utils;
	private AlbumFullScreenAdapter adapter;
	private ViewPager viewPager;
	
	TextView diaryText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);
		
		GoonDB db = new GoonDB(this);

		viewPager = (ViewPager) findViewById(R.id.pager);

		utils = new HelperUtils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);

		adapter = new AlbumFullScreenAdapter(AlbumFullScreenView.this,
				utils.getFilePaths());

		viewPager.setAdapter(adapter);


		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
}
