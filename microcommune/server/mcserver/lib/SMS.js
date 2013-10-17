var sms = {};
var https = require('https');
var md5 = function () {
    return require('crypto').createHash('md5')
};

var ACCOUNT_ID = '0000000041b645530141b9d1b56c0054'; //账户ID
var ACCOUNT_TOKEN = 'a84c27dd1c3646e280ee7b9cdd4041f8'; //账户TOKEN
var APP_ID = 'aaf98fda41b64df00141b9df8c16003d'; //APP的ID
var SUB_ID = '8a48b72b41bf81f60141c4c72b7c0274'; //子账户ID

//计算签名和头
var get_sign = function () {
    var now = new Date();
    var dt = '';
    var a = null;
    a = (now.getFullYear());
    a < 10 ? dt += '0' + a : dt += a;
    a = (now.getMonth() + 1);
    a < 10 ? dt += '0' + a : dt += a;
    a = (now.getDate());
    a < 10 ? dt += '0' + a : dt += a;
    a = (now.getHours());
    a < 10 ? dt += '0' + a : dt += a;
    a = (now.getMinutes());
    a < 10 ? dt += '0' + a : dt += a;
    a = (now.getSeconds());
    a < 10 ? dt += '0' + a : dt += a;

    var sign = md5().update(ACCOUNT_ID + ACCOUNT_TOKEN + dt).digest('hex').toUpperCase();
    var header = new Buffer(ACCOUNT_ID + ':' + dt).toString('base64');

    return {sign: sign, header: header};
}

//发送信息
var send_msg = function (to, msg) {
    var sign = get_sign();
    var opt = {
        hostname: 'app.cloopen.com',
        port: 8883,
        path: '/2013-03-22/Accounts/%s/SMS/Messages'.replace('%s', ACCOUNT_ID) + '?sig=' + sign.sign,
        method: 'POST',
        headers: {
            'Authorization': sign.header,
            'Content-Type': 'application/json;charset=utf-8',
            'Accept': 'application/json'
        },
        rejectUnauthorized: false,
        requestCert: true,
        agent: false
    }

    var raw = JSON.stringify({
        appId: APP_ID,
        to: to,
        body: msg,
        msgType: 0,
        subAccountSid: SUB_ID
    });
    /*var raw = '<?xml version="1.0" encoding="utf-8"?>'
        + '<SMSMessage>'
        + '<appId>%(app_id)s</appId>'
        + '<to>%(to)s</to>'
        + '<body>%(msg)s</body>'
        + '<msgType>0</msgType>'
        + '<subAccountSid>%(sub_id)s</subAccountSid>'
        + '</SMSMessage>';
    raw = raw.replace('%(app_id)s', APP_ID).replace('%(sub_id)s', SUB_ID);
    raw = raw.replace('%(to)s', to).replace('%(msg)s', msg);
*/
    return {opt: opt, body: raw};
}


//创建子账户
var create_sub = function (name) {
    var sign = get_sign();
    var opt = {
        protocol: "https:",
        hostname: 'app.cloopen.com',
        port: 8883,
        path: '/2013-03-22/Accounts/%s/SubAccounts'.replace('%s', ACCOUNT_ID) + '?sig=' + sign.sign,
        method: 'POST',
        headers: {
            'Authorization': sign.header,
            'Content-Type': 'application/json;charset=utf-8',
            'Accept': 'application/json'
        },
        rejectUnauthorized: false,
        requestCert: true,
        agent: false
    }

    var raw = JSON.stringify({
        appId: APP_ID,
        friendlyName: name,
        accountSid: ACCOUNT_ID
    });
    /*var raw = '<?xml version="1.0" encoding="utf-8"?>'
        + '<SubAccount>'
        + '<appId>%(app_id)s</appId>'
        + '<friendlyName>%(name)s</friendlyName>'
        + '<accountSid>%(account_id)s</accountSid>'
        + '</SubAccount>';
    raw = raw.replace('%(app_id)s', APP_ID);
    raw = raw.replace('%(account_id)s', ACCOUNT_ID);
    raw = raw.replace('%(name)s', name);
*/
    return {opt: opt, body: raw};
}


//发出请求
var request = function (obj) {
    var req = https.request(obj.opt, function (res) {
        var buffer = '';
        res.on('data', function (chunk) {
            buffer += chunk;
        });

        res.on('end', function () {
            console.log(res.statusCode);
            console.log(buffer);
        });
    });

    if (obj.opt.method == 'POST') {
        req.write(obj.body || '');
    }

    req.end();
}

/*if (require.main === module) {
 var obj = send_msg('15210721344', '中文');
 var obj = create_sub('965266509@qq.com');
 request(obj);
 }*/
sms.sendMsg = function(to, msg){
    var obj = send_msg(to, msg);
    request(obj);
}
sms.createsub = function(email){
    var obj = create_sub(email);
    request(obj);
}
module.exports = sms;