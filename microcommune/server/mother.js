start();
var flag = true;
var time = new Date().getTime();
var dataerr;
function start() {
    console.log('Mother process is running.');
    var ls = require('child_process').spawn('node', ['./index.js']);
    ls.stdout.on('data', function (data) {
        console.log(data.toString());
    });
    ls.stderr.on('data', function (data) {
        console.log("the err" + data.toString());
        dataerr = data.toString();
    });
    ls.on('exit', function (code) {
        var nowTime = new Date().getTime();
        if (nowTime - time > 300000 || flag == true) {
            time = nowTime;
            flag = false;
            sendEmailToMembers(dataerr);
        }
        console.log('child process exited with code ' + code);
        delete(ls);
        setTimeout(start, 3000);
    });
}
var email = require("mailer");
function sendEmailToMembers(data) {
    email.send(
        {
            ssl: true,
            host: "smtp.exmail.qq.com",//发送 smtp.qq.com，接收 pop.qq.com
            domain: "[112.126.71.180]",//可以在浏览器中输入 http://ip.qq.com/ 得到
            to: "qiaoxiaosong@lejoying.com,fengzhicheng@lejoying.com",//
            from: "open@lejoying.com",
            subject: "112.126.71.180(主服务器)服务器异常停止工作，正在重启",
//            reply_to: "xxx@xxx.com",
            body: "服务器挂机输出的异常信息如下：\n\r" + data,
            authentication: "login",
            username: "open@lejoying.com",
            password: "l@6688",
            debug: false
        },
        function (err, result) {
            if (err) {
                console.log("the err:" + err);
                console.log("服务器异常邮件已发送失败,服务器正在准备重启...");
            } else {
                if (result) {
                    console.log("服务器异常邮件已发送成功,服务器正在准备重启...");
                } else {
                    console.log("服务器异常邮件已发送失败,服务器正在准备重启...");
                }
            }
        }
    );
}
