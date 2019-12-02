package org.esupportail.cas.authentication;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.esupportail.cas.configuration.EsupOtpProperties;
import org.json.JSONObject;

import javax.security.auth.login.FailedLoginException;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

@Slf4j
public class EsupOtpApiAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

	public  String urlApi;
	private  String apiPassword;
	public  String usersSecret;

	public EsupOtpApiAuthenticationHandler(final ServicesManager servicesManager,
										   final PrincipalFactory principalFactory,
										   final EsupOtpProperties esupOtpProperties) {
		super(EsupOtpProperties.DEFAULT_IDENTIFIER, servicesManager, principalFactory, 0);
		urlApi = esupOtpProperties.getUrlApi();
		apiPassword = esupOtpProperties.getApiPassword();
		usersSecret = esupOtpProperties.getUsersSecret();
	}

	@Override
	protected final AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential, String originalPassword)
			throws GeneralSecurityException, PreventedException {
		try {
			if (credential.getPassword() == "")
				throw new FailedLoginException();
			JSONObject response = verifyOtp(credential.getUsername(), credential.getPassword());
			if (response.getString("code").equals("Ok")) {
				return createHandlerResult(credential, this.principalFactory.createPrincipal(credential.getUsername()),
						null);
			} else {
				log.info("Error : " + response.getString("message"));
				throw new FailedLoginException();
			}
		} catch (IOException e) {
			log.info("HTTP Request error", e);
			throw new PreventedException("HTTP Request error", e);
		}
	}

	private JSONObject verifyOtp(String uid, String otp) throws IOException {
		String url = urlApi + "/protected/users/" + uid + "/" + otp + "/" + apiPassword;
		log.debug("EsupOtpApiAuthenticationHandler.verifyOtp(" + uid + "," + otp + ") : " + url);
		URL obj = new URL(url);
		int responseCode;
		HttpURLConnection con = null;
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new JSONObject(response.toString());
	}

	public String getUserHash(String uid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5Md = MessageDigest.getInstance("MD5");
		String md5 = (new HexBinaryAdapter()).marshal(md5Md.digest(usersSecret.getBytes()));
		md5 = md5.toLowerCase();
		String salt = md5 + getSalt(uid);
		MessageDigest sha256Md = MessageDigest.getInstance("SHA-256");
		String userHash = (new HexBinaryAdapter()).marshal(sha256Md.digest(salt.getBytes()));
		userHash = userHash.toLowerCase();
		return userHash;
	}

	public String getSalt(String uid) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		String salt = uid + day + hour;
		return salt;
	}
    
	public String getUrlApi() {
		return urlApi;
	}

	public void setUrlApi(String urlApi) {
		this.urlApi = urlApi;
	}

	public String getUsersSecret() {
		return usersSecret;
	}

	public void setUsersSecret(String usersSecret) {
		this.usersSecret = usersSecret;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}
}
