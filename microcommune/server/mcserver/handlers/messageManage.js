var serverSetting = root.globaldata.serverSetting;
var messageManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
var push = require('../lib/push.js');
/***************************************
 *     URL：/api2/message/send
 ***************************************/
messageManage.send = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneToStr = data.phoneto;
    var gid = data.gid;
    var sendType = data.sendType;
    var phoneto = [];
    var contentType = data.contentType;
    var content = data.content;
    var oldTime = data.time;
    try {
        phoneto = JSON.parse(phoneToStr);
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "发送失败",
            "失败原因": "数据格式不正确"
        }));
        response.end();
        console.log(e);
        return;
    }
    var time = new Date().getTime();
    var messageSelf = {
        contentType: contentType,
        sendType: sendType,
        phone: phone,
        content: content,
        time: time
    };
    if (sendType == "point") {
        messageSelf.phoneto = phoneToStr;
    } else if (sendType == "group") {
        messageSelf.gid = gid;
    } else if (sendType == "tempGroup") {
        messageSelf.tempGid = gid;
    } else {
        response.write(JSON.stringify({
            "提示信息": "发送失败",
            "失败原因": "数据格式不正确",
            time: time,
            sendType: sendType,
            gid: gid,
            phoneTo: phoneToStr,
            oldTime: oldTime
        }));
        response.end();
        return;
    }
    var messageOwn = JSON.stringify(messageSelf);
    client.rpush(phone, messageOwn, function (err, reply) {
        if (err != null) {
            response.write(JSON.stringify({
                "提示信息": "发送失败",
                "失败原因": "数据异常",
                time: time,
                sendType: sendType,
                gid: gid,
                phoneTo: phoneToStr,
                oldTime: oldTime
            }));
            response.end();
            console.log(err);
            return;
        }
        for (var i = 0; i < phoneto.length; i++) {
            var friendPhone = phoneto[i];
            if (friendPhone == phone) {
                continue;
            }
            var messageToOther = {
                contentType: contentType,
                sendType: sendType,
                phone: phone,
                content: content,
                time: time
            };
            if (sendType == "point") {
                messageToOther.phoneto = phoneToStr;
            } else if (sendType == "group") {
                messageToOther.gid = gid;
            } else if (sendType == "tempGroup") {
                messageToOther.tempGid = gid;
            }
            var messageOther = JSON.stringify(messageToOther);
            client.rpush(friendPhone, messageOther, function (err, reply) {
                if (err != null) {
                    response.write(JSON.stringify({
                        "提示信息": "发送失败",
                        "失败原因": "数据异常",
                        time: time,
                        sendType: sendType,
                        gid: gid,
                        phoneTo: phoneToStr,
                        oldTime: oldTime
                    }));
                    response.end();
                    console.log(err);
                    return;
                }
                //通知
            });
//            var event = JSON.stringify({"提示信息": "成功", event: "message", event_content: {message: [messageOther]}})
//            var event = JSON.stringify(messageOther);
            var event0 = JSON.stringify({
                sendType: "event",
                contentType: "message",
                content: messageOther
            });
            push.inform(phone, friendPhone, accessKey, "*", event0);
        }
        response.write(JSON.stringify({
            "提示信息": "发送成功",
            time: time,
            sendType: sendType,
            gid: gid,
            phoneTo: phoneToStr,
            oldTime: oldTime
        }));
        response.end();
    });
}
/***************************************
 *     URL：/api2/message/get
 ***************************************/
messageManage.get = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var flag = data.flag;
    if (phone == undefined || flag == undefined) {
        response.write(JSON.stringify({
            "提示信息": "获取失败",
            "失败原因": "参数不完整"
        }));
        response.end();
        return;
    }
    if (flag == "none") {
        client.get(phone + "flag", function (err, reply) {
            if (err != null) {
                responseErrorMessage(err, response);
                return;
            } else {
                if (reply == null) {
                    setPhoneFlag(phone, response);
                } else {
                    if (isNaN(reply)) {
                        setPhoneFlag(phone, response)
                    } else {
                        get(reply);
                    }
                }
            }
        });
    } else {
        if (isNaN(flag)) {
            setPhoneFlag(phone, response)
        } else {
            get(parseInt(flag));
        }
    }

    function get(from) {
        client.lrange(phone, from, -1, function (err, reply) {
            if (err != null) {
                responseErrorMessage(err, response);
                return;
            }
//            console.log(reply);
            var flag0 = parseInt(from) + reply.length;
            if (reply.length != 0) {
                client.set(phone + "flag", flag0, function (err, reply) {
                    if (err != null) {
                        responseErrorMessage(err, response);
                        return;
                    }
                });
            }
            response.write(JSON.stringify({
                "提示信息": "获取成功",
                messages: reply,
                flag: flag0
            }));
            response.end();
        });
    }

    function setPhoneFlag(phone, response) {
        client.set(phone + "flag", 0, function (err, reply) {
            if (err != null) {
                responseErrorMessage(err, response);
                return;
            }
            get(0);
        });
    }

    function responseErrorMessage(error, response) {
        response.write(JSON.stringify({
            "提示信息": "获取失败",
            "失败原因": "数据异常"
        }));
        response.end();
        console.log(error);
    }
}
messageManage.deletes = function (data, response) {
    response.asynchronous = 1;
    client.keys("*", function (err, reply) {
        if (err != null) {
            response.write(JSON.stringify({
                "提示信息": "删除失败"
            }));
            response.end();
            console.log(err);
            return;
        }
        client.del(reply, function (err, reply) {
            if (err != null) {
                response.write(JSON.stringify({
                    "提示信息": "删除失败"
                }));
                response.end();
                console.log(err);
                return;
            }
            console.log(reply);
        });
        response.write(JSON.stringify({
            "提示信息": "删除成功"
        }));
        response.end();
    });
}
module.exports = messageManage;