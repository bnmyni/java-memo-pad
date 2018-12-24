var MultSelect2 = function(contenterId) {
	this._options = {
			"mode" : 0,//0表select2移除不进入到select1,1表select2移除进入到select1
			"select1" : '',//选择列表select的id
			"select2" : '',//选中列表select的id
			"addBtn" : '',//添加按钮id
			"delBtn" : '',//删除按钮id
			"addAllBtn" : '',//添加所有按钮id
			"delAllBtn" : '',//删除所有按钮id
			"id" : '',//传过来list对应的下拉列表的key别名
			"text" : '',//传过来list对应的下拉列表的text别名
			"data1":[],//选择列表select初始值
			"data2":[]//选中列表select初始值
	};
};
MultSelect2.prototype = {
		_appendSelect : function($select,list){
			var _this = this;
			$select.data('datas',list);
			$.each(list,function(i,val){
				$select.append('<option value="' + val[_this._options['id']] + '">' + val[_this._options['text']] + '</option>');
			});
		},
		_str2Obj : function(obj){
			if (typeof obj === 'string' && obj !==''){
				return $('#' +obj);
			}else{
				return obj;
			}
		},
		getSelectData : function(key,datas){
			var _list = datas || this.getDatas();
			if (_list){
				for (var i = 0; i < _list.length; i ++)
					if (_list[i][this._options['id']] == key){
						return _list[i];
					}
			}
			return null;
		},
		initSelect1 : function(datas){
			var _datas = datas || [];
			var $select1 = this._str2Obj(this._options['select1']); 
			$select1.empty(); 
			this._appendSelect($select1,_datas);
		},
		initSelect2 : function(datas){
			var _datas = datas || [];
			var $select2 = this._str2Obj(this._options['select2']); 
			$select2.empty(); 
			this._appendSelect($select2,_datas);
		},
		init : function(options){
			$.extend(this._options,options);
			var _o = this._options,_this = this;
			var $addBtn = _this._str2Obj(_o['addBtn']),$delBtn =  _this._str2Obj(_o['delBtn']),$addAllBtn =  _this._str2Obj(_o['addAllBtn']),$delAllBtn =  _this._str2Obj(_o['delAllBtn']),$select1 =  _this._str2Obj(_o['select1']),$select2 =  _this._str2Obj(_o['select2']);
			$select1.empty(); 
			_this._appendSelect($select1,_o['data1']);
			if (_o['mode'] ===1){
				$select2.empty(); 
			}
			_this._appendSelect($select2,_o['data2']);
			 $select1.unbind('dblclick').bind('dblclick',function(){
					var options=$(this).find('option:selected'); 
				 	$.each(options,function(i,val){
						if ($select2.find('option[value="'+$(val).attr('value')+'"]').length == 0){
							$select2.append(_this._options['mode'] === 0 ? $(val).clone(true) : $(val));
						}
					});
			});
			 $select2.unbind('dblclick').bind('dblclick',function(){
				 	var options = $(this).find('option:selected');
				 	if (_this._options['mode'] === 0){
				 		options.remove();
				 	}else{
				 		$.each(options,function(i,val){
							if ($select1.find('option[value="'+$(val).attr('value')+'"]').length == 0){
								$select1.append($(val));
							}else{
								$(val).remove();
							}
						});
				 	}
			});
			if ($addBtn){
				$addBtn.unbind('click').bind('click',function(){
					var options=$select1.find('option:selected'); 
					$.each(options,function(i,val){
						if ($select2.find('option[value="'+$(val).attr('value')+'"]').length == 0){
								$select2.append(_this._options['mode'] === 0 ? $(val).clone(true) :  $(val));
						}
					});
				});
			}
			if ($addAllBtn){
				$addAllBtn.unbind('click').bind('click',function(){
					var options=$select1.find('option'); 
				 		$.each(options,function(i,val){
				 			$.each(options,function(i,val){
								if ($select2.find('option[value="'+$(val).attr('value')+'"]').length == 0){
									$select2.append(_this._options['mode'] === 0 ? $(val).clone(true) :  $(val));
								}
							});
						});
				});
			}
			if ($delBtn){
				$delBtn.unbind('click').bind('click',function(){
					var options = $select2.find('option:selected'); 
					if (_this._options['mode'] === 0){
				 		options.remove();
				 	}else{
				 		$.each(options,function(i,val){
							if ($select1.find('option[value="'+$(val).attr('value')+'"]').length == 0){
								$select1.append($(val));
							}else{
								$(val).remove();
							}
						});
				 	}
				});
			}
			if ($delAllBtn){
				$delAllBtn.unbind('click').bind('click',function(){
					var options = $select2.find('option');
					if (_this._options['mode'] === 0){
				 		options.remove();
				 	}else{
				 		$.each(options,function(i,val){
							if ($select1.find('option[value="'+$(val).attr('value')+'"]').length == 0){
								$select1.append($(val));
							}else{
								$(val).remove();
							}
						});
				 	}
				});
			}
		},
		getDatas : function(){
			var $select1 = this._str2Obj(this._options['select1']),$select2 = this._str2Obj(this._options['select2']); 
			var _list1 = $select1.data('datas'),_list2 = $select2.data('datas'),_list = $.merge(_list1,_list2);
			return _list;
		},
		getSelectedDatas : function(){
			var _this = this,$select=  _this._str2Obj(_this._options['select2']),_datas = _this.getDatas(),datas = [];
			if ($select){
				var options=$select.find('option');
				options.each(function(){
					var value = $(this).attr('value'),text = $(this).text();
					var obj = _this.getSelectData(value,_datas);
					//var obj = {};
					//obj[_this._options['id']] = value;
					//obj[_this._options['text']] = text;
					datas.push(obj);
				});
			}
			return datas;
		}
};