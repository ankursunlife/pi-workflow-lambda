package com.aarete.pi.claimprocess.lambdafunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import com.aarete.pi.claimprocess.helper.ClaimProcessHelper;
import com.aarete.pi.claimprocess.helper.DataBaseHelper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class ClaimRejectProcess implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	static final String DB_ENDPOINT = System.getenv("DB_END_POINT");
	static final String DB_USER_NAME = System.getenv("DB_USER_NAME");
	static final String DB_PWD = System.getenv("DB_PWD");
	static final String DB_PORT = System.getenv("DB_PORT");
	static final String DB_NAME = System.getenv("DB_NAME");

	public ClaimRejectProcess() {
		try {

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
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	public APIGatewayProxyResponseEvent claimRejectProcessRequest(APIGatewayProxyRequestEvent input, Context context) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("X-Custom-Header", "application/json");

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

		try {
			final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
			String output = String.format("{ \"message\": \"claim processed\", \"location\": \"%s\" }", pageContents);
			// log.info("Input request: " + input.getBody());
			ClaimProcessHelper.processClaim(input.getBody());

			return response.withStatusCode(HttpStatus.SC_OK).withBody(output);

		} catch (Exception e) {
			// log.error(e.getMessage());
			return response.withBody(e.getMessage()).withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private String getPageContents(String address) throws IOException {
		URL url = new URL(address);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

}
