package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int gid;
	public String name = "";
	public List<String> members = new ArrayList<String>();

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Group g = (Group) o;
				if (gid == g.gid && name.equals(g.name)
						&& members.containsAll(g.members)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}
}
