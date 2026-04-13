package fittoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
@Entity
public class Image {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "`key`", columnDefinition = "TEXT")
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageVariant imageVariant;

    @Column(nullable = false)
    private Long relationId;

    @Column(length = 64)
    private String baseName;

    public Image(String url, ImageType imageType, ImageVariant imageVariant, Long relationId, String baseName) {
        this(null, url, null, imageType, imageVariant, relationId, baseName);
    }

    public Image(String url, ImageType imageType, Long relationId, String baseName) {
        this(null, url, null, imageType, ImageVariant.DEFAULT, relationId, baseName);
    }

    public static Image forKey(
            String key,
            ImageType imageType,
            ImageVariant imageVariant,
            Long relationId,
            String baseName
    ) {
        return new Image(null, null, key, imageType, imageVariant, relationId, baseName);
    }

    public static Image forKey(String key, ImageType imageType, Long relationId, String baseName) {
        return new Image(null, null, key, imageType, ImageVariant.DEFAULT, relationId, baseName);
    }

    public void updateUrlAndBaseName(String url, String baseName) {
        this.url = url;
        this.key = null;
        this.baseName = baseName;
    }

    public void updateKeyAndBaseName(String key, String baseName) {
        this.url = null;
        this.key = key;
        this.baseName = baseName;
    }
}
