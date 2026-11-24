package com.example.helloapp;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.time.Duration;

@RestController
public class CatFactController {

    private final RestClient client;

    public CatFactController() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        client = RestClient.builder().requestFactory(requestFactory).build();
    }

    @GetMapping(path = "/api/cat-fact", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CatFact> catFact() {
        CatFact fact;
        try {
            fact = client.get()
                    .uri("https://catfact.ninja/fact")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CatFact.class);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not load a cat fact", exception);
        }

        if (fact == null || fact.fact() == null || fact.fact().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cat fact service returned an empty fact");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(fact);
    }

    public record CatFact(String fact) {
    }
}
