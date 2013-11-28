var serverSetting = root.globaldata.serverSetting;
var imagesManage = {};
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
var fs = require('fs');
var sha1 = require('../../mcserver/tools/sha1');
/***************************************
 *     URL：/api2/image/upload
 ***************************************/
imagesManage.upload = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
    var imageStr = data.imagedata;
//    console.log(imageStr);
    if (fileName == null || imageStr == null || fileName == "" || imageStr == "" || fileName == undefined || imageStr == undefined) {
        response.write(JSON.stringify({
            "提示信息": "图片上传失败",
            "失败原因": "参数不完整"
        }), function (error) {
//            console.log(error);
            response.end();
        });
        return;
    }
    var base64Data = imageStr.replace(/^data:image\/\w+;base64,/, "");
    var dataBuffer = new Buffer(base64Data, 'base64');
    fs.writeFile(serverSetting.imageFolder + fileName + ".png", dataBuffer, function (error) {
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
imagesManage.check = function (data, response) {
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
    fs.exists(serverSetting.imageFolder + fileName + ".png", function (exists) {
        response.write(JSON.stringify({
            "提示信息": "查找成功",
            "filename": fileName,
            "exists": exists
        }));
        response.end();
    });
}
/***************************************
 *     URL：/api2/image/get
 ***************************************/
imagesManage.get = function (data, response) {
    response.asynchronous = 1;
    var fileName = data.filename;
//    response.writeHead("Content-Type","image/png");
    fs.readFile(serverSetting.imageFolder + fileName + ".png", "base64", function (err, data) {
        response.write(JSON.stringify({
            "提示信息": "获取成功",
            image: "data:image/png;base64," + data
        }));
        response.end();
    });
}
module.exports = imagesManage;