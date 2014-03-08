package com.example.meikobb.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DatabaseHelper extends SQLiteOpenHelper {

	/* 定数 */
	private static final String DATABASE_NAME = "";
	private static final int DATABASE_VERSION = 1;
	

	/*
	 * コンストラクタ
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
//
//	※ 次のメソッドをオーバーライドして実装
//	
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		/*
//		 * CREATE TABLE 文
//		 */
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		/*
//		 * DROP TABLE IF EXISTS 文
//		 */
//	}

}
