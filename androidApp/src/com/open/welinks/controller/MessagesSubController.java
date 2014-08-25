package com.open.welinks.controller;

import com.open.welinks.model.Data;
import com.open.welinks.view.MessagesSubView;

public class MessagesSubController {

	public Data data = Data.getInstance();
	public String tag = "MessagesSubController";
	public MessagesSubView thisView;

	public MainController mainController;

	public MessagesSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

	}

	public void bindEvent() {
	}

}
