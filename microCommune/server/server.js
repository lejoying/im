var http = require("http");
var url = require("url");

function start(route,handle){
    function onRequest(request,response){
        var pathname = url.parse(request.url).pathname;
        route(handle,pathname,request,response);
    }
    http.createServer(onRequest).listen(3000,function(){
        console.log("http://localhost:3000");
    })
}

exports.start = start;