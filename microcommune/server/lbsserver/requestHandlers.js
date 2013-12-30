var requestHandlers = {};
var lbsManage = require('./handlers/lbsManage.js');
requestHandlers.lbsManage = function (request, response, pathObject, data) {
    var operation = pathObject["operation"];
    if (data == null) {
        return;
    }
    if (operation == "create") {
        lbsManage.create(data, response);
    }
    else if (operation == "list") {
        lbsManage.list(data, response);
    }
    else if (operation == "poicreate") {
        lbsManage.poicreate(data, response);
    }
};

module.exports = requestHandlers;