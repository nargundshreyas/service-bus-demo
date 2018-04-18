package com.siemens.mindsphere.svc.servicebus.context;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/** Holder class contains connectivity context to be managed. */
@Component
public class ServiceBusForwardingContextHolder {

    /** The connectivity context holder. */
    private final InheritableThreadLocal<ServiceBusConnectivityContext> connectivityContextHolder;

    /** Instantiates a new forwarding context holder. */
    public ServiceBusForwardingContextHolder() {
        this.connectivityContextHolder = new InheritableThreadLocal<>();
    }

    /** Clear context. */
    public void clearContext() {
        this.connectivityContextHolder.remove();
    }

    /**
     * Gets the context.
     *
     * @return the context
     */
    public ServiceBusConnectivityContext getContext() {
        ServiceBusConnectivityContext connectivityContext = this.connectivityContextHolder.get();

        if (connectivityContext == null) {
            connectivityContext = new ServiceBusForwardingConnectivityContext();
            this.connectivityContextHolder.set(connectivityContext);
        }

        return connectivityContext;
    }

    /**
     * Sets the context.
     *
     * @param connectivityContext the new context
     */
    public void setContext(final ServiceBusConnectivityContext connectivityContext) {
        Assert.notNull(
                connectivityContext, "Only non-null SecurityContext instances are permitted");
        this.connectivityContextHolder.set(connectivityContext);
    }
}
