/*************************************** ***************************************
 * *    Class：session
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/session/sendmessage
     ***************************************/
    "message_send": {
        request: {
            typical: {"uid": "XXX", "userlist": [1,2,3], "message": {"content":"xxx","time":"123123123"}}
        },
        response: {
            success: {"提示信息": "消息发送成功"},
            failed: {"提示信息": "消息发送失败", "失败原因": []}
        }
    },
    /***************************************
     *     URL：/api2/session/getmessages
     ***************************************/
    "message_get": {
        request: {
            typical: {"uid": "XXX","sessionID":"XXX"}
        },
        response: {
            success: {"提示信息": "消息获取成功", "messages":[{"content":"xxx","time":"123123123"},{"content":"xxx","time":"123123123"}]},
            failed: {"提示信息": "消息获取失败", "失败原因": []}}
        }
    }

/*************************************** ***************************************
 * *    Class：account
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/account/verifyphone
     ***************************************/
    "account_verifyphone": {
        request: {
            typical: {"phone": "XXX"}
        },
        response: {
            success: {"提示信息": "手机号验证成功" , "phone":"XXX"},
            failed: {"提示信息": "手机号验证失败", "失败原因": ["手机号不正确","手机号已被注册"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifycode
     ***************************************/
    "account_verifycode": {
        request: {
            typical: {"phone":"XXX","code":"XXX"}
        },
        response: {
            success: {"提示信息": "验证成功","phone":"XXX"},
            failed: {"提示信息": "验证失败","失败原因": ["验证码不正确" || "验证码超时"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifypass
     ***************************************/
    "account_verifypass": {
        request: {
            typical: {"phone": "XXX", "password":"XXX","longitude":"XXX","latitude":"XXX"}
        },
        response: {
            success: {"提示信息": "注册成功",account:{},nowcommunity:{},community:[{},{},{}],friends:[{"nickName":"XXX","phone":"XXX","head":"XXX"},{},{}]},
            failed: {"提示信息": "注册失败", "失败原因": ["保存数据遇到错误"]}
        }
    },
    /***************************************
     *     URL：/api2/account/join
     ***************************************/
    "account_join": {
        request: {
            typical: {"cid": "XXX","phone":"XXX","status":"first" || "true"}
        },
        response: {
            success: {"提示信息": "加入成功"},
            failed: {"提示信息": "加入失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/account/unjoin
     ***************************************/
    "account_unjoin": {
        request: {
            typical: {"cid": "XXX","phone":"XXX"}
        },
        response: {
            success: {"提示信息": "移除成功"},
            failed: {"提示信息": "移除失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/account/auth
     ***************************************/
    "account_auth": {
        request: {
            typical: {"phone": "XXX", "password": "XXX","longitude":"XXX","latitude":"XXX"}
        },
        response: {
            success: {"提示信息": "账号登录成功",account:{},nowcommunity:{},communities:[{},{},{}],friends:[{"nickName":"XXX","phone":"XXX","head":"XXX"},{},{}]},
            failed: {"提示信息": "账号登录失败", "失败原因": ["手机号不存在" || "密码不正确"]}
        }
    },
    /***************************************
     *     URL：/api2/account/trash
     ***************************************/
    "account_trash": {
        request: {
            typical: {"uid": "XXX", "userlist": [1,2,3], "message": {"content":"xxx","time":"123123123"}}
        },
        response: {
            success: {"提示信息": "消息发送成功"},
            failed: {"提示信息": "消息发送失败", "失败原因": []}
        }
    }
}

/*************************************** ***************************************
 * *    Class：commounity
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/community/add
     ***************************************/
    "community_add": {
        request: {
            typical: {}
        },
        response: {
            success: {"提示信息": "创建服务站成功"},
            failed: {"提示信息": "创建服务站失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/community/getall
     ***************************************/
    "community_getall": {
        request: {
            typical: {}
        },
        response: {
            success: {"提示信息": "获取所有社区成功",communities:[{},{},{}]},
            failed: {"提示信息": "获取所有社区成功", "失败原因": ["无社区数据"]}
        }
    }
}

