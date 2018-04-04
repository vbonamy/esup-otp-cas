package org.apereo.cas.adaptors.authy.config.support.authentication;

import org.apereo.cas.adaptors.esupotp.EsupOtpAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.adaptors.esupotp.EsupOtpMultifactorAuthenticationProvider;
import org.apereo.cas.configuration.model.support.mfa.EsupOtpMultifactorProperties;
import org.apereo.cas.authentication.MultifactorAuthenticationProviderBypass;
import org.apereo.cas.authentication.MultifactorAuthenticationUtils;
import org.apereo.cas.adaptors.esupotp.EsupOtpAuthenticationMetaDataPopulator;
import org.springframework.webflow.execution.Action;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This is {@link EsupOtpAuthenticationEventExecutionPlanConfiguration}.
 *
 * @author Francis Le Coq
 * @since 5.2.2
 */
@Configuration("EsupOtpAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class EsupOtpAuthenticationEventExecutionPlanConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    /* Avoid to modify org.apereo.cas.configuration.model.support.mfa.MultifactorAuthenticationProperties
     * Not sure about the result in the future
     */
    @Bean
    public EsupOtpMultifactorProperties esupotpMultifactorProperties() {
        // that line replace casProperties.getAuthn().getMfa().getEsupOtp()
        return new EsupOtpMultifactorProperties(); 
    }

    @ConditionalOnMissingBean(name = "esupotpAuthenticationHandler")
    @Bean
    @RefreshScope
    public AuthenticationHandler esupotpAuthenticationHandler() {
        return new EsupOtpAuthenticationHandler(
        	esupotpMultifactorProperties().getName(), 
        	servicesManager, 
        	esupotpPrincipalFactory()
        );
    }

	@Bean
	public PrincipalFactory esupotpPrincipalFactory() {
		return new DefaultPrincipalFactory();
	}
	
	@Bean
	@RefreshScope
	public MultifactorAuthenticationProvider esupotpAuthenticationProvider() {
        final EsupOtpMultifactorProperties esupotp = esupotpMultifactorProperties();
        final EsupOtpMultifactorAuthenticationProvider p = new EsupOtpMultifactorAuthenticationProvider();
        p.setBypassEvaluator(esupOtpBypassEvaluator());
        p.setGlobalFailureMode(casProperties.getAuthn().getMfa().getGlobalFailureMode());
        p.setOrder(esupotp.getRank());
        p.setId(esupotp.getId());
		return p;
	}
	
    @Bean
    @RefreshScope
    public MultifactorAuthenticationProviderBypass esupOtpBypassEvaluator() {
        return MultifactorAuthenticationUtils.newMultifactorAuthenticationProviderBypass(esupotpMultifactorProperties().getBypass());
    }

	@Bean
	@RefreshScope
	public EsupOtpAuthenticationMetaDataPopulator esupotpAuthenticationMetaDataPopulator() {
		return new EsupOtpAuthenticationMetaDataPopulator(
			casProperties.getAuthn().getMfa().getAuthenticationContextAttribute(),
			esupotpAuthenticationHandler(),
			esupotpAuthenticationProvider()
		);
	}
	
    @ConditionalOnMissingBean(name = "esupotpAuthenticationEventExecutionPlanConfigurer")
    @Bean
    public AuthenticationEventExecutionPlanConfigurer esupotpAuthenticationEventExecutionPlanConfigurer() {
        return plan -> {
            plan.registerAuthenticationHandler(esupotpAuthenticationHandler());
            plan.registerMetadataPopulator(esupotpAuthenticationMetaDataPopulator());
        };
    }
}
