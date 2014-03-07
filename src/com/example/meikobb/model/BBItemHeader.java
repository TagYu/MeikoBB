package com.example.meikobb.model;

public class BBItemHeader implements Comparable {
	private String mDate;
	private String mIndex;
	private String mTitle;
	private String mAuthor;
	private boolean mIsRead;
	private boolean mIsNew;
	
	public BBItemHeader() { }
	public BBItemHeader(String date, String index, String title, String author, int isRead, int isNew) {
		this.mDate = date;
		this.mIndex = index;
		this.mTitle = title;
		this.mAuthor = author;
		this.mIsRead = isRead != 0;
		this.mIsNew = isNew != 0;
	}

	public void setDate(String date) { mDate = date; }
	public void setIdDate(String date) { mDate = date; }
	public void setIndex(String index) { mIndex = index; }
	public void setIdIndex(String index) { mIndex = index; }
	public void setTitle(String title) { mTitle = title; }
	public void setAuthor(String author) { mAuthor = author; }
	public void setIsRead(boolean isRead) { mIsRead = isRead; }
	public void setIsNew(boolean isNew) { mIsRead = isNew; }
	
	public String getDate() { return mDate; }
	public String getIdDate() { return mDate; }
	public String getIndex() { return mIndex; }
	public String getIdIndex() { return mIndex; }
	public String getTitle() { return mTitle; }
	public String getAuthor() { return mAuthor; }
	public boolean getIsRead() { return mIsRead; }
	public boolean getIsNew() { return mIsNew; }
	

	@Override
	public boolean equals(Object o) {
		BBItemHeader item = (BBItemHeader) o;
		return (item.getTitle() == mTitle);
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		BBItemHeader item = (BBItemHeader) o;
		return mTitle.compareTo(item.getTitle());
	}
	
}
