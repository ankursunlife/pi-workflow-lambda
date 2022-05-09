package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum ActionTaken {
	ACCEPT("ACCEPT"), REJECT("REJECT"),OVERRIDE("OVERRIDE"),OVERRIDE_REWORK("OVERRIDE_REWORK"),APPROVE("APPROVE");

	private final String value;
	private final static Map<String, ActionTaken> CONSTANTS = new HashMap<String, ActionTaken>();

	static {
		for (ActionTaken c : values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	ActionTaken(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public String value() {
		return this.value;
	}

	public static ActionTaken fromValue(String value) {
		ActionTaken constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new IllegalArgumentException(value);
		} else {
			return constant;
		}
	}

}
