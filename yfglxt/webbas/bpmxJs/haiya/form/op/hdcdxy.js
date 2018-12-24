/**【活动场地协议】*/
$(function(){
	sfcnzhChangeEvent();//是否场内租户
});
/*租赁时间变化事件**/
function zlsjChangeEvent(){
	validateZujinBiaozhunDates();
	$("[name='s:op_hdcdxy_klzgz:sjd']").trigger("change");
}
/**是否场内租户onchange*/
function sfcnzhChangeEvent(){
	var isZuHu = $("[name='m:op_hdcdxy:sfcnzh']");
	if(isZuHu.length==0) type = $("#sfcnzh").text().trim();
	else isZuHu = isZuHu.val();
	
	if(isZuHu==0 || isZuHu =='否') {
		$("#changNeiZuHu").hide();
		$("input",$("#changNeiZuHu")).val("");
	}
	else  $("#changNeiZuHu").show();
}
/*显示租户信息*/
function showZh(){
	var zhid = $("[name='m:op_hdcdxy:zhID']").val();
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
/*校验临时场地子表填充数据是否重复**/ 
function checkDataBeforeInsert(data,tableName){
    if(tableName == 'op_hdcdxy_lscd'){
        var rows = $(".listRow",$("[tableName='"+tableName+"']"));  //左右子表列
        for(var i =0,row;row=rows[i++];){
          var id = $("[name='s:"+tableName+":lscddm']",$(row)).val();  //唯一值
            if(id == data.WLDPDM) {                       //对话框，返回数据的那列
      	   $.ligerDialog.warn(data.WLDPDM+"已经存在了！","提示信息");
              return false
           }
        }
        
      //校验该铺位是否被租出去
        var zuLinStartDate  =$("[name='m:op_hdcdxy:zlyxqq']").val();
		if(!zuLinStartDate ){  $.ligerDialog.warn("尚未输入租赁开始日期！",'请核查');  return false }
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

/* 表租金标准信息 添加行事件*/
function op_hdcdxy_zjbzxxAddRowAfterEvent(row){
	var preRow = $(row).prev();
	var preNx = $("[name$=':xh']",preRow).val();
	if(!preNx)preNx = 0;
	$("[name$=':xh']",row).val(1+Number(preNx));
	validateZujinBiaozhunDates(row)
};
// 校验租金标准 时间
function validateZujinBiaozhunDates(curRow){
	//租赁有效期起，止
	var zuLinStartDate  =$("[name='m:op_hdcdxy:zlyxqq']").val();
	var zuLinEndDate  =$("[name='m:op_hdcdxy:zlyxqz']").val();
	if(!zuLinStartDate || !zuLinStartDate){
		$.ligerDialog.warn("尚未输入租赁起止日期！",'请核查'); 
		$(curRow).remove();
		return
	}
	
	var rows = $(".listRow:visible",$("[tablename='op_hdcdxy_zjbzxx']"));
	for(var i=0,row;row=rows[i++];){
		var shiJianDuan = $("[name$='sjd']",$(row)).val(i); //时间段
		
		if(i>1){
			//开始日期等于上个结束日期加1
			var startDate = getNewDataStr($("[name$='jsrq']",$(rows[i-2])).val(),1,1);
			$("[name$='ksrq']",$(row)).val(startDate);
		}else{
			var differTotalStartDate = FormDate.compareDate($("[name$='ksrq']",$(row)).val(), zuLinStartDate);
			if(differTotalEndDate>=0)  {
				$.ligerDialog.warn("当前开始日期不能早于 租赁开始日期！",'请核查');
			} 
			$("[name$='ksrq']",$(row)).val(zuLinStartDate);
		}
		var differTotalEndDate = FormDate.compareDate($("[name$='jsrq']",$(row)).val(), zuLinEndDate, "day");
		if(differTotalEndDate<0)  {
			$.ligerDialog.warn("当前结束日期不能晚于 租赁有效期止！",'请核查');
			$("[name$='jsrq']",$(row)).val("")
			return ;
		}
	}
	$("[name$='dj']",curRow).trigger("change");
	$("[name='s:op_hdcdxy_klzgz:sjd']").trigger("change");
}
/*修改租金规则*/
function changeZujinGz(obj){
	var curRow = $(obj).closest(".listRow");
	var danJia = $("[name$='dj']",curRow);
	var zuJin = $("[name$='zj']",curRow);
	var rule = $(obj).val();
	
	if(rule == "2" || rule=="4"){ //纯扣
		danJia.val(0);zuJin.val(0);
		danJia.attr("readonly","readonly");
		zuJin.attr("readonly","readonly");
	}else{
		danJia.removeAttr("readonly");
		zuJin.removeAttr("readonly"); 
	}
	danJia.trigger("change");zuJin.trigger("change");
	createKoulvzu();
	
}

function createKoulvzu(){
	if($(".listRow:visible",$("[tablename$='klz']")).length ==0){
		FormUtil.addRow($('div[tablename$="klz"]'));  
		var appendRow = $(".listRow:visible",$("[tablename$='klz']"))[0];
		$("[name$='klzbh']",appendRow).val(1);
		$("[name$='jckl']",appendRow).val(1);
		$("[name$='ms']",appendRow).val("扣组率1");
	}
}
/**单价计算总租金
 * trigger 'dj'/'zj' 触发者
 * */
function calZongZuJin(obj){
	var trigger = $(obj).attr("name").split(":")[2];
	var curRow = $(obj).closest(".listRow");
	var val = FormUtil.commaback($(obj).val()); 
	if(! val>0) return; 
	
	var mianJi = $("[name='m:op_hdcdxy:jzmj']").val();
	if(!mianJi){ $.ligerDialog.warn(" 合同建筑面积不能为空！",'请核查'); return ; }
	
	var zuJin,danJia;
	/* 如果是单价*/
	if('dj'== trigger){
		danJia = val;
		zuJin = mianJi * danJia;
		$("[name$=':zj']",curRow).val(FormMath.tofixed(zuJin,2));
	}else if('zj' == trigger){
		zuJin = val;
		danJia =zuJin/mianJi;
		$("[name$=':dj']",curRow).val(FormMath.tofixed(danJia,2));
	}else return;
	
	var baoDiType =$("[name$='bdxx']",curRow).val();
	var startDate = $("[name$='ksrq']",curRow).val();
	var endDate = $("[name$='jsrq']",curRow).val();
	if(baoDiType =="0"){ //按月
		var zongZujin = calMountZujin(startDate,endDate,zuJin);
	}else{
		var days = FormDate.dateVal(startDate, endDate, "day");
		var zongZujin = zuJin*days;
	}
	zongZujin = FormMath.tofixed(zongZujin,2);
	$("[name$='zzj']",curRow).val(zongZujin).trigger("change");
 }

/*总租金。月度计算法*/
function calMountZujin(startTime,endTime,zuJin){
	startTime = startTime.replace(/\-/g, "/");
	endTime = endTime.replace(/\-/g, "/");
	var startDate = new Date(startTime); //开始时间
	var endDate = new Date(endTime); //结束时间
	
	var num=0;
	var year=endDate.getFullYear()-startDate.getFullYear();
		num+=year*12;
	var month=endDate.getMonth()-startDate.getMonth();
		num+=month;
		
	var amount = zuJin * num; //月租金
	var day=endDate.getDate()-startDate.getDate()+1;   //
	amount = amount + day*zuJin/30  // 少于一月 减相差金额，多于一月 加多的金额
	return amount;
}


/*生成扣率组规则*/
function createKoulvRules(){
	return;
	//租金标准信息
	var zjbzxx = $(".listRow:visible",$("[tablename='me_zlzjbzxx']"));
	var kouLvs = $(".listRow:visible",$("[tablename='me_zlklz']"));
	var klzggRows =$(".listRow:visible",$("[tablename='me_zlklzgz']")); //扣率组规则
	var klzggTableDiv = $("div[tablename='me_zlklzgg']");
	if(zjbzxx.length==0 ||kouLvs.length==0){
		$.ligerDialog.warn("租金标准信息或者扣率组尚未完善！",'请核查');return;
	}
	klzggRows.remove();debugger;
	var rowIndex=0;
	for(var j=0,kouLv;kouLv=kouLvs[j++];){
		for(var i=0,row;row=zjbzxx[i++];){
			var sjd = $("name$='sjd'",row).val();
			var kouLvZu = $("name$='klzbh'",kouLv).val();
			
			FormUtil.addRow(klzggTableDiv);
			var curRol = $(".listRow:visible",$("[tablename='me_zlklzgz']"))[rowIndex++];
			$("name$='sjd'",curRol).val(sjd);$("name$='sjd'",curRol).trigger("change");
			$("name$='lvz'",curRol).val(kouLvZu);$("name$='lvz'",curRol).trigger("change");
		}
	}
}
/*处理扣率时间段*/
function handelKoulvTime(obj){
	var curRow = $(obj).closest(".listRow");
	var shijianNo = $("[name$='sjd']",curRow).val();
	if(!shijianNo) return ;
	/*取的时间段*/
	var shiJianDuanNo =$("[name='s:op_hdcdxy_zjbzxx:sjd'][value="+shijianNo+"]");
	if(shiJianDuanNo.length ==0) {
		$.ligerDialog.warn("该时间段不存在！ “"+shijianNo+"”",'请核查');
		$("[name$='sjd']",curRow).val("");
		return ;
	}
	var shiJianDuanRow = shiJianDuanNo.closest(".listRow");
	var StartDate = $("[name$='ksrq']",shiJianDuanRow).val();
	var endDate = $("[name$='jsrq']",shiJianDuanRow).val();
	if(!StartDate || !endDate) {
		$.ligerDialog.warn("改时间段信息不完善！ “"+shijianNo+"”",'请核查');
		$("[name$='sjd']",curRow).val("");
		return ;
	}
	
	$("[name$='ksrq']",curRow).val(StartDate);
	$("[name$='jsrq']",curRow).val(endDate);
	
	initXSJEQvalidateRule();
}

/*处理扣率组*/
function handelKoulvGroup(obj){
	var curRow = $(obj).closest(".listRow");
	var koulvNo = $(obj).val();
	if(!koulvNo) return ;
	/*取扣率组*/
	var KouLv =$("[name='s:op_hdcdxy_klz:klzbh'][value="+koulvNo+"]");
	if(KouLv.length ==0) {
		$.ligerDialog.warn("该扣率组不存在！ “"+koulvNo+"”",'请核查');
		$(obj).val("");
		return ;
	}
	initXSJEQvalidateRule();
}
/*循环判断扣率组，初始化销售金额起,校验扣率组金额*/
function initXSJEQvalidateRule(){ 
	var kouLvRules = $(".listRow:visible",$("[tablename='op_hdcdxy_klzgz']"));
	var combineMessage = [];
	for(var i=0,rule;rule=kouLvRules[i++];){
		var sjd = $("[name$='sjd']",rule).val(); /*时间段*/
		var klz = $("[name$='klz']",rule).val();/*扣率组*/
		if(!sjd || !klz) return;
		 /*第一次设置开始金额为当前金额为第几行*/	
		var len=-1;
		for(var j=0,r;r=combineMessage[j++];){
			if(r.split("-")[0] == sjd+","+klz){
				len =Number(r.split("-")[1]);
			}
		}
		if(len==-1){
			$("[name$='zqxsjeq']",rule).val("0");$("[name$='zqxsjeq']",rule).trigger("change");
		}else{
			var JJJine = $("[name$='zqxsjez']",$(kouLvRules[len])).val(); /*上一截止金额*/
			$("[name$='zqxsjeq']",rule).val(JJJine);
		}
		combineMessage.push(sjd+","+klz+"-"+(i-1));
	}
} 
/*校验周期销售金额止*/
function 　checkZqxsjez(obj){
	var curRow = $(obj).closest(".listRow");
	 var JeQi = $("[name$='zqxsjeq']",curRow).val();
	 var jeZhi =$(obj).val();
	 if(JeQi&&jeZhi)
	 if( FormUtil.commaback(JeQi) > FormUtil.commaback(jeZhi)){
		 $.ligerDialog.warn("周期销售金额止:"+jeZhi+  "　不应该小于 周期销售金额起 ："+JeQi+"",'请核查！');
			$(obj).val("");
			return ;
	 }
	 initXSJEQvalidateRule();
}


/*结算标准信息   删除行事件*/
function op_hdcdxy_zjbzxxDelRowBeforeEvent(row){
	var xh = $("[name$=':xh']",row).val();
	$("[name='s:me_lybdxx:xh'][value=" + xh + "]").closest("tr").remove();/*清除旧的记录*/
}

/*分解所有结算标准信息  */
function decomposeAllJs(tableName,fenjieTable){
	var $jsbzxxTrs = $(".listRow:visible",$("[tablename='"+tableName+"']"));   
	$jsbzxxTrs.each(function (i) {
		decomposeSingle($(this),fenjieTable);
	});
	
	var $jsbzxxTrs = $("input[name$=':fjsj']:checked",$("[tablename='"+tableName+"']")).closest("tr");
	showFjsj($jsbzxxTrs,fenjieTable);
}

function decomposeAllYzj(){
	decomposeAllJs('op_hdcdxy_zjbzxx','op_hdcdxy_ydzjfj');
}
function decomposeSingleYzj(){
	decomposeSingleJs('op_hdcdxy_zjbzxx','op_hdcdxy_ydzjfj');
}

/*单个分解*/
function decomposeSingleJs(tableName,fenjieTable){
	$jsbzxxTrs = $("input[name$=':fjsj']:checked",$("div[tablename='"+tableName+"']")).closest("tr");
	if($jsbzxxTrs.length ==0){
		$.ligerDialog.warn("请选择要分解的数据！","提示信息");
		return;
	}
	
	//分解
	decomposeSingle($jsbzxxTrs,fenjieTable);
	showFjsj($jsbzxxTrs,fenjieTable);
} 

/*【通过分解目标行来分解信息，将结果输出至分解表】 
 * 被选中的行：selectRow,分解表的表明fenJieTable*/
function decomposeSingle(selectRow,fenJieTable){
	var xh = selectRow.find("[name$=':xh']").val();	
	if(!xh){
		$.ligerDialog.warn("请选择要分解的时间段","提示");
		return ;
	}
	//分解表
	var fenJieTableDiv = $("div[tablename='"+fenJieTable+"']");
	/*将序号与当前选中行所有分解信息删除*/
	$("[name$=':xh'][value=" + xh + "]",fenJieTableDiv).closest("tr").remove(); /*清除旧的记录*/
	
	var sjd =$("[name$='xh']",selectRow).val(); //序号
	var ksrq =$("[name$='ksrq']",selectRow).val();
	var jsrq =$("[name$='jsrq']",selectRow).val();
	var type=$("[name$='bdxx']",selectRow).val();
	var zj =FormUtil.commaback($("[name$='zj']",selectRow).val()); 
	var sfgz =$("[name='m:op_hdcdxy:zjsfgzID']").val();

	var jsonData =  decompose(sjd,ksrq,jsrq,zj,sfgz,"",type);
	/*填充数据*/
	for (var i = 0, c; c = jsonData[i++];) {
		FormUtil.addRow(fenJieTableDiv);
		var rowcount=$("input[name$=':ny']",fenJieTableDiv).length;
		$($("input[name$=':xh']",fenJieTableDiv).get(rowcount-1)).val(xh);		
		$($("input[name$=':ny']",fenJieTableDiv).get(rowcount-1)).val(c.ZQY);				
		$($("input[name$=':ksrq']",fenJieTableDiv).get(rowcount-1)).val(c.KSRQ);			
		$($("input[name$=':jsrq']",fenJieTableDiv).get(rowcount-1)).val(c.JSRQ);			
		$($("input[name$=':je']",fenJieTableDiv).get(rowcount-1)).val(c.YZJ);	
		$($("input[name$=':scrq']",fenJieTableDiv).get(rowcount-1)).val(c.CDRQ);	
	}

}

/*分解数据*/
function decompose(sjd,ksrq,jsrq,zj,gzid,mbxs,type){
	if(!zj) zj =0;
	if(!type) type=0;
	var paramJson = {jlbh:sjd,ksrq:ksrq,jsrq:jsrq,yzj:zj,zjjsbz:type};
	if(mbxs) paramJson.mbxs =mbxs;
	if(gzid) paramJson.gzid =gzid;
	var jsonParams = [];
	jsonParams.push(paramJson);
	var conf = {aliasName:'decompose',paramJson:JSON.stringify(jsonParams)};
	var json = RunAliasScript(conf); 
    if(json.isSuccess==0){
    	return JSON.parse(json.result);
	 }else{
		 $.ligerDialog.error("分解失败："+json.result,"提示信息");
		 return [];
	 }
}


/*显示分解信息列表 */
function showFjsj(obj,fenJieTable){
	var fenJieTableDiv =$("div[tablename='"+fenJieTable+"']");
	var tableDiv = $(obj).closest("[tablename]");
	var selectRow =$("input[name=':fjsj']:checked",tableDiv).closest("tr");
	
	var xh = selectRow.find("[name$=':xh']").val();/*选择行*/
	
	$(".listRow:visible",fenJieTableDiv).hide();	/*先隐藏全部，在显示个体*/
	$("[name$=':xh'][value=" + xh + "]",fenJieTableDiv).closest("tr").show(); 
	
	$("td.tdNo", $(".listRow:visible",fenJieTableDiv)).each(function(i) {
		$(this).text(i + 1);
	});
	
	$("[name$=':zj']").trigger("blur");
}








/**分解代码 end**/

/**日期加上多少天     type: m=2/d=1/y=3   number:几天 */
function getNewDataStr(curDateStr,number,type){
	if(!curDateStr) return "";
	curDateStr = curDateStr.replace(/\-/g, "/");
	var curDate =  new Date(curDateStr);
	if(type==1) curDate.setDate(curDate.getDate()+number); 
	if(type==2) curDate.setMonth(curDate.getMonth()+number);
	if(type==3) curDate.setFullYear(curDate.getFullYear()+number); 

	var y=curDate.getFullYear();
	var m=curDate.getMonth()+1;
	var d=curDate.getDate();
	 
	if(m<=9)m="0"+m; if(d<=9)d="0"+d;
	var cdate=y+"-"+m+"-"+d;
	return cdate; //开始时间
} 




