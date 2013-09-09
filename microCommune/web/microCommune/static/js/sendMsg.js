$(document).ready(function(){
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