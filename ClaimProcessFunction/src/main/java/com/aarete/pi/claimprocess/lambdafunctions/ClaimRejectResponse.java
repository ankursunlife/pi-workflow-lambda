package com.aarete.pi.claimprocess.lambdafunctions;

import java.util.ArrayList;
import java.util.List;

import com.aarete.pi.claimprocess.helper.ClaimProcessHelper;
import com.aarete.pi.claimprocess.helper.DataBaseHelper;
import com.aarete.pi.claimprocess.helper.Helper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
 

/**
 * Handler for requests to Lambda function.
 */
public class ClaimRejectResponse implements RequestHandler<SQSEvent, SQSBatchResponse> {
	static final String DB_ENDPOINT = System.getenv("DB_END_POINT");
	static final String DB_USER_NAME = System.getenv("DB_USER_NAME");
	static final String DB_PWD = System.getenv("DB_PWD");
	static final String DB_PORT = System.getenv("DB_PORT");
	static final String DB_NAME = System.getenv("DB_NAME");

	public ClaimRejectResponse() {
		try {
			System.out.println("Creating DB Connection");
			DataBaseHelper.createConnectionViaUserPwd(DB_USER_NAME, DB_PWD, DB_ENDPOINT);

		} catch (Exception e) {
			System.err.println("INIT connection FAILED");
			System.err.println(e.getMessage());
			StackTraceElement[] s = e.getStackTrace();
			for (StackTraceElement element : s) {
				System.err.println("\tat " + element);
			}
		}
	}

	@Override
    public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
 
         List<SQSBatchResponse.BatchItemFailure> batchItemFailures = new ArrayList<SQSBatchResponse.BatchItemFailure>();
         String messageId = "";
         for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
             try {
                 //process your message
                 messageId = message.getMessageId();
				 String jsonBody = Helper.base64Decode(message.getBody());
				 System.out.println("Body: " + jsonBody);
				 ClaimProcessHelper.processClaimResponse(jsonBody);
             } catch (Exception e) {
                 //Add failed message identifier to the batchItemFailures list
                 batchItemFailures.add(new SQSBatchResponse.BatchItemFailure(messageId));
             }
         }
         return new SQSBatchResponse(batchItemFailures);
     }
}
