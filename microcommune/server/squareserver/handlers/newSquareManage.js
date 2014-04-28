var squareManage = {};
var serverSetting = root.globaldata.serverSetting;
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var verifyParams = require('./../../mcserver/lib/verifyParams');
var redis = require("redis");
//var client = redis.createClient("6379", "127.0.0.1");
var client = redis.createClient("6379", "115.28.212.79");
var sessionPool = {};
var notifySquareMessageList = [];
var threadNotifyCount = 10;

/***************************************
 *     URL：/api2/square/sendsquaremessage
 ***************************************/
squareManage.sendsquaremessage = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    console.log(data);
    var accessKey = data.accessKey;
    var gid = data.gid;
    var nickName = data.nickName;
    var head = data.head;
    var messageStr = data.message;
    var message = {};
    var arr = [gid, nickName, messageStr];
    if (verifyParams.verifyEmpty(data, arr, response)) {
        sendMessage();
    }
    function sendMessage() {
        try {
            console.info(messageStr);
            message = JSON.parse(messageStr);
            console.info(message + "---" + gid);
            sessionPool[gid] = sessionPool[gid] || [];
            var session = sessionPool[gid][accessKey];
            console.info(session + "=====");
            if (session) {
                sendSquareMessage(message);
            } else {
                response.write(JSON.stringify({
                    "提示消息": "发布广播失败",
                    "失败原因": "用户权限不足"
                }));
                response.end();
                return;
            }
            var gidNum = parseInt(gid);
            if (gidNum < 0) {
                throw "gidNum Not less than zero"
            }
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "发布广播失败",
                "失败原因": "参数格式错误"
            }));
            response.end();
            console.error(e + "-----+++参数格式错误");
            return;
        }

        function sendSquareMessage(content) {
            var time = new Date().getTime();
            var message = {
                gmid: time + "",
                sendType: "square",
                messageType: content.messageType,
                contentType: content.contentType,
                phone: phone,
                nickName: nickName,
                head: head,
                gid: gid,
                content: content.content,
                praiseusers: [],
                time: time
            }
            client.rpush("square_" + gid, message.gmid, function (err, reply) {
                if (err) {
                    response.write(JSON.stringify({
                        "提示信息": "发布广播失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(err);
                    return;
                } else {
                    var squareMessage = JSON.stringify(message);
                    message.flag = reply;
                    client.hset("square_" + gid + "_info", message.gmid, squareMessage, function (err, reply) {
                        if (err) {
                            response.write(JSON.stringify({
                                "提示信息": "发布广播失败",
                                "失败原因": "数据异常"
                            }));
                            response.end();
                            console.error(err);
                            return;
                        } else {
                            notifySquareMessageList.push(message);
                            response.write(JSON.stringify({
                                "提示信息": "发布广播成功",
                                time: message.time
                            }));
                            response.end();
                            notifySquareMessage();
                            /*while (true) {
                             }*/
                        }
                    });
                }
            });
        }
    }
}
var notifyingCount = 0;
function notifySquareMessage() {
    if (notifyingCount < threadNotifyCount && notifySquareMessageList.length > 0) {
        notify();
        notifyingCount--;
        notifySquareMessage();
    }

    function notify() {
        notifyingCount++;
        var message = notifySquareMessageList.shift();
        if (message) {
            var square = sessionPool[message.gid];
            var phone = message.phone;
            for (var index in square) {
                var session = square[index];
                var sessionResponse = session.response;
                if (sessionResponse == null || sessionResponse.flag >= message.flag) {
                    sessionPool[message.gid].count--;
                    continue;
                }
                sessionPool[message.gid][index].response = null;
                sessionResponse.write(JSON.stringify({
                    "提示信息": "获取广播成功",
                    messages: [message],
                    flag: session.flag + 1,
                    onlinecount: sessionPool[message.gid].count
                }));
                sessionResponse.end();
            }
        }
    }
}
//makeSqaureData();
function makeSqaureData() {
    function sleep(numberMillis) {
        var now = new Date();
        var exitTime = now.getTime() + numberMillis;
        while (true) {
            now = new Date();
            if (now.getTime() > exitTime)
                return;
        }
    }

    var path = "";
    for (var i = 0; i < 100; i++) {
        if (i % 5 == 0) {
            path = "706937f57c1b68d8ed2631aaab1d3020aa88eb7d.jpg";
            next();
        } else if (i % 5 == 1) {
            path = "3661c49b12a3e7a4a6ec4c6fb18379035cf8752a.jpg";
            next2();
        } else if (i % 5 == 2) {
            path = "978b3e6986071e464fd6632e1fd864652c42ca27.png";
            next();
        } else if (i % 5 == 3) {
            path = "210e8c8d649f7f9844ff102ec4153705e9c438dc.jpg";
            next2();
        } else if (i % 5 == 4) {
            path = "d9fb7db5dc6e4b06046f0114b12d581ee84cec73.png";
            next();
        }
        function next() {
            var data = {};
            data.phone = "121";
            data.nickName = "李建国";
            data.gid = "98";
            data.accessKey = "lejoying";
            data.head = "210e8c8d649f7f9844ff102ec4153705e9c438dc.jpg";
            data.message = JSON.stringify({
                contentType: "textandimage",
                messageType: "精华",
                content: [
                    {
                        type: "text",
                        details: "微型公社"
                    },
                    {
                        type: "image",
                        details: path
                    }
                ]
            });

//        JSON.parse(data.message);
            squareManage.sendsquaremessageing(data, null);
//        console.error(i + "--" + ((JSON.parse(data.message)).content)[0].details);
            sleep(100);
        }

        function next2() {
            var data = {};
            data.phone = "15210721344";
            data.nickName = "Coolspan";
            data.gid = "98";
            data.accessKey = "lejoying";
            data.head = "d9fb7db5dc6e4b06046f0114b12d581ee84cec73.png";
            data.message = JSON.stringify({
                contentType: "text",
                messageType: "精华",
                content: [
                    {
                        type: "text",
                        details: "北京奇点无限科技有限公司"
                    }
                ]
            });

//        JSON.parse(data.message);
            squareManage.sendsquaremessageing(data, null);
//        console.error(i + "--" + ((JSON.parse(data.message)).content)[0].details);
            sleep(100);
        }
    }
}
/***************************************
 *     URL：/api2/square/getsquaremessage
 ***************************************/
squareManage.getsquaremessage = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    console.log(data);
    var accessKey = data.accessKey;
    var gid = data.gid;
    var flag = data.flag;
    var arr = [gid, flag];
    if (verifyParams.verifyEmpty(data, arr, response)) {
        getMessage();
    }
    function getMessage() {
        if (flag == "none") {
            flag = 0;
        } else {
            try {
                flag = parseInt(flag);
                if (flag < 0) {
                    throw "flag Not less than zero"
                }
            } catch (e) {
                response.write(JSON.stringify({
                    "提示信息": "获取广播失败",
                    "失败原因": "参数格式错误"
                }));
                response.end();
                console.error(e);
                return;
            }
        }
        // to do ......more approach to resolve the accessKey to support the multiple login.
        client.llen("square_" + gid, function (err, reply) {
            if (err) {
                response.write(JSON.stringify({
                    "提示信息": "获取广播失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(err);
                return;
            } else if (reply == 0) {
                next();
            } else {
                if (reply - flag > 100) {
                    flag = reply - 100;
                }
                client.lrange("square_" + gid, flag, -1, function (err, reply) {
                    if (err) {
                        response.write(JSON.stringify({
                            "提示信息": "获取广播失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                        console.error(err);
                        return;
                    } else {
                        if (reply != "") {
                            if (reply.length == 0) {
                                next();
                            } else {
                                client.hmget("square_" + gid + "_info", reply, function (err, replys) {
                                    if (err) {
                                        response.write(JSON.stringify({
                                            "提示信息": "获取广播失败",
                                            "失败原因": "数据异常"
                                        }));
                                        response.end();
                                        console.error(err);
                                        return;
                                    } else {
                                        response.write(JSON.stringify({
                                            "提示信息": "获取广播成功",
                                            messages: replys,
                                            flag: parseInt(flag) + reply.length
                                        }));
                                        response.end();
                                    }
                                });
                            }
                        } else {
                            next();
                        }
                    }
                });
            }
            function next() {
                sessionPool[gid] = sessionPool[gid] || [];
                sessionPool[gid].count = sessionPool[gid].count || 0;
                if (!sessionPool[gid][accessKey]) {
                    sessionPool[gid].count++;
                } else {
                    if (sessionPool[gid][accessKey].response == null) {
                        sessionPool[gid].count++;
                    }
                }
                sessionPool[gid][accessKey] = {flag: parseInt(flag), phone: phone, response: response};
            }
        });
    }
}
/***************************************
 *     URL：/api2/square/getonlinecount
 ***************************************/
squareManage.getonlinecount = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    console.log("gid--" + gid);
    try {
        var agid = parseInt(gid);
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "获取广场人数失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.error(e);
        return;
    }
    if (sessionPool[gid]) {
        response.write(JSON.stringify({
            "提示信息": "获取广场人数成功",
            onlinecount: sessionPool[gid].count
        }));
        response.end();
    } else {
        response.write(JSON.stringify({
            "提示信息": "获取广场人数成功",
            onlinecount: 0
        }));
        response.end();
    }
}
/***************************************
 *     URL：/api2/square/addsquarepraise
 ***************************************/
squareManage.addsquarepraise = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var nickName = data.nickName;
    var gid = data.gid;
    var gmid = data.gmid;
    var operation = data.operation;
    client.hget("square_" + gid + "_info", gmid, function (err, reply) {
            if (err) {
                response.write(JSON.stringify({
                    "提示信息": "点赞广播失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(err);
                return;
            } else {
                if (reply) {
//                    var account = {
//                        phone: phone,
//                        nickName: nickName,
//                        time: new Date().getTime()
//                    };
                    var message = JSON.parse(reply);
                    message.praiseusers = message.praiseusers || [];
                    var praiseusers = message.praiseusers;
                    var users = [];
                    if (operation || operation == "true") {
                        message.praiseusers.push(phone);
                    } else {
                        for (var index in praiseusers) {
                            if (praiseusers[index] != phone) {
                                users.push(praiseusers[index]);
                            }
                        }
                        message.praiseusers = users;
                    }
                    client.hset("square_" + gid + "_info", gmid, JSON.stringify(message), function (err, reply) {
                        if (err) {
                            response.write(JSON.stringify({
                                "提示信息": "点赞广播失败",
                                "失败原因": "数据异常"
                            }));
                            response.end();
                            console.error(err);
                            return;
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "点赞广播成功"
                            }));
                            response.end();
                        }
                    });
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "点赞广播失败",
                        "失败原因": "广播不存在"
                    }));
                    response.end();
                }
            }
        }
    )
    ;

}
/***************************************
 *     URL：/api2/square/addsquarecollect
 ***************************************/
squareManage.addsquarecollect = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var gmid = data.gmid;
    var operation = data.operation;
    var query = [
        "MATCH (account:Account)-[r]->(group:Group)",
        "WHERE account.phone={phone} AND group.gid={gid} AND group.gtype={gtype}",
        "RETURN account,r,group"
    ].join("\n");
    var params = {
        phone: phone,
        gid: gid,
        gtype: "community"
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "收藏广播失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.error(error);
            return;
        } else if (results.length == 0) {
            response.write(JSON.stringify({
                "提示信息": "收藏广播失败",
                "失败原因": "广场不存在"
            }));
            response.end();
        } else {
            var collect = {
                phone: phone,
                gid: gid,
                gmid: gmid,
                time: new Date().getTime()
            };
            var rNode = results.pop().r;
            var rData = rNode.data;
            rData.collects = rData.collects || JSON.stringify([]);
            var collects = JSON.parse(rData.collects);
            var collects_2 = [];
            if (operation) {
                collects.push(collect);
            } else {
                for (var index in collects) {
                    var collect = collects[index];
                    if (collect.gmid != gmid) {
                        collects_2.push(collect);
                    }
                }
                collects = collects_2;
            }
            rData.collects = JSON.stringify(collects);
            rNode.save(function (error, node) {
                response.write(JSON.stringify({
                    "提示信息": "收藏广播成功"
                }));
                response.end();
            });
        }
    });
}
/***************************************
 *     URL：/api2/square/addsquarecomment
 ***************************************/
