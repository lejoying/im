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
});
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
    box.show();
    var toState = new State();
    toState.scale.x = 0.1;
    toState.scale.y = 0.1;
    var fromState = new State();
    animateTransform(box[0], fromState, toState, 100,
        {
            onStart: function () {
            },
            onEnd: function () {
                var fromState1 = new State(toState);
                var toState1 = new State(toState);
                toState1.translate.y = y;
                toState1.scale.x = 1;
                toState1.scale.y = 1;
                animateTransform(box[0], fromState1, toState1, 400);
            }
        }
    );
}