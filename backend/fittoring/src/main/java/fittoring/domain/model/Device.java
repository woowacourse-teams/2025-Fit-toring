package fittoring.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "device")
@Entity
public class Device {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Getter
    @Column(nullable = false, unique = true)
    private String hardwareId;

    @Getter
    @Column(nullable = false)
    private String pushToken;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Device(Member member, String hardwareId, String pushToken) {
        this(null, member, hardwareId, pushToken, null);
    }

    public void updateToken(String token) {
        this.pushToken = token;
    }
}
