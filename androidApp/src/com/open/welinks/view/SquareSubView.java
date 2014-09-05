package com.open.welinks.view;

import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Squares.Square;
import com.open.welinks.model.Data.Squares.Square.SquareMessage;

public class SquareSubView {

	public Data data = Data.getInstance();

	public String tag = "SquareSubView";

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	public LayoutInflater mInflater;

	public SquareSubController thisController;

	public ListBody1 squareListBody;

	public RelativeLayout squareView;

	public ImageView squareReleaseButtonView;
	public TextView squareNameView;
	public RelativeLayout squareContainerView;

	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public void initViews() {
		this.displayMetrics = mainView.displayMetrics;
		this.squareView = mainView.squareView;
		this.mInflater = mainView.mInflater;

		squareReleaseButtonView = (ImageView) squareView.findViewById(R.id.squareReleaseButton);
		squareNameView = (TextView) squareView.findViewById(R.id.squareName);
		squareContainerView = (RelativeLayout) squareView.findViewById(R.id.squareContainer);

		squareListBody = new ListBody1();
		squareListBody.initialize(displayMetrics, squareContainerView);

		showSquareMessages();
	}

	public void showSquareMessages() {
		Square square = data.squares.squareMap.get("1001");
		List<String> squareMessagesOrder = square.squareMessagesOrder;
		Map<String, SquareMessage> squareMessagesMap = square.squareMessagesMap;
		squareListBody.listItemsSequence.clear();
		squareListBody.containerView.removeAllViews();

		for (int i = 0; i < squareMessagesOrder.size(); i++) {
			String key = squareMessagesOrder.get(i);
			SquareMessage squareMessage = squareMessagesMap.get(key);

			SquareMessageBody squareMessageBody = null;
			squareMessageBody = new SquareMessageBody(squareListBody);
			squareMessageBody.initialize(i);
			squareMessageBody.setData(squareMessage);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (115 * displayMetrics.density));
			squareMessageBody.y = this.squareListBody.height;
			squareMessageBody.cardView.setY(squareMessageBody.y);
			squareMessageBody.cardView.setX(0);
			this.squareListBody.height = this.squareListBody.height + 120 * displayMetrics.density;
			this.squareListBody.containerView.addView(squareMessageBody.cardView, layoutParams);
		}
		this.squareListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
	}

	public class SquareMessageBody extends MyListItemBody {

		SquareMessageBody(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView;

		public ImageView messageImageView;
		public TextView messageContentView;
		public TextView messageAuthorView;

		public String option;

		public View initialize(int i) {
			if (i % 2 == 1) {
				this.cardView = mInflater.inflate(R.layout.square_message_item_left, null);
				option = "left";
			} else {
				this.cardView = mInflater.inflate(R.layout.square_message_item_right, null);
				option = "right";
			}

			this.messageImageView = (ImageView) this.cardView.findViewById(R.id.messageImage);
			this.messageContentView = (TextView) this.cardView.findViewById(R.id.messageContent);
			this.messageAuthorView = (TextView) this.cardView.findViewById(R.id.messageAuthor);

			super.initialize(cardView);
			return cardView;
		}

		public void setData(SquareMessage message) {
			if ("left".equals(option)) {
				this.messageImageView.setImageResource(R.drawable.square_temp);
			} else {
				this.messageImageView.setImageResource(R.drawable.square_temp1);
			}
			this.messageContentView.setText(message.content);
			this.messageAuthorView.setText(message.phone);
		}
	}
}
