var serverSetting = root.globaldata.serverSetting;
var communityManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/account/verifyphone
 ***************************************/
communityManage.add = function(data, response){

    var locations = {
        "x1":100,
        "y1":200,
        "x2":200,
        "y2":400
    };
    var community = {
        "name":"天通苑站",
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
            response.write(JSON.stringify({
                "提示信息":"创建服务站成功"
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/account/verifyphone
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
            for(var index in results){
                var it = results[index];
                console.log(JSON.stringify(it.community.data));
            }
            response.write(JSON.stringify({
                "提示信息": "获取所有成功",
                "count": JSON.stringify(results)
            }));
            response.end();
        }
    });
}
module.exports = communityManage;