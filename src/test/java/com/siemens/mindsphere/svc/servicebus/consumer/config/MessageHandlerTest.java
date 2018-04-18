package com.siemens.mindsphere.svc.servicebus.consumer.config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.IMessage;
import com.siemens.mindsphere.svc.servicebus.constant.Constants;
import com.siemens.mindsphere.svc.servicebus.consumer.service.AzureServiceBusConsumer;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusConnectivityContext;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusForwardingContextHolder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.MDC;

@RunWith(MockitoJUnitRunner.class)
public class MessageHandlerTest {

    private final String CORRELATION_ID = "0614340a-2f34-40d8-8a33-a8fe0862ea17";

    private final byte[] MESSAGE_BODY = new String("{\"field\":\"Value\"}").getBytes();

    @Spy @InjectMocks private MessageHandler sut;

    @Mock private ServiceBusForwardingContextHolder forwardingContextHolder;

    @Mock private ServiceBusConnectivityContext context;

    @Mock private AzureServiceBusConsumer receiverService;

    @Mock private IMessage message;

    @Mock private CompletableFuture<Void> result;

    @Mock private Map<String, String> properties;

    @Spy private ObjectMapper objectMapper;

    @Test
    public void shouldReceiveMessage() {

        doNothing().when(this.forwardingContextHolder).clearContext();
        doReturn(this.properties).when(this.message).getProperties();
        doReturn(this.CORRELATION_ID).when(this.properties).get(Constants.HEADER_CORRELATION_ID);
        doReturn(this.MESSAGE_BODY).when(this.message).getBody();

        this.sut.onMessageAsync(this.message);

        Mockito.verify(this.forwardingContextHolder).clearContext();
        Mockito.verify(this.receiverService).onMessageReceived(isA(JsonNode.class));

        // Validate Correlation Id Set in MDC
        assertEquals(MDC.get(Constants.LOG_CORRELATION_ID), this.CORRELATION_ID);
    }
}
