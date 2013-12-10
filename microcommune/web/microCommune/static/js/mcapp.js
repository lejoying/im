/**
 * Created with JetBrains WebStorm.
 * User: 小松
 * Date: 13-12-8
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
/*$(document).ready(function () {
 alert("weixing.com");
 });*/
window.onload = function () {
    var RSAKeyStr0 = "5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841#5db114f97e3b71e1316464bd4ba54b25a8f015ccb4bdf7796eb4767f9828841#3e4ee7b8455ad00c3014e82057cbbe0bd7365f1fa858750830f01ca7e456b659";
    RSA.setMaxDigits(38);
    var developID = RSA.encryptedString(RSA.RSAKey(RSAKeyStr0),"123456");
    /*//    var body = document.getElementsByTagName("body");
     var a = document.createElement("a");
     a.href = "http://weixing.com/login.html"
     a.target = "_blank";
     a.id = "js_weixinglogin";
     document.body.appendChild(a);
     var now_account = window.localStorage.getItem("wxgs_nowAccount");
     if (now_account == null) {
     //        document.getElementById("js_weixinglogin").click();
     } else {*/
    var iframe = document.createElement("iframe");
    iframe.style.visibility = "hidden";
    iframe.src = "http://weixing.com/test/part3.html?developID=" + developID + "&notify_url=" + location.href;
    document.body.appendChild(iframe);
//    }
};
var authCallBack = function (phone, accessKey) {
//    alert(accessKey);
    var msg = document.createElement("lable");
    msg.appendChild(document.createTextNode("phont:" + phone + "\naccessKey3:" + accessKey));
    document.body.appendChild(msg);
}