package fittoring.mentoring.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mentoring")
@Entity
public class Mentoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false)
    private int price;

    private Integer career;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String introduction;

    @Getter
    @Column(nullable = false)
    private boolean isDeleted;

    @Getter
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member mentor;

    public Mentoring(
            Member member,
            int price,
            Integer career,
            String content,
            String introduction
    ) {
        this(null, price, career, content, introduction, false, null, member);
    }

    public void modify(
            int price,
            Integer career,
            String content,
            String introduction
    ) {
        this.price = price;
        this.career = career;
        this.content = content;
        this.introduction = introduction;
    }

    public boolean isCreatedByMember(Long memberId) {
        return this.mentor.isSameIdWith(memberId);
    }

    public String getMentorName() {
        return this.mentor.getName();
    }
}
