$(function () {
    $(".js_morefriend").hide();
    $(".js_morefriend").slideUp();
    $(".js_onlyfriend").slideUp(1);
    $(".js_onlyfriend").slideDown(100, function () {
    });

    $(document).on("click", ".js_chatsendmessage", function () {
        alert($(".js_chatmessagecontent").val());
    });


//    alert($(".chatItem[un=item_2070333132]").html());//根据属性值获取对象jQuery对象
//    js_chatContents
    getMessages("1", function (data) {
        console.log(data);
        console.log(data.messages[0]);
        var js_chatmessagetemplate = getTemplate("js_chatmessagetemplate");
        $(".js_chatContents").html(js_chatmessagetemplate.render(data.messages));
    });
});
function getMessages(flag, next) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "GET",
        url: "/api2/message/get?",
        data: {
            phone: "15210721344",
            accessKey: accountObj.accessKey,
            flag: flag
        },
        success: function (data) {
            next(data);
        }
    });
}
function formattertime(millisecond) {
    var date = new Date(millisecond);
    var Hours = date.getHours().toString().length == 1 ? "0" + date.getHours() : date.getHours();
    var Minutes = date.getMinutes().toString().length == 1 ? "0" + date.getMinutes() : date.getMinutes();
    var Seconds = date.getSeconds().toString().length == 1 ? "0" + date.getSeconds() : date.getSeconds();
    var formattertime = Hours + ":" + Minutes;
    return formattertime;
}
function getTemplate(id) {
    var tenjin = nTenjin;
    var templateDiv = $('.templates #' + id).parent();
    var string = templateDiv.html();
    string = string.replace(/\<\!\-\-\?/g, "<?");
    string = string.replace(/\?\-\-\>/g, "?>");
    string = string.replace(/比较符号大于/g, ">");
    string = string.replace(/比较符号兄小于/g, "<");
    var template = new tenjin.Template();
    template.convert(string);
    return template;
}