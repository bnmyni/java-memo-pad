//设置一变量，用于缓存页面中的原始值，用于重设操作
var originalValue = {};
$(function() {
	// 加载页面数据
	formLoad();
	var validator = $('#modifyInfoForm').validate(
			{
				rules : {
					'realName' : {
						required : true,
						maxlength : 100
					},
					'sex' : 'required',
					'telephone' : {
						maxlength : 30
					},
					'email' : {
						required : true,
						email : true,
						maxlength : 64
					},
					'mobile' : {
						required : true,
						mobile : true,
						maxlength : 11
					}
				},

				messages : {
					'sex' : '请选择性别'
				},
				submitHandler : function(form) {
					$.ajaxSubmit(window.ctxPaths + "/staff/updateStaff.ajax",
							$('#modifyInfoForm').serializeArray(), function(
									data) {
								if (data.success) {
									Q_Alert("修改个人信息成功", function() {
										formLoad()
									});
								} else {
									if (data.message) {
										Q_Alert_Fail(data.message);
									} else {
										Q_Alert_Fail('原因未知');
									}
								}
							});
					return false;
				}
			});

	// 表单数据加载
	function formLoad() {
		$
				.ajaxSubmit(window.ctxPaths + "/staff/findLoginStaff.ajax", {},
						function(rtn) {
							if (rtn.success) {
								var data = rtn.data;
								// 数据成功加载完毕，开始缓存页面中的原始值
								for ( var p in data) {
									originalValue[p] = data[p];
								}
								$.dataInput($('.field'), data);
								$('#modifyInfoForm').json2Form2(rtn.data);
								// 设置登录名：
								$('#loginName').text(data['loginName']);
								if (data['others']
										&& data['others']['departmentName']) {
									$('#staffDepartmentName').closest(
											'.form-group').show();
								}
							} else {
								if (rtn.data && rtn.data.message) {
									Q_Alert_Fail(rtn.data.message, "提示");
								} else {
									Q_Alert_Fail('原因未知');
								}
							}

						});
	}
	$('#resetBtn').bind('click', function() {
		resetForm($('#modifyInfoForm'), validator);
		$("#staffTelephone").val('');
		$('#modifyInfoForm').json2Form2(originalValue);
	});
});