package ru.deevdenis.messageservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import org.openapi.java.api.DefaultApi;
import org.openapi.java.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Import({AppWebSocketConfig.class, RouteConfig.class})
public class AppConfig {

    @Bean
    public DefaultApi sessionServiceApi(
            @Value("${webclient.ssl.enabled}") boolean isSslEnabled,
            @Value("${session-service.url}") String baseUrl,
            @Value("${session-service.timeout}") int timeout,
            SslContext createSslContextWithTruststore
    ) {
        HttpClient httpClient = HttpClient.create(
                ConnectionProvider.builder("custom")
                        .maxIdleTime(Duration.ofSeconds(120))
                        .build()
                )
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn ->
                        conn
                            .addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                );

        if (isSslEnabled) {
            httpClient = httpClient.secure(sslSpec -> sslSpec
                    .sslContext(createSslContextWithTruststore)
                    .handlerConfigurator(sslHandler -> {
                        SSLEngine engine = sslHandler.engine();
                        //engine.setNeedClientAuth(true);
                        SSLParameters params = new SSLParameters();
                        SNIMatcher matcher = new SNIMatcher(0) {
                            @Override
                            public boolean matches(SNIServerName serverName) {
                                return true;
                            }
                        };
                        params.setSNIMatchers(List.of(matcher));
                        engine.setSSLParameters(params);
                    })
            );
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        ApiClient apiClient = new ApiClient(webClient, baseUrl);

        return new DefaultApi(apiClient);
    }

    @Bean
    @SneakyThrows
    public SslContext createSslContextWithTruststore(
            @Value("${server.ssl.key-store}") Resource truststoreFile,
            @Value("${server.ssl.key-store-password}") String resourceFilePassword
    ) {

        //The Keystore
        KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
        truststore.load(truststoreFile.getInputStream(), resourceFilePassword.toCharArray());

        //The TrustManagerFactory will query the KeyStore for information about which remote certificates should be trusted during authorization checks.
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        //The primary responsibility of the KeyManager is to select the authentication credentials that will eventually be sent to the remote host
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(truststore, resourceFilePassword.toCharArray());

        trustManagerFactory.init(truststore);

        return SslContextBuilder.
                forClient()
                .trustManager(trustManagerFactory)
                .keyManager(keyManagerFactory)
                .keyStoreType("PKCS12")
                .protocols("TLSv1","TLSv1.2","TLSv1.1","TLSv1.3","SSLv3")
                .build();
    }
}
