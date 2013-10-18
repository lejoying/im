accessKeyPool = {};

var session = {};

var access = 0;
var sessionPool = {};
accountSession = {};

var redis = require("redis");
var saveClient;
var getClient;

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
    saveMessages(messages);
    for (var count in userlist) {
        var nowTime = new Date().getTime();
        processResponse(userlist[count], function process(sessionResponse) {
            console.log();
            while(new Date().getTime() - nowTime < 10000){

            }
            //getMessages();
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
            console.log(touid + "为离线状态");
        }
    }

    response.write(JSON.stringify({
        "information": "send success"
    }));
    response.end();
}

function saveMessages(messages) {
    saveClient = redis.createClient();
    saveClient.hmset("aa", messages, function (err, reply) {
        if (err != null) {
            console.log(err);
            saveClient.end();
            return;
        }
        saveClient.end();
    });
}

function getMessages() {
    getClient = redis.createClient();
    getClient.hgetall("aa", function (err, reply) {
        if (err != null) {
            console.log(err);
            getClient.end();
            return;
        }
        console.log(reply);
        getClient.end();
    });

}

module.exports = session;var session = {};

var access = 0;
var sessionPool = {};
accountSession = {};

var redis = require("redis");
var saveClient;
var getClient;

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
    saveMessages(messages);
    for (var count in userlist) {
        var nowTime = new Date().getTime();
        processResponse(userlist[count], function process(sessionResponse) {
            console.log();
            while(new Date().getTime() - nowTime < 10000){

            }
            //getMessages();
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
            console.log(touid + "为离线状态");
        }
    }

    response.write(JSON.stringify({
        "information": "send success"
    }));
    response.end();
}

function saveMessages(messages) {
    saveClient = redis.createClient();
    saveClient.hmset("aa", messages, function (err, reply) {
        if (err != null) {
            console.log(err);
            saveClient.end();
            return;
        }
        saveClient.end();
    });
}

function getMessages() {
    getClient = redis.createClient();
    getClient.hgetall("aa", function (err, reply) {
        if (err != null) {
            console.log(err);
            getClient.end();
            return;
        }
        console.log(reply);
        getClient.end();
    });

}
