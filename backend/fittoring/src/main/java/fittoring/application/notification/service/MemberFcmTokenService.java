package fittoring.application.notification.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.MemberFcmTokenRepository;
import fittoring.domain.model.MemberFcmToken;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberFcmTokenService {

    private final MemberFcmTokenRepository memberFcmTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void upsertFcmToken(Long memberId, String token) {
        Optional<MemberFcmToken> tokenOptional = memberFcmTokenRepository.findByMemberId(memberId);
        if (tokenOptional.isEmpty()) {
            saveNewFcmToken(memberId, token);
            return;
        }
        renewFcmToken(tokenOptional.get(), token);
    }

    private void saveNewFcmToken(Long memberId, String token) {
        validateMemberExists(memberId);
        MemberFcmToken memberFcmToken = new MemberFcmToken(memberId, token);
        memberFcmTokenRepository.save(memberFcmToken);
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
        }
    }

    private void renewFcmToken(MemberFcmToken memberFcmToken, String newToken) {
        memberFcmToken.updateToken(newToken);
        memberFcmTokenRepository.save(memberFcmToken);
    }
}
