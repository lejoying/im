/**
 * Created by Coolspan on 2015/3/31.
 */
var serverSetting = root.globaldata.serverSetting;
var labelManege = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var verifyEmpty = require("./../lib/verifyParams.js");
var ajax = require("./../lib/ajax.js");
var push = require('../lib/push.js');

var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);

/***************************************
 *     URL：/api2/label/modifygrouplabel
 ***************************************/
labelManege.modifygrouplabel = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var labels = data.labels;
    var count = 0;
    if (verifyEmpty.verifyEmpty(data, [labels, gid], response)) {
        labels = JSON.parse(labels);
        modify();
    }
    function modify() {
        var query = [
            "MATCH(group:Group)-[r:HAS_LABEL]->(label:Label)",
            "WHERE group.gid={gid}",
            "DELETE r",
            "RETURN group"
        ].join('\n');
        var params = {
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组标签失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else {
                for (var index in labels) {
                    var label = labels[index];
                    createlabel(label);
                }
            }
        });
    }

    function createlabel(labelName) {
        var labelData = {
            name: labelName
        }
        var query = [
            "MATCH(group:Group)",
            "WHERE group.gid={gid}",
            "MERGE(label:Label{name:{name}})",
            "ON CREATE SET label.lid=ID(label)",
            "CREATE UNIQUE group-[r:HAS_LABEL]->label",
            "RETURN group,label"
        ].join('\n');
        var params = {
            gid: parseInt(gid),
            name: labelName
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改群组标签失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length > 0) {
                count++;
//                    created(results);
                if (count == labels.length) {
                    ResponseData(JSON.stringify({
                        "提示信息": "创建群组标签成功"
                    }), response);
                }

            }
        });
    }

    function created(results) {
        console.error("标签创建成功");
        ResponseData(JSON.stringify({
            "提示信息": "创建群组标签成功"
        }), response);
        var event = JSON.stringify({
            sendType: "event",
            contentType: "group_creatalabel",
            content: JSON.stringify({
                type: "group_creatalabel",
                phone: phone,
                time: time,
                status: "success",
                content: label,
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
}
labelManege.getgrouplabels = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var gid = data.gid;
    if (verifyEmpty.verifyEmpty(data, [phone, gid], response)) {
        getlabels();
    }
    function getlabels() {
        var query = [
            "MATCH(group:Group)-[r:HAS_LABEL]->(label:Label)",
            "WHERE group.gid={gid}",
            "RETURN group,label"
        ].join('\n');
        var params = {
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组标签失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length > 0) {
                var labels = [];
                for (var index in results) {
                    var label = results[index].label.data;
                    labels.push(label.name);
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组标签成功",
                    "gid": gid,
                    "labels": labels
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群组标签失败",
                    "失败原因": "群组不存在"
                }), response);
            }
        });
    }
}
labelManege.getlabelsgroups = function (data, response) {
    response.asynchronous = 1;
    var labels = data.labels;
    var nowPage = data.nowpage;
    var pageSize = data.pagesize;
    if (verifyEmpty.verifyEmpty(data, [labels, nowPage, pageSize], response)) {
        labels = JSON.parse(labels);
        getlabelsgroups();
    }
    function getlabelsgroups() {
        var query = [
            "MATCH(group:Group)-[r:HAS_LABEL]->(label:Label)",
            "WHERE label.name IN {labels}",
            "WITH group,count(DISTINCT  label) AS length ",
            "WHERE length>={length}",
            "RETURN group",
            "SKIP {start}",
            "LIMIT {pageSize}",
        ].join('\n');
        var params = {
            labels: labels,
            length: labels.length,
            start: parseInt(nowPage) * parseInt(pageSize),
            pageSize: parseInt(pageSize)
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
                    var groups = [];
                    var groupsMap = {};
                    for (var index in results) {
                        var groupData = results[index].group.data;
                        var location = {};
                        if (groupData.location) {
                            location = JSON.parse(groupData.location);
                        }
                        if (!groupsMap[groupData.gid + ""]) {
                            groups.push(groupData.gid + "");
                            var group = {
                                gid: groupData.gid,
                                icon: groupData.icon,
                                name: groupData.name,
                                notReadMessagesCount: 0,
                                distance: 0,
                                createTime: groupData.createTime,
                                longitude: location.longitude || 0,
                                latitude: location.latitude || 0,
                                description: groupData.description || "",
                                background: groupData.background || "",
                                cover: groupData.cover || "",
                                permission: groupData.permission || "",
                                labels: []
                            };
                            console.log(group);
                            groupsMap[groupData.gid + ""] = group;
                        }
                    }
                    ResponseData(JSON.stringify({
                        "提示信息": "获取标签群组成功",
                        "groups": groups,
                        "groupsMap": groupsMap
                    }), response);
                }
            }
        )
    }
}
labelManege.gethotlabels = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var nowpage = data.nowpage;
    var pagesize = data.pagesize;
    if (verifyEmpty.verifyEmpty(data, [nowpage, pagesize], response)) {
        gethotlabels();
    }
    function gethotlabels() {
        var query = [
            "MATCH(group:Group)-[r:HAS_LABEL]->(label:Label)",
            "WITH label, count(group) AS groups",
            "ORDER BY groups DESC ",
            "SKIP {start}",
            "LIMIT {pagesize}",
            "RETURN label"
        ].join('\n');
        var params = {
            start: parseInt(nowpage) * parseInt(pagesize),
            pagesize: parseInt(pagesize)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取热门标签失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else {
                var labels = [];
                for (var index in results) {
                    var label = results[index].label.data.name;
                    labels.push(label);
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取热门标签成功",
                    "labels": labels
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
module.exports = labelManege;