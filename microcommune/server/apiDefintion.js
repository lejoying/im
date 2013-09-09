/*************************************** ***************************************
 * *    Class：message
 *************************************** ***************************************/

api = {
    /***************************************
     *     URL：/api2/message/send
     ***************************************/
    "message_send": {
        request: {
            typical: {"uid": "XXX", "userlist": [1,2,3], "message": {"content":"xxx","time":"123123123","timeline":"xxx"}}
        },
        response: {
            success: {"提示信息": "消息发送成功"},
            failed: {"提示信息": "消息发送失败", "失败原因": []}
        }
    },
    /***************************************
     *     URL：/api2/message/get
     ***************************************/
    "message_get": {
        request: {
            typical: {"uid": "XXX"}
        },
        response: {
            success: {"提示信息": "消息获取成功", "messages":[{"content":"xxx","time":"123123123","timeline":"xxx"},{"content":"xxx","time":"123123123","timeline":"xxx"}]},
            failed: {"提示信息": "消息获取失败", "失败原因": []}}
        }
    }
