package com.example.meikobb.manager;

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

import com.example.meikobb.model.BBItemHeader;

public class BBHandler {
	private boolean mIsReady = false;
	private CookieManager mCookieManager;
	private LinkedHashMap<String, String> mAuthParams;
	private String mAuthData;
	
	private Pattern mPattern1;
	private HttpHandler_List mHttpHandler_List;
	private HttpHandler_Content mHttpHandler_Content;
	
	private long mLastLoginTestTime = 0;
	
	public void initialize(String authID, String authPW) {
		mCookieManager = new CookieManager();
		mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		mAuthParams = new LinkedHashMap<String, String>();
		mAuthParams.put("IDToken0", "");
		mAuthParams.put("IDToken1", authID); // ID
		mAuthParams.put("IDToken2", authPW); // PASSWORD
		mAuthParams.put("IDButton", "ログイン");
		mAuthParams.put("goto", "aHR0cHM6Ly9ycHhrZWlqaWJhbi5pY3Qubml0ZWNoLmFjLmpwOjQ0My9rZWlqaWJhbi9hcHA/dXJpPWxvZ2luVGVzdCZkdW1teT1hYWFh"); // 要取得？
		mAuthParams.put("SunQueryParamsString", "cmVhbG09bml0ZWNoJg=="); // 要取得？
		mAuthParams.put("encoded", "true");
		mAuthParams.put("gx_charset", "UTF-8");
		
		mAuthData = generateHttpData(mAuthParams);

		mHttpHandler_List = new HttpHandler_List();
		mHttpHandler_Content = new HttpHandler_Content();
		
		mIsReady = true;
	}
	
	public List<BBItemHeader> getAllBBItems() {
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
		parser.setContentHandler(mHttpHandler_List);
		
		try {
			url = new URL("https://slboam.ict.nitech.ac.jp/openam/UI/Login");
			conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			printWriter = new PrintWriter(conn.getOutputStream());
			printWriter.write(mAuthData);
			printWriter.close();
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "getAllBBItems(): GET https://slboam.ict.nitech.ac.jp/openam/UI/Login; Response: " + response);
			
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
		
		
		List<BBItemHeader> items = mHttpHandler_List.getBBItems();

		return items;
	}
	
	
	/* のちに復活 */
//	public void getBBItemContent(BBItemHeader itemData) {
//		int response;
//		URL url;
//		HttpsURLConnection conn = null;
//		PrintWriter printWriter = null;
//		BufferedReader bufferedReader = null;
//		Parser parser = new Parser();
//
//		String id_date = itemData.getIdDate();
//		String id_index = itemData.getIdIndex();
//		
//		if( id_date.isEmpty() || id_index.isEmpty() ) {
//			itemData.setContent("取得に失敗しました");
//			return;
//		}
//		if( !login() ) {
//			itemData.setContent("ログインできませんでした");
//			return;
//		}
//		
//		CookieHandler.setDefault(mCookieManager);
//		parser.setContentHandler(mHttpHandler_Content);
//		
//		try {
//			url = new URL("https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&next_uri=detail&id_date="+id_date+"&id_index="+id_index);
//			conn = (HttpsURLConnection) url.openConnection();
//			conn.setReadTimeout(10000);
//			conn.setConnectTimeout(15000);
//			conn.setRequestMethod("GET");
//			
//			response = conn.getResponseCode();
//			Log.i("BBHandler", "getBBItemContent(): GET https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=keijiban&next_uri=detail&id_date="+id_date+"&id_index="+id_index+"; Response: " + response);
//			if( response != 200 ) { throw new Exception(); }
//
//			parser.parse(new InputSource(new InputStreamReader(conn.getInputStream(), "Shift_JIS")));
//		} catch(IOException e) {
//			Log.e("BBHandler", "IO Exception");
//			e.printStackTrace();
//			return;
//		} catch(SAXException e) {
//			Log.e("BBHandler", "SAXException");
//			e.printStackTrace();
//			return;
//		} catch(Exception e) {
//			Log.e("BBHandler", "Exception");
//			e.printStackTrace();
//			return;
//		} finally {
//			if( conn != null ) { conn.disconnect(); }
//			if( printWriter != null ) { printWriter.close(); }
//			if( bufferedReader != null ) {
//				try {
//					bufferedReader.close();
//				} catch (IOException e) { /* an io error occurred while closing BufferedReader */ }
//			}
//		}
//		
//		itemData.setContent(mHttpHandler_Content.getTheContent());
//	}
	
	
	public boolean login() {
		int response;
		URL url;
		HttpsURLConnection conn = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		
		if( !mIsReady ) return false;
		// 一定時間経過していなければログインしていると判断（無駄なリクエストを抑える）
		long lTmp = System.currentTimeMillis();
		if( lTmp - mLastLoginTestTime < 5000 ) {
			Log.i("BBHandler", "login(): prevented loginTest(); currentTimeMills() - mLastLoginTest = " + (lTmp - mLastLoginTestTime));
			mLastLoginTestTime = lTmp;
			return true;
		}

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
			printWriter = new PrintWriter(conn.getOutputStream());
			printWriter.write(mAuthData);
			printWriter.close();
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "login(): GET https://slboam.ict.nitech.ac.jp/openam/UI/Login; Response: " + response);
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
		Parser parser = new Parser();
		
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
			
