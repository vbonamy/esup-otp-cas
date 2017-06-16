package org.apereo.cas.adaptors.esupotp.web.flow;

import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
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
	private CasWebflowEventResolver esupotpAuthenticationWebflowEventResolver;

    @Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        return this.esupotpAuthenticationWebflowEventResolver.resolveSingle(requestContext);
    }

    public CasWebflowEventResolver getEsupOtpAuthenticationWebflowEventResolver() {
        return esupotpAuthenticationWebflowEventResolver;
    }

    public void setEsupOtpAuthenticationWebflowEventResolver(final CasWebflowEventResolver resolver) {
        this.esupotpAuthenticationWebflowEventResolver = resolver;
    }
}