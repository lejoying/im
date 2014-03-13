var squareManage = {};

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
    var messageStr = data.message;
    var message = {};
    var one = {
        contentType: "text",
        content: "haha"
    }
    console.log(JSON.stringify(one));
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
            contentType: content.contentType,
            phone: phone,
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
                    continue;
                }
                sessionPool[message.gid][index].response = null;
                sessionResponse.write(JSON.stringify({
                    "提示信息": "获取广播成功",
                    messages: [message],
                    flag: session.flag + 1
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
        } else if (reply.length == 0) {
            sessionPool[gid] = sessionPool[gid] || [];
            sessionPool[gid][accessKey] = {flag: parseInt(flag), phone: phone, response: response};
        } else {
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
                            sessionPool[gid] = sessionPool[gid] || [];
                            sessionPool[gid][accessKey] = {flag: parseInt(flag), phone: phone, response: response};
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "获取广播成功",
                                messages: reply,
                                flag: parseInt(flag) + reply.length
                            }));
                            response.end();
                        }
                    } else {
                        sessionPool[gid] = sessionPool[gid] || [];
                        sessionPool[gid][accessKey] = {flag: parseInt(flag), phone: phone, response: response};
                    }
                }
            });
        }
    });
}

module.exports = squareManage;