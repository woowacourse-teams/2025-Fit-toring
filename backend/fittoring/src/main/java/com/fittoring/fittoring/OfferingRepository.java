package com.fittoring.fittoring;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends ListCrudRepository<Offering, Long> {
}
