package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CallingCardModifyYeWuActivity extends Activity {

	private EditText modifyyw;
	private TextView yewulength;
	private List<String> yewu;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify_yewu);
		initView();
		initData();
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	public void initView() {
		modifyyw = (EditText) findViewById(R.id.modifyyewu);
		yewulength = (TextView) findViewById(R.id.yewulength);
		modifyyw.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				yewulength.setText(s.length() + "/240");
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
	}

	public void top_back(View v) {
		finish();
	}

	public void save(View v) {
		if (modifyyw.getText().toString().equals("")) {
			return;
		}
		Intent intent = new Intent(this, CallingCardModifyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("item", CallingCardModifyActivity.MODIFY_YEWU);
		bundle.putString("value", modifyyw.getText().toString());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void random(View v) {
		Random random = new Random();
		int size = yewu.size();
		int temp=random.nextInt(size);
		while(position==temp){
			temp = random.nextInt(size);
		}
		position = temp;
		modifyyw.setText(yewu.get(position));
	}
}
