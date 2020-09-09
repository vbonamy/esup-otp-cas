package org.apereo.cas.config;

import java.util.Optional;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.adaptors.esupotp.EsupOtpAuthenticationHandler;
import org.apereo.cas.adaptors.esupotp.EsupOtpMultifactorAuthenticationProvider;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpAuthenticationWebflowAction;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpAuthenticationWebflowEventResolver;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpGetTransportsAction;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpMultifactorWebflowConfigurer;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.bypass.DefaultChainingMultifactorAuthenticationBypassProvider;
import org.apereo.cas.authentication.bypass.MultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.impl.CasWebflowEventResolutionConfigurationContext;
import org.apereo.cas.web.flow.util.MultifactorAuthenticationWebflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

@Configuration("esupotpConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class EsupOtpConfiguration {
	
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @Qualifier("registeredServiceAccessStrategyEnforcer")
    private ObjectProvider<AuditableExecution> registeredServiceAccessStrategyEnforcer;

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
    private ObjectProvider<CentralAuthenticationService> centralAuthenticationService;

    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private ObjectProvider<AuthenticationSystemSupport> authenticationSystemSupport;

    @Autowired
    @Qualifier("defaultTicketRegistrySupport")
    private ObjectProvider<TicketRegistrySupport> ticketRegistrySupport;
    
    @Autowired
    @Qualifier("servicesManager")
    private ObjectProvider<ServicesManager> servicesManager;

    @Autowired
    @Qualifier("authenticationServiceSelectionPlan")
    private ObjectProvider<AuthenticationServiceSelectionPlan> authenticationRequestServiceSelectionStrategies;

    @Autowired
    @Qualifier("warnCookieGenerator")
    private ObjectProvider<CasCookieBuilder> warnCookieGenerator;

    @Autowired
    @Qualifier("ticketRegistry")
    private ObjectProvider<TicketRegistry> ticketRegistry;

	
	@Bean
	public PrincipalFactory esupotpPrincipalFactory() {
		return new DefaultPrincipalFactory();
	}

	@Bean
	public AuthenticationHandler esupotpAuthenticationHandler() {
		final EsupOtpAuthenticationHandler h = new EsupOtpAuthenticationHandler(
				EsupOtpMultifactorWebflowConfigurer.MFA_ESUPOTP_ID,
				servicesManager.getObject(), 
				esupotpPrincipalFactory(),
				0);
		return h;
	}
        
	@RefreshScope
	@Bean
	public FlowDefinitionRegistry esupotpFlowRegistry() {
		logger.info("esupotpFlowRegistry");
		final FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(this.applicationContext, this.builder);
		builder.setBasePath(CasWebflowConstants.BASE_CLASSPATH_WEBFLOW);
		builder.addFlowLocationPattern("/mfa-esupotp/*-webflow.xml");
		return builder.build();
	}
	
    @Bean
    @RefreshScope
    public MultifactorAuthenticationProviderBypassEvaluator esupOtpBypassEvaluator() {
        return new DefaultChainingMultifactorAuthenticationBypassProvider();
    }


    @Bean
    public MultifactorAuthenticationProvider esupotpAuthenticationProvider() {
    	logger.info("esupotpAuthenticationProvider");
        final EsupOtpMultifactorAuthenticationProvider p = new EsupOtpMultifactorAuthenticationProvider();
        p.setId(EsupOtpMultifactorWebflowConfigurer.MFA_ESUPOTP_ID);
        p.setBypassEvaluator(esupOtpBypassEvaluator());
        return p;
    }

    @ConditionalOnMissingBean(name = "esupotpWebflowConfigurer")
    @Bean
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer esupotpWebflowConfigurer() {
    	logger.info("esupotpWebflowConfigurer");
    	Optional<FlowDefinitionRegistry> mfaFlowDefinitionRegistry = Optional.of(esupotpFlowRegistry());
        return new EsupOtpMultifactorWebflowConfigurer(flowBuilderServices, 
        		loginFlowDefinitionRegistry, 
        		applicationContext, 
        		casProperties, 
        		mfaFlowDefinitionRegistry,
        		MultifactorAuthenticationWebflowUtils.getMultifactorAuthenticationWebflowCustomizers(applicationContext));
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "esupotpCasWebflowExecutionPlanConfigurer")
    public CasWebflowExecutionPlanConfigurer mfaSimpleCasWebflowExecutionPlanConfigurer() {
        return plan -> plan.registerWebflowConfigurer(esupotpWebflowConfigurer());
    }
    
    @ConditionalOnMissingBean(name = "esupotpAuthenticationWebflowAction")
    @Bean
    public Action esupotpAuthenticationWebflowAction() {
        final EsupOtpAuthenticationWebflowAction a = new EsupOtpAuthenticationWebflowAction(esupotpAuthenticationWebflowEventResolver());
        return a;
    }
    
    @Bean
    @RefreshScope public Action esupotpGetTransportsAction() {
        final EsupOtpGetTransportsAction a = new EsupOtpGetTransportsAction();
        return a;
    }

    
    @ConditionalOnMissingBean(name = "esupotpAuthenticationWebflowEventResolver")
    @Bean
    public CasWebflowEventResolver esupotpAuthenticationWebflowEventResolver() {
    	CasWebflowEventResolutionConfigurationContext context = CasWebflowEventResolutionConfigurationContext.builder()
                .authenticationSystemSupport(authenticationSystemSupport.getObject())
                .centralAuthenticationService(centralAuthenticationService.getObject())
                .servicesManager(servicesManager.getObject())
                .ticketRegistrySupport(ticketRegistrySupport.getObject())
                .warnCookieGenerator(warnCookieGenerator.getObject())
                .authenticationRequestServiceSelectionStrategies(authenticationRequestServiceSelectionStrategies.getObject())
                .registeredServiceAccessStrategyEnforcer(registeredServiceAccessStrategyEnforcer.getObject())
                .casProperties(casProperties)
                .ticketRegistry(ticketRegistry.getObject())
                .applicationContext(applicationContext)
                .build();

            return new EsupOtpAuthenticationWebflowEventResolver(context);
    }
    
    
}