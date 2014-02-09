var selectPanel = "js_tempChatTop";
var scrollFlagTempChatTop = false;
var image = new Image();
var vData;
var loadImageFlag = false;
$(function () {//show js_modify_jcrophead_show
    console.log("如果你能看到这段话，那么欢迎你加入微型公社团队，" +
        "让我们一起从事这件伟大的，准备拯救世界的前端攻城师事业，^_-。" +
        "请发送邮件到open@lejoying.com，请注明这是来自微型公社js信息的应聘信息。");
    var img = new Image();

    /*function getBase64() {
     var img = document.getElementById("js_modify_head_file");
     var canvas = document.createElement("canvas");
     canvas.width = img.width;
     canvas.height = img.width;
     var ctx = canvas.getContext("2d");
     ctx.drawImage(img, 0, 0);
     var dataURL = canvas.toDataURL("image/png");
     */
    /*image = {
     width: canvas.width,
     height: canvas.height,
     data: dataURL
     }*/
    /*
     //        alert(dataURL);
     }*/

//    getBase64();
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
//    img.src = "http://im.lejoying.com/static/images/face_man.png";
//    image = document.getElementById("js_modify_head_file");
    image.src = "/static/images/face_man.png";
//    img.crossOrigin = "*";
//    image.src = "/static/images/face_man.png";
//    img.crossOrigin = "Anonymous";
    /*var temp_ctx, temp_canvas;
     temp_canvas = document.createElement('canvas');
     temp_canvas.style.marginTop = "-1800px";
     temp_ctx = temp_canvas.getContext('2d');
     temp_canvas.width = img.width;
     temp_canvas.height = img.height;
     img.crossOrigin = "Anonymous";
     temp_canvas.seto
     img.onload = function () {
     console.log(img.complete);
     console.log("img:" + img.height + "-" + img.width + "--");

     temp_ctx.drawImage(img, 0, 0);
     document.body.appendChild(temp_canvas);
     console.log(temp_canvas);
     //        var data = (temp_canvas.toDataURL("image/png"));
     var data = (temp_canvas.toDataURL("image/png", 1));
     //        image.src = data;
     //    $(images).attr("src", temp_canvas.toDataURL());
     console.log(data);
     //        $("#js_modify_head_file").attr("src", data);
     //        console.log(image.width + "--" + image.height);
     }*/
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
        /*$(".js_chat_one").show();
         $(".js_chat_group").hide();*/


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
        /*$(".js_chat_one").hide();
         $(".js_chat_group").show();
         $(".js_chat_group_temp_up").show();*/


        $("#conversationListContent").css({
            top: "0px"
        });
        $(".js_tempChatTop").removeClass("conmuButton_chatStyle");
        $(".js_circlesTop").removeClass("conmuButtonStyle");
        $(".js_clustersTop").addClass("closefriend_iconStyle");
        $(".js_mainContent").slideUp(1);
        $(".js_clusterMainContent").slideDown(1);
        selectPanel = "js_clustersTop";
        /*var groups = [];
         for (var i = 0; i < 4; i++) {
         var group = {
         tempGid: i,
         name: "群组" + i,
         members: ["125478", "18601330540", "125455525", "18601330540", "125455525", "18601330540"]
         };
         groups.push(group);
         tempGroupsInfo[i] = group;
         }
         getTemplateHtml("user_groups", function (template) {
         $(".js_user_groups").append(template.render(groups));
         });*/
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

    $(".js_accountHead").click(function () {
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
            var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
            if (accountObj.head != "" && accountObj.head != null) {
                $.ajax({
                    type: "POST",
                    url: "/image/get?",
                    data: {
                        phone: accountObj.phone,
                        accessKey: accountObj.accessKey,
                        filename: accountObj.head
                    },
                    success: function (data) {
                        if(data["提示信息"] == "获取图片成功"){
                            image.src = data.image;
                            $("#js_modify_head_file").attr("src", data.image);
                            $("#js_modify_jcrophead_show").attr("src", data.image);
                        }else{
                            $("#js_modify_head_file").attr("src", "/static/images/face_man.png");
                            $("#js_modify_jcrophead_show").attr("src", "/static/images/face_man.png");
                        }
                    }
                });
            } else {
                $("#js_modify_head_file").attr("src", "/static/images/face_man.png");
                $("#js_modify_jcrophead_show").attr("src", "/static/images/face_man.png");
            }
        }
    });
    $(".js_modify_head_close").click(function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        if (accountObj.head != "") {
            $(".js_accountHead").attr("src", imageServer + accountObj.head);
        } else {
            $(".js_accountHead").attr("src", "/static/images/face_man.png");
        }
        $(".js_modifyAccountHeadImgPanel").hide();
    });
    $(".js_modify_head_button_cancel").click(function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        if (accountObj.head != "") {
            $(".js_accountHead").attr("src", imageServer + accountObj.head);
        } else {
            $(".js_accountHead").attr("src", "/static/images/face_man.png");
        }
        $(".js_modifyAccountHeadImgPanel").hide();
    });
    $(".js_modify_head_images_button").click(function () {
        $(".js_upload_headimg_file").click();
//        alert($(".js_upload_headimg_file").val());
    });
    $(".js_upload_headimg_file").change(function () {
        scanHeadImg();
    });
    $(".js_modify_head_button_save_img").click(function () {
        var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
        var base64Data = vData.replace(/^data:image\/\w+;base64,/, "");
        var fileName = hex_sha1(base64Data) + ".png";
        $.ajax({
            type: "GET",
            url: "/image/check?",
            data: {
                filename: fileName
            },
            success: function (data) {
                if (data.exists) {
                    modifyUserHeadImg(fileName);
                } else if (!data.exists) {
                    $.ajax({
                        type: "POST",
                        url: "/image/upload?",
                        data: {
                            phone: accountObj.phone,
                            accessKey: accountObj.accessKey,
                            filename: fileName,
                            imagedata: base64Data
                        },
                        success: function (data) {
                            if (data["提示信息"] == "图片上传成功") {
                                modifyUserHeadImg(fileName);
                            } else {
                                modifyLocalHeadImgSrc(accountObj);
                            }
                        }
                    });
                }
            }
        });
    });
    new Drag($("#js_modifyAccountHeadImgPanel")[0]);
});
function onLoadUserHeadImgError() {
    $(".js_accountHead").attr("src", "/static/images/face_man.png");
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    accountObj.head = "";
    window.localStorage.setItem("wxgs_nowAccount", JSON.stringify(accountObj));
}
function modifyUserHeadImg(fileName) {
    var accountObj = JSON.parse(window.localStorage.getItem("wxgs_nowAccount"));
    $.ajax({
        type: "POST",
        url: "/api2/account/modify?",
        data: {
            phone: accountObj.phone,
            accessKey: accountObj.accessKey,
            account: JSON.stringify({phone: accountObj.phone, head: fileName})
        },
        success: function (data) {
            if (data["提示信息"] == "修改用户信息成功") {
                $(".js_modifyAccountHeadImgPanel").hide();
                accountObj.head = fileName;
                window.localStorage.setItem("wxsgs_nowAccount", JSON.stringify(accountObj));
                modifyLocalHeadImgSrc(accountObj);
            } else {
                modifyLocalHeadImgSrc(accountObj);
            }
        }
    });
}
function modifyLocalHeadImgSrc(accountObj) {
    if (accountObj.head != "") {
        $(".js_accountHead").attr("src", imageServer + accountObj.head);
    } else {
        $(".js_accountHead").attr("src", "/static/images/face_man.png");
    }
}
function scanHeadImg() {
    var file = $(".js_upload_headimg_file")[0].files[0];
    var path = $(".js_upload_headimg_file").val();
    var last = path.substr(path.lastIndexOf(".") + 1).toLowerCase();
    if (last != "jpg" && last != "bmp" && last != "jpeg" && last != "png") {
        alert("$.Prompt('此文件不是图片格式')");
        return;
    } else if (path == "") {
        return;
    }
    if (file.size > window.globaldata.serverSetting.maxUploadImg) {
        alert("$.Prompt('文件大小不能超过1M')");
        return;
    } else {
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function (e) {
//            $(".jcrop-holder img").attr("src", e.target.result);
//            $("#crop_preview").attr("src", e.target.result);
            image.src = e.target.result;
            vData = e.target.result;
            $("#js_modify_head_file").attr("src", vData);
            $("#js_modify_jcrophead_show").attr("src", vData);
        }
    }
}
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
        vData = temp_canvas.toDataURL();
//        console.log("vData:" + vData + "---length:" + vData.length + "cwidth:" + cwidth + "--cheight:" +
//            cheight + "--ax:" + ax + "--ay:" + ay + "--canves:" + temp_canvas.height);
//        tempData = temp_canvas.toDataURL();
        setImagesPostion(vData);
//        alert(vData);
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
