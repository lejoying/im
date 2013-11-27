var image = new Image();
$(document).ready(function () {
    image.src = "static/images/webwxgeticon1A.jpg";
    $(".js_editheadimg").click(function () {
        $(".js_headjrop1").slideUp(100, function () {
            $(".js_headjrop2").css("visibility", "visible");
            var api = $("#xuwanting").Jcrop({
                onChange: showPreview,
                onSelect: showPreview,
                aspectRatio: 1,
                setSelect: [0, 0, 250, 250]
            });
        });
    });
    $(".js_moneymanage").hide();
    $(".js_ketingcom").click(function () {
        var js_kiting = $(".js_kiting").val();
        if (isNaN(js_kiting)) {
            alert("请输入数字");
        } else {
            var temp = parseFloat(js_kiting) * 100;
            if (temp >= 1) {
                $("#js_clickredirct")[0].click();
            } else {
                alert("不能低于0.01");
            }
        }
    });
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
        var vData = temp_canvas.toDataURL();
        $("#crop_preview").attr('src', vData);

        /*//计算预览区域图片缩放的比例，通过计算显示区域的宽度(与高度)与剪裁的宽度(与高度)之比得到
         var rx = $("#preview_box").width() / coords.w;
         var ry = $("#preview_box").height() / coords.h;
         //        alert(coords.w+"--"+rx);
         //通过比例值控制图片的样式与显示
         */
        /*$("#crop_preview").css({
         width: Math.round(rx * $("#xuwanting").width()) + "px",	//预览图片宽度为计算比例值与原图片宽度的乘积
         height: Math.round(rx * $("#xuwanting").height()) + "px",	//预览图片高度为计算比例值与原图片高度的乘积
         marginLeft: "-" + Math.round(rx * coords.x) + "px",
         marginTop: "-" + Math.round(ry * coords.y) + "px"
         });*/
        /*
         var width = Math.round(rx * $("#xuwanting").width());
         var height = Math.round(rx * $("#xuwanting").height());
         var temp_ctx, temp_canvas;
         temp_canvas = document.createElement('canvas');
         temp_ctx = temp_canvas.getContext('2d');
         temp_canvas.width = 100
         temp_canvas.height = 100;
         temp_ctx.drawImage(image, Math.round(rx * coords.x), Math.round(ry * coords.y), width, height, 0, 0, 100, 100);
         var vData = temp_canvas.toDataURL();
         //        $('#crop_result').attr('src', vData);
         //        alert(vData);
         $("#crop_preview").attr('src', vData);*/
    }
}
function scanheadimg() {
    var file = $("#imgfile")[0].files[0];
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function (e) {
        $(".jcrop-holder img").attr("src", e.target.result);
        $("#crop_preview").attr("src", e.target.result);
        image.src = e.target.result;
    }
}
function popupModifyAvatarWin() {
    $("#js_headimgmodify").css("visibility", "visible");
    showHeadImgModify();
}
function showHeadImgModify() {
    js_headimgmodify.style.visibility = 'visible';
    procbg = document.createElement("div");
    procbg.setAttribute("id", "mybg");
    procbg.style.background = "#000";
    procbg.style.width = "100%";
    procbg.style.height = "100%";
    procbg.style.position = "absolute";
    procbg.style.top = "0";
    procbg.style.left = "0";
    procbg.style.zIndex = "500";
    procbg.style.opacity = "0.3";
    procbg.style.filter = "Alpha(opacity=30)";
    document.body.appendChild(procbg);
    document.body.style.overflow = "hidden";
}
function closeProc2() {
    js_headimgmodify.style.visibility = 'hidden';
    procbg.style.visibility = "hidden";
}

