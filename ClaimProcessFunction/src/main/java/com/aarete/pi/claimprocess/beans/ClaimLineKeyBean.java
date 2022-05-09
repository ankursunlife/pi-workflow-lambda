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
public class ClaimLineKeyBean {

	private String claimLineId;
	private String claimLineNum;
	//private String batchID;
}
