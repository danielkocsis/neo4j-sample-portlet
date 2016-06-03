package com.liferay.neo4j.sample.portlet.connector;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

/**
 * @author Daniel Kocsis
 */
public class Neo4jConnector {

	public static Session connect(
		String host, String username, String password) {

		if (Validator.isNull(host)) {
			return null;
		}

		if (!host.startsWith(_BOLT_PROTOCOL_PREFIX)) {
			host = _BOLT_PROTOCOL_PREFIX.concat(host);
		}

		AuthToken authToken = AuthTokens.none();

		if (Validator.isNotNull(password)) {
			authToken = AuthTokens.basic(username, password);
		}

		Config config = Config.build()
			.withMaxSessions(10)
			.withEncryptionLevel(Config.EncryptionLevel.NONE)
			.toConfig();

		Session session = null;

		try (Driver driver = GraphDatabase.driver(host, authToken, config)) {
			session = driver.session();
		}
		catch (Exception e) {
			_log.error("Unable to create Neo4j session: " + e);
		}

		return session;
	}

	private static final String _BOLT_PROTOCOL_PREFIX = "bolt://";

	private static final Log _log = LogFactoryUtil.getLog(Neo4jConnector.class);

}