var serverSetting = root.globaldata.serverSetting;
var shareManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var verifyEmpty = require("./../lib/verifyParams.js");
var ajax = require("./../lib/ajax.js");

/***************************************
 *     URL：/api2/share/addcomment
 ***************************************/
shareManage.addcomment = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var phoneTo = data.phoneto;
    var gid = data.gid;
    var sid = data.sid;
    var gsid = data.gsid;
    var nickName = data.nickName;
    var nickNameTo = data.nickNameTo;
    var head = data.head;
    var headTo = data.headTo;
    var contentType = data.contentType;
    var content = data.content;
    var arr = [gid, sid, gsid, nickName, contentType, content];
    var arr1 = [sid, gsid, nickName, contentType, content];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        modifyShareComments();
    } else if (verifyEmpty.verifyEmpty(data, arr1, response)) {
        newModifyComments();
    }
    function modifyShareComments() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid} AND shares.sid={sid}",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            sid: parseInt(sid),
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "评论群分享失败",
                    "失败原因": "数据异常",
                    gid: gid,
                    sid: sid,
                    gsid: gsid
                }), response);
                console.error(error);
                return;
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "评论群分享失败",
                    "失败原因": "消息不存在",
                    gid: gid,
                    sid: sid,
                    gsid: gsid
                }), response);
            } else {
                var shareNode = results.pop().share;
                var shareData = shareNode.data;
                var comments = shareData.comments;
                var commentsJSON;
                try {
                    commentsJSON = JSON.parse(comments);
                } catch (e) {
                    commentsJSON = [];
                }
                var comment = {
                    phone: phone,
                    phoneTo: phoneTo,
                    nickName: nickName,
                    nickNameTo: nickNameTo,
                    head: head,
                    headTo: headTo || "",
                    contentType: contentType,
                    content: content,
                    time: new Date().getTime()
                };
                commentsJSON.push(comment);
                shareData.comments = JSON.stringify(commentsJSON);
                shareNode.save(function (error, node) {
                    if (error) {
                        ResponseData(JSON.stringify({
                            "提示信息": "评论群分享失败",
                            "失败原因": "数据异常",
                            gid: gid,
                            sid: sid,
                            gsid: gsid
                        }), response);
                        console.error(error);
                        return;
                    } else {
                        checkLbsShare(shareData.comments);
                        ResponseData(JSON.stringify({
                            "提示信息": "评论群分享成功",
                            gid: gid,
                            sid: sid,
                            gsid: gsid
                        }), response);
                    }
                });
            }
        });
    }

    function newModifyComments() {
        var query = [
            "MATCH (shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE share.gsid={gsid} AND shares.sid={sid}",
            "RETURN share"
        ].join("\n");
        var params = {
            sid: parseInt(sid),
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "评论群分享失败",
                    "失败原因": "数据异常",
                    sid: sid,
                    gsid: gsid
                }), response);
                console.error(error);
                return;
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "评论群分享失败",
                    "失败原因": "消息不存在",
                    sid: sid,
                    gsid: gsid
                }), response);
            } else {
                var shareNode = results.pop().share;
                var shareData = shareNode.data;
                var comments = shareData.comments;
                var commentsJSON;
                try {
                    commentsJSON = JSON.parse(comments);
                } catch (e) {
                    commentsJSON = [];
                }
                var comment = {
                    phone: phone,
                    phoneTo: phoneTo,
                    nickName: nickName,
                    nickNameTo: nickNameTo,
                    head: head,
                    headTo: headTo || "",
                    contentType: contentType,
                    content: content,
                    time: new Date().getTime()
                };
                commentsJSON.push(comment);
                shareData.comments = JSON.stringify(commentsJSON);
                shareNode.save(function (error, node) {
                    if (error) {
                        ResponseData(JSON.stringify({
                            "提示信息": "评论群分享失败",
                            "失败原因": "数据异常",
                            sid: sid,
                            gsid: gsid
                        }), response);
                        console.error(error);
                        return;
                    } else {
                        checkLbsShare(shareData.comments);
                        ResponseData(JSON.stringify({
                            "提示信息": "评论群分享成功",
                            sid: sid,
                            gsid: gsid
                        }), response);
                    }
                });
            }
        });
    }

    function checkLbsShare(comments) {
        try {
            ajax.ajax({
                type: "POST",
                url: serverSetting.LBS.DATA_SEARCH,
                data: {
                    key: serverSetting.LBS.KEY,
                    tableid: serverSetting.LBS.SHARESTABLEID,
                    filter: "gsid:" + gsid
                }, success: function (info) {
                    var info = JSON.parse(info);
                    if (info.status == 1 && info.count >= 1) {
                        var id = info.datas[0]._id;
                        modifyLabsShare(comments, id);
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

    function modifyLabsShare(comments, id) {
        try {
            ajax.ajax({
                type: "POST",
                url: serverSetting.LBS.DATA_UPDATA,
                data: {
                    key: serverSetting.LBS.KEY,
                    tableid: serverSetting.LBS.SHARESTABLEID,
                    data: JSON.stringify({
                        _id: id,
                        comments: comments
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
            return;
        }
    }
}
/***************************************
 *     URL：/api2/share/delete
 ***************************************/
shareManage.delete = function (data, response) {
    response.asynchronous = 1;
    var sid = data.sid;
    var gsid = data.gsid;
    var location = data.location;
    var arr = [sid, gsid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        deleteShareNode();
    }
    function deleteShareNode() {
        var query = [
            "MATCH (shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE shares.sid={sid} AND share.gsid={gsid}",
            "DELETE r1",
            "RETURN share"
        ].join("\n");
        var params = {
            sid: parseInt(sid),
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "删除群分享失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                if (location != null && location != undefined && location != "") {
                    deleteLbsShare(gsid, location);
                }
                response.write(JSON.stringify({
                    "提示信息": "删除群分享失败",
                    "失败原因": "群分享不存在"
                }));
                response.end();
            } else {
                if (location != null && location != undefined && location != "") {
                    deleteLbsShare(gsid, location);
                }
                response.write(JSON.stringify({
                    "提示信息": "删除群分享成功"
                }));
                response.end();
            }
            function deleteLbsShare(gsid, location) {
                console.error("deleteLbsShare");
                try {
                    ajax.ajax({
                        type: "POST",
                        url: serverSetting.LBS_DELETE,
                        data: {
                            primaryKey: gsid,
                            location: location
                        }, success: function (info) {
                            var info = JSON.parse(info);
                            if (info.提示信息 == "删除成功") {
                                console.log("success--")
                            } else {
                                console.log("delete error--")
                            }
                        }
                    });
                } catch (e) {
                    console.log(e);
                }
            }
        });
    }
}

/*******************************************************************************
 * * * * * * * * * * * * New Api * * * * * * * * * * * * * * * * * * * * * * * *
 *******************************************************************************/
shareManage.getusershares = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.target;
    if (!phone) {
        phone = data.phone;
    }
    var nowpage = data.nowpage;
    var pagesize = data.pagesize;
    var arr = [phone, nowpage, pagesize];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getUserShares();
    }
    function getUserShares() {
        var query = [
            "MATCH (group:Group)-->(shares:Shares)-->(share:Share)",
            "WHERE  share.phone={phone}",
            "RETURN group,share,shares",
            "ORDER BY share.time DESC",
            "SKIP {start}",
            "LIMIT {pagesize}"
        ].join("\n");
        var params = {
            phone: phone,
            start: parseInt(nowpage) * parseInt(pagesize),
            pagesize: parseInt(pagesize)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享失败",
                    nowpage: nowpage,
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else {
                if (results.length > 0) {
                    var shares = [];
                    var sharesMap = {};
                    for (var index in results) {
                        var groupData = results[index].group.data;
                        var shareData = results[index].share.data;
                        var sharesData = results[index].shares.data;
                        var share = {
                            comments: JSON.parse(shareData.comments),
                            content: shareData.content,
                            praiseusers: shareData.praises ? JSON.parse(shareData.praises) : [],
                            gsid: shareData.gsid,
                            type: shareData.type,
                            time: shareData.time,
                            phone: shareData.phone,
                            sid: sharesData.sid,
                            gid: groupData.gid,
                            totalScore: shareData.totalScore || 0,
                            scores: shareData.scores ? JSON.parse(shareData.scores) : {},
                            status: "sent"
                        };
                        shares.push(shareData.gsid);
                        sharesMap[shareData.gsid] = share;
                    }
                    ResponseData(JSON.stringify({
                        "提示信息": "获取群分享成功",
                        nowpage: nowpage,
                        shares: shares,
                        sharesMap: sharesMap
                    }), response);
                } else {
                    ResponseData(JSON.stringify({
                        "提示信息": "获取群分享成功",
                        nowpage: nowpage,
                        shares: [],
                        sharesMap: {}
                    }), response);
                }
            }
        });
    }
}
var push = require('../lib/push.js');
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
shareManage.sendboardshare = function (data, response) {
    response.asynchronous = 1;
    console.error(data);
    if (data) {
        //return;
    }
    var sid = data.sid;
    var phone = data.phone;
    var nickName = data.nickName;
    var head = data.head;
    var message = data.message;
    //var type = data.type;
    //var content = data.content;
    var gid = data.gid; //unused
    var ogsid = data.ogsid;
    var location = data.location;
    if (verifyEmpty.verifyEmpty(data, [gid, sid, phone, ogsid, nickName, head, message], response)) {
        try {
            message = JSON.parse(message);
            if (message.type == "imagetext") {
                createShare();
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据格式不正确",
                    ogsid: ogsid,
                    sid: sid,
                    gid: gid
                }), response);
            }
        } catch (e) {
            console.error(e);
            ResponseData(JSON.stringify({
                "提示信息": "发布群分享失败",
                "失败原因": "数据格式不正确",
                ogsid: ogsid,
                sid: sid,
                gid: gid
            }), response);
        }
    }
    function createShare() {
        var query = [
            'MATCH (shares:Shares)',
            'WHERE shares.sid={sid}',
            'CREATE UNIQUE shares-[r1:HAS_SHARE]->(share:Share{share})',
            'SET share.gsid=ID(share)',
            'RETURN share'
        ].join("\n");
        var share = {
            phone: phone,
            nickName: nickName,
            head: head,
            //praises: "[]",
            comments: "[]",
            type: message.type,
            nodeType: "Share",
            sid: sid,
            content: message.content,
            totalScore: 0,
            scores: "{}",
            location: location || "[]",
            time: new Date().getTime()
        };
        var params = {
            sid: parseInt(sid),
            share: share
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    sid: sid,
                    gid: gid
                }), response);
                console.error(error);
            } else if (results.length == 0) {
                console.log("发布群分享失败");
                ResponseData(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    sid: sid,
                    gid: gid
                }), response);
            } else {
                var shareData = results.pop().share.data;
                //console.log("发布群分享成功");
                var address = data.address;
                if (location != null && location != undefined && location != "") {
                    createLbsShare(shareData);
                }
                ResponseData(JSON.stringify({
                    "提示信息": "发布群分享成功",
                    time: shareData.time,
                    ogsid: ogsid,
                    sid: sid,
                    gsid: shareData.gsid,
                    gid: gid
                }), response);
                console.log("发布群分享成功");
            }
        });
        function createLbsShare(shareData) {
            try {
                ajax.ajax({
                    type: "POST",
                    url: serverSetting.LBS_SHAERE_CREATE,
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    data: {
                        location: location,
                        primaryKey: shareData.gsid,
                        data: JSON.stringify({
                            nickName: encodeURI(shareData.nickName),
                            gsid: shareData.gsid,
                            phone: shareData.phone,
                            head: shareData.head,
                            content: encodeURI(shareData.content),
                            totalScore: shareData.totalScore,
                            time: shareData.time,
                            scores: "{}"
                        })
                    }, success: function (info) {
                        var info = JSON.parse(info);
                        if (info.提示信息 == "创建成功") {
                            console.log("success--")
                        } else {
                            console.log("error--create")
                        }
                    }
                });
            } catch (e) {
                console.log(e);
                return;
            }
        }
    }
}
//var b = new Buffer("qiaoxiaosong").toString("base64");
//console.log(b);
shareManage.getboardshares = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var gid = data.gid;
    var sid = data.sid;
    var nowpage = data.nowpage;
    var pagesize = data.pagesize;
    if (verifyEmpty.verifyEmpty(data, [gid, sid, nowpage, pagesize], response)) {
        getShareNodes();
    }
    function getShareNodes() {
        var query = [
            "MATCH (shares:Shares)-[r:HAS_SHARE]->(share:Share)",
            "WHERE shares.sid={sid}",
            "RETURN share",
            "ORDER BY share.time DESC",
            "SKIP {start}",
            "LIMIT {pagesize}"
        ].join("\n");
        var params = {
            sid: parseInt(sid),
            start: parseInt(nowpage) * parseInt(pagesize),
            pagesize: parseInt(pagesize)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享失败",
                    "失败原因": "数据异常"
                }), response);
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    gid: gid,
                    sid: sid,
                    nowpage: nowpage,
                    shares: {
                        shareMessagesOrder: [],
                        shareMessagesMap: {}
                    }
                }), response);
            } else {
                var shares = [];
                var sharesMap = {};
                for (var index in results) {
                    var result = results[index];
                    var shareData = result.share.data;
                    var share = {
                        comments: JSON.parse(shareData.comments),
                        content: shareData.content,
                        praiseusers: shareData.praises ? JSON.parse(shareData.praises) : [],
                        gsid: shareData.gsid,
                        type: shareData.type,
                        time: shareData.time,
                        phone: shareData.phone,
                        totalScore: shareData.totalScore || 0,
                        scores: shareData.scores ? JSON.parse(shareData.scores) : {},
                        status: "sent"
                    };
                    shares.push(share.gsid + "");
                    sharesMap[share.gsid] = share;
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    gid: gid,
                    sid: sid,
                    nowpage: nowpage,
                    shares: {
                        shareMessagesOrder: shares,
                        shareMessagesMap: sharesMap
                    }
                }), response);
            }
        });
    }
}
shareManage.addboard = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var name = data.name;
    var osid = data.osid;
    var targetPhones = data.targetphones;
    if (verifyEmpty.verifyEmpty(data, [gid, name, osid, targetPhones], response)) {
        try {
            targetPhones = JSON.parse(targetPhones);
            addBoardNode();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "创建版块失败",
                "失败原因": "数据格式不正确",
                osid: osid
            }), response);
        }
    }
    function addBoardNode() {
        var query = [
            "MATCH (group:Group)",
            "WHERE group.gid={gid}",
            "CREATE UNIQUE group-[r:SHARE]->(shares:Shares{shares})",
            "SET shares.sid=ID(shares)",
            "RETURN group,shares"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            shares: {
                name: name,
                gid: parseInt(gid),
                nodeType: "Shares",
                status: "active",
                type: "Sub",
                createTime: new Date().getTime()
            }
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                ResponseData(JSON.stringify({
                    "提示信息": "创建版块失败",
                    "失败原因": "数据异常",
                    osid: osid
                }), response);
            } else if (results.length > 0) {
                var pop = results.pop();
                var sharesData = pop.shares.data;
                var groupNode = pop.group;
                var groupData = groupNode.data;
                if (groupData.boardSequenceString) {
                    try {
                        var boardOrder = JSON.parse(groupData.boardSequenceString);
                        boardOrder.push(sharesData.sid);
                        groupData.boardSequenceString = JSON.stringify(boardOrder);
                        groupNode.save(function (err, node) {
                        });
                    } catch (e) {
                        groupData.boardSequenceString = null;
                        groupNode.save(function (err, node) {
                        });
                    }
                } else {
                    groupData.boardSequenceString = null;
                    groupNode.save(function (err, node) {
                    });
                }
                ResponseData(JSON.stringify({
                    "提示信息": "创建版块成功",
                    "失败原因": "数据异常",
                    sid: sharesData.sid,
                    gid: groupData.gid,
                    osid: osid
                }), response);
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                for (var index in targetPhones) {
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "group_newboard",
                        content: JSON.stringify({
                            type: "group_newboard",
                            phone: phone,
                            phoneTo: index,
                            gid: groupData.gid,
                            eid: eid,
                            time: time,
                            status: "success",
                            content: name
                        })
                    });
                    client.rpush(index, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, index, accessKey, "*", event);
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "创建版块失败",
                    "失败原因": "数据异常",
                    osid: osid
                }), response);
            }
        });
    }
}
shareManage.modifyboard = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var sid = data.sid;
    var name = data.name;
    var description = data.description;
    var head = data.head;
    var cover = data.cover;
    var status = data.status;
    var targetPhones = data.targetphones;
    if (verifyEmpty.verifyEmpty(data, [sid, targetPhones], response)) {
        try {
            targetPhones = JSON.parse(targetPhones);
            modifyBoardNode();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "修改版块失败",
                "失败原因": "数据格式不正确",
                sid: sid
            }), response);
        }
    }
    function modifyBoardNode() {
        var query = [
            "MATCH (shares:Shares)",
            "WHERE shares.sid={sid}",
            "RETURN shares"
        ].join("\n");
        var params = {
            sid: parseInt(sid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "修改版块失败",
                    "失败原因": "数据异常",
                    sid: sid
                }), response);
            } else if (results.length > 0) {
                var sharesNode = results.pop().shares;
                var sharesData = sharesNode.data;
                if (name != undefined && name != null && name != "") {
                    sharesData.name = name;
                }
                if (head != undefined && head != null && head != "") {
                    sharesData.head = head;
                }
                if (description != undefined && description != null && description != "") {
                    sharesData.description = description;
                }
                if (cover != undefined && cover != null && cover != "") {
                    sharesData.cover = cover;
                }
                if (status != undefined && status != null && status != "") {
                    sharesData.status = status;
                }
                sharesNode.save(function (err, node) {
                });
                ResponseData(JSON.stringify({
                    "提示信息": "修改版块成功",
                    board: sharesData
                }), response);
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                for (var index in targetPhones) {
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "group_updateboard",
                        content: JSON.stringify({
                            type: "group_updateboard",
                            phone: phone,
                            phoneTo: index,
                            sid: sid,
                            eid: eid,
                            time: time,
                            status: "success",
                            content: JSON.stringify(sharesData)
                        })
                    });
                    client.rpush(index, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, index, accessKey, "*", event);
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "修改版块失败",
                    "失败原因": "版块不存在",
                    sid: sid
                }), response);
            }
        });
    }
}
shareManage.getgroupboards = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    if (verifyEmpty.verifyEmpty(data, [gid], response)) {
        getGroupBoardNodes();
    }
    function getGroupBoardNodes() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(board:Shares)",
            "WHERE group.gid={gid}",
            "RETURN group,board"
        ].join("\n");
        var params = {
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块失败",
                    "失败原因": "数据异常"
                }), response);
            } else if (results.length > 0) {
                var groupNode = results[0].group;
                var groupData = groupNode.data;
                var boardsMap = {};
                var boardsMapT = {};
                var boarads = [];
                for (var index in results) {
                    var boardData = results[index].board.data;
                    boardsMap[boardData.sid] = boardData;
                    boardsMapT[boardData.sid] = 1;
                    boarads.push(boardData.sid);
                }
                var boardOrder;
                if (groupData.boardSequenceString) {
                    try {
                        boardOrder = JSON.parse(groupData.boardSequenceString);
                        var flag = false;
                        for (var index in boardOrder) {
                            var sid = boardOrder[index];
                            if (boardsMapT[sid]) {
                                boardsMapT[sid] = 0;
                            } else {
                                flag = true;
                                boardOrder = boarads;
                                groupData.boardSequenceString = JSON.stringify(boarads);
                                groupNode.save(function (err, node) {
                                });
                                break;
                            }
                        }
                        if (!flag) {
                            for (var index in boardsMapT) {
                                if (boardsMapT[index] == 1) {
                                    boardOrder = boarads;
                                    groupData.boardSequenceString = JSON.stringify(boarads);
                                    groupNode.save(function (err, node) {
                                    });
                                    break;
                                }
                            }
                        }
                    } catch (e) {
                        boardOrder = boarads;
                        groupData.boardSequenceString = JSON.stringify(boarads);
                        groupNode.save(function (err, node) {
                        });
                    }
                } else {
                    boardOrder = boarads;
                    groupData.boardSequenceString = JSON.stringify(boarads);
                    groupNode.save(function (err, node) {
                    });
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块成功",
                    gid: gid,
                    boards: boardOrder,
                    boardsMap: boardsMap
                }), response);
            } else {
                console.log("length = 0");
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块失败",
                    "失败原因": "数据异常"
                }), response);
            }
        });
    }
}
shareManage.modifysquence = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var boardSequence = data.boardsequence;
    var targetPhones = data.targetphones;
    if (verifyEmpty.verifyEmpty(data, [gid, boardSequence, targetPhones]), response) {
        try {
            targetPhones = JSON.parse(targetPhones);
            modifyBoardSequence();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "修改版块顺序失败",
                "失败原因": "数据格式不正确"
            }), response);
        }
    }
    function modifyBoardSequence() {
        var query = [
            "MATCH (group:Group)",
            "WHERE group.gid={gid}",
            "RETURN group"
        ].join("\n");
        var params = {
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.log(error);
                ResponseData(JSON.stringify({
                    "提示信息": "修改版块顺序失败",
                    "失败原因": "群组不存在"
                }), response);
            } else if (results.length > 0) {
                var groupNode = results.pop().group;
                var groupData = groupNode.data;
                groupData.boardSequenceString = boardSequence;
                groupNode.save(function (err, node) {
                });
                ResponseData(JSON.stringify({
                    "提示信息": "修改版块顺序成功",
                    group: groupData
                }), response);
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                for (var index in targetPhones) {
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "group_updateboardsequence",
                        content: JSON.stringify({
                            type: "group_updateboardsequence",
                            phone: phone,
                            phoneTo: index,
                            gid: gid,
                            eid: eid,
                            time: time,
                            status: "success",
                            content: data.boardsequence
                        })
                    });
                    client.rpush(index, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, index, accessKey, "*", event);
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "修改版块顺序失败",
                    "失败原因": "群组不存在"
                }), response);
            }
        });
    }
}
shareManage.deleteboard = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var gid = data.gid;
    var sid = data.sid;
    var targetPhones = data.targetphones;
    if (verifyEmpty.verifyEmpty(data, [gid, sid, targetPhones]), response) {
        try {
            targetPhones = JSON.parse(targetPhones);
            deleteBoard();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "删除版块失败",
                "失败原因": "数据格式不正确"
            }), response);
        }
    }
    function deleteBoard() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(board:Shares)",
            "WHERE group.gid={gid} AND board.sid={sid}",
            "DELETE r",
            "RETURN group"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            sid: parseInt(sid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.log(error);
                ResponseData(JSON.stringify({
                    "提示信息": "删除版块失败",
                    "失败原因": "版块不存在"
                }), response);
            } else if (results.length > 0) {
                var groupNode = results.pop().group;
                var groupData = groupNode.data;
                var boardSequence = groupData.boardSequenceString;
                console.log(boardSequence);
                try {
                    boardSequence = JSON.parse(boardSequence);
                    var newboardSequence = [];
                    for (var i in boardSequence) {
                        var boardName = boardSequence[i];
                        if (boardName != sid) {
                            newboardSequence.push(boardName);
                        }
                    }
                    groupData.boardSequenceString = newboardSequence;
                    console.log(groupData.boardSequenceString);
                    groupNode.save(function (err, node) {
                    });
                } catch (e) {
                    console.log(e);
                }
                ResponseData(JSON.stringify({
                    "提示信息": "删除版块成功",
                    group: groupData
                }), response);
                var time = new Date().getTime();
                var eid = phone + "_" + time;
                for (var index in targetPhones) {
                    var event = JSON.stringify({
                        sendType: "event",
                        contentType: "group_deleteboard",
                        content: JSON.stringify({
                            type: "group_deleteboard",
                            phone: phone,
                            phoneTo: index,
                            gid: gid,
                            eid: eid,
                            time: time,
                            status: "success",
                            content: data.sid
                        })
                    });
                    client.rpush(index, event, function (err, reply) {
                        if (err) {
                            console.error("保存Event失败");
                        } else {
                            console.log("保存Event成功");
                        }
                    });
                    push.inform(phone, index, accessKey, "*", event);
                }
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "删除版块失败",
                    "失败原因": "版块不存在"
                }), response);
            }
        });
    }
}
shareManage.getboardshare = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var sid = data.sid;
    var gsid = data.gsid;
    var arr = [sid, gsid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getShareNode();
    }
    function getShareNode() {
        try {
            var query = [
                "MATCH (shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
                "WHERE shares.sid={sid} AND share.gsid={gsid}",
                "RETURN share"
            ].join("\n");
            var params = {
                sid: parseInt(sid),
                gsid: parseInt(gsid)
            };
            db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "获取群分享失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "获取群分享失败",
                        "失败原因": "群分享不存在"
                    }));
                    response.end();
                } else {
                    var shareData = results.pop().share.data;
                    var share = {
                        comments: JSON.parse(shareData.comments),
                        content: shareData.content,
                        praiseusers: shareData.praises ? JSON.parse(shareData.praises) : [],
                        gsid: shareData.gsid,
                        type: shareData.type,
                        time: shareData.time,
                        phone: shareData.phone,
                        totalScore: shareData.totalScore || 0,
                        scores: shareData.scores ? JSON.parse(shareData.scores) : {},
                        status: "sent"
                    };
                    response.write(JSON.stringify({
                        "提示信息": "获取群分享成功",
                        share: share
                    }));
                    response.end();
                }
            });
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "获取群分享失败",
                "失败原因": "群分享不存在"
            }));
            response.end();
        }
    }
}
shareManage.score = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var option = data.option;
    var gsid = data.gsid;
    var location = data.location;
    if (verifyEmpty.verifyEmpty(data, [phone, option, gsid], response)) {
        modifyShareNode();
    }

    function modifyShareNode() {
        var query = [
            "MATCH (share:Share)",
            "WHERE share.gsid={gsid}",
            "RETURN share"
        ].join("\n");
        var params = {
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    console.log(error);
                    ResponseData(JSON.stringify({
                        "提示信息": "评分失败",
                        "失败原因": "数据异常",
                        gsid: gsid
                    }), response);
                } else if (results.length == 0) {
                    ResponseData(JSON.stringify({
                        "提示信息": "评分失败",
                        "失败原因": "分享数据不存在",
                        gsid: gsid
                    }), response);
                } else {
                    var shareNode = results.pop().share;
                    var shareData = shareNode.data;
                    var totalScore = shareData.totalScore;
                    if (!totalScore) {
                        totalScore = 0;
                    }
                    var scores = shareData.scores;
                    if (!scores) {
                        scores = {};
                    } else {
                        scores = JSON.parse(scores);
                    }
                    var score = scores[phone];
                    if (!score) {
                        score = {
                            phone: phone,
                            time: new Date().getTime(),
                            positive: 0,
                            negative: 0,
                            remainNumber: 1
                        }
                        scores[phone] = score;
                    }
                    if (score.remainNumber > 0) {
                        score.remainNumber--;
                        if (option == true || option == "true") {
                            score.positive++;
                            totalScore++;
                            shareData.totalScore = totalScore;
                            shareData.scores = JSON.stringify(scores);
                            shareNode.save(function (err, node) {
                            });
                            var share = {
                                comments: JSON.parse(shareData.comments),
                                content: shareData.content,
                                gsid: shareData.gsid,
                                type: shareData.type,
                                time: shareData.time,
                                phone: shareData.phone,
                                totalScore: shareData.totalScore || 0,
                                scores: shareData.scores ? JSON.parse(shareData.scores) : {},
                                status: "sent"
                            };
                            if (location != null && location != undefined && location != "") {
                                if (share.totalScore <= -5) {
                                    deleteLbsShare(share, location);
                                } else {
                                    modifyLabsShare(share, location);
                                }
                            }
                            ResponseData(JSON.stringify({
                                "提示信息": "评分成功",
                                share: share
                            }), response);
                        } else if (option == false || option == "false") {
                            score.negative++;
                            totalScore--;
                            shareData.totalScore = totalScore;
                            shareData.scores = JSON.stringify(scores);
                            shareNode.save(function (err, node) {
                            });
                            var share = {
                                comments: JSON.parse(shareData.comments),
                                content: shareData.content,
                                gsid: shareData.gsid,
                                type: shareData.type,
                                time: shareData.time,
                                phone: shareData.phone,
                                totalScore: shareData.totalScore || 0,
                                scores: shareData.scores ? JSON.parse(shareData.scores) : {},
                                status: "sent"
                            };
                            if (location != null && location != undefined && location != "") {
                                if (share.totalScore <= -5) {
                                    deleteLbsShare(share, location);
                                } else {
                                    modifyLabsShare(share, location);
                                }
                            }
                            ResponseData(JSON.stringify({
                                "提示信息": "评分成功",
                                share: share
                            }), response);
                        } else {
                            ResponseData(JSON.stringify({
                                "提示信息": "评分失败",
                                "失败原因": "option参数不正确",
                                gsid: gsid
                            }), response);
                        }
                    } else {
                        ResponseData(JSON.stringify({
                            "提示信息": "评分失败",
                            "失败原因": "评分次数已达上限",
                            gsid: gsid
                        }), response);
                    }
                    function modifyLabsShare(share, location) {
                        try {
                            ajax.ajax({
                                type: "POST",
                                url: serverSetting.LBS_SHAERE_UPDATE,
                                data: {
                                    primaryKey: share.gsid,
                                    location: location,
                                    totalScore: share.totalScore,
                                    scores: JSON.stringify(share.scores)
                                }, success: function (info) {
                                    var info = JSON.parse(info);
                                    if (info["提示信息"] == "修改成功") {
                                        console.log("success--")
                                    } else {
                                        console.log("modify error--" + info.失败原因)
                                    }
                                }
                            });
                        } catch (e) {
                            console.log(e);
                        }
                    }

                    function deleteLbsShare(share, location) {
                        try {
                            ajax.ajax({
                                type: "POST",
                                url: serverSetting.LBS_SHAERE_DELETE,
                                data: {
                                    primaryKey: share.gsid,
                                    location: location
                                }, success: function (info) {
                                    var info = JSON.parse(info);
                                    if (info.提示信息 == "删除成功") {
                                        console.log("success--")
                                    } else {
                                        console.log("delete error--")
                                    }
                                }
                            });
                        } catch (e) {
                            console.log(e);
                        }
                    }
                }
            }
        )
    }
}

