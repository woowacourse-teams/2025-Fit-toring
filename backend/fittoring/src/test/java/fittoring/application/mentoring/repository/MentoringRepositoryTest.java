package fittoring.application.mentoring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.RepositoryTestSupport;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MentoringRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("멘토링을 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void mentoringSoftDelete() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1", Gender.MALE, "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        Mentoring mentoring = mentoringRepository.save(
                new Mentoring(
                        mentor,
                        5000,
                        3,
                        "컨텐츠컨텐츠",
                        "자기소개자기소개"
                )
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

    @DisplayName("멘토링을 조회할 때 삭제 상태의 멘토링은 제외하고 조회한다.")
    @Test
    void findMentorings() {
        //given
        Member mentor = memberRepository.save(
                new Member("id1", Gender.MALE, "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        mentoringRepository.save(
                new Mentoring(mentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개")
        );

        Member mentor2 = memberRepository.save(
                new Member("id2", Gender.MALE, "이트레이너", new Phone("010-1234-5678"), Password.from("pw"))
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
