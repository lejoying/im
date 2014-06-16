package com.lejoying.wxgs.activity.mode.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MapStorageDirectoryActivity;
import com.lejoying.wxgs.activity.mode.BaseModeManager.KeyDownListener;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.User;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.handler.OSSFileHandler.ImageMessageInfo;
import com.lejoying.wxgs.app.handler.OSSFileHandler.SaveBitmapInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.SaveSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileSettings;

public class ModifyFragment extends BaseFragment implements OnClickListener,
		OnFocusChangeListener {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	int RESULT_SELECTHEAD = 0x123;
	int RESULT_TAKEPICTURE = 0xa3;
	int RESULT_CATPICTURE = 0x3d;

	List<String> yewu;

	View mContent;
	TextView tv_name;
	EditText et_name;
	TextView tv_sex;
	TextView tv_yewu;
	EditText et_yewu;
	View tv_modifychangepwd;

	ImageView iv_head;
	View rl_head;
	View rl_yewu_edit;
	View rl_name;
	View rl_sex;
	View rl_yewu;

	TextView tv_phone;

	View tv_spacing;
	View rl_editbar;
	View rl_save;
	View rl_cancel;

	View rl_edithead;
	View rl_fromgallery;
	View rl_takepicture;
	View rl_cancelselect;

	View tv_random;

	TextView tv_yewulength;

	boolean isEdit;

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_modifyinfo, null);
		app.fileHandler.getBackgroundImage(app.data.user.userBackground,
				new FileResult() {
					@Override
					public void onResult(String where, Bitmap bitmap) {
						mContent.setBackgroundDrawable(new BitmapDrawable(
								bitmap));

					}
				});

		isEdit = false;
		initData();
		initView();
		mMainModeManager.setKeyDownListener(new KeyDownListener() {

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				boolean flag = true;
				if (keyCode == KeyEvent.KEYCODE_BACK && isEdit) {
					endModify(false);
					flag = false;
				}
				return flag;
			}
		});
		return mContent;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	public void initView() {
		rl_head = mContent.findViewById(R.id.rl_head);
		iv_head = (ImageView) mContent.findViewById(R.id.iv_head);
		tv_name = (TextView) mContent.findViewById(R.id.tv_name);
		et_name = (EditText) mContent.findViewById(R.id.et_name);
		tv_sex = (TextView) mContent.findViewById(R.id.tv_sex);
		tv_modifychangepwd = mContent.findViewById(R.id.tv_modifychangepwd);

		tv_yewu = (TextView) mContent.findViewById(R.id.tv_yewu);
		et_yewu = (EditText) mContent.findViewById(R.id.et_yewu);
		tv_phone = (TextView) mContent.findViewById(R.id.tv_phone);
		rl_yewu_edit = mContent.findViewById(R.id.rl_yewu_edit);
		rl_name = mContent.findViewById(R.id.rl_name);
		rl_yewu = mContent.findViewById(R.id.rl_yewu);
		rl_sex = mContent.findViewById(R.id.rl_sex);
		tv_random = mContent.findViewById(R.id.tv_random);

		final String headFileName = app.data.user.head;
		app.fileHandler.getHeadImage(headFileName, app.data.user.sex,
				new FileResult() {
					@Override
					public void onResult(String where, Bitmap bitmap) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});

		rl_edithead = mContent.findViewById(R.id.rl_edithead);
		rl_fromgallery = mContent.findViewById(R.id.rl_fromgallery);
		rl_takepicture = mContent.findViewById(R.id.rl_takepicture);
		rl_cancelselect = mContent.findViewById(R.id.rl_cancelselect);

		tv_modifychangepwd.setOnClickListener(this);
		rl_fromgallery.setOnClickListener(this);
		rl_takepicture.setOnClickListener(this);
		rl_cancelselect.setOnClickListener(this);

		rl_name.setOnClickListener(this);
		rl_sex.setOnClickListener(this);
		rl_yewu.setOnClickListener(this);
		et_name.setOnFocusChangeListener(this);
		et_yewu.setOnFocusChangeListener(this);

		rl_head.setOnClickListener(this);

		tv_spacing = mContent.findViewById(R.id.tv_spacing);
		rl_editbar = mContent.findViewById(R.id.rl_editbar);
		rl_save = mContent.findViewById(R.id.rl_save);
		rl_cancel = mContent.findViewById(R.id.rl_cancel);

		tv_random.setOnClickListener(this);
		rl_save.setOnClickListener(this);
		rl_cancel.setOnClickListener(this);

		tv_name.setText(app.data.user.nickName);
		tv_sex.setText(app.data.user.sex);
		tv_phone.setText(app.data.user.phone);
		tv_yewu.setText(app.data.user.mainBusiness);

		tv_yewulength = (TextView) mContent.findViewById(R.id.tv_yewulength);

		et_yewu.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				tv_yewulength.setText(s.length() + "/240");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	void modifyMode(View v) {
		changeSelectBackGround(v);
		if (!isEdit) {
			isEdit = true;
			tv_name.setVisibility(View.GONE);
			tv_yewu.setVisibility(View.GONE);
			et_name.setVisibility(View.VISIBLE);
			et_name.setText(tv_name.getText());
			et_yewu.setVisibility(View.VISIBLE);
			et_yewu.setText(tv_yewu.getText());
			rl_yewu_edit.setVisibility(View.VISIBLE);

			tv_spacing.setVisibility(View.VISIBLE);
			rl_editbar.setVisibility(View.VISIBLE);
			rl_edithead.setVisibility(View.GONE);
		}
	}

	void endModify(boolean isSave) {
		hideSoftInput();
		if (isEdit) {
			isEdit = false;
			tv_name.setVisibility(View.VISIBLE);
			tv_yewu.setVisibility(View.VISIBLE);
			if (isSave) {
				User user = new User();
				user.nickName = et_name.getText().toString();
				user.mainBusiness = et_yewu.getText().toString();
				user.sex = tv_sex.getText().toString();
				tv_name.setText(user.nickName);
				tv_yewu.setText(user.mainBusiness);
				tv_sex.setText(user.sex);
				modify(user);
			}
			et_name.setVisibility(View.GONE);
			et_yewu.setVisibility(View.GONE);
			rl_yewu_edit.setVisibility(View.GONE);
			tv_spacing.setVisibility(View.GONE);
			rl_editbar.setVisibility(View.GONE);
			rl_name.setBackgroundColor(Color.argb(0, 255, 255, 255));
			rl_yewu.setBackgroundColor(Color.argb(0, 255, 255, 255));
			rl_sex.setBackgroundColor(Color.argb(0, 255, 255, 255));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_head:
			if (isEdit) {
				Alert.createDialog(getActivity()).setTitle("是否保存修改")
						.setLeftButtonText("保存")
						.setOnConfirmClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputDialog dialog) {
								endModify(true);
								modifyHead();
							}
						}).show();
			} else {
				modifyHead();
			}
			break;
		case R.id.rl_name:
			modifyMode(v);
			requestFocus(et_name);
			break;
		case R.id.rl_yewu:
			modifyMode(v);
			requestFocus(et_yewu);
			break;
		case R.id.rl_sex:
			modifyMode(v);
			if (tv_sex.getText().equals("男")) {
				tv_sex.setText("女");
			} else {
				tv_sex.setText("男");
			}
			break;
		case R.id.rl_save:
			endModify(true);
			break;
		case R.id.rl_cancel:
			endModify(false);
			break;
		case R.id.tv_random:
			et_yewu.requestFocus();
			et_yewu.setText(yewu.get(new Random().nextInt(yewu.size())));
			break;
		case R.id.rl_fromgallery:
			selectPicture();
			rl_edithead.setVisibility(View.GONE);
			break;
		case R.id.rl_takepicture:
			takePicture();
			rl_edithead.setVisibility(View.GONE);
			break;
		case R.id.rl_cancelselect:
			rl_edithead.setVisibility(View.GONE);
			break;

		case R.id.tv_modifychangepwd:
			mMainModeManager.showNext(mMainModeManager.mChangePasswordFragment);
			break;
		default:
			break;
		}
	}

	void selectPicture() {
		Intent selectFromGallery = new Intent(getActivity(),
				MapStorageDirectoryActivity.class);
		MapStorageDirectoryActivity.max = 1;
		startActivityForResult(selectFromGallery, RESULT_SELECTHEAD);
	}

	void takePicture() {
		tempFile = new File(app.sdcardImageFolder, "tempimage");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++));
		}
		Uri uri = Uri.fromFile(tempFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(tackPicture, RESULT_TAKEPICTURE);
	}

	void modifyHead() {
		rl_edithead.setVisibility(View.VISIBLE);
	}

	void changeSelectBackGround(View selectView) {
		rl_name.setBackgroundColor(Color.argb(0, 0, 0, 0));
		rl_yewu.setBackgroundColor(Color.argb(0, 0, 0, 0));
		rl_sex.setBackgroundColor(Color.argb(0, 0, 0, 0));
		selectView.setBackgroundColor(Color.argb(32, 255, 255, 255));
	}

	void requestFocus(EditText editText) {
		editText.requestFocus();
		showSoftInput(editText);
		editText.setSelection(editText.getText().toString().length());
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (et_name.hasFocus()) {
			changeSelectBackGround(rl_name);
		}
		if (et_yewu.hasFocus()) {
			changeSelectBackGround(rl_yewu);
		}

	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_CATPICTURE);
	}

	File tempFile;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SELECTHEAD
				&& resultCode == Activity.RESULT_OK) {
			Uri selectedImage = Uri.parse("file://"
					+ MapStorageDirectoryActivity.selectedImages.get(0));
			startPhotoZoom(selectedImage);
		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = Uri.fromFile(tempFile);
			startPhotoZoom(uri);
		} else if (requestCode == RESULT_CATPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {
			if (tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
			final Bitmap head = (Bitmap) data.getExtras().get("data");
			app.fileHandler.saveBitmap(new SaveBitmapInterface() {

				@Override
				public void setParams(SaveSettings settings) {
					settings.source = head;
					settings.compressFormat = settings.PNG;
					settings.folder = app.sdcardHeadImageFolder;
				}

				@Override
				public void onSuccess(final String fileName, String base64) {
					if (!fileName.equals(app.data.user.head)) {
						final String path = new File(app.sdcardHeadImageFolder,
								fileName).getAbsolutePath();
						app.fileHandler
								.getFileMessageInfo(new FileMessageInfoInterface() {

									@Override
									public void setParams(
											FileMessageInfoSettings settings) {
										settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDSELECTIMAGE;
										settings.path = path;
										settings.fileName = fileName;
									}

									@Override
									public void onSuccess(
											ImageMessageInfo imageMessageInfo) {
										checkImage(imageMessageInfo,
												"image/png", path);
									}
								});

					}
				}
			});
		}

	}

	public void checkImage(final ImageMessageInfo imageMessageInfo,
			final String contentType, final String path) {

		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_CHECK;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", imageMessageInfo.fileName);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					if (jData.getBoolean("exists")) {
						User user = new User();
						user.head = imageMessageInfo.fileName;
						modify(user);
					} else {

						app.fileHandler.uploadFile(new UploadFileInterface() {

							@Override
							public void setParams(UploadFileSettings settings) {
								settings.imageMessageInfo = imageMessageInfo;
								settings.contentType = contentType;
								settings.fileName = imageMessageInfo.fileName;
								settings.path = path;
								settings.uploadFileType = OSSFileHandler.UPLOAD_FILE_TYPE_HEADS;
							}

							@Override
							public void onSuccess(Boolean flag, String fileName) {
								User user = new User();
								user.head = fileName;
								modify(user);
							}
						});
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

//	public void uploadImage(final String fileName, final String base64) {
//		app.networkHandler.connection(new CommonNetConnection() {
//
//			@Override
//			protected void settings(Settings settings) {
//				settings.url = API.DOMAIN + API.IMAGE_UPLOAD;
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("phone", app.data.user.phone);
//				params.put("accessKey", app.data.user.accessKey);
//				params.put("filename", fileName);
//				params.put("imagedata", base64);
//				settings.params = params;
//			}
//
//			@Override
//			public void success(JSONObject jData) {
//				User user = new User();
//				user.head = fileName;
//				modify(user);
//			}
//		});
//	}

	public void modify(final User user) {
		JSONObject account = new JSONObject();
		try {
			if (user.head != null && !user.head.equals("Head")) {
				account.put("head", user.head);
			}
			if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
				account.put("mainBusiness", user.mainBusiness);
			}
			if (user.sex != null && !user.sex.equals("")) {
				account.put("sex", user.sex);
			}
			if (user.nickName != null && !user.mainBusiness.equals("")) {
				account.put("nickName", user.nickName);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		params.put("account", account.toString());

		app.dataHandler.exclude(new Modification() {

			@Override
			public void modifyData(Data data) {
				if (user.head != null && !user.head.equals("Head")) {
					data.user.head = user.head;
				}
				if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
					data.user.mainBusiness = user.mainBusiness;
				}
				if (user.sex != null && !user.sex.equals("")) {
					data.user.sex = user.sex;
				}
				if (user.nickName != null && !user.mainBusiness.equals("")) {
					data.user.nickName = user.nickName;
				}
			}

			@Override
			public void modifyUI() {
				if (user.head != null && !user.head.equals("Head")) {
					final String headFileName = app.data.user.head;
					app.fileHandler.getHeadImage(headFileName,
							app.data.user.sex, new FileResult() {
								@Override
								public void onResult(String where, Bitmap bitmap) {
									iv_head.setImageBitmap(app.fileHandler.bitmaps
											.get(headFileName));
								}
							});

				}
				if (mMainModeManager.mBusinessCardFragment.isAdded()) {
					mMainModeManager.mBusinessCardFragment.initData();
				}
			}
		});

		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_MODIFY;
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
			}
		});
	}

	public void initData() {
		yewu = new ArrayList<String>();
		yewu.add("《兼职一》抄作业，  小学所有课本，70/本，中学所有课本100/本  ，高中所有课本，150/本。…注：本人字体优美洒脱，颇有古人风范。  ");
		yewu.add("《兼职二》代欺负其他小同学，               按身高收费，打1米3～1米4的小同学， 50元/人，  1米4～1米5/60元。1米5-1米6/70元，1米6～1米7/90元。1米7～1米8/100。。1米8以上的活不接 。…注：有哥哥在同一学校的加二十元。爸爸是老师的加三十元每人，《本人有优秀打手二十余人，各个体型魁梧，有丰富沙场做战经验");
		yewu.add("《兼职三》打别人家玻璃。         一楼10/块。二楼20/块，三楼30/块。四楼以上（含四楼）40/块。…注：家里有大狗或爸爸在家的每块加五元，『也可按弹弓收费，每弹弓五元，本人持特制弹弓，自被弹珠，射程远，威力强，命中有保障 』");
		yewu.add("《兼职四》爆别人自行车胎。所有车胎至少爆五个，普通自行车1/次。高档自行车3/次。普通摩托车5/次,高档摩托7/次。所有轿车10/次。拖拉机的活不接。…注：本人自制高档铁针，手劲足，威力大，爆破百分百。");
		yewu.add("《兼职五》涂粪，        往别人家墙上涂粪。每斤粪便四十元，粪便由自己提供。…注：本人有自制折叠式毛唰，可刷三楼以下（含三楼）。家里有大狗、爸爸哥哥在家者每斤加二十元，有风加十元。。…《《特殊情况，也可往老师身上泼粪便。女老师二十元每桶，男老师六十元每桶。体育老师（含跆拳道、柔道、空手道老师）此类活不接。。         …   …所有客户累计满三百元后给六折优惠。");
		yewu.add("本人长期替小学生写寒假，暑假作业，替小学生欺负其他小朋友（限4-8岁）");
		yewu.add("承接以下业务：苦力搬运，钳焊，水电，瓦工，砸墙打地洞，捅厕所下水道，赌场看场。捉奸，逼龙门，黑枪，黑车，打手，军火运输，暗杀，隆胸。。。。。");
		yewu.add("完成日常办公工作，管理本部门各类报表、资料，完成餐饮总监交代的其他工作。");
		yewu.add("督和指导领班及服务员完成日常经营工作，确保前厅工作顺利开展；属中层管理人员。");
		yewu.add("巡视管辖范围内的环境卫生工作，关注所有设备的正常运转。");
		yewu.add("协助经理工作，按分工负责的原则，主持部分日常工作， 对自己分管的工作负主要责任。");
		yewu.add("认真执行总公司采购管理规定和实施细则，严格按采购计划采购，做到及时、适用，合理降低物资积压和采购成本。对购进物品做到票证齐全、票物相符，报帐及时。");
		yewu.add("完成领导交办的其它各项工作。");
		yewu.add("认真贯彻国家技术工作方针、政策和公司有关规定。组织制定工艺技术工作近期和长远发展规划，并制定技术组织措施方案。");
		yewu.add("根据全国人大的决定和全国人大常委会的决定，公布法律，任免国务院总理、副总理、国务委员、各部部长、各委员会主任、审计长、秘书长，授予国家的勋章和荣誉称号，发布特赦令，宣布进入紧急状态，宣布战争状态，发布动员令。");
		yewu.add("依据全国人大或者全国人大常委会的决定行事，只有代表权，没有独立的决定权。");
		yewu.add("负责召集中央政治局会议和中央政治局常务委员会会议，并主持中央书记处的工作。");
		yewu.add("全面领导国务院工作，对全国人大及其常委会负责");
		yewu.add("颁布和废止行政法规、任免特别行政区行政长官、解除戒严");
		yewu.add("统一指挥全国武装力量；决定军事战略和武装力量的作战方针");
		yewu.add("领导和管理中国人民解放军的建设，制定规划、计划并组织实施");
		yewu.add("决定中国人民解放军的体制和编制，规定总部以及军区、军兵种和其他军区级单位的任务和职责");
		yewu.add("批准武装力量的武器装备体制和武器装备发展规划、计划，协同国务院领导管理国防科研生产");
		yewu.add("做饭、打扫，做一点家务，也带孩子");
		yewu.add("依法制定和执行货币政策。");
		yewu.add("发行人民币，管理人民币流通。");
		yewu.add("承办国务院交办的其他事项。");
		yewu.add("准时上下班，认真做好责任区的各项清洁工作，不留死角，坚持规范化服务，坚守工作岗位");
		yewu.add("完成总务处临时分配的其它工作任务");
		yewu.add("按公司要求高标准做好责任区内的清扫保洁工作。");
		yewu.add("每天对责任区内的楼道和道路及游乐设施进行清扫保洁一次");
		yewu.add("做好责任区环境卫生宣传和管理工作台。");
		yewu.add("协助管理处做好小区安防工作，发现可疑人或事，立即向管理处负责人报告。");
		yewu.add("卫生间每日清扫不少于二次，并随时保洁，要求地面干净，无异味，无蚊蝇。");
		yewu.add("代表公司与外界有关部门和机构联络并保持良好合作关系");
		yewu.add("负责将公司的政策、原则、策略等信息，快速、清晰、准确地传达给直接下级");
		yewu.add("负责主持本部的日常工作，召开部门内部工作会议。");
		yewu.add("开展调查研究，及时了解掌握学生中存在的问题和困难，向学校相关部门反映。");
		yewu.add("吸收热爱公益事业、愿意为公众服务的人士为会员。");
		yewu.add("全矿的基层组织建设进一步加强，基层组织建设扎实有效，逐年上台阶。");
		yewu.add("对社员干部进行思想政治教育和风气纪律教育，组织民主评议工作");
		yewu.add("生活中，记得你是谁，网络上，忘了你是谁。");
		yewu.add("把脾气拿出来，那叫本能；把脾气压回去，才叫本事。");
		yewu.add("普通话没有达到一级乙等不要和我说话，打字速度低于每分钟80个请将我删除!");
		yewu.add("生活就像“呼吸”，“呼”是为了出一口气，“吸”是为了争一口气。");
		yewu.add("我自横刀向天笑，笑完之后去睡觉!");
		yewu.add("逆风的方向，更适合飞翔。我不怕万人阻挡，只怕自己投降。");
		yewu.add("你说你会等我回来，你确实等了，还找了一个人一起等。");
		yewu.add("雷锋做了好事不留名，但是每一件事情都记到日记里面。");
		yewu.add("三天不睡觉，一觉睡三天！");
		yewu.add("这年头还整天挂QQ的人，除了上班没事做，就是下班没人爱的人……");
		yewu.add("因为我不把自己当回事儿，所以别人只能把我当回事儿.");
		yewu.add("谁让我不开心，我就让他很伤心.");
		yewu.add("我把他当人时，他把我当狗。我把他当狗时，他才知道我是人。");
		yewu.add("你强，本身就是一种狂。你弱，本身就是一种错。");
		yewu.add("圣诞节一个人过，元旦也一个人过，期末也让我一个人过吧！");
		yewu.add("为了我的奥迪，你的迪奥，咱孩子的奥利奥。努力！奋斗！");
		yewu.add("一个人，要是不逼自己一把，根本不知道自己有多优秀……");
		yewu.add("其实你不用自卑，因为你曾经在几千万甚至以亿计的选手中赢夺冠军");
		yewu.add("每个自称是“姐”的女人都在找很Man的男人，结果发现最Man的是自己。");
		yewu.add("凡事要主动，比如，你可以爬上墙头等红杏。");
		yewu.add("如果你想知道什么是绝望，就买一堆彩票。");
		yewu.add("吃，我所欲也，瘦，亦我所欲也，二者不可得兼，我了个去也。");
		yewu.add("你不是VIP,甚至不是IP,你只是一个P!");
		yewu.add("每当打扫卫生时，学校会说“学校是你家”;可当你一迟到，学校会说“你当学校是你家呢？”");
		yewu.add("我们每个人都是梦想家，当梦走了，就只剩想家了。");
		yewu.add("因为我没翅膀，所以我不完美。因为我很快乐，所以我不痛苦。因为我不蠢，所以我很傻。因为我想的不多，所以我得到很多。");
		yewu.add("我又不是人民币，怎么能让人人都喜欢我?!");
		yewu.add("其实你我都一样，人人都在装，关键是要装像了，装圆了，有一个门槛，装成了就迈进去，成为传说中的性情中人，没装好，就卡在那里了。就是卡门");
		yewu.add("说金钱是罪恶，都在捞;说美女是祸水，都想要;说高处不胜寒，都在爬;说烟酒伤身体，都不戒;说天堂最美好，都不去!");
		yewu.add("我当年也是个痴情的种子，结果下了场雨……淹死了。");
		yewu.add("傻子偷乞丐的钱包，被瞎子看到了，哑巴大吼一声，把聋子吓了一跳，驼子挺身而出，瘸子飞起一脚，通辑犯要拉他去公共安全专家局，麻子说，看我的面子算了。");
		yewu.add("择一城终老，遇一人白首。");
		yewu.add("有一些东西错过了，就一辈子错过了。人是会变的，守住一个不变的承诺，却守不住一颗善变的心。");
		yewu.add("有心的人，再远也会记挂对方；无心的人，近在咫尺却远在天涯。");
		yewu.add("笨人的可怕之处并不在其笨，而在其自作聪明。");
		yewu.add("这个世界上没有好人和坏人，只有做了好事的人，和做了坏事的人。");
		yewu.add("小时候认为流血了就是很严重的事，不管疼不疼，先哭了再说.直到长大后才发觉，其实流泪比流血更疼。");
		yewu.add("我建议大家对我的长相，理解为主，欣赏为辅。​");
		yewu.add("其实，我不是胖，只是懒得瘦。");
		yewu.add("我娘说浪子回头金不换，谁给我金子?我换。");
		yewu.add("不移动，怎联通？");
		yewu.add("睡着睡着，就睡出了理想和口水。");
		yewu.add("扔硬币：正面就去上网、反面就去睡觉，立起来就去写作业。");
		yewu.add("有人嫩得一掐就出水，我却怂得一掐就出鼻涕泡儿。");
		yewu.add("奥巴马，你还记得大明湖畔的那个小拉登么？");
		yewu.add("我不但手气好，脚气也不错。");
	}

}
