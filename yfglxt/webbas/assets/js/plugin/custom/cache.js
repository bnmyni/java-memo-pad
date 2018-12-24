
/**
 * PRM cache 类定义，cache对象在main.jsp 中定义
 * @param {Object} scope
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
var PrmCache = function(scope){
	this.scope = scope;
	this.gloableCache = this._getPrmGoableCache();
	//init
	if(this.gloableCache){
		if(this.gloableCache[this.scope] == undefined){//全局cache，所以不会重新生成cache
			this.gloableCache[this.scope] = {};
		}
	}
	
}

PrmCache.prototype = {
	_getPrmGoableCache : function(){
		if(top===self){//topest window
            if(window.prmGloableCache){
            	return window.prmGloableCache;
            }
        }else{
            var p=parent;
            while(p){
                if(p.prmGloableCache|| p.top==p.self){
                    break;
                }else{
                    p=p.parent;
                }
            }
	        if(p.prmGloableCache){
	            return p.prmGloableCache;
	        }
        }
	},
	put:function(key, value){
		if(this.gloableCache && this.gloableCache[this.scope]){
			this.gloableCache[this.scope][key] = value;
		}
	},
	get:function(key){
		if(this.gloableCache && this.gloableCache[this.scope]){
			return this.gloableCache[this.scope][key];
		}
	},
	clear:function(){
		if(this.gloableCache && this.gloableCache[this.scope]){
			return this.gloableCache[this.scope] = {};
		}
	},
	clearByKey:function(key){
		if(this.gloableCache && this.gloableCache[this.scope]){
			delete this.gloableCache[this.scope][key];
		}
	}
};


/**
 * 查询条件回显工具类,所有的查询条件都保存在main.jsp 的缓存对象中
 * @param {Object} $
 * @memberOf {TypeName} 
 */
;
(function( $, undefined ) {
	
	
	//参数cache定义
	var paramEchoCache = new PrmCache("paramEcho");
	
	
	//数组数据序列化json数据
	var serializeJson = function(array){
		var json = {};
		$(array).each(function(){
			if(json[this.name]){
				if($.isArray(json[this.name])){
					json[this.name].push(this.value);
				}else{
					json[this.name]=[json[this.name],this.value];
				}
			}else{
				json[this.name]=this.value;
			}
		});
		return json;
	}
	
	
	//保存form查询条件
	$.fn.saveQueryParam = function(){
		$(this).each(function(i){//一个页面上可能有多个form
			var forms = paramEchoCache.get(window.location.href);
			if(forms == undefined){
				forms = {};
				paramEchoCache.put(window.location.href,forms);
			}
			forms[$(this).attr('id')] = $(this).serializeArray();
		});
	}
	
	//获取查询条件
	$.fn.getQueryParam = function(){
		var forms = paramEchoCache.get(window.location.href);
		if(forms){
			var fields = forms[$(this).attr('id')];
			if(fields){
				return serializeJson(fields);
			}
		}
		return {};
	}
	
	
	
	$.extend({
		//回显查询条件
		echoQueryParam:function(){
			var forms = paramEchoCache.get(window.location.href);
			if(forms){//多个form
				for(var key in forms){//放置了数据
					var fields = forms[key];
					$.each(fields, function(i, field){
						var f = $('#' + key).find('[name="' + field.name + '"]');
						if(f.length == 0){//兼容火狐浏览器
							f = $('#' + key).parent().find('[name="' + field.name + '"]');
						}
						var vv = field.value;
						if(f.is('input')){
							var _type = f.attr("type");
							if (_type == 'text' || _type == 'hidden'){
								f.val(vv);
							} else if (_type == 'radio'){
								if (f.val() == vv){
									f.attr("checked","checked")
								} 
							} else if (_type == 'checkbox'){
								var arr = vv.split(',');
								for (var i = 0; i < arr.length ;i++){
									if ($(this).val() == arr[i]){
										$(this).attr("checked","checked")
									} 
								}
							} 
						}else if(f.is('select')){
							f.val(vv);
							f.attr('_defaultValue',vv);
							f.trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
						}else if(f.is('textarea')){
							f.val(vv);
						}
					});
				}
			}
		},
		//得到保存的查询条件,对于一个页面只有一个form的情况
		getQueryParams:function(){
			var forms = paramEchoCache.get(window.location.href);
			if(forms){
				for(var id in forms){
					var fields = forms[id];
					if(fields){
						return serializeJson(fields);
					}
				}
				
			}
			return {};
		},
		//清除参数cache
		clearParamCache:function(){
			paramEchoCache.clearByKey(window.location.href);
		}
	});
	
})(jQuery);

$().ready(function() {
	//页面加载完成后回显查询条件
	setTimeout('$.echoQueryParam()',200);
	setTimeout('$.clearParamCache()',300);
});