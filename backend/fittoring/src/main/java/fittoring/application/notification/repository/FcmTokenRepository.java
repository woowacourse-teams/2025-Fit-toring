package fittoring.application.notification.repository;

import fittoring.domain.model.FcmToken;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface FcmTokenRepository extends ListCrudRepository<FcmToken, Long> {

    Optional<FcmToken> findByMemberId(Long memberId);
}
