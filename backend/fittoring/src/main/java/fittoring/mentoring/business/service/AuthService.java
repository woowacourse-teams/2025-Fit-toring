package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.DuplicateLoginIdException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.RefreshToken;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.RefreshTokenRepository;
import fittoring.mentoring.presentation.dto.AuthTokenResponse;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void register(SignUpRequest request) {
        validateDuplicateLoginId(request.loginId());
        Member member = createMember(request);
        memberRepository.save(member);
    }

    public void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException(BusinessErrorMessage.DUPLICATE_LOGIN_ID.getMessage());
        }
    }

    @Transactional
    public AuthTokenResponse login(String loginId, String password) {
        Member member = getMemberByLoginId(loginId);
        member.matchPassword(password);
        String accessToken = jwtProvider.createToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken();

        RefreshToken saveRefreshToken = new RefreshToken(
                member.getId(),
                refreshToken,
                LocalDateTime.now()
        );
        refreshTokenRepository.save(saveRefreshToken);

        return new AuthTokenResponse(accessToken, refreshToken);
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
                request.phone(),
                Password.from(request.password())
        );
    }
}
