//package com.example.meikobb.sqlite;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.example.meikobb.model.BBItemBody;
//import com.example.meikobb.model.BBItemHead;
//
//public class DBBBItemBody extends DatabaseHelper {
//	
//	/* 定数 */
//	public static final String TABLE_NAME = "bb_item_body";
//	public static final String COL_ID_DATE = "id_date";
//	public static final String COL_ID_INDEX = "id_index";
//	public static final String COL_BODY = "body";
//	public static final String COL_IS_LOADED = "is_loaded";
//
//	
//	private DBBBItemBody(Context context) {
//		super(context);
//	}
//	
//	
//	/**
//	 * テーブルの生成
//	 */
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(
//				"CREATE TABLE "+TABLE_NAME+" (" +
//				"  "+COL_ID_DATE+" TEXT," +
//				"  "+COL_ID_INDEX+" TEXT," +
//				"  "+COL_BODY+" TEXT," +
//				"  "+COL_IS_LOADED+" INTEGER" +
//				"  PRIMARY KEY ("+COL_ID_DATE+", "+COL_ID_INDEX+")" +
//				");");
//	}
//	
//	
//	/**
//	 * テーブルの再生成
//	 */
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
//		onCreate(db);
//	}
//	/* --- */
//	
//	
//	
//	
//	/* アダプタメソッド */
//	
//	/**
//	 * 主キーから一行（モデル）を取得
//	 * @param id_date 主キー
//	 * @param id_index 主キー
//	 * @return モデルのインスタンス, データベース内に存在しないとき null
//	 */
//	public BBItemBody findById(String id_date, String id_index) {
//		Cursor cursor = getReadableDatabase().query(
//				TABLE_NAME,
//				null,
//				COL_ID_DATE + " = ? AND " + COL_ID_INDEX + " = ?",
//				new String[]{ id_date, id_index },
//				null,
//				null, null, "1");
//
//		try {
//			if (cursor.moveToNext()) {
//				return cursorToObject(cursor);
//			}
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} finally {
//			cursor.close();
//		}
//
//		return null;
//	}
//	
//	
//	/**
//	 * モデルをデータベースに保存
//	 * @param item モデル
//	 * @throws SQLException
//	 */
//	public void insert(BBItemBody item) throws SQLException {
//		SQLiteDatabase db = getWritableDatabase();
//		
//		db.beginTransaction();
//		try {
//			db.insertOrThrow(TABLE_NAME, null, generateContentValues(item));
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		}
//	}
//	
//	
//	/**
//	 * データベース内のモデルを更新
//	 * @param item モデル
//	 * @param id_date 主キー
//	 * @param id_index 主キー
//	 */
//	public void update(BBItemBody item, String id_date, String id_index) {
//		item.setIdDate(id_date);
//		item.setIdIndex(id_index);
//		update(item);
//	}
//	
//	
//	/**
//	 * データベース内のモデルを更新
//	 * @param item モデル
//	 */
//	public void update(BBItemBody item) {
//		SQLiteDatabase db = getWritableDatabase();
//		
//		db.beginTransaction();
//		try {
//			getWritableDatabase().update(TABLE_NAME, generateContentValues(item),
//					COL_ID_DATE + " = ? AND " + COL_ID_INDEX + " = ?",
//					new String[] { item.getIdDate(), item.getIdIndex() });
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		}
//	}
//	
//	
//	/**
//	 * データベース内の行数（保存されているモデルの数）を取得
//	 * @return テーブルの行数
//	 */
//	public int getCounts() {
//		Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM "+TABLE_NAME, new String[]{});
//		return cursor.moveToFirst() ? cursor.getInt(0) : (-1);
//	}
//	
//	
//	/**
//	 * Cursor から　BBItemBody を生成
//	 * @param cursor
//	 * @return
//	 */
//	public static BBItemBody cursorToObject(Cursor cursor) throws IllegalArgumentException {
//		return new BBItemBody(
//				cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_DATE)),
//				cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_INDEX)),
//				cursor.getString(cursor.getColumnIndexOrThrow(COL_BODY)),
//				cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_LOADED)));
//	}
//	
//	
//	/**
//	 * Cursor から　BBItemBody を生成
//	 * @param cursor
//	 * @return
//	 */
//	public static BBItemBody cursorToObjectAndClose(Cursor cursor) {
//		try {
//			return cursorToObject(cursor);
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			cursor.close();
//		}
//	}
//
//	
//	
//
//	/* 非公開メソッド */
//	
//	/**
//	 * モデルをテーブルに格納するために、型を変換
//	 * @param item モデル
//	 * @return ContentValues
//	 */
//	private ContentValues generateContentValues(BBItemBody item) {
//		ContentValues v = new ContentValues();
//		
//		v.put(COL_ID_DATE, item.getIdDate());
//		v.put(COL_ID_INDEX, item.getIdIndex());
//		v.put(COL_BODY, item.getBody());
//		v.put(COL_IS_LOADED, item.getIsLoaded());
//		
//		return v;
//	}
//}
