package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.CategoryMentoring;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMentoringRepository extends ListCrudRepository<CategoryMentoring, Long> {

    List<CategoryMentoring> findAllByMentoringId(Long mentoringId);
}
