package fittoring.application.community.repository;

import fittoring.domain.model.PostLike;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostLikeRepository extends ListCrudRepository<PostLike, Long> {

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value = """
            INSERT IGNORE INTO post_like (post_id, actor_key_hash, created_at)
            VALUES (:postId, :actorKeyHash, NOW(6))
            """, nativeQuery = true)
    int insertIgnore(
            @Param("postId") Long postId,
            @Param("actorKeyHash") String actorKeyHash
    );

    long deleteByPostIdAndActorKeyHashValue(Long postId, String actorKeyHash);

    boolean existsByPostIdAndActorKeyHashValue(Long postId, String actorKeyHash);
}
