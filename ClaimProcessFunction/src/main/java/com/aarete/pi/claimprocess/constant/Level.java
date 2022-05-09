package com.aarete.pi.claimprocess.constant;

import java.util.HashMap;
import java.util.Map;

public enum Level {
	ONE("ONE"), TWO("TWO"), THREE("THREE"), FOUR("FOUR");

		private final String value;
		private final static Map<String, Level> CONSTANTS = new HashMap<String, Level>();

		static {
			for (Level c : values()) {
				CONSTANTS.put(c.value, c);
			}
		}

		Level(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String value() {
			return this.value;
		}

		public static Level fromValue(String value) {
			Level constant = CONSTANTS.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}
	}
