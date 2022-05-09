package com.aarete.pi.claimprocess.helper;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.InvalidMessageContentsException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class SqsHelper {

	private SqsHelper() {

	}

	public static void postMessageToQueue(String message, String queueUrl) {
		System.out.println(message);
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		// sqs.getQueueUrl(MetaDataConstants.QUEUE_NAME).getQueueUrl();
		//queueUrl = "https://sqs.us-east-1.amazonaws.com/130303733587/claim-workflow-accept-request";
		queueUrl = "https://sqs.us-east-1.amazonaws.com/814719539168/WA-Dec-Accept-claim_recovery";
		SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(message)
				.withDelaySeconds(5);
		try {
			sqs.sendMessage(send_msg_request);
		} catch (InvalidMessageContentsException | UnsupportedOperationException ex) {
			throw ex;
		}
	}
	
	public static List<Message> readMessageFromQueue() {
		List<Message> result  = new ArrayList<>();
		System.out.println("Entering to read message");
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		String queueUrl = "";
		try{
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(10);
			result  = sqs.receiveMessage(receiveMessageRequest).getMessages();
		} catch (Exception ex) {
			System.out.println("Exception in reading message: " + ex.getMessage());
		}
		return result;
	}
}
