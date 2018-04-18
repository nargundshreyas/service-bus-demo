/*
 *
 */
package com.siemens.mindsphere.svc.servicebus.consumer.config;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The Class ConsumerConfig. */
@Configuration
@Slf4j
@Data
public class ConsumerConfig {

    /** The servicebusconnectionstring. */
    @Value(value = "${azure.servicebus.consumer.connectionstring}")
    private String serviceBusconnectionString;

    @Value(value = "${azure.servicebus.consumer.queuename}")
    private String queueName;

    @Value("${azure.servicebus.consumer.receivemode:PEEKLOCK}")
    private String receiveMode;

    /** The message handler. */
    @Autowired private MessageHandler messageHandler;

    /**
     * Servicebus consumer clients.
     *
     * @return the map
     */
    @Bean
    @Qualifier("servicebushelper")
    public QueueClient servicebusConsumerClients() {
        QueueClient queueClient = null;
        try {
            queueClient =
                    new QueueClient(
                            new ConnectionStringBuilder(
                                    this.serviceBusconnectionString, this.queueName),
                            ReceiveMode.valueOf(this.receiveMode));
            log.info("Registering handler for queueclient{}" + this.queueName);
            queueClient.registerMessageHandler(this.messageHandler);
        } catch (ServiceBusException | InterruptedException exception) {
            log.error(
                    "Failed to create service bus receiver queue{}" + this.queueName,
                    exception.getCause());
        }
        return queueClient;
    }
}
