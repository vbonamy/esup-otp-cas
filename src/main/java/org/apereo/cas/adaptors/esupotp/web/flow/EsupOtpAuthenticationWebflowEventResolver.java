package org.apereo.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Set;

/**
 * This is {@link EsupOtpAuthenticationWebflowEventResolver}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpAuthenticationWebflowEventResolver extends AbstractCasWebflowEventResolver {
    
    public EsupOtpAuthenticationWebflowEventResolver(final AuthenticationSystemSupport authenticationSystemSupport,
                                                   final CentralAuthenticationService centralAuthenticationService, final ServicesManager servicesManager,
                                                   final TicketRegistrySupport ticketRegistrySupport, final CookieGenerator warnCookieGenerator,
                                                   final AuthenticationServiceSelectionPlan authenticationSelectionStrategies,
                                                   final MultifactorAuthenticationProviderSelector selector) {
        super(authenticationSystemSupport, centralAuthenticationService, servicesManager, ticketRegistrySupport, warnCookieGenerator,
                authenticationSelectionStrategies, selector);
    }
    
    @Override
    public Set<Event> resolveInternal(final RequestContext context) {
        return handleAuthenticationTransactionAndGrantTicketGrantingTicket(context);
    }

}
