package cn.buaa.myweixin.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class StreamTools {
	/**
	 * 将InputStream读取到byte[]中
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] isToData(InputStream is) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = 0;
		while((len = is.read(buffer))!=-1){
			bos.write(buffer,0,len);
		}
		byte data[] = bos.toByteArray();
		bos.flush();
		bos.close();
		is.close();
		return data;
	}
}
