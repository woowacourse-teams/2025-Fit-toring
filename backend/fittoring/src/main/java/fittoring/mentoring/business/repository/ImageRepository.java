package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ListCrudRepository<Image, Long> {

    Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long relationId);

    @Query("""
            SELECT i
            FROM Image i
            WHERE i.imageType = :imageType AND i.relationId IN :relationIds
            """)
    List<Image> findAllByImageTypeAndRelationIds(
            @Param("imageType") ImageType imageType,
            @Param("relationIds") List<Long> ids
    );

    void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId);
}
