	var list_url  = ctxPaths+ '/announce/template/list.ajax';
	var add_url   = ctxPaths+ '/announce/template/add.ajax';
	var mod_url   = ctxPaths+ '/announce/template/mod.ajax';
	var del_url   = ctxPaths+ '/announce/template/del.ajax';
	var view_url 	 = ctxPaths+ '/announce/template/get.ajax';
	var ue = UE.getEditor('content');
	var grid_selector = "#grid-table";
	var pager_selector = "#grid-pager";
jQuery(function($) {
	
	$("#cat").localRender(CodeDict['announce_cat'],{
		//firstOption:{text:'请选择分类',value:''},
		keyValue:{text:'value',value:'key'}
	});
	
    	initdata();
		 $("#close").click(function(){
            $("#modal-wizard").css({"display":"none"});	
    })
    
    
        var $validation = false;
        $('#modal-wizard .modal-header').ace_wizard();
		//$('[data-rel=tooltip]').tooltip();
		//$('[data-rel=popover]').popover();
		var $validation = false;
				$('#modal-wizard .modal-header').on('change' , function(e, info){
					if(info.step == 1 && $validation) {
						if(!$('#add-from').valid()) return false;
					}
				}).on('finished', function(e) {
					 add_validator();
					 //initdata();
					 $("#modal-wizard").css({"display":"none"});
				}).on('stepclick', function(e){
					e.preventDefault();//this will prevent clicking and selecting steps
				});
			
        $('#modal-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr('disabled');
        var add_url   = ctxPaths+ '/announce/template/add.ajax';
        
		$('#add-form').validate({
					errorElement: 'div',
					errorClass: 'help-block',
					focusInvalid: false,
					rules: {
						tmplName: {
							required: true
						},
						cat: {
							required: true,
							cat: 'required'
						},
						title: {
							required: true,
							title: true
						},
						content: {
							required: true
						},
						content: {
							required: true
						},
					},
					messages: {
						tmplName: {
							required: "请输入模版名称",
							tmplName: "请输入模版名称"
						},
						title: {
							required: "Please specify a title.",
							minlength: "Please specify a secure title."
						},
						content: "50000字内，可以插入图片",
						cat: "Please choose cat"
					},
					highlight: function (e) {
						$(e).closest('.form-group').removeClass('has-info').addClass('has-error');
					},
					success: function (e) {
						$(e).closest('.form-group').removeClass('has-error');//.addClass('has-info');
						$(e).remove();
					},
					errorPlacement: function (error, element) {
						if(element.is(':checkbox') || element.is(':radio')) {
							var controls = element.closest('div[class*="col-"]');
							if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
							else error.insertAfter(element.nextAll('.lbl:eq(0)').eq(0));
						}
						else if(element.is('.select2')) {
							error.insertAfter(element.siblings('[class*="select2-container"]:eq(0)'));
						}
						else if(element.is('.chosen-select')) {
							error.insertAfter(element.siblings('[class*="chosen-container"]:eq(0)'));
						}
						else error.insertAfter(element.parent());
					},
					submitHandler: function (form) {
					},
					invalidHandler: function (form) {
					}
				});
})
			
			function initdata(){
				jqGrid_init($(grid_selector),pager_selector,{
					url: list_url,
					sortable : true,
						sortname : 'TITLE',
					
					
					sortorder:'desc',
					colNames:[
					
						'序列号',
						'标题',
						'模版名称',
						//'内容',
						'栏目类型',
						'创建人',
						'创建时间',
						//'最后修改时间',
						//'附件组ID',
						//'租户/域名',
					
					''
					],
					colModel:[
					
					{name:'tmplId',index:'tmplId', hidden:true},
						   						   
					{name:'title',index:'title', sortable:true,sortname : 'TITLE',width:100,formatter:formatName},
							
					{name:'tmplName',index:'tmplName', sortable:true,sortname : 'TMPL_NAME',width:100},	   						   
					//{name:'content',index:'content', sortable:true,sortname : 'CONTENT',width:100},
							
					{name:'catDesc',index:'catDesc', sortable:false,sortname : 'CAT',width:100},
					
						   						   
					{name:'creatorDesc',index:'creatorDesc', sortable:false,sortname : 'CREATOR',width:100},
							
						   						   
					{name:'createTime',index:'createTime', sortable:true,sortname : 'CREATE_TIME',width:100},
					{name:'myac',index:'', width:120, fixed:true, sortable:false, resize:false,formatter:actionButtons}					
					]
				});
			}
			$("#btntest").click(function(){
        $("#modal-wizard").css({"display":"block"});	
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
                    console.log(data);
                    if(data['success']==true){
                        console.log(data);
                    	 $(grid_selector).trigger("reloadGrid");
						window.location.reload();
						// $("#modal-wizard").formwizard("back");
						$("#modal-wizard").css({"display":"none"});
                        //$("#backBtn").trigger('click');	
                       // alert("ok");
                    }
                    else {
                        if (rtn.data && rtn.data.message) {
                            Q_Alert_Fail(rtn.data.message, "提示");
                        } else {
                            Q_Alert_Fail('原因未知');
                        }
                    }
                },
                error:function(){
                    Q_Alert_Fail('原因未知');
                }
                })		
        
    }	
			function formatName(cellvalue, options, rowObject){
				return '<a href="pages/admin/announce/template_detail.shtml?id='+rowObject['tmplId']+'" >' + cellvalue + '</a>';
			} 
			function actionButtons(cellvalue, options, rowObject){
				return '<div >' + 
				'<a href="pages/admin/announce/template_add.shtml?id='+rowObject['tmplId']+'"  class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >' +
					'<i class=\"ace-icon fa fa-pencil bigger-120\"></i>' +
				'</a>' +  
				'<button onclick=\"deleteEvent(\''+rowObject['tmplId']+'\')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >' +
					'<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>' +
				'</button>'  + 
				'</div>';
			}

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
					$("#tmplId").val(id);
					//$('#audit-form-modal').modal2({backdrop:"static",show:true});
				};
			
			$('#seachBtn').on('click', function(){
				$(grid_selector).jqGrid('setGridParam',{postData: $('#queryForm').serializeJson()}).trigger("reloadGrid");
			});