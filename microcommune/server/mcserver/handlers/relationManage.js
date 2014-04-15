var serverSetting = root.globaldata.serverSetting;
var relationManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var push = require('../lib/push.js');
var sha1 = require('../tools/sha1.js');
var verifyEmpty = require("./../lib/verifyParams.js");
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
                        push.inform(phone, phoneTo, accessKey, "*", {"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}});
                    }
                } else {
                    //checked
                    var rNode = pop.r;
                    var rData = rNode.data;
                    rData.friendStatus = "init";
                    rData.message = message;
                    rData.rid = rid;
                    rNode.save(function (error, node) {
                    });
                    response.write(JSON.stringify({
                        "提示信息": "发送请求成功"
                    }));
                    response.end();
                    console.log("发送请求FRIEND成功---");
                    push.inform(phone, phoneTo, accessKey, "*", {"提示信息": "成功", event: "newfriend", event_content: {phone: phone}});
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
                push.inform(phone, phoneTo, accessKey, "*", {"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}});
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
                    if (rData.friendStatus == "delete") {
                        deleteAccountToAccountRelationNode(account1Data.phone, account2Data.phone);
                    } else {
                        if (rData.friendStatus == "blacklist") {
                            if (rData.phone == account2Data.phone) {
                                rData.friendStatus = "both";
                                rData.phone = account1Data.phone;
                                rNode.save(function (error, node) {
                                });
                            }
                            deleteCircleAccountRelaNode(account1Data.phone, account2Data.phone);
                        } else {
                            rData.friendStatus = "delete";
                            rData.phone = phone;
                            rNode.save(function (error, node) {
                            });
                            deleteCircleAccountRelaNode(account1Data.phone, account2Data.phone);
                        }
                    }
                }
            } else {
                console.log("数据异常0" + "---" + results.length);
                console.log(phoneTo[0] == 121);
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
    var phoneTo = [];
    var arr = [phone, phoneTo, accessKey];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            phoneTo = JSON.parse(phoneToStr);
            modifyAccountBetweenRelation(phone, phoneTo);
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "添加黑名单失败",
                "失败原因": "参数格式错误"
            }));
            response.end();
            console.log(e);
            return;
        }
    }
    function modifyAccountBetweenRelation(phone, phoneTo) {
        var query = [
            'MATCH (account1:Account)-[r:FRIEND]-(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone IN {phoneTo}',
            'SET r.friendStatus="blacklist",r.phone={phone}',
            'RETURN r'
        ].join('\n');
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "添加黑名单失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                /*var rNode = results.pop().r;
                 var rData = rNode.data;
                 rData.friendStatus = "blacklist";
                 rData.phone = phone;
                 rNode.save(function (error, node) {
                 });*/
                response.write(JSON.stringify({
                    "提示信息": "添加黑名单成功"
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "添加黑名单失败",
                    "失败原因": "数据异常"
                }));
                response.end();
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
            'WHERE account.phone={phone} AND r.friendStatus IN ["success","delete","blacklist","both"]',//1,2,3  r不 等于phone
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
                    if ((rData.friendStatus == "delete" || rData.friendStatus == "both") && rData.phone == phone) {
                        continue;
                    } else {
                        var accountData = results[index].account1.data;
                        var account = {
                            uid: accountData.uid,
                            ID: accountData.ID,
                            sex: accountData.sex,
                            phone: accountData.phone,
                            mainBusiness: accountData.mainBusiness,
                            head: accountData.head,
                            byPhone: accountData.byPhone,
                            nickName: accountData.nickName,
                            userBackground: accountData.userBackground,
                            friendStatus: rData.friendStatus
                        };

                        if (rData.friendStatus == "both") {
                            account.friendStatus = "delete";
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
                        console.error(circleData.rid + "----" + accountData.phone);
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
//    agreeAddFriendNode(phone, phoneAsk, rid);
    var arr = [phone, accessKey, phoneAsk, status];
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
                        addCircleAccountRelation(rid, phoneAsk, ridAsk);
                    } else {
                        if (ridAsk != null && ridAsk != undefined && ridAsk != "" && ridAsk != "undefined") {
                            addAskCircleAccountRelation(phone, ridAsk);
                        } else {
                            console.log("添加好友成功");
                            response.write(JSON.stringify({
                                "提示信息": "添加成功"
                            }));
                            response.end();
                            push.inform(phone, phoneAsk, accessKey, "*", {"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}});
                        }
                    }
                }
            });
        }

        function addCircleAccountRelation(rid, phoneAsk, ridAsk) {
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
                        addAskCircleAccountRelation(phone, ridAsk);
                    } else {
                        console.log("添加好友成功");
                        response.write(JSON.stringify({
                            "提示信息": "添加成功"
                        }));
                        response.end();
                        push.inform(phone, phoneAsk, accessKey, "*", {"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}});
                    }
                    /*console.log("添加好友成功");
                     response.write(JSON.stringify({
                     "提示信息": "添加成功"
                     }));
                     response.end();*/
                }
            });
        }

        function addAskCircleAccountRelation(phone, ridAsk) {
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
                    push.inform(phone, phoneAsk, accessKey, "*", {"提示信息": "成功", event: "friendaccept", event_content: {phone: phone}});
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
                        phone: accountData.phone,
                        mainBusiness: accountData.mainBusiness,
                        head: accountData.head,
                        byPhone: accountData.byPhone,
                        nickName: accountData.nickName,
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

module.exports = relationManage;