package org.esupportail.cas.config;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.authentication.RankedMultifactorAuthenticationProviderSelector;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.esupportail.cas.adaptors.esupotp.EsupOtpService;
import org.esupportail.cas.adaptors.esupotp.web.flow.EsupOtpAuthenticationWebflowAction;
import org.esupportail.cas.adaptors.esupotp.web.flow.EsupOtpAuthenticationWebflowEventResolver;
import org.esupportail.cas.adaptors.esupotp.web.flow.EsupOtpGetTransportsAction;
import org.esupportail.cas.adaptors.esupotp.web.flow.EsupOtpMultifactorWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

@Configuration("esupotpConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class EsupOtpConfiguration {
	@Autowired
	private CasConfigurationProperties casProperties;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	@Qualifier("loginFlowRegistry")
	private FlowDefinitionRegistry loginFlowDefinitionRegistry;

	@Autowired
	private FlowBuilderServices flowBuilderServices;

	@Autowired
	@Qualifier("builder")
	private FlowBuilderServices builder;

	@Autowired
	@Qualifier("centralAuthenticationService")
	private CentralAuthenticationService centralAuthenticationService;

	@Autowired
	@Qualifier("defaultAuthenticationSystemSupport")
	private AuthenticationSystemSupport authenticationSystemSupport;

	@Autowired
	@Qualifier("defaultTicketRegistrySupport")
	private TicketRegistrySupport ticketRegistrySupport;

	@Autowired
	@Qualifier("servicesManager")
	private ServicesManager servicesManager;

	@Autowired
	@Qualifier("warnCookieGenerator")
	private CookieGenerator warnCookieGenerator;

    @Autowired
    @Qualifier("authenticationServiceSelectionPlan")
    private AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired(required = false)
    @Qualifier("multifactorAuthenticationProviderSelector")
    private MultifactorAuthenticationProviderSelector multifactorAuthenticationProviderSelector =
            new RankedMultifactorAuthenticationProviderSelector();
    
    @Autowired
    EsupOtpConfigurationProperties esupOtpConfigurationProperties;
            
	@RefreshScope
	@Bean
	public FlowDefinitionRegistry esupotpFlowRegistry() {
		final FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(this.applicationContext, this.builder);
		builder.setBasePath("classpath*:/webflow");
		builder.addFlowLocationPattern("/mfa-esupotp/*-webflow.xml");
		return builder.build();
	}

	@Bean
	public Action esupotpAuthenticationWebflowAction() {
		final EsupOtpAuthenticationWebflowAction a = new EsupOtpAuthenticationWebflowAction();
		a.setEsupOtpAuthenticationWebflowEventResolver(esupotpAuthenticationWebflowEventResolver());
		return a;
	}

	@Bean
	@RefreshScope public Action esupotpGetTransportsAction(EsupOtpService esupOtpService) {
		final EsupOtpGetTransportsAction a = new EsupOtpGetTransportsAction(esupOtpConfigurationProperties, esupOtpService);
		return a;
	}

	@Bean
	public CasWebflowEventResolver esupotpAuthenticationWebflowEventResolver() {
		return new EsupOtpAuthenticationWebflowEventResolver(
			authenticationSystemSupport, 
            centralAuthenticationService, 
            servicesManager, 
            ticketRegistrySupport,
            warnCookieGenerator, 
            authenticationRequestServiceSelectionStrategies, 
            eventPublisher, 
            applicationContext
		);
	}
    
    @ConditionalOnMissingBean(name = "esupotpMultifactorWebflowConfigurer")
    @Bean
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer esupotpMultifactorWebflowConfigurer() {
        final CasWebflowConfigurer w = new EsupOtpMultifactorWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry,
                esupotpFlowRegistry(), applicationContext, casProperties);
        w.initialize();
        return w;
    }

}