var selectPanel = "js_tempChatTop";
var scrollFlagTempChatTop = false;
var image = new Image();
var vData;
var loadImageFlag = false;
var imageServer = window.globaldata.serverSetting.imageServer;
$(function () {//show js_modify_jcrophead_show
    var img = new Image();
    /*$.ajax({
     type: "GET",
     url: "/image/get?",
     data: {
     phone: '121',
     accessKey: "lejoying",
     filename: "2a90e39e7a8d7b380fcec6cffd59172729b3cf49.png"
     },
     success: function (data) {
     alert(data["提示信息"]);
     image.src = data.image;
     }
     });*/
//    image.src = "http://im.lejoying.com/static/images/face_man.png";
    image.src = "/static/images/face_man.png";
//    img.crossOrigin = "*";
//    image.src = "/static/images/face_man.png";
//    img.crossOrigin = "Anonymous";

    img.onload = function () {
        console.log(img.complete);
        console.log("img:" + img.height + "-" + img.width + "--");
        var temp_ctx, temp_canvas;
        temp_canvas = document.createElement('canvas');
        temp_canvas.style.marginTop = "-1800px";
        temp_ctx = temp_canvas.getContext('2d');
        temp_canvas.width = img.width;
        temp_canvas.height = img.height;
        temp_ctx.drawImage(img, 0, 0);
        document.body.appendChild(temp_canvas);
        console.log(temp_canvas);
        //        var data = (temp_canvas.toDataURL("image/png"));
        var data = (temp_canvas.toDataURL("image/png"));
        //        image.src = data;
        //    $(images).attr("src", temp_canvas.toDataURL());
        console.log(data);
        //        $("#js_modify_head_file").attr("src", data);
        //        console.log(image.width + "--" + image.height);
    }

    /*    image.src = "/static/images/face_woman.png";
     var images = document.getElementById("js_modify_head_file");
     var temp_ctx, temp_canvas;
     temp_canvas = document.createElement('canvas');
     temp_ctx = temp_canvas.getContext('2d');
     temp_canvas.width = images.width
     temp_canvas.height = images.height;
     temp_ctx.drawImage(images, 0, 0, images.width, images.height, 0, 0, images.width, images.height);
     var data = (temp_canvas.toDataURL("image/png"));
     image.src = data;
     //    $(images).attr("src", temp_canvas.toDataURL());
     console.log(data);
     $("#js_modify_head_file").attr("src", data);
     console.log(image.width + "--" + image.height);*/
//    image = document.getElementById("js_modify_head_file");
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
        $("#js_head_file").Jcrop({
            onChange: showPreview,
            onSelect: showPreview,
            aspectRatio: 1,
            setSelect: [0, 0, 100, 100]
        });
        $(".jcrop-holder").css({
            "top": "80px",
            "left": "40px"
        });
        $(".jcrop-keymgr").css({
            "visibility": "hidden"
        });
    });
    $(".js_modify_head_close").click(function () {
        $(".js_modifyAccountHeadImgPanel").hide();
    });
    new Drag($("#js_modifyAccountHeadImgPanel")[0]);
});
function showPreview(coords) {
//    console.log(image.src.length);
    if (parseInt(coords.w) > 0) {
        var cx = image.width / 234;
        var cy = image.height / 234;
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
        var vData = temp_canvas.toDataURL();
//        console.log("vData:" + vData + "---length:" + vData.length + "cwidth:" + cwidth + "--cheight:" +
//            cheight + "--ax:" + ax + "--ay:" + ay + "--canves:" + temp_canvas.height);
//        tempData = temp_canvas.toDataURL();
        setImagesPostion(vData);
    }
}
function setImagesPostion(data) {
    $("#js_modify_jcrophead_show").attr('src', data);
    $(".js_accountHead").attr('src', data);
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
