var basePath = ctxPaths;
var list_role_Url = basePath + '/role/listRoles.ajax';
var list_role_res_Url = basePath + '/role/listRoleResource.ajax';
var update_role_res_url = basePath + '/role/updateRoleResource.ajax';
var delete_role_url = basePath + '/role/deleteRole.ajax';
var update_role_url = basePath + '/role/updateRole.ajax';
var role_info_url = basePath + '/role/findRole.ajax';
var check_rolekey_url = basePath + '/role/checkRoleKey.ajax?roleId=';
var grid_selector = "#role-table";
var pager_selector = "#grid-pager";
jQuery(function($) {
	$.permCheck.run();
	jqGrid_init($(grid_selector), pager_selector, {
		url : list_role_Url,
		sortable : true,
		sortname : 'roleKey',
		sortorder : 'desc',
		colNames : [ '角色ID', '角色助记码', '角色名称', '角色描述', '' ],
		colModel : [ {
			name : 'roleId',
			index : 'roleId',
			sortable : false,
			width : 50,
			hidden : true
		}, {
			name : 'roleKey',
			index : 'roleKey',
			sortable : true,
			sortname : 'role_key',
			width : 50
		}, {
			name : 'roleName',
			index : 'roleName',
			sortable : true,
			sortname : 'role_name',
			width : 100
		}, {
			name : 'roleDesc',
			index : 'roleDesc',
			sortable : false,
			width : 120
		}, {
			name : 'myac',
			index : '',
			width : 150,
			fixed : true,
			sortable : false,
			resize : false,
			formatter : actionButtons
		}, ]
	});
	function actionButtons(cellvalue, options, rowObject) {
		var html = '<div >';

		if (rowObject.roleKey != '1001' && rowObject.roleKey != '1002'
				&& rowObject.roleKey != '1003') {

			html += '<button style="display:none"  permCheck="auth_admin_sys_role_roleManage,MODIFY,hide" onclick=\"editEvent('
					+ rowObject.roleId
					+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >'
					+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
					+ '</button>'
					+ '<button style="display:none"  permCheck="auth_admin_sys_role_roleManage,DELETE,hide" onclick=\"deleteEvent('
					+ rowObject.roleId
					+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >'
					+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
					+ '</button>';
		}
		html += '<button style="display:none"  permCheck="auth_admin_sys_role_roleManage,AUTHROLE,hide" onclick=\"editRoleAuth('
				+ rowObject.roleId
				+ ')\" class=\"btn btn-xs btn-success\" data-rel=\"tooltip\" title=\"权限设置\" >'
				+ '<i class=\"ace-icon fa fa-cogs bigger-120\"></i>'
				+ '</button>' + '</div>';

		return html;
	}

});

function editEvent(id) {
	// alert(id);
	// window.location = 'activities-add.html?activityId=' + id;

	$.ajaxSubmit(role_info_url, {
		'roleId' : id
	}, function(data) {
		if (data.success == true) {
			resetForm($('#addnewrole-form').find('form'), add_validator);
			$('#addnewrole-form').find("input[name='roleId']").val(id);
			$("#roleKey").rules("remove");
			$("#roleKey").rules("add", {
				required : true,
				remote : check_rolekey_url + $('#roleId').val(),
				maxlength : 64
			});
			$('#addnewrole-form').find('form').json2Form2(data.data);
			$('#addnewrole-form').modal2({
				backdrop : "static",
				show : true
			});
		} else {
			Q_Alert_Fail(data.message);
		}
	});

};

function deleteEvent(id) {
	Q_Confirm("是否要删除？", function(result) {
		if (result) {
			$.ajaxSubmit(delete_role_url, {
				'roleId' : id
			}, function(data) {
				$('#role-table').jqGrid('setGridParam', {
					page : 1
				}).trigger("reloadGrid");
			});
		}
	});
};

function editRoleAuth(roleId) {
	// alert(roleId + ' ');
	window.location.replace(ctxPaths + '/pages/auth/roleSetting.shtml?roleId='
			+ roleId + "&r=" + (new Date()).getTime());
	return;
	$('#roleTree').empty();

	$('#roleTree').append(
			$('<input name="roleId" type="hidden" value="' + roleId + '">'));

	$.ajaxSubmit(list_role_res_Url, {
		'roleId' : roleId
	}, function(data) {

		if (data.success == true) {

			var tree = $('<div  class=\"dd\"></div>');

			tree.append(buildTree(data.data));

			tree.nestable();

			tree.nestable('collapseAll');

			bindCheckAll(tree);

			$('#roleTree').append(tree);
			var rows = $(grid_selector).getRowData(), ind = 0;
			for (var i = 0; i < rows.length; i++) {
				if (rows[i]['roleId'] == roleId) {
					ind = i;
					break;
				}
			}
			var roleName = $(grid_selector).getCell(ind + 1, 'roleName');
			$('#role-auth-form-rolename').html(roleName);
			$('#role-auth-form').modal2({
				backdrop : "static",
				show : true
			});
		} else {
			Q_Alert_Fail('error');
		}

	});
};
add_validator = $('#add-role-form').validate({
	rules : {
		'roleKey' : {
			required : true,
			remote : check_rolekey_url,
			maxlength : 64
		},
		'roleName' : {
			required : true,
			maxlength : 100
		},
		'roleDesc' : {
			required : false,
			maxlength : 400
		}
	},
	messages : {
		'roleKey' : {
			remote : '角色助记码已存在',
		}
	},
	submitHandler : function(form) {
		$.ajaxSubmit(update_role_url, $(form).serializeArray(), function(data) {
			if (data.success == true) {
				$('#addnewrole-form').modal2('hide');
				$('#role-table').trigger("reloadGrid");

				resetForm($('#addnewrole-form').find('form'));
			} else {
				Q_Alert_Fail(data.message);
			}
		});
		return false;
	}
});

