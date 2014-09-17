function isFromMobile()
{
        return (navigator.userAgent.match(/iphone|android|phone|mobile|wap|netfront|x11|java|operamobi|operamini|ucweb|windowsce|symbian|symbianos|series|webos|sony|blackberry|dopod|nokia|samsung|palmsource|xda|pieplus|meizu|midp|cldc|motorola|foma|docomo|up.browser|up.link|blazer|helio|hosin|huawei|novarra|coolpad|webos|techfaith|palmsource|alcatel|amoi|ktouch|nexian|ericsson|philips|sagem|wellcom|bunjalloo|maui|smartphone|iemobile|spice|bird|zte-|longcos|pantech|gionee|portalmmm|jig browser|hiptop|benq|haier|^lct|320x320|240x320|176x220/i));
}
function isIphone()
{
	return /(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent);
}

function isAndroid()
{
	return /(Android)/i.test(navigator.userAgent);
}