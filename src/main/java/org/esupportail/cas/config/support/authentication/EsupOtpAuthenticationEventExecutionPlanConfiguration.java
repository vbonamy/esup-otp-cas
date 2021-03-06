package org.esupportail.cas.config.support.authentication;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.ChainingMultifactorAuthenticationBypassProvider;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.MultifactorAuthenticationProviderBypass;
import org.apereo.cas.authentication.MultifactorAuthenticationUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.esupportail.cas.adaptors.esupotp.EsupOtpAuthenticationHandler;
import org.esupportail.cas.adaptors.esupotp.EsupOtpAuthenticationMetaDataPopulator;
import org.esupportail.cas.adaptors.esupotp.EsupOtpMultifactorAuthenticationProvider;
import org.esupportail.cas.adaptors.esupotp.EsupOtpService;
import org.esupportail.cas.config.EsupOtpConfigurationProperties;
import org.esupportail.cas.configuration.model.support.mfa.EsupOtpMultifactorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    
    @Autowired
    private EsupOtpConfigurationProperties esupOtpConfigurationProperties;

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
        	esupotpPrincipalFactory(),
        	esupOtpConfigurationProperties,
        	esupOtpService()
        );
    }

	@Bean
	public EsupOtpService esupOtpService() {
		return new EsupOtpService(esupOtpConfigurationProperties);
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
        p.setFailureMode(casProperties.getAuthn().getMfa().getGlobalFailureMode());
        p.setOrder(esupotp.getRank());
        p.setId(esupotp.getId());
		return p;
	}
	
    @Bean
    @RefreshScope
    public MultifactorAuthenticationProviderBypass esupOtpBypassEvaluator() {
    	ChainingMultifactorAuthenticationBypassProvider esupOtpBypassEvaluator = (ChainingMultifactorAuthenticationBypassProvider)MultifactorAuthenticationUtils.newMultifactorAuthenticationProviderBypass(esupotpMultifactorProperties().getBypass());
    	esupOtpBypassEvaluator.addBypass(new EsupOtpBypassProvider(esupOtpService()));
    	return esupOtpBypassEvaluator;
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
            plan.registerAuthenticationMetadataPopulator(esupotpAuthenticationMetaDataPopulator());
        };
    }
}
