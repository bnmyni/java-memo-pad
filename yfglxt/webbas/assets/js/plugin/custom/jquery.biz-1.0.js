/** 
 * @fileoverview  卓望数码 jQuery Common Library
 * @description:封装一些系统公用模块
 * @author oCEAn Zhuang (zhuangruhai@aspirecn.com QQ: 153414843)
 * @version 1.0
 * @date 2013-10-30
 */

/**
 * 统一弹出框
 * @param {Object} $
 */
(function($) {
	$.alerts = {
		verticalOffset: -75, // vertical offset of the dialog from center screen, in pixels
		horizontalOffset: 0, // horizontal offset of the dialog from center screen, in pixels/
		repositionOnResize: true, // re-centers the dialog on window resize
		overlayOpacity: .01, // transparency level of overlay
		overlayColor: '#666666', // base color of overlay
		draggable: false, // make the dialogs draggable (requires UI Draggables plugin)
		okButton: '&nbsp;确认&nbsp;', // text for the OK button
		cancelButton: '&nbsp;取消&nbsp;', // text for the Cancel button
		dialogClass: null, // if specified, this class will be applied to all dialogs

		// Public methods

		alert: function(message, title, callback) {
			if (title == null) title = 'Alert';
			$.alerts._show(title, message, null, 'alert', function(result) {
				if (callback) callback(result);
			});
		},

		confirm: function(message, title, callback) {
			if (title == null) title = 'Confirm';
			$.alerts._show(title, message, null, 'confirm', function(result) {
				if (callback) callback(result);
			});
		},

		prompt: function(message, value, title, callback) {
			if (title == null) title = 'Prompt';
			$.alerts._show(title, message, value, 'prompt', function(result) {
				if (callback) callback(result);
			});
		},

		// Private methods

		_show: function(title, msg, value, type, callback) {

			$.alerts._hide();
			$.alerts._overlay('show');

			$("BODY").append(
				'<div class="popwindow" id="popup_container">' + '<div class="popwintop">' + '<div class="poptitle" id="popup_title">我是标题</div>' + '<div class="popwinright">' + '<div class="closefenge"></div>' + '<div class="daibanright" id="popwindowclose"></div>' + '</div></div>' + '<div  class="popwinframe" id="popup_message">' + '</div></div>');

			if ($.alerts.dialogClass) $("#popup_container").addClass($.alerts.dialogClass);

			// IE6 Fix
			var pos = ($.browser.msie && parseInt($.browser.version) <= 6) ? 'absolute' : 'fixed';

			$("#popup_container").css({
				position: pos,
				zIndex: 99999,
				padding: 0,
				margin: 0
			});

			$("#popup_title").text(title);
			//$("#popup_content").addClass(type);
			//$("#popup_message").text(msg);
			//$("#popup_message").html($("#popup_message").text().replace(/\n/g, '<br />'));

			$("#popup_container").css({
				minWidth: $("#popup_container").outerWidth(),
				maxWidth: $("#popup_container").outerWidth()
			});

			$.alerts._reposition();
			$.alerts._maintainPosition(true);
			$("#popwindowclose").click(function() {
				$.alerts._hide();
				if (callback) {
					callback();
				};
			});
			$('#popup_message').empty();
			switch (type) {
				case 'alert':
					$('<table width="90%" border="0" cellpadding="0" cellspacing="0" class="pop_insert_table"><tr><td class="pop_center">' + msg + '</td></tr></table>').appendTo('#popup_message');
					/**$("#popup_ok").focus().keypress(function(e) {
						if (e.keyCode == 13 || e.keyCode == 27) $("#popup_ok").trigger('click');
					});**/
					break;
				case 'confirm':
					$('<table width="90%" border="0" cellpadding="0" cellspacing="0" class="pop_insert_table"><tr class="rizhiline"><td class="pop_center">' + msg + '</td></tr><tr><td style="padding-top:10px;"><p align="center"><input type="button" id="popup_ok" value="确认" class="bluebtn1" />&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="popup_cancel" value="取消" class="bluebtn1" /></p></td></tr></table>').appendTo('#popup_message');
					$("#popup_ok").click(function() {
						$.alerts._hide();
						if (callback) callback(true);
					});
					$("#popup_cancel").click(function() {
						$.alerts._hide();
						if (callback) callback(false);
					});
					$("#popup_ok").focus();
					$("#popup_ok, #popup_cancel").keypress(function(e) {
						if (e.keyCode == 13) $("#popup_ok").trigger('click');
						if (e.keyCode == 27) $("#popup_cancel").trigger('click');
					});
					break;
				case 'prompt':

					$('<table width="90%" border="0" cellpadding="0" cellspacing="0" class="pop_insert_table"><tr><td class="pop_center1"><p><img src="' + (value == 1 ? 'images/pop_right.png' : 'images/pop_wrong.png') + '" width="70" height="70" border="0"/></p><p style="line-height:50px">' + msg + '</p></td></tr></table>').appendTo('#popup_message');
					/**$("#popup_ok").click(function() {
						var val = $("#popup_prompt").val();
						$.alerts._hide();
						if (callback) callback(val);
					});
					$("#popup_cancel").click(function() {
						$.alerts._hide();
						if (callback) callback(null);
					});
					$("#popup_prompt, #popup_ok, #popup_cancel").keypress(function(e) {
						if (e.keyCode == 13) $("#popup_ok").trigger('click');
						if (e.keyCode == 27) $("#popup_cancel").trigger('click');
					});
					if (value) $("#popup_prompt").val(value);
					$("#popup_prompt").focus().select();**/
					break;
			}
			$("#popup_container").fadeIn(200);

		},

		_hide: function() {
			$("#popup_container").remove();
			$.alerts._overlay('hide');
			$.alerts._maintainPosition(false);
		},

		_overlay: function(status) {
			switch (status) {
				case 'show':
					$.alerts._overlay('hide');
					$("BODY").append('<div id="popup_overlay"></div>');
					$("#popup_overlay").css({
						background: '#666666',
						filter: 'alpha(opacity=60)',
						'-moz-opacity': 0.6,
						opacity: 0.6,
						position: 'absolute',
						left: 0,
						top: 0,
						width: '100%',
						height: $(document).height(),
						'z-index': 1001
					});
					break;
				case 'hide':
					$("#popup_overlay").remove();
					$("#popup_container").fadeOut(200);
					break;
			}
		},

		_reposition: function() {
			//var top = (($(window).height() / 2) - ($("#popup_container").outerHeight() / 2)) + $.alerts.verticalOffset;
			//var left = (($(window).width() / 2) - ($("#popup_container").outerWidth() / 2)) + $.alerts.horizontalOffset;
			var left = (Math.max($(document).width(), $(window).width()) - $("#popup_container").outerWidth()) / 2;
			var top = $(window).scrollTop() + $(window).height() / 9;
			if (top < 0) top = 0;
			if (left < 0) left = 0;

			// IE6 fix
			if ($.browser.msie && parseInt($.browser.version) <= 6) top = top + $(window).scrollTop();

			$("#popup_container").css({
				top: top + 'px',
				left: left + 'px',
				position: 'absolute'
			});
			$("#popup_overlay").height($(document).height());
		},

		_maintainPosition: function(status) {
			if ($.alerts.repositionOnResize) {
				switch (status) {
					case true:
						$(window).bind('resize', function() {
							$.alerts._reposition();
						});
						break;
					case false:
						$(window).unbind('resize');
						break;
				}
			}
		}

	}
	// 快捷方式
	Q_Alert = function(message, callback) {

		$.alerts.alert(message, "提示", callback);
	}
	Q_Confirm = function(message, callback) {
		$.alerts.confirm(message, "提示", callback);
	}
	Q_Prompt = function(message, value, callback) {
		$.alerts.prompt(message, value, "提示", callback);
	}
	Q_Prompt_SUCC = function(message, callback) {
		$.alerts.prompt(message, 1, "提示", callback);
	}
	Q_Prompt_FAIL = function(message, callback) {
		$.alerts.prompt(message, 0, "提示", callback);
	}
})(jQuery);

/**
 * 所有附件上传文本框绑定change事件，当选择文件后失去焦点后会重新校验
 * add by lipeng
 */
$().ready(function() {
	$("input[type=file]").each(function(){
		$(this).live('change',function(){
			$(this).blur();
		});
	});
});

/**
 * 初始化列表，选项为最近10年。
 *
 **/
var initYearSelect = function(id) {
	var s = document.getElementById(id),
		size = 10,
		d = new Date(),
		year = d.getFullYear();
	s.length = 0;
	for (var i = size; i >= 0; i--) {
		s.options[s.length] = new Option((year - (size - i)) + '年', (year - (size - i)));
	}

};
/**
 * 提供静态方法初始化业务相关的列表。
 *
 **/
