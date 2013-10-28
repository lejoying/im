$(document).ready(function(){
    $("#phoneimg").hide();
    var index = 20;
    var timer;
    var i = 0.1;
    $("#bgimg").mouseover(function(){
        $("body").css({
            "background-color":"#333"
        });
        clearInterval(timer);
        $("#phoneimg").show();
        timer = setInterval(function(){
            $("#phoneimg").css({
                "margin-left": index+"px",
                "opacity":i
            });
            index= index+35;
            if(index%10 == 0){
                i=i+0.1;
            }
            if(index > 640){
                clearInterval(timer);
            }
        },50);
    }).mouseout(function(){
            $("body").css({
                "background-color":"#444"
            });
            clearInterval(timer);
            timer = setInterval(function(){
                $("#phoneimg").css({
                    "margin-left": index+"px"
                });
                index= index-35;
                if(index%10 == 0){
                    i=i-0.1;
                }
                if(index < 220){
                    clearInterval(timer);
                    $("#phoneimg").hide();
                }
            },50);
    });
    var chars = [
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
        "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2",
        "3", "4", "5", "6", "7", "8", "9"];
    var count1 = 9;
    var count2 = 10;
    var str1 = "";
    var str2 = "";
    for (var i = 0; i < count1; i++) {
        str1 += chars[parseInt(Math.random() * chars.length)];
    }
    for (var i = 0; i < count2; i++) {
        str2 += chars[parseInt(Math.random() * chars.length)];
    }
    str = str1+"w"+str2;
    $("#tdcode").attr("src", "http://qr.liantu.com/api.php?text="+str);
    request(str);
});
function request(str){
    $.ajax({
        type:"POST",
        url: "/api2/session/eventweb?",
        data: {
            accessKey: str
        },
        success: function(data){
            if(data["提示信息"] == "登录成功"){
                alert("登录成功");
                location.href = "default.html";
            }else{
                request(str);
            }
        },
        failed: function(){
            request(str);
        },
        error: function(){
            request(str);
        }
    });
}

