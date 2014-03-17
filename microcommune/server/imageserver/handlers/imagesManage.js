var serverSetting = root.globaldata.serverSetting;
var imagesManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var fs = require('fs');
var sha1 = require('../../mcserver/tools/sha1');
var ajax = require('../../mcserver/lib/ajax.js');
var redis = require("redis");
var client = redis.createClient(serverSetting.redisPort, serverSetting.redisIP);
/***************************************
 *     URL：/api2/image/upload
 ***************************************/
imagesManage.upload = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
    fileName = fileName.toLowerCase();
    var imageStr = data.imagedata;
//    console.log(imageStr);
    if (fileName == null || imageStr == null || fileName == "" || imageStr == "" || fileName == undefined || imageStr == undefined) {
        response.write(JSON.stringify({
            "提示信息": "图片上传失败",
            "失败原因": "参数不完整"
        }), function () {
            response.end();
        });
        return;
    }
//    var base64Data = imageStr;
    var base64Data = imageStr.replace(/^data:image\/\w+;base64,/, "");
    var dataBuffer = new Buffer(base64Data, 'base64');
    fs.writeFile(serverSetting.imageFolder + fileName, dataBuffer, function (error) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "图片上传失败",
                "失败原因": "数据异常"
            }), function () {
                response.end();
            });
            console.log(error);
            return;
        } else {
            client.HMSET("imageset", fileName, fileName, function (err, reply) {
                if (err != null) {
                    response.write(JSON.stringify({
                        "提示信息": "图片上传失败",
                        "失败原因": "数据异常"
                    }), function () {
                        response.end();
                    });
                    console.log(err);
                    return;
                } else {
                    if (reply == "OK") {
                        response.write(JSON.stringify({
                            "提示信息": "图片上传成功",
                            "filename": fileName
                        }), function () {
                            response.end();
                        });
                    } else {
                        response.write(JSON.stringify({
                            "提示信息": "图片上传失败",
                            "失败原因": "数据异常"
                        }), function () {
                            response.end();
                        });
                    }
                }
            });
        }
    });
}
/***************************************
 *     URL：/api2/image/check
 ***************************************/
imagesManage.check = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename + "";
    fileName = fileName.toLowerCase();
    if (fileName == null || fileName == undefined || fileName == "") {
        response.write(JSON.stringify({
            "提示信息": "查找失败",
            "失败原因": "数据不完整"
        }), function (error) {
            response.end();
        });
        return;
    }
    client.HEXISTS("imageset", fileName, function (error, reply) {
        if (error != null) {
            response.write(JSON.stringify({
                "提示信息": "查找失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else {

            var flag = false;
            console.log(reply);
            if (reply == "1") {
                flag = true;
                console.log(fileName + "---" + reply + "---图片已存在");
            } else {
                console.log(fileName + "---" + reply + "---图片不存在");
            }
            response.write(JSON.stringify({
                "提示信息": "查找成功",
                "filename": fileName,
                "exists": flag
            }));
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/image/get
 ***************************************/
imagesManage.get = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
    fileName = fileName.toLowerCase();
//    response.writeHead("Content-Type","image/png");
    fs.exists(serverSetting.imageFolder, function (exists) {
        if (!exists) {
            fs.mkdir(serverSetting.imageFolder, 777, function () {
                console.log("初始化图片目录成功");
            });
        } else {
            fs.readFile(serverSetting.imageFolder + fileName, "base64", function (err, data) {
                if (err) {
                    response.write(JSON.stringify({
                        "提示信息": "获取图片失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    response.write(JSON.stringify({
                        "提示信息": "获取图片成功",
                        image: "data:image/png;base64," + data
                    }));
                    console.log("SHA1:---" + sha1.hex_sha1(data.trim()));
                    response.end();
                }
            });
        }
    });
}
imagesManage.show = function (data, response) {
    response.asynchronous = 1;
    ajax.ajax({
        url: "https://res.wx.qq.com/zh_CN/htmledition/images/spacer17ced3.gif",
        type: "POST",
        data: {},
        success: function (data) {
            console.log(data);
            response.writeHead(200, {"Content-Type": "image/gif"});
            response.write(JSON.stringify(data));
            response.end();
        }
    });
    /*var fileName = data.filename;
     fileName = fileName.toLowerCase();
     fs.readFile(serverSetting.imageFolder + fileName, "binary", function (error, file) {
     if (error) {
     console.log(error + "\n");
     response.write(JSON.stringify({
     "提示信息": "获取图片失败",
     "失败原因": "数据异常"
     }));
     response.end();
     } else {
     response.writeHead(200, {"Content-Type": "image/png"});
     response.write(file, "binary");
     response.end();
     }
     });*/
}
module.exports = imagesManage;