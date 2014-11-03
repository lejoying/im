var serverSetting = root.globaldata.serverSetting;
var relationManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var push = require('../lib/push.js');
var sha1 = require('../tools/sha1.js');
var verifyEmpty = require("./../lib/verifyParams.js");

var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
/***************************************
 *     URL：/api2/relation/addfriend
 ***************************************/
relationManage.addfriend = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var phoneTo = data.phoneto;
    var rid = data.rid;
    var message = data.message;
    var accessKey = data.accessKey;
    var arr = [phone, phoneTo, accessKey];
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        checkAccountBetweenRelation();
    }
    function checkAccountBetweenRelation() {
        var query = [
            'MATCH (account:Account)-[r]-(account1:Account)',
            'WHERE account.phone={phone} AND account1.phone={phoneTo}',
            'DELETE r',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "添加失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else {
                addFriendNode();
            }
        });
    }

    function addFriendNode() {
        var query = [
            'MATCH (account1:Account),(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone={phoneTo} AND account2.byPhone IN ["allowed","checked"]',
            'CREATE UNIQUE account1-[r:FRIEND]->account2',
            'RETURN  account2, r'
        ].join('\n');

        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "添加失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "添加失败",
                    "失败原因": "用户拒绝"
                }));
                response.end();
            } else {
                var pop = results.pop();
                var accountData = pop.account2.data;
                if (accountData.byPhone == "allowed") {
                    var rNode = pop.r;
                    var rData = rNode.data;
                    rData.friendStatus = "success";
                    rData.eid = eid;
//                    rData.message = message;
//                    rData.rid = rid;
                    rNode.save(function (error, node) {
                    });
                    if (rid != "none" && rid) {
                        addAccountCircleNode(rid, phoneTo);
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "添加成功"
                        }));
                        response.end();
                        var event0 = JSON.stringify({
                            sendType: "event",
                            contentType: "relation_addfriend",
                            content: JSON.stringify({
                                type: "relation_addfriend",
                                phone: phone,
                                phoneTo: phoneTo,
                                eid: eid,
                                time: time,
                                status: "waiting",
                                content: message || ""
                            })
                        });
                        client.rpush(phone, event0, function (err, reply) {
                            if (err) {
                                console.error("保存Event失败");
                            } else {
                                console.log("保存Event成功");
                            }
                        });
                        push.inform(phone, phone, accessKey, "*", event);
                        client.rpush(phoneTo, event0, function (err, reply) {
                            if (err) {
                                console.error("保存Event失败");
                            } else {
                                console.log("保存Event成功");
                            }
                        });
                        push.inform(phone, phoneTo, accessKey, "*", event);

                        var event = JSON.stringify({
                            sendType: "event",
                            contentType: "relation_friendaccept",
                            content: JSON.stringify({
                                type: "relation_friendaccept",
                                phone: phone,
                                phoneTo: phoneTo,
                                eid: eid,
                                time: time,
                                status: "success",
                                content: message || ""
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
                        client.rpush(phoneTo, event, function (err, reply) {
                            if (err) {
                                console.error("保存Event失败");
                            } else {
                                console.log("保存Event成功");
                            }
                        });
                        //{"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}}
                        push.inform(phone, phoneTo, accessKey, "*", event);
                    }
                } else {
                    //checked
                    var rNode = pop.r;
                    var rData = rNode.data;
                    rData.friendStatus = "init";
                    rData.message = message;
                    rData.rid = rid;
                    rData.eid = eid;
                    rNode.save(function (error, node) {
                    });
                    response.write(JSON.stringify({
                        "提示信息": "发送请求成功"
                    }));
                    response.end();
                    console.log("发送请求FRIEND成功---");
                    var event0 = JSON.stringify({
                        sendType: "event",
                        contentType: "relation_addfriend",
                        content: JSON.stringify({
                            type: "relation_addfriend",
                            phone: phone,
                            time: time,
                            phoneTo: phoneTo,
                            eid: eid,
                            status: "waiting",
                            content: message || ""
                        })
                    });
                    //{"提示信息": "成功", event: "newfriend", event_content: {phone: phone}}
                    client.rpush(phone, event0, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phone, accessKey, "*", event0);
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "relation_newfriend",
                        content: JSON.stringify({
                            type: "relation_newfriend",
                            phone: phone,
                            phoneTo: phoneTo,
                            eid: eid,
                            time: time,
                            status: "waiting",
                            content: message || ""
                        })
                    });
                    //{"提示信息": "成功", event: "newfriend", event_content: {phone: phone}}
                    client.rpush(phoneTo, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phoneTo, accessKey, "*", event);
                }
            }
        });
    }

    function addAccountCircleNode(rid, phoneTo) {
        var query = [
            'MATCH (account:Account),(circle:Circle)',
            'WHERE account.phone={phoneTo} AND circle.rid={rid}',
            'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
            'RETURN  r'
        ].join('\n');

        var params = {
            rid: parseInt(rid),
            phoneTo: phoneTo
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
                response.write(JSON.stringify({
                    "提示信息": "添加成功"
                }));
                response.end();
                var event0 = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_addfriend",
                    content: JSON.stringify({
                        type: "relation_addfriend",
                        phone: phone,
                        time: time,
                        phoneTo: phoneTo,
                        eid: eid,
                        status: "waiting",
                        content: message || ""
                    })
                });
                //{"提示信息": "成功", event: "newfriend", event_content: {phone: phone}}
                client.rpush(phone, event0, function (err, reply) {
                    if (err) {
                        console.error("保存Event失败");
                    } else {
                        console.log("保存Event成功");
                    }
                });
                push.inform(phone, phone, accessKey, "*", event0);
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_friendaccept",
                    content: JSON.stringify({
                        type: "relation_friendaccept",
                        phone: phone,
                        phoneTo: phoneTo,
                        eid: eid,
                        time: new Date().getTime(),
                        status: "success",
                        content: message || ""
                    })
                });
                client.rpush(phoneTo, event, function (err, reply) {
                    if (err) {
                        console.error("保存Event失败");
                    } else {
                        console.log("保存Event成功");
                    }
                });
                //{"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}}
                push.inform(phone, phoneTo, accessKey, "*", event);
            }
        });
    }
}
/***************************************
 *     URL：/api2/relation/deletefriend
 ***************************************/
