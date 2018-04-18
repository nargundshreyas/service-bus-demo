package com.siemens.mindsphere.svc.servicebus.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageSender;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.siemens.mindsphere.svc.servicebus.constant.Constants;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusForwardingContextHolder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** The Class AzureServiceBusProducer. */
@Service
@Slf4j
public class AzureServiceBusProducer {

    /** The forwarding context holder. */
    @Autowired private ServiceBusForwardingContextHolder forwardingContextHolder;

    /** The servicebus producer clients. */
    @Autowired
    @Qualifier("producerBean")
    private Map<String, IMessageSender> servicebusProducerClients;

    /** The object mapper. */
    @Autowired private ObjectMapper objectMapper;

    /**
     * Send message.
     *
     * @param <T> the generic type
     * @param queueName the queue name
     * @param payload the payload
     * @throws ServiceBusException the service bus exception
     * @throws InterruptedException the interrupted exception
     */
    public final <T> void sendMessage(final String queueName, final T payload)
            throws ServiceBusException, InterruptedException {
        if (Objects.nonNull(payload)) {
            IMessage message;
            try {
                // Convert payload to byte array.
                message = new Message(this.objectMapper.writeValueAsBytes(payload));
                // attach correlationId property
                message.setProperties(this.addCorrelationHeader(null));
                this.servicebusProducerClients.get(queueName).send(message);
            } catch (IOException e) {
                log.error("Error in payload parsing {}:", e.getCause());
            }
        } else {
            throw new ServiceBusException(true, "Payload cannot be null.");
        }
    }

    /**
     * Send message in asynchronous mode.
     *
     * @param <T> the generic type
     * @param queueName the queue name
     * @param payload the payload
     * @throws ServiceBusException the service bus exception
     * @throws InterruptedException the interrupted exception
     */
    public final <T> void sendAsyncMessage(final String queueName, final T payload)
            throws ServiceBusException, InterruptedException {
        if (Objects.nonNull(payload)) {
            IMessage message;
            try {
                // Convert payload to byte array.
                message = new Message(this.objectMapper.writeValueAsBytes(payload));
                // attach correlationId property
                message.setProperties(this.addCorrelationHeader(null));
                this.servicebusProducerClients.get(queueName).sendAsync(message);
            } catch (IOException e) {
                log.error("Error in payload parsing {}:", e.getCause());
            }
        } else {
            throw new ServiceBusException(true, "Payload cannot be null.");
        }
    }

    /**
     * Send message.
     *
     * @param <T> the generic type
     * @param queueName the queue name
     * @param payload the payload
     * @param headers the headers
     * @throws ServiceBusException the service bus exception
     * @throws InterruptedException the interrupted exception
     */
    public final <T> void sendMessage(
            final String queueName, final T payload, final Map<String, String> headers)
            throws ServiceBusException, InterruptedException {
        if (Objects.nonNull(payload)) {
            IMessage message;
            try {
                // Convert payload to byte array.
                message = new Message(this.objectMapper.writeValueAsBytes(payload));
                // attach correlationId property
                message.setProperties(this.addCorrelationHeader(headers));
                this.servicebusProducerClients.get(queueName).send(message);
            } catch (IOException e) {
                log.error("Error in payload parsing {}:", e.getCause());
            }
        } else {
            throw new ServiceBusException(true, "Payload cannot be null.");
        }
    }

    /**
     * Send message in asynchronous mode.
     *
     * @param <T> the generic type
     * @param queueName the queue name
     * @param payload the payload
     * @param headers the headers
     * @throws ServiceBusException the service bus exception
     * @throws InterruptedException the interrupted exception
     */
    public final <T> void sendAsyncMessage(
            final String queueName, final T payload, final Map<String, String> headers)
            throws ServiceBusException, InterruptedException {
        if (Objects.nonNull(payload)) {
            IMessage message;
            try {
                // Convert payload to byte array.
                message = new Message(this.objectMapper.writeValueAsBytes(payload));
                // attach correlationId property
                message.setProperties(this.addCorrelationHeader(headers));
                this.servicebusProducerClients.get(queueName).sendAsync(message);
            } catch (IOException e) {
                log.error("Error in payload parsing {}:", e.getCause());
            }
        } else {
            throw new ServiceBusException(true, "Payload cannot be null.");
        }
    }

    /**
     * Adds the correlation header.
     *
     * @param headers the headers
     * @return the map
     */
    private Map<String, String> addCorrelationHeader(final Map<String, String> headers) {
        Map<String, String> updatedHeaders =
                headers == null ? new HashMap<>() : this.clearCorrelationIdFromHeaders(headers);
        updatedHeaders.put(
                Constants.HEADER_CORRELATION_ID,
                this.forwardingContextHolder.getContext().getCorrelationId());
        MDC.put(
                Constants.LOG_CORRELATION_ID,
                this.forwardingContextHolder.getContext().getCorrelationId());
        return updatedHeaders;
    }

    /**
     * Clear correlation id from headers.
     *
     * @param headers the headers
     * @return the map
     */
    private Map<String, String> clearCorrelationIdFromHeaders(final Map<String, String> headers) {
        headers.remove(Constants.HEADER_CORRELATION_ID);
        return headers;
    }
}
