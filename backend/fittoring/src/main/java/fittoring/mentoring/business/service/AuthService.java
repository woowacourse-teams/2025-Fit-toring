package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.DuplicateIdException;
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
        validateDuplicateId(request.loginId());
        Member member = createMember(request);
        memberRepository.save(member);
    }

    public void validateDuplicateId(String id) {
        if (memberRepository.existsByLoginId(id)) {
            throw new DuplicateIdException("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
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
