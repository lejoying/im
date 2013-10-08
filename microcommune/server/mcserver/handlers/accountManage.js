var accountManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

accountManage.verifyphone = function(data, response){
    console.log(data);
    var phone = data.phone;
    checkPhone();
    function checkPhone(){
        var query = [
            'MATCH account:Account',
            'WHERE account.phone! ={phone}',
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

            }else{
                
            }
        });
    }
    response.write(JSON.stringify({"提示消息":"手机号验证成功",phone:phone}));
    response.end();
}
accountManage.verifycode = function(data, response){
    console.log(data);
    var phone = data.phone;
    var code = data.code;
    response.write(JSON.stringify({"提示消息":"验证码正确",phone:phone}));
    response.end();
}
accountManage.verifypass = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var password = data.password;

}
/***************************************
 *     URL：/api2/account/auth
 ***************************************/
accountManage.auth = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var password = data.password;

    checkAccountNode();

    function checkAccountNode(){
        var query = [
            'MATCH account:Account',
            'WHERE account.phone! ={phone}',
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
                var accountNode = results.pop().account;
                if (accountNode.data.password == password) {
                    response.write(JSON.stringify({
                        "提示信息": "账号登录成功",
                        "account":accountNode.data
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
        });
    }
}
accountManage.trash = function(data, response){

}

module.exports = accountManage;