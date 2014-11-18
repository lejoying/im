var session = {};
var RSA = require('../../mcserver/tools/RSA');
RSA.setMaxDigits(38);
var pbkeyStr0 = RSA.RSAKeyStr("5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841",
    "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841",
    "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
var pbkey0 = RSA.RSAKey(pbkeyStr0);

var pvkeyStr0 = RSA.RSAKeyStr("10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
    "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
    "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
var pvkey0 = RSA.RSAKey(pvkeyStr0);
var access = 0;
var sha1 = require("./../../mcserver/tools/sha1.js");
var sessionPool = {};
var accessKeyPool = {};
accountSession = {};
//var count = 0;
session.sessionPool = sessionPool;
session.eventwebcodelogin = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var sessionID = data.sessionID;
    var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null && sessionResponse != undefined) {
        sessionResponse.writeHead(200, {
            "Content-Type": "text/javascript"
        });
        sessionResponse.end("window.code=408", function () {
            sessionPool[sessionID] = response;
        });
    } else {
        sessionPool[sessionID] = response;
    }
    /*count++;
     console.log(count);
     if (count > 100000) {
     var index = 0;
     for (var key in sessionPool) {
     delete sessionPool.key;
     index++;
     if (index == 20000)
     break;
     }
     count = count - index;
     }*/
}
session.notifywebcodelogin = notifywebcodelogin;
function notifywebcodelogin(data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var sessionID = data.sessionID;
    var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null && sessionResponse != undefined) {
        var accessKey = sha1.hex_sha1(phone + new Date().getTime());
        sessionResponse.write(JSON.stringify({
            "提示信息": "web端二维码登录成功",
            uid: RSA.encryptedString(pvkey0, phone),
            "accessKey": RSA.encryptedString(pvkey0, accessKey),
            "PbKey": pbkeyStr0
        }));
        sessionResponse.end();
        response.write(JSON.stringify({
            "information": "notifywebcodelogin success"
        }));
        response.end();
    } else {
        if (sessionResponse != undefined) {
            sessionResponse.write(JSON.stringify({
                "提示信息": "web端二维码登录失败",
                "失败原因": "数据异常"
            }));
            sessionResponse.end();
        }
        response.write(JSON.stringify({
            "information": "notifywebcodelogin failed"
        }));
        response.end();
    }
}
session.event = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    console.info(data);
    accountSession[phone] = accountSession[phone] || [];
    console.log("长连接--event");
    accountSession[phone][accessKey] = response;
    /*for (var index in accountSession[phone]) {

     var resp = accountSession[phone][index];
     resp.write(phone + "-" + index,function(){
     console.log("session---" + resp.statusCode);
     });
     }*/
    /*if(accountSession[phone]!=undefined){
     if(accountSession[phone][accessKey] != undefined){
     //        accountSession[phone] = accountSession[phone] || [];
     console.log("常连接--event");
     accountSession[phone][accessKey] = response;
     }
     }else{
     response.write(JSON.stringify({
     "提示信息": "登录失败",
     "失败原因": "请重新登录"
     }));
     response.end();
     }*/
}

session.notify = notify;
function notify(data, response) {
    //phone, sessionID, eventID, event
    var phone = data.phoneTo;
    var sessionID = data.sessionID;
    var eventID = data.eventID;
    var event = data.event;
//    event = event || {eventID: eventID};
    if (sessionID == "*") {
        var sessions = accountSession[phone];
        for (var sessionID in sessions) {
            var sessionResponse = sessions[sessionID];
            sessionResponse.write(event);
            sessionResponse.end();
        }
    } else {
        var sessionResponse = accountSession[phone][sessionID];
        sessionResponse.write(event);
        sessionResponse.end();
    }

    response.write(JSON.stringify({
        "information": "notify success"
    }));
    response.end();
}

module.exports = session;