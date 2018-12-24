/**
 * @fileoverview 卓望数码 jQuery Common Library
 * @description:封装一些系统公用模块
 * @author oCEAn Zhuang (zhuangruhai@aspirecn.com QQ: 153414843)
 * @version 1.0
 * @date 2014-5-28
 */
// 进度条
var Loading = {
	id : '_loading',
	isShow : false,
	show : function() {
		if (!document.getElementById('_loading')) {
			$('body')
					.append(
							'<div class="loading smaller lighter grey" id="'
									+ this.id
									+ '" style="width:150px;"><i class="ace-icon fa fa-spinner fa-spin green bigger-125"></i>&nbsp;正在处理,请稍候...</div>');
			this.$load = $('#' + this.id);
		}
		this.$load.floatDiv({
			show : true
		});
		Loading.isShow = true;
	},
	hide : function() {
		if (this.$load){
			this.$load.floatDiv({
				show : false
			});
			Loading.isShow = false;
		}
	}
};
$(document).ajaxComplete(function(event, xhr, settings) {
	var $ifm = $(top.document).find('iframe:visible');
	var contentH = $ifm.contents().find('body').height();
	
	$ifm.height(contentH);
});
$(document).ajaxComplete(function(event, xhr, settings){
	Loading.hide();
	try{
		var result = jQuery.parseJSON(xhr.responseText);
		/* 用户会话失效或为登录，重定向至登录页面 */
		if(result && result['returnCode'] && result['returnCode']  != '0' && result['redirectUrl']) {
				window.location.replace(result['redirectUrl']);
		}
	}catch(e){}
});
$(document).ajaxStart(function(event, jqxhr, settings){
	if (Loading.isShow )return;
	Loading.show();
});
// 为iframe增加loading
try {
	if (parent != self) {
		var iframe = $(window.parent.document).find('iframe');
		if (iframe && iframe.get(0).tagName == 'IFRAME') {
			iframe.bind('load', function() {
				Loading.hide();
			});
			Loading.show();
		}
	}
} catch (e) {
}
// 阻止回退键的默认行为
$(document)
		.bind(
				"keydown",
				function(e) {
					if (e.which == 8) {
						var targetNodeName = '';
						if (e.target && e.target.nodeName) {
							targetNodeName = e.target.nodeName.toLowerCase();
						}
						if ((targetNodeName != 'input'
								&& targetNodeName != 'textarea' && targetNodeName != 'password')
								|| e.target.readOnly == true) {
							return false;
						}
					}
				});
(function() {
	try {
		// validator 添加默认提示信息
		$.extend(jQuery.validator.messages, {
			required : "不能为空",
			remote : "请修正该字段",
			email : "无效的电子邮件",
			url : "不是合法的网址",
			date : "不是合法的日期",
			dateISO : "不是合法的日期 (ISO).",
			number : "不是合法的数字",
			digits : "只能输入整数",
			creditcard : "不是合法的信用卡号",
			equalTo : "请再次输入相同的值",
			accept : "请输入拥有合法后缀名的字符串",
			maxlength : $.validator.format("请输入一个长度最多是 {0} 的字符串"),
			minlength : $.validator.format("请输入一个长度最少是 {0} 的字符串"),
			rangelength : $.validator.format("请输入一个长度介于 {0} 和 {1} 之间的字符串"),
			range : $.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
			max : $.validator.format("请输入一个最大为{0} 的值"),
			min : $.validator.format("请输入一个最小为{0} 的值")
		});
		// validator 手机校验
		$.validator.addMethod("mobile", function(value, element) {
			var tel = /^1\d{10}$/;
			return this.optional(element) || (tel.test(value));
		}, "请输入有效的手机号码");
		$.validator.addMethod("maxlength", function(value, element,param) {
			var len = $.trim(value).replace(/[^\x00-\xff]/g, '..').length;
			return this.optional(element) || (len <=param);
		}, "不能超过{0}个字节");
		$.validator.addMethod("password", function(value, element) {
			var reg = /^(?=.*\d)(?=.*[A-Za-z])[0-9a-zA-Z]/;
			return this.optional(element) || (reg.test(value));
		}, "必须包含数字和字母");
		//validator添加ace样式效果
		$.validator.setDefaults({
			errorElement : 'div',
			errorClass : 'help-block',
			focusInvalid : false,
			focusCleanup : false,
			highlight : function(e) {
				$(e).closest('.form-group').removeClass('has-info').addClass(
						'has-error');
			},
			success : function(e) {
				$(e).closest('.form-group').removeClass('has-error');// .addClass('has-info');
				$(e).remove();
			},
			errorPlacement : function(error, element) {
				if (element.is(':checkbox') || element.is(':radio')) {
					var controls = element.closest('div[class*="col-"]');
					if (controls.find(':checkbox,:radio').length > 1)
						controls.append(error);
					else
						error.insertAfter(element.nextAll('.lbl:eq(0)').eq(0));
				} else if (element.is('.select2')) {
					error.insertAfter(element
							.siblings('[class*="select2-container"]:eq(0)'));
				} else if (element.is('.chosen-select')) {
					error.insertAfter(element
							.siblings('[class*="chosen-container"]:eq(0)'));
				} else
					error.insertAfter(element.parent());
			}

		});
		$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
			_title : function(title) {
				var $title = this.options.title || '&nbsp;';
				if (("title_html" in this.options)
						&& this.options.title_html == true) {
					title.html($title);
				} else {
					title.text($title);
				}
			}
		}));
	} catch (e) {
	}
	
})();

