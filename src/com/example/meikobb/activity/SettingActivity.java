package com.example.meikobb.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import com.example.meikobb.R;
import com.example.meikobb.fragment.BBFragment;
import com.example.meikobb.manager.BBManager;

public class SettingActivity extends Activity {
	
	/* 定数 */
	public static final String PREF_KEY_AUTH_ID = "auth_id";
	public static final String PREF_KEY_AUTH_PW = "auth_pw";
	

	/* オーバーライドメソッド */
	
	/**
	 * アクティビティ生成時に呼ばれる
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
		
		// アクションバー　アイコンの「戻る」機能追加
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
			getActionBar().setHomeButtonEnabled(true);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * オプションメニューの項目が押されたときに呼ばれる
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home: {
			// 左上のアイコンが押された --> SettingActivity 終了
			finish();
			return true;
		}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 別のアクティビティへ遷移したときに呼ばれる
	 */
	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	
	/*
	 * SettingFragment ： SettingActivity 内のフラグメント
	 */
	public static class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		/* メンバ */
		private boolean mIsAuthValueChanged;
		
		
		/* オーバーライドメソッド */
		
		/**
		 * (non-Javadoc)
		 *  フラグメント生成時に呼ばれる。
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			
			// 保存されている設定値を項目名の下に表示
			EditTextPreference authID = (EditTextPreference) findPreference(PREF_KEY_AUTH_ID);
			authID.setSummary( (authID.getText() == null) ? "-" : authID.getText() );
			EditTextPreference authPW = (EditTextPreference) findPreference(PREF_KEY_AUTH_PW);
			if( authPW != null ) {
				authPW.setSummary( (authPW.getText() == null) ? "-" : "(hidden)" );
			}
			
			// メンバの初期化
			mIsAuthValueChanged = false;
		}
		
		
		/**
		 * 設定値が変更されたときに呼ばれる
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			
			if( key.equals(PREF_KEY_AUTH_ID) ) {
				EditTextPreference authID = (EditTextPreference) findPreference(key);
				authID.setSummary(authID.getText());
				mIsAuthValueChanged = true;
			} else if( key.equals(PREF_KEY_AUTH_PW) ) {
				EditTextPreference authPW = (EditTextPreference) findPreference(key);
				authPW.setSummary( (authPW.getText() == null || authPW.getText().isEmpty()) ? "-" : "(hidden)" );
				mIsAuthValueChanged = true;
			}
			
		}
		
		
		/**
		 * このフラグメントに遷移した時に呼ばれる
		 */
		@Override
		public void onResume() {
			super.onResume();
			
			// 設定値変更時のリスナーを設定
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}
		
		/**
		 * 別のフラグメントへ遷移したときに呼ばれる
		 */
		@Override
		public void onPause() {
			super.onPause();
			
			// 設定値変更時のリスナーを解除
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

			// 認証情報に変更があれば、BBManager を初期化, BBFragment をリロード
			if( mIsAuthValueChanged ) {
				mIsAuthValueChanged = false;
				
				// BBManager 初期化
				MainActivity.initBBManager();
				
				// BBFragment リロード
				BBFragment.setRequireReloadFragment(true);
				
				// 保存されたキャッシュをすべて削除
				BBManager.deleteAllCaches();
			}
		}

	}
	
}
