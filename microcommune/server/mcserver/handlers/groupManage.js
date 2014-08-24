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
                    "失败原因": "数据异常"
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
                    "失败原因": "数据格式不正确"
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
            gtype: data.gtype
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
            'SET group.gid=ID(group)',
            'RETURN group,r'
        ].join('\n');
        var params = {
            phone: phone,
            group: group
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var group = results.pop().group.data;
                createGroupLocation(group);
                if (members.length > 0) {
                    addMembersToGroup(group, members);
                    console.log("开始初始化群组第一批用户个数:" + members.length);
                } else {
                    console.log("未初始化群组第一批用户");
                    response.write(JSON.stringify({
                        "提示信息": "创建群组成功",
                        group: group
                    }))
                    response.end();
//                    setGroupLBSLocation(phone, data.accessKey, location, group);
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "用户不存在"
                }));
                response.end();
            }
        });
    }

    function addMembersToGroup(group, members) {
        var query = [
            'START group=node({gid})',
            'MATCH (account:Account)',
            'WHERE account.phone IN {members}',
            'CREATE UNIQUE group-[r:HAS_MEMBER]->account',
            'RETURN r'
        ].join('\n');
        var params = {
            gid: group.gid,
            members: members
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else {
                console.log("初始化的群组好友成功的个数:" + results.length);
                response.write(JSON.stringify({
                    "提示信息": "创建群组成功",
                    group: group
                }));
                response.end();
                for (var index in members) {
                    push.inform(phone, members[index], accessKey, "*", {"提示信息": "成功", event: "groupstatuschanged", event_content: {gid: group.gid, operation: true}});
                }
//                setGroupLBSLocation(phone, data.accessKey, location, group);
            }
        });
    }

    function createGroupLocation(group) {
        var location = JSON.parse(group.location)
        location = location.longitude + "," + location.latitude
        try {
            ajax.ajax({
                type: "POST",
                url: serverSetting.LBS.DATA_CREATE,
                data: {
                    key: serverSetting.LBS.KEY,
                    tableid: serverSetting.LBS.GROUPTABLEID,
                    loctype: 1,
                    data: JSON.stringify({
                        _name: group.name,
                        _location: location,
                        _address: data.address,
                        gid: group.gid,
                        icon: group.icon,
                        gtype: group.gtype,
                        description: group.description
                    })
                }, success: function (info) {
                    var info = JSON.parse(info);
                    if (info.status == 1) {
                        console.log("success--" + info._id)
                    } else {

                    }
                }
            });
        } catch (e) {
            console.log(e);
            return;
        }
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
            'RETURN r'
        ].join('\n');
        var params = {
            gid: gid,
            members: members
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
                for (var index in accounts) {
                    push.inform(phone, index, accessKey, "*", {"提示信息": "成功", event: "groupmemberchanged", event_content: {gid: gid}});
                }
                for (var index in members) {
                    push.inform(phone, members[index], accessKey, "*", {"提示信息": "成功", event: "groupstatuschanged", event_content: {gid: gid, operation: true}});
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
                    push.inform(phone, removePhone, accessKey, "*", {"提示信息": "成功", event: "groupstatuschanged", event_content: {gid: gid, operation: false}});
                }
                for (var index in accounts) {
                    if (!removeMembers[index]) {
                        push.inform(phone, index, accessKey, "*", {"提示信息": "成功", event: "groupmemberchanged", event_content: {gid: gid}});
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
                getGroupMembers(gid);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function getGroupMembers(gid) {
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
                for (var i = 0; i < results.length; i++) {
                    var member = results[i].account.data;
                    var account = {
                        ID: member.ID,
                        phone: member.phone,
                        nickName: member.nickName,
                        mainBusiness: member.mainBusiness,
                        head: member.head,
                        sex: member.sex,
                        byPhone: member.byPhone
                    };
                    members.push(account);
                }
                console.log(gid + "群組好友个数：" + members.length);
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    members: members
                }))
                response.end();
            } else {
                console.log(gid + "群組好友个数：" + results.length);
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    members: []
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
        checkGroupNode(gid);
        checkGroupLocation(gid);
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
                var groupNode = results.pop().group;
                var groupData = groupNode.data;
                var groupLocation = groupData.location || JSON.stringify({longitude: 116.422324, latitude: 39.906744});
                groupLocation = JSON.parse(groupLocation);
                groupData.name = name || groupData.name;
                if (icon) {
                    groupData.icon = icon;
                }
//                groupData.head = head || groupData.head;
//                var head0 = "";
//                if (groupData.head) {
//                    head0 = groupData.head;
//                }
//                if(head){
//                    head0 = head;
//                    groupData.head = head0;
//                }
                groupData.description = description || groupData.description;
//                groupData.description = groupData.description || "";
                var background0 = "";
                if (groupData.background) {
                    background0 = groupData.background;
                }
                if (background) {
                    background0 = background;
                    groupData.background = background0;
                }
                var currentLocation = {};
                if (location) {
                    currentLocation.longitude = location.longitude || groupLocation.longitude;
                    currentLocation.latitude = location.latitude || groupLocation.latitude;

                    groupData.location = JSON.stringify(currentLocation);
                } else {
                    currentLocation.longitude = groupLocation.longitude;
                    currentLocation.latitude = groupLocation.latitude;
                }
                groupNode.save(function (error) {
                });
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息成功",
                    group: groupNode.data
                }));
                response.end();
                for (var index in accounts) {
                    push.inform(phone, index, accessKey, "*", {"提示信息": "成功", event: "groupinformationchanged", event_content: {gid: gid}});
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

    function checkGroupLocation(gid) {
        ajax.ajax({
            type: "GET",
            url: serverSetting.LBS.DATA_SEARCH,
            data: {
                tableid: serverSetting.LBS.GROUPTABLEID,
                filter: "gid:" + gid,
                key: serverSetting.LBS.KEY
            },
            success: function (info) {
                var info = JSON.parse(info);
                if (info.status == 1) {
                    var id = info.datas[0]._id;
                    modifyGroupLocation(id);
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "查找群组位置信息失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }

    function modifyGroupLocation(id) {
        var location = JSON.parse(data.location);
        ajax.ajax({
            type: "POST",
            url: serverSetting.LBS.DATA_UPDATA,
            data: {
                key: serverSetting.LBS.KEY,
                tableid: serverSetting.LBS.GROUPTABLEID,
                loctype: 2,
                data: JSON.stringify({
                    _id: id,
                    _name: name,
                    _location: location.longitude + "," + location.latitude,
                    _address: address,
                    icon: icon,
                    description: description
                })
            }, success: function (info) {
                var info = JSON.parse(info);
                if (info.status == 1) {
                    response.write(JSON.stringify({
                        "提示信息": "修改用户位置信息成功"
                    }));
                    response.end();
                } else {
                    console.log(info.info);
                    response.write(JSON.stringify({
                        "提示信息": "修改用户位置信息失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }
}
/***************************************
 *     URL：/api2/group/getusergroups
 ***************************************/
groupManage.getusergroups = function (data, response) {
    response.asynchronous = 1;
    var target = data.target;
    console.log("target:" + target);
    var list = [target];
    if (verifyEmpty.verifyEmpty(data, list, response)) {
        checkUserNode(target);
    }

    function checkUserNode(target) {
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={target}',
            'RETURN account'
        ].join('\n');
        var params = {
            target: target
        }
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取好友群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                getUserGroupsNode(target);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取好友群组失败",
                    "失败原因": "好友不存在"
                }));
                response.end();
            }
        });
    }

    function getUserGroupsNode(target) {
        var query = [
            'MATCH (group:Group)-[r:HAS_MEMBER]->(account:Account)',
            'WHERE account.phone={target}',
            'RETURN group'
        ].join('\n');
        var params = {
            target: target
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取好友群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var groups = [];
                for (var i = 0; i < results.length; i++) {
                    var group = results[i].group.data;
                    group.members = [];
                    groups.push(group);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取好友群组成功",
                    groups: groups
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取好友群组成功",
                    groups: []
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/group/get
 ***************************************/
groupManage.get = function (data, response) {
    response.asynchronous = 1;
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
                    throw "gid不是数值";
                } else {
                    gid = maxPrime - gid;
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
            gid: gid
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
                var group = results.pop().group.data;
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息成功",
                    group: group
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取群组信息失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }
}
var ajax = require('../../lbsserver/lib/ajax.js');
/***************************************
 *     URL：/api2/group/getgroupsandmembers
 ***************************************/
groupManage.getgroupsandmembers = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    //(account1:Account)-[r:HAS_GROUP]->(group:Group)-[r1:HAS_MEMBER]->(account:Account)
    getAccountGroups();
    function getAccountGroups() {
        var query = [
            'MATCH (account:Account)<-[HAS_MEMBER]-(group:Group)',
            'WHERE account.phone={phone}',
            'RETURN group'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "数据异常"
                });
                response.end();
                console.error(error);
                return;
            } else if (results.length > 0) {
                var groups = [];
                var length = results.length;
                var count = 0;
                for (var index in results) {
                    count++;
                    var groupData = results[index].group.data;
                    groups.push(groupData.gid);
                    if (count == length) {
                        getGroupsMembers(groups);
                    }
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取群组成功",
                    groups: []
                }));
                response.end();
            }
        });
    }

    function getGroupsMembers(groupIDs) {
        var query = [
            'MATCH (group:Group)-[r1:HAS_MEMBER]->(account:Account)',
            'WHERE group.gid IN {groupIDs}',
            'RETURN group,account'
        ].join('\n');
        var params = {
            groupIDs: groupIDs
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else {
                var groups = [];
                var groupZ = {};
                for (var index in results) {
                    var it = results[index];
                    var groupData = it.group.data;
                    var accountData = it.account.data;
                    var account = {
                        ID: accountData.ID,
                        phone: accountData.phone,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        sex: accountData.sex,
                        byPhone: accountData.byPhone,
                        nickName: accountData.nickName,
                        userBackground: accountData.userBackground
                    };
                    if (groupZ[groupData.gid] == null) {
                        var accounts = [];
                        accounts.push(account);
                        groupData.members = accounts;
                        groupZ[groupData.gid] = groupData;
                        groups.push(groupData);
                    } else {
                        var groupData = groupZ[groupData.gid];
                        groupData.members.push(account);
                    }
                }
                response.write(JSON.stringify({
                    "提示信息": "获取群组成功",
                    groups: groups
                }));
                response.end();
            }
        });
    }
}
/*******************************************************************
 * * * * * * * * * *New Api* * * * * * * * * * * * * * * * * * * * *
 *******************************************************************/
groupManage.getgroupmembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    getAccountGroups();

    var friendsMap = {};
    var groups = [];
    var groupsMap = {};

    function getAccountGroups() {
        var query = [
            'MATCH (account:Account)<-[HAS_MEMBER]-(group:Group)',
            'WHERE account.phone={phone}',
            'RETURN group'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "数据异常"
                });
                response.end();
                console.error(error);
                return;
            } else if (results.length > 0) {
                var length = results.length;
                var count = 0;
                for (var index in results) {
                    count++;
                    var groupData = results[index].group.data;
                    groups.push(groupData.gid);
                    if (count == length) {
                        getGroupsMembers(groups);
                    }
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    relationship: {
                        friendsMap: friendsMap,
                        groups: groups,
                        groupsMap: groupsMap
                    }
                }), response);
            }
        });
    }

    function getGroupsMembers(groupIDs) {
        var query = [
            'MATCH (group:Group)-[r1:HAS_MEMBER]->(account:Account)',
            'WHERE group.gid IN {groupIDs}',
            'RETURN group,account'
        ].join('\n');
        var params = {
            groupIDs: groupIDs
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else {
                groups = [];
                for (var index in results) {
                    var it = results[index];
                    var groupData = it.group.data;
                    var accountData = it.account.data;
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
                        phone: accountData.phone,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        nickName: accountData.nickName,
                        userBackground: accountData.userBackground,
                        addMessage: "",
                        friendStatus: "",
                        alias: "",
                        flag: "none",
                        accessKey: "",
                        distance: 0,
                        notReadMessagesCount: 0,
                        longitude: location.longitude,
                        latitude: location.latitude
                    };
                    friendsMap[account.phone] = account;
                    if (!groupsMap[groupData.gid + ""]) {
                        groups.push(groupData.gid + "");
                        var group = {
                            gid: groupData.gid,
                            icon: groupData.icon,
                            name: groupData.name,
                            notReadMessagesCount: 0,
                            distance: 0,
                            longitude: location.longitude,
                            latitude: location.latitude,
                            description: groupData.description,
                            background: groupData.background
                        };
                        var members = [];
                        members.push(account.phone);
                        group.members = members;
                        groupsMap[groupData.gid + ""] = group;
                    } else {
                        groupsMap[groupData.gid + ""].members.push(account.phone);
                    }
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    relationship: {
                        friendsMap: friendsMap,
                        groups: groups,
                        groupsMap: groupsMap
                    }
                }), response);
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
}
module.exports = groupManage;
