package fittoring.application.image.repository;

import fittoring.domain.model.ImageSession;
import fittoring.domain.model.ImageVariant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ImageSessionRepository extends ListCrudRepository<ImageSession, Long> {

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT INTO image_session (base_name, image_type, image_variant, url)
            VALUES (:baseName, :imageType, :imageVariant, :url)
            ON DUPLICATE KEY UPDATE
              url = VALUES(url),
              image_type = VALUES(image_type)
            """, nativeQuery = true)
    void upsert(
            @Param("baseName") String baseName,
            @Param("imageType") String imageType,
            @Param("imageVariant") String imageVariant,
            @Param("url") String url
    );

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteByBaseNameAndImageVariant(String baseName, ImageVariant imageVariant);
}
