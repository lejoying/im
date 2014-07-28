package com.lejoying.wxgs.activity.mode;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.fragment.ChangePasswordFragment;
import com.lejoying.wxgs.activity.mode.fragment.ChatMessagesFragment;
import com.lejoying.wxgs.activity.mode.fragment.CirclesFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupBusinessCardFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupManagerFragment;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.mode.fragment.ModifyFragment;
import com.lejoying.wxgs.activity.mode.fragment.NewFriendsFragment;
import com.lejoying.wxgs.activity.mode.fragment.ScanQRCodeFragment;
import com.lejoying.wxgs.activity.mode.fragment.SearchFriendFragment;
import com.lejoying.wxgs.activity.mode.fragment.SquareFragment;
import com.lejoying.wxgs.activity.mode.fragment.SquareOnLineUserFragment;

public class MainModeManager extends BaseModeManager {

	boolean isInit;
	FragmentManager mFragmentManager;

	public LinearLayout ll_menu_app;

	int mContentID = R.id.fragmentContent;
	// main
	public CirclesFragment mCirclesFragment;
	public GroupFragment mGroupFragment;
	public SquareFragment mSquareFragment;

	//
	public ScanQRCodeFragment mScanQRCodeFragment;
	public SearchFriendFragment mSearchFriendFragment;
	// public BusinessCardFragment mBusinessCardFragment;
	public ChatMessagesFragment mChatMessagesFragment;
	// public ChatFriendFragment mChatFragment;
	// public ChatGroupFragment mChatGroupFragment;
	public NewFriendsFragment mNewFriendsFragment;
	// public AddFriendFragment mAddFriendFragment;
	public GroupManagerFragment mGroupManagerFragment;
	public ChangePasswordFragment mChangePasswordFragment;
	public GroupBusinessCardFragment mGroupBusinessCardFragment;
	public GroupShareFragment mGroupShareFragment;
	public SquareOnLineUserFragment mSquareOnLineUserFragment;

	public static List<String[]> faceNamesList;

	public MainModeManager(MainActivity activity) {
		super(activity);
		isInit = false;
		mFragmentManager = activity.getSupportFragmentManager();
		ll_menu_app = activity.ll_menu_app;
	}

	@Override
	public void initialize() {
		if (!isInit) {
			isInit = true;
			// main
			mCirclesFragment = new CirclesFragment();
			mCirclesFragment.setMode(this);
			mGroupFragment = new GroupFragment();
			mGroupFragment.setMode(this);
			mSquareFragment = new SquareFragment();
			mSquareFragment.setMode(this);
			mChangePasswordFragment = new ChangePasswordFragment();
			mChangePasswordFragment.setMode(this);

			//
			mScanQRCodeFragment = new ScanQRCodeFragment();
			mScanQRCodeFragment.setMode(this);
			mSearchFriendFragment = new SearchFriendFragment();
			mSearchFriendFragment.setMode(this);
			// mBusinessCardFragment = new BusinessCardFragment();
			// mBusinessCardFragment.setMode(this);
			// mChatFragment = new ChatFriendFragment();
			// mChatFragment.setMode(this);
			mChatMessagesFragment = new ChatMessagesFragment();
			mChatMessagesFragment.setMode(this);
			// mChatGroupFragment = new ChatGroupFragment();
			// mChatGroupFragment.setMode(this);
			mNewFriendsFragment = new NewFriendsFragment();
			mNewFriendsFragment.setMode(this);
			// mAddFriendFragment = new AddFriendFragment();
			// mAddFriendFragment.setMode(this);
			mGroupManagerFragment = new GroupManagerFragment();
			mGroupManagerFragment.setMode(this);
			mGroupBusinessCardFragment = new GroupBusinessCardFragment();
			mGroupBusinessCardFragment.setMode(this);
			mGroupShareFragment = new GroupShareFragment();
			mGroupShareFragment.setMode(this);
			mSquareOnLineUserFragment = new SquareOnLineUserFragment();
			mSquareOnLineUserFragment.setMode(this);
			initFace();
		}
	}

