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
    var messages = data.message;
    client.rpush(phone, messages, function (err, reply) {
        if (err != null) {
            console.log(err);
            return;
        }
        for (var index in phoneto) {
            client.rpush(phoneto[index], messages, function (err, reply) {
                if (err != null) {
                    console.log(err);
                    return;
                }
                //通知
                push.inform(phoneto[index], "*", {event: "message"});
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
                client.set(phone + "flag", parseInt(from)+ reply.length, function (err, reply) {
                    if (err != null) {
                        console.log(err);
                        return;
                    }
                });
            }
            response.write(JSON.stringify(reply));
            response.end();
        });
    }
}
module.exports = messageManage;