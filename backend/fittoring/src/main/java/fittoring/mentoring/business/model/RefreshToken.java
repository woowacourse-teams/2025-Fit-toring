package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String tokenValue;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    @ManyToOne
    private Member member;

    public RefreshToken(String tokenValue, LocalDateTime createAt, Member member) {
        this(null, tokenValue, createAt, member);
    }

    public void update(String tokenValue, LocalDateTime createAt) {
        this.tokenValue = tokenValue;
        this.createAt = createAt;
    }
}
