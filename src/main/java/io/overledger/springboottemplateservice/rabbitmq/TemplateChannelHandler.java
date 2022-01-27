package io.overledger.springboottemplateservice.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.overledger.springboottemplateservice.dto.TemplateRequest;
import io.overledger.springboottemplateservice.services.TemplateService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
@EnableBinding(TemplateOutputChannel.class)
@IntegrationComponentScan
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j

// Template Channel Handler that can be used to consume messages from the Queue.
// To use the Channel Handler, uncomment this class, switch the TemplateOutputChannel to an
// Input Channel and comment out the rabbitmq.TemplateConfig class

public class TemplateChannelHandler {

    TemplateService templateService;
    ObjectMapper objectMapper;

    @StreamListener(TemplateOutputChannel.CHANNEL_NAME)
    public void stateChannelHandler(@Payload String payload, @Header("message") String message, @Header("processTime") Long processTime, @Header("type") String type) throws JsonProcessingException {
        TemplateRequest templateRequest = null;
        if (type.equals(TemplateRequest.class.getName())) {
            templateRequest = this.objectMapper.readValue(payload, TemplateRequest.class);
        }
        if (templateRequest != null) {
            log.info(String.format("Processing templateRequest: %s, templateField: %s, dispatch timestamp: %d, message type: %s.", templateRequest.getTemplateField(), message, processTime, type));
            this.templateService.saveToDatabase(templateRequest);
        }
    }
}
