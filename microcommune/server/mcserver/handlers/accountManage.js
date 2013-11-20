var serverSetting = root.globaldata.serverSetting;
var accountManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require("./../lib/ajax.js");
var sms = require("./../lib/SMS.js");
var sha1 = require("./../tools/sha1.js");
//sms.createsub("coolspan@sina.cn");此子账户已创建
var sms_power = false;
//sms.sendMsg("15210721344","qiaoxiaosong",function(data){console.log(data+"--");});

/***************************************
 *     URL：/api2/account/verifyphone
 ***************************************/
accountManage.verifyphone = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var usage = data.usage;
    var time = new Date().getTime().toString();
    var account = {
        phone: phone,
        code: time.substr(time.length - 6),
        status: "init",
        time: new Date().getTime(),
        head: "",
        nickName: "用户" + phone,
        mainBusiness: "",
        byPhone: "checked",
        byScan: "checked",
        byScanNearBy: "allowed"
    };
    if (usage == "register") {
        checkPhone(phone);
    } else if (usage == "login") {
        getLoginCode(phone);
    } else {
        response.write(JSON.stringify({
            "提示信息": "数据不完整",
            "失败原因": "数据不完整"
        }));
        response.end();
    }
    function checkPhone(phone) {
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                return;
            } else if (results.length == 0) {
                createAccountNode(account);
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                if (accountData.status == "active") {
                    response.write(JSON.stringify({
                        "提示信息": "手机号验证失败",
                        "失败原因": "手机号已被注册"
                    }));
                    response.end();
                } else {
                    var time = new Date().getTime().toString();

                    var bad = time - parseInt(accountData.time);
                    var code = "";
                    if (bad > 600000 || accountData.code == "none") {
                        console.log("++++--" + accountData.code);
                        accountData.code = time.substr(time.length - 6);
                        accountData.time = new Date().getTime();
                        accountNode.save(function (error) {
                        });
                        code = time.substr(time.length - 6);
                    } else {
                        code = accountData.code;
                    }
                    var message = "微型公社手机验证码：" + code + "，欢迎您使用";
                    console.log(message);
                    if (sms_power == true) {
                        sms.sendMsg(phone, message, function (data) {
                            var smsObj = JSON.parse(data);
                            if (smsObj.statusCode == "000000") {
                                response.write(JSON.stringify({
                                    "提示信息": "手机号验证成功",
                                    "phone": account.phone,
                                    "code": sha1.hex_sha1(code)
                                }));
                                response.end();
                            } else {
                                response.write(JSON.stringify({
                                    "提示信息": "手机号验证失败",
                                    "失败原因": "手机号不正确"
                                }));
                                response.end();
                            }
                        });
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "手机号验证成功",
                            "phone": account.phone,
                            "code": sha1.hex_sha1(code)
                        }));
                        response.end();
                    }
                }
            }
        });
    }

    function getLoginCode(phone) {
        var query = [
            'MATCH (account:Account)',
            'WHERE (account.phone={phone}) AND account.status={status}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone,
            status: "active"
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "验证码发送失败",
                    "失败原因": "手机号未注册"
                }));
                response.end();
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                if (accountData.status == "active") {
                    var time = new Date().getTime().toString();
                    var bad = time - parseInt(accountData.time);
                    var code = "";
                    if (bad > 600000 || accountData.code == "none") {
                        accountData.code = time.substr(time.length - 6);
                        accountData.time = new Date().getTime();
                        accountNode.save(function (error) {
                        });
                        code = time.substr(time.length - 6);
                    } else {
                        code = accountData.code;
                    }
                    console.log("登录验证码--" + phone + "--" + code);
                    var message = "微型公社手机验证码：" + code + "，欢迎您使用";
                    if (sms_power == true) {
                        sms.sendMsg(phone, message, function (data) {
                            var smsObj = JSON.parse(data);
                            if (smsObj.statusCode == "000000") {
                                response.write(JSON.stringify({
                                    "提示信息": "验证码发送成功",
                                    "phone": accountData.phone,
                                    "code": sha1.hex_sha1(code)
                                }));
                                response.end();
                            } else {
                                response.write(JSON.stringify({
                                    "提示信息": "验证码发送失败",
                                    "失败原因": "服务器异常"
                                }));
                                response.end();
                            }
                        });
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "验证码发送成功",
                            "phone": accountData.phone,
                            "code": sha1.hex_sha1(code)
                        }));
                        response.end();
                    }
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "验证码发送失败",
                        "失败原因": "手机号未注册"
                    }));
                    response.end();
                }
            }
        });
    }

    function createAccountNode(account) {
        var query = [
            'CREATE (account:Account{account})',
            'SET account.uid=ID(account)',
            'RETURN account'
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
                console.log(message);
                if (sms_power == true) {
                    sms.sendMsg(phone, message, function (data) {
                        var smsObj = JSON.parse(data);
                        if (smsObj.statusCode == "000000") {
                            response.write(JSON.stringify({
                                "提示信息": "手机号验证成功",
                                "phone": accountNode.data.phone,
                                "code": sha1.hex_sha1(account.code)
                            }));
                            response.end();
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "手机号验证失败",
                                "失败原因": "手机号不正确"
                            }));
                            response.end();
                        }
                    });
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "手机号验证成功",
                        "phone": accountNode.data.phone,
                        "code": sha1.hex_sha1(account.code)
                    }));
                    response.end();
                }
            }

        });
    }
}
/***************************************
 *     URL：/api2/account/verifycode
 ***************************************/
