/** 
 * @fileoverview  卓望数码 jQuery Common Library
 * @description:封装一些jQuery公共操作方法
 * @author oCEAn Zhuang (zhuangruhai@aspirecn.com QQ: 153414843)
 * @version 1.0
 * @date 2013-09-18
 */
/**
 * 将form属性转化为JSON对象，支持复选框和select多选
 * @param {Object} $
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
;
(function($){
	$.fn.serializeJson = function(){
	var serializeObj = {};
	var array = this.serializeArray();
	var str = this.serialize();
	$(array).each(function(){
		if(serializeObj[this.name]){
			if($.isArray(serializeObj[this.name])){
				serializeObj[this.name].push(this.value);
			}else{
				serializeObj[this.name]=[serializeObj[this.name],this.value];
			}
		}else{
			serializeObj[this.name]=this.value;
		}
	});
	return serializeObj;
};
})(jQuery);
/**
 * 对form进行jQuery扩展，可将json数据映射到对应的表单元素里,json中的属性名需跟form表单里的元素name一致。
 * @namespace jQuery扩展封装
 * @param {json} json格式数据
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		json2Form: function(json) {
			var _this = this;
			jQuery.each(jQuery(_this).serializeArray(),
				function(index) {
					var name = this['name'];
					for (var a in json) {
						var key = "";
						var val = "";
						if (name.indexOf('.') != -1) {
							key = name.split('.')[0];
							var getval = name.split('.')[1];
							val = json[a][getval];
						} else {
							key = name;
							val = json[a];
						}
						if (jQuery.trim(key) == jQuery.trim(a)) {
							var eve = jQuery(_this).find("[name='" + name + "']");
							if (jQuery(eve).length > 1) {
								for (var i = 0; i < jQuery(eve).length; i++) {
									//判断单选按钮  
									if (jQuery(jQuery(eve)[i]).attr("type") == 'radio') {
										if (jQuery(jQuery(eve)[i]).val() == val) {
											jQuery(jQuery(eve)[i]).attr("checked", true);
										}
									}
								}
							} else {
								jQuery(eve).val(val);
							}
						}
					}
				});
		}
	});
})(jQuery);

$.extend({
	str2Value: function(p,str) {
		var strs = str.split('.');
		var _tt = p;
		for (var i = 0;i < strs.length; i++){
				var tmp = strs[i];
				if (tmp.indexOf('[') != -1){
					var pre = tmp.substring(0,tmp.indexOf('[')),num = tmp.substring(tmp.indexOf('[') + 1,tmp.indexOf(']'));
					if (_tt[pre][num] && typeof (_tt[pre][num]) != 'undefined'){
						_tt = _tt[pre][num];
						continue;
					}else{
						return null;
					}
				}
				if ((_tt[strs[i]]==0||_tt[strs[i]])&& typeof (_tt[strs[i]]) != 'undefined'){
					_tt = _tt[strs[i]];
					continue;
				}else{
					return null;
				}
		}
		return  _tt;
	}
});

;
(function() {
	jQuery.extend(jQuery.fn, {
		json2Form2: function(json) {
			var _this = this,tmp = json,_form = $(this).attr('id');
			if (!_form){
				_form = (new Date()).getTime();//随机生成一个ID
				$(this).attr('id',_form);
			}
			jQuery("#" + _form + " :input" ).each(function(i){
				var _name = $(this).attr('name');
				if (_name){
					var vv = $.str2Value(tmp,_name);
					if (vv){
						var _type = $(this).attr("type");
						if (_type == 'text' || _type == 'hidden'){
							$(this).val(vv);
						} else if (_type == 'radio'){
							$("#" + _form +" :input[type=radio][name='" + _name + "'][value='" + vv + "']").attr("checked","checked"); 
						} else if (_type == 'checkbox'){
							var arr = vv.split(',');
							for (var i = 0; i < arr.length ;i++){
								$("#" + _form +" :input[type=checkbox][name='" + _name + "'][value='" + arr[i] + "']").attr("checked","checked");
							}
						} 
						
					}
				}
			});
			jQuery("#" + _form + " textarea" ).each(function(i){
				var _name = $(this).attr('name');
				if (_name){
					var v = 'tmp.' + _name;
					var vv = $.str2Value(tmp,_name);
					if (vv){
						$(this).val(vv);
					}
				}
			});
			jQuery("#" + _form + " select" ).each(function(i){
				var _name = $(this).attr('name');
				if (_name){
					var vv = $.str2Value(tmp,_name);
					if (vv){
						if ($(this).attr('multiple')){
							var _ss = vv.split(',');
							for (var n = 0; n < _ss.length; n++){
								$(this).children('option[value="'+_ss[n] + '"]').attr("selected","selected");
							}
						}else{
							//if($(this).get(0).length > 0){
								$(this).val(vv);
							//}else{
								$(this).attr('_defaultValue',vv);
							//}
						}
						$(this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
					}
				}
			});
		}
	});
})(jQuery);

$.extend({
	dataInput: function(elems, datas, callback) {
		var _tmp = datas;
		elems.each(function(i){
			var _name = $(this).attr('name');
			if (_name){
				var vv = $.str2Value(_tmp,_name);
				if (vv==0||vv){
					vv = '' + vv;
					if ($(this).is('div') || $(this).is('span') || $(this).is('label') ){
						$(this).html(vv);
					}else if ($(this).is('input')){
						var _type = $(this).attr("type");
						if (_type == 'text' || _type == 'hidden'){
							$(this).val(vv);
						} else if (_type == 'radio'){
							if ($(this).val() == vv){
								$(this).attr("checked","checked")
							} 
						} else if (_type == 'checkbox'){
							var arr = vv.split(',');
							for (var i = 0; i < arr.length ;i++){
								if ($(this).val() == arr[i]){
									$(this).attr("checked","checked")
								} 
							}
						} 
					}else if ($(this).is('select')){
						//if($(this).get(0).length > 0){
							$(this).val(vv);
						//}else{
							$(this).attr('_defaultValue',vv);
						//}
						$(this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
						/**var _tt = vv.split(',');
						for (var n = 0; n < _tt.length; n++ ){
							if ($(this).val() == _tt[n]){
								$(this).attr("selected","selected");
							}
						}**/
					}else if ($(this).is('textarea')){
						$(this).val(vv);
					}
					
				}
			}
		});
		if (typeof callback != 'undefined') {
			callback(datas);
		}
	}
});
$.extend({
	dataInputHtml: function(elems, datas, callback) {
		var _tmp = datas;
		elems.each(function(i){
			var _name = $(this).attr('name');
			if (_name){
				var vv = $.str2Value(_tmp,_name);
				if (vv==0||vv){
					vv = '' + vv;
					if(vv!=null && vv!=""){
				    	vv = vv.replace(/\n/g,"</br>");
				    }
					if ($(this).is('div') || $(this).is('span') || $(this).is('label') ){
						$(this).html(vv);
					}else if ($(this).is('input')){
						var _type = $(this).attr("type");
						if (_type == 'text' || _type == 'hidden'){
							$(this).val(vv);
						} else if (_type == 'radio'){
							if ($(this).val() == vv){
								$(this).attr("checked","checked")
							} 
						} else if (_type == 'checkbox'){
							var arr = vv.split(',');
							for (var i = 0; i < arr.length ;i++){
								if ($(this).val() == arr[i]){
									$(this).attr("checked","checked")
								} 
							}
						} 
					}else if ($(this).is('select')){
						//if($(this).get(0).length > 0){
							$(this).val(vv);
						//}else{
							$(this).attr('_defaultValue',vv);
						//}
						$(this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
						/**var _tt = vv.split(',');
						for (var n = 0; n < _tt.length; n++ ){
							if ($(this).val() == _tt[n]){
								$(this).attr("selected","selected");
							}
						}**/
					}else if ($(this).is('textarea')){
						$(this).val(vv);
					}
					
				}
			}
		});
		if (typeof callback != 'undefined') {
			callback(datas);
		}
	}
});
$.extend({
	dataSubString: function(elems,callback) {
		elems.each(function(i){
			var subStringLength = $(this).attr('subStringLength');
			if (subStringLength){
					if ( $(this).is('span')){
						var spanText = $(this).text();
						var spanTextLength = $(this).text().replace(/[^\x00-\xff]/g, ' ').length;
						if(spanTextLength && spanTextLength > subStringLength){
						    var subStringSpanText = spanText.substring(0,subStringLength)+"...";
							$(this).text(subStringSpanText);
							$(this).attr('title',spanText);
						}
					}else if ($(this).is('input')){
						var inputVal = $(this).val();
						var inputValLength = $(this).val().replace(/[^\x00-\xff]/g, ' ').length;
						if(inputValLength && inputValLength > subStringLength){
						    var subStringInputVal = inputVal.substring(0,subStringLength)+"...";
							$(this).val(subStringInputVal);
							$(this).attr('title',inputVal);
						}
					}
			}
		});
		if (typeof callback != 'undefined') {
			callback(datas);
		}
	}
});

/**
 * 对ajax请求做了封装，统一项目的ajax请求。
 * @namespace jQuery扩展封装
 * @param {url} 请求的url地址
 * @param {params} JSON格式的参数,如{name:'abc','age':10}
 * @param {callback} 调用成功后回调函数,可不传
 * @return json数据
 */
;
$.extend({
	ajaxSubmit: function(url, params, callback) {
		jQuery.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if (typeof callback != 'undefined') {
					callback(data);
				}
			},
			error: function() {
				alert('发生系统错误');
			},
			beforeSend: function() {
				// Handle the beforeSend event
			},
			complete: function() {
				// Handle the complete event
			}
		});
	}
});
/**
*同步提交
*/
$.extend({
	ajaxAsyncSubmit: function(url, params, callback) {
		jQuery.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: params,
			async:false,
			success: function(data) {
				if (typeof callback != 'undefined') {
					callback(data);
				}
			},
			error: function() {
				alert('发生系统错误');
			},
			beforeSend: function() {
				// Handle the beforeSend event
			},
			complete: function() {
				// Handle the complete event
			}
		});
	}
});


