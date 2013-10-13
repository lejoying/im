var serverSetting = root.globaldata.serverSetting;
var communityManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/community/add
 ***************************************/
communityManage.add = function(data, response){

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
/***************************************
 *     URL：/api2/community/getall
 ***************************************/
communityManage.getall = function(data, response){

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
module.exports = communityManage;