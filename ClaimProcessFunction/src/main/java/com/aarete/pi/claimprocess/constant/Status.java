package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	GROUP_QUEUE("GROUP_QUEUE"), MY_QUEUE("MY_QUEUE"),PEND("PEND"),WAITING("WAITING"),BLANK(""),NULL(null);

	private final String value;
	private final static Map<String, Status> CONSTANTS = new HashMap<String, Status>();

	static {
		for (Status c : values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	Status(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public String value() {
		return this.value;
	}

	public static Status fromValue(String value) {
		Status constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new IllegalArgumentException(value);
		} else {
			return constant;
		}
	}

}
