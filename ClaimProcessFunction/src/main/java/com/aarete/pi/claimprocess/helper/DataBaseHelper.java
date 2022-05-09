package com.aarete.pi.claimprocess.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

import lombok.NonNull;

public class DataBaseHelper {

	private static final String JDBC_PREFIX = "jdbc:postgresql:aws://";
	public static Connection connection;

	private DataBaseHelper() {

	}

	public static void updateClaimLine(String claimLines) {
		System.out.println("input string to SP: " + claimLines);
		try {
			CallableStatement callableStatement = connection.prepareCall("CALL public.update_claim_status_code(?)");

			callableStatement.setString(1, claimLines);
			callableStatement.execute();
		} catch (SQLException e) {
			System.err.println("Unable to update. ");
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			StackTraceElement[] s = e.getStackTrace();
			for (StackTraceElement element : s) {
				System.err.println("\tat " + element);
			}

		}
	}

	public static void workflowClaimUpdate(String claimJson) throws Exception{
		System.out.println("input string to SP: " + claimJson);
		try {

			CallableStatement callableStatement = connection.prepareCall("CALL public.workflow_claim_update(?::JSON, ?, ?)");

			callableStatement.setString(1, claimJson);
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.setInt(2, 0);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.setString(3, null);
			System.out.println("Executing SQL Stored Procedure: public.workflow_claim_update");
			callableStatement.execute();
			System.out.println("Response from SQL Stored Procedure [Code: " + callableStatement.getInt(2) +
													", Message: " + callableStatement.getString(3) + "]");
		} catch (SQLException e) {
			System.err.println("Connection FAILED ");
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			StackTraceElement[] s = e.getStackTrace();
			for (StackTraceElement element : s) {
				System.err.println("\tat " + element);
			}
			throw e;
		}
	}

	public static void createConnectionViaUserPwd(@NonNull String username, @NonNull String pwd,
			@NonNull String dbEndpoint) {

		try {

			Class.forName("software.aws.rds.jdbc.postgresql.Driver");
			System.out.println("Trying to establist Connection: " + username + " " + pwd + " " + dbEndpoint);
			connection = (DriverManager.getConnection(JDBC_PREFIX + dbEndpoint, username, pwd));
			System.out.println("Connection Established");

		} catch (SQLException e) {
			System.err.println("Connection FAILED ");
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
			System.err.println("Connection FAILED because of class not found.");
			ex.printStackTrace();
		}
	}

	public static String getAllClaimLinesForClaims(String request) {
		String result;
		try {
			System.out.println("public.get_all_claim_lines_for_claim request : "+ request);
			CallableStatement callableStatement = connection
					.prepareCall("CALL public.get_all_claim_lines_for_claim(?,?)");

			callableStatement.setString(1, request);
			callableStatement.registerOutParameter(2, Types.VARCHAR);
			callableStatement.setString(2, "'{}'");
			callableStatement.execute();
			result = callableStatement.getString(2);
			System.out.println("public.get_all_claim_lines_for_claim result : "+ result);
			return result;
		} catch (SQLException e) {
			System.err.println("Connection FAILED ");
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			StackTraceElement[] s = e.getStackTrace();
			for (StackTraceElement element : s) {
				System.err.println("\tat " + element);
			}

		}
		return null;
	}

	public static String getAllClaimLinesForClaimLine(String request) {
		String result;
		try {

			CallableStatement callableStatement = connection
					.prepareCall("CALL public.get_all_claim_lines_for_claimline(?,?)");

			callableStatement.setString(1, request);
			callableStatement.registerOutParameter(2, Types.VARCHAR);
			callableStatement.setString(2, "'{}'");
			callableStatement.execute();
			result = callableStatement.getString(2);
			return result;
		} catch (SQLException e) {
			System.err.println("Connection FAILED ");
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			StackTraceElement[] s = e.getStackTrace();
			for (StackTraceElement element : s) {
				System.err.println("\tat " + element);
			}

		}
		return null;
	}
}
