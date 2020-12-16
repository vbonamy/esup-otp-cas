package org.esupportail.cas.adaptors.esupotp.web.flow;

import java.util.Set;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link EsupOtpAuthenticationWebflowEventResolver}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@Slf4j
public class EsupOtpAuthenticationWebflowEventResolver extends AbstractCasWebflowEventResolver {
    
    public EsupOtpAuthenticationWebflowEventResolver(final AuthenticationSystemSupport authenticationSystemSupport,
                                                   final CentralAuthenticationService centralAuthenticationService, final ServicesManager servicesManager,
                                                   final TicketRegistrySupport ticketRegistrySupport, final CookieGenerator warnCookieGenerator,
                                                   final AuthenticationServiceSelectionPlan authenticationSelectionStrategies,
                                                   final ApplicationEventPublisher eventPublisher, final ConfigurableApplicationContext applicationContext) {
        super(authenticationSystemSupport, centralAuthenticationService, servicesManager, ticketRegistrySupport, warnCookieGenerator,
                authenticationSelectionStrategies, eventPublisher, applicationContext);
    }
    
    @Override
    public Set<Event> resolveInternal(final RequestContext context) {
        return handleAuthenticationTransactionAndGrantTicketGrantingTicket(context);
    }

}
