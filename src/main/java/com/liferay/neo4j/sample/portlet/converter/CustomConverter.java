package com.liferay.neo4j.sample.portlet.converter;

import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Type;

/**
 * @author Daniel Kocsis
 */
public class CustomConverter {

	public static Object convert(Value value) {
		Type type = value.type();

		switch (type.name()) {
			case "PATH":
				return value.asList(CustomConverter::convert);
			case "NODE":
			case "RELATIONSHIP":
				return value.asMap();
		}

		return value.asObject();
	}

}