squareManage.addsquarecomment = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var nickName = data.nickName;
    var gid = data.gid;
    var gmid = data.gmid;
    var contentType = data.contentType;
    var content = data.content;
    client.hget("square_" + gid + "_comment", gmid, function (err, reply) {
        if (err) {
            response.write(JSON.stringify({
                "提示信息": "评论广播失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.error(err);
            return;
        } else {
            var comment = {
                contentType: contentType,
                content: content,
                phone: phone,
                nickName: nickName,
                time: new Date().getTime()
            };
            var comments;
            if (reply) {
                comments = JSON.parse(reply);
            } else {
                comments = [];
            }
            comments.push(comment);
            client.hset("square_" + gid + "_comment", gmid, JSON.stringify(comments), function (err, reply) {
                if (err) {
                    response.write(JSON.stringify({
                        "提示信息": "评论广播失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(err);
                    return;
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "评论广播成功",
                        time: comment.time
                    }));
                    response.end();
                }
            });
        }
    });
}
/***************************************
 *     URL：/api2/square/getsquarecomments
 ***************************************/
squareManage.getsquarecomments = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    var gmid = data.gmid;
    client.hget("square_" + gid + "_comment", gmid, function (err, reply) {
        if (err) {
            response.write(JSON.stringify({
                "提示信息": "获取广播评论失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.error(err);
            return;
        } else {
            if (reply) {
                response.write(JSON.stringify({
                    "提示信息": "获取广播评论成功",
                    gmid: gmid,
                    comments: JSON.parse(reply)
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "获取广播评论成功",
                    gmid: gmid,
                    comments: []
                }));
                response.end();
            }
        }
    });
}
/***************************************
 *     URL：/api2/square/getsquareusers
 ***************************************/
squareManage.getsquareusers = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    var phones = [];
    var square = sessionPool[gid];
    for (var index in square) {
        var sessionResponse = square[index];
        if (sessionResponse.response == null) {
            continue;
        } else {
            phones.push(sessionResponse.phone);
        }
    }
    var query = [
        "MATCH (account:Account)",
        "WHERE account.phone IN {phones}",
        "RETURN account"
    ].join("\n");
    var params = {
        phones: phones
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "获取广场在线用户失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.error(err);
            return;
        } else if (results.length == 0) {
            response.write(JSON.stringify({
                "提示信息": "获取广场在线用户成功",
                users: []
            }));
            response.end();
        } else {
            var users = [];
            for (var index in results) {
                var accountData = results[index].account.data;
                var account = {
                    ID: accountData.ID,
                    sex: accountData.sex,
                    phone: accountData.phone,
                    mainBusiness: accountData.mainBusiness,
                    head: accountData.head,
                    byPhone: accountData.byPhone,
                    nickName: accountData.nickName,
                    userBackground: accountData.userBackground
                };
                users.push(account);
            }
            response.write(JSON.stringify({
                "提示信息": "获取广场在线用户成功",
                users: users
            }));
            response.end();
        }
    });
}
//modifyRedisData();
function modifyRedisData() {
    client.lrange("square_91", 0, -1, function (err, reply) {
        if (err) {
            console.error(err);
            return;
        } else {
            console.log(reply.length);
            for (var index in reply) {
                var message = JSON.parse(reply[index]);
                console.log(message.phone);
            }
        }
    });
}
module.exports = squareManage;