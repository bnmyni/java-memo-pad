var basePath = ctxPaths;
var list_announce_Url = basePath + '/announce/listannounces.ajax';
var list_announce_res_Url = basePath + '/announce/listannounceResource.ajax';
var update_announce_res_url = basePath + '/announce/updateannounceResource.ajax';
var delete_announce_url = basePath + '/announce/deleteannounce.ajax';
var update_announce_url = basePath + '/announce/updateannounce.ajax';
var announce_info_url = basePath + '/announce/findannounce.ajax';
var check_announcekey_url = basePath + '/announce/checkannounceKey.ajax?announceId=';
var grid_selector = "#announce-table";
var pager_selector = "#grid-pager";
jQuery(function($) {

	/*jqGrid_init($(grid_selector), pager_selector, {
		url : list_announce_Url,
		sortable : true,
		sortname : 'announceKey',
		sortorder : 'desc',
		colNames : [ '角色ID', '角色助记码', '角色名称', '角色描述', '' ],
		colModel : [ {
			name : 'announceId',
			index : 'announceId',
			sortable : false,
			width : 50,
			hidden : true
		}, {
			name : 'announceKey',
			index : 'announceKey',
			sortable : true,
			sortname : 'announce_key',
			width : 50
		}, {
			name : 'announceName',
			index : 'announceName',
			sortable : true,
			sortname : 'announce_name',
			width : 100
		}, {
			name : 'announceDesc',
			index : 'announceDesc',
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
	});*/
	function actionButtons(cellvalue, options, rowObject) {
		var html = '<div >';

		if (rowObject.announceKey != '1001' && rowObject.announceKey != '1002'
				&& rowObject.announceKey != '1003') {

			html += '<button style="display:none"  permCheck="auth_admin_sys_announce_announceManage,MODIFY,hide" onclick=\"editEvent('
					+ rowObject.announceId
					+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >'
					+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
					+ '</button>'
					+ '<button style="display:none"  permCheck="auth_admin_sys_announce_announceManage,DELETE,hide" onclick=\"deleteEvent('
					+ rowObject.announceId
					+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >'
					+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
					+ '</button>';
		}
		html += '<button style="display:none"  permCheck="auth_admin_sys_announce_announceManage,AUTHannounce,hide" onclick=\"editannounceAuth('
				+ rowObject.announceId
				+ ')\" class=\"btn btn-xs btn-success\" data-rel=\"tooltip\" title=\"权限设置\" >'
				+ '<i class=\"ace-icon fa fa-cogs bigger-120\"></i>'
				+ '</button>' + '</div>';

		return html;
	}

});

