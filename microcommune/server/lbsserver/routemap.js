var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/lbs/:operation": requestHandlers.lbsManage
    },
    "post": {
        "/lbs/:operation": requestHandlers.lbsManage
    }
};

module.exports = routemap;