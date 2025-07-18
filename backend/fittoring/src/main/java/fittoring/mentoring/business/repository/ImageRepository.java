package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ListCrudRepository<Image, Long> {

    Optional<Image> findByImageTypeAndRelationId(ImageType imageType, Long id);
}
