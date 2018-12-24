var max = 10;
function initTabEvent() {
	$('.nav-tabs').on('click','li',function(e) {
		var ind = $('.nav-tabs li').index($(this));
			$('.nav-tabs li').removeClass('active');
			$(this).addClass('active');
			$('.tabcontent .tab-pane').hide().eq(ind).show();
		$('#focus_input_iframe').focus();
		
		 var flag = $(this).attr('reloadF');
         if(flag=='reload'){
             $(this).removeAttr('reloadF');
         }
 
		var ifm = frames[ind],
			url = ifm.location.href;
		// iframe路径已设置
		 if(url.indexOf(window.ctxPaths) != -1 && flag == 'reload') {
			ifm.location.reload(true);
		}
		 if(ind == 0){
			  ifm.location.reload(true);
         }
	})
	.on('mouseover','li',function() {
		if ($(this).find('div.closeitem').length > 0 || $(this).attr('doNotClose')) {
			return;
		}
		var $closeItem = $('<div class="closeitem"> <i class="ace-icon fa fa-times red2"></i></div>');
		$(this).append($closeItem);
		$closeItem.bind('click',function(e) {
			e.stopPropagation();
			var _li = $(this).closest('li');
			var lis = $('.nav-tabs li'), ind = lis
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
			$('.nav-tabs li').eq(ind).remove();
			$('.tabcontent .tab-pane').eq(ind).remove();
		});
	})
	.on('mouseout','li',function() {
		var _this = this;
		// if (len == 1)return;
		setTimeout(function() {
			var $closeItem = $(_this).find('div.closeitem');
			$closeItem.remove();
		}, 1000);
	});
};
/*function initNavTabEvent() {
	
	$('.nav-tabs li')
			.unbind('click')
			.bind('click', function() {
				var ind = $('.nav-tabs li').index($(this));
				$('.nav-tabs li').removeClass('active');
				$(this).addClass('active');
				$('.tabcontent .tab-pane').hide().eq(ind).show();
				console.log('init');
			})
			.unbind('mouseover')
			.bind(
					'mouseover',
					function() {
						// if (len == 1)return;
						//alert("aa");
						if ($(this).find('div.closeitem').length > 0
							|| $(this).attr('doNotClose')) {
							return;
						}
						var $closeItem = $('<div class="closeitem"> <i class="ace-icon fa fa-times red2"></i></div>');
						$(this).append($closeItem);
						$closeItem
								.bind(
										'click',
										function(e) {
											e.stopPropagation();
											var _li = $(this).closest('li');
											var lis = $('.nav-tabs li'), ind = lis
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
											$('.nav-tabs li').eq(ind).remove();
											$('.tabcontent .tab-pane').eq(ind)
													.remove();

										});

					}).unbind('mouseout').bind('mouseout', function() {
				var _this = this;
				// if (len == 1)return;
				setTimeout(function() {
					var $closeItem = $(_this).find('div.closeitem');
					$closeItem.remove();
				}, 1000);

			});
}*/
function initTab(title, url) {
	var $lis = $('.tabbable li'), lisLen = $lis.length;
	$tabPanes = $('.tabcontent .tab-pane'), ifmId = '_ifm'
			+ (new Date()).getTime();
	var isExist = $lis.find('a[title="' + title + '"]').length;
	// $tabPanes.find('iframe').attr('id','').attr('name','');
	if (isExist > 0) {
		var _a = $lis.find('a[title="' + title + '"]'), $li = _a.closest('li'), num = $lis
				.index($li), ifm = $tabPanes.find('iframe').eq(num);
		// ifm.attr('id','mainFrame').attr('name','mainFrame');
		var urls = ifm.attr('src').split('#');

		if (urls[0] != url) {
			ifm.attr('id', ifmId);
			ifm.attr('src', url + '#' + ifmId);
		}

		//ifm[0].contentWindow.location.reload(true);
		_a.trigger('click');
		return;
	}
	if (lisLen < max) {
		var _a = $('<a></a>').attr('href', 'javascript:;').attr('title', title)
				.html('<i class="fa fa-star "></i> ' + title);
		var urls = url.split('#');
		var _div = '<div class="tab-pane"><iframe width="100%" height="620" frameborder="no" style="overflow:hidden;" src="'
				+ (urls[0] + '#' + ifmId)
				+ '" name="mainFrame" id="'
				+ ifmId
				+ '" allowtransparency="true"></iframe></div>';
		$('.nav-tabs').append($('<li></li>').append(_a));
		$('.tabcontent').append(_div);
		//initNavTabEvent();
		_a.trigger('click');
	} else {
		var selectedInd = $lis.index($('.tabbable li.active'));
		var nextInd = (selectedInd == max - 1 ? 0 : selectedInd + 1);
		// $tabPanes.find('iframe').attr('id','').attr('name','');
		$tabPanes.find('iframe').eq(nextInd).attr('src', 'about:blank').attr(
				'id', ifmId).attr('name', 'mainFrame').attr('src',
				url + '#' + ifmId);
		$lis.eq(nextInd).find('a').attr('title', title).text(title);
		$lis.eq(nextInd).trigger('click');
	}
}
//initNavTabEvent();
initTabEvent();
$(function() {
	$('#logout').bind('click', function() {
		var logoutUrl = window.ctxPaths + "/pages/login.shtml";
		$.ajax({
			url : window.ctxPaths + '/portal/logout.ajax',
			type : 'POST',
			timeout : 30000,
			dataType : 'json',
			async : false,
			success : function(data) {
				if (data.success) {
					// 退出
					window.location.href = logoutUrl;
				} else {
					if (data.data && data.data.msg) {
						// alert('退出失败，原因：' + data.data.msg);

					} else {
						// alert('退出失败，原因未知');
					}
					window.location.href = logoutUrl;
				}
			},
			error : function() {
				// alert('退出失败，原因未知');
				window.location.href = window.ctxPaths + "/pages/login.shtml";
			}

		});
	});
	$('#menu-toggler').on('click',function() {
		$('#main-container').toggleClass('js-visible');
	});
	// 生成菜单
	var buildMenu = function(menus) {
		var buildMenuHtml = function(menus) {
			var html = [];
			$
					.each(
							menus,
							function(i, menu) {
								html.push('<li class="hsub">');
								html
										.push('<a class="dropdown-toggle" href="#"><i class="'
												+ menu['icon']
												+ '"></i><span class="menu-text">'
												+ menu.text + ' </span>');
								if (menu.leaf != true && menu.leaf != 'true') {
									html
											.push('<b class="arrow fa fa-angle-down"></b>');
								}
								html.push('</a>');
								var initSubMenu = function(menu) {
									if (menu.leaf != true
											&& menu.leaf != 'true') {
										var childrens = menu.children, len = childrens.length;
										html
												.push('<ul class="submenu nav-show">');
										for (var i = 0; i < len; i++) {
											html.push('<li class="hsub">');
											var url = childrens[i].url;
											if (childrens[i].url) {
												// url = url +
												// (url.lastIndexOf('?') == -1?
												// '?' : '&') + (new
												// Date()).getTime();
												html
														.push('<a target="mainFrame" href="javascript:;" url="'
																+ url
																+ '" ><i class="menu-icon fa"></i></i>'
																+ childrens[i].text
																+ '</a><b class="arrow"></b>');
											} else {
												html
														.push('<a class="dropdown-toggle" href="javascript:;"><i class="menu-icon fa fa-caret-right"></i>'
																+ childrens[i].text
																+ '<b class="arrow fa fa-angle-down"></b></a>');
											}
											initSubMenu(childrens[i]);
											html.push('</li>');

										}
										html.push('</ul>');
									}
								};
								initSubMenu(menu);
								html.push('</li>');
							});
			return html.join('');
		};
		var htmlFrame = buildMenuHtml(menus);
		// 追加到menu div 中
		$('#menu').empty().append(htmlFrame);
		$('#menu li:eq(0)').find('a').trigger('click');
		// 设置事件
		// 1)找到含有属性url的a标签，设置点击事件
		$('#menu a[url]').bind(
				'click',
				function() {
					var url = $(this).attr("url");
					if(url.indexOf('/pages/admin/process/process_list.shtml') >= 0){
						//window.open('http://10.12.12.155:8089/bpmx/platform/console/main.ht?Auth_Session=sdfasd&account=admin');
						window.open('/bpmx/platform/console/main.ht?Auth_Session=sdfasd&account=admin');
					}
					$('#menu a[url]').parent('li').removeClass("active");
					$(this).parent('li').addClass("active");
					// 在iframe中打开
					/***********************************************************
					 * if (url && url.indexOf('http') > -1){
					 * $("#mainFrame").attr("src",url); }else{
					 * $("#mainFrame").attr("src",url); }
					 **********************************************************/
					url = url + (url.lastIndexOf('?') == -1 ? '?' : '&')
							+ (new Date()).getTime();
					initTab($(this).text(), url);
		});
		$('#menu a[url]')[0].click();
	};
	$('#modifyPwdBtn,#modifyUserBtn').on('click', function() {
		initTab($(this).text(), $(this).attr('url'));
	});
	// 添加选中效果
	// $("#menu ul li").click(function(){
	// 设置select样式，并找到同级和后代中设置为select的移除select
	// $(this).addClass("selected").siblings("li.selected").removeClass("selected").find('li.selected').removeClass("selected");
	// });

	$.ajax({
		// url: $.appendExtraParams(window.ctxPaths + '/menu.ajax'),
		url : 'portal/menu.ajax',
		type : 'POST',
		timeout : 30000,
		dataType : 'json',
		success : function(data) {
			if (data.success) {
				var menus = data.data;
				if (menus && menus.length > 0) {
					buildMenu(menus);
				}
			}
		}

	});
});
function getViewPort(obj) {
	var viewportwidth = 0, viewportheight = 0;
	if (typeof window.innerWidth != 'undefined') {
		obj = obj || window;
		viewportwidth = obj.innerWidth;
		viewportheight = obj.innerHeight;
	} else if (typeof document.documentElement != 'undefined'
			&& typeof document.documentElement.clientWidth != 'undefined'
			&& document.documentElement.clientWidth != 0) {
		obj = obj || document.documentElement;
		viewportwidth = obj.clientWidth;
		viewportheight = obj.clientHeight;
	}
	return {
		width : viewportwidth,
		height : viewportheight
	};
}
function setCookie(name, value, p) {
	var sCookie = name + '=' + encodeURIComponent(value);
	if (p) {
		if (p.expires) {
			if (p.expires != "session") {
				var etime = new Date();
				if (p.expires instanceof Date) {
					etime = p.expires;
				} else if (!isNaN(p.expires)) {
					etime.setTime(etime.getTime() + p.expires);
				} else if (p.expires == "hour") {
					etime.setHours(etime.getHours() + 1);
				} else if (p.expires == "day") {
					etime.setDate(etime.getDate() + 1);
				} else if (p.expires == "week") {
					etime.setDate(etime.getDate() + 7);
				} else if (p.expires == "year") {
					etime.setFullYear(etime.getFullYear() + 1);
				} else if (p.expires == "forever") {
					etime.setFullYear(etime.getFullYear() + 120);
				} else {
					etime = p.expires;
				}
				sCookie += "; expires=" + etime.toGMTString();
			}
		}
		if (p.path) {
			sCookie += "; path=" + p.path;
		}
		if (p.secure) {
			sCookie += "; secure=" + p.secure;
		}
	}
	document.cookie = sCookie;
}
var initIfmStyle = function() {
	var ifms = $('.tabcontent').find('.tab-pane:visible').find('iframe');
	if (ifms.length == 0)
		return;
	var winWH = getViewPort(), ifm = ifms.eq(0), ifmW = ifm.width(), ifmH = ifm
			.height(), ifmLeft = ifm.offset().left, ifmTop = ifm.offset().top, st = document.documentElement.scrollTop
			+ document.body.scrollTop, sl = document.documentElement.scrollLeft, val = 'winW='
			+ winWH['width']
			+ '&winH='
			+ winWH['height']
			+ '&ifmW='
			+ ifmW
			+ '&ifmH='
			+ ifmH
			+ '&ifmLeft='
			+ ifmLeft
			+ '&ifmTop='
			+ ifmTop
			+ '&scrollTop=' + st + '&scrollLeft=' + sl;
	return val;
};