var SelectObj = {
	rendereSelect: function(id, datas) {
		var s = null;
		if (typeof(id) == 'string') {
			s = document.getElementById(id);
		} else if (typeof(id) == 'object') {
			s = id;
		}
		var len = datas.length;
		s.length = 0;
		for (var i = 0; i < len; i++) {
			s.options[s.length] = new Option(datas[i].text, datas[i].value);
		}
	},
	initApprovalType: function(sid) { //报批类型
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '报批类型1'
		}, {
			value: '2',
			text: '报批类型2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductType: function(sid) { //产品类型
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '产品类型1'
		}, {
			value: '2',
			text: '产品类型2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductSort: function(sid) { //产品分类
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '产品分类1'
		}, {
			value: '2',
			text: '产品分类2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductStatus: function(sid) { //产品状态
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '产品状态1'
		}, {
			value: '2',
			text: '产品状态2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductSupportProvince: function(sid) { //产品支撑省
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '产品支撑省1'
		}, {
			value: '2',
			text: '产品支撑省2'
		}];
		this.rendereSelect(sid, arr);
	},
	initTargetCustomer: function(sid) { //目标客户
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '目标客户1'
		}, {
			value: '2',
			text: '目标客户2'
		}];
		this.rendereSelect(sid, arr);
	},
	initCustomerService: function(sid) { //客户服务
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '客户服务1'
		}, {
			value: '2',
			text: '客户服务2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductAttr: function(sid) { //产品属性
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '产品属性1'
		}, {
			value: '2',
			text: '产品属性2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductImportant: function(sid) { //重要性
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '重要性1'
		}, {
			value: '2',
			text: '重要性2'
		}];
		this.rendereSelect(sid, arr);
	},
	initPartner: function(sid) { //合作方
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '合作方1'
		}, {
			value: '2',
			text: '合作方2'
		}];
		this.rendereSelect(sid, arr);
	},
	initModeOfOperation: function(sid) { //运营方式
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '运营方式1'
		}, {
			value: '2',
			text: '运营方式2'
		}];
		this.rendereSelect(sid, arr);
	},
	initChargingCntrolMnagement: function(sid) { //计费控制管理
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '计费控制管理1'
		}, {
			value: '2',
			text: '计费控制管理2'
		}];
		this.rendereSelect(sid, arr);
	},
	initUserDataAndOrderRelations: function(sid) { //用户数据及订购关系说明
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '用户数据及订购关系说明1'
		}, {
			value: '2',
			text: '用户数据及订购关系说明2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProductStatus: function(sid) { //用户数据及订购关系说明
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '待受理'
		}, {
			value: '2',
			text: '待初审'
		}, {
			value: '1',
			text: '待复审'
		}, {
			value: '2',
			text: '初审打回'
		}, {
			value: '1',
			text: '复审打回'
		}];
		this.rendereSelect(sid, arr);
	},
	initFeedbackChannel: function(sid) { //用户数据及订购关系说明
		var arr = [{
			value: '',
			text: '全部'
		}, {
			value: '1',
			text: '反馈渠道1'
		}, {
			value: '2',
			text: '反馈渠道2'
		}];
		this.rendereSelect(sid, arr);
	},
	initProduct: function(sid) { //用户数据及订购关系说明
		$("#productName").ajaxRender("test/requirement/productName.json", {
			keyValue: {
				text: 'productName',
				value: 'productCode'
			}
		});
	},
	initProductAndSubProduct: function(productId, subProductId) { //用户数据及订购关系说明
		var productObj = productId,
			subProductObj = subProductId;
		if (typeof productId == 'string') {
			productObj = $('#' + productId);
		}
		if (typeof subProductId == 'string') {
			subProductObj = $('#' + subProductId);
		}
		/**productObj.bind('focus', function() {
			if (subProductObj) {
				subProductObj.length = 0;
				subProductObj.options[subProductObj.length] = new Option('--请选择--', '');
			}
		})**/
		productObj.bind('change',function(){
			if($.browser.mozilla){
				alert($(this).attr('_pid'));
				if ($(this).attr('_pid')){
					$(this).attr('pid',$(this).attr('_pid'));
					$(this).attr('_pid','');
				}else{
					$(this).attr('pid','');
					subProductObj.empty();
					subProductObj.append('<option value="">--请选择--</option>');
					subProductObj.trigger('blur');
				}
				
			}else{
				subProductObj.empty();
				subProductObj.append('<option value="">--请选择--</option>');
				subProductObj.trigger('blur');
			}
			
		});
		productObj.autocomplete({
			minLength: 0,
			//source: [ "c++", "java", "php", "coldfusion", "javascript", "asp", "ruby" ],
			source: function(request, response) {
				$.ajax({
					url: "test/requirement/productName.json",
					dataType: "json",
					data: {
						featureClass: "P",
						style: "full",
						maxRows: 12,
						name_startsWith: request.term
					},
					success: function(data) {
						response($.map(data, function(item) {
							return {
								label: item.productName,
								value: item.productCode
							}
						}));
					}
				});
			},
			select: function(event, ui) {
				$(this).val(ui.item.label);
				$(this).attr('pid',ui.item.value);
				$(this).attr('_pid',ui.item.value);
				if (typeof subProductObj != 'undefined') {
					var pid = $(this).attr('pid');
					if (pid){
						subProductObj.ajaxRender("test/requirement/subProductName.json?pid=" + pid, {
							firstOption: {
								text: '--请选择--',
								value: ''
							},
							keyValue: {
								text: 'productName',
								value: 'productCode'
							}
						});
					}else{
						subProductObj.empty();
						subProductObj.append('<option value="">--请选择--</option>');
					}
					//subProductObj.trigger('blur');
				}
				return false;
			}
			
		});
		/**productObj.autocomplete({
			url:"test/requirement/productName2.json",
			minChars: 0,
			onNoMatch: true,
			autoFill: true,
			showResult: function(value, data) {
				return '<span style="color:red">' + value + '</span>';
			},
			onItemSelect: function(item) {
				productObj.attr('pid',item.value);
				subProductObj.ajaxRender("test/requirement/subProductName.json",{firstOption:{text:'--请选择--',value:''},keyValue:{text:'productName',value:'productCode'}},{"suName":"1111"});
			}
			//data:[ ['apple', 1], ['apricot', 2], ['pear', 3], ['prume', 4], ['飞翔', 5], ['飞信', 6]]
		});**/
		/**productObj.bind('blur', function() {
			subProductObj.ajaxRender("test/requirement/subProductName.json", {
				firstOption: {
					text: '--请选择--',
					value: ''
				},
				keyValue: {
					text: 'productName',
					value: 'productCode'
				}
			}, {
				"suName": "1111"
			});
		})**/
		/**productObj.autocomplete({ 
			source: function(request, response) {  
            $.ajax({  
                url: "test/requirement/productName3.json",  
                data: {  
                    name: request.term  
                },  
                dataType: "json",  
                success: function(data, textStatus, jqXHR) {  
                    response($.map(data, function(item, index) {  
                        return item.label;  
                    }));  
                },
			minLength: 0
            });  
        }
		});**/
		/**productObj.autocomplete({
			
			url:'test/requirement/productName3.json',
			showResult: function(value, data) {
				return value.label;
			},
			onItemSelect: function(item) {
				
			}
		});**/
		/**
		productObj.ajaxRender("test/requirement/productName.json",{
			keyValue:{text:'productName',value:'productCode'}
		 });
		productObj.bind("change",function(){
			subProductObj.ajaxRender("test/requirement/subProductName.json",{keyValue:{text:'productName',value:'productCode'}},{"suName":"1111"});
		});
		/**$('#' + productId).bind("change",function(){
			$('#' + subProductId).ajaxRender("test/requirement/subProductName.json",{keyValue:{text:'productName',value:'productCode'}},{"suName":"1111"});
		});**/
	},
	initRequirementType: function(sid) { //用户数据及订购关系说明
		var obj = typeof(sid) == 'string' ? $('#' + sid) : sid;
		obj.ajaxRender("test/requirement/reqType.json", {
			firstOption: {
				text: '--请选择--',
				value: ''
			},
			keyValue: {
				text: 'reqTypeDesc',
				value: 'reqTypeCode'
			}
		});
	},
	initRequirementSource: function(sid) { //用户数据及订购关系说明
		var obj = typeof(sid) == 'string' ? $('#' + sid) : sid;
		obj.ajaxRender("test/requirement/reqSource.json", {
			firstOption: {
				text: '--请选择--',
				value: ''
			},
			keyValue: {
				text: 'reqSourceDesc',
				value: 'reqSourceCode'
			}
		});
	}
}
/**
 * 弹出标签式的列表选项。
 *
 **/
