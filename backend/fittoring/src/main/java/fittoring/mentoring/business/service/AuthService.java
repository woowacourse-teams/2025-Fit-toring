package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.DuplicateLoginIdException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    public void register(SignUpRequest request) {
        validateDuplicateLoginId(request.loginId());
        Member member = createMember(request);
        memberRepository.save(member);
    }

    public void login(String loginId, String password) {
    }

    public void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException(BusinessErrorMessage.DUPLICATE_LOGIN_ID.getMessage());
        }
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
