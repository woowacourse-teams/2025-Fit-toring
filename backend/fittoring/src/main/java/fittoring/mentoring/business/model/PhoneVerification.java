package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "phone_verification")
@Entity
public class PhoneVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Phone phone;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    public PhoneVerification(Phone phone, String code, LocalDateTime expireAt) {
        this(null, phone, code, expireAt);
    }

    public String getPhoneNumber() {
        return this.phone.getNumber();
    }
}