var TagSelectObj = function(contenterId) {
	this._contenterId = contenterId;
}
TagSelectObj.prototype = {
	_options: {
		btnName: '请选择',
		url: ''
	},
	/**_getSelectedTags: function() {
		var ids = [];
		var choiceitems = $('#' + this._contenterId).children('div:eq(1) .choiceitem');
		for (var j = 0; j < choiceitems.length; j++) {
			ids.push(choiceitems[j].attr('tagId'));
		}
	},**/
	getSelectedTags: function() {
		var result = [],
			tags = $('#' + this._contenterId + ' div.choice .item');
		$.each(tags, function() {
			var tagid = $(this).attr('tagid');
			result.push(tagid);
		});
		return result.join(',');
	},
	getTagHTML: function(contenterId, id, text) {
		return '<div class="item" id="' + contenterId + id + '" tagId="' + id + '"><div class="choiceitem">' + text + '</div><div class="closeitem1"> <a href="javascript:;" onclick="javascript:$(this).parent(\'div\').parent(\'div\').fadeRemove();$(\'#\' + $(this).parent(\'div\').parent(\'div\').attr(\'id\') + \'_cb\').attr(\'checked\',false);"></a> </div></div>';
	},
	init: function(opt) {
		var _this = this;
		$.extend(_this._options, opt);
		var divId = _this._contenterId;
		$(' <div style="float:left;"><input width="81px" type="button" style="width:81px;height:25px; line-height:25px;" class="bluebtn" value="' + _this._options.btnName + '"></div></br><div class="choice"></div><div style="clear:both"></div><div  class="popwindow" style="z-index:998;position:absolute;" id="popwindow_' + divId + '"><div class="popwintop"> 				<div class="poptitle">请选择</div> 				<div class="popwinright"> 					<div class="closefenge"></div><a class="daibanright" href="javascript:;" onclick="javascript:$(\'#popwindow_' + divId + '\').hide(200);"></a> </div></div><div class="popwinframe"></div>').appendTo('#' + this._contenterId);
		//$('<div></div><br /><div> <input type="button" width="81px" value="选择" class="bluebtn" style="width:81px;height:25px; line-height:25px;" /></div><div style="clear:both"></div><div  class="popwindow" style="z-index:998" id="popwindow_'+divId + '"><div class="popwintop"> 				<div class="poptitle">请选择</div> 				<div class="popwinright"> 					<div class="closefenge"></div><a class="daibanright" href="javascript:;" onclick="javascript:$(\'#popwindow_'+divId + '\').hide(500);"></a> </div></div><div class="popwinframe"></div>').appendTo('#' + this._contenterId);
		$('#' + divId + ' div:eq(0) :button').bind('click', function() {
			$('#popwindow_' + divId).show();
			if ($('#popwindow_' + divId).children('div.popwinframe').html() == '') {
				$.ajaxSubmit(_this._options.url, {}, function(rtn) {
					if (rtn.success) {
						var datas = rtn.data;
						var tmp = ['<div class="choice_list"><input type="checkbox" name="checkBoxAll" id="checkboxall_' + divId + '"/> 全选</div>'];
						for (var i = 0; i < datas.length; i++) {
							var width = 15;
							if (datas[i].text.length > 3 && datas[i].text.length < 8) {
								width = 30;
							} else if (datas[i].text.length >= 8) {
								width = 35;
							}
							tmp.push('<div class="choice_list" style="width:' + width + '%"><input type="checkbox" id="' + divId + datas[i].id + '_cb" name="checkbox_' + divId + '" value="' + datas[i].id + '"/> ' + datas[i].text + '</div>');
						}
						tmp.push('<div style="clear:both"></div>')
						$('#popwindow_' + divId).children('div.popwinframe').append(tmp.join(''));

						$('input[name=checkbox_' + divId + ']').bind('click', function() {
							var isCheck = $(this).attr('checked') || false;
							if (isCheck) {
								$('#' + divId + ' .choice').append(_this.getTagHTML(divId, $(this).val(), $(this).parent().text()));
								//$('<div class="choiceitem" id="' + divId + $(this).val() + '" tagId="'+ $(this).val() + '">' + $(this).parent().text() + '<div class="closeitem" style="position:relative;left:50px;"><a href="javascript:;" onclick="javascript:$(this).parent(\'div\').parent(\'div\').fadeRemove();$(\'#\' + $(this).parent(\'div\').parent(\'div\').attr(\'id\') + \'_cb\').attr(\'checked\',false);"><img src="images/closeitem.png" width="22" height="22" border="0" /></a></div></div>').appendTo(('#' + divId +' div:eq(0)'));
							} else {
								$('#' + divId + $(this).val()).fadeRemove();
							}
						});
						$.checkbox_chkAll('checkboxall_' + divId, 'checkbox_' + divId, function(allObj, selObjs) {
							var isCheck = allObj.attr('checked') || false;
							if (isCheck) {
								$.each(selObjs, function() {
									$('#' + divId + ' .choice').append(_this.getTagHTML(divId, $(this).val(), $(this).parent().text()));
									//$('<div class="choiceitem" id="' + divId + $(this).val() + '" tagId="' + $(this).val() + '">' + $(this).parent().text() + '<div class="closeitem" style="position:relative;left:50px;"><a href="javascript:;" onclick="javascript:$(this).parent(\'div\').parent(\'div\').fadeRemove();$(\'#\' + $(this).parent(\'div\').parent(\'div\').attr(\'id\') + \'_cb\').attr(\'checked\',false);"><img src="images/closeitem.png" width="22" height="22" border="0" /></a></div></div>').appendTo(('#' + divId +' div:eq(0)'));
								});
							} else {
								$('#' + divId + ' .choice').empty();
							}
						});
					}
				});
			}

		});
	}
}
/**
 * 初始化列表，选项为12月份。
 *
 **/
var initMonthSelect = function(id) {
	var s = document.getElementById(id),
		m = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
		size = m.length;
	s.length = 0;
	for (var i = 0; i < size; i++) {
		s.options[s.length] = new Option(m[i] + '月', m[i]);
	}

}

/**
 * 对Pluploader附件上传插件按照业务需求做简单封装。
 * 初始化:PlUploaderObj.init('uploadFileDiv');
 * 获取上传成功的附件ID,多个以','隔开,PlUploaderObj.getSucc();
 **/
