package org.esupportail.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.web.flow.configurer.AbstractCasMultifactorWebflowConfigurer;
import org.esupportail.cas.authentication.EsupOtpMultifactorAuthenticationProvider;
import org.esupportail.cas.configuration.EsupOtpProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Slf4j
public class EsupOtpMultifactorWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {

    /** Webflow event id. */
    public static final String MFA_ESUPOTP_EVENT_ID = "mfa-esupotp";

    private final FlowDefinitionRegistry flowDefinitionRegistry;

    public EsupOtpMultifactorWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                               final FlowDefinitionRegistry loginFlowRegistry,
                                               final ApplicationContext applicationContext,
                                               final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowRegistry, applicationContext, casProperties);
        this.flowDefinitionRegistry = flowDefinitionRegistry();
    }

    @Override
    protected void doInitialize() {
        registerMultifactorProviderAuthenticationWebflow(getLoginFlow(), MFA_ESUPOTP_EVENT_ID,
                this.flowDefinitionRegistry, EsupOtpProperties.DEFAULT_IDENTIFIER);
    }

    public FlowDefinitionRegistry flowDefinitionRegistry() {
        final FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices);
        builder.setBasePath("classpath*:/webflow");
        builder.addFlowLocationPattern("/"+ MFA_ESUPOTP_EVENT_ID +"/"+ MFA_ESUPOTP_EVENT_ID +"-webflow.xml");
        return builder.build();
    }

    @Bean
    @RefreshScope
    public MultifactorAuthenticationProvider esupOtpMultifactorAuthenticationProvider() {
        final EsupOtpMultifactorAuthenticationProvider p = new EsupOtpMultifactorAuthenticationProvider();
        p.setId(MFA_ESUPOTP_EVENT_ID);
        return p;
    }
}
