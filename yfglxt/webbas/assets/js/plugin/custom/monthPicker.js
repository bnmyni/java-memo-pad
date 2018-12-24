/*********************************************************
*    使用方法
*        控件调用onclick="monthPicker(event,this,this,10)"
*        10:表示年份的起始间距，如：当前年份-10
*    如下，给控件设置为readonly
*    <input type="text" style="border:1px solid #cccccc;"
*        size="15" onclick="monthPicker(event,this,this,10)"
*        onfocus="this.select()" readonly="readonly" />
*   
*    如果页面乱码，把下面包含汉字的定义项放到页面中即可
********************************************************/
var gMonths = new Array("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月");
var WeekDay = new Array("日", "一", "二", "三", "四", "五", "六");
var strToday = "今天";
var strYear = "年";
var strMonth = "月";
var strDay = "日";
var clearButton = "清除";
var splitChar = "-";
var startYear = 2000;
var endYear = 2050;
var dayTdHeight = 12;
var dayTdTextSize = 12;
var gcNotCurMonth = "#E0E0E0";
var gcRestDay = "#FF0000";
var gcWorkDay = "#444444";
var gcMouseOver = "#79D0FF";
var gcMouseOut = "#F4F4F4";
var curMonth = "#F4A460";
var gcToday = "#444444";
var gcTodayMouseOver = "#6699FF";
var gcTodayMouseOut = "#79D0FF";
var gdCtrl = new Object();
var goSelectTag = new Array();
var gdCurDate = new Date();
var giYear = gdCurDate.getFullYear();
var giMonth = gdCurDate.getMonth() + 1;
var giDay = gdCurDate.getDate();
function hanshu() {
	var elements = new Array();
	for (var i = 0; i < arguments.length; i++) {
		var element = arguments[i];
		if (typeof(arguments[i]) == 'string') {
			element = document.getElementById(arguments[i]);
		}
		if (arguments.length == 1) {
			return element;
		}
		elements.Push(element);
	}
	return elements;
}
Array.prototype.Push = function() {
	var startLength = this.length;
	for (var i = 0; i < arguments.length; i++) {
		this[startLength + i] = arguments[i];
	}
	return this.length;
}
String.prototype.HexToDec = function() {
	return parseInt(this, 16);
}
String.prototype.cleanBlank = function() {
	return this.isEmpty() ? "": this.replace(/\s/g, "");
}
function checkColor() {
	var color_tmp = (arguments[0] + "").replace(/\s/g, "").toUpperCase();
	var model_tmp1 = arguments[1].toUpperCase();
	var model_tmp2 = "rgb(" + arguments[1].substring(1, 3).HexToDec() + "," + arguments[1].substring(1, 3).HexToDec() + "," + arguments[1].substring(5).HexToDec() + ")";
	model_tmp2 = model_tmp2.toUpperCase();
	if (color_tmp == model_tmp1 || color_tmp == model_tmp2) {
		return true;
	}
	return false;
}
function hanshuV() {
	try {return hanshu(arguments[0]).value; }catch (e){}
}
function monthPicker(evt, popCtrl, dateCtrl, years) {
	evt.cancelBubble = true;
	gdCtrl = dateCtrl;
	var date = new Date();
	fSetYearMon(giYear, giMonth);
	var ie = navigator.appName == "Microsoft Internet Explorer" ? true: false;
	if (ie) {
		var point = fGetXY2(popCtrl);
	} else {
		var point = fGetXY(popCtrl);
	}
	with(hanshu("calendardiv").style) {
		left = point.x + "px";
		top = (point.y + popCtrl.offsetHeight + 1) + "px";
		visibility = 'visible';
		zindex = '99';
		position = 'absolute';
	}
	altYears(years);
	hanshu("calendardiv").focus();
	$('span#nowMonthSpan').empty().html(giYear+strYear+giMonth+strMonth);
}
function fSetDate(iYear, iMonth, iDay) {
	//var iDayNew = new String(iDay);
	var iMonthNew = new String(iMonth);
	if (iMonthNew.length < 2) {
		iMonthNew = "0" + iMonthNew;
	}
	//gdCtrl.value = iYear + splitChar + iMonthNew + splitChar + iDayNew;
	gdCtrl.value = iYear + iMonthNew;
	fHideCalendar();
}
function fHideCalendar() {
	hanshu("calendardiv").style.visibility = "hidden";
	for (var i = 0; i < goSelectTag.length; i++) {
		goSelectTag[i].style.visibility = "visible";
	}
	goSelectTag.length = 0;
}
function fSetSelected() {
	var iOffset = 0;
	var iDay = 0;
	var iYear = parseInt(hanshu("tbSelYear").value);
	var iMonth = parseInt(arguments[0]);
	var monstr = iMonth;
	if(iMonth<10){
	    monstr  ="0"+iMonth;
	}
	var gMon = giMonth;
	if(giMonth<10){
	    gMon = "0"+giMonth;
	}
	var curTime = giYear+""+gMon;
	var seleTime = iYear+""+monstr;
	if(parseInt(seleTime)<=parseInt(curTime)){
	    fSetDate(iYear, iMonth, iDay);
	}else{
		return false;
	}
}
function Point(iX, iY) {
	this.x = iX;
	this.y = iY;
}
function fBuildCal(iYear, iMonth) {
	var aMonth = new Array();
	for (var i = 1; i < 7; i++) {
		aMonth[i] = new Array(i);
	}
	var dCalDate = new Date(iYear, iMonth - 1, 1);
	var iDayOfFirst = dCalDate.getDay();
	var iDaysInMonth = new Date(iYear, iMonth, 0).getDate();
	var iOffsetLast = new Date(iYear, iMonth - 1, 0).getDate() - iDayOfFirst + 1;
	var iDate = 1;
	var iNext = 1;
	for (var d = 0; d < 7; d++) {
		aMonth[1][d] = (d < iDayOfFirst) ? (iOffsetLast + d) * ( - 1) : iDate++;
	}
	for (var w = 2; w < 7; w++) {
		for (var d = 0; d < 7; d++) {
			aMonth[w][d] = (iDate <= iDaysInMonth) ? iDate++:(iNext++) * ( - 1);
		}
	}
	return aMonth;
}
function fDrawCal(iYear, iMonth, iCellHeight, iDateTextSize) {
	var colorTD = " bgcolor='" + gcMouseOut + "' bordercolor='" + gcMouseOut + "'";
	var styleTD = " valign='middle' align='center' style='height:" + iCellHeight + "px;font-weight:bolder;font-size:" + iDateTextSize + "px;";
	var dateCal = "";
	for (var w = 1; w < 3; w++) {
		dateCal += "<tr>";
		for (var d = 0; d < 7; d++) {
			var tmpid = w + "" + d;
			dateCal += "<td" + styleTD + "cursor:pointer;' onclick='fSetSelected(" + tmpid + ")'>";
			dateCal += "<span id='cellText" + tmpid + "'></span>";
			dateCal += "</td>";
		}
		dateCal += "</tr>";
	}
	return dateCal;
}
function fUpdateCal(iYear, iMonth) {
	for(var t=1;t<3;t++){
		for(var c=1;c<7;c++){
			var mh = 6*(t-1)+c;
			with(hanshu("cellText"+mh)){
				parentNode.bgColor = gcMouseOut;
				parentNode.borderColor = gcMouseOut;
				parentNode.onmouseover = function() {
					this.bgColor = gcMouseOver;
				};
				parentNode.onmouseout = function() {
					this.bgColor = gcMouseOut;
				};
				
				if (iMonth==mh) {
					parentNode.bgColor = curMonth;
				}
			}
		}
	}
}
function fSetYearMon(iYear, iMon) {
	for (var i = 0; i < hanshu("tbSelYear").length; i++) {
		if (hanshu("tbSelYear").options[i].value == iYear) {
			hanshu("tbSelYear").options[i].selected = true;
		}
	}
	fUpdateCal(iYear, iMon);
}
function fPrevMonth() {
	var iMon =0;
	var iYear = hanshu("tbSelYear").value;
	iYear--;
	fSetYearMon(iYear, iMon);
}
function fNextMonth() {
	var iMon = 0;
	var iYear = hanshu("tbSelYear").value;
	iYear++;
	fSetYearMon(iYear, iMon);
}
function fGetXY(aTag) {
	var oTmp = aTag;
	var pt = new Point(0, 0);
	do {
		pt.x += oTmp.offsetLeft;
		pt.y += oTmp.offsetTop;
		oTmp = oTmp.offsetParent;
	} while ( oTmp . tagName . toUpperCase () != "BODY");
	return pt;
}
function fGetXY2(aTag) {
	var oTmp = aTag;
	var pt = new Point(0, 0);
	do {
		pt.x += oTmp.offsetLeft;
		pt.y += oTmp.offsetTop;
		oTmp = oTmp.offsetParent;
	} while ( oTmp . tagName . toUpperCase () != "BODY");
	return pt;
}
function getDateDiv() {
	var noSelectForIE = "";
	var noSelectForFireFox = "";
	var colorTD = " bgcolor='" + gcMouseOut + "' bordercolor='" + gcMouseOut + "'";
	if (document.all) {
		noSelectForIE = "onselectstart='return false;'";
	} else {
		noSelectForFireFox = "-moz-user-select:none;";
	}
	var dateDiv = "";
	dateDiv += "<div id='calendardiv' onclick='event.cancelBubble=true' " + noSelectForIE + " style='" + noSelectForFireFox + "position:absolute;z-index:99;visibility:hidden;border:1px solid #999999;left:600px;top:30px;'>";
	dateDiv += "<table border='0' bgcolor='#E0E0E0' cellpadding='1' cellspacing='1' >";
	dateDiv += "<tr>";
	dateDiv += "<td><input type='button' id='PrevMonth' value='<' style='height:25px;width:25px;font-weight:bolder;' onclick='fPrevMonth()'>";
	dateDiv += "</td><td colspan='4' align='center'><select id='tbSelYear' style='width:80px;border:1px solid #CCC;background-color:#E0E0E0;' onchange='fUpdateCal(hanshuV(\"tbSelYear\"),hanshuV(\"tbSelMonth\"))'>";
	for (var i = giYear; i > giYear-10; i--) {
		dateDiv += "<option value='" + i + "'>" + i + strYear + "</option>";
	}
	dateDiv += "</select></td><td>";
	dateDiv += "<input type='button' id='NextMonth' value='>' style='height:25px;width:25px;font-weight:bolder;' onclick='fNextMonth()'>";
	dateDiv += "</td>";
	dateDiv += "</tr>";
	dateDiv += "<tr><td colspan='6'><div style='border-bottom:1px solid #CCC;'></div></td></tr>";
	for(var t=1;t<3;t++){
		dateDiv += "<tr>";
		for(var c=1;c<7;c++){
			var mh = 6*(t-1)+c;
			dateDiv += "<td "+colorTD+" valign='middle' align='center' style='cursor:pointer;height:25px;font-weight:bolder;font-size:12px;width:25px' onclick='fSetSelected(" + mh + ")'>";
			dateDiv += "<span id='cellText"+mh+"'>"+mh+"</span>";
			dateDiv += "</td>";
		}
		dateDiv += "</tr>";
    }
	dateDiv += "<tr><td colspan='6'><div style='border-bottom:1px solid #CCC;'></div></td></tr>";
	dateDiv += "<tr><td colspan='6'>";
	dateDiv += "<span id='nowMonthSpan' style='float:left;font-size:13px;margin-top:3px;color:#8B7765;'></span>";
	dateDiv += "<input type='button' value='"+clearButton+"' onclick='clearValue()' style='float:right;width:50px;height:25px;line-height:25px;'/>";
	dateDiv += "</td></tr>";
	dateDiv += "</table></div>";
	return dateDiv;
}
with(document) {
	onclick = fHideCalendar;
	write(getDateDiv());
}

function clearValue(){
	gdCtrl.value = '';
	fHideCalendar();
}

function altYears(years){
	if(years!=null){
		var options = "";
		for (var i = giYear; i > giYear-years; i--) {
			options += "<option value='" + i + "'>" + i + strYear + "</option>";
		}
		$('select#tbSelYear').empty().append(options);
	}
}