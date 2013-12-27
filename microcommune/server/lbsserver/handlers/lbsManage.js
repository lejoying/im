var serverSetting = root.globaldata.serverSetting;
var lbsManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require('./../lib/ajax.js');
/***************************************
 *     URL：/api2/lbs/create
 ***************************************/
lbsManage.create = function (data, response) {
    response.asynchronous = 1;
    console.log(data.name);
    var name = data.name;
    var geotype = data.geotype;
    var is_published = data.is_published;
    var ak = data.ak;
    /*ajax.ajax({
        type: "POST",
        url: "http://api.map.baidu.com/geodata/databox?method=create",
        data: {
            name: name,
            "ak": ak
        },
        success: function (data) {
            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(JSON.stringify(data));
            response.end();
        }
    });*/
    ajax.ajax({
        type: "POST",
        url: "http://api.map.baidu.com/geodata/v2/geotable/create",
        data: {
            name: name,
            geotype: parseInt(geotype),
            is_published: parseInt(is_published),
            "ak": ak
        },
        success: function (data) {
            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(JSON.stringify(data));
            response.end();
        }
    });
}
lbsManage.list = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/geotable/list",
        data: {
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