/**
 * 对select进行jQuery扩展，select动态数据获取封装。
 * @namespace jQuery扩展select封装
 * @param {json} json格式数据
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		ajaxRender: function(url, config, params) { //config:{firstOption:{text:'请选择分类',value:''},keyAlias:{text:'name',value:'value'}}
			var _this = this,
				c = config || {}, keyValue = {
					text: 'name',
					value: 'value'
				};
			if (c.keyValue) {
				keyValue = c.keyValue;
			}
			$(_this).empty();
			if (c.firstOption) {
				$(_this).append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
			}
			//var data = [{'name':'选项1','value':'值1'},{'name':'选项2','value':'值2'}];
			jQuery.ajax({
				url: url,
				type: 'GET',
				dataType: 'json',
				async:false,
				data: params,
				success: function(data) {
				    var root = c.root;
				    if(root){
				    	var arr;
						if(root.indexOf(".") > 0){
							arr =root.split(".");
						}
						data = data[arr[0]][arr[1]];
				    }
					if (data) {
						$(_this).empty();
						if (c.firstOption) {
							$(_this).append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
						}
						for (var i = 0; i < data.length; i++) {
							$(_this).append('<option value="' + data[i][keyValue.value] + '">' + data[i][keyValue.text] + '</option>');
						}
						if ($(_this).attr('_defaultValue')){
							$(_this).val($(_this).attr('_defaultValue'));
							$(_this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
						}
					}
				},
				error: function() {
					alert('列表数据获取发生系统错误......');
				},
				beforeSend: function() {
					// Handle the beforeSend event
				},
				complete: function() {
					// Handle the complete event
					//select下拉框选中
					if(c.selectedValue){
								$(_this).val(c.selectedValue);
							}
				}
			});
		},
		localRender:function(data, config){ //config:{firstOption:{text:'请选择分类',value:''},keyAlias:{text:'name',value:'value'}}
			var _this = this,
				c = config || {}, keyValue = {
					text: 'name',
					value: 'value'
				};
			if (c.keyValue) {
				keyValue = c.keyValue;
			}
			$(_this).empty();
			if (c.firstOption) {
				$(_this).append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
			}
			//var data = [{'name':'选项1','value':'值1'},{'name':'选项2','value':'值2'}];
			if (data) {
				for (var i = 0; i < data.length; i++) {
					$(_this).append('<option value="' + data[i][keyValue.value] + '">' + data[i][keyValue.text] + '</option>');
				}
				if ($(_this).attr('_defaultValue')){
					$(_this).val($(_this).attr('_defaultValue'));
				}
			}
		}
	});
})(jQuery);


/**
 * 对移除和清空加了动态效果。
 * @namespace jQuery扩展移除和清空加了动态效果
 * @param {json} 
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		options : {
			speed : 500
		},
		fadeRemove: function(config) { 
			var _this = this;
			$.extend(_this.options, config);
			_this.fadeOut(_this.options.speed,function(){_this.remove();});
		},
		fadeEmpty: function(config) { 
			var _this = this;
			$.extend(_this.options, config);
			_this.fadeOut(_this.options.speed,function(){
				_this.empty();
				if (typeof _this.options.callback == 'function'){
					_this.options.callback();
				}
			});
		}

	});
})(jQuery);

/**
 * 浮层。
 * @namespace jQuery扩展浮层封装
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		_getViewPort: function(obj) {
			var viewportwidth = 0,
				viewportheight = 0;
			if (typeof window.innerWidth != 'undefined') {
				var obj = obj || window;
				viewportwidth = obj.innerWidth;
				viewportheight = obj.innerHeight;
			} else if (typeof document.documentElement != 'undefined' && typeof document.documentElement.clientWidth != 'undefined' && document.documentElement.clientWidth != 0) {
				var obj = obj || document.documentElement;
				viewportwidth = obj.clientWidth;
				viewportheight = obj.clientHeight;
			}
			return {
				width: viewportwidth,
				height: viewportheight
			};
		},
		_calPosition: function(w, h) {
			l = (Math.max($(document).width(), $(window).width()) - w) / 2;
			t = $(window).scrollTop() + $(window).height() / 9;

			//w = this.width(),
			//h = this.height(),
			//st = document.documentElement.scrollTop,
			//sl = document.documentElement.scrollLeft,
			//vW = this._getViewPort().width,
			//vH = this._getViewPort().height,
			//l = vW / 2 - w / 2 + sl,
			//t = vH / 2 -  h / 2 + st - 240;
			/**var t = (($(window).height() / 2) - (h / 2)) -75;
			var l = (($(window).width() / 2) - (w / 2));
			if( t < 0 ) t = 0;
			if( l < 0 ) l = 0;
			
			// IE6 fix
			if( $.browser.msie && parseInt($.browser.version) <= 6 ) t = t + $(window).scrollTop();**/
			return [l, t];
		},
		_setPosition: function(C, B) {
			if (!C) {
				return false;
			}
			var lt = this._calPosition(C.width(), C.height());
			C.css({
				left: lt[0],
				top: lt[1],
				position: 'absolute',
				'z-index': 9991
			});
			var $h = Math.max($(document).height(), $(window).height()),
				$hh = $("html").height();
			//$h = Math.max($h,$hh);
			B.height($h).width('100%');
		},

		_createMask: function() {
			var divMaskId = "_maskDivId";
			if (!document.getElementById(divMaskId)) {
				$('<div id="' + divMaskId + '" style="display:none;"></div>').appendTo('body');
			}
			this._mask = $('#' + divMaskId);
			this._mask.css({
				background: '#666666',
				filter: 'alpha(opacity=60)',
				'-moz-opacity': 0.6,
				opacity: 0.6,
				position: 'absolute',
				left: 0,
				top: 0,
				'z-index': 999
			});
			$('#' + divMaskId).bgiframe();
			this._mask.show();
			return this._mask;
		},
		floatDiv: function(options) {
			var defaults = {};
			this._YQ = $.extend(defaults, options);
			var show = this._YQ.show;
			var _this = this;
			if (this._YQ && !show) {
				$(_this).slideUp(200);
				var divMaskId = "_maskDivId";
				if (document.getElementById(divMaskId)) {
					$('#' + divMaskId).hide();
				}
				return;
			}
			var mask = this._createMask();
			$(this).slideDown(200);
			this._setPosition($(this), mask);
			if (this._YQ && this._YQ.clsBtn) {
				this._YQ.clsBtn.click(function() {
					$(_this).slideUp(200);
					mask.hide();
				});
			}
		}
	});
})(jQuery);
/**
 * 对checkbox全选做简单封装。
 * @namespace jQuery扩展函数
 * @param {chkAllId} checkbox全选Id,{chkName} 被选checkbox name,
 * @return void
 */
