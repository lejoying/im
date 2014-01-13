var session = {};

smsSession = {};
var sessionList = [];
session.event = function (data, response) {
    response.asynchronous = 1;
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

session.notify = function (data, response) {
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

module.exports = session;