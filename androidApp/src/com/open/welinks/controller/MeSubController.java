package com.open.welinks.controller;

import com.open.welinks.model.Data;
import com.open.welinks.view.MeSubView;

public class MeSubController {

	public Data data = Data.getInstance();
	public String tag = "MeSubController";
	public MeSubView thisView;

	public MainController mainController;

	public MeSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

	}

	public void bindEvent() {

	}

}
