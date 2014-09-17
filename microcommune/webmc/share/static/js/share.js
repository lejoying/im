function test() {

	$.ajax({
		type : "POST",
		timeout : 32000,
		url : "/api2/share/getshare?",
		data : {
			phone : 151,
			accessKey : "lejoying",
			gid : Request.QueryString("gid"),
			gsid : Request.QueryString("gsid")
		},
		success : function (data) {
			var contentStr = data.shares[0].content;
			var content = JSON.parse(contentStr);

			$(".user-describe pre")[0].innerText = content[0].detail;
			$(".user-img img")[0].src = "http://images2.we-links.com/images/" + content[1].detail;
		},
		error : function (xhr, error) {}
	});
}

Request = {
	QueryString : function (item) {
		var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]*)(\&?)", "i"));
		return svalue ? svalue[1] : svalue;
	}
}

$(document).ready(function () {
	test();
});
