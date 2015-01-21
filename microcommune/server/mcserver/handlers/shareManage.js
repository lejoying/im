var serverSetting = root.globaldata.serverSetting;
var shareManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var verifyEmpty = require("./../lib/verifyParams.js");
/***************************************
 *     URL：/api2/share/sendshare
 ***************************************/
shareManage.sendshare = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var nickName = data.nickName;
    var messageStr = data.message;
    var gid = data.gid;
    var ogsid = data.ogsid;
    var message;
    var arr = [phone, messageStr, gid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            message = JSON.parse(messageStr);
            if (message.type == "imagetext" || message.type == "voicetext" || message.type == "vote") {
                checkShares(gid, message);
            } else {
                console.log("4");
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据格式不正确",
                    ogsid: ogsid,
                    gid: gid
                }));
                response.end();
            }
        } catch (e) {
            console.error(e);
            response.write(JSON.stringify({
                "提示信息": "发布群分享失败",
                "失败原因": "数据格式不正确",
                ogsid: ogsid,
                gid: gid
            }));
            response.end();
        }
    }
    function checkShares(gid, message) {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)",
            "WHERE group.gid={gid}",
            "RETURN group,shares"
        ].join("\n");
        var params = {
            gid: parseInt(gid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    gid: gid
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                createSharesTypeNode(gid, message);
            } else {
                saveShareNode(message);
            }
        });
    }

    function createSharesTypeNode(gid, message) {
        var query = [
            "MATCH (group:Group)",
            "WHERE group.gid={gid}",
            "CREATE UNIQUE group-[r:SHARE]->(shares:Shares{shares})",
            "RETURN group"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            shares: {
                name: "主版",
                nodeType: "Shares",
                type: "Main",
                gid: gid,
                status: "active",
                createTime: new Date().getTime()
            }
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    gid: gid
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                console.log("3");
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    gid: gid
                }));
                response.end();
            } else {
                saveShareNode(message);
            }
        });
    }

    function saveShareNode(message) {
        var query = [
            'MATCH (group:Group)-[r:SHARE]->(shares:Shares)',
            'WHERE group.gid={gid} AND shares.type="Main"',
            'CREATE shares-[r1:HAS_SHARE]->(share:Share{share})',
            'SET share.gsid=ID(share)',
            'RETURN group,share'
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            share: {
                phone: phone,
                nickName: nickName,
                praises: JSON.stringify([]),
                comments: JSON.stringify([]),
                type: message.type,
                nodeType: "Share",
                content: message.content,
                time: new Date().getTime()
            }
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    gid: gid
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                console.log("发布群分享失败");
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常",
                    ogsid: ogsid,
                    gid: gid
                }));
                response.end();
            } else {
                var shareData = results.pop().share.data;
                console.log("发布群分享成功");
                response.write(JSON.stringify({
                    "提示信息": "发布群分享成功",
                    time: shareData.time,
                    ogsid: ogsid,
                    gsid: shareData.gsid,
                    gid: gid
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/share/getshares
 ***************************************/
shareManage.getshares = function (data, response) {
    response.asynchronous = 1;
    console.info(data);
    var gid = data.gid;
    var nowpage = (data.nowpage);
    var pagesize = (data.pagesize);
    var arr = [gid, nowpage, pagesize];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getSharesNodes();
    }
    function getSharesNodes() {
        var query = [
            "MATCH (group:Group)-[r1:SHARE]->(shares:Shares)-[r2:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND shares.type='Main'",
            "RETURN share",
            "ORDER BY share.time DESC",
            "SKIP {start}",
            "LIMIT {pagesize}"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            start: parseInt(nowpage) * parseInt(pagesize),
            pagesize: parseInt(pagesize)
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
                    "提示信息": "获取群分享成功",
                    gid: gid,
                    nowpage: nowpage,
                    shares: []
                }));
                response.end();
            } else {
                var shares = [];
                for (var index in results) {
                    var result = results[index];
//                    var accountData = result.account.data;
                    var shareData = result.share.data;
//                    shareData.nickName = accountData.nickName;
//                    shareData.head = accountData.head;
//                    shareData.sex = accountData.sex;
                    shares.push(shareData);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    gid: gid,
                    nowpage: nowpage,
                    shares: shares
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/share/addpraise
 ***************************************/
shareManage.addpraise = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var gid = data.gid;
    var gsid = data.gsid;
    var sid = data.sid;
    var option = data.option;
    var arr = [gid, sid, gsid, option];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (option == "true" || option == "false") {
            try {
                parseInt(gid);
                parseInt(sid);
                parseInt(gsid);
            } catch (e) {
                ResponseData(JSON.stringify({
                    "提示信息": "点赞群分享失败",
                    "失败原因": "数据格式不正确",
                    gid: gid,
                    sid: sid,
                    gsid: gsid
                }), response);
                return;
            }
            modifySharePraise();
        } else {
            ResponseData(JSON.stringify({
                "提示信息": "点赞群分享失败",
                "失败原因": "数据格式不正确",
                gid: gid,
                sid: sid,
                gsid: gsid
            }), response);
        }
    }
    function modifySharePraise() {
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
                        "提示信息": "点赞群分享失败",
                        "失败原因": "数据异常",
                        gid: gid,
                        gsid: gsid
                    }), response);
                    console.error(error);
                    return;
                } else if (results.length == 0) {
                    ResponseData(JSON.stringify({
                        "提示信息": "点赞群分享失败",
                        "失败原因": "消息不存在",
                        gid: gid,
                        gsid: gsid
                    }), response);
                } else {
                    var shareNode = results.pop().share;
                    var shareData = shareNode.data;
                    var praise = shareData.praises;
                    var praiseJSON;
                    try {
                        praiseJSON = JSON.parse(praise);
                    } catch (e) {
                        praiseJSON = [];
                    }
                    if (option == "true") {
                        praiseJSON.push(phone);
                    } else if (option == "false") {
                        var tempPraise = [];
                        for (var index in praiseJSON) {
                            if (praiseJSON[index] != phone) {
                                tempPraise.push(praiseJSON[index]);
                            }
                        }
                        praiseJSON = tempPraise;
                    }
                    shareData.praises = JSON.stringify(praiseJSON);
                    shareNode.save(function (error, node) {
                        if (error) {
                            ResponseData(JSON.stringify({
                                "提示信息": "点赞群分享失败",
                                "失败原因": "数据异常",
                                gid: gid,
                                gsid: gsid
                            }), response);
                            console.error(error);
                            return;
                        } else {
                            ResponseData(JSON.stringify({
                                "提示信息": "点赞群分享成功",
                                gid: gid,
                                gsid: gsid
                            }), response);
                        }
                    });
                }
            }
        );
    }
}
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
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        modifyShareComments();
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
                            ResponseData(JSON.stringify({
                                "提示信息": "评论群分享成功",
                                gid: gid,
                                sid: sid,
                                gsid: gsid
                            }), response);
                        }
                    });
                }
            }
        );
    }
}
/***************************************
 *     URL：/api2/share/delete
 ***************************************/
