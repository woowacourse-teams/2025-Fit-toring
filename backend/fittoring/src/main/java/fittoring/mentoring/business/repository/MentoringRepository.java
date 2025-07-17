package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Mentoring;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRepository extends ListCrudRepository<Mentoring, Long> {

}
