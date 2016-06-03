<%@ include file="/init.jsp" %>

<liferay-ui:error-header />

<liferay-ui:error exception="<%= Exception.class %>" message="unexpected-error-happened" />

<liferay-ui:error-principal />