var serverSetting = root.globaldata.serverSetting;
var groupManage = {};
var verifyEmpty = require("./../lib/verifyParams.js");
var push = require('../lib/push.js');
var ajax = require("./../lib/ajax.js");
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
/***************************************
 *     URL：/api2/group/create
 ***************************************/
groupManage.create = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var tempGid = data.tempGid;
    var type = data.type;//createTempGroup,createGroup,upgradeGroup
    var name = data.name;
    var members = data.members;
    var location = data.location;
    var address = data.address;
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    console.log("phone:" + phone + "tempGid:" + tempGid + ",name:" + name + ",members:" + members);
    if (type == "createTempGroup") {
        var timeGid = new Date().getTime() + "";
        if (tempGid != undefined) {
            timeGid = tempGid;
        }
        var group = {
            tempGid: timeGid,
            name: name,
            members: JSON.parse(members)
        };
        client.hset("tempGroup", timeGid, JSON.stringify(group), function (err, reply) {
            if (err != null) {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常",
                    tempGid: tempGid
                }));
                response.end();
                console.log(err);
                return;
            } else if (reply == 1) {
                response.write(JSON.stringify({
                    "提示信息": "创建群组成功",
                    tempGid: timeGid
                }));
                response.end();
                return;
            } else if (reply == 0) {
                response.write(JSON.stringify({
                    "提示信息": "更新群组成功",
                    tempGid: timeGid
                }));
                response.end();
                return;
            }
        });
        //session 推送提示信息给用户，群组信息更新的提醒

    } else if (type == "upgradeGroup" || type == "createGroup") {

        var list = [phone, name, members];
        if (verifyEmpty.verifyEmpty(data, list, response)) {
            try {
                members = JSON.parse(members);
                if (location != undefined) {
                    location = JSON.parse(location);
                }
            } catch (e) {
                console.log(e + "数据格式不正确");
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据格式不正确",
                    tempGid: tempGid
                }));
                response.end();
                return;
            }
            createGroupNode();
        }
    }


    function createGroupNode() {
        var group = {
            name: name,
            icon: "978b3e6986071e464fd6632e1fd864652c42ca27.png",
            gtype: data.gtype,
            createTime: new Date().getTime(),
            nodeType: "Group"
        }
        group.description = data.description || "请输入群组描述信息";
        if (location) {
            group.location = JSON.stringify({
                longitude: location.longitude,
                latitude: location.latitude
            });
        }
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'CREATE account-[r:HAS_GROUP]->(group:Group{group})',
            'SET group.gid=ID(group),r.relation={relation}',
            'RETURN group,r'
        ].join('\n');
        var params = {
            phone: phone,
            group: group,
            relation: "join"
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常",
                    tempGid: tempGid
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var group = results.pop().group.data;
                createGroupLocation(group);
                createShares(group.gid);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "用户不存在",
                    tempGid: tempGid
                }));
                response.end();
            }
        });
    }

    function createShares(gid) {
        var query = [
            "MATCH (group:Group)",
            "WHERE group.gid={gid}",
            "CREATE UNIQUE group-[r:SHARE]->(shares:Shares{shares})",
            "SET shares.sid=ID(shares)",
            "RETURN group,shares"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            shares: {
                name: "主版",
                gid: parseInt(gid),
                nodeType: "Shares",
                status: "active",
                type: "Main",
                createTime: new Date().getTime()
            }
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常",
                    tempGid: tempGid
                }));
                response.end();
            } else if (results.length > 0) {
                var group = results.pop().group.data;
                if (members.length > 0) {
                    addMembersToGroup(parseInt(gid), members);
                    console.log("开始初始化群组第一批用户个数:" + members.length);
                } else {
                    console.log("未初始化群组第一批用户");
                    response.write(JSON.stringify({
                        "提示信息": "创建群组成功",
                        group: group,
                        tempGid: tempGid,
                        address: address
                    }));
                    response.end();
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "group_create",
                        content: JSON.stringify({
                            type: "group_create",
                            time: new Date().getTime(),
                            phone: phone,
                            gid: group.gid,
                            eid: eid,
                            group: group,
                            content: 1
                        })
                    });
                    client.rpush(phone, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phone, accessKey, "*", event);
//                    setGroupLBSLocation(phone, data.accessKey, location, group);
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常",
                    tempGid: tempGid
                }));
                response.end();
            }
        });
    }

    function addMembersToGroup(gid, members) {
        var query = [
            'START group=node({gid})',
            'MATCH (account:Account)',
            'WHERE account.phone IN {members}',
            'CREATE UNIQUE group-[r:HAS_MEMBER]->account',
            'SET r.relation={relation}',
            'RETURN r,group'
        ].join('\n');
        var params = {
            gid: parseInt(gid),
            members: members,
            relation: "join"
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常",
                    tempGid: tempGid
                }));
                response.end();
                console.log(error);
                return;
            } else {
                console.log("初始化的群组好友成功的个数:" + results.length);
                var groupData = results[0].group.data;
                var group = {
                    gid: groupData.gid,
                    icon: groupData.icon,
                    name: groupData.name,
                    notReadMessagesCount: 0,
                    distance: 0,
                    createTime: groupData.createTime,
                    longitude: location.longitude || 0,
                    latitude: location.latitude || 0,
                    description: groupData.description,
                    background: groupData.background,
                    cover: groupData.cover || "",
                    permission: groupData.permission || ""
                };
                if (groupData.boardSequenceString) {
                    try {
                        group.boards = JSON.parse(groupData.boardSequenceString);
                    } catch (e) {
                        group.boards = [];
                    }
                } else {
                    group.boards = [];
                }
                response.write(JSON.stringify({
                    "提示信息": "创建群组成功",
                    group: group,
                    tempGid: tempGid,
                    address: address
                }));
                response.end();
                var event1 = JSON.stringify({
                    sendType: "event",
                    contentType: "group_create",
                    content: JSON.stringify({
                        type: "group_create",
                        time: new Date().getTime(),
                        phone: phone,
                        gid: group.gid,
                        eid: eid,
                        content: members.length
                    })
                });
                var event2 = JSON.stringify({
                    sendType: "event",
                    contentType: "group_addme",
                    content: JSON.stringify({
                        type: "group_addme",
                        time: new Date().getTime(),
                        phone: phone,
                        gid: group.gid,
                        eid: eid,
                        content: members.length
                    })
                });
                for (var index in members) {
                    var phoneTo = members[index];
                    var event = event2;
                    if (phoneTo == phone) {
                        event = event1;
                    }
                    //{"提示信息": "成功", event: "groupstatuschanged", event_content: {gid: group.gid, operation: true}}
                    client.rpush(phoneTo, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phoneTo, accessKey, "*", event);
                }
//                setGroupLBSLocation(phone, data.accessKey, location, group);
            }
        });
    }

    function createGroupLocation(group) {
        var location = JSON.parse(group.location)
        location = JSON.stringify([location.longitude, location.latitude]);

        ajax.ajax({
            type: "POST",
            url: serverSetting.LBS_GROUP_CREATE,
            data: {
                location: location,
                primaryKey: group.gid,
                data: JSON.stringify({
                    gid: group.gid,
                    head: group.icon || "",
                    nickName: encodeURI(group.name || ""),
                    mainBusiness: encodeURI(group.description || ""),
                    cover: group.cover || "",
                    permission: group.permission || "",
                    time: group.createTime || new Date().getTime()
                })
            }, success: function (info) {
                try {
                    if (info.提示信息 == "创建成功") {
                        console.log("success--")
                    } else {
                        console.log("error--create")
                    }
                } catch (e) {
                    console.log(e);
                    return;
                }
            }
        });

    }
}
function setGroupLBSLocation(phone, accessKey, location, group) {
    var Data = {};
    var Group = {};
    if (group.gid) {
        Group.gid = group.gid;
    }
    Group.icon = group.icon || "978b3e6986071e464fd6632e1fd864652c42ca27.png";
    Group.name = group.name || "新建群组";
    Group.description = group.description || "请输入群组描述信息";
    Data.location = location || JSON.stringify({longitude: 116.422324, latitude: 39.906744});
    Data.phone = phone;
    Data.accessKey = accessKey;
    Data.group = JSON.stringify(Group);
    ajax.ajax({
        url: "http://127.0.0.1:8076/lbs/setgrouplocation?",
        type: "POST",
        data: Data,
        success: function (data) {
            data = JSON.parse(data);
            if (data["提示信息"] == "标记群组位置成功") {
                console.log(data["提示信息"] + "---" + data.gid);
            } else {
                console.error(data["提示信息"] + "---" + data["失败原因"]);
            }
        }, error: function (e) {
            console.error("标记群组位置异常" + e);
        }
    });
}
/***************************************
 *     URL：/api2/group/addmembers
 ***************************************/
