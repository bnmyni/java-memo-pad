/*【协议呈批】**/
$(function(){
	loadChangeSffs();
	$("[name='m:op_xycpb:xylx']").trigger("change");
});

/*切换协议类型*/
function changeXylx(obj){
	var xylx = $(obj).val();
	
	$("[xylxType*='xylx-']").hide();
	$("[xylxType='xylx-"+xylx+"']").show();
}

/*租赁时间变化事件**/
function zlsjChangeEvent(){
	validateZujinBiaozhunDates();
	$("[name='s:op_xycpb_klzgz:sjd']").trigger("change");
	validateMysfxm();
}

/*显示租户信息*/
function showZh(){
	var zhid = $("[name='m:op_xycpb:zhID']").val();
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
    if(tableName == 'op_xycpb_lscd'){
        var rows = $(".listRow",$("[tableName='"+tableName+"']"));  //左右子表列
        for(var i =0,row;row=rows[i++];){
          var id = $("[name='s:"+tableName+":cddm']",$(row)).val();  //唯一值
            if(id == data.WLDPDM) {                          //对话框，返回数据的那列
      	   $.ligerDialog.warn(data.WLDPDM+"已经存在了！","提示信息");
              return false
           }
        }
        //校验该铺位是否被租出去
        var zuLinStartDate  =$("[name='m:op_xycpb:zlyxqq']").val();
		if(!zuLinStartDate ){  $.ligerDialog.warn("尚未输入开始日期！",'请核查');  return false }
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
function op_xycpb_zjbzxxAddRowAfterEvent(row){
	var preRow = $(row).prev();
	/*获取原结算标准最后一个序号*/
	if(preRow.length==0 ||preRow.attr("style") == 'display: none;'){
		preRow = $(".listRow:visible",$("[tablename='op_xycpb_yzjbzxx']")).last();
	}
	var preNx = $("[name$=':xh']",preRow).val();
	if(!preNx)preNx = 0;
	$("[name$=':xh']",row).val(1+Number(preNx));
	validateZujinBiaozhunDates(row)
};
// 校验租金标准 时间
function validateZujinBiaozhunDates(curRow){
	//租赁有效期起，止
	var zuLinStartDate  =$("[name='m:op_xycpb:zlyxqq']").val();
	var zuLinEndDate  =$("[name='m:op_xycpb:zlyxqz']").val();
	if(!zuLinStartDate || !zuLinStartDate){  
		$.ligerDialog.warn("尚未输入租赁起止日期！",'请核查'); 
		$(curRow).remove();
		return
	}
	
	var rows = $(".listRow:visible",$("[tablename='op_xycpb_zjbzxx']"));
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
		var differTotalEndDate = FormDate.compareDate($("[name$='jsrq']",$(row)).val(), zuLinEndDate);
		if(differTotalEndDate<0)  {
			$.ligerDialog.warn("当前结束日期不能晚于 租赁有效期止！",'请核查');
			$("[name$='jsrq']",$(row)).val("")
			return ;
		}
	}
	calZongZuJin();
	$("[name='s:op_xycpb_klzgz:sjd']").trigger("change");
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
}
/**单价计算总租金
 * trigger 'dj'/'zj' 触发者 obj = this
 * */
function calZongZuJin(obj){
	var trigger = $(obj).attr("name").split(":")[2];
	var curRow = $(obj).closest(".listRow");
	var val = FormUtil.commaback($(obj).val()); 
	if(! val>0) return; 
	
	var mianJi = $("[name='m:op_xycpb:jzmj']").val();
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


/*加载时，改变【每月收费项目】的【收费方式】*/
function loadChangeSffs(){
	var sffs = $("[name$='sffs']:visible",$("[tablename='op_xycpb']"));
	sffs.each(function (i) {
		changeSffs(this);
	});
}

/*改变【每月收费项目】的【收费方式】*/
function changeSffs(obj){
	var $tr = $(obj).closest("tr");
	var sffs = $("[name$='sffs']",$tr).val(); /*收费方式*/
	$("[name$='dj']",$tr).off();
	switch(sffs){
		case "0":		/*固定金额类型 ： 只能录入收费金额*/
		case "4":		
			 $("[name$='dj']",$tr).attr("readonly","readonly").val("");
			 $("[name$='sfje']",$tr).removeAttr("readonly");
			 $("[name$='kl']",$tr).attr("readonly","readonly").val("");
		  break;
		case "1":		/*比率类型：只能录入比率*/
		case "2":
		case "5":
			 $("[name$=':sfgz']",$tr).val("");
			 $("[name$=':sfgzID']",$tr).val("");
			$("[name$='dj']",$tr).attr("readonly","readonly").val("");
			$("[name$='sfje']",$tr).attr("readonly","readonly").val("");
			$("[name$='kl']",$tr).removeAttr("readonly");
		  break;
		case "3":		/*每平方米单价类型：只能录入单价*/
			 $("[name$='dj']",$tr).removeAttr("readonly");
			$("[name$='sfje']",$tr).attr("readonly","readonly").val("");
			$("[name$='kl']",$tr).attr("readonly","readonly").val("");
			//计算价格
			 $("[name$='dj']",$tr).on("blur",function(){
				 var dj =  FormUtil.commaback($(this).val()); 
				 var curRow = $(this).closest(".listRow"); 
				 var mianJi = $("[name='m:op_xycpb:jzmj']").val();
				 if(!mianJi) alert("尚未生成面积");
				 $("[name$='sfje']",curRow).val(dj*mianJi);
			 });
		  break;
		default:
	}
}

/*校验每月收费项目*/
function validateMysfxm (){
	var zuLinStartDate  =$("[name='m:op_xycpb:zlyxqq']").val();
	var zuLinEndDate  =$("[name='m:op_xycpb:zlyxqz']").val();
	
	var sfxm = $(".listRow:visible",$("[tablename='op_xycpb_mysfxm']"));
	var sfxmArray =[];
	 
	for(var i=0,row;row=sfxm[i++];){
		var ksrq = $("[name$='ksrq']",$(row)).val();
		var jsrq = $("[name$='jsrq']",$(row)).val();
		if(!ksrq) continue;
		var differTotalStartDate = FormDate.compareDate(ksrq, zuLinStartDate);
		if(differTotalStartDate>0){
			$.ligerDialog.warn("每月收费项目开始日期不能早于 租赁开始日期！",'请核查');
			$("[name$='ksrq']",$(row)).val("")
		}
		
		var differTotalEndDate = FormDate.compareDate(jsrq, zuLinEndDate);
		if(differTotalEndDate<0)  {
			$.ligerDialog.warn("每月收费项目结束日期不能晚于 租赁有效期止！",'请核查');
			$("[name$='jsrq']",$(row)).val("")
		}
		var curSfxmID =$("[name$='sfxmID']",$(row)).val();
		if(curSfxmID){
			for(var j=0,prevSfxm;prevSfxm=sfxmArray[j++];){
				var thisSfxmId = prevSfxm.split("$")[0];
				/*当前收费项目第二次出现*/
				if(thisSfxmId == curSfxmID){
					var differToPrevDate = FormDate.compareDate(ksrq,prevSfxm.split("$")[1]);
					if(differToPrevDate>=0){
						$.ligerDialog.warn("同一收费项目日期不得重复！",'请核查');
						$("[name$='ksrq']",$(row)).val("");
					}
				}
			  }
			sfxmArray.push(curSfxmID+"$"+jsrq);
			}
		}
}

/*结算标准信息   删除行事件*/
function op_xycpb_zjbzxxDelRowBeforeEvent(row){
	var xh = $("[name$=':xh']",row).val();
	$("[name='s:op_xycpb_bdxx:xh'][value=" + xh + "]").closest("tr").remove();/*清除旧的记录*/
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
	decomposeAllJs('op_xycpb_zjbzxx','op_xycpb_ydzjfj');
}
function decomposeSingleYzj(){
	decomposeSingleJs('op_xycpb_zjbzxx','op_xycpb_ydzjfj');
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
	var type =$("[name$='bdxx']",selectRow).val();
	var zj =FormUtil.commaback($("[name$='zj']",selectRow).val()); 
	var sfgz =$("[name='m:op_xycpb:zjsfgzID']").val();

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