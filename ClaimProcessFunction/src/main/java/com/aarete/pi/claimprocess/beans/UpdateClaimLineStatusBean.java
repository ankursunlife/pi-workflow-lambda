package com.aarete.pi.claimprocess.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClaimLineStatusBean {

	private String claimLineId;
	private String claimStatusCode;

}
