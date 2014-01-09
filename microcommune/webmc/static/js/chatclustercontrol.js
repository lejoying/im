var scrollInitFlag = false;
var tempSendMessageTimeStamp = [];
$(function () {
    $(".js_chatRightFrame").css({
        visibility: "hidden"
    });
//    $(".js_rightChatPanel").hide();
//    $(".js_chatRightFrame").hide();
    $(".js_morefriend").hide();
    $(".js_morefriend").slideUp();
    $(".js_onlyfriend").slideUp(1);
    $(".js_onlyfriend").slideDown(100, function () {
    });

    $(document).on("click", ".js_chatsendmessage", function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var message = $(".js_chatmessagecontent").val();
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
});
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
    var messsageFlag = data.flag;
    window.sessionStorage.setItem("wxgs_messageFlag", messsageFlag);
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
    $(".js_chatmessagecontent").val("")
    $.ajax({
        type: "POST",
        url: "/api2/message/send?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            phoneto: listPhone,
            message: message
        },
        success: function (data) {
            if (data["提示信息"] == "发送成功") {
                setScrollPosition();
//                var message = JSON.stringify(message);
//                message.time = data.time;
//                alert(formattertime(time));
                var time = tempSendMessageTimeStamp.shift();
                $(".js_messageTime_" + time).html(formattertime(data.time));

            } else {
                alert(data["提示信息"] + "---" + data["失败原因"]);
            }
        }
    });
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
            } else {
                keepQuest();
            }
        },
        error: function () {
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