groupManage.addmembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var members = data.members;
    var time = new Date().getTime();
    var eid = phone + "" + time;
    console.log("phone:" + phone + "gid:" + gid + ",members:" + members);
    var list = [phone, gid, members];
    if (verifyEmpty.verifyEmpty(data, list, response)) {
        try {
            gid = parseInt(gid);
            if (isNaN(gid))
                throw "gid不是数值";
            members = JSON.parse(members);
        } catch (e) {
            console.log(e + "数据格式不正确");
            response.write(JSON.stringify({
                "提示信息": "加入群组失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
            return;
        }
        checkGroupNode(gid);
    }

    function checkGroupNode(gid) {
        var query = [
            'MATCH (group:Group)-[r:HAS_MEMBER]->(account:Account)',
            'WHERE group.gid={gid}',
            'RETURN group,account'
        ].join('\n');
        var params = {
            gid: gid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "加入群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var accounts = {};
                for (var index in results) {
                    var accountData = results[index].account.data;
                    accounts[accountData.phone] = accountData;
                }
                addMembersToGroup(gid, members, accounts);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "加入群组失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function addMembersToGroup(gid, members, accounts) {
        var query = [
            'START group=node({gid})',
            'MATCH (account:Account)',
            'WHERE account.phone IN {members}',
            'CREATE UNIQUE group-[r:HAS_MEMBER]->account',
            'SET r.relation={relation}',
            'RETURN r'
        ].join('\n');
        var params = {
            gid: gid,
            members: members,
            relation: "join"
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "加入群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                console.log("加入群組成功的好友个数:" + results.length);
                response.write(JSON.stringify({
                    "提示信息": "加入群组成功"
                }))
                response.end();
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "group_addmembers",
                    content: JSON.stringify({
                        type: "group_addmembers",
                        time: new Date().getTime(),
                        phone: phone,
                        gid: gid,
                        eid: eid,
                        content: members.length
                    })
                });
                for (var index in accounts) {
                    // {"提示信息": "成功", event: "groupmemberchanged", event_content: {gid: gid}}
                    client.rpush(index, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, index, accessKey, "*", event);
                }
                for (var index in members) {
                    //{"提示信息": "成功", event: "groupstatuschanged", event_content: {gid: gid, operation: true}}
                    var phoneTo = members[index];
                    var event2 = JSON.stringify({
                        sendType: "event",
                        contentType: "group_addme",
                        content: JSON.stringify({
                            type: "group_addme",
                            time: new Date().getTime(),
                            phone: phone,
                            phoneTo: phoneTo,
                            gid: gid,
                            eid: eid,
                            content: members.length
                        })
                    });
                    client.rpush(phoneTo, event2, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phoneTo, accessKey, "*", event2);
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "加入群组失败",
                    "失败原因": "好友不存在"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/group/removemembers
 ***************************************/
groupManage.removemembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var members = data.members;
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    console.log("phone:" + phone + "gid:" + gid + ",members:" + members);
    var list = [phone, gid, members];
    if (verifyEmpty.verifyEmpty(data, list, response)) {
        try {
            gid = parseInt(gid);
            if (isNaN(gid))
                throw "gid不是数值";
            members = JSON.parse(members);
        } catch (e) {
            console.log(e + "数据格式不正确");
            response.write(JSON.stringify({
                "提示信息": "退出群组失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
            return;
        }
        checkGroupNode(gid);
    }

    function checkGroupNode(gid) {
        var query = [
            'MATCH (group:Group)-[r:HAS_MEMBER]->(account:Account)',
            'WHERE group.gid={gid}',
            'RETURN group,account'
        ].join('\n');
        var params = {
            gid: gid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "退出群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log("checkGroupNode" + error);
                return;
            } else if (results.length > 0) {
                var accounts = {};
                for (var index in results) {
                    var accountData = results[index].account.data;
                    accounts[accountData.phone] = accountData;
                }
                removeMembersToGroup(gid, members, accounts);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "退出群组失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function removeMembersToGroup(gid, members, accounts) {
        var query = [
            'START group=node({gid})',
            'MATCH group-[r:HAS_MEMBER]->(account:Account)',
            'WHERE account.phone IN {members}',
            'DELETE r',
            'RETURN group'
        ].join('\n');
        var params = {
            gid: gid,
            members: members
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "退出群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log("removeMembersToGroup" + error);
                return;
            } else if (results.length > 0) {
                console.log("退出群组好友成功的个数：" + results.length);
                response.write(JSON.stringify({
                    "提示信息": "退出群组成功"
                }))
                response.end();
                var removeMembers = {};
                for (var index in members) {
                    var removePhone = members[index];
                    removeMembers[removePhone] = removePhone;
                    var event1 = JSON.stringify({
                        sendType: "event",
                        contentType: "group_removeme",
                        content: JSON.stringify({
                            type: "group_removeme",
                            time: new Date().getTime(),
                            phone: phone,
                            phoneTo: removePhone,
                            gid: gid,
                            eid: eid,
                            content: members.length
                        })
                    });
                    // {"提示信息": "成功", event: "groupstatuschanged", event_content: {gid: gid, operation: false}}
                    client.rpush(removePhone, event1, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, removePhone, accessKey, "*", event1);
                }
                var event2 = JSON.stringify({
                    sendType: "event",
                    contentType: "group_removemembers",
                    content: JSON.stringify({
                        type: "group_removemembers",
                        time: new Date().getTime(),
                        phone: phone,
                        gid: gid,
                        eid: eid,
                        content: members.length
                    })
                });
                for (var index in accounts) {
                    if (!removeMembers[index]) {
                        //{"提示信息": "成功", event: "groupmemberchanged", event_content: {gid: gid}}
                        client.rpush(index, event2, function (err, reply) {
                            if (err) {
                                console.error("保存Event失败");
                            } else {
                                console.log("保存Event成功");
                            }
                        });
                        push.inform(phone, index, accessKey, "*", event2);
                    }
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "退出群组失败",
                    "失败原因": "好友不存在该组"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/group/getallmembers
 ***************************************/
groupManage.getallmembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var gid = data.gid;
    console.log("phone:" + phone + ",gid:" + gid);
    var list = [phone, gid];
    if (verifyEmpty.verifyEmpty(data, list, response)) {
        try {
            gid = parseInt(gid);
            if (isNaN(gid))
                throw "gid不是数值";
        } catch (e) {
            console.log(e + "数据格式不正确");
            response.write(JSON.stringify({
                "提示信息": "获取群组成员失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
            return;
        }
        checkGroupNode(gid);
    }
    function checkGroupNode(gid) {
        var query = [
            'MATCH (group:Group)',
            'WHERE group.gid={gid}',
            'RETURN group'
        ].join('\n');
        var params = {
            gid: gid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log("checkGroupNode" + error);
                return;
            } else if (results.length > 0) {
                var groupData = results.pop().group.data;
                var location;
                try {
                    location = JSON.parse(groupData.location);
                } catch (e) {
                    location = {
                        longitude: 0,
                        latitude: 0
                    }
                }
                var group = {
                    gid: groupData.gid,
                    icon: groupData.icon || "",
                    name: groupData.name,
                    longitude: location.longitude,
                    latitude: location.latitude,
                    createTime: groupData.createTime,
                    description: groupData.description || "",
                    background: groupData.background || "",
                    cover: groupData.cover || "",
                    permission: groupData.permission || ""
                };
                if (groupData.boardSequenceString) {
                    try {
                        group.boards = JSON.parse(groupData.boardSequenceString);
                    } catch (e) {
                        group.boards = [];
                    }
                } else {
                    group.boards = [];
                }
                getGroupMembers(gid, group);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function getGroupMembers(gid, group) {
        var query = [
            'START group=node({gid})',
            'MATCH group-[r:HAS_MEMBER]->(account:Account)',
            'RETURN account'
        ].join('\n');
        var params = {
            gid: gid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var members = [];
                var membersMap = {};
                for (var i = 0; i < results.length; i++) {
                    var member = results[i].account.data;
                    var account = {
                        id: member.ID,
                        phone: member.phone,
                        nickName: member.nickName,
                        mainBusiness: member.mainBusiness || "",
                        head: member.head || "Head",
                        sex: member.sex,
                        age: member.age,
                        byPhone: member.byPhone,
                        longitude: member.longitude || "0",
                        latitude: member.latitude || "0",
                        createTime: member.createTime,
                        lastLoginTime: member.lastlogintime || "",
                        userBackground: member.userBackground || "Back"
                    };
                    members.push(account.phone);
                    membersMap[account.phone] = account;
                }
                console.log(gid + "群組好友个数：" + members.length);
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    group: group,
                    members: members,
                    membersMap: membersMap
                }))
                response.end();
            } else {
                console.log(gid + "群組好友个数：" + results.length);
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    group: group,
                    members: [],
                    membersMap: {}
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/group/modify
 ***************************************/

groupManage.modify = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var icon = data.icon;
    var name = data.name;
    var background = data.background;
    var description = data.description;
    var address = data.address;
    var location = data.location;
    var cover = data.cover;
    if (!cover) {
        cover = data.conver;
    }
    var permission = data.permission;
    var time = new Date().getTime();
    var eid = phone + "" + time;
    console.log("phone:" + phone + ",gid:" + gid + ",name:" + name);
    var list = [phone, gid];
    if (verifyEmpty.verifyEmpty(data, list, response)) {
        try {
            gid = parseInt(gid);
            if (location) {
                location = JSON.parse(location);
            }
            if (isNaN(gid))
                throw "gid不是数值";
        } catch (e) {
            console.log(e + "数据格式不正确");
            response.write(JSON.stringify({
                "提示信息": "修改群组信息失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
            return;
        }
        checkGroupNode(parseInt(gid));
    }

    function checkGroupNode(gid) {
        console.log(gid);
        var query = [
            'MATCH (group:Group)-[r:HAS_MEMBER]->(account:Account)',
            'WHERE group.gid={gid}',
            'RETURN group,account'
        ].join('\n');
        var params = {
            gid: gid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log("checkGroupNode" + error);
                return;
            } else if (results.length > 0) {
                var accounts = {};
                for (var index in results) {
                    var accountData = results[index].account.data;
                    accounts[accountData.phone] = accountData;
                    console.log(accountData.phone);
                }
                modifyGroupNode(results[0].group.data.gid, accounts);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function modifyGroupNode(gid, accounts) {
        var query = [
            'MATCH (group:Group)',
            'WHERE group.gid={gid}',
            'RETURN group'
        ].join('\n');
        var params = {
            gid: gid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var isUpdateLbsData = false;
                var groupNode = results.pop().group;
                var groupData = groupNode.data;
                var groupLocation = groupData.location || JSON.stringify({longitude: 116.422324, latitude: 39.906744});
                try {
                    groupLocation = JSON.parse(groupLocation);
                } catch (e) {
                    groupLocation = {longitude: 0, latitude: 0};
                }
                if (name) {
                    groupData.name = name || groupData.name;
                    isUpdateLbsData = true;
                }
                if (icon) {
                    groupData.icon = icon;
                    isUpdateLbsData = true;
                }
                if (description) {
                    groupData.description = description || groupData.description;
                    isUpdateLbsData = true;
                }
                var background0 = "";
                if (groupData.background) {
                    background0 = groupData.background;
                }
                if (background) {
                    background0 = background;
                    groupData.background = background0;
                }
                if (cover) {
                    groupData.cover = cover;
                    isUpdateLbsData = true;
                }
                if (permission) {
                    groupData.permission = permission;
                    isUpdateLbsData = true;
                }
                var currentLocation = {};
                if (location) {
                    currentLocation.longitude = location.longitude || groupLocation.longitude;
                    currentLocation.latitude = location.latitude || groupLocation.latitude;

                    groupData.location = JSON.stringify(currentLocation);
                    isUpdateLbsData = true;
                }
                groupData.lastModifyTime = time;
                groupNode.save(function (error) {
                });
                var group = {
                    gid: groupData.gid,
                    icon: groupData.icon,
                    name: groupData.name,
                    notReadMessagesCount: 0,
                    distance: 0,
                    createTime: groupData.createTime,
                    longitude: currentLocation.longitude || 0,
                    latitude: currentLocation.latitude || 0,
                    description: groupData.description,
                    background: groupData.background,
                    cover: groupData.cover || "",
                    permission: groupData.permission || ""
                };
                if (groupData.boardSequenceString) {
                    try {
                        group.boards = JSON.parse(groupData.boardSequenceString);
                    } catch (e) {
                        group.boards = [];
                    }
                } else {
                    group.boards = [];
                }
                modifyGroupLbs(groupData);
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息成功",
                    group: group
                }));
                response.end();
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "group_dataupdate",
                    content: JSON.stringify({
                        type: "group_dataupdate",
                        time: time,
                        phone: phone,
                        gid: gid,
                        eid: eid
                    })
                });
                for (var index in accounts) {
                    //{"提示信息": "成功", event: "groupinformationchanged", event_content: {gid: gid}}
                    client.rpush(index, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, index, accessKey, "*", event);
                }
                if (isUpdateLbsData) {
                    checkGroupIsExists(groupData);
                }
//                setGroupLBSLocation(phone, accessKey, JSON.stringify(currentLocation), groupData);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function modifyGroupLbs(group) {
        var location = JSON.parse(group.location);
        location = JSON.stringify([location.longitude, location.latitude]);
        ajax.ajax({
            type: "POST",
            url: serverSetting.LBS_GROUP_UPDATE,
            data: {
                location: location,
                primaryKey: group.gid,
                data: JSON.stringify({
                    head: group.icon || "",
                    nickName: encodeURI(group.name || ""),
                    mainBusiness: encodeURI(group.description || ""),
                    cover: group.cover || "",
                    permission: group.permission || "",
                    time: group.lastModifyTime || new Date().getTime()
                })
            }, success: function (info) {
                try {
                    if (info.提示信息 == "修改成功") {
                        console.log("success--")
                    } else {
                        console.log("error--create")
                    }
                } catch (e) {
                    console.log(e);
                    return;
                }
            }
        });
    }
}

/***************************************
 *     URL：/api2/group/get
 ***************************************/
groupManage.get = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var maxPrime = 100000000000;
    var gid = data.gid;
    var type = data.type;
    if (type == "tempGroup") {
        client.hget("tempGroup", gid, function (err, reply) {
            if (err != null) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(err);
                return;
            } else {
                console.log(reply);
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息成功",
                    group: JSON.parse(reply)
                }));
                response.end();
            }
        });
    } else {
        var list = [gid];
        if (verifyEmpty.verifyEmpty(data, list, response)) {
            try {
                gid = parseInt(gid);
                if (isNaN(gid)) {
                    response.write(JSON.stringify({
                        "提示信息": "获取群组信息失败",
                        "失败原因": "数据格式不正确"
                    }));
                    response.end();
                } else {
                    gid = gid;
                    getGroupNode(gid);
                }
            } catch (e) {
                console.log(e + "数据格式不正确");
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息失败",
                    "失败原因": "数据格式不正确"
                }));
                response.end();
                return;
            }
        }
    }
    function getGroupNode(gid) {
        var query = [
            'MATCH (group:Group)',
            'WHERE group.gid={gid}',
            'RETURN group'
        ].join('\n');
        var params = {
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var groupData = results.pop().group.data;
                var location;
                try {
                    location = JSON.parse(groupData.location);
                } catch (e) {
                    location = {
                        longitude: 0,
                        latitude: 0
                    }
                }
                var group = {
                    gid: groupData.gid,
                    icon: groupData.icon || "",
                    name: groupData.name,
                    longitude: location.longitude || 0,
                    latitude: location.latitude || 0,
                    createTime: groupData.createTime || 0,
                    description: groupData.description || "",
                    background: groupData.background || "",
                    cover: groupData.cover || "",
                    permission: groupData.permission || "",
                    labels: []
                };
                if (groupData.boardSequenceString) {
                    try {
                        group.boards = JSON.parse(groupData.boardSequenceString);
                    } catch (e) {
                        group.boards = [];
                    }
                } else {
                    group.boards = [];
                }
                getLabels(group);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
            function getLabels(group) {
                var query = [
                    "MATCH(group:Group)-[r:HAS_LABEL]->(label:Label)",
                    "WHERE group.gid={gid}",
                    "RETURN group,label"
                ].join('\n');
                var params = {
                    gid: group.gid
                };
                db.query(query, params, function (error, results) {
                    if (error) {
                        ResponseData(JSON.stringify({
                            "提示信息": "获取群组标签失败",
                            "失败原因": "数据异常"
                        }), response);
                        console.error(error);
                        return;
                    } else if (results.length >= 0) {
                        var labels = [];
                        for (var index in results) {
                            var label = results[index].label.data;
                            labels.push(label.name);
                        }
                        group.labels = labels;
                        response.write(JSON.stringify({
                            "提示信息": "获取群组信息成功",
                            group: group
                        }));
                        response.end();
                    }
                });
            }

        });
    }
}

var ajax = require('../../mcserver/lib/ajax.js');
/*******************************************************************
 * * * * * * * * * *New Api* * * * * * * * * * * * * * * * * * * * *
 *******************************************************************/
groupManage.getgroupmembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;

    var startTime = new Date().getTime();

    getAccountGroups();


    var friendsMap = {};
    var groups = [];
    var groupsMap = {};
    var groupCircles = [];
    var groupCirclesMap = {};

    function getAccountGroups() {
        var query = [
            'MATCH (account:Account)<-[r:HAS_MEMBER]-(group:Group)',
            'WHERE account.phone={phone}',
            'RETURN account,group,r'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            console.log("第一次查询query 得到数据：" + ((new Date().getTime()) - startTime));
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length > 0) {
                var length = results.length;
                var count = 0;
                var accountNode;
                var groupOrder = {};
                for (var index in results) {
                    if (count == 0) {
                        accountNode = results[index].account;
                    }
                    count++;
                    var groupData = results[index].group.data;
                    groups.push(parseInt(groupData.gid));
                    groupOrder[groupData.gid] = index;
                    if (count == length) {
                        var accountData = accountNode.data;
                        var groupsOrdering = null;
                        if (accountData.groupsSequenceString) {
                            try {
                                groupsOrdering = JSON.parse(accountData.groupsSequenceString);
                            } catch (e) {
                                groupsOrdering = null
                            }
                        }
                        var newCirclesOrdering;
                        if (groupsOrdering == null) {
                            accountData.groupsSequenceString = JSON.stringify(groups);
                            accountNode.save(function (err, node) {
                                if (!err) {
                                    console.log("重置群组顺序数据成功");
                                }
                            });
                        } else {
                            newCirclesOrdering = [];
                            var isDataConsistentcy = true;
                            for (var index in groupsOrdering) {
                                var gid = groupsOrdering[index];
                                if (groupOrder[gid]) {
                                    newCirclesOrdering.push(parseInt(gid));
                                    groupOrder[gid] = "delete";
                                } else {
                                    isDataConsistentcy = false;
                                }
                            }
                            for (var index in groupOrder) {
                                if (isDataConsistentcy && groupOrder[index] != "delete") {
                                    isDataConsistentcy = false;
                                }
                                if (groupOrder[index] != "delete") {
                                    newCirclesOrdering.push(parseInt(index));
                                }
                            }
                            if (!isDataConsistentcy) {
                                accountData.groupsSequenceString = JSON.stringify(newCirclesOrdering);
                                accountNode.save(function (err, node) {
                                    console.log("初始化群组顺序数据成功");
                                });
                            }
                            groups = newCirclesOrdering;
                        }
//                        console.error(groups);

                        var groupCirclesOrderString = accountData.groupCirclesOrderString;
                        var flag = false;
                        if (groupCirclesOrderString) {
                            try {
                                var groupCircleOrder = JSON.parse(groupCirclesOrderString);
                                for (var index in groupCircleOrder) {
                                    var groupCircle = groupCircleOrder[index];
                                    if (groupCircle.rid) {
                                        groupCircles.push(groupCircle.rid);
                                        groupCirclesMap[groupCircle.rid] = groupCircle;
                                    } else {
                                        flag = true;
                                    }
                                }
                            } catch (e) {
                                flag = true;
                            }
                        } else {
                            flag = true;
                        }
                        if (flag) {
                            groupCircles = [];
                            groupCirclesMap = {};
                            groupCircles.push("8888888");
                            groupCirclesMap["8888888"] = {
                                rid: "8888888",
                                name: "默认分组",
                                groups: []
                            };
                            for (var index1 in results) {
                                var groupData1 = results[index1].group.data;
                                var rNode = results[index1].r;
                                var rData = rNode.data;
                                groupCirclesMap["8888888"].groups.push(groupData1.gid);
                            }
                            accountData.groupCirclesOrderString = JSON.stringify([
                                {
                                    rid: "8888888",
                                    name: "默认分组",
                                    groups: groupCirclesMap["8888888"].groups
                                }
                            ]);
                            accountNode.save(function (err, node) {
                                console.log("初始化群组分组顺序.");
                            });
                        }
                        getGroupsMembers(groups);
                    }
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    relationship: {
                        friendsMap: friendsMap,
                        groups: groups,
                        groupsMap: groupsMap,
                        groupCircles: groupCircles,
                        groupCirclesMap: groupCirclesMap
                    }
                }), response);
            }
        });
    }

    function getGroupsMembers(groupIDs) {
        console.log("第二次查询query ：" + ((new Date().getTime()) - startTime));
        var query = [
            'MATCH (group:Group)-[r:HAS_MEMBER]->(account:Account)',
            'WHERE group.gid IN {groupIDs}',
            'RETURN group,account,r'
        ].join('\n');
        var params = {
            groupIDs: groupIDs
        };
        db.query(query, params, function (error, results) {
            console.log("第二次查询query 得到数据：" + ((new Date().getTime()) - startTime));
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else {
//                groups = [];
                for (var index in results) {
                    var it = results[index];
                    var groupData = it.group.data;
                    var accountData = it.account.data;
                    var rData = it.r.data;
                    var location;

                    if (groupData.location) {
                        try {
                            location = JSON.parse(groupData.location);
                        } catch (e) {
                            location = {
                                longitude: "0",
                                latitude: "0"
                            };
                        }
                    } else {
                        location = {
                            longitude: "0",
                            latitude: "0"
                        };
                    }
                    var account = {
                        id: accountData.ID,
                        sex: accountData.sex,
                        age: accountData.age,
                        phone: accountData.phone,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        nickName: accountData.nickName,
                        userBackground: accountData.userBackground,
                        addMessage: "",
                        friendStatus: "",
                        alias: "",
                        distance: 0,
                        createTime: accountData.createTime,
                        lastLoginTime: accountData.lastlogintime,
                        longitude: accountData.longitude || 0,
                        latitude: accountData.latitude || 0
                    };
                    friendsMap[account.phone] = account;
                    if (!groupsMap[groupData.gid + ""]) {
                        var group = {
                            gid: groupData.gid,
                            icon: groupData.icon,
                            name: groupData.name,
                            notReadMessagesCount: 0,
                            distance: 0,
                            createTime: groupData.createTime,
                            longitude: location.longitude || 0,
                            latitude: location.latitude || 0,
                            description: groupData.description,
                            background: groupData.background,
                            cover: groupData.cover || "",
                            permission: groupData.permission || "",
                            relation: rData.relation || "join",
                            labels: []
                        };
                        //console.log(groupData.name + "---" + rData.relation);
                        if (groupData.boardSequenceString) {
                            try {
                                group.boards = JSON.parse(groupData.boardSequenceString);
                                //group.boards = groupData.boardSequenceString;
                            } catch (e) {
                                group.boards = [];
                            }
                        } else {
                            group.boards = [];
                        }
                        var members = [];
                        if (rData.relation == "join") {//join follow
                            members.push(account.phone);
                        }
                        group.members = members;
                        groupsMap[groupData.gid + ""] = group;
                    } else {
                        var group = groupsMap[groupData.gid + ""];
                        if (rData.relation == "join") {//join follow
                            group.members.push(account.phone);
                        }
                        groupsMap[groupData.gid + ""] = group;
                    }
                    if (phone == account.phone) {
                        var group = groupsMap[groupData.gid + ""];
                        group.relation = rData.relation;
                    }
                }
                getLabels(groups);
            }
        });
    }

    function getLabels(groups) {
        console.log("第三次查询query：" + ((new Date().getTime()) - startTime));
        var query = [
            "MATCH(group:Group)-[r:HAS_LABEL]->(label:Label)",
            "WHERE group.gid IN {groupIDs}",
            "RETURN group,label"
        ].join('\n');
        var params = {
            groupIDs: groups
        };
        db.query(query, params, function (error, results) {
            console.log("第三次查询query  得到数据：" + ((new Date().getTime()) - startTime));
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组标签失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length >= 0) {
                for (var index in results) {
                    var labelData = results[index].label.data;
                    var groupData = results[index].group.data;

                    var group = groupsMap[groupData.gid + ""];
                    if (!group.labels) {
                        group.labels = [];
                    }
                    group.labels.push(labelData.name);
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    relationship: {
                        friendsMap: friendsMap,
                        groups: groups,
                        groupsMap: groupsMap,
                        groupCircles: groupCircles,
                        groupCirclesMap: groupCirclesMap
                    }
                }), response);
                console.log("返回数据：" + ((new Date().getTime()) - startTime));
            }
        });
    }
}

