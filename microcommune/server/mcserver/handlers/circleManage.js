var serverSetting = root.globaldata.serverSetting;
var circleManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/circle/modify
 ***************************************/
circleManage.modify = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var rid = data.rid;
    var name = data.name;
    modifyCircleName();
    function modifyCircleName() {
        var query = [
            "MATCH (account:Account{phone:{phone}})",
            "RETURN account"
        ].join("\n");
        var params = {phone: phone};
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                var circleOrderString = accountData.circlesOrderString;
                if (circleOrderString) {
                    try {
                        var orderObj = JSON.parse(circleOrderString);
                        var oldName = "";
                        for (var index in orderObj) {
                            var obj = orderObj[index];
                            if (rid == obj.rid) {
                                oldName = obj.name;
                                obj.name = name;
                                break;
                            }
                        }
                        accountData.circlesOrderString = JSON.stringify(orderObj);
                        accountNode.save(function (err, node) {
                        });
                        ResponseData(JSON.stringify({
                            "提示信息": "修改成功"
                        }), response);
                        var time = new Date().getTime();
                        var eid = phone + "_" + time;
                        var event = JSON.stringify({
                            sendType: "event",
                            contentType: "relation_updatecirclename",
                            content: JSON.stringify({
                                type: "relation_updatecirclename",
                                phone: phone,
                                eid: eid,
                                rid: rid,
                                name: oldName,
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
                    } catch (e) {
                        ResponseData(JSON.stringify({
                            "提示信息": "修改失败",
                            "失败原因": "数据异常"
                        }), response);
                    }
                } else {
                    ResponseData(JSON.stringify({
                        "提示信息": "修改失败",
                        "失败原因": "数据异常"
                    }), response);
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "修改失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}
/***************************************
 *     URL：/api2/circle/delete
 ***************************************/
circleManage.delete = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var rid = data.rid;
    try {
        rid = parseInt(rid);
        deleteCircle();
    } catch (e) {
        ResponseData(JSON.stringify({
            "提示信息": "删除失败",
            "失败原因": "参数格式错误"
        }), response);
        console.log(e);
    }

    function deleteCircle() {
        var query = [
            "MATCH (account:Account{phone:{phone}})",
            "RETURN account"
        ].join("\n");
        var params = {phone: phone};
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "删除失败",
                    "失败原因": "数据异常"
                }), response);
                console.log(error);
            } else if (results.length > 0) {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                var circleOrderString = accountData.circlesOrderString;
                if (circleOrderString) {
                    var name = "";
                    try {
                        var orderObj = JSON.parse(circleOrderString);
                        var newOrder = [];
                        for (var index in orderObj) {
                            var obj = orderObj[index];
                            if (rid != obj.rid) {
                                newOrder.push(obj);
                            } else {
                                name = obj.name;
                            }
                        }
                        accountData.circlesOrderString = JSON.stringify(newOrder);
                        accountNode.save(function (err, node) {
                        });
                        ResponseData(JSON.stringify({
                            "提示信息": "删除成功"
                        }), response);
                        var time = new Date().getTime();
                        var eid = phone + "_" + time;
                        var event = JSON.stringify({
                            sendType: "event",
                            contentType: "relation_deletecircle",
                            content: JSON.stringify({
                                type: "relation_deletecircle",
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
                    } catch (e) {
                        ResponseData(JSON.stringify({
                            "提示信息": "删除失败",
                            "失败原因": "数据异常"
                        }), response);
                    }
                } else {
                    ResponseData(JSON.stringify({
                        "提示信息": "删除失败",
                        "失败原因": "数据异常"
                    }), response);
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "删除失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}

var redis = require("redis");
var RID = -1;
var RIDclient = redis.createClient(serverSetting.redisPort, "112.126.71.180");
RIDclient.get("RID", function (err, reply) {
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
            console.log("RID:" + reply + "...init data,from server...112.126.71.180");
            RID = reply;
        }
    }
});
var push = require('../lib/push.js');
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
circleManage.createcircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var name = data.name;
    var oldRid = data.rid;
    addCircleNode();
    function addCircleNode() {
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
                    "提示信息": "添加失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                var circlesOrderString = accountData.circlesOrderString;
                var flag = false;
                var rid;
                try {
                    if (circlesOrderString) {
                        var orderObj = JSON.parse(circlesOrderString);
                        rid = ++RID;
                        orderObj.push({rid: rid, name: name});
                        accountData.circlesOrderString = JSON.stringify(orderObj);
                        accountNode.save(function (err, node) {
                        });
                        RIDclient.set("RID", RID, function (err, reply) {
                        });
                    } else {
                        flag = true;
                    }
                } catch (e) {
                    flag = true;
                }
                if (flag) {
                    rid = ++RID;
                    var orderObj = [{rid: rid, name: name}];
                    accountData.circlesOrderString = JSON.stringify(orderObj);
                    accountNode.save(function (err, node) {
                    });
                    RIDclient.set("RID", RID, function (err, reply) {
                    });
                }
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_addcircle",
                    content: JSON.stringify({
                        type: "relation_addcircle",
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
                    "提示信息": "添加成功",
                    "circle": {rid: rid, name: name, friends: []},
                    "rid": oldRid
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "添加失败",
                    "失败原因": "用户不存在"
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
module.exports = circleManage;