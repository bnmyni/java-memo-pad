/**自营百货经销合同*/
$(function(){
	$("[name='s:me_zybhjxht_jskk:kklx']").trigger("change");
});
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

function calAllPuwei(obj){
	var puweiTrs = $(".listRow:visible",$("[tablename$='pwxx']")); 
	var shangPuNums = "";
	puweiTrs.each(function (i) {
	  var pwId = $("[name$=':pwh']",$(this)).val();
	  shangPuNums = shangPuNums + pwId;
	  if(i != puweiTrs.length-1)shangPuNums = shangPuNums+"-";
	});
	$("[name='m:me_zybhjxht:zjh']").val(shangPuNums); 
}

/*校验子表填充数据是否重复**/ 
function checkDataBeforeInsert(data,tableName){
	if(tableName == 'me_zybhjxht_pwxx'){
	      var rows =   $(".listRow",$("[tableName='me_zybhjxht_pwxx']"));
	      for(var i =0,row;row=rows[i++];){
	        var id = $("[name$=':pwID']",$(row)).val();  //唯一值
	          if(id ==data.WLDPID) {                              //对话框，返回数据的那列
	    	   $.ligerDialog.warn(data.WLDPDM+"已经存在了！","提示信息");
	            return false
	         }
	      }
	    //校验该铺位是否被租出去*/
        var zuLinStartDate  =$("[name='m:me_zybhjxht:htyxqq']").val();
		if(!zuLinStartDate ){  $.ligerDialog.warn("尚未输入合同开始日期！",'请核查');  return false }
		var conf ={aliasName:'validatePuweiIsUsed',pwid:data.WLDPID,sDate:zuLinStartDate};
		var json = RunAliasScript(conf);
		if(json.isSuccess ==1){
			 $.ligerDialog.warn(json.msg);return false;
		}else if(json.result){
			$.ligerDialog.warn("该铺位到期日期为:"+json.result+"，请核查！");return false;
		}
	      
    }
     return true;
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