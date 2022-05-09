package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum ClaimStatusCode {
	CLOSED("CLOSED"),BLANK("");

	private final String value;
	private final static Map<String, ClaimStatusCode> CONSTANTS = new HashMap<String, ClaimStatusCode>();

	static {
		for (ClaimStatusCode c : values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	ClaimStatusCode(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public String value() {
		return this.value;
	}

	public static ClaimStatusCode fromValue(String value) {
		ClaimStatusCode constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new IllegalArgumentException(value);
		} else {
			return constant;
		}
	}

}
