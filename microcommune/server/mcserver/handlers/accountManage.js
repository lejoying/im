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
                    accountNode.data.code = time.substr(time.length-6);
                    accountNode.data.time = new Date().getTime();
                    accountNode.save();
                    console.log("2--"+phone+"--"+time.substr(time.length-6));
                    var message = "乐家品质生活服务手机验证码：" + time.substr(time.length-6) + "，欢迎您使用【乐家生活】";
//                    sendPhoneMessage("18612450783",message);
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
                var message = "乐家品质生活服务手机验证码：" + account.code + "，欢迎您使用【乐家生活】";
//                sendPhoneMessage("18612450783",message);
                response.write(JSON.stringify({
                    "提示信息":"手机号验证成功",
                    "phone": accountNode.data.phone
                }));
                response.end();
            }

        });
    }
    function sendPhoneMessage(phone, message) {
            ajax.ajax({
                type: 'GET',
                url: "http://11529-c9239.sms-api.63810.com/api/SmsSend/user/wsds/hash/54c0b95f55a8851cc15f0ccaaea116ae/encode/utf-8/smstype/notify",
                data: {mobile: phone, content: message},
                success: function (dataStr) {
                    //todo check if the message sent failed.
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
                delete accountData.password;
                getCommunity(latitude, longitude, accountData, "注册成功", response);
            }
        });
    }
}
function getCommunity(latitude, longitude, accountData, msg, response){
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
                    var nowcommunity = {};
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
                            nowcommunity = it;
                            if(accountData.status == "unjoin"){
                                response.write(JSON.stringify({
                                    "提示信息": msg,
                                    "account":accountData,
                                    "nowcommunity": it
                                }));
                                response.end();
                                break;
                            }else{
                                getCommunities(accountData, nowcommunity, response);
                                break;
                            }
                        }
                        if(i == results.length){
                            delete community.locations;
                            if(accountData.status == "unjoin"){
                                if(flag == false){
                                    console.log("unjoin false");
                                    response.write(JSON.stringify({
                                        "提示信息": msg,
                                        "account":accountData,
                                        "nowcommunity": community
                                    }));
                                    response.end();
                                }
                            }else{
                                getCommunities(accountData, nowcommunity, response);
                            }
                        }
                    }
                }
            });
        }
    });
}
function getCommunities(accountData, nowcommunity, response){
    var query = [
        'MATCH (account:Account)-[r:HAS_COMMUNITY]->(community:Community)',
        'WHERE account.uid={uid}',
        'RETURN community'
    ].join('\n');
    var params = {
        uid:accountData.uid
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else{
            console.log("获取社区成功---");
            var communities = [];
            if(results.length != 0){
                for(var index in results){
                    var it = results[index].community.data;
                    delete it.locations;
                    communities.push(it);
                }
                getFriends(accountData, nowcommunity, communities, response);
            }else{
                getFriends(accountData, nowcommunity, communities, response);
            }
        }
    });
}
function getFriends(accountData, nowcommunity, communities, response){
    var query = [
        'MATCH (account1:Account)-[r:HAS_FRIEND]->(account2:Account)',
        'WHERE account1.uid={uid}',
        'RETURN account2'
    ].join('\n');
    var params = {
        uid:accountData.uid
    };
    db.query(query, params, function(error, results){
        if(error){
            console.log(error);
            return;
        }else{
            console.log("获取好友成功---");
            var friends = [];
            if(results.length != 0){
                var i = 0;
                for(var index in results){
                    i++;
                    var it = results[index].account2.data;
                    friends.push(it);
                    if(i == results.length){
                        response.write(JSON.stringify({
                            "提示信息" :  "账号登录成功",
                            "account": accountData,
                            "nowcommunity": nowcommunity,
                            "communities": communities,
                            "friends": friends
                        }));
                        response.end();
                    }
                }
            }else{
                console.log("无好友信息---");
                response.write(JSON.stringify({
                    "提示信息" :  "账号登录成功",
                    "account": accountData,
                    "nowcommunity": nowcommunity,
                    "communities": communities,
                    "friends": friends
                }));
                response.end();
            }
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
accountManage.help = function(data, response){
    var md5 = function(){return require('crypto').createHash('md5')};

    var ACCOUNT_ID = '0000000041b645530141b9d1b56c0054'; //账户ID
    var ACCOUNT_TOKEN = 'a84c27dd1c3646e280ee7b9cdd4041f8'; //账户TOKEN
    var APP_ID = 'aaf98fda41b64df00141b9df8c16003d'; //APP的ID
    var SUB_ID = '965266509@qq.com'; //子账户ID

//计算签名和头
    console.log("---+++");
    var sign = function(){
        var now = new Date();
        var dt = ''; var a = null;
        a = (now.getFullYear()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getMonth() + 1); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getDate()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getHours()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getMinutes()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getSeconds()); a < 10 ? dt += '0' + a : dt += a;

        var sign = md5().update(ACCOUNT_ID + ACCOUNT_TOKEN + dt).digest('hex').toUpperCase();
        var header = new Buffer(ACCOUNT_ID + ':' + dt).toString('base64');

        return {sign: sign, header: header};
    };
    ajax.ajax({
        type:"POST",
        url:"https://app.cloopen.com:8883/2013-03-22/Accounts/0000000041b645530141b9d1b56c0054/SubAccounts?sig="+sign.sign,
        data:{
            appId:"aaf98fda41b64df00141b9df8c16003d",
            friendlyName:"965266509@qq.com"
        },
        headers: {
            'Authorization': sign.header,
            'Content-Type': 'application/xml;charset=utf-8',
            'Accept': 'application/xml'
        },
        success:function(serverData){
            console.log("---");
            console.log(serverData);
            response.write(JSON.stringify({
                "str":serverData
            }));
            response.end();
        },
        error:function(error){
            console.log("---+++=="+error);
        }
    });
/*    var https = require('https');
    var md5 = function(){return require('crypto').createHash('md5')};

    var ACCOUNT_ID = '0000000041b645530141b9d1b56c0054'; //账户ID
    var ACCOUNT_TOKEN = 'a84c27dd1c3646e280ee7b9cdd4041f8'; //账户TOKEN
    var APP_ID = 'aaf98fda41b64df00141b9df8c16003d'; //APP的ID
    var SUB_ID = '965266509@qq.com'; //子账户ID

//计算签名和头
    var get_sign = function(){
        var now = new Date();
        var dt = ''; var a = null;
        a = (now.getFullYear()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getMonth() + 1); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getDate()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getHours()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getMinutes()); a < 10 ? dt += '0' + a : dt += a;
        a = (now.getSeconds()); a < 10 ? dt += '0' + a : dt += a;

        var sign = md5().update(ACCOUNT_ID + ACCOUNT_TOKEN + dt).digest('hex').toUpperCase();
        var header = new Buffer(ACCOUNT_ID + ':' + dt).toString('base64');

        return {sign: sign, header: header};
    }

//发送信息
    var send_msg = function(to, msg){
        var sign = get_sign();
        var opt = {
            hostname: 'app.cloopen.com',
            port: 8883,
            path: '/2013-03-22/Accounts/%s/SMS/Messages'.replace('%s', ACCOUNT_ID) + '?sig=' + sign.sign,
            method: 'POST',
            headers: {
                'Authorization': sign.header,
                'Content-Type': 'application/xml;charset=utf-8',
                'Accept': 'application/xml'
            }
        }

        var raw = '<?xml version="1.0" encoding="utf-8"?>'
            + '<SMSMessage>'
            +   '<appId>%(app_id)s</appId>'
            +   '<to>%(to)s</to>'
            +   '<body>%(msg)s</body>'
            +   '<msgType>0</msgType>'
            +   '<subAccountSid>%(sub_id)s</subAccountSid>'
            + '</SMSMessage>';
        raw = raw.replace('%(app_id)s', APP_ID).replace('%(sub_id)s', SUB_ID);
        raw = raw.replace('%(to)s', to).replace('%(msg)s', msg);

        return {opt: opt, body: raw};
    }


//创建子账户
    var create_sub = function(name){
        var sign = get_sign();
        var opt = {
            hostname: 'app.cloopen.com',
            port: 8883,
            path: '/2013-03-22/Accounts/%s/SubAccounts'.replace('%s', ACCOUNT_ID) + '?sig=' + sign.sign,
            method: 'POST',
            headers: {
                'Authorization': sign.header,
                'Content-Type': 'application/xml;charset=utf-8',
                'Accept': 'application/xml'
            }
        }

        var raw = '<?xml version="1.0" encoding="utf-8"?>'
            + '<SubAccount>'
            +   '<appId>%(app_id)s</appId>'
            +   '<friendlyName>%(name)s</friendlyName>'
            +   '<accountSid>%(account_id)s</accountSid>'
            + '</SubAccount>';
        raw = raw.replace('%(app_id)s', APP_ID).replace('%(accoun_id)s', ACCOUNT_ID);
        raw = raw.replace('%(name)s', name);

        return {opt: opt, body: raw};
    }


//发出请求
    var request = function(obj){
        var req = https.request(obj.opt, function(res){
            var buffer = '';
            res.on('data', function(chunk){
                buffer += chunk;
            });

            res.on('end', function(){
                console.log(res.statusCode);
                console.log(buffer);
            });
        });

        if (obj.opt.method == 'POST') {
            req.write(obj.body || '');
        }

        req.end();
    }



    if (require.main === module) {
        var obj = send_msg('15210721344', '中文');
        var obj = create_sub('965266509@qq.com');
        request(obj);
    }
    var obj = send_msg('15210721344', '中文');
    var obj = create_sub('965266509@qq.com');
    request(obj);*/
}

module.exports = accountManage;