shareManage.delete = function (data, response) {
    response.asynchronous = 1;
    var gid = data.gid;
    var gsid = data.gsid;
    var arr = [gid, gsid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        deleteShareNode();
    }
    function deleteShareNode() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid} AND shares.type='Main'",
            "DELETE r1,share",
            "RETURN group"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
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
                response.write(JSON.stringify({
                    "提示信息": "删除群分享失败",
                    "失败原因": "群分享不存在"
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "删除群分享成功"
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/share/deletecomment
 ***************************************/
shareManage.deletecomment = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var commentPhone = data.commentphone;
    var atPhone = data.atphone;
    var gid = data.gid;
    var gsid = data.gsid;
    var arr = [commentPhone, atPhone, gid, gsid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {

    }
    function deleteComment() {
        var query = [
            "MACTH (group:Group)-[r:HAS_SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid} AND shares.type='Main'",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "删除评论失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                response.write(JSON.stringify({
                    "提示信息": "删除评论失败",
                    "失败原因": "群分享不存在"
                }));
                response.end();
            } else {
                var shareNode = results.pop().share;
                var shareData = shareNode.data;
                var comments = JSON.parse(shareData.comments);
                var newComments = [];
                for (var index in comments) {
                    var comment = comments[index];
                    if (comment.phone != commentPhone && comment.phoneto != atPhone) {
                        newComments.push(comment);
                    }
                }
                shareData.comments = JSON.stringify(newComments);
                shareNode.save(function (error, node) {
                    if (error) {
                        response.write(JSON.stringify({
                            "提示信息": "删除评论失败",
                            "失败原因": "数据异常"
                        }));
                        response.end();
                        console.error(error);
                        return;
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "删除评论成功"
                        }));
                        response.end();
                    }
                });
            }
        });
    }
}
/***************************************
 *     URL：/api2/share/getshare
 ***************************************/
shareManage.getshare = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var gid = data.gid;
    var gsid = data.gsid;
    var arr = [gid, gsid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getShareNode();
    }
    function getShareNode() {
        try {
            var query = [
                "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
                "WHERE group.gid={gid} AND share.gsid={gsid} AND shares.type='Main'",
                "RETURN share"
            ].join("\n");
            var params = {
                gid: parseInt(gid),
                gsid: parseInt(gsid)
            };
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "获取群分享失败",
                "失败原因": "群分享不存在"
            }));
            response.end();
        }
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
                    praiseusers: JSON.parse(shareData.praises),
                    gsid: shareData.gsid,
                    type: shareData.type,
                    time: shareData.time,
                    phone: shareData.phone,
                    status: "sent"
                };
                response.write(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    shares: [share]
                }));
                response.end();
            }
        });
    }
}
/***************************************
 *     URL：/api2/share/modifyvote
 ***************************************/