function editEvent(id) {
	// alert(id);
	// window.location = 'activities-add.html?activityId=' + id;

	$.ajaxSubmit(announce_info_url, {
		'announceId' : id
	}, function(data) {
		if (data.success == true) {
			resetForm($('#addnewannounce-form').find('form'), add_validator);
			$('#addnewannounce-form').find("input[name='announceId']").val(id);
			$("#announceKey").rules("remove");
			$("#announceKey").rules("add", {
				required : true,
				remote : check_announcekey_url + $('#announceId').val(),
				maxlength : 64
			});
			$('#addnewannounce-form').find('form').json2Form2(data.data);
			$('#addnewannounce-form').modal2({
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
			$.ajaxSubmit(delete_announce_url, {
				'announceId' : id
			}, function(data) {
				$('#announce-table').jqGrid('setGridParam', {
					page : 1
				}).trigger("reloadGrid");
			});
		}
	});
};

function editannounceAuth(announceId) {
	// alert(announceId + ' ');
	window.location.replace(ctxPaths + '/pages/auth/announceSetting.shtml?announceId='
			+ announceId + "&r=" + (new Date()).getTime());
	return;
	$('#announceTree').empty();

	$('#announceTree').append(
			$('<input name="announceId" type="hidden" value="' + announceId + '">'));

	$.ajaxSubmit(list_announce_res_Url, {
		'announceId' : announceId
	}, function(data) {

		if (data.success == true) {

			var tree = $('<div  class=\"dd\"></div>');

			tree.append(buildTree(data.data));

			tree.nestable();

			tree.nestable('collapseAll');

			bindCheckAll(tree);

			$('#announceTree').append(tree);
			var rows = $(grid_selector).getRowData(), ind = 0;
			for (var i = 0; i < rows.length; i++) {
				if (rows[i]['announceId'] == announceId) {
					ind = i;
					break;
				}
			}
			var announceName = $(grid_selector).getCell(ind + 1, 'announceName');
			$('#announce-auth-form-announcename').html(announceName);
			$('#announce-auth-form').modal2({
				backdrop : "static",
				show : true
			});
		} else {
			Q_Alert_Fail('error');
		}

	});
};
/*add_validator = $('#add-announce-form').validate({
	rules : {
		'announceKey' : {
			required : true,
			remote : check_announcekey_url,
			maxlength : 64
		},
		'announceName' : {
			required : true,
			maxlength : 100
		},
		'announceDesc' : {
			required : false,
			maxlength : 400
		}
	},
	messages : {
		'announceKey' : {
			remote : '角色助记码已存在',
		}
	},
	submitHandler : function(form) {
		$.ajaxSubmit(update_announce_url, $(form).serializeArray(), function(data) {
			if (data.success == true) {
				$('#addnewannounce-form').modal2('hide');
				$('#announce-table').trigger("reloadGrid");

				resetForm($('#addnewannounce-form').find('form'));
			} else {
				Q_Alert_Fail(data.message);
			}
		});
		return false;
	}
});
*/
$('#addBtn').on('click', function() {
	//$("#announceKey").rules("remove");
	/*$("#announceKey").rules("add", {
		required : true,
		remote : check_announcekey_url,
		maxlength : 64
	});*/
	//resetForm($('#addnewannounce-form').find('form'), add_validator);
	$('#announceId').val('');
	$('#addnewannounce-form').modal2({
		backdrop : "static",
		show : true
	});
});
$('#editBtn').on('click', function() {
	//$("#announceKey").rules("remove");
	/*$("#announceKey").rules("add", {
		required : true,
		remote : check_announcekey_url,
		maxlength : 64
	});*/
	//resetForm($('#addnewannounce-form').find('form'), add_validator);
	//$('#announceId').val('');
	$('#addeditannounce-form').modal2({
		backdrop : "static",
		show : true
	});
});
$('#delBtn').on('click', function() {
	confirm("are you sure?");
});

$('#seachBtn').on('click', function() {
	var paramUrl = list_announce_Url;
	if ($('#searchText').val() != '') {
		paramUrl += '?params[\'announceName\']=' + $('#searchText').val();
	}
	jQuery('#announce-table').jqGrid('setGridParam', {
		url : paramUrl,
		page : 1
	}).trigger("reloadGrid");
});

// 角色权限设置
$('#save-auth-btn').on('click', function() {
	var values = [];
	$('#announceTree').find("input[name!='all']:checked").each(function() {
		values.push($(this).val());
	});

	// alert(values.join(','));
	// alert($('#announceTree').find("input[name='announceId']").val());

	var announceId = $('#announceTree').find("input[name='announceId']").val();

	$.ajaxSubmit(update_announce_res_url, {
		'announceId' : announceId,
		'resourceIdAndOperationKey' : values.join(',')
	}, function(data) {
		if (data.success == true) {

			$('#announce-auth-form').modal2('hide');
			// $('#addnewannounce-form').modal2('hide');
			// $('#announce-table').trigger("reloadGrid");

			// resetForm($('#addnewannounce-form').find('form'));
		} else {
			Q_Alert_Fail(data.message);
		}
	});

});

/*******************************************************************************
 * announce tree
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
