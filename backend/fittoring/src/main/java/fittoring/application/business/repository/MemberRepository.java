package fittoring.application.business.repository;

import fittoring.application.business.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberRepository extends ListCrudRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    @Query(value = "SELECT * FROM member WHERE is_deleted = true", nativeQuery = true)
    List<Member> findAllDeleted();

    List<Member> findAllByOrderByRoleAsc();

    boolean existsByLoginId(String loginId);

    boolean existsByPhone_Number(String phone);
}