shareManage.modifyvote = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var gid = data.gid;
    var gsid = data.gsid;
    var option = data.operation;
    var vid = data.vid;
    var arr = [gid, gsid, option, vid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (option == "true" || option == "false") {
            modifyShareVote();
        } else {
            response.write(JSON.stringify({
                "提示信息": "投票失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
        }
    }
    function modifyShareVote() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid} AND shares.type='Main'",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "投票失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "投票失败",
                        "失败原因": "消息不存在"
                    }));
                    response.end();
                } else {
                    var shareNode = results.pop().share;
                    var shareData = shareNode.data;
                    var content = shareData.content;
                    var contentJson;
                    if (content != "" && content != null) {
                        try {
                            contentJson = JSON.parse(content);
                            var voteOptions = contentJson.options;
                            for (var i = 0; i < voteOptions.length; i++) {
                                var voteUsers = [];
                                var areadlyVoteUsers = voteOptions[i].voteusers;
                                for (var j = 0; j < areadlyVoteUsers.length; j++) {
                                    if (phone != areadlyVoteUsers[j]) {
                                        voteUsers.push(areadlyVoteUsers[j]);
                                    }
                                }
                                if (i == parseInt(vid) && "true" == option) {
                                    voteUsers.push(phone);
                                    console.log("------------");
                                }
                                voteOptions[i].voteusers = voteUsers;
                            }
                        } catch (e) {
                            contentJson = {};
                        }
                    }
                    shareData.content = JSON.stringify(contentJson);
                    shareNode.save(function (error, node) {
                        if (error) {
                            response.write(JSON.stringify({
                                "提示信息": "投票失败",
                                "失败原因": "数据异常"
                            }));
                            response.end();
                            console.error(error);
                            return;
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "投票成功"
                            }));
                            response.end();
                        }
                    });
                }
            }
        );
    }
}

/*******************************************************************************
 * * * * * * * * * * * * New Api * * * * * * * * * * * * * * * * * * * * * * * *
 *******************************************************************************/
