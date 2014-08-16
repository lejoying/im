package com.open.welinks.entity;

public class Part {
	public int partNumber;
	public String eTag;
	public boolean status = false;

	public Part() {
		this.partNumber = 0;
		this.eTag = "";
		this.status = false;
	}

	public Part(int partNumber, String eTag) {
		this.partNumber = partNumber;
		this.eTag = eTag;
	}

	public Part(int partNumber, String eTag, boolean status) {
		this.partNumber = partNumber;
		this.eTag = eTag;
		this.status = status;
	}

	public int getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