var PlUploaderObj = function(contenterId) {
	this._contenterId = contenterId;
	this._uploadId = 'uploading_' + this._contenterId;
	this._count = 0;
	this._uploadFileBtnId = 'uploadFileBtn';
	this._attachFileIds = [];
	this._add_attachFileIds = [];
	this._del_attachFileIds = [];
	this._attachGroupId = '';
	this._hiddenId = '';
	this._uploadFinishedId = '';
	this._isInit = false;
	this.uploader = null;
	this._options = {
		btnName: '上传附件',
		btnWidth : '',
		max_file_size: '3mb',
		max_file: 1,
		addUrl: BIZCTX_PATH + '/attachment!add.action',
		mutilAddUrl : BIZCTX_PATH + '/attachment!addForMulti.action',
		delUrl: BIZCTX_PATH + '/attachment!withdraw.action',
		downloadUrl: BIZCTX_PATH + '/attachment!download.action',
		viewUrl:BIZCTX_PATH + '/attachment!view.action',
		attachGroupId : '',
		disTheme: 1,
		hiddenName : '', //附件组ID
		hiddenAttachId : '',//附件ID，多个用逗号隔开，多附件该参数不能为空
		hiddenAttr :null,
		uploadFinishedName : '',
		uploadFinishedAttr : {'uploadfinishedflag':'1','datatype':'uploadFinished','uploadfinishedignore':'uploadfinishedignore'},
		attachTypeId:'',
		isView : false,
		initData: [],
		mime_types: [{
							title: "图片文件(jpg,jpeg,gif)",
							extensions: "jpg,jpeg,gif"
						}, {
							title: "Word文件(doc,docx)",
							extensions: "doc,docx"
						},{
							title: "Pdf文件(pdf)",
							extensions: "pdf"
						}, {
							title: "Rar文件(zip,rar)",
							extensions: "zip,rar"
						}, {
							title: "Excel文件(xls,xlsx)",
							extensions: "xls,xlsx"
						}, {
							title: "PowerPoint文件(ppt,pptx)",
							extensions: "ppt,pptx"
						}]
	};
}
PlUploaderObj.prototype = {
	_disableBrowse: function(flag) { //控制上传按钮启用/禁用以及按钮样式
		var _this = this;
		_this.uploader.disableBrowse(flag);
		if (flag) {
			$('#' + _this._uploadFileBtnId).removeClass('bluebtn').addClass('graybtn');
		} else {
			$('#' + _this._uploadFileBtnId).removeClass('graybtn').addClass('bluebtn');
		}
	},
	_checkMaxSize: function() { //检查是否上传的附件总数已达到最大
		var _this = this;
		if (!_this.getSucc()){
			$('#' + _this._uploadFileBtnId).parent().show();
			//document.getElementById(_this._uploadFileBtnId).style.display = 'inline';
			//$('#' + _this._uploadFileBtnId).parent().show();
			//_this._disableBrowse(false);
			//if (!_this._isInit){
				_this._initPlUpload();
				//_this._isInit = true;
			//}
			return;
		}
		if (_this.getSucc().split(',').length < _this._options.max_file) {
			$('#' + _this._uploadFileBtnId).parent().show();
			//document.getElementById(_this._uploadFileBtnId).style.display = 'inline';
			//if (!_this._isInit){
				_this._initPlUpload();
			//	_this._isInit = true;
			//}
			//$('#' + _this._uploadFileBtnId).parent().show();
			//_this._disableBrowse(false);
		} else {
			//document.getElementById(_this._uploadFileBtnId).style.display = 'none';
			$('#' + _this._uploadFileBtnId).parent().hide();
			if (_this.uploader){
			_this.uploader.destroy();
			//_this._isInit = false;
			//$('#' + _this._uploadFileBtnId).parent().hide();
			//_this._disableBrowse(true);
			}
		}
		
	},
	_bindCloseItemEvent: function() { //绑定上传的附件关闭事件
		var _this = this;
		$('#' + this._contenterId + ' .uploading .closeitem a').bind("click",function() {
			//_this.uploader.trigger('CancelUpload');
			var o = $(this).parent('div').parent('div');
			if (o.attr('attachfileid')){
				$.ajaxSubmit(_this._options.delUrl,{'attachFileId':o.attr('attachfileid')},function(_data){
					if (_data && _data.success){
						_this._del_attachFileIds.push(o.attr('attachfileid'));
						o.fadeOut(500, function() {
							o.remove();
							_this._checkMaxSize();
							_this._initHiddenName();
							_this._initHiddenUpLoadFinishedName();
						});
					}
				});
			}else{
				o.fadeOut(500, function() {
					o.remove();
					_this._checkMaxSize();
					_this._initHiddenName();
					_this._initHiddenUpLoadFinishedName();
				});
			}
			//_this.uploader.refresh();
			//$(this).parent('div').parent('div').remove();
		});
	},
	_changeParam: function(url, name, value) {
		var newUrl = "";
		var reg = new RegExp("(^|)" + name + "=([^&]*)(|$)");
		var tmp = name + "=" + value;
		if (url.match(reg) != null) {
			newUrl = url.replace(eval(reg), tmp);
		} else {
			if (url.match("[\?]")) {
				newUrl = url + "&" + tmp;
			} else {
				newUrl = url + "?" + tmp;
			}
		}
		return newUrl;
	},
	_initDatas: function() { //初始化已上传的附件信息
		var _this = this;
		var _options = _this._options;
		//if (_options.attachGroupId) {
			
			/**$.ajaxSubmit(_options.viewUrl,{'attachGroupId':_options.attachGroupId},function(_data){
				if (_data){
					for (var i = 0; i < _data.files.length; i++) {
						if (_this._options.disTheme == 2) {
							$('#' + this._contenterId).append('<div class="uploading" fileid="' + _data.files[i].fileId + '" style="width:100px;height:100px;"><img src="' + _data.files[i].fileName + '"/>' + (_this._options.isView ? '<div style="margin-top:-38px;margin-left:192px;" class="closeitem"> <a href="javascript:;"></a>':'') + ' </div></div>');
						} else {
							$('#' + this._contenterId).append('<div class="uploading" fileid="' + _data.files[i].fileId + '"><div class="uploadfile">' + _data.files[i].fileName + '</div>'+(_this._options.isView ? '<div style="margin-top:-38px;margin-left:192px;" class="closeitem"> <a href="javascript:;"></a>':'') + '</div></div>');
						}
					}
					this._bindCloseItemEvent();
				}
			});**/
			if (_options.initData.length > 0){
				for (var i = 0; i < _options.initData.length; i++) {
						$('#' + _this._contenterId).attr('attachGroupId', _options.initData[i].groupId);
						if (_options.disTheme == 2) {
							$('#' + _this._contenterId).append('<div class="uploading" attachfileid="' + _options.initData[i].fileId + '" style="width:100px;height:100px;"><div class="uploadcontent"><img src="' + _options.initData[i].fileName + '"/></div>' + (!_this._options.isView ? '<div style="margin-top:-38px;margin-left:192px;" class="closeitem"> <a href="javascript:;"></a>':'') + ' </div></div>');
						} else {
							$('#' + _this._contenterId).append('<div class="uploading" attachfileid="' + _options.initData[i].fileId + '"><div class="uploadcontent"><div class="uploadfile"><a href="'+$.appendExtraParams(_this._options.downloadUrl + "?attachFileId=" + _options.initData[i].fileId) + '" target="_blank" title="' + _options.initData[i].fileName + '">' + _options.initData[i].fileName + '</a></div></div>'+(!_this._options.isView ? '<div style="margin-top:-38px;margin-left:192px;" class="closeitem"> <a href="javascript:;"></a>':'') + '</div></div>');
						}
				 }
				_this._initHiddenName();
				_this._initHiddenUpLoadFinishedName();
				_this._bindCloseItemEvent();
				
			//}
			
		}
		_this._checkMaxSize();
	},
	_initHiddenName : function(){
		/*var name = this._options.hiddenName ? this._options.hiddenName : '';
		var id = 'hidden_' + this._contenterId;
		if (!document.getElementById(id)){
			this._hiddenId = id;
			$('#' + this._uploadFileBtnId).parent().parent().append('<input  type="hidden" '+ (this._options.hiddenName ? 'name="' + this._options.hiddenName + '"' : '') +' id="' + id + '"/>');
			if (this._options.hiddenAttr){
				for (var obj in this._options.hiddenAttr){
					$('#' + id).attr(obj,this._options.hiddenAttr[obj]);
			   }
			}
			
		}*/
		if (this._options.max_file > 1){
			$('#' + this._hiddenId).val(this.getSucc());	
			$('#' + this._attachGroupId).val(this.getGroupId());
		}else{
			if (this.getSucc()){
				$('#' + this._hiddenId).val(this.getGroupId());	
			}else{
				$('#' + this._hiddenId).val('');	
				$('#' + this._hiddenId).trigger('blur');
			}
		}
		/**if(this._options.max_file == 1){
			$('#' + id).val(this.getGroupId());	
		}else{
			$('#' + id).val(this.getGroupId() + "|" + this.getSucc());	
		}**/
		$('#' + this._contenterId).attr('attachFiles',this.getAttachFiles());
		//$('#' + this._hiddenId).trigger('blur');
	},
	_initHiddenUpLoadFinishedName : function(){
		var uploadFinishedName = this._options.uploadFinishedName ? this._options.uploadFinishedName : 'hidden_uploadFinishedName_' + this._contenterId;
		var uploadFinishedId = 'hidden_uploadFinishedId_' + this._contenterId;
		$('#' + uploadFinishedId).parent('div').remove();
		$('#' + this._uploadFileBtnId).parent().parent().append('<div><input type="hidden"'+' name="' + uploadFinishedName + '"  id="' + uploadFinishedId + '" value="uploadFinished" /></div>');
		if (this._options.uploadFinishedAttr){
				for (var obj in this._options.uploadFinishedAttr){
					$('#' + uploadFinishedId).attr(obj,this._options.uploadFinishedAttr[obj]);
			   }
			}
		$('#'+uploadFinishedId).trigger('blur');	
	},
	_addAttacheTypeParam:function(){
		var params = "attachTypeId="+this._options.attachTypeId;
		if (this._options.max_file > 1){
			params += '&attachGroupId=' + this.getGroupId() + '&attachFileIds=' + this.getSucc();
			this._uploadUrl = this._options.mutilAddUrl;
		}else{
			this._uploadUrl = this._options.addUrl;
		}
		if(this._options.addUrl.indexOf("?") == -1){
	        this._uploadUrl += "?" + params;
	    }else{
	    	this._uploadUrl += "&" + params;
	    }
	},
	_composeMimeTypes:function(){
		var _this = this;
		if(_this._options.mime_types && $.isArray(_this._options.mime_types)){//把mime_types组合到一起展示为所有文件
			var all_extensions = [];
			$.each(_this._options.mime_types, function(i,mimeTypes){
				if(mimeTypes.extensions && $.trim(mimeTypes.extensions).length > 0){
					all_extensions.push(mimeTypes.extensions);
				}
			});
			//添加到类型开头
			var all_extensionss = all_extensions.join(",");
			_this._options.mime_types.unshift({title:'所有文件（' + all_extensionss + '）',extensions:all_extensionss});
			
		}
	},
	_initPlUpload : function(){
		var _this = this;
		_this._addAttacheTypeParam();//byhaomingli
		_this.uploader = new plupload.Uploader({
			runtimes: 'gears,html5,flash,silverlight,html4',
			browse_button: _this._uploadFileBtnId, // you can pass in id...
			container: _this._contenterId, // ... or DOM Element itself
			url: _this._uploadUrl,
			multi_selection: false,
			chunk_size: '250kb',
			max_retries:0,
			//multipart:true,
			flash_swf_url: ctxPaths + '/jquery/plugin/plupload/Moxie.swf',
			silverlight_xap_url: ctxPaths + '/jquery/plugin/plupload/Moxie.xap',
			max_file_size: _this._options.max_file_size,
			filters: _this._options.mime_types,
			file_data_name:'attachment',
			headers : {'attachTypeId':_this._options.attachTypeId},
			init: {
			PostInit: function() {

			},
			FilesAdded: function(up, files) {
				$('#' + _this._uploadFileBtnId).parent().hide();
				//document.getElementById(_this._uploadFileBtnId).style.display = 'none';
				plupload.each(files, function(file) {
					//$('#' + _this._contenterId).append('<div class="uploading"><div><div class="uploadfile"><a href="javascript:;" target="_blank">' + file.name + ' (' + plupload.formatSize(file.size) + ')</a></div><span class="red">0%</span></div><div class="uploadbar"  style="width:0%"></div><div class="closeitem"> <a href="javascript:;"></a> </div></div>');
					$('#' + _this._contenterId).append('<div class="uploading"> <div class="uploadcontent"> <div class="uploadfile"><a target="_blank" href="javascript:;" title="' + file.name + '">' + file.name + '</a></div>           <div class="red uploadpercent">0%</div><div class="uploadbarbox"><div style="width: 0%;" class="uploadbar"></div></div>                         </div> <div class="closeitem"> <a href="javascript:;"></a></div></div>');
					//$('.uploading:last').attr('attachFileId', file.name);
				});
				
				/**if ($('#' + _this._contenterId).attr('attachGroupId')) {
					_this.uploader.settings.url = _this._changeParam(_this.uploader.settings.url, 'attachGroupId', $('#' + _this._contenterId).attr('attachGroupId'));
				}**/
				_this._bindCloseItemEvent();
				//up.refresh(); 
				//$('#uploadFileBtn').hide();
				//$('#uploadFileBtn2').show();
				up.refresh();
				_this.uploader.disableBrowse(true);
				_this.uploader.start();
			},
			BeforeUpload : function(up, file){
				var uploadFinishedId = 'hidden_uploadFinishedId_' + _this._contenterId;
				$('#'+uploadFinishedId).attr("uploadfinishedflag","0");
				
				//添加额外参数 ，支持分块传输,by haomingli
				if(up.settings.multipart_params == undefined){
					up.settings.multipart_params = {fileId:file.id }
				}
			},
			UploadProgress: function(up, file) {
				var _percent=file.percent>1?file.percent-1:file.percent;
				$('#' + _this._contenterId + ' .uploading .uploadcontent .uploadpercent:last').html(_percent + '%');
				$('#' + _this._contenterId + ' .uploading .uploadcontent .uploadbarbox .uploadbar:last').width(_percent+ '%');
			},
			UploadComplete: function(up, file) {
				//_this._disableBrowse(false);
				//_this._checkMaxSize();
			//$('.uploading:last').attr('fileId', file.name);
			},
			FileUploaded: function(up, file, res) {
			 var result = {'success':false};
			 var uploadFinishedId = 'hidden_uploadFinishedId_' + _this._contenterId;
			 $('#'+uploadFinishedId).attr("uploadfinishedflag","1");
			 //alert("FileUploaded1");
			 $('#'+uploadFinishedId).trigger('blur');
			 //alert("FileUploaded2");
			 	
			 if (res.response){
				try{
					result = eval(("(" + res.response + ")"));
				}catch(err){
					result = {'success':false,'data':{'msg':'发生未知错误'}};
				}
			 }
			if (result.success){
				_this._add_attachFileIds.push(result.attachFileId);
				$('#' + _this._contenterId + ' .uploading .uploadcontent .uploadpercent:last').remove();
				$('#' + _this._contenterId + ' .uploading .uploadcontent .uploadbarbox:last').remove();
				if (_this._options.disTheme == 2) {
					$('#' + _this._contenterId + ' .uploading .uploadfile :last').remove();
					$('#' + _this._contenterId + ' .uploading:last').css('width', '100px').css('height', '100px').append('<img src="'+$.appendExtraParams (_this._options.viewUrl + "?attachFileId=" + result.attachFileId + "&attachGroupId=" + result.attachGroupId) + '"/>');
				}else{
					$('#' + _this._contenterId + ' .uploading .uploadcontent .uploadfile a:last').attr('href',$.appendExtraParams (_this._options.downloadUrl + "?attachFileId=" + result.attachFileId));
				}
				$('#' + _this._contenterId + ' .uploading:last').attr('attachfileid', result.attachFileId);
				$('#' + _this._contenterId).attr('attachGroupId', result.attachGroupId);
				//$('#h_' + this._contenterId).val(result.attachGroupId + "|");
				_this._initHiddenName();
				_this._initHiddenUpLoadFinishedName();
			}else{
				alert(result.data.msg);
				$('#' + _this._contenterId +' .uploading:last').append('<div style="float:bottom"><a href="javascript:void(0)">续传</a> </div>');
			}
			
				//$('#uploadFileBtn2').hide();
			//$('#uploadFileBtn').show();
			//_this.uploader.init();
			//$('#uploadFileBtn').removeClass('graybtn').addClass('bluebtn');
			up.refresh();
			_this._bindCloseItemEvent();
			//_this._disableBrowse(false);
			_this._checkMaxSize();
			$('#' + _this._hiddenId).trigger('blur');
			if (_this.uploader)_this.uploader.disableBrowse(false);
			
			},
			ChunkUploaded:function(up, file, res){//分块文件每块上传后回调
				result = {'success':false,'data':{'msg':'未知错误'}};
				if (res.response){
					try{
						result = eval(("(" + res.response + ")"));
					}catch(err){
						result = {'success':false,'data':{'msg':'发生未知错误'}};
					}
				}
				if(result.success == false){//上传失败
					//继续上传 to
					alert(result.data.msg);
					$('#' + _this._contenterId +' .uploading:last').remove();
					up.refresh();
					_this._checkMaxSize();
					if (_this.uploader)_this.uploader.disableBrowse(false);
				}
				
			},
			Error: function(up, err) {
				if(err.message.indexOf("HTTP")>-1){
					alert("附件上传失败，请检查网络是否通畅！");
				}else{
					alert(err.message);
				}
				$('#' + _this._contenterId +' .uploading:last').append('<div style="float:bottom;margin-top:14px;"><a class="keepUpload" href="javascript:void(0)">续传</a> </div>');
				$('#' + _this._contenterId +' .uploading .keepUpload').click(function(){
					err.file.status = plupload.UPLOADING;
					up.state= plupload.UPLOADING;
					$('#' + _this._contenterId +' .uploading .keepUpload').remove();
					_this.uploader.trigger("UploadFile", err.file);
				});
				if (_this.uploader)_this.uploader.disableBrowse(false);
			}
		  }
		});
		_this.uploader.init();
	},
	init: function(ops) {
		var _this = this;
		if (typeof ops == 'object') {
			$.extend(_this._options, ops);
		}
		_this._composeMimeTypes();
		_this._uploadFileBtnId = 'uploadBtn_' + _this._contenterId;
		var name = _this._options.hiddenName ? _this._options.hiddenName : '';
		_this._hiddenId  = 'hidden_' + _this._contenterId;
		_this._attachGroupId = 'hidden_groupid_' + _this._contenterId;
		//add by xiaoliangqing on 2014/01/07 start
		var uploadFinishedName = _this._options.uploadFinishedName ? _this._options.uploadFinishedName : 'hidden_uploadFinishedName_' + _this._contenterId;
		_this._options.uploadFinishedName = uploadFinishedName;
		var uploadFinishedId = 'hidden_uploadFinishedId_' + _this._contenterId;
		_this._uploadFinishedId = uploadFinishedId;
		//add by xiaoliangqing on 2014/01/07 end
		
		$('#' + _this._contenterId).empty();
		if (_this._options.max_file > 1){
			$('<div><input type="button" id="' + _this._uploadFileBtnId + '" value="' + _this._options.btnName + '" class="bluebtn" '+(_this._options.btnWidth?'style="width:'+_this._options.btnWidth+'"':'')+'/><input type="hidden" '+ (_this._options.hiddenAttachId ? 'name="' + _this._options.hiddenAttachId + '"' : '') +' id="' + _this._hiddenId  + '"/><input type="hidden" '+ (_this._options.hiddenName ? 'name="' + _this._options.hiddenName + '"' : '') +' id="' + _this._attachGroupId  + '"/></div>').appendTo('#' + _this._contenterId);
		}else{
			$('<div><input type="button" id="' + _this._uploadFileBtnId + '" value="' + _this._options.btnName + '" class="bluebtn" '+(_this._options.btnWidth?'style="width:'+_this._options.btnWidth+'"':'')+'/><input type="hidden" '+ (_this._options.hiddenName ? 'name="' + _this._options.hiddenName + '"' : '') +' id="' + _this._hiddenId  + '"/></div>').appendTo('#' + _this._contenterId);
		}
		
		//$('<div><input type="button" id="' + _this._uploadFileBtnId + '" value="' + _this._options.btnName + '" class="bluebtn" '+(_this._options.btnWidth?'style="width:'+_this._options.btnWidth+'"':'')+'/>').appendTo('#' + _this._contenterId);
		//if (_this._options.max_file > 1){
			//$('#' + _this._contenterId).append('<input type="hidden" '+ (_this._options.hiddenAttachId ? 'name="' + _this._options.hiddenAttachId + '"' : '') +' id="' + _this._hiddenId  + '"/><input type="hidden" '+ (_this._options.hiddenName ? 'name="' + _this._options.hiddenName + '"' : '') +' id="' + _this._attachGroupId  + '"/></div>').appendTo('#' + _this._contenterId);
		//}else{
			//$('#' + _this._contenterId).append('<input type="hidden" '+ (_this._options.hiddenName ? 'name="' + _this._options.hiddenName + '"' : '') +' id="' + _this._hiddenId  + '"/></div>').appendTo('#' + _this._contenterId);
		//}
		$('<div><input  type="hidden"'+' name="' + uploadFinishedName + '"  id="' + uploadFinishedId + '" value="uploadFinished"  />').appendTo('#' + _this._contenterId);
		//$('<div><input type="button" id="' + _this._uploadFileBtnId + '" value="' + _this._options.btnName + '" class="bluebtn"><input type="hidden" '+ (_this._options.hiddenName ? 'name="' + _this._options.hiddenName + '"' : '') +' id="' + id + '"/><input type="hidden" '+' name="' + uploadFinishedName + '"  id="' + uploadFinishedId + '" value="uploadFinished"  /></div>').appendTo('#' + _this._contenterId);
		if (_this._options.hiddenAttr){
			for (var obj in this._options.hiddenAttr){
				$('#' + _this._hiddenId).attr(obj,this._options.hiddenAttr[obj]);
			}
		}
		
		//add by xiaoliangqing on 2014/01/07 start
		if (_this._options.uploadFinishedAttr){
			for (var obj in this._options.uploadFinishedAttr){
				$('#' + uploadFinishedId).attr(obj,this._options.uploadFinishedAttr[obj]);
			 }
		}
		//add by xiaoliangqing on 2014/01/07 end
		
		//$('<div style="float:left;"><a id="' + _this._uploadFileBtnId + '" class="bluebtn">' + _this._options.btnName + '<a></div>').appendTo('#' + _this._contenterId);
		
		_this._initDatas();
	},
	getGroupId: function() {
		return $('#' + this._contenterId).attr('attachGroupId')?$('#' + this._contenterId).attr('attachGroupId'):'';
	},
	getAddAttachFileIds : function(){
		return this._add_attachFileIds.join(',');
	},
	getDelAttachFileIds : function(){
		return this._del_attachFileIds.join(',');
	},
	getAttachFiles: function() {
		var succ = [];
		$('#' + this._contenterId + ' .uploading').each(function(i) {
			if ($(this).attr('attachfileid')) {
				succ.push($(this).attr('attachfileid') + "|" + $(this).find('.uploadfile a').text());
			}
		})
		return succ.join(',');
	},
	getSucc: function() {
		var succ = [];
		$('#' + this._contenterId + ' .uploading').each(function(i) {
			if ($(this).attr('attachfileid')) {
				succ.push($(this).attr('attachfileid'));
			}
		})
		return succ.join(',');
	}

}
/**
 * 解决JS中获取不到base标签href的问题
 */
