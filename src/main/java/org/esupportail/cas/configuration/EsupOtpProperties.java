package org.esupportail.cas.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Getter
@Setter
@ConfigurationProperties(value = "esup.auth.mfa.esupotp", ignoreUnknownFields = false)
public class EsupOtpProperties implements Serializable {

    private static final long serialVersionUID = -7304734732383722585L;
    public static final String  DEFAULT_IDENTIFIER = "mfa-esupotp";
    private String urlApi = "";
    private String usersSecret = "";
    private String apiPassword = "";

}
