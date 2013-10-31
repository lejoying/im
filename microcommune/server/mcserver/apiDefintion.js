/*************************************** ***************************************
 * *    Class：session
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/session/event
     ***************************************/
    "session_eventweb": {
        request: {
            typical: {"accessKey": "XXX"}
        },
        response: {
            success: {"提示信息": "成功"},
            failed: {"提示信息": "失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/session/event
     ***************************************/
    "session_event": {
        request: {
            typical: {"phone": "XXX", "accessKey": "XXX"}
        },
        response: {
            success: {"提示信息": "成功"},
            failed: {"提示信息": "失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/session/nitify
     ***************************************/
    "session_notify": {
        request: {
            typical: {}
        },
        response: {
            success: {"information": "notify success"},
            failed: {}
        }
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
            success: {"提示信息": "手机号验证成功", "phone": "XXX"},
            failed: {"提示信息": "手机号验证失败", "失败原因": ["手机号不正确", "手机号已被注册"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifycode
     ***************************************/
    "account_verifycode": {
        request: {
            typical: {"phone": "XXX", "code": "XXX"}
        },
        response: {
            success: {"提示信息": "验证成功", "phone": "XXX"},
            failed: {"提示信息": "验证失败", "失败原因": ["验证码不正确" || "验证码超时"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifyloginphone
     ***************************************/
    "account_verifyloginphone": {
        request: {
            typical: {"phone": "XXX"}
        },
        response: {
            success: {"提示信息": "验证码发送成功", "phone": "XXX"},
            failed: {"提示信息": "验证码发送失败", "失败原因": ["服务器异常", "手机号未注册"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifylogincode
     ***************************************/
    "account_verifylogincode": {
        request: {
            typical: {"phone": "XXX", "code": "XXX"}
        },
        response: {
            success: {"提示信息": "登录成功", account: {}},
            failed: {"提示信息": "登录失败", "失败原因": ["验证码不正确" || "验证码超时"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifypass
     ***************************************/
    "account_verifypass": {
        request: {
            typical: {"phone": "XXX", "password": "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "注册成功", account: {}},
            failed: {"提示信息": "注册失败", "失败原因": ["保存数据遇到错误"]}
        }
    },
    /***************************************
     *     URL：/api2/account/auth
     ***************************************/
    "account_auth": {
        request: {
            typical: {"phone": "XXX", "password": "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "账号登录成功", account: {}},
            failed: {"提示信息": "账号登录失败", "失败原因": ["手机号不存在" || "密码不正确"]}
        }
    },
    /***************************************
     *     URL：/api2/account/exit
     ***************************************/
    "account_exit": {
        request: {
            typical: {}
        },
        response: {
            success: {"提示信息": "消息发送成功"},
            failed: {"提示信息": "消息发送失败", "失败原因": []}
        }
    },
    /***************************************
     *     URL：/api2/account/verifywebcode
     ***************************************/
    "account_verifywebcode": {
        request: {
            typical: {accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "二维码验证成功"},
            failed: {"提示信息": "二维码验证失败", "失败原因": ["二维码超时"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifywebcodelogin
     ***************************************/
    "account_verifywebcodelogin": {
        request: {
            typical: {phone: "XXX", accessKey: "", status: true || false}
        },
        response: {
            success: {"提示信息": "登录成功"},
            failed: {"提示信息": "登录失败", "失败原因": ["二维码超时" || "取消登录"]}
        }
    },
    /***************************************
     *     URL：/api2/account/getaccount
     ***************************************/
    "account_getaccount": {
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "获取成功", account: {}},
            failed: {"提示信息": "获取失败", "失败原因": "用户不存在"}
        }
    },
    /***************************************
     *     URL：/api2/account/modify
     ***************************************/
    "account_modify": {
        request: {
            typical: {phone: "XXX", accessKey: "XXX", nickName: "XXX", mainBusiness: "XXX"}
        },
        response: {
            success: {"提示信息": "修改成功"},
            failed: {"提示信息": "修改失败", "失败原因": "数据异常"}
        }
    }
}

/*************************************** ***************************************
 * *    Class：relation
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/relation/join
     ***************************************/
    "relation_join": {
        request: {
            typical: {"cid": "XXX", "phone": "XXX"}
        },
        response: {
            success: {"提示信息": "加入成功"},
            failed: {"提示信息": "加入失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/addfriend
     ***************************************/
    "relation_addfriend": {
        request: {
            typical: {"phone": "XXX", "phoneto": "XXX", rid: "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "发送请求成功"},
            failed: {"提示信息": "发送请求失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/getfriends
     ***************************************/
    "relation_getfriends": {
        request: {
            typical: {"phone": "XXX"}
        },
        response: {
            success: {"提示信息": "获取好友成功", friends: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取好友失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/getcommunities
     ***************************************/
    "relation_getcommunities": {
        request: {
            typical: {"phone": "XXX"}
        },
        response: {
            success: {"提示信息": "获取社区成功", communities: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取社区失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/addcircle
     ***************************************/
    "relation_addcircle": {
        request: {
            typical: {"phone": "XXX", circleName: "XXX"}
        },
        response: {
            success: {"提示信息": "添加成功"},
            failed: {"提示信息": "添加失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/relation/getcirclesandfriends
     ***************************************/
    "relation_getcirclesandfriends": {
        request: {
            typical: {"phone": "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "获取密友圈成功", circles: [
                {circle: "", accounts: [
                    {},
                    {},
                    {}
                ]},
                {},
                {}
            ]},
            failed: {"提示信息": "获取密友圈失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/getaskfriends
     ***************************************/
    "relation_getaskfriends": {
        request: {
            typical: {"phone": "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "获取好友请求成功", accounts: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取好友请求失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/addfriendagree
     ***************************************/
    "relation_addfriendagree": {
        request: {
            typical: {"phone": "XXX", accessKey: "XXX", phoneask: "XXX", rid: "XXX", status: true || false}
        },
        response: {
            success: {"提示信息": "添加成功"},
            failed: {"提示信息": "添加失败", "失败原因": ["数据异常"]}
        }
    }
}
/*************************************** ***************************************
 * *    Class：commounity
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/community/find
     ***************************************/
    "community_find": {
        request: {
            typical: {longitude: "XXX", latitude: "XXX"}
        },
        response: {
            success: {"提示信息": "获取成功", community: {}},
            failed: {"提示信息": "获取失败", "失败原因": "社区不存在", community: {}}
        }
    },
    /***************************************
     *     URL：/api2/community/getcommunityfriends
     ***************************************/
    "community_getcommunityfriends": {
        request: {
            typical: {name: "XXX"}
        },
        response: {
            success: {"提示信息": "获取社区好友成功", accounts: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取社区好友失败", "失败原因": "数据异常"}
        }
    }
}
/*************************************** ***************************************
 * *    Class：circle
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/circle/modify
     ***************************************/
    "circle_modify": {
        request: {
            typical: {rid: "XXX", circleName: "XXX"}
        },
        response: {
            success: {"提示信息": "修改成功"},
            failed: {"提示信息": "修改失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/circle/delete
     ***************************************/
    "circle_delete": {
        request: {
            typical: {rid: "XXX"}
        },
        response: {
            success: {"提示信息": "删除成功"},
            failed: {"提示信息": "删除失败", "失败原因": "数据异常"}
        }
    }
}

