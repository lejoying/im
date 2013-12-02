var ajax = require('./ajax.js');

var push = {};
push.inform = function (phone, sessionID, event) {
    ajax.ajax({
        data: {
            phone: phone,
            sessionID: sessionID,
            event: JSON.stringify(event)
        },
        type: 'POST',
        url: "http://127.0.0.1:8071/api2/session/notify",
        success: function (dataStr) {
            console.log("push---" + dataStr);
        }
    });
}
push.notifywebcodelogin = function (phone, sessionID, next) {
    ajax.ajax({
        data: {
            phone: phone,
            sessionID: sessionID
        },
        type: 'POST',
        url: "http://127.0.0.1:8071/api2/session/notifywebcodelogin",
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