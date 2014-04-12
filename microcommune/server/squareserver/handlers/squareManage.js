var squareManage = {};

var verifyParams = require('./../../mcserver/lib/verifyParams');
var redis = require("redis");
var client = redis.createClient("6379", "127.0.0.1");
//var client = redis.createClient("6379", "115.28.212.79");
var sessionPool = {};
var notifySquareMessageList = [];
var threadNotifyCount = 10;
squareManage.sendsquaremessage = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    console.log(data);
    var accessKey = data.accessKey;
    var gid = data.gid;
    var nickName = data.nickName;
    var messageStr = data.message;
    var message = {};
    var arr = [gid, nickName, messageStr];
    if (verifyParams.verifyEmpty(data, arr, response)) {
        sendMessage();
    }
    function sendMessage() {
        try {
            message = JSON.parse(messageStr);
            sessionPool[gid] = sessionPool[gid] || [];
            var session = sessionPool[gid][accessKey];
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
            console.error(e + "-----");
            return;
        }

        function sendSquareMessage(content) {
            var message = {
                sendType: "square",
                contentType: content.contentType,
                phone: phone,
                nickName: nickName,
                gid: gid,
                content: content.content,
                time: new Date().getTime()
            }
            client.rpush("square_" + gid, JSON.stringify(message), function (err, reply) {
                if (err) {
                    response.write(JSON.stringify({
                        "提示信息": "发布广播失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(err);
                    return;
                } else {
                    message.flag = reply;
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
                                response.write(JSON.stringify({
                                    "提示信息": "获取广播成功",
                                    messages: reply,
                                    flag: parseInt(flag) + reply.length
                                }));
                                response.end();
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
squareManage.getonlinecount = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    console.log("gid--" + gid);
    /*try {
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
     */
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
module.exports = squareManage;