var GRID = -1;
var GRIDclient = redis.createClient(serverSetting.redisPort, "112.126.71.180");
GRIDclient.get("GRID", function (err, reply) {
    if (err != null) {
        console.error(err + "as");
        throw "分组RID初始化失败...请查看112.126.71.180服务器";
        return;
    } else {
        if (reply == null) {
            console.warn(reply + "a");
            throw "分组RID初始化失败...请查看112.126.71.180服务器";
            return;
        } else {
            console.log("GRID:" + reply + "...init data,from server...112.126.71.180");
            GRID = reply;
        }
    }
});


groupManage.creategroupcircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var name = data.name;
    var oldRid = data.rid;
    var accessKey = data.accessKey;
    if (verifyEmpty.verifyEmpty(data, oldRid, name, response)) {
        createGroupCircle();
    }
    function createGroupCircle() {
        var query = [
            "MATCH (account:Account{phone:{phone}})",
            "RETURN account"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    ResponseData(JSON.stringify({
                        "提示信息": "创建群组分组失败",
                        "失败原因": "数据异常"
                    }), response);
                    console.error(error + ":::创建群组分组失败");
                } else if (results.length > 0) {
                    var accountNode = results.pop().account;
                    var accountData = accountNode.data;
                    var groupCirclesOrderString = accountData.groupCirclesOrderString;
                    var flag = false;
                    var rid;

                    var orderObj = JSON.parse(groupCirclesOrderString);
                    rid = ++GRID;
                    orderObj.push({rid: rid, name: name, groups: []});
                    accountData.groupCirclesOrderString = JSON.stringify(orderObj);
                    accountNode.save(function (err, node) {
                    });
                    GRIDclient.set("GRID", GRID, function (err, reply) {
                    });

                    var time = new Date().getTime();
                    var eid = phone + "_" + time;
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "group_creategroupcircle",
                        content: JSON.stringify({
                            type: "group_creategroupcircle",
                            phone: phone,
                            eid: eid,
                            rid: rid,
                            name: name,
                            time: time,
                            status: "success",
                            content: ""
                        })
                    });
                    client.rpush(phone, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phone, accessKey, "*", event);

                    ResponseData(JSON.stringify({
                        "提示信息": "创建群组分组成功",
                        "groupCircle": {rid: rid, name: name, groups: []},
                        "oldRid": oldRid
                    }), response);
                }
                else {
                    ResponseData(JSON.stringify({
                        "提示信息": "创建群组分组失败",
                        "失败原因": "用户不存在"
                    }), response);
                }
            }
        )
        ;
    }
}
groupManage.deletegroupcircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var rid = data.rid;
    if (verifyEmpty.verifyEmpty(data, rid, phone, response)) {
        deleteGroupCircle();
    }
    function deleteGroupCircle() {
        var query = [
            'MATCH (account:Account)<-[r:HAS_MEMBER]-(group:Group)',
            'WHERE account.phone={phone} AND r.rid={rid}',
            "SET r.rid=8888888",
            'RETURN account,group,r'
        ].join("\n");
        var params = {
            phone: phone,
            rid: parseInt(rid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "删除群组分组失败",
                    "失败原因": "数据异常1"
                }), response);
                console.error(error);
            } else if (results.length >= 0) {
                deleteAccountGroupCircle();
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "删除群组分组失败",
                    "失败原因": "用户不存在1"
                }), response);
            }
        });
        function deleteAccountGroupCircle() {
            var query = [
                "MATCH (account:Account{phone:{phone}})",
                "RETURN account"
            ].join("\n");
            var params = {
                phone: phone
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    ResponseData(JSON.stringify({
                        "提示信息": "删除群组分组失败",
                        "失败原因": "数据异常2"
                    }), response);
                    console.error(error);
                } else if (results.length > 0) {
                    var pop = results.pop();
                    var accountNode = pop.account;
                    var accountData = accountNode.data;
                    var newGroupCirclesOrderString = [];
                    var groupCirclesOrderString = accountData.groupCirclesOrderString;
                    groupCirclesOrderString = JSON.parse(groupCirclesOrderString);
                    var deleteGroupCircle;
                    for (var index in groupCirclesOrderString) {
                        var groupCircle = groupCirclesOrderString[index];
                        if ((groupCircle.rid + "") != rid) {
                            newGroupCirclesOrderString.push(groupCircle);
                        } else {
                            deleteGroupCircle = groupCircle;
                        }
                    }
                    for (var index in newGroupCirclesOrderString) {
                        var groupCircle = newGroupCirclesOrderString[index];
                        if (groupCircle.rid == "8888888") {
                            for (var index1 in deleteGroupCircle.groups) {
                                var group = deleteGroupCircle.groups[index1];
                                groupCircle.groups.push(group);
                            }
                            break;
                        }
                    }
                    accountData.groupCirclesOrderString = JSON.stringify(newGroupCirclesOrderString);
                    accountNode.save(function (err, node) {
                    });
                    ResponseData(JSON.stringify({
                        "提示信息": "删除群组分组成功"
                    }), response);
                } else {
                    ResponseData(JSON.stringify({
                        "提示信息": "删除群组分组失败",
                        "失败原因": "用户不存在2"
                    }), response);
                }
            });
        }
    }
}
groupManage.modifygroupcircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var name = data.name;
    var dataRid = data.rid;
    var groupCircles = data.groupCircles;
    if (verifyEmpty.verifyEmpty(data, phone, response)) {
        modifyGroupCircle();
    }
    function modifyGroupCircle() {
        var query = [
            "MATCH (account:Account{phone:{phone}})",
            "RETURN account"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组分组失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                var pop = results.pop();
                var accountNode = pop.account;
                var accountData = accountNode.data;
                var groupCirclesOrderString = accountData.groupCirclesOrderString;
                groupCirclesOrderString = JSON.parse(groupCirclesOrderString);
                var flag = false;
                var newGroupCirclesOrderString = [];
                if (groupCircles != undefined && groupCircles != null && groupCircles != "") {
                    groupCircles = JSON.parse(groupCircles);
                    for (var index in groupCircles) {
                        var rid = groupCircles[index];
                        for (var index1 in groupCirclesOrderString) {
                            var groupCircleString = groupCirclesOrderString[index1];
                            var rid1 = groupCircleString.rid + "";
                            if (rid1 == rid) {
                                newGroupCirclesOrderString.push(groupCircleString);
                                break;
                            }
                        }
                    }
                    groupCirclesOrderString = newGroupCirclesOrderString;
                    accountData.groupCirclesOrderString = JSON.stringify(newGroupCirclesOrderString);
                }
                if (dataRid != undefined && dataRid != null && dataRid != "" && name != undefined && name != null && name != "") {
                    newGroupCirclesOrderString = [];
                    for (var index in groupCirclesOrderString) {
                        var groupCircle = groupCirclesOrderString[index];
                        if ((groupCircle.rid + "") == dataRid) {
                            groupCircle.name = name;
                        }
                        newGroupCirclesOrderString.push(groupCircle);
                    }
                    accountData.groupCirclesOrderString = JSON.stringify(newGroupCirclesOrderString);
                }
                accountNode.save(function (err, node) {
                });
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组分组成功"
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组分组失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}
groupManage.movegroupcirclegroups = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var rid = data.rid;
    var orid = data.orid;
    if (verifyEmpty.verifyEmpty(data, phone, gid, rid, orid, response)) {
        moveGroupCircleGroups();
    }
    function moveGroupCircleGroups() {
        var query = [
            'MATCH (account:Account)<-[r:HAS_MEMBER]-(group:Group)',
            'WHERE account.phone={phone} AND group.gid={gid}',
            'RETURN account,r'
        ].join("\n");
        var params = {
            phone: phone,
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "移动群组分组失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                var pop = results.pop();
                var accountNode = pop.account;
                var accountData = accountNode.data;
                var rNode = pop.r;
                var rData = rNode.data;
                var groupCirclesOrderString = accountData.groupCirclesOrderString;
                groupCirclesOrderString = JSON.parse(groupCirclesOrderString);
                for (var index in groupCirclesOrderString) {
                    var groupCircle = groupCirclesOrderString[index];
                    if (groupCircle.rid == rid) {
                        groupCircle.groups.push(parseInt(gid));
                    } else if (groupCircle.rid == parseInt(orid)) {
                        groupCircle.groups.splice(groupCircle.groups.indexOf(parseInt(gid)), 1);
                    }
                }
                accountData.groupCirclesOrderString = JSON.stringify(groupCirclesOrderString);
                rData.rid = parseInt(rid);
                accountNode.save(function (err, node) {
                });
                rNode.save(function (err, node) {
                });
                ResponseData(JSON.stringify({
                    "提示信息": "移动群组分组成功"
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "移动群组分组失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}
groupManage.modifygroupcirclesequence = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var groupSequence = data.sequence;
    var rid = data.rid;
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    modifyGroupSeqence();
    function modifyGroupSeqence() {
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组顺序失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组顺序失败",
                    "失败原因": "用户不存在"
                }), response);
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                var groupCirclesOrderString = accountData.groupCirclesOrderString;
                var newGroupSequence = [];
                groupSequence = JSON.parse(groupSequence);
                for (var index1 in groupSequence) {
                    var gid = groupSequence[index1];
                    newGroupSequence.push(parseInt(gid));
                }
                groupCirclesOrderString = JSON.parse(groupCirclesOrderString);
                var newGroupCirclesOrderString = [];
                for (var index in groupCirclesOrderString) {
                    var circle = groupCirclesOrderString[index];
                    if ((circle.rid + "") == rid) {
                        circle.groups = newGroupSequence;
                    }
                    newGroupCirclesOrderString.push(circle);
                }
                accountData.groupCirclesOrderString = JSON.stringify(newGroupCirclesOrderString);
                accountNode.save(function (err, node) {
                    if (err) {
                        console.info(err);
                    }
                });
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组顺序成功"
                }), response);
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "group_sequence",
                    content: JSON.stringify({
                        type: "group_sequence",
                        phone: phone,
                        time: time,
                        status: "success",
                        content: "",
                        eid: eid
                    })
                });
                client.rpush(phone, event, function (err, reply) {
                    if (err) {
                        console.error("保存Event失败");
                    } else {
                        console.log("保存Event成功");
                    }
                });
                push.inform(phone, phone, accessKey, "*", event);
            }
        });
    }
}

