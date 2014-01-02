$(function () {
    $(document).on("click", ".js_circletop", function () {
        $(".js_onlyfriend").slideUp(500, function () {
            $(".js_morefriend").show();
        });
        $(".js_morefriend").slideDown(500, function () {
        });
    });
});