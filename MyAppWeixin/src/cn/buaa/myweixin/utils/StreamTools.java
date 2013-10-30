package cn.buaa.myweixin.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class StreamTools {
	/**
	 * 将InputStream读取到byte[]中
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] isToData(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = 0;
		byte data[] = null;
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
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return data;
	}
}
