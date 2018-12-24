$(document).ready(function() {	
	if(getP("from_page")){
		$('#from_page').val(getP("from_page"));
	}
	loadDepartmentInfo();
	var myDate = new Date();
	var year = myDate.getFullYear();
	var month = myDate.getMonth() + 1;
	month = month>9?month:('0'+month);
	var day = myDate.getDate();
	day = day>9?day:('0'+day);
	var date = year + "-" + month + "-" + day;
	var dateStr = year+"年"+month+"月"+day+"日";
	$("#appDate").val(date);
	$("#appDateShow").html(dateStr);
});

function loadDepartmentInfo(){
	
	$.ajax({
		url : window.ctxPaths+ "/servapp/servapp/getDepartmentInfo.ajax",
		type : "POST",
		dataType : "json",
		success : function(data) {
			if(data.success){
				var departmentInfo = data.data;
				$('#servOrg').val(departmentInfo);
				if(departmentInfo == '系统事业部-市场销售部-市场销售部'){
					$('#accountSubjectRow').show();
					$('#coastAimRow').show();
					top.scrollTo(0,50);
				}else{
					$('#accountSubjectRow').remove();
					$('#coastAimRow').remove();
				}
			}
		},
		error : function() {
			Q_Alert_Fail('获取部门信息失败,请查看网络');
		}
	});
}

$("#servDate").datepicker({//添加日期选择功能  
	numberOfMonths : 1,//显示几个月  
	showButtonPanel : false,//是否显示按钮面板  
	dateFormat : 'yy-mm-dd',//日期格式  
	clearText : "清除",//清除日期的按钮名称   
	yearSuffix : '年', //年的后缀  
	showMonthAfterYear : true,//是否把月放在年的后面  
	monthNames : [ '一月', '二月', '三月', '四月','五月', '六月', '七月', '八月', '九月', '十月','十一月', '十二月' ],
	dayNames : [ '星期日', '星期一', '星期二', '星期三','星期四', '星期五', '星期六' ],
	dayNamesShort : [ '周日', '周一', '周二', '周三','周四', '周五', '周六' ],
	dayNamesMin : [ '日', '一', '二', '三', '四','五', '六' ],
});

function add_validator(add_url) {
	var form = $("#businessform");
	var url = add_url;
	$.ajax({
		url : url,
		type : "POST",
		dataType : "json",
		data : form.serialize(),
		success : function(data) {
			if(data.success){
				Q_Alerttimeout(data.data, 1500,function(){
					close_current_tag_and_redirct_by_pagename("myApply", true);
				});
			}else{
				Q_Alert_Fail(data.message);
			}
		},
		error : function() {
			Loading.hide();
			Q_Alert_Fail('系统运行异常，请稍后再试');
		}
	})
};			
			
$("#save").click(function() {
	if($("input[name='accountSubject']").size()>0 
			&& $("input[name='accountSubject']:checked").length == 0 ){
		Q_Alert_Fail('科目归属不允许为空，请核对后再提交');
	}else if($("input[name='coastAim']").size()>0 
			&& $("input[name='coastAim']:checked").length == 0 ){
		Q_Alert_Fail('费用目的不允许为空，请核对后再提交');
	}else if($("#servOrg").val() == ""){
		$("#servOrg").focus();
		Q_Alert_Fail('负责接待的部门不允许为空，请核对后再提交');
	}else if($("#servReason").val() == ""){
		$("#servReason").focus();
		Q_Alert_Fail('接待事由不允许为空，请核对后再提交');
	}else if($("#servDate").val() == ""){
		$("#servDate").focus();
		Q_Alert_Fail('接待日期不允许为空，请核对后再提交');
	}else if($("#servLoc").val() == ""){
		$("#servLoc").focus();
		Q_Alert_Fail('接待地点不允许为空，请核对后再提交');
	}else if($("#guestNum").val() == ""){
		$("#guestNum").focus();
		Q_Alert_Fail('来宾人数不允许为空，请核对后再提交');
	}else if($("#servNum").val() == ""){
		$("#servNum").focus();
		Q_Alert_Fail('陪同人数不允许为空，请核对后再提交');
	}else if($("#dishFee").val() == "" && $("#giftFee").val() == ""){
		if($("#dishFee").val() == ""){
			$("#dishFee").focus();
			Q_Alert_Fail('宴请费用不允许为空，请核对后再提交');
		}else{
			$("#giftFee").focus();
			Q_Alert_Fail('纪念品费用不允许为空，请核对后再提交');
		}				
	}else{
		add_validator(window.ctxPaths +  "/servapp/servapp/add.ajax");
	}
});			
			
$("#guestNum").blur(function(){
	CheckFigure_onBlur(this);
	getSumFee();
});
				
$("#guestNum").keyup(function(){
	CheckFigure_onKeyup(this);
	getSumFee();
});
				
$("#servNum").blur(function(){
	CheckFigure_onBlur(this);
	getSumFee();
});
				
$("#servNum").keyup(function(){
	CheckFigure_onKeyup(this);
	getSumFee();
});
						
$("#dishFee").blur(function(){
	CheckFigure_onBlur(this);
	getSumFee();
});
				
$("#dishFee").keyup(function(){
	CheckFigure_onKeyup(this);
	getSumFee();
});
				
$("#giftFee").blur(function(){
	CheckFigure_onBlur(this);
	getSumFee();
});
				
$("#giftFee").keyup(function(){
	CheckFigure_onKeyup(this);
	getSumFee();
});	

$('#servReason').keyup(function(){
	txtMaxCode_onKeyup(this,200);
});	

$('#servReason').blur(function(){
	txtMaxCode_onBlur(this,200);
});	

$('#servRemark').keyup(function(){
	txtMaxCode_onKeyup(this,200);
});	

$('#servRemark').blur(function(){
	txtMaxCode_onBlur(this,200);
});

$('#guestOrg').keyup(function(){
	txtMaxCode_onKeyup(this,80);
});	

$('#guestOrg').blur(function(){
	txtMaxCode_onBlur(this,80);
});

$('#servLoc').keyup(function(){
	txtMaxCode_onKeyup(this,20);
});	

$('#servLoc').blur(function(){
	txtMaxCode_onBlur(this,20);
});

$('#servDate').blur(function(){
	date_onBlur(this);
});		
			
function getSumFee(){
	var guestNum = 0;
	if($('#guestNum').val().length>0){
		guestNum = parseInt($('#guestNum').val());
	}
	var servNum = 0;
	if($('#servNum').val().length>0){
		servNum = parseInt($('#servNum').val());
	}
	var dishFee = 0.00;
	if($('#dishFee').val().length>0){
		dishFee = parseFloat($('#dishFee').val());
	}
	var giftFee = 0.00;
	if($('#giftFee').val().length>0){
		giftFee = parseFloat($('#giftFee').val());
	}
	if((guestNum+servNum)<=0 || (dishFee+giftFee)<=0.00){
		$("#sumFee").text('￥0');
		$("#hideSumFee").val(0.00);
		return;
	}
	var sum = ((guestNum+servNum)*(dishFee+giftFee));
	$("#sumFee").text('￥'+sum);
	$("#hideSumFee").val(sum);
}
			
$("#cancel").click(function(){
	cancel_onclick();
});