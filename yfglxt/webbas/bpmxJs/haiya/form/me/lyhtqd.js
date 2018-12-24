$().ready(function() {
	
	changeSftstk();
	loadChangeSffs();
	loadChangeJsgz();
	validateWyfDates();
	$("[datefmt]").live('change', function() {
		var me = $(this), val = me.val();
		if (!$.isEmpty(val)) {
			var arry = val.split(".");
			if(arry[0].indexOf("-") == -1) return; 
			var sTime = arry[0].replace(/\-/g, "/");
			var datefmt = me.attr("datefmt");
			var nowDate = new Date(sTime).Format(datefmt);
			me.val(nowDate);
		}
	});
	/*校验合同号是否存在*/
	$("input[name='m:me_lyhtqd:hth']").change(function(){
		var hth = $(this).val();
		if(!hth) return;
		if(hth.length != 9){
			$.ligerDialog.warn("合同号为：三位门店号 + 三位楼层号 + 三位本楼层流水","提示");	
			return;
		}
		var conf = {aliasName:'validateHTHisExist',hth:hth};
		var result = RunAliasScript(conf);  
		if(result.result === 0) return; 
		
		hth = hth.substring(0,6);
		if(result.isSuccess == 1) $.ligerDialog.error(result.msg,"哎呦出错了！");
		else  $.ligerDialog.error("该合同号已经存在！","提示");
		
		$(this).val(hth);
	});
	
	$("div[tablename='me_lyhtqd_bdxx']").find(".listRow").hide();	/*【月度租金分解】不能直接控制隐藏，否则权限控制不了,先隐藏全部，在显示个体*/
	$("div[tablename='me_lyhtqd_wyffj']").find(".listRow").hide();	/*【物业费分解】不能直接控制隐藏，否则权限控制不了,先隐藏全部，在显示个体*/
	validateZujinBiaozhunDates();//处理样式显示
	HandlKouLvRules(); //处理扣率组样式显示
});

