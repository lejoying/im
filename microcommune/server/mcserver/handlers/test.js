/**
 * Created by Coolspan on 2014/11/18.
 */
var ajax = require("./../lib/ajax.js");

//getLbsData();
function getLbsData() {
    var groups = [];
    var groupsMap = {};
    ajax.ajax({
        type: "GET",
        url: "http://yuntuapi.amap.com/datamanage/data/list?parameters",
        data: {
            key: "0cd819a62c50d40b75a73f66cb14aa06",
            tableid: "53eacbb9e4b0693fbf5fd0f6",
            page: 1,
            limit: 100
        },
        success: function (data) {
            var datas = JSON.parse(data).datas
            for (var index in datas) {
                var group = datas[index];
                groups.push(group.gid);
            }
            ajax.ajax({
                type: "GET",
                url: "http://yuntuapi.amap.com/datamanage/data/list?parameters",
                data: {
                    key: "0cd819a62c50d40b75a73f66cb14aa06",
                    tableid: "53eacbb9e4b0693fbf5fd0f6",
                    page: 2,
                    limit: 100
                },
                success: function (data) {
                    var datas = JSON.parse(data).datas
                    for (var index in datas) {
                        var group = datas[index];
                        groups.push(parseInt(group.gid));
                    }
                    console.log(groups.toString() + "----" + groups.length);
                    getDBGroups(groups, groupsMap);
                }
            });
        }
    });
}
var serverSetting = require('./../../settings.js');
var serverSetting = root.globaldata.serverSetting;
var neo4j = require('neo4j');
var db = new neo4j.GraphDatabase(serverSetting.neo4jUrl);
function getDBGroups(groups, groupsMap) {
    var query = [
        "MATCH (group:Group)",
        "WHERE group.gid IN {groups}",
        "RETURN group"
    ].join("\n");
    var params = {
        groups: groups
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.error(error);
        } else {
            console.log(results.length);
            var groupsA = [];
            for(var index in results){
                var groupData = results[index].group.data;
                groupsA.push(groupData.gid);
            }
            console.log(groupsA.toString());
        }
    });
}
getDBGroupData();
function getDBGroupData(){
    var query = [
        "MATCH (group:Group)",
        "RETURN group"
    ].join("\n");
    var params = {
    };
    db.query(query, params, function (error, results) {
        if (error) {
            console.error(error);
        } else {
            console.log(results.length);
            var groupsA = [];
            for(var index in results){
                var groupData = results[index].group.data;
                groupsA.push(groupData.gid);
            }
            console.log(groupsA.toString());
        }
    });
}