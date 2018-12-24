$(function() {
	var validator = $('#modifyPwdForm')
			.validate(
					{
						rules : {
							oldPassword : {
								required : true,
								minlength : 6,
								maxlength : 20
							},
							newPassword : {
								required : true,
								password : true,
								minlength : 6,
								maxlength : 20
							},
							newPasswordConfirm : {
								required : true,
								minlength : 6,
								maxlength : 20,
								equalTo : "#newPassword"
							}

						},
						messages : {
							newPasswordConfirm : {
								equalTo : "您两次输入的密码不一致"
							}
						},
						submitHandler : function(form) {
							$
									.ajaxSubmit(
											window.ctxPaths
													+ "/staff/changePwd.ajax",
											$(form).serializeJson(),
											function(data) {
												if (data.success) {
													Q_Alert(
															"修改个人密码成功",
															function() {
																window.location.href = window.ctxPaths
																		+ "/pages/auth/individual/modifyInfo.shtml";
															});
												} else {
													if (data && data.message) {
														Q_Alert_Fail(data.message);
													} else {
														Q_Alert_Fail('原因未知',
																resetModifyPwdForm);
													}
												}
											});
							return false;
						}
					});

	$('#resetBtn').bind('click', function() {
		resetForm($('#modifyPwdForm'), validator);
	});
});