package com.siemens.mindsphere.svc.servicebus.producer;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.IMessageSender;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.siemens.mindsphere.svc.servicebus.TestMessage;
import com.siemens.mindsphere.svc.servicebus.constant.Constants;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusConnectivityContext;
import com.siemens.mindsphere.svc.servicebus.context.ServiceBusForwardingContextHolder;
import com.siemens.mindsphere.svc.servicebus.producer.service.AzureServiceBusProducer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AzureServiceBusProducerTest {

    private static final String CORRELATION_ID = "0614340a-2f34-40d8-8a33-a8fe0862ea17";
    private static final String QUEUE_NAME = "QUEUE_NAME";

    @Spy @InjectMocks private AzureServiceBusProducer sut;

    @Mock private Map<String, IMessageSender> servicebusProducerClients;

    @Mock private IMessageSender messageSender;

    @Mock private ServiceBusForwardingContextHolder forwardingContext;

    @Mock private ServiceBusConnectivityContext context;

    @Mock private ObjectMapper objectMapper;

    private TestMessage message;

    private Map<String, String> customHeader;

    @Before
    public void dataSetUp() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CORRELATION_ID, CORRELATION_ID);
        String body = "Test Message body!!!";
        this.message = new TestMessage(headers, body);

        this.customHeader = new HashMap<>();
        this.customHeader.put("CUSTOM_HEADER", "test-value");
    }

    @Test
    public void shouldSendMessageInSyncModeWithoutHeaders()
            throws ServiceBusException, InterruptedException {
        MDC.put(Constants.LOG_CORRELATION_ID, CORRELATION_ID);

        doReturn(this.messageSender).when(this.servicebusProducerClients).get(QUEUE_NAME);
        doReturn(this.context).when(this.forwardingContext).getContext();
        doReturn(CORRELATION_ID).when(this.context).getCorrelationId();

        this.sut.sendMessage(QUEUE_NAME, this.message);

        Mockito.verify(this.servicebusProducerClients).get(isA(String.class));
        Mockito.verify(this.messageSender).send(isA(Message.class));
    }

    @Test
    public void shouldSendMessageInSyncModeWithHeaders()
            throws ServiceBusException, InterruptedException {
        MDC.put(Constants.LOG_CORRELATION_ID, CORRELATION_ID);

        doReturn(this.messageSender).when(this.servicebusProducerClients).get(QUEUE_NAME);
        doReturn(this.context).when(this.forwardingContext).getContext();
        doReturn(CORRELATION_ID).when(this.context).getCorrelationId();

        this.sut.sendMessage(QUEUE_NAME, this.message, this.customHeader);

        Mockito.verify(this.servicebusProducerClients).get(isA(String.class));
        Mockito.verify(this.messageSender).send(isA(Message.class));
    }

    @Test
    public void shouldSendMessageInASyncModeWithoutHeaders()
            throws ServiceBusException, InterruptedException {
        MDC.put(Constants.LOG_CORRELATION_ID, CORRELATION_ID);

        doReturn(this.messageSender).when(this.servicebusProducerClients).get(QUEUE_NAME);
        doReturn(this.context).when(this.forwardingContext).getContext();
        doReturn(CORRELATION_ID).when(this.context).getCorrelationId();

        this.sut.sendAsyncMessage(QUEUE_NAME, this.message);

        Mockito.verify(this.servicebusProducerClients).get(isA(String.class));
        Mockito.verify(this.messageSender).sendAsync(isA(Message.class));
    }

    @Test
    public void shouldSendMessageInASyncModeWithHeaders()
            throws ServiceBusException, InterruptedException {
        MDC.put(Constants.LOG_CORRELATION_ID, CORRELATION_ID);

        doReturn(this.messageSender).when(this.servicebusProducerClients).get(QUEUE_NAME);
        doReturn(this.context).when(this.forwardingContext).getContext();
        doReturn(CORRELATION_ID).when(this.context).getCorrelationId();

        this.sut.sendAsyncMessage(QUEUE_NAME, this.message, this.customHeader);

        Mockito.verify(this.servicebusProducerClients).get(isA(String.class));
        Mockito.verify(this.messageSender).sendAsync(isA(Message.class));
    }

    @Test(expected = ServiceBusException.class)
    public void shouldThrowExceptionWhenPayloadIsNull()
            throws ServiceBusException, InterruptedException {
        this.sut.sendAsyncMessage(QUEUE_NAME, null);
    }
}
