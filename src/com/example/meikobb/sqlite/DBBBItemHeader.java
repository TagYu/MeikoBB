package com.example.meikobb.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.meikobb.model.BBItemHeader;

public class DBBBItemHeader extends DatabaseHelper {
	
	/* 定数 */
	public static final String TABLE_NAME = "bb_database";
	public static final String COL_ID_DATE = "id_date";
	public static final String COL_ID_INDEX = "id_index";
	public static final String COL_TITLE = "title";
	public static final String COL_AUTHOR = "author";
	public static final String COL_IS_READ = "is_read";
	public static final String COL_IS_NEW = "is_new";

	
	/* 必須メソッド */
	public DBBBItemHeader(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE "+TABLE_NAME+" (" +
				"  "+COL_ID_DATE+" TEXT PRIMARY KEY," +
				"  "+COL_ID_INDEX+" TEXT PRIMARY KEY," +
				"  "+COL_TITLE+" TEXT," +
				"  "+COL_AUTHOR+" TEXT," +
				"  "+COL_IS_READ+" INTEGER," +
				"  "+COL_IS_NEW+" INTEGER" +
				");");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}
	/* --- */
	
	
	
	
	/* アダプタメソッド */
	public BBItemHeader findById(String id_date, String id_index) {
		Cursor cursor = getReadableDatabase().query(
				TABLE_NAME,
				null,
				COL_ID_DATE + " = ? AND " + COL_ID_INDEX + " = ?",
				new String[]{ id_date, id_index },
				null,
				null, null, "1");

		try {
			if (cursor.moveToNext()) {
				return new BBItemHeader(
						cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_DATE)),
						cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_INDEX)),
						cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
						cursor.getString(cursor.getColumnIndexOrThrow(COL_AUTHOR)),
						cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_READ)),
						cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_NEW)));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public void insert(BBItemHeader item) throws SQLException {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		try {
			db.insertOrThrow(TABLE_NAME, null, generateContentValues(item));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void update(BBItemHeader item, String id_date, String id_index) {
		item.setIdDate(id_date);
		item.setIdIndex(id_index);
		update(item);
	}
	public void update(BBItemHeader item) {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		try {
			getWritableDatabase().update(TABLE_NAME, generateContentValues(item),
					COL_ID_DATE + " = ? AND " + COL_ID_INDEX + " = ?",
					new String[] { item.getIdDate(), item.getIdIndex() });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public int getCounts() {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM "+TABLE_NAME, new String[]{});
		return cursor.moveToFirst() ? cursor.getInt(0) : (-1);
	}

	
	
	
	private ContentValues generateContentValues(BBItemHeader item) {
		ContentValues v = new ContentValues();
		
		v.put(COL_ID_DATE, item.getIdDate());
		v.put(COL_ID_INDEX, item.getIdIndex());
		v.put(COL_TITLE, item.getTitle());
		v.put(COL_AUTHOR, item.getAuthor());
		v.put(COL_IS_READ, item.getIsRead());
		v.put(COL_IS_NEW, item.getIsNew());
		
		return v;
	}
}
