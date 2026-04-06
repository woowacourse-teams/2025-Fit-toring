package fittoring.application.community.repository;

import fittoring.domain.model.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends ListCrudRepository<Comment, Long> {

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
}
