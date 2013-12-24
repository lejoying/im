var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/api2/message/:operation": requestHandlers.messageManage,
        "/api2/account/:operation": requestHandlers.accountManage,
        "/api2/community/:operation": requestHandlers.communityManage,
        "/api2/relation/:operation": requestHandlers.relationManage,
        "/api2/circle/:operation": requestHandlers.circleManage,
        "/api2/webcode/:operation": requestHandlers.webcodeManage
    },
    "post": {
        "/api2/message/:operation": requestHandlers.messageManage,
        "/api2/account/:operation": requestHandlers.accountManage,
        "/api2/community/:operation": requestHandlers.communityManage,
        "/api2/relation/:operation": requestHandlers.relationManage,
        "/api2/circle/:operation": requestHandlers.circleManage,
        "/api2/webcode/:operation": requestHandlers.webcodeManage
    }
};

module.exports = routemap;