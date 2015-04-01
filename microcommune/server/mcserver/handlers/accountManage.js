var serverSetting = root.globaldata.serverSetting;
var accountManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var contactDB = new neo4j.GraphDatabase("http://182.92.1.150:7474/");
var ajax = require("./../lib/ajax.js");
var sms = require("./../lib/SMS.js");
var sha1 = require("./../tools/sha1.js");
var verifyEmpty = require("./../lib/verifyParams.js");
var push = require("./../lib/push.js");
var RSA = require('../tools/RSA');
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
var sms_power = false;
//sms.sendMsg("15210721344", "qiaoxiaosong", function (data) {
//    console.log(data + "--");
//});
var accountID = -1;
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
var IDclient = redis.createClient(serverSetting.redisPort, "112.126.71.180");
IDclient.get("ID", function (err, reply) {
    if (err != null) {
        console.error(err + "as");
        throw "用户ID初始化失败...请查看112.126.71.180服务器";
        return;
    } else {
        if (reply == null) {
            console.warn(reply + "a");
            throw "用户ID初始化失败...请查看112.126.71.180服务器";
            return;
        } else {
            console.log("ID:" + reply + "...init data,from server...112.126.71.180");
            accountID = reply;
        }
    }
});
/***************************************
 *     URL：/api2/account/verifyphone
 ***************************************/
