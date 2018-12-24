$(document).ready(function() {
	if(getP("from_page")){
		$('#from_page').val(getP("from_page"));
	}
									
	Loading.show();
	if (getP("biz_id")) {
		editEvent(getP("biz_id"));
	}

	function editEvent(id) {
		$.ajaxSubmit(window.ctxPaths+ "/servapp/servapp/get.ajax",{'biz_id' : id},
			function(data) {
				if (data.success == true) {
					Loading.hide();
					$("#bizId").val(data['bizId']);
					$("#bizStatus").val(data['bizStatus']);
					$("#upStatus").val(data['upStatus']);
					$("#endStatus").val(data['endStatus']);				
					$("#bizSn").val(data['bizSn']);
					$("#endLeader").val(data['endLeader']);
					$("#upLeader").val(data['upLeader']);
					$("#appPerson").val(data['appPerson']);
					if(data.data.servOrg == '系统事业部-市场销售部-市场销售部'){
						$('#accountSubjectRow').show(); 
						$('#coastAimRow').show(); 
					}
					
					
					if(data.data.dishFee){
						$("#dishFee").val(data.data.dishFee);
					}
					if(data.data.giftFee){
						$("#giftFee").val(data.data.giftFee);
					}					
					$("#sumFee").text('￥'+data.data.sumFee);														
					$("#hideSumFee").val(data.data.sumFee);
					$("#appOrg").val(data['appOrg']);
					$('#businessform').json2Form2(data.data);
					$("#servDate").val(data.data.servDate.substr(0,10));
					$("#appDate").val(data.data.appDate.substr(0,10));
					
					$('#appDateShow').text(data.data.appDate.substr(0,4)+'年'+data.data.appDate.substr(5,2)+'月'+data.data.appDate.substr(8,2)+'日');
					$('#txt_appPersonName').val(data.data.appPersonName);
					$("#appPersonName").text(data.data.appPersonName);
					
					if(data.data.departStatus=='Y'){
						$('#div_departLeader').show();
						$("#departLeaderName").text(data.data.departLeaderName);
						var temp = "";
						if(typeof(data.data.departOpinion) == "undefined" 
							|| data.data.departOpinion == null 
							|| data.data.departOpinion == ""
							|| data.data.departOpinion == "null"){
								temp = "";
						}else{
							temp = ":"+data.data.departOpinion;
						}
						var opinion = '(同意'+temp+')';
						$("#departOpinion").text(opinion);
					}else if (data.data.departStatus=='R'){
						$('#div_departLeader').show();
						$("#departLeaderName").text(data.data.departLeaderName);
						var opinion = '(驳回:'+data.data.departOpinion+')';
						$("#departOpinion").text(opinion);
					}
					
					if(data.data.upStatus=='Y'){
						$('#div_upLeader').show();
						$("#upLeaderName").text(data.data.upLeaderName);
						var temp = "";
						if(typeof(data.data.upOpinion) == "undefined" 
							|| data.data.upOpinion == null 
							|| data.data.upOpinion == ""
							|| data.data.upOpinion == "null"){
								temp = "";
						}else{
							temp = ":"+data.data.upOpinion;
						}
						var opinion = '(同意'+temp+')';
						$("#upOpinion").text(opinion);
					}else if (data.data.upStatus=='R'){
						$('#div_upLeader').show();
						$("#upLeaderName").text(data.data.upLeaderName);
						var opinion = '(驳回:'+data.data.upOpinion+')';
						$("#upOpinion").text(opinion);
					}
					if(data.data.endStatus=='Y'){
						$('#div_endLeader').show();
						$("#endLeaderName").text(data.data.endLeaderName);
						var temp = "";
						if(typeof(data.data.endOpinion) == "undefined" 
							|| data.data.endOpinion == null 
							|| data.data.endOpinion == ""
							|| data.data.endOpinion == "null"){
								temp = "";
							}else{
								temp = ":"+data.data.endOpinion;
							}
							var opinion = '(同意'+temp+')';
							$("#endOpinion").text(opinion);
					}else if (data.data.endStatus=='R'){
							$('#div_endLeader').show();
							$("#endLeaderName").text(data.data.endLeaderName);
							var opinion = '(驳回:'+data.data.endOpinion+')';
							$("#endOpinion").text(opinion);
					}										
				} else {
					Loading.hide();
					Q_Alert_Fail(data.message);
				}
		});
	};
	var myDate = new Date();
	var year = myDate.getFullYear();
	var month = myDate.getMonth() + 1;
	var day = myDate.getDate();
	var date = year + "-" + month + "-" + day + "";
});						

$("#servDate").datepicker({//添加日期选择功能  
	numberOfMonths : 1,//显示几个月  
	showButtonPanel : false,//是否显示按钮面板  
	dateFormat : 'yy-mm-dd',//日期格式  
	clearText : "清除",//清除日期的按钮名称   
	yearSuffix : '年', //年的后缀  
	showMonthAfterYear : true,//是否把月放在年的后面  
	monthNames : [ '一月', '二月', '三月', '四月','五月', '六月', '七月', '八月', '九月','十月', '十一月', '十二月' ],
	dayNames : [ '星期日', '星期一', '星期二','星期三', '星期四', '星期五', '星期六' ],
	dayNamesShort : [ '周日', '周一', '周二','周三', '周四', '周五', '周六' ],
	dayNamesMin : [ '日', '一', '二', '三','四', '五', '六' ],
});						
						
$("#cancel").click(function(){
	close_current_tag_and_redirct_by_pagename($('#from_page').val(), false);
});	

$("#save").click(function() {
	if($("#servOrg").val() == ""){
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
		add_validator(window.ctxPaths +  "/servapp/servapp/res.ajax");
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

function add_validator(add_url) {
	Loading.show();
	var form = $("#businessform");
	var url = add_url;
	$.ajax({
		url : url,
		type : "POST",
		dataType : "json",
		data : form.serialize(),
		success : function(data) {
			Loading.hide();
			if(data.success){
				Q_Alerttimeout(data.data, 1500,function(){
					close_current_tag_and_redirct_by_pagename($('#from_page').val(), true);
				});
			}else{
				Q_Alert_Fail(data.message);
			}
		},
		error : function() {
			Loading.hide();
			Q_Alert_Fail('提交申请失败,请查看网络');
		}
	})
};	