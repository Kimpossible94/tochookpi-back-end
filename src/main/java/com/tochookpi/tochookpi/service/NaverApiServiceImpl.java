package com.tochookpi.tochookpi.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class NaverApiServiceImpl implements NaverApiService {
    private final String CLIENT_ID = "1";
    private final String CLIENT_SECRET = "1";
    private final WebClient webClient;

    public NaverApiServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void searchLocal(String query) {
        // items 안나오는 코드
        String apiUrl = UriComponentsBuilder
                .fromHttpUrl("https://openapi.naver.com/v1/search/local")
                .queryParam("query", query)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();
        ResponseEntity<String> block = webClient.get().uri(apiUrl)
                .retrieve()
                .toEntity(String.class)
                .block();

        String body = block.getBody();

        System.out.println(body);



        // 잘되는 코드
//        URI uri = UriComponentsBuilder
//                .fromUriString("https://openapi.naver.com")
//                .path("/v1/search/local.json")
//                .queryParam("query", query)
//                .queryParam("display", 5)
//                .queryParam("start", 1)
//                .queryParam("sort", "random")
//                .encode(Charset.forName("UTF-8"))
//                .build()
//                .toUri();
//
//        WebClient webClient = WebClient.builder()
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
//                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
//                .build();
//
//        ResponseEntity<String> responseEntity = webClient.get()
//                .uri(uri)
//                .retrieve()
//                .toEntity(String.class)
//                .block();
//
//        String responseBody = responseEntity.getBody();
//        System.out.println(responseBody);
    }
}
