/**
 * Created by Coolspan on 2014/11/10.
 */
var bugManage = {};
var email = require("mailer");
var emailPool = {};
bugManage.send = function (data, response) {
    response.asynchronous = 1;
    console.log(data);
    var time2 = data.time;
    var info = data.info;
    var bug = data.bug;
    if (data.content) {
        sendEmailToMembers(data.content);
    } else {
        if (emailPool[info]) {
            var time = emailPool[info];
            var currentTime = new Date().getTime();
            if (currentTime - time > 1000 * 60) {
                emailPool[info] = currentTime;
                sendEmailToMembers(time2 + "\n" + info + "\n" + bug);
            } else {
                response.write(JSON.stringify({
                    "提示信息": "发布成功"
                }));
                response.end();
                return;
            }
        } else {
            emailPool[info] = new Date().getTime();
            sendEmailToMembers(time2 + "\n" + info + "\n" + bug);
        }
    }
    function sendEmailToMembers(data) {
        email.send(
            {
                ssl: true,
                host: "smtp.exmail.qq.com",//发送 smtp.qq.com，接收 pop.qq.com
                domain: "[112.126.71.180]",//可以在浏览器中输入 http://ip.qq.com/ 得到
                to: "open@lejoying.com",//
                from: "open@lejoying.com",
                subject: "bug",
//            reply_to: "xxx@xxx.com",
                body: "bug：\n\r" + data,
                authentication: "login",
                username: "open@lejoying.com",
                password: "l@6688",
                debug: false
            },
            function (err, result) {
                if (err) {
                    console.log("the err:" + err);
                    response.write(JSON.stringify({
                        "提示信息": "发布失败"
                    }));
                    response.end();
                } else {
                    if (result) {
                        response.write(JSON.stringify({
                            "提示信息": "发布成功"
                        }));
                        response.end();
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "发布失败"
                        }));
                        response.end();
                    }
                }
            }
        );
    }
}
module.exports = bugManage;