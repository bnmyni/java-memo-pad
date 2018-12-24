<%@page contentType="java" pageEncoding="utf-8"%>
<%
response.setCharacterEncoding("utf-8");
out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />");
out.println("<meta charset=\"utf-8\" />");
out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\" />");
out.println("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
out.println("<meta http-equiv=\"Cache-Control\" content=\"no-cache\">");
out.println("<meta http-equiv=\"Expires\" content=\"0\">");
out.println("<base href= '" + request.getScheme()+"://" + request.getHeader("host") + request.getContextPath() + "/' />");
out.flush();
%>
<%
	String version ="EUMP1.0.0.001";
	String ctxPath = request.getContextPath();
%>
<script>
	window.ctxPaths = "<%= ctxPath%>";
</script>