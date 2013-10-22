var serverSetting = root.globaldata.serverSetting;
var circleManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/circle/add
 ***************************************/
circleManage.add = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var name = data.name;
    var query = [
        'MATCH (account:Account)',
        'WHERE account.phone={phone}',
        'CREATE UNIQUE account-[r:HAS_CIRCLE]->circle:Circle{circle}',
        'SET circle.rid=ID(circle)',
        'RETURN circle'
    ];0
}
module.exports = circleManage;