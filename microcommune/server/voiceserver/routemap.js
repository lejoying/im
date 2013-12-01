var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/voice/:operation": requestHandlers.voiceManage
    },
    "post": {
        "/voice/:operation": requestHandlers.voiceManage
    }
};

module.exports = routemap;