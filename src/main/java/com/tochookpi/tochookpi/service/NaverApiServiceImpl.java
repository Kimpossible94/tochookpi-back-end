package com.tochookpi.tochookpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tochookpi.tochookpi.dto.NaverApiAddressDTO;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.TochookpiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NaverApiServiceImpl implements NaverApiService {

    @Value("${naver.client.id}")
    private String CLIENT_ID;
    @Value("${naver.client.secret}")
    private String CLIENT_SECRET;
    private final WebClient webClient;

    public NaverApiServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<NaverApiAddressDTO> searchLocal(String query) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/local.json")
                .queryParam("query", query)
                .queryParam("display", 5)
                .queryParam("start", 1)
                .queryParam("sort", "random")
                .encode(Charset.forName("UTF-8"))
                .build()
                .toUri();

        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();

        ResponseEntity<String> response = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .block();

        String body = response.getBody();

        try {
            // JSON String -> Map 변환
            Map<String, Object> responseMap = new ObjectMapper().readValue(body, Map.class);
            List<Map<String, String>> items = (List<Map<String, String>>) responseMap.get("items");

            // AddressDTO 리스트로 변환
            return items.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new TochookpiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private NaverApiAddressDTO convertToDTO(Map<String, String> item) {
        NaverApiAddressDTO dto = new NaverApiAddressDTO();
        dto.setTitle(item.get("title"));
        dto.setCategory(item.get("category"));
        dto.setAddress(item.get("address"));
        dto.setRoadAddress(item.get("roadAddress"));
        dto.setMapx(item.get("mapx"));
        dto.setMapy(item.get("mapy"));
        return dto;
    }
}
