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
    var developID = 1234567;
    var phone = "121";
    var accessKey = "3534de4826313ffb010e6fc081f93b467bfdb8e3";
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
    iframe.src = "http://weixing.com/test/part3.html?developID=" + developID + "&phone=" + phone + "&accessKey=" + accessKey + "&notify_url=" + location.href;
    document.body.appendChild(iframe);
//    }
};
var authCallBack = function(phone, accessKey){
//    alert(accessKey);
    var msg = document.createElement("lable");
    msg.appendChild(document.createTextNode(accessKey));
    document.body.appendChild(msg);
}