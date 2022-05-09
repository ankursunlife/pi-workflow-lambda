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
public class ClaimAcceptRequestMessageBean {

	private String actionTaken;
	private String approverLevel;
	private String actionTakenBy;
	private String comments;
	private int rejectReasonId;
	private long claimNum;
	private String exCode;
	private String claimExCodeLevel;
	private String engagementId;
	private int clientId;
	private String workflowStatus;
	private List<ClaimLineBean> claimLines;

	public ClaimAcceptRequestMessageBean(String json) {
		Gson gson = new Gson();
		ClaimAcceptRequestMessageBean request = gson.fromJson(json, ClaimAcceptRequestMessageBean.class);
		this.actionTaken = request.getActionTaken();
		this.claimLines = request.getClaimLines();
		this.approverLevel = request.getApproverLevel();
		this.comments = request.getComments();
		this.rejectReasonId = request.getRejectReasonId();
		this.claimNum = request.getClaimNum();
		this.exCode = request.getExCode();
		this.claimExCodeLevel = request.getClaimExCodeLevel();
		this.engagementId = request.getEngagementId();
		this.clientId = request.getClientId();
		this.actionTakenBy = request.getActionTakenBy();
		this.workflowStatus = request.getWorkflowStatus();
	}

}
