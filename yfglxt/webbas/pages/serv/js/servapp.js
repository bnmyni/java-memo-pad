var max = 10;

function cancel_onclick(){
	var _li = $(parent.document).find('.nav-tabs .active');
	var lis = $(parent.document).find('.nav-tabs li'), ind = lis.index(_li), next = ind + 1;
	if (_li.hasClass('active')) {
		if (ind == lis.length - 1) {
			next = 0;
		}
		if (lis.length > 1) {
			lis.eq(next).trigger('click');
		}
	}
	$(parent.document).find('.nav-tabs li').eq(ind).remove();
	$(parent.document).find('.tabcontent .tab-pane').eq(ind).remove();		
}

function btn_cancel_onclick(s_title, s_url, reload_flag){
	var _li = $(parent.document).find('.nav-tabs .active');
	var lis = $(parent.document).find('.nav-tabs li'), ind = lis.index(_li);
	var next = lis.index(lis.find('a[title="' + s_title + '"]').parent('li'))

	if(next >= 0){
		if(reload_flag){
			lis.eq(next).attr('reloadF','reload');
		}
		lis.eq(next).trigger('click');
	}else{
		initTab1(s_title, s_url);
	}
	/**
	if (_li.hasClass('active')) {
		if (ind == lis.length - 1) {
			next = 0;
		}
		if (lis.length > 1) {
			lis.eq(next).trigger('click');
		}
	}
	**/
	$(parent.document).find('.nav-tabs li').eq(ind).remove();
	$(parent.document).find('.tabcontent .tab-pane').eq(ind).remove();		
}

function close_current_tag_and_redirct(s_title, s_url, reload_flag){
	btn_cancel_onclick(s_title, s_url, reload_flag);
}

function close_current_tag_and_redirct_by_pagename(s_pagename, reload_flag){
	var s_title = "首页";
	var s_url = window.ctxPaths + "/pages/main.shtml";
	
	if("mainPage" == s_pagename){
		s_title = "首页";
		s_url = window.ctxPaths + "/pages/main.shtml";
	}
	
	if("myApply" == s_pagename){
		s_title = "我的申请";
		s_url = window.ctxPaths + "/pages/serv/myApply.shtml";
	}
	
	if("myReadyApproval" == s_pagename){
		s_title = "待我审批";
		s_url = window.ctxPaths + "/pages/serv/myReadyApproval.shtml";
	}
	
	if("myApproval" == s_pagename){
		s_title = "我审批的";
		s_url = window.ctxPaths + "/pages/serv/myApproval.shtml";
	}
	close_current_tag_and_redirct(s_title, s_url, reload_flag);
}

function CheckFigure_onKeyup(obj) {
	var s = $(obj).val();
	s = s.replace(/[^\d]/g, "");
	$(obj).val(s);
}

function CheckFigure_onBlur(obj) {
	var s = $(obj).val();
	s = s.replace(/[^\d]/g, "");
	$(obj).val(s);
}

function txtMax_onKeyup(obj,max) {
	var s = $(obj).val();
	if(s==""&&s.length<=0){
		return;
	}
	$(obj).val(s.substr(0,max));
}

function txtMax_onBlur(obj,max) {
	var s = $(obj).val();
	if(s==""&&s.length<=0){
		return;
	}
	$(obj).val(s.substr(0,max));
}

function txtMaxCode_onKeyup(obj,max) {
	var s = $(obj).val();
	if(s==""&&s.length<=0){
		return;
	}
	$(obj).val(getSubStrByCode(s,max));
}

function txtMaxCode_onBlur(obj,max) {
	var s = $(obj).val();
	if(s==""&&s.length<=0){
		return;
	}
	$(obj).val(getSubStrByCode(s,max));
}

function getSubStrByCode(str,max){
	var realLength = 0, len = str.length, charCode = -1, realStr="", tempStr = "";
	for (var i = 0; i < len; i++) {
		charCode = str.charCodeAt(i);
		tempStr = str.substr(i,1);
		if (charCode >= 0 && charCode <= 128){
			realLength += 1;
		}else{
			realLength += 2;
		}
		if(realLength>max){
			break;
		}else{
			realStr+=tempStr;
		}
	}
	return realStr;
}
function date_onBlur(obj){
	var datStr = /^(\d{4})-(\d{2})-(\d{2})$/;
	var s = $(obj).val();
	if (!datStr.test(s)) {  
		$(obj).val('');
	} 
}

