$(function() {
	$('input[name="sendemail"]').bind('click', function() {
		if ($(this).val() == '1') {
			// $('#emailSendTimeTr1,#emailSendTimeTr2').show();
			$($('input[name="emailSendTime"]')).each(function(i) {
				$(this).attr('disabled', false);
			});
		} else {
			// $('#emailSendTimeTr1,#emailSendTimeTr2').hide();
			$($('input[name="emailSendTime"]')).each(function(i) {
				$(this).attr('checked', false).attr('disabled', true);
			});
		}
	});
	$('input[name="sendsms"]').bind('click', function() {
		if ($(this).val() == '1') {
			// $('#smsSendTimeTr1,#smsSendTimeTr2').show();
			$($('input[name="smsSendTime"]')).each(function(i) {
				$(this).attr('disabled', false);
			});
		} else {
			// $('#smsSendTimeTr1,#smsSendTimeTr2').hide();
			$($('input[name="smsSendTime"]')).each(function(i) {
				$(this).attr('disabled', true).attr('checked', false);
			});
		}
	});
	function getData() {
		// 获取个性化设置
		$
				.ajaxSubmit(
						window.ctxPaths + '/pendTaskSetting/get.ajax',
						{},
						function(rtn) {
							if (rtn.success) {
								// $.dataInput($('.field'),rtn.data.staff)
								$('#staffMobile').text(rtn.data.staff.mobile);
								$('#staffEmail').text(rtn.data.staff.email);
								$('#setupForm').json2Form2(
										rtn.data.pendTaskSetting);
								// $('#setupForm').json2Form2(rtn.data.pendTaskSetting);
								var r1 = $('input[name="sendemail"]:checked')
										.val();
								if (r1 === '1') {
									$($('input[name="emailSendTime"]'))
											.each(
													function(i) {
														$(this).attr(
																'disabled',
																false);
													});
									var emailsendtime = rtn.data.pendTaskSetting.emailsendtime;
									if (emailsendtime) {
										$('input[name="emailSendTime"]')
												.values(emailsendtime);
									}
								} else {
									// $('#emailSendTimeTr1,#emailSendTimeTr2').hide();
									$($('input[name="emailSendTime"]'))
											.each(
													function(i) {
														$(this).attr('checked',
																false).attr(
																'disabled',
																true);
													});
								}
								var r1 = $('input[name="sendsms"]:checked')
										.val();
								if (r1 === '1') {
									$($('input[name="smsSendTime"]'))
											.each(
													function(i) {
														$(this).attr(
																'disabled',
																false);
													});
									var smssendtime = rtn.data.pendTaskSetting.smssendtime;
									if (smssendtime) {
										$('input[name="smsSendTime"]').values(
												smssendtime);
									}
								} else {
									// $('#smsSendTimeTr1,#smsSendTimeTr2').hide();
									$($('input[name="smsSendTime"]'))
											.each(
													function(i) {
														$(this).attr('checked',
																false).attr(
																'disabled',
																true);
													});
								}
							} else {
								Q_Alert_Fail(rtn.message);
							}
						});
	}
	$('#resetBtn').click(function() {
		resetForm($('#setupForm'));
		getData();
	});
	getData();
	// 提交按钮事件绑定
	$('#submitBtn').click(
			function() {
				var params = {
					sendsms : $('input[name="sendsms"]:checked').val(),
					sendemail : $('input[name="sendemail"]:checked').val(),
					emailsendtime : $('input[name="emailSendTime"]').values(),
					smssendtime : $('input[name="smsSendTime"]').values()
				};
				if (params['sendsms'] === '1' && !params['smssendtime']) {
					Q_Alert_Fail('请勾选短信待办提醒时间');
					return;
				}
				if (params['sendemail'] === '1' && !params['emailsendtime']) {
					Q_Alert_Fail('请勾选邮件待办提醒时间');
					return;
				}
				$.ajaxSubmit(window.ctxPaths + '/pendTaskSetting/update.ajax',
						params, function(rtn) {
							if (rtn.success) {
								Q_Alert('修改成功!');
							} else {
								Q_Alert_Fail(rtn.message);
							}
						});
			});
});