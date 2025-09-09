package fittoring.mentoring.business.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.util.DbCleaner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, JpaConfiguration.class})
@DataJpaTest
class MentoringRepositoryTest {

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("멘토링을 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void mentoringSoftDelete() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        Mentoring mentoring = mentoringRepository.save(
                new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개")
        );

        //when
        mentoringRepository.delete(mentoring);

        //then
        List<Mentoring> deletedMentoring = mentoringRepository.findAllDeleted();
        Mentoring actual = deletedMentoring.getFirst();

        assertSoftly(softly -> {
            softly.assertThat(actual.isDeleted()).isTrue();
            softly.assertThat(actual.getDeletedAt()).isNotNull();
        });
    }

    @DisplayName("멘토링을 조회할 때 상태 상태의 멘토링은 제외하고 조회한다.")
    @Test
    void findMentorings() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        mentoringRepository.save(
                new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개")
        );

        Member mentor2 = memberRepository.save(
                new Member("id2", "MALE", "이트레이너", new Phone("010-1234-5678"), Password.from("pw"))
        );

        Mentoring mentoring2 = mentoringRepository.save(
                new Mentoring(mentor2, 5000, 5, "컨텐츠컨텐츠", "자기소개자기소개")
        );

        mentoringRepository.delete(mentoring2);

        //when
        List<Mentoring> actual = mentoringRepository.findAll();

        //then
        assertThat(actual).hasSize(1);
    }
}
