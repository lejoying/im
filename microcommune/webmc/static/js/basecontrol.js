var selectPanel = "js_tempChatTop";
var scrollFlagTempChatTop = false;
$(function () {
    $(".scrollDiv1").hide();
    $(".js_tempChatTop").addClass("conmuButton_chatStyle");
    $(".js_mainContent").slideUp(1);
    $(".js_js_tempChatMainContent").slideDown(1);
    $(document).on("click", ".js_circlesTop", function () {
        $(".js_onlyfriend").slideUp(500, function () {
            $(".js_morefriend").show();
        });
        $(".js_morefriend").slideDown(500, function () {
        });
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
});