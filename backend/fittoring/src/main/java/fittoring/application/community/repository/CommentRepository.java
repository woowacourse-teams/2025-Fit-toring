package fittoring.application.community.repository;

import fittoring.domain.model.Comment;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends ListCrudRepository<Comment, Long> {

    long countByPostId(Long postId);

    List<Comment> findAllByPostIdIn(Collection<Long> postIds);

    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.post p
            WHERE p.id = :postId
            ORDER BY
                CASE WHEN c.rootId IS NULL THEN c.id ELSE c.rootId END ASC,
                CASE WHEN c.parentId IS NULL THEN 0 ELSE 1 END ASC,
                c.createdAt ASC,
                c.id ASC
            """)
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    @Query(value = "SELECT * FROM comment WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Comment findDeletedById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(
            value = "UPDATE comment SET like_count = like_count + 1 WHERE id = :id AND is_deleted = false",
            nativeQuery = true
    )
    int increaseLikeCount(@Param("id") Long id);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            value = "UPDATE comment SET like_count = like_count - 1 WHERE id = :id AND is_deleted = false AND like_count > 0",
            nativeQuery = true
    )
    int decreaseLikeCount(@Param("id") Long id);

    @Query(value = "SELECT like_count FROM comment WHERE id = :id AND is_deleted = false", nativeQuery = true)
    Optional<Integer> findLikeCountById(@Param("id") Long id);
}
