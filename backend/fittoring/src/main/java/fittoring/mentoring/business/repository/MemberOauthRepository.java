package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.AuthProvider;
import fittoring.mentoring.business.model.MemberOauth;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberOauthRepository extends ListCrudRepository<MemberOauth, Long> {

    Optional<MemberOauth> findByProviderAndProviderMemberId(AuthProvider provider, String providerMemberId);
}
