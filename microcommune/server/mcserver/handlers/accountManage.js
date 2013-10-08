var accountManage = {};
accountManage.verifyphone = function(data, response){
    var phone = data.phone;
    response.write(JSON.stringify({phone:phone}));
    response.end();
}
accountManage.verifycode = function(data, response){
    var phone = data.phone;
    var code = data.code;

    response.write(JSON.stringify({phone:phone}));
    response.end();

}
accountManage.verifypass = function(data, response){

}
accountManage.auth = function(data, response){

}
accountManage.trash = function(data, response){

}

module.exports = accountManage;