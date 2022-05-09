package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum ClaimExCodeLevel {
		 CLAIM_LINE("CLAIM_LINE"), CLAIM("CLAIM");

		private final String value;
		private final static Map<String, ClaimExCodeLevel> CONSTANTS = new HashMap<String, ClaimExCodeLevel>();

		static {
			for (ClaimExCodeLevel c : values()) {
				CONSTANTS.put(c.value, c);
			}
		}

		ClaimExCodeLevel(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String value() {
			return this.value;
		}

		public static ClaimExCodeLevel fromValue(String value) {
			ClaimExCodeLevel constant = CONSTANTS.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}
	}
