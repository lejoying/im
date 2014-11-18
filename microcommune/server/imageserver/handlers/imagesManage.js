var serverSetting = root.globaldata.serverSetting;
var imagesManage = {};
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
//var crypto = require("crypto");
//var app_secret = "UOUAYzQUyvjUezdhZDAmX1aK6VZ5aG";//OtxrzxIsfpFjA7SwPzILwy8Bw21TLhquhboDYROV
//encryptedContent = crypto.createHmac('sha1', app_secret).update(content).digest().toString('base64'); //base64

imagesManage.checkfileexist = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.fileName;
    client.HEXISTS("ImageFileNames", fileName, function (error, reply) {
        if (error != null) {
            response.write(JSON.stringify({
                "提示信息": "查询失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error + "check image is exists");
            return;
        } else {
            var flag = false;
            if (reply == "1") {
                flag = true;
                console.log(fileName + "---" + reply + "---文件已存在");
            } else {
                console.log(fileName + "---" + reply + "---文件不存在");
            }
            response.write(JSON.stringify({
                "提示信息": "查找成功",
                "fileName": fileName,
                "exists": flag
            }));
            response.end();
        }
    });
}
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
imagesManage.uploadfilename = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.fileName;
    var time = new Date().Format("yyyy-MM-dd hh:mm:ss");
    client.HMSET("ImageFileNames", fileName, time, function (err, reply) {
        if (err != null) {
            response.write(JSON.stringify({
                "提示信息": "上传失败",
                "失败原因": "数据异常"
            }), function () {
                response.end();
            });
            console.log(err);
            return;
        } else {
            if (reply == "OK") {
                response.write(JSON.stringify({
                    "提示信息": "上传成功",
                    "fileName": fileName
                }), function () {
                    response.end();
                });
            } else {
                response.write(JSON.stringify({
                    "提示信息": "上传失败",
                    "失败原因": "数据异常"
                }), function () {
                    response.end();
                });
            }
        }
    });
}
module.exports = imagesManage;