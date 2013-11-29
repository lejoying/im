var serverSetting = root.globaldata.serverSetting;
var voiceManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var fs = require('fs');
var sha1 = require('../../mcserver/tools/sha1');
/***************************************
 *     URL：/api2/image/upload
 ***************************************/
voiceManage.upload = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
    var voiceStr = data.voicedata;
//    console.log(voiceStr);
    if (fileName == null || voiceStr == null || fileName == "" || voiceStr == "" || fileName == undefined || voiceStr == undefined) {
        response.write(JSON.stringify({
            "提示信息": "图片上传失败",
            "失败原因": "参数不完整"
        }), function (error) {
//            console.log(error);
            response.end();
        });
        return;
    }
    var base64Data = voiceStr.replace(/^data:audio\/\w+;base64,/, "");
    var dataBuffer = new Buffer(base64Data, 'base64');
    fs.writeFile(serverSetting.voiceFolder + fileName + ".mp3", dataBuffer, function (error) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "图片上传失败",
                "失败原因": "数据异常"
            }), function (error) {
                if (error) {
                    console.log("响应失败" + error);
                } else {
                    console.log("响应失败");
                }
            });
            response.end();
            console.log(error);
            return;
        } else {
            response.write(JSON.stringify({
                "提示信息": "图片上传成功",
                "filename": fileName
            }), function (error) {
                if (error) {
                    console.log("响应成功" + error);
                } else {
//                    console.log("响应成功");
                }
            });
            response.end();
        }
    });
}
/***************************************
 *     URL：/api2/image/check
 ***************************************/
voiceManage.check = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
    console.log(fileName);
    if (fileName == null || fileName == undefined || fileName == "") {
        response.write(JSON.stringify({
            "提示信息": "查找失败",
            "失败原因": "数据不完整"
        }), function (error) {
            response.end();
        });
        return;
    }
    fs.exists(serverSetting.voiceFolder, function (exists) {
        if (!exists) {
            fs.mkdir(serverSetting.voiceFolder, 777, function () {
                console.log("初始化音频目录成功");
            });
        } else {
            fs.exists(serverSetting.voiceFolder + fileName + ".mp3", function (exists) {
                response.write(JSON.stringify({
                    "提示信息": "查找成功",
                    "filename": fileName,
                    "exists": exists
                }));
                response.end();
            });
        }
    });
}
/***************************************
 *     URL：/api2/image/get
 ***************************************/
voiceManage.get = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
//    response.writeHead("Content-Type","image/png");
    fs.readFile(serverSetting.voiceFolder + fileName + ".mp3", "base64", function (err, data) {
        response.write(JSON.stringify({
            "提示信息": "获取成功",
            image: "data:audio/mp3;base64," + data
        }));
        response.end();
    });
}
module.exports = voiceManage;