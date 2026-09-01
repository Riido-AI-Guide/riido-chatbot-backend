package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskRequest;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.ConversationTurnDto;
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

    /** 첫 대화 — 히스토리 없이 질문만 보낸다 */
    public AskResponse ask(String query) {
        return ask(AskRequest.firstTurn(query));
    }

    /** 후속 대화 — 이전 대화(최근 몇 쌍)를 함께 보내 대명사·생략을 풀 수 있게 한다 */
    public AskResponse ask(String query, java.util.List<ConversationTurnDto> history, String conversationId) {
        return ask(new AskRequest(query, history, conversationId));
    }

    private AskResponse ask(AskRequest request) {
        log.debug("AI 질의: {} (히스토리 {}턴)", request.query(), request.history().size());

        AskResponse response;
        try {
            response = restClient.post()
                    .uri("/api/v1/ask")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
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
