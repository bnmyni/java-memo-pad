$("#cat").localRender(CodeDict['announce_cat'],{
		//firstOption:{text:'请选择分类',value:''},
		keyValue:{text:'value',value:'key'}
	});
	var obj_url=ctxPaths+"/lessee/lessee/listExclude.ajax?isExclude=1";
	var audit_url  =ctxPaths+'/announce/announce/audit.ajax';//审批
	var list_url  = ctxPaths+ '/announce/announce/list.ajax';
	var add_url   = ctxPaths+ '/announce/announce/add.ajax';
	var get_url  = ctxPaths+ "/announce/template/get.ajax";
	var mod_url   = ctxPaths+ '/announce/announce/mod.ajax';
	var del_url   = ctxPaths+ '/announce/announce/del.ajax';
	var tpl_list_url  = ctxPaths+ '/announce/template/list.ajax';
	var view_url 	 = ctxPaths+ '/announce/announce/get.ajax';
	var grid_selector = "#grid-table";
	var pager_selector = "#grid-pager";
	$(function($) {
		init();
		
		$.permCheck.run();
		
		$("#savebtn").click(function(){
          $("#modal-wizard").css({"display":"block"});	
		  $("#setingTpl").click(function(){
			$("#tpl").css({"display":"","width":"200px"});
		});
		$('[data-rel=tooltip]').tooltip();
		  $.ajaxSubmit(tpl_list_url, {},
			  function(rtn) {
				  if (rtn.success) {
					  var data = rtn.rows;
					  var html="";
					  //console.log(data);
					  for(var i=0 in data){
						  if(typeof data[i]['title']=="undefined"){
							  break;
						  }else{
							  html+="<option value="+data[i]['tmplId']+">"+data[i]['title']+"</option>";
						  }
					  }
					  $("#tpl").html("<option selected value=''>请选择</option>"+html);
				  } else {
					  if (rtn.data && rtn.data.message) {
						  Q_Alert_Fail(rtn.data.message, "提示");
					  } else {
						  Q_Alert_Fail('原因未知');
					  }
				  }
		});
		$("#tpl").change(function(){
				var tplval=$("#tpl  option:selected").val();
				//console.log(tplval);
				$.ajaxSubmit(get_url,{'id': tplval}, function(data){
					if(data.success == true){
						console.log(data.data);
						$("#title").val(data.data['title']);
						var ue=UE.getEditor('content');
						//判断ueditor 编辑器是否创建成功
					   // ue.addListener("ready", function () {
						// editor准备好之后才可以使用
						
						ue.setContent(data.data['content']);
						//$("#content").css({"width":"900px","height":"300px"});
					   // });
						//UE.getEditor('content').setContent(data.data['content']);
						//$("#content").html(data.data['content']);
					}else{
						Q_Alert_Fail(data.message);
					}
				});
			})
	
		  $.ajaxSubmit(obj_url, {},
					function(rtn) {
						if (rtn.success) {
							var data = rtn.rows;
							var html="";
							for(var i=0 in data){
								if(typeof data[i]['lesseeName']=="undefined"){
									break;
								}else{
									html+="<input style='margin-left:20px;'  type='checkbox' value="+data[i]['lesseeId']+" >"+data[i]['lesseeName'];
								}
							}
							$("#sendObject").html(html);
						} else {
							if (rtn.data && rtn.data.message) {
								Q_Alert_Fail(rtn.data.message, "提示");
							} else {
								Q_Alert_Fail('原因未知');
							}
						}
			});
    	});
		$('#modal-wizard .modal-header').ace_wizard();
		$("#close").click(function(){
				$("#modal-wizard").css({"display":"none"});	
		})
    var ue = UE.getEditor('content');
        var $validation = false;
				$('#modal-wizard .modal-header').on('change' , function(e, info){
					if(info.step == 1 && $validation) {
						if(!$('#add-from').valid()) return false;
					}
				}).on('finished', function(e) {
						add_validator();
						$("#modal-wizard").css({"display":"block"});
					}).on('stepclick', function(e){
					e.preventDefault();//this will prevent clicking and selecting steps
				});
			
        $('#modal-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr('disabled');
		$.ajaxSubmit(tpl_list_url, {},
			  function(rtn) {
				  if (rtn.success) {
					  var data = rtn.rows;
					  var html="";
					  	  for(var i=0 in data){
						  if(typeof data[i]['title']=="undefined"){
							  break;
						  }else{
							  html+="<option value="+data[i]['tmplId']+">"+data[i]['title']+"</option>";
						  }
					  }
					  $("#tpl").html("<option selected value=''>请选择</option>"+html);
				  } else {
					  /*if (rtn.data && rtn.data.message) {
						  Q_Alert_Fail(rtn.data.message, "提示");
					  } else {
						  Q_Alert_Fail('原因未知');
					  }*/
			}

		});
		function add_validator(){
            var form=$("#add-from");
                var url = add_url;
                $.ajax({
                url:url,
                type:"POST",
                dataType:"json",
                data:form.serialize(),
                success: function(data){
                    //console.log(data);
                    if(data['title']!=""){
                       $("#modal-wizard").css({"display":"none"});
						 $(grid_selector).trigger("reloadGrid");
                    }
                    else {
                        
                            Q_Alert_Fail('原因未知');
                       
                    }
                },
                error:function(){
                    Q_Alert_Fail('原因未知');
                }
                })		
			}
		});
			var cat=CodeDict['announce_cat'];
			var catStr="";
			for(var i=0;i<cat.length;i++){
				catStr+="<option value="+cat[i]['value']+">"+cat[i]['value']+"</option>";
			}
			$("#catName").html(catStr);
			$("#catName").change(function(){
				//console.log($(this).val());
			});
			function init(){
				jqGrid_init($(grid_selector),pager_selector,{
					url: list_url,
					sortable : true,
					sortname : 'TITLE',
					sortorder:'desc',
					colNames:[
						'序列号',
						'标题',
						//'内容',
						'栏目类型',
						'状态',
						'创建人',
						'创建时间',
					''
					],
					colModel:[
					
					{name:'announceId',index:'announceId', hidden:true},
						   						   
					{name:'title',index:'title', sortable:true,sortname : 'TITLE',width:100,formatter:formatName},
							
						   						   
					//{name:'content',index:'content', sortable:true,sortname : 'CONTENT',width:100},
							
					{name:'catDesc',index:'catDesc', sortable:false,sortname : 'CAT',width:100},
					
					{name:'statusDesc',index:'statusDesc', sortable:false,sortname : 'STATUS',width:100},
					
						   						   
					{name:'creatorDesc',index:'creatorDesc', sortable:false,sortname : 'CREATOR',width:100},
							
						   						   
					{name:'createTime',index:'createTime', sortable:true,sortname : 'CREATE_TIME',width:100},
					{name:'myac',index:'', width:120, fixed:true, sortable:false, resize:false,formatter:actionButtons}					
					]
				});
			}
			function formatName(cellvalue, options, rowObject){
				return '<a href="pages/admin/announce/announce_detail.shtml?id='+rowObject['announceId']+'" >' + cellvalue + '</a>';
				} 
				function actionButtons(cellvalue, options, rowObject){
					var flag=rowObject['status'];
					if(flag==1){
							return '<div >' + 
								'<a permCheck="admin_announce_announce,UPDATE,hide" href="pages/admin/announce/announce_add.shtml?id='+rowObject['announceId']+'"  class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >' +
									'<i class=\"ace-icon fa fa-pencil bigger-120\"></i>' +
								'</a>' + 
								
								'<a  permCheck="admin_announce_announce,AUDIT,hide" class=\" auditbtn btn btn-xs btn-info \" style="display:"""  onclick=\"auditEvent(\''+rowObject['announceId']+'\')\" data-rel=\"tooltip\" title=\"审批\" >'+'<i class=\"ace-icon fa fa-check-square-o bigger-120\"></i>' +'</a>' + 
								'<button permCheck="admin_announce_announce,DELETE,hide" onclick=\"deleteEvent(\''+rowObject['announceId']+'\')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >' +
									'<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>' +
								'</button>'  + 
							'</div>';
					}else{
						return '<div >' + 
								'<a permCheck="admin_announce_announce,UPDATE,hide" href="pages/admin/announce/announce_add.shtml?id='+rowObject['announceId']+'"  class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >' +
									'<i class=\"ace-icon fa fa-pencil bigger-120\"></i>' +
								'</a>' + 
								
								
								'<button permCheck="admin_announce_announce,DELETE,hide" onclick=\"deleteEvent(\''+rowObject['announceId']+'\')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >' +
									'<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>' +
								'</button>'  + 
							'</div>';
					}
					
				}
			$("#auditOK").click(function(){
				var id=$("#announceId").val();
				////console.log(id);
					$.ajaxSubmit(audit_url+"?announceId="+id+"&status=2",{}, function(data){
					
				    if(data.success == true){
						$(grid_selector).trigger("reloadGrid");
						$('#add-form-modal').css({"display":"none"});
						////console.log("审批通过");
						
					}else{
						//Q_Alert_Fail(data.message);
					}
				});
			})
			$("#auditpass").click(function(){
				var id=$("#announceId").val();
				$.ajaxSubmit(audit_url+"?announceId="+id+"&status=3",{'id': id}, function(data){
					////console.log(data);
				    if(data.success == true){
						$('#add-form-modal').css({"display":"none"});
						 $(grid_selector).trigger("reloadGrid");

					}else{
						Q_Alert_Fail(data.message);
						 $(grid_selector).trigger("reloadGrid"); 
					}
				});
			})
			function editEvent(id){
				$.ajaxSubmit(view_url,{'id': id}, function(data){
				    if(data.success == true){
						resetForm($('#add-form'),add_validator);
						$('#add-form').json2Form2(data.data);
						//$('#updId').val($('#announceId').val());
						$('#add-form-modal').modal2({backdrop:"static",show:true});
					}else{
						Q_Alert_Fail(data.message);
					}
				});
			};
			function viewEvent(id){
				$.ajaxSubmit(view_url,{'id' : id}, function(data){
					$.dataInput($('#view-form-modal').find('.form-control-static'),data.data);
					$('#view-form-modal').modal2({backdrop:"static",show:true});		
				});
			};
			function deleteEvent(id){
				Q_Confirm("是否要删除？",function(result) {
					if(result){
						$.ajaxSubmit(del_url,{'id' : id}, function(data){
							$(grid_selector).trigger("reloadGrid");
						});
					}
				});
			};
			
		
			function auditEvent(id){
					$("#announceId").val(id);
					$('#audit-form-modal').modal2({backdrop:"static",show:true});
				};
			
			
			add_validator = $('#add-form').validate({
					rules: {
					
						'title' : {
							required:true,
							maxlength: 100
						},
						'content' : {
							required:true,
							maxlength: 4000
						},
						'status' : {
							required:true,
							maxlength: 30
						},
						'cat' : {
							required:true,
							maxlength: 30
						},
						'creator' : {
							required:true,
							maxlength: 50
						},
						'createTime' : {
							required:true,
							maxlength: 19
						},
						'lastUpdateTime' : {
							maxlength: 19
						},
						'attachGroupId' : {
							maxlength: 30
						},
						'domain' : {
							required:true,
							maxlength: 30
						}
	 				   
					},
					submitHandler: function (form) {
						var url = add_url;
						if($('#announceId').val() != ''){
							url = mod_url;
						}
						$.ajaxSubmit(url,$(form).serializeJson(),function(data){
								if(data.success == true){
								    $('#add-form-modal').modal2('hide');
									$(grid_selector).trigger("reloadGrid");
								}else{
									Q_Alert_Fail(data.message); 
								}
						});
						return false;		
					}
			});
			
			
			
			$('#seachBtn').on('click', function(){
				$(grid_selector).jqGrid('setGridParam',{postData: $('#queryForm').serializeJson()}).trigger("reloadGrid");
			});
			