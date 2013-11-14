$(document).ready(function () {
//    var base64EncodeChars = "";
//    alert(base64decode(base64EncodeChars));
//    delCookie("phone_cookie");
    window.sessionStorage.clear();
    $(".js_phoneimg").hide();
    var index = 20;
    var timer;
    var i = 0.1;
    $(".js_bgimg").mouseover(function () {
        $("body").css({
            "background-color": "#333"
        });
        clearInterval(timer);
        $(".js_phoneimg").show();
        timer = setInterval(function () {
            $(".js_phoneimg").css({
                "margin-left": index + "px",
                "opacity": i
            });
            index = index + 35;
            if (index % 10 == 0) {
                i = i + 0.1;
            }
            if (index > 640) {
                clearInterval(timer);
            }
        }, 50);
    }).mouseout(function () {
            $("body").css({
                "background-color": "#444"
            });
            clearInterval(timer);
            timer = setInterval(function () {
                $(".js_phoneimg").css({
                    "margin-left": index + "px"
                });
                index = index - 35;
                if (index % 10 == 0) {
                    i = i - 0.1;
                }
                if (index < 220) {
                    clearInterval(timer);
                    $(".js_phoneimg").hide();
                }
            }, 50);
        });
    $(".js_loginp").click(function () {
        $(".js_errorp").html("");
        var phone = $(".js_phonep").val();
//        alert(isNaN(phone)==true);
        var password = $(".js_passp").val();
        if (phone.trim() == "" || password.trim() == "") {
            $(".js_errorp").html("手机号或密码为空，请重新输入!");
        } else if (isNaN(phone) == true) {
            $(".js_errorp").html("手机号不正确,不能为字母或特殊字符!");
        }/* else if (phone.length != 11) {
         $(".js_errorp").html("手机号格式不正确，请重新输入!");
         }*/ else {
            $.ajax({
                type: "POST",
                url: "/api2/account/auth?",
                data: {
                    phone: phone,
                    password: password
                },
                success: function (data) {
                    if (data["提示信息"] == "账号登录成功") {
//                        SetCookie("phone_cookie", data.account.phone);
                        window.localStorage.setItem("wxgs_nowAccount", JSON.stringify(data.account));
                        $(".js_errorp").html(data["提示信息"] + ",用户:" + data.account.phone);
                        location.href = "default.html";
                    } else {
                        $(".js_errorp").html(data["提示信息"] + "," + data["失败原因"]);
                    }
                }
            });
        }
    });
    $(".js_logincode").click(function () {
        var phone = $(".js_phones").val();
        $(".js_errors").html("");
        if (phone.trim() == "") {
            $(".js_errors").html("手机号为空，请重新输入!");
        } else if (isNaN(phone) == true) {
            $(".js_errors").html("手机号格式不正确,不能为字母或特殊字符!");
        }/*else if(phone.length != 11){
         $(".js_errors").html("手机号格式不正确,必须是11位数字!");
         }*/ else {
            $.ajax({
                type: "POST",
                url: "/api2/account/verifyloginphone?",
                data: {
                    phone: phone
                },
                success: function (data) {
                    if (data["提示信息"] == "验证码发送成功") {
                        $(".js_errors").html(data.phone + "获取验证码成功!");
                    } else {
                        $(".js_errors").html(data["提示信息"] + "," + data["失败原因"]);
                    }
                }
            });
        }
    });
    $(".js_logins").click(function () {
        $(".js_errors").html("");
        var phone = $(".js_phones").val();
        var code = $(".js_codes").val();
        if (phone.trim() == "" || code.trim() == "") {
            $(".js_errors").html("手机号或验证码为空，请重新输入!");
        } else if (isNaN(phone) == true || isNaN(code) == true) {
            $(".js_errors").html("手机号或验证码格式不正确,不能为字母或特殊字符!");
        }/* else if (phone.length != 11 || code.length!=6) {
         $(".js_errorp").html("手机号或验证码格式不正确，请重新输入!");
         }*/ else {
            $.ajax({
                type: "POST",
                url: "/api2/account/verifylogincode?",
                data: {
                    phone: phone,
                    code: code
                },
                success: function (data) {
                    if (data["提示信息"] == "登录成功") {
                        SetCookie("phone_cookie", data.account.phone);
                        $(".js_errors").html(data["提示信息"] + "," + data.account.phone);
                        location.href = "default.html";
                    } else {
                        $(".js_errors").html(data["提示信息"] + "," + data["失败原因"]);
                    }
                }
            });
        }
        $(".js_errors").html($(".js_phones").val() + "--" + $(".js_passs").val());
    });
    var chars = [
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
        "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2",
        "3", "4", "5", "6", "7", "8", "9"];
    var count = 20;
    var str = "";
    for (var i = 0; i < count; i++) {
        str += chars[parseInt(Math.random() * chars.length)];
    }
    $(".js_tdcode").attr("src", "http://qr.liantu.com/api.php?text=mc:weblogin:" + hex_sha1(str));
    request(str);
//    $("#tdcode").attr("src", "data:image/jpeg;base64,http://qr.liantu.com/api.php?text="+str);
//    request(str);
//    $("#tdcode").attr("src", "data:image/jpeg;base64,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");
//    request(str);
});
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
function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookie(name);
    if (cval != null) document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}
function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) return unescape(arr[2]);
    else return null;
}
function request(str) {
    var timeold = new Date().getTime();
    $.ajax({
        type: "POST",
        url: "/api2/session/eventweb?",
        timeout: 30000,
        data: {
            accessKey: str
        },
        /*complete: function (XMLHttpRequest, textStatus) {
         XMLHttpRequest.abort();
         alert(textStatus + "---complete--"+XMLHttpRequest);
         },*/
        success: function (data) {
            if (data["提示信息"] == "验证成功") {
                alert("验证成功");
            } else if (data["提示信息"] == "登录成功") {
                alert("登录成功");
                location.href = "default.html";
            } else {
//                request(str);
                alert(data);
                setTimeout(request(str), 30000);
            }
        },
        error: function () {
            setTimeout(request(str), 30000);
//            var timenew = new Date().getTime();
//            alert(timeold+"-dddededdd-"+timenew);
//            alert("error");
        }
    });
}

