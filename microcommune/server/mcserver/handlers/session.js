var session = {};

var access = 0;
var sha1 = require("./../tools/sha1.js");
var sessionPool = {};
var accessKeyPool = {};
accountSession = {};
var count = 0;
session.eventwebcodelogin = function (data, response) {
    response.asynchronous = 1;
    var sessionID = data.sessionID;
    /*var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null && sessionResponse != undefined) {
        sessionResponse.write(JSON.stringify({
            "提示信息": "超时"
        }));
        sessionResponse.end();
    }*/
    sessionPool[sessionID] = response;
    count++;
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
    }
}
session.notifywebcodelogin = notifywebcodelogin;
function notifywebcodelogin(data, response) {
    var phone = data.phone;
    var sessionID = data.sessionID;
    var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null && sessionResponse != undefined) {
        var accessKey = sha1.hex_sha1(phone + new Date.getTime());
        sessionResponse.write(JSON.stringify({
            "提示信息": "web端二维码登录成功",
            phone: phone,
            accessKey: accessKey
        }));
        sessionResponse.end();
    } else {
        sessionResponse.write(JSON.stringify({
            "提示信息": "web端二维码登录失败",
            "失败原因": "数据异常"
        }));
        sessionResponse.end();
    }

    response.write(JSON.stringify({
        "information": "notifywebcodelogin success"
    }));
    response.end();
}
session.event = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    console.log(data);
    console.log(new Date().getTime() + "----");
    accountSession[phone] = accountSession[phone] || [];
    console.log("常连接--event");
    accountSession[phone][accessKey] = response;
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
    var phone = data.phone;
    var sessionID = data.sessionID;
    var eventID = data.eventID;
    var event = data.event;
    event = event || {eventID: eventID};
    if (sessionID == "*") {
        var sessions = accountSession[phone];
        for (var sessionID in sessions) {
            var sessionResponse = sessions[sessionID];
            sessionResponse.write(event);
            sessionResponse.end();
        }
    }
    else {
        var sessionResponse = sessionPool[sessionID];
        sessionResponse.write(event);
        sessionResponse.end();
    }

    response.write(JSON.stringify({
        "information": "notify success"
    }));
    response.end();
}

module.exports = session;