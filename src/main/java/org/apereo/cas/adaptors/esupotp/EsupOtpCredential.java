package org.apereo.cas.adaptors.esupotp;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apereo.cas.authentication.Credential;

import java.io.Serializable;

/**
 * This is {@link EsupOtpCredential}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpCredential implements Credential, Serializable {
    private static final long serialVersionUID = -7570600701132111037L;

    private String token;
    
    private Boolean bypass = false;

	/**
     * Instantiates a new Esup otp token credential.
     */
    public EsupOtpCredential() {
    }

    /**
     * Instantiates a new Esup otp credential.
     *
     * @param token the token
     */
    public EsupOtpCredential(final String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("token", this.token)
                .toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof EsupOtpCredential)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final EsupOtpCredential other = (EsupOtpCredential) obj;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.token, other.token);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder(97, 31);
        builder.append(this.token);
        return builder.toHashCode();
    }

    @Override
    public String getId() {
        return this.token;
    }


    public String getToken() {
        return this.token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public boolean isValid() {
        return this.token != null;
    }
    
    public Boolean getBypass() {
        return bypass;
    }

    public void setBypass(final Boolean bypass) {
        this.bypass = bypass;
    }

    public void activeBypass() {
        this.bypass = true;
    }
}
