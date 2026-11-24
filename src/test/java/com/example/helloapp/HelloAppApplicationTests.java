package com.example.helloapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.github.client-id=test-client",
        "spring.security.oauth2.client.registration.github.client-secret=test-secret"
})
@AutoConfigureMockMvc
class HelloAppApplicationTests {

	@Autowired
	MockMvc mvc;

	@Test
	void contextLoads() {
	}

	@Test
	void welcomeIsPublicAndOffersGitHubLogin() throws Exception {
		for (String path : new String[]{"/", "/welcome.html", "/login"}) {
			mvc.perform(get(path))
					.andExpect(status().isOk())
					.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/oauth2/authorization/github\"")))
					.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Sign out"))))
					.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Server timestamp"))));
		}
	}

	@Test
	void githubLoginStartsOnlyWhenRequested() throws Exception {
		mvc.perform(get("/oauth2/authorization/github"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("https://github.com/login/oauth/authorize?")))
                .andExpect(result -> {
                    String location = java.net.URLDecoder.decode(
                            result.getResponse().getHeader("Location"), java.nio.charset.StandardCharsets.UTF_8);
                    org.junit.jupiter.api.Assertions.assertTrue(location.contains("read:user"));
                    org.junit.jupiter.api.Assertions.assertTrue(location.contains("user:email"));
                });
	}

	@Test
	void otherRoutesRequireAuthentication() throws Exception {
		for (String path : new String[]{"/landing.html", "/api/cat-fact"}) {
			mvc.perform(get(path))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/welcome.html"));
		}
	}

	@Test
	void signedInWelcomeRedirectsToLanding() throws Exception {
		mvc.perform(get("/welcome.html").with(oauth2Login()))
				.andExpect(redirectedUrl("/landing.html"));
	}

	@Test
	void landingShowsGitHubDetailsAndSignOut() throws Exception {
		mvc.perform(get("/landing.html").with(oauth2Login().attributes(attributes -> {
			attributes.put("login", "octocat");
			attributes.put("name", "Octo Cat");
			attributes.put("id", 12345);
			attributes.put("email", "octocat@example.com");
			attributes.put("avatar_url", "https://avatars.githubusercontent.com/u/12345");
		})))
				.andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", "no-store"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("octocat")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Octo Cat")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("12345")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("octocat@example.com")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("src=\"https://avatars.githubusercontent.com/u/12345\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sign out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-url=\"/api/cat-fact\"")));
	}

	@Test
	void landingHandlesMissingOptionalDetails() throws Exception {
		mvc.perform(get("/landing.html").with(oauth2Login()))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Not provided")));
	}

	@Test
	void signOutClearsAuthentication() throws Exception {
		mvc.perform(post("/logout").with(oauth2Login()).with(csrf()))
				.andExpect(unauthenticated())
				.andExpect(redirectedUrl("/welcome.html?logout"));
	}

}
