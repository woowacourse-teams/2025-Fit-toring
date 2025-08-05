package fittoring.mentoring.business.model;

import fittoring.mentoring.business.model.password.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String gender;

    @Getter
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Embedded
    @Column(nullable = false)
    private Password password;

    public Member(String loginId, String gender, String name, String phone, Password password) {
        this(null, loginId, gender, name, phone, password);
    }

    public void matchPassword(String password) {
        this.password.validateMatches(password);
    }

    public String getPassword() {
        return password.getPassword();
    }
}
