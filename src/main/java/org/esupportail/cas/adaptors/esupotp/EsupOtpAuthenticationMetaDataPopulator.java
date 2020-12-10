package org.esupportail.cas.adaptors.esupotp;

import org.apereo.cas.authentication.AuthenticationBuilder;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationManager;
import org.apereo.cas.authentication.AuthenticationTransaction;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.metadata.BaseAuthenticationMetaDataPopulator;

/**
 * This is {@link EsupOtpAuthenticationMetaDataPopulator} which inserts the
 * MFA provider id into the final authentication object.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */

public class EsupOtpAuthenticationMetaDataPopulator extends BaseAuthenticationMetaDataPopulator {
    private final String authenticationContextAttribute;
    private final AuthenticationHandler authenticationHandler;
    private final MultifactorAuthenticationProvider provider;

    public EsupOtpAuthenticationMetaDataPopulator(final String authenticationContextAttribute,
                                                           final AuthenticationHandler authenticationHandler,
                                                           final MultifactorAuthenticationProvider provider) {
        this.authenticationContextAttribute = authenticationContextAttribute;
        this.authenticationHandler = authenticationHandler;
        this.provider = provider;
    }

    public void populateAttributes(final AuthenticationBuilder builder, final AuthenticationTransaction transaction) {
        if (builder.hasAttribute(AuthenticationManager.AUTHENTICATION_METHOD_ATTRIBUTE,
            obj -> obj.toString().equals(this.authenticationHandler.getName()))) {
            builder.mergeAttribute(this.authenticationContextAttribute, this.provider.getId());
        }
    }

    @Override
    public boolean supports(final Credential credential) {
        return this.authenticationHandler.supports(credential);
    }
}

