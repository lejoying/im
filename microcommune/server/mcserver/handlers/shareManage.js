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
    var phone = data.phone;
    var messageStr = data.message;
    var gid = data.gid;
    var message;
    var arr = [phone, messageStr, gid];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        try {
            message = JSON.parse(messageStr);
            if (message.type == "imagetext" || message.type == "voicetext" || message.type == "vote") {
                checkShares(gid, message);
            } else {
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
            "MACTH (group:Group)-[r:SHARE]->(shares:Shares)",
            "WHERE group.gid={gid}",
            "RETURN group,shares"
        ].join("\n");
        var params = {
            gid: gid
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
            "RETURN shares"
        ].join("\n");
        var params = {
            gid: gid,
            shares: {}
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
            gid: gid,
            share: {
                phone: phone,
                praises: JSON.stringify([]),
                comments: JSON.stringify([]),
                type: message.type,
                content: message.content
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
                response.write(JSON.stringify({
                    "提示信息": "发布群分享失败",
                    "失败原因": "数据异常"
                }));
                response.end();
            } else {
                response.write(JSON.stringify({
                    "提示信息": "发布群分享成功",
                    time: new Date().getTime()
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
    var gid = data.gid;
    var nowpage = data.nowpage;
    var pagesize = data.pagesize;
    var arr = [gid, nowpage, pagesize];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        getSharesNodes();
    }
    function getSharesNodes() {
        var query = [
            "MACTH (account:Account)<-[r:HAS_MEMBER]-(group:Group)-[r1:SHARE]->(shares:Shares)-[r2:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND account.phone=share.phone",
            "ORDER BY share.time desc",
            "SKIP {start} LIMIT {pagesize}",
            "RETURN account,share"
        ].join("\n");
        var params = {
            gid: gid,
            start: nowpage * pagesize,
            pagesize: pagesize
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
                    shares: []
                }));
                response.end();
            } else {
                var shares = [];
                for (var index in results) {
                    var result = results[index];
                    var accountData = result.account.data;
                    var shareData = result.share.data;
                    shareData.nickName = accountData.nickName;
                    shareData.head = accountData.head;
                    shareData.sex = accountData.sex;
                    shares.push(shareData);
                }
                response.write(JSON.stringify({
                    "提示信息": "获取群分享成功",
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
    var phone = data.phone;
    var gid = data.gid;
    var gsid = data.gsid;
    var option = data.option;
    var arr = [gid, gsid, option];
    if (verifyEmpty.verifyEmpty(data, arr, response)) {
        if (option == "true" || option == "false") {
            modifySharePraise();
        } else {
            response.write(JSON.stringify({
                "提示信息": "点赞群分享失败",
                "失败原因": "数据格式不正确"
            }));
            response.end();
        }
    }
    function modifySharePraise() {
        var query = [
            "MATCH (group:Group)-[r:SHARE]->(shares:Shares)-[r1:HAS_SHARE]->(share:Share)",
            "WHERE group.gid={gid} AND share.gsid={gsid}",
            "RETURN share"
        ].join("\n");
        var params = {
            gid: gid,
            gsid: gsid
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "点赞群分享失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "点赞群分享失败",
                        "失败原因": "消息不存在"
                    }));
                    response.end();
                } else {
                    var shareNode = results.share.pop();
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
                            response.write(JSON.stringify({
                                "提示信息": "点赞群分享失败",
                                "失败原因": "数据异常"
                            }));
                            response.end();
                            console.error(error);
                            return;
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "点赞群分享成功"
                            }));
                            response.end();
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
    var head = data.head;
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
            gid: gid,
            gsid: gsid
        };
        db.query(query, params, function (error, results) {
                if (error) {
                    response.write(JSON.stringify({
                        "提示信息": "评论群分享失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                    console.error(error);
                    return;
                } else if (results.length == 0) {
                    response.write(JSON.stringify({
                        "提示信息": "评论群分享失败",
                        "失败原因": "消息不存在"
                    }));
                    response.end();
                } else {
                    var shareNode = results.share.pop();
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
                        phoneto: phoneTo,
                        contentType: contentType,
                        content: content
                    };
                    commentsJSON.push(comment);
                    shareData.comments = JSON.stringify(commentsJSON);
                    shareNode.save(function (error, node) {
                        if (error) {
                            response.write(JSON.stringify({
                                "提示信息": "评论群分享失败",
                                "失败原因": "数据异常"
                            }));
                            response.end();
                            console.error(error);
                            return;
                        } else {
                            response.write(JSON.stringify({
                                "提示信息": "评论群分享成功"
                            }));
                            response.end();
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
            gid: gid,
            gsid: gsid
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
            gid: gid,
            gsid: gsid
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
            gid: gid,
            gsid: gsid
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
module.exports = shareManage;