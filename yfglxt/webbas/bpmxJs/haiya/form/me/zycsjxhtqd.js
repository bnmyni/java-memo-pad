/**自营超市经销合同*/
$(function(){
	$("[name='s:me_zybhjxht_jskk:kklx']").trigger("change");
});

/**扣款类型change事件*/
function kklxChangeEvent(obj){
	var kklx = $(obj).val();
	var curRow = $(obj).closest(".listRow");
	$("[name$=':kkje'],[name$=':kkbl']",curRow).removeAttr("readonly");
	switch(kklx){
		case "0":
		case "1":		
			 $("[name$='kkbl']",curRow).attr("readonly","readonly").val("");
		  break;
		case "2":
		case "3":
			 $("[name$=':kkje']",curRow).attr("readonly","readonly").val("");
		  break;
	}
}

/*显示租户信息*/
function showZh(){
	var zhid = $("[name$='zhID']").val();
	if(zhid == null || zhid == ""){
		$.ligerDialog.warn("请先选择租户!","提示");
		return ;
	}
	var url=__ctx + "/platform/form/bpmDataTemplate/detailData_shxxwh.ht?__pk__="+zhid;
	DialogUtil.open({
		height:600,
		width: 800,
		title : "查看租户信息",
		url: url, 
		isResize: true
	});
}