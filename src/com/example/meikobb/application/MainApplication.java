package com.example.meikobb.application;

import android.app.Application;

public class MainApplication extends Application {
	static MainApplication instance;
	
	public MainApplication() {
		instance = this;
	}
	
	public static MainApplication getInstance() {
		return instance;
	}
}
