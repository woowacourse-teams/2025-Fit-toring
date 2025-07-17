package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Image;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ListCrudRepository<Image, Long> {
}
