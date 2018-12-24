var list_url  = ctxPaths+ '/employee/overtime/list.ajax';
var export_url   = ctxPaths+ '/employee/overtime/export.ajax';
var project_url = ctxPaths+ '/employee/overtime/ratio/getProjectlist.ajax';
var grid_selector = "#grid-table";
var pager_selector = "#grid-pager";
var m_end_day = [31,28,31,30,31,30,31,31,30,31,30,31];
var project_list_html = "";
var fa_project_list_html = "";
var fa_project = new Array();

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
	init();
	//查询
	fastSetQryDate('1');
	//$('#seachBtn').click();
});

function init(){
	
	//查询项目信息
	$.ajaxSubmit(project_url, {},
		function(rtn2) {
			if (rtn2.success) {
				$.each(rtn2.data,function(i,e){
					project_list_html = project_list_html + '<option value="'+rtn2.data[i].code_+'" class="sel_code projectCode_'+rtn2.data[i].serviceCode+'">'+rtn2.data[i].code_+'</option>';
					if( $.inArray(rtn2.data[i].serviceCode, fa_project) == -1){
						fa_project_list_html = fa_project_list_html + '<option value="'+rtn2.data[i].serviceCode+'">'+rtn2.data[i].serviceName+'</option>'
						fa_project.push(rtn2.data[i].serviceCode);
					}
				});
				
				$("#code").append(project_list_html);
				$("#serviceName").append(fa_project_list_html);
			}
	});
}

$('#seachBtn').on('click', function(){
	var serviceCode = $("#serviceName").val();
	var serCode = $(".serviceCode");
	/*for(var i  = 0; i < serCode.length; i++){
		if (serCode[i].checked ==true) {
			serviceCode = serviceCode + ",'" + serCode[i].value +"'" ;
		}
	}*/
	var code = $("#code").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	
	$.ajaxSubmit(list_url, {'serviceCode':serviceCode,'code':code,'startDate':startDate,'endDate':endDate},
	  function(rtn) {
		if (rtn.success) {
			workTimeModel(rtn);		  					  
		} else {
		  if (rtn.data && rtn.data.message) {
			  Q_Alert_Fail(rtn.data.message, "提示");
		  } else {
			  Q_Alert_Fail('原因未知');
		  }
		}
	});
});
			
$('#exportBtn').on('click', function(){
	var serviceCode =  $("#serviceName").val();
	var serCode = $(".serviceCode");
	/*for(var i  = 0; i < serCode.length; i++){
		if (serCode[i].checked ==true) {
			serviceCode = serviceCode + ",'" + serCode[i].value +"'" ;
		}
	}*/
	//var serviceCode = $("#serviceCode").val();
	var code = $("#code").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var param = "";
	param = "serviceCode="+serviceCode+"&code="+code+"&startDate="+startDate+"&endDate="+endDate+"&";
	export_url = export_url + "?" + param + new Date().getTime();
	window.open(encodeURI(export_url));
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

$('#restBtn').on('click', function(){
	//$("#code").find(".serviceCode").remove();
	//$("#code").append(code_base_html);
});

function workTimeModel(response){
	var $tableModel =$("#tableModel");
	var htmlTr =[];
	var modelLen;
	
	if(response.data[0] == null){
		htmlTr.push('<tr><td colspan="7" style="text-align: center; line-height: 24px;">暂无数据</td></tr>');
		$tableModel.find("tBody").html(htmlTr.join(""));
	}else{
		$.each(response.data,function(){
			modelLen =this.length;

			$.each(this,function(index){
				if(index==0){
					htmlTr.push('<tr><td rowspan='+modelLen+' class="first" style="width:180px;white-space:pre-wrap" title="'+this["serviceName"]+'">'+this["serviceName"]+'</td>');
				}else{
					htmlTr.push('<tr class="'+(index==(modelLen-1)?"subtotal":"")+'">');
				}

				htmlTr.push(
					'<td>'+this["code"]+'</td>' +
					'<td>'+this["totalPeople"]+'</td>' +
					'<td>'+this["workTime"]+'</td>' +
					'<td>'+this["overTime"]+'</td>' +
					'<td>'+this["averageTime"]+'</td>' +
					'<td>'+this["rate"]+'%</td>');
				htmlTr.push('</tr>');
			});
		});

		$tableModel.find("tBody").html(htmlTr.join(""));
		$('#tableModel tr:last').find('td').addClass('end');
	}
	
}

$("#serviceName").change(function(){
	$("#code").find(".sel_code").hide();
	$("#code").find(".sel_code").remove();
	$("#code").append(project_list_html);
	var allShow = true;
	if($("#serviceName").val() != ''){
		allShow = false;
		$("#code .sel_code").each(function(i,e){
			var str = "projectCode_"+$("#serviceName").val() ;
			var className = e.className;
			className =className.split(' ')[1];
			if(className != str){
				$("#code").find("."+className).remove();
			}
		});
	}
});