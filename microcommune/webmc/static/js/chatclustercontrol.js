var scrollInitFlag = false;
var tempSendMessageTimeStamp = [];
var tempAccountChatMessages = {}
$(function () {
    var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    if (wxgs_tempAccountChatMessages != null) {
        tempAccountChatMessages = wxgs_tempAccountChatMessages;
    }
    $(".js_chatRightFrame").css({
        visibility: "visible"
    });
    /*$(".js_morefriend").hide();
     $(".js_morefriend").slideUp();
     $(".js_onlyfriend").slideUp(1);
     $(".js_onlyfriend").slideDown(100, function () {
     });*/

    $(document).on("click", ".js_chatsendmessage", function () {
        alert(currentChatType);
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var message = $(".js_chatmessagecontent").val();
        if (message.trim() == "") {
            alert("不能发送空白信息");
            return;
        }
        modifyTempChatUser(currentChatUser.phone);
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
        sendMessages(data);

        var sendMessage = [
            JSON.stringify(showMessage)
        ];
        var wxgs_tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
        var baseString = (currentChatType.substr(0, 1)).toLowerCase();
        if (wxgs_tempAccountChatMessages[baseString + "_" + currentChatUser.phone] == undefined) {
            ($(".js_chatContents").find(".noMsgIip")).html("");
        }
        messagesDataSplit({messages: [sendMessage]});
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
            var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
            $(".js_chatContents").append(js_chatmessagetemplate.render(sendMessage));
        }
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
        currentChatType = "POINT";
        $(".chat_one").show();
        $(".chat_group").hide();
        $(this).find(".unreadDot").html("0");
//        alert($(this).attr("id"));
    });
    //add_frend_chat
    //邀请好友加入聊天的窗口拖动注册
    new Drag($(".js_invite_SelectUserChat_frame")[0]);


});
function showTempChatUsersInfo() {
    getTemplateHtml("tempChatUserInfo", function (template) {
        var tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgss_tempChatUsersList"));
        if (tempChatUsersList != null) {
            $("#conversationContainer").html(template.render(tempChatUsersList.reverse()));
        }
    });
}
function modifyTempChatUser(phone) {
    var tempChatUsers = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatUsers"));
    var tempChatUsersList = JSON.parse(window.sessionStorage.getItem("wxgss_tempChatUsersList"));
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
        window.sessionStorage.setItem("wxgss_tempChatUsersList", JSON.stringify(tempChatUsersList));
    } else {
        for (var i = 0; i < tempChatUsersList.length; i++) {
            var accountItem = tempChatUsersList[i];
            if (accountItem == phone) {
                tempChatUsersList.splice(i, 1);
                tempChatUsersList.push(phone);
                window.sessionStorage.setItem("wxgs_tempChatUsers", JSON.stringify(tempChatUsers));
                window.sessionStorage.setItem("wxgss_tempChatUsersList", JSON.stringify(tempChatUsersList));
                break;
            }
        }
    }
}
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

function messagesDataSplit(datas) {
    if (datas.flag != undefined) {
        var messsageFlag = datas.flag;
        window.sessionStorage.setItem("wxgs_messageFlag", messsageFlag);
    }
    var data = datas.messages;
//    alert(JSON.stringify(data));
//    var tempAccountChatMessages = JSON.parse(window.sessionStorage.getItem("wxgs_tempAccountChatMessages"));
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    var count = 0;
    for (var i = 0; i < data.length; i++) {
        count++;
        var message = JSON.parse(data[i]);
        var sendType = message.sendType;
        if (sendType == "point") {
            if (message.phone == accountObj.phone) {
                /*tempAccountChatMessages[message.phone] = tempAccountChatMessages[message.phone] || [];
                 tempAccountChatMessages[message.phone].push(JSON.stringify(message));*/
                var phoneTo = JSON.parse(message.phoneto);
                for (var j = 0; j < phoneTo.length; j++) {
                    var phone = phoneTo[j];
                    tempAccountChatMessages["p_" + phone] = tempAccountChatMessages["p_" + phone] || [];
                    tempAccountChatMessages["p_" + phone].push(JSON.stringify(message));
                }
            } else {
                tempAccountChatMessages["p_" + phone] = tempAccountChatMessages["p_" + phone] || [];
                tempAccountChatMessages["p_" + phone].push(JSON.stringify(message));
            }
        } else if (sendType == "tempGroup") {
            tempAccountChatMessages["t_" + message.tempGid] = tempAccountChatMessages["t_" + message.tempGid] || [];
            tempAccountChatMessages["t_" + message.tempGid].push(JSON.stringify(message));
            if (tempGroupsInfo[message.tempGid] == undefined) {
                getTempGroupInfo(message.tempGid);
                function getTempGroupInfo(tempGid) {
                    $.ajax({
                        type: "GET",
                        url: "/api2/group/get?",
                        data: {
                            phone: accountObj.phone,
                            accessKey: accountObj.accessKey,
                            gid: tempGid,
                            type: "tempGroup"
                        },
                        success: function (data) {
                            if (data["提示信息"] == "获取群组信息成功") {
                                var tempGroup = data.group;
                                tempGroupsInfo[tempGid] = tempGroup;
                                var members = tempGroup.members;
                                var noFriends = [];
                                for (var i = 0; i < members.length; i++) {
                                    if (allCirclesFriends[members[i]] == undefined) {
                                        noFriends.push(members[i]);
                                    }
                                }
                                getTempGroupMembers();
                                getTemplateHtml("user_groups", function (template) {
                                    $(".js_user_groups").append(template.render([tempGroup]));
                                });
                            } else {
                                alert(data["提示信息"] + "," + data["失败原因"]);
                            }
                        }
                    });
                }
            }
        } else if (sendType == "group") {
            tempAccountChatMessages["g_" + message.tempGid] = tempAccountChatMessages["g_" + message.tempGid] || [];
            tempAccountChatMessages["g_" + message.tempGid].push(JSON.stringify(message));
        } else {
            console.log("请注意,逻辑错误-丢失数据--数据为:-" + message);
        }
    }
    window.sessionStorage.setItem("wxgs_tempAccountChatMessages", JSON.stringify(tempAccountChatMessages));
}
function sendMessages(data) {
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