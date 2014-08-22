var requestHandlers = {};
var lbsyunManage = require('./handlers/lbsyunManage.js');
requestHandlers.lbsManage = function (request, response, pathObject, data) {
    var operation = pathObject["operation"];
    if (data == null) {
        return;
    }
    if (operation == "geotable_create") {
        lbsyunManage.geotable_create(data, response);
    }
    else if (operation == "geotable_list") {
        lbsyunManage.geotable_list(data, response);
    }
    else if (operation == "geotable_detail") {
        lbsyunManage.geotable_detail(data, response);
    }
    else if (operation == "geotable_update") {
        lbsyunManage.geotable_update(data, response);
    }
    else if (operation == "geotable_delete") {
        lbsyunManage.geotable_delete(data, response);
    }
    else if (operation == "column_create") {
        lbsyunManage.column_create(data, response);
    }
    else if (operation == "column_list") {
        lbsyunManage.column_list(data, response);
    }
    else if (operation == "column_detail") {
        lbsyunManage.column_detail(data, response);
    }
    else if (operation == "column_update") {
        lbsyunManage.column_update(data, response);
    }
    else if (operation == "column_delete") {
        lbsyunManage.column_delete(data, response);
    }
    else if (operation == "poi_create") {
        lbsyunManage.poi_create(data, response);
    }
    else if (operation == "poi_list") {
        lbsyunManage.poi_list(data, response);
    }
    else if (operation == "poi_detail") {
        lbsyunManage.poi_detail(data, response);
    }
    else if (operation == "poi_update") {
        lbsyunManage.poi_update(data, response);
    }
    else if (operation == "poi_delete") {
        lbsyunManage.poi_delete(data, response);
    }

    else if (operation == "nearby") {
        lbsyunManage.nearby(data, response);
    }
    else if (operation == "local") {
        lbsyunManage.local(data, response);
    }
    else if (operation == "bound") {
        lbsyunManage.bound(data, response);
    }
    else if (operation == "detail") {
        lbsyunManage.detail(data, response);
    }
    if (operation == "updatelocation") {
        lbsyunManage.updatelocation(data, response);
    } else if (operation == "setgrouplocation") {
        lbsyunManage.setgrouplocation(data, response);
    } else if (operation == "nearbyaccounts") {
        lbsyunManage.nearbyaccounts(data, response);
    } else if (operation == "nearbygroups") {
        lbsyunManage.nearbygroups(data, response);
    } else if (operation == "modifyaccountlocation") {
        lbsyunManage.modifyAccountLocation(data, response);
    }
};

module.exports = requestHandlers;