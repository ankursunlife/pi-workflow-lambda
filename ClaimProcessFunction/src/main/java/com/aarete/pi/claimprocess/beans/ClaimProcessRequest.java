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
public class ClaimProcessRequest {

	private String claimLineId;

	private String claimNum;

	private String claimLineNum;
	
	private String actionTaken;
	
	private String actionTakenBy;
	
	private String approverLevel;
	
	private String claimLevelExCode;
	
	private String batchID;
	
	private String clientId;
	
	private String comments;
	
	private int rejectReasonId;
	
	private String exCodeLevel;
	
	private String engagementRole;
	
	private String engagementId;
	
	private String exCode;
	private String assignedToUser;
	
	private List<ExCodeBean> exCodeBeanList;
	
	public ClaimProcessRequest(String json) {
		Gson gson = new Gson();
		ClaimProcessRequest request = gson.fromJson(json, ClaimProcessRequest.class);
       this.actionTaken = request.getActionTaken();
       this.claimLineId = request.getClaimLineId();
       this.claimLineNum = request.getClaimLineNum();
       this.comments = request.getComments();
       this.rejectReasonId = request.getRejectReasonId();
       this.claimNum = request.getClaimNum();
       this.exCode = request.getExCode();
       this.exCodeLevel = request.getExCodeLevel();
       this.engagementId = request.getEngagementId();
       this.engagementRole = request.getEngagementRole();
       this.exCodeBeanList = request.getExCodeBeanList();
       this.batchID = request.getBatchID();
       this.clientId = request.getClientId();
       this.actionTaken = request.getActionTaken();
       this.actionTakenBy = request.getActionTakenBy();
       this.approverLevel = request.getApproverLevel();
       this.claimLevelExCode = request.getClaimLevelExCode();
       this.assignedToUser = request.getAssignedToUser();
	}
	
	
}
