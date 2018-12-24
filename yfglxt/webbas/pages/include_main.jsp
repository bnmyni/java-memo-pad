<%@page contentType="java" pageEncoding="utf-8"%>
<%@ page import="com.aspire.webbas.portal.common.config.Config,com.aspire.webbas.portal.common.entity.Staff"%>  
<%
	String realName = "";
	String departmentName = "";
	Staff loginStaff = (Staff)session.getAttribute("LOGIN_STAFF");
	if (null != loginStaff){
		realName = loginStaff.getRealName();
		//departmentName = loginStaff.getDepartment().getDepartmentName();
	}
	String title = Config.getInstance().getTitle();
%>
<script>
	var title = '<%= title%>';
	var realName = "<%=realName%>";
</script>