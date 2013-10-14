root.globaldata = {};

var serverSetting = {};
globaldata.serverSetting = serverSetting;
serverSetting.environment = "local";//local or server
//serverSetting.debug = true;

if (serverSetting.environment == "local") {
//    serverSetting.imageFolder = "D://Users//ChrisGai//Downloads//nginx-1.3.16//upload//";
    serverSetting.neo4jUrl = "http://115.28.51.197:7474/";
}
else if (serverSetting.environment == "server") {
    serverSetting.imageFolder = "/alidata/upload/";
    serverSetting.neo4jUrl = "http://127.0.0.1:7474/";
}

module.exports = serverSetting;