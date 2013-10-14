package cn.buaa.myweixin.utils;

import java.io.Serializable;
import java.util.List;

public class Community implements Serializable {
	private String cid;
	private String name;
	private List<Serves> serves;
	public Community() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Community(String cid, String name, List<Serves> serves) {
		super();
		this.cid = cid;
		this.name = name;
		this.serves = serves;
	}
	@Override
	public String toString() {
		return "Community [cid=" + cid + ", name=" + name + ", serves="
				+ serves + "]";
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Serves> getServes() {
		return serves;
	}
	public void setServes(List<Serves> serves) {
		this.serves = serves;
	}
	
}