;
$.extend({
	checkbox_chkAll: function(chkAllId, chkName,callback) {
		var checkBoxes = $("input[name=" + chkName + "]");
		$('#' + chkAllId).click(function() {
			var isCheck = $(this).attr('checked') || false;
			$.each(checkBoxes, function() {
				$(this).attr('checked', isCheck);
			});
			if (callback) callback($('#' + chkAllId),checkBoxes);
		});
		
	}
});


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

//Tabs
//参数：按钮（JQ对象）[，对应的层（JQ对象），当前按钮class]
Tabs = function($bts,$divs,cls){
	this.bts = $bts;
	this.divs = $divs || $('<div />');
	this.cls = cls || 'up';
}
Tabs.prototype = {
	init: function(eventType,stopDefault){
		eventType = eventType || 'click';
		stopDefault = stopDefault || false;
		var _this = this;
		
		this.bts.bind(eventType,function(){
			var index = _this.bts.index(this);
			_this.bts.removeClass(_this.cls);
			$(this).addClass(_this.cls);
			_this.show(index, this);
			//return false;
		});
		this.bts.click(function(){
			return stopDefault;
		});
	},
	show: function(index, bt){
		this.divs.hide().eq(index).show();
		this.done(index, bt);
	},
	done: function(index, bt){
		
	}
}
/**
 * 获取url参数值。
 * @param n 为参数名
 **/
