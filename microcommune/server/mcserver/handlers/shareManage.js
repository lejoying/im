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
                    "失败原因": "数据格式不正确"
                }));
                response.end();
            }
        } catch (e) {
            console.error(e);
            response.write(JSON.stringify({
                "提示信息": "发布群分享失败",
                "失败原因": "数据格式不正确"
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
                    "失败原因": "数据异常"
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
            }
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                console.log("3");
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常"
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
            'WHERE group.gid={gid}',
            'CREATE shares-[r1:HAS_SHARE]->(share:Share{share})',
            'SET share.gsid=ID(share)',
            'RETURN group,share'
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            share: {
                phone: phone,
                praises: JSON.stringify([]),
                comments: JSON.stringify([]),
                type: message.type,
                content: message.content,
                time: new Date().getTime()
            }
        };
        db.query(query, params, function (error, results) {
            if (error) {
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常"
                }));
                response.end();
                console.error(error);
                return;
            } else if (results.length == 0) {
                console.log("2");
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            } else {
                var shareData = results.pop().share.data;
                console.log("1");
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
            "WHERE group.gid={gid}",
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
    var option = data.option;
    var arr = [gid, gsid, option];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (option == "true" || option == "false") {
            try {
                parseInt(gid);
                parseInt(gsid);
            } catch (e) {
                ResponseData(JSON.stringify({
                    "提示信息": "点赞群分享失败",
                    "失败原因": "数据格式不正确",
                    gid: gid,
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
                gsid: gsid
            }), response);
        }
    }
    function modifySharePraise() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid}",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
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
    var gsid = data.gsid;
    var nickName = data.nickName;
    var nickNameTo = data.nickNameTo;
    var head = data.head;
    var headTo = data.headTo;
    var contentType = data.contentType;
    var content = data.content;
    var arr = [gid, gsid, nickName, contentType, content];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        modifyShareComments();
    }
    function modifyShareComments() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid}",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
            gsid: parseInt(gsid)
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    ResponseData(JSON.stringify({
                        "提示信息": "评论群分享失败",
                        "失败原因": "数据异常",
                        gid: gid,
                        gsid: gsid
                    }), response);
                    console.error(error);
                    return;
                } else if (results.length == 0) {
                    ResponseData(JSON.stringify({
                        "提示信息": "评论群分享失败",
                        "失败原因": "消息不存在",
                        gid: gid,
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
                                gsid: gsid
                            }), response);
                            console.error(error);
                            return;
                        } else {
                            ResponseData(JSON.stringify({
                                "提示信息": "评论群分享成功",
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
            "WHERE group.gid={gid} AND share.gsid={gsid}",
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
            "WHERE group.gid={gid} AND share.gsid={gsid}",
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
    var gid = data.gid;
    var gsid = data.gsid;
    var arr = [gid, gsid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getShareNode();
    }
    function getShareNode() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid}",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: parseInt(gid),
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
                response.write(JSON.stringify({
                    "提示信息": "获取群分享成功",
                    shares: [shareData]
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
            "WHERE group.gid={gid} AND share.gsid={gsid}",
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
            "WHERE group.gid={gid}",
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
                        phone: shareData.phone
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
function ResponseData(responseContent, response) {
    response.writeHead(200, {
        "Content-Type": "application/json; charset=UTF-8",
        "Content-Length": Buffer.byteLength(responseContent, 'utf8')
    });
    response.write(responseContent);
    response.end();
}
module.exports = shareManage;