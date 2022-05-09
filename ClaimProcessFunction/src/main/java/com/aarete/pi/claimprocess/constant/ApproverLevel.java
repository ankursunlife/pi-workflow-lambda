package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum ApproverLevel {
		AARETE_USER("AARETE_USER"), AARETE_MANAGER("AARETE_MANAGER"),CLIENT_USER("CLIENT_USER"), CLIENT_MANAGER("CLIENT_MANAGER");

		private final String value;
		private final static Map<String, ApproverLevel> CONSTANTS = new HashMap<String, ApproverLevel>();

		static {
			for (ApproverLevel c : values()) {
				CONSTANTS.put(c.value, c);
			}
		}

		ApproverLevel(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String value() {
			return this.value;
		}

		public static ApproverLevel fromValue(String value) {
			ApproverLevel constant = CONSTANTS.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}
	}
