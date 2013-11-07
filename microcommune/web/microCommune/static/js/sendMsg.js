$(document).ready(function () {
    window.onbeforeunload = onbeforeunload_handler;
    function onbeforeunload_handler() {
        var warning = "关闭浏览器聊天记录将会丢失";
        return warning;
    }

    $.getScript("/static/js/nTenjin.js");
    var nowAccount = window.localStorage.getItem("wxgs_nowAccount");
    $($(".nickName")[0]).html(JSON.parse(nowAccount).nickName);
    $(".js_circlesFriends").hide();
    $(".loadMoreConv").hide();
    var wxgs_tempChat = window.sessionStorage.getItem("wxgs_tempChat");
    if (wxgs_tempChat != null && wxgs_tempChat != undefined) {
        var tempChatArr = JSON.parse(window.sessionStorage.getItem("wxgs_tempChatArr"));
        for (var index in tempChatArr) {
//            var it = tempChatArr[index];
            addTempChatAccount(JSON.parse(tempChatArr[index]));
        }
        $(".loadMoreConv").show();
    }
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
                setTimeout(showNotification(), 2000);
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
    $(".DesktopRemind").click(function () {
        if ($(".DesktopRemind .iconPic").attr("check") == undefined || $(".DesktopRemind .iconPic").attr("check") == "true") {
            $(".DesktopRemind .iconPic").attr("check", false);
            $(".DesktopRemind .iconPic").css("background-position", "-71px -599px");
        } else {
            $(".DesktopRemind .iconPic").attr("check", true);
            $(".DesktopRemind .iconPic").css("background-position", "-95px -599px");
        }
    });
    $(".voiceCancel").click(function () {
        if ($(".voiceCancel .iconPic").attr("check") == undefined || $(".voiceCancel .iconPic").attr("check") == "false") {
            $(".voiceCancel .iconPic").attr("check", true);
            $(".voiceCancel .iconPic").css("background-position", "-50px -599px");
        } else {
            $(".voiceCancel .iconPic").attr("check", false);
            $(".voiceCancel .iconPic").css("background-position", "-26px -599px");
        }
    });
    $(".feedback").click(function () {
        showProcFeedBack();
    });
    $(".iconLogout").click(function () {
        window.localStorage.clear();
        window.sessionStorage.clear();
        location.href = "/login.html";
    });
    $("#txl").click(function () {
        $("#conversationContainer").hide();
        $(".js_circlesFriends").show();
        $(".loadMoreConv").hide();
    });
    $("#chooseConversationBtn").click(function () {
        $("#conversationContainer").show();
        $(".js_circlesFriends").hide();
        $(".loadMoreConv").show();
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
                    dialogMessage("notice", data["提示信息"] + "," + data["失败原因"], 1000);
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
    $(".loadMoreConv").click(function () {
        //alert("正在努力加载更多的数据...");
        dialogMessage("loading", "正在获取更多的数据，请稍候...", 2000);
    });
});
function clickDiv() {
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
function setChatMessages(obj) {
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
function showProcFeedBack() {
    js_feedback.style.visibility = 'visible';
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
    /*//    $("#message_box .js_head>img").attr("src", "/static/images/face.jpg");attributes['phone'].nodeValue;
     //    $(".js_createRoomSendMessage")[0].attributes['alt'].nodeValue = it.phone;
     $(".js_createRoomSendMessage").attr("accountphone", it.phone);
     $(".js_createRoomSendMessage").attr("accountnickName", it.nickName);
     $(".js_createRoomSendMessage").attr("circleid", rid);
     $("#message_box .js_nickName").html("昵称：" + it.nickName);
     $("#message_box .js_phone").html("手机号：" + it.phone);
     $("#message_box .js_mainBusiness").html("业务：" + it.mainBusiness);*/
}

function closeProc() {
    message_box.style.visibility = 'hidden';
    procbg.style.visibility = "hidden";
}
function closeProCFeedBack() {
    js_feedback.style.visibility = 'hidden';
    operaterBox.style.display = "none";
    procbg.style.visibility = "hidden";
}

function addTextAreaCss() {
    $("#js_feedback textarea").css("border-color", "#EEC77C");
}
function deleteTextAreaCss() {
    $("#js_feedback textarea").css("border-color", "");
}
function js_sendFeedBack(obj) {
    var content = $(".js_feedbackcontent").val().trim();
    if (content == "") {
        eval("$.Prompt('请输入您要反馈的内容')");
    } else {
        dialogMessage("succ", "谢谢您的反馈!", 2000);
        $(".js_feedbackcontent").val("");
        closeProCFeedBack();
    }
}
function dialogMessage(type, msg, time) {
    var tipHtml = '';
    if (type == 'loading') {
        tipHtml = '<img alt="" src="/static/images/loading.gif">' + (msg ? msg : '正在加载数据...');
    } else if (type == 'notice') {
        tipHtml = '<span class="gtl_ico_hits"></span>' + msg
    } else if (type == 'error') {
        tipHtml = '<span class="gtl_ico_fail"></span>' + msg
    } else if (type == 'succ') {
        tipHtml = '<span class="gtl_ico_succ"></span>' + msg
    }
    if ($('.msgbox_layer_wrap')) {
        $('.msgbox_layer_wrap').remove();
    }
    if (st) {
        clearTimeout(st);
    }
    $("body").prepend("<div class='msgbox_layer_wrap'><span id='mode_tips_v2' style='z-index: 10000;' class='msgbox_layer'><span class='gtl_ico_clear'></span>" + tipHtml + "<span class='gtl_end'></span></span></div>");
    $(".msgbox_layer_wrap").show();
    var st = setTimeout(function () {
        $(".msgbox_layer_wrap").hide();
        clearTimeout(st);
    }, time);
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
function DragDivDrag(titleBarID, message_boxID) {

    var Common = {
        getEvent: function () {//ie/ff
            if (document.all) {
                return window.event;
            }
            func = getEvent.caller;
            while (func != null) {
                var arg0 = func.arguments[0];
                if (arg0) {
                    if ((arg0.constructor == Event || arg0.constructor == MouseEvent) || (typeof (arg0) == "object" && arg0.preventDefault && arg0.stopPropagation)) {
                        return arg0;
                    }
                }
                func = func.caller;
            }
            return null;
        },
        getMousePos: function (ev) {
            if (!ev) {
                ev = this.getEvent();
            }
            if (ev.pageX || ev.pageY) {
                return {
                    x: ev.pageX,
                    y: ev.pageY
                };
            }

            if (document.documentElement && document.documentElement.scrollTop) {
                return {
                    x: ev.clientX + document.documentElement.scrollLeft - document.documentElement.clientLeft,
                    y: ev.clientY + document.documentElement.scrollTop - document.documentElement.clientTop
                };
            }
            else if (document.body) {
                return {
                    x: ev.clientX + document.body.scrollLeft - document.body.clientLeft,
                    y: ev.clientY + document.body.scrollTop - document.body.clientTop
                };
            }
        },
        getItself: function (id) {
            return "string" == typeof id ? document.getElementById(id) : id;
        },
        getViewportSize: { w: (window.innerWidth) ? window.innerWidth : (document.documentElement && document.documentElement.clientWidth) ? document.documentElement.clientWidth : document.body.offsetWidth, h: (window.innerHeight) ? window.innerHeight : (document.documentElement && document.documentElement.clientHeight) ? document.documentElement.clientHeight : document.body.offsetHeight },
        isIE: document.all ? true : false,
        setOuterHtml: function (obj, html) {
            var Objrange = document.createRange();
            obj.innerHTML = html;
            Objrange.selectNodeContents(obj);
            var frag = Objrange.extractContents();
            if (obj.parentNode != null) {
                obj.parentNode.insertBefore(frag, obj);
                obj.parentNode.removeChild(obj);
            }
        }
    }

///------------------------------------------------------------------------------------------------------
    var Class = {
        create: function () {
            return function () {
                this.init.apply(this, arguments);
            }
        }
    }
    var Drag = Class.create();
    Drag.prototype = {
        init: function (titleBar, message_box, Options) {
            //设置点击是否透明，默认不透明
            titleBar = Common.getItself(titleBar);
            message_box = Common.getItself(message_box);
            this.dragArea = { maxLeft: 0, maxRight: Common.getViewportSize.w - message_box.offsetWidth - 2, maxTop: 0, maxBottom: Common.getViewportSize.h - message_box.offsetHeight - 2 };
            if (Options) {
                this.opacity = Options.opacity ? (isNaN(parseInt(Options.opacity)) ? 100 : parseInt(Options.opacity)) : 100;
                this.keepOrigin = Options.keepOrigin ? ((Options.keepOrigin == true || Options.keepOrigin == false) ? Options.keepOrigin : false) : false;
                if (this.keepOrigin) {
                    this.opacity = 50;
                }
                if (Options.area) {
                    if (Options.area.left && !isNaN(parseInt(Options.area.left))) {
                        this.dragArea.maxLeft = Options.area.left
                    }
                    ;
                    if (Options.area.right && !isNaN(parseInt(Options.area.right))) {
                        this.dragArea.maxRight = Options.area.right
                    }
                    ;
                    if (Options.area.top && !isNaN(parseInt(Options.area.top))) {
                        this.dragArea.maxTop = Options.area.top
                    }
                    ;
                    if (Options.area.bottom && !isNaN(parseInt(Options.area.bottom))) {
                        this.dragArea.maxBottom = Options.area.bottom
                    }
                    ;
                }
            }
            else {
                this.opacity = 100, this.keepOrigin = false;
            }
            this.originDragDiv = null;
            this.tmpX = 0;
            this.tmpY = 0;
            this.moveable = false;

            var dragObj = this;

            titleBar.onmousedown = function (e) {
                var ev = e || window.event || Common.getEvent();
                //只允许通过鼠标左键进行拖拽,IE鼠标左键为1 FireFox为0
                if (Common.isIE && ev.button == 1 || !Common.isIE && ev.button == 0) {
                }
                else {
                    return false;
                }

                if (dragObj.keepOrigin) {
                    dragObj.originDragDiv = document.createElement("div");
                    dragObj.originDragDiv.style.cssText = message_box.style.cssText;
                    dragObj.originDragDiv.style.width = message_box.offsetWidth;
                    dragObj.originDragDiv.style.height = message_box.offsetHeight;
                    dragObj.originDragDiv.innerHTML = message_box.innerHTML;
                    message_box.parentNode.appendChild(dragObj.originDragDiv);
                }

                dragObj.moveable = true;
                message_box.style.zIndex = dragObj.GetZindex() + 1;
                var downPos = Common.getMousePos(ev);
                dragObj.tmpX = downPos.x - message_box.offsetLeft;
                dragObj.tmpY = downPos.y - message_box.offsetTop;

                titleBar.style.cursor = "move";
                if (Common.isIE) {
                    message_box.setCapture();
                } else {
                    window.captureEvents(Event.MOUSEMOVE);
                }

                dragObj.SetOpacity(message_box, dragObj.opacity);

                //FireFox 去除容器内拖拽图片问题
                if (ev.preventDefault) {
                    ev.preventDefault();
                    ev.stopPropagation();
                }

                document.onmousemove = function (e) {
                    if (dragObj.moveable) {
                        var ev = e || window.event || Common.getEvent();
                        //IE 去除容器内拖拽图片问题
                        if (document.all) //IE
                        {
                            ev.returnValue = false;
                        }

                        var movePos = Common.getMousePos(ev);
                        message_box.style.left = Math.max(Math.min(movePos.x - dragObj.tmpX, dragObj.dragArea.maxRight), dragObj.dragArea.maxLeft) + "px";
                        message_box.style.top = Math.max(Math.min(movePos.y - dragObj.tmpY, dragObj.dragArea.maxBottom), dragObj.dragArea.maxTop) + "px";

                    }
                };

                document.onmouseup = function () {
                    if (dragObj.keepOrigin) {
                        if (Common.isIE) {
                            dragObj.originDragDiv.outerHTML = "";
                        }
                        else {
                            Common.setOuterHtml(dragObj.originDragDiv, "");
                        }
                    }
                    if (dragObj.moveable) {
                        if (Common.isIE) {
                            message_box.releaseCapture();
                        }
                        else {
                            window.releaseEvents(Event.MOUSEMOVE);
                        }
                        dragObj.SetOpacity(message_box, 100);
                        titleBar.style.cursor = "default";
                        dragObj.moveable = false;
                        dragObj.tmpX = 0;
                        dragObj.tmpY = 0;
                    }
                };
            }
        },
        SetOpacity: function (message_box, n) {
            if (Common.isIE) {
                message_box.filters.alpha.opacity = n;
            }
            else {
                message_box.style.opacity = n / 100;
            }

        },
        GetZindex: function () {
            var maxZindex = 0;
            var divs = document.getElementsByTagName("div");
            for (z = 0; z < divs.length; z++) {
                maxZindex = Math.max(maxZindex, divs[z].style.zIndex);
            }
            return maxZindex;
        }
    }
    new Drag(titleBarID, message_boxID, { opacity: 100, keepOrigin: true }); //, area: { left: 50, right: 500, top: 100, bottom: 400}
}

window.onload = function () {
    DragDivDrag("titleBar", "message_box");
    DragDivDrag("js_titleBarfeedback", "js_feedback");
}