groupManage.movegroupstocircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var groups = JSON.parse(data.groups);
    var groupCircles = JSON.parse(data.groupCircles);
    var groupCirclesMap = JSON.parse(data.groupCirclesMap);
    var rid = data.rid;
    var arr = [phone, groups, groupCircles, groupCirclesMap, rid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        var gids = [];
        for (var index in groups) {
            var gid = groups[index];
            gids.push(parseInt(gid));
        }
        modifygroupcircle(gids);
    }
    function modifygroupcircle(groups) {
        var query = [
            'MATCH (account:Account)<-[r:HAS_MEMBER]-(group:Group)',
            'WHERE account.phone={phone} AND group.gid IN {groups}',
            'SET r.rid={rid}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone,
            groups: groups,
            rid: parseInt(rid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "移动群组到分组失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length > 0) {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                var newGroupCircles = [];
                for (var index in groupCircles) {
                    var groupCircle = groupCirclesMap[groupCircles[index]];
                    var newGroups = [];
                    for (var index1 in groupCircle.groups) {
                        var gid = groupCircle.groups[index1];
                        newGroups.push(parseInt(gid));
                    }
                    groupCircle.groups = newGroups;
                    newGroupCircles.push(groupCircle);
                }
                accountData.groupCirclesOrderString = JSON.stringify(newGroupCircles);
                accountNode.save(function (err, node) {
                    if (err) {
                        console.info(err);
                    }
                });
                ResponseData(JSON.stringify({
                    "提示信息": "移动群组到分组成功"
                }), response);

            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "移动群组到分组失败",
                    "失败原因": "群组不存在"
                }), response);
            }
        });
    }
}


