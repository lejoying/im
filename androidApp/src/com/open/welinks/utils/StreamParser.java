package com.open.welinks.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamParser {

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
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	public static void parseToFile(InputStream inputStream, FileOutputStream outputStream) {
		byte buffer[] = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void parseToFile(byte[] bytes, FileOutputStream outputStream) {
		try {
			outputStream.write(bytes, 0, bytes.length);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}