package org.esupportail.cas.adaptors.esupotp.web.flow;

import java.util.List;
import java.util.Optional;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.trusted.web.flow.AbstractMultifactorTrustedDeviceWebflowConfigurer;
import org.apereo.cas.web.flow.configurer.CasMultifactorWebflowCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;


public class EsupOtpMultifactorTrustWebflowConfigurer extends AbstractMultifactorTrustedDeviceWebflowConfigurer {
	
	private Boolean isDeviceRegistrationRequired = false;

    private final FlowDefinitionRegistry flowDefinitionRegistry;

    public EsupOtpMultifactorTrustWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                      final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                      final boolean enableDeviceRegistration,
                                                      final boolean isDeviceRegistrationRequired,
                                                      final FlowDefinitionRegistry flowDefinitionRegistry,
                                                      final ConfigurableApplicationContext applicationContext,
                                                      final CasConfigurationProperties casProperties,
                                                      final List<CasMultifactorWebflowCustomizer> mfaFlowCustomizers) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties, Optional.of(flowDefinitionRegistry), mfaFlowCustomizers);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
        this.isDeviceRegistrationRequired = isDeviceRegistrationRequired;
    }

    @Override
    protected void doInitialize() {
    	/* no more needed in 6.3 ?
        val flowId = Arrays.stream(flowDefinitionRegistry.getFlowDefinitionIds()).findFirst().get();
        val flow = (Flow) flowDefinitionRegistry.getFlowDefinition(flowId);
        // Hack : override DECISION_STATE_REQUIRE_REGISTRATION that is used (and normally created) by AbstractMultifactorTrustedDeviceWebflowConfigurer.registerMultifactorTrustedAuthentication
        // -> with this, we bypass register form device if isDeviceRegistrationRequired=false
        createDecisionState(flow, CasWebflowConstants.DECISION_STATE_REQUIRE_REGISTRATION,
                isDeviceRegistrationRequired.toString() + " and flashScope.".concat(MFA_TRUSTED_AUTHN_SCOPE_ATTR).concat("== null"),
                CasWebflowConstants.VIEW_ID_REGISTER_DEVICE, CasWebflowConstants.STATE_ID_REGISTER_TRUSTED_DEVICE);
        */
        
        registerMultifactorTrustedAuthentication(this.flowDefinitionRegistry);

    }

}
