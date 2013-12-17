$(document).ready(function () {
    $.getScript("/static/js/nTenjin.js");
    $.getScript("/static/js/setting.js");
    var phone = "121";
    $.ajax({
        type: "POST",
        url: "/api2/relation/getcirclesandfriends?",
        data: {
            phone: "121",
            accessKey: "4e47110a1505d4cd3259091afc176488b31b9cbf"
        },
        success: function (data) {
            if (data["提示信息"] == "获取密友圈成功") {
                var circles_friends = getTemplate("js_circles_friends");
                $(".js_groups").html(circles_friends.render(data.circles));
                window.sessionStorage.setItem("wxgs_circles", JSON.stringify(data.circles));
                var clickGroupIndex = -1;
                $(".appGroup").each(function (i) {
                    $($(".appGroup")[i]).click(function () {
                        var parentClass = this.parentNode.className;
                        var i = parentClass.substr(parentClass.lastIndexOf("_") + 1);
//                        alert((data.circles)[parseInt(i)].name);
                        if (i == clickGroupIndex) {
                            clickGroupIndex = -1;
                            $(".popmenuFrame").slideUp(200);
//                            $(".popmenuFrame")[0].style.visibility = "hidden";
                        } else {
                            $(".popmenuFrame").slideUp(1);
                            clickGroupIndex = i;
                            $(".popmenuFrame").css({
                                visibility: "visible",
                                top: 95 + (Math.floor(i / 3)) * 90 + "px"
                            });
                            $(".sildLeftSharp").css({
                                left: 45 + (i % 3) * 82 + "px"
                            });
//                            alert((data.circles)[i].name);
                            var group_user = getTemplate("js_group_user");
                            $(".sildPopContent").html(group_user.render((data.circles)[i]));
                            $(".user_icon_img1").addClass("js_none");
                            $(".popmenuFrame").slideDown(1000);
                        }
                        $(".schoolmate_txt").slideDown(10);
                        $(".js_modifycirclename").slideUp(10);
                    });
                });
            } else {
                alert(data["提示信息"]);
            }
        }
    });
    $(document).on("click", ".user_icon_add", function () {
        //默认分组的circle_rid == "undefined" 　其他的默认都是数值类型的
        alert("添加好友" + this.getAttribute("circle_rid"));
    });
    $(document).on("click", ".js_addcircle", function () {
        alert("新建密友圈");
    });
    $(document).on("click", ".user_icon", function () {
        var span = $(this).find("span");
//        alert(span.html() + "--" + span.attr("phone"));
    });
    $(".js_modifycirclename").slideUp(10);
    $(document).on("dblclick", ".schoolmate_txt", function () {
        if (this.getAttribute("title") != "默认分组") {
            $(this).slideUp(10);
            $(".js_modifycirclename").slideDown(10);
        }
//            alert("双击分组名称-->" + this.getAttribute("title"));
    });
    $(document).on("mouseup", ".user_icon_img1", function (event) {
        var icon = $(this);
        if (icon.hasClass("js_none")) {
            icon.removeClass("js_none");
            if (event.ctrlKey && event.button == 0) {
                icon.addClass("js_selected");
            } else {
                icon.addClass("js_none");
            }
        } else {
            icon.removeClass("js_selected");
            icon.addClass("js_none");
        }
    });
    $(document).on("click", ".js_modifycirclesubmit", function () {
        $(".schoolmate_txt").slideDown(10);
        $(".js_modifycirclename").slideUp(10);
    });
    $(document).on("click", ".js_modifycirclecancle", function () {
        $(".schoolmate_txt").slideDown(10);
        $(".js_modifycirclename").slideUp(10);
    });
});
function getTemplate(id) {
    var tenjin = nTenjin;
    var templateDiv = $('.templates #' + id).parent();
    var string = templateDiv.html().toString();
    /*    $.get("/static/templates/circles_friends.html", function (templates) {
     //        alert(string);
     alert(string == templates.replace(/\n/g, "").toString());
     });*/
    string = string.replace(/\<\!\-\-\?/g, "<?");
    string = string.replace(/\?\-\-\>/g, "?>");
    string = string.replace(/比较符号大于/g, ">");
    string = string.replace(/比较符号兄小于/g, "<");
    var template = new tenjin.Template();
    template.convert(string);
    return template;
}