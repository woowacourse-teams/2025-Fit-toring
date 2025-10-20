package fittoring.application.mentoring.repository;

import fittoring.domain.model.CategoryMentoring;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMentoringRepository extends ListCrudRepository<CategoryMentoring, Long> {

    @Query(value = "SELECT * FROM category_mentoring WHERE id = :id AND is_deleted = true;", nativeQuery = true)
    CategoryMentoring findDeletedById(@Param("id") Long id);

    @Query("""
                SELECT c.title
                FROM CategoryMentoring cm
                  JOIN FETCH Category c ON cm.category.id = c.id
                WHERE cm.mentoring.id = :mentoringId
            """)
    List<String> findTitlesByMentoringId(Long mentoringId);

    @Query(value = "SELECT * FROM category_mentoring WHERE is_deleted = true", nativeQuery = true)
    List<CategoryMentoring> findAllDeleted();

    void deleteByMentoringId(Long mentoringId);
}
