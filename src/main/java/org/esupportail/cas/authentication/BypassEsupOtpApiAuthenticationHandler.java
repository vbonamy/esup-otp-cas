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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import java.security.MessageDigest;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BypassEsupOtpApiAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static String urlApi;

	private static String apiPassword;

	@Override
	protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)throws GeneralSecurityException, PreventedException{
		try{
			Boolean bypass = true;
			JSONObject response = checkActivateMethods(credential.getUsername(), credential.getPassword());
			for (Object key : response.keySet()) {
				String keyStr = (String)key;
				if(keyStr.equals("methods")){
					Object buff_methods = response.get(keyStr);
					if (buff_methods instanceof JSONObject){
						JSONObject methods = (JSONObject)buff_methods;
						for (Object method : methods.keySet()) {
							String strMethod = (String)method;
							Object methodObj = methods.get(strMethod);
							if(methodObj instanceof JSONObject){
								bypass = false;
							}
						}
					}
				}
			}
			if(bypass){
				return createHandlerResult(credential, createPrincipal(credential.getUsername()), null);
			}else{
				logger.info("Error : "+response.getString("message"));
				throw new FailedLoginException();
			}
		}catch(IOException e){
			logger.info("HTTP Request error", e);
			throw new PreventedException("HTTP Request error", e);
		}
	}

	private JSONObject checkActivateMethods(String uid, String otp) throws IOException {
			String url = urlApi+"/protected/admin/user/"+uid+"/method/"+apiPassword;

			URL obj = new URL(url);
			int responseCode;
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
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

    public void setUrlApi(String urlApi) {
    	this.urlApi = urlApi; 
 	}

 	public void setApiPassword(String apiPassword) {
    	this.apiPassword = apiPassword;
    }	
}