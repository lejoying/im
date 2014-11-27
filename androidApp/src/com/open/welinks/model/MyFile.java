package com.open.welinks.model;

public class MyFile {
	
	public class Status {
		public int Created = 0, LocalStored = 1, Uploading = 2, Uploaded = 3;
		public int state = Created;
	}
	
	
	
	public String path ="";
	public String fileName="";
	public int length;
}
