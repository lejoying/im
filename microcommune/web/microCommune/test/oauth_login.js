$(document).ready(function () {
    $.getScript("./../static/js/sha1.js");
    $(".js_login").click(function () {
        var phone = $(".js_phone").val();
        var password = $(".js_password").val();
        if (phone.trim() == "" || password.trim() == "") {
            alert("用户名和密码不能为空");
        } else {
            $.ajax({
                "type": "POST",
                "url": "/api2/account/auth?",
                data: {
                    phone: phone,
                    password: hex_sha1(password)
                },
                success: function (data) {
                    if (data["提示信息"] == "普通鉴权成功") {
                        location.href = window.top.location.href;
                    } else {
                        alert(data["失败原因"]);
                    }
                }
            });
        }
    });
});