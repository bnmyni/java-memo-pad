if (self != top) top.location.reload();
var defaultInpuTip = {
	loginName: '用户名',
	checkCode: '验证码',
	error_loginName: '请输入账户名!',
	error_password: '请输入密码',
	error_checkCode: '请输入验证码!'
};
var valueEqTip = function(jq, tip) {
	var val = jq.val();
	if (val && tip == val) {
		return true;
	}
	return false;
};
var valueIsEmpty = function(jq) {
	var val = jq.val();
	if (val == null || val == '') {
		return true;
	}
	return false;
};

var errorTip = function(errorMsg) {
	$('#errorDisplayArea').text(errorMsg);
	if ('' == errorMsg) {
		$('#errorDisplayArea').hide();
	} else {
		$('#errorDisplayArea').show();
	}
};

var validFiledSets = {
	_isEmpty: function(field) {
		if (field.value == null || '' == field.value || field.value == defaultInpuTip[field.name]) {
			errorTip(defaultInpuTip['error_' + field.name]);
			$('#' + field.name).focus();
			return true;
		}
		return false;
	},
	isAuto: function(field) {
		return true;
	},
	loginName: function(field) {
		return !this._isEmpty(field);
	},
	password: function(field) {
		if (this._isEmpty(field)) {
			$('#artiPwd').focus();
		} else {
			return true;
		}
	},
	checkCode: function(field) {
		return !this._isEmpty(field);
	}
};

var refreshCheckCode = function(jqO) {
	jqO.attr('src', ctxPaths + '/code/getCode.ajax?timeStamp=' + new Date().getTime());
};

var enterSubmit = function(selector, submitHandler) {
	var targets = $(selector);
	if (targets.length == 0) {
		return;
	}
	if (!(submitHandler instanceof Function))
		return;
	targets.bind('keydown', function(e) {
		if (e.which == 13) {
			submitHandler.apply();
			//阻止默认行为和冒泡
			return false;
		}

	});
};
$(function() {
	if(!isCheckcodeOn){
		$('#checkCode').parent('span').parent('label').remove();
		$('#checkCodeImg').remove();
	}else{
		$('#checkCode').parent('span').parent('label').show();
		$('#checkCodeImg').show();
	}
	var isFocus = $("#loginName").is(":focus");
	if (isFocus) {
		$("#loginName").val('');
	}
	//用户名绑定事件
	$("#loginName").bind({
		focus: function() {
			if (valueEqTip($(this), defaultInpuTip.loginName)) { //与提示相同，清空
				$(this).val('');
			}
		},
		blur: function() {
			if (valueIsEmpty($(this))) {
				$(this).val(defaultInpuTip.loginName);
			}
			//校验逻辑 todo
		}
	});
	//密码绑定事件
	$("#artiPwd").bind('focus', function() {
		$('#password').css('display', '');
		$('#password').focus();
		$(this).css('display', 'none');
	});
	$("#password").bind('blur', function() {
		if (valueIsEmpty($(this))) { //无值，显示假的密码输入框,真输入框隐藏
			$("#artiPwd").css('display', '');
			$(this).css('display', 'none');
		}
		//校验逻辑 todo
	});
	if(isCheckcodeOn){
		//验证码绑定事件
		$("#checkCode").bind({
			focus: function() {
				if (valueEqTip($(this), defaultInpuTip.checkCode)) { //与提示相同，清空
					$(this).val('');
				}
			},
			blur: function() {
				if (valueIsEmpty($(this))) {
					$(this).val(defaultInpuTip.checkCode);
				}
			}
		});
		//验证码链接
		$('#checkCodeImg').bind('click', function() {
			refreshCheckCode($('#checkCodeImg'));
		});
	}
	//登录动作
	var loginAction = function() {
		//校验与序列化数据
		var params = {};
		var fields = $('#loginForm').serializeArray();
		var checkResult = true;
		jQuery
			.each(
				fields,
				function(i, field) {
					checkResult = validFiledSets[field.name](field);
					if (checkResult) {
						if (field.name == 'password') {
							var rsa = new RSAKey();
							rsa
								.setPublic(modulus,
									exponent);
							var passwordDe = rsa
								.encrypt(field.value);
							params[field.name] = passwordDe;
						} else {
							params[field.name] = field.value;
						}
					} else {
						return false;
					}
				});
		if (!checkResult) {
			return;
		}
		//清除错误
		errorTip('');
		//登录按钮灰显
		$('#loginButton').attr("disabled", true);
		//提交
		$
			.ajax({
				url: ctxPaths + '/portal/login.ajax',
				type: 'POST',
				timeout: 30000,
				dataType: 'json',
				data: params,
				success: function(data) {
					if (data.success) {
						//跳转
						var requestUrl = getP('requestUrl');
						window.location.href = ctxPaths + '/pages/main.shtml' + ((requestUrl && requestUrl != 'null') ? '?requestUrl=' + requestUrl : ''); 
					} else {
						if (data.message) {
							errorTip(data.message);
						} else {
							errorTip('登录失败，原因未知');
						}
						if(isCheckcodeOn){
							$('#checkCode').val('');
							refreshCheckCode($('#checkCodeImg'));
						}
						$('#loginButton').removeAttr("disabled");
					}
				},
				error: function(jqXHR, errorMsg) {
					errorTip('登录失败，原因未知');
					if(isCheckcodeOn){
						refreshCheckCode($('#checkCodeImg'));
					}
					$('#loginButton').removeAttr("disabled");
				}
			});
	};
	$('#loginButton').bind('click', loginAction);
	$("#password").keydown(function(e) {
		var curKey = e.which;
		if (curKey == 13) {
			$('#loginButton').trigger('click');
			return false;
		}
	});
	if(isCheckcodeOn){
		$("#checkCode").keydown(function(e) {
			var curKey = e.which;
			if (curKey == 13) {
				$('#loginButton').trigger('click');
				return false;
			}
		});
	}
	//回车提交动作
	enterSubmit('.enter-submit', loginAction);
});