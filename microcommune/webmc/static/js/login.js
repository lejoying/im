$(document).ready(function () {
    window.localStorage.clear();
    window.sessionStorage.clear();
    $(".js_login").hide();
//    $(".js_webcodelogin").show();
//    animationPlay("js_webcodelogin", -330);
    var checkDefault = false;
    var selectLoginMode = "js_webcodelogin";
    initHtml();
    $(document).on("mouseenter", ".js_checklogin", function () {
        $(".js_login").hide();
        var checkThis = $(this);
        if (checkThis.find(".js_applogin").length > 0) {
            $(".js_loginCodeError").html("");
            checkDefault = true;
//            checkThis.find(".js_applogin").show();
            if (selectLoginMode != "js_applogin") {
                animationPlay("js_applogin", -100);
                selectLoginMode = "js_applogin";
            } else {
                checkThis.find(".js_applogin").show();
            }
        } else if (checkThis.find(".js_webcodelogin").length > 0) {
            $(".js_loginCodeError").html("");
//            checkThis.find(".js_webcodelogin").show();
            if (checkDefault) {
                if (selectLoginMode != "js_webcodelogin") {
                    animationPlay("js_webcodelogin", -330);
                    selectLoginMode = "js_webcodelogin";
                } else {
                    checkThis.find(".js_webcodelogin").show();
                }
            } else
                checkThis.find(".js_webcodelogin").show();
        } else if (checkThis.find(".js_phonecodelogin").length > 0) {
            checkDefault = true;
//            checkThis.find(".js_phonecodelogin").show();
            if (selectLoginMode != "js_phonecodelogin") {
                animationPlay("js_phonecodelogin", -200);
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
            $(".js_loginCodeError").html("");
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
                    type: "POST",
                    url: "/api2/account/get?",
                    data: {
                        phone: phone,
                        accessKey: "lejoying",
                        target: phone
                    },
                    success: function (data) {
                        var accountData = data.account;
                        accountData.accessKey = "lejoying";
                        window.localStorage.setItem("wxgs_nowAccount", JSON.stringify(accountData));
                        location.href = "./default.html";
                    }
                });
            }
//        alert($(".js_loginPhone").val() + "---" + $(".js_loginCode").val());
        }
    )
    ;
    $(".js_sendPhoneCode").click(function () {
        alert("正在发送验证码，请稍等片刻....开发中");
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
    $(".js_qrwebcode").attr("src", "http://qr.liantu.com/api.php?text=mc:weblogin:" + hex_sha1(str));
    $(".js_qrwebcode").attr("src", "http://qr.liantu.com/api.php?text=mc:weblogin:" + hex_sha1(str) +
        "&logo=http://im.lejoying.com/static/images/icon.png&fg=6E6E6E&w=200&m=12");
    longRequest(hex_sha1(str));
});
function longRequest(sessionID) {
    getAccount();
    function getAccount() {
        $.ajax({
            type: "POST",
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
            error: function () {
                request(sessionID);
            }
        });
    }

    setInterval(getAccount, 30000);
}
function initHtml() {
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
                setTimeout(function () {
                    box.show();
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