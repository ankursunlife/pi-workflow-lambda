package com.aarete.pi.claimprocess.beans;

import java.util.List;

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
public class ClaimLineBean {

	private long claimLineId;
	private int claimLineNum;
	private String approverLevelOne;
	private String approverLevelTwo;
	private String approverLevelThree;
	private String approverLevelFour;
	private long batchId;
	private String claimStatusCode;
	private boolean isSelected;
	private String status;
	private String claimStatusLevelFour;
	private String claimStatusLevelOne;
	private String claimStatusLevelThree;
	private String claimStatusLevelTwo;
	private List<ExCodeBean> exCodeBeanList;
	private List<ClaimLineListBean> claimLineList;

	public ClaimLineBean(String json) {
		Gson gson = new Gson();
		ClaimLineBean request = gson.fromJson(json, ClaimLineBean.class);
		this.claimLineId = request.getClaimLineId();
		this.claimLineNum = request.getClaimLineNum();
		this.approverLevelOne = request.getApproverLevelOne();
		this.approverLevelTwo = request.getApproverLevelTwo();
		this.approverLevelThree = request.getApproverLevelThree();
		this.approverLevelFour = request.getApproverLevelFour();
		this.batchId = request.getBatchId();
		this.exCodeBeanList = request.getExCodeBeanList();
		this.claimStatusCode = request.getClaimStatusCode();
		this.claimLineList = request.getClaimLineList();
		this.isSelected = request.isSelected();
		this.status = request.getStatus();
		this.claimStatusLevelFour = request.getClaimStatusLevelFour();
		this.claimStatusLevelThree = request.getClaimStatusLevelThree();
		this.claimStatusLevelTwo = request.getClaimStatusLevelTwo();
		this.claimStatusLevelOne = request.getClaimStatusLevelOne();
	}

}
