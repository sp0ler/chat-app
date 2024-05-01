package ru.deevdenis.sessionservice.factory;

import lombok.RequiredArgsConstructor;
import org.openapi.java.api.DefaultApi;
import org.openapi.java.invoker.ApiClient;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MessageServiceApiFactory {

    private final HttpClient httpClient;

    public DefaultApi buildDefaultApi(String baseUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        ApiClient apiClient = new ApiClient(webClient, baseUrl);

        return new DefaultApi(apiClient);
    }

}
