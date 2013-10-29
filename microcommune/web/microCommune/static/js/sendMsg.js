$(document).ready(function(){
    $.ajax({
        type: "POST",
        url: "/api2/account/getaccount?",
        data: {
            phone: "18612450783"
        },
        success: function(data){
//            alert(data);
            $($(".nickName")[0]).html(data.account.nickName);
        }
    });



    $(".chatSend").click(function(){
        alert($("#textInput").val());
        $.ajax({
            type:"POST",
            url:"/sendMsg",
            datatype:"json",
            data:{text:$("#textInput").val()},
            success:function(data){
                alert(data) ;
            }
        }
        );

    });
});