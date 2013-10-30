var serverSetting = root.globaldata.serverSetting;
var relationManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/*************************************** ***************************************
 * *    Relation：Account Community
 *************************************** ***************************************/

/***************************************
 *     URL：/api2/relation/join
 ***************************************/
relationManage.join = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var cid = data.cid;
    var phone = data.phone;
    joinCommunityNode();

    function joinCommunityNode() {
        var query = [
            'START community=node({cid})',
            'MATCH (account:Account)',
            'WHERE account.phone={phone}',
            'CREATE UNIQUE account-[r:HAS_COMMUNITY]->community',
            'RETURN  account, r'
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
                var accountNode = results.pop().account;
                if (accountNode.data.status == "unjoin") {
                    accountNode.data.status = "success";
                    accountNode.save();
                }
                response.write(JSON.stringify({
                    "提示信息": "加入成功"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/relation/getcommunities
 ***************************************/
relationManage.getcommunities = function (data, response) {
    response.asynchronous = 1;

    var phone = data.phone;
    var query = [
        'MATCH (account:Account)-[r:HAS_COMMUNITY]->(community:Community)',
        'WHERE account.phone={phone}',
        'RETURN community'
    ].join('\n');
    var params = {
        phone: phone
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
            response.write(JSON.stringify({
                "提示信息": "获取社区失败",
                "错误原因": "数据异常"
            }));
            response.end();
            return;
        } else {
            console.log("获取社区成功---");
            var communities = [];
            var i = 0;
            for (var index in results) {
                i++;
                var it = results[index].community.data;
                delete it.locations;
                communities.push(it);
                if (i == results.length) {
                    response.write(JSON.stringify({
                        "提示信息": "获取社区成功",
                        "communities": communities
                    }));
                    response.end();
                }
            }
        }
    });
}
/*************************************** ***************************************
 * *    Relation：Account Account
 *************************************** ***************************************/

/***************************************
 *     URL：/api2/relation/addfriend
 ***************************************/
relationManage.addfriend = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var phoneto = data.phoneto;
    var rid = data.rid;
    var accessKey = data.accessKey;
    addFriendNode();

    function addFriendNode() {
        var query = [
            'MATCH (account1:Account),(account2:Account)',
            'WHERE account1.phone={phone} AND account2.phone={phoneto}',
            'CREATE UNIQUE account1-[r:FRIEND]->account2',
            'RETURN  r'
        ].join('\n');

        var params = {
            phone: phone,
            phoneto: phoneto
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
                console.log("添加FRIEND成功---");
                var rNode = results.pop().r;
                var rData = rNode.data;
                rData.status = 0;
                rNode.save();
                addAccountCircleNode(rid, phoneto);
            }
        });
    }

    function addAccountCircleNode(rid, phoneto) {
        var query = [
            'START circle=node({rid})',
            'MATCH (account:Account)',
            'WHERE account.phone={phoneto}',
            'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
            'RETURN  r'
        ].join('\n');

        var params = {
            rid: rid,
            phoneto: phoneto
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
                console.log("添加HAS_FRIEND成功---");
                response.write(JSON.stringify({
                    "提示信息": "添加成功"
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
/*************************************** ***************************************
 * *    Relation：Account Circle
 *************************************** ***************************************/
/***************************************
 *     URL：/api2/relation/addcircle
 ***************************************/
relationManage.addcircle = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var name = data.name;
    var circle = {
        name: name
    };
    var query = [
        'MATCH (account:Account)',
        'WHERE account.phone={phone}',
        'CREATE UNIQUE account-[r:HAS_CIRCLE]->(circle:Circle{circle})',
        'SET circle.rid=ID(circle)',
        'RETURN circle'
    ].join('\n');
    var params = {
        phone: phone,
        circle: circle
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
            return;
        } else if (results.length > 0) {
            console.log("添加密友圈成功---");
            response.write(JSON.stringify({
                "提示信息": "添加成功"
            }));
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "添加失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/relation/getcirclefriends
 ***************************************/
relationManage.getcirclefriends = function (data, response) {
    response.asynchronous = 1;
    var rid = data.rid;
    var query = [
        'MATCH (account:Account)-[r:HAS_FRIEND]->(circle:Circle)',
        'WHERE circle.rid={rid}',
        'RETURN account'
    ].join('\n');
    var params = {
        rid: rid
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "获取密友圈好友失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else {
            console.log("获取密友圈成员成功---");
            var accounts = [];
            for (var index in results) {
                var it = results[index].account.data;
                accounts.push(it);
            }
            response.write(JSON.stringify({
                "提示信息": "获取密友圈好友成功",
                accounts: accounts
            }));
            response.end();
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
    var count = 0;
    var query = [
        'MATCH (account:Account)-[r1:HAS_CIRCLE]->(circle:Circle)',
        'WHERE account.phone={phone}',
        'RETURN circle'
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
            console.log(error);
            return;
        } else {
            var circles = [];
            var i = 0;
            for (var index in results) {
                i++;
                var it = results[index].circle.data;
                var accounts = [];
                getCircleFriends(it.rid, phone, accounts, it, circles, next);
            }
            function next(circles) {
                if (count == results.length) {
                    response.write(JSON.stringify({
                        "提示信息": "获取密友圈成功",
                        circles: circles
                    }));
                    response.end();
                }
            }
        }
    });

    function getCircleFriends(rid, phone, accounts, its, circles, next) {
        var query = [
            'MATCH (circle:Circle)-[r:HAS_FRIEND]->(account1:Account)-[r1:FRIEND]-(account2:Account)',
            'WHERE account2.phone={phone} AND circle.rid={rid} AND r1.status in[1,2,3]',
            'RETURN account1'
        ].join('\n');
        var params = {
            phone: phone,
            rid: rid
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "获取密友圈失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                var i = 0;
                for (var index in results) {
                    i++;
                    var it = results[index].account1.data;
                    delete it.password;
                    delete it.time;
                    delete it.code;

                    accounts.push(it);
                    if (i == results.length) {
                        its.accounts = accounts;
                        circles.push(its);
                        count++;
                        next(circles);
                    }
                }
            } else {
                its.accounts = accounts;
                circles.push(its);
                count++;
                next(circles);
            }
        });
    }
}
/***************************************
 *     URL：/api2/relation/addfriendagree
 ***************************************/
relationManage.addfriendagree = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var phoneAsk = data.phoneask;
    var rid = data.rid;
    var status = data.status;
    var accessKey = data.accessKey;
    if (status) {
        agreeAddFriendNode(phone, phoneAsk);
    } else {
        refuseAddFriend(rid, phoneAsk, phone);
    }
    function agreeAddFriendNode(phone, phoneAsk) {
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
                rData.status = 1;
                rNode.save();
                response.write(JSON.stringify({
                    "提示信息": "添加成功"
                }));
                response.end();
            }
        });
    }

    function refuseAddFriend(rid, phoneAsk, phone) {
        deleteFriend();
        function deleteFriend() {
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
                        "提示信息": "删除失败",
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
                'WHERE circle.rid={rid} AND account.phone={phoneAsk}'
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
                        "提示信息": "删除失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "删除成功"
                    }));
                    response.end();
                }
            });
        }
    }
}

module.exports = relationManage;