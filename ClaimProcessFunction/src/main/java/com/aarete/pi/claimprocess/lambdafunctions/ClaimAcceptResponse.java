package com.aarete.pi.claimprocess.lambdafunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class ClaimAcceptResponse implements RequestHandler<SQSEvent, SQSBatchResponse> {

	public ClaimAcceptResponse() {
		try {
			System.out.println("Creating DB Connection");
			Map<Object, Object> dbProps = Helper.getDBSecrets();
			DataBaseHelper.createConnectionViaUserPwd(dbProps.get("username").toString()
													, dbProps.get("password").toString()
													, dbProps.get("host") + ":" 
													+ dbProps.get("port") + "/" 
													+ dbProps.get("engine"));
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
				 System.out.println(message.getBody());
				ClaimProcessHelper.processClaimResponse(message.getBody());
             } catch (Exception e) {
                 //Add failed message identifier to the batchItemFailures list
                 batchItemFailures.add(new SQSBatchResponse.BatchItemFailure(messageId));
             }
         }
         return new SQSBatchResponse(batchItemFailures);
     }
}
