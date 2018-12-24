
	
/**
 * 根据cookie名字取cookie的值
 */
getCookie = function (name){
    var start = document.cookie.indexOf( name + "=" );
    var len = start + name.length + 1;
    if ( ( !start ) && ( name != document.cookie.substring( 0, name.length ) ) ) {
    	return null;
    }
    if ( start == -1 )
        return null;

    var end = document.cookie.indexOf( ';', len );
    if ( end == -1 )
        end = document.cookie.length;

    return unescape( document.cookie.substring( len, end ));
}


/**
 * 获取并设置当前页面的CONTEXT_PATH,SERVLET_PATH,SESSION_ID
 */
getContextPath=function(){
    //类似request.getContextPath(),从location.pathname中获取【假定有contextpath】
    var pn=location.pathname;
    var end=pn.indexOf("/",1);
    return pn.substring(0,end);
};
CONTEXT_PATH=getContextPath();
getServletPath=function(){
    var pn=location.pathname;
    var end=pn.indexOf('/',1);
    return pn.substring(end);
};
SERVLET_PATH=getServletPath();
getSessionId=function(){
    var ckName="jsessionid";
    var arr = document.cookie.match(new RegExp("(^| )"+ckName+"=([^;]*)(;|$)"));
    if(arr != null){
        return unescape(arr[2]);
    }else{
        arr=document.cookie.match(new RegExp("(^| )"+ckName.toUpperCase()+"=([^;]*)(;|$)"));
	    if(arr != null){
	        return unescape(arr[2]);
        }else{
            return null;
        }
    }
};
SESSION_ID=getSessionId();
CONTEXT_PATH = getContextPath();






/**
 * 设置所有发起Ajax请求都必须带上的参数
 * 注意：只有通过Jquery.ajax请求的才会有此参数【Ext.data.Store缺省采用Ext.Ajax.request】
 * 这里的domain没有开头的"/"字符
 * isAjax参数用于服务器端判断请求类型(isAjax=true为ajax请求,无此参数或此参数值为false为一般请求)
 * 
 * 使用其他途径发起的请求，如果需要，请自行添加参数
 * 
 * 使用PortalClient时因为前端有可能不在portal应用中,ticket跟domain保存在cookie中
 * 需要先查看cookie,若cookie中无保存则使用SESSION_ID和CONTEXT_PATH
 * 
 */
var COOKIE_DOMAIN = 'portal_domain';
var COOKIE_TICKET = 'ticket';

getDomain = function(){
	var vDomain = getCookie(COOKIE_DOMAIN);
	if(vDomain == null){
		vDomain = DOMAIN;
	}
	if(vDomain == null || vDomain == undefined){
		vDomain = CONTEXT_PATH.substring(1);
	}
	return vDomain;
}


$.appendExtraParams = function(url){
    var vTicket = getCookie(COOKIE_TICKET);
    var vDomain = getDomain();
    if(vTicket==null) {
        vTicket = SESSION_ID;
    }
    if(url.indexOf("?")==-1){
        return url+"?ticket="+vTicket+"&domain="+vDomain;
    }else{
        return url+"&ticket="+vTicket+"&domain="+vDomain;
    }
};

 
 

/**
 *  对Jquery.param 方法进行扩展，增加参数值前后空格去除功能
 * @param {Object} a
 * @param {Object} traditional
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
 
 jQuery.param = function( a, traditional ) {
	var r20 = /%20/g;
	var prefix,
		s = [],
		add = function( key, value ) {
			// If value is a function, invoke it and return its value
			value = jQuery.isFunction( value ) ? value() : ( value == null ? "" : value );
			s[ s.length ] = encodeURIComponent( key ) + "=" + encodeURIComponent( jQuery.trim(value) );
		};

	// Set traditional to true for jQuery <= 1.3.2 behavior.
	if ( traditional === undefined ) {
		traditional = jQuery.ajaxSettings && jQuery.ajaxSettings.traditional;
	}

	// If an array was passed in, assume that it is an array of form elements.
	if ( jQuery.isArray( a ) || ( a.jquery && !jQuery.isPlainObject( a ) ) ) {
		// Serialize the form elements
		jQuery.each( a, function() {
			add( this.name, this.value );
		});

	} else {
		// If traditional, encode the "old" way (the way 1.3.2 or older
		// did it), otherwise encode params recursively.
		for ( prefix in a ) {
			buildParams( prefix, a[ prefix ], traditional, add );
		}
	}

	// Return the resulting serialization
	return s.join( "&" ).replace( r20, "+" );
};

function buildParams( prefix, obj, traditional, add ) {
	var name,
	rbracket = /\[\]$/;
	if ( jQuery.isArray( obj ) ) {
		// Serialize array item.
		jQuery.each( obj, function( i, v ) {
			if ( traditional || rbracket.test( prefix ) ) {
				// Treat each array item as a scalar.
				add( prefix, v );

			} else {
				// Item is non-scalar (array or object), encode its numeric index.
				buildParams( prefix + "[" + ( typeof v === "object" ? i : "" ) + "]", v, traditional, add );
			}
		});

	} else if ( !traditional && jQuery.type( obj ) === "object" ) {
		// Serialize object item.
		for ( name in obj ) {
			buildParams( prefix + "[" + name + "]", obj[ name ], traditional, add );
		}

	} else {
		// Serialize scalar item.
		add( prefix, obj );
	}
}


//对ajax方法进行扩展,此函数不能重复执行
;(function($){  
    var ajax=$.ajax;
    //认证用额外参数
    var extraParams = {ticket:(getCookie(COOKIE_TICKET)!=null?getCookie(COOKIE_TICKET):SESSION_ID),domain:getDomain(),isAjax:true,ts:new Date().getTime()};
    $.ajax=function(s){
    	//添加额外参数
		var extraPramsArray = [];
    	for(var i in extraParams){
    		extraPramsArray.push(i + '=' + extraParams[i]);
    	}
		var extraPramsStr = extraPramsArray.join("&");
    	if(s.url.indexOf("?") == -1){
    		s.url = s.url + "?" + extraPramsStr;
    	}else{
    		s.url = s.url + "&" + extraPramsStr;
    	}
    	
    	//设置为不进行缓存
    	s.cache = false;
    	
    	//扩展success方法
        var oldSucess=s.success;
        s.success=function(data, textStatus, jqXHR){
            //加入扩展代码
        	if(data.success != true && data.success !='true'){
        		var realData = data.data;
    			if(realData && realData.sessionInvalid){
					var redirectUrl = CONTEXT_PATH + "/portal/login.jsp";
					if(realData.redirectUrl){
						if(realData.redirectUrl.indexOf("/")==0){
							redirectUrl =CONTEXT_PATH +  realData.redirectUrl;	
						}else{
							redirectUrl = 	realData.redirectUrl;		
						}
					}
					var tmpUrl = redirectUrl;
					var  isFormword  =  (tmpUrl.replace(/\?.*$/,"") != window.location.pathname);
					if(isFormword){
						alert(realData.msg);
						window.top.location.href = redirectUrl;
						return;
					}
			     }
        	}
        	if(oldSucess){
        		oldSucess(data,textStatus,jqXHR);	
        	}
        }
        var oldError = s.error;
        s.error = function (XMLHttpRequest, textStatus, errorThrown) {
        	$().prm_hide_loading();
        	if("timeout" == textStatus || "error" == textStatus){
        		alert("系统服务器忙，请稍候再试！");
        	}
        }
        ajax(s);  
    }  
})(jQuery); 