function jqGrid_init($grid_selector,pager_selector,options){
	
	$(window).on('resize.jqGrid', function () {
		var pageContentWidth = $(".page-content").width();
		$grid_selector.jqGrid('setGridWidth', pageContentWidth  - 1);
		$('#gbox_' + $grid_selector.attr('id')).parent().find('.ui-jqgrid,.ui-jqgrid-view,.ui-jqgrid-hdiv,.ui-jqgrid-bdiv,.ui-jqgrid-pager').width(pageContentWidth);//修复1px引起的横向滚动条
		//$grid_selector.setGridHeight($('#gbox_' + $grid_selector.attr('id')).parent().find('.ui-jqgrid-bdiv table').height() + 2);
   });
   $grid_selector.jqGrid($.extend({
		datatype: "json",
		height: "auto",
		//width: "100%",
		rowNum:10,
		rowList:[10,20,30,60],
		pager: pager_selector,
		autowidth: true,
		viewrecords: true,
		altRows: true,
		beforeRequest : function(){
			var params = $(this).getGridParam(),url = params['url'],postData =  params['postData'];
			var columnName = postData['sidx'];
			if (columnName){
				var column = $(this).getColProp(columnName);
				var sortName = column['sortname'] || postData['sidx'];
				url = addUrlParam(url,"sortName",sortName);
				url = addUrlParam(url,"order",postData['sord']);
				$(this).setGridParam({"url":url});
			}
			
		},
		//multiselect: true,
		loadComplete : function() {
			$.permCheck.run();
			var table = this;
			setTimeout(function(){
				styleCheckbox(table);
				//updateActionIcons(table);
				updatePagerIcons(table);
				//enableTooltips(table);
			}, 0);
		}
	},options));
   $grid_selector.jqGrid('navGrid',pager_selector,
	   {
		edit:false,add:false,del:false,
		search: false,
		refresh: true,
		refreshicon : 'icon-refresh green'
	   }
	);
    //replace icons with FontAwesome icons like above
	function updatePagerIcons(table) {
		var replacement = 
		{
			'ui-icon-seek-first' : 'ace-icon fa fa-angle-double-left bigger-140',
			'ui-icon-seek-prev' : 'ace-icon fa fa-angle-left bigger-140',
			'ui-icon-seek-next' : 'ace-icon fa fa-angle-right bigger-140',
			'ui-icon-seek-end' : 'ace-icon fa fa-angle-double-right bigger-140'
		};
		$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
			var icon = $(this);
			var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
			
			if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
		});
	}

     function styleCheckbox(table) {
		$(table).find('input:checkbox').addClass('ace')
		.wrap('<label />')
		.after('<span class="lbl align-top" />');

		$('.ui-jqgrid-labels th[id*="_cb"]:first-child')
		.find('input.cbox[type=checkbox]').addClass('ace')
		.wrap('<label />').after('<span class="lbl align-top" />');
	
	}
	
}



