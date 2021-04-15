package org.esupportail.cas.config.support.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.bypass.BaseMultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.web.support.WebUtils;
import org.esupportail.cas.adaptors.esupotp.EsupOtpMethod;
import org.esupportail.cas.adaptors.esupotp.EsupOtpService;
import org.esupportail.cas.config.EsupOtpConfigurationProperties;
import org.esupportail.cas.configuration.model.support.mfa.EsupOtpMultifactorProperties;
import org.json.JSONObject;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsupOtpBypassProvider extends BaseMultifactorAuthenticationProviderBypassEvaluator {

	private static final long serialVersionUID = 1L;

    EsupOtpService esupOtpService;
    
    EsupOtpConfigurationProperties esupOtpConfigurationProperties;
    
	public EsupOtpBypassProvider(EsupOtpService esupOtpService,
			EsupOtpConfigurationProperties esupOtpConfigurationProperties) {
		super(EsupOtpMultifactorProperties.DEFAULT_IDENTIFIER);
		this.esupOtpService = esupOtpService;
		this.esupOtpConfigurationProperties = esupOtpConfigurationProperties;
	}

	@Override
	public boolean shouldMultifactorAuthenticationProviderExecuteInternal(Authentication authentication,
			RegisteredService registeredService, MultifactorAuthenticationProvider provider,
			HttpServletRequest request) {
		try {					
			log.debug("mfa-esupotp bypass evaluation ...");		
			final RequestContext context = RequestContextHolder.getRequestContext();
			if(context == null) {
				log.trace("Not in flow context");
				return false;
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
	
				if (esupOtpConfigurationProperties.getByPassIfNoEsupOtpMethodIsActive() && esupOtpService.bypass(listMethods)) {
					log.info(String.format("no method active for %s - mfa-esupotp bypass", uid));
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
