package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.DuplicateIdException;
import fittoring.mentoring.business.model.Member;
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
        Member member = request.toEntity();
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public void validateDuplicateId(String id) {
        if (memberRepository.existsByLoginId(id)) {
            throw new DuplicateIdException("이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
        }
    }
}
