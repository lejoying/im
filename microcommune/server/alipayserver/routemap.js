var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/alipay/:operation": requestHandlers.alipayManage
    },
    "post": {
        "/alipay/:operation": requestHandlers.alipayManage
    }
};

module.exports = routemap;