//重置form,添加validator样式清除
function resetForm($form, $validator) {
	$form[0].reset();
	if ($validator) {
		$validator.resetForm();
		$form.find('.form-group').removeClass('has-error');
	}
}


var Q_Alert_Fail =  function(content,callback){
	var _content = '<div class="red" id="dialog-message"><i class="ace-icon fa fa-exclamation-triangle red"></i> ' + content + '</div>';
	bootbox.dialog({
		message: _content,
		closeButton : false,
		callback : callback,
		buttons: 			
				{
					"danger" :
					 {
						"label" : "<i class='ace-icon fa fa-check'></i>确定",
						"className" : "btn-sm btn-success",
						"callback": function() {
							if (typeof callback === 'function'){
								callback.call(this);
							}
						}
					}
				}
	});
	//$._setPosition($('.modal-dialog'));
};
var Q_Alert =  function(content,callback){
	var _content = '<div class="green" id="dialog-message"><i class="ace-icon fa fa-check"></i> ' + content + '</div>';
	bootbox.dialog({
		message: _content,
		closeButton : false,
		callback : callback,
		buttons: 			
				{
					"success" :
					 {
						"label" : "<i class='ace-icon fa fa-check'></i>确定",
						"className" : "btn-sm btn-success",
						"callback": function() {
						 if (typeof callback === 'function'){
							 callback.call(this);
						}
					}
				}
			}
	});
	//$._setPosition($('.modal-dialog'));
};



var Q_Alerttimeout =  function(content,interval,callback){
	var _content = '<div style="height:50px;" class="green" id="dialog-message"><i class="ace-icon fa fa-check"></i> ' + content + '</div>';
	bootbox.dialog({
		message: _content,
		closeButton : false,
		callback : callback,
		
	});
	window.setTimeout(callback,interval); 
	//$._setPosition($('.modal-dialog'));
};

var Q_Confirm =  function(content,callback){
	bootbox.setDefaults({
		locale : 'zh_CN'
	});
	var _defaults = {
		message: content,
		buttons: {
		  confirm: {
			 label: "确定",
			 className: "btn-danger btn-sm",
		  },
		  cancel: {
			 label: "取消",
			 className: "btn-sm",
		  }
		},
		callback: callback
	  };
	bootbox.confirm(_defaults);
	//$._setPosition($('.modal-dialog'));
};
var Q_Prompt =  function(content,callback){
	bootbox.setDefaults({
		locale : 'zh_CN'
	});
	bootbox.prompt(content, function(result) {
		if (typeof callback === 'function'){
			callback.call(this,result);
		}
	});
	//$._setPosition($('.modal-dialog'));
};

