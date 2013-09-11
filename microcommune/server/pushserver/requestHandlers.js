var requestHandlers = {};

var globaldata = root.globaldata;


/**
 *  http://127.0.0.1:8061/api2/session/event?account=user1&sessionID=user1231325456546
 *  http://127.0.0.1:8061/api2/session/notify?account=user1&sessionID=user1231325456546&eventID=update
 *     post     JSON.stringify(event={eventID:"update", data,....})
 */
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


module.exports = requestHandlers;