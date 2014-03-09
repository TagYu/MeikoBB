package com.example.meikobb.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meikobb.R;
import com.example.meikobb.manager.BBManager;
import com.example.meikobb.model.BBItemBody;
import com.example.meikobb.model.BBItemHead;

public class BBDetailFragment extends BBFragment {
	/*
	 * TODO フラグメント作成
	 */
	
	
	
	/* コンストラクタ */
	
	/**
	 * インスタンスの生成
	 * @return
	 */
	public static BBDetailFragment newInstsance(BBItemHead itemHead) {
		BBDetailFragment fragment = new BBDetailFragment();
		
		// 引数の処理
		Bundle args = new Bundle();
		args.putString("id_date", itemHead.getIdDate());
		args.putString("id_index", itemHead.getIdIndex());
		fragment.setArguments(args);
		
		return fragment;
	}
	
	
	
	
	/* オーバーライドメソッド */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_bb_detail, container, false);
		
		// 引数受け取り
		Bundle args = getArguments();
		String idDate = args.getString("id_date");
		String idIndex = args.getString("id_index");
		
		BBItemHead itemHead = BBManager.getHead(idDate, idIndex);
		if( itemHead != null ) {
			// タイトルの設定
			TextView tvTitle = (TextView) view.findViewById(R.id.fragment_bb_detail_textview_title);
			tvTitle.setText(itemHead.getTitle());
			
			// 本文の設定
			if( BBManager.isBodyLoaded(itemHead) ) {
				// 取得済みの場合、そのまま表示
				BBItemBody itemBody = BBManager.getBody(itemHead);
				TextView tvBody = (TextView) view.findViewById(R.id.fragment_bb_detail_textview_body);
				tvBody.setText(itemBody.getBody());
			} else {
				// 未取得の場合、裏で読み込んで表示
				(new AsyncTask<BBItemHead, Void, BBItemBody>() {

					@Override
					protected BBItemBody doInBackground(BBItemHead... params) {
						BBItemHead itemHead = params[0];
						
						if( itemHead != null ) {
							return BBManager.getBody(itemHead);
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(final BBItemBody itemBody) {
						if( itemBody != null ) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									TextView tvBody = (TextView) view.findViewById(R.id.fragment_bb_detail_textview_body);
									tvBody.setText(itemBody.getBody());
								}
							});
						}
					}
					
				}).execute(itemHead);
			}
		}
		
		return view;
	}
	
	
	
	
	/* 勝手に作ったメソッド */
	
	
	/**
	 * フラグメント再描画を行う際に呼ぶ
	 */
	@Override
	protected void onReload() {
		getFragmentManager().beginTransaction().replace(this.getId(), newInstance()).commit();
	}
}