groupManage.follow = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    if (verifyEmpty.verifyEmpty(data, [gid], response)) {
        createRelation();
    }
    function createRelation() {
        var query = [
            'MATCH (account:Account),(group:Group)',
            'WHERE account.phone={phone} AND group.gid={gid}',
            'CREATE UNIQUE group-[r:HAS_MEMBER]->account',
            'RETURN r,group'
        ].join("\n");
        var params = {
            phone: phone,
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "关注群组失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "关注群组失败",
                    "失败原因": "用户或群组不存在"
                }), response);
            } else {
                var pop = results.pop();
                var rNode = pop.r;
                var rData = rNode.data;
                rData.relation = "follow";
                rData.rid = "8888888";
                rNode.save(function (err, node) {
                });
                var groupData = pop.group.data;
                var location = JSON.parse(groupData.location || {
                    longitude: 0,
                    latitude: 0
                });
                var group = {
                    gid: groupData.gid,
                    icon: groupData.icon || "",
                    name: groupData.name,
                    longitude: location.longitude || 0,
                    latitude: location.latitude || 0,
                    createTime: groupData.createTime || 0,
                    description: groupData.description || "",
                    background: groupData.background || "",
                    cover: groupData.cover || "",
                    permission: groupData.permission || "",
                    labels: []
                };
                ResponseData(JSON.stringify({
                    "提示信息": "关注群组成功",
                    group: group
                }), response);
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "group_all",
                    content: JSON.stringify({
                        type: "group_all",
                        phone: phone,
                        time: time,
                        status: "success",
                        content: "",
                        eid: eid
                    })
                });
                client.rpush(phone, event, function (err, reply) {
                    if (err) {
                        console.error("保存Event失败");
                    } else {
                        console.log("保存Event成功");
                    }
                });
                push.inform(phone, phone, accessKey, "*", event);
            }
        });
    }
}
function ResponseData(responseContent, response) {
    response.writeHead(200, {
        "Content-Type": "application/json; charset=UTF-8",
        "Content-Length": Buffer.byteLength(responseContent, 'utf8')
    });
    response.write(responseContent);
    response.end();

    /* response.writeHead(200, { 'Content-Type': 'text/plain',
     'Trailer': 'Content-MD5' });
     response.write(fileData);
     response.addTrailers({'Content-MD5': "7895bf4b8828b55ceaf47747b4bca667"});
     response.end();*/
}