relationManage.deletefriend = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneToStr = data.phoneto;
    var phoneTo = [];
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    var arr = [phone, phoneToStr, accessKey];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            phoneTo = JSON.parse(phoneToStr);
            modifyAccountBetweenRelation(phone, phoneTo);
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "删除失败",
                "失败原因": "参数格式错误"
            }));
            response.end();
            console.error(e);
            return;
        }
    }
    function modifyAccountBetweenRelation(phone, phoneTo) {
        console.log(phoneTo);
        var query = [
            'MATCH (account1:Account)-[r:FRIEND]-(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone IN {phoneTo}',
            'RETURN account1,r,account2'
        ].join('\n');
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({

                    "提示信息": "删除失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error + "modifyAccountBetweenRelation");
                return;
            } else if (results.length > 0) {
                for (var index in results) {
                    var rNode = results[index].r;
                    var account1Data = results[index].account1.data;
                    var account2Data = results[index].account2.data;
                    var rData = rNode.data;
                    if (rData.friendStatus == "delete" && rData.phone != phone) {
                        deleteAccountToAccountRelationNode(account1Data.phone, account2Data.phone);
                    } else {
                        rData.friendStatus = "delete";
                        rData.phone = phone;
                        rNode.save(function (error, node) {
                        });
                        deleteCircleAccountRelaNode(account1Data.phone, account2Data.phone);
                    }
                }
            } else {
//                console.log("数据异常0" + "---" + results.length);
//                console.log(phoneTo[0] == 121);
                response.write(JSON.stringify({
                    "提示信息": "删除失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            }
        });
    }

    function deleteAccountToAccountRelationNode(phone, phoneTo) {
        var query = [
            'MATCH (account1:Account)-[r:FRIEND]-(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone={phoneTo}',
            'DELETE r',
            'RETURN account1'
        ].join('\n');
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "删除失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error + "deleteAccountToAccountRelationNode");
                return;
            } else {
                deleteCircleAccountRelaNode(phone, phoneTo);
            }
        });
    }

    function deleteCircleAccountRelaNode(phone, phoneTo) {
        var query = [
            'MATCH (account:Account)-[r:HAS_CIRCLE]->(circle:Circle)-[r1:HAS_FRIEND]->(account1:Account)',
            'WHERE account.phone={phone} AND account1.phone={phoneTo}',
            'DELETE r1',
            'RETURN account'
        ].join('\n');
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "删除失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error + "deleteCircleAccountRelaNode");
                return;
            } else {
                response.write(JSON.stringify({
                    "提示信息": "删除成功"
                }));
                response.end();
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_deletefriend",
                    content: JSON.stringify({
                        type: "relation_deletefriend",
                        phone: phone,
                        phoneTo: phoneTo,
                        eid: eid,
                        time: new Date().getTime(),
                        status: "success",
                        content: ""
                    })
                });
                client.rpush(phoneTo, event, function (err, reply) {
                    if (err) {
                        console.error("保存Event失败");
                    } else {
                        console.log("保存Event成功");
                    }
                });
                //{"提示信息": "成功", event: "friendstatuschanged", event_content: {phone: phone, operation: "delete"}}
                push.inform(phone, phoneTo, accessKey, "*", event);
            }
        });
    }
}