accountManage.verifyphone = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var usage = data.usage;
    var arr = [phone, usage];
    var account;
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (usage == "register") {
            checkContactAccount(phone);
        } else if (usage == "login") {
            getLoginCode(phone);
        } else {
            responseFailMessage(response, "手机号验证失败", "数据不完整");
        }
    }

    function checkContactAccount(phone) {
        var query = [
            "MATCH (account:Account)<-[r:HAS_CONTACT]-(other:Account)",
            "WHERE account.phone={phone}",
            "RETURN r"
        ].join("\n");
        var params = {
            phone: phone
        };
        contactDB.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                next(getNickName());
            } else if (results.length > 0) {
                var nickNames = {};
                for (var index in results) {
                    var rData = results[index].r.data;
                    if (nickNames[rData.nickName]) {
                        nickNames[rData.nickName]++;
                    } else {
                        nickNames[rData.nickName] = 1;
                    }
                }
                var index0 = -1;
                var nickName
                for (var index in nickNames) {
                    var i = nickNames[index];
                    if (i > index0) {
                        index0 = i;
                        nickName = index;
                    }
                }
                next(nickName);
            } else {
                next(getNickName());
            }
        });
        function next(nickName) {
            var time = new Date().getTime();
            account = {
                phone: phone,
                code: (time + "").substr((time + "").length - 6),
                status: "init",
                time: time,
                head: getHead(),
                nickName: nickName,
                mainBusiness: "",
                age: time % 40,
                nodeType: "Account",
                byPhone: "checked",
                byScan: "checked",
                byScanNearBy: "allowed",
                sex: time / 2 == 0 ? "男" : "女",
                userBackground: "userBackground.jpg"
            };
            checkPhone(phone);
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
        var message = "验证码：" + code + "，10分钟内有效，请勿泄漏，欢迎您使用【微型社区】";
        console.log(message);
        //next();
        //return;
        if (sms_power == true) {
            if (account.phone.length == 11 && account.phone.substr(0, 1) == "1") {
                push.smsSend(account.phone, message, function (data) {
                    if (JSON.parse(data).information == "notify success") {
                        next();
                    } else {
                        responseFailMessage(response, promptMessage + "失败", "发送失败");//服务器异常
                    }
                });
            } else {
                console.log("不足11位的测试帐号,验证码:" + code);
            }

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
                "code": sha1.hex_sha1(code),
                c: code
            }));
            response.end();
        }
    }
}
function getHead() {
    var time = new Date().getTime();
    var heads = [
        "4a8fe36ad954e4b092846f312f3ff8ab176d0064.png", "719309CD7281C54AE2E671816F1060F74DBE392F.osp", "0BD297A73EB1E1C9F20F05B14E18457926755E49.osp", "2ED3F5E22DCE3505F7C590BCB6BC74932A1736DF.osp", "009A3379FCF1A85F02BBA7B31C806472C19B9D91.osp", "23A48EAC968FB7E2F268D9C2E6934C828739C8BE.osj", "36BE4B035D8114E6C13542DCB9503348D6894174.osp", "119F605941E84A11442962DD5240DC7A17332DAC.osp", "99F0C05088333A0C7654439C3160206BD696F6E8.osj", "73EFEA36C07F5B0C681B81D94DA6F0613D4B9FDF.osj", "63F939E3B845A2340E9BBB0FF0D6C8EEB6AB55D9.osj", "63EEF70757B78876DF704446D09FAE8693BBA6B4.osj", "299242B303D0FBE03B5C82AF7A9637F893CD25A6.osp", "857030E7310E15331C51E94B08ED84983D120D85.osj", "04509474d195a9dcc409323e592d79745d81235b.png", "B4809CB82093D6E87F9B2EDDBE15E7031994C9D3.osj", "E3650FC0F11C99400CAC05859FE833AA7A01C9C2.osp", "C76DF1FDAB83FB9C14FBB081677E390921C072C2.osp", "D3A8C52F32DF01CF0947238DFEA79C3B1AE0F82C.osp", "59BA1A6746F37FC4209F0F2C93DAE7882AA5DAC2.osp"
    ];
    return heads[time % 20];
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
                "提示信息": "退出成功",
                "失败原因": "AccessKey Invalid"
            }));
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "退出成功",
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
    console.log(data);
    var target = data.target;
    var arr = [phone, target];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            target = JSON.parse(target);
            getAccountNode(target);
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "获取用户信息失败",
                "失败原因": "数据格式不正确"
            }), response);
            console.error(e);
        }
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
                ResponseData(JSON.stringify({
                    "提示信息": "获取用户信息失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取用户信息失败",
                    "失败原因": "用户不存在"
                }), response);
            } else {
                var accounts = [];
                for (var index in results) {
                    var accountData = results[index].account.data;
                    var account = {
                        id: accountData.ID,
                        ID: accountData.ID,
                        phone: accountData.phone,
                        nickName: accountData.nickName,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        sex: accountData.sex,
                        age: accountData.age,
                        byPhone: accountData.byPhone,
                        createTime: accountData.createTime,
                        userBackground: accountData.userBackground,
                        lastLoginTime: accountData.lastlogintime,
                        longitude: accountData.longitude,
                        latitude: accountData.latitude
                    };
                    if (account.phone == phone) {
                        account.circlesOrderString = accountData.circlesOrderString || "[]";
                        account.groupsSequenceString = accountData.groupsSequenceString || "[]";
                        try {
                            if (accountData.blacklist) {
                                account.blackList = JSON.parse(accountData.blacklist);
                            } else {
                                account.blackList = [];
                            }
                        } catch (e) {
                            console.error(e);
                            account.blackList = [];
                        }
                        account.commonUsedLocations = accountData.commonUsedLocation ? JSON.parse(accountData.commonUsedLocation) : []
                    }
                    accounts.push(account);
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取用户信息成功",
                    accounts: accounts
                }), response);
            }
        });
    }
}
/***************************************
 *     URL：/api2/account/modify
 ***************************************/
