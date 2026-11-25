package com.example.helloapp.catfact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatFactControllerTests {

    private static final String CAT_FACT_URL = "https://catfact.ninja/fact";

    private MockRestServiceServer server;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        mvc = MockMvcBuilders.standaloneSetup(new CatFactController(builder.build())).build();
    }

    @Test
    void returnsCatFactWithNoStoreCacheControl() throws Exception {
        server.expect(requestTo(CAT_FACT_URL))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("{\"fact\":\"Cats sleep 70% of their lives.\"}", MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/cat-fact"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(jsonPath("$.fact").value("Cats sleep 70% of their lives."));

        server.verify();
    }

    @Test
    void returnsBadGatewayWhenUpstreamFails() throws Exception {
        server.expect(requestTo(CAT_FACT_URL))
                .andRespond(withServerError());

        mvc.perform(get("/api/cat-fact"))
                .andExpect(status().isBadGateway());

        server.verify();
    }

    @Test
    void returnsBadGatewayWhenBodyIsEmpty() throws Exception {
        server.expect(requestTo(CAT_FACT_URL))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/cat-fact"))
                .andExpect(status().isBadGateway());

        server.verify();
    }

    @Test
    void returnsBadGatewayWhenFactFieldIsNull() throws Exception {
        server.expect(requestTo(CAT_FACT_URL))
                .andRespond(withSuccess("{\"fact\":null}", MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/cat-fact"))
                .andExpect(status().isBadGateway());

        server.verify();
    }

    @Test
    void returnsBadGatewayWhenFactFieldIsBlank() throws Exception {
        server.expect(requestTo(CAT_FACT_URL))
                .andRespond(withSuccess("{\"fact\":\"   \"}", MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/cat-fact"))
                .andExpect(status().isBadGateway());

        server.verify();
    }
}
