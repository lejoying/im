var gps = {};
var ajax = require('./ajax.js');

gps.toBaiDuLocation = function (longitude, latitude, next) {
    ajax.ajax({
        type: "GET",
        url: "http://api.map.baidu.com/ag/coord/convert",
        data: {
            from: 0,
            to: 4,
            x: longitude,
            y: latitude
        },
        success: function (data) {
            var data = JSON.parse(data);
            data.longitude = new Buffer(data.x, 'base64').toString();
            data.latitude = new Buffer(data.y, 'base64').toString();
            next(data);
        }
    });
}
module.exports = gps;