//TODO Lbs data
accountManage.modify = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var accountStr = data.account;
    var oldPassWord = null;//data.oldpassword;
    var arr = [phone, accessKey, accountStr];
    var account = {};
    var time = new Date().getTime();
    var eid = phone + "_" + time;
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
                    if (oldPassWord != null && oldPassWord != undefined && oldPassWord != "") {
                        if (oldPassWord != accountData.password) {
                            response.write(JSON.stringify({
                                "提示信息": "修改用户信息失败",
                                "失败原因": "原密码不正确"
                            }));
                            response.end();
                            return;
                        }
                    }
                    accountData.password = account.password.toLowerCase();
                    if (accountData.status == "init") {
                        accountData.ID = ++accountID;
                        accountData.status = "active";
                        IDclient.set("ID", accountID, function (err, reply) {
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
                        initDefaultGroup(phone);
                    }
                }
                if (account.head != undefined && account.head != null && account.head != "") {
                    accountData.head = account.head;
                }
                if (account.longitude != undefined && account.longitude != null && account.longitude != "") {
                    var time = new Date().getTime();
                    accountData.lastlogintime = time;
                    accountData.longitude = account.longitude;
                }
                if (account.latitude != undefined && account.latitude != null && account.latitude != "") {
                    accountData.latitude = account.latitude;
                }
                if (account.commonUsedLocation != undefined && account.commonUsedLocation != null && account.commonUsedLocation != "") {
                    accountData.commonUsedLocation = account.commonUsedLocation;
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
                        var event = JSON.stringify({
                            sendType: "event",
                            contentType: "account_dataupdate",
                            content: JSON.stringify({
                                type: "account_dataupdate",
                                phone: phone,
                                time: new Date().getTime(),
                                status: "success",
                                content: "",
                                content: "",
                                eid: eid
                            })
                        });
                        client.rpush(phone, event, function (err, reply) {
                            if (err) {
                                console.error("保存Event失败");
                            } else {
                                console.log("保存Event成功");
                            }
                        });
                        push.inform(phone, phone, accessKey, "*", event);
                    }
                });
                if (account.longitude) {
                    longitude = account.longitude;
                    latitude = account.latitude;
                    address = account.address;
                    checkLbsAccount(accountData);
                }
            }
        });
    }

    var longitude;
    var latitude;
    var address;

    function checkLbsAccount(accountData) {
        try {
            ajax.ajax({
                type: "POST",
                url: serverSetting.LBS.DATA_SEARCH,
                data: {
                    key: serverSetting.LBS.KEY,
                    tableid: serverSetting.LBS.ACCOUNTTABLEID,
                    filter: "phone:" + accountData.phone
                }, success: function (info) {
                    var info = JSON.parse(info);
                    if (info.status == 1 && info.count >= 1) {
                        var id = info.datas[0]._id;
                        modifyLbsAccount(id);
                        console.log("success--" + info._id)
                    } else {
                        createLbsAccount(accountData);
                        console.log("check error--" + info.status)
                    }
                }
            });
        } catch (e) {
            console.log(e);
        }
    }

    function createLbsAccount(accountData) {
        try {
            ajax.ajax({
                type: "POST",
                url: serverSetting.LBS.DATA_CREATE,
                data: {
                    key: serverSetting.LBS.KEY,
                    tableid: serverSetting.LBS.ACCOUNTTABLEID,
                    loctype: 1,//2
                    data: JSON.stringify({
                        _name: accountData.nickName,
                        _location: longitude + "," + latitude,
                        _address: address,
                        sex: accountData.sex,
                        mainBusiness: accountData.mainBusiness,
                        lastlogintime: accountData.lastlogintime,
                        phone: accountData.phone,
                        head: accountData.head
                    })
                }, success: function (info) {
                    var info = JSON.parse(info);
                    if (info.status == 1 && info.count >= 1) {
                        var id = info.datas[0]._id;
                        console.log("success--" + info._id)
                    } else {
                        console.log("check error--" + info.status)
                    }
                }
            });
        } catch (e) {
            console.log(e);
            return;
        }
    }

    function modifyLbsAccount(id) {
        try {
            ajax.ajax({
                type: "POST",
                url: serverSetting.LBS.DATA_UPDATA,
                data: {
                    key: serverSetting.LBS.KEY,
                    tableid: serverSetting.LBS.ACCOUNTTABLEID,
                    data: JSON.stringify({
                        _id: id,
                        _location: longitude + "," + latitude,
                        _address: address
                    })
                }, success: function (info) {
                    var info = JSON.parse(info);
                    if (info.status == 1) {
                        console.log("success--" + info._id)
                    } else {
                        console.log("modify error--" + info.status)
                    }
                }
            });
        } catch (e) {
            console.log(e);
        }
    }
}
function initDefaultGroup(phone) {
    var query = [
        'MATCH (account:Account),(group:Group)',
        'WHERE account.phone={phone} AND group.gid={gid}',
        'CREATE UNIQUE group-[r:HAS_MEMBER]->account',
        'RETURN account,group'
    ].join("\n");
    var params = {
        phone: phone,
        gid: 1887
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.error("initDefaultGroup:" + error);
        } else if (results.length == 0) {
            console.log("初始化好友向导群组失败");
        } else {
            console.log("初始化好友向导群组成功");
        }
    });
}

