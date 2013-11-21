
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./index.js');
var express = require("express");
var app = express();
//var app = module.exports;

// Configuration

/*app.configure(function(){
//  app.set('views', __dirname + '/views');
//  app.set('view engine', 'html');
//  app.register('.html', require('ejs'));
//  app.use(express.bodyParser());
//  app.use(express.methodOverride());
//  app.use(app.router);
//  app.use(express.static(__dirname + '/public'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function(){
  app.use(express.errorHandler());
});*/

// Routes

//app.get('/', routes.index);
app.get('/alipay/alipayto',routes.alipayto);
app.get('/alipay/paynotify',routes.paynotify);
app.get('/alipay/payreturn',routes.payreturn);

app.listen(8075, function(){
  console.log("The Alipay server is running.8075");
});
