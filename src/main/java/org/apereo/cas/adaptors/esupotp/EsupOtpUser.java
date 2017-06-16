package org.apereo.cas.adaptors.esupotp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

import org.apereo.cas.adaptors.esupotp.EsupOtpMethod;

/**
 * This is {@link EsupOtpUser}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpUser implements Serializable {
	private static final long serialVersionUID = -8908901234511037L;

	private String uid;
	private String hash;
	private String sms;
	private String mail;
	private List<EsupOtpMethod> methods;

	/**
	 * Instantiates a new Esup Otp user.
	 */
	public EsupOtpUser() {
	}

	public EsupOtpUser(final String uid, final String hash) {
		this.uid = uid;
		this.hash = hash;
	}

	public EsupOtpUser(final String uid, final String hash, List<EsupOtpMethod> methods, JSONObject transports) {
		this.uid = uid;
		this.hash = hash;
		this.methods = methods;
		parseJsonTransports(transports);
	}
	
	public void parseJsonTransports(JSONObject transports){
		for (Object transport : transports.keySet()) {
			if(transport.toString().equals("sms"))this.sms = transports.getString(transport.toString());
			if(transport.toString().equals("mail"))this.mail = transports.getString(transport.toString());
		}
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<EsupOtpMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<EsupOtpMethod> methods) {
		this.methods = methods;
	}
}
