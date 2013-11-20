var AlipayConfig = {
    partner: "2088002080191054",
    key: "jh9ovfio2nu4j71l73kne03rz6s2iaev",
    seller_email: "wsds888@163.com",
    notify_url: "http://im.lejoying.com",
    return_url: "http://im.lejoying.com",
    ALIPAY_HOST: "mapi.alipay.com",
    HTTPS_VERIFY_PATH: "/gateway.do?service=notify_verify&",
    ALIPAY_PATH: "gateway.do?",
    log_path: "~/alipay_log_.txt",
    input_charset: "UTF-8",
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
    //body=Hello&buyer_email=13758698870&buyer_id=2088002007013600&discount=-5&extra_common_param=你好，这是测试商户的广告。&gmt_close=2008-10-22 20:49:46&gmt_create=2008-10-22 20:49:31&gmt_payment=2008-10-22 20:49:50&gmt_refund=2008-10-29 19:38:25&is_total_fee_adjust=N&notify_id=70fec0c2730b27528665af4517c27b95&notify_time=2009-08-12 11:08:32&notify_type=交易状态同步通知(trade_status_sync)&out_trade_no=3618810634349901&payment_type=1&price=10.00&quantity=1&refund_status=REFUND_SUCCESS&seller_email=chao.chenc1@alipayserver.com&seller_id=2088002007018916&sign=_p_w_l_h_j0b_gd_aejia7n_ko4_m%2Fu_w_jd3_nx_s_k_mxus9_hoxg_y_r_lunli_pmma29_t_q%3D%3D&sign_type=DSA&subject=iphone手机&total_fee=10.00&trade_no=2008102203208746&trade_status=TRADE_FINISHED&use_coupon=N

    var crypto = require('crypto');
    return crypto.createHash('md5').update(prestr).digest("hex");
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

/**
 * 在应用中发送付款请求，替换掉构造form的应用
 * @param req
 * @param res
 */
exports.alipayto = function (req, res) {
    var out_trade_no = '20120708132324';
    //subject 汉字不能签名的问题
    var subject = "Pay Money";
    var sParaTemp = [];
    sParaTemp.push(["payment_type", "1"]);
    sParaTemp.push(["out_trade_no", out_trade_no]);
    sParaTemp.push(["subject", subject]);
    //    sParaTemp.push(["show_url", show_url]);
    sParaTemp.push(["logistics_type", "POST"]);
    sParaTemp.push(["logistics_fee", "0"]);
    sParaTemp.push(["logistics_payment", "BUYER_PAY"]);
    sParaTemp.push(["price", "0.01"]);
    sParaTemp.push(["quantity", "1"]);

    var trade_create_by_buyer = function (sParaTemp) {
        //增加基本配置
        sParaTemp.push(["service", "trade_create_by_buyer"]);
        sParaTemp.push(["partner", AlipayConfig.partner]);
        sParaTemp.push(["seller_email", AlipayConfig.seller_email]);
        sParaTemp.push(["_input_charset", AlipayConfig.input_charset]);
        var buildURL = function (sParaTemp) {
            var buildRequestPara = function (sParaTemp) {
                var sPara = [];
                //除去数组中的空值和签名参数
                for (var i1 = 0; i1 < sParaTemp.length; i1++) {
                    var value = sParaTemp[i1];
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
                prestr = prestr + AlipayConfig.key; //把拼接后的字符串再与安全校验码直接连接起来
                var crypto = require('crypto');
                var mysign = crypto.createHash('md5').update(prestr).digest("hex");
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
    //向支付宝网关发出请求
//    requestUrl(AlipayConfig.ALIPAY_HOST,show_url,function(data){
//        console.log(data);
//    });
    res.redirect("https://" + AlipayConfig.ALIPAY_HOST + "/" + sURL);
};
exports.paynotify = function (req, res) {
    //http://127.0.0.1:3000/paynotify?trade_no=2008102203208746&out_trade_no=3618810634349901&discount=-5&payment_type=1&subject=iphone%E6%89%8B%E6%9C%BA&body=Hello&price=10.00&quantity=1&total_fee=10.00&trade_status=TRADE_FINISHED&refund_status=REFUND_SUCCESS&seller_email=chao.chenc1%40alipay.com&seller_id=2088002007018916&buyer_id=2088002007013600&buyer_email=13758698870&gmt_create=2008-10-22+20%3A49%3A31&is_total_fee_adjust=N&gmt_payment=2008-10-22+20%3A49%3A50&gmt_close=2008-10-22+20%3A49%3A46&gmt_refund=2008-10-29+19%3A38%3A25&use_coupon=N&notify_time=2009-08-12+11%3A08%3A32&notify_type=%E4%BA%A4%E6%98%93%E7%8A%B6%E6%80%81%E5%90%8C%E6%AD%A5%E9%80%9A%E7%9F%A5%28trade_status_sync%29&notify_id=70fec0c2730b27528665af4517c27b95&sign_type=DSA&sign=_p_w_l_h_j0b_gd_aejia7n_ko4_m%252Fu_w_jd3_nx_s_k_mxus9_hoxg_y_r_lunli_pmma29_t_q%253D%253D&extra_common_param=%E4%BD%A0%E5%A5%BD%EF%BC%8C%E8%BF%99%E6%98%AF%E6%B5%8B%E8%AF%95%E5%95%86%E6%88%B7%E7%9A%84%E5%B9%BF%E5%91%8A%E3%80%82
    //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
    var params = req.query;


//    console.log(req.query());
    var trade_no = req.query.trade_no;				//支付宝交易号
    var order_no = req.query.out_trade_no;	        //获取订单号
    var total_fee = req.query.total_fee;	        //获取总金额
    var subject = req.query.subject;//商品名称、订单名称
    console.log(trade_no + "支付宝交号");
    var body = "";
    if (req.query.body != null) {
        body = req.query.body;//商品描述、订单备注、描述
    }
    var buyer_email = req.query.buyer_email;		//买家支付宝账号
    var trade_status = req.query.trade_status;		//交易状态
    //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
    AlipayNotify.verity(params, function (result) {
        if (result) {
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status == "TRADE_FINISHED") {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //该种交易状态只在两种情况下出现
                //1、开通了普通即时到账，买家付款成功后。
                //2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
            } else if (trade_status == "TRADE_SUCCESS") {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            res.end("success");	//请不要修改或删除——

            //////////////////////////////////////////////////////////////////////////////////////////
        } else {
            res.end("fail");
        }

    });
};
//http://127.0.0.1:3000/payreturn?is_success=T&sign=b1af584504b8e845ebe40b8e0e733729&sign_type=MD5&body=Hello&buyer_email=xinjie_xj%40163.com&buyer_id=2088101000082594&exterface=create_direct_pay_by_user&out_trade_no=6402757654153618&payment_type=1&seller_email=chao.chenc1%40alipay.com&seller_id=2088002007018916&subject=%E5%A4%96%E9%83%A8FP&total_fee=10.00&trade_no=2008102303210710&trade_status=TRADE_FINISHED&notify_id=RqPnCoPT3K9%252Fvwbh3I%252BODmZS9o4qChHwPWbaS7UMBJpUnBJlzg42y9A8gQlzU6m3fOhG&notify_time=2008-10-23+13%3A17%3A39&notify_type=trade_status_sync&extra_common_param=%E4%BD%A0%E5%A5%BD%EF%BC%8C%E8%BF%99%E6%98%AF%E6%B5%8B%E8%AF%95%E5%95%86%E6%88%B7%E7%9A%84%E5%B9%BF%E5%91%8A%E3%80%82
exports.payreturn = function (req, res) {
    //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
    var params = req.query;


//    console.log(req.query());
    var trade_no = req.query.trade_no;				//支付宝交易号
    var order_no = req.query.out_trade_no;	        //获取订单号
    var total_fee = req.query.total_fee;	        //获取总金额
    var subject = req.query.subject;//商品名称、订单名称
    var body = "";
    if (req.query.body != null) {
        body = req.query.body;//商品描述、订单备注、描述
    }
    var buyer_email = req.query.buyer_email;		//买家支付宝账号
    var trade_status = req.query.trade_status;		//交易状态
    //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

    AlipayNotify.verity(params, function (result) {
        //如果成功，插入表记录
        if (result) {
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status == "TRADE_FINISHED") {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //该种交易状态只在两种情况下出现
                //1、开通了普通即时到账，买家付款成功后。
                //2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
            } else if (trade_status == "TRADE_SUCCESS") {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            res.end("success");	//请不要修改或删除——

            //////////////////////////////////////////////////////////////////////////////////////////
        } else {
            res.end("fail");
        }

    });
};
