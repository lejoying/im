var requestHandlers = {};

var globaldata = root.globaldata;

var squareManage = require('./handlers/squareManage.js');
requestHandlers.squareManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "event") {
        squareManage.event(data, response);
    }
    else if (operation == "notify") {
        squareManage.notify(data, response);
    }
};

module.exports = requestHandlers;