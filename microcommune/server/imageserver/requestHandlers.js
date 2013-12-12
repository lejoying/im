var requestHandlers = {};

var globaldata = root.globaldata;

var imagesManage = require('./handlers/imagesManage.js');
requestHandlers.imagesManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "upload") {
        imagesManage.upload(data, response);
    }
    else if (operation == "check") {
        imagesManage.check(data, response);
    }
    else if (operation == "get") {
        imagesManage.get(data, response);
    }
    else if (operation == "show") {
        imagesManage.show(data, response);
    }
};

module.exports = requestHandlers;