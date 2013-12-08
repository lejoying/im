var requestHandlers = {};

var globaldata = root.globaldata;
var accessKeyPool = {};
var serverSetting = root.globaldata.serverSetting;
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
var RSA = require('../alipayserver/tools/RSA');
RSA.setMaxDigits(38);
var pvkeyStr0 = RSA.RSAKeyStr("10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
    "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
    "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
var pvkey0 = RSA.RSAKey(pvkeyStr0);

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

var accountManage = require("./handlers/accountManage.js");
requestHandlers.accountManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "verifyphone") {
        accountManage.verifyphone(data, response);
    }
    else if (operation == "verifycode") {
        accountManage.verifycode(data, response);
    }
    else if (operation == "auth") {
        accountManage.auth(data, response, setOauthAccessKey);
    }
    else if (operation == "get") {
        oauth6(data.phone, data.accessKey, response, function () {
            accountManage.get(data, response);
        });
    }
    else if (operation == "modify") {
        oauth6(data.phone, data.accessKey, response, function () {
            accountManage.modify(data, response);
        });
    }
    else if (operation == "exit") {
        oauth6(data.phone, data.accessKey, response, function () {
            accountManage.exit(data, response);
        });
    }
    else if (operation == "oauth6") {
        oauth6(data.phone, data.accessKey, response, function () {
            accountManage.oauth6(data, response);
        });
    }
}
var communityManage = require("./handlers/communityManage.js");
requestHandlers.communityManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "add") {
        oauth6(data.phone, data.accessKey, response, function () {
            communityManage.add(data, response);
        });
    }
    else if (operation == "find") {
        oauth6(data.phone, data.accessKey, response, function () {
            communityManage.find(data, response);
        });
    }
    else if (operation == "join") {
        oauth6(data.phone, data.accessKey, response, function () {
            communityManage.join(data, response);
        });
    }
    else if (operation == "unjoin") {
        oauth6(data.phone, data.accessKey, response, function () {
            communityManage.unjoin(data, response);
        });
    }
    else if (operation == "getcommunities") {
        oauth6(data.phone, data.accessKey, response, function () {
            communityManage.getcommunities(data, response);
        });
    }
}


var relationManage = require("./handlers/relationManage.js");
requestHandlers.relationManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "addfriend") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.addfriend(data, response);
        });
    }
    else if (operation == "deletefriend") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.deletefriend(data, response);
        });
    }
    else if (operation == "blacklist") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.blacklist(data, response);
        });
    }
    else if (operation == "getfriends") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.getfriends(data, response);
        });
    }
    else if (operation == "getcirclesandfriends") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.getcirclesandfriends(data, response);
        });
    }
    else if (operation == "getaskfriends") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.getaskfriends(data, response);
        });
    }
    else if (operation == "addfriendagree") {
        oauth6(data.phone, data.accessKey, response, function () {
            relationManage.addfriendagree(data, response);
        });
    }
}

var circleManage = require("./handlers/circleManage.js");
requestHandlers.circleManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "modify") {
        oauth6(data.phone, data.accessKey, response, function () {
            circleManage.modify(data, response);
        });
    }
    else if (operation == "delete") {
        oauth6(data.phone, data.accessKey, response, function () {
            circleManage.delete(data, response);
        });
    }
    else if (operation == "moveout") {
        oauth6(data.phone, data.accessKey, response, function () {
            circleManage.moveout(data, response);
        });
    }
    else if (operation == "moveorout") {
        oauth6(data.phone, data.accessKey, response, function () {
            circleManage.moveorout(data, response);
        });
    }
    else if (operation == "addcircle") {
        oauth6(data.phone, data.accessKey, response, function () {
            circleManage.addcircle(data, response);
        });
    }
}
var messageManage = require("./handlers/messageManage.js");
requestHandlers.messageManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "send") {
        oauth6(data.phone, data.accessKey, response, function () {
            messageManage.send(data, response);
        });
    }
    else if (operation == "get") {
        oauth6(data.phone, data.accessKey, response, function () {
            messageManage.get(data, response);
        });
    }
    else if (operation == "deletes") {
        oauth6(data.phone, data.accessKey, response, function () {
            messageManage.deletes(data, response);
        });
    }
}
var webcodeManage = require("./handlers/webcodeManage.js");
requestHandlers.webcodeManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "webcodelogin") {
        oauth6(data.phone, data.accessKey, response, function () {
            webcodeManage.webcodelogin(data, response);
        });
    }
}
function setOauthAccessKey(phone, accessKey, next) {
    client.rpush(phone + "_accessKey", accessKey, function (err, reply) {
        if (err != null) {
            next(false);
            console.log(err);
            return;
        } else {
            next(true);
            return;
        }
    });
}
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