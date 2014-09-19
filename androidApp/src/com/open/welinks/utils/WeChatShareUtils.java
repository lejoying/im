package com.open.welinks.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.open.welinks.model.Constant;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeChatShareUtils {

	private static WeChatShareUtils utils;

	public boolean flag;

	private IWXAPI api;

	private Context context;

	private enum Status {
		friends, moments
	}

	public static WeChatShareUtils getInstance(Context context) {
		if (utils == null) {
			utils = new WeChatShareUtils(context);
		}
		return utils;
	}

	private WeChatShareUtils(Context context) {
		this.context = context;
		api = WXAPIFactory.createWXAPI(context, Constant.WECHAT_ADDID, true);
		flag = api.registerApp(Constant.WECHAT_ADDID);
	}

	public void shareMessageToWXFriends(Bitmap bitmap, String content, String phone, String gid, String gsid) {
		shareToWeChat(Status.friends, bitmap, content, phone, gid, gsid);
	}

	public void shareMessageToWXMoments(Bitmap bitmap, String content, String phone, String gid, String gsid) {
		shareToWeChat(Status.moments, bitmap, content, phone, gid, gsid);
	}

	private void shareToWeChat(Status status, Bitmap bitmap, String content, String phone, String gid, String gsid) {
		String url = "http://www.we-links.com/share/share.html?phone=" + phone + "&gid=" + gid + "&gsid=" + gsid;// 收到分享的好友点击信息会跳转到这个地址去

		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;

		WXMediaMessage localWXMediaMessage = new WXMediaMessage(localWXWebpageObject);
		localWXMediaMessage.title = "我分享了一个群分享，点击看看吧";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = content;
		localWXMediaMessage.thumbData = getBitmapBytes(bitmap);

		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		if (api.getWXAppSupportAPI() >= 0x21020001) {
			localReq.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		if (status == Status.friends) {
			localReq.scene = SendMessageToWX.Req.WXSceneSession;
		}
		localReq.transaction = System.currentTimeMillis() + "";
		localReq.message = localWXMediaMessage;

		IWXAPI api = WXAPIFactory.createWXAPI(context, Constant.WECHAT_ADDID, true);
		api.sendReq(localReq);
	}

	private byte[] getBitmapBytes(Bitmap bitmap) {
		Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		int i;
		int j;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			i = bitmap.getWidth();
			j = bitmap.getWidth();
		} else {
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
		while (true) {
			localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 80, 80), null);
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
				e.printStackTrace();
			}
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
	}
}
