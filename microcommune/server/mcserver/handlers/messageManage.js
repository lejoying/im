var serverSetting = root.globaldata.serverSetting;
var messageManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

var redis = require("redis");
var client = redis.createClient();
var push = require('../lib/push.js');
messageManage.send = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var phoneto = JSON.parse(data.phoneto);
    var message = JSON.parse(data.message);
    var messageOwn = JSON.stringify({
        type: message.type,
        phone: phone,
        phoneto: data.phoneto,
        content: message.content,
        time: new Date().getTime()
    });
    client.rpush(phone, messageOwn, function (err, reply) {
        if (err != null) {
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
                    console.log(err);
                    return;
                }
                //通知
                push.inform(phoneto[index], "*", {"提示信息": "成功", event: "message"});
                //response
                response.write(JSON.stringify({
                    "information": "send success"
                }));
                response.end();
            });
        }
    });
}
messageManage.get = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var flag = data.flag;
    if (flag == "none") {
        client.get(phone + "flag", function (err, reply) {
            if (err != null) {
                console.log(err);
                client.set(phone + "flag", 0, function (err, reply) {
                    if (err != null) {
                        console.log(err);
                        return;
                    }
                    get(0);
                });
                return;
            }
            get(reply);
        });
    } else {
        get(flag);
    }

    function get(from) {
        client.lrange(phone, from, -1, function (err, reply) {
            if (err != null) {
                console.log(err);
                return;
            }
            if (reply.length != 0) {
                client.set(phone + "flag", parseInt(from) + reply.length, function (err, reply) {
                    if (err != null) {
                        console.log(err);
                        return;
                    }
                });
            }
            response.write(JSON.stringify({
                "提示信息": "获取成功",
                messages: reply
            }));
            response.end();
        });
    }
}
module.exports = messageManage;