package fittoring.application.member.repository;

import fittoring.admin.repository.CustomMemberRepository;
import fittoring.domain.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends ListCrudRepository<Member, Long>, CustomMemberRepository {

    Optional<Member> findByLoginId(String loginId);

    @Query(value = "SELECT * FROM member WHERE is_deleted = true", nativeQuery = true)
    List<Member> findAllDeleted();

    @Query("SELECT m.name FROM Member m WHERE m.id = :id")
    Optional<String> findNameById(@Param("id") Long id);

    boolean existsByLoginId(String loginId);

    boolean existsByPhone_Number(String phoneNumber);

    Optional<Member> findByPhone_Number(String phoneNumber);
}
