var serverSetting = root.globaldata.serverSetting;
var webcodeManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var sha1 = require('../tools/sha1.js');
var push = require('../lib/push.js');
/***************************************
 *     URL：/api2/webcode/webcodelogin
 ***************************************/
webcodeManage.webcodelogin = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var sessionID = data.sessionID;

    push.notifywebcodelogin(phone, accessKey, sessionID, function (data) {
        if (JSON.parse(data).information == "notifywebcodelogin success") {
            response.write(JSON.stringify({
                "提示信息": "二维码登陆成功",
                "phone": phone
            }));
            response.end();
        } else if (JSON.parse(data).information == "notifywebcodelogin faild") {
            response.write(JSON.stringify({
                "提示信息": "二维码登陆失败",
                "失败原因": "客户端连接不存在"
            }));
            JSON.stringify()
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "二维码登陆失败",
                "失败原因": "数据异常"
            }));
            response.end();
        }
    });
}
module.exports = webcodeManage;
