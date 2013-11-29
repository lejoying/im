var http = require("http");
var route = require("./lib/route");
var routemap = require("./routemap");

var i = 1;

http.createServer(
    function (request, response) {

        response.writeHead(200, {
            "Content-Type": "application/json; charset=UTF-8"
        });
        route(routemap, request.url, request, response);

        i++;
        console.log("The voice server has been accessed " + i);
        if (response.asynchronous == null) {
            response.end();
        }

    }).listen(8073);

console.log("The voice server is running.8073");
