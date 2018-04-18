package com.siemens.mindsphere.svc.servicebus;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ToString
@AllArgsConstructor
public class TestMessage {

    /** The headers. */
    private final Map<String, String> headers;

    /** The body. */
    private final String body;
}
