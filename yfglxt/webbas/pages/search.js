$(document).ready(function(e) {
	var pages=1;
	var countpage=1;
	var max = 10;
	 $("#searchbtn").click(function() {
		 $('.badge').text(0);
        var keyword =encodeURI($("#searchKey").val());
        if (keyword == "") {
            $("#searchKey").focus();
            return false;
        }
        var list_url = ctxPaths + "/search/search.ajax?q=" + keyword + "";
       initajax(list_url);
    });
	var code=CodeDict.search_tag;
		//console.log(code);
		var str="";
		for(var i in code){
			var name=code[i]['value'];
			var num=code[i]['key'];
			if(typeof name=="undefined"){
				continue;
			}else{
			str+="<li><a href=\"javascript:;\"  num="+num+" title="+name+">"+name+"</a><small><span  class=\"badge badge-success\">0</span></small></li>";
			}
		}
	$("#showhistory").click(function(){
		$("#showhistorydetail").css({"display":"block"});
	});
	$("#closehistory").click(function(){
		$("#showhistorydetail").css({"display":"none"});
	});
	$("#statistic ul").html(str);
	function initajax(list_url){
		
		var statistic=$("#statistic li");
		$("#allnum").css({"display":""});
		 $("#result-items ul").html("");
		 $.ajaxSubmit(list_url, {},
        function(rtn) {
            if (rtn.success) {
                var data = rtn.rows;
                var html = "";
                var word=$("#searchKey").val();
				var  stat=rtn.statistic;
					var json = eval(stat) ;
					if(json){
						for(var i in json){
							var one = json[i];
							if(one['count'] && one['name']){
								switch (one['name']){
									case '公告' : $("#statistic ul span").get(0).innerHTML=one['count'];break;
									case '信息' : $("#statistic ul span").get(1).innerHTML=one['count'];break;
									case '新闻' :  $("#statistic ul span").get(2).innerHTML=one['count'];break;
									case '收文' :  $("#statistic ul span").get(3).innerHTML=one['count'];break;
									case '会议' : $("#statistic ul span").get(4).innerHTML=one['count'];break;
									case '合同' : $("#statistic ul span").get(5).innerHTML=one['count'];break;
									case '其他' : $("#statistic ul span").get(6).innerHTML=one['count'];break;									
									default :break;
								}
							}
						}	
					}
					$("#statistic li a").each(function(index) {
                        $(this).attr("searchkey",word);
                    });

			//$("#statistic").html(htmlhot+html1);
                $("#result").html("为您找到相关的结果" + rtn.records + "个");
				var page = "";
                for(var i=1;i<=rtn.total;i++){
					page+="<li><a  >"+i+"</a></li>&nbsp;&nbsp;&nbsp;&nbsp;";
				}
				var upage=parseInt(rtn.page)-1;
				var nextpage=parseInt(rtn.page)+1;
               $("#pager ul").html("<li class=\"disabled\"><a id='upageevent' title="+upage+" href=\"javascript:;\"><i class=\"ace-icon fa fa-angle-double-left\"></i>		</a>	</li>"+page+"<li><a id='nextpageevent' title="+nextpage+" href=\"javascript:;\"><i class=\"ace-icon fa fa-angle-double-right\"></i>							</a>	</li>");
                //////////////////////////////////////////////////////////////////////////var str=;
				//console.log(data);
                for (var i=0;i<data.length;i++) {
                    if (typeof data[i]['content'] == "undefined") {
                        break;
                    } else {
                        var announceId = data[i]['announceId'];
                        var title = data[i]['title'];
                        var contnent = data[i]['content'];
                        ////console.log(contnent.substr(0,500));
                        var ctime = data[i]['createTime'];
                        html += " <li style=\"width:95%; margin-bottom:30px; float:left;\"> <h5 class=\"title\"> <a target=\"mainFrame\" title="+title+" href='javascript:;' url=\"" + ctxPaths + "/pages/admin/announce/announce_detail.shtml?id=" + announceId + "\"  style=\"text-decoration:underline; font-size:18px\"><em>" + title + "</em></a><span style='float:right' class=\" fr\">" + ctime + "</span></h5> " + contnent.substr(0, 400) + "……            </li>";
                    }
                }
                $("#result-items ul").html(html);
            } else {
                if (rtn.data && rtn.data.message) {
                    Q_Alert_Fail(rtn.data.message, "提示");
                } else {
                    Q_Alert_Fail('原因未知');
                }
				countpage=rtn.total;
				pages=rtn.page;
            }
		})
	}
	$("#statistic li").on("click","a",function(){
		var keyword=encodeURI($(this).attr("searchkey"));
		var cat=encodeURI($(this).attr("title"));
		var num=$(this).attr("num");
			var list_url = ctxPaths + "/search/search.ajax?q=" + keyword + "&cat="+num+"";
			initajax(list_url);
	});
	
    $('#resultul').on('click', 'a',function() {
		//$("#mainjs").attr("href","");
        var url = $(this).attr("url");
        initTab1($(this).html(), url);
        initNavTabEvent1();
    });
	
	 $('#startTime').datepicker({
        format: 'yyyy-MM-dd',
        language: 'en',
        pickDate: true,
        pickTime: true,
        hourStep: 1,
        minuteStep: 15,
        secondStep: 30,
        inputMask: true
      });
	   $('#endTime').datepicker({
        format: 'yyyy-MM-dd',
        language: 'en',
        pickDate: true,
        pickTime: true,
        hourStep: 1,
        minuteStep: 15,
        secondStep: 30,
        inputMask: true
      });
    $("#seninorlink").click(function() {
        $("#senior").css({
            "display": "block"
        });
        $("#searchkeyword").css({
            "display": "none"
        });

    });
    $("#jiandan").click(function() {
        $("#searchkeyword").css({
            "display": ""
        });
        $("#senior").css({
            "display": "none"
        });
    });
	function getCookie(name)
	{
	　　var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
	　　if(arr !=null) return unescape(arr[2]); return null;
	}
	//alert(getCookie("searchKey"));
	if(getCookie("searchKey")!="undefined"&&getCookie("searchKey")!=""&&getCookie("searchKey")!=null){
		var list_url = ctxPaths + "/search/search.ajax?q=" + getCookie("searchKey") + "";
		   initajax(list_url);
	}
	function nextpageevent(){
		if(pages>countpage){
			return false;
		}else{
			var page=$("#nextpage").attr("title");	
		 var includeKey = encodeURI($("#includeKey").val());
        var paichu = $("#paichu").val();
        var modul = $("#modul:selected").val();
        var author = $("#author").val();
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
		 var keyword = encodeURI($("#searchKey").val());
        var list_url = ctxPaths + "/search/search.ajax?q=" + keyword+"&page="+page+"";
		var adv_url=ctxPaths + "/search/search.ajax?q=" + includeKey + "&q1="+paichu+"&cat=modul"+"&from="+startTime+"&to="+endTime+"&page="+page+"";
		initajax(list_url);
		}
	}
	$("#pager").on("click","a",function(){
		var page=$(this).html();	
		 var includeKey = $("#includeKey").val();
		 encodeURI(includeKey, "UTF-8"); 
        var paichu = $("#paichu").val();
        var modul = $("#modul:selected").val();
        var author = $("#author").val();
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
		 var keyword = $("#searchKey").val();
        var list_url = ctxPaths + "/search/search.ajax?q=" + keyword+"&page="+page+"";
		var adv_url=ctxPaths + "/search/search.ajax?q=" + includeKey + "&q1="+paichu+"&cat=modul"+"&from="+startTime+"&to="+endTime+"&page="+page+"";
		initajax(list_url);
	})
	$("#upage").click(function(){
		if(pages<1){
			return false;
		}else{	
		var page=$("#upage").attr("title");	
		 var includeKey = encodeURI($("#includeKey").val());
		 encodeURI(includeKey, "UTF-8"); 
        var paichu = $("#paichu").val();
        var modul = $("#modul:selected").val();
        var author = $("#author").val();
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
		 var keyword = $("#searchKey").val();
        var list_url = ctxPaths + "/search/search.ajax?q=" + keyword+"&page="+page+"";
		var adv_url=ctxPaths + "/search/search.ajax?q=" + includeKey + "&q1="+paichu+"&cat=modul"+"&from="+startTime+"&to="+endTime+"&page="+page+"";
		initajax(list_url);
		}
	})
    $("#searchbtn1").click(function() {
       var page=$(this).html();	
		 var includeKey = encodeURI($("#includeKey").val());
        var paichu = encodeURI($("#paichu").val());
        var modul = $("#modul").val();
        var author = encodeURI($("#author").val());
        var startTime =encodeURI($("#startTime").val());
        var endTime = encodeURI($("#endTime").val());
        var list_url = ctxPaths + "/search/search.ajax?q=" + includeKey + "&q1="+paichu+"&cat="+modul+""+"&from="+startTime+"&to="+endTime+"";
       initajax(list_url);
    });
  
    function initNavTabEvent1() {
        $('.nav-tabs li', parent.document).unbind('click').bind('click',
        function() {
            var ind = $('.nav-tabs li', parent.document).index($(this));
            $('.nav-tabs li', parent.document).removeClass('active');
            $(this).addClass('active');
            $('.tabcontent .tab-pane', parent.document).hide().eq(ind).show();
        }).unbind('mouseover').bind('mouseover',
        function() {
            // if (len == 1)return;
            if ($(this).find('div.closeitem', parent.document).length > 0) {
                return;
            }
            var $closeItem = $('<div class="closeitem"> <i class="ace-icon fa fa-times red2"></i></div>', parent.document);
            $(this).append($closeItem);
            $closeItem.bind('click',
            function(e) {
                e.stopPropagation();
                var _li = $(this).closest('li');
                var lis = $('.nav-tabs li', parent.document),
                ind = lis.index(_li),
                next = ind + 1;
                if (_li.hasClass('active')) {
                    if (ind == lis.length - 1) {
                        next = 0;
                    }
                    if (lis.length > 1) {
                        lis.eq(next).trigger('click');
                    }
                }
                $('.nav-tabs li', parent.document).eq(ind).remove();
                $('.tabcontent .tab-pane', parent.document).eq(ind).remove();
            });
        }).unbind('mouseout').bind('mouseout',
        function() {
            var _this = this;
            // if (len == 1)return;
            setTimeout(function() {
                var $closeItem = $(_this).find('div.closeitem', parent.document);
                $closeItem.remove();
            },
            1000);
        });
    }
    function initTab1(title, url) {
        var $lis = $('.tabbable li', parent.document),
        lisLen = $lis.length;
        $tabPanes = $('.tabcontent .tab-pane', parent.document),
        ifmId = '_ifm' + (new Date()).getTime();
        var isExist = $lis.find('a[title="' + title + '"]').length;
         $tabPanes.find('iframe').attr('id','').attr('name','');
        if (isExist > 0) {
            var _a = $lis.find('a[title="' + title + '"]'),
            $li = _a.closest('li'),
            num = $lis.index($li),
            ifm = $tabPanes.find('iframe', parent.document).eq(num);
            ifm.attr('id','mainFrame').attr('name','mainFrame');
            var urls = ifm.attr('src').split('#');
            if (urls[0] != url) {
                ifm.attr('id', ifmId);
                ifm.attr('src', url);
            }
            _a.trigger('click');
            return;
        }
        if (lisLen < max) {
            var _a = $('<a></a>').attr('href', 'javascript:;').attr('title', title).html('<i class="fa fa-star "></i> ' + title);
            var urls = url.split('#');
            var _div = '<div class="tab-pane"><iframe width="100%" height="700" frameborder="no" style="overflow:hidden;" src="' + (urls[0]) + '" name="mainFrame" id="' + ifmId + '" allowtransparency="true"></iframe></div>';
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
            $tabPanes.find('iframe', parent.document).eq(nextInd).attr('src', 'about:blank').attr('id', ifmId).attr('name', 'mainFrame').attr('src', url);
            $lis.eq(nextInd).find('a').attr('title', title).text(title);
            $lis.eq(nextInd).trigger('click');
        }
    }
	$("#searchKey").click(function(){
		if($("#searchKey").val()==""){
			$("#searchtool").css({"display":"none"});	
		}else{
			$("#searchtool").css({"display":"block"});	
		}
		
	});
	$(document).bind('click', function(event) { 
		  
		var evt = event.srcElement ? event.srcElement : event.target;    
		if(evt.id == 'searchtool' || evt.id == 'searchKey' ) return; // 如果是元素本身，则返回
		else {
			$('#searchtool').hide(); // 如不是则隐藏元素
		}   
	});
	

	$("#searchKey").keyup(function(e) {
      var keyword=$("#searchKey").val();
		var url=ctxPaths+"/search/suggest.ajax?prefix="+encodeURI(keyword)+"";
		//console.log(keyword);
		$("#searchtool").html("");
		 $.ajaxSubmit(url, {},
			  function(rtn) {
				  if (rtn.success) {
					  var data = rtn.rows;
					  var html="";
					  var flag=1;
					 for(var i=0;i<data.length;i++){
						 html+="<li > "+data[i]+" </li> ";
					 }
					 $("#searchtool").append(html);
					 if(html==""){
						 //$("#searchtool").html("  <li>没有为你搜索到！</li>");
						 $("#searchtool").css({"display":"none"});	
					 }else{
						  $("#searchtool").css({"display":"block"});
					 }
					 $("#searchtool li").click(function(){
		                // alert('ok');
			             $("#searchKey").val($(this).html());
			             $("#searchtool").css({"display":"none"});	
						
	                 });
					
					 
				  } else {
					  
					 /* if (rtn.data && rtn.data.message) {
						  Q_Alert_Fail(rtn.data.message, "提示");
					  } else {
						  Q_Alert_Fail('原因未知');
					  }*/
				  }
		});
    });

	$("#searchKey").keypress(function(){
		var e = e || window.event;
		if(event.keyCode==13){
			
			  var keyword = encodeURI($("#searchKey").val());
			  encodeURI(keyword, "UTF-8"); 
			if (keyword == "") {
				$("#searchKey").focus();
				return false;
			}
			var list_url = ctxPaths + "/search/search.ajax?q=" + keyword + "";
			////console.log(keyword);
		   initajax(list_url);
		   return false;
		}
	})
})