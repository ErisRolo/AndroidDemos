package com.kgc.visitshop.application;


import com.kgc.visitshop.utils.LogUtils;

import org.litepal.LitePalApplication;


public class App extends LitePalApplication {
	
	private App app;
	
	public App initApplication(){
		app = new App();
		return app;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.setISLogCat(true);
	}

}
