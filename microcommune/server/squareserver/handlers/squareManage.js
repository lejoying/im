var squareManage = {};

smsSession = {};
var sessionList = [];

var redis = require("redis");
var client = redis.createClient("6379", "115.28.212.79");


squareManage.sendsquaremessage = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    var messageStr = data.message;
    var message = {};
    try {
        message = JSON.parse(messageStr);
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息":"发布广播失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.error(e);
        return;
    }

//    client.



    var sessionID = data.sessionID;
    if (smsSession[sessionID] != undefined) {
        smsSession[sessionID] = response;
        for (var i = 0; i < sessionList.length; i++) {
            var smsResponse = sessionList[i];
            if (smsResponse.sessionID == sessionID) {
                sessionList.splice(i, 1);
                var smsResponse = {
                    sessionID: sessionID,
                    session: response
                };
                sessionList.push(smsResponse);
                break;
            }
        }
    } else {
        smsSession[sessionID] = response;
        var smsResponse = {
            sessionID: sessionID,
            session: response
        };
        sessionList.push(smsResponse);
    }
}

squareManage.notify = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var message = data.message;
    var smsResponse = (sessionList.pop());
    if (smsResponse != undefined) {
        delete smsSession[smsResponse.sessionID];
        var sessionResponse = smsResponse.session;
        sessionResponse.write(JSON.stringify({
            information: "event success",
            phone: phone,
            message: message
        }));
        sessionResponse.end();

        response.write(JSON.stringify({
            information: "notify success"
        }));
        response.end();
    } else {
        response.write(JSON.stringify({
            information: "notify failed"
        }));
        response.end();
    }
}

module.exports = squareManage;