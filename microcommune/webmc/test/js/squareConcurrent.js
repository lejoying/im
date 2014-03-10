function commit()
{
	alert($(".js_textarea_text").val());

	var text = $(".js_textarea_text").val();
	var phone = $(".js_textarea_phone").val();
	sendMessage(phone, text);

}

function sendMessage(phone, text)
{
	$.ajax(
	{
		type : "POST",
		url : "/api2/square/sendsquaremessage",
		datatype : "json",
		data :
		{
			phone : phone,
			accessKey : "lejoying" +"testuser"+ 1,
			gid : "91",
			message : JSON.stringify(
			{
				contentType : "text",
				content : text
			}
			)
		},
		success : function (data)  {}
	}
	);
}

function getMessage(phone, flag)
{
	$.ajax(
	{
		type : "GET",
		url : "/api2/square/getsquaremessage",
		timeout : 30000,
		data :
		{
			phone : phone,
			accessKey : "lejoying" + phone,
			gid : "91",
			flag : flag
		},
		success : function (data)
		{
			console.log(phone, " get message ", data, data.messages[0].content);
			var newflag = data.flag;
			getMessage(phone, newflag);
		},
		error : function (XMLHttpRequest, textStatus, errorThrown)
		{
			if (textStatus == "timeout")
			{
				getMessage(phone, flag);
			}
		}
	}
	);
}

function createConcurrentUser()
{
	var userCount = 5;
	for (var id = 0; id < userCount; id++)
	{

		getMessage("testuser" + id, -1);

	}

}

createConcurrentUser();
