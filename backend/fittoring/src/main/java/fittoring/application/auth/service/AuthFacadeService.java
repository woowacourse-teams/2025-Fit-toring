package fittoring.application.auth.service;

import fittoring.application.auth.presentation.dto.response.KakaoTokenResponse;
import fittoring.application.auth.presentation.dto.response.KakaoUserInfoResponse;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.infrastructure.OauthClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthFacadeService {

    private final OauthClientService oauthClientService;
    private final AuthService authService;

    public LoginInfoDto kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = oauthClientService.requestKakaoToken(code);
        String kakaoAccessToken = tokenResponse.access_token();
        KakaoUserInfoResponse userInfoResponse = oauthClientService.requestKakaoId(kakaoAccessToken);
        Long kakaoId = userInfoResponse.id();

        return authService.processKakaoLogin(kakaoId);
    }
}
