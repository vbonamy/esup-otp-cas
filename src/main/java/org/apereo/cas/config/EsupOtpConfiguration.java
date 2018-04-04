package org.apereo.cas.config;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpGetTransportsAction;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpAuthenticationWebflowAction;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpAuthenticationWebflowEventResolver;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpMultifactorWebflowConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationMetaDataPopulator;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.authentication.RankedMultifactorAuthenticationProviderSelector;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Configuration("esupotpConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class EsupOtpConfiguration {
	@Autowired
	private CasConfigurationProperties casProperties;

	@Autowired
	private ApplicationContext applicationContext;

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
    
    @Autowired(required = false)
    @Qualifier("multifactorAuthenticationProviderSelector")
    private MultifactorAuthenticationProviderSelector multifactorAuthenticationProviderSelector =
            new RankedMultifactorAuthenticationProviderSelector();
            
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
	@RefreshScope public Action esupotpGetTransportsAction() {
		final EsupOtpGetTransportsAction a = new EsupOtpGetTransportsAction();
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
            multifactorAuthenticationProviderSelector
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