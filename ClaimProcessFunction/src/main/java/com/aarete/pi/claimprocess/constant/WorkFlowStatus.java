package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum WorkFlowStatus {
	SUCCESS("SUCCESS"), FAILED("FAILED");

	private final String value;
	private final static Map<String, WorkFlowStatus> CONSTANTS = new HashMap<String, WorkFlowStatus>();

	static {
		for (WorkFlowStatus c : values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	WorkFlowStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public String value() {
		return this.value;
	}

	public static WorkFlowStatus fromValue(String value) {
		WorkFlowStatus constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new IllegalArgumentException(value);
		} else {
			return constant;
		}
	}

}
