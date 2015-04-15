<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="header.jsp" flush="true">
	<jsp:param name="title" value="SVN Access Tool - Log Reader" />
</jsp:include>

<H1>SVN Access Tool - Log Reader</H1>

<script type="text/javascript">
	function GetSelectedItem() {

		chosen = "";
		len = document.filter.radio_button.length

		for (i = 0; i < len; i++) {
			if (document.filter.radio_button[i].checked) {
				chosen = document.filter.radio_button[i].value
			}
		}

		if (chosen == "") {
			alert("No Location Chosen")
		} else if (chosen == "daysAgo") {
			document.filter.from.disabled = true;
			document.filter.daysAgo.disabled = false;
		} else if (chosen == "from") {
			document.filter.daysAgo.disabled = true;
			document.filter.from.disabled = false;
		}
	}
</script>

<form action="ShowResultsServlet" method="post" name="filter">
	<div>
		<table class="featuresTable" style="width: 500px">
			<tr>
				<td>Time interval from:</td>
				<td><input type="text" name="from"
					value="<c:out value="${from}" escapeXml="true" />" /></td>
				<td><input type="radio" name="radio_button" checked="checked"
					value="from" onClick=GetSelectedItem() /></td>
			</tr>
			<tr>
				<td>Time interval to:</td>
				<td><input type="text" name="to"
					value="<c:out value="${from}" escapeXml="true" />" /></td>
				<td></td>
			</tr>
			<tr>
				<td>Search:</td>
				<td><input type="text" name="search" value="" /></td>
				<td></td>
			</tr>
			<tr>
				<td>Search in:</td>
				<td><select name="repos" size="1">
						<option value="#allRepos">All repositories</option>
						<c:forEach items="${repository}" var="svn">
							<option value="${svn}">${svn}</option>
						</c:forEach>
				</select></td>
				<td></td>
			</tr>
			<tr>
				<td>Days Ago:</td>
				<td><input type="text" name="daysAgo" disabled="disabled"
					value="<c:out value="${daysAgo}" escapeXml="true" />" /></td>
				<td><input type="radio" name="radio_button" value="daysAgo"
					onClick=GetSelectedItem() /></td>
			</tr>
		</table>
		<div align="right" style="width: 500px; margin: 0 auto;">
			<input type="submit" name="show_button" value="show"
				style="margin: 10px; margin-right: 30px;">
		</div>
	</div>
</form>


<jsp:include page="footer.jsp" flush="true" />