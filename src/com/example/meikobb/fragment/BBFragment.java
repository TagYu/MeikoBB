package com.example.meikobb.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	
	
	/**
	 * フラグメントのビュー生成時に呼ばれる
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_bb, container, false);
		
		/* 試作 */
		(new AsyncTask<Void, Void, List<BBItemHead>>() {

			/* バックグラウンドで処理 */
			@Override
			protected List<BBItemHead> doInBackground(Void... params) {
				
				// リスト取得
				int num = BBManager.reloadHeads();
				if( num < 0 ) {
					return null;
				}
				
				return BBManager.getHeads("10");
			}
			
			/* 処理完了時のリスナー */
			@Override
			protected void onPostExecute(final List<BBItemHead> list) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 描画
						ListView listView = (ListView) view.findViewById(R.id.fragment_bb_list);
						
						ArrayAdapter<String> adapter = null;
						
						if( list == null ) {
							adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_test1, new String[]{"取得エラー"});
						} else {
							List<String> stringList = new ArrayList<String>();
							for(Object obj : list.toArray()) {
								BBItemHead item = (BBItemHead) obj;
								stringList.add(item.getTitle());
							}
							adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_test1, stringList);
						}
						
						listView.setAdapter(adapter);
					}
				});
			}
			
		}).execute();
		
		return view;
	}
	
	
	/**
	 * フラグメントの再生時に呼ばれる
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		// フラグメントの再描画が必要な場合、再描画する
		if( sRequireReload ) {
			sRequireReload = false;
			getFragmentManager().beginTransaction().replace(this.getId(), newInstance()).commit();
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
	
}
