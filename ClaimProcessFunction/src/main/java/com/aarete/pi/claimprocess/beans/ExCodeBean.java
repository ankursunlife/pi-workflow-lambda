package com.aarete.pi.claimprocess.beans;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExCodeBean {

	private String exCodeId;// e.g. DUP,PTP

	private String exCodeName;// e.g. DUP,PTP

	private String pillar;

	private String source;

	private float confidence;

	private String brief;

	private String lineOpportunityAmount;

	private String actionTaken;// e.g. ACCEPT, REJECT

	private String opportunityClaimNum;

	private String exCodeType;

	private String claimExCodeLevel;
	
	private String approverOneAction;
	private String approverTwoAction;
	private String approverThreeAction;
	private String approverFourAction;
	
	private double prioritizationScore;
	
	//private int sortOrder;
	private int orderRating;
	private boolean isActive;

	public ExCodeBean(String json) {
		Gson gson = new Gson();
		ExCodeBean request = gson.fromJson(json, ExCodeBean.class);
		
		this.exCodeId = request.getExCodeId();
		this.exCodeName = request.getExCodeName();
		this.pillar = request.getPillar();
		this.source = request.getSource();
		this.confidence = request.getConfidence();
		this.brief = request.getBrief();
		this.lineOpportunityAmount = request.getLineOpportunityAmount();
		this.actionTaken = request.getActionTaken();
		this.opportunityClaimNum = request.getOpportunityClaimNum();		
		this.exCodeType = request.getExCodeType();
		this.claimExCodeLevel = request.getClaimExCodeLevel();
		this.approverOneAction = request.getApproverOneAction();
		this.approverTwoAction = request.getApproverTwoAction();
		this.approverThreeAction = request.getApproverThreeAction();
		this.approverFourAction = request.getApproverFourAction();
		this.prioritizationScore = request.getPrioritizationScore();
		this.orderRating = request.getOrderRating();
		this.isActive = request.isActive();
	}

}
