package fittoring.mentoring.business.service;

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
        Member member = request.toEntity();
        memberRepository.save(member);
    }
}
