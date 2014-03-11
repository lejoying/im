package com.lejoying.wxgs.app.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StreamParser {

	public JSONObject parseToJSONObject(InputStream is) {
		JSONObject jsonObject = null;
		try {
			byte[] b = parseToByteArray(is);
			jsonObject = new JSONObject(new String(b));
		} catch (JSONException e) {
		}
		return jsonObject;
	}

	public static byte[] parseToByteArray(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = 0;
		byte data[] = null;
		if (is != null) {
			try {
				while ((len = is.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bos.flush();
					data = bos.toByteArray();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	public static Bitmap parseToBitmap(InputStream inputStream) {
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		return bitmap;
	}

}
