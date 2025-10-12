package fittoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image_session")
@Entity
public class ImageSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_name", length = 64, nullable = false)
    private String baseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageVariant imageVariant;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ImageSession(
            String baseName,
            ImageType imageType,
            ImageVariant imageVariant,
            String url,
            LocalDateTime createdAt
    ) {
        this(null, baseName, imageType, imageVariant, url, createdAt);
    }

    public void update(String url, ImageType imageType) {
        this.url = url;
        this.imageType = imageType;
    }
}
