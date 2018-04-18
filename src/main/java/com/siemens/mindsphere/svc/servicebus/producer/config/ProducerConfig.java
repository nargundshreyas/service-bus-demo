package com.siemens.mindsphere.svc.servicebus.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.ClientFactory;
import com.microsoft.azure.servicebus.IMessageSender;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The Class ProducerConfig. */
@Configuration
@ConfigurationProperties(prefix = "azure.servicebus.producer")
@Slf4j
public class ProducerConfig {

    private List<ServiceBusParameter> servicebusparameter;

    public List<ServiceBusParameter> getServicebusparameter() {
        return this.servicebusparameter;
    }

    public void setServicebusparameter(final List<ServiceBusParameter> servicebusparameter) {
        this.servicebusparameter = servicebusparameter;
    }

    public static class ServiceBusParameter {
        private String name;
        private String connectionstring;

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getConnectionstring() {
            return this.connectionstring;
        }

        public void setConnectionstring(final String connectionstring) {
            this.connectionstring = connectionstring;
        }
    }

    /**
     * ObjectMapper Bean.
     *
     * @return the ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Servicebus producer clients.
     *
     * @return the map
     */
    @Bean
    @Qualifier("producerBean")
    public Map<String, IMessageSender> servicebusProducerClients() {
        Map<String, IMessageSender> servicebusProducerClients = new HashMap<>();
        if (Objects.nonNull(this.getServicebusparameter())) {
            this.getServicebusparameter()
                    .forEach(
                            (servicebus) -> {
                                String queueName = servicebus.getName();
                                String connectionString = servicebus.getConnectionstring();
                                try {
                                    log.info(
                                            "Configuring Service bus sender client for {}service bus ",
                                            queueName);
                                    ConnectionStringBuilder connectionStringBuilder =
                                            new ConnectionStringBuilder(
                                                    connectionString, queueName);
                                    servicebusProducerClients.put(
                                            queueName,
                                            ClientFactory
                                                    .createMessageSenderFromConnectionStringBuilder(
                                                            connectionStringBuilder));
                                } catch (InterruptedException | ServiceBusException e) {
                                    String servicebusConfigFailedMessage =
                                            String.format(
                                                    "Configuring Service Bus Sender failed for %s",
                                                    queueName);
                                    log.error(servicebusConfigFailedMessage, queueName);
                                }
                            });
            return servicebusProducerClients;
        }
        return null;
    }
}