//使数据库中的注册用户数据和高德Lbs云中数据保持一致
//不存在的就创建新的lbs数据，存在就更新使数据一致，如果出现异常数据，删除掉查询出的数据并创建新的lbs数据
//setAllAccountToLbsData();
function setAllAccountToLbsData() {
    var query = [
        "MATCH (account:Account)",
        "WHERE account.status={status}",
        "RETURN account"
    ].join("\n");
    var params = {
        status: "active"
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.error(error);
        } else {
            for (var index in results) {
                var accountData = results[index].account.data;
                checkAccountIsExists(accountData);
            }
        }
    });
}

function checkAccountIsExists(account) {
    ajax.ajax({
        type: "GET",
        url: "http://yuntuapi.amap.com/datamanage/data/list",
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.ACCOUNTTABLEID,
            filter: "phone:" + account.phone
        }, success: function (info) {
            var info = JSON.parse(info);
            if (info.status == 1) {
                //console.log("success--" + info.datas.length)
                if (info.count == 0) {
                    createAccountLbsData(account);
                } else if (info.count == 1) {
                    updateAccountLbsData(account, info.datas[0]._id);
                } else if (info.count > 1) {
                    var ids = "";
                    var pois = info.datas;
                    for (var index in pois) {
                        var poi = pois[index];
                        if (ids == "") {
                            ids = poi._id;
                        } else {
                            ids = ids + "," + poi._id;
                        }
                    }
                    deleteAccountLbsData(account, ids);
                }
            } else {
                console.log(info.info + "--");
            }
        }
    });
}

function createAccountLbsData(account) {
    var addressLocation = (account.longitude ? account.longitude : "104.394729") + "," + (account.latitude ? account.latitude : "31.125698");
    ajax.ajax({
        type: "POST",
        url: serverSetting.LBS.DATA_CREATE,
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.ACCOUNTTABLEID,
            loctype: 1,
            data: JSON.stringify({
                _name: account.nickName,
                _location: addressLocation,
                uid: account.uid,
                phone: account.phone,
                sex: account.sex,
                lastlogintime: account.lastLoginTime,
                status: account.status,
                age: account.age,
                mainBusiness: account.mainBusiness,
                head: account.head
            })
        }, success: function (info) {
            var info = JSON.parse(info);
            if (info.status == 1) {
                console.log("success--" + info._id)
            } else {
                console.error(info.info + "-create");
            }
        }
    });
}
function updateAccountLbsData(account, id) {

    var nickName = account.nickName;
    nickName = nickName.replace(/ /g, "&nbsp;");
    var mainBusiness = account.mainBusiness;
    mainBusiness = mainBusiness.replace(/ /g, "&nbsp;");

    ajax.ajax({
        type: "POST",
        url: serverSetting.LBS.DATA_UPDATA,
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.ACCOUNTTABLEID,
            data: JSON.stringify({
                _id: id,
                _name: nickName,
                uid: account.uid,
                phone: account.phone,
                sex: account.sex,
                lastlogintime: account.lastLoginTime,
                status: account.status,
                age: account.age,
                mainBusiness: mainBusiness,
                head: account.head
            })
        }, success: function (info) {
            try {
                var info = JSON.parse(info);
                if (info.status == 1) {
                    console.log("更新成功" + info.info);
                } else {
                    console.log(info.info + "--" + account.phone);
                }
            } catch (e) {
                console.log(JSON.stringify({
                    _id: id,
                    _name: account.nickName,
                    uid: account.uid,
                    phone: account.phone,
                    sex: account.sex,
                    lastlogintime: account.lastLoginTime,
                    status: account.status,
                    age: account.age,
                    mainBusiness: account.mainBusiness,
                    head: account.head
                }));
            }
        }
    });
}
function deleteAccountLbsData(account, ids) {
    ajax.ajax({
        type: "POST",
        url: serverSetting.LBS.DATA_DELETE,
        data: {
            key: serverSetting.LBS.KEY,
            tableid: serverSetting.LBS.ACCOUNTTABLEID,
            ids: ids
        }, success: function (info) {
            var info = JSON.parse(info);
            if (info.status == 1) {
                console.log("删除成功");
                createAccountLbsData(account);
            } else {
                console.log(info.info + "--" + ids);
            }
        }
    });
}


