package com.digitusforum.firewall.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.jackson.JsonNodeValueReader;

public class Util {
	
	public static ModelMapper map = new ModelMapper();

	public static ModelMapper getMapper() {
		map.getConfiguration().addValueReader(new JsonNodeValueReader());
		return map;
	}
}
