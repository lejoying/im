var scrollInitFlag = false;
var tempSendMessageTimeStamp = [];
var inviteSelectGroupID = -1;
var inviteSelectedUsers = {};

var currentChatType = "";
var currentChatGroup = {};

$(function () {

    getTemplateHtml("tempChatUserInfo", function (template) {
        var tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgss_tempChatUsersList"));
        if (tempChatUsersList != null) {
            $("#conversationContainer").html(template.render(tempChatUsersList.reverse()));
        }
    });

    $(".js_chatRightFrame").css({
        visibility: "visible"
    });
    /*$(".js_morefriend").hide();
     $(".js_morefriend").slideUp();
     $(".js_onlyfriend").slideUp(1);
     $(".js_onlyfriend").slideDown(100, function () {
     });*/

    $(document).on("click", ".js_chatsendmessage", function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var message = $(".js_chatmessagecontent").val();
        if (message.trim() == "") {
            alert("不能发送空白信息");
            return;
        }
        getTemplateHtml("tempChatUserInfo", function (template) {
            var div = document.createElement("div");
            var text = template.render([allCirclesFriends[currentChatUser.phone]]);
            div.innerHTML = text;
            $("#conversationContainer")[0].insertBefore($(div).find(".chatListColumn")[0], $("#conversationContainer")[0].firstChild);
            $(".listContent").css({
                top: 0 + "px"
            });
            $(".chatListColumn").attr("class", "chatListColumn");
            $("#js_conv_wxgsid_" + currentChatUser.phone).attr("class", "chatListColumn activeColumn");
        });
        modifyTempChatUser(currentChatUser.phone);
        var listPhone = [];
        listPhone.push(currentChatUser.phone);
        var messageObj = {
            type: "text",
            content: message
        };
        var time = new Date().getTime()
        tempSendMessageTimeStamp.push(time);
        var sendMessage = [
            JSON.stringify({type: "text", time: time, phone: accountObj.phone, phoneto: JSON.stringify(listPhone), content: message})
        ];
        var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
        if (wxgs_tempAccountChatMessages[currentChatUser.phone] == undefined) {
            $(".js_chatContents").html("");
        }
        messagesDataSplit({messages: [sendMessage]});
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").append(js_chatmessagetemplate.render(sendMessage));

        sendMessages(JSON.stringify(listPhone), JSON.stringify(messageObj));
    });


//    alert($(".chatItem[un=item_2070333132]").html());//根据属性值获取对象jQuery对象
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    if (accountObj != undefined) {
        keepQuest();
        pullUsersMessage(accountObj);
    }

    $("#conversationContainer>div").click(function () {
        //临时会话好友列表的显示操作
//        ($(this).attr("class", "chatListColumn"));
    });

    $(".js_chat_one_addfrriends").click(function () {
        $(".js_invite_SelectUserChat_frame").show();
        getTemplateHtml("invite_circles_friends", function (template) {
            var circles = JSON.parse(window.sessionStorage.getItem("wxgs_circles"));
            $(".js_invite_friends_chat_frame").html(template.render(circles));
        });
    });
    $(".js_add_friend_chat_complete").click(function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        $(".js_invite_SelectUserChat_frame").hide();
        $(".js_add_friend_chat_pop").hide();
        $(".js_already_invite_select_friends").html("");
        var members = [];
        for (var index in inviteSelectedUsers) {
            members.push(index);
        }
        if (members.length > 0) {
            $.ajax({
                type: "POST",
                url: "/api2/group/create?",
                data: {
                    phone: accountObj.phone,
                    accessKey: accountObj.accessKey,
                    type: "createTempGroup",
                    name: "",
                    members: JSON.stringify(members)
                },
                success: function (data) {
                    alert(data["提示信息"]);
                }
            });
        }
        inviteSelectGroupID = -1;
        inviteSelectedUsers = {};
    });
    $(document).on("click", ".js_add_friend_chat_group", function () {
        //js_invite_circles_friends
        var index = $(this).attr("index");
        var circle_id = $(this).attr("circle_id");
        if (inviteSelectGroupID != circle_id) {
            inviteSelectGroupID = circle_id;
            if (circle_id == "undefined") {
                circle_id = undefined;
            }
            $(".js_add_friend_chat_pop").show();
            $(".js_add_friend_chat_sild").css({
                left: 45 + (index % 3) * 80 + "px"
            });
            $(".js_invite_circles_friends").css({
                "margin-top": -(Math.floor(index / 3)) * 85 + "px"
            });
            getTemplateHtml("invite_circles_friends_item", function (template) {
                var circles = JSON.parse(window.sessionStorage.getItem("wxgs_circles"));
                for (var index in circles) {
                    var circle = circles[index];
                    if (JSON.stringify(circle.rid) == circle_id) {
                        $(".js_add_friend_chat_pop_frends").html(template.render(circle));
                        break;
                    }
                }
            });
        } else {
            inviteSelectGroupID = -1;
            $(".js_add_friend_chat_pop").hide();
            $(".js_invite_circles_friends").css({
                "margin-top": "0px"
            });
        }
    });
    $(document).on("click", ".js_invite_friend_chat_icon", function () {
        var icon = $(this).find("img");
        var phone = $(this).attr("phone");
        if (icon.hasClass("js_invite_chat_icon")) {
            delete inviteSelectedUsers[phone];
            icon.removeClass("js_invite_chat_icon");
            icon.addClass("js_no_invite_chat_icon");
            var targetObj = $(".js_already_invite_selected_user_" + phone)[0];
            targetObj.parentNode.removeChild(targetObj);
            if ($(".js_already_invite_select_friends .add_frend_chat_checkedfrend").length >= 3) {
                $(".js_already_invite_select_friends").css({
                    width: parseInt(($(".js_already_invite_select_friends").css("width")).replace("px", "")) - 60 + "px"
                });
            }
        } else {
            icon.addClass("js_invite_chat_icon");
            inviteSelectedUsers[phone] = "select";
            if (icon.hasClass("js_no_invite_chat_icon")) {
                icon.removeClass("js_no_invite_chat_icon");
            }
            getTemplateHtml("already_invite_circles_friends", function (template) {
                $(".js_already_invite_select_friends").append(template.render([allCirclesFriends[phone]]));
                if ($(".js_already_invite_select_friends .add_frend_chat_checkedfrend").length >= 3) {
                    $(".js_already_invite_select_friends").css({
                        width: parseInt(($(".js_already_invite_select_friends").css("width")).replace("px", "")) + 60 + "px"
                    });
                }
            });
        }
    });
    $(document).on("click", ".js_invite_users", function () {
        var phone = $(this).attr("phone");
        var targetObj = $(".js_invite_friend_chat_icon[phone=" + phone + "]");
        if (targetObj.attr("class") != undefined) {
            var img = targetObj.find("img");
            if (img.hasClass("js_invite_chat_icon")) {
                img.removeClass("js_invite_chat_icon");
                img.addClass("js_no_invite_chat_icon");
            }
        }
        delete inviteSelectedUsers[phone];
        var invite_user = $(".js_already_invite_selected_user_" + phone)[0];
        invite_user.parentNode.removeChild(invite_user);
        if ($(".js_already_invite_select_friends .add_frend_chat_checkedfrend").length >= 3) {
            $(".js_already_invite_select_friends").css({
                width: parseInt(($(".js_already_invite_select_friends").css("width")).replace("px", "")) - 60 + "px"
            });
        }
    });
    $(document).on("mouseover", ".js_already_invite_select_friends", function () {
        ($(this).find(".js_add_friend_chat_checked_headimg")).css({
            visibility: "visible"
        });
    });
    $(document).on("mouseout", ".js_already_invite_select_friends", function () {
        ($(this).find(".js_add_friend_chat_checked_headimg")).css({
            visibility: "hidden"
        });
    });
    $(document).on("click", ".js_groupchat_boderbox_template", function () {
        alert("js_groupchat_boderbox_template");
    });

    $(document).on("click", ".js_add_friend_chat_lefticon", function () {
        if ($(".js_already_invite_select_friends .add_frend_chat_checkedfrend").length >= 3) {
            var width = parseInt(($(".js_already_invite_select_friends").css("width")).replace("px", ""));
            var marginLeft = parseInt(($(".js_already_invite_select_friends").css("marginLeft")).replace("px", ""));
            var maxMarginLeft = width - 180;
            if (marginLeft + 60 <= 0) {
                $(".js_already_invite_select_friends").css({
                    "margin-left": (marginLeft + 60) + "px"
                });
            }
        } else {
            $(".js_already_invite_select_friends").css({
                "margin-left": 0 + "px"
            });
        }
    });
    $(document).on("click", ".js_add_friend_chat_righticon", function () {
        if ($(".js_already_invite_select_friends .add_frend_chat_checkedfrend").length >= 3) {
            var width = parseInt(($(".js_already_invite_select_friends").css("width")).replace("px", ""));
            var marginLeft = parseInt(($(".js_already_invite_select_friends").css("marginLeft")).replace("px", ""));
            var maxMarginLeft = width - 180;
            if (marginLeft - 60 > -maxMarginLeft) {
                $(".js_already_invite_select_friends").css({
                    "margin-left": marginLeft - 60 + "px"
                });
            }
        } else {
            $(".js_already_invite_select_friends").css({
                "margin-left": 0 + "px"
            });
        }
    });


    $(document).on("click", "#conversationContainer>div", function () {
        $("#conversationContainer>div").attr("class", "chatListColumn");
        $(this).attr("class", "chatListColumn activeColumn");
        currentChatUser = allCirclesFriends[$(this).attr("username")];
//                $(".js_rightChatPanel").show();
        showUserChatMessages(currentChatUser);
        if (currentChatUser.head != "") {
            $(".js_js_onlyfriend_headimg").attr("src", window.globaldata.serverSetting.imageServer + currentChatUser.head);
        } else {
            $(".js_js_onlyfriend_headimg").attr("src", "static/images/face_man.png");
        }
        $(".js_onlyfriend_nickName").html(currentChatUser.nickName);
//                $(".js_onlyfriend_mainBusiness").html("主要业务: " + currentChatUser.mainBusiness);
        $(".js_rightChatPanel").show();
//        alert($(this).attr("id"));
    });
    //add_frend_chat
    //邀请好友加入聊天的窗口拖动注册
    new Drag($(".js_invite_SelectUserChat_frame")[0]);


});
function modifyTempChatUser(phone) {
    var tempChatUsers = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatUsers"));
    var tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgss_tempChatUsersList"));
    if (tempChatUsers == null) {
        tempChatUsers = {};
        tempChatUsersList = [];
    }
    var account = allCirclesFriends[phone];
    console.log(allCirclesFriends);
    if (tempChatUsers[phone] == undefined) {
        tempChatUsers[phone] = account;
        tempChatUsersList.push(account);
        window.sessionStorage.setItem("wxgs_tempChatUsers", JSON.stringify(tempChatUsers));
        window.sessionStorage.setItem("wxgss_tempChatUsersList", JSON.stringify(tempChatUsersList));
    } else {
        for (var i = 0; i < tempChatUsersList.length; i++) {
            var accountItem = tempChatUsersList[i];
            if (accountItem.phone == phone) {
                tempChatUsersList.splice(i, 1);
                tempChatUsersList.push(account);
                window.sessionStorage.setItem("wxgs_tempChatUsers", JSON.stringify(tempChatUsers));
                window.sessionStorage.setItem("wxgss_tempChatUsersList", JSON.stringify(tempChatUsersList));
                break;
            }
        }
    }
}
function showUserChatMessages(account) {
    var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    var messages = wxgs_tempAccountChatMessages[account.phone];
    if (messages != undefined) {
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").html(js_chatmessagetemplate.render(wxgs_tempAccountChatMessages[account.phone]));
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

function messagesDataSplit(data) {
    if (data.flag != undefined) {
        var messsageFlag = data.flag;
        window.sessionStorage.setItem("wxgs_messageFlag", messsageFlag);
    }
    var data = data.messages;
//    alert(JSON.stringify(data));
    var tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    for (var i = 0; i < data.length; i++) {
        var message = JSON.parse(data[i]);
        if (message.phone == accountObj.phone) {
            /*tempAccountChatMessages[message.phone] = tempAccountChatMessages[message.phone] || [];
             tempAccountChatMessages[message.phone].push(JSON.stringify(message));*/
            var phoneTo = JSON.parse(message.phoneto);
            for (var j = 0; j < phoneTo.length; j++) {
                var phone = phoneTo[j];
                if (tempAccountChatMessages[phone] != undefined) {
                    tempAccountChatMessages[phone].push(JSON.stringify(message));
                } else {
                    var array = [];
                    array.push(JSON.stringify(message));
                    tempAccountChatMessages[phone] = array;
                }
            }
        } else {
//            var phoneTo = JSON.parse(message.phoneto);
            if (tempAccountChatMessages[message.phone] != undefined) {
                tempAccountChatMessages[message.phone].push(JSON.stringify(message));
            } else {
                var array = [];
                array.push(JSON.stringify(message));
                tempAccountChatMessages[message.phoneTo] = array;
            }
        }
//        alert(JSON.stringify(tempAccountChatMessages));
    }
    window.sessionStorage.setItem("wxgs_tempAccountChatMessages", JSON.stringify(tempAccountChatMessages));
}
function sendMessages(listPhone, message) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $(".js_chatmessagecontent").val("");
    $.ajax({
            type: "POST",
            timeout: 5000,
            url: "/api2/message/send?",
            data: {
                phone: accountObj.phone,
                accessKey: accountObj.accessKey,
                phoneto: listPhone,
                message: message
            },
            success: function (data) {
                var time = tempSendMessageTimeStamp.shift();
                if (data["提示信息"] == "发送成功") {
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
                XMLHttpRequest.abort();
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

function setScrollPosition() {
    if ($(".js_chatContents").height() < $(".chatScorll").height()) {
        $(".scrollDiv").hide();
    } else {
        $(".scrollDiv").show();
    }
//    alert($(".scrollDiv").);
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

function keepQuest() {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "GET",
        timeout: 20000,
        url: "/api2/session/event?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey
        },
        success: function (data) {
            if (data["提示信息"] == "成功") {
                if (data.event == "message") {
                    var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
                    $(".js_chatContents").append(js_chatmessagetemplate.render(data.event_content.message));
                    setScrollPosition();
                    keepQuest();
                    getMessages(window.sessionStorage.getItem("wxgs_messageFlag"), messagesDataSplit);
//                    alert("message");
                } else if (data.event == "newfriend") {
                    alert("newFriend");
                } else if (data.event == "friendaccept") {
                    alert("friendAccept");
                } else {
                    keepQuest();
                    alert("神器的效果");
                }
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
        error: function (xhr, error) {
            xhr.abort();
            keepQuest();
        }
    });
}

function getMessages(flag, next) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "GET",
        url: "/api2/message/get?",
        data: {
            phone: "121",
            accessKey: accountObj.accessKey,
            flag: flag
        },
        success: function (data) {
            next(data);
        }
    });
}

function formattertime(millisecond) {
    var date = new Date(millisecond);
    var Hours = date.getHours().toString().length == 1 ? "0" + date.getHours() : date.getHours();
    var Minutes = date.getMinutes().toString().length == 1 ? "0" + date.getMinutes() : date.getMinutes();
    var Seconds = date.getSeconds().toString().length == 1 ? "0" + date.getSeconds() : date.getSeconds();
    var formattertime = Hours + ":" + Minutes;
    return formattertime;
}

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