var serverSetting = root.globaldata.serverSetting;
var communityManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require("./../lib/ajax.js");
/*

*/
/***************************************
 *     URL：/api2/community/add
 ***************************************//*

communityManage.add = function(data, response){
    response.asynchronous = 1;
    var locations = { lat1: 40,
        lng1: 116,
        lat2: 40,
        lng2: 116
    };
    var community = {
        "name":"天通苑北站",
        "locations":JSON.stringify(locations)
    };
    var query = [
        'CREATE (community:Community{community})',
        'SET community.cid=ID(community)',
        'RETURN community'
    ].join('\n');

    var params = {
        community:community
    };

    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else{
            if(results.length == 0){
                response.write(JSON.stringify({
                    "提示信息":"创建服务站失败",
                    "失败原因":"数据异常"
                }));
                response.end();
            }else{
                response.write(JSON.stringify({
                    "提示信息":"创建服务站成功"
                }));
                response.end();
            }
        }
    });
}
*/
/***************************************
 *     URL：/api2/community/getall
 ***************************************//*

communityManage.getall = function(data, response){
    response.asynchronous = 1;
    var query = [
        'MATCH (community:Community)',
        'RETURN community'
    ].join('\n');

    var params = {};

    db.query(query, params, function(error,results){
        if(error){
            console.log(error);
            return;
        }else{
            if(results.length == 0){
                response.write(JSON.stringify({
                    "提示信息": "获取所有社区失败",
                    "失败原因": "无社区数据"
                }));
                response.end();
            }else{
                var communities = [];
                for(var index in results){
                    var it = results[index].community.data;
                    communities.push(it);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取所有社区成功",
                    "communities": communities
                }));
                response.end();
            }
        }
    });
}
*/
/***************************************
 *     URL：/api2/community/find
 ***************************************/
communityManage.find = function(data, response){
    response.asynchronous = 1;
    var longitude = data.longitude;
    var latitude = data.latitude;
    findCommunity(longitude, latitude);

    function findCommunity(longitude, latitude){
        console.log(new Date().getTime());
        ajax.ajax({
            type:"GET",
            url:"http://map.yanue.net/gpsApi.php",
            data:{
                lng:parseFloat(longitude),
                lat:parseFloat(latitude)
            },
            success:function(serverData){
                console.log(new Date().getTime());
                var localObj = JSON.parse(serverData);
                var lat = parseFloat(localObj.baidu.lat);
                var lng = parseFloat(localObj.baidu.lng);
                var query = [
                    'MATCH (community:Community)',
                    'RETURN community'
                ].join('\n');
                var params = {};
                db.query(query, params, function(error, results){
                    if(error){
                        console.log(error);
                        return;
                    }else{
                        var flag = false;
                        var i = 0;
                        var community = {};
                        for(var index in results){
                            i++;
                            var it = results[index].community.data;
                            if(it.name == "天通苑站"){
                                community = it;
                            }
                            var locations = JSON.parse(it.locations);
                            if(((parseFloat(locations.lat1)<lat) && (lat<parseFloat(locations.lat2))) && ((parseFloat(locations.lng1)<lng) && (lng<parseFloat(locations.lng2)))){
                                flag = true;
                                delete it.locations;
                                console.log("获取当前社区成功-"+JSON.stringify(it));
                                response.write(JSON.stringify({
                                    "提示信息": "获取成功",
                                    "community": it
                                }));
                                response.end();
                                break;

                            }
                            if(i == results.length){
                                delete community.locations;
                                if(flag == false){
                                    console.log("获取默认社区成功--"+JSON.stringify(community));
                                    response.write(JSON.stringify({
                                        "提示信息": "获取失败",
                                        "失败原因": "社区不存在",
                                        community: community
                                    }));
                                    response.end();
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}
module.exports = communityManage;