/***************************************
 *     URL：/api2/relation/blacklist
 ***************************************/
relationManage.blacklist = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneToStr = data.phoneto;
    var operation = data.operation;
    var phoneTo = [];
    var arr = [phoneToStr];
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            phoneTo = JSON.parse(phoneToStr);
            addFriendToMyBlackList(phone, phoneTo);
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "更新黑名单失败",
                "失败原因": "参数格式错误"
            }));
            response.end();
            console.error(e);
            return;
        }
    }
    function addFriendToMyBlackList(phone, phoneTo) {
        var query = [
            "MATCH (account:Account)",
            "WHERE account.phone={phone}",
            "RETURN account"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "更新黑名单失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "更新黑名单失败",
                    "失败原因": "用户不存在"
                }));
                response.end();
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                if (accountData.blacklist) {
                    if (operation) {
                        var blackListObj = JSON.parse(accountData.blacklist);
                        for (var index in phoneTo) {
                            var key = phoneTo[index];
                            if (key != null && key != "") {
                                blackListObj.push(key);
                            }
                        }
                    } else {
                        var phoneToMap;
                        for (var index in phoneTo) {
                            phoneToMap[index] = "in";
                        }
                        var list = [];
                        var blackListObj = JSON.parse(accountData.blacklist);
                        for (var index in blackListObj) {
                            var key = blackListObj[index];
                            if (!phoneToMap[key]) {
                                list.push(key);
                            }
                        }
                        blackListObj = list;
                    }
                    accountData.blacklist = JSON.stringify(blackListObj);
                } else {
                    if (operation) {
                        accountData.blacklist = phoneToStr;
                    }
                }
                accountNode.save(function (error, node) {
                    if (error) {
                        response.write(JSON.stringify({
                            "提示信息": "更新黑名单失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                        console.log(error);
                        return;
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "更新黑名单成功"
                        }));
                        response.end();
                    }
                });
            }
        });
    }
}
/***************************************
 *     URL：/api2/relation/getfriends
 ***************************************/
relationManage.getfriends = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var query = [
        'MATCH (account1:Account)-[r:FRIEND]-(account2:Account)',
        'WHERE account1.phone={phone}',
        'RETURN account2'
    ].join('\n');
    var params = {
        phone: phone
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
            return;
        } else {
            console.log("获取好友成功---");
            var friends = [];
            var i = 0;
            for (var index in results) {
                i++;
                var it = results[index].account2.data;
                friends.push(it);
                if (i == results.length) {
                    response.write(JSON.stringify({
                        "提示信息": "获取好友成功",
                        "friends": friends
                    }));
                    response.end();
                }
            }
        }
    });
}
/***************************************
 *     URL：/api2/relation/getcirclesandfriends
 ***************************************/