//处理数据库数据
//getGroups();
function getGroups() {
    var query = [
        "MATCH (group:Group)",
        "RETURN group"
    ].join("\n");
    var params = {};
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
        } else {
            var groupsMap = {};
            var i = 0;
            for (var index in results) {
                i++;
                var groupData = results[index].group.data;
                groupsMap[groupData.gid] = 1;
            }
            console.log("共有群组:" + i);
            getGroupShares(groupsMap);
        }
    });
}

function getGroupShares(groups) {
    var query = [
        "MATCH (group:Group)-->(Shares:Shares)",
        "RETURN group"
    ].join("\n");
    var params = {};
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
        } else {
            var i = 0;
            for (var index in results) {
                i++;
                var groupData = results[index].group.data;
                groups[groupData.gid] = 0;
            }
            console.log("已创建分享:" + i);
            var repares = [];
            for (var index in groups) {
                if (groups[index] == 1) {
                    repares.push(parseInt(index));
                }
            }
            createGroupShares(repares);
        }
    });
}

function createGroupShares(groups) {
    var query = [
        "MATCH (group:Group)",
        "WHERE group.gid IN {groups}",
        "CREATE UNIQUE group-[r:SHARE]->(shares:Shares{shares})",
        "RETURN shares"
    ].join("\n");
    var params = {
        groups: groups,
        shares: {
            nodeType: "Shares"
        }
    };
    console.log("A:" + typeof groups[0]);
    console.log("需要创建:" + groups);
    console.log("需要创建个数:" + groups.length);
    db.query(query, params, function (error, results) {
        if (error) {
            console.log(error);
        } else {
            var res = [];
            for (var index in results) {
                var sharesData = results[index].shares.data;
                res.push(sharesData.gid);
            }
            console.log("刚创建:" + res);
            console.log("刚创建个数:" + res.length);
        }
    });
}

