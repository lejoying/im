var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/api2/voice/:operation": requestHandlers.voiceManage
    },
    "post": {
        "/api2/voice/:operation": requestHandlers.voiceManage
    }
};

module.exports = routemap;