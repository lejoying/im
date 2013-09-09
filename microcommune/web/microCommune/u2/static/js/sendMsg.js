$(document).ready(function(){
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