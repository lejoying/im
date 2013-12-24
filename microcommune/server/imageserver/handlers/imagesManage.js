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
    //fs.exists 判断指定的目录下是否存在你想索引的文件，查询结果返回的exists是Boolean类型true or false
    /*    fs.exists(serverSetting.imageFolder + fileName + ".png", function (exists) {
     response.write(JSON.stringify({
     "提示信息": "查找成功",
     "filename": fileName,
     "exists": exists
     }));
     response.end();
     });*/

    var query = [
        'MATCH (account:Account)',
        'WHERE account.head={fileName}',
        'RETURN account'
    ].join('\n');

    var params = {
        fileName: fileName
    };

    db.query(query, params, function (error, resulse) {
        if (error) {
            response.write(JSON.stringify({
                "提示信息": "查找失败",
                "失败原因": "数据异常"
            }));
            response.end();
            console.log(error);
            return;
        } else if (resulse.length == 0) {
            response.write(JSON.stringify({
                "提示信息": "查找成功",
                filename: fileName,
                exists: false
            }));
            response.end();
        } else {
            response.write(JSON.stringify({
                "提示信息": "查找成功",
                filename: fileName,
                exists: true
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
//    response.writeHead("Content-Type","image/png");
    fs.exists(serverSetting.imageFolder, function (exists) {
        if (!exists) {
            fs.mkdir(serverSetting.imageFolder, 777, function () {
                console.log("初始化图片目录成功");
            });
        } else {
            fs.readFile(serverSetting.imageFolder + fileName + ".png", "base64", function (err, data) {
                if (err) {
                    response.write(JSON.stringify({
                        "提示信息": "获取图片失败",
                        "失败原因": "数据异常"
                    }));
                    response.end();
                } else {
                    /*response.writeHead(200, {
                     "Content-Type": "image/jpeg"
                     });
                     response.write("<img src='data:image/png;base64," + data + "'/>");
                     response.end();*/
                    response.write(JSON.stringify({
                        "提示信息": "获取图片成功",
                        image: "data:image/png;base64," + data
                    }));
                    response.end();
                }
            });
        }
    });
}
imagesManage.show = function (data, response) {
    response.asynchronous = 1;
    console.log("request handlers 'show' was called");
    fs.readFile(serverSetting.imageFolder + data.filename + ".png", "binary", function (error, file) {
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
    });
}
module.exports = imagesManage;