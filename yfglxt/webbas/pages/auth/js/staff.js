var basePath = ctxPaths;
var list_staff_Url = basePath + '/staff/listStaff.ajax';
var create_staff_url = basePath + '/staff/createStaff.ajax';
var delete_staff_url = basePath + '/staff/deleteStaff.ajax';
var update_staff_url = basePath + '/staff/updateStaff.ajax';
var check_login_name_url = basePath + '/staff/checkStaffLoginName.ajax';
var locked_staff_url = basePath + '/staff/lockStaff.ajax';
var staff_info_url = basePath + '/staff/findStaff.ajax';
var staff_role_url = basePath + '/staff/listStaffRoles.ajax';
var update_staff_role_url = basePath + '/staff/updateStaffRole.ajax';
var reset_pwd_url = basePath + '/staff/resetPwd.ajax';

var add_validator;
var edit_validator;
jQuery(function($) {
	$.permCheck.run();
	var grid_selector = "#staff-table";
	var pager_selector = "#grid-pager";
	jqGrid_init($(grid_selector), pager_selector, {
		url : list_staff_Url,
		sortable : true,
		sortname : 'loginName',
		sortorder : 'desc',
		colNames : [ '用户ID', '用户名', '真实姓名', '性别', '手机', '邮箱', '状态', '' ],
		colModel : [ {
			name : 'staffId',
			index : 'staffId',
			sortable : false,
			width : 50,
			hidden : true
		}, {
			name : 'loginName',
			index : 'loginName',
			sortable : true,
			sortname : 'login_name',
			width : 100
		}, {
			name : 'realName',
			index : 'realName',
			sortable : true,
			sortname : 'real_name',
			width : 100
		}, {
			name : 'sexName',
			index : 'sexName',
			sortable : true,
			sortname : 'sex',
			width : 100
		}, {
			name : 'mobile',
			index : 'mobile',
			sortable : true,
			sortname : 'mobile',
			width : 100
		}, {
			name : 'email',
			index : 'email',
			sortable : true,
			sortname : 'email',
			width : 100
		}, {
			name : 'statusName',
			index : 'statusName',
			sortable : true,
			sortname : 'status',
			width : 100
		}, {
			name : 'myac',
			index : '',
			width : 200,
			fixed : true,
			sortable : false,
			resize : false,
			formatter : actionButtons
		}, ]
	});

	jQuery('#role-table').jqGrid({
		url : '',
		datatype : "json",
		height : 410,
		// width: "100%",
		colNames : [ '角色id', '角色名称', '' ],
		colModel : [ {
			name : 'roleId',
			index : 'roleId',
			sortable : false,
			width : 50,
			hidden : true
		}, {
			name : 'roleName',
			index : 'roleName',
			sortable : false,
			width : 250
		}, {
			name : 'myac',
			index : '',
			width : 80,
			fixed : true,
			sortable : false,
			resize : false,
			formatter : actionCheckBox
		}, ],
		autowidth : true
	});

	function actionButtons(cellvalue, options, rowObject) {
		var updateStatus, showText, btnClass;

		if (rowObject.status == 'LOCKED') {
			operation = 'unlock';
			showText = '解锁';
			btnClass = 'btn-success';
			iconClass = 'fa-unlock';
		} else {
			operation = 'lock';
			showText = '锁定';
			btnClass = 'btn-primary';
			iconClass = 'fa-lock';
		}
		return '<div >'
				+ '<button style="display:none"  permCheck="auth_admin_sys_staff_staffManage,UPDATE,hide" onclick=\"editEvent('
				+ rowObject.staffId
				+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >'
				+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
				+ '</button>'
				+ '<button style="display:none"  permCheck="auth_admin_sys_staff_staffManage,DELETE,hide" onclick=\"deleteEvent('
				+ rowObject.staffId
				+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >'
				+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
				+ '</button>'
				+ '<button style="display:none"  permCheck="auth_admin_sys_staff_staffManage,LOCKSTAFF,hide" onclick=\"updateStatusEvent('
				+ rowObject.staffId
				+ ',\''
				+ operation
				+ '\')\" class=\"btn btn-xs '
				+ btnClass
				+ '\" data-rel=\"tooltip\" title=\"'
				+ showText
				+ '\">'
				+ '<i class=\"ace-icon fa '
				+ iconClass
				+ ' bigger-120\"></i>'
				+ '</button>'
				+ '<button style="display:none"  permCheck="auth_admin_sys_staff_staffManage,AUTHSTAFF,hide" onclick=\"assignRoleEvent(\''
				+ rowObject.staffId
				+ '\',\''
				+ rowObject.loginName
				+ '\')\" class=\"btn btn-xs btn-warning\" data-rel=\"tooltip\" title=\"分配角色\">'
				+ '<i class=\"ace-icon fa fa-users bigger-120\"></i>'
				+ '</button>' + '</div>';
	}

	add_validator = $('#addForm').validate(
			{
				rules : {
					'loginName' : {
						required : true,
						remote : check_login_name_url,
						maxlength : 100
					},
					'realName' : {
						required : true,
						maxlength : 100
					},
					'sex' : {
						'required' : true
					},
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
					},
					'password' : {
						required : true,
						minlength : 6,
						maxlength : 20
					},
					'pwdConfirm' : {
						required : true,
						minlength : 6,
						maxlength : 20,
						equalTo : "#password"
					}
				},
				messages : {
					'loginName' : {
						remote : '用户名已存在',
					},
					'sex' : {
						required : '请选择性别'
					},
					'pwdConfirm' : {
						equalTo : "您两次输入的密码不一致"
					}
				},
				submitHandler : function(form) {
					$.ajaxSubmit(create_staff_url, $(form).serializeArray(),
							function(data) {
								if (data.success) {
									$('#addnewstaff-modal').modal2('hide');
									$('#staff-table').trigger("reloadGrid");
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

	edit_validator = $('#editForm').validate(
			{
				rules : {
					'loginName' : {
						required : true,
						maxlength : 100
					},
					'realName' : {
						required : true,
						maxlength : 100
					},
					'sex' : {
						'required' : true
					},
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
					'loginName' : {
						required : '不能为空',
						remote : '用户名已存在',
						maxlength : '不能超过100个字符'
					},
					'realName' : {
						required : '不能为空',
						maxlength : '不能超过100个字符'
					},
					'sex' : {
						required : '请选择性别'
					},
					'email' : {
						required : '不能为空',
						email : '不是有效的邮箱',
						maxlength : '不能超过64个字符'
					},
					'mobile' : {
						required : '不能为空',
						maxlength : '不能超过11个字符'
					}
				},
				submitHandler : function(form) {
					$.ajaxSubmit(update_staff_url, $(form).serializeArray(),
							function(data) {
								if (data.success) {
									$('#editstaff-modal').modal2('hide');
									$('#staff-table').trigger("reloadGrid");
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

});
function resetAddForm() {
	resetForm($('#addForm'), add_validator);
}
/**$('#addNewStaffBtn').on('click', function() {
	resetAddForm();
	$('#updId').val('');
	$('#loginName').removeAttr('disabled');
	$('#addnewstaff-modal').modal2({
		backdrop : "static",
		show : true
	});
});**/
function actionCheckBox(cellvalue, options, row) {

	var checked = '';
	if (row.others.check == 'true') {
		checked = 'checked=\'true\'';
	}
	return "<div class=\"checkbox\" >" + "<label class=\"btn-sm\" >"
			+ "<input type=\"checkbox\" onclick=\"updateStaffRole(this)\" "
			+ checked + " value=\"" + row.roleId
			+ "\" class=\"ace ace-checkbox-2\" />"
			+ "<span class=\"lbl\">&nbsp;</span>" + "</label>" + "</div>";
};

function editEvent(id) {
	$.ajaxSubmit(staff_info_url, {
		staffId : id
	}, function(data) {
		resetForm($('#editForm'), edit_validator);
		if (data.success) {
			$('#editForm').json2Form2(data.data);
			// $('#updId').val(id);
			// $('#loginName').attr('disabled','disabled');
			$('#editstaff-modal').modal2({
				backdrop : "static",
				show : true
			});
		} else {
			if (data.message) {
				Q_Alert_Fail(data.message);
			} else {
				Q_Alert_Fail('原因未知');
			}
		}
	});
};

function deleteEvent(id) {
	Q_Confirm("是否要删除？", function(result) {
		if (result) {
			$.ajaxSubmit(delete_staff_url, {
				'staffId' : id
			}, function(data) {
				$('#staff-table').jqGrid('setGridParam', {
					page : 1
				}).trigger("reloadGrid");
			});
		}
	});
};

function updateStatusEvent(staffId, oper) {
	// alert(staffId + ' ' + oper);
	$.ajaxSubmit(locked_staff_url, {
		'operation' : oper,
		'staffId' : staffId
	}, function(data) {
		$('#staff-table').trigger("reloadGrid");
	});
};

function assignRoleEvent(staffId, name) {
	$('#role-to-staff').find("span[name='loginName']").html(name);

	$('#role-to-staff').find("input[name='staffId']").val(staffId);

	var paramUrl = staff_role_url + '?staffId=' + staffId;

	jQuery('#role-table').jqGrid('setGridParam', {
		url : paramUrl
	}).trigger("reloadGrid");

	$('#role-to-staff').modal2({
		backdrop : "static",
		show : true
	});

};

function updateStaffRole(box) {

	// alert('ok ' + box.checked);
	var staffId = $('#role-to-staff').find("input:hidden[name='staffId']")
			.val();
	var oper = '';

	if (box.checked == true) {
		oper = 'add';
	} else {
		oper = 'del';
	}

	$.ajaxSubmit(update_staff_role_url, {
		'operation' : oper,
		'staffId' : staffId,
		'roleId' : $(box).val()
	}, function(data) {

		if (data.success == false) {
			Q_Alert_Fail(data.message);
			box.checked = !box.checked;
		}

	});
}
var resetPwdForm = $('#resetpassword-modal').find('form').validate({
	rules : {
		'password' : {
			required : true,
			password : true
		},
		'pwdConfirm' : {
			required : true,
			password : true,
			equalTo : "#password2"
		}
	},
	submitHandler : function(form) {
		// var form = $('#add-dept-modal').find('input,textarea');
		var params = $(form).serializeJson();
		// alert('params:' + params.length);
		$.ajaxSubmit(reset_pwd_url, params, function(data) {
			if (data.success == true) {
				$('#resetpassword-modal').modal2('hide');
				Q_Alert('重置成功');
			} else {
				Q_Alert_Fail(data.message);
			}

		});
		return false;
	}
});
$('#reset-password-btn').on('click', function() {
	// $('#editstaff-modal').modal2('hide');
	resetForm($('#resetpassword-modal').find('form'), resetPwdForm);
	$('#loginName2').val($('#updLoginName').val());
	$('#resetpassword-modal').modal2({
		backdrop : "static",
		show : true
	});
});

$('#reset-cancel-btn').on('click', function() {
	$('#resetpassword-modal').modal2('hide');
	$('#editstaff-modal').modal2({
		backdrop : "static",
		show : true
	});
});

$('#seachBtn').on('click', function() {
	jQuery('#staff-table').jqGrid('setGridParam', {
		postData : $('#queryForm').serializeJson(),
		page : 1
	}).trigger("reloadGrid");
});