var detail_url   = ctxPaths+ '/employee/overtime/department/detail_list.ajax';
var export_url = ctxPaths+ '/employee/overtime/department/detil_export.ajax';

var grid_selector = "#grid-table";
var pager_selector = "#grid-pager";
var m_end_day = [31,28,31,30,31,30,31,31,30,31,30,31];

var param_department3 = getParam("department3");
var param_department4 = '';
if(getParam("department4") != null){
	param_department4 = getParam("department4");
}
var param_startDate = getParam("startDate");
var param_endDate = getParam("endDate");

$(function($) {
	
	$.datepicker.regional['zh-CN'] = {   
		clearText: '清除',   
		clearStatus: '清除已选日期',   
		closeText: '关闭',   
		closeStatus: '不改变当前选择',   
		prevText: '<上月',   
		prevStatus: '显示上月',   
		prevBigText: '<<',   
		prevBigStatus: '显示上一年',   
		nextText: '下月>',   
		nextStatus: '显示下月',   
		nextBigText: '>>',   
		nextBigStatus: '显示下一年',   
		currentText: '今天',   
		currentStatus: '显示本月',   
		monthNames: ['一月','二月','三月','四月','五月','六月', '七月','八月','九月','十月','十一月','十二月'],   
		monthNamesShort: ['一','二','三','四','五','六', '七','八','九','十','十一','十二'],   
		monthStatus: '选择月份',   
		yearStatus: '选择年份',   
		weekHeader: '周',   
		weekStatus: '年内周次',   
		dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],   
		dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],   
		dayNamesMin: ['日','一','二','三','四','五','六'],   
		dayStatus: '设置 DD 为一周起始',   
		dateStatus: '选择 m月 d日, DD',   
		dateFormat: 'yy-mm-dd',   
		firstDay: 1,   
		initStatus: '请选择日期',   
		isRTL: false};   
	$.datepicker.setDefaults($.datepicker.regional['zh-CN']);   
	$("#startDate").datepicker();
	$("#endDate").datepicker();
	
	$("#startDate").val(param_startDate);
	$("#endDate").val(param_endDate);
	//$('#seachBtn').click();
	
	var grid_selector = "#apply-table";
	var pager_selector = "#grid-pager";
	init();
});


function init(){
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	jqGrid_init($(grid_selector),pager_selector,{
		url: detail_url,
		postData:{"params":{
							department3:param_department3,
							department4:param_department4,
							userNo:$("#userNo").val(),
							realname:$("#realname").val(),
							startDate:startDate,
							endDate:endDate}
				},
		rowNum:60,
		sortable : true,
		sortname : 'rate',
		sortorder:'desc',
		loadComplete : function(xhr){
			if (xhr.success) {
				
			  } else {
				  if (xhr.data && xhr.data.message) {
					  Q_Alert_Fail(xhr.data.message, "提示");
				  } else {
					  Q_Alert_Fail('原因未知');
				  }
			  }
		},
		colNames : ['工号','姓名', '三级部门', '四级部门', '标准工时', '加班工时','加班率'],
		colModel : [
			{name : 'userNo',index : 'userNo',width : 180,fixed : false,sortable : false,resize : false}, 
			{name : 'realname',index : 'realname',fixed : false,resize : false,width : 100},
			{name : 'department3',index : 'department3',fixed : false,resize : false,sortable : false,sortname : 'department3',width : 100},
			{name : 'department4',index : 'department4',fixed : false,resize : false,sortable : false,sortname : 'department4',width : 100},
			{name : 'workTime',index : 'workTime',fixed : false,resize : false,sortable : false,sortname : 'workTime',width : 100},
			{name : 'overTime',index : 'overTime',fixed : false,resize : false,sortable : true,sortname : 'overTime',width : 100},
			{name : 'rate',index : 'rate',fixed : false,resize : false,sortable : true,sortname : 'rate',formatter : addpred,width : 100},
			]
	});
};

$('#seachBtn').on('click', function(){
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	jQuery('#grid-table').jqGrid('setGridParam', {
		postData :{ "params":{
					department3:param_department3,
					department4:param_department4,
					userNo:$("#userNo").val(),
					realname:$("#realname").val(),
					type_:$("#type_").val(),
					startDate:startDate,
		endDate:endDate},
		page : 1,
		sortname : 'rate'
		}
	}).trigger("reloadGrid");
	
});		
		
$('#exportBtn').on('click', function(){
	var department3 = param_department3;
	var department4 = param_department4;
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var param = "";
	param =  "userNo="+$("#userNo").val()+"&realname="+$("#realname").val()+"&department3="+department3+"&department4="+department4+"&startDate="+startDate+"&endDate="+endDate+"&type_="+$("#type_").val()+"&";
	var url = export_url + "?" + param + new Date().getTime();
	window.open(encodeURI(url));
});
	
function fastSetQryDate(a){
	var curDate = new Date();
	curDate.setDate(curDate.getDate() - 1);
	$("#endDate").val(dateFormat(curDate));	
	if(0 == a){ // 周					
		curDate.setDate(curDate.getDate() - 6 );
		$("#startDate").val(dateFormat(curDate));
	}else{
		curDate.setMonth(curDate.getMonth() - a);
		curDate.setDate(curDate.getDate() - 1);
		$("#startDate").val(dateFormat(curDate));
	}
	$('#seachBtn').click();
}
	
function dateFormat(date){
	return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
}

function getParam(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var url = decodeURI(window.location.search);
	var r = url.substr(1).match(reg); 
	if (r != null) {
		return unescape(r[2]); 
	}else{
		return null;
	}
}

function addpred(cellvalue, options, rowObject) {		
	return "<div>"+rowObject.rate+"%</div>";				
}

