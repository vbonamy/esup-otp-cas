package org.esupportail.cas.authentication;

import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

import javax.security.auth.login.FailedLoginException;
import org.jasig.cas.authentication.PreventedException;
import java.security.GeneralSecurityException;


public class EsupOtpApiAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

	@Override
	protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)throws GeneralSecurityException, PreventedException{
		throw new FailedLoginException("Invalid credentials.");
	}
}