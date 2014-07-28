package com.tmstudio.goon;

import java.util.ArrayList;
import java.util.HashMap;

import com.tmstudio.goon.database.GoonDB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class TipsTitle extends Activity {

	String ctid;
	private ListView tipsListview;
	ArrayList<String> tipsList;
	ArrayList<String> tipid;
	private ArrayAdapter<String> listAdapter;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>(); 
	// BackPress
	private long backPressed = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tips);

		
		GoonDB db = new GoonDB(this);	     
	    data = db.getCategoryTip() ;
	    tipsList = new ArrayList<String>(); 
	    
	    for(int i = 0 ; i < data.size() ; i++){
	    	//ctid = data.get(i).get("ctid");
	    	String title = data.get(i).get("title");
	    	//tipid.add(ctid);
	    	tipsList.add(title);    	
	    }

	    tipsListview = (ListView) findViewById(R.id.tipslistview);		
	    String textString = data.get(0).get("title");      
	    // Create ArrayAdapter using the planet list.  
	    listAdapter = new ArrayAdapter<String>(this, R.layout.tips_row, tipsList);  	      
	    tipsListview.setAdapter( listAdapter );        
	    tipsListview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String cid = data.get(position).get("ctid").toString();
	        	Intent intent = new  Intent(TipsTitle.this, TipsTitleAndDesc.class);
				intent.putExtra("ctid", cid);
				startActivity(intent);	
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (backPressed + 2000 > System.currentTimeMillis()) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_HOME);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), "Press click again to exit!", Toast.LENGTH_SHORT).show();
		}
		backPressed = System.currentTimeMillis();
	}
}