var linkTo = function(url) {
	var _url = url;
	if ($.browser.msie) {
		if (document.getElementsByTagName('base') && document.getElementsByTagName('base')[0].href) {
			_url = document.getElementsByTagName('base')[0].href + url;
		}
	}
	window.location.href = _url;
}
var companyTipMsg = {
		w:{
			"n11":"限制为11位数字",
			"n":"请输入数字",
			"len20":"长度不能超过20个字节",
			"len30":"最大长度为30个字符",
			"zj80":"最大长度为80个字节",
			"h":"请输入汉字",
			"zj100":"最大长度为50个汉字",
			"len8":"最大长度为8个字符",
			"len6":"最大长度为6位数字",
			"len2":"最大长度为2位数字",
			"zj50":"最大长度为25个汉字",
			"len18":"最大长度为18个字符",
			"zj1000":"最大长度为500个汉字",
			"n6":"限制为6位数字",
			"len80":"长度不能超过80个字节",
			"zj160":"最大长度为80个汉字",
			"zj200":"最大长度为100个汉字",
			"zj60":"最大长度为30个汉字",
			"ulogin":"申请帐号名已存在",
			"uCompanyStatus":"该公司处于审批流程中，不允许进行帐号注册",
			"rulogin":"申请帐号名已存在",
			"uemail":"Email已存在",
			"umobile":"手机号码已存在",
			"ruemail":"Email已存在",
			"rumobile":"手机号码已存在",
			"value":"请输入大写英文字母和数字",
			"pwd":"必须同时包含数字和字母",
			"len6-20":"密码长度在6-20个字符",
			"name":"只能输入汉字或字母",
			"elen30":"只能输入不超过30个字节",
			"rlen80":"不超过40个汉字，80个字节",
			"nlen20":"申请帐号名只能由英文字母、数字和中文组成，最长20位",
			"account":"申请帐号名只能由汉字、数字组成",
			"mobile":"请输入正确的手机号码"
		}
}
var getValidformDefaultConfig = function(config) {
	var _default = {
		tiptype: tipType.rightSide,
		ajaxPost: false,
		showAllError:true,
		//dragonfly:true,
		datatype: { //传入自定义datatype类型，可以是正则，也可以是函数（函数内会传入一个参数）;
			"zh1-6" : /^[\u4E00-\u9FA5\uf900-\ufa2d]{1,6}$/,
			"zh1-25" : /^[\u4E00-\u9FA5\uf900-\ufa2d]{1,25}$/,
			"s1-6":/^[\u4E00-\u9FA5\uf900-\ufa2d\w\.\s]{1,6}$/,
			"*1-1000":/^[\w\W]{1,1000}$/,
			"*1-18":/^[\w\W]{1,18}$/,
			"s1-50":/^[\u4E00-\u9FA5\uf900-\ufa2d\w\.\s]{1,50}$/,
			"h":/^[\u4E00-\u9FFF]+$/,
			"n0-100":/^(0|[0-9][0-9]?|100)$/,
			"len6":/^\d{1,6}$/,
			"len2":/^\d{1,2}$/,
			"len18":/^.{1,18}$/,
			"g20":/^\d{20}$/,
			"100%":/^(0|[0-9][0-9]?|100)$/,
			"zj100":/^.{1,50}$/,
			"len8":/^.{1,8}$/,
			"nlen20":/^.{1,20}$/,
			"name":/^[A-Za-z\u4e00-\u9fa5]+$/,
			"pwd":/^(?=.*\d)(?=.*[A-Za-z])[0-9a-zA-Z]/,
			//"pwd":"/(?:?<!(?:[^a-zA-Z][a-zA-Z0-9])) /",
			"len6-20":/^[\w\W]{6,20}$/,
			"zj50":/^.{1,25}$/,
			"zj1000":/^.{1,500}$/,
			"n6":/^\d{6}$/,
			"len80":/^.{1,40}$/,
			"zj160":/^.{1,80}$/,
			"zj2000":/^.{1,1000}$/,
			"zj80":/^.{1,40}$/,
			"len20":/^.{1,10}$/,
			"zj200":/^.{1,100}$/,
			"zj60":/^.{1,30}$/,
			"date" : /^(\d{4})\-(\d{2})\-(\d{2})$/,
			"len30":/^.{1,30}$/,
			"len20":/^.{1,20}$/,
			"zj80":/^.{1,40}$/,
			"rlen80":/^.{1,40}$/,
			"elen30":/^.{1,30}$/,
			"account":/^[\u4e00-\u9fa5]+[0-9]+$|^[0-9]+[\u4e00-\u9fa5]+$/,
			"h":/^[\u4E00-\u9FFF]+$/,
			"value":/^[-.A-Z0-9\u4E00-\u9FFF]+$/,
			"n11":/^\d{11}$/,
			"n":/^\d+$/,
			"nlen20":/^.{1,20}$/,
			"s1-80":/^[\u4E00-\u9FA5\uf900-\ufa2d\w\.\s]{1,80}$/,
			"mobile":dataType.mobile,
			"ulogin":dataType.uLoginName,
			"umobile":dataType.uMobile,
			"uemail":dataType.uEmail,
			"rulogin":dataType.ruLoginName,
			"rumobile":dataType.ruMobile,
			"ruemail":dataType.ruEmail,
			"uCompanyStatus":dataType.uCompanyStatus,
			"proportion":/^(100|[0-9]?\d((\.\d)?|((\.\d\d)?)))$/,
			"phone":/^(([0\+]\d{2,3}-)?(0\d{2,3})-)?(\d{7,8})(-(\d{3,}))?$/,											
			"fax":/^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/,
			"postCode":/^[1-9]\d{5}$/,
			"positiveInteger":/^[1-9]\d*$/,
			"scoreCode":/^((0|[0-9][0-9]?|100)|[A-Z]{1})$/,
			"email":dataType.email,
			"number100":dataType.number100,
			"bankAccount":/^[\d\-\*]*$/,
			"getIntentionBase":dataType.getIntentionBase,
			"vzhiben": /^[0-9]\d{0,5}(\.\d{0,2})?$/,
			"examScore":/^([1-9]|[0-9]((\.[1-9])|((\.[0-9][1-9])|(\.[1-9][0])))|[1-9][0-9]|[1-9][0-9]?((\.\d)?|((\.\d\d)?)))$/,
			"specialChars" : function(gets, obj, curform, regxp){//非法字符 ' " < >
				var zh = /['">\<]/;
				if (zh.test(gets)){
					return '不能含有特殊字符 > <  \' "';
				}
				return true;
			},
			"recheck":function(gets, obj, curform, regxp){//校验重复密码，自带的在IE8下焦点有问题
				var recheckpwd = obj.attr('recheckpwd');
				if (gets && recheckpwd && gets != $('#' + recheckpwd).val()){
					return false;
				}
				return true;
			},
			"checkOrgCode":function(gets, obj, curform, regxp){//组织结构代码简单校验
				if (!gets && gets.length > 10){
					return '不是有效的组织机构代码';
				}
				return true;
			},
			"validateMaxLength" :function(gets, obj, curform, regxp) {
				if (!gets) {
					return false;
				}
				var len = 0,_len = obj.attr('maxlength');
				if (gets){
					len = gets.replace(/[^\x00-\xff]/g, '..').length;
				}
				if (_len && len > _len){
					return '长度不能超过' + _len + '个字节或'+ _len/2 + '个汉字';
				}
				return true;
			},
			"startTimeValid": function(gets, obj, curform, regxp) {
				//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;
				if (!gets) {
					return false;
				}
				var from = obj,
					to = $('#' + obj.attr('endtimeflag'));
				if (from.val() != '' && to.val() != '' && !isEndTimeGtStartTime(from.val(), to.val())) {
					
					return false;
				}
				setTimeout(function(){to.trigger('blur');},1000);
				//to.trigger('blur');
				return true;
				//注意return可以返回true 或 false 或 字符串文字，true表示验证通过，返回字符串表示验证失败，字符串作为错误提示显示，返回false则用errmsg或默认的错误提示;
			},
			"endTimeValid": function(gets, obj, curform, regxp) {
				//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;
				if (!gets) {
					return false;
				}
				var st = obj.attr('starttimeflag');
				if (st) {
					var from = $('#' + st),
						to = obj;
					if (from.val() != '' && to.val() != '' && !isEndTimeGtStartTime(from.val(), to.val())) {
						return false;
					}
				}
				setTimeout(function(){from.trigger('blur');},1000);
				//from.trigger('blur');
				return true;
				//注意return可以返回true 或 false 或 字符串文字，true表示验证通过，返回字符串表示验证失败，字符串作为错误提示显示，返回false则用errmsg或默认的错误提示;
			},
			"validProductName": function(gets, obj, curform, regxp) {
				//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;
				if (obj.attr('pid') && obj.attr('pid') != '') {
					return true;
				}
				return false;
			},
			"uploadFinished": function(gets, obj, curform, regxp) {
				//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;
				if (!gets) {
					return false;
				}
				var uploadfinishedflag = obj.attr('uploadfinishedflag');
				if (uploadfinishedflag && uploadfinishedflag == '0'){
					return '附件上传未完成，不能提交';
				}
				return true;
			},
			"maxValue": function(gets, obj, curform, regxp) {
				//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;
				if (!gets) {
					return true;
				}
				var value = gets,
				_value = obj.attr('maxValue');
				
				if(value){
					var re =/^[1-9]+[0-9]*$/;
					if(!re.test(value)){
						return "请填入正确整数";
					}
				}
				
				if(parseInt(_value) < parseInt(value)){
					return "请输入大于"+_value+"的整数";
				}
				
				return true;
			},
			"maxNumberLength": function(gets, obj, curform, regxp) {
				//参数gets是获取到的表单元素值，obj为当前表单元素，curform为当前验证的表单，regxp为内置的一些正则表达式的引用;
				if (!gets) {
					return true;
				}
				var value = gets,
				_value = obj.attr('maxNumberLength');
				
				if(value){
					var re =/^[1-9]+[0-9]*$/;
					if(!re.test(value)){
						return "请填入正确整数";
					}
				}
				
				if(parseInt(_value) < parseInt(value)){
					return "请输入大于"+_value+"的整数";
				}
				
				return true;
			}
		}
	}
	//$.extend(validation.tipmsg.w,companyTipMsg.w);
	return typeof config == 'object' ? $.extend(_default, config) : _default;
}


/**
 * 根据常量值，select动态数据获取封装。
 * @selectId select的id
 * @constantKey 获取数据的健
 * @config 传入的参数
 * @return void
 */
function ajaxRenderConstant(selectId,constantKey, config) { //config:{firstOption:{text:'请选择分类',value:''},keyAlias:{text:'name',value:'value'}}
			var _this = $("#"+selectId);
			c = config || {}, keyValue = {
				text: 'name',
				value: 'value'
			};
			if (c.keyValue) {
				keyValue = c.keyValue;
			}
			_this.empty();
			if (c.firstOption) {
				_this.append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
			}
			var data = constantData[constantKey];
			if (data) {
						for (var i = 0; i < data.length; i++) {
							_this.append('<option value="' + data[i][keyValue.value] + '">' + data[i][keyValue.text] + '</option>');
						}
					}
		
		    //select下拉框选中
			if(c.selectedValue){
						$(_this).val(c.selectedValue);
					}		
		
		}
		
/**
 * 解析json数据
 * @return void
 */		
function parseData(values,key,oldValues){        
      if(!(values instanceof Array) && typeof values == 'object'){

               if(oldValues == undefined){

                        oldValues = values;

               }

               for(var id in values){

                        if(typeof values[id] != 'function' && !(values[id] instanceof Array) && typeof values[id] != 'object'){

                                  if(key != undefined){

                                           oldValues[key + '.' + id] = values[id];

                                           delete values[id];

                                  }

                        }else if(typeof values[id] != 'function' && !(values[id] instanceof Array) && typeof values[id] == 'object'){

                                  var valuess = values[id];

                                  for(var idd in valuess){

                                           var newKey = id + '.' + idd;

                                           if(key != undefined){

                                                    newKey = key + '.' + id + '.' + idd;

                                           }

                                           oldValues[newKey] = valuess[idd];

                                           delete values[id];

                                           parseData(oldValues[newKey],newKey,oldValues);

                                  }

                        }

               }

      }

};

/**
 * 解析json数据put值到表单元素
 * @return void
 */	
function jsonToForm(json){
	parseData(json);
	var fields = $(".field,input");
	jQuery.each( fields, function(i, field){
		var name= $(field).attr("name");
		var value = json[name];
		if($(field).is('input')){
			$(field).val(value);
		}else{
			$(field).text(value);
		}
	      
	});
}
/**
*reset form elements
*/
function resetValue(formId){
	var _ei = $('#'+formId); 
	_ei.find('input[type=text]').val('');
	_ei.find('input[type=hidden]').val('');
	_ei.find('select').attr('value','');
	_ei.find('input[type=date]').val('');
}


/**
 * PRM 业务层封装的一些工具类和对象
 */
;
(function($){
var downLoadAttachUrl = BIZCTX_PATH + '/attachment!download.action';
$.fn.extend({
	createAttachLink:function(){
		return this.each(function( j ) {
			var attachFileId = $(this).find('span[isAttachId]').text();
			var attachName = $(this).find('span[isAttachName]').text();
			var subStringLength =$(this).find('span[isAttachName]').attr('subStringLength');
			if($.trim(attachFileId) != '' && $.trim(attachName) != ''){
				var attachNameLength = attachName.replace(/[^\x00-\xff]/g, ' ').length;
				if(subStringLength==''||subStringLength==undefined || subStringLength==''){
					subStringLength=30;
				}
				var aHtml= '<a  href="' + $.appendExtraParams(downLoadAttachUrl + '?attachFileId=' + attachFileId) + '" target="_blank">' + attachName + '</a>';
				if(attachNameLength &&attachNameLength>subStringLength){
					var subStringSpanText = attachName.substring(0,subStringLength)+"...";
					aHtml = '<a  href="' + $.appendExtraParams(downLoadAttachUrl + '?attachFileId=' + attachFileId) + '" target="_blank" title="'+attachName+'">' + subStringSpanText + '</a>';
				}
				$(this).empty().append(aHtml);
			}
			
		});
	}
});	
	
})(jQuery);

;
(function($){
$.fn.extend({
	renderSelect:function(){
		return this.each(function( j ) {
			var size = 4,
			d = new Date(),
			year = d.getFullYear();
			$(this).empty();
			$(this).append('<option value="">请选择</option>');
			for (var i = size; i >= 0; i--) {
				$(this).append('<option value="' + ((year - (size - i))) + '">'+((year - (size - i)))+'</option>');
			}
			$(this).val('');
		});
	}
});	
})(jQuery);
/**
 * 初始化最近10年下拉列表
 * @param {Object} id
 */
var initYearSelect = function(id) {
	var s = document.getElementById(id),
		size = 4,
		d = new Date(),
		year = d.getFullYear();
	s.length = 0;
	s.options[s.length] = new Option('请选择', (year - (size - i)));
	for (var i = size; i >= 0; i--) {
		s.options[s.length] = new Option((year - (size - i)), (year - (size - i)));
	}

};

/**
 * 扩展数组的包含功能
 * @param {Object} obj
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
Array.prototype.contains = function(element) {  
    for (var i = 0; i < this.length; i++) {  
        if (this[i] == element) {  
            return true;  
        }  
    }  
    return false;  
}
;
(function($){
$.fn.extend({
	isValueDuplicate:function(){
		var arr = [];
		var flag = false;
		this.each(function( j ) {
			var v = $(this).val();
			if (arr.contains(v)){
				flag = true;
			}
			arr.push(v);
		});
		return flag;
	}
});	
})(jQuery);


//对datepicker进行扩展，增加清除功能
$(document).ready(function(){
	$.datepicker.setDefaults({
	  changeYear: true,
      showButtonPanel: true,
      closeText: '清除',
      onChangeMonthYear:function(year, month, inst){
		setTimeout(function(){$(".ui-datepicker-close").unbind().bind("click", function (){ 
			inst['input'].val('');
             //$(input).val(''); 
             return false;
        });},500);  
      },
      beforeShow: function(input,inst) {
		setTimeout(function(){$(".ui-datepicker-close").unbind().bind("click", function (){ 
			inst['input'].val('');
             //$(input).val(''); 
             return false;
        });},500);  
      },
      onSelect:function(){
         $(this).trigger('blur');//再次触发失去焦点事件用于校验
      }
   }); 
	try{
		$( document ).tooltip();
		$('a[name="attachFileTip"]').attr('title','上传的附件只支持:图片文件(jpg,jpeg,gif),Word文件(doc,docx),Pdf文件(pdf),Rar文件(zip,rar),Excel文件(xls,xlsx),PowerPoint文件(ppt,pptx)。');
		$('a[name="attachFileTip"]').tooltip();
	}catch(err){}
});

//阻止回退键的默认行为
$(document).bind("keydown",function(e){
	if (e.which == 8 ){
		var targetNodeName = '';
		if(e.target && e.target.nodeName){
			targetNodeName =  e.target.nodeName.toLowerCase();
		}
		if((targetNodeName != 'input' && targetNodeName != 'textarea' && targetNodeName != 'password') || e.target.readOnly == true){
			return false;
		}
	  }
});

/**
 * 通过button和链接button中设置的权限属性（属性名为permCheck的格式是"[resKey,]operKey[,hidden|disable]"）发起后台鉴权请求，通过后台返回的结果，对按钮或链接进行置灰和隐藏操作
 * @param {Object} url
 * @param {Object} resKey
 * @memberOf {TypeName} 
 */
;
(function( $, undefined ) {
	PermCheck=function(options){
		if(this.initialized && this.initialized === true){
			return;
		}
		this.options = options || {};
		if(this.options.url == undefined){
			this.options.url = ctxPaths+"/resAuth!pageComponentAuth.ajax";
		}
	}
	PermCheck.prototype={
		_setBehavior : function(behavior){
				//判断是原生button还是链接button
				if("button" == $(this).attr("type") || "submit" == $(this).attr("type")){
					if(behavior == 'disable'){
						$(this).attr("disabled",true);		
					 }else{
						$(this).hide();
					 }
				}else{
					var linkButton;
					if('A' == $(this).get(0).tagName){
						linkButton = $(this);
						
					}else{
						linkButton = $(this).find('a');
					}
					if(linkButton.length > 0){
						var aText = linkButton.html();
						if(behavior == 'disable'){
							$('<span>' + aText + '</span>').appendTo($(this).parent()).addClass("graybutton");
							//清除链接
							 linkButton.remove();
							 //批量审批按钮不可见 add by xiaoliangqing
							 $("#batchAuditSpan").hide();
						}else{
							$(this).hide();
						}
					}
				}
		},
		run:function(options){
			var _this = this;
			$.extend(_this.options,options);
			var resKeyArray = [];
			var operKeyArray = [];
			var targetObjArray = [];
			//获取权限检查数组
			$('[permCheck]').each(function(){
				var permCheckValue = $(this).attr("permCheck");
				var arr=permCheckValue.split(",");
		        var resKey;
		        var operKey;
		        var targetObj = this;
		        var behavior;
		        if(arr.length==1){
		            resKey=_this.options.resKey
		            operKey=arr[0];
		            behavior="hidden";
		        }else if(arr.length==3){
		           	resKey=arr[0];
		            operKey=arr[1];
		            behavior=arr[2];
		        }else{
		            if(arr[1]=='hidden' ||arr[1]=='disable'){
		                resKey=_this.options.resKey;
		               	operKey=arr[0];
		                behavior=arr[1];
		            }else{
		                resKey=arr[0];
		               	operKey=arr[1];
		                behavior="hidden";
		            }
		        }
		      resKeyArray.push(resKey);
		      operKeyArray.push(operKey);
		      $(targetObj).attr("behavior", behavior);
		      targetObjArray.push(targetObj);
			});
			
			
			//定义回调函数
			var cb = function(r){
				if(r.success){
					var data = r.data;
					for(var i=0;i<data.length;i++){
						if(data[i].result==false){
							if($(targetObjArray[i]).attr("behavior")=='disable'){
								_this._setBehavior.call(targetObjArray[i], 'disable');
							}else{
								_this._setBehavior.call(targetObjArray[i]);
							}
						}
					}
				}
			}
			
			//发请求到后台批量检查权限
			if(resKeyArray.length > 0){
				var resKeys = resKeyArray.join(',');
				var operKeys = operKeyArray.join(',');
				$.post(_this.options.url,{'resKeys':resKeys,'operKeys':operKeys},cb);
			}
			
		}
	};
	
	$.permCheck = new PermCheck(); // singleton instance
	$.permCheck.initialized = true;
})(jQuery);

//覆盖block ui的默认行为和样式
$.blockUI.custom = {
	css: {
			padding:	0,
			margin:		0,
			width:		'30%',
			top:		'40%',
			left:		'35%',
			textAlign:	'center',
			color:		'#000',
			border:		'0px solid #aaa',
			backgroundColor:'#fff',
			cursor:		'default'
	},
	fadeIn:0,
	fadeOut:0,
	baseZ:99999,
	// styles for the overlay 
    overlayCSS:  { 
        backgroundColor: '#CCC', 
        opacity:         0.6, 
        cursor:          'default' 
    }
	
}

$.extend($.blockUI.defaults,$.blockUI.custom );


/**
 * 基于block ui封装 的alert，confirm，prompt对话框
 * @param {Object} $
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
;
(function( $, undefined ) {
	//dialog jq对象
	var dialog;
	
	//关闭动作
	var close = function(callback,r){
		//关闭，回调
		$.unblockUI(); 
		if(callback && $.isFunction(callback)){
    	  callback(r);
        }
		
	}
	
	var getTextWidth  = function(text){
		var textDiv = $('<div></div>').appendTo('body');
		textDiv.css('position','absolute');
		textDiv.css('left',-1000);
		textDiv.css('top',-1000);
		textDiv.html(text);
		var w = textDiv.outerWidth();
		textDiv.remove();
		return w;
	}
	
	$.extend({
	/**
	 * 
	 * @param {Object} msg  提示信息 必须 
	 * @param {Object} title 提示title 可选 默认为提示
	 * @param {Object} success  成功提示还是失败提示，true|false 可选，默认为false
	 * @param {Object} callback  点确定按钮后执行的回调函数 可选
	 */
		prm_alert:function(msg,title,success,callback){
			$.prm_show_dialog({
				msg:msg,
				title:title || "提示",
				callback:callback,
				success:success,
				buttons:{
					ok:'确认'
				}
			});
			
		},
		/**
		 * 
		 * @param {Object} msg 提示信息 必须
		 * @param {Object} title 提示title 可选，默认为提示
		 * @param {Object} callback 点击确定和取消按钮后执行的回调函数，掺入的参数值为yes|no ,来指定用户选择的动作是确定还是取消
		 */
		prm_confirm:function(msg,title,callback){
			$.prm_show_dialog({
				msg:msg,
				title:title || "提示",
				callback:callback,
				buttons:{
					ok:'确认',
					cancel:'取消'
				}
			});
		},
		prm_prompt:function(){
			//todo
			
		},
		/**
		 * show dialog （功能强大）
		 * @param {Object} options  msg,titie,callback,buttons:{ok:'确定',cancel:'取消'}
		 * @memberOf {TypeName} 
		 */
		prm_show_dialog:function(options){
			//创建dialog html
			if(dialog == undefined){
				dialog = $('<div class="dialog_shadow" style="display:none" name="prm_dialog">' + 
							'<div class="title"><span class="titlespan"></span><span class="rightarrow" style="font-size:12px;"><a href="javascript:;"><div class="dialog-close"></div></a></span></div>' +
							'<div style="padding:2px 15px;">' + 
							'<div class="dialog-content"></div>' + 
							'<div class="dialog-button"></div>' +
							'<div class="dialog-problem" style="padding-bottom:5px;"></div>' +
							'</div>' + 
							'</div>').appendTo(document.documentElement);
			}
			
			
			options = options || {};
			if(options.title){
				$('.dialog-title',dialog).css('display','');
				$('.titlespan',dialog).text(options.title);
			}else{
				$('.dialog-title',dialog).css('display','none');
			}
			
			$('.dialog-content',dialog).empty().html('<p><span class="icon-dialog ' + (options.success? 'icon-dialog-ok':'icon-dialog-warn') + '"></span><span class="dialog-msg">' + options.msg + '</span><div style="clear:both;"></div></p>');
			if(options.buttons){
				var buttonHtml = '<p><span class="button">';
				if(options.buttons.ok){
					buttonHtml = buttonHtml + '<a href="javascript:;" name="yes">' + options.buttons.ok + '</a></span>';
				}
				if(options.buttons.cancel){
					buttonHtml = buttonHtml + '<span class="button"><a href="javascript:;" name="no">' + options.buttons.cancel + '</a></span>';
				}
				buttonHtml = buttonHtml + '</p>';
				
				$('.dialog-button',dialog).empty().html(buttonHtml);
				$('.dialog-button a[name=yes]',dialog).click(function(){
					$(this).unbind();
					close(options.callback, 'yes');
				});
				$('.dialog-button a[name=no]',dialog).click(function(){
					$(this).unbind();
					close(options.callback, 'no');
				});
			}
			if(!options.success){
				$('.dialog-problem',dialog).empty().html( '<span style="float:left;margin:5px;"><a href="javascript:window.top.feedBackBtHandler();">问题反馈</a></span>');
			}
			if(options.width){
				$('.dialog-msg',dialog).css('width',options.width - 80);
			}
			//弹出层
			$.blockUI({ message: dialog, css: { width: (options.width? options.width:(getTextWidth(options.msg) + 80) + 'px') } ,height:'auto'}); 
			$('.title a',dialog).unbind().click(function(){
				close(options.callback, 'no');
			});
		}
	});
})(jQuery);



