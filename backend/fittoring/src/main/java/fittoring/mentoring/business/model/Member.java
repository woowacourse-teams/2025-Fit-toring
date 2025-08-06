package fittoring.mentoring.business.model;

import fittoring.mentoring.business.model.password.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Phone phone;

    @Embedded
    @Column(nullable = false)
    private Password password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    public Member(String loginId, String gender, String name, Phone phone, Password password) {
        this(null, loginId, gender, name, phone, password, MemberRole.MENTEE);
    }

    public void matchPassword(String password) {
        this.password.validateMatches(password);
    }

    public void registerAsMentor() {
        if (this.role != MemberRole.ADMIN) {
            this.role = MemberRole.MENTOR;
        }
    }

    public String getPassword() {
        return password.getPassword();
    }

    public String getPhoneNumber() {
        return this.phone.getNumber();
    }
}
