package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Mentoring;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRepository extends ListCrudRepository<Mentoring, Long> {

    @Query("""
            SELECT m
              FROM CategoryMentoring cm
              JOIN cm.mentoring m
             WHERE cm.category.title IN (
                      COALESCE(:title1, '__NULL__'),
                      COALESCE(:title2, '__NULL__'),
                      COALESCE(:title3, '__NULL__')
                  )
             GROUP BY m.id
             HAVING COUNT (DISTINCT cm.category.title) =
                (CASE WHEN :title1 IS NOT NULL THEN 1 ELSE 0 END +
                 CASE WHEN :title2 IS NOT NULL THEN 1 ELSE 0 END +
                 CASE WHEN :title3 IS NOT NULL THEN 1 ELSE 0 END)
            """)
    List<Mentoring> findAllMentoringWithFilter(
            @Param("title1") String categoryTitle1,
            @Param("title2") String categoryTitle2,
            @Param("title3") String categoryTitle3
    );
}