accountManage.verifycode = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var code = data.code;

    checkPhoneCode();
    function checkPhoneCode() {
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "验证失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length > 0) {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                if (accountData.code == code && code != "none") {
                    var time = new Date().getTime();
                    var bad = time - parseInt(accountData.time);
                    if (bad > 600000) {
                        response.write(JSON.stringify({
                            "提示信息": "验证失败",
                            "失败原因": "验证码超时"
                        }));
                        response.end();
                    } else {
                        console.log("验证成功---");
                        accountData.code = "none";
                        accountNode.save(function (error) {
                        });
                        response.write(JSON.stringify({
                            "提示信息": "验证成功",
                            "phone": phone,
                            accessKey: sha1.hex_sha1(phone + code)
                        }));
                        response.end();
                    }
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "验证失败",
                        "失败原因": "验证码不正确"
                    }));
                    response.end();
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "验证失败",
                    "失败原因": "手机号不存在"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/auth
 ***************************************/
accountManage.auth = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var password = data.password;
    checkAccountNode();

    function checkAccountNode() {
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
                response.write(JSON.stringify({
                    "提示信息": "普通鉴权失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "普通鉴权失败",
                    "失败原因": "手机号不存在"
                }));
                response.end();
            } else {
                var accountData = results.pop().account.data;
                if (accountData.status == "init") {
                    response.write(JSON.stringify({
                        "提示信息": "普通鉴权失败",
                        "失败原因": "手机号不存在"
                    }));
                    response.end();
                } else {
                    if (accountData.password == password) {
                        console.log("普通鉴权成功---");
                        var time = phone + new Date().getTime();
                        response.write(JSON.stringify({
                            "提示信息": "普通鉴权成功",
                            "accessKey": sha1.hex_sha1(time)
                        }));
                        response.end();
//                        accountSession[phone] = accountSession[phone] || [];
//                        accountSession[phone][accessKey] = null;
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "普通鉴权失败",
                            "失败原因": "密码不正确"
                        }));
                        response.end();
                    }
                }
            }
        });
    }
}

accountManage.exit = function (data, response) {

}
/***************************************
 *     URL：/api2/account/get
 ***************************************/
accountManage.get = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var target = data.target;
    var query = [
        'MATCH (account:Account)',
        'WHERE account.phone={phone}',
        'RETURN account'
    ].join('\n');
    var params = {
        phone: target
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "获取用户信息失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else if (results.length == 0) {
            response.write(JSON.stringify({
                "提示信息": "获取用户信息失败",
                "失败原因": "用户不存在"
            }));
            response.end();
        } else {
            var accountData = results.pop().account.data;
            var account = {
                phone: accountData.phone,
                nickName: accountData.nickName,
                mainBusiness: accountData.mainBusiness,
                head: accountData.head,
                byPhone: accountData.byPhone
            };
            response.write(JSON.stringify({
                "提示信息": "获取用户信息成功",
                account: account
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/account/modify
 ***************************************/
accountManage.modify = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var accountStr = data.account;
    var account = JSON.parse(accountStr);

    var query = [
        'MATCH (account:Account)',
        'WHERE account.phone={phone}',
        'RETURN account'
    ].join('\n');
    var params = {
        phone: phone
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "修改用户信息失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else if (results.length == 0) {
            response.write(JSON.stringify({
                "提示信息": "修改用户信息失败",
                "失败原因": "数据异常"
            }));
            response.end();
        } else {
            var accountNode = results.pop().account;
            var accountData = accountNode.data;
            if (account.nickName != undefined && account.nickName != null && account.nickName != "") {
                accountData.nickName = account.nickName;
            }
            if (account.mainBusiness != undefined && account.mainBusiness != null && account.mainBusiness != "") {
                accountData.mainBusiness = account.mainBusiness;
            }
            if (account.password != undefined && account.password != null && account.password != "") {
                accountData.password = account.password;
                if (accountData.status == "init") {
                    accountData.status = "active";
                }
            }
            accountNode.save(function (error) {
            });
            response.write(JSON.stringify({
                "提示信息": "修改用户信息成功"
            }));
            response.end();
        }
    });
}

module.exports = accountManage;