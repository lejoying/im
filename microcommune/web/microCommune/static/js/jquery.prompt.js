/***
 * Prompt提示语插件
 * 编写时间：2013年4月8号
 * version:Prompt.1.0.js
 * author:小宇<i@windyland.com>
***/
(function($){
		$.extend({
			PromptBox:{
				defaults : {
					name  :	"T"+ new Date().getTime(),
					content :"This is tips!",							//弹出层的内容(text文本、容器ID名称、URL地址、Iframe的地址)
					width : 200,									//弹出层的宽度
					height : 70,							
					time:2000,//设置自动关闭时间，设置为0表示不自动关闭
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
					this.blank = $("#fb"+ops.name);						//遮罩层对象
					this.content = $("#"+ops.name+" .content");				//弹出层内容对象
					this.dialog = $("#"+ops.name+"");						//弹出层对象
					if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {//判断IE6
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
					if ($.isNumeric(ops.time)&&ops.time>0){//自动关闭
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