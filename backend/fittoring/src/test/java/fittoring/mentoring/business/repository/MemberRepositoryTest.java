package fittoring.mentoring.business.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.model.Member;
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
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("삭제 상태인 멤버만 조회할 수 있다.")
    @Test
    void findAllDeleted() {
        //given
        Member savedMember = memberRepository.save(
                new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        memberRepository.delete(savedMember);

        //when
        List<Member> allDeletedMember = memberRepository.findAllDeleted();

        //then
        Member actual = allDeletedMember.getFirst();
        assertThat(actual.isDeleted()).isTrue();
    }

    @DisplayName("멤버를 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void softDelete() {
        //given
        Member savedMember = memberRepository.save(
                new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        //when
        memberRepository.delete(savedMember);

        //then
        List<Member> allDeletedMember = memberRepository.findAllDeleted();

        Member actual = allDeletedMember.getFirst();
        assertSoftly(softly -> {
            softly.assertThat(actual.isDeleted()).isTrue();
            softly.assertThat(actual.getDeletedAt()).isNotNull();
        });
    }
}
