package fittoring.application.notification.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.repository.FcmTokenRepository;
import fittoring.domain.model.FcmToken;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void upsertFcmToken(Long memberId, String token) {
        Optional<FcmToken> tokenOptional = fcmTokenRepository.findByMemberId(memberId);
        if (tokenOptional.isEmpty()) {
            saveNewFcmToken(memberId, token);
            return;
        }
        renewFcmToken(tokenOptional.get(), token);
    }

    private void saveNewFcmToken(Long memberId, String token) {
        validateMemberExists(memberId);
        FcmToken fcmToken = new FcmToken(memberId, token);
        fcmTokenRepository.save(fcmToken);
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage());
        }
    }

    private void renewFcmToken(FcmToken fcmToken, String newToken) {
        fcmToken.updateToken(newToken);
        fcmTokenRepository.save(fcmToken);
    }
}
