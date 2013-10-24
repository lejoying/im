var requestHandlers = {};

var globaldata = root.globaldata;

var session = require('./handlers/session.js');
requestHandlers.session = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "eventweb") {
        session.eventweb(data, response);
    }
    else if (operation == "event") {
        session.event(data, response);
    }
    else if(operation == "notify"){
        session.notify(data, response);
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
    else if (operation == "verifywebcode") {
        accountManage.verifywebcode(data, response);
    }
    else if (operation == "verifywebcodelogin") {
        accountManage.verifywebcodelogin(data, response);
    }
    else if (operation == "getaccount") {
        accountManage.getaccount(data, response);
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
    else if (operation == "getcommunities") {
        relationManage.getcommunities(data, response);
    }
    else if (operation == "addfriend") {
        relationManage.addfriend(data, response);
    }
    else if (operation == "getfriends") {
        relationManage.getfriends(data, response);
    }
    else if (operation == "addcircle") {
        relationManage.addcircle(data, response);
    }
}

var circleManage = require("./handlers/circleManage.js");
requestHandlers.circleManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "modify") {
        circleManage.modify(data, response);
    }
    else if (operation == "delete") {
        circleManage.delete(data, response);
    }
}

module.exports = requestHandlers;