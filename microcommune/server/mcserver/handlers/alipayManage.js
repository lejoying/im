var serverSetting = root.globaldata.serverSetting;
var alipayManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var session = require('./session.js');
var sha1 = require('../tools/sha1.js');
var alipay = require('../alipay_config.js').alipay;
/***************************************
 *     URL：/api2/alipayserver/alipayto
 ***************************************/
alipayManage.alipayto = function (data, response) {
    var datas = {
        out_trade_no: new Date().getTime(),
        subject: "subject" + new Date().getTime(),
        price: 0.2,
        quantity: 2,
        logistics_fee: 0.3,
        logistics_type: "POST",
        logistics_payment: "BUYER_PAY"
    };

    alipay.trade_create_by_buyer(datas, response);
}
module.exports = alipayManage;