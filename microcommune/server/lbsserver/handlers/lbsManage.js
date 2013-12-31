var serverSetting = root.globaldata.serverSetting;
var lbsManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require('./../lib/ajax.js');
var gps = require('./../lib/convertGPS.js');
/***************************************
 *     URL：/lbs/create
 ***************************************/
lbsManage.geotable_create = function (data, response) {
    response.asynchronous = 1;
    console.log(JSON.stringify(data));
    var name = data.name;
    var geotype = data.geotype;
    var is_published = data.is_published;
    var ak = data.ak;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/geotable/create",
        data: {
            name: name,
            geotype: geotype,
            is_published: is_published,
            ak: ak
        },
        success: function (data) {
            //            console.log(unescape(data.replace(/\\u/gi, '%u')));
            console.log(data);
            response.write(data);
            response.end();
        }
    });
    //gps转换成百度的经纬度
    /*gps.toBaiDuLocation(113.93832783228, 22.502412986242, function (data) {
     console.log(data);
     });*/
}
lbsManage.geotable_list = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/geotable/list",
        data: {
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.geotable_detail = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/geotable/detail",
        data: {
            id:"46530",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.geotable_update = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/geotable/update",
        data: {
            id:"46350",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            is_published:1,
            name: "name"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.poicreate = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        url: "http://api.map.baidu.com/geodata/v2/poi/create",
        data: {
            latitude: 40.083009,
            longitude: 116.461552,
            coord_type: 3,
            geotable_id: new Date().getTime(),
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(JSON.stringify(data));
            response.end();
        }
    });
}
module.exports = lbsManage;