//使数据库中的群组数据和高德Lbs云中数据保持一致
//不存在的就创建新的lbs数据，存在就更新使数据一致，如果出现异常数据，删除掉查询出的数据并创建新的lbs数据
//setAllGroupNodeToLbs();
function setAllGroupNodeToLbs() {
    var query = [
        "MATCH (group:Group)",
        "WHERE group.gtype={gtype}",
        "RETURN group"
    ].join("\n");
    var params = {
        gtype: "group"
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.log("数据异常");
        } else {
            for (var index in results) {
                var groupData = results[index].group.data;
                //console.log(groupData.gid);
                checkGroupIsExists(groupData);
            }
            console.error(results.length);
        }
    });
}
function checkGroupIsExists(group) {
    ajax.ajax({
        type: "GET",
        url: "http://yuntuapi.amap.com/datamanage/data/list",
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.GROUPTABLEID,
            filter: "gid:" + group.gid
        }, success: function (info) {
            var info = JSON.parse(info);
            if (info.status == 1) {
                //console.log("success--" + info.datas.length)
                if (info.count == 0) {
                    createGroupLbsData(group);
                } else if (info.count == 1) {
                    updateGroupLbsData(group, info.datas[0]._id);
                } else if (info.count > 1) {
                    var ids = "";
                    var pois = info.datas;
                    for (var index in pois) {
                        var poi = pois[index];
                        if (ids == "") {
                            ids = poi._id;
                        } else {
                            ids = ids + "," + poi._id;
                        }
                    }
                    deleteGroupLbsData(group, ids);
                }
            } else {
                console.log(info.info + "--");
            }
        }
    });
}

