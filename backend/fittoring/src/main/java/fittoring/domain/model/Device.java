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

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Getter
    @Column(nullable = false, unique = true)
    private String hardwareId;

    @Getter
    @Column(nullable = false)
    private String pushToken;

    @Getter
    @Column(nullable = false)
    private boolean isPushEnabled;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Device(Member member, String hardwareId, String pushToken) {
        this(null, member, hardwareId, pushToken, true, null);
    }

    public void updateToken(String token) {
        this.pushToken = token;
    }

    public void enablePush() {
        this.isPushEnabled = true;
    }

    public void disablePush(){
        this.isPushEnabled = false;
    }
}
