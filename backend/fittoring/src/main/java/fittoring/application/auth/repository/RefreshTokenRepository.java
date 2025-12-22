package fittoring.application.auth.repository;

import fittoring.domain.model.RefreshToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends ListCrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenValue(String token);

    void deleteAllByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken rt SET rt.tokenValue = :newTokenValue, rt.createAt = :issuedAt WHERE rt.tokenValue = :oldTokenValue")
    int updateToken(@Param("oldTokenValue") String oldTokenValue, @Param("newTokenValue") String newTokenValue,
                    @Param("issuedAt") LocalDateTime issuedAt);
}
