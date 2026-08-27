package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskRequest;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final RestClient restClient;

    public ChatService(
            RestClient.Builder builder,
            @Value("${app.ai.base-url}") String baseUrl,
            @Value("${app.ai.connect-timeout:5s}") Duration connectTimeout,
            @Value("${app.ai.read-timeout:60s}") Duration readTimeout
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        log.info("AI 서버 base-url: {}", baseUrl);
    }

    public AskResponse ask(String query) {
        log.debug("AI 질의: {}", query);

        AskResponse response;
        try {
            response = restClient.post()
                    .uri("/api/v1/ask")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AskRequest(query))
                    .retrieve()
                    .body(AskResponse.class);
        } catch (RestClientException e) {
            throw new IllegalStateException("AI 서버 호출에 실패했습니다: " + e.getMessage(), e);
        }

        if (response == null) {
            throw new IllegalStateException("AI 서버 응답이 비어 있습니다");
        }

        log.debug("문서 {}건 참조", response.documents() == null ? 0 : response.documents().size());
        return response;
    }
}
