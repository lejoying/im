var serverSetting = root.globaldata.serverSetting;
var webcodeManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);

/***************************************
 *     URL：/api2/webcode/webcodelogin
 ***************************************/
webcodeManage.webcodelogin = function (data, response) {
    response.asynchronous = 1;
    var phone = data.phone;
    var accessKey = data.accessKey;
    var sessionID = data.sessionID;

}
module.exports = webcodeManage;
