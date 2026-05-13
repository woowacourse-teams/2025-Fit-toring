package fittoring.application.community.repository;

import fittoring.domain.model.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends ListCrudRepository<Post, Long>, CustomPostRepository {

    @Query(value = "SELECT * FROM post WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Post findDeletedById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE post SET like_count = like_count + 1 WHERE id = :id AND is_deleted = false", nativeQuery = true)
    int increaseLikeCount(@Param("id") Long id);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            value = "UPDATE post SET like_count = like_count - 1 WHERE id = :id AND is_deleted = false AND like_count > 0",
            nativeQuery = true
    )
    int decreaseLikeCount(@Param("id") Long id);

    @Query(value = "SELECT like_count FROM post WHERE id = :id AND is_deleted = false", nativeQuery = true)
    Optional<Integer> findLikeCountById(@Param("id") Long id);
}
