package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum ExCodeTypes {
		 CLAIM_LINE("CLAIM_LINE"), CLAIM("CLAIM");

		private final String value;
		private final static Map<String, ExCodeTypes> CONSTANTS = new HashMap<String, ExCodeTypes>();

		static {
			for (ExCodeTypes c : values()) {
				CONSTANTS.put(c.value, c);
			}
		}

		ExCodeTypes(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String value() {
			return this.value;
		}

		public static ExCodeTypes fromValue(String value) {
			ExCodeTypes constant = CONSTANTS.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}
	}

