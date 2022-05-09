package com.aarete.pi.claimprocess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aarete.pi.claimprocess.beans.ClaimAcceptRequestMessageBean;
import com.aarete.pi.claimprocess.beans.ClaimFetchInfoRequestBean;
import com.aarete.pi.claimprocess.beans.ClaimLineBean;
import com.aarete.pi.claimprocess.beans.ClaimLineKeyBean;
import com.aarete.pi.claimprocess.beans.ClaimProcessRequest;
import com.aarete.pi.claimprocess.beans.ExCodeBean;
import com.aarete.pi.claimprocess.beans.UpdateClaimLineStatusBean;
import com.aarete.pi.claimprocess.constant.MetaDataConstants;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ClaimProcessHelper {

	private ClaimProcessHelper() {

	}

	public static void processClaim(String claims) {
		JSONObject jsonObj = new JSONObject(claims);
		JSONArray arrayData = jsonObj.getJSONArray("claimProcessList");
		List<ClaimLineBean> claimLines = new ArrayList<ClaimLineBean>();

		for (int i = 0; i < arrayData.length(); i++) {
			ClaimProcessRequest claimProcessRequest = new ClaimProcessRequest(arrayData.getJSONObject(i).toString());

			ClaimAcceptRequestMessageBean claimAcceptRequestMessageBean = new ClaimAcceptRequestMessageBean();

			mapClaimBean(claimProcessRequest, claimAcceptRequestMessageBean);

			String allClaimLineForClaimJSONString = createGetClaimLinesForClaimRequest(claimProcessRequest);
			claimLines = mapClaimLines(DataBaseHelper.getAllClaimLinesForClaims(allClaimLineForClaimJSONString),claimProcessRequest.getApproverLevel());

			claimAcceptRequestMessageBean.setClaimLines(claimLines);

			// SEND Message to Queue
			String queueUrl = System.getenv("SQS_ACCEPT_REQUEST");
			SqsHelper.postMessageToQueue(Helper.convertToJson(claimAcceptRequestMessageBean), queueUrl);

			// Call status Update procedure
			  updateClaimLineStatus(claimLines,"IN_PROGRESS");
		}

	}

	public static void processClaimLines(String input) throws Exception {

		System.out.println("input string: " + input);
		JSONObject jsonObj = new JSONObject(input);
		JSONArray arrayData = jsonObj.getJSONArray("claimProcessList");

		// Firstly Validate
		validateClaimLines(arrayData);
		 System.out.println("Request Validated Successfullly ");

		Map<ClaimLineKeyBean, ClaimLineBean> selectedClaimLinesForClaims = getMapOfSelectedClaimLinesForClaim(
				arrayData);
		Map<ClaimLineKeyBean, ClaimLineBean> allClaimLinesForClaims = getMapOfAllClaimLineForClaim(arrayData);

		for (ClaimLineKeyBean claimLineKeyBean : selectedClaimLinesForClaims.keySet()) {
			allClaimLinesForClaims.put(claimLineKeyBean, selectedClaimLinesForClaims.get(claimLineKeyBean));
			allClaimLinesForClaims.get(claimLineKeyBean).setSelected(Boolean.TRUE);
		}
		
		List<ClaimLineBean> claimLineBeanForDecision = mapClaimLineForDecision(selectedClaimLinesForClaims,allClaimLinesForClaims);

		// Start creating Decision request Message Bean
		ClaimAcceptRequestMessageBean claimAcceptRequestMessageBean = new ClaimAcceptRequestMessageBean();
		mapClaimBean( new ClaimProcessRequest(arrayData.getJSONObject(0).toString()), claimAcceptRequestMessageBean);
		
		List<ClaimLineBean> allUpdatedClaimLineBeanForCLaim = new ArrayList<ClaimLineBean>(allClaimLinesForClaims.values());
		claimAcceptRequestMessageBean.setClaimLines(allUpdatedClaimLineBeanForCLaim);
		
		String queueUrl = System.getenv("SQS_ACCEPT_REQUEST");
		SqsHelper.postMessageToQueue(Helper.convertToJson(claimAcceptRequestMessageBean), queueUrl);

		// Call status Update procedure
		updateClaimLineStatus(allUpdatedClaimLineBeanForCLaim,"IN_PROGRESS");
	}

	private static List<ClaimLineBean> mapClaimLineForDecision(
			Map<ClaimLineKeyBean, ClaimLineBean> selectedClaimLinesForClaims,
			Map<ClaimLineKeyBean, ClaimLineBean> allClaimLinesForClaims) {
		
		List<ClaimLineBean> result = new ArrayList<ClaimLineBean>();
		for (ClaimLineKeyBean claimLineKeyBean : selectedClaimLinesForClaims.keySet()) {
			allClaimLinesForClaims.put(claimLineKeyBean, selectedClaimLinesForClaims.get(claimLineKeyBean));
			allClaimLinesForClaims.get(claimLineKeyBean).setSelected(Boolean.TRUE);
		}
		
		return result;
	}

	/**
	 * @param arrayData
	 * @throws JSONException
	 */
	private static void validateClaimLines(JSONArray arrayData) throws JSONException {
		for (int i = 0; i < arrayData.length(); i++) {
			ClaimProcessRequest claimProcessRequest = new ClaimProcessRequest(arrayData.getJSONObject(i).toString());
			// In case of any invalid request Exceptioin will occur and lambda function will
			// break
			Helper.validateClaimLineRequest(claimProcessRequest);
		}
	}

	public static void processUnProcessedClaim(String input) {
		ClaimAcceptRequestMessageBean claimAcceptRequestMessageBean = new ClaimAcceptRequestMessageBean(input);
		// Call status Update procedure
		updateClaimLineStatus(claimAcceptRequestMessageBean.getClaimLines(),"");
	}

	public static ClaimAcceptRequestMessageBean createClaimLineMesage(ClaimProcessRequest request) {
		ClaimAcceptRequestMessageBean claim = new ClaimAcceptRequestMessageBean();
		ClaimLineBean claimLine = new ClaimLineBean();
		mapClaimBean(request, claim);
		claim.setClaimLines(new ArrayList<ClaimLineBean>());
		claimLine.setClaimLineId(Long.parseLong(request.getClaimLineId()));
		claimLine.setClaimLineNum(Integer.valueOf(request.getClaimLineNum()));
		claim.getClaimLines().add(claimLine);

		return claim;
	}

	public static ClaimAcceptRequestMessageBean createClaimMesage(ClaimProcessRequest request) {
		ClaimAcceptRequestMessageBean result = new ClaimAcceptRequestMessageBean();

		return result;
	}

	public static void processClaimResponse() {
		List<Message> messages = SqsHelper.readMessageFromQueue();
		System.out.println("Total Message recived: " + messages.size());
		for (Message message : messages) {
			System.out.println(message.getBody());

		}
	}

	public static void processClaimResponse(String claimJson) throws Exception {
		System.out.println("Entered the function");
		ClaimAcceptRequestMessageBean claimAcceptMessage = new ClaimAcceptRequestMessageBean(claimJson);
		System.out.println("Converted to class: " + claimAcceptMessage);
		if (!Helper.validateClaimAcceptResponse(claimAcceptMessage)) {
			System.out.println("Validation failed.");
			List<UpdateClaimLineStatusBean> updateClaimLineStatusBeans = new ArrayList<UpdateClaimLineStatusBean>();
			Gson gson = new GsonBuilder().create();
			UpdateClaimLineStatusBean updateClaimLineStatusBean = new UpdateClaimLineStatusBean();
			updateClaimLineStatusBean.setClaimLineId(String.valueOf(claimAcceptMessage.getClaimLines().get(0).getClaimLineId()));
			updateClaimLineStatusBean.setClaimStatusCode("");
			updateClaimLineStatusBeans.add(updateClaimLineStatusBean);

			// CALL the SP
			DataBaseHelper.updateClaimLine(gson.toJson(updateClaimLineStatusBeans));
			throw new Exception();
		}
		System.out.println("Validated successully.");
		DataBaseHelper.workflowClaimUpdate(claimJson);
	}

	private static List<ClaimLineBean> mapClaimLines(String request, String approverLevel) {
		List<ClaimLineBean> result = new ArrayList<ClaimLineBean>();
		JSONObject jsonObj = new JSONObject(request);
		JSONArray arrayData = jsonObj.getJSONArray("claimlines");
		for (int i = 0; i < arrayData.length(); i++) {
			ClaimLineBean claim = new ClaimLineBean(arrayData.getJSONObject(i).toString());
			ClaimLineBean claimLineBean = new ClaimLineBean();
			claimLineBean.setClaimLineId(claim.getClaimLineId());
			claimLineBean.setClaimLineNum(Integer.valueOf(claim.getClaimLineNum()));
			claimLineBean.setSelected(Boolean.FALSE);
			claimLineBean.setBatchId(claim.getBatchId());
			//setting actionTaken
			List<ExCodeBean> tempExCodeBean = claim.getExCodeBeanList();
			tempExCodeBean.stream().forEach(exCodeBean -> exCodeBean.setActionTaken(getActionTakenBasedOnApproverLevel(exCodeBean, approverLevel)));
			
			claimLineBean.setExCodeBeanList(tempExCodeBean);
			claimLineBean.setStatus(getClaimLineStatusBasedOnApproverLevel(claim, approverLevel));
			result.add(claimLineBean);
		}
		return result;
	}

	private static ClaimLineBean mapClaimLine(ClaimProcessRequest claimProcessRequest) {

		ClaimLineBean claimLineBean = new ClaimLineBean();
		claimLineBean.setBatchId(Long.parseLong(claimProcessRequest.getBatchID()));
		claimLineBean.setClaimLineNum(Integer.valueOf(claimProcessRequest.getClaimLineNum()));
		claimLineBean.setClaimLineId(Long.parseLong(claimProcessRequest.getClaimLineId()));
		claimLineBean.setExCodeBeanList(mapExCodeBeanList(claimProcessRequest));

		return claimLineBean;
	}

	private static List<ExCodeBean> mapExCodeBeanList(ClaimProcessRequest claimProcessRequest) {

		List<ExCodeBean> result = new ArrayList<ExCodeBean>();
		for (ExCodeBean exCodeRequestBean : claimProcessRequest.getExCodeBeanList()) {
			ExCodeBean exCodeBean = new ExCodeBean();
			exCodeBean.setExCodeId(exCodeRequestBean.getExCodeId());
			exCodeBean.setConfidence(exCodeRequestBean.getConfidence());
			exCodeBean.setExCodeType(exCodeRequestBean.getExCodeType());
			exCodeBean.setPrioritizationScore(exCodeRequestBean.getPrioritizationScore());
			exCodeBean.setOrderRating(exCodeRequestBean.getOrderRating());
			exCodeBean.setActionTaken(
					getActionTakenBasedOnApproverLevel(exCodeRequestBean, claimProcessRequest.getApproverLevel()));

			result.add(exCodeBean);
		}

//		AtomicInteger sortOrder = new AtomicInteger();
//		result.stream()
//				.sorted(Comparator.comparing(ExCodeBean::getPrioritizationScore)
//						.thenComparing(ExCodeBean::getConfidence).reversed())
//				.forEach(exCodeBean -> exCodeBean.setSortOrder(sortOrder.incrementAndGet()));
		return result;
	}

	private static void mapClaimBean(ClaimProcessRequest claimProcessRequest, ClaimAcceptRequestMessageBean claimBean) {

		claimBean.setActionTaken(claimProcessRequest.getActionTaken());
		claimBean.setApproverLevel(claimProcessRequest.getApproverLevel());
		claimBean.setClaimLines(new ArrayList<ClaimLineBean>());
		claimBean.setClaimNum(Long.parseLong(claimProcessRequest.getClaimNum()));
		claimBean.setClientId(Integer.valueOf(claimProcessRequest.getClientId()));
		claimBean.setComments(claimProcessRequest.getComments());
		claimBean.setEngagementId(String.valueOf(claimProcessRequest.getEngagementId()));
		claimBean.setExCode(claimProcessRequest.getExCode());
		claimBean.setClaimExCodeLevel(claimProcessRequest.getClaimLevelExCode());
		claimBean.setActionTakenBy(claimProcessRequest.getActionTakenBy());
		claimBean.setRejectReasonId(claimProcessRequest.getRejectReasonId());
	}

	private static String createGetClaimLinesForClaimRequest(ClaimProcessRequest request) {
		ClaimFetchInfoRequestBean claimFetchInfoRequestBean = new ClaimFetchInfoRequestBean();
		claimFetchInfoRequestBean.setClaimNum(String.valueOf(request.getClaimNum()));
		claimFetchInfoRequestBean.setEngagementId(String.valueOf(request.getEngagementId()));
		Gson gson = new GsonBuilder().create();
		return gson.toJson(claimFetchInfoRequestBean);
	}

	private static void updateClaimLineStatus(List<ClaimLineBean> claimLineBeans, String status) {

		List<UpdateClaimLineStatusBean> updateClaimLineStatusBeans = new ArrayList<UpdateClaimLineStatusBean>();
		for (ClaimLineBean claimLineBean : claimLineBeans) {
			UpdateClaimLineStatusBean updateClaimLineStatusBean = new UpdateClaimLineStatusBean();
			updateClaimLineStatusBean.setClaimLineId(String.valueOf(claimLineBean.getClaimLineId()));
			updateClaimLineStatusBean.setClaimStatusCode(status);
			updateClaimLineStatusBeans.add(updateClaimLineStatusBean);
		}

		Gson gson = new GsonBuilder().create();
		// CALL the SP
		DataBaseHelper.updateClaimLine(gson.toJson(updateClaimLineStatusBeans));
	}

	private static String getActionTakenBasedOnApproverLevel(ExCodeBean exCodeBean, String approverLevel) {
		String result = "";
		switch (approverLevel) {
		case MetaDataConstants.APPROVER_AARETE_USER:
			result = exCodeBean.getApproverOneAction();
			break;
		case MetaDataConstants.APPROVER_AARETE_MANAGER:
			result = exCodeBean.getApproverTwoAction();
			break;
		case MetaDataConstants.APPROVER_CLIENT_USER:
			result = exCodeBean.getApproverThreeAction();
			break;
		case MetaDataConstants.APPROVER_CLIENT_MANAGER:
			result = exCodeBean.getApproverFourAction();
			break;
		}
		return result;

	}
	
	private static String getClaimLineStatusBasedOnApproverLevel(ClaimLineBean claimLineBean, String approverLevel) {
		String result = "";
		switch (approverLevel) {
		case MetaDataConstants.APPROVER_AARETE_USER:
			result = claimLineBean.getClaimStatusLevelOne();
			break;
		case MetaDataConstants.APPROVER_AARETE_MANAGER:
			result = claimLineBean.getClaimStatusLevelTwo();
			break;
		case MetaDataConstants.APPROVER_CLIENT_USER:
			result = claimLineBean.getClaimStatusLevelThree();
			break;
		case MetaDataConstants.APPROVER_CLIENT_MANAGER:
			result = claimLineBean.getClaimStatusLevelFour();
			break;
		}
		return result;

	}

	private static Map<ClaimLineKeyBean, ClaimLineBean> getMapOfAllClaimLineForClaim(JSONArray arrayData) throws Exception {
		System.out.println("getting selected claimlines for a claim started:");
		Map<ClaimLineKeyBean, ClaimLineBean> result = new HashMap<ClaimLineKeyBean, ClaimLineBean>();
		ClaimProcessRequest claimProcessRequest = new ClaimProcessRequest(arrayData.getJSONObject(0).toString());
		// Set<ClaimKeyBean> claimsKey = createClaimsKeySet(arrayData);
		String spInput = createGetClaimLinesForClaimRequest(claimProcessRequest);
		// WIll fetch all the claimline of claim from DB
		String allClaimLinesForClaim = DataBaseHelper.getAllClaimLinesForClaims(spInput);
		if(StringUtils.isNullOrEmpty(allClaimLinesForClaim)) {
			throw new Exception("ClaimLines not found for: "+spInput);
		}
		List<ClaimLineBean> claimLines = mapClaimLines(
				allClaimLinesForClaim,claimProcessRequest.getApproverLevel());

		for (ClaimLineBean claimLineBean : claimLines) {
			ClaimLineKeyBean claimLineKeyBean = new ClaimLineKeyBean();
			// claimLineKeyBean.setBatchID(claimLineBean.getBatchID());
			claimLineKeyBean.setClaimLineId(String.valueOf(claimLineBean.getClaimLineId()));
			claimLineKeyBean.setClaimLineNum(String.valueOf(claimLineBean.getClaimLineNum()));
			result.put(claimLineKeyBean, claimLineBean);

		}
		System.out.println("all selected claimlines for a claim finished:" + result);
		return result;
	}

	private static Map<ClaimLineKeyBean, ClaimLineBean> getMapOfSelectedClaimLinesForClaim(JSONArray arrayData) {
		System.out.println("getting all claimlines for a claim started:");
		Map<ClaimLineKeyBean, ClaimLineBean> result = new HashMap<ClaimLineKeyBean, ClaimLineBean>();
		for (int i = 0; i < arrayData.length(); i++) {
			ClaimProcessRequest claimProcessRequest = new ClaimProcessRequest(arrayData.getJSONObject(i).toString());
			ClaimLineKeyBean claimLineKeyBean = new ClaimLineKeyBean();
			// claimLineKeyBean.setBatchID(claimProcessRequest.getBatchId());
			claimLineKeyBean.setClaimLineId(claimProcessRequest.getClaimLineId());
			claimLineKeyBean.setClaimLineNum(claimProcessRequest.getClaimLineNum());
			ClaimLineBean claimLine = mapClaimLine(claimProcessRequest);

			result.put(claimLineKeyBean, claimLine);

		}
		System.out.println("all claimlines for a claim finished: "+ result);
		return result;
	}

}
