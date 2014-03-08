package com.example.meikobb.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.meikobb.activity.SettingActivity;
import com.example.meikobb.model.BBItemBody;
import com.example.meikobb.model.BBItemHead;
import com.example.meikobb.sqlite.DBBBItemBody;
import com.example.meikobb.sqlite.DBBBItemHead;

public class BBManager {
	static BBHandler mBBHandler;
	static DBBBItemHead mDBBBItemHead;
	static DBBBItemBody mDBBBItemBody;
	
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
		String authID = prefs.getString(SettingActivity.PREF_KEY_AUTH_ID, "");
		String authPW = prefs.getString(SettingActivity.PREF_KEY_AUTH_PW, "");
		mBBHandler.initialize(authID, authPW);

		mDBBBItemHead = new DBBBItemHead(context);
		mDBBBItemBody = new DBBBItemBody(context);
	}
	
	
	
	/**
	 * DB 内の記事一覧を取得
	 */
	public List<BBItemHead> getHeads(String filter, String orderBy, String limit) {
		List<BBItemHead> list = new ArrayList<BBItemHead>();
		
		Cursor cursor = mDBBBItemHead.getReadableDatabase().query(
				DBBBItemHead.TABLE_NAME,
				null,
				DBBBItemHead.COL_TITLE + "||" + DBBBItemHead.COL_AUTHOR
						+ " LIKE ?", new String[] { "%" + filter + "%" }, null,
				null, orderBy, limit);

		try {
			while (cursor.moveToNext()) {
				list.add(new BBItemHead(
						cursor.getString(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_ID_DATE)),
						cursor.getString(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_ID_INDEX)),
						cursor.getString(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_DATE_SHOW)),
						cursor.getString(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_DATE_EXEC)),
						cursor.getString(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_TITLE)),
						cursor.getString(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_AUTHOR)),
						cursor.getInt(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_IS_READ)),
						cursor.getInt(cursor.getColumnIndexOrThrow(DBBBItemHead.COL_IS_NEW))));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		
		return list;
	}
	public List<BBItemHead> getHeads(String limit) {
		return getHeads("", DBBBItemHead.COL_ID_DATE, limit);
	}
	public List<BBItemHead> getHeads(String orderBy, String limit) {
		return getHeads("", orderBy, limit);
	}
	
	
	/**
	 * WEB 上から記事一覧を再取得
	 * @return
	 */
	public int reloadHeads() {
		int count = mDBBBItemHead.getCounts();
		
		List<BBItemHead> obtained = mBBHandler.getAllBBItems();
		Iterator<BBItemHead> it = obtained.iterator();
		
		while(it.hasNext()) {
			BBItemHead item = it.next();
			if( mDBBBItemHead.findById(item.getIdDate(), item.getIdIndex()) == null ) {
				mDBBBItemHead.insert(item);
			} else {
				mDBBBItemHead.update(item);
			}
		}
		
		return (mDBBBItemHead.getCounts() - count);
	}
	
	/**
	 * 記事の内容を取得
	 * @param itemHead
	 */
	public BBItemBody loadBody(BBItemHead itemHead) {
		BBItemBody itemBody = mDBBBItemBody.findById(itemHead.getIdDate(), itemHead.getIdIndex());
		
		// DB 未挿入の場合は新規インスタンスを作成
		if( itemBody == null ) {
			itemBody = new BBItemBody(itemHead.getIdDate(), itemHead.getIdIndex(), null, false);
			mDBBBItemBody.insert(itemBody);
		}
		
		// 内容未取得の場合は WEB から取得
		if( !itemBody.getIsLoaded() ) {
			itemBody = mBBHandler.getBBItemBody(itemHead);
			mDBBBItemBody.update(itemBody);
		}
		
		return itemBody;
	}
	
	/**
	 * 記事の内容の再取得
	 */
	public BBItemBody reloadBody(BBItemHead itemHead) {
		BBItemBody itemBody = mDBBBItemBody.findById(itemHead.getIdDate(), itemHead.getIdIndex());

		// DB 未挿入の場合は新規インスタンスを作成
		if( itemBody == null ) {
			itemBody = new BBItemBody(itemHead.getIdDate(), itemHead.getIdIndex(), null, false);
			mDBBBItemBody.insert(itemBody);
		}

		// 内容未取得の場合は WEB から取得
		itemBody = mBBHandler.getBBItemBody(itemHead);
		mDBBBItemBody.update(itemBody);
		
		return itemBody;
	}
	
	/**
	 * 記事の内容が取得済みか確認
	 * @param item
	 */
	public boolean isBodyLoaded(BBItemHead itemHead) {
		BBItemBody itemBody = mDBBBItemBody.findById(itemHead.getIdDate(), itemHead.getIdIndex());
		return ((itemBody != null) ? itemBody.getIsLoaded() : false);
	}
	
	
}
