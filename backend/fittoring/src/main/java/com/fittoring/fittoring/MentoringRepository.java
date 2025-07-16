package com.fittoring.fittoring;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRepository extends ListCrudRepository<Mentoring, Long> {

}
