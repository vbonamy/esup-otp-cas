package org.apereo.cas.adaptors.esupotp.web.flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import org.apereo.cas.adaptors.esupotp.EsupOtpTransportCredential;
import org.apereo.cas.web.support.WebUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is {@link EsupOtpSencCodeAction}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@RefreshScope
@Component("esupotpTransportService")
public class EsupOtpTransportService {
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${cas.mfa.esupotp.urlApi:CAS}")
	private String urlApi;

	public String sendCode(EsupOtpTransportCredential transportCredential) {
        try {
                JSONObject response = sendCodeRequest(transportCredential.getUid(), transportCredential.getUserHash(), transportCredential.getTransport(), transportCredential.getMethod());
                if(response.getString("code").equals("Ok"))return "success";
        } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        return "error";
	}

	private JSONObject sendCodeRequest(String uid, String userHash, String transport, String method) throws IOException, NoSuchAlgorithmException {
		String url = urlApi + "/users/" + uid + "/methods/"+ method +"/transports/"+ transport + "/" + userHash;
		URL obj = new URL(url);
		int responseCode;
		HttpURLConnection con = null;
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
        logger.info("mfa-esupotp request send to [{}]", (String) url);
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
}
