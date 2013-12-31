var serverSetting = root.globaldata.serverSetting;
var lbsManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require('./../lib/ajax.js');
var gps = require('./../lib/convertGPS.js');

//LBS 云存储
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
            id: "46530",
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
            id: "46350",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            is_published: 1,
            name: "name"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.geotable_delete = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/geotable/delete",
        data: {
            id: "47531",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.column_create = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/column/create",
        data: {
            geotable_id: "47530",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            name: "名称",
            key: "name",
            type: 3,
            max_length: 100,
            is_sortfilter_field: 0,
            is_search_field: 1,
            is_index_field: 1
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.column_list = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/column/list",
        data: {
            geotable_id: "47530",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.column_detail = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/column/detail",
        data: {
            id: 20053,
            geotable_id: "47530",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.column_update = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/column/update",
        data: {
            id: 20053,
            geotable_id: "47530",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            name: "名称api"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.column_delete = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/column/delete",
        data: {
            id: 20053,
            geotable_id: "47530",
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}


lbsManage.poi_create = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/poi/create",
        data: {
            latitude: 40.3,
            longitude: 116.3,
            coord_type: 3,
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            title: "xiaosong"
//            name: "song",
//            address: "北京市",
//            tags: "song"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.poi_list = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/poi/list",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
//            name: "api"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.poi_detail = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v2/poi/detail",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            id: 50817106
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.poi_update = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/poi/update",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            id: 50817831,
            coord_type: 3,
            title: "coolspan"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.poi_delete = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v2/poi/delete",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            id: 50817106
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}

//LBS 云检索
lbsManage.nearby = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geosearch/v2/nearby",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            q: "xiaosong",
            location: "116.25,40.25",
            radius: 200000 // 默认1000 M
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.local = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geosearch/v2/local",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            q: "xiaosong"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.bound = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geosearch/v2/bound",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            q: "xiaosong",
            bounds: "116.1,40.1;116.2,40.2"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
lbsManage.detail = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geosearch/v2/detail/50817831",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh"
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            response.write(data);
            response.end();
        }
    });
}
module.exports = lbsManage;