package com.toiletnearby.place.client;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import com.toiletnearby.place.service.PlaceSearchCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// Java HttpClient로 Kakao Local keyword API를 호출한다.
@Component
public class KakaoPlaceSearchHttpClient implements PlaceSearchClient {

    private static final String KAKAO_AUTHORIZATION_PREFIX = "KakaoAK ";
    private static final String KAKAO_API_HOST = "dapi.kakao.com";

    private final ObjectMapper objectMapper;
    private final String kakaoBaseUrl;
    private final String kakaoRestApiKey;
    private final HttpClient httpClient;

    public KakaoPlaceSearchHttpClient(
            ObjectMapper objectMapper,
            @Value("${kakao.local.base-url}") String kakaoBaseUrl,
            @Value("${kakao.local.rest-api-key:}") String kakaoRestApiKey
    ) {
        this.objectMapper = objectMapper;
        this.kakaoBaseUrl = kakaoBaseUrl;
        this.kakaoRestApiKey = kakaoRestApiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    // Kakao API를 호출하고 우리 응답 DTO 목록으로 변환한다.
    @Override
    public List<PlaceSearchResponseDto> search(PlaceSearchCondition condition) {
        ensureApiKeyConfigured();

        HttpRequest request = HttpRequest.newBuilder(buildUri(condition))
                .timeout(Duration.ofSeconds(5))
                .header("Authorization", KAKAO_AUTHORIZATION_PREFIX + kakaoRestApiKey)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "장소 검색 API 호출에 실패했습니다.");
            }

            return parseResults(response.body());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "장소 검색 API 응답을 읽을 수 없습니다.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "장소 검색 API 호출이 중단되었습니다.", e);
        }
    }

    // Kakao keyword search API URI를 만든다.
    private URI buildUri(PlaceSearchCondition condition) {
        URI baseUri = createVerifiedKakaoBaseUri();

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(baseUri)
                .path("/v2/local/search/keyword.json")
                .queryParam("query", condition.query())
                .queryParam("page", condition.page())
                .queryParam("size", condition.size());

        if (condition.hasCoordinate()) {
            builder.queryParam("x", condition.x());
            builder.queryParam("y", condition.y());
        }

        if (condition.radius() != null) {
            builder.queryParam("radius", condition.radius());
        }

        return builder.build().encode().toUri();
    }

    // 외부 API base URL은 사용자 입력이 아니라 설정값이지만, 허용된 Kakao host인지 한 번 더 확인한다.
    private URI createVerifiedKakaoBaseUri() {
        URI baseUri = URI.create(kakaoBaseUrl);

        if (!"https".equalsIgnoreCase(baseUri.getScheme()) || !KAKAO_API_HOST.equalsIgnoreCase(baseUri.getHost())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API base URL 설정이 올바르지 않습니다.");
        }

        return baseUri;
    }

    // Kakao 응답 JSON의 documents 배열을 우리 응답 DTO로 바꾼다.
    private List<PlaceSearchResponseDto> parseResults(String responseBody) throws IOException {
        JsonNode documents = objectMapper.readTree(responseBody).path("documents");
        List<PlaceSearchResponseDto> results = new ArrayList<>();

        for (JsonNode document : documents) {
            results.add(new PlaceSearchResponseDto(
                    document.path("id").asString(),
                    document.path("place_name").asString(),
                    document.path("category_name").asString(),
                    document.path("phone").asString(),
                    document.path("address_name").asString(),
                    document.path("road_address_name").asString(),
                    parseDouble(document, "x"),
                    parseDouble(document, "y"),
                    document.path("place_url").asString(),
                    parseDistance(document)
            ));
        }

        return results;
    }

    // Kakao REST API key가 없으면 외부 API를 호출할 수 없다.
    private void ensureApiKeyConfigured() {
        if (kakaoRestApiKey == null || kakaoRestApiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 REST API 키가 설정되지 않았습니다.");
        }
    }

    // Kakao 응답의 좌표 문자열을 double로 변환한다.
    private double parseDouble(JsonNode node, String fieldName) {
        String value = node.path(fieldName).asString();

        if (value == null || value.isBlank()) {
            return 0;
        }

        return Double.parseDouble(value);
    }

    // Kakao 응답의 distance 문자열을 int로 변환한다.
    private int parseDistance(JsonNode node) {
        String value = node.path("distance").asString();

        if (value == null || value.isBlank()) {
            return 0;
        }

        return Integer.parseInt(value);
    }
}
