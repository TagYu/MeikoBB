package com.example.meikobb.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.meikobb.R;
import com.example.meikobb.manager.BBManager;
import com.example.meikobb.model.BBItemHead;

public class BBListFragment extends BBFragment {
	/*
	 * TODO フラグメント作成
	 */
	
	/* メンバ */
	boolean mIsFirstViewLoad;
	
	
	/* newInstance() */
	
	/**
	 * インスタンスの生成
	 * @return
	 */
	public static BBListFragment newInstance() {
		BBListFragment fragment = new BBListFragment();
		
		return fragment;
	}
	

	/* コンストラクタ */
	public BBListFragment() {
		super();
		
		// メンバ初期化
		mIsFirstViewLoad = true;
	}
	
	
	/* オーバーライドメソッド */
	
	
	/**
	 * フラグメントのビュー生成時に呼ばれる
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_bb_list, container, false);
		
		/* 試作 */
		final ListView listView = (ListView) view.findViewById(R.id.fragment_bb_list_listview);
		List<BBItemHead> list = BBManager.getHeads();
		BBListAdapter adapter = new BBListAdapter(getActivity(), 0, list);
		listView.setAdapter(adapter);
		
		
		/* アイテムが押された時のリスナーを登録 */
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View itemView, int position,
					long arg3) {
				BBItemHead itemHead = (BBItemHead) listView.getItemAtPosition(position);
				
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.activity_main_fragment,
								BBDetailFragment.newInstsance(itemHead))
						.addToBackStack(null).commit();
			}
		});
		
		
		// 2回目以降の表示では、これ以降の処理をしない。
		if( !mIsFirstViewLoad ) {
			return view;
		}
		
		
		/* 裏で更新処理 */
		(new AsyncTask<Void, Void, List<BBItemHead>>() {

			/* バックグラウンドで処理 */
			@Override
			protected List<BBItemHead> doInBackground(Void... params) {
				
				// リスト取得
				int num = BBManager.reloadHeads();
				if( num < 0 ) {
					return null;
				}
				
				return BBManager.getHeads(null);
			}
			
			/* 処理完了時のリスナー */
			@Override
			protected void onPostExecute(final List<BBItemHead> list) {
				Activity activity;
				if( (activity = getActivity()) != null ) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 描画
							ListView tmplistView = (ListView) view.findViewById(R.id.fragment_bb_list_listview);
							
							BBListAdapter adapter;
							
							if( list == null ) {
								adapter = new BBListAdapter(getActivity(), 0, new BBItemHead[]{new BBItemHead("", "", "", "", "取得エラー", "", false, false)});
							} else {
								adapter = new BBListAdapter(getActivity(), 0, list);
							}
							
							tmplistView.setAdapter(adapter);
						}
					});
				}
			}
			
		}).execute();
		/* 試作　終わり */
		
		mIsFirstViewLoad = false;
		
		return view;
	}
	
	
	/**
	 * 状態を保存（メモリ上からの破棄時に呼ばれる）
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/*
		 * このメソッドは onCreate() 内で setRetainInstsance(true) としている場合は呼ばれない
		 * (BBFragment (継承クラス) に記述あり)
		 */
	}
	
	
	
	
	
	/* 勝手に作ったメソッド */
	
	
	/**
	 * フラグメント再描画を行う際に呼ぶ
	 */
	@Override
	protected void onReload() {
		getFragmentManager().beginTransaction().replace(this.getId(), newInstance()).commit();
	}
	
	
	
	
	
	
	
	
	
	
	
	/* リストのアダプタ */
	private class BBListAdapter extends ArrayAdapter<BBItemHead> {
		private LayoutInflater mLayoutInflater;
		
		public BBListAdapter(Context context, int resource,
				List<BBItemHead> objects) {
			super(context, resource, objects);

			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public BBListAdapter(Context context, int resource, BBItemHead[] objects){
			super(context, resource, objects);

			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			BBItemHead itemHead = (BBItemHead) getItem(position);
			
			// convertViewは使い回しされている可能性があるので、nullのときだけ新しく作る
			if( view == null ) {
				view = mLayoutInflater.inflate(R.layout.list_item_test1, null);
			}
			
			// リストアイテムの内容を設定
			if( itemHead != null ) {
				TextView textView = (TextView) view.findViewById(R.id.list_item_text1_textview_title);
				
				textView.setText(itemHead.getTitle());
			}
			
			// アニメーションの設定
			
			

			/*
			 * TODO
			 *  - リストアイテムの内容の設定
			 *  - アニメーションの設定
			 */
			return view;
		}
		
	}
}
