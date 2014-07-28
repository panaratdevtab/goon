package com.tmstudio.goon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TipsTitleAndDescription extends Activity {
	
	TextView tip_title, tip_desc;
	String getTitle, getDesc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tips_description);
		
		tip_title = (TextView) findViewById(R.id.title_tip_desc);
		tip_desc = (TextView) findViewById(R.id.txt_desc);
		
		Intent intent =  getIntent();
		getTitle = intent.getStringExtra("title");
		getDesc = intent.getStringExtra("description");
		
		tip_title.setText(getTitle);
		tip_desc.setText(getDesc);
		
	}
}
