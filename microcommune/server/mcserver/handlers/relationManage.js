var serverSetting = root.globaldata.serverSetting;
var relationManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/relation/join
 ***************************************/
relationManage.add = function(data, response){
    response.asynchronous = 1;
    console.log(data);
    var cid = data.cid;
    var phone = data.phone;
    joinCommunityNode();

    function joinCommunityNode(){
        var query = [
            'START community=node({cid})',
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'CREATE UNIQUE account-[r:HAS_COMMUNITY]->community',
            'RETURN  account, r'
        ].join('\n');

        var params = {
            cid: parseInt(cid),
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "加入失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            } else {
                console.log("加入成功---");
                var accountNode = results.pop().account;
                if(accountNode.data.status == "unjoin"){
                    accountNode.data.status = "success";
                    accountNode.save();
                }
                response.write(JSON.stringify({
                    "提示信息": "加入成功"
                }));
                response.end();
            }
        });
    }
}
module.exports = relationManage;
