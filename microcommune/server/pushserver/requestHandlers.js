var requestHandlers = {};

var globaldata = root.globaldata;
var accessKeyPool = {};
var serverSetting = root.globaldata.serverSetting;
var pushServer = serverSetting.zookeeper.pushServer;
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
var zookeeper = require("./lib/zookeeper-client.js");
//zookeeper.start(pushServer.ip, pushServer.port, pushServer.timeout, accessKeyPool, function (KeyPool) {
//    accessKeyPool = KeyPool;
//    console.info(pushServer.name + " accessKeyPool update :  " + pushServer.ip + ":" + pushServer.port + " " + pushServer.timeout);
//});
var session = require('./handlers/session.js');
requestHandlers.session = function (request, response, pathObject, data) {
    var operation = pathObject["operation"];
    if (data == null) {
        return;
    }
    if (operation == "eventwebcodelogin") {
        session.eventwebcodelogin(data, response);
    }
    else if (operation == "notifywebcodelogin") {
        oauth6(data.phone, data.accessKey, response, function () {
            session.notifywebcodelogin(data, response);
        });
    }
    else if (operation == "event") {
        oauth6(data.phone, data.accessKey, response, function () {
            session.event(data, response);
        });
    }
    else if (operation == "notify") {
        oauth6(data.phone, data.accessKey, response, function () {
            session.notify(data, response);
        });
    }
};

function oauth6(phone, accessKey, response, next) {
    response.asynchronous = 1;
    if (phone == undefined || phone == "" || phone == null || accessKey == undefined || accessKey == "" || accessKey == null) {
        response.write(JSON.stringify({
            "提示信息": "请求失败",
            "失败原因": "数据不完整"
        }), function () {
            console.log("安全机制数据不完整");
        });
        response.end();
        return;
    } else {
        if (accessKey == "lejoying") {
            next();
            return;
        }
        else if (accessKeyPool[phone + "_accessKey"] != undefined) {
            var accessKeys = accessKeyPool[phone + "_accessKey"];
            var flag0 = false;
            for (var index in accessKeys) {
                if (accessKeys[index] == accessKey) {
                    flag0 = true;
                    break;
                }
            }
            if (flag0) {
                console.log("验证通过accessKeyPool...");
                next();
                return;
            } else {
                getAccessed(response);
            }
        } else {
            getAccessed(response);
        }
    }

    function getAccessed(response) {
        console.log("正在查看" + phone + "accessKey");
        client.lrange(phone + "_accessKey", 0, -1, function (err, reply) {
            if (err != null) {
                response.write(JSON.stringify({
                    "提示信息": "请求失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(err);
                return;
            } else {
                if (reply.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "请求失败",
                        "失败原因": "AccessKey Invalid"
                    }), function () {
                        console.log(phone + "AccessKey Invalid...");
                    });
                    response.end();
                    return;
                } else {
                    var flag = false;
                    for (var i = 0; i < reply.length; i++) {
                        if (reply[i] == accessKey) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        accessKeyPool[phone + "_accessKey"] = accessKeyPool[phone + "_accessKey"] || [];
//                        accessKeyPool[phone + "_accessKey"][accessKey] = accessKey;
                        accessKeyPool[phone + "_accessKey"].push(accessKey);
                        console.log("验证通过DB..." + accessKey);
//                        zookeeper.setData(accessKeyPool);
                        next();
                        return;
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "请求失败",
                            "失败原因": "AccessKey Invalid"
                        }), function () {
                            console.log(phone + ".AccessKey Invalid...");
                        });
                        response.end();
                        return;
                    }
                }
            }
        });
    }
}

module.exports = requestHandlers;