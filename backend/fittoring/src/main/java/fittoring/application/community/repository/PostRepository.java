package fittoring.application.community.repository;

import fittoring.domain.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends ListCrudRepository<Post, Long>, CustomPostRepository {

    @Query(value = "SELECT * FROM post WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Post findDeletedById(@Param("id") Long id);
}
