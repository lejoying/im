package com.open.welinks.controller;

import com.open.welinks.model.Data;
import com.open.welinks.view.SquareSubView;

public class SquareSubController {

	public Data data = Data.getInstance();
	public String tag = "SquareSubController";
	public SquareSubView thisView;

	public MainController mainController;

	public SquareSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

	}

	public void bindEvent() {

	}

}
