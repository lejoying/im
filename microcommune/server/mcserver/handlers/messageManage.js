var serverSetting = root.globaldata.serverSetting;
var messageManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

var redis = require("redis");
var saveClient;
var getClient;
messageManage.send = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var userlist = data.userlist;
    var messages = data.messages;
    function send(uid, userlist, messages, response) {
        saveMessages(messages);
        for (var index in userlist) {
            var nowTime = new Date().getTime();
            processResponse(userlist[index], function process(sessionResponse) {
                while(new Date().getTime() - nowTime < 10000){

                }
                //getMessages();
                sessionResponse.write(JSON.stringify(messages));
                sessionResponse.end();
            });
        }

        function processResponse(toPhone, process) {
            var sessions = accountSession[toPhone];
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
}
messageManage.get = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
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
}
module.exports = messageManage;