function initNavTabEvent1() {
	var context = parent.document;
	$('.nav-tabs li',context)
			/*.unbind('click')
			.bind('click', function() {
				var ind = $('.nav-tabs li',context).index($(this));
				var win = $('.tabcontent .tab-pane',context).hide().eq(ind).find('iframe'),
					src = win.attr('src');
				win = win[0].contentWindow;
				$('.nav-tabs li',context).removeClass('active');
				$(this).addClass('active');
				$('.tabcontent .tab-pane',context).hide().eq(ind).show();
				if(navigator.userAgent.indexOf("MSIE")>0){  
					window.location.href=window.location.href;
				}else{
					window.location.reload();
				}
				$('#focus_input_iframe',context).focus();
				win.location.href = src;
				//win.attr('src',src);
			})*/
			.unbind('mouseover')
			.bind(
					'mouseover',
					function() {
						// if (len == 1)return;
						if ($(this).find('div.closeitem',context).length > 0 || $(this).attr('doNotClose')) {
							return;
						}
						var $closeItem = $('<div class="closeitem"> <i class="ace-icon fa fa-times red2"></i></div>',context);
						$(this).append($closeItem);
						$closeItem
								.bind(  'click',
										function(e) {
											e.stopPropagation();
											var _li = $(this).closest('li');
											var lis = $('.nav-tabs li',context), ind = lis
												.index(_li), next = ind + 1;
											if (_li.hasClass('active')) {
												if (ind == lis.length - 1) {
													next = 0;
												}
												if (lis.length > 1) {
													lis.eq(next).trigger(
															'click');
												}
											}
											$('.nav-tabs li',context).eq(ind).remove();
											$('.tabcontent .tab-pane',context).eq(ind)
												.remove();

										});

					}).unbind('mouseout').bind('mouseout', function() {
				var _this = this;
				// if (len == 1)return;
				setTimeout(function() {
					var $closeItem = $(_this).find('div.closeitem',context);
					$closeItem.remove();
				}, 1000);

			});
}
function initTab1(title, url) {
	var $lis = $('.tabbable li', parent.document), lisLen = $lis.length;
	$tabPanes = $('.tabcontent .tab-pane', parent.document), ifmId = '_ifm'
			+ (new Date()).getTime();
	var isExist = $lis.find('a[title="' + title + '"]').length;
	// $tabPanes.find('iframe').attr('id','').attr('name','');
	if (isExist >0) {
		var _a = $lis.find('a[title="' + title + '"]'), $li = _a.closest('li'), num = $lis
				.index($li), ifm = $tabPanes.find('iframe',parent.document).eq(num);
		// ifm.attr('id','mainFrame').attr('name','mainFrame');
		var urls = ifm.attr('src').split('#');

		if (urls[0] != url) {
			ifm.attr('id', ifmId);
			ifm.attr('src', url);
			$lis.removeClass('active');
			$li.addClass('active');
			$tabPanes.hide().eq(num).show();
			return;
		}
		$li.trigger('click');
		return;
	}
	if (lisLen < max) {
		var _a = $('<a></a>').attr('href', 'javascript:;').attr('title', title)
				.html('<i class="fa fa-star "></i> ' + title);
		var urls = url.split('#');
		var _div = '<div class="tab-pane"><iframe width="100%" height="600" frameborder="no" style="overflow:hidden;" src="'
				+ (urls[0] )
				+ '" name="mainFrame" id="'
				+ ifmId
				+ '" allowtransparency="true"></iframe></div>';
		$('.tabcontent', parent.document).append(_div);
		$('.nav-tabs', parent.document).append($('<li></li>').append(_a));
		
		//initNavTabEvent1();
	/*	var ind = $('.nav-tabs li',parent.document).index($(this));
		$('.nav-tabs li',parent.document).removeClass('active');
		$(this).addClass('active');
		$('.tabcontent .tab-pane',parent.document).hide().eq(ind).show();*/
		_a.closest('li').trigger('click');
	} else {
		var selectedInd = $lis.index($('.tabbable li.active', parent.document));
		var nextInd = (selectedInd == max - 1 ? 0 : selectedInd + 1);
		// $tabPanes.find('iframe').attr('id','').attr('name','');
		$tabPanes.find('iframe',parent.document).eq(nextInd).attr('src', 'about:blank').attr(
				'id', ifmId).attr('name', 'mainFrame').attr('src',
				url );
		$lis.eq(nextInd).find('a').attr('title', title).text(title);
		$lis.eq(nextInd).trigger('click');
	}
}