package org.esupportail.cas.adaptors.esupotp;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.services.RegisteredService;
import org.esupportail.cas.config.EsupOtpConfigurationProperties;
import org.esupportail.cas.configuration.model.support.mfa.EsupOtpMultifactorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The authentication provider
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {

    private static final long serialVersionUID = 4789727148634156909L;
        
    @Override
    public String getId() {
        return StringUtils.defaultIfBlank(super.getId(), EsupOtpMultifactorProperties.DEFAULT_IDENTIFIER);
    }

    @Override
    public boolean isAvailable(final RegisteredService service) {
        return true;
    }

    @Override
    public String getFriendlyName() {
        return "Esup OTP MFA";
    }
}
