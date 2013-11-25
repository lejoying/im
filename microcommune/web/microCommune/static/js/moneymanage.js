$(document).ready(function () {
    $("#xuwanting").Jcrop({
        onChange:showPreview,
        onSelect:showPreview,
        aspectRatio:1
    });
    //简单的事件处理程序，响应自onChange,onSelect事件，按照上面的Jcrop调用
    function showPreview(coords){
        if(parseInt(coords.w) > 0){
            //计算预览区域图片缩放的比例，通过计算显示区域的宽度(与高度)与剪裁的宽度(与高度)之比得到
            var rx = $("#preview_box").width() / coords.w;
            var ry = $("#preview_box").height() / coords.h;
            //通过比例值控制图片的样式与显示
            $("#crop_preview").css({
                width:Math.round(rx * $("#xuwanting").width()) + "px",	//预览图片宽度为计算比例值与原图片宽度的乘积
                height:Math.round(rx * $("#xuwanting").height()) + "px",	//预览图片高度为计算比例值与原图片高度的乘积
                marginLeft:"-" + Math.round(rx * coords.x) + "px",
                marginTop:"-" + Math.round(ry * coords.y) + "px"
            });
        }
    }
    /*var jcrop_api,
        boundx,
        boundy,

    // Grab some information about the preview pane
        $preview = $('#preview-pane'),
        $pcnt = $('#preview-pane .preview-container'),
        $pimg = $('#preview-pane .preview-container img'),

        xsize = $pcnt.width(),
        ysize = $pcnt.height();

    console.log('init',[xsize,ysize]);
    $('#testImage').Jcrop({
        onChange: updatePreview,
        onSelect: updatePreview,
        aspectRatio: "1:1"
    },function(){
        // Use the API to get the real image size
        var bounds = this.getBounds();
        boundx = bounds[0];
        boundy = bounds[1];
        // Store the API in the jcrop_api variable
        jcrop_api = this;

        // Move the preview into the jcrop container for css positioning
        $preview.appendTo(jcrop_api.ui.holder);
    });

    function updatePreview(c)
    {
        if (parseInt(c.w) > 0)
        {
            var rx = xsize / c.w;
            var ry = ysize / c.h;

            $pimg.css({
                width: Math.round(rx * boundx) + 'px',
                height: Math.round(ry * boundy) + 'px',
                marginLeft: '-' + Math.round(rx * c.x) + 'px',
                marginTop: '-' + Math.round(ry * c.y) + 'px'
            });
        }
    };*/
    /*$('#testImage').imgAreaSelect({
        handles: true,
        x1: 0,
        y1: 0,
        x2: 250,
        y2: 250,
        aspectRatio: "1:1",
        minHeight: 100,
        minWidth: 100,
        onSelectEnd: onSelectEnd
    });*/

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
function onSelectEnd(img, selection) {
    if(parseInt(selection.width) > 0){
        //计算预览区域图片缩放的比例，通过计算显示区域的宽度(与高度)与剪裁的宽度(与高度)之比得到
        var rx = $("#previewArea").width() / selection.width;
        var ry = $("#previewArea").height() / selection.height;
        //通过比例值控制图片的样式与显示
        $("#testImage1").css({
            width:Math.round(rx * $("#previewArea").width()) + "px",	//预览图片宽度为计算比例值与原图片宽度的乘积
            height:Math.round(rx * $("#previewArea").height()) + "px",  //预览图片高度为计算比例值与原图片高度的乘积
            marginLeft:"-" + Math.round(rx * selection.x1) + "px",
            marginTop:"-" + Math.round(ry * selection.y1) + "px"
        });
    }
//    alert('width: ' + selection.width + '; height: ' + selection.height+"-x1-"+selection.x1+"-y1-"+selection.y1);
}

