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
/***************************************
 *     URL：/api2/circle/moveorout
 ***************************************/
circleManage.moveorout = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneTo = data.phoneto;
    var rid = data.rid;
    var filter = (data.filter).toUpperCase();
    var query;
    var successMSG = "";
    var errorMSG = "";
    try {
        phoneTo = JSON.parse(phoneTo);
        next(phoneTo);
    } catch (e) {
        response.write(JSON.stringify({
            "提示消息": "移入失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.error(e);
        return;
    }
    function next(phoneTo) {
        if (filter == "REMOVE") {
            query = [
                'MATCH (circle:Circle)-[r:HAS_FRIEND]->(account:Account)',
                'WHERE circle.rid={rid} AND account.phone IN {phoneTo}',
                'DELETE r',
                'RETURN circle'
            ].join('\n');
            successMSG = JSON.stringify({
                "提示消息": "移出成功"
            });
            errorMSG = JSON.stringify({
                "提示消息": "移出失败",
                "失败原因": "数据异常"
            });
        } else if (filter == "SHIFTIN") {
            query = [
                'START circle=node({rid})',
                'MATCH (account:Account)',
                'WHERE account.phone IN {phoneTo}',
                'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
                'RETURN  r'
            ].join('\n');
            successMSG = JSON.stringify({
                "提示消息": "移入成功"
            });
            errorMSG = JSON.stringify({
                "提示消息": "移入失败",
                "失败原因": "数据异常"
            });
        }
        params = {
            rid: parseInt(rid),
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(errorMSG);
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                response.write(successMSG);
                response.end();

            } else {
                response.write(errorMSG);
                response.end();
            }
        });
    }


}
/***************************************
 *     URL：/api2/circle/moveout
 ***************************************/
circleManage.moveout = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneToArrayStr = data.phoneto;
    var phoneTo = JSON.parse(phoneToArrayStr);
//    var phoneTo = data.phoneto;
    var oldRid = data.oldrid;
    var newRid = data.newrid;
    var createFlag = false;
    if ((newRid != "undefined" && newRid != undefined) && newRid != "none") {
        createFlag = true;
    } else {
        createFlag = false;
    }
    if ((oldRid != "undefined" && oldRid != undefined) && oldRid != "none") {
        deleteRelationNode(phoneTo, newRid, oldRid);
    } else {
        if (createFlag) {
            createRelationNode(phoneTo, newRid);
        } else {
            response.write(JSON.stringify({
                "提示信息": "移动成功"
            }));
            response.end();
        }
    }
    function deleteRelationNode(phoneTo, newRid, oldRid) {
        var query = [
            'MATCH (circle:Circle)-[r:HAS_FRIEND]->(account:Account)',
            'WHERE circle.rid={rid} AND account.phone IN {phoneTo}',
            'DELETE r',
            'RETURN circle'
        ].join('\n');

        var params = {
            rid: parseInt(oldRid),
            phoneTo: phoneTo
        };

        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error + "deleteRelationNode");
                return;
            } else if (results.length > 0) {
                if (createFlag) {
//                    throw  "出现异常了，快点处理......哈哈";
                    createRelationNode(phoneTo, newRid);
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "移动成功"
                    }));
                    response.end();
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            }
        });
    }

    function createRelationNode(phoneTo, newRid) {
        var query = [
            'START circle=node({newRid})',
            'MATCH (account:Account)',
            'WHERE account.phone IN {phoneTo}',
            'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
            'RETURN r'
        ].join('\n');
        var params = {
            newRid: parseInt(newRid),
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                response.write(JSON.stringify({
                    "提示信息": "移动成功"
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/circle/addcircle
 ***************************************/
circleManage.addcircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var name = data.name;
    var oldRid = data.rid;
    var circle = {
        name: name
    };
    var query = [
        'MATCH (account:Account)',
        'WHERE account.phone={phone}',
        'CREATE UNIQUE account-[r:HAS_CIRCLE]->(circle:Circle{circle})',
        'SET circle.rid=ID(circle)',
        'RETURN circle'
    ].join('\n');
    var params = {
        phone: phone,
        circle: circle
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "添加失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else if (results.length > 0) {
            console.log("创建密友圈成功---");
            var circleData = results.pop().circle.data;
            var accounts = [];
            circleData.friends = accounts;
            response.write(JSON.stringify({
                "提示信息": "添加成功",
                "circle": circleData,
                "rid": oldRid
            }));
            response.end();
        } else {
            console.log("创建密友圈失败---");
            response.write(JSON.stringify({
                "提示信息": "添加失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
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