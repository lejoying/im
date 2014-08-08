var userInformation = {
	currentUser : {
		userBackground = "Back",
		sex : "male",
		id : 168987,
		phone : "",
		nickName : "",
		mainBusiness : "",
		head : "Head";
		accessKey : "";
		flag : "none";
	},
	localConfig : {
		deviceid : "",
		line1Number : "",
		imei : "",
		imsi : ""
	},
	serverConfig : {}
}

var relationship = {
	friendsMap : {
		"13566668881" : {
			userBackground = "Back",
			sex : "male",
			id : 168988,
			phone : "13566668881",
			nickName : "",
			mainBusiness : "",
			head : "Head",
			accessKey : "",
			flag : "none",
			distance : 132564,
			friendStatus : "",
			addMessage : "",
			notReadMessagesCount : 12,
			longitude : "",
			latitude : "",
			alias : "",
		},
		"13566668882" : {
			userBackground = "Back",
			sex : "male",
			id : 168988,
			phone : "13566668882",
			nickName : "",
			mainBusiness : "",
			head : "Head",
			accessKey : "",
			flag : "none",
			distance : 132564,
			friendStatus : "",
			addMessage : "",
			notReadMessagesCount : 12,
			longitude : "",
			latitude : "",
			alias : "",
		},
	},
	circles : ["好友", "同学", "同乡"],
	circlesMap : {
		"好友" : {
			rid : 123,
			name : "好友",
			friends : ["13355558881", "15266688897"]
		},
		"默认分组" : {
			rid : 8888888,
			name : "默认分组",
			friends : ["13355558881", "15266688897"]
		},
	},
	groups : ["好友圈", "同学会", "同乡会"],
	groupsMap : {
		"好友圈" : {
			gid : 123,
			icon : "",
			name : "好友圈",
			notReadMessagesCount : 12,
			distance : 4562;
			longitude : "";
			latitude : "";
			description : "";
			background : "";
			members : ["13355558881", "15266688897"]
		},
		"同学会" : {
			gid : 124,
			icon : "",
			name : "同学会",
			notReadMessagesCount : 12,
			distance : 4562;
			longitude : "";
			latitude : "";
			description : "";
			background : "";
			members : ["13355558881", "15266688897"]
		},
	},
};

var message = {
	friendMessageMap : {
		"13566998877" : [{
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			}, {
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			}
		],
		"13566998878" : [{
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			}, {
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			},
		],
	},

	groupMessageMap : {
		"好友圈" : [{
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			}, {
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			}
		],
		"同学会" : [{
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			}, {
				type : 1,
				time = "",
				sendType = "",
				gid = "",
				status = "",
				phone = "",
				nickName = "",
				contentType : "";
				content : "",
			},
		],
	},
	messagesOrder : ["同学会", "13566998877", "13566998878"],
};

var share = {

	"同学会" : {
		sharesOrder : ["1575", "1576", "1577"],
		sharesMap : {
			"1575" : {
				mType : 1,
				gsid : 1575,
				type : "imagetext",
				phone : "13899998888",
				time : 1387467785445,
				praiseusers : [],
				comments : [],
				content : ""
			},

			"1576" : {
				mType : 1,
				gsid : 1576,
				type : "imagetext",
				phone : "13899998888",
				time : 1387467785445,
				praiseusers : [],
				comments : [{

						phone : "",
						nickName : "",
						head : "",
						phoneTo : "",
						nickNameTo : "",
						headTo : "",
						contentType : "text",
						content : "",
						time : 1356546894,
					}
				],
				content : ""
			},
			"1577" : {
				mType : 1,
				gsid : 1577,
				type : "imagetext",
				phone : "13899998888",
				time : 1387467785445,
				praiseusers : [],
				comments : [],
				content : ""
			},
		}
	}
}
