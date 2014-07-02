var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/api2/message/:operation": requestHandlers.messageManage,
        "/api2/account/:operation": requestHandlers.accountManage,
        "/api2/community/:operation": requestHandlers.communityManage,
        "/api2/relation/:operation": requestHandlers.relationManage,
        "/api2/circle/:operation": requestHandlers.circleManage,
        "/api2/webcode/:operation": requestHandlers.webcodeManage,
        "/api2/group/:operation": requestHandlers.groupManage,
        "/api2/share/:operation": requestHandlers.shareManage
    },
    "post": {
        "/api2/message/:operation": requestHandlers.messageManage,
        "/api2/account/:operation": requestHandlers.accountManage,
        "/api2/community/:operation": requestHandlers.communityManage,
        "/api2/relation/:operation": requestHandlers.relationManage,
        "/api2/circle/:operation": requestHandlers.circleManage,
        "/api2/webcode/:operation": requestHandlers.webcodeManage,
        "/api2/group/:operation": requestHandlers.groupManage,
        "/api2/share/:operation": requestHandlers.shareManage
    }
};

module.exports = routemap;