//package com.tmstudio.goon.database;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.view.Window;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.example.goon.R;
//
//public class Test extends Activity {
//	SQLiteDatabase mDb;
//	GoonDbHelper mHelper;
//	Cursor mCursor;
//	
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.test);
//
//		checkDatabase();
//
//		ListView listView1 = (ListView) findViewById(R.id.listView1);
//		ArrayList<String> dirArray = new ArrayList<String>();
//
//		mHelper = new GoonDbHelper(getApplicationContext());
//		mDb = mHelper.getWritableDatabase();
//		mCursor = mDb.rawQuery("SELECT * FROM " + GoonDbHelper.TABLE_NAME, null);
//		mCursor.moveToFirst();
//		while (!mCursor.isAfterLast()) {
//			dirArray.add("Weight : "
//					+ mCursor.getString(mCursor
//							.getColumnIndex(GoonDbHelper.COL_WM_WEIGHT)));
//			mCursor.moveToNext();
//		}
//
//		ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(
//				getApplicationContext(), android.R.layout.simple_list_item_1,
//				dirArray);
//		listView1.setAdapter(adapterDir);
//	}
//
//	public void checkDatabase() {
//		String url = "/data/data/" + getPackageName() + "/databases/GoonDb";
//		File f = new File(url);
//		if (!f.exists()) {
//			try {
//				mHelper = new GoonDbHelper(this);
//				mDb = mHelper.getWritableDatabase();
//				mDb.close();
//				mHelper.close();
//				InputStream in = getAssets().open("GoonDb");
//				OutputStream out = new FileOutputStream(url);
//				byte[] buffer = new byte[in.available()];
//				in.read(buffer);
//				out.write(buffer, 0, buffer.length);
//				in.close();
//				out.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void onPause() {
//		super.onPause();
//		mHelper.close();
//		mDb.close();
//	}
//}
