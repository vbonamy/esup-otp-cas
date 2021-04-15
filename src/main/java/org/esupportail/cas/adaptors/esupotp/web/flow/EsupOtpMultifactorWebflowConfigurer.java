package org.esupportail.cas.adaptors.esupotp.web.flow;

import java.util.List;
import java.util.Optional;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasMultifactorWebflowConfigurer;
import org.apereo.cas.web.flow.configurer.CasMultifactorWebflowCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link EsupOtpMultifactorWebflowConfigurer}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpMultifactorWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {

    /** Webflow event id. */
    public static final String MFA_ESUPOTP_EVENT_ID = "mfa-esupotp";

    private FlowDefinitionRegistry flowDefinitionRegistry;

    public EsupOtpMultifactorWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                           final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                           final FlowDefinitionRegistry flowDefinitionRegistry,
                                                           final ConfigurableApplicationContext applicationContext,
                                                           final CasConfigurationProperties casProperties,
                                                           final List<CasMultifactorWebflowCustomizer> mfaFlowCustomizers) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties, Optional.of(flowDefinitionRegistry), mfaFlowCustomizers);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
    }
    
    @Override
    protected void doInitialize() {
    	registerMultifactorProviderAuthenticationWebflow(getLoginFlow(), MFA_ESUPOTP_EVENT_ID, MFA_ESUPOTP_EVENT_ID);
    }
}
