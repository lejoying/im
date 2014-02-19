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
    else if (operation == "geotable_delete") {
        lbsManage.geotable_delete(data, response);
    }
    else if (operation == "column_create") {
        lbsManage.column_create(data, response);
    }
    else if (operation == "column_list") {
        lbsManage.column_list(data, response);
    }
    else if (operation == "column_detail") {
        lbsManage.column_detail(data, response);
    }
    else if (operation == "column_update") {
        lbsManage.column_update(data, response);
    }
    else if (operation == "column_delete") {
        lbsManage.column_delete(data, response);
    }
    else if (operation == "poi_create") {
        lbsManage.poi_create(data, response);
    }
    else if (operation == "poi_list") {
        lbsManage.poi_list(data, response);
    }
    else if (operation == "poi_detail") {
        lbsManage.poi_detail(data, response);
    }
    else if (operation == "poi_update") {
        lbsManage.poi_update(data, response);
    }
    else if (operation == "poi_delete") {
        lbsManage.poi_delete(data, response);
    }

    else if (operation == "nearby") {
        lbsManage.nearby(data, response);
    }
    else if (operation == "local") {
        lbsManage.local(data, response);
    }
    else if (operation == "bound") {
        lbsManage.bound(data, response);
    }
    else if (operation == "detail") {
        lbsManage.detail(data, response);
    }
    if (operation == "loginpoi") {
        lbsManage.loginpoi(data, response);
    } else if (operation == "grouppoi") {
        lbsManage.grouppoi(data, response);
    }
};

module.exports = requestHandlers;