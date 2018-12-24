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
	var grid_selector = "#readyApproval-table";
	var pager_selector = "#grid-pager";
	jqGrid_init($(grid_selector), pager_selector, {
		url : window.ctxPaths + "/servapp/servapp/myReadyApproval.ajax",
		shrinkToFit: true,
		forceFit: true,
		sortable : true,
		sortname : 'createTime',
		sortorder : 'desc',
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
		var url = 'pages/serv/Businessreception_app.shtml?from_page=myReadyApproval&biz_id='+rowObject.bizId;
		return '<div >'
				+ "<a target='mainFrame' href='javascript:;' url='"+url+"'>审批</a>"
				+ '</div>';
	}
});

$('#readyApproval-table').on('click','a', function(){
	var url = $(this).attr("url");
	initTab1($(this).html(), url);
	//initNavTabEvent1();
});

$('#btn_search').on('click', function() {
	jQuery('#readyApproval-table').jqGrid('setGridParam', {
		postData : $('#queryForm').serializeJson(),
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