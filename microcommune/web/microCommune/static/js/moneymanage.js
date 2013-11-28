var image = new Image();
var vData;
$(document).ready(function () {
    $.getScript("/static/js/sha1.js");
    var nowAccount = window.localStorage.getItem("wxgs_nowAccount");
    var src;
    $.ajax({
        type: "POST",
        url: "/api2/image/get?",
        data: {
            filename: JSON.parse(nowAccount).head
        },
        success: function (data) {
//            alert(data["提示信息"]);
            image.src = data["image"];
            $(".js_headjrop1 img").attr("src", data["image"]);
            $("#xuwanting").attr("src", data["image"]);
            $(".jcrop-holder img").attr("src", data["image"]);
            $("#crop_preview").attr("src", data["image"]);
            $("#preview_box").attr("src", data["image"]);
        }
    });
//    image.src = "/static/images/webwxgeticon1A.jpg";
    $(".js_editheadimg").click(function () {
        $(".js_headjrop1").slideUp(100, function () {
            $(".js_headjrop2").css("visibility", "visible");
            $(".js_headjrop2").slideDown(100, function () {
            });
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
    $(".js_saveHeadImg").click(function () {
        var nowAccount = window.localStorage.getItem("wxgs_nowAccount");
        var fileName = hex_sha1(vData);
        var accountObj = JSON.parse(nowAccount);
        $.ajax({
            type: "POST",
            url: "/api2/image/check?",
            data: {
                filename: fileName
            },
            success: function (data) {
                if (data["提示信息"] == "查找成功") {
                    if (data["exists"]) {
                        modifyAccountHeadImg(accountObj.phone, accountObj.accessKey, fileName);
                    } else {
                        $.ajax({
                            type: "POST",
                            url: "/api2/image/upload?",
                            data: {
                                filename: fileName,
                                imagedata: vData
                            },
                            success: function (data) {
                                if (data["提示信息"] == "图片上传成功") {
                                    modifyAccountHeadImg(accountObj.phone, accountObj.accessKey, fileName);
                                } else {
                                    eval("$.Prompt('" + data["提示信息"] + "')");
                                }
                            }
                        });
                    }
                } else {
                    eval("$.Prompt('" + data["失败原因"] + "')");
                    cancleHeadImg();
                }
            }
        });
    });
    $(".js_cancleHeadImg").click(function () {
        cancleHeadImg();
    });
});
function modifyAccountHeadImg(phone, accessKey, head) {
    $.ajax({
        type: "POST",
        url: "/api2/account/modify?",
        data: {
            phone: phone,
            accessKey: accessKey,
            account: JSON.stringify({
                phone: phone,
                head: head
            })
        },
        success: function (data) {
            if (data["提示信息"] == "修改用户信息成功") {
                var nowAccount = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
                nowAccount.head = head;
                window.localStorage.setItem("wxgs_nowAccount", JSON.stringify(nowAccount));
                cancleHeadImg();
                eval("$.Prompt('图片上传成功')");
            } else {
                eval("$.Prompt('图片上传失败')");
            }
        }
    });
}
function cancleHeadImg() {
    $(".js_headjrop2").slideUp(100, function () {
        $(".js_headjrop2").css("visibility", "hidden");
        $(".js_headjrop1").slideDown(100, function () {
        });
    });
}
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
    var path = $("#imgfile").val();
    var last = path.substr(path.lastIndexOf(".") + 1).toLowerCase();
    if (last != "jpg" && last != "bmp" && last != "jpeg" && last != "png") {
        eval("$.Prompt('此文件不是图片格式')");
        return;
    }
    if (file.size > 1048576) {
        eval("$.Prompt('文件大小不能超过1M')");
        return;
    } else {
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function (e) {
            $(".jcrop-holder img").attr("src", e.target.result);
            $("#crop_preview").attr("src", e.target.result);
            image.src = e.target.result;
        }
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
    /*$(".js_headjrop2").slideUp(1, function () {
     $(".js_headjrop2").css("visibility", "hidden");
     $(".js_headjrop1").slideDown(1, function () {
     });
     });*/
    cancleHeadImg();
    procbg.style.visibility = "hidden";
}

