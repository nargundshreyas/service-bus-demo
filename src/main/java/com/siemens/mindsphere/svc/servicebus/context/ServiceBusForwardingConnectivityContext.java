package com.siemens.mindsphere.svc.servicebus.context;

import com.siemens.mindsphere.svc.servicebus.constant.Constants;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/** Implementation of Forwarding {@link ServiceBusConnectivityContext}. */
@Slf4j
public class ServiceBusForwardingConnectivityContext implements ServiceBusConnectivityContext {

    /** The Constant BEARER_TYPE. */
    private static final String BEARER_TYPE = "Bearer";

    /** The access token. */
    private String accessToken;

    /** The correlation id. */
    private String correlationId;

    /*
     * (non-Javadoc)
     *
     * @see com.siemens.mindsphere.svc.servicebus.context.ConnectivityContext#
     * getCorrelationId()
     */
    @Override
    public String getCorrelationId() {
        if (this.correlationId == null) {
            this.internalSetCorrelationId(generateCorrelationId());
        }
        return this.correlationId;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.siemens.mindsphere.svc.servicebus.context.ConnectivityContext#
     * setOrGenerateCorrelationId(java.lang.String)
     */
    @Override
    public void setOrGenerateCorrelationId(final String correlationId) {
        if (StringUtils.hasText(correlationId)) {
            this.internalSetCorrelationId(correlationId);
        } else {
            this.internalSetCorrelationId(generateCorrelationId());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.siemens.mindsphere.svc.servicebus.context.ConnectivityContext#
     * getAccessToken()
     */
    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.siemens.mindsphere.svc.servicebus.context.ConnectivityContext#
     * setAccessToken(java.lang.String)
     */
    @Override
    public void setAccessToken(final String accessToken) {
        log.debug(
                "Change access token; access token length: <{}>", //
                accessToken == null //
                        ? null //
                        : Integer.toString(accessToken.length()) //
                );

        if (accessToken != null && !accessToken.startsWith(BEARER_TYPE)) {
            this.accessToken = String.format("%s %s", BEARER_TYPE, accessToken);
        } else {
            this.accessToken = accessToken;
        }
    }

    /**
     * Internal set correlation id.
     *
     * @param correlationId the correlation id
     */
    private void internalSetCorrelationId(final String correlationId) {
        log.debug(
                "Change correlation ID (from: {}) -> (to: {})", this.correlationId, correlationId);
        this.correlationId = correlationId;
    }

    /**
     * Generate correlation id.
     *
     * @return the string
     */
    private static String generateCorrelationId() {
        return MDC.get(Constants.LOG_CORRELATION_ID) != null
                ? MDC.get(Constants.LOG_CORRELATION_ID)
                : UUID.randomUUID().toString().replaceAll("-", "");
    }
}
