function getShare() {
//    $(".photos")[0].append(textNode);
    $.ajax({
        type: "POST",
        timeout: 32000,
        url: "/api2/share/getshare",
        data: {
            gid: Request.QueryString("gid"),
            gsid: Request.QueryString("gsid")
        },
        success: function (data) {
            if (data.shares != undefined && data.shares != null) {
                var contentStr = data.shares[0].content;
                var content = JSON.parse(contentStr);
                for (var index in content) {
                    var shareContent = content[index];
                    if (shareContent.type == "image") {
                        var textNode = document.createElement("img");
                        textNode.src = "http://images2.we-links.com/images/" + shareContent.detail;
                        $(".photos").append(textNode);
                    } else {
                        $(".user-describe pre")[0].innerText = shareContent.detail;
                    }
                }
                var commentsStr = data.shares[0].comments;
                var comments = JSON.parse(commentsStr);
                var comment_list = getTemplate("comment-list");
                $(".user-comment ul").append(comment_list.render(comments));
            }
        },
        error: function (xhr, error) {
        }
    });
}
function getAccount() {
    $.ajax({
        type: "POST",
        timeout: 32000,
        url: "/api2/account/get",
        data: {
            phone: Request.QueryString("phone"),
            target: "[\"" + Request.QueryString("phone") + "\"]"
        }, success: function (data) {
            var account = data.accounts[0];
            $(".user-name")[0].innerText = account.nickName;
            $(".user-mainBusiness")[0].innerText = account.mainBusiness;
            ($("#mask").next())[0].src = "http://images2.we-links.com/heads/" + account.head;
        },
        error: function (xhr, error) {

        }
    });
}
Request = {
    QueryString: function (item) {
        var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]*)(\&?)", "i"));
        return svalue ? svalue[1] : svalue;
    }
}

$(document).ready(function () {
    getAccount();
    getShare();
});
function getTemplate(id) {
    var tenjin = nTenjin;
    var templateDiv = $('.templates .' + id).parent();
    var string = templateDiv.html();
    string = string.replace(/\<\!\-\-\?/g, "<?");
    string = string.replace(/\?\-\-\>/g, "?>");
    string = string.replace(/比较符号大于/g, ">");
    string = string.replace(/比较符号小于/g, "<");
    var template = new tenjin.Template();
    template.convert(string);
    return template;
}
function formattertime(millisecond) {
    var date = new Date(millisecond);
    var Hours = date.getHours().toString().length == 1 ? "0" + date.getHours() : date.getHours();
    var Minutes = date.getMinutes().toString().length == 1 ? "0" + date.getMinutes() : date.getMinutes();
    var Seconds = date.getSeconds().toString().length == 1 ? "0" + date.getSeconds() : date.getSeconds();
    var formattertime = Hours + ":" + Minutes;
    return formattertime;
}