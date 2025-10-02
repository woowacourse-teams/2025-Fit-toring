package fittoring.application.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicateLoginIdException;
import fittoring.application.exception.DuplicatePhoneException;
import fittoring.application.exception.InvalidTokenException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.domain.model.AuthProvider;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberOauth;
import fittoring.domain.model.Phone;
import fittoring.domain.model.RefreshToken;
import fittoring.domain.model.password.Password;
import fittoring.application.repository.MemberOauthRepository;
import fittoring.application.repository.MemberRepository;
import fittoring.application.repository.RefreshTokenRepository;
import fittoring.application.service.dto.KakaoTokenResponse;
import fittoring.application.service.dto.KakaoUserInfoResponse;
import fittoring.application.infra.OauthClientService;
import fittoring.application.presentation.dto.AuthTokenResponse;
import fittoring.application.presentation.dto.OauthSignUpRequest;
import fittoring.application.presentation.dto.SignUpRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final OauthClientService oauthClientService;
    private final MemberOauthRepository memberOauthRepository;

    @Transactional
    public void register(SignUpRequest request) {
        validateDuplicateLoginId(request.loginId());
        validateDuplicatePhone(request.phone());
        Member member = createMember(request);
        memberRepository.save(member);
    }

    public void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException(BusinessErrorMessage.DUPLICATE_LOGIN_ID.getMessage());
        }
    }

    private void validateDuplicatePhone(String phone) {
        if (memberRepository.existsByPhone_Number(phone)) {
            throw new DuplicatePhoneException(BusinessErrorMessage.DUPLICATE_PHONE.getMessage());
        }
    }

    @Transactional
    public AuthTokenResponse login(String loginId, String password) {
        Member member = getMemberByLoginId(loginId);
        member.matchPassword(password);
        return getAuthorizedTokenResponse(member);
    }

    private AuthTokenResponse getAuthorizedTokenResponse(Member member) {
        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken();

        RefreshToken saveRefreshToken = new RefreshToken(
                refreshToken, LocalDateTime.now(), member
        );
        refreshTokenRepository.save(saveRefreshToken);

        return new AuthTokenResponse(accessToken, refreshToken, null);
    }

    @Transactional
    public AuthTokenResponse reissue(String refreshToken) {
        jwtProvider.validateToken(refreshToken);
        RefreshToken findRefreshToken = getRefreshToken(refreshToken);
        String newAccessToken = jwtProvider.createAccessToken(findRefreshToken.getMember().getId());
        String newRefreshToken = jwtProvider.createRefreshToken();
        findRefreshToken.update(newRefreshToken, LocalDateTime.now());

        return new AuthTokenResponse(newAccessToken, newRefreshToken, null);
    }

    private RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(() -> new InvalidTokenException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage()));
    }

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.LOGIN_ID_NOT_FOUND.getMessage()));
    }

    private Member createMember(SignUpRequest request) {
        return new Member(
                request.loginId(),
                request.gender(),
                request.name(),
                new Phone(request.phone()),
                Password.from(request.password())
        );
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteAllByMemberId(memberId);
    }

    public AuthTokenResponse kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = oauthClientService.requestKakaoToken(code);
        String kakaoAccessToken = tokenResponse.access_token();

        KakaoUserInfoResponse userInfoResponse = oauthClientService.requestKakaoId(kakaoAccessToken);
        Long kakaoId = userInfoResponse.id();

        Optional<MemberOauth> memberOauth = memberOauthRepository.findByProviderAndProviderMemberId(AuthProvider.KAKAO,
                String.valueOf(kakaoId));

        if (memberOauth.isPresent()) {
            Member member = memberOauth.get().getMember();
            return getAuthorizedTokenResponse(member);
        }

        String oauthSignUpToken = jwtProvider.createOauthSignUpToken(String.valueOf(kakaoId));
        return new AuthTokenResponse(null, null, oauthSignUpToken);
    }

    @Transactional
    public MemberOauth registerOauthMember(OauthSignUpRequest request, String oauthSignUpToken) {
        String oauthId = String.valueOf(jwtProvider.getSubjectFromPayloadBy(oauthSignUpToken));
        String randomLoginId = RandomStringUtils.randomAlphanumeric(20);
        String randomPw = RandomStringUtils.randomAlphanumeric(20);
        Member member = new Member(
                randomLoginId,
                request.gender(),
                request.name(),
                new Phone(request.phone()),
                Password.from(randomPw)
        );
        memberRepository.save(member);
        MemberOauth memberOauth = new MemberOauth(member, AuthProvider.KAKAO, oauthId);
        memberOauthRepository.save(memberOauth);
        return memberOauth;
    }

    public AuthTokenResponse loginOauthMember(MemberOauth memberOauth) {
        Member member = memberOauth.getMember();
        return getAuthorizedTokenResponse(member);
    }
}
