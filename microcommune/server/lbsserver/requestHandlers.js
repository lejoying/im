var requestHandlers = {};
var lbsManage = require('./handlers/lbsManage.js');
requestHandlers.lbsManage = function (request, response, pathObject, data) {
    var operation = pathObject["operation"];
    if (data == null) {
        return;
    }
    if (operation == "geotable_create") {
        lbsManage.geotable_create(data, response);
    }
    else if (operation == "geotable_list") {
        lbsManage.geotable_list(data, response);
    }
    else if (operation == "geotable_detail") {
        lbsManage.geotable_detail(data, response);
    }
    else if (operation == "geotable_update") {
        lbsManage.geotable_update(data, response);
    }
    else if (operation == "poicreate") {
        lbsManage.poicreate(data, response);
    }
};

module.exports = requestHandlers;