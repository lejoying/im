var browerType = getOs().Browser;
$(document).ready(function () {
//    alert(browerType);
//    alert("您的浏览器类型为:" + getOs().Browser);
    window.localStorage.clear();
    window.sessionStorage.clear();
    window.sessionStorage.setItem("wxgs_browerType", browerType);
    $(".js_login").hide();
    var checkDefault = false;
    var selectLoginMode = "js_webcodelogin";
    initHtml();
    $(document).on("mouseenter", ".js_checklogin", function () {
//        initPosition();
        $(".js_login").hide();
        var checkThis = $(this.parentNode);
        if (checkThis.find(".js_applogin").length > 0) {
            $(".js_loginCodeError").html("&nbsp;");
            checkDefault = true;
            if (selectLoginMode != "js_applogin") {
                if (browerType == "Chrome") {
                    animationPlay("js_applogin", -100);
                } else {
                    $(".js_applogin").css({
                        "top": "-200px"
                    });
                    $(".js_applogin").show();
                }
                selectLoginMode = "js_applogin";
            } else {
                checkThis.find(".js_applogin").show();
            }
        } else if (checkThis.find(".js_webcodelogin").length > 0) {
            $(".js_loginCodeError").html("&nbsp;");
            if (checkDefault) {
                if (selectLoginMode != "js_webcodelogin") {
                    if (browerType == "Chrome") {
                        animationPlay("js_webcodelogin", -330);
                    } else {
                        $(".js_webcodelogin").css({
                            "top": "-430px"
                        });
                        $(".js_webcodelogin").show();
                    }
                    selectLoginMode = "js_webcodelogin";
                } else {
                    checkThis.find(".js_webcodelogin").show();
                }
            } else
                checkThis.find(".js_webcodelogin").show();
        } else if (checkThis.find(".js_phonecodelogin").length > 0) {
            checkDefault = true;
            if (selectLoginMode != "js_phonecodelogin") {
                if (browerType == "Chrome") {
                    animationPlay("js_phonecodelogin", -200);
                } else {
                    $(".js_phonecodelogin").css({
                        "top": "-200px"
                    });
                    $(".js_phonecodelogin").show();
                }
                selectLoginMode = "js_phonecodelogin";
            } else {
                checkThis.find(".js_phonecodelogin").show();
            }
        }
    });
    $(document).on("mouseenter", ".js_login", function () {
    });
    $(".js_downloadAppHtml").click(function () {
        location.href = "./app.html";
    });
    $(".js_phoneCodeSubmit").click(function () {
            $(".js_loginCodeError").html("&nbsp;");
            var phone = $(".js_loginPhone").val();
            var code = $(".js_loginCode").val();
            if (phone.trim() == "" || code.trim() == "") {
                $(".js_loginCodeError").html("手机号、验证码不能为空");
                return;
            } else if (isNaN(phone) || isNaN(code)) {
                $(".js_loginCodeError").html("手机号、验证码仅能是数字");
                return;
            } else if (phone.indexOf(" ") != -1 || code.indexOf(" ") != -1) {
                $(".js_loginCodeError").html("手机号、验证码不能含有空格");
                return;
            } else {
                phone = phone.trim();
                $.ajax({
                    type: "GET",
                    url: "/api2/account/verifycode?",
                    data: {
                        phone: phone,
                        code: code
                    },
                    success: function (data) {
                        if (data["提示信息"] == "验证成功") {
                            RSA.setMaxDigits(38);
                            var pbkey = data.PbKey;
                            var pbkey0 = RSA.RSAKey(pbkey);
                            var phone0 = RSA.decryptedString(pbkey0, data.uid);
                            var accessKey0 = RSA.decryptedString(pbkey0, data.accessKey);
                            $.ajax({
                                type: "POST",
                                url: "/api2/account/get?",
                                data: {
                                    phone: phone0,
                                    accessKey: accessKey0,
                                    target: JSON.stringify([phone0])
                                },
                                success: function (data) {
                                    if (data["提示信息"] == "获取用户信息成功") {
                                        var accountData = {};
                                        accountData = (data.accounts)[0];
                                        accountData.accessKey = accessKey0;
                                        accountData.PbKey = pbkey;
                                        window.localStorage.setItem("wxgs_nowAccount", JSON.stringify(accountData));
                                        window.sessionStorage.setItem("wxgs_messageFlag", "none");
                                        window.sessionStorage.setItem("wxgs_tempAccountChatMessages", JSON.stringify({}));
                                        location.href = "./default.html";
                                    } else {
                                        $(".js_loginCodeError").html(data["提示信息"] + "," + data["失败原因"]);
                                    }
                                }
                            });
                        } else {
                            $(".js_loginCodeError").html(data["提示信息"] + "," + data["失败原因"] + ".");
                        }
                    },
                    error: function () {
                        $(".js_loginCodeError").html("网络超时,请重试.");
                    }
                });
            }
//        alert($(".js_loginPhone").val() + "---" + $(".js_loginCode").val());
        }
    )
    ;
//    $(".js_sendPhoneCode").bind("click", getCodeClickEvent());
    $(".js_sendPhoneCode").bind("click", getCodeClickEvent);
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
//    $(".js_qrwebcode").attr("src", "http://qr.liantu.com/api.php?text=mc:weblogin:" + hex_sha1(str));
    $(".js_qrwebcode").attr("src", "http://qr.liantu.com/api.php?text=mc:weblogin:" + hex_sha1(str) +
        "&logo=http://im.lejoying.com/static/images/icon.png&fg=6E6E6E&w=200&m=12");
    longRequest(hex_sha1(str));
});
function getCodeClickEvent() {
    var time = 60;
    $(".js_sendPhoneCode").html("短信已发出,请耐心等候...60");
    var interval = setInterval(function () {
        $(".js_sendPhoneCode").html("短信已发出,请耐心等候..." + --time);
        if (time == 0) {
            $(".js_sendPhoneCode").bind("click", getCodeClickEvent);
            $(".js_sendPhoneCode").html("发送手机验证码");
            window.clearInterval(interval);
        }
    }, 1000);
    $(".js_loginCodeError").html("&nbsp;");
    var phone = $(".js_loginPhone").val();
    if (phone.trim() == "") {
        $(".js_loginCodeError").html("手机号不能为空");
        return;
    } else if (isNaN(phone)) {
        $(".js_loginCodeError").html("手机号仅能是数字");
        return;
    } else if (phone.indexOf(" ") != -1) {
        $(".js_loginCodeError").html("手机号不能含有空格");
        return;
    } else {
        $(".js_sendPhoneCode").unbind("click", getCodeClickEvent);
        $.ajax({
            type: "POST",
            url: "/api2/account/verifyphone?",
            data: {
                phone: phone,
                usage: "login"
            },
            success: function (data) {
                var message = data["提示信息"];
                message = message.substr(message.length - 2);
                if (message == "成功") {
                    $(".js_loginCodeError").html(data["提示信息"]);
                } else {
                    $(".js_loginCodeError").html(data["提示信息"] + "," + data["失败原因"]);
                }
            },
            error: function () {
                $(".js_loginCodeError").html("网络超时,请重试.");
            }
        });
    }
//    alert("正在发送验证码，请稍等片刻....开发中");
}
function longRequest(sessionID) {
    getAccount();
    function getAccount() {
        $.ajax({
            type: "POST",
            timeout: 32000,
            url: "/api2/session/eventwebcodelogin?",
            data: {
                sessionID: sessionID
            },
            success: function (data) {
                if (window.code != undefined) {

                } else if (data["提示信息"] == "web端二维码登录成功") {
//                    getAccountSelfMessage(data.phone, data.accessKey);
                    var phone0 = RSA.decryptedString(pbkey0, data.phone);
                    var accessKey0 = RSA.decryptedString(pbkey0, data.accessKey)
                    getAccountSelfMessage(phone0, accessKey0, data.phone);
                } else {
                    alert(data["提示信息"]);
                }
            },
            error: function (xhr, error) {
                xhr.abort();
//                longRequest(sessionID);
            }
        });
    }

    setInterval(getAccount, 30000);
}
function initHtml() {
    if (browerType == "Chrome") {
        var box = $(".js_webcodelogin");
        var toState = new State();
        toState.translate.y = -330;
        var fromState = new State();
        animateTransform(box[0], fromState, toState, 100, {
            onStart: function () {
            },
            onEnd: function () {
                $(".js_webcodelogin").show();
            }
        });
    } else {
        $(".js_webcodelogin").show();
        $(".js_webcodelogin").css({
            "top": "-430px"
        });
    }
}
function initPosition() {
    $(".js_applogin").css({
        "top": "-100px"
    });
    $(".js_webcodelogin").css({
        "top": "-100px"
    });
    $(".js_phonecodelogin").css({
        "top": "0px"
    });
}
function getOs() {
    var ClientParams = {};

    var Sys = {};
    var ua = navigator.userAgent.toLowerCase();
    var s;
    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
        (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
            (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                    (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
    if (Sys.ie) {
        ClientParams.Browser = "IE";
    }
    else if (Sys.firefox) {
        ClientParams.Browser = "Firefox";
    }
    else if (Sys.chrome) {
        ClientParams.Browser = "Chrome";
    }
    else if (Sys.opera) {
        ClientParams.Browser = "Opera";
    }
    else if (Sys.safari) {
        ClientParams.Browser = "Safari";
    }
    else {
        ClientParams.Browser = "无法检测出您正在使用的浏览器版本!";
        return false;
    }
    return ClientParams;
}
function animationPlay(className, y) {
    var box = $("." + className);
    var toState = new State();
//    toState.scale.x = 0.0;
//    toState.scale.y = 0.0;
    var fromState = new State();
    animateTransform(box[0], fromState, toState, 100,
        {
            onStart: function () {
            },
            onEnd: function () {
                var setTimeOut = setTimeout(function () {
                    $(".js_login").hide();
                    box.show();
                    window.clearTimeout(setTimeOut);
                }, 5);
                var fromState1 = new State(toState);
                var toState1 = new State(toState);
                toState1.translate.y = y;
                toState1.scale.x = 1;
                toState1.scale.y = 1;
                animateTransform(box[0], fromState1, toState1, 100);
            }
        }
    );
}