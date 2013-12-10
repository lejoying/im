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
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneto = {};
    var message = {};
    try {
        phoneto = JSON.parse(data.phoneto);
        message = JSON.parse(data.message);
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "发送失败",
            "失败原因": "数据格式不正确"
        }));
        response.end();
        console.log(e);
        return;
    }
    var messageOwn = JSON.stringify({
        type: message.type,
        phone: phone,
        phoneto: data.phoneto,
        content: message.content,
        time: new Date().getTime()
    });
    client.rpush(phone, messageOwn, function (err, reply) {
        if (err != null) {
            response.write(JSON.stringify({
                "提示信息": "发送失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(err);
            return;
        }
        for (var index in phoneto) {
            var messageOther = JSON.stringify({
                type: message.type,
                phone: phone,
                phoneto: JSON.stringify([phoneto[index]]),
                content: message.content,
                time: new Date().getTime()
            });
            client.rpush(phoneto[index], messageOther, function (err, reply) {
                if (err != null) {
                    response.write(JSON.stringify({
                        "提示信息": "发送失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(err);
                    return;
                }
                //通知
                push.inform(phoneto[index], accessKey, "*", {"提示信息": "成功", event: "message"});
                //response
                response.write(JSON.stringify({
                    "提示信息": "发送成功"
                }));
                response.end();
            });
        }
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
            var flag0 = 0;
            if (reply.length != 0) {
                flag0 = parseInt(from) + reply.length;
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