//loading,支持整个页面loading和元素loading
;
(function( $, undefined ) {
	var loadingOption = {message: '<div class="loading">请稍候...</div>' ,css: { width: '80px' },overlayCSS:  { backgroundColor: '#fff',opacity:0.5}};
	$.fn.extend({
		prm_show_loading:function(){
			 if(this.length == 0 || this[0] == document){
				$.blockUI(loadingOption);
			 }else{
				$(this).block(loadingOption); 
			 }
		},
		prm_hide_loading:function(){
			 if(this.length == 0 || this[0] == document){
				$.unblockUI(loadingOption);
			 }else{
				$(this).unblock(); 
			 }
		}
	});
	
})(jQuery);
;

//为iframe增加loading
if(parent != self){
	var iframe = $(window.parent.document).find('iframe');
	if(iframe && iframe.get(0).tagName == 'IFRAME' && window.parent.prmGloableCache){//父页面是main.jsp页面
		iframe.bind('load',function(){
			iframe.parent('div').prm_hide_loading();
		});
		iframe.parent('div').prm_show_loading();
		
	}
}

//datagrid默认配置修改
$.fn.datagrid.prm_custom = {
	fitColumns:true,
	singleSelect:true,
	loadFilter:function(data){
		var initRoot = $(this).attr("initRoot");
		//var initRoot = $("#hezuoshenqingListTable").attr("initRoot");
		var arr;
		if(initRoot && initRoot.indexOf(".") > 0){
				arr =initRoot.split(".");
			}

		if (typeof data.length == "number"
				&& typeof data.splice == "function") {
			return {
				total : data.length,
				rows : data
			};
		} else {
              var json = data[arr[0]][arr[1]];
              if(json){
            	  for(var i = 0; i < json.length; i++){
	            	  parseData(json[i]);
	              }
              }
			return {
				total : data.totalCount,
				rows : json
			};
		}
	},
	onLoadError : function() {
		alert("列表数据加载失败");
	}
}

