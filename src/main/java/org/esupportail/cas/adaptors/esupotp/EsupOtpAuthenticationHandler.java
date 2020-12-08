package org.esupportail.cas.adaptors.esupotp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.security.auth.login.FailedLoginException;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.esupportail.cas.config.EsupOtpConfigurationProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * An authentication handler that uses the token provided to authenticator
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
	
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    EsupOtpConfigurationProperties esupOtpConfigurationProperties;
    
	/**
	 * Instantiates a new Esup otp authentication handler.
	 */
    public EsupOtpAuthenticationHandler(final String name, final ServicesManager servicesManager, final PrincipalFactory principalFactory, final EsupOtpConfigurationProperties esupOtpConfigurationProperties) {
    	super(name, servicesManager, principalFactory, esupOtpConfigurationProperties.getRank());
    	this.esupOtpConfigurationProperties = esupOtpConfigurationProperties;
    }

	/**
	 * Init.
	 */
	@PostConstruct
	public void init() {
	}

	@Override
	protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential)
			throws GeneralSecurityException, PreventedException {
		final EsupOtpCredential esupotpCredential = (EsupOtpCredential) credential;
		final String otp = esupotpCredential.getToken();
		final RequestContext context = RequestContextHolder.getRequestContext();
		final String uid = WebUtils.getAuthentication(context).getPrincipal().getId();
		
		//Uncomment for bypass users with no activated methods
		if(esupotpCredential.getBypass()) {
			return createHandlerResult(esupotpCredential, this.principalFactory.createPrincipal(uid), new ArrayList<>(0));
		}
		try {
			JSONObject response = verifyOtp(uid, otp);
			if(response.getString("code").equals("Ok")){
				return createHandlerResult(esupotpCredential, this.principalFactory.createPrincipal(uid), new ArrayList<>(0));
			}
		} catch (IOException e) {
			 logger.error("doAuthentication failed", e);
		}
		throw new FailedLoginException("Failed to authenticate code " + otp);
	}

	@Override
	public boolean supports(final Credential credential) {
		return EsupOtpCredential.class.isAssignableFrom(credential.getClass());
	}

	private JSONObject verifyOtp(String uid, String otp) throws IOException {
		String url = esupOtpConfigurationProperties.getUrlApi() + "/protected/users/" + uid + "/" + otp + "/" + esupOtpConfigurationProperties.getApiPassword();
		URL obj = new URL(url);
		int responseCode;
		HttpURLConnection con = null;
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
        logger.info("Mfa-esupotp request send to [{}]", (String) url);
		responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
        logger.info("Connection success to [{}]", (String) url);

		return new JSONObject(response.toString());
	}
}