/*显示租户信息*/
function showZh(){
	var zhid = $("[name='m:me_lyhtqd:zhID']").val();
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
/*显示品牌信息*/
function showPP(){
	var ppId = $("[name='m:me_lyhtqd:jyppID']").val();
	if(!ppId){
		$.ligerDialog.warn("请先选择品牌!","提示");
		return ;
	}
	var url=__ctx + "/platform/form/bpmDataTemplate/detailData_ppxxdj.ht?__pk__="+ppId;
	DialogUtil.open({
		height:600,
		width: 800,
		title : "查看品牌信息",
		url: url, 
		isResize: true
	});
}

/*校验子表填充数据是否重复**/ 
function checkDataBeforeInsert(data,tableName){
    if(tableName == 'me_lyhtqd_qtzkft' || tableName == 'me_lyhtqd_hykzkft'){
      var rows =   $(".listRow",$("[tableName='"+tableName+"']"));  //左右子表列
      for(var i =0,row;row=rows[i++];){
        var id = $("[name='s:"+tableName+":ppID']",$(row)).val();  //唯一值
          if(id ==data.PPBM) {                            //对话框，返回数据的那列
    	   $.ligerDialog.warn(data.PPNAME+"已经存在了！","提示信息");
            return false
         }
      }
    }
    
    if(tableName == 'me_lyhtqd_hyklx'){
        var rows = $(".listRow",$("[tableName='"+tableName+"']"));  //左右子表列
        for(var i =0,row;row=rows[i++];){
          var id = $("[name='s:"+tableName+":hyklxID']",$(row)).val();  //唯一值
            if(id == data.HYKTYPE) {                            //对话框，返回数据的那列
      	   $.ligerDialog.warn(data.HYKNAME+"已经存在了！","提示信息");
              return false
           }
        }
      }
    if(tableName == 'me_lyhtqd_pwxx'){
        var rows =   $(".listRow",$("[tableName='me_lyhtqd_pwxx']"));  //左右子表列
        
      //校验该铺位是否被租出去
        var zuLinStartDate  =$("[name='m:me_lyhtqd:htyxqq']").val();
		if(!zuLinStartDate ){  $.ligerDialog.warn("尚未输入租赁开始日期！",'请核查');  return false }
		var conf ={aliasName:'validatePuweiIsUsed',pwid:data.WLDPID,sDate:zuLinStartDate};
		var json = RunAliasScript(conf);
		if(json.isSuccess ==1){
			 $.ligerDialog.warn(json.msg);return false;
		}else if(json.result){
			$.ligerDialog.warn("该铺位到期日期为:"+json.result+"，请核查！");return false;
		}
        
        for(var i =0,row;row=rows[i++];){
          var id = $("[name='s:me_lyhtqd_pwxx:pwID']",$(row)).val();  //唯一值
            if(id ==data.WLDPID) {                              //对话框，返回数据的那列
      	   $.ligerDialog.warn(data.WLDPDM+"已经存在了！","提示信息");
              return false
           }
        }
      $("[name='m:me_lyhtqd:splx']").val(data.DYLX);
      }
    
     return true;
  }


/*租赁时间变化事件**/
function zlsjChangeEvent(){
	validateZujinBiaozhunDates();
	$("[name='s:me_lyhtqd_klzgz:sjd']").trigger("change");
	validateWyfDates();
	validateMysfxm();
}

//计算所有铺位,与面积
function calAllPuwei(obj){ 
	var puweiTrs = $(".listRow:visible",$("[tablename='me_lyhtqd_pwxx']")); 
	var shangPuNums = "";
	puweiTrs.each(function (i){
	  var pwId = $("[name$='pwh']",$(this)).val();
	  shangPuNums = shangPuNums + pwId;
	  if(i != puweiTrs.length-1)shangPuNums = shangPuNums+"-";
	});
	$("[name='m:me_lyhtqd:sp']").val(shangPuNums); 
}


/* 【表结算标准信息】 添加行事件*/
function me_lyhtqd_jsbzxxAddRowAfterEvent(row){
	var preRow = $(".listRow:visible",$("[tablename='me_lyhtqd_jsbzxx']")).not(row).last();
	
	var preNx = $("[name$=':xh']",preRow).val();
	if(!preNx)preNx = 0;
	$("[name$=':xh']",row).val(1+Number(preNx));
	$("[name$='sjd']",row).val(1+Number(preNx));
	validateZujinBiaozhunDates(row)
	changeJsgz(row);
};
// 校验租金标准 时间
function validateZujinBiaozhunDates(curRow){
	//租赁有效期起，止 
	var zuLinStartDate  =$("[name='m:me_lyhtqd:htyxqq']").val();
	var zuLinEnd  =$("[name='m:me_lyhtqd:htyxqz']");
	var zuLinEndDate =zuLinEnd.val();
	//length！=0 只读判断
	if((!zuLinEndDate || !zuLinStartDate)&& curRow){  
		$.ligerDialog.warn("尚未输入租赁起止日期！",'请核查'); 
		$(curRow).remove();
		return
	}
	
	var rows = $(".listRow:visible",$("[tablename='me_lyhtqd_jsbzxx']"));
	for(var i=0,row;row=rows[i++];){
		if(i>1){
			// 处理比较颜色
			fillColorByCompare(row,$(rows[i-2]),"bdxs"); // 保底销售
			fillColorByCompare(row,$(rows[i-2]),"bdkl"); // 保底扣率
			fillColorByCompare(row,$(rows[i-2]),"mbxs"); // 目标销售
			fillColorByCompare(row,$(rows[i-2]),"cmbkl"); // 超目标销售
			fillColorByCompare(row,$(rows[i-2]),"zbd"); // 总保底
			fillColorByCompare(row,$(rows[i-2]),"zmb"); // 总目标
			if(zuLinEnd.length ==0) continue; // 如果只读状态不再判断其他
			
			//开始日期等于上个结束日期加1
			var startDate = getNewDataStr($("[name$='jsrq']",$(rows[i-2])).val(),1,1);
			$("[name$='ksrq']",$(row)).val(startDate);
		}else {
			if(zuLinEnd.length ==0) continue;
			
			var differTotalStartDate = FormDate.compareDate($("[name$='ksrq']",$(row)).val(), zuLinStartDate, "day");
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
	$("[name='s:me_lyhtqd_klzgz:sjd']").trigger("change");
}
/**
 *  与目标数据大小进行比较，只读取 name+_td ，非只读去input $:+name
 * @param currentRow 当前行
 * @param tragetRow 目标行
 * @param name 名字
 */
function fillColorByCompare(currentRow,tragetRow,name){
	var curObj = $("[name$=':"+name+"']",currentRow);
	if(curObj.length ==0){ /*只读状态*/
		curObj = $("[name='"+name+"_td']",currentRow);
		var targetVal=FormUtil.commaback( $("[name='"+name+"_td']",tragetRow).text().trim() );
		var currentVal =FormUtil.commaback(curObj.text().trim());
	}else{
		var targetVal =FormUtil.commaback( $("[name$=':"+name+"']",tragetRow).val() ); //比较对象金额
		var currentVal =FormUtil.commaback(curObj.val());
		if(!targetVal || !currentVal) return; //对象为0不再计算 
	}
	if(currentVal>targetVal){
		curObj.css("color","red");
	}else if(currentVal<targetVal) {
		curObj.css("color","green");
	}else curObj.css("color","");
}


/*加载时，改变【结算标准信息】的【结算规则】*/
function loadChangeJsgz(){
	var $jsgz = $("[name$=':jsgz']:visible",$("[tablename='me_lyhtqd_jsbzxx']")); 
	$jsgz.each(function (i) {
		changeJsgz(this);
	});
}

/*改变【结算标准信息】的【结算规则】*/
function changeJsgz(obj){
	var $tr = $(obj).closest("tr");
	var jsgz = $("[name$=':jsgz']",$tr).val(); /*结算规则*/
	generateKlz();
	switch(jsgz){
		case "1":		/*实销实结，产生一条扣率组，保底销售(只读)、保底扣率(只读)、目标销售(只读)、超额目标扣率(只读)、总保底(只读)、总目标(只读)，分解为(保底销售)*/
			 $("[name$=':bdxs']",$tr).attr("readonly","readonly").val("");	
			 $("[name$=':bdkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':mbxs']",$tr).attr("readonly","readonly").val("");
			 
			 $("[name$=':cmbkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zbd']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zmb']",$tr).attr("readonly","readonly").val("");
		 break;
		case "2":		/*固定毛利额，产生一条扣率组，保底毛利(可编辑)、保底扣率(只读)、目标销售(只读)、超额目标扣率(只读)、总保底(只读)、总目标(只读)，分解为(保底毛利)*/
			 $("[name$=':bdxs']",$tr).removeAttr("readonly");	
			 $("[name$=':bdkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':mbxs']",$tr).attr("readonly","readonly").val("");
			 
			 $("[name$=':cmbkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zbd']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zmb']",$tr).attr("readonly","readonly").val("");
		  break;
		case "3":		/*有保底销售无目标销售，产生一条扣率组，保底销售(可编辑)、保底扣率(可编辑)、目标销售(只读)、超额目标扣率(只读)、总保底(只读)、总目标(只读)，分解为(保底销售)*/
			 $("[name$=':bdxs']",$tr).removeAttr("readonly");	
			 $("[name$=':bdkl']",$tr).removeAttr("readonly");	
			 $("[name$=':mbxs']",$tr).attr("readonly","readonly").val("");
			 
			 $("[name$=':cmbkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zbd']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zmb']",$tr).attr("readonly","readonly").val("");
		  break;
		case "4":		/*有保底毛利无目标毛利，产生一条扣率组，保底毛利(可编辑)、保底扣率(只读)、目标销售(只读)、超额目标扣率(只读)、总保底(只读)、总目标(只读)，分解为(保底毛利)*/
			 $("[name$=':bdxs']",$tr).removeAttr("readonly");	
			 $("[name$=':bdkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':mbxs']",$tr).attr("readonly","readonly").val("");
			 
			 $("[name$=':cmbkl']",$tr).attr("readonly","readonly").val("");	
			 $("[name$=':zbd']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zmb']",$tr).attr("readonly","readonly").val("");
			break;
		case "5":		/*有保底销售有目标销售，产生一条扣率组，保底毛利(可编辑)、保底扣率(可编辑)、目标销售(可编辑)、超额目标扣率(可编辑)、总保底(只读)、总目标(只读)，分解为(保底销售)*/
			 $("[name$=':bdxs']",$tr).removeAttr("readonly");		
			 $("[name$=':bdkl']",$tr).removeAttr("readonly");
			 $("[name$=':mbxs']",$tr).removeAttr("readonly");
			 
			 $("[name$=':cmbkl']",$tr).removeAttr("readonly");	
			 $("[name$=':zbd']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zmb']",$tr).attr("readonly","readonly").val("");	
			break;
		case "6":		/*有保底毛利有目标毛利，产生一条扣率组，保底毛利(可编辑)、保底扣率(只读)、目标销售(可编辑)、超额目标扣率(可编辑)、总保底(只读)、总目标(只读)，分解为(保底毛利)*/
			 $("[name$=':bdxs']",$tr).removeAttr("readonly");	
			 $("[name$=':bdkl']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':mbxs']",$tr).removeAttr("readonly");
			 
			 $("[name$=':cmbkl']",$tr).removeAttr("readonly");
			 $("[name$=':zbd']",$tr).attr("readonly","readonly").val("");
			 $("[name$=':zmb']",$tr).attr("readonly","readonly").val("");
			break;
		default:
	}
}

function calBaodiMubiao(obj,targetInput){
	var curRow = $(obj).closest(".listRow"); 
	var baodi = FormUtil.commaback($(obj).val());
	if(!baodi) return;
	var mianJi = $("[name='m:me_lyhtqd:jzmj']").val();
	if(!mianJi){
		$.ligerDialog.warn("建筑面积尚未计算生成!",'请核查');
		return ;
	}
	var startDate = $("[name$=':ksrq']",curRow).val();
	var endDate = $("[name$=':jsrq']",curRow).val();
	var ZongJine = calMountZujin(startDate,endDate,baodi);
	ZongJine =FormMath.tofixed(ZongJine,2);
	$("[name$='"+targetInput+"']",curRow).val(ZongJine).trigger("change");
	validateZujinBiaozhunDates();
}


/*产生一条扣率组*/
function generateKlz(){
	if($(".listRow:visible",$("[tablename$='klz']")).length ==0){
		FormUtil.addRow($('div[tablename$="klz"]'));  
		var appendRow = $(".listRow:visible",$("[tablename$='klz']"))[0];
		$("[name$='klzbh']",appendRow).val(1);
		$("[name$='jckl']",appendRow).val(0);
		$("[name$='ms']",appendRow).val("扣组率1");
	}
}

//单价计算总租金
function calZongZuJin(obj){
	var curRow = $(obj).closest(".listRow");
	var danJia = FormUtil.commaback($("[name$='dj']",curRow).val()); // 单价
	if(! danJia>0) return; 
	
	var mianJi = $("[name='m:me_lyhtqd:jzmj']").val();
	if(!mianJi){
		$.ligerDialog.warn(" 合同建筑面积不能为空！",'请核查');
		return ;
	}
	var baoDiType =$("[name$='bdxx']",curRow).val();
	var zuJin = mianJi * danJia;
	$("[name$=':zj']",curRow).val(zuJin); $("[name$=':zj']",curRow).trigger("change");
	
	var startDate = $("[name$='ksrq']",curRow).val();
	var endDate = $("[name$='jsrq']",curRow).val();
	if(baoDiType =="0"){ //按月
		var zongZujin = calMountZujin(startDate,endDate,zuJin);
	}else{
		var days = FormDate.dateVal(startDate, endDate, "day");
		var zongZujin = zuJin*days;
	}
	zongZujin =FormMath.tofixed(zongZujin,2);
	$("[name$='zzj']",curRow).val(zongZujin); $("[name$='zzj']",curRow).trigger("change");
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
	//时间段
	var rows = $(".listRow:visible",$("[tablename='me_zlzjbzxx']"));
	var kouLvs = $(".listRow:visible",$("[tablename='me_zlklz']"));
	for(var i=0,row;row=rows[i++];){
		for(var j=0,kouLv;kouLv=kouLvs[j++];){

		}
	}
}
/*处理扣率组大小比较*/
function HandlKouLvRules(){
	var kouLvRules = $(".listRow:visible",$("[tablename='me_lyhtqd_klzgz']"));
	var combineMessage = [];
	for(var i=0,rule;rule=kouLvRules[i++];){
		var sjdInput=$("[name$=':sjd']",rule);
		if(sjdInput.length==0){ /*只读状态*/
			var sjd = $("[name='sjd_td']",rule).text().trim(); /*时间段*/
			var klz = $("[name='sjd_td']",rule).text().trim();/*扣率组*/
		}else{
			var sjd = sjdInput.val(); /*时间段*/
			var klz = $("[name$=':klz']",rule).val();/*扣率组*/
		}
		if(!sjd || !klz) return;
		 /*第一次设置开始金额为当前金额为第几行*/	
		var len=-1;
		for(var j=0,r;r=combineMessage[j++];){
			if(r.split("-")[0] == sjd+","+klz){
				len =Number(r.split("-")[1]);
			}
		}
		if(len!=-1){
			fillColorByCompare(rule,kouLvRules[len],"kl");
		}
		combineMessage.push(sjd+","+klz+"-"+(i-1));
	}
}

/*处理扣率时间段*/
function handelKoulvTime(obj){
	var curRow = $(obj).closest(".listRow");
	var shijianNo = $("[name$='sjd']",curRow).val();
	if(!shijianNo) return ;
	/*取的时间段*/
	var shiJianDuanNo =$("[name='s:me_lyhtqd_jsbzxx:sjd'][value="+shijianNo+"]");
	if(shiJianDuanNo.length ==0) {
		$.ligerDialog.warn("该时间段不存在！ “"+shijianNo+"”",'请核查');
		$("[name$='sjd']",curRow).val("");
		return ;
	}
	var shiJianDuanRow = shiJianDuanNo.closest(".listRow");
	var StartDate = $("[name$='ksrq']",shiJianDuanRow).val();
	var endDate = $("[name$='jsrq']",shiJianDuanRow).val();
	if(!StartDate || !endDate) {
		$.ligerDialog.warn("该时间段信息不完善！ “"+shijianNo+"”",'请核查');
		$("[name$='sjd']",curRow).val("");
		return ;
	}
	
	$("[name$='ksrq']",curRow).val(StartDate);
	$("[name$='jsrq']",curRow).val(endDate);
}

/*处理扣率组*/
function handelKoulvGroup(obj){
	var curRow = $(obj).closest(".listRow");
	var koulvNo = $(obj).val();
	if(!koulvNo) return ;
	/*取扣率组*/
	var KouLv =$("[name='s:me_lyhtqd_klz:klzbh'][value="+koulvNo+"]");
	if(KouLv.length ==0) {
		$.ligerDialog.warn("该扣率组不存在！ “"+koulvNo+"”",'请核查');
		$(obj).val("");
		return ;
	}
}
/*【物业费条款添加事件】me_lyhtqd_wyftk*/
function me_lyhtqd_wyftkAddRowAfterEvent(curRow){
	var preRow = $(curRow).prev();
	/*获取原结算标准最后一个序号*/
	if(preRow.length==0 || preRow.attr("style") == 'display: none;'){
		preRow = $(".listRow:visible",$("[tablename='me_lyhtqd_ywyftk']")).last();
	}
	var preNx = $("[name$='nx']",preRow).val();
	if(!preNx) preNx=0;
	$("[name$='nx']",curRow).val(1+Number(preNx));
	$("[name$='xh']",curRow).val(1+Number(preNx));
	if(validateWyfDates(curRow) == false)$(curRow).remove();
}

/*校验物业费时间**/
function validateWyfDates(curRow){
	//租赁有效期起
	var zuLinStart  =$("[name='m:me_lyhtqd:htyxqq']");
	var zuLinStartDate  =zuLinStart.val();
	var zuLinEndDate  =$("[name='m:me_lyhtqd:htyxqz']").val();
	//非只读状态
	if((!zuLinStartDate || !zuLinStartDate) && curRow){ 
		$.ligerDialog.warn("尚未输入租赁起止日期！",'请核查'); 
		return false;
	}
	//循环计算开始日期
	var rows = $(".listRow:visible",$("[tablename='me_lyhtqd_wyftk']"));
	for(var i=0,row;row=rows[i++];){
		if(i>1){
			fillColorByCompare(row,$(rows[i-2]),"xs");
			fillColorByCompare(row,$(rows[i-2]),"dj");
			fillColorByCompare(row,$(rows[i-2]),"je");
			fillColorByCompare(row,$(rows[i-2]),"zje");
		}
		if(zuLinStart.length==0) continue;
		
		var shiJianDuan = $("[name$=':sjd']",$(row)).val(i); //时间段
		if(i>1){
			//开始日期等于上个结束日期加1
			var startDate = getNewDataStr($("[name$=':jsrq']",$(rows[i-2])).val(),1,1);
			$("[name$=':ksrq']",$(row)).val(startDate);
		}else{
			$("[name$=':ksrq']",$(row)).val(zuLinStartDate);
		}
		//校验结束日期
		var jsrq =$("[name$=':jsrq']",row).val();
		if(!jsrq) continue;
		var differTotalEndDate = FormDate.compareDate(jsrq, zuLinEndDate);
		if(differTotalEndDate<0){
			$.ligerDialog.warn("结束日期不能晚于租赁有效期止！",'请核查！');
			$("[name$=':jsrq']",curRow).val("")
		}
	}
}


/*总物业费**/
function calZongWuYeFei(obj){
	var curRow = $(obj).closest(".listRow"); 
	//校验是否超过总日期
	var zuLinEndDate =$("[name='m:me_lyhtqd:htyxqz']").val();
	var differTotalEndDate = FormDate.compareDate($("[name$=':jsrq']",curRow).val(), zuLinEndDate);
	if(differTotalEndDate<0)  {
		$.ligerDialog.warn("当前结束日期不能晚于租赁有效期止！",'请核查');
		$("[name$='jsrq']",curRow).val("");
		return ;
	}
	
	var danJia = FormUtil.commaback($("[name$='dj']",curRow).val()); // 单价
	if(! danJia>0) return; 
	
	var mianJi = $("[name='m:me_lyhtqd:jzmj']").val();
	if(!mianJi){
		$.ligerDialog.warn("建筑面积尚未计算生成!",'请核查');
		return ;
	}

	var baoDiType =$("[name$='glfxx']",curRow).val();
	var xs = $("[name$='xs']",curRow).val();
	var jine = FormMath.tofixed(mianJi * danJia * xs,2);
	$("[name$='je']",curRow).val(jine); $("[name$='je']",curRow).trigger("change");
	
	var startDate = $("[name$='ksrq']",curRow).val();
	var endDate = $("[name$='jsrq']",curRow).val();
	
	if(baoDiType =="0"){ //按月
		var zongJine = calMountZujin(startDate,endDate,jine);
	}else{
		var days = FormDate.dateVal(startDate, endDate, "day");
		var zongJine = jine*days;
	}
	zongJine = FormMath.tofixed(zongJine,2);
	$("[name$='zje']",curRow).val(zongJine);$("[name$='zje']",curRow).trigger("change");
 }

/*加载时，改变【每月收费项目】的【收费方式】*/
function loadChangeSffs(){
	var sffs = $("[name$='sffs']:visible",$("[tablename='me_zlmysfxm']"));
	sffs.each(function (i) {
		changeSffs(this);
	});
}
/*改变【是否特殊条款】*/
function changeSftstk(){
	var sftstk = $("[name='m:me_lyhtqd:sftstk']").val();
	if(typeof sftstk == "undefined"){
		sftstk = $.trim($("#sftstk").text());
	}
	if(sftstk == "1" || sftstk == "是"){
		$(".tstk_tr").show();
	}else{
		$(".tstk_tr").hide();
	}
}

/*改变【申报类型】*/
function changeSblx(){
	var sblx = $("[name='m:me_lyhtqd:sblx']").val();
	if(typeof sblx == "undefined"){
		sblx = $.trim($("#sblx").text());
	}
	if(sblx == "2" || sblx == "重新申报"){
		$("#sbyy_tr").show();
	}else{
		$("#sbyy_tr").hide();
	}
}

/*改变【每月收费项目】的【收费方式】*/
function changeSffs(obj){
	var $tr = $(obj).closest("tr");
	var sffs = $("[name$='sffs']",$tr).val(); /*收费方式*/
	$("[name$='dj']",$tr).off();
	 $("[name='shouFeiGuiZe']",$tr).show();
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
			 $("[name='shouFeiGuiZe']",$tr).hide().parent().css("width","170px");
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
				 var mianJi = $("[name='m:me_zltksbb:jzmj']").val();
				 if(!mianJi) alert("尚未生成面积");
				 $("[name$='sfje']",curRow).val(FormMath.tofixed(dj*mianJi,2));
			 });
		  break;
		default:
	}
}

