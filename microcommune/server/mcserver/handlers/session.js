var session = {};

var access = 0;

sessionPool = {};
accessKeyPool = {};
accountSession = {};
var count = 0;
session.eventweb = function (data, response) {
    response.asynchronous = 1;
    var sessionID = data.sessionID;
    var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null) {
        sessionResponse.end();
    }
    sessionPool[sessionID] = response;
    count++;
    console.log(count);
    if(count>100000){
        var index = 0;
        for(var key in sessionPool){
            delete sessionPool.key;
            index++;
            if(index == 20000)
                break;
        }
        count = count-index;
    }
}
session.event = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var sessionID = data.sessionID;
    accountSession[phone] = accountSession[phone] || [];
    accountSession[phone][sessionID] = response;
}

session.notify = notify;
function notify(phone, sessionID, eventID, event, response) {

    event = event || {eventID: eventID};
    if (sessionID == "*") {
        var sessions = accountSession[phone];
        for (var sessionID in sessions) {
            var sessionResponse = sessions[sessionID];
            sessionResponse.write(JSON.stringify(event));
            sessionResponse.end();
        }
    }
    else {
        var sessionResponse = sessionPool[sessionID];
        sessionResponse.write(JSON.stringify(event));
        sessionResponse.end();
    }

    response.write(JSON.stringify({
        "information": "notify success"
    }));
    response.end();
}

module.exports = session;