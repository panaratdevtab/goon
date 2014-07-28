package com.tmstudio.goon;

import com.tmstudio.goon.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
 
public class TabhostActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost);
   
        TabHost tabHost = getTabHost();
        
        // set icon and get intent for Tabhosts
        tabHost.addTab(tabHost.newTabSpec("diary").setIndicator("",getResources().getDrawable(R.drawable.tab_diary_click))
        		.setContent(new Intent(this, CalendarView.class)));
        
        tabHost.addTab(tabHost.newTabSpec("album").setIndicator("",getResources().getDrawable(R.drawable.tab_album_click))
        		.setContent(new Intent(this, AlbumGridView.class)));
        
        tabHost.addTab(tabHost.newTabSpec("newborn").setIndicator("",getResources().getDrawable(R.drawable.tab_newborn_click))
        		.setContent(new Intent(this, NewBorn.class)));
        
        tabHost.addTab(tabHost.newTabSpec("tips").setIndicator("",getResources().getDrawable(R.drawable.tab_tips_click))
        		.setContent(new Intent(this, TipsTitle.class)));
        
        tabHost.addTab(tabHost.newTabSpec("weight").setIndicator("",getResources().getDrawable(R.drawable.tab_weight_click))
        		.setContent(new Intent(this, Report.class)));
              
    }
}
