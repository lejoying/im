var serverSetting = root.globaldata.serverSetting;
var relationManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/*************************************** ***************************************
 * *    Relation：Account Community
 *************************************** ***************************************/

/***************************************
 *     URL：/api2/relation/join
 ***************************************/
relationManage.join = function(data, response){
    response.asynchronous = 1;
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
/***************************************
 *     URL：/api2/relation/getCommunities
 ***************************************/
relationManage.getCommunities = function(data, response){
    response.asynchronous = 1;

    var phone = data.phone;
    var query = [
        'MATCH (account:Account)-[r:HAS_COMMUNITY]->(community:Community)',
        'WHERE account.phone={phone}',
        'RETURN community'
    ].join('\n');
    var params = {
        phone: phone
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            response.write(JSON.stringify({
                "提示信息" :  "获取社区失败",
                "错误原因": "数据异常"
            }));
            response.end();
            return;
        }else{
            console.log("获取社区成功---");
            var communities = [];
            var i=0;
            for(var index in results){
                i++;
                var it = results[index].community.data;
                delete it.locations;
                communities.push(it);
                if(i == results.length){
                    response.write(JSON.stringify({
                        "提示信息" :  "获取社区成功",
                        "communities": communities
                    }));
                    response.end();
                }
            }
        }
    });
}
/*************************************** ***************************************
 * *    Relation：Account Account
 *************************************** ***************************************/

/***************************************
 *     URL：/api2/relation/addfriend
 ***************************************/
relationManage.addfriend = function(data, response){
    response.asynchronous = 1;

    var phonefrom = data.phonefrom;
    var phoneto = data.phoneto;
    addFriendNode();

    function addFriendNode(){
        var query = [
            'MATCH (account1:Account),(account2:Account)',
            'WHERE account1.phone={phonefrom} AND account2.phone={phoneto}',
            'CREATE UNIQUE account1-[r:HAS_FRIEND]->account2',
            'RETURN  r'
        ].join('\n');

        var params = {
            phonefrom: phonefrom,
            phoneto: phoneto
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "添加失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            } else {
                console.log("添加成功---");
                response.write(JSON.stringify({
                    "提示信息": "添加成功"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/relation/getfriends
 ***************************************/
relationManage.getfriends = function(data, response){
    response.asynchronous = 1;

    var phone = data.phone;
    var query = [
        'MATCH (account1:Account)-[r:HAS_FRIEND]-(account2:Account)',
        'WHERE account1.phone={phone}',
        'RETURN account2'
    ].join('\n');
    var params = {
        phone: phone
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else{
            console.log("获取好友成功---");
            var friends = [];
            var i = 0;
            for(var index in results){
                i++;
                var it = results[index].account2.data;
                friends.push(it);
                if(i == results.length){
                    response.write(JSON.stringify({
                        "提示信息" :  "获取好友成功",
                        "friends": friends
                    }));
                    response.end();
                }
            }
        }
    });
}
/*************************************** ***************************************
 * *    Relation：Account Circle
 *************************************** ***************************************/


module.exports = relationManage;