relationManage.getcirclesandfriends = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var circleOrder = {};
    var arr = [phone, accessKey];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getCirclesNode(phone);
    }
    function getCirclesNode(phone) {
        var query = [
            'MATCH (account:Account)-[HAS_CIRCLE]->(circle:Circle)',
            'WHERE account.phone={phone}',
            'RETURN circle',
            'ORDER BY circle.rid ASC'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取密友圈失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error + "-");
                return;
            } else if (results.length > 0) {
                var circles = {};

                for (var index in results) {
                    var circleData = results[index].circle.data;
//                    console.log(index)
                    circleOrder[circleData.rid + "order"] = index;
//                    console.log(index);
                    circles[circleData.rid] = circleData;
                }
                getAccountsNode(circles, phone);
            } else {
                getAccountsNode(null, phone);
            }
        });
    }

    function getAccountsNode(circles, phone) {
        var query = [
            'MATCH (account:Account)-[r:FRIEND]-(account1:Account)',
            'WHERE account.phone={phone} AND r.friendStatus IN ["success","delete"]',//1,2,3  r不 等于phone
            'RETURN r, account1'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取密友圈失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error + "--");
                return;
            } else if (results.length > 0) {
                var accounts = {};
                for (var index in results) {
                    var rData = results[index].r.data;
                    if (rData.friendStatus == "delete" && rData.phone == phone) {
                        continue;
                    } else {
                        var accountData = results[index].account1.data;
                        var account = {
                            ID: accountData.ID,
                            sex: accountData.sex,
                            age: accountData.age,
                            phone: accountData.phone,
                            mainBusiness: accountData.mainBusiness,
                            head: accountData.head,
                            byPhone: accountData.byPhone,
                            nickName: accountData.nickName,
                            userBackground: accountData.userBackground,
                            friendStatus: rData.friendStatus
                        };

                        if (rData.alias != null) {
                            var alias = JSON.parse(rData.alias);
                            if (alias[account.phone] != null) {
                                account.alias = alias[account.phone];
                            }
                        }
                        accounts[accountData.phone] = account;
                    }
                }
                getCircleFriendsNode(accounts, circles, phone);
            } else {
                getCircleFriendsNode(null, circles, phone);
            }
        });
    }

    function getCircleFriendsNode(accounts, circles, phone) {
        var query = [
            'MATCH (account1:Account)-[r1:HAS_CIRCLE]->(circle:Circle)-[r2:HAS_FRIEND]->(account2:Account)',
            'WHERE account1.phone={phone}',
            'RETURN circle, account2'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "获取密友圈失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(error + "--");
                    return;
                } else if (results.length > 0) {
                    var circles2 = [];
                    var arr = {};
                    var accounts_if = {};
                    for (var index in results) {
                        var circleData = results[index].circle.data;
                        var accountData = results[index].account2.data;
//                        console.error(circleData.rid + "----" + accountData.phone);
                        if (arr[circleData.rid] == null) {
                            var accounts2 = [];
                            accounts2.push(accounts[accountData.phone]);
                            accounts_if[accountData.phone] = "join";
//                            delete accounts_bak[accountData.phone];
                            circleData.accounts = accounts2;
                            arr[circleData.rid] = circleData;
//                            circles2.push(circleData);
                            circles2[circleOrder[circleData.rid + "order"]] = circleData;
                            delete circles[circleData.rid];
                        } else {
                            arr[circleData.rid].accounts.push(accounts[accountData.phone]);
                            accounts_if[accountData.phone] = "join";
//                            delete accounts_bak[accountData.phone];
                        }
                    }
                    if (JSON.stringify(circles) != "{}") {
                        for (var index in circles) {
                            var it = circles[index];
                            it.accounts = [];
//                            circles2.push(it);
                            circles2[circleOrder[it.rid + "order"]] = it;
//                            console.log(circleOrder[it.rid + "order"])
                        }
                    }
                    var circle = {
                        name: "默认分组"
                    };
                    var accounts2 = [];
                    for (var index in accounts) {
                        if (!accounts_if[accounts[index].phone]) {
                            accounts2.push(accounts[index]);
                        }
                    }
                    /*if (JSON.stringify(accounts) != "{}") {
                     for (var index in accounts) {
                     var it = accounts[index];
                     accounts2.push(it);
                     }
                     }*/
                    circle.accounts = accounts2;
                    circles2.push(circle);
                    response.write(JSON.stringify({
                        "提示信息": "获取密友圈成功",
                        circles: circles2
                    }));
                    response.end();
                } else {
                    var circles2 = [];
                    if (circles != null) {
                        for (var index in circles) {
                            var it = circles[index];
                            it.accounts = [];
//                            circles2.push(it);
                            circles2[circleOrder[it.rid + "order"]] = it;
                        }
                    }
                    var accounts2 = [];
                    if (accounts != null) {
                        for (var index in accounts) {
                            var it = accounts[index];
                            accounts2.push(it);
                        }
                    }
                    var circle = {
                        name: "默认分组",
                        accounts: accounts2
                    };
                    circles2.push(circle);
                    response.write(JSON.stringify({
                        "提示信息": "获取密友圈成功",
                        circles: circles2
                    }));
                    response.end();
                }
            }
        )
        ;
    }
}

/***************************************
 *     URL：/api2/relation/addfriendagree
 ***************************************/
