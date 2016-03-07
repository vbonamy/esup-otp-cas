package org.esupportail.cas.authentication;

import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

import javax.security.auth.login.FailedLoginException;
import org.jasig.cas.authentication.PreventedException;
import java.security.GeneralSecurityException;
import java.io.IOException;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class EsupOtpApiAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

	@Override
	protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)throws GeneralSecurityException, PreventedException{
		try{
			System.out.println(verifyOtp(credential.getUsername(), credential.getPassword()));
			throw new FailedLoginException();
		}catch(IOException e){
			// System.out.println(e.toString());
			throw new PreventedException("HTTP Request error", e);
		}
	}

	private String testGetRequest() throws IOException {
			String url = "http://localhost:3000/send_code/google_authenticator/app/john";

			URL obj = new URL(url);
			int responseCode;
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			responseCode = con.getResponseCode();
			
			// System.out.println("\nSending 'GET' request to URL : " + url);
			// System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
	}

	private String verifyOtp(String uid, String otp) throws IOException {
			String url = "http://localhost:3000/verify_code/google_authenticator/"+uid+"/"+otp;

			URL obj = new URL(url);
			int responseCode;
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			responseCode = con.getResponseCode();
			
			// System.out.println("\nSending 'GET' request to URL : " + url);
			// System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
	}
}