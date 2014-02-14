var allCirclesFriends = {};
var dropStatus = "none";
var mouseX = 0;
var mouseY = 0;
var checkGroup = false;
var checkGroupId = -1;
var selectedDropUsers = {};
var oldSelectedGroupClass = "";
var newSelectedGroupClass = "";

var selectedAddCircleGroupFlag = false;

var selectedDeleteOrBlack = "";
var imageServer = window.globaldata.serverSetting.imageServer;
var currentChatUser = {};
$(document).ready(function () {
    var accountStr = window.localStorage.getItem("wxgs_nowAccount");
    if (accountStr == undefined) {
        location.href = "./index.html";
    }
//    $.getScript("/static/js/nTenjin.js");
//    $.getScript("/static/js/animation.js");
    (function ($) {
        $.extend($.fn, {
            longPress: function (time, callBack) {
                time = time || 1000;
                var timer = null;
                $(this).mousedown(function (e) {
                    var i = 0;
                    var _this = $(this);
                    timer = setInterval(function () {
                        i += 10;
                        if (i >= time) {
                            clearTimeout(timer);
                            var positionX = e.pageX - _this.offset().left || 0;
                            var positionY = e.pageY - _this.offset().top || 0;
                            typeof callBack == 'function' && callBack.call(_this, e, positionX, positionY);
                        }
                    }, 10)
                }).mouseup(function () {
                        clearTimeout(timer);
                    })
            }
        });
    })(jQuery);
//    var phone = "121";
    var imageServer = window.globaldata.serverSetting.imageServer;
//    window.localStorage.setItem("wxgs_nowAccount", JSON.stringify({phone: "121", accessKey: "lejoying", nickName: "麦穗儿香", head: "d9fb7db5dc6e4b06046f0114b12d581ee84cec73"}));
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    if (accountObj != undefined) {
        allCirclesFriends[accountObj.phone] = accountObj;
        $(".js_accountNickName").html(accountObj.nickName);
        $(".js_accountNickName").attr("title", accountObj.nickName);
        if (accountObj.head != "") {
            $(".js_accountHead").attr("src", imageServer + "/" + accountObj.head);
        } else {
            $(".js_accountHead").attr("src", "/static/images/face_man.png");
        }

        $.ajax({
            type: "POST",
            url: "/api2/relation/getcirclesandfriends?",
            data: {
                phone: accountObj.phone,
                accessKey: accountObj.accessKey
            },
            success: function (data) {
                if (data["提示信息"] == "获取密友圈成功") {
                    getTemplateHtml("circles_friends", function (template) {
                        $(".js_groups").html(template.render(data.circles));
                        for (var index in data.circles) {
                            var circle = (data.circles)[index];
                            var accounts = circle.accounts;
                            for (var i = 0; i < accounts.length; i++) {
                                var account = accounts[i];
                                account.rid = circle.rid;
                                allCirclesFriends[account.phone] = account;
                            }
                        }
                        showTempChatUsersInfo();
                        window.sessionStorage.setItem("wxgs_circles", JSON.stringify(data.circles));
                        var clickGroupIndex = -1;
                        $(".appGroup").each(function (i) {
                            $($(".appGroup")[i]).click(function () {
                                var circles = JSON.parse(window.sessionStorage.getItem("wxgs_circles"));
                                checkGroup = false;
                                checkGroupId = -1;
                                selectedDropUsers = {};
                                $(".popmenuFrame").css({
                                    marginTop: "0px"
                                });
                                var parentClass = this.parentNode.className;
                                var i = parentClass.substr(parentClass.lastIndexOf("_") + 1);
                                if (i == clickGroupIndex) {
                                    clickGroupIndex = -1;
                                    $(".popmenuFrame").slideUp(200);
                                    oldSelectedGroupClass = "";
//                                alert(oldSelectedGroupClass);
                                    if (i > 5) {
                                        $("#mainBox").css({
                                            marginTop: 0
                                        });
                                    }
                                } else {
                                    $(".popmenuFrame").slideUp(1);
                                    oldSelectedGroupClass = parentClass;
                                    clickGroupIndex = i;
                                    $(".popmenuFrame").css({
                                        visibility: "visible",
                                        top: 95 + (Math.floor(i / 3)) * 90 + "px"
                                    });
                                    $(".sildLeftSharp").css({
                                        left: 45 + (i % 3) * 82 + "px"
                                    });
                                    var group_user = getTemplate("js_group_user");
                                    $(".sildPopContent").html(group_user.render(circles[i]));
//                                getScroll();
//                            $("#ScroLine").css("height", "100px");
//                            alert(Math.ceil(((data.circles)[i].accounts.length + 1) / 4));
//                            alert((180 / Math.ceil(((data.circles)[i].accounts.length + 1) / 4) * 64) * 180);
                                    if (Math.ceil(((data.circles)[i].accounts.length + 1) / 4) > 2) {
//                                alert(180 / (Math.ceil(((data.circles)[i].accounts.length + 1) / 4) * 64));
//                                $("#ScroLine").css("height", (180 / $(".group_user").height) * 180);
                                        $("#ScroLine").css("height", (195 / (Math.ceil(((data.circles)[i].accounts.length + 1) / 4) * 64)) * 195);
                                    } else {
                                        $("#ScroLine").css("height", "214");
                                    }
                                    $(".user_icon").longPress(200, function (e, x, y) {
                                        if (dropStatus == "down") {
//                                        $(".popmenuFrame").slideUp(1000);
                                            dropStatus = "dropping";
//                                          alert("longPress");
                                            $(".js_menuPanel").show();
                                            $(".popmenuFrame").css({
                                                marginTop: "500px"
                                            });
                                            $("#mainBox").css({
                                                marginTop: 0
//                                        marginTop: -((Math.floor(i / 3)) * 90 + 5)
                                            });
                                        }
                                        var icon1 = $(this);
                                        if (icon1.hasClass("js_selected")) {
                                            icon1.removeClass("js_selected");
                                            icon1.addClass("js_moving");
                                            next();
                                        }
                                        function next() {
                                            icon1.css("top", mouseY - y);
                                            icon1.css("left", mouseX - x);
                                        }
                                    });
                                    $(".user_icon").addClass("js_none");
                                    $(".popmenuFrame").slideDown(100);
                                    oldSelectedGroupClass = parentClass;
//                                alert(oldSelectedGroupClass);
                                    if (i > 5) {
                                        $("#mainBox").css({
                                            marginTop: -((Math.floor(i / 3) - 1) * 90 + 5)
//                                        marginTop: -((Math.floor(i / 3)) * 90 + 5)
                                        });
                                    }
                                }
                                $(".schoolmate_txt").slideDown(10);
                                $(".js_modifycirclename").slideUp(10);
                            });
                        });
                    });
                } else {
                    alert(data["提示信息"]);//获取密友圈失败的处理
                }
            }
        });
    }
    $("body").mousemove(function (e) {
        if (dropStatus == "dropping") {
            var icon = $(".js_moving");
            if (icon.hasClass("js_moving")) {
                icon.css("top", e.clientY - 42);
                icon.css("left", e.clientX - 32);
//                icon.css("top", e.clientY - 42 + 30);
//                icon.css("left", e.clientX - 32 - 490);
            }
        }
        if (dropStatus == "down") {
            mouseX = e.clientX;
            mouseY = e.clientY;
//            mouseX = e.clientX + 30;
//            mouseY = e.clientY - 490;
        }
    });
    $("body").mouseup(function (e) {
        if (dropStatus == "dropping") {
            var phoneTo = [];
            for (var index in selectedDropUsers) {
                phoneTo.push(index);
            }
            if (selectedDeleteOrBlack == "delete") {
                selectedDeleteOrBlack = "";
                deleteFriend(JSON.stringify(phoneTo));
            } else if (selectedDeleteOrBlack == "blacklist") {
                selectedDeleteOrBlack = "";
                blacklistFriend(JSON.stringify(phoneTo));
            } else {
                selectedDeleteOrBlack = "";
            }

            //            var iconGroups = $(".js_icon_group_dropping");
            //            iconGroups.removeClass("js_icon_group_dropping");
        }
        dropStatus = "none";
        $(".appGroup").css({
            border: "2px solid rgb(0, 153, 205)"
        });
        $(".appGroup").removeClass("js_icon_group_dropping");
        $(".user_icon").removeClass("js_moving");
        $(".js_menuPanel").hide();
        if (dropStatus == "down") {
            $(".user_icon").addClass("js_none");//可以实现仅选择一个好友进行操作
        }
        if (checkGroup) {
            checkGroup = false;
//                selectedDropUsers = {};
            if (checkGroupId != -1) {
//                    checkGroupId = -1;
                var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
                var circles = JSON.parse(window.sessionStorage.getItem("wxgs_circles"));
                var selectedDropUser = {};
//                alert(JSON.stringify(selectedDropUser) == "{}");
                var array = [];
                var index0 = "";
                for (var index in selectedDropUsers) {
                    index0 = index;
                    array.push(index);
                }
                moveOutFriendGroup(accountObj, JSON.stringify(array), checkGroupId, selectedDropUsers[index0]);
            } else {
                checkGroupId = -1;
                selectedDropUsers = {};
            }
        }
    });
    //这段代码导致发送消息框无法使用
    /*$(document).on("mousedown", ".user_icon", function (event) {
     if (dropStatus == "none") {
     mouseX = event.clientX;
     //            mouseX = event.clientX - 500;
     mouseY = event.clientY;
     }
     });*/
    $(document).on("mouseup", ".user_icon", function (event) {
        var icon = $(this);
        var phone = icon.find("span").attr("phone");
        var circle_rid = $(this.parentNode.parentNode.parentNode).find(".schoolmate_txt").attr("circle_rid");
        if (icon.hasClass("js_none")) {

            if (event.ctrlKey && event.button == 0) {
                dropStatus = "down";
                icon.removeClass("js_none");
                icon.addClass("js_selected");
                selectedDropUsers[phone] = circle_rid;
            } else {
                //------------------------------------------------------------------------------------------------------点击头像聊天的处理
                currentChatType = "POINT";
//                clickHeadImgToChatPanel();
                currentChatUser = allCirclesFriends[phone];
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
                $(".js_chat_one").show();
                $(".js_chat_group").hide();
//                alert(JSON.stringify(allCirclesFriends[phone]));//获取当前聊天用户的信息
//                icon.addClass("js_none");
//                delete selectedDropUsers[phone];
            }
        } else {
            icon.removeClass("js_selected");
            icon.removeClass("js_moving");
            icon.addClass("js_none");
            delete selectedDropUsers[phone];
        }
    });
    $(document).on("mouseover", "#js_groupusers", function () {
        if (dropStatus == "dropping") {
            $(this).find(".appGroup").css({
                border: "2px solid red"
            });
            $(this).find(".appGroup").addClass("js_icon_group_dropping");
            checkGroup = true;
            checkGroupId = $(this).attr("circle_rid");
            newSelectedGroupClass = $(this).attr("class");
//            alert($(this).attr("class"));
        }
    });
    $(document).on("mouseleave", "#js_groupusers", function () {
        if (dropStatus == "dropping") {
            $(this).find(".appGroup").css({
                border: "2px solid rgb(0, 153, 205)"
            });
            $(this).find(".appGroup").removeClass("js_icon_group_dropping");
            checkGroup = false;
            checkGroupId = -1;
            newSelectedGroupClass = "";
        }
    });

    $(document).on("click", ".js_circleAddFriend", function () {
        //默认分组的circle_rid == "undefined" 　其他的默认都是数值类型的
        $(".js_findFriendBtn").attr("circle_rid", this.getAttribute("circle_rid"));
        $(".js_findFriend").css({
            display: "block"
        });
        $(".js_findFriendPhone").focus();
        new Drag($(".js_findFriend")[0]);
    });
    $(".schoolmate_txt").slideDown(10);
    $(".js_modifycirclename").slideUp(10);
    $(document).on("click", ".js_addcircle", function () {
//        alert("新建密友圈");
        var i = (JSON.parse(window.sessionStorage.getItem("wxgs_circles"))).length + 1;
        $(".popmenuFrame").css({
            visibility: "visible",
            top: 95 + (Math.floor(i / 3)) * 90 + "px"
        });
        var group_user = getTemplate("js_group_user");
        $(".sildPopContent").html(group_user.render([]));
        //新建分组不显示任何元素
//        $(".sildPopContent").html("");
        $(".js_modifycirclename input[type=text]").val("");
        $(".popmenuFrame").slideDown(100);
//        $(".schoolmate_txt").html("aa");
        $(".schoolmate_txt").slideUp(10);
        $(".js_modifycirclename").slideDown(10);
        selectedAddCircleGroupFlag = true;
//        $(".js_modifycirclename").slideUp(10);
    });
    $(document).on("click", ".user_icon", function () {
        var span = $(this).find("span");
//        alert(span.html() + "--" + span.attr("phone"));
    });
    $(".js_modifycirclename").slideUp(10);
    var oldCircleName = "";
    $(document).on("dblclick", ".schoolmate_txt", function () {
        if (this.getAttribute("title") != "默认分组") {
            $(this).slideUp(10);
            $(".js_modifycirclename").slideDown(10);
            oldCircleName = $(".js_modifycirclename input[type=text]").val();
        }
        //            alert("双击分组名称-->" + this.getAttribute("title"));
    });

    $(document).on("click", ".js_modifycirclesubmit", function () {
        var circle_rid = this.parentNode.getAttribute("circle_rid");
        var newCircleName = $(".js_modifycirclename input[type=text]").val();
        if (oldCircleName == newCircleName) {
            if (oldCircleName.trim() != "") {
                $(".schoolmate_txt").slideDown(10);
                $(".js_modifycirclename").slideUp(10);
            } else {
                alert("不能为空");
            }
        } else {
            if (newCircleName.trim() == "") {
                alert("不能为空");
            } else {
                if (newCircleName.length > 20) {
                    alert("长度不能超过20位");
                } else {
                    if (!selectedAddCircleGroupFlag) {
                        modifyCircleName(circle_rid, newCircleName, $(".js_circleName_" + circle_rid).html());
                        $(".schoolmate_txt").slideDown(10);
                        $(".js_modifycirclename").slideUp(10);
                    } else {
                        addCircleGroup(newCircleName);
                    }
                }
            }
        }
    });
    $(document).on("click", ".js_modifycirclecancle", function () {
        if (!selectedAddCircleGroupFlag) {
            $(".schoolmate_txt").slideDown(10);
            $(".js_modifycirclename").slideUp(10);
            oldCircleName = "";
        } else {
            $(".popmenuFrame").slideUp(10);
            selectedAddCircleGroupFlag = false;
        }
    });
    $(document).on("click", ".js_findFriendBtn", function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        $(".js_findFriendErrorMessage").html("");
        var phone = $(".js_findFriendPhone").val().trim();
        if (phone == "") {
            $(".js_findFriendErrorMessage").html("手机号不能为空。");
        } else if (isNaN(phone)) {
            $(".js_findFriendErrorMessage").html("手机号格式不正确。");
        } else {
            $.ajax({
                type: "GET",
                url: "/api2/account/get?",
                data: {
                    phone: accountObj.phone,
                    accessKey: accountObj.accessKey,
                    target: JSON.stringify([phone])
                },
                success: function (data) {
                    if (data["提示信息"] == "获取用户信息成功") {
                        $(".js_findFriendPhone").val("");
                        $(".js_findFriendErrorMessage").html("");
                        $(".js_addFriendErrorMessage").html("");
                        var accountData = (data.accounts)[0];
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
                    } else {
                        $(".js_findFriendErrorMessage").html(data["失败原因"]);
                    }
                }
            });
        }
    });
    $(document).on("click", ".js_friendMessage_addFriend", function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var phone = $(this).attr("phone");
        $(".js_addFriendErrorMessage").html("");
        var flag = false;
        if (phone != accountObj.phone) {
            var circlesObj = JSON.parse(window.sessionStorage.getItem("wxgs_circles"));
            A:for (var index in circlesObj) {
                var accounts = circlesObj[index].accounts;
                if (accounts.length == 0)
                    continue;
                B:for (var index1 in accounts) {
                    var account = accounts[index1];
                    if (account.phone == phone) {
                        flag = true;
                        break A;
                    }
                }
            }
            if (flag) {
                $(".js_addFriendErrorMessage").html(phone + "已经是您的好友,不能重复添加。");
            } else {
                $(".js_addFriendErrorMessage").html("");
                $(".js_addFriendSubmit").attr("circle_rid", $(this).attr("circle_rid"));
                $(".js_addFriendSubmit").attr("phone", $(this).attr("phone"));
                $(".js_friendMessage").css({display: "none"});
                $(".js_addFriend").css({display: "block"});
//                new Drag($(".js_addFriend")[0]);
                //                $(".js_addFriendErrorMessage").html(phone + "不是您的好友。");
            }
        } else {
            if (phone == accountObj.phone) {
                $(".js_addFriendErrorMessage").html("不能添加自己为好友。")
            }
        }
    });
    $(document).on("click", ".js_addFriendSubmit", function () {
//        alert($(this).attr("circle_rid") == "undefined");
//        alert($(".js_addFriendVerifyMessage").val().trim() + "---" + $(this).attr("circle_rid") + "---" + $(this).attr("phone"));
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var verifyMessage = $(".js_addFriendVerifyMessage").val();
        if (verifyMessage.length > 40) {
            alert("验证信息太长");
        } else {
            $.ajax({
                type: "POST",
                url: "/api2/relation/addfriend?",
                data: {
                    phone: accountObj.phone,
                    accessKey: accountObj.accessKey,
                    phoneto: $(this).attr("phone"),
                    rid: $(this).attr("circle_rid"),
                    message: verifyMessage
                },
                success: function (data) {
                    alert(data["提示信息"]);
                }
            });
            $(".js_addFriendVerifyMessage").val("");
            $(".js_addFriend").css({
                display: "none"
            });
        }

    });
    $(document).on("click", ".js_findFriend_close", function () {
        $(this.parentNode.parentNode).css({
            display: "none"
        });
        $(".js_findFriendPhone").val("");
        $(".js_findFriendErrorMessage").html("");
        $(".js_addFriendErrorMessage").html("");
        $(".js_findFriend").css({
            "top": "200px",
            "left": "315px"
        });
        $(".js_friendMessage").css({
            "top": "200px",
            "left": "315px"
        });
        $(".js_addFriend").css({
            "top": "200px",
            "left": "315px"
        });
    });
    $(".js_menuPanel").hide();
    $(document).on("mouseenter", ".js_deleteFriend", function () {
        selectedDeleteOrBlack = "delete";
        $(this).css({
            opacity: 1
        });
//        alert("删除-js_deleteFriendover");
    });
    $(document).on("mouseleave", ".js_deleteFriend", function () {
        selectedDeleteOrBlack = "";
        $(this).css({
            opacity: 0.5
        });
//        alert("删除-js_deleteFriendleave");
    });
    $(document).on("mouseenter", ".js_blackListFriend", function () {
        selectedDeleteOrBlack = "blacklist";
        $(this).css({
            opacity: 1
        });
    });
    $(document).on("mouseleave", ".js_blackListFriend", function () {
        selectedDeleteOrBlack = "";
        $(this).css({
            opacity: 0.5
        });
    });
    /* $(document).on("click", ".js_user_icon", function () {
     alert($(this).find("span").attr("phone"));
     });*/
});
function deleteFriend(phoneTo) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        url: "/api2/relation/deletefriend?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            phoneto: phoneTo
        },
        success: function (data) {
            alert(data["提示信息"]);
        }
    });
}

