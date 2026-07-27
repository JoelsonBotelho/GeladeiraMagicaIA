package dev.java10x.GeladeiraMagicaIA.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import dev.java10x.GeladeiraMagicaIA.model.FoodItemModel;
import reactor.core.publisher.Mono;

@Service
public class ChatGptService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public ChatGptService(
        WebClient webClient,
        @Value("${gemini.api.key}") String apiKey,
        @Value("${gemini.api.model}") String model
    ) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "A variável de ambiente GEMINI_API_KEY não foi configurada."
            );
        }

        if (model == null || model.isBlank()) {
            throw new IllegalStateException(
                "O modelo do Gemini não foi configurado."
            );
        }
    }

    public Mono<String> generateRecipe(List<FoodItemModel> foodItems) {

        if (foodItems == null || foodItems.isEmpty()) {
            return Mono.just(
                "Não existem alimentos disponíveis para gerar uma receita."
            );
        }

        String alimentos = foodItems.stream()
            .map(item -> String.format(
                "%s (%s) - Quantidade: %d, Validade: %s",
                item.getNome(),
                item.getCategoria(),
                item.getQuantidade(),
                item.getData_validade()
            ))
            .collect(Collectors.joining("\n"));

        String prompt = """
            Com base nos alimentos abaixo, sugira uma receita criativa e prática.
            Priorize os alimentos cuja validade está mais próxima.
            Informe:
            - nome da receita;
            - ingredientes e quantidades;
            - modo de preparação;
            - tempo estimado;
            - número de porções.
            Alimentos disponíveis:
            %s
            """.formatted(alimentos);

        Map<String, Object> requestMap = Map.of(
            "systemInstruction", Map.of(
                "parts", List.of(
                    Map.of(
                        "text",
                        "Você é um assistente especializado em receitas "
                            + "e redução de desperdício alimentar."
                    )
                )
            ),
            "contents", List.of(
                Map.of(
                    "role", "user",
                    "parts", List.of(
                        Map.of("text", prompt)
                    )
                )
            ),
            "generationConfig", Map.of(
                "temperature", 0.5,
                "maxOutputTokens", 800
            )
        );

        return webClient.post()
            .uri(
                "/v1beta/models/{model}:generateContent",
                model
            )
            .header("x-goog-api-key", apiKey)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .bodyValue(requestMap)
            .retrieve()
            .bodyToMono(Map.class)
            .map(this::extractContent)
            .timeout(Duration.ofSeconds(60))
            .onErrorResume(
                WebClientResponseException.TooManyRequests.class,
                this::handleTooManyRequests
            )
            .onErrorMap(
                TimeoutException.class,
                exception -> new IllegalStateException(
                    "O Gemini demorou mais de 60 segundos para responder. "
                        + "Tente novamente.",
                    exception
                )
            )
            .onErrorMap(
                WebClientResponseException.class,
                exception -> new IllegalStateException(
                    "Erro ao comunicar com o Gemini: "
                        + exception.getStatusCode()
                        + " - "
                        + exception.getResponseBodyAsString(),
                    exception
                )
            );
    }

    private String extractContent(Map<?, ?> response) {
        Object candidatesObject = response.get("candidates");

        if (
            !(candidatesObject instanceof List<?> candidates)
                || candidates.isEmpty()
        ) {
            return extractBlockedResponse(response);
        }

        Object firstCandidateObject = candidates.get(0);

        if (!(firstCandidateObject instanceof Map<?, ?> firstCandidate)) {
            return "A resposta do Gemini possui um formato inesperado.";
        }

        Object contentObject = firstCandidate.get("content");

        if (!(contentObject instanceof Map<?, ?> content)) {
            return "A resposta do Gemini não contém conteúdo.";
        }

        Object partsObject = content.get("parts");

        if (!(partsObject instanceof List<?> parts) || parts.isEmpty()) {
            return "A resposta do Gemini não contém texto.";
        }

        String generatedText = parts.stream()
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(part -> part.get("text"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .filter(text -> !text.isBlank())
            .collect(Collectors.joining("\n"));

        if (generatedText.isBlank()) {
            return "Nenhuma receita foi gerada.";
        }

        return generatedText;
    }

    private String extractBlockedResponse(Map<?, ?> response) {
        Object promptFeedbackObject = response.get("promptFeedback");

        if (promptFeedbackObject instanceof Map<?, ?> promptFeedback) {
            Object blockReason = promptFeedback.get("blockReason");

            if (blockReason != null) {
                return "A solicitação foi bloqueada pelo Gemini. Motivo: "
                    + blockReason;
            }
        }

        return "Nenhuma receita foi gerada.";
    }

    private boolean isTemporaryError(Throwable throwable) {
        if (!(throwable instanceof WebClientResponseException exception)) {
            return false;
        }

        HttpStatusCode status = exception.getStatusCode();

        return status.value() == 429 || status.is5xxServerError();
    }

    private Mono<String> handleTooManyRequests(
        WebClientResponseException.TooManyRequests exception
    ) {
        String responseBody = exception.getResponseBodyAsString();

        System.err.println(
            "Erro 429 retornado pelo Gemini: " + responseBody
        );

        return Mono.error(
            new IllegalStateException(
                "A quota ou o limite de requisições do Gemini foi atingido. "
                    + "Tente novamente mais tarde.",
                exception
            )
        );
    }
}