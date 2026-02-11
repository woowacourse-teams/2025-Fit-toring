package fittoring.domain.model;

import fittoring.domain.model.password.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "member")
@Entity
public class Member {

    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

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

    @Getter
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Getter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Member(String loginId, Gender gender, String name, Phone phone, Password password) {
        this(null, loginId, gender, name, phone, password, MemberRole.MENTEE, false, null);
    }

    public Member(
            String loginId,
            Gender gender,
            String name,
            Phone phone,
            Password password,
            MemberRole role
    ) {
        this(null, loginId, gender, name, phone, password, role, false, null);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phone = new Phone(phoneNumber);
    }

    public void updatePassword(String password) {
        this.password = Password.from(password);
    }

    public void registerAsMentor() {
        if (this.role != MemberRole.ADMIN) {
            this.role = MemberRole.MENTOR;
        }
    }

    public void matchPassword(String password) {
        this.password.validateMatches(password);
    }

    public boolean isMentee() {
        return MemberRole.isMentee(this.role);
    }

    public boolean isSameIdWith(Long memberId) {
        return this.id.equals(memberId);
    }

    public boolean isNotAdmin() {
        return this.role != MemberRole.ADMIN;
    }

    public String getPasswordValue() {
        return password.getValue();
    }

    public String getPhoneNumber() {
        return this.phone.getNumber();
    }
}
