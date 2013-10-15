var requestHandlers = {};

var globaldata = root.globaldata;

var session = require('./handlers/session');
requestHandlers.session = function (request, response, pathObject, getParam) {
    if (getParam == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "get") {
        var uid = getParam["uid"];
        var sessionID = getParam["sessionID"];
        var eventID = getParam["eventID"];
        session.get(uid, sessionID, response);
    }
    else if (operation == "send") {
        var uid = getParam["uid"];
        var userlist = JSON.parse(getParam["userlist"]);
        var messages = JSON.parse(getParam["messages"]);
        session.send(uid, userlist, messages, response);
    }
};

var accountManage = require("./handlers/accountManage.js");
requestHandlers.accountManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "verifyphone") {
        accountManage.verifyphone(data, response);
    }
    else if (operation == "verifycode") {
        accountManage.verifycode(data, response);
    }
    else if (operation == "verifypass") {
        accountManage.verifypass(data, response);
    }
    else if (operation == "auth") {
        accountManage.auth(data, response);
    }
    else if (operation == "exit") {
        accountManage.exit(data, response);
    }
    else if (operation == "help") {
        accountManage.help(data, response);
    }
}
var communityManage = require("./handlers/communityManage.js");
requestHandlers.communityManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "find") {
        communityManage.find(data, response);
    }
}


var relationManage = require("./handlers/relationManage.js");
requestHandlers.relationManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "join") {
        relationManage.join(data, response);
    }
    else if (operation == "addfriend") {
        relationManage.addfriend(data, response);
    }
}

module.exports = requestHandlers;