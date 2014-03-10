var scrollInitFlag = false;
var tempSendMessageTimeStamp = [];
var tempAccountChatMessages = {}
var tempChatUsers = {};
var tempChatUsersList = [];
$(function () {

    var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    if (wxgs_tempAccountChatMessages != null) {
        tempAccountChatMessages = wxgs_tempAccountChatMessages;
    }
    var wxgs_tempChatUsers = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatUsers"));
    var wxgs_tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatUsersList"));
    if (wxgs_tempChatUsers != null) {
        tempChatUsers = wxgs_tempChatUsers;
        tempChatUsersList = wxgs_tempChatUsersList;
    }
    $(".js_chatRightFrame").css({
        visibility: "hidden"
    });
    /*$(".js_morefriend").hide();
     $(".js_morefriend").slideUp();
     $(".js_onlyfriend").slideUp(1);
     $(".js_onlyfriend").slideDown(100, function () {
     });*/
    $(document).on("focus", ".js_chatmessagecontent", function () {
        document.onkeydown = function (event) {
            var e = event ? event : (window.event ? window.event : null);
            if (event.ctrlKey && e.keyCode == 13) {
                clickSendMessage();
            }
        }
    });
    $(document).on("click", ".js_chatsendmessage", function () {
        clickSendMessage();
    });
    function clickSendMessage() {
        //        alert(currentChatType);
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var message = $(".js_chatmessagecontent").val();
        if (message.trim() == "") {
            alert("不能发送空白信息");
            return;
        }
        if (currentChatType == "POINT") {
            modifyTempChatUser(currentChatUser.phone);
        }
        var listPhone = [];
        if (currentChatType == "POINT") {
            listPhone.push(currentChatUser.phone);
        } else if (currentChatType == "TEMPGROUP" || currentChatType == "GROUP") {
            listPhone = currentChatGroup.members;
        }
        var messageObj = {
            contentType: "text",
            content: message
        };
        var time = new Date().getTime()
        tempSendMessageTimeStamp.push(time);
        var showMessage = { sendType: "point",
            contentType: "text",
            time: time,
            phone: accountObj.phone,
            phoneto: JSON.stringify(listPhone),
            content: message
        };
        var data = {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            sendType: "point",
            phoneto: JSON.stringify(listPhone),
            message: JSON.stringify(messageObj)
        };
        if (currentChatType == "TEMPGROUP") {
            data.gid = currentChatGroup.tempGid;
            showMessage.sendType = "tempGroup";
            data.sendType = "tempGroup";
            showMessage.tempGid = currentChatGroup.tempGid;
        } else if (currentChatType == "GROUP") {
            data.gid = currentChatGroup.gid;
            data.sendType = "group";
            showMessage.sendType = "group";
            showMessage.gid = currentChatGroup.gid;
        }
        var sendMessage = [
            JSON.stringify(showMessage)
        ];

        sendMessages(data, showMessage);
        var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
        var baseString = (currentChatType.substr(0, 1)).toLowerCase();
        if (wxgs_tempAccountChatMessages[baseString + "_" + currentChatUser.phone] == undefined) {
            ($(".js_chatContents").find(".noMsgIip")).html("");
        }
//        messagesDataSplit({messages: [sendMessage]});
        if (currentChatType == "POINT") {
            getTemplateHtml("tempChatUserInfo", function (template) {
                if ($("#js_conv_wxgsid_" + currentChatUser.phone).attr("id") != undefined) {
                    $("#conversationContainer")[0].insertBefore($("#js_conv_wxgsid_" + currentChatUser.phone)[0], $("#conversationContainer")[0].firstChild);
                } else {
                    var div = document.createElement("div");
                    var text = template.render([currentChatUser.phone]);
                    div.innerHTML = text;
                    $("#conversationContainer")[0].insertBefore($(div).find(".chatListColumn")[0], $("#conversationContainer")[0].firstChild);
                }
                $(".listContent").css({
                    top: 0 + "px"
                });
                $(".chatListColumn").attr("class", "chatListColumn");
                $("#js_conv_wxgsid_" + currentChatUser.phone).attr("class", "chatListColumn activeColumn");
            });
        }
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").append(js_chatmessagetemplate.render(sendMessage));
        setScrollPosition();
    }


//    alert($(".chatItem[un=item_2070333132]").html());//根据属性值获取对象jQuery对象
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    if (accountObj != undefined) {
        keepQuest();
        pullUsersMessage(accountObj);
    }

    /*$("#conversationContainer>div").click(function () {
     //临时会话好友列表的显示操作
     //        ($(this).attr("class", "chatListColumn"));
     });*/

    $(document).on("click", "#conversationContainer>div", function () {
        $("#conversationContainer>div").attr("class", "chatListColumn");
        $(this).attr("class", "chatListColumn activeColumn");
        currentChatUser = allCirclesFriends[$(this).attr("username")];
        currentChatType = "POINT";
//                $(".js_rightChatPanel").show();
        showUserChatMessages(currentChatUser);
        if (currentChatUser.head != "") {
            $(".js_js_onlyfriend_headimg").attr("src", window.globaldata.serverSetting.imageServer + currentChatUser.head);
        } else {
            $(".js_js_onlyfriend_headimg").attr("src", "static/images/face_man.png");
        }
        $(".js_js_onlyfriend_headimg").attr("phone", currentChatUser.phone);
        $(".js_onlyfriend_nickName").html(currentChatUser.nickName);
//                $(".js_onlyfriend_mainBusiness").html("主要业务: " + currentChatUser.mainBusiness);
        $(".js_rightChatPanel").show();
        $(".chat_one").show();
        $(".chat_group").hide();
        $(this).find(".unreadDot").html("0");
        $(this).find(".unreadDot").hide();
//        alert($(this).attr("id"));
    });
    //add_frend_chat
    //邀请好友加入聊天的窗口拖动注册
    new Drag($(".js_invite_SelectUserChat_frame")[0]);

    $(document).on("click", ".js_js_onlyfriend_headimg", function () {
        var accountData = allCirclesFriends[$(this).attr("phone")];
        $(".js_findFriendPhone").val("");
        $(".js_findFriendErrorMessage").html("");
        $(".js_addFriendErrorMessage").html("");
        $(".js_friendMessage_addFriend").attr("circle_rid", $(".js_findFriendBtn").attr("circle_rid"));
        $(".js_friendMessage_addFriend").attr("phone", accountData.phone);
        var mainBusiness = accountData.mainBusiness;
        $(".js_findFriend").css({display: "none"});
        $(".js_friendMessage").css({display: "block"});
        new Drag($(".js_friendMessage")[0]);
        $(".js_friendMessage_nickName").html("昵&nbsp;&nbsp;&nbsp;称：" + accountData.nickName);
        $(".js_friendMessage_phone").html("手机号：" + accountData.phone);
        $(".js_friendMessage_mainBusiness").html(mainBusiness.substr(0, 72) + "...");
        $(".js_friendMessage_mainBusiness").attr("title", mainBusiness);
        $(".js_addFriendTitle").html("添加好友 (" + accountData.phone + ")");
        $(".js_addFriendNickName").html("昵&nbsp;&nbsp;&nbsp;称：" + accountData.nickName);
        if (accountData.head != "" && accountData.head != null) {
            $(".js_friendMessage_img").attr("src", imageServer + accountData.head);
        } else {
            $(".js_friendMessage_img").attr("src", "/static/images/face_man.png");
        }
    });
    //初始化表情的Title显示
    var smail = $(".smile_box");
    var smailWXGSFace = ["微笑", "撇嘴", "色", "发呆", "得意", "流泪", "害羞", "闭嘴", "睡", "大哭", "尴尬", "发怒", "调皮", "呲牙", "惊讶", "难过", "酷", "冷汗", "抓狂", "吐", "偷笑", "可爱", "白眼", "傲慢", "饥饿", "困", "惊恐", "流汗", "憨笑", "大兵", "奋斗", "咒骂", "疑问", "嘘", "晕", "折磨", "衰", "骷髅", "敲打", "再见", "擦汗", "抠鼻", "鼓掌", "糗大了", "坏笑", "左哼哼", "右哼哼", "哈欠", "鄙视", "委屈", "快哭了", "阴险", "亲亲", "吓", "可怜", "菜刀", "西瓜", "啤酒", "篮球", "乒乓", "咖啡", "饭", "猪头", "玫瑰", "凋谢", "示爱", "爱心", "心碎", "蛋糕", "闪电", "炸弹", "刀", "足球", "瓢虫", "便便", "月亮", "太阳", "礼物", "拥抱", "强", "弱", "握手", "胜利", "抱拳", "勾引", "拳头", "差劲", "爱你", "NO", "OK", "爱情", "飞吻", "跳跳", "发抖", "怄火", "转圈", "磕头", "回头", "跳绳", "挥手", "激动", "街舞", "献吻", "左太极", "右太极"];
    for (var i = 0; i < 105; i++) {
        var dom = document.createElement("a");
        dom.title = smailWXGSFace[i];
        dom.name = i;
        smail[0].appendChild(dom);
    }
    $(document).on("click", ".smile_box>a", function () {
        var face = "[" + $(this).attr("title") + "]";
        var obj = document.getElementById("textInput");
        obj.value = (obj.value.substr(0, pos) + face + obj.value.substr(pos, obj.value.length));
        $(".js_wxgsFacePanel").hide();
        pos += face.length;
        setCaret();
    });
    $(".js_checkFace").click(function () {
        if ($(".js_wxgsFacePanel").css("display") == "block") {
            $(".js_wxgsFacePanel").hide();
        } else {
            $(".js_wxgsFacePanel").show();
        }
    });
});
var pos = -1;
function setCaret() {
    var oTxt1 = document.getElementById("textInput");
    var cursurPosition = -1;
    if (oTxt1.selectionStart) {//非IE浏览器
        oTxt1.selectionStart = pos;
    } else {//IE
        oTxt1.focus();
        /*var range = oTxt1.createTextRange();//创建出现bug
         range.move("character", pos);
         range.select();*/
    }
}
function getPosition(obj) {
    try {
        var result = 0;
        if (obj.selectionStart) { //IE以外
            console.log(event);
            result = obj.selectionStart
        } else { //IE
            var rng;
            console.log((obj.tagName).toLowerCase() == "textarea");
            if ((obj.tagName).toLowerCase() == "textarea") { //TEXTAREA
                console.log(event);
                rng = event.srcElement.createTextRange();
                rng.moveToPoint(event.x, event.y);
            } else { //Text
                rng = document.selection.createRange();
            }
            rng.moveStart("character", -event.srcElement.value.length);
            result = rng.text.length;
        }
    } catch (e) {
        return 0;
    }
    return result;
}
function getValue(obj) {
    if (obj.value) {
        pos = getPosition(obj);
        console.log(pos);
//        alert(obj.value.substr(0, pos) + "[输入内容]" + obj.value.substr(pos, obj.value.length));
    }
}
var WXGSFaceMap = {
    "微笑": "0", "撇嘴": "1", "色": "2", "发呆": "3", "得意": "4", "流泪": "5", "害羞": "6", "闭嘴": "7", "睡": "8", "大哭": "9",
    "尴尬": "10", "发怒": "11", "调皮": "12", "呲牙": "13", "惊讶": "14", "难过": "15", "酷": "16", "冷汗": "17", "抓狂": "18", "吐": "19",
    "偷笑": "20", "可爱": "21", "愉快": "21", "白眼": "22", "傲慢": "23", "饥饿": "24", "困": "25", "惊恐": "26", "流汗": "27", "憨笑": "28",
    "悠闲": "29", "大兵": "29", "奋斗": "30", "咒骂": "31", "疑问": "32", "嘘": "33", "晕": "34", "疯了": "35", "折磨": "35", "衰": "36",
    "骷髅": "37", "敲打": "38", "再见": "39", "擦汗": "40", "抠鼻": "41", "鼓掌": "42", "糗大了": "43", "坏笑": "44", "左哼哼": "45", "右哼哼": "46",
    "哈欠": "47", "鄙视": "48", "委屈": "49", "快哭了": "50", "阴险": "51", "亲亲": "52", "吓": "53", "可怜": "54", "菜刀": "55", "西瓜": "56",
    "啤酒": "57",
    "篮球": "58",
    "乒乓": "59",
    "咖啡": "60",
    "饭": "61",
    "猪头": "62",
    "玫瑰": "63",
    "凋谢": "64",
    "嘴唇": "65",
    "示爱": "65",
    "爱心": "66",
    "心碎": "67",
    "蛋糕": "68",
    "闪电": "69",
    "炸弹": "70",
    "刀": "71",
    "足球": "72",
    "瓢虫": "73",
    "便便": "74",
    "月亮": "75",
    "太阳": "76",
    "礼物": "77",
    "拥抱": "78",
    "强": "79",
    "弱": "80",
    "握手": "81",
    "胜利": "82",
    "抱拳": "83",
    "勾引": "84",
    "拳头": "85",
    "差劲": "86",
    "爱你": "87",
    "NO": "88",
    "OK": "89",
    "爱情": "90",
    "飞吻": "91",
    "跳跳": "92",
    "发抖": "93",
    "怄火": "94",
    "转圈": "95",
    "磕头": "96",
    "回头": "97",
    "跳绳": "98",
    "挥手": "99",
    "激动": "100",
    "街舞": "101",
    "献吻": "102",
    "左太极": "103",
    "右太极": "104",


    "Smile": "0",
    "Grimace": "1",
    "Drool": "2",
    "Scowl": "3",
    "Chill": "4",
    "Sob": "5",
    "Shy": "6",
    "Shutup": "7",
    "Silent": "7",
    "Sleep": "8",
    "Cry": "9",
    "Awkward": "10",
    "Pout": "11",
    "Angry": "11",
    "Wink": "12",
    "Tongue": "12",
    "Grin": "13",
    "Surprised": "14",
    "Surprise": "14",
    "Frown": "15",
    "Cool": "16",
    "Ruthless": "16",
    "Tension": "17",
    "Blush": "17",
    "Scream": "18",
    "Crazy": "18",
    "Puke": "19",
    "Chuckle": "20",
    "Joyful": "21",
    "Slight": "22",
    "Smug": "23",
    "Hungry": "24",
    "Drowsy": "25",
    "Panic": "26",
    "Sweat": "27",
    "Laugh": "28",
    "Loafer": "29",
    "Commando": "29",
    "Strive": "30",
    "Determined": "30",
    "Scold": "31",
    "Doubt": "32",
    "Shocked": "32",
    "Shhh": "33",
    "Dizzy": "34",
    //"Crazy" : "35",
    "Tormented": "35",
    "BadLuck": "36",
    "Toasted": "36",
    "Skull": "37",
    "Hammer": "38",
    "Wave": "39",
    "Relief": "40",
    "Speechless": "40",
    "DigNose": "41",
    "NosePick": "41",
    "Clap": "42",
    "Shame": "43",
    "Trick": "44",
    "Bah！L": "45",
    "Bah！R": "46",
    "Yawn": "47",
    "Lookdown": "48",
    "Pooh-pooh": "48",
    "Wronged": "49",
    "Shrunken": "49",
    "Puling": "50",
    "TearingUp": "50",
    "Sly": "51",
    "Kiss": "52",
    "Uh-oh": "53",
    "Wrath": "53",
    "Whimper": "54",
    "Cleaver": "55",
    "Melon": "56",
    "Watermelon": "56",
    "Beer": "57",
    "Basketball": "58",
    "PingPong": "59",
    "Coffee": "60",
    "Rice": "61",
    "Pig": "62",
    "Rose": "63",
    "Wilt": "64",
    "Lip": "65",
    "Heart": "66",
    "BrokenHeart": "67",
    "Cake": "68",
    "Lightning": "69",
    "Bomb": "70",
    "Dagger": "71",
    "Soccer": "72",
    "Ladybug": "73",
    "Poop": "74",
    "Moon": "75",
    "Sun": "76",
    "Gift": "77",
    "Hug": "78",
    "Strong": "79",
    "ThumbsUp": "79",
    "Weak": "80",
    "ThumbsDown": "80",
    "Shake": "81",
    "Victory": "82",
    "Peace": "82",
    "Admire": "83",
    "Fight": "83",
    "Beckon": "84",
    "Fist": "85",
    "Pinky": "86",
    "Love": "87",
    "RockOn": "87",
    "No": "88",
    "Nuh-uh": "88",
    "OK": "89",
    "InLove": "90",
    "Blowkiss": "91",
    "Waddle": "92",
    "Tremble": "93",
    "Aaagh!": "94",
    "Twirl": "95",
    "Kotow": "96",
    "Lookback": "97",
    "Dramatic": "97",
    "Jump": "98",
    "JumpRope": "98",
    "Give-in": "99",
    "Surrender": "99",
    "Hooray": "100",
    "HeyHey": "101",
    "Meditate": "101",
    "Smooch": "102",
    "TaiJi L": "103",
    "TaiChi L": "103",
    "TaiJi R": "104",
    "TaiChi R": "104",

    "微笑": "0",
    "撇嘴": "1",
    "色": "2",
    "發呆": "3",
    "得意": "4",
    "流淚": "5",
    "害羞": "6",
    "閉嘴": "7",
    "睡": "8",
    "大哭": "9",
    "尷尬": "10",
    "發怒": "11",
    "調皮": "12",
    "呲牙": "13",
    "驚訝": "14",
    "難過": "15",
    "酷": "16",
    "冷汗": "17",
    "抓狂": "18",
    "吐": "19",
    "偷笑": "20",
    "愉快": "21",
    "白眼": "22",
    "傲慢": "23",
    "饑餓": "24",
    "累": "25",
    "驚恐": "26",
    "流汗": "27",
    "憨笑": "28",
    "悠閑": "29",
    "奮鬥": "30",
    "咒罵": "31",
    "疑問": "32",
    "噓": "33",
    "暈": "34",
    "瘋了": "35",
    "衰": "36",
    "骷髏頭": "37",
    "敲打": "38",
    "再見": "39",
    "擦汗": "40",
    "摳鼻": "41",
    "鼓掌": "42",
    "羞辱": "43",
    "壞笑": "44",
    "左哼哼": "45",
    "右哼哼": "46",
    "哈欠": "47",
    "鄙視": "48",
    "委屈": "49",
    "快哭了": "50",
    "陰險": "51",
    "親親": "52",
    "嚇": "53",
    "可憐": "54",
    "菜刀": "55",
    "西瓜": "56",
    "啤酒": "57",
    "籃球": "58",
    "乒乓": "59",
    "咖啡": "60",
    "飯": "61",
    "豬頭": "62",
    "玫瑰": "63",
    "枯萎": "64",
    "嘴唇": "65",
    "愛心": "66",
    "心碎": "67",
    "蛋糕": "68",
    "閃電": "69",
    "炸彈": "70",
    "刀": "71",
    "足球": "72",
    "甲蟲": "73",
    "便便": "74",
    "月亮": "75",
    "太陽": "76",
    "禮物": "77",
    "擁抱": "78",
    "強": "79",
    "弱": "80",
    "握手": "81",
    "勝利": "82",
    "抱拳": "83",
    "勾引": "84",
    "拳頭": "85",
    "差勁": "86",
    "愛你": "87",
    "愛你": "88",
    "OK": "89",
    "愛情": "90",
    "飛吻": "91",
    "跳跳": "92",
    "發抖": "93",
    "噴火": "94",
    "轉圈": "95",
    "磕頭": "96",
    "回頭": "97",
    "跳繩": "98",
    "投降": "99",
    "激動": "100",
    "亂舞": "101",
    "獻吻": "102",
    "左太極": "103",
    "右太極": "104"};
