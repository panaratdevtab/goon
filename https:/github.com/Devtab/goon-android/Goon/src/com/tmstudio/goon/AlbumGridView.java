package com.tmstudio.goon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.GridView;

import com.tmstudio.goon.R;

public class AlbumGridView extends Activity {

	private HelperUtils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private AlbumGridViewAdapter adapter;
	private GridView gridView;
	private int columnWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);

		gridView = (GridView) findViewById(R.id.grid_view);

		utils = new HelperUtils(this);

		// Initilizing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths();
//		imagePaths.add("http://1.bp.blogspot.com/-6JFvxg5cQ0o/UnjVl5-m63I/AAAAAAAA0tQ/624mf88FtVo/s1600/rilakkuma-picture-of-from-21341.jpg");
//		imagePaths.add("http://www.aplacetolovedogs.com/wp-content/uploads/2013/04/baby-husky.jpg");
//		imagePaths.add("http://i.imgur.com/eS94w.jpg");
//		imagePaths.add("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQbnO71sYSjpGEC8W0T4rPvdpahR1PBE_DyZmqTf-LJY3RvRrbV");

		// Gridview adapter
		adapter = new AlbumGridViewAdapter(AlbumGridView.this, imagePaths,
				columnWidth);
		adapter.notifyDataSetChanged();

		// setting grid view adapter
		gridView.setAdapter(adapter);
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				HelperAppConstant.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((HelperAppConstant.NUM_OF_COLUMNS + 1) * padding)) / HelperAppConstant.NUM_OF_COLUMNS);

		gridView.setNumColumns(HelperAppConstant.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
	}
	
}

