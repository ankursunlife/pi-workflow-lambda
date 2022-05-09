package com.aarete.pi.claimprocess.helper;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.aarete.pi.claimprocess.beans.ClaimAcceptRequestMessageBean;
import com.aarete.pi.claimprocess.beans.ClaimLineBean;
import com.aarete.pi.claimprocess.beans.ClaimLineKeyBean;
import com.aarete.pi.claimprocess.beans.ClaimLineListBean;
import com.aarete.pi.claimprocess.beans.ClaimProcessRequest;
import com.aarete.pi.claimprocess.beans.ExCodeBean;
import com.aarete.pi.claimprocess.constant.ActionTaken;
import com.aarete.pi.claimprocess.constant.ApproverLevel;
import com.aarete.pi.claimprocess.constant.ClaimExCodeLevel;
import com.aarete.pi.claimprocess.constant.ClaimStatusCode;
import com.aarete.pi.claimprocess.constant.ExCodeTypes;
import com.aarete.pi.claimprocess.constant.Level;
import com.aarete.pi.claimprocess.constant.Status;
import com.aarete.pi.claimprocess.constant.WorkFlowStatus;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.utils.StringUtils;

public class Helper {

	static final String DB_SM_NAME = System.getenv("DB_SM_NAME");
	static final String AWS_SM_REGION = System.getenv("AWS_SM_REGION");

	private Helper() {
		super();
	}

	public static String convertToJson(ClaimAcceptRequestMessageBean claimBean) {
		Gson gson = new GsonBuilder().create();
		JSONObject data = new JSONObject(new JSONTokener(gson.toJson(claimBean)));
		return data.toString();
	}

	public static String base64Decode(String message) {
		if (StringUtils.isEmpty(message)) {
			return "";
		}

		byte[] bytes = Base64.getDecoder().decode(message);
		return new String(bytes);
	}

	@SuppressWarnings("unchecked")
	public static Map<Object, Object> getDBSecrets() {
		System.out.println("Connecting to AWS Secrets Manager ...");
		Map<Object, Object> secretsMap = new HashMap<Object, Object>();
		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(AWS_SM_REGION).build();
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(DB_SM_NAME);
		GetSecretValueResult getSecretValueResult = null;

		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
			if (getSecretValueResult.getSecretString() != null) {
				String secret = getSecretValueResult.getSecretString();
				ObjectMapper mapper = new ObjectMapper();
				secretsMap = mapper.readValue(secret, Map.class);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return secretsMap;
	}

	public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
		for (T each : enumeration.getEnumConstants()) {
			if (each.toString().equalsIgnoreCase(search)) {
				return each;
			}
		}
		return null;
	}

	public static boolean validateClaimLineRequest(ClaimProcessRequest request) {

		validateForNullField(request.getClaimLineId());
		validateForNullField(request.getClaimLineNum());

		ClaimLineKeyBean claimLineKeyBean = new ClaimLineKeyBean();
		claimLineKeyBean.setClaimLineId(request.getClaimLineId());
		claimLineKeyBean.setClaimLineNum(request.getClaimLineNum());

		validateForNullField(request.getClaimNum(), claimLineKeyBean);
		validateForNullField(request.getEngagementId(), claimLineKeyBean);

		if (searchEnum(ActionTaken.class, request.getActionTaken()) == null) {
			throw new BadRequestExceptionHandler(
					"Action taken value is not valid for ClaimLineKeyBean : " + claimLineKeyBean);
		}
		if (searchEnum(ApproverLevel.class, request.getApproverLevel()) == null) {
			throw new BadRequestExceptionHandler(
					"ApproverLevel value is not valid for ClaimLineKeyBean : " + claimLineKeyBean);
		}
		if (searchEnum(ClaimExCodeLevel.class, request.getClaimLevelExCode()) == null) {
			throw new BadRequestExceptionHandler(
					"ClaimLevelExCode value is not valid for ClaimLineKeyBean : " + claimLineKeyBean);
		}
		for (ExCodeBean exCodeBean : request.getExCodeBeanList()) {
			if (searchEnum(ExCodeTypes.class, exCodeBean.getExCodeType()) == null) {
				throw new BadRequestExceptionHandler(
						"ExCodeTypes value is not valid for ClaimLineKeyBean : " + claimLineKeyBean);
			}
		}

		//In case of any failure method break and create exception
		return true;
	}

	public static boolean validateClaimAcceptResponse(ClaimAcceptRequestMessageBean claimAcceptMessage) {
		if (StringUtils.isEmpty(claimAcceptMessage.getWorkflowStatus()) || Helper.searchEnum(WorkFlowStatus.class,
				claimAcceptMessage.getWorkflowStatus()) == WorkFlowStatus.FAILED) {
			System.out.println("Not a valid value for WorkFlowStatus: "+ claimAcceptMessage.getWorkflowStatus());
			return false;
		}
		if (searchEnum(ActionTaken.class, claimAcceptMessage.getActionTaken()) == null) {
			System.out.println("Not a valid value for ActionTaken: "+ claimAcceptMessage.getActionTaken());
			return false;
		}
		if (searchEnum(ApproverLevel.class, claimAcceptMessage.getApproverLevel()) == null) {
			System.out.println("Not a valid value for ApproverLevel: "+ claimAcceptMessage.getApproverLevel());
			return false;
		}
		if (searchEnum(ClaimExCodeLevel.class, claimAcceptMessage.getClaimExCodeLevel()) == null) {
			System.out.println("Not a valid value for ClaimExCodeLevel: "+ claimAcceptMessage.getClaimExCodeLevel());
			return false;
		}

		if (!validateClaimLineBean(claimAcceptMessage.getClaimLines())) {
			return false;
		}

		return true;
	}

	private static boolean validateClaimLineBean(List<ClaimLineBean> claimLines) {
		for (ClaimLineBean claimLine : claimLines) {
			if (searchEnum(ClaimStatusCode.class, claimLine.getClaimStatusCode()) == null) {
				System.out.println("Not a valid value for claimline.status: "+ claimLine.getClaimStatusCode());
				return false;
			}

			for (ExCodeBean exCodeBean : claimLine.getExCodeBeanList()) {
				if (searchEnum(ExCodeTypes.class, exCodeBean.getExCodeType()) == null) {
					System.out.println("Not a valid value for ExCodeBean.excodetype: "+ exCodeBean.getExCodeType());
					return false;
				}
			}

			for (ClaimLineListBean claimLineList : claimLine.getClaimLineList()) {
				if (searchEnum(Level.class, claimLineList.getLevel()) == null) {
					System.out.println("Not a valid value for claimLineList.level: "+ claimLineList.getLevel());
					return false;
				}
				if (searchEnum(Status.class, claimLineList.getStatus()) == null) {
					System.out.println("Not a valid value for claimLineList.status: "+ claimLineList.getStatus());
					return false;
				}
			}
		}
		return true;
	}

	private static void validateForNullField(String value) {
		if (StringUtils.isEmpty(value)) {
			throw new BadRequestExceptionHandler("Value can not be null.");
		}
	}

	private static void validateForNullField(String value, ClaimLineKeyBean claimLineKeyBean) {
		if (StringUtils.isEmpty(value)) {
			throw new BadRequestExceptionHandler("Value can not be null for ClaimLineKeyBean : " + claimLineKeyBean);
		}
	}
}
