package fittoring.application.business.repository;

import fittoring.application.business.model.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends ListCrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenValue(String token);

    void deleteAllByMemberId(Long memberId);
}
