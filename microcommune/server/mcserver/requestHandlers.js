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
    var phone0 = RSA.decryptedString(pvkey0, data.phone);
    var accessKey0 = RSA.decryptedString(pvkey0, data.accessKey);
    data.phone = phone0;
    data.accessKey = accessKey0;

    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "eventwebcodelogin") {
        if (oauth6(data.phone, data.accessKey, response))
            session.eventwebcodelogin(data, response);
    }
    else if (operation == "notifywebcodelogin") {
        if (oauth6(data.phone, data.accessKey, response))
            session.notifywebcodelogin(data, response);
    }
    else if (operation == "event") {
        if (oauth6(data.phone, data.accessKey, response))
            session.event(data, response);
    }
    else if (operation == "notify") {
        if (oauth6(data.phone, data.accessKey, response))
            session.notify(data, response);
    }
};

var accountManage = require("./handlers/accountManage.js");
requestHandlers.accountManage = function (request, response, pathObject, data) {
    var phone0 = RSA.decryptedString(pvkey0, data.phone);
    var accessKey0 = RSA.decryptedString(pvkey0, data.accessKey);
    data.phone = phone0;
    data.accessKey = accessKey0;

    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "verifyphone") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.verifyphone(data, response);
    }
    else if (operation == "verifycode") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.verifycode(data, response);
    }
    else if (operation == "auth") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.auth(data, response);
    }
    else if (operation == "get") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.get(data, response);
    }
    else if (operation == "modify") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.modify(data, response);
    }

    else if (operation == "exit") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.exit(data, response);
    }
    else if (operation == "verifywebcode") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.verifywebcode(data, response);
    }
    else if (operation == "verifywebcodelogin") {
        if (oauth6(data.phone, data.accessKey, response))
            accountManage.verifywebcodelogin(data, response);
    }
}
var communityManage = require("./handlers/communityManage.js");
requestHandlers.communityManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "add") {
        communityManage.add(data, response);
    }
    else if (operation == "find") {
        communityManage.find(data, response);
    }
    else if (operation == "join") {
        communityManage.join(data, response);
    }
    else if (operation == "unjoin") {
        communityManage.unjoin(data, response);
    }
    else if (operation == "getcommunities") {
        communityManage.getcommunities(data, response);
    }
}


var relationManage = require("./handlers/relationManage.js");
requestHandlers.relationManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "addfriend") {
        relationManage.addfriend(data, response);
    }
    else if (operation == "deletefriend") {
        relationManage.deletefriend(data, response);
    }
    else if (operation == "blacklist") {
        relationManage.blacklist(data, response);
    }
    else if (operation == "getfriends") {
        relationManage.getfriends(data, response);
    }
    else if (operation == "getcirclesandfriends") {
        relationManage.getcirclesandfriends(data, response);
    }
    else if (operation == "getaskfriends") {
        relationManage.getaskfriends(data, response);
    }
    else if (operation == "addfriendagree") {
        relationManage.addfriendagree(data, response);
    }
}

var circleManage = require("./handlers/circleManage.js");
requestHandlers.circleManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "modify") {
        circleManage.modify(data, response);
    }
    else if (operation == "delete") {
        circleManage.delete(data, response);
    }
    else if (operation == "moveout") {
        circleManage.moveout(data, response);
    }
    else if (operation == "moveorout") {
        circleManage.moveorout(data, response);
    }
    else if (operation == "addcircle") {
        circleManage.addcircle(data, response);
    }
}
var messageManage = require("./handlers/messageManage.js");
requestHandlers.messageManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "send") {
        messageManage.send(data, response);
    }
    else if (operation == "get") {
        messageManage.get(data, response);
    }
    else if (operation == "deletes") {
        messageManage.deletes(data, response);
    }
}
var webcodeManage = require("./handlers/webcodeManage.js");
requestHandlers.webcodeManage = function (request, response, pathObject, data) {
    var phone0 = RSA.decryptedString(pvkey0, data.phone);
    var accessKey0 = RSA.decryptedString(pvkey0, data.accessKey);
    data.phone = phone0;
    data.accessKey = accessKey0;

    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "webcodelogin") {
        if (oauth6(data.phone, data.accessKey, response)) {
            webcodeManage.webcodelogin(data, response);
        }
    }
}
function oauth6(phone, accessKey, response) {
    if (phone == undefined || phone == "" || phone == null || accessKey == undefined || accessKey == "" || accessKey == null) {
        response.write(JSON.stringify({
            "提示信息": "请求失败",
            "失败原因": "数据不完整"
        }));
        response.end();
        return false;
    }
    if (accessKeyPool[phone + "_mobile"] != undefined) {
        return true;
    } else {
        client.get(phone + "_mobile", function (err, reply) {
            if (err) {
                response.write(JSON.stringify({
                    "提示信息": "请求失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(err);
                return false;
            } else {
                if (reply == null) {
                    response.write(JSON.stringify({
                        "提示信息": "请求失败",
                        "失败原因": "令牌无效"
                    }));
                    response.end();
                    console.log("令牌无效...");
                    return false;
                } else {
                    if (reply != accessKey) {
                        response.write(JSON.stringify({
                            "提示信息": "请求失败",
                            "失败原因": "令牌无效"
                        }));
                        response.end();
                        console.log("令牌超时...");
                        return false;
                    } else {
                        accessKeyPool[phone + "_mobile"] = reply;
                        console.log("验证通过...");
                        return true;
                    }
                }
            }
            return false;
        });
    }
}

module.exports = requestHandlers;