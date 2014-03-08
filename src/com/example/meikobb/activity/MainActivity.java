package com.example.meikobb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.meikobb.R;
import com.example.meikobb.application.MainApplication;
import com.example.meikobb.manager.BBManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.action_settings: {
			startActivity(new Intent(this, SettingActivity.class));
		}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * public static initialize()
	 * MainActivity, Manager の初期化
	 */
	public static void initialize() {
		// 初期化
		initBBManager();
	}
	
	
	/**
	 * BBManager の初期化
	 */
	public static void initBBManager() {
		BBManager.initialize(MainApplication.getInstance());
	}

	
	/*
	 * TODO Activity をつくる
	 */
}
