var serverSetting = root.globaldata.serverSetting;
var circleManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/circle/modify
 ***************************************/
circleManage.modify = function (data, response) {
    response.asynchronous = 1;
    var rid = data.rid;
    var name = data.name;
    var circle = {
        name: name
    };
    var query = [
        'MATCH (circle:Circle)',
        'WHERE circle.rid={rid}',
        'SET circle.name={name}',
        'RETURN circle'
    ].join('\n');
    var params = {
        rid: parseInt(rid),
        name: name
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "修改失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else if (results.length > 0) {
            console.log("修改密友圈成功---");
            response.write(JSON.stringify({
                "提示信息": "修改成功"
            }));
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "修改失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/circle/delete
 ***************************************/
circleManage.delete = function (data, response) {
    response.asynchronous = 1;
    var rid = data.rid;
    var query = [
        'MATCH other-[r]-(circle:Circle)',
        'WHERE circle.rid={rid}',
        'DELETE circle,r',
        'RETURN circle,r'
    ].join('\n');
    var params = {
        rid: parseInt(rid)
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
            return;
        } else if (results.length > 0) {
            console.log("删除密友圈成功---");
            response.write(JSON.stringify({
                "提示信息": "删除成功"
            }));
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "删除失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/circle/moveorout
 ***************************************/
circleManage.moveorout = function (data, response) {
    response.asynchronous = 1;
    var phoneTo = data.phoneto;
    var rid = data.rid;
    var filter = (data.filter).toUpperCase();
    var query;
    var successMSG = "";
    var errorMSG = "";
    if (filter == "REMOVE") {
        query = [
            'MATCH (circle:Circle)-[r:HAS_FRIEND]->(account:Account)',
            'WHERE circle.rid={rid} AND account.phone={phoneTo}',
            'DELETE r',
            'RETURN circle'
        ].join('\n');
        successMSG = JSON.stringify({
            "提示消息": "移出成功"
        });
        errorMSG = JSON.stringify({
            "提示消息": "移出失败",
            "失败原因": "数据异常"
        });
    } else if (filter == "SHIFTIN") {
        query = [
            'START circle=node({rid})',
            'MATCH (account:Account)',
            'WHERE account.phone={phoneTo}',
            'CREATE UNIQUE circle-[r:HAS_FRIEND]->account',
            'RETURN  r'
        ].join('\n');
        success = JSON.stringify({
            "提示消息": "移入成功"
        });
        error = JSON.stringify({
            "提示消息": "移入失败",
            "失败原因": "数据异常"
        });
    }
    params = {
        rid: parseInt(rid),
        phoneTo: phoneTo
    };
    db.query(query, params, function (error, results) {
        if (error) {
            response.write(errorMSG);
            response.end();
            console.log(error);
            return;
        } else if (results.length > 0) {
            response.write(successMSG);
            response.end();

        } else {
            response.write(errorMSG);
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/circle/moveout
 ***************************************/
circleManage.moveout = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phoneTo = data.phoneto;
    var oldRid = data.oldrid;
    var newRid = data.newrid;
    if (oldRid != "undefined" && oldRid != undefined) {
        deleteRelationNode(phoneTo, newRid, oldRid);
    } else {
        createRelationNode(phoneTo, newRid);
    }
    function deleteRelationNode(phoneTo, newRid, oldRid) {
        var query = [
            'MATCH (circle:Circle)-[r:HAS_FRIEND]->(account:Account)',
            'WHERE circle.rid={rid} AND account.phone={phoneTo}',
            'DELETE r',
            'RETURN circle'
        ].join('\n');

        var params = {
            rid: parseInt(oldRid),
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error + "deleteRelationNode");
                return;
            } else if (results.length > 0) {
                if (newRid != "undefined" && newRid != undefined) {
                    createRelationNode(phoneTo, newRid);
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "移动成功"
                    }));
                    response.end();
                }
            } else {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            }
        });
    }

    function createRelationNode(phoneTo, newRid) {
        var query = [
            'START circle=node({newRid})',
            'MATCH (account:Account)',
            'WHERE account.phone={phoneTo}',
            'CREATE circle-[r:HAS_FRIEND]->account',
            'RETURN r'
        ].join('\n');
        var params = {
            newRid: parseInt(newRid),
            phoneTo: phoneTo
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.log(error);
                return;
            } else if (results.length > 0) {
                response.write(JSON.stringify({
                    "提示信息": "移动成功"
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "移动失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/circle/addcircle
 ***************************************/
circleManage.addcircle = function (data, response) {
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
            response.write(JSON.stringify({
                "提示信息": "添加失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else if (results.length > 0) {
            console.log("创建密友圈成功---");
            var circleData = results.pop().circle.data;
            var accounts = [];
            circleData.accounts = accounts;
            response.write(JSON.stringify({
                "提示信息": "添加成功",
                "circle": circleData
            }));
            response.end();
        } else {
            console.log("创建密友圈失败---");
            response.write(JSON.stringify({
                "提示信息": "添加失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
module.exports = circleManage;