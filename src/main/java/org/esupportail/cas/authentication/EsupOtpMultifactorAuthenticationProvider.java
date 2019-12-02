package org.esupportail.cas.authentication;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;
import org.esupportail.cas.configuration.EsupOtpProperties;


@Slf4j
@NoArgsConstructor
public class EsupOtpMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {

    private static final long serialVersionUID = 4789727148634156909L;

    @Override
    public String getId() {
        return StringUtils.defaultIfBlank(super.getId(), EsupOtpProperties.DEFAULT_IDENTIFIER);
    }
    @Override
    public String getFriendlyName() {
        return "Esup-Otp Authenticator";
    }
}