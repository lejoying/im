var dropStatus = "none";
var mouseX = 0;
var mouseY = 0;
$(document).ready(function () {
    (function ($) {
        $.extend($.fn, {
            longPress: function (time, callBack) {
                time = time || 1000;
                var timer = null;
                $(this).mousedown(function (e) {
                    var i = 0;
                    var _this = $(this);
                    timer = setInterval(function () {
                        i += 10;
                        if (i >= time) {
                            clearTimeout(timer);
                            var positionX = e.pageX - _this.offset().left || 0;
                            var positionY = e.pageY - _this.offset().top || 0;
                            typeof callBack == 'function' && callBack.call(_this, e, positionX, positionY);
                        }
                    }, 10)
                }).mouseup(function () {
                        clearTimeout(timer);
                    })
            }
        });
    })(jQuery);
    $.getScript("/static/js/nTenjin.js");
    $.getScript("/static/js/animation.js");
    $.getScript("/static/js/setting.js");
    var phone = "121";
    $.ajax({
        type: "POST",
        url: "/api2/relation/getcirclesandfriends?",
        data: {
            phone: "121",
            accessKey: "lejoying"
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
                        if (i == clickGroupIndex) {
                            clickGroupIndex = -1;
                            $(".popmenuFrame").slideUp(200);
                            if (i > 5) {
                                var box = $("#mainBox");
                                var toState = new State();
                                var fromState = new State();
                                animateTransform(box[0], fromState, toState, 200,
                                    {
                                        onStart: function () {
                                        },
                                        onEnd: function () {
                                            var fromState1 = new State(toState);
                                            var toState1 = new State(toState);
                                            toState1.translate.y = 0;
                                            animateTransform(box[0], fromState1, toState1, 600);
                                        }
                                    }
                                );
                            }
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
                            var group_user = getTemplate("js_group_user");
                            $(".sildPopContent").html(group_user.render((data.circles)[i]));
                            $(".user_icon").longPress(200, function (e, x, y) {
                                if (dropStatus == "down") {
                                    dropStatus = "dropping";
//                                alert("longPress");
                                }
                                var icon1 = $(this);
                                if (icon1.hasClass("js_selected")) {
                                    icon1.removeClass("js_selected");
                                    icon1.addClass("js_moving");
                                    next();
                                }
                                function next() {
                                    icon1.css("top", mouseY - y);
                                    icon1.css("left", mouseX - x);
                                }
                            });
                            $(".user_icon").addClass("js_none");
                            $(".popmenuFrame").slideDown(1000);
                            if (i > 5) {
                                var box = $("#mainBox");
                                var toState = new State();
                                toState.scale.x = 0.5;
                                toState.scale.y = 0.5;
                                var fromState = new State();
                                animateTransform(box[0], fromState, toState, 200,
                                    {
                                        onStart: function () {
                                        },
                                        onEnd: function () {
                                            var fromState1 = new State(toState);
                                            var toState1 = new State(toState);
                                            toState1.translate.y = -((Math.floor(i / 3)) * 90 + 5);
                                            toState1.scale.x = 1;
                                            toState1.scale.y = 1;
                                            animateTransform(box[0], fromState1, toState1, 400);
                                        }
                                    }
                                );
                            }
                        }
                        $(".schoolmate_txt").slideDown(10);
                        $(".js_modifycirclename").slideUp(10);
                    });
                });
            } else {
                alert(data["提示信息"]);//获取密友圈失败的处理
            }
        }
    });
    $("body").mousemove(function (e) {
        if (dropStatus == "dropping") {
            var icon = $(".js_moving");
            if (icon.hasClass("js_moving")) {
                icon.css("top", e.clientY - 42);
                icon.css("left", e.clientX - 32);
            }
        }
        if (dropStatus == "down") {
            mouseX = e.clientX;
            mouseY = e.clientY;
        }
    });
    $("body").mouseup(function (e) {
        if (dropStatus == "dropping") {
//            var iconGroups = $(".js_icon_group_dropping");
//            iconGroups.removeClass("js_icon_group_dropping");
        }
        dropStatus = "none";
        $(".user_icon").removeClass("js_moving");
        $(".user_icon").addClass("js_none");
//        $(".sildPopContent .popappIcon").removeClass("js_moving1");
//        $(".sildPopContent .popappIcon").removeClass("js_moving2");
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
    var oldCircleName = "";
    $(document).on("dblclick", ".schoolmate_txt", function () {
        if (this.getAttribute("title") != "默认分组") {
            $(this).slideUp(10);
            $(".js_modifycirclename").slideDown(10);
            oldCircleName = $(".js_modifycirclename input[type=text]").val();
        }
//            alert("双击分组名称-->" + this.getAttribute("title"));
    });
    $(document).on("mousedown", ".user_icon", function (event) {
        if (dropStatus == "none") {
            dropStatus = "down";
            mouseX = event.clientX;
            mouseY = event.clientY;
        }
    });
    $(document).on("mouseup", ".user_icon", function (event) {
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
            icon.removeClass("js_moving");
            icon.addClass("js_none");
        }
    });
    /*$(document).on("longPress", ".user_icon_img1", function (e, x, y) {
     alert("longPress");
     });*/
    $(document).on("click", ".js_modifycirclesubmit", function () {
        var circle_rid = this.parentNode.getAttribute("circle_rid");
        var newCircleName = $(".js_modifycirclename input[type=text]").val();
        if (oldCircleName == newCircleName) {
            if (oldCircleName.trim() != "") {
                $(".schoolmate_txt").slideDown(10);
                $(".js_modifycirclename").slideUp(10);
            } else {
                alert("不能为空");
            }
        } else {
            if (newCircleName.trim() == "") {
                alert("不能为空");
            } else {
                if (newCircleName.length > 20) {
                    alert("长度不能超过20位");
                } else {
                    modifyCircleName(circle_rid, newCircleName);
                    this.parentNode.innerHTML = "aaa";
                    alert($(this).parent().parent().childNodes);
                    $(".schoolmate_txt").slideDown(10);
                    $(".js_modifycirclename").slideUp(10);
                }
            }
        }
    });
    $(document).on("click", ".js_modifycirclecancle", function () {
        $(".schoolmate_txt").slideDown(10);
        $(".js_modifycirclename").slideUp(10);
        oldCircleName = "";
    });
});
function modifyCircleName(rid, newCircleName) {
    $.ajax({
        type: "POST",
        url: "/api2/circle/modify?",
        data: {
            phone: "121",
            accessKey: "lejoying",
            rid: rid,
            name: newCircleName
        },
        success: function (data) {
            alert(data["提示信息"]);
        }
    });
}
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