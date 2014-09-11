package com.open.welinks.model;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Environment;

public class FileHandlers {

	public static FileHandlers fileHandlers;

	public File sdcard;
	public File sdcardFolder;
	public File sdcardImageFolder;
	public File sdcardVoiceFolder;
	public File sdcardHeadImageFolder;
	public File sdcardBackImageFolder;
	public File sdcardThumbnailFolder;

	public class Bitmaps {
		public Map<String, SoftReference<Bitmap>> softBitmaps = new Hashtable<String, SoftReference<Bitmap>>();

		public void put(String key, Bitmap bitmap) {
			softBitmaps.put(key, new SoftReference<Bitmap>(bitmap));
		}

		public Bitmap get(String key) {
			if (softBitmaps.get(key) == null) {
				return null;
			}
			return softBitmaps.get(key).get();
		}
	}

	public Bitmaps bitmaps = new Bitmaps();

	public static FileHandlers getInstance() {
		if (fileHandlers == null) {
			fileHandlers = new FileHandlers();
		}
		return fileHandlers;
	}

	public FileHandlers() {
		sdcard = Environment.getExternalStorageDirectory();
		sdcardFolder = new File(sdcard, "welinks");
		if (!sdcardFolder.exists()) {
			sdcardFolder.mkdirs();
		}
		sdcardImageFolder = new File(sdcardFolder, "image");
		if (!sdcardImageFolder.exists()) {
			sdcardImageFolder.mkdirs();
		}
		sdcardVoiceFolder = new File(sdcardFolder, "voice");
		if (!sdcardVoiceFolder.exists()) {
			sdcardVoiceFolder.mkdirs();
		}
		sdcardHeadImageFolder = new File(sdcardFolder, "head");
		if (!sdcardHeadImageFolder.exists()) {
			sdcardHeadImageFolder.mkdirs();
		}
		sdcardBackImageFolder = new File(sdcardFolder, "background");
		if (!sdcardBackImageFolder.exists()) {
			sdcardBackImageFolder.mkdirs();
		}
		sdcardThumbnailFolder = new File(sdcardFolder, "thumbnail");
		if (!sdcardThumbnailFolder.exists()) {
			sdcardThumbnailFolder.mkdirs();
		}
	}
}
