# ServiceBusHelper Library

## Azure Implementation:

ServiceBus Helper library helps Mindsphere Microservice developer to easily connect to Azure Service Bus through their microservice and use it to simply send and receive message.


### Why to use?

ServiceBusHelper library takes care of Service Bus Queue's Sender and Receiver configuration.

Microservice doesn't need to do sender/receiver configuration. Just adding required properties in application.properties enable service developer to use Azure's Service Bus Queues.

ServiceBusHelper library also takes care of adding Correlation-Id to application context.

### How to use?

1. Add ServiceBusHelper dependency in your gradle file

2. For sending data to Service Bus Queue:

	a. Add Following property to your application.properties,
	
	Note: You can send messages to multiple queues using this library. In such a case, you need to provide mapping of service bus queue name to service bus connection string.
    
		azure.servicebus.producer.servicebusparameter[0].name={service-bus-name}
    	azure.servicebus.producer.servicebusparameter[0].connectionstring={service-bus-connectionstring}
    	
    	azure.servicebus.producer.servicebusparameter[1].name={service-bus-name}
    	azure.servicebus.producer.servicebusparameter[1].connectionstring={service-bus-connectionstring}
        
        ...

    b. Add following packages to 'scanBasePackages' along with root package in Application file:
        com.siemens.mindsphere.svc.servicebus.producer
        com.siemens.mindsphere.svc.servicebus.context     
        
    c. In your service class Autowire AzureServiceBusProducer bean,
    
   		@Autowired
		private AzureServiceBusProducer serviceBusProducer;
    
     d. ServiceBusHelper support both sync and async send operation to Service Bus.
     
     For sending data Synchronously, use below method,
     
     	sendMessage(T data)

        OR

        sendMessage(T data, Map<String, String> headers)
        
        Example:
            serviceBusProducer.sendMessage("{\"message\":\"This is a test Message.\"}");
     		
        
     For sending data Asynchronously, use below method,
     
     	sendAsyncMessage(T data)

        OR

        sendAsyncMessage(T data, Map<String, String> headers)
        
        Example:
     		serviceBusProducer.sendAsyncMessage("{\"message\":\"This is a test Message.\"}");
   
3.	For receiving data from Service bus:
	
    a. Add Following properties to your application.properties,
    
		azure.servicebus.consumer.connectionstring={service-bus-queue-connection-string}
		azure.servicebus.consumer.queuename={service-bus-queue-name}
		azure.servicebus.consumer.receivemode={receive-mode}(Optional.Default mode is PEEKLOCK)
    
    b. Add following packages to 'scanBasePackages' along with root package in Application file:
        com.siemens.mindsphere.svc.servicebus.consumer
        com.siemens.mindsphere.svc.servicebus.context

    c. Implement AzureServiceBusConsumer interface and override onMessageReceived method.

    Received data is of JsonNode type. Service developer need to write code to convert JsonNode to DTO.
    Note that onMessageReceived will be invoked when message is received from the queue.

    Example:
    
        import org.apache.log4j.MDC;
        import org.springframework.stereotype.Service;

        import com.fasterxml.jackson.databind.JsonNode;
        import com.siemens.mindsphere.svc.servicebus.constant.Constants;
        import com.siemens.mindsphere.svc.servicebus.consumer.service.AzureServiceBusConsumer;

        import lombok.extern.slf4j.Slf4j;

        @Service
        @Slf4j
        public class ServiceBusConsumerImpl implements AzureServiceBusConsumer {

            @Override
            public void onMessageReceived(final JsonNode message) {
                log.info("The correaltion id is :" + MDC.get(Constants.LOG_CORRELATION_ID));
                log.info("Received Message {}:" + message);
        }
    