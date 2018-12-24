/*$("#cat").localRender(CodeDict['announce_cat'],{
		//firstOption:{text:'请选择分类',value:''},
		keyValue:{text:'value',value:'key'}
	});*/
	
	var list_url   = ctxPaths+ '/employee/overtime/department/list.ajax';
	var export_url = ctxPaths+ '/employee/overtime/department/export.ajax';
	var detail_url = 'pages/admin/employee/department_overtime_detail.shtml';
	var grid_selector = "#grid-table";
	var pager_selector = "#grid-pager";
	var m_end_day = [31,28,31,30,31,30,31,31,30,31,30,31];
	var code_base_html = '<option value="安全系统部" class="serviceCode serviceCode_00">安全系统部</option>'+
					'<option value="运维系统部" class="serviceCode serviceCode_00">运维系统部</option>'+
					'<option value="运营系统部" class="serviceCode serviceCode_00">运营系统部</option>'+
					'<option value="平台开发一部" class="serviceCode serviceCode_01">平台开发一部</option>'+
					'<option value="平台开发二部" class="serviceCode serviceCode_01">平台开发二部</option>'+
					'<option value="测试部" class="serviceCode serviceCode_01">测试部</option>'+
					'<option value="需求部" class="serviceCode serviceCode_01">需求部</option>'+
					'<option value="架构部" class="serviceCode serviceCode_01">架构部</option>';
	$(function($) {
		
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
		
		fastSetQryDate('1');
    });

			$('#seachBtn').on('click', function(){
				var department3 = ""; //$("#serviceCode").val();
				var serCode = $(".serviceCode");
				for(var i  = 0; i < serCode.length; i++){
					if (serCode[i].checked ==true) {
						if('00' == serCode[i].value){
							department3 = department3 + ",'应用研发部'" ;
						}else if('01' == serCode[i].value){
							department3 = department3 + ",'平台研发二部'" ;
						}
						
					}
				}
				var department4 = $("#department4").val();
				var type_ = $("#type_").val();
				var startDate = $("#startDate").val();
				var endDate = $("#endDate").val();
				
				$.ajaxSubmit(list_url, {'department3':department3,'department4':department4,'startDate':startDate,'endDate':endDate,'type_':type_},
			      function(rtn) {
				    if (rtn.success) {
					  var data = rtn.data;
					  var html="";
					  //console.log(data);
					  var $tableModel =$("#tableModel");
				      var htmlTr =[];
				      var modelLen;
				 
				        $.each(data,function(len){
				            modelLen =this.length;
				
				            $.each(this,function(index){
				                if(index==0){
									var url = detail_url+'?department3='+this["department3"]+'&startDate='+startDate+'&endDate='+endDate;
				                    if(len == data.length -1){
										htmlTr.push('<tr><td rowspan='+modelLen+' class="first"> '+this["department3"]+'</td>');
									}else{
										htmlTr.push('<tr><td rowspan='+modelLen+' class="first"> <a target="mainFrame" onclick="infoDetail(this)" url="'+encodeURI(url)+'" >'+this["department3"]+'</a></td>');
									}
									
									url = url  +'&department4='+ this["department4"]
									htmlTr.push('<td> <a target="mainFrame" onclick="infoDetail(this)" url="'+encodeURI(url)+'" >'+this["department4"]+'</a></td>');
				                }else{
				                    htmlTr.push('<tr class="'+(index==(modelLen-1)?"subtotal":"")+'">');
									if(index==(modelLen-1)){
										htmlTr.push('<td>'+this["department4"]+'</td>');
									}else{
										var url = detail_url+'?department3='+this["department3"]+'&department4='+ this["department4"]+'&startDate='+startDate+'&endDate='+endDate;
										htmlTr.push('<td> <a target="mainFrame" onclick="infoDetail(this)" url="'+encodeURI(url)+'" >'+this["department4"]+'</a></td>');
									}
				                }
				
				                htmlTr.push(
									'<td>'+this["totalPeople"]+'</td>' +
				                    '<td>'+this["avgPeople"]+'</td>' +
				                    '<td>'+this["workTime"]+'</td>' +
				                    '<td>'+this["overTime"]+'</td>' +
				                    '<td>'+this["averageTime"]+'</td>' +
				                    '<td>'+this["rate"]+'</td>');
				                htmlTr.push('</tr>');
				            });
				        });
				
				        $tableModel.find("tBody").html(htmlTr.join(""));	
						$('#tableModel tr:last').find('td').addClass('end');
					} else {
					  if (rtn.data && rtn.data.message) {
						  Q_Alert_Fail(rtn.data.message, "提示");
					  } else {
						  Q_Alert_Fail('原因未知');
					  }
					}
			});
		});
			
			$('#exportBtn').on('click', function(){
				var department3 = ""; //$("#serviceCode").val();
				var serCode = $(".serviceCode");
				for(var i  = 0; i < serCode.length; i++){
					if (serCode[i].checked ==true) {
						if('00' == serCode[i].value){
							department3 = department3 + ",'应用研发部'" ;
						}else if('01' == serCode[i].value){
							department3 = department3 + ",'平台研发二部'" ;
						}
					}
				}
				//var serviceCode = $("#serviceCode").val();
				var department4 = $("#department4").val();
				var startDate = $("#startDate").val();
				var endDate = $("#endDate").val();
				var param = "";
				param = "department3="+department3+"&department4="+department4+"&startDate="+startDate+"&endDate="+endDate+"&";
				export_url = export_url + "?" + param + new Date().getTime();
				window.open(encodeURI(export_url));
			});
		
		$("input[type='checkbox']").change(function(){
			//$("#department4").find(".serviceCode").hide();
			$("#department4").find(".serviceCode").remove();
			$("#department4").append(code_base_html);
			var allShow = true;
			$("input[type='checkbox']").each(function(){
				if($(this).is(':checked')){	
					allShow = false;
				}else{
					$("#department4").find(".serviceCode_" + $(this).val()).remove();
				}	
			});
			if(allShow){
				$("#department4").find(".serviceCode").remove();
				$("#department4").append(code_base_html);
			}
		});
		
	function fastSetQryDate(a){
		var curDate = new Date();
		curDate.setDate(curDate.getDate() - 1);
		$("#endDate").val(dateFormat(curDate));	
		if(0 == a){ // 周					
			curDate.setDate(curDate.getDate() - 6 );
			$("#startDate").val(dateFormat(curDate));
		}else{
			curDate.setMonth(curDate.getMonth() - a);
			curDate.setDate(curDate.getDate() - 1);
			$("#startDate").val(dateFormat(curDate));
		}
		$('#seachBtn').click();
	}
		
	function dateFormat(date){
		return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
	}
	
	function infoDetail(_this){	
		var url = $(_this).attr("url");
		var title = $(_this).html();
		
		var $lis = $('.tabbable li', parent.document), lisLen = $lis.length;
		$tabPanes = $('.tabcontent .tab-pane', parent.document), ifmId = '_ifm'
				+ (new Date()).getTime();
		var isExist = $lis.find('a[title="' + title + '"]').length;
		if (isExist >0) {
			var _a = $lis.find('a[title="' + title + '"]'), $li = _a.closest('li'), num = $lis
					.index($li), ifm = $tabPanes.find('iframe',parent.document).eq(num);
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
	
			_a.closest('li').trigger('click');
		} else {
			var selectedInd = $lis.index($('.tabbable li.active', parent.document));
			var nextInd = (selectedInd == max - 1 ? 0 : selectedInd + 1);
			$tabPanes.find('iframe',parent.document).eq(nextInd).attr('src', 'about:blank').attr(
					'id', ifmId).attr('name', 'mainFrame').attr('src',
					url );
			$lis.eq(nextInd).find('a').attr('title', title).text(title);
			$lis.eq(nextInd).trigger('click');
		}
	}
	