function deleteGroupLbsData(group, ids) {
    ajax.ajax({
        type: "POST",
        url: serverSetting.LBS.DATA_DELETE,
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.GROUPTABLEID,
            ids: ids
        }, success: function (info) {
            var info = JSON.parse(info);
            if (info.status == 1) {
                console.log("删除成功");
                createGroupLbsData(group);
            } else {
                console.log(info.info + "--");
            }
        }
    });
}

function updateGroupLbsData(group, id) {
    var addressLocation = "";
    var location = group.location;
    if (location) {
        try {
            location = JSON.parse(location);
            addressLocation = (location.longitude ? location.longitude : "104.394729") + "," + (location.latitude ? location.latitude : "31.125698");
        } catch (e) {
            addressLocation = "104.394729,31.125698";
        }
    } else {
        addressLocation = "104.394729,31.125698";
    }
    var name = group.name;
    name = name.replace(/ /g, "&nbsp;");
    ajax.ajax({
        type: "POST",
        url: serverSetting.LBS.DATA_UPDATA,
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.GROUPTABLEID,
            loctype: 1,
            data: JSON.stringify({
                _id: id,
                _name: name,
                _location: addressLocation,
                //_address: "",
                icon: group.icon,
                gtype: group.gtype,
                permission: group.permission ? group.permission : "open",
                cover: group.cover ? group.cover : "",
                createTime: group.createTime ? group.createTime : 1412578255571,
                description: group.description == "请输入群组描述信息" ? "" : group.description
                //description:group.description == "请输入群组描述信息" ? "无 描述" : group.description
            })
        }, success: function (info) {
            try {
                var info = JSON.parse(info);
                if (info.status == 1) {
                    console.log("更新成功" + info.info);
                } else {
                    console.log(info.info + "--" + group.gid);
                    console.error(JSON.stringify({
                        _id: id,
                        _name: group.name,
                        _location: addressLocation,
                        //_address: "",
                        icon: group.icon,
                        gtype: group.gtype,
                        permission: group.permission ? group.permission : "open",
                        cover: group.cover ? group.cover : "",
                        createTime: group.createTime ? group.createTime : 1412578255571,
                        description: group.description == "请输入群组描述信息" ? "" : group.description
                    }));
                }
            } catch (e) {
                console.error(info);
                console.error(JSON.stringify({
                    _id: id,
                    _name: group.name,
                    _location: addressLocation,
                    //_address: "",
                    icon: group.icon,
                    gtype: group.gtype,
                    permission: group.permission ? group.permission : "open",
                    cover: group.cover ? group.cover : "",
                    createTime: group.createTime ? group.createTime : 1412578255571,
                    description: group.description == "请输入群组描述信息" ? "" : group.description
                }));
            }
        }
    });
}
function createGroupLbsData(group) {
    var addressLocation = "";
    var location = group.location;
    if (location) {
        try {
            location = JSON.parse(location);
            addressLocation = (location.longitude ? location.longitude : "104.394729") + "," + (location.latitude ? location.latitude : "31.125698");
        } catch (e) {
            addressLocation = "104.394729,31.125698";
        }
    } else {
        addressLocation = "104.394729,31.125698";
    }
    ajax.ajax({
        type: "POST",
        url: serverSetting.LBS.DATA_CREATE,
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.GROUPTABLEID,
            loctype: 1,
            data: JSON.stringify({
                _name: group.name,
                _location: addressLocation,
                //_address: "",
                gid: group.gid,
                icon: group.icon,
                gtype: group.gtype,
                permission: group.permission ? group.permission : "open",
                cover: group.cover ? group.cover : "",
                createTime: group.createTime ? group.createTime : 1412578255571,
                description: group.description == "请输入群组描述信息" ? "" : group.description
            })
        }, success: function (info) {
            var info = JSON.parse(info);
            if (info.status == 1) {
                console.log("success--" + info._id)
            } else {
                console.error(info.info);
            }
        }
    });
}

module.exports = groupManage;
