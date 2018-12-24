var dataType = {};
dataType.email =function(gets, obj, curform, regxp) {
	if (!gets) {
		return false;
	}
	var email = /^(-|\.|\w)+\@((-|\w)+\.)+[A-Za-z]{2,}$/;
	if(email.test(gets)){
		if(gets.length > 100){
			return "邮件地址长度不能超过100个字符";
		}
	}else{
		return "邮箱地址格式不对";
	}
	return true;
}
dataType.number100 =function(gets, obj, curform, regxp) {
	if (!gets) {
		return false;
	}
	var reg = /^(\d{1,2}(\.\d{1,2})?|100|100.0|100.00)$/;
	if(gets.length > 0){
		if(!reg.test(gets)){
			return "分值范围:大于等于0小于等于100,最多两位小数";
		}
	}
	return true;
}
dataType.validateMaxLength =function(gets, obj, curform, regxp) {
	if (!gets) {
		return false;
	}
	var len = 0,_len = obj.attr('maxlength');
	if (gets){
		len = gets.replace(/[^\x00-\xff]/g, '..').length;
	}
	if (_len && len > _len){
			return '长度不能超过' + _len + '个字符或'+ _len/2 + '个汉字';
	}
	return true;
}
/*dataType.mobile = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var reg;
	$.ajax({
		url:BIZCTX_PATH + '/prmConfig!viewSimsConfig.action',
		async:false,
		data:{simsName:'MOBILE_REG_JS'},
		dataType:'json',
		cache:true,
		success:function(data){
			if(data.success){
				var config = data.data.config;
				//alert(config.configValue);
				reg = eval(config.configValue);
			}
		}
	})
	if(reg && reg.test(gets)){
		return true;
	}
	return false;
}*/


//申请帐号名是否存在
dataType.uLoginName = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findULoginReg.action',
		async:false,
		data:{'accountApply.accountName':gets},
		dataType:'json',
		success:function(data){
			if(data.success)
				{
					result=data.data.result;
				}
		}
	})
	if(result=="true")
		{
			return true;
		}
	return false;
}
//公司是否在流程中
dataType.uCompanyStatus = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findUCompanyStatus.action',
		async:false,
		data:{'accountApply.company.cnName':gets},
		dataType:'json',
		success:function(data){
			if(data.success)
				{
					result=data.data.result;
				}
		}
	})
	if(result=="true")
		{
			return true;
		}
	return false;
}
//申请帐号名是否存在
dataType.ruLoginName = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	var historyName=$("#accountNameHistory").val();
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findULoginReg.action',
		async:false,
		data:{'accountApply.accountName':gets,'type':'0','history':historyName},
		dataType:'json',
		success:function(data){
			if(data.success)
				{
					result=data.data.result;
				}
		}
	})
	if(result=="true")
		{
			return true;
		}
	return false;
}


//EMAIL是否存在
/*dataType.uEmail = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findULoginReg.action',
		async:false,
		data:{"accountApply.email":gets},
		dataType:'json',
		success:function(data){
			if(data.success)
			{
				result=data.data.result;
			}
		}
	})
	if(result=="true")
	{
		return true;
	}
	return false;
}*/
//EMAIL是否存在
dataType.ruEmail = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	var historyEmail=$("#emailHistory").val();
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findULoginReg.action',
		async:false,
		data:{"accountApply.email":gets,'type':'0','history':historyEmail},
		dataType:'json',
		success:function(data){
			if(data.success)
			{
				result=data.data.result;
			}
		}
	})
	if(result=="true")
	{
		return true;
	}
	return false;
}

//手机是否存在
/*dataType.uMobile = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findULoginReg.action',
		async:false,
		data:{"accountApply.mobile":gets},
		dataType:'json',
		success:function(data){
			if(data.success)
			{
				result=data.data.result;
			}
		}
	})
	if(result=="true")
	{
		return true;
	}
	return false;
}*/

//手机是否存在
dataType.ruMobile = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	var historyMobile=$("#mobileHistory").val();
	$.ajax({
		url:BIZCTX_PATH + '/authprm/findULoginReg.action',
		async:false,
		data:{"accountApply.mobile":gets,'type':'0','history':historyMobile},
		dataType:'json',
		success:function(data){
			if(data.success)
			{
				result=data.data.result;
			}
		}
	})
	if(result=="true")
	{
		return true;
	}
	return false;
}

//获取合作意向基地
dataType.getIntentionBase = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	var intentionBase ="";
	$.ajax({
		url:BIZCTX_PATH + '/preq/spApply!doGetIntentionBase.action',
		async:false,
		data:{},
		dataType:'json',
		success:function(data){
			if(data.success)
			{
				intentionBase=data.data.baseId;
			}
		}
	})
	if("null"==intentionBase || intentionBase==gets)
	{
		return true;
	}
	return false;
}

