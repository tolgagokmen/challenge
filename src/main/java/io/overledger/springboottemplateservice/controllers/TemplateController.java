package io.overledger.springboottemplateservice.controllers;

import io.overledger.springboottemplateservice.dto.TemplateRequest;
import io.overledger.springboottemplateservice.dto.TemplateResponse;
import io.overledger.springboottemplateservice.mongodb.TemplateDocument;
import io.overledger.springboottemplateservice.services.TemplateService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(TemplateController.RESOURCE_NAME)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TemplateController {

    static final String RESOURCE_NAME = "templates";
    TemplateService templateService;

    // Example of a Post API. This saves the message in the Database and publishes it to the Queue, then returns whatever is sent in the Request Body.
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TemplateResponse> postStuff(@RequestBody @NotNull TemplateRequest templateRequest) {
        return this.templateService.postStuff(templateRequest);
    }

    // Example of a GET API. This reads the database and returns the document which has the 'templateField' sent in the Path Variable.
    @GetMapping(value = "/{templatePathVariable}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TemplateDocument> getStuff(@NotNull @PathVariable(name = "templatePathVariable") String templatePathVariable) {
        return this.templateService.getStuff(templatePathVariable);
    }
}
