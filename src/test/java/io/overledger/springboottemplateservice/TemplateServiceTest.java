package io.overledger.springboottemplateservice;

import io.overledger.springboottemplateservice.dto.TemplateRequest;
import io.overledger.springboottemplateservice.dto.TemplateResponse;
import io.overledger.springboottemplateservice.exceptions.ErrorDetailsTemplate;
import io.overledger.springboottemplateservice.mongodb.TemplateDocument;
import io.overledger.springboottemplateservice.mongodb.TemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import java.util.List;
import java.util.UUID;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ContextConfiguration(classes = SpringBootTemplateApplication.class, loader = SpringBootContextLoader.class)
public class TemplateServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TemplateRepository templateRepository;

    @Test
    void contextLoads() {
        assertTrue(templateRepository != null);
    }
    @Test
    public void shouldGetTemplateResponse() throws Exception {

        // Given
        templateRepository.deleteAll().block();
        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();

        // When
        ResponseEntity<TemplateResponse> actual = restTemplate.getForEntity(format("/templates/%s", "hello-spring"), TemplateResponse.class);

        // Then
        assertThat(actual.getStatusCode(), is(OK));
        assertThat(actual.getBody().getTemplateField(), is("hello-spring"));

    }
    @Test
    public void shouldNotCreateRecord() throws Exception {

        // Given
        templateRepository.deleteAll().block();

        // When
        TemplateRequest templateRequest= TemplateRequest.builder().templateField("How are you?").build();
        ResponseEntity<TemplateResponse> actual = restTemplate.postForEntity("/templates",templateRequest, TemplateResponse.class);

        // Then
        assertThat(actual.getStatusCode(), is(OK));
        assertThat(actual.getBody().getTemplateField(), is("Always peachy!"));
        List<TemplateDocument> templateMessages = this.templateRepository
                .findAll()
                .collectList().block();;
        assertThat(templateMessages.size(), is(0));

    }
    @Test
    public void shouldGetReturnNumber() throws Exception {
        // Given
        templateRepository.deleteAll().block();

        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();
        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();
        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();
        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();

        // When
        TemplateRequest templateRequest= TemplateRequest.builder().templateField("hello-spring").build();
        ResponseEntity<TemplateResponse> actual = restTemplate.postForEntity("/templates",templateRequest, TemplateResponse.class);

        // Then
        assertThat(actual.getStatusCode(), is(OK));
        assertThat(actual.getBody().getTemplateField(), is("42"));
    }
    @Test
    public void when_dublicateRecord_thenShouldGetReturnError() throws Exception {
        // Given
        templateRepository.deleteAll().block();

        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();
        templateRepository.save(new TemplateDocument(UUID.randomUUID(), "hello-spring")).block();

        // When
        TemplateRequest templateRequest= TemplateRequest.builder().templateField("hello-spring").build();
        try{
            ResponseEntity<ErrorDetailsTemplate> actual = restTemplate.getForEntity(format("/templates/%s", "hello-spring"), ErrorDetailsTemplate.class);
            fail("Should Get Error");
        }catch (Exception e){
            // Then
            assertNotNull(e);
        }
    }

}