	private void initFace() {
		faceNamesList = new ArrayList<String[]>();
		String[] faceNames1 = new String[] { "[微笑]", "[撇嘴]", "[色]", "[发呆]",
				"[得意]", "[流泪]", "[害羞]", "[闭嘴]", "[睡]", "[大哭]", "[尴尬]", "[发怒]",
				"[调皮]", "[呲牙]", "[惊讶]", "[难过]", "[酷]", "[冷汗]", "[抓狂]", "[吐]",
				"[偷笑]", "[可爱]", "[白眼]", "[傲慢]", "[饥饿]", "[困]", "[惊恐]", "[流汗",
				"[憨笑]", "[大兵]", "[奋斗]", "[咒骂]", "[疑问]", "[嘘]", "[晕]", "折磨]",
				"[衰]", "[骷髅]", "[敲打]", "[再见]", "[擦汗]", "[抠鼻]", "[鼓掌]", "[糗大了]",
				"[坏笑]", "[左哼哼]", "[右哼哼]", "[哈欠]", "[鄙视]", "[委屈]", "[快哭了]",
				"[阴险]", "[亲亲]", "[吓]", "[可怜]", "[菜刀]", "[西瓜]", "[啤酒]", "[篮球]",
				"[乒乓]", "[咖啡]", "[饭]", "[猪头]", "[玫瑰]", "[凋谢]", "[示爱]", "[爱心]",
				"[心碎]", "[蛋糕]", "[闪电]", "[炸弹]", "[刀]", "[足球]", "[瓢虫]", "[便便]",
				"[月亮]", "[太阳]", "[礼物]", "[拥抱]", "[强", "[弱]", "[握手]", "[胜利]",
				"[抱拳]", "[勾引]", "[拳头]", "[差劲]", "[爱你]", "[NO]", "[OK]", "[爱情]",
				"[飞吻]", "[跳跳]", "[发抖]", "[怄火]", "[转圈]", "[磕头]", "[回头]", "[跳绳]",
				"[挥手]", "[激动]", "[街舞]", "[献吻]", "[左太极]", "[右太极]" };
		String[] faceNames2 = new String[] { "<笑脸>", "<开心>", "<大笑>", "<热情>",
				"<眨眼>", "<色>", "<接吻>", "<亲吻>", "<脸红>", "<露齿笑>", "<满意>", "<戏弄>",
				"<吐舌>", "<无语>", "<得意>", "<汗>", "<失望>", "<低落>", "<呸>", "<焦虑>",
				"<担心>", "<震惊>", "<悔恨>", "<眼泪>", "<哭>", "<破涕为笑>", "<晕>", "<恐惧>",
				"<心烦>", "<生气>", "<睡觉>", "<生病>", "<恶魔>", "<外星人>", "<心>", "<心碎>",
				"<丘比特>", "<闪烁>", "<星星>", "<叹号>", "<问号>", "<睡着>", "<水滴>",
				"<音乐>", "<火>", "<便便>", "<强>", "<弱>", "<拳头>", "<胜利>", "<上>",
				"<下>", "<右>", "<左>", "<第一>", "<强壮>", "<吻>", "<热恋>", "<男孩>",
				"<女孩>", "<女士>", "<男士>", "<天使>", "<骷髅>", "<红唇>", "<太阳>", "<下雨>",
				"<多云>", "<雪人>", "<月亮>", "<闪电>", "<海浪>", "<猫>", "<小狗>", "<老鼠>",
				"<仓鼠>", "<兔子>" };
		faceNamesList.add(faceNames1);
		faceNamesList.add(faceNames2);

	}

	@Override
	public void release() {
		isInit = false;
		super.release();
	}

	public void handleMenu(boolean flag) {
		if (flag) {
			if (ll_menu_app.getVisibility() == View.GONE) {
				ll_menu_app.setVisibility(View.VISIBLE);
			}
		} else {
			if (ll_menu_app.getVisibility() == View.VISIBLE) {
				ll_menu_app.setVisibility(View.GONE);
			}
		}
	}
}
