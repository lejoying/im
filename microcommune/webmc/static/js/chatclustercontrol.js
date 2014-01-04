$(function () {
    $(".js_morefriend").hide();
    $(".js_morefriend").slideUp();
    $(".js_onlyfriend").slideUp(1);
    $(".js_onlyfriend").slideDown(100, function () {
    });

    $(document).on("click", ".js_chatsendmessage", function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var message = $(".js_chatmessagecontent").val();
        var listPhone = [];
        listPhone.push("122");
        var messageObj = {
            type: "text",
            content: message
        };
        var sendMessage = [
            JSON.stringify({type: "text", time: new Date().getTime(), phone: accountObj.phone, phoneto: JSON.stringify(listPhone), content: message})
        ];
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").append(js_chatmessagetemplate.render(sendMessage));
        $.ajax({
            type: "POST",
            url: "/api2/message/send?",
            data: {
                phone: "121",
                accessKey: "lejoying",
                phoneto: JSON.stringify(listPhone),
                message: JSON.stringify(messageObj)
            },
            success: function (data) {
                if (data["提示信息"] == "发送成功") {
                    setScrollPosition();
                } else {
                    alert(data["提示信息"] + "---" + data["失败原因"]);
                }
            }
        });
    });


//    alert($(".chatItem[un=item_2070333132]").html());//根据属性值获取对象jQuery对象
//    js_chatContents
    getMessages("0", function (data) {
//        console.log(data);
//        console.log(data.messages[0]);
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").html(js_chatmessagetemplate.render(data.messages));
        setScrollPosition();
    });
    keepQuest();
});
function setScrollPosition() {
    var height = (($(".chatScorll").height() / $(".js_chatContents").height()) * $(".chatScorll").height()) - 15;
    $(".scrollDiv").css({
        height: height + "px"
    });
    $(".scrollDiv").css({
        "top": $(".chatScorll").height() - height - 30 + "px"
    });
    $(".js_chatContents").css({
        "top": -($(".js_chatContents").height() - $(".chatScorll").height() + 55) + "px"
    });
}
function keepQuest() {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "GET",
        url: "/api2/session/event?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey
        },
        success: function (data) {
            if (data["提示信息"] == "成功") {
                if (data.event == "message") {
                    alert("message");
                } else if (data.event == "newfriend") {
                    alert("newFriend");
                } else if (data.event == "friendaccept") {
                    alert("friendAccept");
                } else {
                    alert("神器的效果");
                }
            } else {
                keepQuest();
            }
        },
        error: function () {
            keepQuest();
        }
    });
}
function getMessages(flag, next) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "GET",
        url: "/api2/message/get?",
        data: {
            phone: "121",
            accessKey: accountObj.accessKey,
            flag: flag
        },
        success: function (data) {
            next(data);
        }
    });
}
function formattertime(millisecond) {
    var date = new Date(millisecond);
    var Hours = date.getHours().toString().length == 1 ? "0" + date.getHours() : date.getHours();
    var Minutes = date.getMinutes().toString().length == 1 ? "0" + date.getMinutes() : date.getMinutes();
    var Seconds = date.getSeconds().toString().length == 1 ? "0" + date.getSeconds() : date.getSeconds();
    var formattertime = Hours + ":" + Minutes;
    return formattertime;
}
function getTemplate(id) {
    var tenjin = nTenjin;
    var templateDiv = $('.templates #' + id).parent();
    var string = templateDiv.html();
    string = string.replace(/\<\!\-\-\?/g, "<?");
    string = string.replace(/\?\-\-\>/g, "?>");
    string = string.replace(/比较符号大于/g, ">");
    string = string.replace(/比较符号兄小于/g, "<");
    var template = new tenjin.Template();
    template.convert(string);
    return template;
}