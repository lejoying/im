var fs = require("fs");
formidable = require("formidable");

function server1(request,response){
    console.log("server1 was called");
    var body = '<html>'+
        '<head>'+
        '<meta http-equiv="Content-Type" content="text/html; '+
        'charset=UTF-8" />'+
        '</head>'+
        '<body>'+
        '<form action="/server2" method="post">'+
        '<textarea name="text" rows="20" cols="60"></textarea>'+
        '<input type="submit" value="提交数据" />'+
        '</form>'+
        '</body>'+
        '</html>';

    response.writeHead(200, {"Content-Type": "text/html"});
    response.write(body);
    response.end();
}

function server2(request,response){
    console.log("server2 was called");
    var postData = "";
    request.setEncoding("utf8");
    request.addListener("data",function(postDataChunk){
        postData += postDataChunk;
    });

    request.addListener("end",function(){
        response.writeHead(200,{"Content-Type":"text/html"});
        response.write(postData);
        response.end();
    })  ;

}

function uploadImage(request,response){

    response.writeHead(200,{"Content-Type":"text/html"});
    response.write("<html><head><meta http-equiv='Content-Type' content='text/html'" +
        "charset='utf8'><title>上传图片</title></head><body><form action='/upload' method='post' enctype='multipart/form-data'>" +
        "<input type='file' name='upload'><input type='submit' value='上传'></form></body></html>");
    response.end();

}
function upload(request,response){
    var form = new formidable.IncomingForm();
    form.uploadDir="D:/";
    form.parse(request, function(error, fields, files) {
        console.log("parsing done");
        fs.renameSync(files.upload.path, "D:/2.jpg");

        response.writeHead(200, {"Content-Type": "text/html"});
        response.write("received image:<br/>");
        response.write("<img src='/show' />");
        response.end();
    });
}

function show(request,response){
    fs.readFile("D:/2.jpg", "binary", function(error, file) {
        if(error) {
            response.writeHead(500, {"Content-Type": "text/plain"});
            response.write(error + "\n");
            response.end();
        } else {
            response.writeHead(200, {"Content-Type": "image/png"});
            response.write(file, "binary");
            response.end();
        }
    });
}

function sleep(milliSeconds){
    var startTime = new Date().getTime();
    while(new Date().getTime()<startTime+milliSeconds);
}

exports.server1 = server1;
exports.server2 = server2;
exports.uploadImage = uploadImage;
exports.upload = upload;
exports.show = show;