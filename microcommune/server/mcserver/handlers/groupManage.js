var serverSetting = root.globaldata.serverSetting;
var groupManage = {};
var verifyEmpty = require("./../lib/verifyParams.js");
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
            name: name
        }
        if (data.description) {
            group.description = data.description;
        } else {
            group.description = "";
        }
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
                    if (location != undefined) {
                        setGroupLBSLocation(phone, data.accessKey, location, group);
                    }
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
                }))
                response.end();
            }
        });
    }
}
function setGroupLBSLocation(phone, accessKey, location, group) {
    ajax.ajax({
        url: "127.0.0.1:8076/lbs/setgrouplocation?",
        type: "GET",
        data: {
            phone: phone,
            accessKey: accessKey,
            location: JSON.stringify({
                longitude: location.longitude,
                latitude: location.latitude
            }),
            group: JSON.stringify({
                gid: group.gid,
                name: group.name,
                description: group.description
            })
        },
        success: function (data) {
            if (data["提示信息"] == "标记群组位置成功") {
                console.log(data["提示信息"] + "---" + data.gid);
            } else {
                console.error(data["提示信息"] + "---" + data["失败原因"]);
            }
        }
    });
}
/***************************************
 *     URL：/api2/group/addmembers
 ***************************************/
groupManage.addmembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
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
                    "提示信息": "加入群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                addMembersToGroup(gid, members);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "加入群组失败",
                    "失败原因": "群组不存在"
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
                    "提示信息": "退出群组失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log("checkGroupNode" + error);
                return;
            } else if (results.length > 0) {
                removeMembersToGroup(gid, members);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "退出群组失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function removeMembersToGroup(gid, members) {
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
    var name = data.name;
    var description = data.description;
    var location = data.location;
    console.log("phone:" + phone + ",gid:" + gid + ",name:" + name);
    var list = [phone, gid, name];
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
                    "提示信息": "修改群组信息失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log("checkGroupNode" + error);
                return;
            } else if (results.length > 0) {
                modifyGroupNode(gid);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }

    function modifyGroupNode(gid) {
        var query = [
            'START group=node({gid})',
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
                if (name) {
                    groupData.name = name;
                }
                if (description) {
                    groupData.description = description;
                }
                if (location) {
                    groupData.location = JSON.stringify({
                        longitude: location.longitude,
                        latitude: location.latitude
                    });
                }
                groupNode.save(function (error) {
                });
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息成功",
                    group: groupNode.data
                }));
                response.end();
                if (location) {
                    setGroupLBSLocation(phone, accessKey, location, groupData);
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "修改群组信息失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
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

    var phone = data.phone;
    //(account1:Account)-[r:HAS_GROUP]->(group:Group)-[r1:HAS_MEMBER]->(account:Account)
    var query = [
        'MATCH (account1:Account)<-[r:HAS_MEMBER]-(group:Group)-[r1:HAS_MEMBER]->(account:Account)',
        'WHERE account1.phone={phone}',
        'RETURN group,account,account1'
    ].join('\n');
    var params = {
        phone: phone
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
                    nickName: accountData.nickName
                };
                if (groupZ[groupData.gid] == null) {
                    var accounts = [];
                    var account_own = it.account1.data;
                    accounts.push(account_own);
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

module.exports = groupManage;
