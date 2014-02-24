var serverSetting = root.globaldata.serverSetting;
var lbsManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require('./../lib/ajax.js');
var MD5 = require('./../lib/md5.js');
var gps = require('./../lib/convertGPS.js');
var ak = "qD4I881MqTR7NZQ2TYTa2ZGh";
var sk = "ACfYlSkHjGkuie3GmKVdsXTPuINEFUim";
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
    var url = (getMySign({
        name: name,
        geotype: geotype,
        is_published: is_published,
        ak: ak
    }));
    var crypto = require('crypto');
    console.log(url);
    console.log(encodeURI(url));
    var sn = crypto.createHash('md5').update("/geodata/v3/geotable/create" + "?" + url + sk, 'utf8').digest("hex");
    console.log(sn);
    console.log(MD5.hex_md5(url));
    ajax.ajax({
        type: "POST",
        ajaxType: "FORM",
        url: "http://api.map.baidu.com/geodata/v3/geotable/create",
        data: {
            name: name,
            geotype: geotype,
            is_published: is_published,
            ak: ak,
            sn: sn
        },
        success: function (data) {
            console.log(unescape(data.replace(/\\u/gi, '%u')));
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
function getSn(ak, sk, url, queryString_arrays, method) {

}
var getMySign = function (params) {
    var sPara = [];
    if (!params) return null;
    for (var key in params) {
        sPara.push([key, params[key]]);
    }
    console.log(sPara.toString());
    sPara.sort();
    var prestr = "";
    for (var i2 = 0; i2 < sPara.length; i2++) {
        var obj = sPara[i2];
        if (i2 == sPara.length - 1) {
            prestr = prestr + obj[0] + "=" + obj[1];
        } else {
            prestr = prestr + obj[0] + "=" + obj[1] + "&";
        }
    }
    return prestr;
    /*prestr = prestr + AlipayConfig.key;
     var crypto = require('crypto');
     str = prestr;
     return crypto.createHash('md5').update(prestr, 'utf8').digest("hex");*/
};
lbsManage.geotable_list = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v3/geotable/list",
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
        url: "http://api.map.baidu.com/geodata/v3/geotable/detail",
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
        url: "http://api.map.baidu.com/geodata/v3/geotable/update",
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
        url: "http://api.map.baidu.com/geodata/v3/geotable/delete",
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
        url: "http://api.map.baidu.com/geodata/v3/column/create",
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
        url: "http://api.map.baidu.com/geodata/v3/column/list",
        data: {
            geotable_id: "50513",
            ak: "9MBoVuWESUbrqxL5indWugNn"
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
        url: "http://api.map.baidu.com/geodata/v3/column/detail",
        data: {
            id: 26289,
            geotable_id: "50513",
            ak: "9MBoVuWESUbrqxL5indWugNn"
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
        url: "http://api.map.baidu.com/geodata/v3/column/update",
        data: {
            id: 26289,
            geotable_id: "50513",
            ak: "9MBoVuWESUbrqxL5indWugNn",
            is_sortfilter_field: 1,
            is_index_field: 1
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
        url: "http://api.map.baidu.com/geodata/v3/column/delete",
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
    var time1 = new Date().getTime();
    console.log("开始事件：" + time1);
    for (var i = 0; i < 100; i++) {
        ajax.ajax({
            type: "POST",
            ajaxType: "FORM",
            url: "http://api.map.baidu.com/geodata/v3/poi/create",
            data: {
                latitude: 40.2001 + i / 10000,
                longitude: 116.2001 + i / 10000,
                coord_type: 3,
                geotable_id: 47530,
                ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
                title: "xiaosong" + i
//            name: "song",
//            address: "北京市",
//            tags: "song"
            },
            success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
                var time2 = new Date().getTime();
                console.log("结束事件：" + time2);
                var time = time2 - time1;
//                console.log("耗时：" + time + "ms");
//                response.write(data);
//                response.end();
            }
        });
    }
    console.log("发送结束" + ((new Date().getTime()) - time1));
    response.end();
}
lbsManage.poi_list = function (data, response) {
    response.asynchronous = 1;
//    var time1 = new Date().getTime();
//    console.log("开始事件：" + time1 + "---------------------");
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v3/poi/list",
        data: {
            geotable_id: 50513,
            ak: "9MBoVuWESUbrqxL5indWugNn",
            gid: "120,120",
            page_index: 0,
            page_size: 200
        },
        success: function (data) {
//            console.log(unescape(data.replace(/\\u/gi, '%u')));
            var time2 = new Date().getTime();
//            console.log("结束事件：" + time2);
//            var time = time2 - time1;
//            console.log("耗时：" + time + "ms----------------------------");
            response.write(data);
            response.end();
        }
    });
}
lbsManage.poi_detail = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/geodata/v3/poi/detail",
        data: {
            geotable_id: 50512,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            id: 91964671
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
        url: "http://api.map.baidu.com/geodata/v3/poi/update",
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
        url: "http://api.map.baidu.com/geodata/v3/poi/delete",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            title: "xiaosong"
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
        url: "http://api.map.baidu.com/geosearch/v3/nearby",
        data: {
            geotable_id: 47530,
            ak: "qD4I881MqTR7NZQ2TYTa2ZGh",
            q: "",
            location: "116.25,40.25",//"116.25,40.25"
            radius: 5000, // 默认1000 M,
            sortby: "distance:1",
            page_index: 0,
            page_size: 12
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
        url: "http://api.map.baidu.com/geosearch/v3/local",
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
        url: "http://api.map.baidu.com/geosearch/v3/bound",
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
        url: "http://api.map.baidu.com/geosearch/v3/detail/50817831",
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
//----------------------------------------------------------------------------------------------------------------------
//var LBS_AK = "qD4I881MqTR7NZQ2TYTa2ZGh";
var LBS_AK = "9MBoVuWESUbrqxL5indWugNn";

/***************************************
 *     URL：/lbs/updatelocation
 ***************************************/
lbsManage.updatelocation = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var locationStr = data.location;
    var accountStr = data.account;
    var location = {};
    var account = {};

    var EARTH_RADIUS = 6378137.0;    //单位M
    var PI = Math.PI;

    function getRad(d) {
        return d * PI / 180.0;
    }

    function getFlatternDistance(lat1, lng1, lat2, lng2) {
        var f = getRad((lat1 + lat2) / 2);
        var g = getRad((lat1 - lat2) / 2);
        var l = getRad((lng1 - lng2) / 2);

        var sg = Math.sin(g);
        var sl = Math.sin(l);
        var sf = Math.sin(f);

        var s, c, w, r, d, h1, h2;
        var a = EARTH_RADIUS;
        var fl = 1 / 298.257;

        sg = sg * sg;
        sl = sl * sl;
        sf = sf * sf;

        s = sg * (1 - sl) + (1 - sf) * sl;
        c = (1 - sg) * (1 - sl) + sf * sl;

        w = Math.atan(Math.sqrt(s / c));
        r = Math.sqrt(s * c) / w;
        d = 2 * w * a;
        h1 = (3 * r - 1) / 2 / c;
        h2 = (3 * r + 1) / 2 / s;

        return d * (1 + fl * (h1 * sf * (1 - sg) - h2 * (1 - sf) * sg));
    }

    try {
        location = JSON.parse(locationStr);
        account = JSON.parse(accountStr);
//        console.log(getFlatternDistance(39.916, 116.404, 39.915, 116.404));
        /*response.write(JSON.stringify({
         "提示信息": "标记用户位置成功",
         phone: phone
         }));
         response.end();
         return;*/
        nearbyLoginPoi(account);
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "标记用户位置失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.log(e);
        return;
    }
    function nearbyLoginPoi(account) {
        ajax.ajax({
            type: "GET",
            url: "http://api.map.baidu.com/geodata/v3/poi/list",
            data: {
                geotable_id: 50512,
                ak: LBS_AK,
                phone: phone,
                title: phone,
                status: "1,1",
                tags: "account",
                page_index: 0,
                page_size: 200
            },
            success: function (data) {
                var poisObj = JSON.parse(data);
                if (poisObj.status == 0) {
                    if (poisObj.size == 0) {
                        createLoginPoi();
                    } else if (poisObj.size >= 1) {
                        var pois = poisObj.pois;
                        var createPoiFlag = true;
                        for (var i = 0; i < pois.length; i++) {
                            var poiObj = pois[i];
                            var modify_time = 0;
                            if (poiObj.modify_time) {
                                modify_time = (new Date(poiObj.modify_time)).getTime();
                            } else {
                                modify_time = (new Date(poiObj.create_time)).getTime();
                            }
                            var now_time = new Date().getTime();
                            var bad_time = now_time - modify_time;
                            if (bad_time < 1000 * 60 * 60) {
                                var distance = getFlatternDistance(location.latitude, location.longitude, (poiObj.location)[1], (poiObj.location)[0])
                                if (distance <= 1000) {
//                                    modifyLoginPoi(poiObj, true);
                                    response.write(JSON.stringify({
                                        "提示信息": "标记用户位置成功",
                                        phone: phone
                                    }));
                                    response.end();
                                    console.log("时间在1个小时内，" + bad_time + "并且距离在1公里内" + distance);
                                } else {
                                    if (createPoiFlag) {
                                        createPoiFlag = false;
                                        modifyLoginPoi(poiObj, true);
                                    } else {
                                        modifyLoginPoi(poiObj, false);
                                    }
                                }
                            } else {
                                if (i == pois.length - 1) {
                                    if (!createPoiFlag) {
                                        modifyLoginPoi(poiObj, true);
                                    } else {
                                        modifyLoginPoi(poiObj, false);
                                    }
                                } else {
                                    modifyLoginPoi(poiObj, false);
                                }
                            }
                        }
                    }
                } else {
                    console.log(unescape(data.replace(/\\u/gi, '%u')));
                    response.write(JSON.stringify({
                        "提示信息": "标记用户位置失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
        /*ajax.ajax({
         type: "GET",
         url: "http://api.map.baidu.com/geosearch/v3/nearby",
         data: {
         geotable_id: 50512,
         ak: LBS_AK,
         q: "",
         coord_type: 3,
         location: location.longitude + "," + location.latitude,//"116.25,40.25"
         radius: 50000, // 默认1000 M,
         filter: "phone:[" + phone + "]|status:[1]"
         },
         success: function (data1) {
         var poisObj = JSON.parse(data1);
         if (poisObj.status == 0) {
         if (poisObj.size == 0) {

         } else if (poisObj.size == 1) {
         //modify_time,create_time   format  example 1392783009
         var poiObj = (poisObj.contents)[0];
         if (!(poiObj.modify_time)) {
         var create_time = poiObj.create_time;
         next(create_time);
         } else {
         var modify_time = poiObj.modify_time;
         next(modify_time);
         }
         function next(time) {
         var now_time = Math.floor((new Date().getTime()) / 1000);
         var bad = now_time - time;
         if (bad > 60 * 60) {
         modifyLoginPoi(poiObj, true);
         } else {
         console.log("标记用户位置成功" + bad);
         response.write(JSON.stringify({
         "提示信息": "标记用户位置成功",
         phone: phone
         }));
         response.end();
         }
         }
         } else if (poisObj.size > 1) {
         var contents = poisObj.contents;
         for (var i = 0; i < contents.length; i++) {
         var poiObj = contents[i];
         if (i == contents.length - 1) {
         modifyLoginPoi(poiObj, true);
         } else {
         modifyLoginPoi(poiObj, false);
         }
         }
         } else {
         console.log(data1);
         response.write(JSON.stringify({
         "提示信息": "标记用户位置失败",
         "失败原因": "数据异常"
         }));
         response.end();
         }
         } else {
         console.log(data1);
         response.write(JSON.stringify({
         "提示信息": "标记用户位置失败",
         "失败原因": "数据异常"
         }));
         response.end();
         }
         }
         });*/
    }

    function modifyLoginPoi(poiObj, flag) {
        ajax.ajax({
            type: "POST",
            ajaxType: "FORM",
            url: "http://api.map.baidu.com/geodata/v3/poi/update",
            data: {
                geotable_id: 50512,
                ak: LBS_AK,
                id: poiObj.id,
                coord_type: 3,
                status: 0
            },
            success: function (data2) {
                var poiObj = JSON.parse(data2);
                if (flag) {
                    if (poiObj.status == 0) {
                        createLoginPoi();
                    } else {
                        console.log(unescape(data2.replace(/\\u/gi, '%u')));
                        response.write(JSON.stringify({
                            "提示信息": "标记用户位置失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                    }
                } else {
                    console.log("处理多个登录在线状态");
                }
            }
        });
    }

    function createLoginPoi() {
        ajax.ajax({
            type: "POST",
            ajaxType: "FORM",
            url: "http://api.map.baidu.com/geodata/v3/poi/create",
            data: {
                latitude: location.latitude,
                longitude: location.longitude,
                coord_type: 3,
                geotable_id: 50512,
                ak: LBS_AK,
                title: phone,
                phone: phone,
                nickName: account.nickName,
                head: account.head,
                mainBusiness: account.mainBusiness,
                status: 1,
                tags: "account",
                address: ""
            },
            success: function (data3) {
                var poiObj = JSON.parse(data3);
                if (poiObj.status == 0) {
                    console.log(unescape(data3.replace(/\\u/gi, '%u')));
                    response.write(JSON.stringify({
                        "提示信息": "标记用户位置成功",
                        phone: phone
                    }));
                    response.end();
                } else {
                    console.log(unescape(data3.replace(/\\u/gi, '%u')));
                    response.write(JSON.stringify({
                        "提示信息": "标记登录位置失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }
}
/***************************************
 *     URL：/lbs/setgrouplocation
 ***************************************/
lbsManage.setgrouplocation = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var locationStr = data.location;
    var groupStr = data.group;
    var group = {};
    var location = {};
    try {
        location = JSON.parse(locationStr);
        group = JSON.parse(groupStr);
        /*response.write(JSON.stringify({
         "提示信息": "标记群组位置成功",
         gid: group.gid
         }));
         response.end();
         return;*/
        checkGroup();
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "标记群组位置失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.log(e);
        return;
    }
    function checkGroup() {
        ajax.ajax({
            type: "GET",
            url: "http://api.map.baidu.com/geodata/v3/poi/list",
            data: {
                geotable_id: 50513,
                ak: LBS_AK,
                gid: group.gid + "," + group.gid,
                title: group.name,
                tags: "group"
            },
            success: function (data) {
                var poisObj = JSON.parse(data);
//                console.log(unescape(data.replace(/\\u/gi, '%u')));
                if (poisObj.status == 0) {
                    var contents = poisObj.pois;
                    if (poisObj.size == 0) {
                        createGroupPoi(group);
                    } else if (poisObj.size == 1) {
                        var poiObj = contents[0];
                        updateGroupPoi(poiObj.id);
                    } else {
                        console.log(data);
                        response.write(JSON.stringify({
                            "提示信息": "标记群组位置失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                    }
                } else {
                    console.log(data);
                    response.write(JSON.stringify({
                        "提示信息": "标记群组位置失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }

    function updateGroupPoi(id) {
        ajax.ajax({
            type: "POST",
            ajaxType: "FORM",
            url: "http://api.map.baidu.com/geodata/v3/poi/update",
            data: {
                geotable_id: 50513,
                ak: LBS_AK,
                id: id,
                coord_type: 3,
                latitude: location.latitude,
                longitude: location.longitude,
                name: group.name,
                title: group.name,
                description: group.description
            },
            success: function (data) {
                var poiObj = JSON.parse(data);
                if (poiObj.status == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "标记群组位置成功",
                        gid: group.gid
                    }));
                    response.end();
                } else {
                    console.log(data);
                    response.write(JSON.stringify({
                        "提示信息": "标记群组位置失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }

    function createGroupPoi(group) {
        ajax.ajax({
            type: "POST",
            ajaxType: "FORM",
            url: "http://api.map.baidu.com/geodata/v3/poi/create",
            data: {
                latitude: location.latitude,
                longitude: location.longitude,
                coord_type: 3,
                geotable_id: 50513,
                ak: LBS_AK,
                title: group.gid,
                gid: group.gid,
                name: group.name,
                description: group.description,
                tags: "group",
                address: ""
            },
            success: function (data) {
                var poiObj = JSON.parse(data);
                if (poiObj.status == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "标记群组位置成功",
                        gid: group.gid
                    }));
                    response.end();
                } else {
                    console.log(data);
                    response.write(JSON.stringify({
                        "提示信息": "标记群组位置失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }
}
/***************************************
 *     URL：/lbs/nearbyaccounts
 ***************************************/
lbsManage.nearbyaccounts = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var areaStr = data.area;
    var area = {};
    try {
        var accounts = [];
        for (var i = 0; i < 10; i++) {
            var account = {
                phone: "121" + i,
                nickName: "李建国" + i,
                head: "6330116f4ca1332647429154fbe50cd7b17bd95d.png",
                mainBusiness: "送水，按摩，家政",
                location: {
                    longitude: 116.25,
                    latitude: 40.25
                },
                modify_time: new Date().getTime(),
                distance: 100 * i
            };
            accounts.push(account);
        }
        response.write(JSON.stringify({
            "提示信息": "获取附近好友成功",
            accounts: accounts
        }));
        response.end();
        return;
        /*area = JSON.parse(areaStr);
         if (area.radius) {
         nearbyLoginAccounts(area);
         } else {
         area.radius = 2000;
         nearbyLoginAccounts(area);
         }*/
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "获取附近用户失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.log(e);
        return;
    }
    function nearbyLoginAccounts(area) {
        ajax.ajax({
            type: "GET",
            url: "http://api.map.baidu.com/geosearch/v3/nearby",
            data: {
                geotable_id: 50512,
                ak: LBS_AK,
                q: "",
                coord_type: 3,
                location: area.longitude + "," + area.latitude,//"116.25,40.25"
                radius: area.radius, // 默认1000 M,
                sortby: "distance:1",
                filter: "status:[1]",
                tags: "account",
                page_index: 0,
                page_size: 12
            },
            success: function (data) {
                var poisObj = JSON.parse(data);
                if (poisObj.status == 0) {
                    var contents = poisObj.contents;
                    var accounts = [];
                    for (var i = 0; i < contents.length; i++) {
                        var poiObj = contents[i];
                        var account = {
                            phone: poiObj.phone,
                            nickName: poiObj.nickName,
                            head: poiObj.head,
                            mainBusiness: poiObj.mainBusiness,
                            location: {
                                longitude: (poiObj.location)[0],
                                latitude: (poiObj.location)[1]
                            },
                            modify_time: poiObj.modify_time,
                            distance: poiObj.distance
                        };
                        accounts.push(account);
                    }
                    response.write(JSON.stringify({
                        "提示信息": "获取附近好友成功",
                        accounts: accounts
                    }));
                    response.end();
                } else {
                    console.log(data);
                    response.write(JSON.stringify({
                        "提示信息": "获取附近好友失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }
}
/***************************************
 *     URL：/lbs/nearbygroups
 ***************************************/
lbsManage.nearbygroups = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var areaStr = data.area;
    var area = {};
    try {
        var groups = [];
        for (var i = 0; i < 10; i++) {
            var group = {
                gid: i + 1,
                name: "分组" + i,
                description: "描述" + i,
                location: {
                    longitude: 116.25,
                    latitude: 40.25
                },
                modify_time: new Date().getTime(),
                distance: 100 * i
            };
            groups.push(group);
        }
        response.write(JSON.stringify({
            "提示信息": "获取附近群组成功",
            groups: groups
        }));
        response.end();
        return;
        /*area = JSON.parse(areaStr);
         if (area.radius) {
         nearbyGroups(area);
         } else {
         area.radius = 2000;
         nearbyGroups(area);
         }*/
    } catch (e) {
        response.write(JSON.stringify({
            "提示信息": "获取附近群组失败",
            "失败原因": "参数格式错误"
        }));
        response.end();
        console.log(e);
        return;
    }
    function nearbyGroups(area) {
        ajax.ajax({
            type: "GET",
            url: "http://api.map.baidu.com/geosearch/v3/nearby",
            data: {
                geotable_id: 50513,
                ak: LBS_AK,
                q: "",
                coord_type: 3,
                location: area.longitude + "," + area.latitude,//"116.25,40.25"
                radius: area.radius, // 默认1000 M,
                sortby: "distance:1",
                tags: "group",
                page_index: 0,
                page_size: 12
            },
            success: function (data) {
                var poisObj = JSON.parse(data);
                if (poisObj.status == 0) {
                    var contents = poisObj.contents;
                    var groups = [];
                    for (var i = 0; i < contents.length; i++) {
                        var poiObj = contents[i];
                        var group = {
                            gid: poiObj.gid,
                            name: poiObj.name,
                            description: poiObj.description,
                            location: {
                                longitude: (poiObj.location)[0],
                                latitude: (poiObj.location)[1]
                            },
                            modify_time: poiObj.modify_time,
                            distance: poiObj.distance
                        };
                        groups.push(group);
                    }
                    response.write(JSON.stringify({
                        "提示信息": "获取附近群组成功",
                        groups: groups
                    }));
                    response.end();
                } else {
                    console.log(data);
                    response.write(JSON.stringify({
                        "提示信息": "获取附近群组失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            }
        });
    }
}
module.exports = lbsManage;