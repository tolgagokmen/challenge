package io.overledger.springboottemplateservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TemplateException.class)
    public ResponseEntity<ErrorDetailsTemplate> handleTemplateException(TemplateException ex, WebRequest request) {
        ErrorDetailsTemplate errorDetailsTemplate = new ErrorDetailsTemplate(
                "Template Service",
                42,
                System.currentTimeMillis(),
                String.format("Failed processing the request: %s", ex.getMessage()),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetailsTemplate, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
