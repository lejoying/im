var requestHandlers = {};

var globaldata = root.globaldata;

var alipayManage = require('./handlers/alipayManage.js');
requestHandlers.alipayManage = function (request, response, pathObject, data) {
    if (data == null) {
        return;
    }
    var operation = pathObject["operation"];
    if (operation == "alipayto") {
        alipayManage.alipayto(data, response);
    }
    else if (operation == "paynotify") {
        alipayManage.paynotify(data, response);
    }
    else if (operation == "batch_trans_notify") {
        alipayManage.batch_trans_notify(data, response);
    }
    else if (operation == "batch_trans_notify_by_notify_url") {
        alipayManage.batch_trans_notify_by_notify_url(data, response);
    }
};

module.exports = requestHandlers;