package fittoring.mentoring.business.model;

import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.model.password.PasswordConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Convert(converter = PasswordConverter.class)
    @Column(nullable = false)
    private Password password;

    private Member(String loginId, String gender, String name, String phone, Password password) {
        this(null, loginId, gender, name, phone, password);
    }

    public static Member of(String loginId, String gender, String name, String phone, String password) {
        return new Member(loginId, gender, name, phone, Password.createEncrypt(password));
    }

    public String getPassword() {
        return password.getPassword();
    }
}
