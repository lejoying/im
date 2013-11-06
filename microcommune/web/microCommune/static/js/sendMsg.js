$(document).ready(function () {
//    window.sessionStorage.clear();
    window.onbeforeunload = onbeforeunload_handler;
    function onbeforeunload_handler() {
        var warning = "关闭浏览器聊天记录将会丢失";
        return warning;
    }

    var wxgs_tempChat = window.sessionStorage.getItem("wxgs_tempChat");
    if (wxgs_tempChat != null && wxgs_tempChat != undefined) {
        var tempChatArr = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatArr"));
        for (var index in tempChatArr) {
//            var it = tempChatArr[index];
            addTempChatAccount(JSON.parse(tempChatArr[index]));
        }
    }
    $.getScript("/static/js/nTenjin.js");
    var nowAccount = window.localStorage.getItem("wxgs_nowAccount");
    $($(".nickName")[0]).html(JSON.parse(nowAccount).nickName);
    $(".js_circlesFriends").hide();
//    $(".prompteds").hide();
    $.ajax({
        type: "POST",
        url: "/api2/relation/getcirclesandfriends?",
        data: {
            phone: JSON.parse(nowAccount).phone
        },
        success: function (data) {
            if (data["提示信息"] == "获取密友圈成功") {
//                window.sessionStorage.setItem("wxgs_tempChat", JSON.stringify({}));
                setTimeout(showNotification(), 3000);
                window.sessionStorage.setItem("circles", JSON.stringify(data.circles));
                var circles_friends = getTemplate("circles_friends");
                $(".js_circlesFriends").html(circles_friends.render(data["circles"]));
                $(".circles_friends .friendDetail span img").click(function () {
                    var obj = JSON.parse(window.sessionStorage.getItem("circles"));
                    var phone = this.parentNode.parentNode.getAttribute("phone");
                    var rid = this.parentNode.parentNode.getAttribute("circleid");
                    if (rid != "undefined") {
                        for (var index1 in obj) {
                            var it1 = obj[index1];
                            if (it1.rid == rid) {
                                var accounts = it1.accounts;
                                for (var index2 in accounts) {
                                    var it2 = accounts[index2];
                                    if (it2.phone == phone) {
                                        showProc(it2, rid);
                                    }
                                }
                            }
                        }
                    } else {
                        for (var index1 in obj) {
                            var it1 = obj[index1];
                            if (it1.rid == undefined) {
                                var accounts = it1.accounts;
                                for (var index2 in accounts) {
                                    var it2 = accounts[index2];
                                    if (it2.phone == phone) {
                                        showProc(it2, rid);
                                    }
                                }
                            }
                        }
                    }
                });
                $(".circles_friends .friendDetail>div").mouseover(function () {
//                    var evt = evt || window.event;
//                    alert(evt.clientX+"--"+evt.clientY);
//                    var j = new Endrag(this, this.parentNode.id, 0, 0);
                }).mousedown(function () {
//                        alert("===");
                    });
            }
        }
    });

    $(".addFriends").click(function () {
        alert("addFriends");
    });
    $(".voiceCancel").click(function () {
        alert("voiceCancel");
    });
    $(".feedback").click(function () {
        alert("feedback");
    });
    $(".iconLogout").click(function () {
        alert("iconLogout");
    });
    $("#txl").click(function () {
        $("#conversationContainer").hide();
        $(".js_circlesFriends").show();
    });
    $("#chooseConversationBtn").click(function () {
        $("#conversationContainer").show();
        $(".js_circlesFriends").hide();
    });
    $(".chatSend").click(function () {
        var content = $("#textInput").val();
        $("#textInput").val("");
        var date = new Date();
        var hours = date.getHours();
        var minutes = date.getMinutes();
        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? "0" + minutes : minutes;
        $("#chat_chatmsglist").append('<div un="item_2070333132" class="chatItem me">     ' +
            '<div class="time"> <span class="timeBg left"></span> ' + hours + ':' + minutes + ' <span class="timeBg right"></span> </div>       ' +
            ' <div class="chatItemContent"> <img username="gh_c639eef72f78" click="showProfile" title="云上" un="avatar_gh_c639eef72f78" onerror="reLoadImg(this)" src="static/images/webwxgeticon4.jpg" class="avatar"> <div msgid="2070333132" un="cloud_2070333132" class="cloud cloudText">     ' +
            ' <div style="" class="cloudPannel">                                                                                                                                                                                                                                                                                          ' +
            '   <div class="sendStatus">   </div>                                                                                                                                                                                                                                                                                          ' +
            '   <div class="cloudBody">                                                                                                                                                                                                                                                                                                       ' +
            '      <div class="cloudContent">                                                                                                                                                                                                                                                                                                  ' +
            '           <pre style="white-space:pre-wrap">' + content + '</pre>                                                                                                                                         ' +
            '           </div>                                                                                                                                                                                                                                                                                                                         ' +
            '        </div>     ' +
            '         <div class="cloudArrow "></div>    ' +
            '      </div>     ' +
            '   </div>   ' +
            '</div>    ' +
            '</div>');
        var phone = JSON.parse(window.localStorage.getItem("wxgs_nowAccount")).phone;
//        var phoneto = $("#js_chat .chatName")[0].attributes['accountphone'].nodeValue;
        var phoneto = $("#js_chat .chatName")[0].getAttribute("accountphone");
//        var circleid = $("#js_chat .chatName")[0].attributes['circleid'].nodeValue;
        var circleid = $("#js_chat .chatName")[0].getAttribute("circleid");
        var message = $("#textInput").val();
        var listPhone = [];
        listPhone.push(phoneto);
        var messages = {
            type: "text",
            content: {
                text: message
            }
        };
        $.ajax({
            type: "POST",
            url: "/api2/message/send?",
            data: {
                phone: phone,
                phoneto: JSON.stringify(listPhone),
                message: JSON.stringify(messages)
            },
            success: function (data) {
                if (data["提示信息"] == "发送成功") {
                    var tempChat = JSON.parse(window.sessionStorage.getItem("wxgs_tempChat"));
                    var tempChatArr = window.sessionStorage.getItem("wxgs_tempChatArr");
                    if (tempChat != null) {
                        if (tempChat[phoneto] != "" && tempChat[phoneto] != undefined) {
                            var account = tempChat[phoneto];
                            var accountObj = JSON.parse(account);
                            $("#conv_wxid_" + accountObj.uid)[0].parentNode.removeChild($("#conv_wxid_" + accountObj.uid)[0]);
                            var tempChatArrObj = JSON.parse(tempChatArr);
                            for (var index in tempChatArrObj) {
                                if (tempChatArrObj[index] == account) {
                                    tempChatArrObj.splice(index, 1);
                                    tempChatArrObj.push(account);
                                    window.sessionStorage.setItem("wxgs_tempChatArr", JSON.stringify(tempChatArrObj));
                                    break;
                                }
                            }
                            window.sessionStorage.setItem("wxgs_tempChat", JSON.stringify(tempChat));
                            addTempChatAccount(accountObj);
                        } else {
                            addTempChatCheck(tempChat, JSON.parse(tempChatArr), phoneto, circleid)
                        }
                    } else {
                        addTempChatCheck(tempChat, JSON.parse(tempChatArr), phoneto, circleid)
                    }
                } else {
                    alert(data["提示信息"] + "," + data["失败原因"]);
                }
            }
        });
    });
    function addTempChatCheck(tempChat, tempChatArr, phoneto, circleid) {
        if (tempChat == null) {
            tempChat = {};
            tempChatArr = [];
        }
        var circles = JSON.parse(window.sessionStorage.getItem("circles"));
        if (circleid == "undefined") {
            for (var index1 in circles) {
                var it1 = circles[index1];
                if (it1.rid == undefined) {
                    var accounts = it1.accounts;
                    for (var index2 in accounts) {
                        var it2 = accounts[index2];
                        if (it2.phone == phoneto) {
                            var tempChatobj = tempChat;
                            it2.rid = "undefined";
                            tempChatobj[phoneto] = JSON.stringify(it2);
                            window.sessionStorage.setItem("wxgs_tempChat", JSON.stringify(tempChatobj));
                            var tempChatArrObj = tempChatArr;
                            tempChatArrObj.push(JSON.stringify(it2));
                            window.sessionStorage.setItem("wxgs_tempChatArr", JSON.stringify(tempChatArrObj));
                            addTempChatAccount(it2);
                        }
                    }
                }
            }
        } else {
            for (var index1 in circles) {
                var it1 = circles[index1];
                if (it1.rid == parseInt(circleid)) {
                    var accounts = it1.accounts;
                    for (var index2 in accounts) {
                        var it2 = accounts[index2];
                        if (it2.phone == phoneto) {
                            var tempChatobj = tempChat;
                            it2.rid = it1.rid;
                            tempChatobj[phoneto] = JSON.stringify(it2);
                            window.sessionStorage.setItem("wxgs_tempChat", JSON.stringify(tempChatobj));
                            var tempChatArrObj = tempChatArr;
                            tempChatArrObj.push(JSON.stringify(it2));
                            window.sessionStorage.setItem("wxgs_tempChatArr", JSON.stringify(tempChatArrObj));
                            addTempChatAccount(it2);
                        }
                    }
                }
            }
        }
    }
    clickDiv();
    $(".js_createRoomSendMessage").click(function () {
        closeProc();
        $("#js_chat")[0].style.visibility = "visible";
        $("#js_chat .chatName").html(this.getAttribute("accountnickName"));
        $("#js_chat .chatName").attr("accountphone", this.getAttribute("accountphone"));
        $("#js_chat .chatName").attr("circleid", this.getAttribute("circleid"));
    });
});
function clickDiv(){
    $("#conversationContainer>div").click(function () {
//            clickDiv();
        $("#js_chat")[0].style.visibility = "visible";
        $(".chatListColumn").attr("class", "chatListColumn");
        $(this).attr("class", "chatListColumn activeColumn");
        var phone = this.getAttribute("phone");
        var tempChat = JSON.parse(window.sessionStorage.getItem("wxgs_tempChat"));
        setChatMessages(JSON.parse(tempChat[phone]));
    });
}
function setChatMessages(obj){
    $("#js_chat .chatName").html(obj.nickName);
    $("#js_chat .chatName").attr("accountphone", obj.phone);
    $("#js_chat .chatName").attr("circleid", obj.rid);
    clickDiv();
}
function addTempChatAccount(obj) {
    $(".chatListColumn").attr("class", "chatListColumn");
    var str = '<div style="display:none;" class="clicked"></div>' +
        '<span style="display:none" class="unreadDot">0</span> <span style="display:none" class="unreadDotS"></span>' +
        '<div class="avatar_wrap"><img click1="showProfile@.chatListColumn" src="static/images/webwxgeticon4.jpg" class="avatar"></div>' +
        '<div class="extend">' +
        '<p class="time"></p>' +
        '<div class="edited"><i class="editedIcon"></i></div>' +
        '<div style="display:none;" class="mute"></div>' +
        '</div>' +
        '<div class="info">' +
        '<div class="nickName">' +
        '<div style="" class="left name">' + obj.nickName + '</div>' +
        '<div class="clr"></div>' +
        '</div>' +
        '<div class="descWrapper"> <img style="display:none;" class="tipIcon iconSendFailed sendFailedStatus" src="/static/images/spacer17ced3.gif"> <img class="tipIcon iconSending sendingStatus hide" src="/static/images/spacer17ced3.gif" style="display: none;">' +
        '<p class="desc">' + obj.mainBusiness + '</p>' +
        '</div>' +
        '</div>' +
        '<div class="clr">' +
        '</div>';
    var div = document.createElement('div');
    $(div).attr("class", "chatListColumn activeColumn");
    $(div).attr("id", "conv_wxid_" + obj.uid);
    $(div).attr("username", "wxid_t2fqz99bmakt21");
    $(div).attr("phone", obj.phone);
    $(div).attr("un", "wxid_t2fqz99bmakt21");
    div.innerHTML = str;
    $("#conversationContainer")[0].insertBefore(div, $("#conversationContainer")[0].firstChild);
}
function showProc(it, rid) {
    message_box.style.visibility = 'visible';
    // 创建灰色背景层
    procbg = document.createElement("div");
    procbg.setAttribute("id", "mybg");
    procbg.style.background = "#000";
    procbg.style.width = "100%";
    procbg.style.height = "100%";
    procbg.style.position = "absolute";
    procbg.style.top = "0";
    procbg.style.left = "0";
    procbg.style.zIndex = "500";
    procbg.style.opacity = "0.3";
    procbg.style.filter = "Alpha(opacity=30)";
    //背景层加入页面
    document.body.appendChild(procbg);
    document.body.style.overflow = "hidden";
//    $("#message_box .js_head>img").attr("src", "/static/images/face.jpg");attributes['phone'].nodeValue;
//    $(".js_createRoomSendMessage")[0].attributes['alt'].nodeValue = it.phone;
    $(".js_createRoomSendMessage").attr("accountphone", it.phone);
    $(".js_createRoomSendMessage").attr("accountnickName", it.nickName);
    $(".js_createRoomSendMessage").attr("circleid", rid);
    $("#message_box .js_nickName").html("昵称：" + it.nickName);
    $("#message_box .js_phone").html("手机号：" + it.phone);
    $("#message_box .js_mainBusiness").html("业务：" + it.mainBusiness);
}
//拖动
function drag(obj) {
    var s = obj.style;
    var b = document.body;
    var x = event.clientX + b.scrollLeft - s.pixelLeft;
    var y = event.clientY + b.scrollTop - s.pixelTop;

    var m = function () {
        if (event.button == 1) {
            s.pixelLeft = event.clientX + b.scrollLeft - x;
            s.pixelTop = event.clientY + b.scrollTop - y;
        } else {
            document.onmousemove = null;
//            document.detachEvent("onmousemove", m);
        }
    }
    document.onmousemove = m;
//    document.attachEvent("onmousemove", m)

    if (!this.z)
        this.z = 999;
    s.zIndex = ++this.z;
    event.cancelBubble = true;
}

