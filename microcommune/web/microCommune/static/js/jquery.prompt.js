/***
 * Prompt��ʾ����
 * ��дʱ�䣺2013��4��8��
 * version:Prompt.1.0.js
 * author:С��<i@windyland.com>
***/
(function($){
		$.extend({
			PromptBox:{
				defaults : {
					name  :	"T"+ new Date().getTime(),
					content :"This is tips!",							//�����������(text�ı�������ID��ơ�URL��ַ��Iframe�ĵ�ַ)
					width : 200,									//������Ŀ��
					height : 70,							
					time:1000,//�����Զ��ر�ʱ�䣬����Ϊ0��ʾ���Զ��ر�
					bg:true
				},
				timer:{
					stc:null,
					clear:function(){
						if(this.st)clearTimeout(this.st);
						if(this.stc)clearTimeout(this.stc);
						}
				},
				config:function(def){
					this.defaults = $.extend(this.defaults,def);
				},
				created:false,
				create : function(op){
					this.created=true;
					var ops = $.extend({},this.defaults,op);
					this.element = $("<div class='Prompt_floatBoxBg' id='fb"+ops.name+"'></div><div class='Prompt_floatBox' id='"+ops.name+"'><div class='content'></div></div>");
					$("body").prepend(this.element);
					this.blank = $("#fb"+ops.name);						//���ֲ����
					this.content = $("#"+ops.name+" .content");				//���������ݶ���
					this.dialog = $("#"+ops.name+"");						//���������
					if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {//�ж�IE6
						this.blank.css({height:$(document).height(),width:$(document).width()});
					}
				},
				show:function(op){
					this.dialog.show();
					var ops = $.extend({},this.defaults,op);
					this.content.css({width:ops.width});
					this.content.html(ops.content);
					var Ds = {
							   width:this.content.outerWidth(true),
							   height:this.content.outerHeight(true)
							   };
					if(ops.bg){
						this.blank.show();
						this.blank.animate({opacity:"0.5"},"normal");		
					}else{
						this.blank.hide();
						this.blank.css({opacity:"0"});
					}
					this.dialog.css({
									width:Ds.width,
									height:Ds.height,
									left:(($(document).width())/2-(parseInt(Ds.width)/2)-5)+"px",
									top:(($(window).height()-parseInt(Ds.height))/2+$(document).scrollTop())+"px"
									});
					if ($.isNumeric(ops.time)&&ops.time>0){//�Զ��ر�
						this.timer.clear();
						this.timer.stc = setTimeout(function (){			
							var DB = $.PromptBox;
							DB.close();
						},ops.time);	
					}
				},
				close:function(){
					var DB = $.PromptBox;
						DB.blank.animate({opacity:"0.0"},
										 "normal",
										 function(){
											DB.blank.hide();
											DB.dialog.hide();	
										  });		
						DB.timer.clear();
				}
			},
			Prompt:function(con,t,ops){
				if(!$.PromptBox.created){$.PromptBox.create(ops);}
				if($.isPlainObject(con)){
					if(con.close){
						$.PromptBox.close();
					}else{
						$.PromptBox.config(con);
					}
					return true;
				}
				ops = $.extend({},$.PromptBox.defaults,ops,{time:t});
				ops.content = con||ops.content;
				$.PromptBox.show(ops);
			}
		})  	 
})(jQuery);