package org.apereo.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;


/**
 * This is {@link EsupOtpAuthenticationWebflowAction}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
public class EsupOtpAuthenticationWebflowAction extends AbstractAction {
	
	private final CasWebflowEventResolver esupotpAuthenticationWebflowEventResolver;

    public EsupOtpAuthenticationWebflowAction(CasWebflowEventResolver esupotpAuthenticationWebflowEventResolver) {
		super();
		this.esupotpAuthenticationWebflowEventResolver = esupotpAuthenticationWebflowEventResolver;
	}

	@Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        return this.esupotpAuthenticationWebflowEventResolver.resolveSingle(requestContext);
    }

}