(function() {
	jQuery.extend(jQuery.fn, {
		modal2: function(config) { 
			if (!config || config === 'show' || config['show'] != false){
				//$._setPosition($(this).find('.modal-dialog'));
				$(this).drag({handler:'div.modal-header,div.modal-header > h4',opacity: 0.75});
			}
			$(this).modal(config);
		}
	});
})(jQuery);
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
			this.options.url = (window.ctxPaths || '' )+ "/auth/pageAuth.ajax";
		}
	};
	PermCheck.prototype={
		_setBehavior : function(result){
				//判断是原生button还是链接button
				if ($(this).attr("behavior") == 'delete'){
					if (!result){
						$(this).remove();
					}
				}else{
					if("button" == $(this).attr("type") || "submit" == $(this).attr("type")){
						if($(this).attr("behavior") == 'disable'){
							$(this).attr("disabled",(result?false:true));		
						 }else{
							 if (result){
								 $(this).show();
							 }else{
								 $(this).hide();
							 }
						 }
					}else{
						if (result){
							 $(this).show();
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
			var cache = $.data(document.body,'PermCheck') || {};
			//获取权限检查数组
			$('[permCheck]').each(function(){
				var permCheckValue = $(this).attr("permCheck");
				var arr=permCheckValue.split(",");
		        var resKey;
		        var operKey;
		        var targetObj = this;
		        var behavior;
		        if(arr.length==1){
		            resKey=_this.options.resKey;
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
		        var _result = cache[permCheckValue];
		       if (_result != null && _result != 'undefined'){
		    	   _this._setBehavior.call(targetObj, _result);
		       }else{
		    	   resKeyArray.push(resKey);
				   operKeyArray.push(operKey);
				   $(targetObj).attr("behavior", behavior);
				   targetObjArray.push(targetObj);
		       }
			});
			
			
			//定义回调函数
			var cb = function(r){
				if(r.success){
					var data = r.data;
					for(var i=0;i<data.length;i++){
						_this._setBehavior.call(targetObjArray[i], data[i].result);
						cache[$(targetObjArray[i]).attr('permCheck')] = data[i].result;
						/**if(data[i].result==false){
							if($(targetObjArray[i]).attr("behavior")=='disable'){
								_this._setBehavior.call(targetObjArray[i], 'disable');
							}else{
								_this._setBehavior.call(targetObjArray[i]);
							}
						}**/
					}
					$.data(document.body,'PermCheck',cache);
					if (_this.options['callback'] && typeof _this.options.callback == 'function'){
						_this.options.callback.call(this,data);
					}
				}
				
			};
			
			//发请求到后台批量检查权限
			if(resKeyArray.length > 0){
				var resKeys = resKeyArray.join(',');
				var operKeys = operKeyArray.join(',');
				/**$.iframeSubmit({
					url : _this.options.url,
					params : {'resKeys':resKeys,'operKeys':operKeys},
					callback : cb
				});**/
				$.post(_this.options.url,{'resKeys':resKeys,'operKeys':operKeys},cb);
			}else{
				if (_this.options['callback'] && typeof _this.options.callback == 'function'){
					_this.options.callback.call(this,[]);
				}
			}
			
		}
	};
	
	$.permCheck = new PermCheck(); // singleton instance
	$.permCheck.initialized = true;
	$.permCheck.setDefaults =  function(){
		var values = {};
		if (arguments.length === 2) {
		      // allow passing of single key/value...
		      values[arguments[0]] = arguments[1];
		    } else {
		      // ... and as an object too
		      values = arguments[0];
		    }
		    $.extend(this.options, values);
	};
})(jQuery);

var ifmAutoHeight =  function(){
	/*if (window.parent) {
		var vHeight = document.body.offsetHeight;//ff,chrom
		if(!+[1,])vHeight = document.body.scrollHeight;//兼容ie
		var curl = window.location.href,curls = curl.split('#');
		var parentIfmId = '';
		if (curls.length > 1){
			parentIfmId = curls[curls.length - 1];
		}else{
			parentIfmId = window.parent.cache.getCache(document.referrer);
		}
		if (parentIfmId && window.parent.document.getElementById(parentIfmId)){
			var _vHeight = $("#" + parentIfmId).data('_initHeight');
			if (_vHeight != vHeight){
				window.parent.document.getElementById(parentIfmId).style.height = vHeight + "px";
				$("#" + parentIfmId).data('_initHeight',vHeight);
			}
			//window.parent.cache.setCache(location.href.split('#')[0],parentIfmId);
		}	
	}*/
}
$(function(){
	if (self != top){
		
		var ifmTimer = null;
		function delayIfmAutoHeight(){
			if (ifmTimer)return;
			ifmTimer = setTimeout(function(){ifmAutoHeight();ifmTimer=null;},500);
		}
		if(window.attachEvent){
			if (!!document.documentMode){
				document.documentElement.attachEvent('onDOMSubtreeModified', delayIfmAutoHeight, false);
			}else{
				document.documentElement.addEventListener('DOMSubtreeModified', delayIfmAutoHeight, false);
			}
			
		}else{
			document.documentElement.addEventListener('DOMNodeInserted', delayIfmAutoHeight, false);
			document.documentElement.addEventListener('DOMNodeRemoved',delayIfmAutoHeight, false);
		}
		try {
			$(window)
					.resize(
							function() {
								delayIfmAutoHeight();
							});
		} catch (e) {
			alert(e);
		}
		setTimeout(function(){
			delayIfmAutoHeight();
		},100);

	}
});
