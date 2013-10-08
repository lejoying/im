var accountManage = {};
accountManage.verifyphone = function(data, response){
    console.log(data);
    var phone = data.phone;
    response.write(JSON.stringify({"提示消息":"手机号验证成功",phone:phone}));
    response.end();
}
accountManage.verifycode = function(data, response){
    console.log(data);
    var phone = data.phone;
    var code = data.code;
    response.write(JSON.stringify({"提示消息":"验证码正确",phone:phone}));
    response.end();
}
accountManage.verifypass = function(data, response){

}
accountManage.auth = function(data, response){
    response.asynchronous = 1;
    
}
accountManage.trash = function(data, response){

}

module.exports = accountManage;