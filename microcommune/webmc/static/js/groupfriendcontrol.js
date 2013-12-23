var dropStatus = "none";
var mouseX = 0;
var mouseY = 0;
$(document).ready(function () {
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
    var phone = "121";
    var imageServer = window.globaldata.serverSetting.imageServer;
    window.localStorage.setItem("wxgs_nowAccount", JSON.stringify({phone: "121", accessKey: "lejoying", nickName: "麦穗儿香", head: "d9fb7db5dc6e4b06046f0114b12d581ee84cec73"}));
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $(".js_accountNickName").html(accountObj.nickName + "-Active");
    $(".js_accountNickName").attr("title", accountObj.nickName + "-Active");
    $(".js_accountHead").attr("src", imageServer + "/" + accountObj.head + ".png");
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


                    window.sessionStorage.setItem("wxgs_circles", JSON.stringify(data.circles));
                    var clickGroupIndex = -1;
                    $(".appGroup").each(function (i) {
                        $($(".appGroup")[i]).click(function () {
                            var parentClass = this.parentNode.className;
                            var i = parentClass.substr(parentClass.lastIndexOf("_") + 1);
                            if (i == clickGroupIndex) {
                                clickGroupIndex = -1;
                                $(".popmenuFrame").slideUp(200);
                                if (i > 5) {
                                    var box = $("#mainBox");
                                    var toState = new State();
                                    var fromState = new State();
                                    animateTransform(box[0], fromState, toState, 200,
                                        {
                                            onStart: function () {
                                            },
                                            onEnd: function () {
                                                var fromState1 = new State(toState);
                                                var toState1 = new State(toState);
                                                toState1.translate.y = 0;
                                                animateTransform(box[0], fromState1, toState1, 600);
                                            }
                                        }
                                    );
                                }
//                            $(".popmenuFrame")[0].style.visibility = "hidden";
                            } else {
                                $(".popmenuFrame").slideUp(1);
                                clickGroupIndex = i;
                                $(".popmenuFrame").css({
                                    visibility: "visible",
                                    top: 95 + (Math.floor(i / 3)) * 90 + "px"
                                });
                                $(".sildLeftSharp").css({
                                    left: 45 + (i % 3) * 82 + "px"
                                });
                                var group_user = getTemplate("js_group_user");
                                $(".sildPopContent").html(group_user.render((data.circles)[i]));
                                getScroll();
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
                                        dropStatus = "dropping";
//                                alert("longPress");
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
                                $(".popmenuFrame").slideDown(1000);
                                if (i > 5) {
                                    var box = $("#mainBox");
                                    var toState = new State();
                                    toState.scale.x = 0.5;
                                    toState.scale.y = 0.5;
                                    var fromState = new State();
                                    animateTransform(box[0], fromState, toState, 200,
                                        {
                                            onStart: function () {
                                            },
                                            onEnd: function () {
                                                var fromState1 = new State(toState);
                                                var toState1 = new State(toState);
                                                toState1.translate.y = -((Math.floor(i / 3)) * 90 + 5);
                                                toState1.scale.x = 1;
                                                toState1.scale.y = 1;
                                                animateTransform(box[0], fromState1, toState1, 400);
                                            }
                                        }
                                    );
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
    $("body").mousemove(function (e) {
        if (dropStatus == "dropping") {
            var icon = $(".js_moving");
            if (icon.hasClass("js_moving")) {
                icon.css("top", e.clientY - 42);
                icon.css("left", e.clientX - 32);
            }
        }
        if (dropStatus == "down") {
            mouseX = e.clientX;
            mouseY = e.clientY;
        }
    });
    $("body").mouseup(function (e) {
        if (dropStatus == "dropping") {
//            var iconGroups = $(".js_icon_group_dropping");
//            iconGroups.removeClass("js_icon_group_dropping");
        }
        dropStatus = "none";
        $(".user_icon").removeClass("js_moving");
        $(".user_icon").addClass("js_none");
//        $(".sildPopContent .popappIcon").removeClass("js_moving1");
//        $(".sildPopContent .popappIcon").removeClass("js_moving2");
    });
    $(document).on("click", ".js_circleAddFriend", function () {
        //默认分组的circle_rid == "undefined" 　其他的默认都是数值类型的
        $(".js_findFriendBtn").attr("circle_rid", this.getAttribute("circle_rid"));
        $(".js_findFriend").css({
            display: "block"
        });
        $(".js_findFriendPhone").focus();
    });
    $(document).on("click", ".js_addcircle", function () {
        alert("新建密友圈");
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
    $(document).on("mousedown", ".user_icon", function (event) {
        if (dropStatus == "none") {
            dropStatus = "down";
            mouseX = event.clientX;
            mouseY = event.clientY;
        }
    });
    $(document).on("mouseup", ".user_icon", function (event) {
        var icon = $(this);
        if (icon.hasClass("js_none")) {
            icon.removeClass("js_none");
            if (event.ctrlKey && event.button == 0) {
                icon.addClass("js_selected");
            } else {
                icon.addClass("js_none");
            }
        } else {
            icon.removeClass("js_selected");
            icon.removeClass("js_moving");
            icon.addClass("js_none");
        }
    });
    /*$(document).on("longPress", ".user_icon_img1", function (e, x, y) {
     alert("longPress");
     });*/
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
                    modifyCircleName(circle_rid, newCircleName, $(".js_circleName_" + circle_rid).html());
                    $(".schoolmate_txt").slideDown(10);
                    $(".js_modifycirclename").slideUp(10);
                }
            }
        }
    });
    $(document).on("click", ".js_modifycirclecancle", function () {
        $(".schoolmate_txt").slideDown(10);
        $(".js_modifycirclename").slideUp(10);
        oldCircleName = "";
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
                    target: phone
                },
                success: function (data) {
                    if (data["提示信息"] == "获取用户信息成功") {
                        $(".js_findFriendPhone").val("");
                        $(".js_findFriendErrorMessage").html("");
                        $(".js_addFriendErrorMessage").html("");
                        var accountData = data.account;
                        $(".js_friendMessage_addFriend").attr("circle_rid", $(".js_findFriendBtn").attr("circle_rid"));
                        $(".js_friendMessage_addFriend").attr("phone", accountData.phone);
                        var mainBusiness = accountData.mainBusiness;
                        $(".js_findFriend").css({display: "none"});
                        $(".js_friendMessage").css({display: "block"});
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
    });
    $(".js_menuPanel").hide();
    $(document).on("mouseover", ".js_deleteFriend", function () {
        alert("删除-js_deleteFriend");
    });
    $(document).on("mouseover", ".js_blackListFriend", function () {
        alert("黑名单-js_blackListFriend");
    });
});
function getScroll() {

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
    /*    var tenjin = nTenjin;
     var templateDiv = $('.templates #' + id).parent();
     var string = templateDiv.html().toString();
     $.get("/static/templates/circles_friends.html", function (templates) {
     //        alert(string);
     alert(string.length == templates.length);
     alert(string == templates);
     alert(string);
     alert(templates.replace(/\r/g, ""));
     }, "html");
     var template = new tenjin.Template();
     string = string.replace(/\<\!\-\-\?/g, "<?");
     string = string.replace(/\?\-\-\>/g, "?>");
     string = string.replace(/比较符号大于/g, ">");
     string = string.replace(/比较符号兄小于/g, "<");
     template.convert(string);
     return template;*/
    var tenjin = nTenjin;
    $.get("/static/templates/" + templateHtml + ".html", function (result) {
        //        alert(string);
//                    alert(string.length == templates.length);
//                    alert(string == templates);
//                    alert(string);
//                    alert(templates.replace(/\r/g,""));
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
//    $("#ScroLine")[0].style.top = Y + "px";
    $("#ScroLine")[0].style.top = Y + "px";
    $(".group_user").css({
        marginTop: -Y * 0.5 + "px"
    });
//    $("#ScroRight").css({
//        marginTop: "0px"
//    });
//    alert(Y);
    /*var box = $(".group_user");
     var toState = new State();
     var fromState = new State();
     animateTransform(box[0], fromState, toState, 1,
     {
     onStart: function () {
     },
     onEnd: function () {
     var fromState1 = new State(toState);
     var toState1 = new State(toState);
     toState1.translate.y = - Y;
     animateTransform(box[0], fromState1, toState1, 1);
     }
     }
     );*/
//    $(".group_user")[0].scrollTop = SH;
//    $(".group_user")[0].style.top = -500 + "px";
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