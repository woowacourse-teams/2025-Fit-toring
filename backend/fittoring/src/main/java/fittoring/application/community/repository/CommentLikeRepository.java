package fittoring.application.community.repository;

import fittoring.domain.model.CommentLike;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentLikeRepository extends ListCrudRepository<CommentLike, Long> {

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value = """
            INSERT IGNORE INTO comment_like (comment_id, actor_key_hash, created_at)
            VALUES (:commentId, :actorKeyHash, NOW(6))
            """, nativeQuery = true)
    int insertIgnore(
            @Param("commentId") Long commentId,
            @Param("actorKeyHash") String actorKeyHash
    );

    long deleteByCommentIdAndActorKeyHashValue(Long commentId, String actorKeyHash);

    @Query("""
            SELECT cl.comment.id
            FROM CommentLike cl
            WHERE cl.comment.id IN :commentIds
                AND cl.actorKeyHash.value = :actorKeyHash
            """)
    List<Long> findLikedCommentIds(
            @Param("commentIds") Collection<Long> commentIds,
            @Param("actorKeyHash") String actorKeyHash
    );
}
