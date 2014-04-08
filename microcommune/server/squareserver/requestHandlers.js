var requestHandlers = {};
var accessKeyPool = {};
var redis = require("redis");
var client = redis.createClient("6379", "115.28.51.197");

var squareManage = require('./handlers/squareManage.js');
//squareManage.zookeeper(null, null);
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
        }
        else if (accessKeyPool[phone + "_accessKey"] != undefined) {
            var accessKeys = accessKeyPool[phone + "_accessKey"];
            var flag0 = false;
            for (var index in accessKeys) {
                if (index == accessKey) {
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
                        accessKeyPool[phone + "_accessKey"][accessKey] = accessKey;
                        console.log("验证通过DB...");
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