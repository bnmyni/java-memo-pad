var basePath = ctxPaths;
var dept_tree_Url = basePath + '/department/listDepartmentTree.ajax';
var find_dept_Url = basePath + '/department/findDepartment.ajax';
var find_dept_info_Url = basePath + '/department/findDepartmentInfo.ajax';
var update_dept_url = basePath + '/department/updateDepartment.ajax';
var delete_dept_url = basePath + '/department/delDepartment.ajax';
var update_dept_role_url = basePath + '/department/updateDepartmentRole.ajax';
var dept_role_url = basePath + '/department/listDepartmentRoles.ajax';
var list_staff_url = basePath + '/staff/listStaff.ajax';
var update_staff_department_url = basePath + '/staff/updateStaffDepartment.ajax';
var list_role_by_staffids_url = basePath + '/staff/listRoleByStaffIds.ajax';
var update_staff_roles_department_url = basePath + '/staff/updateStaffRolesDepartment.ajax';
var find_login_staff = basePath + '/staff/findLoginStaff.ajax';
var create_staff_url = basePath + '/staff/createStaff.ajax';
var check_login_name_url = basePath + '/staff/checkStaffLoginName.ajax';
var myStaff = {},departmentId = '';
// var check_login_name_url = basePath+ '/auth/checkStaffLoginName.ajax';
// var locked_staff_url = basePath+ '/auth/lockStaff.ajax';
// var unlocked_staff_url = 'auth/un/lockStaff.ajax';
// var role_info_url = basePath+ '/auth/findRole.ajax';
var DataSourceTree = function(options) {
	this._data = options.data;
	this._delay = options.delay;
};

