package com.example.meikobb.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meikobb.R;

public class BBFragment extends Fragment {
	/*
	 * TODO フラグメント作成
	 */
	
	/* メンバ */
	private static boolean bFlgReload;
	
	
	
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
		Log.i("BBFragment", "*****************************onCreate");
		
		// 回転時に初期化しないようにする
		setRetainInstance(true);
		
	}
	
	
	/**
	 * フラグメントのビュー生成時に呼ばれる
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bb, container, false);
		Log.i("BBFragment", "*****************************onCreateView");
		return view;
	}
	
	
	/**
	 * フラグメントの再生時に呼ばれる
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		// フラグメントの再描画が必要な場合、再描画する
		if( bFlgReload ) {
			bFlgReload = false;
			getFragmentManager().beginTransaction().replace(this.getId(), newInstance()).commit();
			this.onDestroy();
		}
	}
	
	
	
	
	
	/* 勝手に作ったメソッド */
	
	/**
	 * フラグメントを再描画するかどうかの設定
	 * @param reload 再描画する場合 true, しない場合 false
	 */
	public static void setReloadFragment(boolean reload) {
		bFlgReload = reload;
	}
	
}
