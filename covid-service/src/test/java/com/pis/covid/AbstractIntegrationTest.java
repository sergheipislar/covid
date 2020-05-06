package com.pis.covid;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("integration_test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    protected <T> ResponseEntity<T[]> get(Class<T[]> tClass) {
        HttpEntity<T[]> request = new HttpEntity<>(null);
        return restTemplate.exchange(getPath(), HttpMethod.GET, request, tClass);
    }

    protected <T> ResponseEntity<T> post(T object, Class<T> tClass) {
        return post(getApiPath(), object, tClass);
    }

    protected <T> ResponseEntity<T> post(String apiPath, T object, Class<T> tClass) {
        HttpEntity<T> request = new HttpEntity<>(object, createHttpHeaders());
        return restTemplate.postForEntity(getPath(apiPath), request, tClass);
    }

    protected <T> ResponseEntity<T> put(T object, Class<T> tClass) {
        HttpEntity<T> request = new HttpEntity<>(object, createHttpHeaders());
        return restTemplate.exchange(getPath(), HttpMethod.PUT, request, tClass);
    }

    protected ResponseEntity<?> delete(String idName, String id) {
        return delete(getApiPath(), idName, id);
    }

    protected ResponseEntity<?> delete(String apiPath, String idName, String id) {
        HttpEntity<?> request = new HttpEntity<>(null);
        return restTemplate.exchange(getPath(apiPath) + "?{key}={value}", HttpMethod.DELETE, request, String.class, idName, id);
    }

    abstract String getApiPath();

    private String getPath() {
        return getPath(getApiPath());
    }

    private String getPath(String apiPath) {
        return "http://localhost:" + port + apiPath;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
