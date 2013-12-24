var requestHandlers = require("./requestHandlers");

var routemap = {
    "get": {
        "/api2/session/:operation": requestHandlers.session
    },
    "post": {
        "/api2/session/:operation": requestHandlers.session
    }
};

module.exports = routemap;