//getLbsAllShares();
function getLbsAllShares() {
    var query = [
        "MATCH (shares:Shares)-->(share:Share)",
        "WHERE shares.sid={sid}",
        "RETURN share"
    ].join("\n")
    var params = {};
    db.query(query, params, function (error, results) {
        if (error) {
            console.error(error);
        } else if (results.length == 0) {
            console.log("无数据");
        } else {
            for (var index in results) {
                var shareData = results[index].share.data;

            }
        }
    });
}
function createLbsShare(shareData) {
    try {
        ajax.ajax({
            type: "POST",
            url: serverSetting.LBS_CREATE,
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            data: {
                location: location,
                primaryKey: shareData.gsid,
                data: JSON.stringify({
                    nickName: encodeURI(shareData.nickName),
                    gsid: shareData.gsid,
                    phone: shareData.phone,
                    head: shareData.head,
                    content: encodeURI(shareData.content),
                    totalScore: shareData.totalScore,
                    time: shareData.time,
                    scores: "{}"
                })
            }, success: function (info) {
                var info = JSON.parse(info);
                if (info.提示信息 == "创建成功") {
                    console.log("success--")
                } else {
                    console.log("error--create")
                }
            }
        });
    } catch (e) {
        console.log(e);
        return;
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

module.exports = shareManage;