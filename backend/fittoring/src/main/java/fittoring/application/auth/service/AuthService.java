package fittoring.application.auth.service;

import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.presentation.dto.response.KakaoTokenResponse;
import fittoring.application.auth.presentation.dto.response.KakaoUserInfoResponse;
import fittoring.application.auth.repository.MemberOauthRepository;
import fittoring.application.auth.repository.RefreshTokenRepository;
import fittoring.application.auth.service.dto.AuthTokenDto;
import fittoring.application.auth.service.dto.LoginInfoDto;
import fittoring.application.auth.service.dto.RegisterMemberDto;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicateLoginIdException;
import fittoring.application.exception.DuplicatePhoneException;
import fittoring.application.exception.InvalidTokenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.member.service.dto.RegisterOAuthDto;
import fittoring.domain.model.AuthProvider;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberOauth;
import fittoring.domain.model.Phone;
import fittoring.domain.model.RefreshToken;
import fittoring.domain.model.password.Password;
import fittoring.infrastructure.OauthClientService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String LOGIN_ID_NOT_FOUND_MESSAGE = BusinessErrorMessage.LOGIN_ID_NOT_FOUND.getMessage();
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtExtractor jwtExtractor;
    private final JwtProvider jwtProvider;
    private final OauthClientService oauthClientService;
    private final MemberOauthRepository memberOAuthRepository;
    private final PhoneVerificationService phoneVerificationService;

    @Transactional
    public void register(RegisterMemberDto dto) {
        validateDuplicateLoginId(dto.loginId());
        validateDuplicatePhone(dto.phoneNumber());
        phoneVerificationService.checkVerificationStatus(new Phone(dto.phoneNumber()));
        Member member = createMember(dto);
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

    private Member createMember(RegisterMemberDto dto) {
        return new Member(
                dto.loginId(),
                dto.gender(),
                dto.name(),
                new Phone(dto.phoneNumber()),
                Password.from(dto.password())
        );
    }

    @Transactional
    public LoginInfoDto login(String loginId, String password) {
        Member member = getMemberByLoginId(loginId);
        member.matchPassword(password);
        AuthTokenDto authTokenDto = getAuthorizedTokenResponse(member);

        return new LoginInfoDto(member.getId(), authTokenDto);
    }

    private AuthTokenDto getAuthorizedTokenResponse(Member member) {
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken();

        RefreshToken saveRefreshToken = new RefreshToken(
                refreshToken, LocalDateTime.now(), member
        );
        refreshTokenRepository.save(saveRefreshToken);

        return new AuthTokenDto(accessToken, refreshToken, null);
    }

    @Transactional
    public AuthTokenDto reissue(String refreshToken) {
        jwtProvider.validateToken(refreshToken);
        RefreshToken findRefreshToken = getRefreshToken(refreshToken);
        Member memberByRT = findRefreshToken.getMember();
        String newAccessToken = jwtProvider.createAccessToken(memberByRT.getId(), memberByRT.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken();
        findRefreshToken.update(newRefreshToken, LocalDateTime.now());

        return new AuthTokenDto(newAccessToken, newRefreshToken, null);
    }

    private RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(() -> new InvalidTokenException(BusinessErrorMessage.TOKEN_NOT_FOUND.getMessage()));
    }

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteAllByMemberId(memberId);
    }

    public LoginInfoDto kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = oauthClientService.requestKakaoToken(code);
        String kakaoAccessToken = tokenResponse.access_token();
        KakaoUserInfoResponse userInfoResponse = oauthClientService.requestKakaoId(kakaoAccessToken);
        Long kakaoId = userInfoResponse.id();
        Optional<MemberOauth> memberOauth = memberOAuthRepository.findByProviderAndProviderMemberId(
                AuthProvider.KAKAO,
                String.valueOf(kakaoId)
        );
        if (memberOauth.isPresent()) {
            return allowOauthLogin(memberOauth);
        }
        return allowOauthRegistration(kakaoId);
    }

    private LoginInfoDto allowOauthLogin(Optional<MemberOauth> memberOauth) {
        Member member = memberOauth.get().getMember();
        AuthTokenDto authTokenDto = getAuthorizedTokenResponse(member);
        return new LoginInfoDto(member.getId(), authTokenDto);
    }

    private LoginInfoDto allowOauthRegistration(Long kakaoId) {
        String oauthSignUpToken = jwtProvider.createOauthSignUpToken(String.valueOf(kakaoId));
        AuthTokenDto authTokenDto = new AuthTokenDto(null, null, oauthSignUpToken);
        return new LoginInfoDto(null, authTokenDto);
    }

    @Transactional
    public RegisterOAuthDto registerOauthMember(OauthSignUpRequest request, String oauthSignUpToken) {
        TokenPayload payload = jwtProvider.getSubjectFromPayloadBy(oauthSignUpToken);
        String oauthId = String.valueOf(payload.sub());
        Member member = memberRepository.findByPhone_Number(request.phone())
                .orElseGet(() -> {
                    Member newMember = getRandomIdPwMember(request);
                    memberRepository.save(newMember);
                    return newMember;
                });
        MemberOauth memberOAuth = new MemberOauth(member, AuthProvider.KAKAO, oauthId);
        memberOAuthRepository.save(memberOAuth);
        return new RegisterOAuthDto(member.getId(), getAuthorizedTokenResponse(member));
    }

    private Member getRandomIdPwMember(OauthSignUpRequest request) {
        String randomLoginId = RandomStringUtils.randomAlphanumeric(20);
        validateDuplicateLoginId(randomLoginId);
        String randomPw = RandomStringUtils.randomAlphanumeric(20);

        return new Member(
                randomLoginId,
                request.gender(),
                request.name(),
                new Phone(request.phone()),
                Password.from(randomPw)
        );
    }

    @Transactional(readOnly = true)
    public String findLoginId(String name, String phoneNumber) {
        Member member = memberRepository.findByPhone_Number(phoneNumber)
                .orElseThrow(() -> new MemberNotFoundException(LOGIN_ID_NOT_FOUND_MESSAGE));
        if (!member.getName().equals(name)) {
            throw new MemberNotFoundException(LOGIN_ID_NOT_FOUND_MESSAGE);
        }
        return member.getLoginId();
    }

    @Transactional
    public Member resetPassword(String loginId, String phoneNumber, String password) {
        phoneVerificationService.checkVerificationStatus(new Phone(phoneNumber));
        Member member = getMemberByLoginId(loginId);
        member.updatePassword(password);
        return member;
    }

    public TokenPayload extractMemberId(String accessToken) {
        return jwtProvider.getSubjectFromPayloadBy(accessToken);
    }
}