$('#addNewRoleBtn').on('click', function() {
	$("#roleKey").rules("remove");
	$("#roleKey").rules("add", {
		required : true,
		remote : check_rolekey_url,
		maxlength : 64
	});
	resetForm($('#addnewrole-form').find('form'), add_validator);
	$('#roleId').val('');
	$('#addnewrole-form').modal2({
		backdrop : "static",
		show : true
	});
});

$('#seachBtn').on('click', function() {
	var paramUrl = list_role_Url;
	if ($('#searchText').val() != '') {
		paramUrl += '?params[\'roleName\']=' + $('#searchText').val();
	}
	jQuery('#role-table').jqGrid('setGridParam', {
		url : paramUrl,
		page : 1
	}).trigger("reloadGrid");
});

// 角色权限设置
$('#save-auth-btn').on('click', function() {
	var values = [];
	$('#roleTree').find("input[name!='all']:checked").each(function() {
		values.push($(this).val());
	});

	// alert(values.join(','));
	// alert($('#roleTree').find("input[name='roleId']").val());

	var roleId = $('#roleTree').find("input[name='roleId']").val();

	$.ajaxSubmit(update_role_res_url, {
		'roleId' : roleId,
		'resourceIdAndOperationKey' : values.join(',')
	}, function(data) {
		if (data.success == true) {

			$('#role-auth-form').modal2('hide');
			// $('#addnewrole-form').modal2('hide');
			// $('#role-table').trigger("reloadGrid");

			// resetForm($('#addnewrole-form').find('form'));
		} else {
			Q_Alert_Fail(data.message);
		}
	});

});

/*******************************************************************************
 * role tree
 */
/*
 * 
 */
function buildTree(tree) {
	var root = $('<ol class="dd-list"></ol>');

	$.each(tree, function(n, obj) {
		var item = $('<li class=\"dd-item\"></li>');

		item.append('<div class=\"dd2-content\">' + obj.text + '</div>');

		if (obj.leaf == false) {
			item.append(buildChild(obj.children));
		}

		root.append(item);
	});

	return root;
};

function buildChild(children) {
	var list = $('<ol class="dd-list"></ol>');
	var items = '';

	$.each(children, function(n, obj) {

		if (obj.leaf == false) {
			list.append(buildItem(obj));
		} else {
			items += buildLeaf(obj);
		}
	});

	if (items.length > 0) {
		// alert('items.length=' + items.length);

		// 加入全选按钮

		items = "<div class=\"checkbox\">"
				+ "<label class=\"btn-sm\">"
				+ "<input name=\"all\" type=\"checkbox\" class=\"ace ace-checkbox-2\" />"
				+ "<span class=\"lbl\"><strong > 全选</strong>&nbsp;&nbsp;</span>"
				+ "</label>" + "</div>" +

				items;

		// 将item放入li中
		items = '<li class=\"dd-item\">' + '<div class=\"dd2-content\">'
				+ '<div class=\"form-inline\">' + items + '</div>' + '</div>'
				+ '</li>';

		// alert(items);

		list.append($(items));
	}

	return list;
};

function buildItem(child) {
	// var itemRow = $();
	var item = $('<li class=\"dd-item\"></li>');

	item.append('<div class=\"dd2-content\">' + child.text + '</div>');
	if (child.leaf == false) {
		item.append(buildChild(child.children));
	}

	return item;
};

function buildLeaf(child) {

	var checked = '';
	if (child.checked == true) {
		checked = 'checked=\'true\'';
	}

	return "<div class=\"checkbox\">" + "<label class=\"btn-sm\">"
			+ "<input type=\"checkbox\" " + checked + " value=\"" + child.id
			+ "\" class=\"ace ace-checkbox-2\" />" + "<span class=\"lbl\"> "
			+ child.text + "&nbsp;&nbsp;&nbsp;</span>" + "</label>" + "</div>";

};
function bindCheckAll(table) {
	var checkAll = table.find("input[name='all']");
	// alert(checkAll.length);

	checkAll.bind('click', function() {
		// alert('check');
		var that = this;

		$(this).closest('.dd-item').find("input:checkbox[name!='all']").each(
				function() {
					this.checked = that.checked;
				});

	});
};
