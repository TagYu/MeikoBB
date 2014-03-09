package com.example.meikobb.manager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.util.Log;

import com.example.meikobb.model.BBItemBody;
import com.example.meikobb.model.BBItemHead;

public class BBHandler {
	private boolean mIsReady = false;
	private CookieManager mCookieManager;
	private LinkedHashMap<String, String> mAuthParams;
	private String mAuthData;
	
	private HttpHandler_Head mHttpHandler_Head;
	private HttpHandler_Body mHttpHandler_Body;
	
	private long mLastLoginTestTime = 0;
	
	public void initialize(String authID, String authPW) {
		mCookieManager = new CookieManager();
		mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		mAuthParams = new LinkedHashMap<String, String>();
		mAuthParams.put("IDToken0", "");
		mAuthParams.put("IDToken1", authID); // ID
		mAuthParams.put("IDToken2", authPW); // PASSWORD
		mAuthParams.put("IDButton", "ログイン");
		mAuthParams.put("goto", "aHR0cHM6Ly9ycHhrZWlqaWJhbi5pY3Qubml0ZWNoLmFjLmpwOjQ0My9rZWlqaWJhbi9hcHA/dXJpPWxvZ2luVGVzdCZkdW1teT1hYWFh");
		mAuthParams.put("SunQueryParamsString", "cmVhbG09bml0ZWNoJg==");
		mAuthParams.put("encoded", "true");
		mAuthParams.put("gx_charset", "UTF-8");
		
		mAuthData = generateHttpData(mAuthParams);

		mHttpHandler_Head = new HttpHandler_Head();
		mHttpHandler_Body = new HttpHandler_Body();
		
		mIsReady = true;
	}
	
	public List<BBItemHead> getAllBBItems() {
		int response;
		URL url;
		HttpsURLConnection conn = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		Parser parser = new Parser();
		
		if( !mIsReady ) return null;
		if( !login() ) {
			return null;
		}

		CookieHandler.setDefault(mCookieManager);
		parser.setContentHandler(mHttpHandler_Head);
		
		try {
			/*
			 * URL: https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&next_uri&event_code=reload&no_read=false&on_flag=false&reference_flag=false&order=info.bulletin_start_date&order_kind=desc
			 *  - params:
			 *    - uri=keijiban : （必須）
			 *    - next_uri= :
			 *    - event_code=reload : (空でも問題なかった）
			 *    - no_read : 既読非表示(true), それ以外(false)
			 *    - on_flag : 強調のみ表示(true), それ以外(false)
			 *    - reference_flag : 参考非表示(true), それ以外(false)
			 *    - order=info.bulletin_start_date : ORDER BY 句
			 *    - order_kind=desc : 降順、昇順
			 */
			url = new URL("https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&no_read=false&on_flag=false&reference_flag=false&order=info.bulletin_start_date&order_kind=desc");
			conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "getAllBBItems(): GET https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&no_read=false&on_flag=false&reference_flag=false&order=info.bulletin_start_date&order_kind=desc Response: " + response);
			
			parser.parse(new InputSource(new InputStreamReader(conn.getInputStream(), "Shift_JIS")));
		} catch(IOException e) {
			Log.e("BBHandler", "IO Exception");
			e.printStackTrace();
			return null;
		} catch(SAXException e) {
			Log.e("BBHandler", "SAXException");
			e.printStackTrace();
			return null;
		} catch(Exception e) {
			Log.e("BBHandler", "Exception");
			e.printStackTrace();
			return null;
		} finally {
			if( conn != null ) { conn.disconnect(); }
			if( printWriter != null ) { printWriter.close(); }
			if( bufferedReader != null ) {
				try {
					bufferedReader.close();
				} catch (IOException e) { /* an io error occurred while closing BufferedReader */ }
			}
		}
		
		
		List<BBItemHead> items = mHttpHandler_Head.getBBItems();
		Log.i("BBHandler", items.size() + " items found");

