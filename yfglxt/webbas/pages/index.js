$(document).ready(function(e) {
var max=10;
$(".accounceUL").css({"margin-left":"-6px"})

function initNavTabEvent1() {
	$('.nav-tabs li',parent.document)
			.unbind('click')
			.bind('click', function() {
				var ind = $('.nav-tabs li',parent.document).index($(this));
				$('.nav-tabs li',parent.document).removeClass('active');
				$(this).addClass('active');
				$('.tabcontent .tab-pane',parent.document).hide().eq(ind).show();
				window.location.reload();
			})
			.unbind('mouseover')
			.bind(
					'mouseover',
					function() {
						// if (len == 1)return;
						if ($(this).find('div.closeitem',parent.document).length > 0 || $(this).attr('doNotClose')) {
							return;
						}
						var $closeItem = $('<div class="closeitem"> <i class="ace-icon fa fa-times red2"></i></div>',parent.document);
						$(this).append($closeItem);
						$closeItem
								.bind(  'click',
										function(e) {
											e.stopPropagation();
											var _li = $(this).closest('li');
											var lis = $('.nav-tabs li',parent.document), ind = lis
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
											$('.nav-tabs li',parent.document).eq(ind).remove();
											$('.tabcontent .tab-pane',parent.document).eq(ind)
												.remove();

										});

					}).unbind('mouseout').bind('mouseout', function() {
				var _this = this;
				// if (len == 1)return;
				setTimeout(function() {
					var $closeItem = $(_this).find('div.closeitem',parent.document);
					$closeItem.remove();
				}, 1000);

			});
}
	$('#simple-colorpicker-1').ace_colorpicker({pull_right:true}).on('change', function(){
		var color_class = $(this).find('option:selected').data('class');
		var new_class = 'widget-box';
		if(color_class != 'default')  new_class += ' widget-color-'+color_class;
		$(this).closest('.widget-box').attr('class', new_class);
	});
	
	

	/////////////////////////////公告待办URL/////////////////////////	
	
	$.permCheck.run();
	var grid_selector = "#todo-table";
	var pager_selector = "#grid-pager";
	jqGrid_init($(grid_selector), pager_selector, {
		url : window.ctxPaths + "/servapp/servapp/list.ajax",
		sortable : true,
		sortname : 'createTime',
		sortorder : 'desc',
		shrinkToFit: true,
		forceFit: true,
		colNames : [ '流程编号','申请人', '申请时间', '金额', '客户名称', '当前状态','操作'],
		colModel : [ {
			name : 'bizSn',
			index : '',
			width : 180,
			fixed : false,
			sortable : false,
			resize : false,
			//formatter : addSNs
		}, {
			name : 'appPersonName',
			index : 'appPersonName',
			fixed : false,
			resize : false,
			sortable : true,
			sortname : 'appPersonName',
			width : 100,
		}, {
			name : 'servDate',
			index : 'servDate',
			fixed : false,
			resize : false,
			sortable : true,
			sortname : 'servDate',
			width : 110,
			formatter : addDates
		}, {
			name : 'sumFee',
			index : 'sumFee',
			fixed : false,
			resize : false,
			sortable : true,
			sortname : 'sumFee',
			width : 150,
			formatter : addSumFees
		}, {
			name : 'guestOrg',
			index : 'guestOrg',
			fixed : false,
			resize : false,
			sortable : false,
			width : 400,
			formatter : addGuestOrgs
		}, {
			name : 'tag',
			index : '',
			width : 150,
			fixed : false,
			sortable : false,
			resize : false,
			formatter : addTags
		}, {
			name : 'action',
			index : '',
			width : 100,
			fixed : false,
			sortable : false,
			resize : false,
			formatter : addActions
		},]
	});
	$(window).trigger('resize.jqGrid');
	function addSNs(cellvalue, options, rowObject) {		
		return "<div class='center'>"
				+ "<label class='position-relative'><span class='lbl'>"+options.rowId+"</span></label>"
				+ '</div>';
	}
	
	function addDates(cellvalue, options, rowObject) {		
		return "<div>"+rowObject.appDate.substr(0,10)+"</div>";				
	}
	
	function addSumFees(cellvalue, options, rowObject) {		
		return "<div>￥"+rowObject.sumFee+"</div>";				
	}
	
	function addGuestOrgs(cellvalue, options, rowObject) {	
		var guestOrgShowText = "";
		if(rowObject.guestOrg){
			guestOrgShowText = rowObject.guestOrg;
		}
		return '<div class="text-ellipsis">'+guestOrgShowText+"</div>";				
	}
	
	function addTags(cellvalue, options, rowObject) {
		var tagClass = "label-success";
		var tagText = "";
		if (rowObject.bizStatus == 'res') {
			tagClass = 'label-warning';
			if(rowObject.appStatus=='B'){
				tagText = "已撤回";
			}else{
				tagText = "已驳回";
			}			
		} else if(rowObject.bizStatus == 'success'){
			tagClass = 'label-grey';
			tagText = "已完成";
		} else if(rowObject.bizStatus == 'del'){
			tagClass = 'label-grey';
			tagText = "已删除";
		}else if(rowObject.bizStatus == 'upper'){
			tagText = "待 "+rowObject.upLeaderName+" 审批";
		}else if(rowObject.bizStatus == 'last'){
			tagText = "待 "+rowObject.endLeaderName+" 审批";
		}else if(rowObject.bizStatus == 'depart'){
			tagText = "待 "+rowObject.departLeaderName+" 审批";
		}
		return '<div >'
				+ "<span class='label label-sm "+tagClass+"'>"+tagText+"</span>"
				+ '</div>';
	}
	
	function addActions(cellvalue, options, rowObject) {
		var url = 'pages/serv/Businessreception_app.shtml?from_page=mainPage&biz_id='+rowObject.bizId;
		var op = '审批';
		if (rowObject.bizStatus == 'res') {
			op = '修改';								
			url = 'pages/serv/Businessreception_res.shtml?from_page=mainPage&biz_id='+rowObject.bizId;
		}
		return '<div >'
				+ "<a target='mainFrame' href='javascript:;' url='"+url+"'>"+op+"</a>"
				+ '</div>';
	}
	
	$('#todo-table').on('click','a', function(){
		var url = $(this).attr("url");
		initTab1($(this).html(), url);
		//initNavTabEvent1();
	});
	
	// scrollables
	$('.scrollable').each(function () {
		var $this = $(this);
		$(this).ace_scroll({
			size: $this.data('height') || 100,
			//styleClass: 'scroll-left scroll-margin scroll-thin scroll-dark scroll-light no-track scroll-visible'
		});
	});
	$('.scrollable-horizontal').each(function () {
		var $this = $(this);
		$(this).ace_scroll(
		  {
			horizontal: true,
			styleClass: 'scroll-top',//show the scrollbars on top(default is bottom)
			size: $this.data('width') || 500,
			mouseWheelLock: true
		  }
		).css({'padding-top': 12});
	});

	$(window).on('resize.scroll_reset', function() {
		$('.scrollable-horizontal').ace_scroll('reset');
	});
	$(".widget-container-col").sortable({
		connectWith: '.widget-container-col',
		items:'> .widget-box',
		opacity:0.8,
		revert:true,
		forceHelperSize:true,
		placeholder: 'widget-placeholder',
		forcePlaceholderSize:true,
		tolerance:'pointer',
		start: function(event, ui){
			//when an element is moved, it's parent becomes empty with almost zero height.
			//we set a min-height for it to be large enough so that later we can easily drop elements back onto it
			ui.item.parent().css({'min-height':ui.item.height()})
			//ui.sender.css({'min-height':ui.item.height() , 'background-color' : '#F5F5F5'})
		},
		update: function(event, ui) {
			ui.item.parent({'min-height':''})
			//p.style.removeProperty('background-color');
		}
	});
	
	
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
			ifm.attr('src', url );
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
		$('.nav-tabs', parent.document).append($('<li></li>').append(_a));
		$('.tabcontent', parent.document).append(_div);
		/*var ind = $('.nav-tabs li',parent.document).index($(this));
				$('.nav-tabs li',parent.document).removeClass('active');
				$(this).addClass('active');
				$('.tabcontent .tab-pane',parent.document).hide().eq(ind).show();*/
		//initNavTabEvent1();
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






function initsearchTab(title, url) {
	var $lis = $('.tabbable li', parent.document), lisLen = $lis.length;
	$tabPanes = $('.tabcontent .tab-pane', parent.document), ifmId = '_ifm'
			+ (new Date()).getTime();
	var isExist = $lis.find('a[title="' + title + '"]').length;
	// $tabPanes.find('iframe').attr('id','').attr('name','');
	
	if (lisLen < max) {
		var _a = $('<a></a>').attr('href', 'javascript:;').attr('title', title)
				.html('<i class="fa fa-star "></i> ' + title);
		var urls = url.split('#');
		var _div = '<div class="tab-pane"><iframe width="100%" height="600" frameborder="no" style="overflow:hidden;" src="'
				+ (urls[0] )
				+ '" name="mainFrame" id="'
				+ ifmId
				+ '" allowtransparency="true"></iframe></div>';
		$('.nav-tabs', parent.document).append($('<li></li>').append(_a));
		$('.tabcontent', parent.document).append(_div);
		var ind = $('.nav-tabs li',parent.document).index($(this));
				$('.nav-tabs li',parent.document).removeClass('active');
				$(this).addClass('active');
				$('.tabcontent .tab-pane',parent.document).hide().eq(ind).show();
		_a.trigger('click');
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
	
		function setCookie(name,value)

		{
		
		/*
		
		*--------------- setCookie(name,value) -----------------
		
		* setCookie(name,value)
		
		* 功能:设置得变量name的值
		
		* 参数:name,字符串;value,字符串.
		
		* 实例:setCookie('username','baobao')
		
		*--------------- setCookie(name,value) -----------------
		
		*/
		　　var Days = 30; //此 cookie 将被保存 30 天
		　　var exp　= new Date();
		　　exp.setTime(exp.getTime() + Days*24*60*60*1000);
		　　document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
		　　location.href = "pages/admin/search/search_list.shtml"; //接收页面.
		
		}
	
})

