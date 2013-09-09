var server = require("./server/server.js");
var router = require("./server/router.js");
var requestHandle = require("./server/handle.js");

var handle = {};
handle["/sendMsg"] = requestHandle.server2;

server.start(router.route,handle);