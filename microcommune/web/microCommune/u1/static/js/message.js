$(document).ready(function(){
    getMessage();
    $(".chatSend").click(function(){
        $.ajax({
            type:"POST",
            url:"/sendMsg",
            datatype:"json",
            data:{
                touser:"user2",
                text:$("#textInput").val()},
            success:function(data){

            }
        }
        );

    });
});


function getMessage(){
    $.ajax({
        type:"POST",
        url:"/api2/message/get",
        timeout:30000,
        data:{
            "uid":"user1"
        },
        success:function(data){
            proccessMessage(data);
            getmessage();
        },
        failed:function(){
            getMessage();
        }

    });
}

function proccessMessage(data){
    alert(data);
}