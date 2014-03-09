package com.example.meikobb.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.meikobb.R;
import com.example.meikobb.manager.BBManager;
import com.example.meikobb.model.BBItemHead;

public class BBFragment extends Fragment {
	/*
	 * TODO フラグメント作成
	 */
	
	/* メンバ */
	private static boolean sRequireReload;
	
	
	
	/* コンストラクタ */
	
	/**
	 * インスタンスの生成
	 * @return
	 */
	public static BBFragment newInstance() {
		BBFragment fragment = new BBFragment();
		
		return fragment;
	}
	
	
	
	
	/* オーバーライドメソッド */
	
	/**
	 * フラグメント生成時に呼ばれる
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 回転時に初期化しないようにする
		setRetainInstance(true);
		
	}
	
	
//	※ オーバーライドして実装
//	
//	/**
//	 * フラグメントのビュー生成時に呼ばれる
//	 */
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		final View view = inflater.inflate(R.layout.fragment_bb, container, false);
//		
//		// ビューの作成
//		
//		return view;
//	}
	
	
	/**
	 * フラグメントの再生時に呼ばれる
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		// フラグメントの再描画が必要な場合、再描画する
		if( sRequireReload ) {
			sRequireReload = false;
			this.onReload();
			this.onDestroy();
		}
	}
	
	
	
	
	
	/* 勝手に作ったメソッド */
	
	/**
	 * フラグメントを再描画するかどうかの設定
	 * @param reload 再描画する場合 true, しない場合 false
	 */
	public static void setRequireReloadFragment(boolean reload) {
		sRequireReload = reload;
	}
	
	
	/**
	 * フラグメント再描画を行う際に呼ぶ
	 * ※ サブクラスはこのメソッドをオーバーライドする必要がある
	 */
	protected void onReload() {
		getFragmentManager().beginTransaction().replace(this.getId(), newInstance()).commit();
	}
	
}
