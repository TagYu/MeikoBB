package com.example.meikobb.model;

public class BBItemHead implements Comparable {
	private String mIdDate;
	private String mIdIndex;
	private String mDateShow;
	private String mDateExec;
	private String mTitle;
	private String mAuthor;
	private boolean mIsRead = false;
	private boolean mIsNew = true;
	
	public BBItemHead() { }
	public BBItemHead(String idDate, String idIndex, String dateShow, String dateExec, String title, String author, int isRead, int isNew) {
		this(idDate, idIndex, dateShow, dateExec, title, author, (isRead != 0), (isNew != 0));
	}
	public BBItemHead(String idDate, String idIndex, String dateShow, String dateExec, String title, String author, boolean isRead, boolean isNew) {
		this.mIdDate = idDate;
		this.mIdIndex = idIndex;
		this.mDateShow = dateShow;
		this.mDateExec = dateExec;
		this.mTitle = title;
		this.mAuthor = author;
		this.mIsRead = isRead;
		this.mIsNew = isNew;
	}

	public void setIdDate(String idDate) { mIdDate = idDate; }
	public void setIdIndex(String idIndex) { mIdIndex = idIndex; }
	public void setDateShow(String dateShow) { mDateShow = dateShow; }
	public void setDateExec(String dateExec) { mDateExec = dateExec; }
	public void setTitle(String title) { mTitle = title; }
	public void setAuthor(String author) { mAuthor = author; }
	public void setIsRead(boolean isRead) { mIsRead = isRead; }
	public void setIsNew(boolean isNew) { mIsRead = isNew; }
	
	public String getIdDate() { return mIdDate; }
	public String getIdIndex() { return mIdIndex; }
	public String getDateShow() { return mDateShow; }
	public String getDateExec() { return mDateExec; }
	public String getTitle() { return mTitle; }
	public String getAuthor() { return mAuthor; }
	public boolean getIsRead() { return mIsRead; }
	public boolean getIsNew() { return mIsNew; }
	

	@Override
	public boolean equals(Object o) {
		BBItemHead item = (BBItemHead) o;
		return (item.getIdDate() == mIdDate && item.getIdIndex() == mIdIndex);
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		BBItemHead item = (BBItemHead) o;
		return mIdDate.compareTo(item.getIdDate()) * 10 + mIdIndex.compareTo(item.getIdIndex());
	}
	
}
