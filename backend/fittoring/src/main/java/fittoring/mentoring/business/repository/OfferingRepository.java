package fittoring.mentoring.business.repository;

import fittoring.mentoring.business.model.Offering;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends ListCrudRepository<Offering, Long> {
}
