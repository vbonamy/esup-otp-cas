package org.apereo.cas.adaptors.esupotp;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;
import org.apereo.cas.configuration.model.support.mfa.EsupOtpMultifactorProperties;
import org.apereo.cas.services.RegisteredService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.webflow.execution.Event;

/**
 * The authentication provider
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {

    private static final long serialVersionUID = 4789727148634156909L;
    
    @Autowired
    @Qualifier("esupotpAuthenticationHandler")
    private AuthenticationHandler esupotpAuthenticationHandler;

    @Value("${cas.mfa.esupotp.rank:0}")
    private int rank;
        
    @Override
    public String getId() {
        return StringUtils.defaultIfBlank(super.getId(), EsupOtpMultifactorProperties.DEFAULT_IDENTIFIER);
    }

    @Override
    public int getOrder() {
        return this.rank;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    public String getFriendlyName() {
        return "Esup OTP MFA";
    }
}