			response = conn.getResponseCode();
			Log.i("BBHandler", "loginCheck(): GET https://rpxkeijiban.ict.nitech.ac.jp/keijiban/app?uri=login_check; Response: " + response);
			if( response == 200 ) return true;
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
	
	
	
	
	
	
	private class HttpHandler_List implements ContentHandler {
		
		private int mDepth = 0;
		private boolean mIsInTable = false;
		private int mTdCount = 0;
		
		private List<BBItemHeader> mBBItemList = new ArrayList<BBItemHeader>();
		private BBItemHeader mTmpItem;
		
		private String mTmpStr;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			
			if( mIsInTable ) {
				if( mTdCount == 7 ) {
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endElement(String uri, String localName, String qName) 
				throws SAXException {
			// TODO Auto-generated method stub
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = false;
			}
			
			if( mIsInTable ) {
				if( localName.equals("tr") ) {
					if( mTmpItem.getIndex() != null ) {
						mBBItemList.add(mTmpItem);
					}
				} else if( localName.equals("td") ) {
					if( mTdCount == 7 ) { mTmpItem.setTitle(mTmpStr.trim()); }
					else if( mTdCount == 9 ) { mTmpItem.setAuthor(mTmpStr.trim()); }
				}
			}
			
			--mDepth;
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) 
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDocumentLocator(Locator arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			mDepth = 0;
			mIsInTable = false;
			mTdCount = 0;
			mBBItemList.clear();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			// TODO Auto-generated method stub
			++mDepth;
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = true;
			}
			
			if( mIsInTable ) {
				if( localName.equals("tr") ) {
					mTdCount = 0;
					mTmpItem = new BBItemHeader();
				}
				if( localName.equals("td") ) {
					++mTdCount;
					mTmpStr = "";
				}
				
				if( mTdCount == 1 && localName.equals("input") ) {
					// retrieve id_date and id_index
					String valName = atts.getValue("name");
					if( valName != null && valName.equals("id_date") ) {
						mTmpItem.setDate(atts.getValue("value"));
					} else if( valName != null && valName.equals("id_index") ) {
						mTmpItem.setIndex(atts.getValue("value"));
					}
				}
			}
		}

		@Override
		public void startPrefixMapping(String arg0, String arg1)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}
		
		public List<BBItemHeader> getBBItems() {
			return mBBItemList;
		}
		
	}
	
	
	private class HttpHandler_Content implements ContentHandler {
		
		private int mDepth = 0;
		private boolean mIsInTable = false;
		private boolean mIsTheContent = false;
		private int mTrCount = 0;
		
		private String mTmpStr;
		private String mTheContent;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			
			if( mIsTheContent ) {
				for(int i = 0; i < length; ++i) {
					mTmpStr += ch[start + i];
				}
			}
			
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endElement(String uri, String localName, String qName) 
				throws SAXException {
			// TODO Auto-generated method stub
			
			if( mDepth == 4 && localName.equals("table") ) {
				mIsInTable = false;
			}
			
			if( mIsInTable ) {
				if( mTrCount == 5 && localName.equals("td") ) {
					mIsTheContent = false;
					mTheContent = mTmpStr;
				}
			}
			
			--mDepth;
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) 
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDocumentLocator(Locator arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			mDepth = 0;
			mIsInTable = false;
			mIsTheContent = false;
			mTrCount = 0;
			mTmpStr = "";
			mTheContent = "";
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			// TODO Auto-generated method stub
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
					mIsTheContent = true;
				}
				
				if( mIsTheContent && localName.equals("br") ) {
					mTmpStr += "\n";
				}
			}
		}

		@Override
		public void startPrefixMapping(String arg0, String arg1)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}
		
		
		public String getTheContent() {
			return mTheContent;
		}
		
	}

}
