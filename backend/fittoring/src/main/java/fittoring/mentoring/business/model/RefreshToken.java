package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false)
    private Long memberId;

    @Getter
    @Column(nullable = false)
    private String token;

    @Getter
    @Column(nullable = false)
    private LocalDateTime createAt;

    public RefreshToken(Long memberId, String token, LocalDateTime createAt) {
        this(null, memberId, token, createAt);
    }

    public void update(String token, LocalDateTime createAt) {
        this.token = token;
        this.createAt = createAt;
    }
}
