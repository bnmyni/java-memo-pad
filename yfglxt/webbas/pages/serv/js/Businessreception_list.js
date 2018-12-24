$(document).ready(function() {
	Loading.show();
	if (getP("biz_id")) {
		editEvent(getP("biz_id"));
	}
	if(getP("from_page")){
		$('#from_page').val(getP("from_page"));		
	}
		
	function editEvent(id) {
		$.ajaxSubmit(window.ctxPaths+ "/servapp/servapp/get.ajax",{'biz_id' : id},
		function(data) {
			if (data.success == true) {
				Loading.hide();
				$("#bizId").val(data.data.bizId);
				$("#servOrg").text(data.data.servOrg);
				if(data.data.servOrg == '系统事业部-市场销售部-市场销售部'){
					$("#accountSubjectRow").show();
					$("#coastAimRow").show();
				}
				if(data.data.guestOrg){
					$("#guestOrg").text(data.data.guestOrg);
				}
				$("#servReason").text(data.data.servReason);
				$("#servLoc").text(data.data.servLoc);
				$("#guestNum").text(data.data.guestNum);
				$("#servNum").text(data.data.servNum);
				if(data.data.servRemark){
					$("#servRemark").text(data.data.servRemark);
				}
				if(data.data.dishFee){
					$("#dishFee").text(data.data.dishFee);
				}
				if(data.data.giftFee){
					$("#giftFee").text(data.data.giftFee);
				}	
				$("#sumFee").text(data.data.sumFee);
				$("#servDate").text(data.data.servDate.substr(0,10));
				var date_str = data.data.appDate.substr(0,4)+'年'+data.data.appDate.substr(5,2)+'月'+data.data.appDate.substr(8,2)+'日';
				if(data.data.bizStatus=='success'){
					date_str = "年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日";
				}
				$('#appDateShow').html(date_str);
				if(data.data.servType=='out'){
					$("#servType").html('外事(√)&nbsp;&nbsp;&nbsp;商务(&nbsp;)&nbsp;&nbsp;&nbsp;其他公务(&nbsp;)');
				}else if(data.data.servType=='biz'){
					$("#servType").html('外事(&nbsp;)&nbsp;&nbsp;&nbsp;商务(√)&nbsp;&nbsp;&nbsp;其他公务(&nbsp;)');
				}else if(data.data.servType=='other'){
					$("#servType").html('外事(&nbsp;)&nbsp;&nbsp;&nbsp;商务(&nbsp;)&nbsp;&nbsp;&nbsp;其他公务(√)');
				}
				
				if(data.data.accountSubject=='1'){
					$("#accountSubject").html('安全业务线(√)&nbsp;&nbsp;&nbsp;非安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}else if(data.data.accountSubject=='2'){
					$("#accountSubject").html('安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;非安全业务线(√)&nbsp;&nbsp;&nbsp;');
				}else{
					$("#accountSubject").html('安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;非安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}
				if(data.data.coastAim=='1'){
					$("#coastAim").html('日常业务招待费(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}else if(data.data.coastAim=='2'){
					$("#coastAim").html('日常业务招待费(√)&nbsp;&nbsp;&nbsp;');
				}else {
					$("#coastAim").html('日常业务招待费(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}
				
				if(data.data.bizStatus=='success'){
					$('#print').show();
				}
				if(getP("from_page")=="myApply" && data.data.upStatus!='R'
					&& data.data.upStatus!='Y'&& data.data.endStatus!='R'
					&& data.data.endStatus!='Y'){
					$('#back').show();
				}
				$('#back').show();
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
	}	
});

$("#back").click(function(){
	Loading.show();
	var form = $("#businessform");
	$.ajax({
		url : window.ctxPaths +  "/servapp/servapp/back.ajax",
		type : "POST",
		dataType : "json",
		data : form.serialize(),
		success : function(data) {
			Loading.hide();
			if(data.success){
				Q_Alerttimeout(data.data, 1500,function(){
					close_current_tag_and_redirct_by_pagename("myApply", false);
				});
			}else{
				Q_Alert_Fail(data.message);
			}
		},
		error : function() {
			Loading.hide();
			Q_Alert_Fail('提交驳回申请失败,请查看网络');
		}
	})
});

$("#print").click(function() {
	$(this).hide();
	$("#cancel").hide();
	window.print();
	$(this).show();
	$("#cancel").show();
});	

$("#cancel").click(function(){		
	close_current_tag_and_redirct_by_pagename($('#from_page').val(), false);
});	