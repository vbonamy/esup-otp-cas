package org.esupportail.cas.adaptors.esupotp.web.flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;
import org.esupportail.cas.adaptors.esupotp.EsupOtpMethod;
import org.esupportail.cas.adaptors.esupotp.EsupOtpUser;
import org.esupportail.cas.config.EsupOtpConfigurationProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import lombok.AllArgsConstructor;

/**
 * This is {@link EsupOtpGetTransportsAction}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@AllArgsConstructor
public class EsupOtpGetTransportsAction extends AbstractAction {
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private EsupOtpConfigurationProperties esupOtpConfigurationProperties;

    @Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        final RequestContext context = RequestContextHolder.getRequestContext();
        final String uid = WebUtils.getAuthentication(context).getPrincipal().getId();
        String userHash = getUserHash(uid);
        String waitingForMethodName = "push";

        requestContext.getFlowScope().put("uid", uid);
        requestContext.getFlowScope().put("userHash", userHash);
        requestContext.getFlowScope().put("divNoCodeDisplay", false);

        JSONObject userInfos = getUserInfos(uid);
        List<EsupOtpMethod> listMethods = new ArrayList<EsupOtpMethod>();
        EsupOtpUser user = new EsupOtpUser(uid, userHash);

        try {
            JSONObject methods = (JSONObject) ((JSONObject) userInfos.get("user")).get("methods");
            JSONObject transports = (JSONObject) ((JSONObject) userInfos.get("user")).get("transports");

            Boolean defaultWaitingFor = false;
            for (String method : methods.keySet()) {
                if ("waitingFor".equals(method)) {
                    defaultWaitingFor = (Boolean) methods.get(method);
                } else if ("codeRequired".equals(method)) {
                	Boolean codeRequired = (Boolean) methods.get(method);
                	logger.debug("codeRequired : {}", codeRequired);
                } else {
                    listMethods.add(new EsupOtpMethod(method, (JSONObject) methods.get(method)));
                }
            }
            
            //Uncomment for bypass users with no activated methods
            if (bypass(listMethods)) {
                logger.info("mfa-esupotp bypass");
                return new EventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_BYPASS);
            }
            
            // will give order to the page to display only WaitingFor block if needed
            requestContext.getFlowScope().put("divNoCodeDisplay", defaultWaitingFor);
            requestContext.getFlowScope().put("waitingForMethodName", waitingForMethodName);
            
            user = new EsupOtpUser(uid, userHash, listMethods, transports);
        } catch (JSONException e) {
        	logger.error("JSONException ...", e);
        }
        List<Map<String, String>> listTransports = this.getTransports(listMethods);

        requestContext.getFlowScope().put("apiUrl", esupOtpConfigurationProperties.getUrlApi());
        requestContext.getFlowScope().put("listTransports", listTransports);
        requestContext.getFlowScope().put("user", user);

        return new EventFactorySupport().event(this, "authWithCode");
    }

    private List<Map<String, String>> getTransports(List<EsupOtpMethod> methods) {
    	List<Map<String, String>> transports = new ArrayList<>();
        //Map<Integer, Map> transports = new HashMap<>();
        Map <String, String> transport;

        for (EsupOtpMethod method : methods) {
            if (method.getActive() && method.getTransports().size() > 0) {
                for(String transportName : method.getTransports() ){                
                    transport = new HashMap<>();  
                    transport.put("method", method.getName());
                    transport.put("transport", transportName);
                    transports.add(transport);
                }
            }
        }
        logger.info("Size [{}] [{}]",  + transports.size(), transports.toString());
        
        return transports;
    }

    private JSONObject getUserInfos(String uid) throws IOException, NoSuchAlgorithmException {
        String url = esupOtpConfigurationProperties.getUrlApi() + "/users/" + uid + "/" + getUserHash(uid);
        URL obj = new URL(url);
        HttpURLConnection con = null;
        con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        logger.info("mfa-esupotp request send to [{}]", url.toString());
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        logger.debug("getUserInfos({}) : {}", uid, response.toString());
        
        return new JSONObject(response.toString());
    }

    public String getUserHash(String uid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5Md = MessageDigest.getInstance("MD5");
        String md5 = (new HexBinaryAdapter()).marshal(md5Md.digest(esupOtpConfigurationProperties.getUsersSecret().getBytes()));
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

    public Boolean bypass(List<EsupOtpMethod> methods) throws JSONException, IOException {
        Boolean bypass = true;
        for (EsupOtpMethod method : methods) {
            if (method.getActive()) {
                bypass = false;
            }
        }
        return bypass;
    }
}
