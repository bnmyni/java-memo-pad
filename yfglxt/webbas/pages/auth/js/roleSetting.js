var basePath = ctxPaths;
var list_role_res_Url = basePath + '/role/listRoleResource.ajax';
var update_role_res_url = basePath + '/role/updateRoleResource.ajax';
var role_info_url = basePath + '/role/findRole.ajax';
var roleId = getP('roleId');
$(function() {
	getRole(roleId);
	function getRole(id) {
		$.ajaxSubmit(role_info_url, {
			'roleId' : id
		}, function(data) {
			if (data.success) {
				$.dataInput($('.form-control-static'), data.data);
				editRoleAuth(id);
			} else {
				Q_Alert_Fail(data.message);
			}
		});

	}
	;
	function editRoleAuth(roleId) {
		// alert(roleId + ' ');

		$('#roleTree').empty();

		$('#roleTree')
				.append(
						$('<input name="roleId" type="hidden" value="' + roleId
								+ '">'));

		$.ajaxSubmit(list_role_res_Url, {
			'roleId' : roleId
		}, function(data) {

			if (data.success == true) {

				var tree = $('<div  class=\"dd\"></div>');

				tree.append(buildTree(data.data));

				tree.nestable();

				tree.nestable('expandAll');

				bindCheckAll(tree);

				$('#roleTree').append(tree);
				$('button[data-action]').bind('click', function() {
					setTimeout(function() {
						ifmAutoHeight();
					}, 500);
				});
			} else {
				Q_Alert_Fail('error');
			}

		});
	}
	;

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
				Q_Alert('保存成功', function() {
					$('#backBtn').trigger('click');
				});
				// $('#role-auth-form').modal2('hide');
				// $('#addnewrole-form').modal2('hide');
				// $('#role-table').trigger("reloadGrid");

				// resetForm($('#addnewrole-form').find('form'));
			} else {
				Q_Alert_Fail(data.message);
			}
		});

	});
	$('#backBtn').on('click', function() {
		location.href = document.referrer;
	});
	/***************************************************************************
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
	}
	;

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

			items = "<div class=\"checkbox\" style=\"display:inline\">"
					+ "<label class=\"btn-sm\">"
					+ "<input name=\"all\" type=\"checkbox\" class=\"ace ace-checkbox-2\" />"
					+ "<span class=\"lbl\"><strong > 全选</strong>&nbsp;&nbsp;</span>"
					+ "</label>" + "</div>" +

					items;

			// 将item放入li中
			items = '<li class=\"dd-item\">' + '<div class=\"dd2-content\">'
					+ '<div class=\"form-inline\">' + items + '</div>'
					+ '</div>' + '</li>';

			// alert(items);

			list.append($(items));
		}

		return list;
	}
	;

	function buildItem(child) {
		// var itemRow = $();
		var item = $('<li class=\"dd-item\"></li>');

		item.append('<div class=\"dd2-content\">' + child.text + '</div>');
		if (child.leaf == false) {
			item.append(buildChild(child.children));
		}

		return item;
	}
	;

	function buildLeaf(child) {

		var checked = '';
		if (child.checked == true) {
			checked = 'checked=\'true\'';
		}

		return "<div class=\"checkbox\" style=\"display:inline\">"
				+ "<label class=\"btn-sm\">" + "<input type=\"checkbox\" "
				+ checked + " value=\"" + child.id
				+ "\" class=\"ace ace-checkbox-2\" />"
				+ "<span class=\"lbl\"> " + child.text
				+ "&nbsp;&nbsp;&nbsp;</span>" + "</label>" + "</div>";

	}
	;

	function bindCheckAll(table) {
		var checkAll = table.find("input[name='all']");
		// alert(checkAll.length);

		checkAll.bind('click', function() {
			// alert('check');
			var that = this;

			$(this).closest('.dd-item').find("input:checkbox[name!='all']")
					.each(function() {
						this.checked = that.checked;
					});

		});

	}
	;
	/***************************************************************************
	 * end role tree
	 */

});
