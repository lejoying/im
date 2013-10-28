var serverSetting = root.globaldata.serverSetting;
var circleManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/circle/modify
 ***************************************/
circleManage.modify = function(data, response){
    response.asynchronous = 1;
    var rid = data.rid;
    var name = data.name;
    var circle = {
        name: name
    };
    var query = [
        'MATCH (circle:Circle)',
        'WHERE circle.rid={rid}',
        'SET circle.name={name}',
        'RETURN circle'
    ].join('\n');
    var params = {
        rid: parseInt(rid),
        name: name
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else if(results.length>0){
            console.log("添加密友圈成功---");
            response.write(JSON.stringify({
                "提示信息": "修改成功"
            }));
            response.end();
        }else{
            response.write(JSON.stringify({
                "提示信息": "修改失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/circle/delete
 ***************************************/
circleManage.delete = function(data, response){
    response.asynchronous = 1;
    var rid = data.rid;
    var query = [
        'MATCH other-[r]-(circle:Circle)',
        'WHERE circle.rid={rid}',
        'DELETE circle,r',
        'RETURN circle,r'
    ].join('\n');
    var params = {
        rid: parseInt(rid)
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else if(results.length>0){
            console.log("删除密友圈成功---");
            response.write(JSON.stringify({
                "提示信息": "删除成功"
            }));
            response.end();
        }else{
            response.write(JSON.stringify({
                "提示信息": "删除失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
module.exports = circleManage;