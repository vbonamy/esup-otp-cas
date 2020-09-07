package org.apereo.cas.adaptors.esupotp.web.flow;

import java.util.Set;

import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.impl.CasWebflowEventResolutionConfigurationContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link EsupOtpAuthenticationWebflowEventResolver}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpAuthenticationWebflowEventResolver extends AbstractCasWebflowEventResolver {
   
	public EsupOtpAuthenticationWebflowEventResolver(
			CasWebflowEventResolutionConfigurationContext webflowEventResolutionConfigurationContext) {
		super(webflowEventResolutionConfigurationContext);
	}

	@Override
	public Set<Event> resolveInternal(final RequestContext context) {
        return handleAuthenticationTransactionAndGrantTicketGrantingTicket(context);
    }

}
