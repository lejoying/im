var selectPanel = "js_tempChatTop";
var scrollFlagTempChatTop = false;
var image = new Image();
$(function () {//show js_modify_jcrophead_show
    image.src = "/static/images/face_man.png";

    $(".js_modifyAccountHeadImgPanel").hide();
    $(".scrollDiv1").hide();
    $(".js_tempChatTop").addClass("conmuButton_chatStyle");
    $(".js_mainContent").slideUp(1);
    $(".js_js_tempChatMainContent").slideDown(1);
    $(document).on("click", ".js_circlesTop", function () {
        /*$(".js_onlyfriend").slideUp(500, function () {
         $(".js_morefriend").show();
         });
         $(".js_morefriend").slideDown(500, function () {
         });*/
    });
    $(".js_tempChatTop").click(function () {
        $(".js_tempChatTop").addClass("conmuButton_chatStyle");
        $(".js_circlesTop").removeClass("conmuButtonStyle");
        $(".js_clustersTop").removeClass("closefriend_iconStyle");

        $(".js_mainContent").slideUp(1);
        $(".js_js_tempChatMainContent").slideDown(1);
        selectPanel = "js_tempChatTop";
    });
    $(".js_circlesTop").click(function () {
        $("#conversationListContent").css({
            top: "0px"
        });
        $(".js_tempChatTop").removeClass("conmuButton_chatStyle");
        $(".js_circlesTop").addClass("conmuButtonStyle");
        $(".js_clustersTop").removeClass("closefriend_iconStyle");
        $(".js_mainContent").slideUp(1);
        $(".js_circlesFriends").slideDown(1);
        selectPanel = "js_circlesTop";
    });
    $(".js_clustersTop").click(function () {
        $("#conversationListContent").css({
            top: "0px"
        });
        $(".js_tempChatTop").removeClass("conmuButton_chatStyle");
        $(".js_circlesTop").removeClass("conmuButtonStyle");
        $(".js_clustersTop").addClass("closefriend_iconStyle");
        $(".js_mainContent").slideUp(1);
        $(".js_clusterMainContent").slideDown(1);
        selectPanel = "js_clustersTop";
    });
    $(".js_appManageTop").click(function () {
        $("#conversationListContent").css({
            top: "0px"
        });
        $(".js_tempChatTop").removeClass("conmuButton_chatStyle");
        $(".js_circlesTop").removeClass("conmuButtonStyle");
        $(".js_clustersTop").removeClass("closefriend_iconStyle");
        $(".js_mainContent").slideUp(1);
        $(".js_appManageMainContent").slideDown(1);
        selectPanel = "js_appManageTop";
    });
    $(".js_loadingTempData").click(function () {
        alert("九妹正在为您努力加载数据");
    });

    $(".js_Account_HeadImg").click(function () {
        $("#js_modify_head_file").attr("src", "/static/images/face_man.png");
        $("#js_modify_jcrophead_show").attr("src", "/static/images/face_man.png");
        /*$("#js_modify_head_file").css({
         "width": "234px",
         "height": "234px"
         });*/
        if ($(".js_modifyAccountHeadImgPanel").css('display') != "block") {
            $(".js_modifyAccountHeadImgPanel").css({
                "top": "40px",
                "left": "0px"
            });
            var browerType = window.sessionStorage.getItem("wxgs_browerType");
//            $(".js_modifyAccountHeadImgPanel").show();
//            alert(browerType);
            if (browerType == "Chrome") {
                var box = $(".js_modifyAccountHeadImgPanel");
                var toState = new State();
                toState.scale.x = 0.0;
                toState.scale.y = 0.0;
                var fromState = new State();
                animateTransform(box[0], fromState, toState, 1,
                    {
                        onStart: function () {
                        },
                        onEnd: function () {
                            var setTimeOut = setTimeout(function () {
                                box.show();
                                window.clearTimeout(setTimeOut);
                            }, 1);
                            var fromState1 = new State(toState);
                            var toState1 = new State(toState);
                            toState1.translate.x = 415;
                            toState1.translate.y = 110;
                            toState1.scale.x = 1;
                            toState1.scale.y = 1;
                            animateTransform(box[0], fromState1, toState1, 200);
                        }
                    }
                );
            } else {
                $(".js_modifyAccountHeadImgPanel").css({
                    "top": "150px",
                    "left": "415px"
                });
                $(".js_modifyAccountHeadImgPanel").show();
            }
        }
        $("#js_modify_head_file").Jcrop();
        /*$("#js_modify_head_file").Jcrop({
         onChange: showPreview,
         onSelect: showPreview,
         aspectRatio: 1,
         setSelect: [0, 0, 100, 100]
         });*/
    });
    $(".js_modify_head_close").click(function () {
        $(".js_modifyAccountHeadImgPanel").hide();
    });

//    new Drag($("#js_modifyAccountHeadImgPanel")[0]);
});
//简单的事件处理程序，响应自onChange,onSelect事件，按照上面的Jcrop调用
function showPreview(coords) {
    if (parseInt(coords.w) > 0) {
        var cx = image.width / 250;
        var cy = image.height / 250;
        var cwidth = Math.round(coords.w * cx);
        var cheight = Math.round(coords.h * cy);
        var ax = coords.x * cx;
        var ay = coords.y * cy;
        var temp_ctx, temp_canvas;
        temp_canvas = document.createElement('canvas');
        temp_ctx = temp_canvas.getContext('2d');
        temp_canvas.width = 100
        temp_canvas.height = 100;
        temp_ctx.drawImage(image, ax, ay, cwidth, cheight, 0, 0, 100, 100);
        vData = temp_canvas.toDataURL();
        tempData = temp_canvas.toDataURL();
        $("#js_modify_jcrophead_show").attr('src', vData);
    }
}
function Drag(o) {
    var rDrag = {
        o: null,
        init: function (o) {
            o.onmousedown = this.start;
        },
        start: function (e) {
            var o;
            e = rDrag.fixEvent(e);
            e.preventDefault && e.preventDefault();
            rDrag.o = o = this;
            o.x = e.clientX - rDrag.o.offsetLeft;
            o.y = e.clientY - rDrag.o.offsetTop;
            document.onmousemove = rDrag.move;
            document.onmouseup = rDrag.end;
        },
        move: function (e) {
            e = rDrag.fixEvent(e);
            var oLeft, oTop;
            oLeft = e.clientX - rDrag.o.x;
            oTop = e.clientY - rDrag.o.y;
            rDrag.o.style.left = oLeft + 'px';
            rDrag.o.style.top = oTop + 'px';
        },
        end: function (e) {
            e = rDrag.fixEvent(e);
            rDrag.o = document.onmousemove = document.onmouseup = null;
        },
        fixEvent: function (e) {
            if (!e) {
                e = window.event;
                e.target = e.srcElement;
                e.layerX = e.offsetX;
                e.layerY = e.offsetY;
            }
            return e;
        }
    }
    rDrag.init(o);
}
