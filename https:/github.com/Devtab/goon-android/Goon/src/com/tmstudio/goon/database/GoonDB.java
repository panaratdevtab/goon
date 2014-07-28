package com.tmstudio.goon.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GoonDB extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "GoonDB.db";
	private static final String TABLE_WEIGHT_MEASURE = "WeightMeasure";
	private static final String TABLE_DIARY_NOTE = "DiaryNote";
	private static final String TABLE_DIARY_TIPS = "DiaryTips";
	private static final String TABLE_LIBRARY_GALLERY = "LibraryGallery";
	private static final String TABLE_TIP_CATEGORY = "TipCategory";
	private static final String TABLE_TIP_DETAIL = "TipDetail";
	private static SQLiteDatabase db;
	public GoonDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//dropTable(db);
		this.db = db;
		createTable(db);
	}

	private void createTable(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DIARY_NOTE
				+ "(date TEXT PRIMARY KEY" + ", note_text TEXT"
				+ ", image TEXT" + ", isupload INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DIARY_TIPS
				+ "(dtid INTEGER PRIMARY KEY" + ", text TEXT"
				+ ", num_date INTEGER" + ", modified TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIBRARY_GALLERY
				+ "(libraryId INTEGER PRIMARY KEY" + ", galleryId INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TIP_CATEGORY
				+ "(ctid INTEGER PRIMARY KEY" + ", title TEXT"
				+ ", modified TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TIP_DETAIL
				+ "(tdid INTEGER PRIMARY KEY" + ", tid INTEGER"
				+ ", title TEXT" + ", description TEXT" + ", modified TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_WEIGHT_MEASURE
				+ "(date TEXT PRIMARY KEY" + ", weight TEXT);");

	}

	// Diary Note
	public long insertDiaryNote(String date, String note_txt, String image,
			int isupdate) {
		long rows;
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("date", date);
			val.put("note_text", note_txt);
			val.put("image", image);
			val.put("isupload", isupdate);

			rows = db.insert(TABLE_DIARY_NOTE, null, val);
			db.close();
		} catch (Exception e) {
			e.getStackTrace();
			rows = -1;
		}
		return rows;
	}

	// Update diary note
	public long updateDiaryNote(String date, String note_txt, String image,
			int isUpdate) {
		long rows;
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("date", date);
			val.put("note_text", note_txt);
			val.put("image", image);
			val.put("isupload", isUpdate);

			rows = db.update(TABLE_DIARY_NOTE, val, "date = ?",
					new String[] { String.valueOf(date) });
			db.close();

		} catch (Exception e) {
			rows = -1;
		}

		return rows;
	}

	// get diary note by date
	public HashMap<String, String> getDiaryNoteByDate(String date) {
		HashMap<String, String> map;
		SQLiteDatabase db;
		db = this.getReadableDatabase();
		String strSQL = "SELECT * FROM " + TABLE_DIARY_NOTE + " WHERE date = '"
				+ date + "'";
		Cursor cursor = db.rawQuery(strSQL, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				map = new HashMap<String, String>();
				map.put("date", cursor.getString(0));
				map.put("note_text", cursor.getString(1));
				map.put("image", cursor.getString(2));
				map.put("isupdate", cursor.getInt(3) + "");
				return map;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// get diary note
	public ArrayList<HashMap<String, String>> getDiaryNote() {
		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();
			String strSQL = "SELECT * FROM " + TABLE_DIARY_NOTE
					+ " Order by 1 asc";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("date", cursor.getString(0));
						map.put("note_text", cursor.getString(1));
						map.put("image", cursor.getString(2));
						map.put("isupdate", cursor.getInt(3) + "");
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}
	}

	// get note text diary note by date
	public String getDiaryNoteByDate_noteText(String date) {
		String note = "";
		SQLiteDatabase db;
		db = this.getReadableDatabase();
		String strSQL = "SELECT note_text FROM " + TABLE_DIARY_NOTE
				+ " WHERE date = '" + date + "'";
		Cursor cursor = db.rawQuery(strSQL, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				note = cursor.getString(0);
			} else {
				note = "";
			}
		} else {
			note = "";
		}
		return note;
	}

	// check diary
	public boolean checkDiary(String date) {
		HashMap<String, String> map;
		map = getDiaryNoteByDate(date);
		if (map == null) {
			return false;
		} else {
			return true;
		}
	}

	// DiaryTips
	// insert diary tips
	public long insertDiaryTips(int dtid, String tip_text, int num_date,
			String modified) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("dtid", dtid);
			val.put("text", tip_text);
			val.put("num_date", num_date);
			val.put("modified", modified);

			long rows = db.insert(TABLE_DIARY_TIPS, null, val);

			db.close();
			return rows;
		} catch (Exception e) {
			return -1;
		}
	}

	// get diary tips
	public ArrayList<HashMap<String, String>> getDiaryTips() {
		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();// Read Data
			String strSQL = "SELECT * FROM " + TABLE_DIARY_TIPS;
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("dtid", cursor.getString(0));
						map.put("text", cursor.getString(1));
						map.put("num_date", cursor.getString(2));
						map.put("modified", cursor.getString(3));
						list.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}
	}

	// get diary tips by date
	public ArrayList<HashMap<String, String>> getDiaryTipsByDate(String date) {
		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();// Read Data
			String strSQL = "SELECT * FROM " + TABLE_DIARY_TIPS
					+ " where num_date = '" + date + "'";
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("dtid", cursor.getString(0));
						map.put("text", cursor.getString(1));
						map.put("num_date", cursor.getString(2));
						map.put("modified", cursor.getString(3));
						list.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}
	}

	// LibraryGallery
	// insert library gallery
	public long insertLibraryGallery(int libraryId, int galleryId) {
		long rows = 0;
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("libraryId", libraryId);
			val.put("galleryId", galleryId);

			rows = db.insert(TABLE_LIBRARY_GALLERY, null, val);
			db.close();
		} catch (Exception e) {
			rows = -1;
		}

		return rows;
	}

	// TipCategory
	// insert category tip
	public long insertCategoryTip(int id, String title, String modified) {
		long rows = 0;
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("ctid", id);
			val.put("title", title);
			val.put("modified", modified);

			rows = db.insert(TABLE_TIP_CATEGORY, null, val);
			db.close();
		} catch (Exception e) {
			rows = -1;
		}

		return rows;
	}

	// get category tip
	public ArrayList<HashMap<String, String>> getCategoryTip() {
		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();// Read Data
			String strSQL = "SELECT * FROM " + TABLE_TIP_CATEGORY;
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("ctid", cursor.getString(0));
						map.put("title", cursor.getString(1));
						map.put("modified", cursor.getString(2));
						list.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}
	}

	// TipDetail
	// insert tip detail
	public long insertTipDetail(int tdid, int tid, String title,
			String description, String modified) {
		long rows = 0;
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("tdid", tdid);
			val.put("tid", tid);
			val.put("title", title);
			val.put("description", description);
			val.put("modified", modified);

			rows = db.insert(TABLE_TIP_DETAIL, null, val);
			db.close();
		} catch (Exception e) {
			rows = -1;
		}
		return rows;
	}

	// get tip detail by tid
	public ArrayList<HashMap<String, String>> getTipDetailByTid(int tid) {
		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();// Read Data
			String strSQL = "SELECT * FROM " + TABLE_TIP_DETAIL + " where tid = " + tid;
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("tdid", cursor.getString(0));
						map.put("tid", cursor.getString(1));
						map.put("title", cursor.getString(2));
						map.put("description", cursor.getString(3));
						map.put("modified", cursor.getString(4));
						list.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}
	}
	// get tip detail by tdid
		public ArrayList<HashMap<String, String>> getTipDetailByTdid(int tdid) {
			try {
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map;
				SQLiteDatabase db;
				db = this.getReadableDatabase();// Read Data
				String strSQL = "SELECT * FROM " + TABLE_TIP_DETAIL + " where tdid = '" + tdid + "'";
				Cursor cursor = db.rawQuery(strSQL, null);

				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							map = new HashMap<String, String>();
							map.put("tdid", cursor.getString(0));
							map.put("tid", cursor.getString(1));
							map.put("title", cursor.getString(2));
							map.put("description", cursor.getString(3));
							map.put("modified", cursor.getString(4));
							list.add(map);
						} while (cursor.moveToNext());
					}
				}
				cursor.close();
				db.close();
				return list;
			} catch (Exception e) {
				e.getStackTrace();
				return null;
			}
		}

	// WeightMeasure
	// insert weight
	public long insertWeightMeasure(String date, String weight) {
		try {
			SQLiteDatabase db;
			db = this.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put("date", date);
			val.put("weight", weight);

			long rows = db.insert(TABLE_WEIGHT_MEASURE, null, val);

			db.close();
			return rows;
		} catch (Exception e) {
			return -1;
		}
	}

	// get weight by date
	public String getWeightByDate(String date) {
		int fDate, fMonth;
		String weight = "0.0";
		String[] sMDate = date.split("/");
		int mDate = Integer.parseInt(sMDate[2]);
		int mMonth = Integer.parseInt(sMDate[1]);
		String fSDate = getFirstDate();
		if (fSDate != null) {
			String[] sFDate = fSDate.split("/");
			fDate = Integer.parseInt(sFDate[2]);
			fMonth = Integer.parseInt(sFDate[1]);
		} else {
			fDate = 0;
			fMonth = 0;
		}
		SQLiteDatabase db;
		db = this.getReadableDatabase();

		String strSQL = "SELECT * FROM WeightMeasure WHERE date like '" + date
				+ "';";
		Cursor cursor = db.rawQuery(strSQL, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				weight = cursor.getString(1);
			} else {
				if (mDate > fDate && mMonth >= fMonth) {
					weight = getLastWeight();
				} else {
					weight = "0.0";
				}
			}
		} else {
			weight = "0.0";
		}
		cursor.close();
		db.close();
		return weight;
	}

	// get weight by month
	public ArrayList<HashMap<String, String>> getWeightByMonth(int month) {
		try {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();// Read Data
			String sMonth = month + "";
			if (month < 10) {
				sMonth = "0" + sMonth;
			}

			String strSQL = "SELECT * FROM WeightMeasure Where date like '%/"
					+ sMonth + "/%'" + " Order by 1 asc";
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("date", cursor.getString(0));
						map.put("weight", cursor.getString(1));
						list.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}

	}

	// get weight
	public ArrayList<HashMap<String, String>> getWeight() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {

			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase();// Read Data

			String strSQL = "SELECT * FROM WeightMeasure Order by 1 asc";
			Cursor cursor = db.rawQuery(strSQL, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("date", cursor.getString(0));
						map.put("weight", cursor.getString(1));
						list.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return list;
		} catch (Exception e) {
			e.getStackTrace();
			list = null;
		}
		return null;
	}

	// update weight
	public long UpdateWeightMeasure(String date, String weight) {
		try {
			long rows;
			SQLiteDatabase db;
			db = this.getWritableDatabase();// Write Data
			String strSQL = "SELECT * FROM WeightMeasure WHERE date like '"
					+ date + "';";
			Cursor cursor = db.rawQuery(strSQL, null);
			String cWeight;
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					cWeight = cursor.getString(1);
				} else {
					cWeight = "0.0";
				}
			} else {
				cWeight = "0.0";
			}
			cursor.close();

			ContentValues val = new ContentValues();
			val.put("date", date);
			val.put("weight", weight);
			if (cWeight != "0.0") {
				rows = db.update("WeightMeasure", val, "date = ?",
						new String[] { String.valueOf(date) });
			} else {
				rows = insertWeightMeasure(date, weight);
			}
			db.close();
			return rows;
		} catch (Exception e) {
			return -1;
		}
	}

	// get last weight insert
	public String getLastWeight() {
		int size;
		String lastWeight;
		ArrayList<HashMap<String, String>> list;
		HashMap<String, String> map = null;

		list = getWeight();
		size = list.size();
		if (list.size() > 0) {
			map = list.get(size - 1);
			lastWeight = map.get("weight");
		} else {
			lastWeight = "";
		}

		return lastWeight;
	}

	// get first date
	public String getFirstDate() {
		String fDate;

		ArrayList<HashMap<String, String>> list;
		HashMap<String, String> map;

		list = getWeight();
		if (list.equals(null)) {
			map = list.get(0);
			fDate = map.get("date");
		} else {
			fDate = null;
		}
		return fDate;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void dropTable(){
		long row = 0;
		String dropDiary = "DROP TABLE " + TABLE_DIARY_NOTE;
		String dropWeight = "DROP TABLE " + TABLE_WEIGHT_MEASURE;
		db.execSQL(dropDiary);
		db.execSQL(dropWeight);
		onCreate(db);
	}

}
