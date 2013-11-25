var express = require('express')
    , routes = require('./alipayManage');
var express = require("express");
var app = express();
app.post('/alipay/alipayto', routes.alipayto);
app.post('/alipay/paynotify', routes.paynotify);
app.get('/alipay/batch_trans_notify', routes.batch_trans_notify);
app.post('/alipay/batch_trans_notify_by_notify_url', routes.batch_trans_notify_by_notify_url);
app.listen(8075, function () {
    console.log("The Alipay server is running.8075");
});
