package org.esupportail.cas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@PropertySource(ignoreResourceNotFound = true, value={"classpath:esupotp.properties", "file:/var/cas/config/esupotp.properties", "file:/opt/cas/config/esupotp.properties", "file:/etc/cas/config/esupotp.properties", "file:${cas.standalone.configurationDirectory}/esupotp.properties"})
@ConfigurationProperties(prefix = "esupotp", ignoreUnknownFields = false)
public class EsupOtpConfigurationProperties implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	int rank = 0;
	
	String urlApi = "CAS";
	
	String usersSecret = "CAS";
	
	String apiPassword = "CAS";
	
	Boolean byPassIfNoEsupOtpMethodIsActive = true;
	
	Boolean trustedDeviceEnabled = true;
	
	Boolean isDeviceRegistrationRequired = false;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("rank : {}", rank);
		log.info("urlApi : {}", urlApi); 
		log.info("usersSecret : {}", usersSecret); 
		log.info("apiPassword : {}", apiPassword); 
	}
	
}