$.extend($.fn.datagrid.defaults,$.fn.datagrid.prm_custom);


/**
 * 超过长度的字符串截取
 * add by 李鹏
 * @param str 文本
 * @param width 宽度
 * @return 封装后的文本
 */
function dataLimit(str,width){
	var str=str?String(str):str;
	if(str&&str.length){
		var totalCount=5.0;
		for (var i=0; i<str.length; i++) { 
			var c = str.charCodeAt(i); 
			if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) { 
	            totalCount+=7.5; 
	        }else {     
	            totalCount+=12; 
	        } 
			if(totalCount>=width){
				return "<div title='"+str+"'>"+str.substring(0,i)+"...</div>";
			}
		 }
	}
	return str;
}

		
/**   
 * 表格提示框组件   
 */   
$.extend($.fn.datagrid.methods, {    
    /** 
     * 开打提示功能   
     * @param {} jq   
     * @param {} params 提示消息框的样式   
     * @return {}   
     */   
    doCellTip : function(jq, params) {
        function showTip(data, td, e) {
            if ($(td).text() == "")    
                return;    
			//alert(data.tooltip.html());
			//alert($(td).text());
            data.tooltip.text($(td).text()).css({    
				top : (e.pageY + 10) + 'px',    
				left : (e.pageX + 20) + 'px',    
				//'z-index' : $.fn.window.defaults.zIndex,    
				'z-index' : 100, 
				display : 'block'
			});    
			
        };    
        return jq.each(function() {
            var grid = $(this);    
            var options = $(this).data('datagrid');    
            if (!options.tooltip) {    
                var panel = grid.datagrid('getPanel').panel('panel');    
                var defaultCls = {
                    'border' : '1px solid #333',    
                    'padding' : '1px',    
                    'color' : '#333',    
                    //'background' : '#f7f5d1',   
					'background' : '#ffffff',					
                    'position' : 'absolute',    
                    'max-width' : '200px',    
                    'border-radius' : '4px',    
                    '-moz-border-radius' : '4px',    
                    '-webkit-border-radius' : '4px',    
                    'display' : 'none'    
                }    
                var tooltip = $("<div></div>").appendTo('body');    
                tooltip.css($.extend({}, defaultCls, params.cls));    
                options.tooltip = tooltip;    
                panel.find('.datagrid-body').each(function() {    
                    var delegateEle = $(this).find('> div.datagrid-body-inner').length    
                            ? $(this).find('> div.datagrid-body-inner')[0]    
                            : this;    
                    $(delegateEle).undelegate('td', 'mouseover').undelegate(    
                            'td', 'mouseout').undelegate('td', 'mousemove')    
                            .delegate('td', {    
                                'mouseover' : function(e) {
                                    if (params.delay) {    
                                        if (options.tipDelayTime)    
                                            clearTimeout(options.tipDelayTime);    
                                        var that = this;    
                                        options.tipDelayTime = setTimeout(    
                                                function() {    
                                                    showTip(options, that, e);    
                                                }, params.delay);    
                                    } else {    
                                        showTip(options, this, e);    
                                    }    
   
                                },    
                                'mouseout' : function(e) {    
                                    if (options.tipDelayTime)    
                                        clearTimeout(options.tipDelayTime);    
                                    options.tooltip.css({    
										'display' : 'none'    
									});    
                                },    
                                'mousemove' : function(e) {    
                                    var that = this;    
                                    if (options.tipDelayTime) {    
                                        clearTimeout(options.tipDelayTime);    
                                        options.tipDelayTime = setTimeout(    
                                                function() {    
                                                    showTip(options, that, e);    
                                                }, params.delay);    
                                    } else {    
                                        showTip(options, that, e);    
                                    }    
                                }    
                            });    
                });    
   
            }    
   
        });    
    },    
    /** 
     * 关闭消息提示功能   
     * @param {} jq   
     * @return {}   
     */   
    cancelCellTip : function(jq) {    
        return jq.each(function() {    
                    var data = $(this).data('datagrid');    
                    if (data.tooltip) {    
                        data.tooltip.remove();    
                        data.tooltip = null;    
                        var panel = $(this).datagrid('getPanel').panel('panel');    
                        panel.find('.datagrid-body').undelegate('td',    
                                'mouseover').undelegate('td', 'mouseout')    
                                .undelegate('td', 'mousemove')    
                    }    
                    if (data.tipDelayTime) {    
                        clearTimeout(data.tipDelayTime);    
                        data.tipDelayTime = null;    
                    }    
                });    
    }    
});   


