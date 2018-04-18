package com.siemens.mindsphere.svc.servicebus.context;

/** Interface contains objects those need to be forwarded all along the request lifecycle. */
public interface ServiceBusConnectivityContext {

    /**
     * Gets the correlation id.
     *
     * @return the correlation id
     */
    String getCorrelationId();

    /**
     * Sets the or generate correlation id.
     *
     * @param correlationId the new or generate correlation id
     */
    void setOrGenerateCorrelationId(String correlationId);

    /**
     * Gets the access token.
     *
     * @return the access token
     */
    String getAccessToken();

    /**
     * Sets the access token.
     *
     * @param accessToken the new access token
     */
    void setAccessToken(String accessToken);
}
