package org.esupportail.cas.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.esupportail.cas.authentication.BypassEsupOtpApiAuthenticationHandler;
import org.esupportail.cas.authentication.EsupOtpApiAuthenticationHandler;
import org.esupportail.cas.web.flow.EsupOtpMultifactorWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Configuration("esupOtpConf")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableScheduling
@Slf4j
public class EsupOtpConf {

    @Bean @Autowired
    public BypassEsupOtpApiAuthenticationHandler bypassEsupOtpApiAuthenticationHandler(final ServicesManager servicesManager,
									                                                   final PrincipalFactory principalFactory,
                                                                                       final EsupOtpProperties esupOtpProperties) {
        return new BypassEsupOtpApiAuthenticationHandler(servicesManager, principalFactory, esupOtpProperties);
    }

    @Bean @Autowired
    public EsupOtpApiAuthenticationHandler esupOtpApiAuthenticationHandler(final ServicesManager servicesManager,
										   final PrincipalFactory principalFactory,
										   final EsupOtpProperties esupOtpProperties) {
        return new EsupOtpApiAuthenticationHandler(servicesManager, principalFactory, esupOtpProperties);
    }

    @Bean
    public EsupOtpProperties esupOtpProperties() {
        return new EsupOtpProperties();
    }

    @Bean @Autowired
    public EsupOtpMultifactorWebflowConfigurer esupOtpMultifactorWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                               final FlowDefinitionRegistry loginFlowRegistry,
                                               final ApplicationContext applicationContext,
                                               final CasConfigurationProperties casProperties) {
        return new EsupOtpMultifactorWebflowConfigurer(flowBuilderServices, loginFlowRegistry, applicationContext, casProperties);
    }

}
