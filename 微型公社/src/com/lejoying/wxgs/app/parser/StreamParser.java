package com.lejoying.wxgs.app.parser;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StreamParser {

	public static JSONObject parseToJSONObject(InputStream is) {
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
				bos.flush();
				data = bos.toByteArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	public static Bitmap parseToBitmap(InputStream is) {
		byte[] b = parseToByteArray(is);
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		return bitmap;
	}

	public static Object parseToObject(InputStream is) {
		Object obj = null;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(is);
			obj = objectInputStream.readObject();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	public static void parseToObjectFile(FileOutputStream outputStream,
			Object obj) {
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void parseToFile(InputStream inputStream,
			FileOutputStream outputStream) {
		byte buffer[] = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
