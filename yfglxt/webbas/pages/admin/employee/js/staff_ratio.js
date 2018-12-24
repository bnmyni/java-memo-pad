var detail_url = ctxPaths+ '/employee/overtime/ratio/list.ajax';
var update_url = ctxPaths+ '/employee/overtime/ratio/update.ajax';
var export_url = ctxPaths+ '/employee/overtime/ratio/detil_export.ajax';
var project_url = ctxPaths+ '/employee/overtime/ratio/getProjectlist.ajax';
var save_project_url = ctxPaths+ '/employee/overtime/ratio/saveProjectlist.ajax';

var doimport_url = ctxPaths+ '/employee/overtime/ratio/doImport.ajax';

var grid_selector = "#grid-table";
var pager_selector = "#grid-pager";
var project_list_html = "";
var fa_project_list_html = "";
var fa_project = new Array();

var code_base_html = '<option value="安全系统部" class="serviceCode serviceCode_00">安全系统部</option>'+
				'<option value="运维系统部" class="serviceCode serviceCode_00">运维系统部</option>'+
				'<option value="运营系统部" class="serviceCode serviceCode_00">运营系统部</option>'+
				'<option value="平台开发一部" class="serviceCode serviceCode_01">平台开发一部</option>'+
				'<option value="平台开发二部" class="serviceCode serviceCode_01">平台开发二部</option>'+
				'<option value="测试部" class="serviceCode serviceCode_01">测试部</option>'+
				'<option value="需求部" class="serviceCode serviceCode_01">需求部</option>'+
				'<option value="架构部" class="serviceCode serviceCode_01">架构部</option>';

