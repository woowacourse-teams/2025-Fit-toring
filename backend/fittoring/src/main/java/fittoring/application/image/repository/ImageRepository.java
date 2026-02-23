package fittoring.application.image.repository;

import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
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
public interface ImageRepository extends ListCrudRepository<Image, Long> {

    boolean existsByBaseNameAndImageVariant(String baseName, ImageVariant imageVariant);

    Optional<Image> findByImageTypeAndRelationIdAndImageVariant(
            ImageType imageType,
            Long relationId,
            ImageVariant imageVariant
    );

    Optional<Image> findByImageTypeAndBaseNameAndImageVariant(
            ImageType imageType,
            String baseName,
            ImageVariant imageVariant
    );

    List<Image> findByImageTypeAndRelationIdIn(ImageType imageType, Collection<Long> relationIds);

    @Query("""
              SELECT i
                FROM Image i
               WHERE i.relationId = :relationId
                 AND i.imageType = :type
                 AND i.imageVariant IN (:prefer, :fallback)
            """)
    List<Image> findThumbnailByImageTypeAndRelationId(
            @Param("relationId") Long relationId,
            @Param("type") ImageType imageType,
            @Param("prefer") ImageVariant prefer,
            @Param("fallback") ImageVariant fallback
    );

    @Query("""
              SELECT i
              FROM Image i
              WHERE i.relationId IN :relationIds
                  AND i.imageType = :imageType
            """)
    List<Image> findByRelationIdsAndImageType(
            @Param("relationIds") List<Long> relationIds,
            @Param("imageType") ImageType imageType
    );

    @Query("""
              SELECT i.relationId
              FROM Image i
              WHERE i.relationId IN :relationIds
                  AND i.imageType = :imageType
                  AND i.imageVariant = :imageVariant
            """)
    List<Long> findRelationIdsInByImageTypeAndVariant(
            @Param("relationIds") List<Long> relationIds,
            @Param("imageType") ImageType imageType,
            @Param("imageVariant") ImageVariant imageVariant
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = """
            INSERT INTO image (url, image_type, image_variant, relation_id, base_name)
            VALUES (:url, :imageType, :imageVariant, :relationId, :baseName)
            ON DUPLICATE KEY UPDATE
                url = VALUES(url),
                base_name = VALUES(base_name)
            """, nativeQuery = true)
    void upsert(
            @Param("url") String url,
            @Param("imageType") String imageType,
            @Param("imageVariant") String imageVariant,
            @Param("relationId") Long relationId,
            @Param("baseName") String baseName
    );

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("""
            DELETE FROM Image i
            WHERE i.imageType = :type and i.relationId = :relationId
            """)
    void deleteByImageTypeAndRelationId(
            @Param("type") ImageType imageType,
            @Param("relationId") Long relationId
    );
}
