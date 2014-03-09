package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Circle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int rid;
	public String name = "";
	public List<String> phones = new ArrayList<String>();

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Circle c = (Circle) o;
				if (rid == c.rid && name.equals(c.name)
						&& phones.containsAll(c.phones)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}
}
