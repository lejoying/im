root.globaldata = {};

var serverSetting = {};
globaldata.serverSetting = serverSetting;
serverSetting.environment = "local";//local or server
//serverSetting.debug = true;
serverSetting.EARTH_RADIUS = 6378137.0;
var LBS = {};
serverSetting.LBS = LBS;
serverSetting.LBS.LBS_AK = "9MBoVuWESUbrqxL5indWugNn";
serverSetting.LBS.POI_LIST = "http://api.map.baidu.com/geodata/v3/poi/list";
serverSetting.LBS.POI_UPDATE = "http://api.map.baidu.com/geodata/v3/poi/update";
serverSetting.LBS.POI_CREATE = "http://api.map.baidu.com/geodata/v3/poi/create";
serverSetting.LBS.NEARBY = "http://api.map.baidu.com/geosearch/v3/nearby";
if (serverSetting.environment == "local") {
    serverSetting.imageFolder = "D://nginx//upload//images//";
    serverSetting.voiceFolder = "D://nginx//upload//voices//";
    serverSetting.neo4jUrl = "http://115.28.51.197:7474/";
//    serverSetting.redisIP = "115.28.51.197";
    serverSetting.redisIP = "127.0.0.1";
    serverSetting.redisPort = "6379";
}
else if (serverSetting.environment == "server") {
    serverSetting.imageFolder = "/alidata/upload/images/";
    serverSetting.voicesFolder = "/alidata/upload/voices/";
    serverSetting.neo4jUrl = "http://127.0.0.1:7474/";
    serverSetting.redisIP = "127.0.0.1";
    serverSetting.redisPort = "6379";
}

module.exports = serverSetting;