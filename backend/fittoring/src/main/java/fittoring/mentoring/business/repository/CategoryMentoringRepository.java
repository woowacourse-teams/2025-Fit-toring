package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.CategoryMentoring;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMentoringRepository extends ListCrudRepository<CategoryMentoring, Long> {

    @Query("""
                SELECT cm
                FROM CategoryMentoring cm 
                JOIN FETCH Mentoring m ON cm.mentoring = m 
                WHERE m.id = :mentoringId
            """)
    List<CategoryMentoring> findAllByMentoringId(Long mentoringId);

    @Query("""
                SELECT c.title
                FROM CategoryMentoring cm INNER JOIN Category c ON cm.category.id = c.id
                WHERE cm.mentoring.id = :mentoringId
            """)
    List<String> findTitlesByMentoringId(Long mentoringId);

    void deleteByMentoringId(Long mentoringId);

    @Query(value = "SELECT * FROM category_mentoring WHERE is_deleted = true", nativeQuery = true)
    List<CategoryMentoring> findAllDeleted();
}
