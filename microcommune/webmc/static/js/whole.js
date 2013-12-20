


/**************************** index页chi start *********************************************************/

$(function(){

	if ($(".imgslider").length >= 1) {
		return;
	};

	

		if ($(window).width() > 2094) {

			var docWidth = 2094;

			$('#in_body').width(2094);

			$('.in_left').css('left',($(window).width()-1080)/2);

			$('.in_right').css('right',($(window).width()-1080)/2+980);



		}

		else if ($(window).width() <= 1180) {

			var docWidth = 1024;

			$('.in_arrow').width(1024);
			if ($(window).width() <= 1180) {

				$('#in_body').width(1024);

				$('.in_left').css('left','0');

				$('.in_right').css('right','0');

			}

		}

		else {

			var docWidth = $(window).width();

			$('.in_left').css('left',($(window).width()-1080)/2);

			$('.in_right').css('right',($(window).width()-1080)/2);

		}





	$(window).resize(function(){

		if ($(window).width() > 2094) {

			$('#in_body').width(2094);

			$('.in_move').width(2094) 

			$('.in_bigone').width(2094);

			$('.in_bigtwo').width(2094);

			$('.in_bigthree').width(2094);



			$('.in_arrow').width(2094); 

			$('.in_left').css('left',($(window).width()-1080)/2);

			$('.in_right').css('right',($(window).width()-1080)/2);		}

		else if($(window).width() <= 1180){

			$('#in_body').width(1024);

			$('#in_body .in_move').width(1024) 

			$('#in_body .in_bigone').width(1024);

			$('#in_body .in_bigtwo').width(1024);

			$('#in_body .in_bigthree').width(1024);



			$('.in_arrow').width($(window).width()); 

			$('.in_left').css('left','0');

			$('.in_right').css('right','0');

		}

		else {

			$('#in_body').width($(window).width());

			$('.in_move').width($(window).width()) 

			$('.in_bigone').width($(window).width());

			$('.in_bigtwo').width($(window).width());

			$('.in_bigthree').width($(window).width());



			$('.in_arrow').width($(window).width());

			$('.in_left').css('left',($(window).width()-1080)/2);

			$('.in_right').css('right',($(window).width()-1080)/2);

		}

	});





	var in_moveT = null,moveR = true,bigThis = false;

	var in_clockT = null;

	var n1 = 0;	//点击右滚

	var n2 = 2;	//点击左滚

	var n3 = 0;	//自动滚动

	var n4 = 0;	//判断推动

	var strR = 1,strL = 0;

	var isIE6 = !-[1,]&&!window.XMLHttpRequest;





	$('.chi .in_move').width(docWidth) 

	$('.chi .in_bigone').width(docWidth);

	$('.chi .in_bigtwo').width(docWidth);

	$('.chi .in_bigthree').width(docWidth);

	/*

	 算法来源：http://www.robertpenner.com/easing/

	 */

	var Tween = {

		Quart: {

			easeInOut: function(t,b,c,d){

				if ((t/=d/2) < 1) return c/2*t*t*t*t + b;

				return -c/2 * ((t-=2)*t*t*t - 2) + b;

			}

		}

	}

	/* 大图滚动 2013/9/27 BY Shirley Xie

		n 为第N页

		boolean=true 为向右滚，boolean=false 为向左滚	

		boolean1=true 为点操作,boolean1=false 为左右滚动按钮或者自动滚动操作 

	 */

	function move(n,boolean,boolean1) {

		if (in_moveT) {

			clearTimeout(in_moveT);

		}



		if (boolean) {

			var t = docWidth;

			_moveRight();

		}

		else {

			var t = 0;

			_moveLeft();

		}



		function _moveRight() {

			if (t > 0) {	// 向右翻临界

				if (isIE6) {

					t-=30;

				}

				else {

					t-=6;

				}

				

				if (n == 0) {	// 显示第2页

					if (boolean1 && !bigThis && (n4==3||n4==0)) {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigtwo').css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && !bigThis && n4==2) {

						$('.chi .in_bigone').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigtwo').css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$('.chi .in_bigone').css({left:0+"px","z-index":"90"});

						$('.chi .in_bigtwo').css({left:docWidth+"px","z-index":"100"});

						$('.chi .in_bigthree').css({"z-index":"80"});						

					}



					$('.chi .in_bigtwo').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else if (n == 1) {	// 显示第3页

					if (boolean1 && !bigThis && n4==2) {

						$('.chi .in_bigone').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigthree').css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && !bigThis && (n4==3||n4==0)) {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigthree').css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"90"});

						$('.chi .in_bigthree').css({left:docWidth+"px","z-index":"100"});

						$('.chi .in_bigone').css({"z-index":"80"});							

					}

					$('.chi .in_bigthree').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else {	// 显示第1页

					if (boolean1 && !bigThis && n4==1) {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigone').css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && !bigThis && n4==2) {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigone').css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$('.chi .in_bigone').css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"90"});

						$('.chi .in_bigone').css({left:docWidth+"px","z-index":"100"});

						$('.chi .in_bigtwo').css({"z-index":"80"});					

					}

					$('.chi .in_bigone').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});						

				}

				in_moveT = setTimeout(_moveRight,10);

			}

		}

		function _moveLeft() { 

			if (t < docWidth) {		// 向左翻临界

				if (isIE6) {

					t+=30;

				}

				else {

					t+=6;

				}

				

				if (n == 2) {	// 显示第3页

					if (boolean1 && !bigThis && n4==0) {

						$('.chi .in_bigone').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"120"});

						$('.chi .in_bigtwo').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

					}

					else if (boolean1 && !bigThis && (n4==-1||n4==2)) {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"90"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigtwo').css({"z-index":"80"});											

					}

					$('.chi .in_bigone').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else if (n == 1) {	// 显示第2页

					if (boolean1 && !bigThis && (n4==-1||n4==2)) {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"120"});

						$('.chi .in_bigone').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

					}

					else if (boolean1 && !bigThis && n4==1) {

						$('.chi .in_bigone').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"90"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigone').css({"z-index":"80"});

					}

					$('.chi .in_bigthree').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else {	// 显示第1页

					if (boolean1 && !bigThis && n4==1) {

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigthree').css({left:0+"px","z-index":"120"});

						$('.chi .in_bigthree').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

					}

					else if (boolean1 && !bigThis && n4==0) {

						$('.chi .in_bigthree').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigone').css({left:0+"px","z-index":"110"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$('.chi .in_bigone').css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$('.chi .in_bigone').css({left:0+"px","z-index":"90"});

						$('.chi .in_bigtwo').css({left:0+"px","z-index":"100"});

						$('.chi .in_bigthree').css({"z-index":"80"});

					}

					$('.chi .in_bigtwo').css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				in_moveT = setTimeout(_moveLeft,10);

			}

		}

		

		$('.chi .in_pos img').attr({'src':'/static/images/in_gray.png'}).css({'border':'0px red solid','overflow':'hidden'});

		if (n==2 && boolean==true)

		{

			$('.chi .in_pos img').eq(0).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else if (n==2 && boolean==false) {

			$('.chi .in_pos img').eq(2).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else if (n==1 && boolean==false) {

			$('.chi .in_pos img').eq(n).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else if (n==0 && boolean==false) {

			$('.chi .in_pos img').eq(0).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else {

			$('.chi .in_pos img').eq(n+1).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

	}



	/* 自动左右滚动 */

	function in_clockRight() {

		if (n3 == 3) {

			n3 = 0;

		}

		move(n3,true,false);

		

		n2 = n3;

		n3++;

		n1 = n3;

	}

	function in_clockLeft() {

		if (n3 == -1) {

			n3 = 2;

		}

		move(n3,false,false);

		n1 = n3;

		n3--;

		n2 = n3;		

	}

	if (isIE6) {

		in_clockT=setInterval(in_clockRight,8000);

	}

	else{

		in_clockT=setInterval(in_clockRight,7000);

	}



	/* 小圆点点击 */

	$('.chi .in_pos span').click(function(){

		$('.chi .in_pos img').attr({'src':'/static/images/in_gray.png'}).css({'border':'0px red solid','overflow':'hidden'});

		$(this).find('img').attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		clearInterval(in_clockT);

		n4 = n3;

	})

	function in_dots(nR,strR,nL,strL) {

		if (moveR) {

			if (strR) {

				bigThis = true;

			}

			n3 = nR;

			move(n3,true,true);

			n3++;

			bigThis = false;

			if (isIE6) {

				in_clockT=setInterval(in_clockRight,8000);

			}

			else{

				in_clockT=setInterval(in_clockRight,7000);

			}

		}

		else {

			if (strL) {

				bigThis = true;

			}

			n3 = nL;

			move(n3,false,true);

			n3--;

			bigThis = false;

			if (isIE6) {

				in_clockT=setInterval(in_clockRight,8000);

			}

			else{

				in_clockT=setInterval(in_clockRight,7000);

			}

		}

		n1 = n3;

		n2 = n3;

	}

	$('.chi .in_pos span').eq(0).click(function () {

		strR = (n4==3 || n4==0);

		strL = (n4==-1 || n4==2);

		in_dots(2,strR,0,strL);

	})

	$('.chi .in_pos span').eq(1).click(function () {

		strR = (n4==1);

		strL = (n4==0);

		in_dots(0,strR,1,strL);

	})

	$('.chi .in_pos span').eq(2).click(function () {

		strR = (n4==2);

		strL = (n4==1);

		in_dots(1,strR,2,strL);

	})



	/* 右耳朵点击 */

	$('.chi .in_right').click(function(){

		if (n1 == 3) {

			n1 = 0;

		}

		clearInterval(in_clockT);

		move(n1,true,false);

		n2 = n1;

		n1++;

		n3 = n1;

		if (isIE6) {

			in_clockT=setInterval(in_clockRight,8000);

		}

		else{

			in_clockT=setInterval(in_clockRight,7000);

		}

		moveR = true;

	})

	/* 左耳朵点击 */

	$('.chi .in_left').click(function(){

		if (n2 == -1) {

			n2 = 2;

		}

		clearInterval(in_clockT);

		move(n2,false,false);

		n1 = n2;

		n2--;

		n3 = n2;

		if (isIE6) {

			in_clockT=setInterval(in_clockLeft,8000);

		}

		else{

			in_clockT=setInterval(in_clockLeft,7000);

		}

		moveR = false;

	})//big pic end

	

	

})/**************************** index页 end *********************************************************///////////////////////////////////

/***************** 大图滚动 START *************************************////////////////////////////////////////////////////////////////////////

$(function (){
	if ($(window).width() > 2094) {
		var docWidth = 2094;
	}
	else if ($(window).width() < 1024) {
		var docWidth = 1024;
	}
	else {
		var docWidth = $(window).width();
	}
	var $inBigTwo = $('.in_move .in_bigtwo');
	var $inBigThree = $('.in_move .in_bigthree');
	$inBigTwo.css({"left": docWidth});
	$inBigThree.css({"left": docWidth});
});
window.onload = function(){
	/*无缝滚动*/

	var  m=0;

	$('#in_pics').html($('#in_pic').html());

	function sport(){

		$('.in_bg').scrollLeft(m+=2);

		

		if($('.in_bg').scrollLeft()>=$('#in_pic').width()){

			m=0;

			$('.in_bg').scrollLeft(0);

		}

		

	}

	t4=setInterval(sport,20);

	$('.in_bg').mouseover(function(){

		if (t4) 

		{

			clearInterval(t4);

		}

		

	});

	$('.in_bg').mouseout(function(){

		t4=setInterval(sport,20);

	});
	if($(".imgslider").length >= 1){
		return;
	}
	var in_moveT = null,moveR = true,bigThis = false;

	var in_clockT = null;

	var n1 = 0;	//点击右滚

	var n2 = 2;	//点击左滚

	var n3 = 0;	//自动滚动

	var n4 = 0;	//判断推动

	var strR = 1,strL = 0;

	var isIE6 = !-[1,]&&!window.XMLHttpRequest;
	var $inBigOne = $('.in_eng .in_bigone');
	var $inBigTwo = $('.in_eng .in_bigtwo');
	var $inBigThree = $('.in_eng .in_bigthree');
	var $inBigFour = $('.in_eng .in_bigfour');



	if ($(window).width() > 2094) {

		var docWidth = 2094;

	}

	else if ($(window).width() < 1024) {

		var docWidth = 1024;

	}

	else {

		var docWidth = $(window).width();

	}

	$('.in_eng .in_move').width(docWidth) 

	$inBigOne.width(docWidth);

	$inBigTwo.width(docWidth);

	$inBigThree.width(docWidth);
	$inBigTwo.css({"left": docWidth});
	$inBigThree.css({"left": docWidth});

	/*

	 算法来源：http://www.robertpenner.com/easing/

	 */

	var Tween = {

		Quart: {

			easeInOut: function(t,b,c,d){

				if ((t/=d/2) < 1) return c/2*t*t*t*t + b;

				return -c/2 * ((t-=2)*t*t*t - 2) + b;

			}

		}

	}

	/* 大图滚动 2013/9/27 BY Shirley Xie

		n 为第N页

		boolean=true 为向右滚，boolean=false 为向左滚	

		boolean1=true 为点操作,boolean1=false 为左右滚动按钮或者自动滚动操作 

	 */

	function move(n,boolean,boolean1) {

		if (in_moveT) {

			clearTimeout(in_moveT);

		}



		if (boolean) {

			var t = docWidth;

			_moveRight();

		}

		else {

			var t = 0;

			_moveLeft();

		}



		function _moveRight() {

			if (in_moveT) {

				clearTimeout(in_moveT);

			}

			if (t > 0) {	// 向右翻临界

				if (isIE6) {

					t-=30;

				}

				else {

					t-=6;

				}



				if (n == 0) {	// 显示第2页

					if (boolean1 && !bigThis && (n4==3||n4==0)) {

						$inBigThree.css({left:0+"px","z-index":"100"});

						$inBigFour.css({left:0+"px","z-index":"99"});

						$inBigOne.css({left:0+"px","z-index":"110"});

						$inBigTwo.css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && !bigThis && n4==2) {

						$inBigOne.css({left:0+"px","z-index":"100"});

						$inBigThree.css({left:0+"px","z-index":"110"});

						$inBigTwo.css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$inBigTwo.css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$inBigOne.css({left:0+"px","z-index":"90"});

						$inBigTwo.css({left:docWidth+"px","z-index":"100"});

						$inBigThree.css({"z-index":"80"});

						$inBigFour.css({"z-index":"70"});					

					}



					$inBigTwo.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else if (n == 1) {	// 显示第3页

					if (boolean1 && !bigThis && n4==2) {

						$inBigOne.css({left:0+"px","z-index":"100"});

						$inBigTwo.css({left:0+"px","z-index":"110"});

						$inBigThree.css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && !bigThis && (n4==3||n4==0)) {

						$inBigTwo.css({left:0+"px","z-index":"100"});

						$inBigOne.css({left:0+"px","z-index":"110"});

						$inBigThree.css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$inBigThree.css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$inBigTwo.css({left:0+"px","z-index":"90"});

						$inBigThree.css({left:docWidth+"px","z-index":"100"});

						$inBigOne.css({"z-index":"80"});							

					}

					$inBigThree.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else {	// 显示第1页

					if (boolean1 && !bigThis && n4==1) {

						$inBigThree.css({left:0+"px","z-index":"100"});

						$inBigTwo.css({left:0+"px","z-index":"110"});

						$inBigOne.css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && !bigThis && n4==2) {

						$inBigTwo.css({left:0+"px","z-index":"100"});

						$inBigThree.css({left:0+"px","z-index":"110"});

						$inBigOne.css({left:docWidth+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$inBigOne.css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$inBigThree.css({left:0+"px","z-index":"90"});

						$inBigOne.css({left:docWidth+"px","z-index":"100"});

						$inBigTwo.css({"z-index":"80"});					

					}

					$inBigOne.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});						

				}

				in_moveT = setTimeout(_moveRight,10);

			}

		}

		function _moveLeft() { 

			if (t < docWidth) {		// 向左翻临界

				if (isIE6) {

					t+=30;

				}

				else {

					t+=6;

				}

				

				if (n == 2) {	// 显示第3页

					if (boolean1 && !bigThis && n4==0) {

						$inBigOne.css({left:0+"px","z-index":"100"});

						$inBigThree.css({left:0+"px","z-index":"110"});

						$inBigTwo.css({left:0+"px","z-index":"120"});

						$inBigTwo.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

					}

					else if (boolean1 && !bigThis && (n4==-1||n4==2)) {

						$inBigTwo.css({left:0+"px","z-index":"100"});

						$inBigThree.css({left:0+"px","z-index":"110"});

						$inBigOne.css({left:0+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$inBigThree.css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$inBigThree.css({left:0+"px","z-index":"90"});

						$inBigOne.css({left:0+"px","z-index":"100"});

						$inBigTwo.css({"z-index":"80"});											

					}

					$inBigOne.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else if (n == 1) {	// 显示第2页

					if (boolean1 && !bigThis && (n4==-1||n4==2)) {

						$inBigThree.css({left:0+"px","z-index":"110"});

						$inBigTwo.css({left:0+"px","z-index":"110"});

						$inBigOne.css({left:0+"px","z-index":"120"});

						$inBigOne.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

					}

					else if (boolean1 && !bigThis && n4==1) {

						$inBigOne.css({left:0+"px","z-index":"100"});

						$inBigTwo.css({left:0+"px","z-index":"110"});

						$inBigThree.css({left:0+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$inBigTwo.css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$inBigTwo.css({left:0+"px","z-index":"90"});

						$inBigThree.css({left:0+"px","z-index":"100"});

						$inBigOne.css({"z-index":"80"});

					}

					$inBigThree.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				else {	// 显示第1页

					if (boolean1 && !bigThis && n4==1) {

						$inBigTwo.css({left:0+"px","z-index":"100"});

						$inBigOne.css({left:0+"px","z-index":"110"});

						$inBigThree.css({left:0+"px","z-index":"120"});

						$inBigThree.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

					}

					else if (boolean1 && !bigThis && n4==0) {

						$inBigThree.css({left:0+"px","z-index":"100"});

						$inBigOne.css({left:0+"px","z-index":"110"});

						$inBigTwo.css({left:0+"px","z-index":"120"});

					}

					else if (boolean1 && bigThis) {

						$inBigOne.css({left:0+"px","z-index":"120"});

						return;

					}

					else {

						$inBigOne.css({left:0+"px","z-index":"90"});

						$inBigTwo.css({left:0+"px","z-index":"100"});

						$inBigThree.css({"z-index":"80"});

					}

					$inBigTwo.css({left:Math.ceil(Tween.Quart.easeInOut(t,0,docWidth,docWidth)) + "px"});

				}

				in_moveT = setTimeout(_moveLeft,10);

			}

		}

		

		$('.in_eng .in_pos img').attr({'src':'/static/images/in_gray.png'}).css({'border':'0px red solid','overflow':'hidden'});

		if (n==2 && boolean==true)

		{

			$('.in_eng .in_pos img').eq(0).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else if (n==2 && boolean==false) {

			$('.in_eng .in_pos img').eq(2).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else if (n==1 && boolean==false) {

			$('.in_eng .in_pos img').eq(n).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else if (n==0 && boolean==false) {

			$('.in_eng .in_pos img').eq(0).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

		else {

			$('.in_eng .in_pos img').eq(n+1).attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		}

	}



	/* 自动左右滚动 */

	function in_clockRight() {

		if (n3 == 3) {

			n3 = 0;

		}

		move(n3,true,false);

		

		n2 = n3;

		n3++;

		n1 = n3;

	}

	function in_clockLeft() {

		if (n3 == -1) {

			n3 = 2;

		}

		move(n3,false,false);

		n1 = n3;

		n3--;

		n2 = n3;		

	}

	

	if (isIE6) {

		in_clockT=setInterval(in_clockRight,8000);

	}

	else{

		//in_clockT=setInterval(in_clockRight,7000);

	}

	/* 小圆点点击 */

	$('.in_eng .in_pos span').click(function(){

		$('.in_eng .in_pos img').attr({'src':'/static/images/in_gray.png'}).css({'border':'0px red solid','overflow':'hidden'});

		$(this).find('img').attr({'src':'/static/images/in_blue.png'}).css({'border':'0px red solid','overflow':'hidden'});

		clearInterval(in_clockT);

		n4 = n3;

	})

	function in_dots(nR,strR,nL,strL) {

		if (moveR) {

			if (strR) {

				bigThis = true;

			}

			n3 = nR;

			move(n3,true,true);

			n3++;

			bigThis = false;

			if (isIE6) {

				in_clockT=setInterval(in_clockRight,8000);

			}

			else{

				in_clockT=setInterval(in_clockRight,7000);

			}

		}

		else {

			if (strL) {

				bigThis = true;

			}

			n3 = nL;

			move(n3,false,true);

			n3--;

			bigThis = false;

			if (isIE6) {

				in_clockT=setInterval(in_clockLeft,8000);

			}

			else{

				in_clockT=setInterval(in_clockLeft,7000);

			}

		}

		n1 = n3;

		n2 = n3;

	}

	$('.in_eng .in_pos span').eq(0).click(function () {

		strR = (n4==3 || n4==0);

		strL = (n4==-1 || n4==2);

		in_dots(2,strR,0,strL);

	})

	$('.in_eng .in_pos span').eq(1).click(function () {

		strR = (n4==1);

		strL = (n4==0);

		in_dots(0,strR,1,strL);

	})

	$('.in_eng .in_pos span').eq(2).click(function () {

		strR = (n4==2);

		strL = (n4==1);

		in_dots(1,strR,2,strL);

	})



	/* 右耳朵点击 */

	$('.in_eng .in_right').click(function(){

		if (n1 == 4) {

			n1 = 0;

		}

		clearInterval(in_clockT);

		move(n1,true,false);

		n2 = n1;

		n1++;

		n3 = n1;

		if (isIE6) {

			in_clockT=setInterval(in_clockRight,8000);

		}

		else{

			// in_clockT=setInterval(in_clockRight,7000);

		}

		moveR = true;

	})

	/* 左耳朵点击 */

	$('.in_eng .in_left').click(function(){

		if (n2 == -1) {

			n2 = 2;

		}

		clearInterval(in_clockT);

		move(n2,false,false);

		n1 = n2;

		n2--;

		n3 = n2;

		if (isIE6) {

			in_clockT=setInterval(in_clockLeft,8000);

		}

		else{

			in_clockT=setInterval(in_clockLeft,7000);

		}

		moveR = false;

	})//big pic end





	
	

}/**************************** index页 end *********************************************************///////////////////////////////////




// 
$(function () {
	var $body = $(".imgslider");
	var $wrap = $body.find(".in_move");
	var $items = $wrap.find(".in_item");
	var $rightBtn = $wrap.find(".in_right");
	var $leftBtn = $wrap.find(".in_left");
	var $dots = $wrap.find(".in_pos img");

	var ind = 0; //当前显示的
	var zind = 101;	// 当前显示的zind起始值,不用动
	var focusDotImg = "/static/images/in_blue.png";
	var unfocusDotImg = "/static/images/in_gray.png";
	var t;

	// 初始化位置
	$items.each(function (i,val) {
		var background = $(val).attr("data-img");
		
		if (i == 0) {
			$(val).css({
				"left":'0px',
				"z-index":zind,
				"background-image":"url("+background+")"
			});
		}else{
			$(val).css({
				"left":winW,
				"z-index":zind,
				"background-image":"url("+background+")"
			});
		}
	});

	var winW = $(window).width();

	var resizet = null;

	$(window).resize(function(){
	
		
		if (resizet) {
			clearTimeout(resizet);
		};


		if ($(window).width() > 2094) {
			winW = 2094
			$('#in_body').width(2094);
			$('.in_move').width(2094) 
			$('.in_bigone').width(2094);
			$('.in_bigtwo').width(2094);
			$('.in_bigthree').width(2094);


			$('.in_arrow').width(2094); 
			$('.in_left').css('left',($(window).width()-1080)/2);
			$('.in_right').css('right',($(window).width()-1080)/2);
		}else if($(window).width() <= 1180){
			winW = 1024
			$('#in_body').width(1024);
			$('#in_body .in_move').width(1024) 
			$('#in_body .in_bigone').width(1024);
			$('#in_body .in_bigtwo').width(1024);
			$('#in_body .in_bigthree').width(1024);

			$('.in_arrow').width(1024); 
			$('.in_left').css('left','0');
			$('.in_right').css('right','0');
		}else {
			winW = $(window).width();
			$('#in_body').width($(window).width());
			$('.in_move').width($(window).width()) 
			$('.in_bigone').width($(window).width());
			$('.in_bigtwo').width($(window).width());
			$('.in_bigthree').width($(window).width());

			$('.in_arrow').width($(window).width());
			$('.in_left').css('left',($(window).width()-1080)/2);
			$('.in_right').css('right',($(window).width()-1080)/2);
		}

		resizet = setTimeout(function () {
			$items.stop(true,true);
			$items.each(function (i,val) {
				var left = $(this).css("left") - 0;
				if (left != 0) {
					$(this).css("left")
				};
			})
		},50)

	});

	$rightBtn.click(next);

	function next () {
		ind++;
		if (ind >= $items.length) {
			ind = 0;
		};

		fixzind();

		tabRight(ind);

		if (t) {
			clearTimeout(t);
		};
		t = setTimeout(next, 7000);
	}

	function tabRight (index) {
		ind = index;
		$items.eq(index).stop().css({
			"left":winW,
			"z-index":getMaxZ()+1
		}).animate({
			"left" : 0
		}, 1500,"linear");

		$dots.each(function (i,val) {
			$(val).attr("src",i==index ? focusDotImg : unfocusDotImg);
		});
	}

	function tabLeft (index) {
		ind = index;
		$items.eq(index).stop().css({
			"left":0,
			"z-index":getMixZ()-1
		})
		$items.not(":eq("+index+")").stop().css({
			
		}).animate({
			"left" : winW
		}, 1500,"linear");

		$dots.each(function (i,val) {
			$(val).attr("src",i==ind ? focusDotImg : unfocusDotImg);
		});
	}

	$leftBtn.click(function () {
		ind--;
		if (ind < 0) {
			ind = $items.length - 1;
		};

		fixzind();

		tabLeft(ind);

		if (t) {
			clearTimeout(t);
		};
		t = setTimeout(next, 7000);

	});

	$dots.hover(function () {
		if (t) {
			clearTimeout(t);
		};
		var index = $dots.index(this);
		
		if (index > ind) {
			tabRight(index)
		}else if(index < ind){
			
			tabLeft(index)
		}

	},function () {
		t = setTimeout(next, 7000);
	})

	function getMixZ() {
		var mix;
		$items.each(function (i,val) {
			if (mix === undefined) {
				mix = $(val).css("z-index") - 0;
				return;
			};
			if (mix > $(val).css("z-index") - 0) {
				mix = $(val).css("z-index") - 0;
			};
		});
		return mix;
	}

	function getMaxZ () {
		var max;
		$items.each(function (i,val) {
			if (max === undefined) {
				max = $(val).css("z-index") - 0;
				return;
			};
			if (max < $(val).css("z-index") - 0) {
				max = $(val).css("z-index") - 0;
			};
		});
		return max;
	}

	function fixzind () {
		if (getMixZ() <= 80) {
			$items.css("z-index","+=20");
		};
		if (getMaxZ() >= 120) {
			$items.css("z-index","-=20");
		};
	}

	t = setTimeout(next, 6000);

})