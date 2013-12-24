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
            console.log("push---" + dataStr);
        }
    });
}
push.notifywebcodelogin = function (phone, phoneTo, accessKey, sessionID, next) {
    ajax.ajax({
        data: {
            phone: phone,
            phoneTo: phoneTo,
            accessKey: accessKey,
            sessionID: sessionID
        },
        type: 'POST',
        url: "http://127.0.0.1:8077/api2/session/notifywebcodelogin",
        success: function (dataStr) {
            console.log("push---" + dataStr);
            next(dataStr);
        },
        error: function () {
            next(JSON.stringify({
                "information": "notifywebcodelogin error"
            }));
        }

    });
}

module.exports = push;