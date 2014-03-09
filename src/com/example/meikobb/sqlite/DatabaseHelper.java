package com.example.meikobb.sqlite;

import com.example.meikobb.model.BBItemBody;
import com.example.meikobb.model.BBItemHead;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 参考：
 *   - http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 *   - http://androidgreeve.blogspot.jp/2014/01/android-sqlite-multiple-table-basics.html#.UxsynflhVIk
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	/* ===============================================================
	 *  定数 
	 * =============================================================== */
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;
	
	/* ---------------------------------------------------------------
	 * BBItemHead
	 * --------------------------------------------------------------- */
	public static final String BBItemHead_TABLE_NAME = "bb_item_head";
	public static final String BBItemHead_COL_ID_DATE = "id_date";
	public static final String BBItemHead_COL_ID_INDEX = "id_index";
	public static final String BBItemHead_COL_DATE_SHOW = "date_show";
	public static final String BBItemHead_COL_DATE_EXEC = "date_exec";
	public static final String BBItemHead_COL_TITLE = "title";
	public static final String BBItemHead_COL_AUTHOR = "author";
	public static final String BBItemHead_COL_IS_READ = "is_read";
	public static final String BBItemHead_COL_IS_NEW = "is_new";
	
	/* ---------------------------------------------------------------
	 * BBItemBody
	 * --------------------------------------------------------------- */
	public static final String BBItemBody_TABLE_NAME = "bb_item_body";
	public static final String BBItemBody_COL_ID_DATE = "id_date";
	public static final String BBItemBody_COL_ID_INDEX = "id_index";
	public static final String BBItemBody_COL_BODY = "body";
	public static final String BBItemBody_COL_IS_LOADED = "is_loaded";
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ===============================================================
	 *  SQL文
	 * =============================================================== */
	
	/* ---------------------------------------------------------------
	 * BBItemHead
	 * --------------------------------------------------------------- */
	private static final String BBItemHead_CREATE_TABLE_SQL = 
			"CREATE TABLE "+BBItemHead_TABLE_NAME+" (" +
			"  "+BBItemHead_COL_ID_DATE+" TEXT," +
			"  "+BBItemHead_COL_ID_INDEX+" TEXT," +
			"  "+BBItemHead_COL_DATE_SHOW+" TEXT," +
			"  "+BBItemHead_COL_DATE_EXEC+" TEXT," +
			"  "+BBItemHead_COL_TITLE+" TEXT," +
			"  "+BBItemHead_COL_AUTHOR+" TEXT," +
			"  "+BBItemHead_COL_IS_READ+" INTEGER," +
			"  "+BBItemHead_COL_IS_NEW+" INTEGER," +
			"  PRIMARY KEY ("+BBItemHead_COL_ID_DATE+", "+BBItemHead_COL_ID_INDEX+")" +
			")";
	
	/* ---------------------------------------------------------------
	 * BBItemBody
	 * --------------------------------------------------------------- */
	private static final String BBItemBody_CREATE_TABLE_SQL =
			"CREATE TABLE "+BBItemBody_TABLE_NAME+" (" +
			"  "+BBItemBody_COL_ID_DATE+" TEXT," +
			"  "+BBItemBody_COL_ID_INDEX+" TEXT," +
			"  "+BBItemBody_COL_BODY+" TEXT," +
			"  "+BBItemBody_COL_IS_LOADED+" INTEGER," +
			"  PRIMARY KEY ("+BBItemBody_COL_ID_DATE+", "+BBItemBody_COL_ID_INDEX+")" +
			")";
	

	/* メンバ */
	private static DatabaseHelper sInstance;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ===============================================================
	 *  SQLiteHelper に必要な部分
	 * =============================================================== */
	
	/* インスタンスの生成 */
	public static DatabaseHelper getInstance(Context context) {
		// * quoted from:
		// http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
		// -----------------------------------------------------------------------------------------
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	/* コンストラクタ */
	protected DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* テーブル作成 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BBItemHead_CREATE_TABLE_SQL);
		db.execSQL(BBItemBody_CREATE_TABLE_SQL);
	}

	/* テーブルのアップグレード（DATABASE_VERSION 更新時) */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+BBItemHead_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+BBItemBody_TABLE_NAME);
		
		onCreate(db);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ===============================================================
	 *  アダプタメソッド
	 * =============================================================== */
	
	/* ---------------------------------------------------------------
	 * BBItemHead
	 * --------------------------------------------------------------- */
	/**
	 * 主キーから一行（モデル）を取得
	 * @param id_date 主キー
	 * @param id_index 主キー
	 * @return モデルのインスタンス, データベース内に存在しないとき null
	 */
	public BBItemHead BBItemHead_findById(String id_date, String id_index) {
		Cursor cursor = getReadableDatabase().query(
				BBItemHead_TABLE_NAME,
				null,
				BBItemHead_COL_ID_DATE + " = ? AND " + BBItemHead_COL_ID_INDEX + " = ?",
				new String[]{ id_date, id_index },
				null,
				null, null, "1");
		
		try {
			if( cursor.moveToFirst() ) {
				return BBItemHead_cursorToObjectAndClose(cursor);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * モデルをデータベースに保存
	 * @param item モデル
	 * @throws SQLException
	 */
	public void BBItemHead_insert(BBItemHead item) throws SQLException {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		try {
			db.insertOrThrow(BBItemHead_TABLE_NAME, null, BBItemHead_generateContentValues(item));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * データベース内のモデルを更新
	 * @param item モデル
	 * @param id_date 主キー
	 * @param id_index 主キー
	 */
	public void BBItemHead_update(BBItemHead item, String id_date, String id_index) {
		item.setIdDate(id_date);
		item.setIdIndex(id_index);
		BBItemHead_update(item);
	}
	
	/**
	 * データベース内のモデルを更新
	 * @param item モデル
	 */
	public void BBItemHead_update(BBItemHead item) {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		try {
			getWritableDatabase().update(BBItemHead_TABLE_NAME, BBItemHead_generateContentValues(item),
					BBItemHead_COL_ID_DATE + " = ? AND " + BBItemHead_COL_ID_INDEX + " = ?",
					new String[] { item.getIdDate(), item.getIdIndex() });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * データベース内の行数（保存されているモデルの数）を取得
	 * @return テーブルの行数
	 */
	public int BBItemHead_getCounts() {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM "+BBItemHead_TABLE_NAME, new String[]{});
		try {
			return cursor.moveToFirst() ? cursor.getInt(0) : (-1);
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * Cursor から　BBItemHead を生成
	 * @param cursor
	 * @return
	 */
	public static BBItemHead BBItemHead_cursorToObject(Cursor cursor) throws IllegalArgumentException {
		if( cursor.isBeforeFirst() || cursor.isAfterLast() ) return null;
		return new BBItemHead(
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemHead_COL_ID_DATE)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemHead_COL_ID_INDEX)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemHead_COL_DATE_SHOW)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemHead_COL_DATE_EXEC)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemHead_COL_TITLE)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemHead_COL_AUTHOR)),
				cursor.getInt(cursor.getColumnIndexOrThrow(BBItemHead_COL_IS_READ)),
				cursor.getInt(cursor.getColumnIndexOrThrow(BBItemHead_COL_IS_NEW)));
	}
	
	/**
	 * Cursor から　BBItemHead を生成
	 * @param cursor
	 * @return
	 */
	public static BBItemHead BBItemHead_cursorToObjectAndClose(Cursor cursor){
		try {
			return BBItemHead_cursorToObject(cursor);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * モデルをテーブルに格納するために、型を変換
	 * @param item モデル
	 * @return ContentValues
	 */
	private ContentValues BBItemHead_generateContentValues(BBItemHead item) {
		ContentValues v = new ContentValues();
		
		v.put(BBItemHead_COL_ID_DATE, item.getIdDate());
		v.put(BBItemHead_COL_ID_INDEX, item.getIdIndex());
		v.put(BBItemHead_COL_DATE_SHOW, item.getDateShow());
		v.put(BBItemHead_COL_DATE_EXEC, item.getDateExec());
		v.put(BBItemHead_COL_TITLE, item.getTitle());
		v.put(BBItemHead_COL_AUTHOR, item.getAuthor());
		v.put(BBItemHead_COL_IS_READ, item.getIsRead());
		v.put(BBItemHead_COL_IS_NEW, item.getIsNew());
		
		return v;
	}

	
	
	
	
	
	
	
	
	
	
	
	/* ---------------------------------------------------------------
	 * BBItemBody
	 * --------------------------------------------------------------- */
	
	/**
	 * 主キーから一行（モデル）を取得
	 * @param id_date 主キー
	 * @param id_index 主キー
	 * @return モデルのインスタンス, データベース内に存在しないとき null
	 */
	public BBItemBody BBItemBody_findById(String id_date, String id_index) {
		Cursor cursor = getReadableDatabase().query(
				BBItemBody_TABLE_NAME,
				null,
				BBItemBody_COL_ID_DATE + " = ? AND " + BBItemBody_COL_ID_INDEX + " = ?",
				new String[]{ id_date, id_index },
				null,
				null, null, "1");

		try {
			if (cursor.moveToFirst()) {
				return BBItemBody_cursorToObject(cursor);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * モデルをデータベースに保存
	 * @param item モデル
	 * @throws SQLException
	 */
	public void BBItemBody_insert(BBItemBody item) throws SQLException {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		try {
			db.insertOrThrow(BBItemBody_TABLE_NAME, null, BBItemBody_generateContentValues(item));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * データベース内のモデルを更新
	 * @param item モデル
	 * @param id_date 主キー
	 * @param id_index 主キー
	 */
	public void BBItemBody_update(BBItemBody item, String id_date, String id_index) {
		item.setIdDate(id_date);
		item.setIdIndex(id_index);
		BBItemBody_update(item);
	}
	
	/**
	 * データベース内のモデルを更新
	 * @param item モデル
	 */
	public void BBItemBody_update(BBItemBody item) {
		SQLiteDatabase db = getWritableDatabase();
		
		db.beginTransaction();
		try {
			getWritableDatabase().update(BBItemBody_TABLE_NAME, BBItemBody_generateContentValues(item),
					BBItemBody_COL_ID_DATE + " = ? AND " + BBItemBody_COL_ID_INDEX + " = ?",
					new String[] { item.getIdDate(), item.getIdIndex() });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * データベース内の行数（保存されているモデルの数）を取得
	 * @return テーブルの行数
	 */
	public int BBItemBody_getCounts() {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM "+BBItemBody_TABLE_NAME, new String[]{});
		try {
			return cursor.moveToFirst() ? cursor.getInt(0) : (-1);
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * Cursor から　BBItemBody を生成
	 * @param cursor
	 * @return
	 */
	public static BBItemBody BBItemBody_cursorToObject(Cursor cursor) throws IllegalArgumentException {
		if( cursor.isBeforeFirst() || cursor.isAfterLast() ) return null;
		return new BBItemBody(
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemBody_COL_ID_DATE)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemBody_COL_ID_INDEX)),
				cursor.getString(cursor.getColumnIndexOrThrow(BBItemBody_COL_BODY)),
				cursor.getInt(cursor.getColumnIndexOrThrow(BBItemBody_COL_IS_LOADED)));
	}
	
	/**
	 * Cursor から　BBItemBody を生成
	 * @param cursor
	 * @return
	 */
	public static BBItemBody BBItemBody_cursorToObjectAndClose(Cursor cursor) {
		try {
			return BBItemBody_cursorToObject(cursor);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * モデルをテーブルに格納するために、型を変換
	 * @param item モデル
	 * @return ContentValues
	 */
	private ContentValues BBItemBody_generateContentValues(BBItemBody item) {
		ContentValues v = new ContentValues();
		
		v.put(BBItemBody_COL_ID_DATE, item.getIdDate());
		v.put(BBItemBody_COL_ID_INDEX, item.getIdIndex());
		v.put(BBItemBody_COL_BODY, item.getBody());
		v.put(BBItemBody_COL_IS_LOADED, item.getIsLoaded());
		
		return v;
	}
}
