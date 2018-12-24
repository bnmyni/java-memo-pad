jQuery(function($) {
	$.datepicker.regional['zh-CN'] = {   
        clearText: '清除',   
        clearStatus: '清除已选日期',   
        closeText: '关闭',   
        closeStatus: '不改变当前选择',   
        prevText: '<上月',   
        prevStatus: '显示上月',   
        prevBigText: '<<',   
        prevBigStatus: '显示上一年',   
        nextText: '下月>',   
        nextStatus: '显示下月',   
        nextBigText: '>>',   
        nextBigStatus: '显示下一年',   
        currentText: '今天',   
        currentStatus: '显示本月',   
        monthNames: ['一月','二月','三月','四月','五月','六月', '七月','八月','九月','十月','十一月','十二月'],   
        monthNamesShort: ['一','二','三','四','五','六', '七','八','九','十','十一','十二'],   
        monthStatus: '选择月份',   
        yearStatus: '选择年份',   
        weekHeader: '周',   
        weekStatus: '年内周次',   
        dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],   
        dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],   
        dayNamesMin: ['日','一','二','三','四','五','六'],   
        dayStatus: '设置 DD 为一周起始',   
        dateStatus: '选择 m月 d日, DD',   
        dateFormat: 'yy-mm-dd',   
        firstDay: 1,   
        initStatus: '请选择日期',   
        isRTL: false};   
        $.datepicker.setDefaults($.datepicker.regional['zh-CN']);   
	$("#startDate").datepicker();
	$("#endDate").datepicker();
	
	$.permCheck.run();
	var grid_selector = "#approval-table";
	var pager_selector = "#grid-pager";
	jqGrid_init($(grid_selector), pager_selector, {
		url : window.ctxPaths + "/servapp/servapp/myApproval.ajax",
		shrinkToFit: true,
		forceFit: true,
		sortable : true,
		sortname : 'createTime',
		sortorder : 'desc',
		loadComplete: function() {
			var thCheckAll = $('.ui-jqgrid-htable').find('th:first-child');
			$('<label><input type="checkbox" id="select_all">全选</label>').prependTo(thCheckAll).css({position: 'absolute','z-index':1,top:'10px','font-weight':700});
			$.permCheck.run();
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
		},
		colNames : ['', '流程编号','申请人', '申请时间', '金额', '客户名称', '当前状态','操作'],
		colModel : [
		{
			sortable: false,
			width: 60,
			formatter: addCheckbox
		}, {
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
	function addCheckbox(cellvalue,options,rowObject) {
		var url = rowObject.bizId;
		if(rowObject.bizStatus == 'success') {
			return '<div class="center">'
			+ '<input type="checkbox" url=' + url + ' class="js-print-item"></div>';
		}
		else {
			return '<div class="center">'
			+ '<input type="checkbox" disabled="disabled"></div>';
		}
	}
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
		var url = 'pages/serv/Businessreception_list.shtml?from_page=myApproval&biz_id='+rowObject.bizId;
		return '<div >'
				+ "<a target='mainFrame' href='javascript:;' url='"+url+"'>查看</a>"
				+ '</div>';
	}
});
function addTable(arr,tempstr) {
	var ret = [];
	$.each(arr,function() {
	var node = $(tempstr);

		node.find('.servOrg').text(this.servOrg);
		if(this.guestOrg) {
			node.find('.guestOrg').text(this.guestOrg);
		}
		if(this.servOrg == '系统事业部-市场销售部-市场销售部'){
			//node.find('.accountSubject').text(this.accountSubject);
			//node.find('.coastAim').text(this.coastAim);
			
			if(this.accountSubject=='1'){
					node.find('.accountSubject').html('安全业务线(√)&nbsp;&nbsp;&nbsp;非安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}else if(this.accountSubject=='2'){
					node.find('.accountSubject').html('安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;非安全业务线(√)&nbsp;&nbsp;&nbsp;');
				}else{
					node.find('.accountSubject').html('安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;非安全业务线(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}
				
				if(this.coastAim=='1'){
					node.find('.coastAim').html('日常业务招待费(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}else if(this.coastAim=='2'){
					node.find('.coastAim').html('日常业务招待费(√)&nbsp;&nbsp;&nbsp;');
				}else {
					node.find('.coastAim').html('日常业务招待费(&nbsp;)&nbsp;&nbsp;&nbsp;');
				}
		}else{
			node.find("#accountSubjectRow").remove();
			node.find("#coastAimRow").remove();
		}
		node.find(".servReason").text(this.servReason);
		node.find(".servLoc").text(this.servLoc);
		node.find(".guestNum").text(this.guestNum);
		node.find(".servNum").text(this.servNum);
		if(this.servRemark){
			node.find(".servRemark").text(this.servRemark);
		}
		if(this.dishFee){
			node.find(".dishFee").text(this.dishFee);
		}
		if(this.giftFee){
			node.find(".giftFee").text(this.giftFee);
		}	
		node.find('.sumFee').text(this.sumFee);
		node.find(".servDate").text(this.servDate.substr(0,10));
		var date_str = this.appDate.substr(0,4)+'年'+this.appDate.substr(5,2)+'月'+this.appDate.substr(8,2)+'日';
		if(this.bizStatus=='success'){
			date_str = "年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日";
		}
		node.find('.appDateShow').html(date_str);
		if(this.servType=='out'){
			node.find(".servType").html('外事(√)&nbsp;&nbsp;&nbsp;商务(&nbsp;)&nbsp;&nbsp;&nbsp;其他公务(&nbsp;)');
		}else if(this.servType=='biz'){
			node.find(".servType").html('外事(&nbsp;)&nbsp;&nbsp;&nbsp;商务(√)&nbsp;&nbsp;&nbsp;其他公务(&nbsp;)');
		}else if(this.servType=='other'){
			node.find(".servType").html('外事(&nbsp;)&nbsp;&nbsp;&nbsp;商务(&nbsp;)&nbsp;&nbsp;&nbsp;其他公务(√)');
		}

		node.find('.appPersonName').text(this.appPersonName);
		if(this.upStatus=='Y'){
			node.find('.div_upLeader').show();
			node.find(".upLeaderName").text(this.upLeaderName);
			var temp = "";
			if(typeof(this.upOpinion) == "undefined" 
				|| this.upOpinion == null 
				|| this.upOpinion == ""
				|| this.upOpinion == "null"){
					temp = "";
			}else{
				temp = ":"+this.upOpinion;
			}
			var opinion = '(同意'+temp+')';
			node.find(".upOpinion").text(opinion);
		}else if (this.upStatus=='R'){
			node.find('.div_upLeader').show();
			node.find(".upLeaderName").text(this.upLeaderName);
			var opinion = '(驳回:'+this.upOpinion+')';
			node.find(".upOpinion").text(opinion);
		}
		if(this.endStatus=='Y'){
			node.find('.div_endLeader').show();
			node.find(".endLeaderName").text(this.endLeaderName);
			var temp = "";
			if(typeof(this.endOpinion) == "undefined" 
				|| this.endOpinion == null 
				|| this.endOpinion == ""
				|| this.endOpinion == "null"){
					temp = "";
				}else{
					temp = ":"+this.endOpinion;
				}
				var opinion = '(同意'+temp+')';
				node.find(".endOpinion").text(opinion);
		}else if (this.endStatus=='R'){
				node.find('.div_endLeader').show();
				node.find(".endLeaderName").text(this.endLeaderName);
				var opinion = '(驳回:'+this.endOpinion+')';
				node.find(".endOpinion").text(opinion);
		}		

	ret.push(node);
	});
	return ret;
}
$('.js-print-all').on('click',function() {
	var ids = [];

	$('.js-print-item:checked').each(function() {
		ids.push($(this).attr('url'));
	});

	if(ids.length == 0){
		top.Q_Alert('请至少选择一条记录进行打印！');
		return;
	} 
	var tableTemp = '<form class="print-form"><div class="titleDiv">' +
						'<strong>卓望公司业务招待审批单</strong> <span class="appDateShow"></span></div>' +
				'<table height="374" border="1" class="buessiontable">' +
					'<tr>' +
						'<td class="tdleft textbg" colspan="2"><strong>负责接待的部门</strong></td>' +
						'<td colspan="3" class="servOrg"></td>' +
					'</tr>' +
					'<tr>' +
						'<td class="tdleft textbg" colspan="2"><strong>招待类型</strong></td>' +
						'<td colspan="3" class="servType"></td>' +
					'</tr>' +
					'<tr id="accountSubjectRow">' +
						'<td class="tdleft textbg" colspan="2"><strong>科目归属</strong></td>' +
						'<td colspan="3" class="accountSubject"></td>' +
					'</tr>' +
					'<tr id="coastAimRow">' +
						'<td class="tdleft textbg" colspan="2"><strong>费用目的</strong></td>' +
						'<td colspan="3" class="coastAim"></td>' +
					'</tr>' +
					'<tr>' +
						'<td class="tdleft textbg" colspan="2"><strong>来宾单位</strong></td>' +
						'<td colspan="3" class="guestOrg"></td>' +
					'</tr>' +
					'<tr>' +
						'<td class="tdleft textbg" colspan="2"><strong>接待事由</strong></td>' +
						'<td colspan="3" class="servReason"></td>' +
					'</tr>' +
					'<tr>' +
						'<td class="tdleft textbg" colspan="2"><strong>接待日期</strong></td>' + 
						'<td width="211" class="servDate"></td>' + 
						'<td class="textbg tdleft grid-title-large" width="138"><strong>接待地点</strong></td>' + 
						'<td width="184" style="word-break:break-all" class="servLoc" ></td>' + 
					'</tr>' + 
					'<tr>' + 
						'<td class="tdleft textbg" colspan="2"><strong>来宾人数</strong></td>' + 
						'<td class="guestNum"></td>' + 
						'<td class="textbg tdleft grid-title-large"><strong>陪同人数</strong></td>' + 
						'<td class="servNum"></td>' + 
					'</tr>' + 
					 '<tr>' + 
        '<td  rowspan="2" class="tdleft textbg grid-title grid-title-small"><strong>接待<br>标准</strong></td>' + 
        '<td  class="tdleft textbg grid-title grid-title-small"><strong class="field-danwei">宴请</strong></td>' + 
        '<td class="dishFee"></td>' + 
        '<td rowspan="2" class="tdleft textbg grid-title grid-title-large"><strong>预计支出金额（元）</strong></td>' + 
        '<td rowspan="2">' + 
            '<label class="sumFee"></label>' + 
          '</td>' + 
      '</tr>' + 
      '<tr>' + 
        '<td  class="tdleft textbg grid-title grid-title-small"><strong class="field-danwei">纪念品</strong></td>' + 
        '<td class="giftFee"></td>' + 
      '</tr>' + 
					'<tr>' +
						'<td class="tdleft textbg grid-title-small" colspan="2" ><strong>备注</strong></td>' +
						'<td colspan="3" class="servRemark"></td>' + 
					'</tr>' +
				'</table>' +
				'<div class="footerDiv">' +
					'<div align="left">' +
					'<strong>经办人：</strong><strong class="appPersonName"></strong></span></div>' +
					'<div align="left" style="display:none" class="div_upLeader">' +
						'<strong>三级部门负责人：</strong><strong class="upLeaderName"></strong></span><strong class="upOpinion"></strong></div>' +
					'<div align="left" style="display:none" class="div_endLeader">' +
						'<strong>二级部门负责人：</strong><strong class="endLeaderName"></strong></span><strong class="endOpinion"></strong></div>' +
				'</div></form>';
	//console.log(tableTemp);
	var str_ids = "";
	for(var i=0;i<ids.length;i++){
		if(i==0){
			str_ids+=ids[i];
		}else{
			str_ids+=";"+ids[i];
		}
	}
	$.ajaxSubmit(window.ctxPaths+ "/servapp/servapp/getList.ajax",{'biz_ids' : str_ids},
	function(data) {
		Loading.hide();
		if($.isArray(data.data)&&data.success) {
			var newtables = addTable(data.data,tableTemp);
			processData(newtables);
		}
	});

	function processData(newtables) {
		var $overlay =$('<div class="print-overlay"></div>');
		$.each(newtables,function() {
			$overlay.append(this);
		});
		$overlay.find('.print-form:gt(0)').each(function() {
			this.style.paddingTop = '100px';
		});	
		var iframe = $('<iframe></iframe>');
		iframe.appendTo('body').css({width: "0", height: "0"});
		//var baseURL= window.
		var doc = iframe[0].contentWindow.document;
		doc.write('<!DOCTYPE html>');
		$('link').each(function() {
			doc.write('<link rel="stylesheet" href="' + window.ctxPaths + '/' + $(this).attr("href") +'" />');
		});
		var html = '<link type="text/css" rel="stylesheet" href="' + window.ctxPaths +'/pages/css/common.css">' +
				'<link rel="stylesheet" href="' + window.ctxPaths + '/pages/css/print.css">' +
				$overlay.html();
		doc.write(html);
		doc.close();
		iframe[0].contentWindow.focus();
		setTimeout(function() {
			iframe[0].contentWindow.print();
			$overlay.remove();
			iframe.remove();
		},1000);	
	}
});
$('.page-content').on('click','#select_all',function() {
	$('.js-print-item').prop('checked',this.checked);
});
$('#approval-table').on('click','a', function(){
	var url = $(this).attr("url");
	initTab1($(this).html(), url);
	//initNavTabEvent1();
});

$('#btn_search').on('click', function() {
	jQuery('#approval-table').jqGrid('setGridParam', {
		postData : $('#queryForm1').serializeJson(),
		page : 1
	}).trigger("reloadGrid");
});

$("#guestOrg").keydown(function(e) {
	var curKey = e.which;
	if (curKey == 13) {
		$('#btn_search').trigger('click');
		return false;
	}
});