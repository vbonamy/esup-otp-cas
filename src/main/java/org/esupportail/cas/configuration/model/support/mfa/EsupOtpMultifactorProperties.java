package org.esupportail.cas.configuration.model.support.mfa;

import org.apereo.cas.configuration.model.support.mfa.BaseMultifactorAuthenticationProviderProperties;

/**
 * This is {@link EsupOtpMultifactorProperties}.
 *
 * @author Francis Le Coq
 * @since 5.2.2
 */
public class EsupOtpMultifactorProperties extends BaseMultifactorAuthenticationProviderProperties {
    /**
     * Provider id by default.
     */
    public static final String DEFAULT_IDENTIFIER = "mfa-esupotp";

    private static final long serialVersionUID = -7401748853833491119L;

    public EsupOtpMultifactorProperties() {
        setId(DEFAULT_IDENTIFIER);
    }

}
