/**
 * Date: 2013.04.15
 * session:
 *  http://127.0.0.1:8061/api2/session/event?account=user1&sessionID=user1231325456546
 *  http://127.0.0.1:8061/api2/session/notify?account=user1&sessionID=user1231325456546&eventid=update
 *     post     JSON.stringify(event={eventID:"update", data,....})
 */

var session = {};

var access = 0;

var sessionPool = {};
var accountSession = {};

session.get = function (uid, sessionID, response) {
    response.asynchronous = 1;

    console.log("1");
    var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null) {
        sessionResponse.end();
    }
    console.log("2");

    sessionPool[sessionID] = response;
    accountSession[uid] = accountSession[uid] || [];
    accountSession[uid][sessionID] = response;

   // console.log(accountSession);

    for(var sid in accountSession[user1]){

    }

}

function getMsg(uid,sessionID){
    var array = accountSession[uid];

    var reponse = accountSession[uid][sessionID];
    event = {
       "a":1
    }
    reponse.write(JSON.stringify(event));
    reponse.end();
}

session.notify = notify;
function notify(uid, sessionID, eventID, event, response) {

    event = event || {eventID: eventID};
    if (sessionID == "*") {
        var sessions = accountSession[uid];
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