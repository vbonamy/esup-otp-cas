package org.esupportail.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractMultifactorTrustedDeviceWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class EsupOtpMultifactorTrustWebflowConfigurer extends AbstractMultifactorTrustedDeviceWebflowConfigurer {

    private final FlowDefinitionRegistry flowDefinitionRegistry;

    public EsupOtpMultifactorTrustWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                      final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                      final boolean enableDeviceRegistration,
                                                      final FlowDefinitionRegistry flowDefinitionRegistry,
                                                      final ApplicationContext applicationContext,
                                                      final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, enableDeviceRegistration, applicationContext, casProperties);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
    }

    @Override
    protected void doInitialize() {
        registerMultifactorTrustedAuthentication(this.flowDefinitionRegistry);
    }

}
