$(document).ready(function () {
//    var RSAKeyStr = window.localStorage.getItem("wxgs_PbKey");
    var developID = Request("developID");
    var now_Account = window.localStorage.getItem("wxgs_nowAccount");
    if (now_Account == null) {
//        alert("尚未登录微型公社");
//        window.open("http://weixing.com/login.html");
        window.top.location.href = "http://weixing.com/login.html";
    } else {
        now_Account = JSON.parse(now_Account);
        var phone = now_Account.phone;
        var accessKey = now_Account.accessKey;
        var notify_url = Request("notify_url");
        var url = notify_url.substr(0, notify_url.indexOf(".") + 4);//http://oauth.com
//    window.top.alert(developID);
//        alert(url);
        $.ajax({
            type: "POST",
            url: "/api2/account/oauth6?",
            data: {
                phone: phone,
                accessKey: accessKey,
                developID: developID
            },
            success: function (data) {
//            alert(window.top.location.href);
                var iframe = document.createElement("iframe");
                iframe.style.visibility = "hidden";
                iframe.src = url + "/web_auth.html?developID=" + developID + "&phone=" + phone + "&accessKey3=" + data.accessKey3;
                document.body.appendChild(iframe);
            }
        });
    }
});
function Request(strName) {
    var strHref = window.document.location.href;
    var intPos = strHref.indexOf("?");
    var strRight = strHref.substr(intPos + 1);
    var arrTmp = strRight.split("&");
    for (var i = 0; i < arrTmp.length; i++) {
        var arrTemp = arrTmp[i].split("=");
        if (arrTemp[0].toUpperCase() == strName.toUpperCase()) return arrTemp[1];
    }
    return "";
}