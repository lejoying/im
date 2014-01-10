var requestHandlers = {};

var globaldata = root.globaldata;

var session = require('./handlers/session.js');
requestHandlers.session = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "event") {
        session.event(data, response);
    }
    else if (operation == "notify") {
        session.notify(data, response);
    }
};

module.exports = requestHandlers;