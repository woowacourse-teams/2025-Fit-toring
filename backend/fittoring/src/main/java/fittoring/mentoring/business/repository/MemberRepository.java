package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Member;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberRepository extends ListCrudRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByPhone_Number(String phone);
}
