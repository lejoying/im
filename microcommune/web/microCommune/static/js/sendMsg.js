$(document).ready(function () {
    $.getScript("/static/js/nTenjin.js");
    SetCookie("phone_cookie", "123");
//    alert(GetCookie("wxgs"));
    $.ajax({
        type: "POST",
        url: "/api2/account/getaccount?",
        data: {
            phone: GetCookie("phone_cookie")
        },
        success: function (data) {
//            alert(data);
            $($(".nickName")[0]).html(data.account.nickName);
        }
    });
    var i = 0;
    $("#txl").click(function () {
        $("#conversationContainer").hide();
        $(".js_circlesFriends").show();
        if (i == 0) {
            i =1;
            $.ajax({
                type: "POST",
                url: "/api2/relation/getcirclesandfriends?",
                data: {
                    phone: GetCookie("phone_cookie")
                },
                success: function (data) {
                    if (data["提示信息"] == "获取密友圈成功") {
                        var circles_friends = getTemplate("circles_friends");
                        $(".js_circlesFriends").html(circles_friends.render(data["circles"]));
                    }
                }
            });
        }
    });
    $("#chooseConversationBtn").click(function () {
        $("#conversationContainer").show();
        $(".js_circlesFriends").hide();
    });

    $(".chatSend").click(function () {
        alert($("#textInput").val());
        $.ajax({
                type: "POST",
                url: "/sendMsg",
                datatype: "json",
                data: {text: $("#textInput").val()},
                success: function (data) {
                    alert(data);
                }
            }
        );

    });
});
//根据id获取模版
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
function SetCookie(name, value)
//设定Cookie值
{
    var expdate = new Date();
    var argv = SetCookie.arguments;
    var argc = SetCookie.arguments.length;
    var expires = (argc > 2) ? argv[2] : null;
    var path = (argc > 3) ? argv[3] : null;
    var domain = (argc > 4) ? argv[4] : null;
    var secure = (argc > 5) ? argv[5] : false;
    if (expires != null) expdate.setTime(expdate.getTime() + ( expires * 1000 ));
    document.cookie = name + "=" + escape(value) + ((expires == null) ? "" : ("; expires=" + expdate.toGMTString()))
        + ((path == null) ? "" : ("; path=" + path)) + ((domain == null) ? "" : ("; domain=" + domain))
        + ((secure == true) ? "; secure" : "");
}
function setCookieTime(name, value, time) {
    var strsec = getsec(time);
    var exp = new Date();
    exp.setTime(exp.getTime() + strsec * 1);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}
function getsec(str) {
    alert(str);
    var str1 = str.substring(1, str.length) * 1;
    var str2 = str.substring(0, 1);
    if (str2 == "s") {
        return str1 * 1000;
    } else if (str2 == "h") {
        return str1 * 60 * 60 * 1000;
    } else if (str2 == "d") {
        return str1 * 24 * 60 * 60 * 1000;
    }
}
function GetCookie(name)
//获得Cookie的原始值
{
    var arg = name + "=";
    var alen = arg.length;
    var clen = document.cookie.length;
    var i = 0;
    while (i < clen) {
        var j = i + alen;
        if (document.cookie.substring(i, j) == arg)
            return GetCookieVal(j);
        i = document.cookie.indexOf(" ", i) + 1;
        if (i == 0) break;
    }
    return null;
}
function GetCookieVal(offset)
//获得Cookie解码后的值
{
    var endstr = document.cookie.indexOf(";", offset);
    if (endstr == -1)
        endstr = document.cookie.length;
    return unescape(document.cookie.substring(offset, endstr));
}