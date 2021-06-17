package org.esupportail.cas.adaptors.esupotp;

import java.io.Serializable;

/**
 * This is {@link EsupOtpCredential}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpCredentialTransport implements Serializable {
    private static final long serialVersionUID = 6182390898687671093L;

    private String transport;
    
    private String method;
    
    private String uid;
    
    private String userHash;

	/**
     * Instantiates a new Esup otp token credential.
     */
    public EsupOtpCredentialTransport() {
    }

    public String getTransport() {
        return this.transport;
    }

    public void setTransport(final String transport) {
        this.transport = transport;
    }
    
    public String getUid() {
        return this.uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }
    
    public String getMethod() {
        return this.method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }
    
    public String getUserHash() {
		return userHash;
	}

	public void setUserHash(final String userHash) {
		this.userHash = userHash;
	}
}
