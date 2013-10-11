var serverSetting = root.globaldata.serverSetting;
var accountManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require("./../lib/ajax.js");

/***************************************
 *     URL：/api2/account/verifyphone
 ***************************************/
accountManage.verifyphone = function(data, response){
    var phone = data.phone;
    var time = new Date().getTime().toString();
    console.log("1--"+phone+"--"+time.substr(time.length-6));
    var account = {
        phone: phone,
        code: time.substr(time.length-6),
        status: "init",
        time: new Date().getTime(),
        head:"",
        nickName:"用户"+phone
    };
    checkPhone();
    function checkPhone(){
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results){
            if(error){
                console.error(error);
                return;
            }else if(results.length == 0){
                createAccountNode();
            }else{
                var accountNode = results.pop().account;
                if(accountNode.data.status == "unjoin" || accountNode.data.status == "success"){
                    response.write(JSON.stringify({
                        "提示信息":"手机号验证失败",
                        "失败原因":"手机号已被注册"
                    }));
                    response.end();
                }else{
                    var time = new Date().getTime().toString();
                    accountNode.data.code = time.substr(time.length-6);
                    accountNode.data.time = new Date().getTime();
                    accountNode.save();
                    console.log("2--"+phone+"--"+time.substr(time.length-6));
                    response.write(JSON.stringify({
                        "提示信息":"手机号验证成功",
                        "phone": account.phone
                    }));
                    response.end();
                }
            }
        });
    }
    function createAccountNode() {
        var query = [
            'CREATE (account:Account{account})',
            'SET account.uid=ID(account)',
            'RETURN  account'
        ].join('\n');

        var params = {
            account: account
        };

        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                return;
            } else {
                var accountNode = results.pop().account;
                console.log("获取验证码成功---");
                response.write(JSON.stringify({
                    "提示信息":"手机号验证成功",
                    "phone": accountNode.data.phone
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/verifycode
 ***************************************/
accountManage.verifycode = function(data, response){
    var phone = data.phone;
    var code = data.code;

    checkPhoneCode();
    function checkPhoneCode(){
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone} AND account.status={status}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone,
            status: "init"
        };
        db.query(query, params, function (error, results){
            if(error){
                console.error(error);
                return;
            }else{
                var accountData = results.pop().account.data;
                if(accountData.code == code){
                    var time = new Date().getTime();
                    var bad = time-parseInt(accountData.time);
                    if(bad > 600000){
                        response.write(JSON.stringify({
                            "提示信息":"验证失败",
                            "失败原因":"验证码超时"
                        }));
                        response.end();
                    }else{
                        console.log("验证成功---");
                        response.write(JSON.stringify({
                            "提示信息":"验证成功",
                            "phone":phone
                        }));
                        response.end();
                    }
                }else{
                    response.write(JSON.stringify({
                        "提示信息":"验证失败",
                        "失败原因":"验证码不正确"
                    }));
                    response.end();
                }
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/verifypass
 ***************************************/
accountManage.verifypass = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var password = data.password;
    var longitude = data.longitude;
    var latitude = data.latitude;
    checkPhone();
    function checkPhone(){
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results){
            if(error){
                response.write(JSON.stringify({
                    "提示信息":"注册失败",
                    "失败原因":"保存数据遇到错误"
                }));
                response.end();
            }else{
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                accountData.password = password;
                accountData.status = "unjoin";
                accountNode.save();
                console.log("注册成功---");
                getCommunity(latitude, longitude);
            }
        });
    }
}
function getCommunity(latitude, longitude){
    console.log(latitude+"----"+longitude);
    ajax.ajax({
        type:"GET",
        url:"http://map.yanue.net/gpsApi.php",
        data:{
            lng:parseFloat(longitude),
            lat:parseFloat(latitude)
        },
        success:function(serverData){
            var localObj = JSON.parse(serverData);
            var lat = parseFloat(localObj.baidu.lat);
            var lng = parseFloat(localObj.baidu.lng);
//                var lat = 40.060000;
//                var lng = 116.420000;
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
                    var community = {};
                    var i = 0;
                    for(var index in results){
                        i++;
                        var it = results[index].community.data;
                        var locations = JSON.parse(it.locations);
                        if(it.name == "天通苑站"){
                            community = it;
                        }
                        if(((parseFloat(locations.lat1)<lat) && (lat<parseFloat(locations.lat2))) && ((parseFloat(locations.lng1)<lng) && (lng<parseFloat(locations.lng2)))){
                            flag = true;
                            response.write(JSON.stringify({
                                "提示信息":"注册成功",
                                "nowcommunity": it
                            }));
                            response.end();
                            break;
                        }
                        if(i == results.length){
                            delete community.locations;
                            if(flag == false){
                                response.write(JSON.stringify({
                                    "提示信息":"注册成功",
                                    "nowcommunity": community
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
/***************************************
 *     URL：/api2/account/auth
 ***************************************/
accountManage.auth = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var password = data.password;
    var longitude = data.longitude;
    var latitude = data.latitude;
    checkAccountNode();

    function checkAccountNode(){
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN  account'
        ].join('\n');

        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "账号登录失败",
                    "失败原因": "手机号不存在"
                }));
                response.end();
            } else {
                var accountData = results.pop().account.data;
                if(accountData.status == "init"){
                    response.write(JSON.stringify({
                        "提示信息": "账号登录失败",
                        "失败原因": "手机号不存在"
                    }));
                    response.end();
                }else{
                    if (accountData.password == password) {
                        console.log("账号登录成功---");
                        response.write(JSON.stringify({
                            "提示信息": "账号登录成功",
                            "account":accountData
                        }));
                        response.end();
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "账号登录失败",
                            "失败原因": "密码不正确"
                        }));
                        response.end();
                    }
                }
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/join
 ***************************************/
accountManage.join = function(data, response){
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
            'RETURN  r'
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
                response.write(JSON.stringify({
                    "提示信息": "加入成功"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/unjoin
 ***************************************/
accountManage.unjoin = function(data, response){
    response.asynchronous = 1;
    var cid = data.cid;
    var phone = data.phone;
    unJoinCommunityNode();

    function unJoinCommunityNode(){
        var query = [
            'MATCH (account:Account)-[r:HAS_COMMUNITY]->(community:Community)',
            'WHERE account.phone={phone} AND community.cid={cid}',
            'DELETE r',
            'RETURN account'
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
                    "提示信息": "移除失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            } else {
                console.log("移除成功---");
                response.write(JSON.stringify({
                    "提示信息": "移除成功"
                }));
                response.end();
            }
        });
    }
}
accountManage.trash = function(data, response){

}

module.exports = accountManage;