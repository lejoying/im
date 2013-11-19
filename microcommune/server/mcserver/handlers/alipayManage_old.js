var serverSetting = root.globaldata.serverSetting;
var alipayManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var session = require('./session.js');
var sha1 = require('../tools/sha1.js');
/***************************************
 *     URL：/api2/alipay/alipayto
 ***************************************/
alipayManage.alipayto = function (data, response) {
    var out_trade_no = new Date().getTime() + "";
    var subject = "支付宝subject" + new Date().getTime();
    var sParaTemp = [];
    sParaTemp.push(["payment_type", "1"]);
    sParaTemp.push(["out_trade_no", out_trade_no]);
    sParaTemp.push(["subject", subject]);
    sParaTemp.push(["logistics_type", "POST"]);
    sParaTemp.push(["logistics_fee", "0.1"]);
    sParaTemp.push(["logistics_payment", "BUYER_PAY"]);
    sParaTemp.push(["price", "0.1"]);
    sParaTemp.push(["quantity", "1"]);
    var trade_create_by_buyer = function (sParaTemp) {
        //增加基本配置
        sParaTemp.push(["service", "trade_create_by_buyer"]);
        sParaTemp.push(["partner", AlipayConfig.partner]);
        sParaTemp.push(["return_url", AlipayConfig.return_url]);
//        sParaTemp.push(["notify_url", AlipayConfig.notify_url]);
        sParaTemp.push(["seller_email", AlipayConfig.seller_email]);
        sParaTemp.push(["_input_charset", AlipayConfig.input_charset]);

        var buildURL = function (sParaTemp) {
            var buildRequestPara = function (sParaTemp) {
                var sPara = [];
                //除去数组中的空值和签名参数
                for (var i1 = 0; i1 < sParaTemp.length; i1++) {
                    var value = sParaTemp[i1];
//                    console.log(value);
                    if (value[1] == null || value[1] == "" || value[0] == "sign"
                        || value[0] == "sign_type") {
                        continue;
                    }
                    sPara.push(value);
                }
                sPara.sort();
                //生成签名结果
                var prestr = "";
                //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                for (var i2 = 0; i2 < sPara.length; i2++) {
                    var obj = sPara[i2];
                    if (i2 == sPara.length - 1) {
                        prestr = prestr + obj[0] + "=" + obj[1];
                    } else {
                        prestr = prestr + obj[0] + "=" + obj[1] + "&";
                    }

                }
                prestr = prestr + AlipayConfig.key;
                console.log(prestr);
                var crypto = require('crypto');
                var mysign = crypto.createHash('md5').update(prestr).digest("hex");
//                console.log(crypto.createHash('md5').update(prestr)+"--");
//                console.log(crypto.createHash('md5').update(prestr).digest("hex")+"----");
                //签名结果与签名方式加入请求提交参数组中
                sPara.push(["sign", mysign]);
                sPara.push(["sign_type", AlipayConfig.sign_type]);

                return sPara;
            };
            //待请求参数数组
            var sPara = buildRequestPara(sParaTemp);
            var path = AlipayConfig.ALIPAY_PATH;


            for (var i3 = 0; i3 < sPara.length; i3++) {
                var obj = sPara[i3];
                var name = obj[0];
                var value = obj[1];
                if (i3 < (sPara.length - 1)) {
                    path = path + name + "=" + value + "&";
                } else {
                    path = path + name + "=" + value;
                }
            }
            return path.toString();
        };

        return buildURL(sParaTemp);
    };
    //构造函数，生成请求URL
    var sURL = trade_create_by_buyer(sParaTemp);
    console.log(sURL);
//    向支付宝网关发出请求
    requestUrl(AlipayConfig.ALIPAY_HOST,"im.lejoying.com",function(data){
        console.log(data);
    });
    console.log("https://" + AlipayConfig.ALIPAY_HOST + "/" + encodeURI(sURL));

}
var AlipayConfig = {
    partner:"2088002080191054",
    key:"jh9ovfio2nu4j71l73kne03rz6s2iaev",
    seller_email:"coolspan@sina.cn",
    notify_url:"http://im.lejoying.com",
    return_url:"http://im.lejoying.com",
//      支付宝通知验证地址
    ALIPAY_HOST: "mapi.alipay.com",
    HTTPS_VERIFY_PATH: "/gateway.do?service=notify_verify&",
    ALIPAY_PATH: "gateway.do?",
//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

// 调试用，创建TXT日志路径
    log_path: "~/alipay_log_.txt",

// 字符编码格式 目前支持 gbk 或 utf-8
    input_charset: "utf-8",

// 签名方式 不需修改
    sign_type: "MD5"
};
var AlipayNotify = {
    verity: function (params, callback) {
        var mysign = getMySign(params);
        var sign = params["sign"] ? params["sign"] : "";
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

        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "responseTxt=" + responseTxt + "\n notify_url_log:sign=" + sign + "&mysign="
        //              + mysign + "\n 返回参数：" + AlipayCore.createLinkString(params);
        //AlipayCore.logResult(sWord);


        //验证
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //mysign与sign不等，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
    }
};
//获取验证码
var getMySign = function (params) {
    var sPara = [];//转换为数组利于排序 除去空值和签名参数
    if (!params) return null;
    for (var key in params) {
        if ((!params[key]) || params[key] == "sign" || params[key] == "sign_type") {
            console.log('null:' + key);
            continue;
        }
        ;
        sPara.push([key, params[key]]);
    }
    sPara.sort();
    //生成签名结果
    var prestr = "";
    //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
    for (var i2 = 0; i2 < sPara.length; i2++) {
        var obj = sPara[i2];
        if (i2 == sPara.length - 1) {
            prestr = prestr + obj[0] + "=" + obj[1];
        } else {
            prestr = prestr + obj[0] + "=" + obj[1] + "&";
        }

    }
    prestr = prestr + AlipayConfig.key; //把拼接后的字符串再与安全校验码直接连接起来
    //body=Hello&buyer_email=13758698870&buyer_id=2088002007013600&discount=-5&extra_common_param=你好，这是测试商户的广告。&gmt_close=2008-10-22 20:49:46&gmt_create=2008-10-22 20:49:31&gmt_payment=2008-10-22 20:49:50&gmt_refund=2008-10-29 19:38:25&is_total_fee_adjust=N&notify_id=70fec0c2730b27528665af4517c27b95&notify_time=2009-08-12 11:08:32&notify_type=交易状态同步通知(trade_status_sync)&out_trade_no=3618810634349901&payment_type=1&price=10.00&quantity=1&refund_status=REFUND_SUCCESS&seller_email=chao.chenc1@alipay.com&seller_id=2088002007018916&sign=_p_w_l_h_j0b_gd_aejia7n_ko4_m%2Fu_w_jd3_nx_s_k_mxus9_hoxg_y_r_lunli_pmma29_t_q%3D%3D&sign_type=DSA&subject=iphone手机&total_fee=10.00&trade_no=2008102203208746&trade_status=TRADE_FINISHED&use_coupon=N

    var crypto = require('crypto');
    return crypto.createHash('md5').update(prestr).digest("hex");
};

var requestUrl = function (host, path, callback) {
    var https = require('https');

    var options = {
        host: host,
        port: 443,
        path: path,
        method: 'POST'
    };

    var req = https.request(options, function (res) {
        console.log("statusCode: ", res.statusCode);
        console.log("headers: ", res.headers);

        res.on('data', function (d) {
            callback(d);
        });
    });
    req.end();

    req.on('error', function (e) {
        console.error(e);
    });
};
module.exports = alipayManage;