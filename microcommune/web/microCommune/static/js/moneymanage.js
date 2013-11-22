$(document).ready(function(){
//    $(".js_moneymanage").hide();
    $(".js_ketingcom").click(function(){
        var js_kiting = $(".js_kiting").val();
        if(isNaN(js_kiting)){
            alert("请输入数字");
        }else{
            var temp = parseFloat(js_kiting)*100;
            if(temp >= 1){
                alert(js_kiting);
                $.ajax({
                    type: "POST",
                    url: "/alipay/alipayto?",
                    data: {
                        money: js_kiting
                    },
                    success: function(data){
                        eval("$.Prompt('"+data+"')");
                    }
                });
            }else{
                alert("不能低于0.01");
            }
        }
    });
});