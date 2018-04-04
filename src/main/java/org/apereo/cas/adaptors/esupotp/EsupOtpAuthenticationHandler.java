package org.apereo.cas.adaptors.esupotp;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import javax.annotation.PostConstruct;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An authentication handler that uses the token provided to authenticator
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
	
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${cas.mfa.esupotp.urlApi:CAS}")
	private String urlApi;
	
	@Value("${cas.mfa.esupotp.apiPassword:CAS}")
	private String apiPassword;

	/**
	 * Instantiates a new Esup otp authentication handler.
	 */
    public EsupOtpAuthenticationHandler(final String name, final ServicesManager servicesManager, final PrincipalFactory principalFactory) {
        super(name, servicesManager, principalFactory, null);
    }

	/**
	 * Init.
	 */
	@PostConstruct
	public void init() {
	}

	@Override
	protected HandlerResult doAuthentication(final Credential credential)
			throws GeneralSecurityException, PreventedException {
		final EsupOtpCredential esupotpCredential = (EsupOtpCredential) credential;
		final String otp = esupotpCredential.getToken();
		final RequestContext context = RequestContextHolder.getRequestContext();
		final String uid = WebUtils.getAuthentication(context).getPrincipal().getId();
		
		//Uncomment for bypass users with no activated methods
		if(esupotpCredential.getBypass())return createHandlerResult(esupotpCredential, this.principalFactory.createPrincipal(uid), null);
		try {
			JSONObject response = verifyOtp(uid, otp);
			if(response.getString("code").equals("Ok")){
				return createHandlerResult(esupotpCredential, this.principalFactory.createPrincipal(uid), null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new FailedLoginException("Failed to authenticate code " + otp);
	}

	@Override
	public boolean supports(final Credential credential) {
		return EsupOtpCredential.class.isAssignableFrom(credential.getClass());
	}

	private JSONObject verifyOtp(String uid, String otp) throws IOException {
		String url = urlApi + "/protected/users/" + uid + "/" + otp + "/" + apiPassword;
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