DataSourceTree.prototype.data = function(options, callback) {
	var $data = null;

	if (!("name" in options) && !("type" in options)) {
		$data = this._data;// the root tree
		callback({
			data : $data
		});
		return;
	} else if ("type" in options && options.type == "folder") {
		if ("additionalParameters" in options
				&& "children" in options.additionalParameters)
			$data = options.additionalParameters.children;
		else
			$data = {};// no data
	}

	if ($data != null)// this setTimeout is only for mimicking some random
						// delay
		setTimeout(function() {
			callback({
				data : $data
			});
		}, 200);

	// we have used static data here
	// but you can retrieve your data dynamically from a server using ajax call
	// checkout examples/treeview.html and examples/treeview.js for more info
};
var validator = $('#add-dept-form').validate(
		{
			rules : {
				'departmentName' : {
					required : true,
					maxlength : 100
				},
				'departmentDesc' : {
					required : false,
					maxlength : 100
				},
				'address' : {
					required : false,
					maxlength : 200
				},
				'email' : {
					required : false,
					email : true,
					maxlength : 50
				}
			},
			submitHandler : function(form) {
				// var form = $('#add-dept-modal').find('input,textarea');
				var params = $(form).serializeJson();
				// alert('params:' + params.length);
				$.ajaxSubmit(update_dept_url, params, function(data) {
					if (data.success == true) {
						// refreshDeptTree();
						if (params['departmentId']) {
							treePlus.edit(params['departmentId'],
									params['departmentName']);
						} else {
							treePlus.append(params['parentId'], data.data);
							// refreshDeptTree();
						}
						$('#add-dept-modal').modal2('hide');
					} else {
						Q_Alert_Fail(data.message); 
					}

				});
				return false;
			}
});
function findLoginStaff(){
	$.ajax(find_login_staff, {
        type:"GET",
        async: false,
        success: function (json) {
			if (json && json['success']){
				myStaff = json['data'];
				departmentId = myStaff['departmentId'] || '';
			}
		}
	});
}
jQuery(function($) {
	findLoginStaff();
	// 初始化组织树
	refreshDeptTree();

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

	$('#add-role-btn').on(
			'click',
			function() {
				// alert('ok');
				var deptId = $('#departmentId').val();
				// alert($('#departmentName').text());

				$('#role-to-dept').find("span[name='departmentName']").html(
						$('#departmentName').text());

				$('#role-to-dept').find("input[name='departmentId']").val(
						deptId);

				var paramUrl = dept_role_url + '?departmentId=' + deptId;

				jQuery('#role-table').jqGrid('setGridParam', {
					url : paramUrl
				}).trigger("reloadGrid");

				$('#role-to-dept').modal2({backdrop:"static",show:true});
			});
	/**$('#add-staff-btn').on('click', function() {
		showAddStaffModal();
	});**/
	$('#auth-staff-btn').on('click', function() {
		if ($('#dept-roles').val() ===''){
			Q_Alert_Fail('当前组织无角色，请先为该组织添加角色。'); 
			return;
		}
		showSelectedAuthStaffModal();
	});
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
						equalTo : "两次输入的密码不一致"
					}
				},
				submitHandler : function(form) {
					$('#addstaff-roles').val($('input[name="staffRoleList"]').values());
					$.ajaxSubmit(create_staff_url, $(form).serializeArray(),
							function(data) {
								if (data.success) {
									$('#addnewstaff-modal').modal2('hide');
									selectOneDepartment($('#departmentId').val());
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
	function resetAddForm() {
		resetForm($('#addForm'), add_validator);
	}
	$('#add-staff-btn').on('click', function() {
		resetAddForm();
		if (typeof citys !=='undefined' && citys.length > 0){
			$('#staff-cityid').empty().localRender(citys,{
				firstOption:{text:'请选择地市',value:''},
				keyAlias:{text:'cityId',value:'CityName'}
			});
			$('#cityArea').show();
		}
		var roles = $('#dept-roles').data('list'),roleList = [];
		for (var i = 0;i < roles.length; i++){
			roleList.push('<label class="btn-sm"><input type="checkbox" class="ace" name="staffRoleList" value="'+roles[i]['roleId']+'">');
			roleList.push('<span class="lbl">'+roles[i]['roleName']+'&nbsp;</span></label>');
		}
		$('#staff-roleList').empty().html(roleList.join(''));
		$('#addstaff-departmentId').val($('#departmentId').val());
		$('#addnewstaff-modal').modal2({
			backdrop : "static",
			show : true
		});
	});
});
function showSelectedAuthStaffModal() {
	var $modal = $('#auth-staff-selected-modal');
	var multSelect2 = new MultSelect2();
	multSelect2.init({
		"mode":1,
		"select1" : 'auth-staff-list',// 选择列表select的id
		"select2" : 'selected-auth-staff-list',// 选中列表select的id
		"addBtn" : 'auth-staff-list-add-btn',// 添加按钮id
		"delBtn" : 'auth-staff-list-del-btn',// 删除按钮id
		"addAllBtn" : 'auth-staff-list-addall-btn',// 添加所有按钮id
		"delAllBtn" : 'auth-staff-list-delall-btn',// 删除所有按钮id
		"id" : 'staffId',// 传过来list对应的下拉列表的key别名
		"text" : 'realName',// 传过来list对应的下拉列表的text别名
		"data1" : $('#dept-staffs').data('list'),// 选择列表select初始值
		"data2" : []
	// 选中列表select初始值
	});
	$('#auth-staff-list-selected-tip').html("");
	$('#auth-staff-list,#selected-auth-staff-list').unbind('click').bind('click',function(){
		var option = $(this).find("option:selected").get(0);
		if (!option)return;
		var data = multSelect2.getSelectData(option.value);
		if (data){
			$('#auth-staff-list-selected-tip').html("描述：" + option.text + " -> " + data['loginName']);
		}
	});
	$('#selected-auth-staff-tip').html('');
	$('#save-selected-auth-staff-btn').unbind('click').bind('click',function(){
		var datas = multSelect2.getSelectedDatas();
		if (datas.length == 0){
			$('#selected-auth-staff-tip').html('【提示:所选用户不能为空。】');
			return;
		}else if (datas.length > 10){
			$('#selected-auth-staff-tip').html('【提示:所选用户个数不能超过10个。】');
			return;
		}
		$modal.modal2('hide');
		var staffIds = [];
		$.each(datas,function(i,val){
			staffIds.push(val['staffId']);
		});
		$.ajaxSubmit(list_role_by_staffids_url,  {
			departmentId : $('#departmentId').val(),
			staffIds : staffIds.join(",")
		}, function(data) {
			if (data.success == true) {
				showAuthStaffModal(datas,data.data);
			} else {
				Q_Alert_Fail(data.message); 
			}
		});
		
	});
	$modal.modal2({backdrop:"static",show:true});
}
function toBreakWord(str){
	if (!str || str ==='')return '';
	var s = [];
	for (var i = 0; i < str.length; i++){
		s.push(str[i]);
		if ( i != str.length - 1)
		s.push('<br/>');
	}
	return s.join('');
}
function showAuthStaffModal(staffs,staffRoles) {
	var $modal = $('#auth-staff-modal'),$table = $('#auth-staff-table'),roles =$('#dept-roles').data('list'),html = [];
	$table.empty();
	html.push('<thead>');
	html.push('<tr><th style="width:180px;padding:0;"><div class="out"><b>姓名</b><em>角色</em></div></th>');
	for (var i= 0; i < staffs.length; i++){
		html.push('<th><a  href="javascript:;" data-rel="tooltip" title="'+staffs[i]['realName']+'"><div  class="th_1">'+toBreakWord(staffs[i]['realName'])+'</div></a></th>');
	}
	html.push('</tr>');
	html.push('</thead>');
	html.push('<tbody>');
	for (var i= 0; i < roles.length; i++){
		html.push('<tr>');
		html.push('<td><div class="txt-overflow-hidden" style="width:160px;" title="'+roles[i]['roleName']+'">'+roles[i]['roleName']+'</div></td>');
		for (var j= 0; j < staffs.length;j++){
			html.push('<td><label><input type="checkbox" value="' + staffs[j]['staffId'] +'|' + roles[i]['roleId']+'" name="authStaff2Role" class="ace"/><span class="lbl"></span></label></td>');
		} 
		html.push('</tr>');
	}
	html.push('</tbody>');
	$table.append(html.join(''));
	$('#auth-staff-department').html($('#departmentName').html());
	var selectedRoles = [],staffIds = [];
	$.each(staffRoles,function(i,val){
		selectedRoles.push(val['staffId'] + "|" + val['roleId']);
	});
	$.each(staffs,function(i,val){
		staffIds.push(val['staffId']);
	});
	$('input[name="authStaff2Role"]').values(selectedRoles.join(','));
	$modal.modal2({backdrop:"static",show:true});
	$('[data-rel=tooltip]').tooltip();
	$('#save-auth-staff-btn').unbind('click').bind('click',function(){
		$.ajaxSubmit(update_staff_roles_department_url,  {
			departmentId : $('#departmentId').val(),
			staffIds : staffIds.join(","),
			staffIdRoles : $('input[name="authStaff2Role"]').values()
		}, function(data) {
			$modal.modal2('hide');
			if (data.success == true) {
				Q_Alert(data.data); 
			} else {
				Q_Alert_Fail(data.message); 
			}
		});
	});
}
function showAddStaffModal() {
	var $addStaffModal = $('#add-staff-modal');
	$('#staff-list-seach-area').show();
	$('#add-staff-form').get(0).reset();
	var multSelect2 = new MultSelect2();
	var select2_params = {
		"select1" : 'staff-list',// 选择列表select的id
		"select2" : 'selected-staff-list',// 选中列表select的id
		"addBtn" : 'staff-list-add-btn',// 添加按钮id
		"delBtn" : 'staff-list-del-btn',// 删除按钮id
		"addAllBtn" : 'staff-list-addall-btn',// 添加所有按钮id
		"delAllBtn" : 'staff-list-delall-btn',// 删除所有按钮id
		"id" : 'staffId',// 传过来list对应的下拉列表的key别名
		"text" : 'realName',// 传过来list对应的下拉列表的text别名
		"data1" : [],// 选择列表select初始值
		"data2" : []
	// 选中列表select初始值
	};
	multSelect2.init(select2_params);
	multSelect2.initSelect2($('#dept-staffs').data('list'));
	$('#staff-list-selected-tip').html("");
	$('#staff-list,#selected-staff-list').unbind('click').bind('click',function(){
		var option = $(this).find("option:selected").get(0);
		if (!option)return;
		var data = multSelect2.getSelectData(option.value);
		if (data){
			$('#staff-list-selected-tip').html("描述：" + option.text + " -> " + data['loginName']);
		}
	});
	function searchStaffList(params) {
		$.ajaxSubmit(list_staff_url, $.extend(params, {
			rows : 100000,
			page : 1,
			'params["noAdmin"]' : 1
		}), function(data) {
			if (data.success == true) {
				/**multSelect2.init($.extend(select2_params, {
					"data1" : data.rows,
					"data2" : []
				}));**/
				multSelect2.initSelect1(data.rows);
			} else {
				Q_Alert_Fail(data.message); 
			}
		});
	}
	$('#staff-list-seach-btn').unbind('click').bind('click', function() {
		searchStaffList({
			'params["keyword"]' : $('#searchText2').val()
		});
	});
	$('#save-staff-btn').unbind('click').bind('click', function() {
		var staffs = multSelect2.getSelectedDatas(), c = [],staffIds = [];
		$.each(staffs, function(i, val) {
			c.push(val['realName']);
			staffIds.push(val['staffId']);
		});
		$.ajaxSubmit(update_staff_department_url, {
			'departmentId' : $('#departmentId').val(),
			'staffIds' : staffIds.join(',')
		}, function(data) {
			if (data.success == true) {
				$('#dept-staffs').data('list', staffs).val(c.join(","));
				$addStaffModal.modal2('hide');
			} else {
				Q_Alert_Fail(data.message); 
			}
		});
	});

	$("input[name='searchType']").unbind('click').bind('click', function() {
		var value = $("input[name='searchType']:checked").val();
		if (value == '0') {
			$('#staff-list-seach-area').hide();
			searchStaffList();
		} else if (value == '1') {
			$('#staff-list-seach-area').show();
			multSelect2.initSelect1([]);
		}
	});
	
	$addStaffModal.find('h4').html('【<span class="green txt-overflow-hidden" style="max-width:320px;">' + $('#departmentName').text() + '</span>】用户分配');
	$addStaffModal.modal2({backdrop:"static",show:true});
}

function selectOneDepartment(deptId) {
	Loading.isShow = true;
	if (departmentId == deptId){
		$('#authBtnGrp').hide();
	}else{
		$('#authBtnGrp').show();
	}
	// alert(deptId);
	$.ajaxSubmit(find_dept_info_Url, {
		'departmentId' : deptId
	}, function(data) {

		// alert(data.success);

		if (data.success == true) {
			$('#departmentName').html(data.data.departmentName);
			$('#departmentId').val(deptId);

			if (data.data.others.roles != 'undefinded') {
				var c = [],roles = data.data.others.roles;
				$.each(roles,function(i,val){
					c.push(val['roleName']);
				});
				$('#dept-roles').data('list',roles).val(c.join(","));
			} else {
				$('#dept-roles').data('list',[]).val('');
			}

			if (data.data.others.staffs != 'undefinded') {
				var c = [],staffs = data.data.others.staffs;
				$.each(staffs,function(i,val){
					c.push(val['realName']);
				});
				$('#dept-staffs').data('list',staffs).val(c.join(","));
			} else {
				$('#dept-staffs').data('list',[]).val('');
			}

			// 按钮可点
			$('#add-role-btn').removeClass('disabled');
			$('#add-staff-btn').removeClass('disabled');
			$('#auth-staff-btn').removeClass('disabled');
		} else {
			Q_Alert_Fail(data.message); 
		}

	});

};

function addDepartmentEvent(deptId) {
	// alert(deptId);
	resetForm($('#add-dept-form'), validator);
	$('#add-dept-modal').find("input:hidden[name='parentId']").val(deptId);
	$('#add-dept-modal').find("input:hidden[name='departmentId']").val('');
	$('#add-dept-form').find('h4').html('新增组织');
	$('#add-dept-modal').modal2({backdrop:"static",show:true});
};

function editDepartmentEvent(deptId) {
	$('#add-dept-modal').find("input:hidden[name='departmentId']").val(deptId);
	$.ajaxSubmit(find_dept_Url, {
		'departmentId' : deptId
	}, function(data) {
		if (data.success == true) {
			var $addDeptForm = $('#add-dept-form');
			resetForm($addDeptForm, validator);
			loadForm($addDeptForm, data.data);
			$addDeptForm.find('h4').html('修改组织');
			$('#add-dept-modal').modal2({backdrop:"static",show:true});
		} else {
			Q_Alert_Fail(data.message); 
		}

	});
};

function delDepartmentEvent(deptId) {
	// alert(deptId);
	if (departmentId == deptId){
		Q_Alert_Fail('你没有权限删除当前组织!'); 
		return;
	}
	bootbox.confirm({
		message : "是否要删除？",
		buttons : {
			confirm : {
				label : "确定",
				className : "btn-danger btn-sm",
			},
			cancel : {
				label : "取消",
				className : "btn-sm",
			}
		},
		callback : function(result) {
			if (result) {
				$.ajaxSubmit(delete_dept_url, {
					'departmentId' : deptId
				}, function(data) {
					// $('#role-table').trigger("reloadGrid");
					if (data.success == true) {
						treePlus.del(deptId);
					} else {
						Q_Alert_Fail(data.message); 
					}
				});
			}
		}
	});
};

var treePlus = {
	getOptBtn : function(id) {
			if (id === '-999') {
				return '<span class=\'margin-left-32\' data-id=\''
						+ id
						+ '\' style=\'display:none\'><a class=\'blue margin-left4\' href=\'javascript:;\' addBtn permCheck=\'auth_admin_sys_dept_deptManage,MODIFY,delete\' ><i class=\'ace-icon glyphicon glyphicon-plus green\'></i></a><a class=\'blue margin-left4\' href=\'javascript:;\' editBtn permCheck=\'auth_admin_sys_dept_deptManage,MODIFY,delete\'><i class=\'ace-icon fa fa-pencil\'></i></a></span>';
			} else {
				return '<span class=\'margin-left-32\' data-id=\''
						+ id
						+ '\' style=\'display:none\'><a class=\'blue margin-left4\' href=\'javascript:;\' addBtn permCheck=\'auth_admin_sys_dept_deptManage,MODIFY,delete\'><i class=\'ace-icon glyphicon glyphicon-plus green\'></i></a><a class=\'blue margin-left4\' href=\'javascript:;\' editBtn permCheck=\'auth_admin_sys_dept_deptManage,MODIFY,delete\'><i class=\'ace-icon fa fa-pencil\'></i></a><a class=\'red margin-left4\' href=\'javascript:;\' delBtn permCheck=\'auth_admin_sys_dept_deptManage,DELETE,delete\'><i class=\'ace-icon fa fa-trash-o\'></i></a></span>';
			}
	},
	del : function(dataId) {
		var _parent = $('span[data-id="' + dataId + '"]').parent('div');
		if (_parent.hasClass('tree-folder-name')) {
			_parent.parent('.tree-folder-header').parent('.tree-folder')
					.remove();
		} else {
			if (_parent.parent('.tree-item').parent('.tree-folder-content').find('.tree-item').length ==1){
				var _parent2 = _parent.parent('.tree-item').parent('.tree-folder-content').parent('.tree-folder');
				///_parent2.removeClass('tree-folder').addClass('tree-item');
				//_parent2.prepend(_parent2.children('.tree-folder-header').children('.tree-folder-name').html());
				_parent2.replaceWith('<div class="tree-item"><div class="tree-item-name">'+_parent2.children('.tree-folder-header').children('.tree-folder-name').html()+'</div></div>');
				//_parent2.children('.tree-folder-content').remove();
			}else{
				_parent.parent('.tree-item').remove();
			}
		}
	},
	append : function(parentDeptId, itemData) {
		var _parent = $('span[data-id="' + parentDeptId + '"]').parent('div');
		if (_parent.hasClass('tree-folder-name')) {
			var treeItem = '<div class="tree-item"><div class="tree-item-name"><span class="margin-right32" deptid="'
					+ itemData['departmentId']
					+ '">'
					+ itemData['departmentName']
					+ '</span>'+treePlus.getOptBtn(itemData['departmentId'])+'</div></div>';
			
			var expand = _parent.parent('.tree-folder-header').children('i');
			if (expand.hasClass('tree-plus')) {
				expand.trigger('click');
			}
			setTimeout(function() {
				_parent.parent('.tree-folder-header').parent('.tree-folder')
						.children('.tree-folder-content').append(treeItem);
			}, 500);
		} else {
			var treeFolder = '<div class="tree-folder"><div class="tree-folder-header"><i class="ace-icon tree-minus"></i><div class="tree-folder-name"><span class="margin-right32" deptid="'
					+ parentDeptId
					+ '">'
					+ $('span[deptId="' + parentDeptId + '"]').html()
					+ '</span>'+treePlus.getOptBtn(parentDeptId)+'</div>				</div>				<div class="tree-folder-content"><div style="display: block;" class="tree-item"><div class="tree-item-name"><span class="margin-right32" deptid="'
					+ itemData['departmentId']
					+ '">'
					+ itemData['departmentName']
					+ '</span>'+treePlus.getOptBtn(itemData['departmentId'])+'</div></div></div><div style="display: none;" class="tree-loader"><div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div></div></div>';
			_parent.parent('.tree-item').replaceWith(treeFolder);
			
		}
	},
	edit : function(dataId, txt) {
		$('span[deptId="' + dataId + '"]').html(txt);
	}
};
function refreshDeptTree() {
	$
			.ajaxSubmit(
					dept_tree_Url,
					{},
					function(data) {
						if (data.success == true) {
							var $tree = $('#tree1');
							$tree.empty().removeClass('tree-unselectable');
							var initEvent = function() {
								//var minWidth = $tree.css('min-width').replace('px','') || 320;
								//minWidth = parseInt(minWidth,10);
								//if ($tree.scrollLeft() > 0){
								//	$tree.width($tree.width() + 60);
								//}
								
								$('span[deptId]')
										.unbind('click')
										.bind(
												'click',
												function(e) {
													var nodeId = $(this).attr(
															'deptId');
													selectOneDepartment(nodeId);
													e.stopPropagation();
												});
								$('a[addBtn]').unbind('click').bind(
										'click',
										function(e) {
											var nodeId = $(this).parent('span')
													.attr('data-id');
											addDepartmentEvent(nodeId);
											e.stopPropagation();
										});
								$('a[editBtn]').unbind('click').bind(
										'click',
										function(e) {
											var nodeId = $(this).parent('span')
													.attr('data-id');
											;
											editDepartmentEvent(nodeId);
											e.stopPropagation();
										});
								$('a[delBtn]').unbind('click').bind(
										'click',
										function(e) {
											var nodeId = $(this).parent('span')
													.attr('data-id');
											;
											delDepartmentEvent(nodeId);
											e.stopPropagation();
										});
								$('.tree-folder-header,.tree-item').unbind(
										'mouseover').unbind('mouseout').bind(
										'mouseover',
										function() {
											$(this).find('span[data-id]')
													.show();
										}).bind('mouseout', function() {
											$(this).find('span[data-id]').hide();
								});
								$.permCheck.run();
							};
							if (/msie/.test(navigator.userAgent.toLowerCase())
									&& !$.support.leadingWhitespace) {// ie8以下不支持DOMNodeInserted,这里通过模拟做监听
								var _tmp = $tree.html();
								setInterval(function() {
									if (_tmp != $tree.html()) {
										initEvent();
										_tmp = $tree.html();
									}
								}, 100);
							} else {
								$tree.bind('DOMNodeInserted', function() {
									initEvent();
								});
							}
							var datas = data.data;
							var treeMap = [];
							
							var subTree = function(nodes) {
								var len = nodes.length;
								treeMap
										.push(",additionalParameters:{children:{");
								for (var i = 0; i < len; i++) {
									treeMap.push('"' + nodes[i]['id'] + '":{');
									treeMap.push('name:"<span class=\'margin-right32\' deptId=\''
											+ nodes[i]['id'] + '\'>'
											+ nodes[i]['text'] + '</span>'
											+ treePlus.getOptBtn(nodes[i]['id']) + '"');
									treeMap.push(',type:"'
											+ (nodes[i]['leaf'] ? 'item'
													: 'folder') + '"');
									if (nodes[i]['children']
											&& nodes[i]['children'].length > 0) {
										subTree(nodes[i]['children']);
									}

									if (i != len - 1) {
										treeMap.push('},');
									} else {
										treeMap.push('}');
									}
								}
								treeMap.push("}}");
							};
							var transformTree = function(nodes) {
								var len = nodes.length;
								treeMap.push("{");
								for (var i = 0; i < len; i++) {
									treeMap.push('"' + nodes[i]['id'] + '":{');
									treeMap.push('name:"<span class=\'margin-right32\' deptId=\''
											+ nodes[i]['id'] + '\'>'
											+ nodes[i]['text'] + '</span>'
											+ treePlus.getOptBtn(nodes[i]['id']) + '"');
									treeMap.push(',type:"'
											+ (nodes[i]['leaf'] ? 'item'
													: 'folder') + '"');
									if (nodes[i]['children']
											&& nodes[i]['children'].length > 0) {
										subTree(nodes[i]['children']);
									}
									if (i != len - 1) {
										treeMap.push('},');
									} else {
										treeMap.push('}');
									}
								}
								treeMap.push("}");
							};
							transformTree(datas);
							var tree_Data_Source = new DataSourceTree({
								data : eval(("(" + treeMap.join('') + ")"))
							});
							$tree
									.ace_tree({
										dataSource : tree_Data_Source,
										multiSelect : true,
										loadingHTML : '<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
										'open-icon' : 'ace-icon tree-minus',
										'close-icon' : 'ace-icon tree-plus',
										'selectable' : false,
										'selected-icon' : null,
										'unselected-icon' : null
									});

						} else {
							Q_Alert_Fail(data.message); 
						}

					});
};

function addIcons(node) {

	return '<div class="pull-right action-buttons">'
			+ '<a class="blue" href="javascript:addDepartmentEvent(\''
			+ node.id
			+ '\')">'
			+ '<i class="ace-icon glyphicon glyphicon-plus green bigger-130"></i>'
			+ '</a>'
			+ '<a class="blue" href="javascript:editDepartmentEvent(\''
			+ node.id + '\')">'
			+ '<i class="ace-icon fa fa-pencil bigger-130"></i>' + '</a>'
			+ '<a class="red" href="javascript:delDepartmentEvent(\'' + node.id
			+ '\')">' + '<i class="ace-icon fa fa-trash-o bigger-130"></i>'
			+ '</a>' + '</div>';
};

function actionCheckBox(cellvalue, options, row) {

	var checked = '';
	if (row.others.check == 'true') {
		checked = 'checked=\'true\'';
	}
	return "<div class=\"checkbox\" >"
			+ "<label class=\"btn-sm\" >"
			+ "<input type=\"checkbox\" onclick=\"updateDepartmentRole(this)\" "
			+ checked + " value=\"" + row.roleId
			+ "\" class=\"ace ace-checkbox-2\" />"
			+ "<span class=\"lbl\">&nbsp;</span>" + "</label>" + "</div>";
};

function updateDepartmentRole(box) {

	var deptId = $('#role-to-dept').find("input:hidden[name='departmentId']")
			.val();
	var oper = '';

	if (box.checked == true) {
		oper = 'add';
	} else {
		oper = 'del';
	}

	$.ajaxSubmit(update_dept_role_url, {
		'operation' : oper,
		'departmentId' : deptId,
		'roleId' : $(box).val()
	}, function(data) {
		selectOneDepartment(deptId);
	});
};

function loadForm(form, data) {
	form.find("input,textarea,select").each(function() {
		var input = $(this);

		input.val(data[input.attr('name')]);
	});
};