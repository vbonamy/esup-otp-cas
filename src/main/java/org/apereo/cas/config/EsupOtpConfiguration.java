package org.apereo.cas.config;

import java.util.ArrayList;
import java.util.Optional;

import org.apereo.cas.adaptors.esupotp.EsupOtpMultifactorAuthenticationProvider;
import org.apereo.cas.adaptors.esupotp.web.flow.EsupOtpMultifactorWebflowConfigurer;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.configurer.CasMultifactorWebflowCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

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

        
	@RefreshScope
	@Bean
	public FlowDefinitionRegistry esupotpFlowRegistry() {
		final FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(this.applicationContext, this.builder);
		builder.setBasePath("classpath*:/webflow");
		builder.addFlowLocationPattern("/mfa-esupotp/*-webflow.xml");
		return builder.build();
	}

    @Bean
    public MultifactorAuthenticationProvider esupotpAuthenticationProvider() {
        final EsupOtpMultifactorAuthenticationProvider p = new EsupOtpMultifactorAuthenticationProvider();
        p.setId(EsupOtpMultifactorWebflowConfigurer.MFA_ESUPOTP_ID);
        return p;
    }

    @Bean
    public CasWebflowConfigurer esupotpWebflowConfigurer() {
    	Optional<FlowDefinitionRegistry> mfaFlowDefinitionRegistry = Optional.of(esupotpFlowRegistry());
        return new EsupOtpMultifactorWebflowConfigurer(flowBuilderServices, 
        		loginFlowDefinitionRegistry, 
        		applicationContext, 
        		casProperties, 
        		mfaFlowDefinitionRegistry,
        		new ArrayList<CasMultifactorWebflowCustomizer>());
    }
    
}