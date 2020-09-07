package org.apereo.cas.adaptors.esupotp.web.flow;

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

    public static final String MFA_ESUPOTP_ID = "mfa-esupotp";

    public EsupOtpMultifactorWebflowConfigurer(FlowBuilderServices flowBuilderServices,
			FlowDefinitionRegistry loginFlowDefinitionRegistry, ConfigurableApplicationContext applicationContext,
			CasConfigurationProperties casProperties, Optional<FlowDefinitionRegistry> mfaFlowDefinitionRegistry,
			List<CasMultifactorWebflowCustomizer> mfaFlowCustomizers) {
		super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties, mfaFlowDefinitionRegistry,
				mfaFlowCustomizers);
	}

    
    @Override
    protected void doInitialize() {
        registerMultifactorProviderAuthenticationWebflow(getLoginFlow(), MFA_ESUPOTP_ID, MFA_ESUPOTP_ID);

    }
}