package com.tmstudio.goon.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GoonDbHelper extends SQLiteOpenHelper {

	public static int TABLE_VERSION = 1;
	public static String TABLE_NAME = "GoonDb";

	public static final String TABLE_NAME_DN = "DiaryNote";
	public static final String TABLE_NAME_DT = "DiaryTips";
	public static final String TABLE_NAME_TC = "TipCategory";
	public static final String TABLE_NAME_TD = "TipDetail";
	public static final String TABLE_NAME_WM = "WeightMeasure";

	public static final String COL_DN_DATE = "date";
	public static final String COL_DN_NOTE = "note_text";
	public static final String COL_DN_IMG = "image";
	public static final String COL_DN_ISUPLOAD = "isupload";

	public static final String COL_DT_DATEID = "did";
	public static final String COL_DT_TEXT = "text";
	public static final String COL_DT_NUMDATE = "num_date";
	public static final String COL_DT_MODIF = "modified";

	public static final String COL_TC_CATID = "ctid";
	public static final String COL_TC_TITLE = "title";
	public static final String COL_TC_MODIF = "modified";

	public static final String COL_TD_TDID = "tdid";
	public static final String COL_TD_TID = "tid";
	public static final String COL_TD_TITLE = "title";
	public static final String COL_TD_DESC = "description";
	public static final String COL_TD_MODIF = "modified";

	public static final String COL_WM_DATE = "date";
	public static final String COL_WM_WEIGHT = "weight";

	Context context;
	SQLiteDatabase db;
	Cursor cursor;

	public GoonDbHelper(Context context) {
		super(context, TABLE_NAME, null, TABLE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}


	

}
