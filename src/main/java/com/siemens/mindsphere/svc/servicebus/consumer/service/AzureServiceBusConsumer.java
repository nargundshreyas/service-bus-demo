package com.siemens.mindsphere.svc.servicebus.consumer.service;

import com.fasterxml.jackson.databind.JsonNode;

/** The Interface ReceiverService. */
public interface AzureServiceBusConsumer {

    /**
     * On message received.
     *
     * @param message the message
     */
    public void onMessageReceived(JsonNode message);
}