$(function($) {
	
	var grid_selector = "#apply-table";
	var pager_selector = "#grid-pager";
	init();
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
		
	jqGrid_init($(grid_selector),pager_selector,{
		url: detail_url,
		postData:{},
		sortable : true,
		sortname : 't.user_no',
		//sortorder:'desc',
		loadComplete : function(xhr){
			if (xhr.success) {
				//添加ID标记
				$.each(xhr.rows,function(i,e){
					$(grid_selector).find("tr button").eq(i+1).attr("data-id",xhr.rows[i].userNo);
				});
			} else {
				if (xhr.data && xhr.data.message) {
					Q_Alert_Fail(xhr.data.message, "提示");
				} else {
					Q_Alert_Fail('原因未知');
				}
			}            
		},
		colNames : ['工号','姓名', '三级部门', '四级部门','项目', '子项目', '投入百分比','操作'],
		colModel : [
			{name : 'userNo',index : 'userNo',width : 100,fixed : false,sortable : false,resize : false}, 
			{name : 'realName',index : 'realName',fixed : false,resize : false,width : 100},
			{name : 'department3',index : 'department3',fixed : false,resize : false,sortable : false,width : 100},
			{name : 'department4',index : 'department4',fixed : false,resize : false,sortable : false,width : 100},
			{name : 'serviceName',index : 'serviceName',fixed : false,resize : false,sortable : false,width : 100},
			{name : 'code',index : 'code',fixed : false,resize : false,sortable : false,width : 100},
			{name : 'ratio',index : 'ratio',fixed : false,resize : false,sortable : true,width : 100,formatter : addpred,},
			{name : 'userNo',index : 'userNo',fixed : false,resize : false,sortable : false,width : 80,
				formatter:function(cellvalue, options, rowObject){
                                         return "<button class='redact btn  btn-primary  btn-xs' onclick='redact(&quot;"+rowObject.realName+"&quot;,&quot;"+rowObject.department4+"&quot;)' >" +
                                             "<i class='fa fa-pencil'></i>" +
                                             "</button>";
                                     }
			}
			]
	});
};

$('#seachBtn').on('click', function(){
	//var startDate = $("#startDate").val();
	//var endDate = $("#endDate").val();
	var department3 = ""; //$("#serviceCode").val();
	var serCode = $(".serviceCode");
	for(var i  = 0; i < serCode.length; i++){
		if (serCode[i].checked ==true) {
			if('00' == serCode[i].value){
				department3 = department3 + ",'应用研发部'" ;
			}else if('01' == serCode[i].value){
				department3 = department3 + ",'平台研发二部'" ;
			}
			
		}
	}
	/*var department3 = $("#department3").val();
	if(department3 == '00'){
		department3 = '应用研发部';
	}else if(department3 == '01'){
		department3 = '平台研发二部';
	}*/
	jQuery('#grid-table').jqGrid('setGridParam', {
		postData :{ "params":{
					department3:department3,
					department4:$("#department4").val(),
					userNo:$("#userNo").val(),
					realName:$("#realName").val(),
					type_:$("#type_").val(),
					code:$("#code").val(),
					serviceName:$("#serviceName").val()
					//startDate:startDate,
					//endDate:endDate  type_=, userNo=, realName=, code=, department4=, serviceName=
					},
				
			},
			page : 1,
			sortname : 't.user_no'
	}).trigger("reloadGrid");
});		

	
$('#exportBtn').on('click', function(){
	var department3 = ""; //$("#serviceCode").val();
	var serCode = $(".serviceCode");
	for(var i  = 0; i < serCode.length; i++){
		if (serCode[i].checked ==true) {
			if('00' == serCode[i].value){
				department3 = department3 + ",'应用研发部'" ;
			}else if('01' == serCode[i].value){
				department3 = department3 + ",'平台研发二部'" ;
			}
			
		}
	}
	
	//var department3 = $("#department3").val();
	var department4 = $("#department4").val();
	//var startDate = $("#startDate").val();
	//var endDate = $("#endDate").val();
	var param = "";
	param =  "userNo="+$("#userNo").val()+"&realName="+$("#realName").val()+"&department3="+department3+"&department4="+department4+"&serviceCode="+$("#serviceName").val()+"&code="+$("#code").val()+"&type_="+$("#type_").val()+"&";
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

//导入
$('#importBtn').on('click', function(){
	$.ajax({
		type: "POST",
		url: doimport_url,
        data: {},
        async:true,
		success: function (response) {
			//alert(response.data);
			jQuery('#grid-table').jqGrid('setGridParam', {
				postData :$('#queryForm').serializeJson(),
				page : 1,
				sortname : 't.user_no'
			}).trigger("reloadGrid");
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			alert("数据同步异常");
		}
	});
});

/*编辑=*/
var userNo_ = "";
var realName_ ="";

var col = 0;
function redact(realName,department4){
	$("#proj_div").empty();
	//project_list_html = "<option>全部</option>";
	col = 0;
	 //弹窗
	bootbox.dialog({
		title:"人员投入信息编辑",
		message:$(".pop_moduless").html(),
		show: true,
		backdrop: true,
		animate: true,
		className:"details_pop"
	});

	$.ajaxSubmit(detail_url, {"params":{department4:department4,realName:realName}},
		function(rtn) {
			if (rtn.success) {
				$.each(rtn.rows[0],function(i,e){
					//console.log(data.data.id);
					
					$(".details_pop").find("."+i).children("span").text(e);
					
					if(i == 'department3'){
						$(".details_pop").find("."+i).children("span").text(e+"/"+rtn.rows[0].department4);
					}
					
					if(i == 'type_'){
						if(e == 0){
							$(".details_pop").find("."+i).children("span").text("自有");
						}else{
							$(".details_pop").find("."+i).children("span").text("外包");
						}
					}
					
					if(i == 'code'){
						if(e != null && e != ""){
							var htmlPorj = '';
							$(".details_pop .proj_div").empty();
							$(".proj_div").empty();
							for(var a = 0; a < rtn.rows.length; a++){
								htmlPorj = '<div class="col-xs-6" style="height:45px;"><div class="form-group"><label>项目：</label><select id="code'+col+'" class="code  code'+col+'"><option value="" class="sel_code">全部</option></select></div></div><div class="col-xs-5" style="height:45px;"><div class="form-group ratio"><label>投入比例：</label><input class="ratio'+col+'" value="">%</div></div>';
								//htmlPorj = '<div class="col-xs-6"><div class="form-group"><label>项目：</label><select id="serviceName'+col+'" class="serviceName  serviceName'+col+'"><option value="" class="">全部</option></select></div></div> <div class="col-xs-6"><div class="form-group"><label>子项目：</label><select id="code'+col+'" class="code  code'+col+'"><option value="" class="sel_code">全部</option></select></div></div><div class="col-xs-5"><div class="form-group ratio"><label>投入比例：</label><input class="ratio'+col+'" value="">%</div></div>';
								$(".details_pop .proj_div").append(htmlPorj);
								
								$(".code"+col).append(project_list_html);
								$(".code"+col).find("option[value='"+rtn.rows[a].code+"']").attr("selected",true);
								$(".ratio"+col).val(rtn.rows[a].ratio);
								
								$(".serviceName"+col).append(fa_project_list_html);
								$(".serviceName"+col).find("option[value='"+rtn.rows[a].serviceCode+"']").attr("selected",true);
								col++;
							}
						}
						
					}
					
				});
				init();
			} else {
				if (rtn.data && rtn.data.message) {
				  Q_Alert_Fail(rtn.data.message, "提示");
				} else {
				  Q_Alert_Fail('原因未知');
				}
			}
	});		
};

function addProject(){
	var codeNum = "code"+col;
	var htmlPorj = '<div class="col-xs-6" style="height:45px;"><div class="form-group"><label>项目：</label><select id="'+codeNum+'" class="code  '+codeNum+'"><option value="" class="sel_code">全部</option></select></div></div><div class="col-xs-6" style="height:45px;"><div class="form-group ratio"><label>投入比例：</label><input class="ratio'+col+'" />(%)</div></div>';
	$(".details_pop .proj_div").append(htmlPorj);
	
	$("."+codeNum).find(".sel_code").remove();
	$("."+codeNum).append(project_list_html);
	col++;
}

function saveProject(){
	var userNo = $(".details_pop .userNo span").html();
	var code_list = $(".details_pop .code");
	var sub_code = "";
	var sub_ratio = "";
	var all = 0;
	for(var i=0;i< code_list.length; i++){
		sub_code = sub_code+ ";"+ $(".details_pop #code"+i).val();
		sub_ratio = sub_ratio + ";"+ $(".details_pop .ratio"+i).val();
		all  =parseInt(all)+ parseInt($(".details_pop .ratio"+i).val());
	}
	if(all != 100){
		alert("投入总比例必须为100%");
		return false;
	}
	
	$.ajaxSubmit(save_project_url, {"userNo":userNo,"code_":sub_code,"ratioList":sub_ratio},
		function(rtn) {
			Q_Alert_Fail(rtn.data);
	});
}

function addpred(cellvalue, options, rowObject) {		
	return "<div>"+rowObject.ratio+"%</div>";				
}


$("input[type='checkbox']").change(function(){
	$("#department4").find(".serviceCode").hide();
	$("#department4").append(code_base_html);
	var allShow = true;
	$("input[type='checkbox']").each(function(){
		if($(this).is(':checked')){	
			allShow = false;
		}else{
			$("#department4").find(".serviceCode_" + $(this).val()).remove();
		}	
	});
	if(allShow){
		$("#department4").find(".serviceCode").remove();
		$("#department4").append(code_base_html);
	}
});

/*
$("#department3").change(function(){
	$("#department4").find(".serviceCode").hide();
	$("#department4").append(code_base_html);
	var allShow = true;
	if($("#department3").val() == '00'){
		allShow = false;
		$("#department4").find(".serviceCode_01").remove();
	}else if($("#department3").val() == '01'){
		allShow = false;
		$("#department4").find(".serviceCode_00").remove();
	}
	if(allShow){
		$("#department4").find(".serviceCode").remove();
		$("#department4").append(code_base_html);
	}
});*/

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