/*校验每月收费项目*/
function validateMysfxm (){
	var zuLinStartDate  =$("[name='m:me_lyhtqd:htyxqq']").val();
	var zuLinEndDate  =$("[name='m:me_lyhtqd:htyxqz']").val();
	
	var sfxm = $(".listRow:visible",$("[tablename='me_lyhtqd_mysfxm']"));
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
function me_lyhtqd_jsbzxxDelRowBeforeEvent(row){
	var xh = $("[name$=':xh']",row).val();
	$("[name='s:me_lyhtqd_bdxx:xh'][value=" + xh + "]").closest("tr").remove();/*清除旧的记录*/
}
function me_lyhtqd_wyftkDelRowBeforeEvent(row){
	var xh = $("[name$=':xh']",row).val();
	$("[name='s:me_lyhtqd_wyffj:xh'][value=" + xh + "]").closest("tr").remove();/*清除旧的记录*/
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
function decomposeAllWyf(){
	decomposeAllJs('me_lyhtqd_wyftk','me_lyhtqd_wyffj');
}
function decomposeSingleWyf(){
	decomposeSingleJs('me_lyhtqd_wyftk','me_lyhtqd_wyffj');
}
function decomposeAllBaodi(){
	decomposeAllJs('me_lyhtqd_jsbzxx','me_lyhtqd_bdxx');
}
function decomposeSingleBaodi(){
	decomposeSingleJs('me_lyhtqd_jsbzxx','me_lyhtqd_bdxx');
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
	var yzj,mbxs,type;
	//物业费
	if(fenJieTable == 'me_lyhtqd_wyffj'){
		yzj =FormUtil.commaback($("[name$='je']",selectRow).val());
		type =$("[name$='glfxx']",selectRow).val();
	//标准租金
	}else{
		yzj=FormUtil.commaback($("[name$='bdxs']",selectRow).val()); 
		 mbxs =FormUtil.commaback($("[name$='mbxs']",selectRow).val()); 
	}
	var jsonData = decompose(sjd,ksrq,jsrq,yzj,"",mbxs,type);
	/*填充数据*/
	for (var i = 0, c; c = jsonData[i++];) {
		FormUtil.addRow(fenJieTableDiv);
		var rowcount=$("input[name$=':ny']",fenJieTableDiv).length;
		$($("input[name$=':xh']",fenJieTableDiv).get(rowcount-1)).val(xh);		
		$($("input[name$=':ny']",fenJieTableDiv).get(rowcount-1)).val(c.ZQY);				
		$($("input[name$=':ksrq']",fenJieTableDiv).get(rowcount-1)).val(c.KSRQ);			
		$($("input[name$=':jsrq']",fenJieTableDiv).get(rowcount-1)).val(c.JSRQ);			
		if(fenJieTable == 'me_lyhtqd_wyffj'){
			$($("input[name$=':je']",fenJieTableDiv).get(rowcount-1)).val(c.YZJ);	
		}else{
			$($("input[name$=':bdxsml']",fenJieTableDiv).get(rowcount-1)).val(c.YZJ);	
			$($("input[name$=':mbxsml']",fenJieTableDiv).get(rowcount-1)).val(c.MBXS);
		}
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