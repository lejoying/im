package com.open.welinks.controller;

import com.open.welinks.model.Data;
import com.open.welinks.view.FriendsSubView;

public class FriendsSubController {

	public Data data = Data.getInstance();
	public String tag = "FriendsSubController";

	public FriendsSubView thisView;
	public FriendsSubController thisController;

	public MainController mainController;

	public FriendsSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

	}

	public void bindEvent() {

	}

}
