package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ListCrudRepository<Image, Long> {

    Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long relationId);

    void deleteByImageTypeAndRelationId(ImageType imageType, Long relationId);

    List<Image> findAllByImageTypeAndRelationIdIn(ImageType imageType, Collection<Long> relationIds);
}