function blacklistFriend(phoneTo) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        url: "/api2/relation/blacklist?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            phoneto: phoneTo
        },
        success: function (data) {
            alert(data["提示信息"]);
        }
    });
}

function moveOutFriendModifyCirclesData(phoneTo, newCircleRid, oldCircleRid, circles) {
//    alert(oldCircleRid);
    var circles = circles;
    var tempPhoneIndex = [];
    var y = 0;
    var oldCircleId = oldCircleRid;
    if (oldCircleId == "undefined") {
        oldCircleId = undefined;
    }
    A:for (var i = 0; i < circles.length; i++) {
        var accounts = circles[i].accounts;
        if (JSON.stringify(circles[i].rid) == oldCircleId) {
            B:for (var j = 0; j < accounts.length; j++) {
                var account = accounts[j];
                var index = $.inArray(account.phone, phoneTo);
                if (index >= 0) {
                    y++;
                    tempPhoneIndex.push(j);
                    ModifyCirclesLocalData(circles, newCircleRid, account, next);
                    function next(index) {
                        var accountsLength = accounts.length;
                        if (accountsLength <= 4 && circles[index].accounts.length <= 4) {
                            $(".js_appGroup_headimg" + account.phone).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
                        } else if (accountsLength <= 4 && circles[index].accounts.length >= 4) {
                            $(".js_appGroup_headimg" + account.phone).remove();
                            alert("remove");
                        } else if (accountsLength >= 4 && circles[index].accounts.length <= 4) {
                            if ($(".js_appGroup_headimg" + account.phone).attr("class") == undefined) {
                                var imageServer = window.globaldata.serverSetting.imageServer;
                                var img = document.createElement("img");
                                $(img).css({
                                    "width": "18px",
                                    "height": "18px",
                                    "margin-top": "5px",
                                    "border-radius": "50%"
                                });
                                $(img).attr("class", "js_appGroup_headimg" + account.phone);
                                $(img).attr("src", imageServer + "/" + account.head + ".png");
                                $(".js_app_" + i).find(".appGroup").add();
                                $(img).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
//                                alert("头像不存在，创建头像");
                            } else {
                                $(".js_appGroup_headimg" + account.phone).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
                            }
                        } else {
                            if (circles[index].accounts.length.length <= 4) {
                                if ($(".js_appGroup_headimg" + account.phone).attr("class") == undefined) {
                                    var imageServer = window.globaldata.serverSetting.imageServer;
                                    var img = document.createElement("img");
                                    $(img).css({
                                        "width": "18px",
                                        "height": "18px",
                                        "margin-top": "5px",
                                        "border-radius": "50%"
                                    });
                                    $(img).attr("class", "js_appGroup_headimg" + account.phone);
                                    $(img).attr("src", imageServer + "/" + account.head + ".png");
                                    $(".js_app_" + i).find(".appGroup").add();
                                    $(img).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
//                                alert("头像不存在，创建头像");
                                } else {
                                    $(".js_appGroup_headimg" + account.phone).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
                                }
                            } else {
                                var length = ($("." + newSelectedGroupClass).find(".appGroup").find("img").length);
                                if (length < 4) {
                                    if ($(".js_appGroup_headimg" + account.phone).attr("class") == undefined) {
                                        var imageServer = window.globaldata.serverSetting.imageServer;
                                        var img = document.createElement("img");
                                        $(img).css({
                                            "width": "18px",
                                            "height": "18px",
                                            "margin-top": "5px",
                                            "border-radius": "50%"
                                        });
                                        $(img).attr("class", "js_appGroup_headimg" + account.phone);
                                        $(img).attr("src", imageServer + "/" + account.head + ".png");
                                        $(".js_app_" + i).find(".appGroup").add();
                                        $(img).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
//                                alert("头像不存在，创建头像");
                                    } else {
                                        $(".js_appGroup_headimg" + account.phone).appendTo($("." + newSelectedGroupClass).find(".appGroup"));
                                    }
                                }
                                var length = ($("." + oldSelectedGroupClass).find(".appGroup").find("img").length);

                            }
                        }
                        if (phoneTo.length == y) {
                            for (var x = tempPhoneIndex.length - 1; x >= 0; x--) {
                                accounts.splice(tempPhoneIndex[x], 1);
                            }
                        }
                        window.sessionStorage.setItem("wxgs_circles", JSON.stringify(circles));
                    }
                } else {
                    continue;
                }
            }
        } else {
            continue;
        }
    }
}
function ModifyCirclesLocalData(circles, newCircleRid, account, next) {
//    var circles = circles;
    var newCircleId = newCircleRid;
    if (newCircleId == "undefined") {
        newCircleId = undefined;
    }
    C:for (var i = 0; i < circles.length; i++) {
        var circle = circles[i];
//        alert(JSON.stringify(circle.rid) == undefined);
        if (JSON.stringify(circle.rid) == newCircleId) {
            var accounts = circle.accounts;
            accounts.push(account);
            window.sessionStorage.setItem("wxgs_circles", JSON.stringify(circles));
            next(i);
            break C;
        } else {
            continue;
        }
    }
}
function addCircleGroup(circleName) {
    alert(circleName);
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        url: "/api2/circle/addcircle?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            name: circleName
        },
        success: function (data) {
            if (data["提示信息"] == "添加成功") {
                alert(data["提示信息"]);
            } else {
                alert(data["提示信息"] + "---" + alert(data["失败原因"]));
            }
            $(".popmenuFrame").slideUp(10);
            selectedAddCircleGroupFlag = false;
        }
    });
    $(".popmenuFrame").slideUp(10);
    selectedAddCircleGroupFlag = false;
}
function moveOutFriendGroup(accountObj, phoneTo, newCircleId, oldCircleId) {
//    delete selectedDropUsers[phoneTo];
    $.ajax({
        type: "POST",
        url: "/api2/circle/moveout?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            phoneto: phoneTo,
            oldrid: oldCircleId,
            newrid: newCircleId
        },
        success: function (data) {
            if (data["提示信息"] == "移动成功") {
                selectedDropUsers = {};
                var circles = JSON.parse(window.sessionStorage.getItem("wxgs_circles"));
                moveOutFriendModifyCirclesData(JSON.parse(phoneTo), newCircleId, oldCircleId, circles);
            } else {
                alert(data["提示信息"] + "---" + data["失败原因"]);
            }
        }
    });
}
function modifyCircleName(rid, newCircleName, oldCircleName) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        url: "/api2/circle/modify?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            rid: rid,
            name: newCircleName
        },
        success: function (data) {
            if (data["提示信息"] == "修改成功") {
                $(".js_circleName_" + rid).html(newCircleName);
                $(".js_circlename" + rid).html(newCircleName);
                $(".js_circleName_" + rid).attr("title", newCircleName);
            }
        }
    });
}

