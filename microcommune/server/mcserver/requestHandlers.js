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
    else if (operation == "notify") {
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
    else if (operation == "verifyloginphone") {
        accountManage.verifyloginphone(data, response);
    }
    else if (operation == "verifylogincode") {
        accountManage.verifylogincode(data, response);
    }
    else if (operation == "getaccount") {
        accountManage.getaccount(data, response);
    }
    else if (operation == "modify") {
        accountManage.modify(data, response);
    }
    else if (operation == "qrcode") {
        accountManage.qrcode(data, response);
    }
}
var communityManage = require("./handlers/communityManage.js");
requestHandlers.communityManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "add") {
        communityManage.add(data, response);
    }
    else if (operation == "find") {
        communityManage.find(data, response);
    }
    else if (operation == "join") {
        communityManage.join(data, response);
    }
    else if (operation == "unjoin") {
        communityManage.unjoin(data, response);
    }
    else if (operation == "getcommunities") {
        communityManage.getcommunities(data, response);
    }
}


var relationManage = require("./handlers/relationManage.js");
requestHandlers.relationManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "addfriend") {
        relationManage.addfriend(data, response);
    }
    else if (operation == "moveout") {
        relationManage.moveorout(data, response);
    }
    else if (operation == "deletefriend") {
        relationManage.deletefriend(data, response);
    }
    else if (operation == "blacklist") {
        relationManage.blacklist(data, response);
    }
    else if (operation == "getfriends") {
        relationManage.getfriends(data, response);
    }
    else if (operation == "addcircle") {
        relationManage.addcircle(data, response);
    }
    else if (operation == "getcirclesandfriends") {
        relationManage.getcirclesandfriends(data, response);
    }
    else if (operation == "getaskfriends") {
        relationManage.getaskfriends(data, response);
    }
    else if (operation == "addfriendagree") {
        relationManage.addfriendagree(data, response);
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
var messageManage = require("./handlers/messageManage.js");
requestHandlers.messageManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "send") {
        messageManage.send(data, response);
    }
    else if (operation == "get") {
        messageManage.get(data, response);
    }
}

module.exports = requestHandlers;