relationManage.addfriendagree = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var phoneAsk = data.phoneask;
    var rid = data.rid;
    var status = data.status;
    var accessKey = data.accessKey;
    var eid = data.eid;
//    agreeAddFriendNode(phone, phoneAsk, rid);
    var arr = [phone, accessKey, phoneAsk, status];
    var time = new Date().getTime();
    var eid = phone + "" + time;
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (status == "true") {
            agreeAddFriendNode(phone, phoneAsk, rid);
        } else {
            refuseAddFriend(phone, phoneAsk, rid);
        }
    }
    function agreeAddFriendNode(phone, phoneAsk, rid) {
        modifyStatusNode(phone, phoneAsk, rid);
        function modifyStatusNode(phone, phoneAsk, rid) {
            var query = [
                'MATCH (account1:Account)-[r:FRIEND]->(account2:Account)',
                'WHERE account2.phone={phone} AND account1.phone={phoneAsk}',
                'RETURN r'
            ].join('\n');
            var params = {
                phone: phone,
                phoneAsk: phoneAsk
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "添加失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "添加失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    var rNode = results.pop().r;
                    var rData = rNode.data;
                    var ridAsk = rData.rid;
                    rData.friendStatus = "success";
                    rNode.save(function (error, node) {
                    });
                    if (rid != null && rid != undefined && rid != "" && rid != "undefined") {
                        addCircleAccountRelation(rid, phoneAsk, ridAsk, rData.eid);
                    } else {
                        if (ridAsk != null && ridAsk != undefined && ridAsk != "" && ridAsk != "undefined") {
                            addAskCircleAccountRelation(phone, ridAsk, rData.eid);
                        } else {
                            console.log("添加好友成功");
                            response.write(JSON.stringify({
                                "提示信息": "添加成功"
                            }));
                            response.end();
                            var event = JSON.stringify({
                                sendType: "event",
                                contentType: "relation_friendaccept",
                                content: JSON.stringify({
                                    type: "relation_friendaccept",
                                    phone: phone,
                                    time: time,
                                    phoneTo: phoneAsk,
                                    eid: rData.eid,
                                    status: "success",
                                    content: ""
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
                            client.rpush(phoneAsk, event, function (err, reply) {
                                if (err) {
                                    console.error("保存Event失败");
                                } else {
                                    console.log("保存Event成功");
                                }
                            });
                            //{"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}}
                            push.inform(phone, phoneAsk, accessKey, "*", event);
                        }
                    }
                }
            });
        }

        function addCircleAccountRelation(rid, phoneAsk, ridAsk, eid) {
            var query = [
                'START circle=node({rid})',
                'MATCH (account:Account)',
                'WHERE account.phone={phone}',
                'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
                'RETURN r'
            ].join('\n');
            var params = {
                rid: parseInt(rid),
                phone: phoneAsk
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "添加失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "添加失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    if (ridAsk != null && ridAsk != undefined && ridAsk != "" && ridAsk != "undefined") {
                        addAskCircleAccountRelation(phone, ridAsk, eid);
                    } else {
                        console.log("添加好友成功");
                        response.write(JSON.stringify({
                            "提示信息": "添加成功"
                        }));
                        response.end();
                        var event = JSON.stringify({
                            sendType: "event",
                            contentType: "relation_friendaccept",
                            content: JSON.stringify({
                                type: "relation_friendaccept",
                                phone: phone,
                                phoneTo: phoneAsk,
                                eid: eid,
                                time: time,
                                status: "success",
                                content: ""
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
                        client.rpush(phoneAsk, event, function (err, reply) {
                            if (err) {
                                console.error("保存Event失败");
                            } else {
                                console.log("保存Event成功");
                            }
                        });
                        //{"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}}
                        push.inform(phone, phoneAsk, accessKey, "*", event);
                    }
                    /*console.log("添加好友成功");
                     response.write(JSON.stringify({
                     "提示信息": "添加成功"
                     }));
                     response.end();*/
                }
            });
        }

        function addAskCircleAccountRelation(phone, ridAsk, eid) {
            var query = [
                'START circle=node({rid})',
                'MATCH (account:Account)',
                'WHERE account.phone={phone}',
                'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
                'RETURN r'
            ].join('\n');
            var params = {
                rid: parseInt(ridAsk),
                phone: phone
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "添加失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "添加失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    console.log("添加好友成功");
                    response.write(JSON.stringify({
                        "提示信息": "添加成功"
                    }));
                    response.end();
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "relation_friendaccept",
                        content: JSON.stringify({
                            type: "relation_friendaccept",
                            phone: phone,
                            phoneTo: phoneAsk,
                            eid: eid,
                            time: new Date().getTime(),
                            status: "success",
                            content: ""
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
                    client.rpush(phoneAsk, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    //{"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}}
                    push.inform(phone, phoneAsk, accessKey, "*", event);
                }
            });
        }
    }

    function refuseAddFriend(phone, phoneAsk, rid) {
        deleteFriend(phone, phoneAsk, rid);
        function deleteFriend(phone, phoneAsk, rid) {
            var query = [
                'MATCH (account1:Account)-[r:FRIEND]->(account2:Account)',
                'WHERE account2.phone={phone} AND account1.phone={phoneAsk}',
                'DELETE r',
                'RETURN r'
            ].join('\n');
            var params = {
                phone: phone,
                phoneAsk: phoneAsk
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "拒绝失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "拒绝失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    deleteCircleAccountRelation(rid, phoneAsk);
                }
            });
        }

        function deleteCircleAccountRelation(rid, phoneAsk) {
            var query = [
                'MATCH (circle:Circle)-[r:HAS_FRIEND]->（account:Account)',
                'WHERE circle.rid={rid} AND account.phone={phoneAsk}',
                'DELETE r',
                'RETURN r'
            ].join('\n');
            var params = {
                rid: rid,
                phoneAsk: phoneAsk
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "拒绝失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.log(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "拒绝失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "拒绝成功"
                    }));
                    response.end();
                }
            });
        }
    }
}

