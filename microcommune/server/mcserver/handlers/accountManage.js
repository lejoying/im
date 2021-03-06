var serverSetting = root.globaldata.serverSetting;
var accountManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var ajax = require("./../lib/ajax.js");
var sms = require("./../lib/SMS.js");
var sha1 = require("./../tools/sha1.js");
var verifyEmpty = require("./../lib/verifyParams.js");
var push = require("./../lib/push.js");
var RSA = require('../../alipayserver/tools/RSA');
RSA.setMaxDigits(38);
var pbkeyStr0 = RSA.RSAKeyStr("5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841",
    "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841",
    "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
var pbkey0 = RSA.RSAKey(pbkeyStr0);

var pvkeyStr0 = RSA.RSAKeyStr("10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
    "10f540525e6d89c801e5aae681a0a8fa33c437d6c92013b5d4f67fffeac404c1",
    "3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659");
var pvkey0 = RSA.RSAKey(pvkeyStr0);
//sms.createsub("coolspan@sina.cn");此子账户已创建
var sms_power = true;
//sms.sendMsg("15210721344","qiaoxiaosong",function(data){console.log(data+"--");});
var accountID = -1;
var redis = require("redis");
var client = redis.createClient("6379", "115.28.51.197");
client.get("ID", function (err, reply) {
    if (err != null) {
        console.error(err);
        throw "用户ID初始化失败...请查看115.28.51.197服务器";
        return;
    } else {
        if (reply == null) {
            console.warn(reply);
            throw "用户ID初始化失败...请查看115.28.51.197服务器";
            return;
        } else {
            console.log("ID:" + reply + "...init data,from server...115.28.51.197");
            accountID = reply;
        }
    }
});
/***************************************
 *     URL：/api2/account/verifyphone
 ***************************************/
accountManage.verifyphone = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var usage = data.usage;
    var arr = [phone, usage];
    var account;
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        var time = new Date().getTime();
        account = {
            phone: phone,
            code: (time.toString()).substr((time.toString()).length - 6),
            status: "init",
            time: time,
            head: "",
            nickName: "用户" + time,
            mainBusiness: "",
            byPhone: "checked",
            byScan: "checked",
            byScanNearBy: "allowed",
            sex: time / 2 == 0 ? "男" : "女",
            userBackground: "userBackground.jpg"
        };
        if (usage == "register") {
            checkPhone(phone);
        } else if (usage == "login") {
            getLoginCode(phone);
        } else {
            responseFailMessage(response, "手机号验证失败", "数据不完整");
        }
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
                responseFailMessage(response, "手机号验证失败", "数据异常");
                console.error(error);
                return;
            } else if (results.length == 0) {
                createAccountNode(account);
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                if (accountData.status == "active") {
                    responseFailMessage(response, "手机号验证失败", "手机号已被注册");
                } else {
                    sendSMSMessage(accountData, checkCodeTime(accountNode, accountData), "手机号验证", response);
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
                responseFailMessage(response, "验证码发送失败", "数据异常");
                console.error(error);
                return;
            } else if (results.length == 0) {
                responseFailMessage(response, "验证码发送失败", "手机号未注册");
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                if (accountData.status == "active") {
                    var code = checkCodeTime(accountNode, accountData);
                    sendSMSMessage(accountData, code, "验证码发送", response);
                } else {
                    responseFailMessage(response, "验证码发送失败", "手机号未注册");
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
                responseFailMessage(response, "验证码发送失败", "数据异常");
                console.error(error);
                return;
            } else {
                var accountNode = results.pop().account;
                sendSMSMessage(accountNode.data, accountNode.data.code, "手机号验证", response);
            }
        });
    }

    function responseFailMessage(response, prompt, reason) {
        response.write(JSON.stringify({
            "提示信息": prompt,
            "失败原因": reason
        }), function () {
            response.end();
        });
    }

    function checkCodeTime(accountNode, accountData) {
        var time = new Date().getTime();
        var bad = time - parseInt(accountData.time);
        if (bad > 600000 || accountData.code == "none") {
            time = time + "";
            accountData.code = time.substr(time.length - 6);
            accountData.time = new Date().getTime();
            accountNode.save(function (error, node) {
            });
            return time.substr(time.length - 6);
        } else {
            return accountData.code;
        }
    }

    function sendSMSMessage(account, code, promptMessage, response) {
        var message = "微型公社手机验证码：" + code + "，欢迎您使用";
        console.log(message);
        if (sms_power == true) {
            push.smsSend(account.phone, message, function (data) {
                if (JSON.parse(data).information == "notify success") {
                    next();
                } else {
                    responseFailMessage(response, promptMessage + "失败", "手机号不正确");
                }
            });
            /*sms.sendMsg(account.phone, message, function (data) {
             var smsObj = JSON.parse(data);
             if (smsObj.statusCode == "000000") {
             next();
             } else {
             responseFailMessage(response, promptMessage + "失败", "手机号不正确");
             }
             });*/
        } else {
            next();
        }
        function next() {
            response.write(JSON.stringify({
                "提示信息": promptMessage + "成功",
                "phone": account.phone,
                "code": sha1.hex_sha1(code)
            }));
            response.end();
        }
    }
}
/***************************************
 *     URL：/api2/account/verifycode
 ***************************************/
