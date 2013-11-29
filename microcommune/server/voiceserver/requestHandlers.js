var requestHandlers = {};

var globaldata = root.globaldata;

var voiceManage = require("./handlers/voiceManage");
requestHandlers.voiceManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "upload") {
        voiceManage.upload(data, response);
    }
    else if (operation == "check") {
        voiceManage.check(data, response);
    }
    else if (operation == "get") {
        voiceManage.get(data, response);
    }
}

module.exports = requestHandlers;