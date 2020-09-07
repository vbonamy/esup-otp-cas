package org.apereo.cas.adaptors.esupotp;

import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpMultifactorWebflowConfigurer;
import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

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
        return EsupOtpMultifactorWebflowConfigurer.MFA_ESUPOTP_ID;
    }

    @Override
    public int getOrder() {
        return this.rank;
    }

	@Override
	public String getFriendlyName() {
		return EsupOtpMultifactorWebflowConfigurer.MFA_ESUPOTP_ID;
	}
}
