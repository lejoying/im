function isFromMobile()
{
        return (navigator.userAgent.match(/iphone|android|phone|mobile|wap|netfront|x11|java|operamobi|operamini|ucweb|windowsce|symbian|symbianos|series|webos|sony|blackberry|dopod|nokia|samsung|palmsource|xda|pieplus|meizu|midp|cldc|motorola|foma|docomo|up.browser|up.link|blazer|helio|hosin|huawei|novarra|coolpad|webos|techfaith|palmsource|alcatel|amoi|ktouch|nexian|ericsson|philips|sagem|wellcom|bunjalloo|maui|smartphone|iemobile|spice|bird|zte-|longcos|pantech|gionee|portalmmm|jig browser|hiptop|benq|haier|^lct|320x320|240x320|176x220/i));
}
var lazyLoad = {
    hp_lazy:'hp_lazy',
    Init: function () {
            return $("img[hp_lazy]");
    },
    Calculate: function (lazyloadobject) {
            var windowHeight = $(window).height();
            var arrReturn = {};
            var _scrollTop;
            if (lazyloadobject.length == 0) {
                    return null;
            }
            else {
                    lazyloadobject.each(function (i) {

                            _scrollTop = parseInt($(this).offset().top*zoom - windowHeight);

                            if (!arrReturn.hasOwnProperty(_scrollTop)) {
                                    arrReturn[_scrollTop] = new Array();
                            }
                            arrReturn[_scrollTop].push($(this));
                    });
                    this.ArrLoad = arrReturn;
                    return arrReturn;
            }
    },
    ArrLoad: null,
    IsLoad: function (scrolltop, objectstop) {
            if (objectstop != null && objectstop != {}) {
                    for (i in this.ArrLoad) {
                            if (parseInt(i) <= scrolltop && this.ArrLoad.hasOwnProperty(i)) {

                                    for (j = 0; j < this.ArrLoad[i].length; j++) {
                                            this.ArrLoad[i][j].attr("src", this.ArrLoad[i][j].attr("hp_lazy")).removeAttr("hp_lazy");
                                    }
                                    delete this.ArrLoad[i];
                            }
                    }
            }
    },
    Run: function () {
            var lazyLoadObject = this.Init();
            this.Calculate(lazyLoadObject);
            arrScrollTop = this.ArrLoad;
            if (arrScrollTop == null) {
                    return false;
            }
            else {
                    var _this = this;
                    _this.IsLoad($(window).scrollTop(), arrScrollTop);
                    $(window).scroll(function () {

                            _this.IsLoad($(this).scrollTop() , arrScrollTop);
                    });
            }
    }
};

var nav	= '';
if (/Firefox/i.test(navigator.userAgent)){
	nav	= 'firefox';
}

var zoom    = 1; 

var resize  = function(){	
     document.body.style.zoom	= parseInt(document.documentElement.clientWidth) / 640;
     zoom	= document.body.style.zoom;
     if(parseInt(zoom)>1)   zoom    = 1;
 }
var imgfunc = function(){
    //$(document.body).css('overflow-y','hidden');
    var width   = $(window).width()/zoom;
    var height  = $(window).height()/zoom;
    var f   = 'width',e='max-height';
    if(width>height){
	f   = 'max-width';
	e   = 'height';
    }
    
    if(arguments.length>0){
	var o	= $('.bigimg-div');
	$('.bigimg').attr('style','');
	if(o.length==0)	return;
    }else{
	var o	= $('<div class="bigimg-div"><img class="loadimg" src="w_img/loading_opt.gif"/><img class="none bigimg" src="'+$(this).attr('big_img')+'"/></div>');
	o.appendTo($(document.body));
	$('.bigimg').load(function(){
	    $(this).removeClass('none');
	    $('.loadimg').remove();
	});
	o.click(function(){
	    $('.bigimg-div').remove();
	    $(document.body).css('overflow-y','');
	    return false;
	});

	
	
    }
    
    $('.bigimg').css(f,'100%').css(e,'100%');
    o.css('left','0').css('top',$(window).scrollTop()/zoom).css('width',width).css('height',height).css('line-height',height+'px');
};