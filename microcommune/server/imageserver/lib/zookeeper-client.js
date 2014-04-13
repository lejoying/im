var zk = {};
var count = 0;
var zookeeper = require('node-zookeeper-client');
var path = "";
var client;
var ModifyAccessKeyPool;
function connect() {
    client.getData(
        '/zk',
        function (event) {
            console.log('Got event: %s.', event.type);
            if (event.type == 3) {
                connect();
            } else {
                log.error("zookeeper client get Date make bug ,please deal with --- In squareManage.js");
            }
        },
        function (error, data, stat) {
            if (error) {
                console.log(error.stack);
                return;
            }
            console.log('Got data: %s', data.toString('utf8'));
        }
    );
}
zk.setData = function (accessKeyPool) {
    var account = JSON.stringify(accessKeyPool);
    var buffer = new Buffer(account);
    client.setData('/accessKeyPool', buffer, function (error, stat) {
        if (error) {
            console.log(error.stack);
            zk.exists();
            return;
        }
        zk.exists();
        console.log('Data is set.');
    });
}
zk.getData = function () {
    client.getData(
        '/accessKeyPool',
        function (error, data, stat) {
            if (error) {
                console.log(error.stack);
                zk.exists();
                return;
            }
            if (data) {
//                console.log('getData Got data: %s', data.toString('utf8'));
                var KeyPool;
                try {
                    KeyPool = JSON.parse(data.toString('utf8'));
                    ModifyAccessKeyPool(KeyPool);
                } catch (e) {
                    console.log('getData Got data: %s', "data Exception, no JSON Object");
                    zk.setData({});
                }
            } else {
                console.log('getData Got data: %s', "Illegal data");
                zk.setData({});
            }
        }
    );
}
zk.exists = function (accessKeyPool, flag) {
    client.exists('/accessKeyPool',
        function (event) {
            if (event.type == 1) {
                console.error(path + " exists event: 1");
                zk.exists();
            } else if (event.type == 2) {
                console.error(path + " exists event: 2");
                zk.exists();
            } else if (event.type == 3) {
                console.error(path + " exists event: 3");
                zk.getData();
            } else if (event.type == 4) {
                console.error(path + " exists event: 4");
                zk.getData();
            } else {
                console.error(path + " exists Exception deal with ......");
            }
        }, function (error, stat) {
            if (error) {
                console.log(error.stack);
                zk.exists();
                return;
            }
            if (stat) {
                console.log('Node exists. --status flag:' + flag);
                if (flag) {
                    zk.getData();
                }
            } else {
                console.log('Node does not exist.');
                if (count == 0) {
                    zk.create(accessKeyPool);
                }
            }
        });
}
zk.create = function (accessKeyPool) {
    count++;
    client.create("/accessKeyPool", function (error) {
        if (error) {
            console.log('Failed to create node: %s due to: %s.', path, error);
            zk.exist();
        } else {
            console.log('Node: %s is successfully created.', path);
            if (accessKeyPool) {
                zk.setData(accessKeyPool);
            }
        }
    });
}
zk.start = function (ip, port, timeout, accessKeyPool, next) {
    path = ip + ":" + port;
    ModifyAccessKeyPool = next;
    client = zookeeper.createClient(ip + ":" + port, { sessionTimeout: timeout });
    client.connect();
//    client.once('connected', function () {
//        console.log('Zookeeper Connected to the server.');
//    });
    zk.exists(accessKeyPool, true);
}
module.exports = zk;