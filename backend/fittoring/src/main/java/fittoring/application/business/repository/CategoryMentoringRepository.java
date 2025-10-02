package fittoring.application.business.repository;

import fittoring.application.business.model.CategoryMentoring;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMentoringRepository extends ListCrudRepository<CategoryMentoring, Long> {

    @Query("""
                SELECT c.title
                FROM CategoryMentoring cm
                  JOIN FETCH Category c ON cm.category.id = c.id
                WHERE cm.mentoring.id = :mentoringId
            """)
    List<String> findTitlesByMentoringId(Long mentoringId);

    void deleteByMentoringId(Long mentoringId);

    @Query(value = "SELECT * FROM category_mentoring WHERE is_deleted = true", nativeQuery = true)
    List<CategoryMentoring> findAllDeleted();
}
