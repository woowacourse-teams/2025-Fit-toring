package fittoring.application.image.repository;

import fittoring.domain.model.ImageSession;
import org.springframework.data.repository.ListCrudRepository;

public interface ImageSessionRepository extends ListCrudRepository<ImageSession, Long> {
}
