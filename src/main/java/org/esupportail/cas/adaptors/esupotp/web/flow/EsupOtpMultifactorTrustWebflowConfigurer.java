package org.esupportail.cas.adaptors.esupotp.web.flow;

import java.util.Arrays;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractMultifactorTrustedDeviceWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

import lombok.val;


public class EsupOtpMultifactorTrustWebflowConfigurer extends AbstractMultifactorTrustedDeviceWebflowConfigurer {
	
	private Boolean isDeviceRegistrationRequired = false;

    private final FlowDefinitionRegistry flowDefinitionRegistry;

    public EsupOtpMultifactorTrustWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                      final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                      final boolean enableDeviceRegistration,
                                                      final boolean isDeviceRegistrationRequired,
                                                      final FlowDefinitionRegistry flowDefinitionRegistry,
                                                      final ApplicationContext applicationContext,
                                                      final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, enableDeviceRegistration, applicationContext, casProperties);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
        this.isDeviceRegistrationRequired = isDeviceRegistrationRequired;
    }

    @Override
    protected void doInitialize() {
        val flowId = Arrays.stream(flowDefinitionRegistry.getFlowDefinitionIds()).findFirst().get();
        val flow = (Flow) flowDefinitionRegistry.getFlowDefinition(flowId);
        // Hack : override DECISION_STATE_REQUIRE_REGISTRATION that is used (and normally created) by AbstractMultifactorTrustedDeviceWebflowConfigurer.registerMultifactorTrustedAuthentication
        createDecisionState(flow, CasWebflowConstants.DECISION_STATE_REQUIRE_REGISTRATION,
                isDeviceRegistrationRequired.toString(),
                CasWebflowConstants.VIEW_ID_REGISTER_DEVICE, CasWebflowConstants.STATE_ID_REGISTER_TRUSTED_DEVICE);
        registerMultifactorTrustedAuthentication(this.flowDefinitionRegistry);

    }

}
