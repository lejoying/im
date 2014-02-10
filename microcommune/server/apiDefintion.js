/*************************************** ***************************************
 * *    Class：session
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/session/eventwebcodelogin
     ***************************************/
    "session_eventwebcodelogin": {
        description: {
            id: 1000000,
            url: "/api2/session/eventwebcodelogin"
        },
        request: {
            typical: {"sessionID": "XXX"}
        },
        response: {
            success: {"提示信息": "web端二维码登录成功", phone: "XXX", accessKey: sha1("phone" + "time")},
            failed: {"提示信息": "web端二维码登录失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/session/notifywebcodelogin
     ***************************************/
    "session_notifywebcodelogin": {
        description: {
            id: 1000001,
            url: "/api2/session/notifywebcodelogin"
        },
        request: {
            typical: {phone: "XX", sessionID: "XXX"}
        },
        response: {
            success: {"information": "notifywebcodelogin success"},
            failed: {"information": "notifywebcodelogin failed"}
        }
    },
    /***************************************
     *     URL：/api2/session/event
     ***************************************/
    "session_event": {
        description: {
            id: 1000002,
            url: "/api2/session/event"
        },
        request: {
            typical: {"phone": "XXX", "accessKey": "XXX"}
        },
        response: {
            success: {"提示信息": "成功", event: "message" || "newfriend" || "friendaccept", event_content: {phone: "XXX", messages: "XXX"}},
            failed: {"提示信息": "失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/session/notify
     ***************************************/
    "session_notify": {
        description: {
            id: 1000003,
            url: "/api2/session/notify"
        },
        request: {
            typical: {phone: "XX", sessionID: "XXX", event: "XXX"}
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
        description: {
            id: 1000100,
            url: "/api2/account/verifyphone"
        },
        request: {
            typical: {"phone": "XXX", usage: "register" || "login"}
        },
        response: {
            success: {"提示信息": "手机号验证成功" || "验证码发送成功", "phone": "XXX", code: sha1("NNN")},
            failed: {"提示信息": "手机号验证失败" || "验证码发送失败", "失败原因": ["手机号不正确", "手机号已被注册", "数据异常", "手机号未注册"]}
        }
    },
    /***************************************
     *     URL：/api2/account/verifycode
     ***************************************/
    "account_verifycode": {
        description: {
            id: 1000101,
            url: "/api2/account/verifycode"
        },
        request: {
            typical: {"phone": "XXX", "code": "XXX"}
        },
        response: {
            success: {"提示信息": "验证成功", "uid": "XXX", accessKey: sha1("phone" + "code"), "PbKey": "XXX"},
            failed: {"提示信息": "验证失败", "失败原因": ["验证码不正确" || "验证码超时" || "手机号不存在" || "数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/account/auth
     ***************************************/
    "account_auth": {
        description: {
            id: 1000102,
            url: "/api2/account/auth"
        },
        request: {
            typical: {"phone": "XXX", "password": "XXX"}
        },
        response: {
            success: {"提示信息": "普通鉴权成功", "uid": "XXX", accessKey: sha1("phone" + "time"), "PbKey": "XXX"},
            failed: {"提示信息": "普通鉴权失败", "失败原因": ["手机号不存在" || "密码不正确" || "数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/account/get
     ***************************************/
    "account_get": {
        description: {
            id: 1000103,
            url: "/api2/account/get"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", target: ["XXX", "XXX", "XXX"]}
        },
        response: {
            success: {"提示信息": "获取用户信息成功", accounts: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取用户信息失败", "失败原因": "用户不存在" || "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/account/modify
     ***************************************/
    "account_modify": {
        description: {
            id: 1000104,
            url: "/api2/account/modify"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", account: JSON.stringify({phone: "XXX", nickName: "XXX", mainBusiness: "XXX", password: "XXX", head: "XXX"})}
        },
        response: {
            success: {"提示信息": "修改用户信息成功"},
            failed: {"提示信息": "修改用户信息失败", "失败原因": "数据异常" || "用户不存在"}
        }
    },
    /***************************************
     *     URL：/api2/account/exit
     ***************************************/
    "account_exit": {
        description: {
            id: 1000105,
            url: "/api2/account/exit"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "退出成功"},
            failed: {"提示信息": "退出失败", "失败原因": "AccessKey Invalid" || "数据异常"}
        }
    }
}

/*************************************** ***************************************
 * *    Class：webcode
 *************************************** ***************************************/
api = {
    /***************************************
     *     URL：/api2/webcode/webcodelogin
     ***************************************/
    "webcode_webcodelogin": {
        description: {
            id: 1000200,
            url: "/api2/webcode/webcodelogin"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", sessionID: "XXX"}
        },
        response: {
            success: {"提示信息": "二维码登录成功", phone: "XXX"},
            failed: {"提示信息": "二维码登录失败", "失败原因": "客户端连接不存在" || "数据异常"}
        }
    }
}
/*************************************** ***************************************
 * *    Class：relation
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/relation/addfriend
     ***************************************/
    "relation_addfriend": {
        description: {
            id: 1000300,
            url: "/api2/relation/addfriend"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", phoneto: "XXX", rid: "XXX", message: "XXX"}
        },
        response: {
            success: {"提示信息": "添加成功" || "发送请求成功"},
            failed: {"提示信息": "添加失败", "失败原因": ["数据异常" || "用户拒绝"]}
        }
    },
    /***************************************
     *     URL：/api2/relation/deletefriend
     ***************************************/
    "relation_deletefriend": {
        description: {
            id: 1000301,
            url: "/api2/relation/deletefriend"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", phoneto: "XXX"}
        },
        response: {
            success: {"提示信息": "删除成功"},
            failed: {"提示信息": "删除失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/relation/blacklist
     ***************************************/
    "relation_blacklist": {
        description: {
            id: 1000302,
            url: "/api2/relation/blacklist"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", phoneto: "XXX"}
        },
        response: {
            success: {"提示信息": "添加黑名单成功"},
            failed: {"提示信息": "添加黑名单失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/relation/getfriends
     ***************************************/
    "relation_getfriends": {
        description: {
            id: 1000303,
            url: "/api2/relation/getfriends"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
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
     *     URL：/api2/relation/getcirclesandfriends
     ***************************************/
    "relation_getcirclesandfriends": {
        description: {
            id: 1000304,
            url: "/api2/relation/getcirclesandfriends"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "获取密友圈成功", circles: [
                {rid: "XX", name: "XX", accounts: [
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
        description: {
            id: 1000305,
            url: "/api2/relation/getaskfriends"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
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
        description: {
            id: 1000306,
            url: "/api2/relation/addfriendagree"
        },
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
     *     URL：/api2/community/add
     ***************************************/
    "community_add": {
        description: {
            id: 1000400,
            url: "/api2/community/add"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", name: "XXX", description: "XXX", longitude: "XXX", latitude: "XXX"}
        },
        response: {
            success: {"提示信息": "创建服务站成功", community: {}},
            failed: {"提示信息": "创建服务站失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/community/find
     ***************************************/
    "community_find": {
        description: {
            id: 1000401,
            url: "/api2/community/find"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", longitude: "XXX", latitude: "XXX"}
        },
        response: {
            success: {"提示信息": "获取成功", community: {}},
            failed: {"提示信息": "获取失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/community/join
     ***************************************/
    "community_join": {
        description: {
            id: 1000402,
            url: "/api2/community/join"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", cid: "XXX"}
        },
        response: {
            success: {"提示信息": "加入成功"},
            failed: {"提示信息": "加入失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/community/unjoin
     ***************************************/
    "community_unjoin": {
        description: {
            id: 1000403,
            url: "/api2/community/unjoin"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", cid: "XXX"}
        },
        response: {
            success: {"提示信息": "退出成功"},
            failed: {"提示信息": "退出失败", "失败原因": ["数据异常"]}
        }
    },
    /***************************************
     *     URL：/api2/community/getcommunities
     ***************************************/
    "community_getcommunities": {
        description: {
            id: 1000404,
            url: "/api2/community/getcommunities"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
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
     *     URL：/api2/community/getcommunityfriends
     ***************************************/
    "community_getcommunityfriends": {
        description: {
            id: 1000405,
            url: "/api2/community/getcommunityfriends"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", name: "XXX"}
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
        description: {
            id: 1000500,
            url: "/api2/circle/modify"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", rid: "XXX", circleName: "XXX"}
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
        description: {
            id: 1000501,
            url: "/api2/circle/delete"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", rid: "XXX"}
        },
        response: {
            success: {"提示信息": "删除成功"},
            failed: {"提示信息": "删除失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/circle/moveorout
     ***************************************/
    "circle_moveorout": {
        description: {
            id: 1000502,
            url: "/api2/circle/delete"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", phoneto: "XXX", rid: "XXX", filter: ["REMOVE", "SHIFTIN"]}
        },
        response: {
            success: {"提示信息": "移出成功" || "移入成功"},
            failed: {"提示信息": "移出失败" || "移入失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/circle/moveout
     ***************************************/
    "circle_moveout": {
        description: {
            id: 1000503,
            url: "/api2/circle/moveout"
        },
        request: {
            typical: {phone: "XXX", phoneto: ["XXX", "XXX", "XXX"], accessKey: "XXX", oldrid: "XXX", newrid: "XXX"}
        },
        response: {
            success: {"提示信息": "移动成功"},
            failed: {"提示信息": "移动失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/circle/addcircle
     ***************************************/
    "circle_addcircle": {
        description: {
            id: 1000504,
            url: "/api2/circle/addcircle"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", circleName: "XXX"}
        },
        response: {
            success: {"提示信息": "添加成功", circle: {rid: "NNN", name: "XXX"}},
            failed: {"提示信息": "添加失败", "失败原因": "数据异常"}
        }
    }
}
/*************************************** ***************************************
 * *    Class：message
 *************************************** ***************************************/
api = {
    /***************************************
     *     URL：/api2/message/send
     ***************************************/
    "message_send": {
        description: {
            id: 1000600,
            url: "/api2/message/send"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", phoneto: ["XXX", "XXX", "XXX"], gid: "XXX", sendType: ["point" || "group" || "tempGroup"], message: {contentType: "text" || "image" || "voice", content: "XXX"}}
        },
        response: {
            success: {"提示信息": "发送成功", time: "XXX"},
            failed: {"提示信息": "发送失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/message/get
     ***************************************/
    "message_get": {
        description: {
            id: 1000601,
            url: "/api2/message/get"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", flag: "NNN"}
        },
        response: {
            success: {"提示信息": "获取成功", messages: [
                {type: "text" || "image" || "voice", phone: "NNN", phoneto: "NNN", content: "XXX", time: new Date().getTime(), flag: "NNN"}
            ]},
            failed: {"提示信息": "获取失败", "失败原因": "数据异常"}
        }
    }
}
/*************************************** ***************************************
 * *    Class：image
 *************************************** ***************************************/
api = {
    /***************************************
     *     URL：/image/upload
     ***************************************/
    "image_upload": {
        description: {
            id: 1000700,
            url: "/image/upload"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", filename: "XXX", imagedata: "XXX"}
        },
        response: {
            success: {"提示信息": "图片上传成功", "filename": "XXX"},
            failed: {"提示信息": "图片上传失败", "失败原因": "数据异常"}
        }
    },
    /***************************************
     *     URL：/image/check
     ***************************************/
    "image_check": {
        description: {
            id: 1000701,
            url: "/image/check"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", filename: "XXX"}
        },
        response: {
            success: {"提示信息": "查找成功", "filename": "XXX", "exists": true || false},
            failed: {"提示信息": "查找失败", "失败原因": "数据不完整"}
        }
    },
    /***************************************
     *     URL：/image/get
     ***************************************/
    "image_get": {
        description: {
            id: 1000702,
            url: "/image/get"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", filename: "XXX"}
        },
        response: {
            success: {"提示信息": "获取图片成功", "image": "XXX"},
            failed: {"提示信息": "获取图片失败", "失败原因": "数据异常"}
        }
    }
}
/*************************************** ***************************************
 * *    Class：alipay
 *************************************** ***************************************/
api = {
    /***************************************
     *     URL：/alipay/alipayto
     ***************************************/
    "alipay_alipayto": {
        description: {
            id: 1000800,
            url: "/alipay/alipayto"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", money: "NNN"}
        },
        response: {
            success: {"提示信息": "获取成功", "url": "XXX"},
            failed: {}
        }
    },
    /***************************************
     *     URL：/alipay/paynotify
     ***************************************/
    "alipay_paynotify": {
        description: {
            id: 1000801,
            url: "/alipay/paynotify"
        },
        request: {
            typical: {}
        },
        response: {
            success: "success",
            failed: "fail"
        }
    },
    /***************************************
     *     URL：/alipay/send_goods_confirm_by_platform
     ***************************************/
    "alipay_send_goods_confirm_by_platform": {
        description: {
            id: 1000802,
            url: "/alipay/send_goods_confirm_by_platform"
        },
        request: {
            typical: {trade_no: "NNN"}
        },
        response: {
            success: "success",
            failed: "fail"
        }
    }
}
/*************************************** ***************************************
 * *    Class：sms
 *************************************** ***************************************/
api = {
    /***************************************
     *     URL：/api2/sms/event
     ***************************************/
    "sms_event": {
        description: {
            id: 1000900,
            url: "/api2/sms/event"
        },
        request: {
            typical: {sessionID: "XXX"}
        },
        response: {
            success: {"information": "event success", phone: "XXX", message: "XXX"},
            failed: {"information": "event failed"}
        }
    },
    /***************************************
     *     URL：/api2/sms/notify
     ***************************************/
    "sms_notify": {
        description: {
            id: 1000901,
            url: "/api2/sms/notify"
        },
        request: {
            typical: {phone: "XXX", messgae: "XXX"}
        },
        response: {
            success: {"information": "notify success"},
            failed: {"information": "notify failed"}
        }
    }
}
/*************************************** ***************************************
 * *    Class：group
 *************************************** ***************************************/
api = {
    /***************************************
     *     URL：/api2/group/create
     ***************************************/
    "group_create": {
        description: {
            id: 1001000,
            url: "/api2/group/create"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", type: ["createTempGroup", "createGroup", "upgradeGroup"], tempGid: "XXX", name: "XXX", members: ["XXX", "XXX", "XXX"]}
        },
        response: {
            success: {"提示信息": "创建群组成功", group: {}},
            failed: {"提示信息": "创建群组失败", "失败原因": "数据异常" || "用户不存在"}
        }
    },
    /***************************************
     *     URL：/api2/group/addmembers
     ***************************************/
    "group_addmembers": {
        description: {
            id: 1001001,
            url: "/api2/group/addmembers"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", gid: "XXX", members: ["XXX", "XXX", "XXX"]}
        },
        response: {
            success: {"提示信息": "加入群组成功"},
            failed: {"提示信息": "加入群组失败", "失败原因": "群组不存在" || "数据异常" || "数据格式不正确" || "好友不存在"}
        }
    },
    /***************************************
     *     URL：/api2/group/removemembers
     ***************************************/
    "group_removemembers": {
        description: {
            id: 1001002,
            url: "/api2/group/removemembers"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", gid: "XXX", members: ["XXX", "XXX", "XXX"]}
        },
        response: {
            success: {"提示信息": "退出群组成功"},
            failed: {"提示信息": "退出群组失败", "失败原因": "群组不存在" || "数据异常" || "好友不存在该组" || "数据格式不正确"}
        }
    },
    /***************************************
     *     URL：/api2/group/getallmembers
     ***************************************/
    "group_getallmembers": {
        description: {
            id: 1001003,
            url: "/api2/group/getallmembers"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", gid: "XXX"}
        },
        response: {
            success: {"提示信息": "获取群组成员成功", members: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取群组成员失败", "失败原因": "群组不存在" || "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/group/modify
     ***************************************/
    "group_modify": {
        description: {
            id: 1001004,
            url: "/api2/group/modify"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", gid: "XXX", name: "XXX"}
        },
        response: {
            success: {"提示信息": "修改群组信息成功", group: {}},
            failed: {"提示信息": "修改群组信息失败", "失败原因": "群组不存在" || "数据异常"}
        }
    },
    /***************************************
     *     URL：/api2/group/getusergroups
     ***************************************/
    "group_getusergroups": {
        description: {
            id: 1001005,
            url: "/api2/group/getusergroups"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", target: "XXX"}
        },
        response: {
            success: {"提示信息": "获取好友群组成功", groups: []},
            failed: {"提示信息": "获取好友群组失败", "失败原因": "数据异常" || "好友不存在"}
        }
    },
    /***************************************
     *     URL：/api2/group/get
     ***************************************/
    "group_get": {
        description: {
            id: 1001006,
            url: "/api2/group/get"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX", gid: "XXX", type: ["group" || "tempGroup"]}
        },
        response: {
            success: {"提示信息": "获取群组信息成功", group: {}},
            failed: {"提示信息": "获取群组信息失败", "失败原因": "数据异常" || "群组不存在" || "数据格式不正确"}
        }
    },
    /***************************************
     *     URL：/api2/group/getgroupsandmembers
     ***************************************/
    "group_getgroupsandmembers": {
        description: {
            id: 1001007,
            url: "/api2/group/getgroupsandmembers"
        },
        request: {
            typical: {phone: "XXX", accessKey: "XXX"}
        },
        response: {
            success: {"提示信息": "获取群组成功", groups: [
                {},
                {},
                {}
            ]},
            failed: {"提示信息": "获取群组失败", "失败原因": "数据异常"}
        }
    }
}
