<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="header.jsp" flush="true">
	<jsp:param name="title" value="SVN Access Tool - Log Reader" />
</jsp:include>

<H1>SVN Access Tool - Log Reader</H1>
<H2>
	Results from:
	<c:out value="${from}" escapeXml="true" />
	to:
	<c:out value="${to}" escapeXml="true" />
</H2>

<table border=1 class="featuresTable">
	<c:forEach items="${logs}" var="log">

		<c:if test="${log.valid == false}">
			<tr class="red_font">
		</c:if>
		<c:if test="${log.valid == true}">
			<tr class="green_font">
		</c:if>
		<th><c:out value="${log.typeOfMessage}" escapeXml="true" /></th>
		<th><c:out value="${log.dateOfMessage}" escapeXml="true" /></th>
		<th><c:out value="${log.timeOfMessage}" escapeXml="true" /></th>
		<th><c:out value="${log.loggedUser}" escapeXml="false" /></th>
		</tr>
		<tr>
			<td colspan=4><c:out value="${log.node}" escapeXml="true" /></td>
		</tr>
		<tr class="h">
			<td colspan=4>Group changes:</td>
		</tr>

		<c:forEach items="${log.groupChanges}" var="group">
			<tr>
				<td class="no_right_border"></td>
				<td colspan="3" class="no_left_border"><c:out value="${group}"
						escapeXml="false"/></td>
			</tr>
		</c:forEach>


		<tr class="h">
			<td colspan=4>User changes:</td>
		</tr>
		<c:forEach items="${log.userChanges}" var="user">
			<tr>
				<td class="no_right_border"></td>
				<td colspan="3" class="no_left_border"><c:out value="${user}"
						escapeXml="false" /></td>
			</tr>
		</c:forEach>

		<c:if test="${!empty log.inheritance}">
			<tr class="h">
				<td colspan=4><c:out value="${log.inheritance}"
						escapeXml="true" /></td>
			</tr>
		</c:if>


		<c:if test="${log.valid == true}">
			<tr class="h">
				<td colspan=4>Backup files:</td>
			</tr>
			<tr>
				<td class="no_right_border"></td>
				<td colspan="3" class="no_left_border"><c:out
						value="${log.backupFileAcl}" escapeXml="false" /></td>
			</tr>
			<tr>
				<td class="no_right_border"></td>
				<td colspan="3" class="no_left_border"><c:out
						value="${log.backupFileModAcl}" escapeXml="false" /></td>
			</tr>
		</c:if>
		<tr>
			<th colspan="4" class="spacerow"></th>
		</tr>

	</c:forEach>

</table>

<br />
<h2><a href="./LogReader">back</a></h2>
<jsp:include page="footer.jsp" flush="true" />