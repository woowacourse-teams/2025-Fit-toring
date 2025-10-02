package fittoring.application.business.model;

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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageVariant imageVariant;

    @Column(nullable = false)
    private Long relationId;

    public Image(String url, ImageType imageType, ImageVariant imageVariant, Long relationId) {
        this(null, url, imageType, imageVariant, relationId);
    }

    public Image(String url, ImageType imageType, Long relationId) {
        this(null, url, imageType, ImageVariant.DEFAULT, relationId);
    }
}
