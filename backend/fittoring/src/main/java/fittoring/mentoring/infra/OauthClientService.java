package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.dto.KakaoTokenResponse;
import fittoring.mentoring.business.service.dto.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class OauthClientService {

    private final RestClient restClient;
    private static final String kakaoTokenRequestUri = "https://kauth.kakao.com/oauth/token";
    private static final String kakaoUserInfoRequestUri = "https://kapi.kakao.com/v2/user/me";
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${kakao.client-id}")
    private String kakaoClientId;

    public OauthClientService(@Qualifier("defaultRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public KakaoTokenResponse requestKakaoToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        return restClient.post()
                .uri(kakaoTokenRequestUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse requestKakaoId(String kakaoAccessToken) {
        return restClient.get()
                .uri(kakaoUserInfoRequestUri)
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }
}
