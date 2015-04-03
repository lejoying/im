root.globaldata = {};

var serverSetting = {};
globaldata.serverSetting = serverSetting;
serverSetting.environment = "server";//local or server
//serverSetting.debug = true;
serverSetting.EARTH_RADIUS = 6378137.0;
var LBS = {};
serverSetting.LBS = LBS;
serverSetting.LBS.LBS_AK = "9MBoVuWESUbrqxL5indWugNn";
serverSetting.LBS.POI_LIST = "http://api.map.baidu.com/geodata/v3/poi/list";
serverSetting.LBS.POI_UPDATE = "http://api.map.baidu.com/geodata/v3/poi/update";
serverSetting.LBS.POI_CREATE = "http://api.map.baidu.com/geodata/v3/poi/create";
serverSetting.LBS.NEARBY = "http://api.map.baidu.com/geosearch/v3/nearby";

serverSetting.LBS.KEY = "0cfd7855ab036b58c865e7b0bde42146";
serverSetting.LBS.ACCOUNTTABLEID = "53eacbe4e4b0693fbf5fd13b";
serverSetting.LBS.GROUPTABLEID = "53eacbb9e4b0693fbf5fd0f6";
serverSetting.LBS.SHARESTABLEID = "54f520e3e4b0ff22e1fc52d3";
serverSetting.LBS.DATA_CREATE = "http://yuntuapi.amap.com/datamanage/data/create";
serverSetting.LBS.DATA_UPDATA = "http://yuntuapi.amap.com/datamanage/data/update";
serverSetting.LBS.DATA_DELETE = "http://yuntuapi.amap.com/datamanage/data/delete";
serverSetting.LBS.DATA_SEARCH = "http://yuntuapi.amap.com/datamanage/data/list";

serverSetting.LBS_SHAERE = "http://10.252.45.120/";//182.92.107.229  10.252.45.120
serverSetting.LBS_ACCOUNT = "http://10.165.121.75/";//123.57.58.84  10.165.121.75
serverSetting.LBS_SHAERE_CREATE = serverSetting.LBS_SHAERE + "api2/lbs/create";
serverSetting.LBS_SHAERE_UPDATE = serverSetting.LBS_SHAERE + "api2/lbs/updateshare";
serverSetting.LBS_SHAERE_DELETE = serverSetting.LBS_SHAERE + "api2/lbs/deleteshare";
serverSetting.LBS_ACCOUNT_MODIFY = serverSetting.LBS_ACCOUNT + "api2/lbs/modifyaccount";
var zookeeper = {};
zookeeper.mcServer = {
    ip: "115.28.51.197",
    port: "2181",
    timeout: "100000",
    name: "mcServer"
}
zookeeper.squareServer = {
    ip: "115.28.212.79",
    port: "2182",
    timeout: "100000",
    name: "squareServer"
}
zookeeper.pushServer = {
    ip: "115.28.51.197",
    port: "2183",
    timeout: "100000",
    name: "pushServer"
}
zookeeper.imageServer = {
    ip: "115.28.51.197",
    port: "2184",
    timeout: "100000",
    name: "imageServer"
}
serverSetting.zookeeper = zookeeper;

if (serverSetting.environment == "local") {
    serverSetting.imageFolder = "D://nginx//upload//images//";
    serverSetting.voiceFolder = "D://nginx//upload//voices//";
    serverSetting.neo4jUrl = "http://112.126.71.175:7474/";//115.28.51.197
//    serverSetting.redisIP = "115.28.51.197";
    serverSetting.redisIP = "112.126.71.180";
    serverSetting.redisPort = "6379";
}
else if (serverSetting.environment == "server") {
    serverSetting.imageFolder = "/alidata/upload/images/";
    serverSetting.voicesFolder = "/alidata/upload/voices/";
    serverSetting.neo4jUrl = "http://112.126.71.175:7474/";//112.126.71.175
    serverSetting.redisIP = "127.0.0.1";
    serverSetting.redisPort = "6379";
}

module.exports = serverSetting;