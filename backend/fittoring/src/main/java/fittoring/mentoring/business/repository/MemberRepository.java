package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Member;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberRepository extends ListCrudRepository<Member, Long> {

    boolean existsByLoginId(String loginId);
}
