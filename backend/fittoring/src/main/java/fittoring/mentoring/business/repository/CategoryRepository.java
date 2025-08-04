package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Category;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, Long> {

    boolean existsByTitle(String title);

    Optional<Category> findByTitle(String title);
}