//分析消息的内容并进行显示
function messageContentTypeSplitShow(type, content) {
    if (type == "text") {
        if (content.indexOf("[") != -1 && content.indexOf("]") != -1) {
            var contents = "";
            var arr = content.split("[");
            for (var index in arr) {
                var str = arr[index];
                if (str.indexOf("]") != -1) {
                    var i = str.indexOf("]");
                    if (i >= 1) {
                        var faceStr = str.substr(0, i);
                        if (WXGSFaceMap[faceStr] != undefined) {
                            contents += "<img src=/static/images/wxgsface/" + WXGSFaceMap[faceStr] + ".png />" + str.substr(i + 1);
                        } else {
                            contents += "smile" + str.substr(i + 1);
                        }
                    } else {
                        contents += "[" + str;
                    }
                } else {
                    if (str == "") {
                        if (index == (arr.length - 1)) {
                            contents += "[" + str;
                        } else {
                            contents += str;
                        }
                    } else if (content.substr(0, 1) == "[") {
                        contents += "[" + str;
                    } else if (index != 0) {
                        contents += "[" + str;
                    } else {
                        contents += str;
                    }
                }
            }
            return contents;
        }
        return content;
    } else if (type == "image") {
//        var image = new Image();
        return "<img src=" + imageServer + content + ">";
    } else {
        return content + "---" + type;
    }
}
//显示临时聊天用户的详细信息
function showTempChatUsersInfo() {
    getTemplateHtml("tempChatUserInfo", function (template) {
        var tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatUsersList"));
        if (tempChatUsersList != null) {
            $("#conversationContainer").html(template.render(tempChatUsersList.reverse()));
        }
    });
}
//修改临时聊天用户的显示顺序
function modifyTempChatUser(phone) {
//    var tempChatUsers = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatUsers"));
//    var tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgss_tempChatUsersList"));
    if (tempChatUsers == null) {
        tempChatUsers = {};
        tempChatUsersList = [];
    }
    var account = allCirclesFriends[phone];
//    console.log(allCirclesFriends);
    if (tempChatUsers[phone] == undefined) {
        tempChatUsers[phone] = "chat";
        tempChatUsersList.push(phone);
        window.sessionStorage.setItem("wxgs_tempChatUsers", JSON.stringify(tempChatUsers));
        window.sessionStorage.setItem("wxgs_tempChatUsersList", JSON.stringify(tempChatUsersList));
    } else {
        for (var i = 0; i < tempChatUsersList.length; i++) {
            var accountItem = tempChatUsersList[i];
            if (accountItem == phone) {
                tempChatUsersList.splice(i, 1);
                tempChatUsersList.push(phone);
                window.sessionStorage.setItem("wxgs_tempChatUsers", JSON.stringify(tempChatUsers));
                window.sessionStorage.setItem("wxgs_tempChatUsersList", JSON.stringify(tempChatUsersList));
                break;
            }
        }
    }
}
//显示当前的聊天消息记录
function showUserChatMessages(account) {
    var baseString = (currentChatType.substr(0, 1)).toLowerCase();
    var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    var messages = wxgs_tempAccountChatMessages[baseString + "_" + account.phone];
    if (messages != undefined) {
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").html(js_chatmessagetemplate.render(wxgs_tempAccountChatMessages[baseString + "_" + account.phone]));
        setScrollPosition();
    } else {
        var htmlStr = '<div class="noMsgIip" id="noMsgTip">' +
            '<div class="noMsgTipPic"></div>' +
            '<p>暂时没有新消息</p>' +
            '</div>';
        $(".js_chatContents").html(htmlStr);
    }
    if ($(".js_chatRightFrame").css("visibility") == "hidden") {
        $(".js_chatRightFrame").css({
            visibility: "visible"
        });
    }
}
//获取聊天记录
function pullUsersMessage(account) {
    getMessages(window.sessionStorage.getItem("wxgs_messageFlag"), function (data) {
        messagesDataSplit(data);
//        console.log(data);
//        console.log(data.messages[0]);
//        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
//        $(".js_chatContents").html(js_chatmessagetemplate.render(data.messages));
//        setScrollPosition();
    });
}
//分析用户聊天消息并进行存储
function messagesDataSplit(datas) {
    var flag = window.sessionStorage.getItem("wxgs_messageFlag");
    if (datas.flag != undefined) {
        var messsageFlag = datas.flag;
        window.sessionStorage.setItem("wxgs_messageFlag", messsageFlag);
    }
    var data = datas.messages;
//    alert(JSON.stringify(data));
//    var tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    var count = 0;
    var phones = {};
    for (var i = 0; i < data.length; i++) {
        count++;
        var message = JSON.parse(data[i]);
        if (flag == "none") {
            messageCountNotice(phones, accountObj, message);
            phones[message.phone] = "message";
        }
        var sendType = message.sendType;
        if (sendType == "point") {
            if (message.phone == accountObj.phone) {
                /*tempAccountChatMessages[message.phone] = tempAccountChatMessages[message.phone] || [];
                 tempAccountChatMessages[message.phone].push(JSON.stringify(message));*/
                if (datas.flag == undefined) {
                    var phoneTo = JSON.parse(message.phoneto);
                    for (var j = 0; j < phoneTo.length; j++) {
                        var phone = phoneTo[j];
                        tempAccountChatMessages["p_" + phone] = tempAccountChatMessages["p_" + phone] || [];
                        tempAccountChatMessages["p_" + phone].push(JSON.stringify(message));
                    }
                }
            } else {
                var phone = message.phone;
                tempAccountChatMessages["p_" + phone] = tempAccountChatMessages["p_" + phone] || [];
                tempAccountChatMessages["p_" + phone].push(JSON.stringify(message));
            }
        } else if (sendType == "tempGroup") {
            if (message.phone == accountObj.phone && datas.flag != undefined) {
                continue;
            }
            if (tempGroupsInfo[message.tempGid] == undefined) {
                getTempGroupInfo(accountObj, message.tempGid, "tempGroup");
            }
            tempAccountChatMessages["t_" + message.tempGid] = tempAccountChatMessages["t_" + message.tempGid] || [];
            tempAccountChatMessages["t_" + message.tempGid].push(JSON.stringify(message));
            $(".js_groupchat_boderbox_template[group_gid=" + message.tempGid + "]").find(".js_groupchat_friends_message_info").html((message.content).substr(0, 10) + "...");
        } else if (sendType == "group") {
            if (message.phone == accountObj.phone && datas.flag != undefined) {
                continue;
            }
            tempAccountChatMessages["g_" + message.gid] = tempAccountChatMessages["g_" + message.gid] || [];
            tempAccountChatMessages["g_" + message.gid].push(JSON.stringify(message));
            $(".js_groupchat_boderbox_template[group_gid=" + message.gid + "]").find(".js_groupchat_friends_message_info").html((message.content).substr(0, 10) + "...");
        } else {
            console.log("请注意,逻辑错误-丢失数据--数据为:-" + message);
        }
    }
    window.sessionStorage.setItem("wxgs_tempAccountChatMessages", JSON.stringify(tempAccountChatMessages));
}
//获取临时群组的详细信息
function getTempGroupInfo(accountObj, tempGid, type) {
    $.ajax({
        type: "POST",
        url: "/api2/group/get?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            gid: tempGid,
            type: type
        },
        success: function (data) {
            if (data["提示信息"] == "获取群组信息成功") {
                if (type == "tempGroup") {
                    var tempGroup = data.group;
                    tempGroupsInfo[tempGroup.tempGid] = tempGroup;
                    window.sessionStorage.setItem("wxgs_tempGroupsInfo", JSON.stringify(tempGroupsInfo));
                    var members = tempGroup.members;
                    var noFriends = [];
                    for (var i = 0; i < members.length; i++) {
                        if (allCirclesFriends[members[i]] == undefined) {
                            noFriends.push(members[i]);
                        }
                    }
                    getTempGroupMembers(accountObj, noFriends);
                    getTemplateHtml("user_groups", function (template) {
                        $(".js_user_groups").append(template.render([tempGroup]));
                        var messageNum = $(".js_groupchat_boderbox_template[group_gid=" + tempGroup.tempGid + "]").find(".groupchat_number");
                        var num = messageNum.find(".groupchat_number_info");
                        num.html(parseInt(num.html()) + 1);
                        messageNum.css({
                            "visibility": "visible"
                        });
                    });
                } else if (type == "group") {
                    var group = data.group;
                    groupsInfo[group.gid] = group;
                    getGroupMembers(accountObj, group.gid);
                }
            } else {
                alert(data["提示信息"] + "," + data["失败原因"]);
            }
        }
    });
}
//获取正式群组的所有群组和用户
function getGroupMembers(accountObj, gid) {
    $.ajax({
        type: "POST",
        url: "/api2/group/getallmembers?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            gid: gid
        },
        success: function (data) {
            if (data["提示信息"] == "获取群组成员成功") {
                var members = data.members;
                var membersPhone = [];
                for (var i = 0; i < members.length; i++) {
                    var member = members[i];
                    membersPhone.push(member.phone);
                    allCirclesFriends[member.phone] = member;
                }
                var group = groupsInfo[gid];
                group.members = membersPhone;
                window.sessionStorage.setItem("wxgs_groupsInfo", JSON.stringify(groupsInfo));
                window.session.setItem("wxgs_allCirclesFriends", JSON.stringify(allCirclesFriends));
            }
        }
    });
}
//获取临时分组的用户信息
function getTempGroupMembers(accountObj, noFriends) {
    if (noFriends.length > 0) {
        $.ajax({
            type: "POST",
            url: "/api2/account/get?",
            data: {
                phone: accountObj.phone,
                accessKey: accountObj.accessKey,
                target: JSON.stringify(noFriends)
            },
            success: function (data) {
                if (data["提示信息"] == "获取用户信息成功") {
                    var members = data.accounts;
                    for (var i = 0; i < members.length; i++) {
                        var member = members[i];
                        allCirclesFriends[member.phone] = member;
                    }
                    window.sessionStorage.setItem("wxgs_allCirclesFriends", JSON.stringify(allCirclesFriends));
                } else {
                    getTempGroupMembers(accountObj, noFriends);
                }
            }
        });
    }
}
//发送消息
function sendMessages(data, showMessage) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $(".js_chatmessagecontent").val("");
    $.ajax({
            type: "POST",
            timeout: 5000,
            url: "/api2/message/send?",
            data: data,
            success: function (data) {
                var time = tempSendMessageTimeStamp.shift();
                if (data["提示信息"] == "发送成功") {
                    showMessage.time = data.time;
                    messagesDataSplit({messages: [JSON.stringify(showMessage)]});
                    setScrollPosition();
//                var message = JSON.stringify(message);
//                message.time = data.time;
//                alert(formattertime(time));
                    $(".js_messageTime_" + time).html(formattertime(data.time));

                } else {
//                js_sendStatus_#{post1.time}
                    $(".js_sendStatus_" + time).find("label").css({
                        "visibility": "visible"
                    });
                    alert(data["提示信息"] + "---" + data["失败原因"]);
                }
            },
            error: function (XMLHttpRequest, error) {
//                XMLHttpRequest.abort();
                console.log(error);
                var time = tempSendMessageTimeStamp.shift();
                $(".js_sendStatus_" + time).find("label").css({
                    "visibility": "visible"
                });
            }
        }
    )
    ;
}
//设置滚动条的位置
function setScrollPosition() {
    if ($(".js_chatContents").height() < $(".chatScorll").height()) {
        $(".scrollDiv").hide();
        $(".js_chatContents").css({
            "top": "0px"
        });
    } else {
        $(".scrollDiv").show();
        var height = (($(".chatScorll").height() / $(".js_chatContents").height()) * $(".chatScorll").height()) - 15;
        $(".scrollDiv").css({
            height: height + "px"
        });
        $(".scrollDiv").css({
            "top": $(".chatScorll").height() - height - 30 + "px"
        });
        $(".js_chatContents").css({
            "top": -($(".js_chatContents").height() - $(".chatScorll").height() + 55) + "px"
        });
        if ($(".js_chatRightFrame").css("visibility") == "hidden") {
            $(".js_chatRightFrame").css({
                visibility: "visible"
            });
        }
    }
//    alert($(".scrollDiv").);
}
//长连接
function keepQuest() {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        timeout: 20000,
        url: "/api2/session/event?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey
        },
        success: function (data) {
            if (data["提示信息"] == "成功") {
                if (data.event == "message") {
                    var message = JSON.parse(data.event_content.message);
                    var phones = {};
                    messageCountNotice(phones, accountObj, message);
                    phones[message.phone] = "message";
//                    keepQuest();
                    getMessages(window.sessionStorage.getItem("wxgs_messageFlag"), messagesDataSplit);
//                    alert("message");
                } else if (data.event == "newfriend") {
                    alert("newFriend");
                } else if (data.event == "friendaccept") {
                    alert("friendAccept");
                } else {
//                    keepQuest();
                    alert("神器的效果");
                }
                keepQuest();
            } else if (data["提示信息"] == "请求失败") {
                if (data["失败原因"] == "AccessKey Invalid") {
                    window.localStorage.clear();
                    window.sessionStorage.clear();
                    location.href = "./index.html";
                } else {
                    keepQuest();
                }
            } else {
                keepQuest();
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
//            xhr.abort();
//            alert(textStatus + "---" + errorThrown);
            if (textStatus == "timeout") {
//                XMLHttpRequest.abort();
                keepQuest();
            }
        }
    });
}
//处理临时聊天页面的用户信息和消息条数的显示
function messageCountNotice(phones, accountObj, message) {
    var sendType = message.sendType;
    if (sendType == "point") {
        function next() {
            if (tempChatUsers[message.phone] != undefined) {
                var target = $("#js_conv_wxgsid_" + message.phone).find(".unreadDot");
                var messageAccount = parseInt(target.html());
                target.html(messageAccount + 1);
                target.show();
            } else {
                if (phones[message.phone] == undefined) {
                    getTemplateHtml("tempChatUserInfo", function (template) {
                        $("#conversationContainer").append(template.render([message.phone]));
                        var target = $("#js_conv_wxgsid_" + message.phone).find(".unreadDot");
                        var messageAccount = parseInt(target.html());
                        target.html(messageAccount + 1);
                        target.show();
                        tempChatUsers[message.phone] = "chat";
                        tempChatUsersList.push(message.phone);
                        window.sessionStorage.setItem("wxgs_tempChatUsers", JSON.stringify(tempChatUsers));
                        window.sessionStorage.setItem("wxgs_tempChatUsersList", JSON.stringify(tempChatUsersList));
                    });
                }
                /*var target = $("#js_conv_wxgsid_" + message.phone).find(".unreadDot");
                 var messageAccount = parseInt(target.html());
                 target.html(messageAccount + 1);
                 target.show();*/
            }
        }

        if (currentChatType == "POINT") {
            if (currentChatUser.phone == message.phone) {
                var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
                $(".js_chatContents").append(js_chatmessagetemplate.render([JSON.stringify(message)]));
                setScrollPosition();
            } else {
                //---------------------------------------------------------------------------------------
                next();
            }
        } else {
            next();
        }
    } else if (sendType == "tempGroup") {
        function next2() {
            if (tempGroupsInfo[message.tempGid] != undefined) {
                var messageAccountObj = $(".js_groupchat_boderbox_template[group_gid=" + message.tempGid + "]").find(".groupchat_number");
                var num = messageAccountObj.find(".groupchat_number_info");
                num.html(parseInt(num.html()) + 1);
                messageAccountObj.css({
                    "visibility": "visible"
                });
            } else {
                getTempGroupInfo(accountObj, message.tempGid, "tempGroup");
            }
        }

        if (currentChatType == "TEMPGROUP") {
            if (currentChatGroup.tempGid == message.tempGid) {
                var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
                $(".js_chatContents").append(js_chatmessagetemplate.render([JSON.stringify(message)]));
                setScrollPosition();
            } else {
                next2();
            }
        } else {
            next2();
        }
    } else if (sendType == "group") {
        function next3() {
            if (groupsInfo[message.gid] != undefined) {
                var messageAccountObj = $(".js_groupchat_boderbox_template[group_gid=" + message.gid + "]").find(".groupchat_number");
                var num = messageAccountObj.find(".groupchat_number_info");
                num.html(parseInt(num.html()) + 1);
                messageAccountObj.css({
                    "visibility": "visible"
                });
            } else {
                getTempGroupInfo(accountObj, message.gid, "group");
            }
        }

        if (currentChatType == "GROUP") {
            if (currentChatGroup.gid == message.gid) {
                var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
                $(".js_chatContents").append(js_chatmessagetemplate.render([JSON.stringify(message)]));
                setScrollPosition();
            } else {
                next3();
            }
        } else {
            next3();
        }
    }
}
//获取服务器上的聊天信息
function getMessages(flag, next) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        url: "/api2/message/get?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            flag: flag
        },
        success: function (data) {
            if (data["提示信息"] == "获取成功") {
                next(data);
            } else {
                alert(data["提示信息"] + "," + data["失败原因"]);
            }
        }
    });
}
//格式化时间的格式
function formattertime(millisecond) {
    var date = new Date(millisecond);
    var Hours = date.getHours().toString().length == 1 ? "0" + date.getHours() : date.getHours();
    var Minutes = date.getMinutes().toString().length == 1 ? "0" + date.getMinutes() : date.getMinutes();
    var Seconds = date.getSeconds().toString().length == 1 ? "0" + date.getSeconds() : date.getSeconds();
    var formattertime = Hours + ":" + Minutes;
    return formattertime;
}
//nTenjin模版的使用
function getTemplate(id) {
    var tenjin = nTenjin;
    var templateDiv = $('.templates #' + id).parent();
    var string = templateDiv.html();
    string = string.replace(/\<\!\-\-\?/g, "<?");
    string = string.replace(/\?\-\-\>/g, "?>");
    string = string.replace(/比较符号大于/g, ">");
    string = string.replace(/比较符号兄小于/g, "<");
    var template = new tenjin.Template();
    template.convert(string);
    return template;
}