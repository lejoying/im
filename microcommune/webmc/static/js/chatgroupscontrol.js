var inviteSelectGroupID = -1;
var inviteSelectedUsers = {};

var groups = [];
var groupsInfo = {};
var tempGroupsInfo = {};
var currentChatType = "POINT";//POINT,GROUP,TEMPGROUP
var currentChatGroup;
$(function () {
    var tempData = window.sessionStorage.getItem("wxgs_tempGroupsInfo");
    if (tempData != undefined) {
        tempGroupsInfo = JSON.parse(tempData);
        var tempGroups = [];
        for (var index in tempGroupsInfo) {
            tempGroups.push(tempGroupsInfo[index]);
        }
        getTemplateHtml("user_groups", function (template) {
            $(".js_user_groups").append(template.render(tempGroups));
        });
    }
    $(document).on("click", ".js_chat_one_addfrriends", function () {
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
            inviteSelectedUsers = {};
            var data = {
                phone: accountObj.phone,
                accessKey: accountObj.accessKey
            };
            if (currentChatType == "POINT") {
                members.push(accountObj.phone);
                members.push(currentChatUser.phone);
                data.members = JSON.stringify(members);
                data.name = "";
                data.type = "createTempGroup";
                createTempGroup(data);
            } else if (currentChatType == "TEMPGROUP") {
                data.tempGid = currentChatGroup.tempGid;
                var tempGroup = tempGroupsInfo[currentChatGroup.tempGid];
                var already_inGroup = tempGroup.members;
                members = members.concat(already_inGroup);
                data.members = JSON.stringify(members);
                data.name = tempGroup.name;
                data.type = "createTempGroup";
                createTempGroup(data);
            } else if (currentChatType == "GROUP") {
                //正式群，邀请部分好机油加入群组聊天
                data.gid = currentChatGroup.gid;
                data.members = JSON.stringify(members);
                addMembersToGroup(data);
            }
            function createTempGroup(dataFrom) {
                $.ajax({
                    type: "POST",
                    url: "/api2/group/create?",
                    data: dataFrom,
                    success: function (data) {
                        if (data["提示信息"] == "创建群组成功" || data["提示信息"] == "更新群组成功") {
                            var tempGid = data.tempGid;
                            var tempGroup = {
                                tempGid: tempGid,
                                name: dataFrom.name,
                                members: JSON.parse(dataFrom.members)
                            };
                            if (dataFrom.tempGid == undefined) {
                                getTemplateHtml("user_groups", function (template) {
                                    $(".js_user_groups").append(template.render([tempGroup]));
                                    alert(JSON.stringify(tempGroup));
                                });
                            }
                            tempGroupsInfo[tempGid] = tempGroup;
                            window.sessionStorage.setItem("wxgs_tempGroupsInfo", JSON.stringify(tempGroupsInfo));
                        }
                    }
                });
            }

            function addMembersToGroup(dataFrom) {
                $.ajax({
                    type: "POST",
                    url: "/api2/group/addmembers?",
                    data: dataFrom,
                    success: function (data) {
                        console.log(data);
                    }
                });
            }
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
        $(".js_chat_one").hide();
        $(".js_chat_group").show();
        $(".js_chat_group_temp_up").show();
        var target = $(this);
        currentChatType = (target.attr("groupType")).toUpperCase();
        if (currentChatType == "TEMPGROUP") {
            var tempGid = target.attr("group_gid");
            currentChatGroup = tempGroupsInfo[tempGid];
            getTemplateHtml("group_users", function (template) {
                var length = (currentChatGroup.members).length;
                $(".js_chat_group_friends").html(template.render(currentChatGroup));
                var tempGroupName = currentChatGroup.name;
                if (tempGroupName != undefined && tempGroupName != null && tempGroupName != "") {
                    $(".js_chat_group_info_groupName").html(tempGroupName);
                } else {
                    $(".js_chat_group_info_groupName").html("临时群");
                }
                $(".js_chat_group_info_count").html("(" + length + "人)");
                if (length > 4) {
                    var left = (length + 1) * 60 - 300;
                    $(".js_chat_group_friends").css({
                        "margin-left": -left + "px"
                    });
                } else {
                    $(".js_chat_group_friends").css({
                        "margin-left": "0px"
                    });
                }
            });
        }
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
    $(document).on("click", ".js_chat_group_temp_up", function () {
        alert("UP");
//        $(".js_chat_group_temp").hide();
//        $(".js_chat_one_more").show();
    });
    $(document).on("click", ".js_chat_group_left_icon", function () {
        alert("js_chat_group_left_icon");
    });
    $(document).on("click", ".js_chat_group_right_icon", function () {
        alert("js_chat_group_right_icon");
    });
});
function getUserAllGroups() {
    $.ajax({
        type: "GET",
        url: "/api2/group/get"
    });
}