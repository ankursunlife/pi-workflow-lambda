package com.aarete.pi.claimprocess.beans;

import com.google.gson.Gson;

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
public class ClaimFetchInfoRequestBean {

	private String claimNum;
	private String engagementId;
	private String claimLineId;

	public ClaimFetchInfoRequestBean(String json) {
		Gson gson = new Gson();
		ClaimFetchInfoRequestBean request = gson.fromJson(json, ClaimFetchInfoRequestBean.class);
		this.claimNum = request.getClaimNum();
		this.engagementId = request.getEngagementId();
		this.claimLineId = request.getClaimLineId();
	}

}