//sha1Pwd();
function sha1Pwd() {
    var query = [
        'MATCH (account:Group)',
        'RETURN account'
    ].join('\n');
    var arr = [1412751055571, 1412664655571, 1412578255571, 1412491855571, 1412405455571]
    db.query(query, {}, function (error, results) {
        if (error) {
            console.error(error);
            return;
        } else {
            for (var i = 0; i < results.length; i++) {
                var accountNode = results[i].account;
                var accountData = accountNode.data;
                var phone = accountData.gid;
                console.error(phone);
//                if (accountdata.password) {
//                    if ((accountdata.password).length < 30 && accountdata.password != null && accountdata.status == "active") {
//                        accountdata.password = sha1.hex_sha1(accountdata.password);
//                    }
//                    accountnode.save(function (err, node) {
//                    });
//                }

                accountData.createTime = arr[i % 5];
                accountNode.save(function (err, node) {
                });
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

function ResponseData(responseContent, response) {
    response.writeHead(200, {
        "Content-Type": "application/json; charset=UTF-8",
        "Content-Length": Buffer.byteLength(responseContent, 'utf8')
    });
    response.write(responseContent);
    response.end();
}
function getNickName() {
    var nickNames = ["微笑的泪滴", "夏末的回忆。", "随风而逝的、秋", "泪中的、苦涩", "微笑的孤独", "骨子里的淡漠", "依、痛彻心扉", "唯美小王子", "素颜繁华梦",
        "长梦永不醒", "林花谢了春红", "戒不掉、拾念╰", "回不去的单纯", "不羁的蚂蚁", "っ留不住啲都走勒", "点头式、表白", "捂着胃，说心疼", "回眸╰つ依然是你",
        "试徒╰去淡抹", "虚伪释怀伤痛", "烟后很憔悴", "被雨打湿的爱", "抬头唯有微笑", "似风恋雨", "别对我、讲道理", "这个杀手好怕丑", "伤的、唯美", "不带刺的刺猬",
        "流逝·忘却", "痛。自找的", "泪、湿了眼睛", "曼陀罗的香气", "难得明白", "寂寞的季末", "早点说声不爱我", "社会的虚伪", "霸占你的爱", "欧东残月", "黑白式丶回忆",
        "夕阳下的黎明", "温柔的背后", "爱、流逝╮", "为梦而生", "偶是你的傻子", "陌白海棠", "゛花开半夏°", "再次被人伤了....", "爱你才来这里", "封心锁爱",
        "雨后·彩虹", "我一直在笑。", "云卷云舒", "不弃！相爱一世", "最暖的伴", "夕阳之恋", "简单是福", "孤城月影", "喜欢劈腿你就去跳芭蕾", "无力落地旳苍白",
        "画笔下的爱", "╰残存的记忆。", "湿了眼角的泪", "樱花.雨凉", "阳光下的蜡烛", "破裤子缠腿", "各花入各眼", "不得很犀利", "简单的句号", "月无言", "相思、轻放下",
        "醉亦惜红颜", "冷雨洗心", "爱的、对角线", "冬天去卖雪", "飘渺的灵魂", "缘分惹的祸", "雨季、看花落", "凤的泪懂我", "鱼甾空中飞", "碎花裙的性感", "稀释，昔日",
        "哭泣的百合", "So、醉爱", "指尖流过的沙", "浅言、默语", "怀念、你的依赖", "零碎的星晨", "╰花落勿相离", "红尘笑笑寂寥", "孓恋系、殊途", "凉吟う小柔情",
        "这颗草旳英姿╯", "山青花欲燃", "沉默不是无语", "忧馨伤。落殇花", "终已不顾", "回忆比不过现实", "孤芳^赏恋雪", "飞奔的爱情", "繁华的.谢意", "1根有毒的草",
        "胡闹是一种依赖╮", "寂寞星雨", "无声的雨", "空梦残月", "分手的节拍", "错过了……", "凛冻深秋寂", "心碎的折磨", "猎魂骑士", "花、涧泪。", "夏目漱石的草",
        "时光未去人也老", "心伤在你的背影", "薰衣草的誓言", "心痛的感觉", "预定、你的温柔", "谁也不常在", "寂默的背影", "伤心的小雨点", "ぃ要找疼自己的人",
        "你的泪太假", "你打算如何伤我丶", "唯爱绕指柔", "一颗善良的心", "天秤上起舞", "默认，生死苦等。", "渲染致命的暧昧つ", "带着你对我的好", "华丽的舞姬",
        "莫欺少年穷。", "幼稚的野蛮", "花落谁指尖", "旧城故人勿念", "最深的记忆", "虚幻的飘渺", "安夏。沫悲伤", "失落了自己", "鱼群中的海豚", "黑色雨滴", "风花雪雨",
        "智者无敌", "回忆又见喜欢", "ぉ绝伦ゞ独舞", "英雄有泪", "幸福相伴", "夕日那般伤痛", "誓言遇上谎言", "只为你而留", "姿醉、魅瑰", "落泪之前转身", "在路上在一旁", "情已去！心已碎", "伱不 、会懂",
        "现在丶彼此左右", "匿名的宝贝", "浅岛宁夏", "冬天里的白松", "夏至未至", "六翼堕落天使", "提笔画红颜", "似毒的爱", "守望的距离", "风中留影", "孤独的狼",
        "久伴不腻", "为菜吃狂", "妩媚不是妖", "沉默的曹操", "紫星、流雨", "誓言ぃ太无邪", "失眠中的幸福", "花再强也会谢", "失去了笑容。", "卑微de忏悔", "假装、淡定",
        "糖、甜到心痛", "双鱼在哭泣", "落败的始终是距", "爱伤了，一切", "顺时针遗忘", "伴静赏日落", "悲伤式、沉默", "こ薄荷的微光", "愿和你流浪", "爱从零开始",
        "触摸那片纯白", "殇狼之泪", "爱比恨多一些", "别让我后悔", "冬日的温暖", "心暖不过一瞬间", "含情旳笑脸", "这，也是醉了", "耀眼的宇宙", "转身，撕心裂",
        "醉眼乜斜", "高跟鞋の魅惑", "一个人的烟火", "望穿秋水的守候", "跳动的、黑白色", "无尽誓约", "伤、心痛", "幸福请了假", "过去的过去", "腊月里的梅花",
        "奔腾奇迹的梦想", "签个什么呢", "彼此心里的唯一", "忆那一眸温柔", "繁华落尽不静好", "拱土的小猪", "ず缺一分安然", "他笑ゞ我幸福", "眉毛快跟上",
        "放肆的青春ん", "孤单摩天轮", "被未成年引诱。", "ˊ花葬了爱", "格式化性能", "那痛か一定会好", "你滴眼泪会说谎", "哎呦喂，我哒小苹果", "丢了刺的刺猬",
        "其实，无所谓", "べ我有我的小情绪つ", "深之入骨的情丶", "眼泪的海港", "我因花心被判无期", "遥远的幸福何在", "飞出凉山的鹰", "剩下空心要不要", "苍白的拂晓",
        "泪  只为你流", "放不开、你的爱", "从开始到现在", "ぃ雨天ツ阳光", "你笑的好美", "仅有的执着", "倾悦影、安悦心", "在悲伤中走过", "爱得只剩狼狈", "心灵的天空",
        "观月，聆星", "命运般的你", "不给孩找后妈", "梨花落心扉づ", "思维没逻辑、", "光脚丫的雨孩", "烟酒殉、年华", "许给谁的温柔", "谁是我的倾诉者", "对你何止爱",
        "春天的风筝", "未来丶在前方", "0°C的浪漫", "蒙懂丶装给谁", "指尖泪妖绕", "晒着月亮哼着歌", "汗水浸透的青春", "╭夜晚看太阳", "花丶刺痛的伤", "傻笑伴随泪水",
        "蓝魔之泪", "寂寞的夜夜夜"
    ];
    var time = new Date().getTime();
    var number = time % 265;
    return nickNames[number];
    //console.log(nickNames.length);
}
module.exports = accountManage;