var getP = function(n) {
	var hrefstr, pos, parastr, para, tempstr;
	hrefstr = window.location.href;
	pos = hrefstr.indexOf("?");
	parastr = hrefstr.substring(pos + 1);
	para = parastr.split("&");
	tempstr = "";
	for (i = 0; i < para.length; i++) {
		tempstr = para[i];
		pos = tempstr.indexOf("=");
		if (tempstr.substring(0, pos).toLowerCase() == n.toLowerCase()) {
			return tempstr.substring(pos + 1);
		}
	}
	return '';
}

var isEndTimeGtStartTime = function(startTime,endTime){  
    var start=new Date(startTime.replace("-", "/").replace("-", "/"));  
    var end=new Date(endTime.replace("-", "/").replace("-", "/"));  
    if(end<start){  
        return false;  
    }  
    return true;  
}  


/**
 * ----------------------------------
 * prm 封装的一些公共组件和工具类
 * ----------------------------------
 */
//命名空间定义
Aspire = {
	prm:{
		SSL_SECURE_URL: ctxPaths + '/jquery/plugin/custom/css/image/s.gif'
	}
};
(function($){
	/**
	 * 为form表单添加导出功能
	 * @memberOf {TypeName} 
	 */
	$.fn.extend({
		exportData:function(setting){
			var id = new Date();
			frame = (
				jQuery("<iframe frameborder='0' width='0' height='0'/>")
				.attr({'id':id,'name':id,src:Aspire.prm.SSL_SECURE_URL}).addClass('export-hidden')
			).appendTo( document.documentElement );
			
	        //frame加载后的回调喊出
	        function cb(){
	            var response = ''
	
	           // r.argument = setting ? setting.argument : null;
	
	            try { //
	                var doc = frame.contents();
	                
	                if(doc && doc[0].body){//处理html数据
	                    response =eval('(' + doc.find('body').html() + ')'); 
	                }
	                if(doc && doc[0].XMLDocument){//处理xml数据
	                    response = doc[0].XMLDocument;
	                }else {//其他数据 当作文本来处理
	                    response = eval('(' + doc + ')');
	                }
		            }
	            catch(e) {
	                // ignore
	            }
				frame.unbind('load',cb);
				
				if(setting.callback && typeof setting.callback == "function"){
					setting.callback.call(frame,response,setting);
				}
	            //this.fireEvent("requestcomplete", this, r, o);
	
	            setTimeout(function(){frame.remove();}, 100);
	        }
	
	        frame.bind('load',cb);
	        //form.submit();
	        //拼参数
	        var p = setting.data;
            if(typeof p == "object"){
                p = $.param(p);
            }
	        //如果用自己定义的参数就不用从表单中取    
	        if(setting.data==null){
	                setting.url = setting.url || $(this).attr('action');
	                var f = $(this).serialize();
	                p = p ? (p + '&' + f) : f;
	         }	        
			//最终的url
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + '_dc=' + (new Date().getTime());
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + p;
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + "acceptContentType=html";
	         setting.url = $.appendExtraParams(setting.url);
	        frame.attr('src',setting.url);
		}
		
	})
})(jQuery);