accountManage.verifycode = function (data, response, next) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var code = data.code;
    var arr = [phone, code];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (code == "000000") {
            var accessKey0 = sha1.hex_sha1(phone + "000000");
            next(phone, accessKey0, function (flag) {
                if (flag) {
                    response.write(JSON.stringify({
                        "提示信息": "验证成功",
                        uid: RSA.encryptedString(pvkey0, phone),
                        accessKey: RSA.encryptedString(pvkey0, accessKey0),
                        PbKey: pbkeyStr0
                    }));
                    response.end();
                    console.warn("000000验证码跳过验证");
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "验证失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                }
            });
            return;
        }
        checkPhoneCode(phone, code);
    }
    function checkPhoneCode(phone, code) {
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
                if (accountData.status == "active" || accountData.status == "init") {
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
                            accountNode.save(function (error, node) {
                            });
                            var accessKey0 = sha1.hex_sha1(phone + code);
                            next(phone, accessKey0, function (flag) {
                                if (flag) {
                                    response.write(JSON.stringify({
                                        "提示信息": "验证成功",
                                        uid: RSA.encryptedString(pvkey0, phone),
                                        accessKey: RSA.encryptedString(pvkey0, accessKey0),
                                        PbKey: pbkeyStr0
                                    }));
                                    response.end();
                                } else {
                                    response.write(JSON.stringify({
                                        "提示信息": "验证失败",
                                        "失败原因": "数据异常"
                                    }));
                                    response.end();
                                }
                            });
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
                        "失败原因": "手机号已锁定"
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
accountManage.auth = function (data, response, next) {
    response.asynchronous = 1;
    var phone = data.phone;
    var password = data.password;
    var arr = [phone, password];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        checkAccountNode(phone, password.toLowerCase());
    }

    function checkAccountNode(phone, password) {
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
                    if (accountData.status == "active") {
                        if (accountData.password == password) {
                            console.log("普通鉴权成功---");
                            var accessKey = sha1.hex_sha1(phone + new Date().getTime());
                            console.log("accessKey:---" + accessKey);
                            next(phone, accessKey, function (flag) {
                                if (flag) {
                                    response.write(JSON.stringify({
                                        "提示信息": "普通鉴权成功",
                                        "uid": RSA.encryptedString(pvkey0, accountData.phone),
                                        "accessKey": RSA.encryptedString(pvkey0, accessKey),
                                        "PbKey": pbkeyStr0
                                    }));
                                    response.end();
                                } else {
                                    response.write(JSON.stringify({
                                        "提示信息": "普通鉴权失败",
                                        "失败原因": "数据异常"
                                    }));
                                    response.end();
                                }
                            });
//                        accountSession[phone] = accountSession[phone] || [];
//                        accountSession[phone][accessKey] = null;
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "普通鉴权失败",
                                "失败原因": "密码不正确"
                            }));
                            response.end();
                        }
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "普通鉴权失败",
                            "失败原因": "手机号已锁定"
                        }));
                        response.end();
                    }
                }
            }
        });
    }
}
accountManage.exit = function (data, response, next) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    next(phone, accessKey, function (flag) {
        if (flag == true) {
            response.write(JSON.stringify({
                "提示信息": "退出成功"
            }));
            response.end();
        } else if (flag == false) {
            response.write(JSON.stringify({
                "提示信息": "退出失败",
                "失败原因": "AccessKey Invalid"
            }));
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "退出失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });


}
/***************************************
 *     URL：/api2/account/get
 ***************************************/
accountManage.get = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    console.log(phone);
    var target = data.target;
    var arr = [phone, accessKey, target];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        target = JSON.parse(target);
        getAccountNode(target);
    }
    function getAccountNode(target) {
        var query = [
            'MATCH (account:Account)',
            'WHERE account.phone IN {phone}',
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
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "获取用户信息失败",
                    "失败原因": "用户不存在"
                }));
                response.end();
            } else {
                var accounts = [];
                for (var index in results) {
                    var accountData = results[index].account.data;
                    var account = {
                        ID: accountData.ID,
                        phone: accountData.phone,
                        nickName: accountData.nickName,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        sex: accountData.sex,
                        byPhone: accountData.byPhone,
                        userBackground: accountData.userBackground
                    };
                    accounts.push(account);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取用户信息成功",
                    accounts: accounts
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/modify
 ***************************************/
accountManage.modify = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var accountStr = data.account;
    var arr = [phone, accessKey, accountStr];
    var account = {};
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            account = JSON.parse(accountStr);
            if (account.nickName) {
                checkAccountNickName(phone, account);
            } else {
                modifyAccountNode(phone, account);
            }
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "修改用户信息失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
            return;
        }
    }
    function checkAccountNickName(phone, account) {
        var query = [
            'MATCH (account:Account)',
            'WHERE account.nickName={nickName}',
            'RETURN account'
        ].join('\n');
        var params = {
            nickName: account.nickName
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "修改用户信息失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                modifyAccountNode(phone, account);
//                response.write(JSON.stringify({
//                    "提示信息": "修改用户信息失败",
//                    "失败原因": "用户不存在"
//                }));
//                response.end();
            } else {
                var accountData = results.pop().account.data;
                if (accountData.phone == phone) {
                    modifyAccountNode(phone, account);
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "修改用户信息失败",
                        "失败原因": "昵称已存在"
                    }));
                    response.end();
                }
            }
        });
    }

    function modifyAccountNode(phone, account) {
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
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "修改用户信息失败",
                    "失败原因": "用户不存在"
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
                if (account.sex != undefined && account.sex != null && account.sex != "") {
                    accountData.sex = account.sex;
                }
                if (account.userBackground != undefined && account.userBackground != null && account.userBackground != "") {
                    accountData.userBackground = account.userBackground;
                }
                if (account.password != undefined && account.password != null && account.password != "") {
                    accountData.password = account.password;
                    if (accountData.status == "init") {
                        accountData.ID = ++accountID;
                        accountData.status = "active";
                        client.set("ID", accountID, function (err, reply) {
                            if (err != null) {
                                response.write(JSON.stringify({
                                    "提示信息": "修改用户信息失败",
                                    "失败原因": "数据异常"
                                }));
                                response.end();
                                console.error(err);
                                return;
                            }
                        });
                    }
                }
                if (account.head != undefined && account.head != null && account.head != "") {
                    accountData.head = account.head;
                }
                accountNode.save(function (err, node) {
                    if (err) {
                        response.write(JSON.stringify({
                            "提示信息": "修改用户信息失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                        console.error(err);
                        return;
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "修改用户信息成功"
                        }));
                        response.end();
                        push.inform(phone, phone, accessKey, "*", {"提示信息": "成功", event: "userinformationchanged", event_content: {phone: phone}});
                    }
                });
            }
        });
    }
}
//sha1Pwd();
function sha1Pwd() {
    var query = [
        'MATCH (account:Account)',
        'RETURN account'
    ].join('\n');
    db.query(query, {}, function (error, results) {
        if (error) {
            console.error(error);
            return;
        } else {
            for (var i = 0; i < results.length; i++) {
                var accountNode = results[i].account;
                var accountData = accountNode.data;
                var phone = accountData.phone;
                console.error(phone);
                if (accountData.password) {
                    if ((accountData.password).length < 30 && accountData.password != null && accountData.status == "active") {
                        accountData.password = sha1.hex_sha1(accountData.password);
                    }
                    accountNode.save(function (err, node) {

                    });
                }
            }


        }
    });

}
accountManage.oauth6 = function (data, response) {
    response.asynchronous = 1;
    var developID = data.developID;
    var phone = data.phone;
    var accessKey = data.accessKey;
    response.write(JSON.stringify({
        "提示信息": "授权成功",
        accessKey3: sha1.hex_sha1(developID + new Date().getTime())
    }));
    response.end();
}
module.exports = accountManage;