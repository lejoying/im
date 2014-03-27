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
			if (o instanceof Circle) {
				Circle c = (Circle) o;
				if (rid == c.rid && name.equals(c.name)
						&& phones.containsAll(c.phones)) {
					flag = true;
				}
			} else if (o instanceof String) {
				String i = (String) o;
				if (rid == Integer.valueOf(i).intValue()) {
					flag = true;
				}
			}
		}
		return flag;
	}
}
