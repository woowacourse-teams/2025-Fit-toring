package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.RefreshToken;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends ListCrudRepository<RefreshToken, Long> {
}
