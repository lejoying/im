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
    if (operation == "verifycode") {
        accountManage.verifycode(data, response);
    }
    if (operation == "verifypass") {
        accountManage.verifypass(data, response);
    }
    if (operation == "auth") {
        accountManage.auth(data, response);
    }
    if (operation == "trash") {
        accountManage.trash(data, response);
    }
}


var paccountManage = require("./handlers/paccountManage.js");
requestHandlers.paccountManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "get") {
        var uid = getParam["uid"];
        var sessionID = getParam["sessionID"];
        var eventID = getParam["eventID"];
        paccountManage.get(uid, sessionID, response);
    }
    else if (operation == "send") {
        var uid = getParam["uid"];
        var userlist = JSON.parse(getParam["userlist"]);
        var messages = JSON.parse(getParam["messages"]);
        paccountManage.send(uid, userlist, messages, response);
    }
}


module.exports = requestHandlers;