package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.CategoryMentoring;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMentoringRepository extends ListCrudRepository<CategoryMentoring, Long> {

    List<CategoryMentoring> findAllByMentoringId(Long mentoringId);

    @Query("""
        SELECT c.title
        FROM CategoryMentoring cm INNER JOIN Category c
        WHERE cm.mentoring.id = :mentoringId
    """)
    List<String> findTitleByMentoringId(Long mentoringId);
}
