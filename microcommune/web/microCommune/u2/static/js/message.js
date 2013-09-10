var sessionID;
$(document).ready(function(){
    sessionID = "user2";// + new Date().getTime();
    getMessage();
    $(".chatSend").click(function(){
        $.ajax({
                type:"POST",
                url:"/sendMsg",
                datatype:"json",
                data:{
                    touser:"user1",
                    text:$("#textInput").val()},
                success:function(data){

                }
            }
        );

    });
});


function getMessage(){
    $.ajax({
        type:"GET",
        url:"/api2/message/get",
        timeout:30000,
        data:{
            "uid":"user2",
            "sessionID":  sessionID
        },
        success:function(data){
            proccessMessage(data);
            getMessage();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if (textStatus == "timeout") {
                getMessage();
            }
        }

    });
}

function proccessMessage(data){
    alert(data);
}