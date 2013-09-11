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

var redis = require("redis");
var redisClient = redis.createClient();

session.get = function (uid, sessionID, response) {
    response.asynchronous = 1;

    var sessionResponse = sessionPool[sessionID];
    if (sessionResponse != null) {
        sessionResponse.end();
    }

    sessionPool[sessionID] = response;
    accountSession[uid] = accountSession[uid] || [];
    accountSession[uid][sessionID] = response;

}

session.send = send;
function send(uid, userlist, messages, response) {

    for (var count in userlist) {
        processResponse(userlist[count], function process(sessionResponse) {
            sessionResponse.write(JSON.stringify(messages));
            sessionResponse.end();
        });
    }

    function processResponse(touid, process) {
        var sessions = accountSession[touid];
        if (sessions != undefined) {
            for (var sessionID in sessions) {
                var sessionResponse = sessions[sessionID];
                process(sessionResponse);
            }
        } else {
            console.log(touid+"为离线状态");
        }
    }

    response.write(JSON.stringify({
        "information": "send success"
    }));
    response.end();
}

function saveMessages(){

}

function getMessages (){

}

module.exports = session;