var selectPanel = "js_tempChatTop";
var scrollFlagTempChatTop = false;
$(function () {
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
     $(".js_modifyAccountHeadImgPanel").show();
     $(".js_modifyAccountHeadImgPanel").css({
     "top": "150px",
     "left": "415px"
     });
     }
     });
    $(".js_modify_head_close").click(function () {
        $(".js_modifyAccountHeadImgPanel").hide();
    });

    new Drag($("#js_modifyAccountHeadImgPanel")[0]);
});
function Drag(o){
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