function getTemplateHtml(templateHtml, next) {
    var tenjin = nTenjin;
    $.get("/static/templates/" + templateHtml + ".html?time=" + new Date().getTime(), function (result) {
        var template = new tenjin.Template();
        var string = result.replace(/\r/g, "");
        string = string.replace(/\<\!\-\-\?/g, "<?");
        string = string.replace(/\?\-\-\>/g, "?>");
        string = string.replace(/比较符号大于/g, ">");
        string = string.replace(/比较符号兄小于/g, "<");
        template.convert(string);
        next(template);
    }, "html");
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

//------------------------------------------------------------------------
/*$(function () {
 $("#ScroLine").css("height", ($(".group_user").height() / $(".group_user")[0].scrollHeight) * $(".group_user").height());
 });*/
var Scrolling = false;

function $1(o) {
    return document.getElementById(o)
}

function ScroMove() {
    Scrolling = true
}

document.onmousemove = function (e) {
    if (Scrolling == false)return;
    ScroNow(e)
}
document.onmouseup = function (e) {
    Scrolling = false
}
function ScroNow(event) {
    var event = event ? event : (window.event ? window.event : null);
    var Y = event.clientY - $(".js_group_users")[0].getBoundingClientRect().top - $("#ScroLine")[0].clientHeight / 2;
    var H = $("#ScroRight")[0].clientHeight - $("#ScroLine")[0].clientHeight;
    var SH = Y / H * ($(".group_user")[0].scrollHeight - $(".group_user")[0].clientHeight);
    if (Y < 0)Y = 0;
    if (Y > H)Y = H;
    $("#ScroLine")[0].style.top = Y + "px";
    $(".group_user").css({
        marginTop: -Y * 0.5 + "px"
    });
}

function ScrollWheel() {
    var flag = event.wheelDelta == 120;
    var Y = $(".js_group_users")[0].scrollTop;
    var H = $(".js_group_users")[0].scrollHeight - $(".js_group_users")[0].clientHeight;
    if (event.wheelDelta >= 120) {
        Y = Y - 80
    } else {
        Y = Y + 80
    }
    if (Y < 0)Y = 0;
    if (Y > H)Y = H;
    $(".js_group_users")[0].scrollTop = Y;
    var SH = Y / H * $("#ScroRight")[0].clientHeight - $("#ScroLine")[0].clientHeight;
    if (SH < 0)SH = 0;
    $("#ScroLine")[0].style.top = SH + "px";
}
function tempChatMainContent() {
    var flag = event.wheelDelta == 120;
//    var H = $("#mainBox")[0].clientHeight - $("#mainBox")[0].scrollHeight-280;
    var H = ($(".listContentWrap").height() - $("#mainBox")[0].scrollHeight - 288) * ((window.screen.availHeight + 90) / document.documentElement.clientHeight);
    var top = ($(".listContent").css("top")).replace("px", "");
//    alert(top);
    if (event.wheelDelta < 0) {
        var Y = parseInt(top);// - 30
        if (Y <= H) {
            Y = H;
            return;
//            alert("这是最底部了.");
        }
//        alert(Y);
        $(".listContent").css({
            top: Y + "px"
        });
    } else {
        var Y = parseInt(top);// +30
        if (Y > 0) {
            Y = 0;
            return;
//            alert("这是最顶部了.");
        }
//        alert(Y);
        $(".listContent").css({
            top: Y + "px"
        });
    }
    /*var flag = event.wheelDelta == 120;
     var Y = $(".listContent")[0].scrollTop;
     var H = $(".listContent")[0].scrollHeight - $(".listContent")[0].clientHeight;
     if (event.wheelDelta >= 120) {
     Y = Y - 80
     } else {
     Y = Y + 80
     }
     if (Y < 0)Y = 0;
     if (Y > H)Y = H;
     $(".listContent").css({
     top: 100 + "px"
     });*/
    /*var SH = Y / H * $("#ScroRight")[0].clientHeight - $("#ScroLine")[0].clientHeight;
     if (SH < 0)SH = 0;
     $("#ScroLine")[0].style.top = SH + "px";*/
}