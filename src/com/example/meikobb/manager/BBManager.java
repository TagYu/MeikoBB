package com.example.meikobb.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.meikobb.activity.SettingsActivity;
import com.example.meikobb.model.BBItemHeader;
import com.example.meikobb.sqlite.DBBBItemHeader;

public class BBManager {
	static BBHandler mBBHandler;
	static DBBBItemHeader mDBBBItemHeader;
	
	/* コンストラクタ */
	public BBManager(Context context) {
		initialize(context);
	}
	
	
	/**
	 * initialize
	 * @return
	 */
	public static void initialize(Context context) {
		mBBHandler = new BBHandler();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String authID = prefs.getString(SettingsActivity.PREF_KEY_AUTH_ID, "");
		String authPW = prefs.getString(SettingsActivity.PREF_KEY_AUTH_PW, "");
		mBBHandler.initialize(authID, authPW);
		
		mDBBBItemHeader = new DBBBItemHeader(context);
	}
	
	
	
	/**
	 * DB 内の記事一覧を取得
	 */
	public List<BBItemHeader> getBBItems(String filter, String orderBy, String limit) {
		List<BBItemHeader> list = new ArrayList<BBItemHeader>();
		
		Cursor cursor = mDBBBItemHeader.getReadableDatabase().query(
				DBBBItemHeader.TABLE_NAME,
				null,
				DBBBItemHeader.COL_TITLE + "||" + DBBBItemHeader.COL_AUTHOR
						+ " LIKE ?", new String[] { "%" + filter + "%" }, null,
				null, orderBy, limit);

		try {
			while (cursor.moveToNext()) {
				list.add(new BBItemHeader(
						cursor.getString(cursor
								.getColumnIndexOrThrow(DBBBItemHeader.COL_ID_DATE)),
						cursor.getString(cursor
								.getColumnIndexOrThrow(DBBBItemHeader.COL_ID_INDEX)),
						cursor.getString(cursor
								.getColumnIndexOrThrow(DBBBItemHeader.COL_TITLE)),
						cursor.getString(cursor
								.getColumnIndexOrThrow(DBBBItemHeader.COL_AUTHOR)),
						cursor.getInt(cursor
								.getColumnIndexOrThrow(DBBBItemHeader.COL_IS_READ)),
						cursor.getInt(cursor
								.getColumnIndexOrThrow(DBBBItemHeader.COL_IS_NEW))));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		
		return list;
	}
	public List<BBItemHeader> getBBItems(String limit) {
		return getBBItems("", DBBBItemHeader.COL_ID_DATE, limit);
	}
	public List<BBItemHeader> getBBItems(String orderBy, String limit) {
		return getBBItems("", orderBy, limit);
	}
	
	
	/**
	 * WEB 上から記事一覧を再取得
	 * @return
	 */
	public int reloadBBItems() {
		int count = mDBBBItemHeader.getCounts();
		
		List<BBItemHeader> obtained = mBBHandler.getAllBBItems();
		Iterator<BBItemHeader> it = obtained.iterator();
		
		while(it.hasNext()) {
			BBItemHeader item = it.next();
			if( mDBBBItemHeader.findById(item.getIdDate(), item.getIdIndex()) == null ) {
				mDBBBItemHeader.insert(item);
			} else {
				mDBBBItemHeader.update(item);
			}
		}
		
		return (mDBBBItemHeader.getCounts() - count);
	}
	
	/**
	 * 記事の内容を取得
	 * @param itemData
	 */
//	public void getItemContent(BBItemHeader itemData) {
//		mBBHandler.getBBItemContent(itemData);
//	}
	/*
	 * TODO 記事の内容取得の部分を作成
	 */
	
	/*
	 * TODO 記事内容を保持するクラス、およびそのマッピングを行う sqlite ヘルパーをつくる
	 */
	
	
}
