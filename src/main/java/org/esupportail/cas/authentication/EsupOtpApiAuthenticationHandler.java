package org.esupportail.cas.authentication;

import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

import org.json.*;

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

	public static String httpUrlApi;

	public static String httpsUrlApi;

	@Override
	protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)throws GeneralSecurityException, PreventedException{
		try{
			JSONObject response = verifyOtp(credential.getUsername(), credential.getPassword());
			if(response.getString("code").equals("Ok")){
				return createHandlerResult(credential, createPrincipal(credential.getUsername()), null);
			}else{
				throw new FailedLoginException();
			}
		}catch(IOException e){
			throw new PreventedException("HTTP Request error", e);
		}
	}

	private JSONObject verifyOtp(String uid, String otp) throws IOException {
			String url = httpUrlApi+"/verify_code/"+uid+"/"+otp;

			URL obj = new URL(url);
			int responseCode;
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return new JSONObject(response.toString());
	}

	/**
     * Creates a CAS principal with attributes
     *
     * @param username Username that was successfully authenticated which is used for principal
     *
     * @return Principal
     *
     * @throws LoginException On security policy errors related to principal creation.
     */
    protected Principal createPrincipal(final String username){
        return new SimplePrincipal(username);
    }

    public String getHttpUrlApi() {
    	return httpUrlApi; 
    }

    public void setHttpUrlApi(String httpUrlApi) {
    	this.httpUrlApi = httpUrlApi; 
 	}

 	public String getHttpsUrlApi() {
    	return httpsUrlApi; 
    }

    public void setHttpsUrlApi(String httpsUrlApi) {
     	this.httpsUrlApi = httpsUrlApi; 
 	}	
}