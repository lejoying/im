$(document).ready(function(){
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