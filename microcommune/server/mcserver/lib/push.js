var ajax = require('./ajax.js');

var push = {};
push.inform = function (phone, phoneTo, accessKey, sessionID, event) {
    ajax.ajax({
        data: {
            phone: phone,
            accessKey: accessKey,
            phoneTo: phoneTo,
            sessionID: sessionID,
            event: JSON.stringify(event)
        },
        type: 'POST',
        url: "http://127.0.0.1:8077/api2/session/notify",
        success: function (dataStr) {
            console.log("push-notify--" + dataStr);
        }
    });
}
push.notifywebcodelogin = function (phone, accessKey, sessionID, next) {
    ajax.ajax({
        data: {
            phone: phone,
            accessKey: accessKey,
            sessionID: sessionID
        },
        type: 'POST',
        url: "http://127.0.0.1:8077/api2/session/notifywebcodelogin",
        success: function (dataStr) {
            console.log("push-notifywebcodelogin--" + dataStr);
            next(dataStr);
        },
        error: function () {
            next(JSON.stringify({
                "information": "notifywebcodelogin error"
            }));
        }

    });
}
push.smsSend = function (phone, message, next) {
    ajax.ajax({
        type: "POST",
        url: "http://127.0.0.1:8074/api2/sms/notify?",
        data: {
            phone: phone,
            message: message
        },
        success: function (data) {
            next(data);
            console.log("push-smsSend--" + data);
        }, error: function () {
            console.log("sms error");
        }
    });
}
module.exports = push;