(function() {
	jQuery.extend(jQuery.fn, {
		changePlus: function(callback) {
			if(window.navigator.userAgent.indexOf("MSIE")>=1){
				$(this).get(0).onpropertychange = callback;
			}else{
				$(this).bind('change',callback);
			}
		}
	});
})(jQuery);

//form表单ajax提交
//请求示例
/*
function test(){ 
	$('#form').importData({
		type: 'post',
		url: 'http://127.0.0.1:18080/admin/test/test.txt',
		dataType: 'json',
		success: function(data){  
			alert(data.data.list[0].baseId); 
		},  
		error: function(XmlHttpRequest, textStatus, errorThrown){  
			alert( "error");  
		}
	});
}
*/
(function() {
$.extend($.fn, {
	importData:function(config) {
		//表单
		//var $form = $('#'+config.id);
		var $form = $('#'+$(this).attr('id'));
		var form = $form[0];
		//alert(111);
		/*参数
		var opts = $.extend({}, $.ajaxSettings, {
			type: 'post',  
			url: "http://127.0.0.1:8080/test/test.txt" ,  
			dataType: 'json',
			success: function(data){  
				alert("success");  
				alert(data.data.list[0].baseId); 
			},  
			error: function(XmlHttpRequest, textStatus, errorThrown){  
				alert( "error");  
			}

		});*/
		var opts = $.extend({}, $.ajaxSettings, config);
		
		opts.url = $.appendExtraParams(opts.url);
		
		//alert(222);
		//var id = 'jqFormIO' + $.fn.ajaxSubmit.counter++;
		var id = new Date();
		var $io = $('<iframe id="' + id + '" name="' + id + '" />');
		var io = $io[0];
		
		//目前jquery新版本已经不支持$.browser方法了
		//var op8 = $.browser.opera && window.opera.version() < 9;
		//if ($.browser.msie || op8) io.src = 'javascript:false;document.write("");';
		
		//隐藏iframe
		$io.css({ position: 'absolute', top: '-1000px', left: '-1000px' });
		
		//alert(333);
		var xhr = { // mock object
			responseText: null,
			responseXML: null,
			status: 0,
			statusText: 'n/a',
			getAllResponseHeaders: function() {},
			getResponseHeader: function() {},
			setRequestHeader: function() {}
		};
		
		var g = opts.global;
		// trigger ajax global events so that activity/block indicators work like normal
		if (g && ! $.active++) $.event.trigger("ajaxStart");
		if (g) $.event.trigger("ajaxSend", [xhr, opts]);
		//alert(444);
		
		
		var cbInvoked = 0;
		var timedOut = 0;
		
		// take a breath so that pending repaints get some cpu time before the upload starts
		setTimeout(function() {
			$io.appendTo('body');
			// jQuery's event binding doesn't work for iframe events in IE
			//io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);
			//alert(555);
			// make sure form attrs are set
			var encAttr = form.encoding ? 'encoding' : 'enctype';
			var t = $form.attr('target');
			$form.attr({
				target:   id,
				method:  'POST',
				encAttr: 'multipart/form-data',
				action:   opts.url
			});

			// support timout
			if (opts.timeout)
				setTimeout(function() { timedOut = true; cb(); }, opts.timeout);
			//添加ifram加载事件
			io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);
			form.submit();
			//alert(666);
			$form.attr('target', t); // reset target
		}, 10);
		
		function cb() {
			if (cbInvoked++) return;
			
			io.detachEvent ? io.detachEvent('onload', cb) : io.removeEventListener('load', cb, false);

			var ok = true;
			try {
				if (timedOut) throw 'timeout';
				// extract the server response from the iframe
				var data, doc;
				//alert(777);
				doc = io.contentWindow ? io.contentWindow.document : io.contentDocument ? io.contentDocument : io.document;
				xhr.responseText = doc.body ? doc.body.innerHTML : null;
				xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;

				if (opts.dataType == 'json' || opts.dataType == 'script') {
					var ta = doc.getElementsByTagName('textarea')[0];
					data = ta ? ta.value : xhr.responseText;
					if (opts.dataType == 'json')
						
						//data = jQuery.parseJSON(jQuery(data).text());//可以去除<pre>标签
						data = jQuery.parseJSON(data);
						//eval("data = " + data);
						
					else
						$.globalEval(data);
				}
				else if (opts.dataType == 'xml') {
					data = xhr.responseXML;
					if (!data && xhr.responseText != null)
						data = toXml(xhr.responseText);
				}
				else {
					data = xhr.responseText;
				}
				//alert('999'+data);
			}
			catch(e){
				//alert(e.message);
				ok = false;
				//$.handleError(opts, xhr, 'error', e);
				opts.error(e);
			}

			// ordering of these callbacks/triggers is odd, but that's how $.ajax does it
			if (ok) {
				opts.success(data, xhr.responseText, 'success');
				if (g) $.event.trigger("ajaxSuccess", [xhr, opts]);
			}
			if (g) $.event.trigger("ajaxComplete", [xhr, opts]);
			if (g && ! --$.active) $.event.trigger("ajaxStop");
			if (opts.complete) opts.complete(xhr, ok ? 'success' : 'error');

			// clean up
			setTimeout(function() { 
				$io.remove(); 
				xhr.responseXML = null;
			}, 100);
		};
	}
	
})
})(jQuery);

jQuery.fn.limit=function(){
    //这里的div去掉的话，就可以运用li和其他元素上了，否则就只能div使用。
    this.each(function(){
		var objString = jQuery.trim($(this).text());
		var objLength = jQuery.trim($(this).text()).length;
		var num = $(this).attr("limit");
		if(num&&objLength > num){
			//这里可以设置鼠标移动上去后显示标题全文的title属性，可去掉。
			$(this).attr("title",objString);
			objString = $(this).text(objString.substring(0,num) + "...");
		}
		//如果存在该插件则初始化，反之则不初始化
		if($(this).poshytip){
			$(this).poshytip();
		}
	})
}