		return items;
	}
	
	
	public BBItemBody getBBItemBody(BBItemHead itemHead) {
		int response;
		URL url;
		HttpsURLConnection conn = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		Parser parser = new Parser();

		String id_date = itemHead.getIdDate();
		String id_index = itemHead.getIdIndex();
		
		// 返すインスタンス
		BBItemBody itemBody = new BBItemBody(id_date, id_index, null, false);
		
		if( id_date.isEmpty() || id_index.isEmpty() ) {
			itemBody.setBody("取得に失敗しました");
			return itemBody;
		}
		if( !login() ) {
			itemBody.setBody("ログインできませんでした");
			return itemBody;
		}
		
		CookieHandler.setDefault(mCookieManager);
		parser.setContentHandler(mHttpHandler_Body);
		
		try {
			url = new URL("https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&next_uri=detail&id_date="+id_date+"&id_index="+id_index);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "getBBItemContent(): GET https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&next_uri=detail&id_date="+id_date+"&id_index="+id_index+"; Response: " + response);
			if( response != 200 ) { throw new Exception(); }

			parser.parse(new InputSource(new InputStreamReader(conn.getInputStream(), "Shift_JIS")));
		} catch(IOException e) {
			Log.e("BBHandler", "IO Exception");
			e.printStackTrace();
			return itemBody;
		} catch(SAXException e) {
			Log.e("BBHandler", "SAXException");
			e.printStackTrace();
			return itemBody;
		} catch(Exception e) {
			Log.e("BBHandler", "Exception");
			e.printStackTrace();
			return itemBody;
		} finally {
			if( conn != null ) { conn.disconnect(); }
			if( printWriter != null ) { printWriter.close(); }
			if( bufferedReader != null ) {
				try {
					bufferedReader.close();
				} catch (IOException e) { /* an io error occurred while closing BufferedReader */ }
			}
		}
		
		itemBody.setBody(mHttpHandler_Body.getBody());
		itemBody.setIsLoaded(true);
		
		return itemBody;
	}
	
	
	public boolean login() {
		int response;
		URL url;
		HttpsURLConnection conn = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		
		if( !mIsReady ) return false;
		
		Log.i("BBHandler", "login() : attempting to login");
		// 一定時間経過していなければログインしていると判断（無駄なリクエストを抑える）
		long lTmp = System.currentTimeMillis();
		if( lTmp - mLastLoginTestTime < 5000 ) {
			Log.i("BBHandler", "login(): prevented loginTest(); currentTimeMills() - mLastLoginTest = " + (lTmp - mLastLoginTestTime));
			mLastLoginTestTime = lTmp;
			return true;
		}
		Log.i("BBHandler", "login() : do login");

		CookieHandler.setDefault(mCookieManager);
		
		if( loginCheck() ) {
			mLastLoginTestTime = System.currentTimeMillis();
			return true;
		}
		
		// clear cookie
		mCookieManager.getCookieStore().removeAll();
		
		// login attempt
		Log.i("BBHandler", "Attempt to login;");
		try {
			url = new URL("https://slboam.ict.nitech.ac.jp/openam/UI/Login");
			conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(true);
			printWriter = new PrintWriter(conn.getOutputStream());
			printWriter.write(mAuthData);
			printWriter.close();
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "login(): POST https://slboam.ict.nitech.ac.jp/openam/UI/Login; Response: " + response);
			if( response != 200 ) { throw new Exception(); }
		} catch(IOException e) {
			Log.e("BBHandler", "IO Exception");
			e.printStackTrace();
			return false;
		} catch(SAXException e) {
			Log.e("BBHandler", "SAXException");
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			Log.e("BBHandler", "Exception");
			e.printStackTrace();
			return false;
		} finally {
			if( conn != null ) { conn.disconnect(); }
			if( printWriter != null ) { printWriter.close(); }
			if( bufferedReader != null ) {
				try {
					bufferedReader.close();
				} catch (IOException e) { /* an io error occurred while closing BufferedReader */ }
			}
		}
		
		
		// check login
		if( loginCheck() ) {
			mLastLoginTestTime = System.currentTimeMillis();
			return true;
		}
		
		return false;
	}
	
	
	public boolean loginCheck() {
		int response;
		URL url;
		HttpsURLConnection conn = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		
		if( !mIsReady ) return false;

		CookieHandler.setDefault(mCookieManager);
		
		Log.i("BBHandler", "loginCheck()");
		
		// test login
		try {
			url = new URL("https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=login_check");
			conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "loginCheck(): GET https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=login_check; Response: " + response);
			if( response == 200 ) return true;
			else if( response == 302 ) return false;
			else if( response == 304 ) return false;
			else { throw new Exception(); }

		} catch(IOException e) {
			Log.e("BBHandler", "IO Exception");
			e.printStackTrace();
			return false;
		} catch(SAXException e) {
			Log.e("BBHandler", "SAXException");
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			Log.e("BBHandler", "Exception");
			e.printStackTrace();
			return false;
		} finally {
			if( conn != null ) { conn.disconnect(); }
			if( printWriter != null ) { printWriter.close(); }
			if( bufferedReader != null ) {
				try {
					bufferedReader.close();
				} catch (IOException e) { /* an io error occurred while closing BufferedReader */ }
			}
		}
	}
	
	
	
	public static String generateHttpData(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		Set<Entry<String, String>> set = params.entrySet();
		Iterator<Entry<String, String>> it = set.iterator();
		Entry<String, String> e;
		while(it.hasNext()) {
			e = it.next();
			sb.append(e.getKey());
			sb.append("=");
			sb.append(e.getValue());
			if( it.hasNext() ) { sb.append("&"); }
		}
		return sb.toString();
	}
	
	
	
	
	
	
	private class HttpHandler_Head implements ContentHandler {
		
		private int mDepth = 0;
		private boolean mIsInTable = false;
		private int mTdCount = 0;
		
		private List<BBItemHead> mBBItemList = new ArrayList<BBItemHead>();
		private BBItemHead mTmpItem;
		
		private String mTmpStr;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if( mIsInTable ) {
				if( mTdCount == 3 ) {
					for(int i = 0; i < length; ++i) {
						mTmpStr += ch[start + i];
					}
				} else if ( mTdCount == 4 ) {
					for(int i = 0; i < length; ++i) {
						mTmpStr += ch[start + i];
					}
				} else if( mTdCount == 7 ) {
					for(int i = 0; i < length; ++i) {
						mTmpStr += ch[start + i];
					}
					
				} else if( mTdCount == 9 ) {
					for(int i = 0; i < length; ++i) {
						mTmpStr += ch[start + i];
					}
				}
			}
			
		}

		@Override
		public void endDocument() throws SAXException {
		}

		@Override
		public void endElement(String uri, String localName, String qName) 
				throws SAXException {
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = false;
			}
			
			if( mIsInTable ) {
				if( localName.equals("tr") ) {
					if( mTmpItem.getIdIndex() != null ) {
						mBBItemList.add(mTmpItem);
					}
				} else if( localName.equals("td") ) {
					if( mTdCount == 3 ) { mTmpItem.setDateShow(mTmpStr.trim()); }
					else if( mTdCount == 4 ) { mTmpItem.setDateExec(mTmpStr.trim()); }
					else if( mTdCount == 7 ) { mTmpItem.setTitle(mTmpStr.trim()); }
					else if( mTdCount == 9 ) { mTmpItem.setAuthor(mTmpStr.trim()); }
				}
			}
			
			--mDepth;
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) 
				throws SAXException {
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
		}

		@Override
		public void setDocumentLocator(Locator arg0) {
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
		}

		@Override
		public void startDocument() throws SAXException {
			mDepth = 0;
			mIsInTable = false;
			mTdCount = 0;
			mBBItemList.clear();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			++mDepth;
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = true;
			}
			
			if( mIsInTable ) {
				if( localName.equals("tr") ) {
					mTdCount = 0;
					mTmpItem = new BBItemHead();
				}
				if( localName.equals("td") ) {
					++mTdCount;
					mTmpStr = "";
				}
				
				if( mTdCount == 1 && localName.equals("input") ) {
					// retrieve id_date and id_index
					String valName = atts.getValue("name");
					if( valName != null && valName.equals("id_date") ) {
						mTmpItem.setIdDate(atts.getValue("value"));
					} else if( valName != null && valName.equals("id_index") ) {
						mTmpItem.setIdIndex(atts.getValue("value"));
					}
				} else if ( mTdCount == 3 ) {
					// retrieve date_show
					// --> characters() --> endElement()
				} else if ( mTdCount == 4 ) {
					// retrieve date_exec
					// --> characters() --> endElement()
				}
			}
		}

		@Override
		public void startPrefixMapping(String arg0, String arg1)
				throws SAXException {
		}
		
		public List<BBItemHead> getBBItems() {
			return mBBItemList;
		}
		
	}
	
	
	private class HttpHandler_Body implements ContentHandler {
		
		private int mDepth = 0;
		private boolean mIsInTable = false;
		private boolean mIsBody = false;
		private int mTrCount = 0;
		
		private String mTmpStr;
		private String mBody;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			
			if( mIsBody ) {
				for(int i = 0; i < length; ++i) {
					mTmpStr += ch[start + i];
				}
			}
			
		}

		@Override
		public void endDocument() throws SAXException {
		}

		@Override
		public void endElement(String uri, String localName, String qName) 
				throws SAXException {
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = false;
			}
			
			if( mIsInTable ) {
				if( mTrCount == 5 && localName.equals("td") ) {
					mIsBody = false;
					mBody = mTmpStr;
				}
			}
			
			--mDepth;
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) 
				throws SAXException {
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
		}

		@Override
		public void setDocumentLocator(Locator arg0) {
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
		}

		@Override
		public void startDocument() throws SAXException {
			mDepth = 0;
			mIsInTable = false;
			mIsBody = false;
			mTrCount = 0;
			mTmpStr = "";
			mBody = "";
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			++mDepth;
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = true;
				mTrCount = 0;
			}
			
			if( mIsInTable ) {
				if( localName.equals("tr") ) {
					++mTrCount;
				}
				
				if( mTrCount == 5 && localName.equals("td") ) {
					mIsBody = true;
				}
				
				if( mIsBody && localName.equals("br") ) {
					mTmpStr += "\n";
				}
			}
		}

		@Override
		public void startPrefixMapping(String arg0, String arg1)
				throws SAXException {
		}
		
		
		public String getBody() {
			return mBody;
		}
		
	}

}
