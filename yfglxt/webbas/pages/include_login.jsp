<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page
	import="com.aspire.webbas.portal.common.config.Config,com.aspire.webbas.portal.common.util.RSAUtil"%>
<%
	String requestUrl = request.getParameter("requestUrl");
	if (requestUrl != null) {
	    requestUrl = java.net.URLEncoder.encode(requestUrl, "UTF-8");
	}
	
	String title = Config.getInstance().getTitle();
	String modulus = RSAUtil.bigIntToHexStr(RSAUtil.getDefaultPublicKey().getModulus());
	String exponent = RSAUtil.bigIntToHexStr(RSAUtil.getDefaultPublicKey().getPublicExponent());
	
	/*
	boolean enableCheckCode = Config.getInstance().enableCheckCode();
	*/
%>
<script type="text/javascript">
    window.title      = '<%=title%>';
	window.requestUrl = '<%=requestUrl%>';
	window.modulus    = '<%=modulus%>';
	window.exponent   = '<%=exponent%>';
</script>
