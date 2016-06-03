package com.liferay.neo4j.sample.portlet;

import com.liferay.neo4j.sample.portlet.connector.Neo4jConnector;
import com.liferay.neo4j.sample.portlet.converter.CustomConverter;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Daniel Kocsis
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=neo4j_sample_portlet_Neo4jSamplePortlet",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class Neo4jSamplePortlet extends MVCPortlet {

	public void connect(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String neo4jHostName = ParamUtil.getString(
			actionRequest, "neo4jHostName");

		Session session = Neo4jConnector.connect(
			neo4jHostName, "neo4j", "neo4j");

		if (session == null) {
			SessionErrors.add(actionRequest, Exception.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		UUID sessionId = UUID.randomUUID();

		_sessionsMap.put(sessionId.toString(), session);

		actionResponse.setRenderParameter(
			"neo4jSessionId", sessionId.toString());
	}

	public void disconnect(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String neo4jSessionId = ParamUtil.getString(
			actionRequest, "neo4jSessionId");

		if (Validator.isNull(neo4jSessionId)) {
			return;
		}

		Session session = _sessionsMap.get(neo4jSessionId);

		if (session != null) {
			_sessionsMap.remove(neo4jSessionId);

			session.close();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), themeDisplay.getPlid(),
			PortletRequest.RENDER_PHASE);

		actionRequest.setAttribute(WebKeys.REDIRECT, portletURL.toString());

		sendRedirect(actionRequest, actionResponse);
	}

	public void query(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String neo4jSessionId = ParamUtil.getString(
			actionRequest, "neo4jSessionId");

		if (Validator.isNull(neo4jSessionId)) {
			disconnect(actionRequest, actionResponse);

			return;
		}

		Session session = _sessionsMap.get(neo4jSessionId);

		if (session == null) {
			disconnect(actionRequest, actionResponse);

			return;
		}

		String cypher = ParamUtil.getString(actionRequest, "cypher");

		if (Validator.isNull(cypher)) {
			return;
		}

		StatementResult result = session.run(cypher);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		result
			.list(r -> r.asMap(CustomConverter::convert))
			.stream()
			.map(r -> Neo4jSamplePortlet.toJson(r))
			.forEach(jsonArray::put);

		actionResponse.setRenderParameter(
			"neo4jQueryResult", jsonArray.toString());
	}

	protected static JSONObject toJson(Map<String, Object> map) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}

		return jsonObject;
	}

	@Activate
	protected void activate() {
		_sessionsMap = new ConcurrentHashMap<>();
	}

	@Deactivate
	protected void deactivate() {
		if (_sessionsMap.isEmpty()) {
			return;
		}

		for (Session session : _sessionsMap.values()) {
			session.close();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Neo4jSamplePortlet.class);

	private Map<String, Session> _sessionsMap;

}