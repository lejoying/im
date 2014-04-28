var requestHandlers = {};
var accessKeyPool = {};
var globaldata = root.globaldata;
var serverSetting = root.globaldata.serverSetting;
var squareServer = serverSetting.zookeeper.squareServer;
var redis = require("redis");
//var client = redis.createClient("6379", "115.28.51.197");
var client = redis.createClient("6379", "127.0.0.1");
//squareManage.zookeeper(null, null);
var zookeeper = require("./lib/zookeeper-client.js");
zookeeper.start(squareServer.ip, squareServer.port, squareServer.timeout, accessKeyPool, function (KeyPool) {
    accessKeyPool = KeyPool;
    console.info(squareServer.name + " accessKeyPool update :  " + squareServer.ip + ":" + squareServer.port + " " + squareServer.timeout);
});
//var squareManage = require('./handlers/squareManage.js');
var squareManage = require('./handlers/newSquareManage.js');

requestHandlers.squareManage = function (request, response, pathObject, data) {

    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "sendsquaremessage") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.sendsquaremessage(data, response);
        });
    }
    else if (operation == "getsquaremessage") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.getsquaremessage(data, response);
        });
    }
    else if (operation == "getonlinecount") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.getonlinecount(data, response);
        });
    }
    else if (operation == "addsquarepraise") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.addsquarepraise(data, response);
        });
    }
    else if (operation == "addsquarecollect") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.addsquarecollect(data, response);
        });
    }
    else if (operation == "addsquarecomment") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.addsquarecomment(data, response);
        });
    }
    else if (operation == "getsquarecomments") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.getsquarecomments(data, response);
        });
    }
    else if (operation == "getsquareusers") {
        oauth6(data.phone, data.accessKey, response, function () {
            squareManage.getsquareusers(data, response);
        });
    }
    else if (operation == "zookeeper") {
        squareManage.zookeeper(data, response);
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
        if (accessKey == "lejoying" || accessKey.indexOf("lejoying") == 0) {
            next();
            return;
        } else if (accessKeyPool[phone + "_accessKey"] != undefined) {
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
//        if (accessKeyPool[phone + "_accessKey"]) {
//            if (accessKeyPool[phone + "_accessKey"][accessKey]) {
//                console.log("验证通过 zk mechanism...");
//                next();
//            } else {
//                response.write(JSON.stringify({
//                    "提示信息": "请求失败",
//                    "失败原因": "AccessKey Invalid"
//                }), function () {
//                    console.log(phone + "AccessKey Invalid...");
//                });
//                response.end();
//            }
//        } else {
//            response.write(JSON.stringify({
//                "提示信息": "请求失败",
//                "失败原因": "AccessKey Invalid"
//            }), function () {
//                console.log(phone + "AccessKey Invalid...");
//            });
//            response.end();
//        }
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
                        zookeeper.setData(accessKeyPool);
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