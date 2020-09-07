package org.apereo.cas.adaptors.esupotp;

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

	private String authenticationContextAttribute;

    private AuthenticationHandler authenticationHandler;

    private MultifactorAuthenticationProvider provider;

	@Override
	public void populateAttributes(AuthenticationBuilder builder, AuthenticationTransaction transaction) {
        if (builder.hasAttribute(AuthenticationManager.AUTHENTICATION_METHOD_ATTRIBUTE,
                obj -> obj.toString().equals(this.authenticationHandler.getName()))) {
            builder.mergeAttribute(this.authenticationContextAttribute, this.provider.getId());
        }
    }

    @Override
    public boolean supports(final Credential credential) {
        return this.authenticationHandler.supports(credential);
    }

    public void setAuthenticationContextAttribute(final String authenticationContextAttribute) {
        this.authenticationContextAttribute = authenticationContextAttribute;
    }

    public void setAuthenticationHandler(final AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    public void setProvider(final MultifactorAuthenticationProvider provider) {
        this.provider = provider;
    }


}

