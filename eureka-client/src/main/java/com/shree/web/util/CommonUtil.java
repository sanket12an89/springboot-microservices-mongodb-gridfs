package com.shree.web.util;

import java.util.UUID;

import com.fasterxml.uuid.Generators;

public class CommonUtil {

	public static String uniqueNumberGenerators() {
		UUID uuid = Generators.timeBasedGenerator().generate();
		return uuid.toString().replace("-", "");
	}
}
