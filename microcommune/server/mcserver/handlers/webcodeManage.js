var serverSetting = root.globaldata.serverSetting;
var webcodeManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var session = require('./session.js');
var sha1 = require('../tools/sha1.js');

/***************************************
 *     URL：/api2/webcode/webcodelogin
 ***************************************/
webcodeManage.webcodelogin = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var sessionID = data.sessionID;
    var sessionResponse = session.sessionPool[sessionID];
    if (sessionResponse != null && sessionResponse != undefined) {
        var accessKey = phone + new Date();
        sessionResponse.write(JSON.stringify({
            "提示信息": "web端二维码登录成功",
            "phone": phone,
            "accessKey": sha1.hex_sha1(accessKey)
        }));
        sessionResponse.end();
        response.write(JSON.stringify({
            "提示信息": "二维码登录成功",
            "phone": phone
        }));
        response.end();
    } else {
        sessionResponse.write(JSON.stringify({
            "提示信息": "web端二维码登录失败",
            "失败原因": "数据异常"
        }));
        sessionResponse.end();
        response.write(JSON.stringify({
            "提示信息": "二维码登录失败",
            "失败原因": "客户端连接不存在"
        }));
        response.end();
    }
}
module.exports = webcodeManage;
