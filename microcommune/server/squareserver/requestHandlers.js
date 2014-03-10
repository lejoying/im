var requestHandlers = {};

var globaldata = root.globaldata;

var squareManage = require('./handlers/squareManage.js');
requestHandlers.squareManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "sendsquaremessage") {
        squareManage.sendsquaremessage(data, response);
    }
    else if (operation == "getsquaremessage") {
        squareManage.getsquaremessage(data, response);
    }
};

module.exports = requestHandlers;