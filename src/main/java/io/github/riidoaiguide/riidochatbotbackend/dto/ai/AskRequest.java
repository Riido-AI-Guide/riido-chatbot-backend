package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AskRequest(@JsonProperty("query") String query) {}