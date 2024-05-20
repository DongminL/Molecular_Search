package com.example.molecularsearch.chem_info.web.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@ComponentScan(basePackages = {"org.springframework.web.reactive.function.client"})
@Configuration
public class WebClientConfig {

    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
    int processors = Runtime.getRuntime().availableProcessors();    // PC의 Processor 개수

    /* HttpClient 설정 */
    HttpClient httpClient = HttpClient.create(
            ConnectionProvider.builder("ApiConnections")
                    .maxConnections(processors * 2) // 최대 Connection 개수
                    .pendingAcquireTimeout(Duration.ofMillis(0))    //커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
                    .pendingAcquireMaxCount(-1) //커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
                    .maxIdleTime(Duration.ofMillis(1000L))  // 최대 유휴 시간 1초로 설정 (PubChem API의 HTTP KeepAlive timeout보다 작음)
                    .evictInBackground(Duration.ofMillis(1000L)) // 1초마다 유휴 Connections 확인하고 제거
                    .build()
            )
            .responseTimeout(Duration.ofSeconds(30));    // 응답 초과 시간 30초로 설정

    /* 응답값 크기 */
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 5)) // 5MB로 제한
            .build();

    /* WebClient 객체 생성 Bean 등록*/
    @Bean
    public WebClient webClient() {
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY); // URI 인코딩

        return WebClient.builder()
                .uriBuilderFactory(factory) // URI 인코딩 설정
                .clientConnector(new ReactorClientHttpConnector(httpClient))    // HTTPClient 설정
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // Header의 Content_Type 부분 JSON 형식으로 명시
                .exchangeStrategies(exchangeStrategies) // 응답값의 크기를 10MB로 설정
                .build();    // 인스턴스 생성
    }
}
