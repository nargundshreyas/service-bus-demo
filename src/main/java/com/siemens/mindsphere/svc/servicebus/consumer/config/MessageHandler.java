package com.siemens.mindsphere.svc.servicebus.consumer.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.siemens.mindsphere.svc.servicebus.constant.Constants;
import com.siemens.mindsphere.svc.servicebus.consumer.service.AzureServiceBusConsumer;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusForwardingConnectivityContext;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusForwardingContextHolder;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/** The Class MessageHandler. */
@Slf4j
@Service
@ConditionalOnBean(AzureServiceBusConsumer.class)
public class MessageHandler implements IMessageHandler {

    /** The forwarding context holder. */
    private final ServiceBusForwardingContextHolder forwardingContextHolder;

    /** The receiver service. */
    private final AzureServiceBusConsumer receiverService;

    /** The object mapper. */
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageHandler(
            final ServiceBusForwardingContextHolder forwardingContextHolder,
            final AzureServiceBusConsumer receiverService) {
        this.forwardingContextHolder = forwardingContextHolder;
        this.receiverService = receiverService;
        this.objectMapper = new ObjectMapper();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.microsoft.azure.servicebus.IMessageHandler#onMessageAsync(com.
     * microsoft.azure.servicebus.IMessage)
     */
    @Override
    public CompletableFuture<Void> onMessageAsync(final IMessage message) {
        log.debug("Message intercepted by MessageHandler: {}", message.getBody().toString());
        this.forwardingContextHolder.clearContext();
        ServiceBusForwardingConnectivityContext forwardingConnectivityContext =
                new ServiceBusForwardingConnectivityContext();
        // extract corraltionId from message
        String correlationId = this.extractCorrelationIdFromMessage(message);
        forwardingConnectivityContext.setOrGenerateCorrelationId(correlationId);
        // set correlationId in context
        MDC.put(Constants.LOG_CORRELATION_ID, forwardingConnectivityContext.getCorrelationId());
        this.forwardingContextHolder.setContext(forwardingConnectivityContext);
        // callback for message received

        Optional<JsonNode> optionalJsonNode = this.convert(message.getBody());

        optionalJsonNode.ifPresent(
                jsonNode -> {
                    this.receiverService.onMessageReceived(jsonNode);
                });
        return CompletableFuture.completedFuture(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.microsoft.azure.servicebus.IMessageHandler#notifyException(java.lang. Throwable,
     * com.microsoft.azure.servicebus.ExceptionPhase)
     */
    @Override
    public void notifyException(final Throwable exception, final ExceptionPhase phase) {}

    /**
     * Extract correlation id from message.
     *
     * @param message the message
     * @return the string
     */
    private String extractCorrelationIdFromMessage(final IMessage message) {
        Map<String, String> propertiesMap = message.getProperties();
        String correlationId = null;
        if (Objects.nonNull(propertiesMap)
                && Objects.nonNull(propertiesMap.get(Constants.HEADER_CORRELATION_ID))) {
            correlationId = propertiesMap.get(Constants.HEADER_CORRELATION_ID);
        }
        return correlationId;
    }

    /**
     * Convert.
     *
     * @param ba the ba
     * @return the optional
     */
    private Optional<JsonNode> convert(final byte[] ba) {
        try {
            return Optional.ofNullable(this.objectMapper.readValue(ba, JsonNode.class));
        } catch (IOException e) {
            log.error("Data format is not valid data will be dropped {}", new String(ba));
        }
        return Optional.empty();
    }
}
