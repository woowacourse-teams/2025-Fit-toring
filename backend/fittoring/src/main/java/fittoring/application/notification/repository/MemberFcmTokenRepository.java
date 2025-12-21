package fittoring.application.notification.repository;

import fittoring.domain.model.MemberFcmToken;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberFcmTokenRepository extends ListCrudRepository<MemberFcmToken, Long> {

    Optional<MemberFcmToken> findByMemberId(Long memberId);
}
