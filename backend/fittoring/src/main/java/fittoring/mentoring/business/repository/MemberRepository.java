package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberRepository extends ListCrudRepository<Member, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByPhone_Number(String phone);

    Optional<Member> findByLoginId(String loginId);

    @Query(value = "SELECT * FROM member WHERE is_deleted = true", nativeQuery = true)
    List<Member> findAllDeleted();
}
