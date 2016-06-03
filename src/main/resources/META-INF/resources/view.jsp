<%@ include file="/init.jsp" %>

<%
String neo4jSessionId = ParamUtil.getString(request, "neo4jSessionId");
String result = ParamUtil.getString(request, "neo4jQueryResult");
%>

<c:choose>
	<c:when test="<%= Validator.isNull(neo4jSessionId) %>">
		<portlet:actionURL name="connect" var="connectURL" />

		<aui:form action="<%= connectURL %>" method="post" name="fm1">
			<aui:input label="host-url" name="neo4jHostName" placeholder="hostname e.g.: localhost" showRequiredLabel="<%= true %>" type="text">
				<aui:validator name="required" />
			</aui:input>

			<aui:button-row>
				<aui:button cssClass="btn-lg" type="submit" value="connect" />
			</aui:button-row>
		</aui:form>
	</c:when>
	<c:otherwise>
		<portlet:actionURL name="query" var="queryURL">
			<portlet:param name="neo4jSessionId" value="<%= neo4jSessionId %>" />
		</portlet:actionURL>

		<aui:form action="<%= queryURL %>" method="post" name="fm1">
			<aui:input label="cypher" name="cypher" placeholder="MATCH (n) RETURN n" type="textBox" />

			<aui:input cssClass="lfr-textarea-container" disabled="true" label="result" name="result" resizable="true" type="textarea" value="<%= result %>" wrap="soft" />

			<aui:button-row>
				<aui:button cssClass="btn-lg" type="submit" value="run" />

				<portlet:actionURL name="disconnect" var="disconnectURL">
					<portlet:param name="neo4jSessionId" value="<%= neo4jSessionId %>" />
				</portlet:actionURL>

				<aui:button cssClass="btn-lg" href="<%= disconnectURL %>" name="disconnectButton" value="disconnect" />
			</aui:button-row>
		</aui:form>
	</c:otherwise>
</c:choose>