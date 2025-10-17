package fittoring.application.mentoring.repository;

import fittoring.domain.model.Category;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, Long> {

    Optional<Category> findByTitle(String title);

    boolean existsByTitle(String title);
}
