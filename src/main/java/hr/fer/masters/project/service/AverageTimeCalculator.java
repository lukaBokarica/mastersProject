package hr.fer.masters.project.service;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import com.slack.api.Slack;

/*
@Component
public class AverageTimeCalculator {
    @Autowired
    private final WebClient client;
    private final String token;
    private final String userId;
    private final String channelId;

    public AverageTimeCalculator(WebClient client, @Value("${SLACK_APP_TOKEN}") String token,
                                 @Value("${SLACK_USER_ID}") String userId,
                                 @Value("${SLACK_CHANNEL_ID}") String channelId) throws SlackApiException, IOException {
        this.client = client;
        this.token = token;
        this.userId = userId;
        this.channelId = channelId;
    }

    public Mono<Double> calculateAverageTime() {
        // Retrieve all messages in the channel
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("slack.com")
                        .path("/api/conversations.history")
                        .queryParam("token", token)
                        .queryParam("channel", channelId)
                        .build())
                .retrieve()
                .bodyToMono(SlackConversationHistoryResponse.class)
                .flatMapIterable(SlackConversationHistoryResponse::getMessages)
                // Filter messages by user
                .filter(message -> message.getUser().equals(userId))
                // Calculate time between messages
                .reduce(new ArrayList<Long>(), (list, message) -> {
                    if (list.isEmpty()) {
                        list.add(message.getTs());
                    } else {
                        list.add(message.getTs() - list.get(list.size() - 1));
                    }
                    return list;
                })
                // Calculate average time
                .map(deltas -> deltas.stream()
                        .mapToDouble(delta -> delta / 1000) // convert to seconds
                        .average()
                        .orElse(0.0));
    }
}*/
