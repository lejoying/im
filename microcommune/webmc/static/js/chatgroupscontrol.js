var inviteSelectGroupID = -1;
var inviteSelectedUsers = {};

var groups = [];
var groupsInfo = {};
var tempGroupsInfo = {};
var currentChatType = "POINT";//POINT,GROUP,TEMPGROUP
var currentChatGroup;
$(function () {
    if (window.localStorage.getItem("wxgs_nowAccount")) {
        getUserAllGroups();//获取所有的正式群组和好友
    }
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
    //正式群组不显示
    /*var groupData = window.sessionStorage.getItem("wxgs_groupsInfo");
     if (groupData != undefined) {
     groupsInfo = JSON.parse(groupData);
     var groups = [];
     for (var index in groupsInfo) {
     groups.push(groupsInfo[index]);
     }
     getTemplateHtml("user_groups", function (template) {
     $(".js_user_groups").append(template.render(groups));
     });
     }*/
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
                /*var group = groupsInfo[currentChatGroup.gid];
                 if (group != undefined) {
                 var groupMembers = group.members;
                 members = members.concat(groupMembers);
                 }*/
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
                            showTempGroupInfoAndMessages("TEMPGROUP", tempGid);
                            currentChatType = "TEMPGROUP";
                            $(".js_chat_one").hide();
                            $(".js_chat_group").show();
                            $(".js_chat_group_temp_up").show();
                            $(".js_chat_group_temp_up").css({
                                "display": "block"
                            });
                        }
                    }
                });
            }

            //正式群组添加好友
            function addMembersToGroup(dataFrom) {
                $.ajax({
                    type: "POST",
                    url: "/api2/group/addmembers?",
                    data: dataFrom,
                    success: function (data) {
                        if (data["提示信息"] == "加入群组成功") {
                            var currentMembers = JSON.parse(dataFrom.members);
                            var gid = dataFrom.gid;
                            var group = groupsInfo[gid];
                            var members = groups.members;
                            group.members = members.concat(currentMembers);
                            currentChatGroup = group;
                            window.sessionStorage.setItem("wxgs_groupsInfo", JSON.stringify(groupsInfo));
                        } else {
                            alert(data["提示信息"] + "," + data["失败原因"]);
                        }
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
        if (currentChatType == "TEMPGROUP" || currentChatType == "GROUP") {
            var flag = true;
            var members = currentChatGroup.members;
            for (var i = 0; i < members.length; i++) {
                if (members[i] == phone) {
                    flag = false;
                    alert("此好友已在该群组");
                    break;
                }
            }
            if (flag) {
                next();
            }
        } else if (currentChatType == "POINT") {
            next();
        }
        function next() {
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
        $(".js_chatRightFrame").css({
            visibility: "visible"
        });
        $(".js_chat_one").hide();
        $(".js_chat_group").show();
        $(".js_chat_group_temp_up").show();
        $(".js_chat_group_temp_up").css({
            "display": "block"
        });
        var target = $(this);
        currentChatType = (target.attr("groupType")).toUpperCase();
        var tempGid = target.attr("group_gid");
        if (currentChatType == "TEMPGROUP") {
            showTempGroupInfoAndMessages("TEMPGROUP", tempGid);
            $(".js_chat_group_temp").show();
            $(".js_chat_group_temp_up").css({
                "display": "block"
            });
            $(".js_chat_one_more").hide();

        } else if (currentChatType == "GROUP") {
            showTempGroupInfoAndMessages("GROUP", tempGid);
            $(".js_chat_group_temp").hide();
            $(".js_chat_group_temp_up").css({
                "display": "none"
            });
            $(".js_chat_one_more").show();
        }
        var messageNum = target.find(".groupchat_number");
        var num = messageNum.find(".groupchat_number_info");
        num.html("0");
        messageNum.css({
            "visibility": "hidden"
        });
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
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var tempGroup = tempGroupsInfo[currentChatGroup.tempGid];
        $.ajax({
            type: "POST",
            url: "/api2/group/create?",
            data: {
                phone: accountObj.phone,
                accessKey: accountObj.accessKey,
                type: "upgradeGroup",
                name: accountObj.phone + "群组",
                members: JSON.stringify(tempGroup.members)
            },
            success: function (data) {
                if (data["提示信息"] == "创建群组成功") {
                    var group = data.group;
                    group.members = tempGroup.members;
                    delete tempGroupsInfo[tempGroup.tempGid];
                    groupsInfo[group.gid] = group;
                    window.sessionStorage.setItem("wxgs_tempGroupsInfo", JSON.stringify(tempGroupsInfo));
                    window.sessionStorage.setItem("wxgs_groupsInfo", JSON.stringify(groupsInfo));
                    // 临时群组的消息转换为正式群组的消息，但是数据格式不一致，选择放弃之前的临时群组聊天数据
                    /*var messages = tempAccountChatMessages["t_" + tempGroup.tempGid];
                     if (messages != undefined) {
                     tempAccountChatMessages["g_" + group.gid] = messages;
                     }*/
                    $(".js_chat_group_temp").hide();
                    $(".js_chat_group_temp_up").css({
                        "display": "none"
                    });
                    $(".js_chat_one_more").show();
                } else {
                    alert(data["提示信息"] + "," + data["失败原因"]);
                }
            }
        });
    });
    $(document).on("click", ".js_chat_group_left_icon", function () {
        var maxLeft = $(".js_chat_group_friends").width() - 310;
        var margin_left = parseInt(($(".js_chat_group_friends").css("margin-left")).replace("px", ""));
        if (margin_left + 60 <= 0) {
            $(".js_chat_group_friends").css({
                "margin-left": margin_left + 60 + "px"
            });
        } else {
            alert("已经到最左侧了");
        }
    });
    $(document).on("click", ".js_chat_group_right_icon", function () {
        var maxLeft = ($(".js_chat_group_friends").width()) - 310;
        var margin_left = parseInt(($(".js_chat_group_friends").css("margin-left")).replace("px", ""));
//        alert(maxLeft);
        if (margin_left - 60 > -maxLeft) {
            $(".js_chat_group_friends").css({
                "margin-left": margin_left - 60 + "px"
            });
        } else {
            alert("已经到最右侧了");
        }
    });
    $(document).on("click", ".js_chat_one_more", function () {
        alert("js_chat_one_more");
    });
    $(document).on("click", ".js_chat_group_friends .js_chat_group_friends_onlyone", function () {
//        alert($(this).attr("phone"));
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
});
function getGroupFinalMessage(type, gid) {
    if (type == "tempGroup") {
        var messages = tempAccountChatMessages["t_" + gid];
        if (messages != undefined) {
            var content = JSON.parse(messages[messages.length - 1]).content;
            return content.substr(0, 10) + "...";
        } else {
            return "";
        }
    } else if (type == "group") {
        var messages = tempAccountChatMessages["g_" + gid];
        if (messages != undefined) {
            var content = JSON.parse(messages[messages.length - 1]).content;
            return content.substr(0, 10) + "...";
        } else {
            return "";
        }
    }
}
function showTempGroupInfoAndMessages(type, tempGid) {
//    var tempGid = target.attr("group_gid");

    getTemplateHtml("group_users", function (template) {
        if (type == "TEMPGROUP") {
            currentChatGroup = tempGroupsInfo[tempGid];
        } else if (type == "GROUP") {
            currentChatGroup = groupsInfo[tempGid];
        }
        var length = (currentChatGroup.members).length;
        $(".js_chat_group_friends").html(template.render(currentChatGroup));
        var tempGroupName = currentChatGroup.name;
        if (tempGroupName != undefined && tempGroupName != null && tempGroupName != "") {
            $(".js_chat_group_info_groupName").html(tempGroupName);
        } else {
            $(".js_chat_group_info_groupName").html("临时群");
        }
        $(".js_chat_group_info_count").html("(" + length + "人)");
        if (length > 5) {
            var left = (length + 1) * 60 - 300;
            $(".js_chat_group_friends").css({
                "margin-left": -left + "px",
                "width": (length + 2) * 60 + "px"
            });
        } else {
            $(".js_chat_group_friends").css({
                "margin-left": "0px"
            });
        }
    });
    var firstStr = (currentChatType.toLowerCase()).substr(0, 1);
    if (tempAccountChatMessages[firstStr + "_" + tempGid] != undefined) {
        ($(".js_chatContents").find(".noMsgIip")).html("");
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").html(js_chatmessagetemplate.render(tempAccountChatMessages[firstStr + "_" + tempGid]));
    } else {
        var htmlStr = '<div class="noMsgIip" ide="noMsgTip">' +
            '<div class="noMsgTipPic"></div>' +
            '<p>暂时没有新消息</p>' +
            '</div>';
        $(".js_chatContents").html(htmlStr);
    }
    setScrollPosition();
}
function getUserAllGroups() {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "GET",
        url: "/api2/group/getgroupsandmembers?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey
        },
        success: function (data) {
            if (data["提示信息"] == "获取群组成功") {
                getTemplateHtml("user_groups", function (template) {
                    var groups = data.groups;
                    for (var index in groups) {
                        var group = groups[index];
                        var members = group.members;
                        var membersPhone = [];
                        for (var i = 0; i < members.length; i++) {
                            var member = members[i];
                            allCirclesFriends[member.phone] = member;
                            membersPhone.push(member.phone);
                        }
                        var groupData = {
                            gid: group.gid,
                            name: group.name,
                            members: membersPhone
                        }
                        $(".js_user_groups").append(template.render([groupData]));
                        groupsInfo[group.gid] = groupData;
                    }
                    window.sessionStorage.setItem("wxgs_allCirclesFriends", JSON.stringify(allCirclesFriends));
                });
            } else {
                alert(data["提示信息"] + "," + data["失败原因"]);
            }
        }
    });
}