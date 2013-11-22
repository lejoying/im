var AlipayConfig = {
    partner: "2088002080191054",
    key: "jh9ovfio2nu4j71l73kne03rz6s2iaev",
    seller_email: "wsds888@163.com",
    notify_url: "http://im.lejoying.com/alipay/paynotify?",
    return_url: "http://weihu.lejoying.com",
    ALIPAY_HOST: "mapi.alipay.com",
    HTTPS_VERIFY_PATH: "/gateway.do?service=notify_verify&",
    ALIPAY_PATH: "gateway.do?",
    log_path: "~/alipay_log_.txt",
    input_charset: "UTF-8",
    sign_type: "MD5"
};
var fs = require('fs');
var str = "";
var AlipayNotify = {
    verity: function (params, callback) {
        var mysign = getMySign(params);
        console.log(str);
        var sign = params["sign"] ? params["sign"] : "";
        console.log(mysign + "------" + sign);
        if (mysign == sign) {
            var responseTxt = "true";
            if (params["notify_id"]) {
                //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

                var partner = AlipayConfig.partner;
                var veryfy_path = AlipayConfig.HTTPS_VERIFY_PATH + "partner=" + partner + "&notify_id=" + params["notify_id"];

                requestUrl(AlipayConfig.ALIPAY_HOST, veryfy_path, function (responseTxt) {
                    if (responseTxt) {
                        callback(true);
                    } else {
                        callback(false);
                    }
                });
            }
        } else {
            callback(false);
        }
//        写日志记录（若要调试，请取消下面两行注释）
        var sWord = "responseTxt=" + responseTxt + "\n notify_url_log:sign=" + sign + "&mysign="
            + mysign + "\n 返回参数：" + createLinkstring(params);
        logResult(sWord);
    }
};
var logResult = function (word) {
    word = word || '';
    var str = "执行日期：" + Date().toString() + "\n" + word + "\n";
    fs.appendFile('../notify_url_access.log', str);
};
var createLinkstring = function (para) {
    var ls = '';
    for (var k in para) {
        ls = ls + k + '=' + para[k] + '&';
    }
    ls = ls.substring(0, ls.length - 1);
    return ls;
};
var getMySign = function (params) {
    var sPara = [];
    if (!params) return null;
    for (var key in params) {
        if (params[key] == null || params[key] == "" || key == "sign" || key == "sign_type") {
            continue;
        }
        sPara.push([key, params[key]]);
    }
    sPara.sort();
    var prestr = "";
    for (var i2 = 0; i2 < sPara.length; i2++) {
        var obj = sPara[i2];
        if (i2 == sPara.length - 1) {
            prestr = prestr + obj[0] + "=" + obj[1];
        } else {
            prestr = prestr + obj[0] + "=" + obj[1] + "&";
        }
    }
    prestr = prestr + AlipayConfig.key;
    var crypto = require('crypto');
    str = prestr;
    return crypto.createHash('md5').update(prestr, 'utf8').digest("hex");
};
var getAlipayUrl = function (sPara) {
    var path = AlipayConfig.ALIPAY_PATH;
    for (var index in sPara) {
        var value = sPara[index];
        path = path + index + "=" + value + "&";
    }
    path = path.substr(0, path.length - 1);
    return path.toString();
};
var requestUrl = function (host, path, callback) {
    var https = require('https');
    var options = {
        host: host,
        port: 443,
        path: path,
        method: 'GET'
    };
    var req = https.request(options, function (res) {
//        console.log("statusCode: ", res.statusCode);
        res.on('data', function (d) {
            callback(d);
        });
    });
    req.end();
    req.on('error', function (e) {
        console.error(e);
    });
};
exports.alipayto = function (req, res) {
    var sParaTemp = {
        service: "trade_create_by_buyer",
        partner: AlipayConfig.partner,
        seller_email: AlipayConfig.seller_email,
        _input_charset: AlipayConfig.input_charset,
        payment_type: "1",
        out_trade_no: new Date().getTime() + "",
        subject: "充值服务",
        logistics_type: "POST",
        logistics_fee: "0",
        logistics_payment: "BUYER_PAY",
        price: "0.01",
        quantity: "1",
        return_url: AlipayConfig.return_url,
        notify_url: AlipayConfig.notify_url
    };
    var mysign = getMySign(sParaTemp);
    sParaTemp["sign"] = mysign;
    sParaTemp["sign_type"] = AlipayConfig.sign_type;
    var sURL = getAlipayUrl(sParaTemp);
//    console.log("https://" + AlipayConfig.ALIPAY_HOST + "/" + sURL);
    /*requestUrl(AlipayConfig.ALIPAY_HOST, "http://www.lejoying.com", function (data) {
     });*/
    res.redirect("https://" + AlipayConfig.ALIPAY_HOST + "/" + sURL);
};
var count = 0;
var maxData = 2 * 1024 * 1024; //prevent mass post data
var querystring = require('querystring');
function getPostData(request, response, next) {
    if (request.method == "POST") {
        response.asynchronous = 1;
        var postData = '';
        request.on('data',function (chunk) {
            postData += chunk;
            if (postData.length > maxData) {
                pstData = '';
                this.pause;
                response.writeHead(413);
                response.end('Request too large');
            }
        }).on('end', function () {
                if (!postData) {
                    response.end();
                    return;
                }
                var postDataObject = querystring.parse(postData);
                next(postDataObject);

            });
    }
    else {
        next(null);
    }
}

exports.paynotify = function (req, res) {
    getPostData(req, res, function (data) {
        console.log("支付宝交易号:" + data['trade_no'] + "买家email:" + data['buyer_email'] + "---" + count++);
        var trade_status = data['trade_status'];
        AlipayNotify.verity(data, function (result) {
            if (result) {
                if (trade_status == "WAIT_BUYER_PAY") {
                    //等待买家付款
                } else if (trade_status == "WAIT_SELLER_SEND_GOODS") {
                    //等待卖家发货
                } else if (trade_status == "WAIT_BUYER_CONFIRM_GOODS") {
                    //卖家已发货
                } else if (trade_status == "TRADE_FINISHED") {
                    //交易成功交易
                } else if (trade_status == "TRADE_CLOSED") {
                    //交易中途关闭
                }
                console.log("success");
                res.end("success");
            } else {
                console.log("fail");
                res.end("fail");
            }
        });
    });
};