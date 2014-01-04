$(function () {
    $(document).on("click", ".js_circletop", function () {
        $(".js_onlyfriend").slideUp(500, function () {
            $(".js_morefriend").show();
        });
        $(".js_morefriend").slideDown(500, function () {
        });
    });
    $(".listOperatorContent>a").click(function () {


        alert(this.title == "好友");
        /*$(this).css({
            "background-position": "-52px -60px"
        });*/
    });
    $(".js_morefriend").ond
});