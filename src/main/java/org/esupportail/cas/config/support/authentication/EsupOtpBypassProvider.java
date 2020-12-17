package org.esupportail.cas.config.support.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.MultifactorAuthenticationProviderBypass;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.web.support.WebUtils;
import org.esupportail.cas.adaptors.esupotp.EsupOtpMethod;
import org.esupportail.cas.adaptors.esupotp.EsupOtpService;
import org.json.JSONObject;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class EsupOtpBypassProvider implements MultifactorAuthenticationProviderBypass {

	private static final long serialVersionUID = 1L;

    EsupOtpService esupOtpService;
    
	@Override
	public boolean shouldMultifactorAuthenticationProviderExecute(Authentication authentication,
			RegisteredService registeredService, MultifactorAuthenticationProvider provider,
			HttpServletRequest request) {
		try {		
			final RequestContext context = RequestContextHolder.getRequestContext();
			if(context == null) {
				log.debug("Not in flow context");
			} else {
				final String uid = WebUtils.getAuthentication(context).getPrincipal().getId();
	
				JSONObject userInfos = esupOtpService.getUserInfos(uid);
				List<EsupOtpMethod> listMethods = new ArrayList<EsupOtpMethod>();
	
				JSONObject methods = (JSONObject) ((JSONObject) userInfos.get("user")).get("methods");
	
				for (String method : methods.keySet()) {
					if (!"waitingFor".equals(method) && !"codeRequired".equals(method)) {
						listMethods.add(new EsupOtpMethod(method, (JSONObject) methods.get(method)));
					}
				}
	
				//Uncomment for bypass users with no activated methods
				if (esupOtpService.bypass(listMethods)) {
					log.info("mfa-esupotp bypass");
					return false;
				}
			}
		} catch (Exception e) {
			log.error("Exception ...", e);
			return false;
		}
		return true;
	}
	
}
