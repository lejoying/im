$(document).ready(function () {
    $(".class").onclick(function(){
        var icon = $(this);
        if (icon.hasClass("js_mousedown")) {
            icon.removeClass("js_mousedown");
            icon.addClass("js_moving");
            icon.css({
                property:value,
                property:value,
                property:value
            });
            icon.css("property","value");
        }
    });
});