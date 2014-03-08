package com.example.meikobb.model;

public class BBItemBody implements Comparable {
	private String mIdDate;
	private String mIdIndex;
	private String mBody;
	private boolean mIsLoaded = false;
	
	public BBItemBody() { }
	public BBItemBody(String idDate, String idIndex, String body, int isLoaded) {
		this(idDate, idIndex, body, (isLoaded != 0));
	}
	public BBItemBody(String idDate, String idIndex, String body, boolean isLoaded) {
		this.mIdDate = idDate;
		this.mIdIndex = idIndex;
		this.mBody = body;
		this.mIsLoaded = isLoaded;
	}

	public void setIdDate(String idDate) { mIdDate = idDate; }
	public void setIdIndex(String idIndex) { mIdIndex = idIndex; }
	public void setBody(String body) { mBody = body; }
	public void setIsLoaded(boolean isLoaded) { mIsLoaded = isLoaded; }
	
	public String getIdDate() { return mIdDate; }
	public String getIdIndex() { return mIdIndex; }
	public String getBody() { return mBody; }
	public boolean getIsLoaded() { return mIsLoaded; }

	@Override
	public boolean equals(Object o) {
		BBItemBody item = (BBItemBody) o;
		return (item.getIdDate() == mIdDate && item.getIdIndex() == mIdIndex);
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		BBItemBody item = (BBItemBody) o;
		return mIdDate.compareTo(item.getIdDate()) * 10 + mIdIndex.compareTo(item.getIdIndex());
	}
	
}
