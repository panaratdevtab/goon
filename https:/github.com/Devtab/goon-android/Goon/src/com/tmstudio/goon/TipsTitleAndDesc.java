package com.tmstudio.goon;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tmstudio.goon.R;
import com.tmstudio.goon.database.GoonDB;

@SuppressLint("SimpleDateFormat")
public class TipsTitleAndDesc extends Activity {

	GoonDB db;
	String tdid, title, desc;
	int ctid;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();  
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.tips_desc);

		db = new GoonDB(this);		
		Intent intent =  getIntent();
		String ctidString = intent.getStringExtra("ctid");
		
		ctid = Integer.parseInt(ctidString);

		// 1. pass context and data to the custom adapter
		TipsTitleAndDescAdapter adapter = new TipsTitleAndDescAdapter(this, generateData());

		// 2. Get ListView from activity_main.xml
		ListView listView = (ListView) findViewById(R.id.listviewdes);

		// 3. setListAdapter
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(TipsTitleAndDesc.this, TipsTitleAndDescription.class);
	        	intent.putExtra("title", data.get(position).get("title").toString());
	        	intent.putExtra("description", data.get(position).get("description").toString());
	            startActivity(intent);
			}
		});
	}

	
	private ArrayList<TipsTitleAndDescItem> generateData() {

	    data = db.getTipDetailByTid(ctid) ;
	    ArrayList<String> tipsDescList = new ArrayList<String>(); 
	    ArrayList<TipsTitleAndDescItem> items = new ArrayList<TipsTitleAndDescItem>();
	    
	    for(int i = 0 ; i < data.size() ; i++){    	
	    	title = data.get(i).get("title");
	    	desc = data.get(i).get("description");    	
			items.add(new TipsTitleAndDescItem(title, desc));		
	    }

		return items;
	}
}
