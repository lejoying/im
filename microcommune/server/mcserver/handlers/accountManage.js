var serverSetting = root.globaldata.serverSetting;
var accountManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var sms = require("./../lib/SMS.js");
//sms.createsub("coolspan@sina.cn");此子账户已创建
var sms_power = false;
//sms.sendMsg("15210721344","qiaoxiaosong",function(data){console.log(data+"--");});

/***************************************
*     URL：/api2/account/verifyphone
***************************************/
accountManage.verifyphone = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var time = new Date().getTime().toString();
    console.log("1--"+phone+"--"+time.substr(time.length-6));
    var account = {
        phone: phone,
        code: time.substr(time.length-6),
        status: "init",
        time: new Date().getTime(),
        head:"",
        nickName:"用户"+phone,
        mainBusiness:""
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
                    var bad = time-parseInt(accountNode.data.time);
                    var code = "";
                    if(bad > 600000){
                        accountNode.data.code = time.substr(time.length-6);
                        accountNode.data.time = new Date().getTime();
                        accountNode.save();
                        code = time.substr(time.length-6);
                    }else{
                        code = accountNode.data.code;
                    }
                    console.log("注册验证码--"+phone+"--"+code);
                    var message = "微型公社手机验证码：" + code + "，欢迎您使用";
                    if(sms_power == true){
                        sms.sendMsg(phone, message,function(data){
                            var smsObj = JSON.parse(data);
                            if(smsObj.statusCode == "000000"){
                                response.write(JSON.stringify({
                                    "提示信息":"手机号验证成功",
                                    "phone": account.phone
                                }));
                                response.end();
                            }else{
                                response.write(JSON.stringify({
                                    "提示信息":"手机号验证失败",
                                    "失败原因": "手机号不正确"
                                }));
                                response.end();
                            }
                        });
                    }else{
                        response.write(JSON.stringify({
                            "提示信息":"手机号验证成功",
                            "phone": account.phone
                        }));
                        response.end();
                    }
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
                var message = "微型公社手机验证码：" + account.code + "，欢迎您使用";
                console.log(accountNode.data.code);
                if(sms_power == true){
                    sms.sendMsg(phone, message,function(data){
                        var smsObj = JSON.parse(data);
                        if(smsObj.statusCode == "000000"){
                            response.write(JSON.stringify({
                                "提示信息":"手机号验证成功",
                                "phone": accountNode.data.phone
                            }));
                            response.end();
                        }else{
                            response.write(JSON.stringify({
                                "提示信息":"手机号验证失败",
                                "失败原因": "手机号不正确"
                            }));
                            response.end();
                        }
                    });
                }else{
                    response.write(JSON.stringify({
                        "提示信息":"手机号验证成功",
                        "phone": accountNode.data.phone
                    }));
                    response.end();
                }
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
    response.asynchronous = 1;
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
 *     URL：/api2/account/verifyloginphone
 ***************************************/
accountManage.verifyloginphone = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    checkPhone();
    function checkPhone(){
        var query = [
            'MATCH (account:Account)',
            'WHERE (account.phone={phone}) AND (account.status={status1} OR account.status={status2})',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone,
            status1: "unjoin",
            status2: "success"
        };
        db.query(query, params, function (error, results){
            if(error){
                console.error(error);
                return;
            }else if(results.length == 0){
                response.write(JSON.stringify({
                    "提示信息":"验证码发送失败",
                    "失败原因":"手机号未注册"
                }));
                response.end();
            }else{
                var accountNode = results.pop().account;
                if(accountNode.data.status == "unjoin" || accountNode.data.status == "success"){
                    var time = new Date().getTime().toString();
                    var bad = time-parseInt(accountNode.data.time);
                    var code = "";
                    if(bad > 600000){
                        accountNode.data.code = time.substr(time.length-6);
                        accountNode.data.time = new Date().getTime();
                        accountNode.save();
                        code = time.substr(time.length-6);
                    }else{
                        code = accountNode.data.code;
                    }
                    console.log("登录验证码--"+phone+"--"+code);
                    var message = "微型公社手机验证码：" + code + "，欢迎您使用";
                    if(sms_power == true){
                        sms.sendMsg(phone, message,function(data){
                            var smsObj = JSON.parse(data);
                            if(smsObj.statusCode == "000000"){
                                response.write(JSON.stringify({
                                    "提示信息":"验证码发送成功",
                                    "phone": account.phone
                                }));
                                response.end();
                            }else{
                                response.write(JSON.stringify({
                                    "提示信息":"验证码发送成功",
                                    "失败原因": "服务器异常"
                                }));
                                response.end();
                            }
                        });
                    }else{
                        response.write(JSON.stringify({
                            "提示信息":"验证码发送成功",
                            "phone": account.phone
                        }));
                        response.end();
                    }
                    response.write(JSON.stringify({
                        "提示信息":"验证码发送成功",
                        "phone": account.phone
                    }));
                    response.end();
                }else{
                    response.write(JSON.stringify({
                        "提示信息":"验证码发送失败",
                        "失败原因":"手机号未注册"
                    }));
                    response.end();
                }
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/verifyloginpass
 ***************************************/
accountManage.verifylogincode = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var code = data.code;
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
                console.log(error);
                return;
            }else{
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                accountData.code = code;
                var bad = time-parseInt(accountData.time);
                if(bad > 600000){
                    console.log("验证码超时---");
                    response.write(JSON.stringify({
                        "提示信息": "登录失败" ,
                        "失败原因": "验证码超时"
                    }));
                    response.end();
                }else{
                    if(accountData.code == code){
                        console.log("登录成功---");
                        response.write(JSON.stringify({
                            "提示信息": "登录成功" ,
                            "account": accountData
                        }));
                        response.end();
                    }else{
                        console.log("验证码不正确---");
                        response.write(JSON.stringify({
                            "提示信息": "登录失败" ,
                            "失败原因": "验证码不正确"
                        }));
                        response.end();
                    }
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
    var accessKey = data.accessKey;
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
                delete accountData.password;
                createCircleNode(accountData.uid);
                accountSession[phone] = accountSession[phone] || [];
                accountSession[phone][accessKey] = null;
                response.write(JSON.stringify({
                    "提示信息": "注册成功" ,
                    "account":accountData
                }));
                response.end();
            }
        });
    }
    function createCircleNode(uid){
        var circle = {
            name: "密友圈1"
        };
        var query = [
            'START account=node({uid})',
            'CREATE UNIQUE account-[r:HAS_CIRCLE]->(circle:Circle{circle})',
            'SET circle.rid=ID(circle)',
            'RETURN circle'
        ];
        var params = {
            uid: uid,
            circle: circle
        };
        db.query(query, params, function(error, results){
            if(error){
                console.log(error);
                return;
            }else{
                console.log("注册成功，并创建默认密友圈");
            }
        });
    }
}

/***************************************
 *     URL：/api2/account/auth
 ***************************************/
accountManage.auth = function(data, response){
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var password = data.password;
    var accessKey = data.accessKey;
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
                        delete accountData.password;
                        console.log("账号登录成功---");
                        response.write(JSON.stringify({
                            "提示信息" :  "账号登录成功",
                            "account": accountData
                        }));
                        response.end();
                        accountSession[phone] = accountSession[phone] || [];
                        accountSession[phone][accessKey] = null;
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

accountManage.exit = function(data, response){

}
accountManage.verifywebcode = function(data, response){
    response.asynchronous = 1;
    console.log(data);
    var accessKey = data.accessKey;
    var it = sessionPool;
    var count = 0;
//    var flag = false;
    var resp = it[accessKey];
    /*for(var index in it){
        console.log(index);
        count++;
        if(count == 1){
            delete it[index];
        }
    }*/
    if(resp != null && resp != undefined){
        try{
            console.log("bbbbbbbbbbbbbbbbbbbb");
            var flag = false;
            resp.write(JSON.stringify({
                "提示信息": "等待验证"
            }), function(){
                flag = true;
                console.log("cccccccccccccccccccccc");
            });
            console.log(resp);
            resp.end(JSON.stringify({
                "提示信息2": "结束"
            }),function(){
                console.log("eeeeeeeeeeeeeeee"+flag);
            });
            console.log(resp);
            console.log("ddddddddddddddd---------");
        }catch(error){
            console.log(error);
        }

        response.write(JSON.stringify({
            "提示信息": "二维码验证成功"
        }));
        response.end();
    }else{
        response.write(JSON.stringify({
            "提示信息": "二维码验证失败",
            "失败原因": "二维码超时"
        }));
        response.end();
    }
    /*for(var index in it){
        count++;
        if(accessKey.equals(index)){
            var post = it[index];

            flag = true;
            break;
        }
        if(count = it.length){
            if(flag == false){
                response.write(JSON.stringify({
                    "提示信息": "二维码验证失败",
                    "失败原因": "二维码超时"
                }));
                response,end();
            }
        }
    }*/
}
accountManage.verifywebcodelogin = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var status = data.status;
    if(status == false){
        delete sessionPool.accessKey;
        response.write(JSON.stringify({
            "提示信息": "登录失败",
            "失败原因": "取消登录"
        }));
        response.end();
    }else{
       getAccountNode();
        account
    }
    function getAccountNode(){
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function(error, results){
            if(error){
                console.log(error);
                return;
            }else{
                var accountData = results.pop().account.data;
                delete accountData.password;
                response.write(JSON.stringify({
                    "提示信息": "登录成功",
                    "account": accountData
                }));
                response.end();
            }
        });
    }
}
accountManage.getaccount = function(data, response){
    response.asynchronous = 1;
    var phone = data.phone;
    var query = [
        'MATCH (account:Account)',
        'WHERE account.phone={phone}',
        'RETURN account'
    ].join('\n');
    var params = {
        phone: phone
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else if(results.length == 0){
            response.write(JSON.stringify({
                "提示信息": "获取失败",
                "失败原因": "用户不存在"
            }));
            response.end();
        }else{
            var accountData = results.pop().account.data;
            delete accountData.password;
            response.write(JSON.stringify({
                "提示信息": "获取成功",
                account: accountData
            }));
            response.end();
        }
    });
}


module.exports = accountManage;