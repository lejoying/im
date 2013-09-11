var sessionID;
$(document).ready(function () {
    sessionID = "user1" + new Date().getTime();
    getMessage();
    $(".chatSend").click(function () {

        var date = new Date();

        var hours = date.getHours();
        var minutes = date.getMinutes();

        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? "0" + minutes : minutes;
        var content = $("#textInput").val();

        $("#textInput").val("");
        $("#chat_chatmsglist").append(' <div un="item_2070333141" class="chatItem me">       ' +
            '   <div class="chatItemContent"> <img username="gh_c639eef72f78" click="showProfile" title="生活小助手" un="avatar_gh_c639eef72f78" onerror="reLoadImg(this)" src="static/images/face.jpg" class="avatar">            ' +
            '      <div msgid="2070333141" un="cloud_2070333141" class="cloud cloudText">                                                                                                                           ' +
            '         <div style="" class="cloudPannel"> <div class="sendStatus">   </div>                                                                                                         ' +
            '            <div class="cloudBody">                                                                                                                                                          ' +
            '               <div class="cloudContent">   ' + content + '                                                                                                                                                  ' +
            '                   </div>                                                                                                                                                             ' +
            '                 </div>                                                                                                                                                                ' +
            '                    <div class="cloudArrow "></div>                                                                                                                                 ' +
            '               </div>                                                                                                                                                                    ' +
            '</div>                                                                                                                                                                      ' +
            ' </div>                                                                                                                                                                        ' +
            '  </div> ');

        $.ajax({
                type: "POST",
                url: "/api2/message/send",
                datatype: "json",
                data: {
                    uid: "user1",
                    userlist: JSON.stringify(["user2", "user3"]),
                    messages: JSON.stringify({
                        content: content,
                        time: new Date().getTime()
                    })
                },
                success: function (data) {

                }
            }
        );

    });
});


function getMessage() {
    $.ajax({
        type: "GET",
        url: "/api2/message/get",
        timeout: 30000,
        data: {
            "uid": "user1",
            "sessionID": sessionID
        },
        success: function (data) {
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

function proccessMessage(data) {
    var date = new Date(data.time);

    var hours = date.getHours();
    var minutes = date.getMinutes();

    hours = hours < 10 ? "0" + hours : hours;
    minutes = minutes < 10 ? "0" + minutes : minutes;

    $("#chat_chatmsglist").append('<div un="item_2070333132" class="chatItem you">     ' +
        '<div class="time"> <span class="timeBg left"></span> ' + hours + ':' + minutes + ' <span class="timeBg right"></span> </div>       ' +
        ' <div class="chatItemContent"> <img username="gh_c639eef72f78" click="showProfile" title="云上" un="avatar_gh_c639eef72f78" onerror="reLoadImg(this)" src="static/images/webwxgeticon4.jpg" class="avatar"> <div msgid="2070333132" un="cloud_2070333132" class="cloud cloudText">     ' +
        ' <div style="" class="cloudPannel">                                                                                                                                                                                                                                                                                          ' +
        '   <div class="sendStatus">   </div>                                                                                                                                                                                                                                                                                          ' +
        '   <div class="cloudBody">                                                                                                                                                                                                                                                                                                       ' +
        '      <div class="cloudContent">                                                                                                                                                                                                                                                                                                  ' +
        '           <pre style="white-space:pre-wrap"><img src="/zh_CN/htmledition/images/qqface/65.png">' + data.content + '</pre>                                                                                                                                         ' +
        '           </div>                                                                                                                                                                                                                                                                                                                         ' +
        '        </div>     ' +
        '         <div class="cloudArrow "></div>    ' +
        '      </div>     ' +
        '   </div>   ' +
        '</div>    ' +
        '</div>');
}