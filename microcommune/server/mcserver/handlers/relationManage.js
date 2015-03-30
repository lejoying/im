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
 *     URL：/api2/relation/modifyalias
 ***************************************/
relationManage.modifyalias2 = function (data, response) {
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
relationManage.intimatefriends2 = function (data, response) {
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

relationManage.modifysequence2 = function (data, response) {
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
//TODO
/**
 * *********************************************************************************************************************
 * @type {exports.GraphDatabase}
 */
var contactDB = new neo4j.GraphDatabase("http://182.92.1.150:7474/");
relationManage.updatecontact = function (data, response) {
    response.asynchronous = 1;
    var startTime = new Date().getTime();
    //console.log(data);
    var phone = data.phone;
    var contact = data.contact;
    var arr = [contact];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            contact = JSON.parse(contact);
            checkUserExists(phone);
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "更新通讯录失败",
                "失败原因": "数据异常"
            }), response);
            console.error(e);
        }
    }
    function checkUserExists(phone) {
        var query = [
            "MATCH (account:Account{phone:{phone}})",
            "WITH count(account) AS number",
            "WHERE number = 0",
            "CREATE (account:Account{account2})",
            "SET account.uid=ID(account)",
            "RETURN account"
        ].join("\n");
        var params = {
            phone: phone,
            account2: {
                phone: phone,
                isregist: true
            }
        };
        contactDB.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "更新通讯录失败",
                    "失败原因": "数据异常"
                }), response);
                console.error("checkUserExists:" + error);
            } else {
                var phones = [];
                for (var index in contact) {
                    if (index != phone) {
                        phones.push(index);
                    }
                }
                createContactAccountNode(phones);
            }
        });
    }

    function createContactAccountNode(phones) {
        //console.log(phones);
        var query = [
            "MATCH (account:Account{phone:{phone}})-[HAS_CONTACT]->(account2:Account)",
            "WHERE account2.phone IN {phones}",
            "SET account.isregist={isregist}",
            "RETURN account,account2"
        ].join("\n");
        var params = {
            phone: phone,
            phones: phones,
            isregist: true
        };
        contactDB.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "更新通讯录失败",
                    "失败原因": "数据异常"
                }), response);
                console.error("createContactAccountNode:" + error);
            } else {
                var alreadyExists = {};
                for (var index in results) {
                    var accountData = results[index].account2.data;
                    alreadyExists[accountData.phone] = "in";
                    //console.log(accountData.phone);
                }
                var updateList = [];
                var updateMap = {};
                for (var index in contact) {
                    if (!alreadyExists[index]) {
                        updateList.push(index);
                        updateMap[index] = "in";
                    }
                }
                console.error("通讯录全部:" + phones.length + "个,已存在关系:" + ( phones.length - updateList.length) + "个");
                //console.log(JSON.stringify(updateList));
                if (updateList.length > 0) {
                    createAlreadyExistsAccount(updateMap, updateList);
                } else {
                    checkContactDBFollowTWo();
                    ResponseData(JSON.stringify({
                        "提示信息": "更新通讯录成功"
                    }), response);
                }
            }
        });
    }

    function createAlreadyExistsAccount(updateMap, updateList) {
        var query = [
            "MATCH (account:Account{phone:{phone}}),(account2:Account)",
            "WHERE account2.phone IN {phones}",
            "CREATE UNIQUE account-[r:HAS_CONTACT]->account2",
            "RETURN account2,r"
        ].join("\n");
        var params = {
            phone: phone,
            phones: updateList
        };
        contactDB.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "更新通讯录失败",
                    "失败原因": "数据异常"
                }), response);
                console.error("createAlreadyExistsAccount:" + error);
            } else {
                var alreadyCreateAccount = {};
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    alreadyCreateAccount[account2Data.phone] = "in";
                    var rNode = results[index].r;
                    var rData = rNode.data;
                    console.log(contact[account2Data.phone]);
                    rData.nickName = contact[account2Data.phone].nickName;
                    rData.head = contact[account2Data.phone].head;
                    rNode.save(function (err, node) {
                    });
                }
                var needCreateAccout = [];
                for (var index in updateMap) {
                    if (!alreadyCreateAccount[index]) {
                        needCreateAccout.push(index);
                    }
                }
                createAccountAndRelatiuon(needCreateAccout);
            }
        });
    }

    function createAccountAndRelatiuon(needCreateAccout) {
        console.error("通过通讯录创建的新用户个数:" + needCreateAccout.length);
        for2();

        function for2() {
            var successCount = 0;
            var failedCount = 0;

            for (var index in needCreateAccout) {
                var key = needCreateAccout[index];
                var query = [
                    "MATCH (account:Account{phone:{phone}})",
                    "CREATE UNIQUE account-[r:HAS_CONTACT]->(account2:Account{account2})",
                    "SET account2.uid=ID(account2),r.nickName={nickName},r.head={head}",
                    "RETURN  account2, r"
                ].join("\n");
                var params = {
                    phone: phone,
                    nickName: contact[key].nickName,
                    head: contact[key].head,
                    account2: {
                        phone: key,
                        isregist: false
                    }
                };
                contactDB.query(query, params, function (error, results) {
                    if (error) {
                        console.error(error);
                    } else if (results.length == 0) {
                        successCount++;
                    } else {
                        failedCount++;
                    }//
                });
            }
            ResponseData(JSON.stringify({
                "提示信息": "更新通讯录成功"
            }), response);
            var endTime = new Date().getTime();
            console.log("初始化所有时长：" + (endTime - startTime));
            console.log("创建成功:" + successCount + "个，创建失败:" + failedCount);

            checkContactDBFollowTWo();
        }
    }

    function checkContactDBFollowTWo() {
        console.log("start check follow relation : ContactDB");
        var query = [
            "MATCH (account:Account)-[r:HAS_CONTACT]->(account2:Account)-[r2:HAS_CONTACT]->(account:Account)",
            "WHERE account.phone={phone}",
            "RETURN account2"
        ].join("\n");
        var params = {
            phone: phone
        };
        contactDB.query(query, params, function (error, results) {
            if (error) {
                console.log(error);
            } else if (results.length > 0) {
                var accounts = [];
                var accountsMap = {};
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    accounts.push(account2Data.phone);
                    accountsMap[account2Data.phone] = "in"
                }
                console.log("存在的互粉关系:" + results.length + "个，需要处理：" + accounts.length);
                checkDBFollowTWo(accounts, accountsMap);
            } else {
                console.log("无需要处理的互粉关系.");
            }
        });
    }

    function checkDBFollowTWo(accountsD, accountsMapD) {
        console.log("start check follow relation :DB");
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)-[r2:FOLLOW]->(account:Account)",
            "WHERE account.phone={phone} AND account2.phone IN {phones}",
            "RETURN account2"
        ].join("\n");
        var params = {
            phone: phone,
            phones: accountsD
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.log(error);
            } else if (results.length > 0) {
                var accounts = [];
                var accountsMap = {};
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    accountsMapD[account2Data.phone] = "out"
                }
                for (var index in accountsMapD) {
                    if (accountsMapD[index] == "in") {
                        accounts.push(index);
                        accountsMap[index] = "in";
                    }
                }
                console.log("已经存在的粉丝：" + results.length + ",需要添加的粉丝：" + accounts.length);
                //checkLeftrelation(accounts, accountsMap);
                justTry(accounts, accountsMap);
            } else {
                //checkLeftrelation(accountsD, accountsMapD);
                justTry(accountsD, accountsMapD);
            }
        });
    }

    function justTry(accountsA, accountsMapA) {
        console.log(phone + "---" + accountsA.length);
        var query = [
            "MATCH (account:Account),(account2:Account)",
            "WHERE account.phone = {phoneNumber} AND account2.phone IN {phoneTo}",
            "MERGE account2-[r2:FOLLOW]->account-[r:FOLLOW]->account2",
            "RETURN account,account2,r,r2"
        ].join("\n");
        var params = {
            phoneNumber: phone,
            phoneTo: accountsA
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
            } else {
                console.log("成功：" + results.length);
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    var rNode = results[index].r;
                    var rData = rNode.data;
                    rData.rid = "9999999";
                    if (contact[account2Data.phone]) {
                        rData.alias = contact[account2Data.phone].nickName || "";
                    }
                    rNode.save(function (err, node) {
                    });
                }
            }
        });
    }

    //old code ****************************************************
    function checkLeftrelation(accountsF, accountsMapF) {
        console.log("start check follow relation : 判断向右的单向follow");
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)",
            "WHERE account.phone = {phone} AND account2.phone IN {phoneTo}",
            "RETURN account,account2,r"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: accountsF
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
            } else if (results.length > 0) {
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    accountsMapF[account2Data.phone] = "out";
                    var rNode = results[index].r;
                    var rData = rNode.data;
                    rData.alias = contact[account2Data.phone].nickName || ""
                    rNode.save(function (err, node) {
                    });
                }
                var accounts = [];
                var accountsMap = [];
                for (var index in accountsMapF) {
                    if (accountsMapF[index] == "in") {
                        accounts.push(index);
                        accountsMap[index] = "in";
                    } else {
                        accountsMapF[index] = "in";
                    }
                }
                console.log("存在向右的单向follow:" + results.length);
                if (accounts.length > 0) {
                    createLeftrelation(accounts, accountsMap, accountsF, accountsMapF);
                } else {
                    checkRightRelation(accountsF, accountsMapF);
                }
            } else {
                createLeftrelation(accountsF, accountsMapF, accountsF, accountsMapF);
            }
        });
    }

    function createLeftrelation(accountsG, accountsMapG, accountsF, accountsMapF) {
        var query = [
            "MATCH (account:Account),(account2:Account)",
            "WHERE account.phone = {phone} AND account2.phone IN {phoneTo}",
            "CREATE UNIQUE account-[r:FOLLOW]->account2",
            "RETURN account,account2,r"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: accountsG
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
            } else {
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    var rNode = results[index].r;
                    var rData = rNode.data;
                    rData.alias = contact[account2Data.phone].nickName || ""
                    rNode.save(function (err, node) {
                    });
                }
                console.log("更新向右的单向follow:" + results.length);
                checkRightRelation(accountsF, accountsMapF);
            }
        });
    }

    function checkRightRelation(accountsF, accountsMapF) {
        console.log("start check follow relation : 判断向左的单向follow");
        var query = [
            "MATCH (account:Account)<-[r:FOLLOW]-(account2:Account)",
            "WHERE account.phone = {phone} AND account2.phone IN {phoneTo}",
            "RETURN account,account2,r"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: accountsF
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
            } else if (results.length > 0) {
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    accountsMapF[account2Data.phone] = "out";
                }
                var accounts = [];
                var accountsMap = [];
                for (var index in accountsMapF) {
                    if (accountsMapF[index] == "in") {
                        accounts.push(index);
                        accountsMap[index] = "in";
                    } else {
                        accountsMapF[index] = "in";
                    }
                }
                console.log("存在向左的单向follow:" + results.length);
                if (accounts.length > 0) {
                    createRightrelation(accounts, accountsMap);
                }
            } else {
                createRightrelation(accountsF, accountsMapF);
            }
        });
    }

    function createRightrelation(accountsH, accountsMapH) {
        var query = [
            "MATCH (account:Account),(account2:Account)",
            "WHERE account.phone = {phone} AND account2.phone IN {phoneTo}",
            "CREATE UNIQUE account-[r:FOLLOW]->account2",
            "RETURN account,account2,r"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: accountsH
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
            } else {
                console.log("更新向左的单向follow:" + results.length);
                //for (var index in results) {
                //    var account2Data = results[index].account2.data;
                //    var rNode = results[index].r;
                //    var rData = rNode.data;
                //    rData.alias = contact[account2Data.phone].nickName || ""
                //    rNode.save(function (err, node) {
                //    });
                //}
            }
        });
    }
}
relationManage.intimatefriends = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    getFriends();
    function getFriends() {
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)-[r2:FOLLOW]->(account:Account)",
            "WHERE account.phone={phone}",
            "RETURN account,account2,r"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取密友圈失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                var accountNode = results[0].account;
                var accountData = accountNode.data;
                var circlesOrderString = accountData.circlesOrderString;
                var flag = false;
                var circles = [];
                var circlesMap = {};
                if (circlesOrderString) {
                    try {
                        var circleOrder = JSON.parse(circlesOrderString);
                        for (var index in circleOrder) {
                            var circle = circleOrder[index];
                            if (circle.rid) {
                                circles.push(circle.rid);
                                circle.friends = [];
                                circlesMap[circle.rid] = circle;
                            } else {
                                flag = true;
                            }
                        }
                        if (!circlesMap["8888888"] || !circlesMap["9999999"]) {
                            flag = true;
                        }
                    } catch (e) {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (flag) {
                    circles = [];
                    circlesMap = {};
                    circles.push("9999999");
                    circlesMap["9999999"] = {
                        rid: "9999999",
                        name: "通讯录好友",
                        friends: []
                    };
                    circles.push("8888888");
                    circlesMap["8888888"] = {
                        rid: "8888888",
                        name: "默认分组",
                        friends: []
                    };
                    accountData.circlesOrderString = JSON.stringify([{
                        rid: "9999999",
                        name: "通讯录好友"
                    }, {
                        rid: "8888888",
                        name: "默认分组"
                    }]);
                    accountNode.save(function (err, node) {
                        console.log("初始化分组顺序.");
                    });
                }
                var friendsMap = {};
                var friends = [];
                for (var index in results) {
                    var account2Data = results[index].account2.data;
                    var rNode = results[index].r;
                    var rData = rNode.data;
                    if (circlesMap[rData.rid]) {
                        circlesMap[rData.rid].friends.push(account2Data.phone);
                    } else {
                        circlesMap["8888888"].friends.push(account2Data.phone);
                    }
                    var account = {
                        id: account2Data.ID,
                        sex: account2Data.sex,
                        age: account2Data.age,
                        phone: account2Data.phone,
                        mainBusiness: account2Data.mainBusiness,
                        head: account2Data.head,
                        nickName: account2Data.nickName,
                        userBackground: account2Data.userBackground,
                        addMessage: rData.message,
                        lastLoginTime: account2Data.lastlogintime,
                        alias: "",
                        createTime: account2Data.createTime,
                        longitude: account2Data.longitude || 0,
                        latitude: account2Data.latitude || 0
                    };
                    if (rData.alias != null) {
                        account.alias = rData.alias;
                    }
                    if (!friendsMap[account2Data.phone]) {
                        friends.push(account2Data.phone);
                    }
                    friendsMap[account2Data.phone] = account;
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取密友圈成功",
                    relationship: {
                        circles: circles,
                        circlesMap: circlesMap,
                        friendsMap: friendsMap,
                        friends: friends
                    }
                }), response);
            } else {
                var circles = [];
                var circlesMap = {};
                circles.push("9999999");
                circlesMap["9999999"] = {
                    rid: "9999999",
                    name: "通讯录好友",
                    friends: []
                };
                circles.push("8888888");
                circlesMap["8888888"] = {
                    rid: "8888888",
                    name: "默认分组",
                    friends: []
                };
                ResponseData(JSON.stringify({
                    "提示信息": "获取密友圈成功",
                    relationship: {
                        circles: circles,
                        circlesMap: circlesMap,
                        friendsMap: {},
                        friends: []
                    }
                }), response);
            }
        });
    }
}
relationManage.getfollow = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    getAttentionNode();
    function getAttentionNode() {
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)",
            "WHERE account.phone = {phone}",
            "RETURN account,r,account2"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取关注列表失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else {
                var friends = [];
                var friendsMap = {};
                for (var index in results) {
                    var rData = results[index].r.data;
                    var account2Data = results[index].account2.data;
                    var account = {
                        id: account2Data.ID,
                        sex: account2Data.sex,
                        age: account2Data.age,
                        phone: account2Data.phone,
                        mainBusiness: account2Data.mainBusiness,
                        head: account2Data.head,
                        nickName: account2Data.nickName,
                        userBackground: account2Data.userBackground,
                        addMessage: rData.message || "",
                        lastLoginTime: account2Data.lastlogintime,
                        alias: "",
                        createTime: account2Data.createTime,
                        longitude: account2Data.longitude || 0,
                        latitude: account2Data.latitude || 0
                    };
                    if (rData.alias != null) {
                        account.alias = rData.alias;
                    }
                    friends.push(account.phone);
                    friendsMap[account.phone] = account;
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取关注列表成功",
                    friends: friends,
                    friendsMap: friendsMap
                }), response);
            }
        });
    }
}
relationManage.getfans = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    getAttentionNode();
    function getAttentionNode() {
        var query = [
            "MATCH (account:Account)<-[r:FOLLOW]-(account2:Account)",
            "WHERE account.phone = {phone}",
            "RETURN account,r,account2"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取粉丝列表失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else {
                var friends = [];
                var friendsMap = {};
                for (var index in results) {
                    var rData = results[index].r.data;
                    var account2Data = results[index].account2.data;
                    var account = {
                        id: account2Data.ID,
                        sex: account2Data.sex,
                        age: account2Data.age,
                        phone: account2Data.phone,
                        mainBusiness: account2Data.mainBusiness,
                        head: account2Data.head,
                        nickName: account2Data.nickName,
                        userBackground: account2Data.userBackground,
                        addMessage: rData.message || "",
                        lastLoginTime: account2Data.lastlogintime,
                        alias: "",
                        createTime: account2Data.createTime,
                        longitude: account2Data.longitude || 0,
                        latitude: account2Data.latitude || 0
                    };
                    if (rData.alias != null) {
                        account.alias = rData.alias;
                    }
                    friends.push(account.phone);
                    friendsMap[account.phone] = account;
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取粉丝列表成功",
                    friends: friends,
                    friendsMap: friendsMap
                }), response);
            }
        });
    }
}
relationManage.follow = function (data, response) {
    response.asynchronous = 1;
    console.log("follow" + data);
    var phone = data.phone;
    var phoneTo = data.target;
    var message = data.message || "";
    var accessKey = data.accessKey;
    if (verifyEmpty.verifyEmpty(data, [phoneTo], response)) {
        checkRelation();
    }
    function checkRelation() {
        var query = [
            "MATCH (account:Account)<-[r:FOLLOW]-(account2:Account)",
            "WHERE account.phone={phone} AND account2.phone={phoneTo}",
            "RETURN account,r,account2"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "添加好友失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                createRelation(true);
            } else {
                createRelation(false);
            }
        });
    }

    function createRelation(flag) {
        var query = [
            "MATCH (account:Account),(account2:Account)",
            "WHERE account.phone={phone} AND account2.phone={phoneTo}",
            "CREATE UNIQUE account-[r:FOLLOW]->account2",
            "RETURN account,r,account2"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "添加好友失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "添加好友失败",
                    "失败原因": "用户不存在"
                }), response);
            } else {
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                if (flag) {
                    ResponseData(JSON.stringify({
                        "提示信息": "添加好友成功"
                    }), response);
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
                    push.inform(phone, phoneTo, accessKey, "*", event);
                } else {
                    var rNode = results.pop().r;
                    var rData = rNode.data;
                    rData.message = message;
                    rNode.save(function (error, results) {
                    });
                    ResponseData(JSON.stringify({
                        "提示信息": "发送请求成功"
                    }), response);
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
                    push.inform(phone, phone, accessKey, "*", event0);
                    var event1 = JSON.stringify({
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
                    client.rpush(phoneTo, event1, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, phoneTo, accessKey, "*", event1);
                }
            }
        });
    }
}
relationManage.modifycircle = function (data, response) {
    var phone = data.phone;
    console.log(data);
    var accessKey = data.accessKey;
    var phoneTo = data.targetphones;
    var rid = data.rid;
    if (verifyEmpty.verifyEmpty(data, [phoneTo], response)) {
        try {
            phoneTo = JSON.parse(phoneTo);
            modifyRid(phoneTo);
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "修改失败",
                "失败原因": "数据格式不正确"
            }), response);
            console.error(e);
        }
    }
    function modifyRid(phoneTo) {
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)",
            "WHERE account.phone={phone} AND account2.phone IN {phoneTo}",
            "SET r.rid={rid}",
            "RETURN account,r,account2"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: phoneTo,
            rid: rid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_updatefriendcircle",
                    content: JSON.stringify({
                        type: "relation_updatefriendcircle",
                        phone: phone,
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
                ResponseData(JSON.stringify({
                    "提示信息": "修改成功"
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "修改失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}
relationManage.modifyalias = function (data, response) {
    response.asynchronous = 1;
    console.info(data);
    var phone = data.phone;
    var friend = data.target;
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
            'MATCH (account1:Account)-[r:FOLLOW]->(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone={friend}',
            "SET r.alias={alias}",
            'RETURN r'
        ].join('\n');
        var params = {
            phone: phone,
            friend: friend,
            alias: friendAlias
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改备注失败",
                    "失败原因": "数据异常"
                }), response);
                console.error("modifyAlias:" + error);
            } else if (results.length == 0) {
                //TODO event
                ResponseData(JSON.stringify({
                    "提示信息": "修改备注失败",
                    "失败原因": "好友不存在"
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "修改备注成功"
                }), response);
            }
        });
    }
}
relationManage.canclefollow = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneTo = data.target;
    if (verifyEmpty.verifyEmpty(data, [phoneTo], response)) {
        try {
            //phoneTo = JSON.parse(phoneTo);
            cancelFellowRelation();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "取消关注失败",
                "失败原因": "数据格式不正确"
            }), response);
            console.error(e);
        }
    }
    function cancelFellowRelation() {
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)",
            "WHERE account.phone={phone} AND account2.phone={phoneTo}",
            "DELETE r",
            "RETURN account,account2"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "取消关注失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
                //TODO event
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_updatefollow",
                    content: JSON.stringify({
                        type: "relation_updatefollow",
                        phone: phone,
                        phoneTo: phoneTo,
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
                var event2 = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_updatefans",
                    content: JSON.stringify({
                        type: "relation_updatefans",
                        phone: phone,
                        phoneTo: phoneTo,
                        eid: eid,
                        time: time,
                        status: "success",
                        content: ""
                    })
                });
                client.rpush(phoneTo, event2, function (err, reply) {
                    if (err) {
                        console.error("保存Event失败");
                    } else {
                        console.log("保存Event成功");
                    }
                });
                push.inform(phone, phoneTo, accessKey, "*", event2);
                ResponseData(JSON.stringify({
                    "提示信息": "取消关注成功"
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "取消关注失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}
relationManage.deletefriend = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneTo = data.target;
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    if (verifyEmpty.verifyEmpty(data, [phoneTo], response)) {
        try {
            //phoneTo = JSON.parse(phoneTo);
            deleteFellow();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "删除好友失败",
                "失败原因": "数据格式不正确"
            }), response);
            console.error(e);
        }
    }
    function deleteFellow() {
        var query = [
            "MATCH (account:Account)-[r:FOLLOW]->(account2:Account)-[r2:FOLLOW]->(account:Account)",
            "WHERE account.phone={phone} AND account2.phone={phoneTo}",
            "DELETE r,r2",
            "RETURN account,account2"
        ].join("\n");
        var params = {
            phone: phone,
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "删除好友失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else if (results.length > 0) {
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
                push.inform(phone, phoneTo, accessKey, "*", event);
                ResponseData(JSON.stringify({
                    "提示信息": "删除好友成功"
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "删除好友失败",
                    "失败原因": "用户不存在"
                }), response);
            }
        });
    }
}
relationManage.blacklist = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var phoneTo = data.target;
    var operation = data.operation;
    var time = new Date().getTime();
    var eid = phone + "_" + time;
    if (verifyEmpty.verifyEmpty(data, [phoneTo], response)) {
        try {
            addFriendToMyBlackList(phone, phoneTo);
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "更新黑名单失败",
                "失败原因": "参数格式错误"
            }));
            response.end();
            console.error(e);
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
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "更新黑名单失败",
                    "失败原因": "用户不存在"
                }));
                response.end();
            } else {
                var event = JSON.stringify({
                    sendType: "event",
                    contentType: "relation_blacklist",
                    content: JSON.stringify({
                        type: "relation_blacklist",
                        phone: phone,
                        phoneTo: phoneTo,
                        time: time,
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
                var accountNode = results.pop().account;
                var accountData = accountNode.data;
                var praise = accountData.praises;
                var praiseJSON;
                try {
                    praiseJSON = JSON.parse(praise);
                } catch (e) {
                    praiseJSON = [];
                }
                if (operation == "true") {
                    praiseJSON.push(phoneTo);
                } else if (operation == "false") {
                    var tempPraise = [];
                    for (var index in praiseJSON) {
                        if (praiseJSON[index] != phoneTo) {
                            tempPraise.push(praiseJSON[index]);
                        }
                    }
                    praiseJSON = tempPraise;
                }
                accountData.blacklist = JSON.stringify(praiseJSON);
                accountNode.save(function (err, node) {
                    if (err) {
                        response.write(JSON.stringify({
                            "提示信息": "更新黑名单失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                        console.log(err);
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
relationManage.modifysequence = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var circleSequence = data.sequence;
    var accessKey = data.accessKey;
    var time = new Date().getTime();
    var eid = phone + "_" + time;
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
                    contentType: "relation_circlesequence",
                    content: JSON.stringify({
                        type: "relation_circlesequence",
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


//liaoliao new APi************************************************************************
relationManage.fuzzyquery = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var keyWord = data.keyword;
    getFuzzyAccountNodes();
    function getFuzzyAccountNodes() {
        var query = [
            "MATCH (account:Account)",
            "WHERE account.phone ={keyword}",// OR account.nickName={keyword}
            "RETURN account"
        ].join("\n");
        var params = {
            keyword: keyWord
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "查询失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
            } else {
                console.log(results.length);
                var accounts = [];
                var accountsMap = {};
                for (var index in results) {
                    var accountData = results[index].account.data;
                    if (!accountsMap[accountData.phone]) {
                        accounts.push(accountData.phone);
                        var account = {
                            id: accountData.ID,
                            sex: accountData.sex,
                            age: accountData.age,
                            phone: accountData.phone,
                            mainBusiness: accountData.mainBusiness,
                            head: accountData.head,
                            nickName: accountData.nickName,
                            userBackground: accountData.userBackground,
                            lastLoginTime: accountData.lastlogintime,
                            createTime: accountData.createTime,
                            longitude: accountData.longitude || 0,
                            latitude: accountData.latitude || 0
                        };
                        accountsMap[accountData.phone] = account;
                    }
                    //continue;
                }
                ResponseData(JSON.stringify({
                    "提示信息": "查询成功",
                    accounts: accounts,
                    accountsMap: accountsMap
                }), response);
            }
        });
    }


}
module.exports = relationManage;