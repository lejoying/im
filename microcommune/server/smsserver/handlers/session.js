var session = {};

smsSession = {};

session.event = function (data, response) {
    response.asynchronous = 1;
    smsSession["sms"] = response;
}

session.notify = notify;
function notify(data, response) {

    var session = smsSession["sms"];
    session.write(data);
    session.end();

    response.write(JSON.stringify({
        "information": "send sms success"
    }));
    response.end();
}

module.exports = session;