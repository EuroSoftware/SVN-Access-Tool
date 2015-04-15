<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="header.jsp" flush="true">
	<jsp:param name="title" value="SVN Access Tool - Log Reader" />
</jsp:include>

<H1>SVN Access Tool - Log Reader</H1>
<H1>${error}</H1>
<jsp:include page="footer.jsp" flush="true" />