shareManage.getgroupshares = function (data, response) {
    response.asynchronous = 1;
    console.info(data);
    var gid = data.gid;
    var nowpage = (data.nowpage);
    var pagesize = (data.pagesize);
    var arr = [gid, nowpage, pagesize];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getSharesNodes();
    }
    function getSharesNodes() {
        var query = [
            "MATCH (group:Group)-[r1:SHARE]->(shares:Shares)-[r2:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND shares.type='Main'",
            "RETURN share",
            "ORDER BY share.time DESC",
            "SKIP {start}",
            "LIMIT {pagesize}"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            start: parseInt(nowpage) * parseInt(pagesize),
            pagesize: parseInt(pagesize)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享失败",
                    "失败原因": "数据异常"
                }), response);
                console.error(error);
                return;
            } else if (results.length == 0) {
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    gid: gid,
                    nowpage: nowpage,
                    shares: {
                        shareMessagesOrder: [],
                        shareMessagesMap: {}
                    }
                }), response);
            } else {
                var sharesOrder = [];
                var sharesMap = {};
                for (var index in results) {
                    var result = results[index];
                    var shareData = result.share.data;
                    var share = {
                        comments: JSON.parse(shareData.comments),
                        content: shareData.content,
                        praiseusers: JSON.parse(shareData.praises),
                        gsid: shareData.gsid,
                        type: shareData.type,
                        time: shareData.time,
                        phone: shareData.phone,
                        status: "sent"
                    };
                    sharesOrder.push(share.gsid + "");
                    sharesMap[share.gsid] = share;
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    gid: gid,
                    nowpage: nowpage,
                    shares: {
                        shareMessagesOrder: sharesOrder,
                        shareMessagesMap: sharesMap
                    }
                }), response);
            }
        });
    }
}
shareManage.getusershares = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var phone = data.phone;
    var nowpage = data.nowpage;
    var pagesize = data.pagesize;
    var arr = [phone, nowpage, pagesize];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getUserShares();
    }
    function getUserShares() {
        var query = [
            "MATCH (group:Group)-->(shares:Shares)-->(share:Share)",
            "WHERE  share.phone={phone} AND shares.type='Main'",
            "RETURN group,share",
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
                    gid: gid,
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
                        var share = {
                            comments: JSON.parse(shareData.comments),
                            content: shareData.content,
                            praiseusers: JSON.parse(shareData.praises),
                            gsid: shareData.gsid,
                            type: shareData.type,
                            time: shareData.time,
                            phone: shareData.phone,
                            gid: groupData.gid,
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
    var message = data.message;
    //var type = data.type;
    //var content = data.content;
    var gid = data.gid; //unused
    var ogsid = data.ogsid;
    if (verifyEmpty.verifyEmpty(data, [gid, sid, phone, ogsid, nickName, message], response)) {
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
        var params = {
            sid: parseInt(sid),
            share: {
                phone: phone,
                nickName: nickName,
                praises: "[]",
                comments: "[]",
                type: message.type,
                nodeType: "Share",
                sid: sid,
                content: message.content,
                time: new Date().getTime()
            }
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
    }
}
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
                        praiseusers: JSON.parse(shareData.praises),
                        gsid: shareData.gsid,
                        type: shareData.type,
                        time: shareData.time,
                        phone: shareData.phone,
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
shareManage.getboard = function (data, response) {
    response.asynchronous = 1;
    var sid = data.sid;
    if (verifyEmpty.verifyEmpty(data, [sid], response)) {
        getBoardNode();
    }
    function getBoardNode() {
        var query = [
            "MATCH (board:Shares)",
            "WHERE board.sid={sid}",
            "RETURN board"
        ].join("\n");
        var params = {
            sid: parseInt(sid)
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块失败",
                    "失败原因": "数据异常"
                }), response);
            } else if (results.length > 0) {
                var boardData = results.pop().board.data;
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块成功",
                    board: boardData
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块失败",
                    "失败原因": "版块不存在"
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
shareManage.getboards = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    if (verifyEmpty.verifyEmpty(data, [phone], response)) {
        try {
            getBoardNodes();
        } catch (e) {
            ResponseData(JSON.stringify({
                "提示信息": "获取版块失败",
                "失败原因": "数据格式不正确"
            }), response);
        }
    }
    function getBoardNodes() {
        var query = [
            "MATCH (account:Account)<-[r:HAS_MEMBER]-(group:Group)-[r2:SHARE]->(board:Shares)",
            "WHERE account.phone={phone}",
            "RETURN group,board"
        ].join("\n");
        var params = {
            phone: phone
        };
        db.query(query, params, function (error, results) {
            if (error) {
                console.error(error);
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块失败",
                    "失败原因": "数据异常"
                }), response);
            } else if (results.length > 0) {
                var groupBoard = {};
                var saveBoard = {};
                var boardsMap = {};
                for (var index in results) {
                    var groupData = results[index].group.data;
                    var boardData = results[index].board.data;
                    boardsMap[boardData.sid] = boardData;
                    if (groupData.boardSequenceString && groupBoard[groupData.gid] == null) {
                        if (saveBoard[groupData.gid]) {
                            saveBoard[groupData.gid].order.push(boardData.sid);
                        } else {
                            try {
                                groupBoard[groupData.gid] = JSON.parse(groupData.boardSequenceString);
                            } catch (e) {
                                saveBoard[groupData.gid] = {
                                    groupNode: results[index].group,
                                    order: [boardData.sid]
                                }
                            }
                        }
                    } else {
                        if (saveBoard[groupData.gid]) {
                            saveBoard[groupData.gid].order.push(boardData.sid);
                        } else {
                            saveBoard[groupData.gid] = {
                                groupNode: results[index].group,
                                order: [boardData.sid]
                            }
                        }
                    }
                }
                for (var index in saveBoard) {
                    var groupNode = saveBoard[index].groupNode;
                    var groupData = groupNode.data;
                    var order = saveBoard[index].order;
                    groupBoard[groupData.gid] = order;
                    groupData.boardSequenceString = JSON.stringify(order);
                    groupNode.save(function (err, node) {
                    });
                }
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块成功",
                    groupBoard: groupBoard,
                    boardsMap: boardsMap
                }), response);
            } else {
                ResponseData(JSON.stringify({
                    "提示信息": "获取版块成功",
                    groupBoard: {},
                    boardsMap: {}
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
        } catch (e) {
            response.write(JSON.stringify({
                "提示信息": "获取群分享失败",
                "失败原因": "群分享不存在"
            }));
            response.end();
        }
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
                    praiseusers: JSON.parse(shareData.praises),
                    gsid: shareData.gsid,
                    type: shareData.type,
                    time: shareData.time,
                    phone: shareData.phone,
                    status: "sent"
                };
                response.write(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    share: share
                }));
                response.end();
            }
        });
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


function ResponseData(responseContent, response) {
    response.writeHead(200, {
        "Content-Type": "application/json; charset=UTF-8",
        "Content-Length": Buffer.byteLength(responseContent, 'utf8')
    });
    response.write(responseContent);
    response.end();
}

module.exports = shareManage;