var serverSetting = root.globaldata.serverSetting;
var groupManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
/***************************************
 *     URL：/api2/group/create
 ***************************************/
groupManage.create = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var tempGid = data.tempGid;
    var groupName = data.groupName;
    var membersStr = data.members;
    console.log("phone:" + phone + "tempGid:" + tempGid + ",groupName:" + groupName + ",membersStr:" + membersStr);
    createGroup();
    function createGroup() {
        var group = {
            groupName: groupName
        }
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'CREATE group:Group({group})',
            'SET group.gid=ID(group)',
            'CREATE UNIQUE account-[r]->group',
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
                var group = response.pop().group.data;
                var members = JSON.stringify(membersStr);
                if (members.length > 0) {
                    addMembersToGroup(group, members);
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "创建群组成功",
                        group: group
                    }))
                    response.end();
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "创建群组失败",
                    "失败原因": "数据异常"
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
                response.write(JSON.stringify({
                    "提示信息": "创建群组成功",
                    group: group
                }))
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/group/addmembers
 ***************************************/
groupManage.addmembers = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var gid = data.gid;
    var membersStr = data.members;
    console.log("phone:" + phone + "gid:" + gid + ",membersStr:" + membersStr);
    var members = JSON.parse(membersStr);
    addMembersToGroup(gid, members);
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
                response.write(JSON.stringify({
                    "提示信息": "加入群组成功"
                }))
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "加入群组失败",
                    "失败原因": "群组不存在"
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
    var membersStr = data.members;
    console.log("phone:" + phone + "gid:" + gid + ",membersStr:" + membersStr);
    var members = JSON.parse(membersStr);
    removeMembersToGroup(gid, members);
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
                console.log(error);
                return;
            } else if (results.length > 0) {
                response.write(JSON.stringify({
                    "提示信息": "退出群组成功"
                }))
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "退出群组失败",
                    "失败原因": "群组不存在"
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
    console.log("phone:" + phone + "gid:" + gid);
    var members = JSON.parse(membersStr);
    getGroupMembers(gid);
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
                    members.push(member);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员成功",
                    members: members
                }))
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取群组成员失败",
                    "失败原因": "群组不存在"
                }));
                response.end();
            }
        });
    }
}
module.exports = groupManage;