/***************************************
 *     URL：/api2/relation/getaskfriends
 ***************************************/
relationManage.getaskfriends = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var arr = [phone, accessKey];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getAskAccounts(phone);
    }
    function getAskAccounts(phone) {
        var query = [
            'MATCH (account1:Account)-[r:FRIEND]->(account2:Account)',
            'WHERE account2.phone={phone} AND r.friendStatus={status}',
            'RETURN account1, r'
        ].join('\n');
        var params = {
            phone: phone,
            status: "init"
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取好友请求失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else {
                console.log("获取好友请求成功---" + results.length);
                var accounts = [];
                for (var index in results) {
                    var accountData = results[index].account1.data;
                    var rData = results[index].r.data;
                    var account = {
                        uid: accountData.uid,
                        ID: accountData.ID,
                        sex: accountData.sex,
                        age: accountData.age,
                        phone: accountData.phone,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        byPhone: accountData.byPhone,
                        nickName: accountData.nickName,
                        lastLoginTime: accountData.lastlogintime,
                        friendStatus: rData.friendStatus,
                        rid: rData.rid,
                        message: rData.message
                    };
                    accounts.push(account);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取好友请求成功",
                    accounts: accounts
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/relation/modifyalias
 ***************************************/
relationManage.modifyalias = function (data, response) {
    response.asynchronous = 1;
    console.info(data);
    var phone = data.phone;
    var friend = data.friend;
    var friendAlias = data.alias;
    var arr;
    if (friendAlias == "") {
        arr = [phone, friend];
    } else {
        arr = [phone, friend, friendAlias];
    }
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        modifyAlias();
    }
    function modifyAlias() {
        var query = [
            'MATCH (account1:Account)-[r:FRIEND]-(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone={friend}',
            'RETURN r'
        ].join('\n');
        var params = {
            phone: phone,
            friend: friend
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "修改备注失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error + "modifyAlias");
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "修改备注失败",
                    "失败原因": "好友不存在"
                }));
                response.end();
            } else {
                var rNode = results.pop().r;
                var rData = rNode.data;
                var alias = {};
                if (rData.alias != null) {
                    alias = JSON.parse(rData.alias);
                }
                if (friendAlias == "") {
                    if (alias[friend] != null) {
                        delete alias[friend];
                    }
                } else {
                    alias[friend] = friendAlias;
                }
                rData.alias = JSON.stringify(alias);
                rNode.save(function (error, node) {
                    if (error) {
                        console.error(error + "save");
                    }
                });
                response.write(JSON.stringify({
                    "提示信息": "修改备注成功"
                }));
                response.end();
            }
        });
    }
}
/*************************************************************
 * * * * * * * * * * * * New Api * * * * * * * * * * * * * * *
 *************************************************************/
