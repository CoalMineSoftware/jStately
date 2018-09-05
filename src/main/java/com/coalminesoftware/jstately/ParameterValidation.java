package com.coalminesoftware.jstately;

public final class ParameterValidation {
	private ParameterValidation() { }

	public static void assertNotNull(String message, Object parameter) {
		if(parameter == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
