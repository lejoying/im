/***
 * ����Jqueryʱ����
 * ��дʱ�䣺2012��7��14��
 * version:manhuaTip.1.0.js
***/
$(function() {
	$.fn.manhuaTip = function(options) {
		var defaults = {
			Event : "click",
			timeOut : 2000
		};
		var options = $.extend(defaults,options);
		var $tip = $(this);
		//�ı�����¼�
		$tip.live(options.Event,function(e){	
			var type = $(this).attr('ty');		
			var msg = $(this).attr('msg');
		  	var tipHtml = '';
			if (type =='loading'){
				tipHtml = '<img alt="" src="/static/images/loading.gif">'+(msg ? msg : '�����ύ����������Ժ�...');
			} else if (type =='notice'){
				tipHtml = '<span class="gtl_ico_hits"></span>'+msg
			} else if (type =='error'){
				tipHtml = '<span class="gtl_ico_fail"></span>'+msg
			} else if (type =='succ'){
				tipHtml = '<span class="gtl_ico_succ"></span>'+msg
			}
			if ($('.msgbox_layer_wrap')) {
				$('.msgbox_layer_wrap').remove();
			}
			if (st){
				clearTimeout(st);
			}
			$("body").prepend("<div class='msgbox_layer_wrap'><span id='mode_tips_v2' style='z-index: 10000;' class='msgbox_layer'><span class='gtl_ico_clear'></span>"+tipHtml+"<span class='gtl_end'></span></span></div>");
			$(".msgbox_layer_wrap").show();
			var st = setTimeout(function (){
				$(".msgbox_layer_wrap").hide();
				clearTimeout(st);
			},options.timeOut);
		});	
	}
});