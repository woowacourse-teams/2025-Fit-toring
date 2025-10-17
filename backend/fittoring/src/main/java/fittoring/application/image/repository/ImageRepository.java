package fittoring.application.image.repository;

import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends ListCrudRepository<Image, Long> {

    Optional<Image> findByImageTypeAndRelationIdAndImageVariant(
            ImageType imageType,
            Long relationId,
            ImageVariant imageVariant
    );

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

    void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId);


    List<Image> findByImageTypeAndRelationIdIn(ImageType imageType, Collection<Long> relationIds);
}
