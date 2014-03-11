package com.lejoying.wxgs.app.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StreamParser {

	public JSONObject parseToJSONObject(InputStream is) throws IOException {
		JSONObject jsonObject = null;
		byte[] b = parseToByteArray(is);
		try {
			jsonObject = new JSONObject(new String(b));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static byte[] parseToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = 0;
		byte data[] = null;
		if (is != null) {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
			data = bos.toByteArray();
			bos.close();
		}
		return data;
	}

	public static Bitmap parseToBitmap(InputStream is) throws IOException {
		byte[] b = parseToByteArray(is);
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		return bitmap;
	}

}