//验证码是否正确
dataType.checkCode = function(gets,obj,curform,regxp){
//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;	
	var result = false;
	$.ajax({
		url:ctxPaths + '/checkCode!clientCheck.ajax',
		async:false,
		data:{"checkCode":gets},
		dataType:'json',
		success:function(data){
			result = data.success;
		}
	})
	if(result===true)
	{
		return true;
	}
	return "验证码不正确！";
}

var tipType = {};

tipType = {
	rightSide :function(msg, o, cssctl) {
		//msg：提示信息;
		//o:{obj:,type:*,curform:*}, obj指向的是当前验证的表单元素（或表单对象），type指示提示的状态，值为1、2、3、4， 1：正在检测/提交数据，2：通过验证，3：验证失败，4：提示ignore状态, curform为当前form对象;
		//cssctl:内置的提示信息样式控制函数，该函数需传入两个参数：显示提示信息的对象 和 当前提示的状态（既形参o中的type）;
		
		if (!o.obj.is("form")) {//验证表单元素时o.obj为该表单元素，全部验证通过提交表单时o.obj为该表单对象;
			var objtip = o.obj
					.siblings(".tipinfo");
			if (objtip.length == 0) {
				var html = '<span class="tipinfo"> <span class="Validform_checktip"></span> <span class="dec" dec="dec"><s class="dec1">&#9670;</s><s class="dec2">&#9670;</s></span></span>';
				o.obj.after(html);
			}
			var realObjTip = o.obj.next().children('.Validform_checktip');
			cssctl(realObjTip, o.type);
			realObjTip.text(msg);
			objtip =  o.obj.next();
			if (o.type == 2 || o.type == 4) {//正常
				objtip.hide();
			} else {//其他情况  显示
				//if (objtip.is(":visible")) {
					//if (o.obj.attr('type') == 'hidden'){
						//o.obj.siblings('[className!="tipinfo"]').get(0).focus();
					//}
					//return;
				//}
				var tmp = o.obj;
				if (tmp.attr('type') == 'hidden'){
					if(tmp.attr('uploadfinishedignore') && (tmp.attr('uploadfinishedignore') == 'uploadfinishedignore')){
						tmp = tmp.parent('div').siblings('.uploading');
					}else{
						tmp = tmp.siblings('[className!="tipinfo"]');
					}
				}
				var left = tmp
						.position().left, top = tmp
						.position().top;
				var dec = objtip.find('span[dec="dec"]');
				if (o.obj.attr('tipType')){
					if (o.obj.attr('tipType').toLowerCase() == 'top'){
						top -= (tmp.height() + 4); 
						dec.attr('class','dec_top');
					}else if (o.obj.attr('tipType').toLowerCase() == 'bottom'){
						top += tmp.height(); 
						dec.attr('class','dec_bottom');
					}
				} else {
						left += tmp.width();
						dec.attr('class','dec');
						if(tmp.hasClass("uploading")){
							left+=22;
						}
				}
				objtip.css({
					left : left,
					top : top
					
				}).show();
				try{
					tmp.get(0).focus();
				}catch(err){}
			}
		} else {
			var objtip = o.obj
					.find("#msgdemo");
			cssctl(objtip, o.type);
			objtip.text(msg);
		}
	}
}

//validation原生email格式有问题，重新定义其正则表达式
//Validform.util.dataType = /^(-|\.|\w)+\@((-|\w)+\.)+[A-Za-z]{2,}$/;

//扩展或重设提示信息
var tipmsg={//默认提示文字;
		tit:"提示信息",
		w:{
			"*":"不能为空！",
			"*6-16":"请填写6到16位任意字符！",
			"n":"请填写数字！",
			"n6-16":"请填写6到20位数字！",
			"s":"不能输入特殊字符！",
			"s6-18":"请填写6到18位字符！",
			"p":"请填写邮政编码！",
			"m":"请填写手机号码！",
			"e":"邮箱地址格式不对！",
			"url":"请填写网址！",
			"ulogin":"帐号名已存在"
		},
		def:"请填写正确信息！",
		undef:"datatype未定义！",
		reck:"两次输入的内容不一致！",
		r:" ",
		c:"正在检测信息…",
		s:"请{填写|选择}{0|信息}！",
		v:"所填信息没有经过验证，请稍后…",
		p:"正在提交数据…"
	}

$.extend(true,$.Tipmsg, tipmsg);

												 