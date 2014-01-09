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

var currentChatUser = {};
$(document).ready(function () {
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
//                                alert("longPress");
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
    /*$(document).on("mousedown", ".user_icon", function (event) {
        if (dropStatus == "none") {
            mouseX = event.clientX;
//            mouseX = event.clientX - 500;
            mouseY = event.clientY;
        }
    });*/
});