relationManage.intimatefriends = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var accessKey = data.accessKey;
    var circleOrder = {};
    var arr = [phone];
    var defaultCircleData = {
        rid: 8888888,
        name: "默认分组",
        friends: []
    }
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getCirclesNode(phone);
    }
    function getCirclesNode(phone) {
        var query = [
            'MATCH (account:Account)-[HAS_CIRCLE]->(circle:Circle)',
            'WHERE account.phone={phone}',
            'RETURN account,circle',
            'ORDER BY circle.rid ASC'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            var circles = [];
            var circlesMap = {};

            circlesMap[defaultCircleData.rid] = defaultCircleData;
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取密友圈失败",
                    "失败原因": "数据异常"
                }), response);
                response.end();
                console.log(error + "-getCirclesNode");
                return;
            } else if (results.length > 0) {
                var accountNode;
                var accountData;
                var tempCircles = [];
                for (var index in results) {
                    if (index == 0) {
                        accountNode = results[index].account;
                    }
                    var circleData = results[index].circle.data;
                    circleData.friends = [];
                    circlesMap[circleData.rid] = circleData;
                    tempCircles.push(circleData.rid);
                    circleOrder[circleData.rid] = index;
                }
                accountData = accountNode.data;
                var circlesOrdering = null;
                if (accountData.circlesOrderString) {
                    try {
                        circlesOrdering = JSON.parse(accountData.circlesOrderString);
                    } catch (e) {
                        circlesOrdering = null
                    }
                }
                var newCirclesOrdering;
                if (circlesOrdering == null) {
                    newCirclesOrdering = tempCircles;
                    newCirclesOrdering.push(defaultCircleData.rid);
                    accountData.circlesOrderString = JSON.stringify(newCirclesOrdering);
                    accountNode.save(function (err, node) {
                        console.log("重置分组顺序数据成功");
                    });
                } else {
                    newCirclesOrdering = [];
                    var isDataConsistentcy = true;
                    var isAddDefaultCircle = false;
                    for (var index in circlesOrdering) {
                        var circleRid = circlesOrdering[index];
                        if (circleRid == defaultCircleData.rid) {
                            if (!isAddDefaultCircle) {
                                newCirclesOrdering.push(parseInt(circleRid));
                                isAddDefaultCircle = true;
                            }
                            continue;
                        }
                        if (circleOrder[circleRid]) {
                            newCirclesOrdering.push(parseInt(circleRid));
                            circleOrder[circleRid] = "delete";
                        } else {
                            isDataConsistentcy = false;
                        }
                    }
                    for (var index in circleOrder) {
                        if (isDataConsistentcy && index != defaultCircleData.rid && circleOrder[index] != "delete") {
                            isDataConsistentcy = false;
                        }
                        if (circleOrder[index] != "delete") {
                            newCirclesOrdering.push(parseInt(index));
                        }
                    }
                    if (!isDataConsistentcy) {
                        newCirclesOrdering.push(parseInt(defaultCircleData.rid));
                        accountData.circlesOrderString = JSON.stringify(newCirclesOrdering);
                        accountNode.save(function (err, node) {
                            console.log("初始化分组顺序数据成功");
                        });
                    }
                }
                getAccountsNode(newCirclesOrdering, circlesMap, phone);
            } else {
                circles.push(defaultCircleData.rid);
                getAccountsNode(circles, circlesMap, phone);
            }
        });
    }

    function getAccountsNode(circles, circlesMap, phone) {
        var query = [
            'MATCH (account:Account)-[r:FRIEND]-(account1:Account)',
            'WHERE account.phone={phone} AND r.friendStatus IN ["success","delete"] AND r.phone<>{phone}',// AND r.friendStatus IN ["success","delete" ] //1,2,3  r不 等于phone
            'RETURN r, account1'
        ].join('\n');
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取密友圈失败",
                    "失败原因": "数据异常"
                }), response);
                console.log(error + "--getAccountsNode");
                return;
            } else if (results.length > 0) {
                var accounts = {};
                for (var index in results) {
                    var rData = results[index].r.data;
                    if (rData.friendStatus == "delete" && rData.phone == phone) {
                        continue;
                    } else {
                        var accountData = results[index].account1.data;
                        var account = {
                            id: accountData.ID,
                            sex: accountData.sex,
                            age: accountData.age,
                            phone: accountData.phone,
                            mainBusiness: accountData.mainBusiness,
                            head: accountData.head,
//                            byPhone: accountData.byPhone,
                            nickName: accountData.nickName,
                            userBackground: accountData.userBackground,
                            addMessage: rData.message,
                            friendStatus: rData.friendStatus,
                            lastLoginTime: accountData.lastlogintime,
                            alias: "",
                            flag: "none",
//                            accessKey: "",
                            distance: 0,
//                            notReadMessagesCount: 0,
                            createTime: accountData.createTime,
                            longitude: accountData.longitude || 0,
                            latitude: accountData.latitude || 0
                        };
                        if (rData.alias != null) {
                            var alias = JSON.parse(rData.alias);
                            if (alias[account.phone] != null) {
                                account.alias = alias[account.phone];
                            }
                        }
                        accounts[accountData.phone] = account;
                    }
                }
                getCircleFriendsNode(accounts, circles, circlesMap, phone);
            } else {
                getCircleFriendsNode(null, circles, circlesMap, phone);
            }
        });
    }

    function getCircleFriendsNode(accounts, circles, circlesMap, phone) {
        console.log(circles);
        var query = [
            'MATCH (circle:Circle)-[r2:HAS_FRIEND]->(account:Account)',//(account1:Account)-[r1:HAS_CIRCLE]->
            'WHERE circle.rid IN {circles}',//account1.phone={phone}
            'RETURN circle,account'
        ].join('\n');
        var params = {
            circles: circles
        };
        db.query(query, params, function (error, results) {
//                circles.push(defaultCircleData.rid);
                if (error) {
                    ResponseData(JSON.stringify({
                        "提示信息": "获取密友圈失败",
                        "失败原因": "数据异常"
                    }), response);
                    console.log(error + "--getCircleFriendsNode");
                    return;
                } else if (results.length > 0) {
                    var arr = {};
                    var accounts_if = {};
                    for (var index in results) {
                        var circleData = results[index].circle.data;
                        var accountData = results[index].account.data;
//                        console.log(circleData.rid+"---"+accountData.phone);
                        if (arr[circleData.rid] == null) {
                            accounts_if[accountData.phone] = "join";
                            circlesMap[circleData.rid].friends.push(accountData.phone);
                            arr[circleData.rid] = "already";
                        } else {
                            accounts_if[accountData.phone] = "join";
                            circlesMap[circleData.rid].friends.push(accountData.phone);
                        }
                    }
                    for (var index in accounts) {
                        if (!accounts_if[index]) {
                            circlesMap[defaultCircleData.rid].friends.push(index);
                        }
                    }
                    if (!accounts) {
                        accounts = [];
                    }
                    ResponseData(JSON.stringify({
                        "提示信息": "获取密友圈成功",
                        relationship: {
                            circles: circles,
                            circlesMap: circlesMap,
                            friendsMap: accounts
                        }
                    }), response);
                } else {
                    if (accounts != null) {
                        for (var index in accounts) {
                            circlesMap[defaultCircleData.rid].friends.push(index);
                        }
                    } else {
                        accounts = [];
                    }
                    ResponseData(JSON.stringify({
                        "提示信息": "获取密友圈成功",
                        relationship: {
                            circles: circles,
                            circlesMap: circlesMap,
                            friendsMap: accounts
                        }
                    }), response);
                }
            }
        );
    }
}
//   /api2 / relation / modifysequence

relationManage.modifysequence = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var circleSequence = data.sequence;
    var accessKey = data.accessKey;
    var time = new Date().getTime();
    var eid = phone + "" + time;
    modifyAccountSeqence();
    function modifyAccountSeqence() {
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
                ResponseData(JSON.stringify({
                    "提示信息": "修改分组顺序失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改分组顺序失败",
                    "失败原因": "用户不存在"
                }), response);
            } else {
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                accountData.circlesOrderString = circleSequence;
                accountNode.save(function (err, node) {
                    if (error) {
                        console.error(err);
                    }
                });
                ResponseData(JSON.stringify({
                    "提示信息": "修改分组顺序成功"
                }), response);
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "account_dataupdate",
                    content: JSON.stringify({
                        type: "account_dataupdate",
                        phone: phone,
                        time: time,
                        status: "success",
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
    }
}

function ResponseData(responseContent, response) {
    response.writeHead(200, {
        "Content-Type": "application/json; charset=UTF-8",
        "Content-Length": Buffer.byteLength(responseContent, 'utf8')
    });
    response.write(responseContent);
    response.end();
}
module.exports = relationManage;