function closeProc() {
    message_box.style.visibility = 'hidden';
    procbg.style.visibility = "hidden";
}

if (!window.webkitNotifications) {
    alert("您的浏览器不支持Notification桌面通知!");
}
function RequestPermission(callback) {
    window.webkitNotifications.requestPermission(callback);
}
var notification;
function showNotification() {
    if (window.webkitNotifications.checkPermission() > 0) {
        RequestPermission(showNotification);
    } else {
        notification = window.webkitNotifications.createNotification("http://avatar.csdn.net/F/8/1/1_qxs965266509.jpg", "乔晓松", "上班中...！");
        notification.onshow = function () {
            setTimeout('notification.cancel()', 5000);
        }
        notification.onclick = function () {
        }
        notification.show();
    }
}
//根据id获取模版
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
function SetCookie(name, value)
//设定Cookie值
{
    var expdate = new Date();
    var argv = SetCookie.arguments;
    var argc = SetCookie.arguments.length;
    var expires = (argc > 2) ? argv[2] : null;
    var path = (argc > 3) ? argv[3] : null;
    var domain = (argc > 4) ? argv[4] : null;
    var secure = (argc > 5) ? argv[5] : false;
    if (expires != null) expdate.setTime(expdate.getTime() + ( expires * 1000 ));
    document.cookie = name + "=" + escape(value) + ((expires == null) ? "" : ("; expires=" + expdate.toGMTString()))
        + ((path == null) ? "" : ("; path=" + path)) + ((domain == null) ? "" : ("; domain=" + domain))
        + ((secure == true) ? "; secure" : "");
}
function setCookieTime(name, value, time) {
    var strsec = getsec(time);
    var exp = new Date();
    exp.setTime(exp.getTime() + strsec * 1);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}
function getsec(str) {
    alert(str);
    var str1 = str.substring(1, str.length) * 1;
    var str2 = str.substring(0, 1);
    if (str2 == "s") {
        return str1 * 1000;
    } else if (str2 == "h") {
        return str1 * 60 * 60 * 1000;
    } else if (str2 == "d") {
        return str1 * 24 * 60 * 60 * 1000;
    }
}
function GetCookie(name)
//获得Cookie的原始值
{
    var arg = name + "=";
    var alen = arg.length;
    var clen = document.cookie.length;
    var i = 0;
    while (i < clen) {
        var j = i + alen;
        if (document.cookie.substring(i, j) == arg)
            return GetCookieVal(j);
        i = document.cookie.indexOf(" ", i) + 1;
        if (i == 0) break;
    }
    return null;
}
function GetCookieVal(offset)
//获得Cookie解码后的值
{
    var endstr = document.cookie.indexOf(";", offset);
    if (endstr == -1)
        endstr = document.cookie.length;
    return unescape(document.cookie.substring(offset, endstr));
}