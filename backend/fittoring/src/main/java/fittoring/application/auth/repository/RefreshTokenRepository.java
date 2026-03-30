package fittoring.application.auth.repository;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(String tokenValue, Long memberId, long ttlMillis);

    Optional<Long> findMemberIdByTokenValue(String tokenValue);

    void deleteByTokenValue(String tokenValue);

    void deleteAllByMemberId(Long memberId);
}
