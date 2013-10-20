$(document).ready(function(){
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
    $("#tdcode").attr("src", "http://qr.liantu.com/api.php?text="+str);
    request(str);
});
function request(str){
    $.ajax({
        type:"POST",
        url: "/api2/session/eventweb?",
        data: {
            sessionID: str
        },
        success: function(data){
            if(data["提示信息"] == "登录成功"){
                alert("登录成功");
                location.href = "default.html";
            }else{
                request(str);
            }
            request(str);
        },
        failed: function(){
            request(str);
        },
        error: function(){
            request(str);
        }
    });
}

